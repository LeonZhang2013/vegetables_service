package com.zhongshu.vegetables.dao.beans;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

/**
 * 后台easyui要求格式
 * @author hp
 *
 * @param <T>
 */
public class Pager<T> {

	private int rows = 10;//每页显示多少条
	
	private int page = 1;//当前页
	
	private long total = 0;
	
	private List<T> data = new ArrayList<T>();
	
	public long getTotalPage() {
		return (long)Math.ceil((double)total / (double)rows);
	}

	public long getTotal() {
		return total;
	}
	
	public void setTotal(long total) {
		this.total = total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}
	
	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public List<T> getData() {
		return data;
	}
	
	public void setData(List<T> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}
}
