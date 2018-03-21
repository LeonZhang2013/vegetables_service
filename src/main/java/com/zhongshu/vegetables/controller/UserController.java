package com.zhongshu.vegetables.controller;

import com.zhongshu.vegetables.bean.Address;
import com.zhongshu.vegetables.bean.Permission;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.AliYunService;
import com.zhongshu.vegetables.service.UserService;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("user")
@Transactional
public class UserController extends BaseController {

    @RequestMapping("login")
    public SingleResult<Object> login(String username, String password) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
                Map<String, Object> login = userService.login(username, password);
                result.setCode(Code.SUCCESS);
                result.setData(login);
                result.setMessage("登陆成功");
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "register", method = RequestMethod.POST)
    public SingleResult<String> register(User user, String city,String address, String vercode) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (commonService.verCode(user.getMobile(), vercode)) {
                if(null == user.getRole_id())user.setRole_id(13L);
                userService.addUser(null,user,city,address);
                result.setCode(Code.SUCCESS);
                result.setMessage("注册用户成功");
            } else {
                result.setCode(Code.ERROR);
                result.setMessage("验证码有误");
            }
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "getAddress", method = RequestMethod.POST)
    public SingleResult<Object> getAddress(String address_id){
        SingleResult<Object> result = new SingleResult<>();
        try {
            Map<String, Object> address = userService.getAddress(address_id);
            result.setCode(Code.SUCCESS);
            result.setData(address);
        } catch (CustomException e) {
            result.setCode(Code.ERROR);
            result.setData(e.getMessage());
        }
        return result;
    }

    /**
     * 发送验证短信。
     *
     * @param phone 获取电话号码
     * @return
     */
    @RequestMapping("getVerCode")
    public SingleResult<Map<String, Object>> getVerCdoe(String phone,boolean update) {
        SingleResult<Map<String, Object>> result = new SingleResult<>();
        try {
            //如果是更新密码，就不用验证手机号是否存在。
            if(!update)userService.phoneHasEixst(phone);
            result = aliYunService.sendMassage(phone);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "getPermission", method = RequestMethod.POST)
    public SingleResult<Object> getPermissionByRoleId(String role_id) {
        SingleResult<Object> result = new SingleResult<>();
        if(role_id==null){
            result.setCode(Code.ERROR);
            result.setMessage("你没有权限");
            return result;
        }
        try {
            List<Permission> permissions = userService.getPermissionById(role_id);
            result.setCode(Code.SUCCESS);
            result.setData(permissions);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
