package com.zhongshu.vegetables.controller;


import com.zhongshu.vegetables.bean.Attribute;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("attribute")
public class AttributeController extends BaseController {

    @RequestMapping("getAttributes")
    public MultiResult<Map<String, Object>> getAttributes(Integer islist) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> list = attributeService.getAttributes(islist);
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

    @RequestMapping("getSearchKeys")
    public MultiResult<Map<String, Object>> getSearchKeys(Long attribute_id,Integer islist) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> list = attributeService.getSearchKeys(attribute_id,islist);
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

    @RequestMapping("updateAttribute")
    public SingleResult<Object> updateAttribute(Attribute attribute) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            boolean isOk;
            if (attribute.getId() != null) {
                isOk = attributeService.update(attribute);
            } else {
                isOk = attributeService.insert(attribute);
            }
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

    @RequestMapping("updateSearchKey")
    public SingleResult<Object> updateSearchKey(Long id,String name,int islist,long attribute_id) {
        SingleResult<Object> result = new SingleResult<>();
        try {
            boolean isOk = false;
            if (id != null) {
                isOk = attributeService.updateSearchKey(id,name,islist,attribute_id);
            } else {
                isOk = attributeService.insertSearchKey(name,islist,attribute_id);
            }
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
