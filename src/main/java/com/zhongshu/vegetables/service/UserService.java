package com.zhongshu.vegetables.service;

import com.zhongshu.vegetables.bean.Permission;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.RoleDao;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.config.AliYunSMS;
import com.zhongshu.vegetables.service.back.AuthService;
import com.zhongshu.vegetables.utils.StringUtils;
import com.zhongshu.vegetables.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService {

    @Autowired
    AliYunSMS aliYunSMS;


    public User getUserByToken(String token) {
        return userDao.getUserByToken(token);
    }

    @Transactional
    public Map<String, Object> login(String username, String password) throws CustomException {
        User user = null;
        Map<String, Object> data = new HashMap<>();
        try {
            user = userDao.login(username, password);
            if (user == null) throw new CustomException("用户名或密码错误");
            List<Role> role = commonDao.getRoles(user.getRole_id());
            if (user.getRole_id() == 0) {
                throw new CustomException("正在审核中...");
            }
            //是否允许多用户登录
            if (StringUtils.isNull(user.getToken()) || user.getIs_multi() == 0) {
                String token = StringUtils.uuid();
                userDao.updateToken(user.getId(), token);
                user.setToken(token);
            }
            Map<String, Object> address = userDao.getAddressByUserId(user.getId());
            List<Permission> list = userDao.getPermissionByRoleId(user.getRole_id());
            Map<String, Object> notice = commonDao.getNotice();

            data.put("permission", list);
            data.put("user", user);
            data.put("address", address);
            data.put("roleList", role);
            data.put("notice", notice);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return data;
    }


    @Transactional
    public boolean updatePwd(String mobile, String newPwd) throws CustomException {
        if (!StringUtils.isNotBlank(newPwd)) throw new CustomException("密码不能为空");
        try {
            return userDao.updateUserPwd(mobile, newPwd);
        } catch (Exception e) {
            throw new CustomException("修改用户名出错");
        }
    }

    @Transactional
    public long addUser(User parent, User user, String city, String address) throws CustomException {
        String encryptPass = null;
        try {
            encryptPass = Tools.encryptPass(user.getUsername(), user.getPassword());
        } catch (Exception e) {
            throw new CustomException("加密失败");
        }
        user.setPassword(encryptPass);

        User queryUser = userDao.getUserByMobile(user.getUsername());
        if (queryUser != null) {
            throw new CustomException("用户名或手机号已存在");
        }
        Long id = Tools.generatorId();
        user.setAddress_id(id);
        user.setId(id);
        int  effect = userDao.save(user);
        if (effect == 0) throw new CustomException("创建用户失败");
        effect = userDao.addAddress(user.getId(), id, city, address, user.getNickname(), user.getMobile());
        if (effect == 0) throw new CustomException("创建用户失败");
        boolean wallet = walletDao.createWallet(id);
        if (!wallet) throw new CustomException("初始化钱包失败");
        return effect;
    }

    public void phoneHasEixst(String phone) throws CustomException {
        User queryUser = userDao.getUserByMobile(phone);
        if (queryUser != null) {
            throw new CustomException("手机号已存在");
        }
    }

    public List<Permission> getPermissionById(String user_id) {
        return userDao.getPermissionByRoleId(Long.parseLong(user_id));
    }

    public Map<String, Object> getAddress(String address_id) throws CustomException {
        return userDao.getAddressById(address_id);
    }

    public List<Map<String, Object>> getSupplierList() {
        return userDao.getSupplierList();
    }


    public List<Map<String, Object>> getPurchase(Role role) {
        return userDao.getPurchase(role);
    }

    public void updateBuyTask(String purchaseId, List<String> cateIds) throws CustomException {
        userDao.updateBuyTask(purchaseId, cateIds);
    }

    public List<Map<String, Object>> getRoles() {
        return userDao.getRoles();
    }

    public boolean updateMultiLogin(Long userId, Integer multi) {
        return userDao.updateMultiLogin(userId, multi);
    }

    public boolean updateParentId(String user_id, Long parent_id) {
        return userDao.updateParentId(user_id,parent_id);
    }

    public boolean updateFreight(String user_id, Double freight) {
       return userDao.updateFreight(user_id,freight);
    }

    public String getProxyId(Long user_id) {
        String sql = "SELECT u.role_id,(select role_id from user p where p.id = u.parent_id) p_role_id,u.parent_id from user u where id =?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user_id);
        Map<String, Object> user = maps.get(0);
        String supplier_id = null;
        //Integer 对比建议使用+-
        if(user.get("role_id")!=null){
            if(Integer.parseInt(user.get("role_id").toString())-AuthService.PROXY == 0){
                supplier_id = user_id.toString();
            }
        }
        if(user.get("p_role_id")!=null){
            if(Integer.parseInt(user.get("p_role_id").toString())- AuthService.PROXY == 0){
                supplier_id = user.get("parent_id").toString();
            }
        }
       return supplier_id;
    }
}
