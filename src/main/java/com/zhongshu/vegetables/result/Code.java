package com.zhongshu.vegetables.result;

public enum Code {

	SUCCESS(1,"请求成功"),
	ERROR(0,"请求失败"),
	REPEAT(9,"重复提交"),
	NO_DATA(2,"暂无数据"),
	EXP_PARAM(3,"参数错误"),
	NO_AUTH(4,"权限不足"),
	EXP_TOKEN(5,"token错误"),
	EXP_SIGNATURE(6,"无效的签名"),
	DISABLED(7,"该账号已被禁止使用"),
	PWD_ERROR(8,"用户名或密码错误！");
	
	private int status;
	
	private String message;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	private Code(int status,String message){
		this.status = status;
		this.message = message;
	}
	
}
