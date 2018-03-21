package com.zhongshu.vegetables.controller.back;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("back/user")
public class UserManagerController extends BaseController {


    @RequestMapping("getUserList")
    public MultiResult<Map<String, Object>> getUserList(Pager<Map<String, Object>> pager, Long role_id, String key, Integer enable) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            result = managerUserService.getUserList(pager, getUser(), role_id, key, enable);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setCode(Code.ERROR);
        }
        return result;
    }


    @RequestMapping("updateRole")
    public SingleResult<Object> updateRole(String user_id, Integer role_id) {
       SingleResult<Object> result = new SingleResult<Object>();
        result.setCode(Code.ERROR);
        try {
            //如果该用户有供应商品，必须先去除掉。
            long count = productService.getSupplierProductCountById(user_id);
            if (count > 0) {
                result.setMessage("请分配该供应商所属" + count + "个商品");
            } else {
                boolean isOk = managerUserService.updateRole(user_id, role_id);
                if (isOk) {
                    result.setCode(Code.SUCCESS);
                    if(role_id.intValue()== 9){
                        result.setMessage("授权成功,请编辑运费");
                    }else{
                        result.setMessage("授权成功");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateFreight")
    public SingleResult<Object> updateFreight(String user_id, Double freight) {
        SingleResult<Object> result = new SingleResult<Object>();
        result.setCode(Code.ERROR);
        try {
            boolean isOk = userService.updateFreight(user_id, freight);
            if (isOk) {
                result.setCode(Code.SUCCESS);
                result.setMessage("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateParentId")
    public SingleResult<Object> updateProductPercent(String user_id, Long parent_id) {
        SingleResult<Object> result = new SingleResult<Object>();
        result.setCode(Code.ERROR);
        try {
            boolean isOk = userService.updateParentId(user_id, parent_id);
            if (isOk) {
                result.setCode(Code.SUCCESS);
                result.setMessage("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateProductPercent")
    public SingleResult<Object> updateProductPercent(String user_id, Integer percent_price) {
        SingleResult<Object> result = new SingleResult<Object>();
        result.setCode(Code.ERROR);
        try {
            boolean isOk = productService.updateProductPercent(user_id, percent_price);
            if (isOk) {
                result.setCode(Code.SUCCESS);
                result.setMessage("修改成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateState")
    public SingleResult<Object> updateState(String user_id, String state) {
        SingleResult<Object> result = new SingleResult<Object>();
        try {
            //如果该用户有供应商品，有需要供应的商品，必须先全部去掉，才能冻结。
            if (state.trim().equals("0")) {
                long count = productService.getSupplierProductCountById(user_id);
                if (count > 0) new CustomException("请分配该供应商所属" + count + "个商品");
            }
            result = managerUserService.updateState(user_id, state);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "addUser", method = RequestMethod.POST)
    public SingleResult<String> register(User user, String address, String city, String vercode) {
        SingleResult<String> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            if (commonService.verCode(user.getMobile(), vercode)) {
                if (auth.hasPermission(getUser().getRole_id(), auth.P_MgUser)) {
                    userService.addUser(getUser(), user, city, address);
                    result.setCode(Code.SUCCESS);
                    result.setMessage("注册成功");
                } else {
                    result.setMessage("你没有权限");
                }
            } else {
                result.setMessage("验证码有误");
            }
        } catch (Exception e) {
            if (e.getMessage().indexOf("Incorrect") > 0) {
                result.setMessage("请不要输入特殊字符");
            } else {
                result.setMessage(e.getMessage());
            }
        }
        return result;
    }

    @RequestMapping(value = "updateMultiLogin", method = RequestMethod.POST)
    public SingleResult<String> updateMultiLogin(Integer multi) {
        SingleResult<String> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            boolean isOk = userService.updateMultiLogin(getUserId(), multi);
            if (isOk) result.setCode(Code.SUCCESS);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "updatePwd", method = RequestMethod.POST)
    public SingleResult<String> updatePwd(String newPwd, String vercode, String mobile) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (commonService.verCode(mobile, vercode)) {
                userService.updatePwd(mobile, newPwd);
                result.setCode(Code.SUCCESS);
                result.setMessage("");
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


    @RequestMapping(value = "updateAddress", method = RequestMethod.POST)
    public SingleResult<Object> updateAddress(String user_id, String address) {
        SingleResult<Object> result = new SingleResult<Object>();
        try {
            result = managerUserService.updateAddress(user_id, address);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "updateUserCity", method = RequestMethod.POST)
    public SingleResult<Object> updateUserCity(String user_id, String city) {
        SingleResult<Object> result = new SingleResult<Object>();
        try {
            result = managerUserService.updateUserCity(user_id, city);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }


    @RequestMapping(value = "getSupplierList", method = RequestMethod.GET)
    public SingleResult<Object> getSupplierList() {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> users = userService.getSupplierList();
            result.setCode(Code.SUCCESS);
            result.setData(users);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }



}
