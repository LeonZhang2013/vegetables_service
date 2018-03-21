package com.zhongshu.vegetables.service.back;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.service.BaseService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class GroupService extends BaseService {


    public List<Map<String,Object>> getGroup() {
        String sql = "select * from buy_group";
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        return maps;
    }

    public boolean update(Long user_id,String groups) throws CustomException {
        boolean isOk = false;
        JSONArray jsonArray = JSONObject.parseArray(groups);
        String deleteSql = "DELETE FROM `buy_task` WHERE `user_id` = ?";
        int update = jdbcTemplate.update(deleteSql, user_id);
        if(update<0) throw new CustomException("更新失败");
        String sql = "insert into buy_task (user_id,group_id) values (?,?)";
        for (int i=0; i<jsonArray.size(); i++){
            jdbcTemplate.update(sql,user_id,jsonArray.get(i));
        }

        return isOk;
    }

    public List<Map<String,Object>> getMyGroup(String user_id) {
        return jdbcTemplate.queryForList("select * FROM buy_task WHERE user_id = ?",user_id);
    }
}
