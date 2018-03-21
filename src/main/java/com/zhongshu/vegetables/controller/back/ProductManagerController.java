package com.zhongshu.vegetables.controller.back;

import com.zhongshu.vegetables.bean.Product;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.controller.BaseController;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("back/product")
public class ProductManagerController extends BaseController {

    @RequestMapping("changeIsList")
    public SingleResult<String> changeIsList(String id,String islist) {
        SingleResult<String> result = new SingleResult<>();
        try {
            productService.changeIsList(id,islist);
            result.setCode(Code.SUCCESS);
            result.setMessage("上传成功");
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    /**
     * 获取供应商
     * @param pager
     * @param attribute
     * @param keyword
     * @param promotion
     * @param user_id
     * @param isList
     * @return
     */
    @RequestMapping("getProductPySupplier")
    public MultiResult<Map<String, Object>> getProductPySupplier(Pager<Map<String, Object>> pager,String attribute, String keyword,Integer promotion, String user_id ,Integer isList) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String,Object>> data = productService.getProductPySupplier(pager, attribute, keyword,promotion,user_id, isList,getUser());
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }


    @RequestMapping("unrelatedProduct")
    public MultiResult<Map<String, Object>> unrelatedProduct(Pager<Map<String, Object>> pager) {
        MultiResult<Map<String, Object>> result = new MultiResult<Map<String, Object>>();
        try {
            List<Map<String,Object>> data = productService.unrelatedProduct(pager);
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

}
