package com.zhongshu.vegetables.controller.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.zhongshu.vegetables.config.Alipay;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("pay")
public class PayController extends BaseController {

    @RequestMapping("listener")
    public void payNotify(HttpServletRequest request) {
//获取支付宝POST过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }
//切记alipaypublickey是支付宝的公钥，请去open.alipay.com对应应用下查看。
//boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String publicKey, String charset, String sign_type)
        try {
            boolean flag = AlipaySignature.rsaCheckV1(params, Alipay.ALIPAY_PUBLIC_KEY, Alipay.CHARSET,Alipay.SIGN_TYPE);
            if(flag){
                String status = params.get("trade_status");
                if(status.equals("TRADE_SUCCESS")){
                    String order_id = params.get("out_trade_no");
                    orderManagerService.payOk(order_id, OrderStatus.WAIT_PAY);
                }
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }

    @RequestMapping("getPaymentInfo")
    public SingleResult<Object> getPaymentInfo(String order_id,String desc) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            if (null != order_id) {
                BigDecimal total = orderService.getUserOrderPrice(order_id);
                String data = orderManagerService.getPaymentInfo(order_id,total,desc);
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



}

