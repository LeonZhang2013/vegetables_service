package com.zhongshu.vegetables.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.bean.Product;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.service.back.AuthService;
import com.zhongshu.vegetables.utils.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ProductService extends BaseService {


    /**
     * 分页获得商品列表
     * 商品ID、标题、图片、价格、单位
     *
     * @param pager
     *@param user  @return
     * @throws Exception
     */
    public List<Map<String, Object>> getProductList(Pager<Map<String, Object>> pager, String attribute, String keyword, Integer islist, Integer promotion, User user) throws Exception {
        BigDecimal userPercent = userDao.getUserPercent(user.getId());
        List<Map<String, Object>> productList = productDao.getProductList(pager, attribute, keyword, islist, promotion, user);
        Arith.calProductSalePrice(productList,userPercent);
        return productList;

    }

    public List<Map<String, Object>> getProductUnit() {
        return productDao.getUnit();
    }



    @Transactional
    public void addProduct(Product product, long oper_id, String imagesStr, String supplierId) throws CustomException {
        Long productId = Tools.generatorId();
        JSONArray imageJson = JSON.parseArray(imagesStr);
        productDao.addImage(productId, imageJson);
        productDao.addSupplier(productId, supplierId);
        product.setId(productId);
        product.setOper_id(oper_id);
        product.setPrice_sale(getPriceSale(product.getPrice(), product.getPercent_price()));
        product.setPic_url(imageJson.getJSONObject(0).getString("bucketPath") + (imageJson.getJSONObject(0).getString("image")));
        int effect = productDao.addProduct(product);
        if (effect == 0) throw new CustomException("保存数据失败");
    }

    @Transactional
    public void updateProduct(Product item, String images, String supplierId) throws CustomException {
        JSONArray imagesJson = JSON.parseArray(images);
        if (imagesJson != null && imagesJson.size() > 0) {
            productDao.addImage(item.getId(), imagesJson);
            item.setPic_url(imagesJson.getJSONObject(0).getString("bucketPath") + (imagesJson.getJSONObject(0).getString("image")));
        }
        if(item.getPrice()!=null&&item.getPercent_price()!=null){
            item.setPrice_sale(getPriceSale(item.getPrice(), item.getPercent_price()));
        }
        if (supplierId != null && !supplierId.equals("null")) {
            int effect = productDao.updateSupplier(item.getId(), supplierId);
            if (effect == 0) throw new CustomException("更新失败");

        }
        if (productDao.updateProduct(item) == 0)
            throw new CustomException("更新数据失败");
    }

    public void changeIsList(String id, String islist) throws CustomException {
        int effect = productDao.changeIsList(id, islist);
        if (effect == 0) throw new CustomException("更新失败更新失败");
    }




    public long getSupplierProductCountById(String user_id) throws Exception {
        return productDao.getProductCountById(user_id);
    }


    public List<Map<String, Object>> getProductPySupplier(Pager<Map<String, Object>> pager, String attribute, String keyword,Integer promotion,
                                                          String user_id, Integer islist, User user) throws Exception {
        List<Map<String, Object>> productPySupplier = null;
        //如果当前是商家只能让他访问自己的商品.
        if (!auth.hasPermission(user.getRole_id(), AuthService.P_MgAllProduct)) {
            productPySupplier = productDao.getProductBySelf(pager,attribute, keyword, user.getId().toString(), islist,user);
        } else {
            if (StringUtils.isNotNull(user_id)) {
                productPySupplier = productDao.getProductBySelf(pager, attribute,keyword, user_id, islist,user);
            } else {
                productPySupplier = productDao.getProductList(pager, attribute, keyword, islist,promotion,user);
                BigDecimal userPercent = userDao.getUserPercent(user.getId());
                Arith.calProductSalePrice(productPySupplier,userPercent);
            }
        }
        return productPySupplier;
    }

    public List<Map<String, Object>> getUserLikeProduct(Pager<Map<String, Object>> pager, String keyword, String attribute, String userId) throws CustomException {
        if(userId==null||userId.length()<0){
            throw new CustomException("必须指定用户");
        }
        return productDao.getUserLikeProduct(pager, keyword, attribute, userId);
    }


    public List<Map<String, Object>> getKeyword(Pager<Map<String, Object>> pager, String key, String attribute, Integer islist) {
        return productDao.getKeyword(pager,key,attribute,islist);
    }

    public List<Map<String,Object>> unrelatedProduct(Pager<Map<String, Object>> pager) {
        return productDao.unrelatedProduct(pager);
    }

    public boolean updateProductPercent(String user_id, Integer percent_price) {
        return productDao.updateProductPercent(user_id,percent_price);
    }
}
