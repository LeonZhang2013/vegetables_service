package com.zhongshu.vegetables.service.back;

import com.zhongshu.vegetables.dao.beans.OrderStatus;
import com.zhongshu.vegetables.exception.CustomException;
import com.zhongshu.vegetables.service.BaseService;
import com.zhongshu.vegetables.utils.MySql;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Service
public class DistributionService extends BaseService {


    public List<Map<String, Object>> getDisItem(Long proxy_id, String keyword) {
        return orderDao.getDisItem(proxy_id,keyword);
    }

    public List<Map<String, Object>> getDisProductToUser(Long proxy_id, Long product_id) {
        return orderDao.getDisProductToUser(proxy_id, product_id);
    }

    @Transactional
    public boolean saveDisProduct(List<Map<String, Object>> detailList, Long proxy_id, Long product_id, Boolean isFinish) {
        boolean isOk = false;
        List<Map<String, Object>> disProduct = getDisProductToUser(proxy_id, product_id);
        Map<String, Object> order = disProduct.get(0);
        MySql mySql = new MySql();
        mySql.append("update user_order set purchase_num = ?,purchase_weight = ?,mark_price = ?," );
        mySql.append("purchase_price = ?,purchase_other_charge = ?");
        if (isFinish) {
            mySql.append(",order_status = "+ OrderStatus.DISTRIBUTION.getStatus());
        }
        mySql.append(" where id = ?");

        if (null != detailList && detailList.size() > 0){
            int[] ints = jdbcTemplate.batchUpdate(mySql.toString(), new BatchPreparedStatementSetter() {
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    Map<String, Object> item = detailList.get(i);
                    ps.setObject(1, item.get("purchase_num"));
                    ps.setObject(2, item.get("purchase_weight"));
                    ps.setObject(3, order.get("mark_price"));
                    ps.setObject(4, order.get("purchase_price"));
                    ps.setObject(5, order.get("purchase_other_charge"));
                    ps.setObject(6, item.get("id"));
                }
                public int getBatchSize() {
                    return detailList.size();
                }
            });
            isOk = ints.length > 0;
        }
        if(isFinish) {
            String sql = "UPDATE proxy_order SET order_status = ? WHERE order_status = ? and proxy_id = ? and product_id = ?";
            jdbcTemplate.update(sql, OrderStatus.DISTRIBUTION.getStatus(), OrderStatus.WAIT_SEND.getStatus(),proxy_id, product_id);
        }

        return isOk;
    }

}
