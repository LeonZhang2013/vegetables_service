package com.zhongshu.vegetables.controller.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RequestMapping(OrderManagerController.OM)
@RestController
//@Api(name = "后台管理-订单管理", description = "后台管理-订单管理")
//@ApiVersion(since = "1.0")
public class OrderManagerController extends BaseController {

    public static final String OM = "back/om";

    @RequestMapping(value = "getOrderStatus", method = RequestMethod.GET)
    public MultiResult<Map<String, Object>> getOrderStatus() {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            result = orderManagerService.getOrderStatus();
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }


    @RequestMapping(value = "deliverProduct", method = RequestMethod.POST)
    public MultiResult<Map<String, Object>> deliverProduct(String freight, String weight, String termini) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        result.setCode(Code.ERROR);
        try {
            if (freight == null || freight.trim().length() == 0) throw new Exception("运费不能为空");
            boolean isOk = orderManagerService.deliverProduct(getUser(), freight, weight, termini);
            if (isOk) {
                result.setCode(Code.SUCCESS);
                result.setMessage("发送成功");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping(value = "getOrderList", method = RequestMethod.GET)
    public MultiResult<Map<String, Object>> getOrderList(Integer status, String key, Pager<Map<String, Object>> pager) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> data = orderManagerService.getOrderList(status, key, pager, getUser());
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "getOrderDetail", method = RequestMethod.GET)
    public SingleResult<Object> getOrderDetail(Long order_id, Integer status) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            if (null != order_id) {
                List<Map<String, Object>> orderDetail = orderManagerService.getOrderDetail(order_id, status);
                result.setCode(Code.SUCCESS);
                result.setData(orderDetail);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "operateOrder", method = RequestMethod.POST)
    public synchronized SingleResult<String> operateOrder(Long order_id, Integer status) {
        SingleResult<String> result = new SingleResult<>();
        try {
            result.setCode(Code.ERROR);
            if (null != order_id && null != status) {
                boolean isOk = orderManagerService.operateOrder(order_id, status, getUserId());
                if (isOk) {
                    result.setCode(Code.SUCCESS);
                } else {
                    result.setCode(Code.NO_DATA);
                }
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    @RequestMapping(value = "cancelOrder", method = RequestMethod.POST)
    public synchronized SingleResult<String> cancelOrder(Long order_id, Long sub_id) {

        SingleResult<String> result = new SingleResult<>();
        try {
            boolean isOk = orderManagerService.cancelOrder(order_id, sub_id);
            if (isOk) {
                result.setCode(Code.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }

    @RequestMapping(value = "generateSupplierOrder", method = RequestMethod.POST)
    public synchronized SingleResult<String> generateSupplierOrder(String supplierId, String payMoney, String price) {
        SingleResult<String> result = new SingleResult<>();
        try {
            if (null != supplierId && null != payMoney) {
                orderManagerService.generateSupplierOrder(getUserId(), supplierId, payMoney, price);
                result.setCode(Code.SUCCESS);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }





}
