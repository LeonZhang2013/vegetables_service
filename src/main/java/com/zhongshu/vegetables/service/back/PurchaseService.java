package com.zhongshu.vegetables.service.back;

import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.Arith;
import com.zhongshu.vegetables.utils.MySql;
import com.zhongshu.vegetables.utils.SQLTools;
import com.zhongshu.vegetables.utils.StringUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 采购管理
 *
 * @author lynn
 */
@Component
@Transactional
public class PurchaseService extends BaseService {


    /**
     * 获取有采购商品的商家。(主界面)
     *
     * @param user
     * @return
     * @throws CustomException
     */
    public List<Map<String, Object>> getBuyTaskSuppliers(User user) throws CustomException {
        MySql mySql = new MySql();//
        mySql.append("SELECT u.nickname,a.city,a.address,a.mobile,o.buy_num,o.purchase_num,");
        mySql.append("o.purchase_weight,o.purchase_price,o.purchase_other_charge,o.supplier_id,o.payed");
        mySql.append("FROM `proxy_order` o,`buy_task` t, `product` p,`user` u,`user_address` a");
        mySql.append("WHERE o.supplier_id = u.id and o.product_id = p.id and a.id = u.address_id  ");
        mySql.notNullAppend("and p.`group_id` = t.group_id and o.order_status = ?", OrderStatus.WAIT_CHECK.getStatus());
        mySql.notNullAppend("and t.user_id = ? ", user.getId());
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());
        Iterator<Map<String, Object>> iterator = maps.iterator();

        List<Map<String, Object>> suppliers = new ArrayList<>();
        boolean isOk;
        while (iterator.hasNext()) {
            isOk = false;
            Map<String, Object> next = iterator.next();
            for (int i = 0; i < suppliers.size(); i++) {
                Map<String, Object> supplier = suppliers.get(i);
                if (supplier.get("supplier_id").toString().equals(next.get("supplier_id").toString())) {
                    add(supplier, next, "buy_num");
                    add(supplier, next, "purchase_num");
                    add(supplier, next, "payed");
                    addTotalPrice(supplier, next);
                    isOk = true;
                    break;
                }
            }
            if (!isOk) {
                addTotalPrice(next, next);
                suppliers.add(next);
            }
        }
        return suppliers;
    }

    private void addTotalPrice(Map<String, Object> supplier, Map<String, Object> next) {
        BigDecimal p_num = null, p_weight = null, p_price = null, p_other_charge = null, total_price = null;

        Object totalPrice = supplier.get("purchase_total_price");
        total_price = totalPrice != null ? (BigDecimal) totalPrice : new BigDecimal("0");
        try {
            p_num = new BigDecimal(next.get("purchase_num").toString());
            p_weight = new BigDecimal(next.get("purchase_weight").toString());
            p_price = new BigDecimal(next.get("purchase_price").toString());
            Object poc = next.get("purchase_other_charge");
            p_other_charge = new BigDecimal(poc != null ? poc.toString() : "0");
        } catch (Exception e) {
            return;
        }
        total_price = total_price.add(p_weight.multiply(p_price).add(p_other_charge.multiply(p_num)));
        supplier.put("purchase_total_price", total_price);
    }

    private void add(Map<String, Object> supplier, Map<String, Object> next, String key) {
        Object ao = supplier.get(key);
        BigDecimal a = new BigDecimal(ao != null ? ao.toString() : "0");
        Object bo = next.get(key);
        BigDecimal b = new BigDecimal(bo != null ? bo.toString() : "0");
        supplier.put(key, a.add(b));
    }

    /**
     * 生成新订单
     *
     * @param user
     * @param supplier_id 供应商id (可选)
     * @param product_id  商品id (可选)
     * @throws CustomException
     */
    @Transactional
    public void generatePurchaseTask(User user, String supplier_id, String product_id) throws CustomException {
        if (!auth.hasPermission(user.getRole_id(), auth.P_REFRESH_PURCHASE)) {
            throw new CustomException("你没有权限");
        }
        //1 获取新的订单和旧的订单的订购数量
        MySql mySql = new MySql();
        int effect = 0;
        //这里的user_id 就是供应商的id nickname就是供应商的name   status 2：以付款，3 是待发货
        mySql.append("SELECT  o.proxy_id ,o.`product_id`, o.`supplier_id`,SUM(o.`buy_num`) `buy_num`");
        mySql.notNullAppend("FROM `user_order` o,buy_task t,product p WHERE (`order_status` = ? or `order_status` = ?) "
                , OrderStatus.WAIT_CHECK.getStatus(), OrderStatus.WAIT_SEND.getStatus());
        mySql.notNullAppend("and o.product_id = p.id and p.group_id = t.group_id and t.user_id = ? ", user.getId());
        mySql.notNullAppend(" and `supplier_id` = ? ", supplier_id);
        mySql.notNullAppend(" and `product_id` = ?", product_id);
        mySql.append("GROUP BY  `proxy_id`,`product_id`,supplier_id");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());

        //2.更新新订单，如果更新数量为0表示没有新订单产生，抛出异常
        mySql = new MySql();
        mySql.notNullAppend("update user_order o,buy_task t,product p set o.`order_status` = ? where o.`order_status` = ? ",
                OrderStatus.WAIT_SEND.getStatus(), OrderStatus.WAIT_CHECK.getStatus());
        mySql.notNullAppend("and o.product_id = p.id and p.group_id = t.group_id and t.user_id = ? ", user.getId());
        mySql.notNullAppend(" and `supplier_id` = ? ", supplier_id);
        mySql.notNullAppend(" and `product_id` = ?", product_id);
        effect = jdbcTemplate.update(mySql.toString(), mySql.getValues());

        //3、如果有新订单就插入到代理商数据库中//
        mySql = new MySql();
        mySql.append("update proxy_order set buy_num = ? where order_status=? and proxy_id = ? and supplier_id = ? and product_id = ?");
        String insert = "insert into proxy_order (buy_num,order_status,proxy_id,supplier_id,product_id) values(?,?,?,?,?)";
        for (int i = 0; i < maps.size(); i++) {
            Map<String, Object> uorder = maps.get(i);
            mySql.addValue(uorder.get("buy_num"));
            mySql.addValue(OrderStatus.WAIT_CHECK.getStatus());//更新商品进入采购状态 2
            mySql.addValue(uorder.get("proxy_id"));
            mySql.addValue(uorder.get("supplier_id"));
            mySql.addValue(uorder.get("product_id"));
            effect = jdbcTemplate.update(mySql.toString(), mySql.getValues());
            if (effect == 0) {
                effect = jdbcTemplate.update(insert, mySql.getValues());
                if (effect == 0) throw new CustomException("生成失败");
            }
            mySql.cleanValues();
        }
    }

    /**
     * 获取商品列表（采购二级商品列表）
     *
     * @param user_id
     * @param supplier_id
     * @return
     */
    public List<Map<String, Object>> getProductsBySupplerId(Long user_id, String supplier_id) {
        MySql mySql = new MySql();
        mySql.append("SELECT p.pic_url,p.id,p.title,SUM(o.`buy_num`) `buy_num`,SUM(o.`purchase_num`) purchase_num,o.`purchase_price`,");
        mySql.append("p.price,SUM(purchase_weight) purchase_weight,SUM(`purchase_other_charge`*o.`purchase_num`) sub_other_price,");
        mySql.append("SUM(o.`purchase_price`*o.purchase_weight) sub_price,o.mark_price,o.purchase_other_charge,");
        mySql.append("p.unit,p.buy_unit,o.payed,o.remark,o.purchase_address");
        mySql.append("FROM `proxy_order` o,`product` p,buy_task b");
        mySql.notNullAppend("WHERE o.order_status = ? ", OrderStatus.WAIT_CHECK.getStatus());
        mySql.notNullAppend("and o.product_id = p.id and o.`supplier_id` = ?", supplier_id);
        if (!user_id.toString().equals(supplier_id)) {
            mySql.notNullAppend("and p.group_id = b.group_id and b.user_id = ? ", user_id);
        }
        mySql.append("GROUP BY o.`product_id`");
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());


        return maps;
    }


    /**
     * 根据商品ID获得商品的代理商 （采购三级界面）
     * 客户名、订购件数、总斤数、小计
     *
     * @param product_id
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getProxyProductByProductId(Long supplier_id, Long product_id) throws Exception {
        MySql mySql = new MySql();
        mySql.append("SELECT o.id,o.proxy_id,u.nickname,a.city,SUM(o.buy_num) `buy_num`,o.purchase_num,o.purchase_weight,");
        mySql.append("o.payed, o.purchase_address ,o.purchase_other_charge,o.mark_price,o.purchase_price,p.num product_num,");
        mySql.append("SUM(purchase_weight*purchase_price) sub_price");
        mySql.append("FROM `proxy_order` o,`user` u,`user_address` a,product p");
        mySql.notNullAppend("WHERE  o.order_status = ? and u.address_id = a.id and o.`proxy_id` = u.id and o.product_id = p.id",
                OrderStatus.WAIT_CHECK.getStatus());
        mySql.notNullAppend("and o.`supplier_id` = ?", supplier_id);
        mySql.notNullAppend("and o.`product_id` = ?", product_id);
        mySql.append("GROUP BY o.`proxy_id`");
        List<Map<String, Object>> data = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());
        return data;
    }


    /**
     * 采购商品界面
     * 保存修改商品价格（可以单独设置），每个订单都设置采购地址
     *
     * @param detailList
     * @param address
     * @param mark_price @return
     * @throws Exception
     */
    @Transactional
    public boolean savePurchase(List<Map<String, Object>> detailList, String payed, String product_id,
                                String address, Double pur_price, Double mark_price, Double other_charge, String remark) throws Exception {
        boolean isOk = false;
        if (pur_price != null) {
            BigDecimal pur = new BigDecimal(pur_price);
            BigDecimal mar = new BigDecimal(mark_price == null ? pur_price : pur_price);
            BigDecimal price = pur.add(mar).divide(new BigDecimal(2));
            //计算费用
            String updateItemSql = "update product set price=? ,price_sale=price+price*`percent_price`/100 WHERE `id` = ?";
            int effect = jdbcTemplate.update(updateItemSql, price, product_id);
            if (effect == 0) throw new CustomException("保存数据失败");
        }
        String update = "update proxy_order set purchase_num = ?,purchase_weight = ?,mark_price = ?," +
                "purchase_address = ?,purchase_price = ?,payed = ?,purchase_other_charge = ?,remark = ?where id = ?";
        if (null != detailList && detailList.size() > 0) {
            int[] ints = jdbcTemplate.batchUpdate(update, new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Map<String, Object> item = detailList.get(i);
                    ps.setObject(1, item.get("purchase_num"));
                    ps.setObject(2, item.get("purchase_weight"));
                    ps.setObject(3, mark_price);
                    ps.setObject(4, address);
                    ps.setObject(5, pur_price);
                    ps.setObject(6, payed);
                    ps.setObject(7, other_charge);
                    ps.setObject(8, remark);
                    ps.setObject(9, item.get("id"));
                }

                public int getBatchSize() {
                    return detailList.size();
                }
            });
            isOk = ints.length > 0;
        }
        return isOk;
    }

}
