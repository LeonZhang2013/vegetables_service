package com.zhongshu.vegetables.service;

import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.bean.Version;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonService extends BaseService {
    /**
     * 检查更新
     * @param code
     * @param packageName
     * @return
     * @throws Exception
     */
    public Version checkVersion(Integer code, String packageName)throws Exception{
        return commonDao.checkVersion(code,packageName);
    }

    /**
     * 查询这 角色id
     * @param role_id 获取的角色列表不能大于自己的角色。
     * @return
     */
    public List<Role> getRoles(Long role_id) {
        return commonDao.getRoles(role_id);
    }

    public List<Role> getRoles() {
        return commonDao.getRoles();
    }

    /**
     * 查询这 角色id
     * @param role_id 获取的角色列表不能大于自己的角色。
     * @return
     */
    public Role getRole(Long role_id) {
        return commonDao.getRole(role_id);
    }



    public boolean verCode(String phone, String verCode) throws CustomException {
        boolean isOk = false;
        List<String> codes = verCodeDao.getVerCode(phone);//查询未过期的验证码
        if (codes != null && codes.size() > 0) {
            for (String vc : codes) {//可能发送多条验证码。
                if (vc.equals(verCode)) {//匹配验证码
                    isOk = true;
                    int deleteInt = verCodeDao.delCodeByPhone(phone);
                    break;
                }
            }
        }
        return isOk;
    }

    /**
     * 获得省市区
     * @param parentId
     * @return
     * @throws Exception
     */
    public MultiResult<Map<String,Object>> getArea(Long parentId)throws Exception{
        MultiResult<Map<String,Object>> result = new MultiResult<>();
        List<Map<String,Object>> data = entityManagerDao.executeQuery("select * from area where enable=1 and parent_id = :parentId", Entity.options().add("parentId", parentId).build(), null,0);
        if(null != data && data.size() > 0){
            result.setCode(Code.SUCCESS);
            result.setData(data);
        }else{
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    public List<Map<String,Object>>  getPermissions() {
        return commonDao.getPermissions();
    }

    public void updateRole(Long userId, String role_id, JSONArray groupJson) {
        commonDao.updateRole(userId,role_id,groupJson);
    }

    public List<Map<String,Object>> getMyPermissions(String role_id) {
        return commonDao.getMyPermissions(role_id);
    }


}
