package com.zhongshu.vegetables.dao.beans;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Entity implements Serializable{

	private static final long serialVersionUID = -7347892315313291226L;
	private Map<String,Object> params = new HashMap<>();
	private String table;
	
	public static class Builder{
		private Map<String,Object> params = new HashMap<>();
		private String table;
		
		public Builder add(String key ,Object value){
			this.params.put(key, value);
			return this;
		}
		
		public Builder addAll(Map<String,Object> params){
			this.params.putAll(params);
			return this;
		}
		
		public Builder table(String table){
			this.table = table;
			return this;
		}
		
		public Entity build(){
			return new Entity(this);
		}
	}
	
	public static Builder options(){
		return new Builder();
	}
	
	public void add(String key,Object value){
		params.put(key, value);
	}
	
	public void addAll(Map<String,Object> map){
		params.putAll(map);
	}
	
	public void remove(String key){
		params.remove(key);
	}
	
	private Entity(Builder builder){
		params.putAll(builder.params);
		this.table = builder.table;
	}
	
	public Map<String,Object> getField(){
		return params;
	}
	
	public Object get(String key){
		return params.get(key);
	}
	
	public String getTable() {
		return table;
	}
	
	public void setTable(String table) {
		this.table = table;
	}
	
	@Override
	public String toString() {
		return params.toString();
	}
	
	@SuppressWarnings("unchecked")
	public static Entity parse(String json){
		Map<String,Object> map = (Map<String,Object>)JSON.parse(json);
		return new Entity.Builder().addAll(map).build();
	}
}
