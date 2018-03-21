package com.zhongshu.vegetables.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.SingleResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("rolePermission")
public class RolePermissionController extends BaseController {

    @RequestMapping(value = "getMyGroup")
    public SingleResult<Object> getPurchaseGroup(String user_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> groups = groupService.getGroup();
            List<Map<String, Object>> myGroup = groupService.getMyGroup(user_id);
            for (int i = 0; i < groups.size(); i++) {
                Object item = groups.get(i).get("id");
                groups.get(i).put("check", "false");
                for (int j = 0; j < myGroup.size(); j++) {
                    Object exit = myGroup.get(j).get("group_id");
                    if (item.toString().equals(exit.toString())) {
                        groups.get(i).put("check", "true");
                    }
                }
            }
            result.setCode(Code.SUCCESS);
            result.setData(groups);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "getPurchase")
    public SingleResult<Object> getPurchase() {
        SingleResult<Object> result = new SingleResult<>();
        try {
            Role role = commonService.getRole(getUser().getRole_id());
            List<Map<String, Object>> users = userService.getPurchase(role);
            result.setCode(Code.SUCCESS);
            result.setData(users);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "updateBuyTask")
    public SingleResult<Object> updateBuyTask(String purchaseId, String groups) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            JSONArray groupJson = JSON.parseArray(groups);
            List<String> groupIds = new ArrayList<>();
            for (int i = 0; i < groupJson.size(); i++) {
                groupIds.add(groupJson.getJSONObject(i).getString("id"));
            }
            userService.updateBuyTask(purchaseId, groupIds);
            result.setCode(Code.SUCCESS);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("getRoles")
    public SingleResult<Object> getRoles(){
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Role> roles = commonService.getRoles();
            result.setCode(Code.SUCCESS);
            result.setData(roles);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    /**
     * 获取权限。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("getMyPermissions")
    public SingleResult<Object> getPermission(String role_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> permissions = commonService.getPermissions();
            List<Map<String, Object>> myPermissions = commonService.getMyPermissions(role_id);
            for (int i = 0; i < permissions.size(); i++) {
                Object item = permissions.get(i).get("id");
                permissions.get(i).put("check", "false");
                for (int j = 0; j < myPermissions.size(); j++) {
                    Object exit = myPermissions.get(j).get("permission_id");
                    if (item.toString().equals(exit.toString())) {
                        permissions.get(i).put("check", "true");
                    }
                }
            }

            result.setCode(Code.SUCCESS);
            result.setData(permissions);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 获取权限。
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("updateRole")
    public SingleResult<Object> updateRole(String role_id, String permissions) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            JSONArray groupJson = JSON.parseArray(permissions);
            commonService.updateRole(getUserId(),role_id, groupJson);
            result.setCode(Code.SUCCESS);
            result.setMessage("更新成功");
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}

