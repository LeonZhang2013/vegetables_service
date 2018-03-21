package com.zhongshu.vegetables.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.dao.beans.Pager;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MySql {

    StringBuffer sql;
    List<Object> values = new ArrayList<>();

    public MySql(String s) {
        sql = new StringBuffer(s);
        sql.append(" ");
    }

    public MySql() {
        sql = new StringBuffer();
    }

    public void append(String s) {
        sql.append(" ").append(s).append(" ");
    }

    public void limit(Pager<Map<String, Object>> pager) {
        sql.append(" limit ");
        sql.append((pager.getPage() - 1) * pager.getRows());
        sql.append(",");
        sql.append(pager.getRows()).append(" ");

    }

    public void orderBy(String... columns) {
        if (columns.length == 0) return;
        sql.append(" order by ");
        for (String column : columns) {
            sql.append(column).append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" ");
    }

    public void orderByDesc(String... columns) {
        if (columns.length == 0) return;
        sql.append(" order by ");
        for (String column : columns) {
            sql.append(column).append(" desc,");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(" ");
    }




    @Override
    public String toString() {
        System.out.println("==========================sqsqsqsqsqsqsqs===============================");
        System.out.println( sql.toString());
        for (int i=0; i<values.size(); i++){
            System.out.print(values.get(i).toString());
        }
        System.out.println();
        System.out.println("===========================qlqlqlqlqlqlqlql==============================");
        return sql.toString();
    }

    public void notNullAppend(String s, Object... keys) {
        boolean isOk = true;
        List<Object> temp = new ArrayList<>();
        for (Object key : keys) {
            if (StringUtils.isNotNull(key)&&!key.toString().contains("null")) {
                temp.add(key);
            } else {
                isOk = false;
                break;
            }
        }
        if (isOk) {
            append(s);
            values.addAll(temp);
        }
    }


    public Object[] getValues() {
        return values.toArray();
    }

    public void addValue(Object value) {
        values.add(value);
    }

    public void createInsert(Object bean, String tableName) {
        SQL sql1 = new SQL() {{
            INSERT_INTO(tableName);
            JSONObject userMap = (JSONObject) JSON.toJSON(bean);
            for (Map.Entry item : userMap.entrySet()) {
                if (StringUtils.isNotNull(item.getValue())) {
                    INTO_COLUMNS("`" + item.getKey() + "`");
                    INTO_VALUES("?");
                    values.add(item.getValue());
                }
            }
        }};
        sql.append(sql1.toString());
    }

    public void clean() {
        sql = new StringBuffer();
        values.clear();
    }

    public void cleanValues() {
        values.clear();
    }

    public long getCount(JdbcTemplate jdbcTemplate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(*) num ").append(sql.substring(sql.indexOf("from"),sql.length()));
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sb.toString(), getValues());
        return (long)maps.get(0).get("num");
    }

    public void update(String s) {
        sql.append(s);
    }


}
