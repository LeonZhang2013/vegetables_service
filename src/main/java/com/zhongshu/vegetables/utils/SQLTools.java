package com.zhongshu.vegetables.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.dao.beans.Pager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public  class SQLTools {

    public static SqlInfo getInsertSQL(Object obj,String tableName) {
        Map<String, Object> toMap = (JSONObject) JSON.toJSON(obj);
        StringBuffer sql = new StringBuffer("insert into "+tableName+" (");
        StringBuffer flag = new StringBuffer(") values (");
        List<Object> list = new ArrayList<>();
        for (Map.Entry item : toMap.entrySet()) {
            if (item.getValue() != null) {
                sql.append(item.getKey()).append(",");
                flag.append("?,");
                list.add(item.getValue());
            }
        }
        sql.deleteCharAt(sql.length() - 1);
        flag.deleteCharAt(flag.length() - 1);
        sql.append(flag).append(")");
        return new SqlInfo(sql.toString(),list);
    }


    public static SqlInfo getUpdateById(Object obj, String tableName, Long id) {
        Map<String, Object> toMap = (JSONObject) JSON.toJSON(obj);
        StringBuffer sql = new StringBuffer("update "+tableName+" set ");
        List<Object> list = new ArrayList<>();
        for (Map.Entry item : toMap.entrySet()) {
            if(!item.getKey().equals("id"))
                if (item.getValue() != null) {
                    sql.append(item.getKey()).append("=?,");
                    list.add(item.getValue());
                }
        }
        list.add(id);
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" where id=?");
        return new SqlInfo(sql.toString(),list);
    }

    public static void limit(StringBuilder sql, Pager<Map<String, Object>> pager) {
        sql.append(" limit ");
        sql.append((pager.getPage()-1)*pager.getRows());
        sql.append(",");
        sql.append(pager.getRows());
    }

    public static String FuzzyKey(String keyword) {
        if(keyword!=null&&keyword.length()>0){
            StringBuilder sb = new StringBuilder();
            char[] chars = keyword.toCharArray();
            for (int i=0; i<chars.length; i++){
                sb.append("%");
                sb.append(chars[i]);
            }
            sb.append("%");
            keyword = sb.toString();
        }
        return keyword;
    }

    public static void removePass(List<Map<String, Object>> data) {
        for (int i=0; i<data.size();i++){
            data.get(i).remove("password");
        }
    }
}
