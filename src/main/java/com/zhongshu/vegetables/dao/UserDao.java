package com.zhongshu.vegetables.dao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.bean.Permission;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.bean.User;
import com.zhongshu.vegetables.dao.beans.Entity;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.utils.MySql;
import com.zhongshu.vegetables.utils.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Autowired
    EntityManagerDao entityManagerDao;
    private List<Map<String, Object>> employee;


    public User getUserByToken(String token) {
        String sql = "SELECT * from USER where token=?";
        RowMapper<User> rowMap = new BeanPropertyRowMapper<User>(User.class);
        List<User> users = jdbcTemplate.query(sql, new Object[]{token}, rowMap);
        if (users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

    public User login(String username, String pass) throws Exception {
        pass = Tools.encryptPass(username, pass);
        String sql = "SELECT * from USER where username=? and password=?";
        RowMapper<User> rowMap = new BeanPropertyRowMapper<User>(User.class);
        List<User> users = jdbcTemplate.query(sql, new Object[]{username, pass}, rowMap);
        if (users.size() == 0) {
            return null;
        }
        users.get(0).setPassword(null);
        return users.get(0);
    }

    public boolean updateUserPwd(String mobile, String password) throws Exception {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT username FROM user WHERE mobile = ?", mobile);
        password = Tools.encryptPass(maps.get(0).get("username").toString(), password);
        String sql = "update user set password=? where mobile = ?";
        int effect = jdbcTemplate.update(sql, password, mobile);
        return effect == 1;
    }

    public int save(User user) throws CustomException {
        Map<String, Object> toMap = (JSONObject) JSON.toJSON(user);
        StringBuffer sql = new StringBuffer("insert into user (");
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
        System.out.println(sql.toString());
        return jdbcTemplate.update(sql.toString(), list.toArray());
    }

    public User getUserByMobile(String mobile) {
        String sql = "select * from user where mobile=?";
        RowMapper<User> rowMap = new BeanPropertyRowMapper<>(User.class);
        List<User> users = jdbcTemplate.query(sql, new Object[]{mobile}, rowMap);
        if (users == null || users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

    public int updateToken(Long id, String token) throws Exception {
        String sql = "update user set token = :token where id = :id";
        return entityManagerDao.executeUpdate(sql, Entity.options().add("token", token).add("id", id).build());
    }

    public int addAddress(Long userId, Long addressId, String city, String address, String nickname, String mobile) {
        String sql = "insert into user_address (id,nickname,mobile,city,address,user_id) values(?,?,?,?,?,?)";
        return jdbcTemplate.update(sql, addressId, nickname, mobile, city, address, userId);
    }

    public List<Permission> getPermissionByRoleId(Long role_id) {
        String sql = "select p.* from permission p, role_permission r where r.role_id =? and r.`permission_id`  = p.id";
        RowMapper<Permission> rowMap = new BeanPropertyRowMapper<>(Permission.class);
        List<Permission> permission = jdbcTemplate.query(sql, new Object[]{role_id}, rowMap);
        return permission;
    }

    public int updateUserRole(String user_id, Integer role_id) {
        String sql = "update user set role_id=? where id = ?";
        int effect = jdbcTemplate.update(sql, role_id, user_id);
        return effect;

    }

    public int updateUserState(String user_id, String state) {
        String sql = "update user set enable=? where id = ?";
        int effect = jdbcTemplate.update(sql, state, user_id);
        return effect;

    }

    public int updateUserAddress(String user_id, String address) {
        String sql = "update user_address set address=? where id = (SELECT address_id from user where id=?) ";
        int effect = jdbcTemplate.update(sql, address, user_id);
        return effect;
    }

    public int updateUserCity(String user_id, String city) {
        String sql = "update user_address set city=? where id = (SELECT address_id from user where id=?) ";
        int effect = jdbcTemplate.update(sql, city, user_id);
        return effect;
    }

    public Map<String, Object> getAddressByUserId(Long id) throws CustomException {
        String sql = "select * from `user_address`  where user_id=?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, id);
        if (maps.size() == 0) {
            throw new CustomException("该用户没有地址");
        }
        return maps.get(0);
    }

    public Map<String, Object> getAddressById(String address_id) throws CustomException {
        String sql = "select id,mobile,nickname,city,address,user_id from `user_address`  where id=?";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql, address_id);
        if (maps.size() == 0) {
            throw new CustomException("该用户没有地址");
        }
        return maps.get(0);
    }


    public List<Map<String, Object>> getSupplierList() {
        String sql = "select u.id,CONCAT(u.nickname,ua.address) name from `user` u ,user_address ua where role_id=7 and u.id = ua.user_id";
        return jdbcTemplate.queryForList(sql);
    }


    /**
     * 通过商品关联获取所有商品供应者．
     *
     * @return
     */
    public List<Map<String, Object>> getSupplierListByProduct() {
        String sql = "select user_id id,(SELECT CONCAT(nickname,address) name  FROM `user_address` WHERE `user_id` = st.`user_id` ) name from `supply_task` st GROUP BY user_id";
        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public List<Map<String, Object>> getPurchase(Role role) {
        MySql mySql = new MySql("select u.id,`nickname`,`mobile`,`enable`,`parent_id`,`role_id`,r.name,");
        mySql.append("(SELECT CONCAT(city,address)  address FROM `user_address` WHERE `user_id` = u.`id`) `address` FROM `user` u,role r");
        mySql.append("WHERE u.role_id = r.id and `role_id` in (SELECT `role_id` FROM `role_permission` WHERE `permission_id` = 302)");
        return jdbcTemplate.queryForList(mySql.toString());
        // mySql.append("WHERE u.role_id = r.id and `role_id` in (SELECT `role_id` FROM `role_permission` WHERE `permission_id` = 302 and `role_id` !=5)");
        //return jdbcTemplate.queryForList(mySql.toString(),role.getLevel());
    }

    @Transactional
    public void updateBuyTask(String purchaseId, List<String> cateIds) throws CustomException {
        String delSql = "delete from buy_task where user_id=?";
        int update = jdbcTemplate.update(delSql, purchaseId);
        if (cateIds.size() > 0) {
            String insertSql = "insert into buy_task (user_id,group_id) values(?,?) ";
            int[] ints = jdbcTemplate.batchUpdate(insertSql, new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setString(1, purchaseId);
                    ps.setString(2, cateIds.get(i).toString());
                }

                public int getBatchSize() {
                    return cateIds.size();
                }
            });
            if (ints.length != cateIds.size()) {
                throw new CustomException("更新失败");
            }
        }
    }

    public List<Map<String, Object>> getRoles() {
        String sql = "select * from role";
        return jdbcTemplate.queryForList(sql);
    }

    public boolean updateMultiLogin(Long userId, Integer multi) {
        int update = jdbcTemplate.update("update user set is_multi = ? where id = ?", multi, userId);
        return update > 0;
    }

    public boolean updateFreight(String user_id, Double freight) {
        int update = jdbcTemplate.update("update user_address set freight=? where id = ?", freight, user_id);
        return update > 0;
    }

    /**
     * 获取该用户到达目的地每吨运费。
     *
     * @param userId
     * @param role_id
     * @return
     */
    public BigDecimal getUserFreight(Long userId, Long role_id) throws CustomException {
        MySql mySql = new MySql();
        if (role_id.intValue() == 6) {
            mySql.append("SELECT a.freight FROM `user`u,`user` up, `user_address` a WHERE up.id = u.parent_id and up.id = a.user_id and u.`id` = ?");
        } else {
            mySql.append("select a.freight from user_address a,user u WHERE a.user_id = u.id and u.id = ?");
        }
        List<Map<String, Object>> data = jdbcTemplate.queryForList(mySql.toString(), userId);
        if (data.size() == 0) throw new CustomException("无法计算运费，联系配送商");
        Object charge = data.get(0).get("freight");
        return new BigDecimal(charge.toString());
    }

    public BigDecimal getUserFreight(Long userId) throws CustomException {
        List<Map<String, Object>> maps = jdbcTemplate.queryForList("SELECT role_id FROM user WHERE id = ?", userId);
        Object role_id = maps.get(0).get("role_id");
        return getUserFreight(userId, Long.parseLong(role_id.toString()));
    }

    public BigDecimal getUserPercent(Long user_id) {
        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM USER WHERE id = ?", user_id);
        final BigDecimal percent_price = new BigDecimal(users.get(0).get("percent_price").toString());
        return percent_price;
    }


    public boolean updateParentId(String user_id, Long parent_id) {
        String sql = "update user set parent_id = ?,percent_price = 15 where id = ?";
        int update = jdbcTemplate.update(sql, parent_id, user_id);
        return update > 0;
    }


}
