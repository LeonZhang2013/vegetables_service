package com.zhongshu.vegetables.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;



@Repository
public class PurchaseDao {


    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     * @return
     */
    public List<Map<String, Object>> getCheckSupplier() {
        String sql = "select COUNT(u.id) count, u.id,u.`nickname` ,u.`mobile` ,ua.address from `user` u, `order_detail` od,`item` it,supply_task st,`user_address` ua" +
                "WHERE it.`id` = od.item_id and u.id = st.user_id and st.product_id = it.id and ua.user_id = u.id GROUP BY u.id";
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * @return
     */
    public List<Map<String, Object>> getCheckProduct(String supplier_id) {
        String sql = "select od.id,od.order_id,od.item_id,i.pic_url,i.title,i.`price`,(SUM(od.purchase_num)*i.`price`) total_price,SUM(od.purchase_num) pur_total_num,od.purchase_address,SUM(od.num) order_total  from `item` i,supply_task st,`order_detail` od" +
                " WHERE i.id = st.product_id and od.item_id = st.product_id and st.user_id = ? GROUP BY i.id";

        return jdbcTemplate.queryForList(sql,supplier_id);
    }
}
