package com.zhongshu.vegetables.controller.back;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.bean.Attribute;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("buyGroup")
public class BugGroupController extends BaseController {

    @RequestMapping("getGroup")
    public MultiResult<Map<String, Object>> getGroup() {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> list = groupService.getGroup();
            if (list.size() == 0) {
                result.setCode(Code.NO_DATA);
            } else {
                result.setCode(Code.SUCCESS);
                result.setData(list);
            }
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("updateBuyGroup")
    public SingleResult<Object> updateBuyGroup(Long user_id,String groups) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            boolean isOk;
            isOk = groupService.update(user_id,groups);
            if (isOk) {
                result.setCode(Code.SUCCESS);
                result.setMessage("更新成功");
            }
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
