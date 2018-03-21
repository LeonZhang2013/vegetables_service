package com.zhongshu.vegetables.service.back;

import com.zhongshu.vegetables.bean.Permission;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.Arith;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购管理
 *
 * @author lynn
 */
@Component
@Transactional
public class AuthService extends BaseService {

    public static final Permission P_Shopping = new Permission(100, "购买商品");
    public static final Permission P_PyShopping = new Permission(200, "辅助下单");
    public static final Permission P_Purchase = new Permission(300, "采购商品");
    public static final Permission P_MgProduct = new Permission(400, "商品管理");
    public static final Permission P_EDIT_PRODUCT = new Permission(410,"完全编辑商品");
    public static final Permission P_MgAllProduct = new Permission(420, "管理所有商品");
    public static final Permission P_MgOrder = new Permission(500, "订单管理");
    public static final Permission P_MgUser = new Permission(600, "用户管理");
    public static final Permission P_MgAllUser = new Permission(610, "管理所有用户");
    public static final Permission P_AddUser = new Permission(602, "添加用户");
    public static final Permission P_CheckOrder = new Permission(700,"察看所有订单");
    public static final Permission P_proxy = new Permission(800,"代理商权限");
    public static final Permission P_CANCEL_ORDER = new Permission(503,"取消订单");
    public static final Permission P_DELIVER_GOODS = new Permission(505,"一件发货");

    public static final Permission P_BIG_PURCHASE_CHECK = new Permission(302,"验货权");
    public static final Permission P_PURCHASE_PRODUCT_UNIT = new Permission(303,"分件货");
    public static final Permission P_PURCHASE_PRODUCT_DESC = new Permission(304,"分斤货");
    public static final Permission P_CHANGE_PRICE = new Permission(305,"定价");
    public static final Permission P_PAY_PRODUCT = new Permission(306,"付货款");
    public static final Permission P_REFRESH_PURCHASE = new Permission(310,"刷新采购权限");
    public static final Permission P_DISTRIBUTION = new Permission(900,"配送商品");


    public static final int PROXY = 9;
    public static final int CUSTOMER = 6;

    public boolean hasPermission(Long role_id,Permission permission) {
        boolean hasPer = false;
        List<Map<String, Object>> permissonList = roleDao.getPermissonById(role_id);
        for (int i=0;i<permissonList.size();i++) {
            String per = permissonList.get(i).get("permission_id").toString();
            if(per.equals(permission.getId().toString())){
                hasPer =true;
                break;
            }
        }
        return hasPer;
    }
}
