package com.zhongshu.vegetables.controller;

import com.zhongshu.vegetables.bean.Product;
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
@RequestMapping("product")
public class ProductController extends BaseController {


    /**
     * 获取商品
     * @param pager
     * @param attribute //因为 product 数据库中存储的是 attribute的 name 不是 id 所以需要使用 name ,原因二，使用如果使用 id 在模糊搜索时 1 和 10 如果关键字是 1 就会有混淆。
     * @param keyword
     * @param islist
     * @param promotion
     * @return
     */
    @RequestMapping("getProducts")
    public MultiResult<Map<String, Object>> getProducts(Pager<Map<String, Object>> pager,String attribute,String keyword,Integer islist,Integer promotion) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> productList = productService.getProductList(pager, attribute, keyword, islist,promotion,getUser());
            if(productList.size()>0){
                result.setCode(Code.SUCCESS);
                result.setData(productList);
            }else{
                result.setCode(Code.NO_DATA);
                result.setMessage("已经到底了");
            }
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }



    @RequestMapping("getKeyword")
    public SingleResult<Object> getKeyword(Pager<Map<String, Object>> pager,String key,String attribute,Integer islist) {
        SingleResult<Object> data = new SingleResult<>();
        try {
            List<Map<String, Object>> maps = productService.getKeyword(pager,key,attribute,islist);
            data.setCode(Code.SUCCESS);
            data.setData(maps);
        } catch (Exception e) {
            data.setCode(Code.ERROR);
        }
        return data;
    }


    @RequestMapping("getProductUnit")
    public SingleResult<Object> getProductUnit() {
        SingleResult<Object> result = new SingleResult<>();
        result.setCode(Code.ERROR);
        try {
            List<Map<String, Object>> productUnit = productService.getProductUnit();
            result.setCode(Code.SUCCESS);
            result.setData(productUnit);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("getUserLikeProducts")
    public MultiResult<Map<String, Object>> getUserLikeProducts(Pager<Map<String, Object>> pager, String keyword, String attribute, String user_id) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            List<Map<String, Object>> maps = productService.getUserLikeProduct(pager, keyword, attribute, user_id);
            result.setCode(Code.SUCCESS);
            result.setData(maps);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @RequestMapping("uploadProduct")
    public MultiResult<Map<String, Object>> uploadProduct(Product product, String imagesJson, String supplierId) {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        try {
            if (product.getId() != null&&product.getId()>0) {
                productService.updateProduct(product, imagesJson,supplierId);
            } else {
                productService.addProduct(product, getUserId(), imagesJson,supplierId);
            }
            result.setCode(Code.SUCCESS);
            result.setMessage("上传成功");
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
        }
        return result;
    }
}
