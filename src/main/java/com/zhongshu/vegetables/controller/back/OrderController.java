package com.zhongshu.vegetables.controller.back;

import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.utils.StringUtils;
import com.zhongshu.vegetables.utils.Tools;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RequestMapping(OrderController.BUY)
@RestController
//@Api(name = "后台管理-辅助下单", description = "后台管理-辅助下单")
//@ApiVersion(since = "1.0")
public class OrderController extends BaseController {

    public static final String BUY = "back/order";

    @RequestMapping(value = "getCartNum", method = RequestMethod.GET)
    public SingleResult<Long> getCartNum(Long userId) {
        SingleResult<Long> result = new SingleResult<>();
        try {
            if (null != userId) {
                result = orderService.getCartNum(userId);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "addShopcart", method = RequestMethod.POST)
    public SingleResult<Long> addShopcart(Long user_id, Long product_id, Integer num) {
        SingleResult<Long> result = new SingleResult<>();
        try {
            if (null != user_id && null != product_id && null != num) {
                orderService.addShopcart(user_id, product_id, num);
                result.setCode(Code.SUCCESS);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "addOrder", method = RequestMethod.POST)
    public synchronized SingleResult<String> addOrder(Long user_id,Long role_id, String comment, String address_id) {
        SingleResult<String> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            String proxy_id = userService.getProxyId(user_id);
            if(proxy_id==null){
                throw new CustomException("没有指定配送商");
            }
            Long order_id = Tools.generatorId();
            BigDecimal totalPrice = orderManagerService.addOrder(user_id,order_id,role_id,proxy_id);
            String paymentInfo  = orderManagerService.getPaymentInfo(order_id.toString(),totalPrice,"订单付款");
            result.setCode(Code.SUCCESS);
            result.setData(paymentInfo);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 计算运费
     * @param userId
     * @param role_id
     * @return
     */
    @RequestMapping(value = "calculationFreight", method = RequestMethod.GET)
    public synchronized SingleResult<Object> calculationFreight(Long userId,Long role_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            String freight = orderService.getShopCartFreight(userId,role_id).toString();
            result.setCode(Code.SUCCESS);
            result.setData(freight);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "getShopCartList", method = RequestMethod.GET)
    public MultiResult<Map<String, Object>> getShopCartList(Long userId) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> data = orderService.getShopcartList(userId);
            if (null != data && data.size() > 0) {
                result.setCode(Code.SUCCESS);
                result.setData(data);
            } else {
                result.setCode(Code.NO_DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "setShopcart", method = RequestMethod.POST)
    public SingleResult<String> setShopcart(Long cartId, Long userId, Integer num) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (null != cartId && null != userId && null != num) {
                boolean isOk = orderService.setShopcart(cartId, userId, num);
                if(isOk){
                    result.setCode(Code.SUCCESS);
                }
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "deleteShopcart", method = RequestMethod.POST)
    public SingleResult<String> deleteShopcart(Long cartId, Long userId) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (null != cartId && null != userId) {
                result = orderService.deleteShopcart(cartId, userId);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "batchDeleteShopcart", method = RequestMethod.POST)
    public SingleResult<String> batchDeleteShopcart(String ids, Long userId) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (StringUtils.isNotBlank(ids) && null != userId) {
                result = orderService.batchDeleteShopcart(StringUtils.convertString2List(ids, ","), userId);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "clearShopcart", method = RequestMethod.POST)
    public SingleResult<String> clearShopcart(Long userId) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (null != userId) {
                result = orderService.clearShopcart(userId);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "getShopcartByIdList", method = RequestMethod.GET)
    public MultiResult<Map<String, Object>> getShopcartByIdList(String ids, Long userId) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            if (StringUtils.isNotBlank(ids) && null != userId) {
                result = orderService.getShopcartByIdList(StringUtils.convertString2List(ids, ","), userId);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "batchCommitCart", method = RequestMethod.POST)
    public MultiResult<Map<String, Object>> batchCommitCart(String user_id, String carts) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            orderService.batchSaveCart(user_id, carts);
            result.setCode(Code.SUCCESS);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
