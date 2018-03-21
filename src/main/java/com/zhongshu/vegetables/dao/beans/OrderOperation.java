package com.zhongshu.vegetables.dao.beans;

/**
 * 订单操作枚举
 * @author
 *
 */
public enum OrderOperation {

	/**
	 * 下单
	 */
	PLACE_ORDER(1),
	/**
	 * 确认付款
	 */
	CONFIRM_PAY(2),
	/**
	 * 核算
	 */
	CHECK(3),
	/**
	 * 发货 
	 */
	SEND(4),
	/**
	 * 完成
	 */
	COMPLETE(5),
	/**
	 * 取消
	 */
	CANCEL(6);
	
	private int operation;
	
	private OrderOperation(int operation){
		this.operation = operation;
	}
	
	public int getOperation() {
		return operation;
	}
	
}
