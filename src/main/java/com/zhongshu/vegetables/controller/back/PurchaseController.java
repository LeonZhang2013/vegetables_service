package com.zhongshu.vegetables.controller.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhongshu.vegetables.bean.User;
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
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@RequestMapping("back/purchase")
@RestController
public class PurchaseController extends BaseController {


    /**
     * 获取有采购任务的商家
     *
     * @return
     */
    @RequestMapping(value = "generatePurchaseTask", method = RequestMethod.GET)
    public SingleResult<Object> generatePurchaseTask(String supplier_id,String product_id) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            purchaseService.generatePurchaseTask(getUser(),supplier_id, product_id);
            result.setCode(Code.SUCCESS);
        } catch (Exception e) {
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 获取有采购任务的商家
     *
     * @return
     */
    @RequestMapping(value = "getSupplierList", method = RequestMethod.GET)
    public SingleResult<Object> getSuppliersBuyTask() {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> checkSupplier = purchaseService.getBuyTaskSuppliers(getUser());
            if (checkSupplier == null) {
                result.setCode(Code.NO_DATA);
            } else {
                result.setCode(Code.SUCCESS);
                result.setData(checkSupplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    /**
     * 获取采购商自己的商品
     *
     * @return
     */
    @RequestMapping(value = "getProductsBySupplerId", method = RequestMethod.GET)
    public MultiResult<Map<String, Object>> getProductsBySupplerId(String supplier_id) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> checkSupplier = purchaseService.getProductsBySupplerId(getUserId(),supplier_id);
            for (int i=0; i<checkSupplier.size(); i++){
                Object weight = checkSupplier.get(i).get("purchase_weight");
                if (weight == null) checkSupplier.get(i).put("purchase_weight","0");
                Object sub_price = checkSupplier.get(i).get("sub_price");
                if (sub_price == null) checkSupplier.get(i).put("sub_price","0.00");
            }
            if (checkSupplier == null) {
                result.setCode(Code.NO_DATA);
            } else {
                result.setCode(Code.SUCCESS);
                result.setData(checkSupplier);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }


    /**
     * 获取代理商所代理的商品
     *
     * @param supplier_id
     * @param product_id
     * @return
     */
    @RequestMapping(value = "getProxyProductByProductId", method = RequestMethod.GET)
    public SingleResult<Object> getProxyProductByProductId(Long supplier_id, Long product_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            if (null != product_id) {
                List<Map<String, Object>> data = purchaseService.getProxyProductByProductId(supplier_id, product_id);
                result.setCode(Code.SUCCESS);
                result.setData(data);
            } else {
                result.setCode(Code.EXP_PARAM);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
        }
        return result;
    }


    /**
     * @param json
     * @param product_id
     * @param purchase_address
     * @param purchase_price
     * @param mark_price
     * @param other_charge
     * @return
     */
    @RequestMapping(value = "savePurchase", method = RequestMethod.POST)
    public SingleResult<String> savePurchase(String json,String product_id,String purchase_address,Double purchase_price,
                                             Double mark_price,Double other_charge,String payed,String remark) {
        SingleResult<String> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            List<Map<String, Object>> detailList = null;
            if (StringUtils.isNotBlank(json)) {
                detailList = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {}.getType());
            }
            boolean isOk = purchaseService.savePurchase(detailList, payed,product_id, purchase_address,
                    purchase_price,mark_price, other_charge,remark);
            if (isOk){
                result.setCode(Code.SUCCESS);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
           result.setMessage(e.getMessage());
        }
        return result;
    }




}




