package com.zhongshu.vegetables.dao;


import com.zhongshu.vegetables.bean.Attribute;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.utils.SQLTools;
import com.zhongshu.vegetables.utils.SqlInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
public class AttributeDao {


    @Autowired
    JdbcTemplate jdbcTemplate;

    public int updateById(Attribute attribute) {
        SqlInfo sqlInfo = SQLTools.getUpdateById(attribute, attribute.TableName, attribute.getId());
        return jdbcTemplate.update(sqlInfo.getSql(), sqlInfo.getValues());
    }

    public int insert(Attribute attribute) {
        SqlInfo sqlInfo = SQLTools.getInsertSQL(attribute, attribute.TableName);
        return jdbcTemplate.update(sqlInfo.getSql(), sqlInfo.getValues());
    }

    public List<Map<String, Object>> getAttributes(Integer islist) {
        String sql = "select * from `attribute` ";
        if (islist != null) {
            sql += " WHERE islist = " + islist;
        }
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getSearchKeys(Long attribute_id, Integer islist) {
        String sql = "select * from search_key where attribute_id = ?";
        if (islist != null) {
            sql += " and islist = " + islist;
        }
        return jdbcTemplate.queryForList(sql, attribute_id);
    }

    public int updateSearchKey(Long id, String name, int islist, long attribute_id) {
        String sql = "update search_key set name = ?,islist = ?,attribute_id = ? where id = ?";
        int update = jdbcTemplate.update(sql, name, islist, attribute_id, id);
        return update;
    }

    public int insertSearchKey(String name, int islist, long attribute_id) throws CustomException {
        String sql = "select id from search_key where name = ? and attribute_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, name, attribute_id);
        if (maps.size() > 0) {
            throw new CustomException("关键字已存在");
        }
        sql = "insert into search_key (id,name,islist,attribute_id) values (null,?,?,?)";
        int update = jdbcTemplate.update(sql, name, islist, attribute_id);
        return update;
    }
}
