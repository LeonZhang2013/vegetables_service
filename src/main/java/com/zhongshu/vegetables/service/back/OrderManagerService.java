package com.zhongshu.vegetables.service.back;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.config.Alipay;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.MultiResult;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.*;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * 订单管理
 * 订单操作，前提是待核算操作，将待核算的订单的商品采购地址同步，
 * 只要有一个订单的商品存在采购地址，则另一个订单的相同商品也修改为该采购地址
 *
 * @author lynn
 */
@Component
@Transactional
public class OrderManagerService extends BaseService {

    private static final int REFUND = 4;
    private static final int PROFIT = 2;
    private static final int CASH = 3;


    /**
     * 获得订单状态列表
     *
     * @return
     * @throws Exception
     */
    public MultiResult<Map<String, Object>> getOrderStatus() throws Exception {
        MultiResult<Map<String, Object>> result = new MultiResult<>();
        List<Map<String, Object>> data = OrderStatus.getOrderStatus();
        if (null != data && data.size() > 0) {
            result.setCode(Code.SUCCESS);
            result.setData(data);
        } else {
            result.setCode(Code.NO_DATA);
        }
        return result;
    }

    /**
     * 根据获取实时 订单价格。
     * 关键词：订单编号
     * 列表字段：下单日期、订单号、订单总价、状态
     * 待核算（包括）之前需要根据商品来计算，待核算（不包括）之后直接取数
     *
     * @param status
     * @param key
     * @param pager
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrderList(Integer status, String key, Pager<Map<String, Object>> pager, User user) throws Exception {

        MySql mySql = new MySql();
        mySql.append(" SELECT o.id sub_id,u.id user_id,o.order_id id,o.order_status ,u.nickname,a.mobile ,a.city,a.address,os.name status_name,");
        mySql.append("SUM(o.sub_price) `product_total_price`,SUM(o.buy_num) buy_num,SUM(o.sub_freight) total_freight,o.create_time  ");
        mySql.append(" FROM `user` u,`user_order` o,user_address a ,order_status os ");
        mySql.append(" WHERE u.id = o.user_id and a.user_id = u.id and os.id = o.order_status ");
        mySql.notNullAppend("  and o.order_status = ? ", status);
        key = SQLTools.FuzzyKey(key);
        mySql.notNullAppend(" and (o.order_id like ? or u.nickname like ?) ", key, key);
        //如果有管理订单的功能查询多订单，跳过括号内的sql 判断
        if (!auth.hasPermission(user.getRole_id(), auth.P_MgOrder)) {
            mySql.notNullAppend(" and (u.id=? or u.parent_id = ?) ", user.getId(), user.getId());
        }
        mySql.append("GROUP BY o.order_id");
        mySql.orderByDesc("o.create_time");
        mySql.limit(pager);
        List<Map<String, Object>> data = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> order = data.get(i);
            order.put("create_time", priseTimestamp((Timestamp) order.get("create_time")));
            if (auth.hasPermission(user.getRole_id(), auth.P_DISTRIBUTION)) {//有权限
                switch (OrderStatus.getOrderStatus(status)) {
                    case WAIT_PAY:
                    case DISTRIBUTION:
                        order.put("oper_status", "true");
                }
            }
            if (auth.hasPermission(user.getRole_id(), auth.P_MgOrder)) {//有权限
                switch (OrderStatus.getOrderStatus(status)) {
                    case WAIT_PAY:
                        order.put("oper_status", "true");
                }
            }
            if (user.getId().toString().equals(order.get("user_id").toString())) {
                switch (OrderStatus.getOrderStatus(status)) {
                    case WAIT_PAY:
                    case WAIT_OK:
                        order.put("oper_status", "true");
                }
            }
        }

        return data;
    }

    private Object priseTimestamp(Timestamp timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp.getTime());
        return DateUtils.parseCalendar2String(calendar, "yyyy-MM-dd HH:mm:ss");
    }


    /**
     * 获得订单详情
     * 客户姓名、客户电话、收货地址、备注、下车费、运费、订单总价、商品数量、详情按钮
     * 订单商品详情（商品标题、商品图片、订购数量、商品单价、小计）
     * 待付款和待核算的订单价格需要计算（预估价）
     *
     * @param order_id
     * @param status
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getOrderDetail(Long order_id, Integer status) throws Exception {
        MySql mySql = new MySql();
        mySql.append("select o.*,p.title,p.num product_num,p.unit,p.buy_unit,p.pic_url,os.name status_name ");
        mySql.append("from user_order o,`product` p,order_status os");
        mySql.append("where p.id = o.product_id  and os.id = o.order_status ");
        mySql.append("and o.order_id = ?");
        mySql.addValue(order_id);
        mySql.notNullAppend("and o.order_status = ?", status);
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(mySql.toString(), mySql.getValues());
        return maps;
    }

    /**
     * 订单操作
     * 待付款 待核算 待发货前可填写运费和上车费
     * 之后的订单不用填写
     * 获得当前订单状态，改变为下一个状态
     * 确认收款后，将订单合并
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @Transactional
    public boolean operateOrder(Long orderId, Integer status, Long operId) throws Exception {
        if (status.intValue() == OrderStatus.DISTRIBUTION.getStatus()) {
            calcuRebate(orderId, status);
        }
        return orderDao.operateOrder(orderId, status, operId);
    }


    /**
     * 取消订单
     *
     * @param order_id
     * @param sub_id
     * @return
     * @throws Exception
     */
    public boolean cancelOrder(Long order_id, Long sub_id) throws Exception {
        if (order_id == null && sub_id == null) {
            throw new CustomException("没有填写删除条件");
        }
        MySql mySql = new MySql();
        mySql.append("update user_order set `order_status` = ? where ");
        mySql.addValue(OrderStatus.CANCEL.getStatus());
        if (order_id != null) {
            mySql.notNullAppend(" order_id = ?", order_id);
        } else {
            mySql.notNullAppend(" id = ?", sub_id);
        }
        int update = jdbcTemplate.update(mySql.toString(), mySql.getValues());
        return update > 0;
    }

    /**
     * 提交订单
     * 用户ID、地址ID
     * 商品ID、订单ID、数量、用户ID
     * 价格通过商品来关联，因此价格字段暂时为空
     * 核算后将价格字段填充
     *
     * @return
     * @throws Exception
     */
    @Transactional
    public BigDecimal addOrder(Long user_id, Long order_id, Long role_id, String proxy_id) throws Exception {
        BigDecimal user_percent = userDao.getUserPercent(user_id);
        BigDecimal userPreFreight = userDao.getUserFreight(user_id, role_id);
        return orderDao.saveOrder(user_id, order_id, proxy_id, user_percent, userPreFreight);
    }

    public void generateSupplierOrder(Long userId, String supplierId, String payMoney, String price) throws CustomException {
        String sql = "update supplier_order set payed_price = ?, should_pay= ?, oper_id = ? where supplier_id=? and  DATEDIFF(create_time,NOW())=0  ";
        int effect = jdbcTemplate.update(sql, payMoney, price, userId, supplierId);
        if (effect == 0) {
            sql = "insert into supplier_order (payed_price,should_pay,oper_id,supplier_id) values(?,?,?,?)";
            effect = jdbcTemplate.update(sql, payMoney, price, userId, supplierId);
            if (effect == 0)
                throw new CustomException("保存失败");
        }
    }


    public void payOk(String order_id, OrderStatus waitPay) {
        String sql = "update user_order set `order_status` = ? where order_id = ? and order_status = ?";
        jdbcTemplate.update(sql, OrderStatus.WAIT_CHECK.getStatus(), order_id, waitPay.getStatus());
    }

    @Transactional
    public boolean deliverProduct(User user, String freight, String weight, String termini) throws CustomException {
        //更新订单状态为 发货状态（4）
        if (weight == null) weight = "0";
        if (termini == null) termini = "无";
        String sql = "insert into freight (freight,oper_id,weight,termini) values(?,?,?,?) ";
        int update = jdbcTemplate.update(sql, freight, user.getId(), weight, termini);

        boolean updateStatus = updateStatus(user);
        return updateStatus;
    }

    /**
     * 改变状态为发货,计算利润。
     *
     * @param user
     * @return
     * @throws CustomException
     */
    @Transactional
    public boolean updateStatus(User user) throws CustomException {
        String sql = "update user_order o,product p,buy_task t set o.order_status  = ? where o.order_status = ? " +
                "and o.product_id = p.id and p.group_id = t.group_id and t.user_id = ? ";
        int update = jdbcTemplate.update(sql, OrderStatus.SEND.getStatus(), OrderStatus.WAIT_SEND.getStatus(), user.getId());
        sql = "update proxy_order o,product p,buy_task t set o.order_status  = ? where o.order_status = ? " +
                "and o.product_id = p.id and p.group_id = t.group_id and t.user_id = ? ";
        update = jdbcTemplate.update(sql, OrderStatus.WAIT_SEND.getStatus(), OrderStatus.WAIT_CHECK.getStatus(), user.getId());
        //if (update == 0) throw new CustomException("发货失败");
        return true;
    }


    @Transactional
    public synchronized String withdrawals(Map<String, Object> wallet, String zfb, String amount) throws AlipayApiException, CustomException {
        BigDecimal allAmount = new BigDecimal(wallet.get("amount").toString());
        BigDecimal castAmount = new BigDecimal(amount);
        if (allAmount.compareTo(castAmount) < 0) {
            throw new CustomException("余额不足");
        }

        //修改数据库。
        String user_id = wallet.get("user_id").toString();
        int effect = jdbcTemplate.update("UPDATE wallet set amount = amount - ? where user_id = ?", amount, user_id);
        if (effect == 0) throw new CustomException("提现失败");

        AlipayClient alipayClient = Alipay.generateAlipayClient("GBK");
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("out_biz_no", System.currentTimeMillis());
        jsonObject.put("payee_type", "ALIPAY_LOGONID");
        jsonObject.put("payee_account", zfb);
        jsonObject.put("amount", amount);
        //jsonObject.put("payer_show_name","上海交通卡退款");
        //jsonObject.put("payee_real_name","唐雪冰");
        //jsonObject.put("remark","转账备注");
        String d = jsonObject.toJSONString();
        request.setBizContent(d);
        AlipayFundTransToaccountTransferResponse response = alipayClient.execute(request);

        if (response.isSuccess()) {//成功 添加记录 和设置支付宝
            jdbcTemplate.update("UPDATE wallet SET zhifubao = ? WHERE user_id = ?",zfb,user_id);
            insertRecord(user_id, CASH, "提现",allAmount.subtract(castAmount).toString() ,"-"+ castAmount.toString(), "-1");
        }else{//失败 处理把货币修改获取。回去
            jdbcTemplate.update("UPDATE wallet set amount = amount + ? where user_id = ?", amount, user_id);
            throw new CustomException(response.getSubMsg());
        }
        return "提现成功";
    }

    public String getPaymentInfo(String order_id, BigDecimal totalPrice, String desc) throws CustomException {
        if (totalPrice.floatValue() <= 0) {
            throw new CustomException("费用必须大于0");
        }

        String result = "生成订单失败";
        //实例化客户端
        AlipayClient alipayClient = Alipay.generateAlipayClient();
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(desc == null ? "" : desc);
        model.setSubject(desc == null ? "世纪中蔬" : desc);
        model.setOutTradeNo(order_id.toString());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(totalPrice.setScale(2, BigDecimal.ROUND_UP).toString());
       // model.setTotalAmount("0.01");
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(Alipay.LISTENER);
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            result = response.getBody();
        } catch (AlipayApiException e) {
            result = e.getErrMsg();
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 计算利润
     * @param order_id
     * @param status
     */
    @Transactional
    public void calcuRebate(Long order_id, Integer status) {
        List<Map<String, Object>> data = jdbcTemplate.queryForList("select o.*,p.title,p.buy_unit from user_order o,product p where o.product_id= p.id and  o.order_id = ? and o.order_status = ?", order_id, status);
        if (data.size() == 0) return;
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal zero = new BigDecimal("0");
        String user_id = data.get(0).get("user_id").toString();
        String add_profit = "UPDATE wallet SET amount = amount+? WHERE user_id = ?";
        for (int i = 0; i < data.size(); i++) {
            Map<String, Object> item = data.get(i);
            BigDecimal buy_num = new BigDecimal(item.get("buy_num").toString());
            BigDecimal purchase_num = new BigDecimal(item.get("purchase_num").toString());
            BigDecimal piece_price = new BigDecimal(item.get("piece_price").toString());

            //计算代理商  如果采购数量大于0就计算 反点利润
            if (zero.compareTo(purchase_num) != 0) {
                totalAmount = totalAmount.add(purchase_num.multiply(piece_price));
            }

            //计算用户退费 存在漏洞,暂时人工退款．
//            if (buy_num.compareTo(purchase_num) != 0) {
//                BigDecimal offset = buy_num.subtract(purchase_num);
//                BigDecimal offsetPrice = offset.multiply(piece_price);
//                //计算运费退还
//                BigDecimal sub_freight = new BigDecimal(item.get("sub_freight").toString());
//                BigDecimal tui_fright = sub_freight.divide(buy_num, BigDecimal.ROUND_HALF_UP);
//                offsetPrice = offsetPrice.add(tui_fright);
//                //================更新钱包==================
//                jdbcTemplate.update(add_profit, offsetPrice, user_id);
//                BigDecimal amount = walletDao.getAmount(user_id);
//                //================插入记录=============
//                insertRecord(user_id, REFUND, "退款-"+item.get("title")+"-"+offset+item.get("buy_unit"), "+"+amount.toString(), offsetPrice.toString(), order_id.toString());
//            }
        }

        //代理商反点
        //==================计算利益===============
        BigDecimal percent_rebate = new BigDecimal(data.get(0).get("percent_rebate").toString());
        String proxy_id = data.get(0).get("proxy_id").toString();
        BigDecimal offsetAmount = totalAmount.multiply(percent_rebate).divide(new BigDecimal(100), BigDecimal.ROUND_HALF_UP);


        //=================更新钱包=================
        jdbcTemplate.update(add_profit, offsetAmount, proxy_id);
        //================插入记录=============
        BigDecimal amount = walletDao.getAmount(proxy_id);
        insertRecord(proxy_id, PROFIT, "收益-总货款: "+totalAmount.toString()+" 利润:"+percent_rebate+"%", amount.toString(), "+"+offsetAmount.toString(), order_id.toString());
    }

    /**
     * 记录
     * @param user_id   用户id
     * @param type  类型
     * @param remark  描述
     * @param amount  修改后金额
     * @param userAmount  修改金额
     * @param order_id 订单id   选填
     */
    public void insertRecord(String user_id, int type, String remark, String amount, String userAmount, String order_id) {
        String insert_record = "INSERT INTO wallet_record (user_id,record_type,remark,amount,amount_offset,user_order_id) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.update(insert_record, user_id, type, remark, amount, userAmount, order_id);
    }
}
