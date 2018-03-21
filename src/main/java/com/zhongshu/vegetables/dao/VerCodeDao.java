package com.zhongshu.vegetables.dao;

import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.bean.VerCode;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class VerCodeDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    public void saveCode(String phone, String verCode, Date expirseDate) {
        String sql = "insert into vercode (phone,code,expires_in) values(?,?,?)";
        jdbcTemplate.update(sql,phone,verCode,expirseDate);
    }

    public List<String> getVerCode(String phone) {
        String sql = "select code from vercode where phone=?";
        return jdbcTemplate.queryForList(sql,String.class,phone);
    }

    public int delCodeByPhone(String phone) {
        String sql = "DELETE from vercode where phone=?";
        return jdbcTemplate.update(sql,phone);
    }
}
