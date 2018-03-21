package com.zhongshu.vegetables.dao;

import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.bean.Product;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.utils.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class ProductDao {


    @Resource
    JdbcTemplate jdbcTemplate;


    public List<Map<String, Object>> getUnit() {
        String sql = "select * from unit";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    public boolean addImage(Long productId, JSONArray imagesJson) {

        String sql = "INSERT INTO product_image (path,oss_path,product_id) VALUES(?,?,?)";
        int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Long id = Tools.generatorId();
                String path = imagesJson.getJSONObject(i).get("image").toString();
                String servicePath = imagesJson.getJSONObject(i).get("bucketPath").toString();
                ps.setString(1, path);
                ps.setString(2, servicePath);
                ps.setString(3, productId.toString());
            }

            public int getBatchSize() {
                return imagesJson.size();
            }
        });
        return ints.length != 0;

    }

    public int addProduct(Product product) {
        SqlInfo sqlInfo = new SQLTools().getInsertSQL(product, "product");
        System.out.println(sqlInfo.getSql());
        return jdbcTemplate.update(sqlInfo.getSql(), sqlInfo.getValues());
    }

    public int updateProduct(Product product) {
        SqlInfo sqlInfo = new SQLTools().getUpdateById(product, "product",  product.getId());
        return jdbcTemplate.update(sqlInfo.getSql(), sqlInfo.getValues());
    }


    public int changeIsList(String id, String islist) {
        String sql = "update product set islist = ? where id=?";
        return jdbcTemplate.update(sql, islist, id);
    }


    public List<Map<String,Object>> getProductBySelf(Pager<Map<String, Object>> pager,String attribute, String keyword, String suppler_id, Integer isList, User user) {
        MySql sql = new MySql();
        sql.append("select p.*,(select nickname from `user`  where st.user_id = id) supplierName, st.user_id supplierId ");
        sql.append("FROM product  p,`supply_task` st where islist =? and st.product_id = p.id  ");
        sql.addValue(isList);
        keyword = SQLTools.FuzzyKey(keyword);
        sql.notNullAppend("and p.title like ? ",keyword);
        sql.notNullAppend(" and st.user_id = ? ",suppler_id);
        sql.notNullAppend(" and p.attribute like ? ","%"+attribute+"%");
        sql.limit(pager);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), sql.getValues());

        Arith.calProductSalePrice(maps,user.getPercent_price());
        return maps;
    }

    public int updateSupplier(Long productId, String supplierId) {
        String sql = "update supply_task set user_id = ? WHERE product_id = ?";
       return jdbcTemplate.update(sql,supplierId,productId);
    }

    public int deleteSupplierProduct(Long productId) {
        String sql = "delete from supply_task WHERE product_id = ?";
        return jdbcTemplate.update(sql,productId);
    }

    public int addSupplier(Long productId, String supplierId) {
        String sql = "insert into supply_task (user_id,product_id) values(?,?)";
        return jdbcTemplate.update(sql,supplierId,productId);
    }


    public long getProductCountById(String user_id) {
        String sql = "SELECT COUNT(*) count FROM `supply_task` WHERE `user_id` = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user_id);
        return Long.parseLong(maps.get(0).get("count").toString());
    }


    public List<Map<String,Object>> getUserLikeProduct(Pager<Map<String, Object>> pager, String keyword, String attribute, String userId) {
        MySql sql = new MySql("SELECT p.* FROM `order_detail` od,`order` o ,`product` p where islist = 1 ");
        sql.notNullAppend("and od.`product_id` = p.id and o.id = od.order_id and o.`user_id` = ?",userId);
        keyword = SQLTools.FuzzyKey(keyword);
        sql.notNullAppend("and (code like ? or title like ? or alias_pinyin like ? or alias like ?)",keyword,keyword,keyword,keyword);
        sql.notNullAppend("and attribute like ?","%"+attribute+"%");
        sql.append(" GROUP BY p.`id` ORDER BY o.create_time DESC ");
        sql.limit(pager);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), sql.getValues());
        return maps;
    }

    public List<Map<String, Object>> getKeyword(Pager<Map<String, Object>> pager, String keyword, String attribute, Integer islist) {
        MySql sql = new MySql("SELECT `title`,`attribute`,`promotion` FROM `product` where 1=1 ");
        sql.notNullAppend(" and islist = ? ",islist);
        keyword = SQLTools.FuzzyKey(keyword);
        sql.notNullAppend(" and (code like ? or title like ? or alias_pinyin like ? or alias like ?) ",keyword,keyword,keyword,keyword);
        sql.notNullAppend(" and attribute like ? ","%"+attribute+"%");
        sql.orderByDesc("id");
        sql.limit(pager);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), sql.getValues());
        return maps;
    }

    /**
     * 搜索
     * @param pager
     * @param attribute
     * @param keyword 模糊搜索
     *@param islist
     * @param promotion   @return
     */
    public List<Map<String, Object>> getProductList(Pager<Map<String, Object>> pager, String attribute, String keyword, Integer islist, Integer promotion, User user) {
        MySql sql = new MySql("SELECT p.*,u.nickname supplier,u.id supplier_id from product p,supply_task st, user u WHERE p.id = st.product_id and u.id = st.user_id ");
        sql.notNullAppend(" and p.islist = ? ",islist);
        keyword = SQLTools.FuzzyKey(keyword);
        sql.notNullAppend(" and (p.code like ? or p.title like ? or p.alias_pinyin like ? or p.alias like ?) ",keyword,keyword,keyword,keyword);
        sql.notNullAppend(" and p.attribute like ? ","%"+attribute+"%");
        sql.notNullAppend(" and p.promotion = ? ",promotion);
        sql.orderByDesc("p.id");
        sql.limit(pager);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql.toString(), sql.getValues());
        return maps;
    }


    public List<Map<String,Object>> unrelatedProduct(Pager<Map<String, Object>> pager) {
        MySql mySql = new MySql();
        mySql.append("SELECT * FROM `product` WHERE `group_id` = 0");
        mySql.limit(pager);
        return jdbcTemplate.queryForList(mySql.toString());
    }


    public boolean updateProductPercent(String user_id, Integer percent_price) {
        int update = jdbcTemplate.update("update user set percent_price = ? where id = ?", percent_price, user_id);
        return update>0;
    }
}
