package com.zhongshu.vegetables.dao.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单状态
 * 待付款、待核算、待发货、已发货、已收货、已完成
 *
 * @author 李熠
 */
public enum OrderStatus {

    /**
     * 待付款
     */
    WAIT_PAY(1, "待付款", "待付款"),
    /**
     * 待采购
     */
    WAIT_CHECK(2, "已付款", "采购中"),
    /**
     * 贷发货
     */
    WAIT_SEND(3, "待发货", "待发货"),
    /**
     * 待发货
     */
    SEND(4, "已发货", "待收货"),
    /**
     * 待分配
     */
    DISTRIBUTION(5, "配送中", "完成"),
    /**
     *
     */
    WAIT_OK(6, "待确认", "完成"),
    /**
     *
     */
    COMPLETE(7, "已完成", "完成"),
    /**
     * 已取消
     */
    CANCEL(0, "已取消", "已取消");

    private int status;

    private String message;

    private String btn;

    private OrderStatus(int status, String message, String btn) {
        this.status = status;
        this.message = message;
        this.btn = btn;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus() {
        return status;
    }

    public String getBtn() {
        return btn;
    }

    public static OrderStatus getOrderStatus(int status) {
        OrderStatus orderStatus = OrderStatus.WAIT_PAY;
        OrderStatus orderStatuses[] = OrderStatus.values();
        for (OrderStatus item : orderStatuses) {
            if (status == item.status) {
                return item;
            }
        }
        return orderStatus;
    }

    public static List<Map<String, Object>> getOrderStatus() {
        List<Map<String, Object>> list = new ArrayList<>();
        OrderStatus orderStatuses[] = OrderStatus.values();
        for (OrderStatus orderStatus : orderStatuses) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", orderStatus.getStatus());
            map.put("name", orderStatus.getMessage());
            list.add(map);
        }
        return list;
    }

    public boolean cancel() {
        switch (this) {
            case WAIT_PAY:
            case WAIT_CHECK:
                return true;
            default:
                return false;
        }
    }

    public OrderStatus next() {
        switch (this) {
            case WAIT_PAY:
                return WAIT_CHECK;
            case WAIT_CHECK:
                return WAIT_SEND;
            case WAIT_SEND:
                return SEND;
            case SEND:
                return DISTRIBUTION;
            case DISTRIBUTION:
                return WAIT_OK;
            case WAIT_OK:
                return COMPLETE;
            default:
                return CANCEL;
        }
    }

}
