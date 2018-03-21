package com.zhongshu.vegetables.controller.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("distribution")
public class DistributionController extends BaseController {

    @RequestMapping(value = "getDisProduct", method = RequestMethod.GET)
    public synchronized SingleResult<Object> getDisItem(Long proxy_id,String keyword) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> distribution = distributionService.getDisItem(proxy_id,keyword);
            result.setCode(Code.SUCCESS);
            result.setData(distribution);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "getDisUser", method = RequestMethod.GET)
    public synchronized SingleResult<Object> getDisProductToUser(Long proxy_id, Long product_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            List<Map<String, Object>> toUser = distributionService.getDisProductToUser(proxy_id, product_id);
            result.setCode(Code.SUCCESS);
            result.setData(toUser);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "saveDisProduct", method = RequestMethod.POST)
    public synchronized SingleResult<Object> saveDisProduct(String json, Long proxy_id, Long product_id, String isFinish) {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            List<Map<String, Object>> detailList = JSON.parseObject(json, new TypeReference<List<Map<String, Object>>>() {
            }.getType());
            boolean isOk = distributionService.saveDisProduct(detailList, proxy_id, product_id, "true".equals(isFinish));
            if (isOk) {
                result.setCode(Code.SUCCESS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage(e.getMessage());
        }
        return result;
    }


}
