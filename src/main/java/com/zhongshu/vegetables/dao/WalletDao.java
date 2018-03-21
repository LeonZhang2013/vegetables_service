package com.zhongshu.vegetables.dao;

import com.zhongshu.vegetables.dao.beans.Pager;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.utils.MySql;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Repository
public class WalletDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    public boolean hasWallet(Long id) {
        String sql = "select idcard from wallet where user_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, id);
        return maps.get(0).get("idcard") != null;
    }

    public boolean updateWallet(Long id, String idcard, String password) {
        String sql = "UPDATE wallet set idcard = ?,password = ? WHERE user_id = ?";
        int update = jdbcTemplate.update(sql,idcard, password,id);
        return update > 0;
    }

    public boolean createWallet(Long id) {
        String sql = "INSERT INTO wallet (user_id) VALUES (?)";
        int update = jdbcTemplate.update(sql, id);
        return update > 0;
    }

    public String getIdCard(String user_id) throws CustomException {
        String sql = "SELECT idcard FROM wallet WHERE user_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user_id);
        if (maps.size() == 0) {
            throw new CustomException("身份证不正确");
        }
        return maps.get(0).get("idcard").toString();
    }

    public Map<String, Object> verPass(Long user_id, String encryptPass) throws CustomException {
        String sql = "SELECT * FROM wallet WHERE user_id = ?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, user_id);
        Map<String, Object> map = maps.get(0);
        if (!map.get("password").toString().equals(encryptPass)) {
            throw new CustomException("密码错误");
        }
        if (map.get("enable").toString().equals("0")) {
            throw new CustomException("钱包不可用");
        }
        map.remove("password");
        map.remove("idcard");
        return map;
    }

    public List<Map<String, Object>> getRecordTypes() {
        String sql = "SELECT * FROM record_type";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    public List<Map<String, Object>> getRecords(Pager<Map<String, Object>> pager, String user_id, Integer type) {
        MySql mySql = new MySql();
        mySql.append("SELECT * FROM wallet_record where user_id = ? and record_type = ?");
        mySql.limit(pager);
        return jdbcTemplate.queryForList(mySql.toString(),user_id,type);
    }

    public BigDecimal getAmount(String user_id) {
        MySql mySql = new MySql();
        mySql.append("SELECT amount FROM wallet where user_id = ?");
        return  new BigDecimal(jdbcTemplate.queryForList(mySql.toString(),user_id).get(0).get("amount").toString());
    }
}
