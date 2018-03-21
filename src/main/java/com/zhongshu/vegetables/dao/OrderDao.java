package com.zhongshu.vegetables.dao;

import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.dao.beans.Freight;
import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.utils.Arith;
import com.zhongshu.vegetables.utils.MySql;
import com.zhongshu.vegetables.utils.SQLTools;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class OrderDao {


    @Resource
    JdbcTemplate jdbcTemplate;

    @Transactional
    public BigDecimal saveOrder(Long user_id, Long order_id, String proxy_id, BigDecimal user_prencet, BigDecimal userPreFreight) throws Exception {
        checkRecommit(user_id);
        MySql mySql = new MySql();

        mySql.append("SELECT p.*,c.product_id,c.num buy_num,u.parent_id,p.percent_price,");
        mySql.append(" (SELECT user_id FROM supply_task where product_id = p.id) supplier_id ");
        mySql.append("FROM product p,shop_cart c,`user` u WHERE u.id = c.user_id and p.id = c.product_id and c.user_id = ?");
        List<Map<String, Object>> orders = jdbcTemplate.queryForList(mySql.toString(), user_id);
        BigDecimal totalPrice = Arith.calShopCartPrice(orders, user_prencet, userPreFreight);
        mySql.clean();
        mySql.append("INSERT INTO `user_order`(`user_id`,`product_id`,`price_sale`,`piece_price`,`sub_freight`,");
        mySql.append("`sub_price`,`order_status`,`buy_num`,`order_id`,`proxy_id`,`supplier_id`,`percent_rebate`) values(?,?,?,?,?,?,?,?,?,?,?,?)");
        int[] ints = jdbcTemplate.batchUpdate(mySql.toString(), new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map<String, Object> item = orders.get(i);
                ps.setString(1, user_id.toString());
                ps.setString(2, item.get("product_id").toString());
                ps.setString(3, item.get("price_sale").toString());
                ps.setString(4, item.get("piece_price").toString());
                ps.setString(5, item.get("sub_freight").toString());
                ps.setString(6, item.get("sub_price").toString());
                ps.setString(7, OrderStatus.WAIT_PAY.getStatus() + "");
                ps.setString(8, item.get("buy_num").toString());
                ps.setString(9, order_id.toString());
                ps.setString(10, proxy_id);
                ps.setString(11, item.get("supplier_id").toString());
                ps.setString(12, item.get("percent_price").toString());
            }

            public int getBatchSize() {
                return orders.size();
            }
        });
        //清空购物车
        String sqlDel = "delete from shop_cart where user_id = ?";
        int effect = jdbcTemplate.update(sqlDel, user_id);
        if (effect == 0) throw new CustomException("清空购物车失败");
        return totalPrice;
    }

    public List<Map<String, Object>> getUserCart(Long userId) {
        String sql = "select p.*,s.product_id,s.id cart_id,s.num cart_item_num from shop_cart s,product p where s.product_id = p.id and s.user_id = ?";
        List<Map<String, Object>> data = jdbcTemplate.queryForList(sql, userId);
        for (int i = 0; i < data.size(); i++) {//由于 我使用 p.* 所以我需要把 p.id 替换成 cart_id.
            Object cart_id = data.get(i).get("cart_id");
            data.get(i).put("id", cart_id);
        }
        return data;
    }


    //检查重复提交(在30秒内，不能提交两次)
    private void checkRecommit(Long userId) throws Exception {
        String sql = "SELECT create_time FROM `user_order` where user_id = ? ORDER BY create_time DESC LIMIT 0,1";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, userId);
        if (maps.size() > 0) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(maps.get(0).get("create_time").toString());
            long offset = System.currentTimeMillis() - date.getTime();
            if (offset < 10000) {//10秒内不能提交两次
                throw new CustomException("提交太快，休息一会");
            }
        }
    }


    public void batchUpdate(String user_id, JSONArray cartItems) {
        String sqlDelete = "delete from shop_cart where user_id=?";
        int effect = jdbcTemplate.update(sqlDelete, user_id);
        String sql = "insert into shop_cart(user_id,product_id,num) values(?,?,?)";
        int[] ints = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                String id = cartItems.getJSONObject(i).get("id").toString();
                String num = cartItems.getJSONObject(i).get("num").toString();
                ps.setString(1, user_id);
                ps.setString(2, id);
                ps.setString(3, num);
            }

            public int getBatchSize() {
                return cartItems.size();
            }
        });
    }

    public Freight calculationFreight(Long userId, BigDecimal charge) throws CustomException {
        String sql = "select sc.num buy_num,p.weight from shop_cart sc,product p where p.id = sc.product_id and user_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, userId);
        return Arith.calTotalFreight(maps, charge);
    }


    public boolean operateOrder(Long orderId, Integer status, Long operId) {
        String sql = "update user_order set order_status = ? where order_id = ? and order_status = ?";
        int update = jdbcTemplate.update(sql, OrderStatus.getOrderStatus(status).next().getStatus(), orderId, status);
        return update > 0;
    }

    public List<Map<String, Object>> getDisItem(Long proxy_id, String keyword) {
        MySql mySql = new MySql();
        mySql.append("select p.*,SUM(o.buy_num) buy_num,SUM(o.purchase_num) purchase_num from proxy_order o,product p ");
        mySql.append("where p.id = o.product_id and o.`proxy_id`  = ? and o.order_status = ?");
        mySql.addValue(proxy_id);
        mySql.addValue(OrderStatus.WAIT_SEND.getStatus());
        keyword = SQLTools.FuzzyKey(keyword);
        mySql.notNullAppend(" and (p.code like ? or p.title like ? or p.alias_pinyin like ? or p.alias like ?) ", keyword, keyword, keyword, keyword);
        mySql.append("group by o.product_id");
        return jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());
    }


    public List<Map<String, Object>> getDisProductToUser(Long proxy_id, Long product_id) {
        String sql = "select u.nickname,o.*,p.num from user_order o,`user` u,product p " +
                "where u.id = o.user_id and o.product_id = p.id and o.`proxy_id`  = ? and o.product_id = ? and o.order_status = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, proxy_id, product_id, OrderStatus.SEND.getStatus());
        return maps;
    }
}
