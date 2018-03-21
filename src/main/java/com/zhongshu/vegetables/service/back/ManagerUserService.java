package com.zhongshu.vegetables.service.back;

import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.RoleDao;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.MySql;
import com.zhongshu.vegetables.utils.SQLTools;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleInfo;
import java.util.List;
import java.util.Map;


@Service
public class ManagerUserService extends BaseService{


    /**
     * 分页获得用户列表
     * @param pager
     * @param role_id
     * @param enable
     * @return
     * @throws Exception
     */
    public MultiResult<Map<String,Object>> getUserList(Pager<Map<String, Object>> pager, User user, Long role_id, String key, Integer enable)throws Exception{
        MySql sql = new MySql("select u.*, r.name role_name,a.city,a.address,a.freight, ");
        sql.append(" (select CONCAT(city,'  ',nickname) from user_address where user_id = u.parent_id) parent_name");
        sql.append(" from user u,role r,user_address a where u.role_id = r.id and u.address_id = a.id ");
        sql.append("and r.level > (select level from role where id = ?) and u.role_id = ?");
        sql.addValue(user.getRole_id());
        sql.addValue(role_id);
        sql.notNullAppend(" and u.enable = ?",enable);
        if(!auth.hasPermission(user.getRole_id(),auth.P_MgAllProduct)){
            sql.notNullAppend(" and u.parent_id= ? ",user.getId());
        }
        key = SQLTools.FuzzyKey(key);
        sql.notNullAppend(" and (u.mobile like ? or a.address like ? or u.nickname like ? or a.city like ?)",key,key,key,key);
        sql.orderBy("u.enable asc","u.id desc");
        //long count = sql.getCount(jdbcTemplate);
        sql.limit(pager);
        MultiResult<Map<String,Object>> result = new MultiResult<>();
        List<Map<String,Object>> data = jdbcTemplate.queryForList(sql.toString(),sql.getValues());
        if(null != data && data.size() > 0){
            SQLTools.removePass(data);
            result.setCode(Code.SUCCESS);
            result.setData(data);
        }else{
            result.setCode(Code.NO_DATA);
        }
         //result.setTotal(count);
        return result;
    }

    public boolean updateRole(String user_id, Integer role_id) throws CustomException {
        //如果是代理商（9 是代理商） 就检查 配送地址是否能计算运费。
        int effect = userDao.updateUserRole(user_id,role_id);
        return effect>0;
    }

    public SingleResult<Object> updateState(String user_id, String state) {
        SingleResult<Object> result = new SingleResult<>();
        int effect = userDao.updateUserState(user_id,state);
        if(effect > 0){
            result.setCode(Code.SUCCESS);
            result.setMessage("授权成功");
        }else{
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    public SingleResult<Object> updateAddress(String user_id,String address) {
        SingleResult<Object> result = new SingleResult<>();
        int effect = userDao.updateUserAddress(user_id,address);
        if(effect > 0){
            result.setCode(Code.SUCCESS);
            result.setMessage("修改成功");
        }else{
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    public SingleResult<Object> updateUserCity(String user_id,String city) {
        SingleResult<Object> result = new SingleResult<>();
        int effect = userDao.updateUserCity(user_id,city);
        if(effect > 0){
            result.setCode(Code.SUCCESS);
            result.setMessage("修改成功");
        }else{
            result.setCode(Code.NO_DATA);
        }
        return result;
    }


}
