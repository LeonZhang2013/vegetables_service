package com.zhongshu.vegetables.dao;

import com.zhongshu.vegetables.bean.Permission;
import com.zhongshu.vegetables.bean.Role;
import com.zhongshu.vegetables.bean.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class RoleDao {

    @Resource
    JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getPermissonById(Long id){
        String sql = "select permission_id, p.name  from role_permission rp,`permission` p where  rp.permission_id = p.id  and role_id=?";
        List<Map<String, Object>> listId= jdbcTemplate.queryForList(sql,id);
        return listId;
    }


}
