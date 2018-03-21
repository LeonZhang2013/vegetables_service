package com.zhongshu.vegetables.service.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.Arith;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 辅助下单
 *
 * @author lynn
 */
@Component
@Transactional
public class OrderService extends BaseService {


    /**
     * 获得购物车商品总量
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public SingleResult<Long> getCartNum(Long userId) throws Exception {
        SingleResult<Long> result = new SingleResult<>();
        Map<String, Object> map = entityManagerDao.executeQuery("SELECT SUM(num) num FROM shop_cart WHERE user_id = :userId", Entity.options().add("userId", userId).build());
        if (null != map && map.size() > 0 && map.get("num") != null) {
            result.setCode(Code.SUCCESS);
            result.setData(Long.parseLong(map.get("num") + ""));
        } else {
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    /**
     * 加入购物车
     * 如果是同样的商品，则直接修改，加上num
     *
     * @param user_id
     * @param product_id
     * @param num
     * @return
     * @throws Exception
     */
    public void addShopcart(Long user_id, Long product_id, Integer num) throws Exception {
        if (!calcuStory(product_id, num)) {
            throw new CustomException("库存不足");
        }
        String sql = "select * from shop_cart where user_id = ? and product_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user_id, product_id);
        int effect = 0;
        if (null != maps && maps.size() > 0) {
            Map<String, Object> map = maps.get(0);
            num += Integer.parseInt(map.get("num").toString());
            sql = "update shop_cart set num = ? where id = ?";
            effect = jdbcTemplate.update(sql, num, map.get("id"));
        } else {
            sql = "insert into shop_cart (user_id,product_id,num) VALUES (?,?,?)";
            effect = jdbcTemplate.update(sql, user_id, product_id, num);
        }
        if (effect == 0) {
            throw new CustomException("添加失败");
        }
    }

    /**
     * 计算库存
     */
    public boolean calcuStory(Long product_id, Integer num) {
        String sql = "select stock from product WHERE id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, product_id);
        int stock = 0;
        if (null != maps && maps.size() > 0) {
            Map<String, Object> map = maps.get(0);
            stock = Integer.parseInt(map.get("stock").toString());
            if (stock == -1) return true;
            stock = stock - num;
            if (stock >= 0) {
                sql = "update product set stock = ? where id = ?";
                int update = jdbcTemplate.update(sql, stock);
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * 获得用户的购物车列表
     * 图片、标题、单价、数量、合计
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getShopcartList(Long userId) throws Exception {
        List<Map<String, Object>> data = orderDao.getUserCart(userId);
        BigDecimal user_percent = userDao.getUserPercent(userId);
        Arith.calProductSalePrice(data, user_percent);
        return data;
    }

    /**
     *  购物车其中一个商品数量加减数量
     *
     * @param cartId
     * @return
     * @throws Exception
     */
    public boolean setShopcart(Long cartId, Long userId, Integer num) throws Exception {
        String sql = "update shop_cart set num = num+? where id = ? and user_id = ?";
        int update = jdbcTemplate.update(sql, num, cartId, userId);
        return update > 0;
    }

    /**
     * 删除购物车
     *
     * @param cartId
     * @param userId
     * @return
     * @throws Exception
     */
    public SingleResult<String> deleteShopcart(Long cartId, Long userId) throws Exception {
        SingleResult<String> result = new SingleResult<>();
        Entity entity = Entity.options().add("cartId", cartId).add("userId", userId).build();
        entityManagerDao.executeUpdate("delete from shop_cart where id = :cartId and user_id = :userId", entity);
        result.setCode(Code.SUCCESS);
        return result;
    }

    /**
     * 批量删除购物车
     *
     * @param idList
     * @param userId
     * @return
     * @throws Exception
     */
    public SingleResult<String> batchDeleteShopcart(List<Long> idList, Long userId) throws Exception {
        SingleResult<String> result = new SingleResult<>();
        Entity entity = Entity.options().add("idList", idList).add("userId", userId).build();
        entityManagerDao.executeUpdate("delete from shop_cart where id in :idList and user_id = :userId", entity);
        result.setCode(Code.SUCCESS);
        return result;
    }

    /**
     * 清空购物车
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public SingleResult<String> clearShopcart(Long userId) throws Exception {
        SingleResult<String> result = new SingleResult<>();
        Entity entity = Entity.options().add("userId", userId).build();
        entityManagerDao.executeUpdate("delete from shop_cart where user_id = :userId", entity);
        result.setCode(Code.SUCCESS);
        return result;
    }

    /**
     * 获得用户选择的购物车列表
     * 图片、标题、单价、数量、合计
     *
     * @param userId
     * @return
     * @throws Exception
     */
    public MultiResult<Map<String, Object>> getShopcartByIdList(List<Long> idList, Long userId) throws Exception {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        Entity entity = Entity.options().add("userId", userId).add("idList", idList).build();
        String sql = "select  s.id id,i.id product_id,i.title title,i.pic_url pic_url,i.price price,s.num num,i.num item_num,(select name from unit where id = i.buy_unit_id) buy_unit,(select name from unit where id = i.unit_id) unit from shop_cart s,item i where s.product_id = i.id and s.id in :idList and s.user_id = :userId";
        List<Map<String, Object>> data = entityManagerDao.executeQuery(sql, entity, null, 0);
        if (null != data && data.size() > 0) {
            for (Map<String, Object> map : data) {
                double price = Double.parseDouble(map.get("price") + "");
                int num = Integer.parseInt(map.get("num") + "");
                map.put("total", Arith.mul(price, num));
            }
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } else {
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    public void batchSaveCart(String user_id, String carts) {
        final JSONArray cartItems = JSON.parseArray(carts);
        orderDao.batchUpdate(user_id, cartItems);
    }

    public BigDecimal getShopCartFreight(Long userId, Long role_id) throws CustomException {
        BigDecimal perFreight = userDao.getUserFreight(userId, role_id);
        return orderDao.calculationFreight(userId, perFreight).getFreight();
    }


    public BigDecimal getUserOrderPrice(String order_id) {
        String sql = "select sum(sub_price)+sum(sub_freight) totalPrice from user_order where order_id = ? ";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, order_id);
        return new BigDecimal(maps.get(0).get("totalPrice").toString());
    }
}
