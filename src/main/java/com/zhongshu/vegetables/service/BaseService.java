package com.zhongshu.vegetables.service;

import com.zhongshu.vegetables.config.ZSConfig;
import com.zhongshu.vegetables.dao.*;
import com.zhongshu.vegetables.dao.beans.OrderOperation;
import com.zhongshu.vegetables.service.back.AuthService;
import com.zhongshu.vegetables.service.back.GroupService;
import com.zhongshu.vegetables.utils.Arith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseService {

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    @Autowired
    protected EntityManagerDao entityManagerDao;

    @Autowired
    protected OrderDao orderDao;

    @Autowired
    UserService userService;

    @Autowired
    protected ZSConfig zsConfig;

    @Autowired
    protected UserDao userDao;

    @Autowired
    VerCodeDao verCodeDao;

    @Autowired
    CommonDao commonDao;

    @Autowired
    protected RoleDao roleDao;

    @Autowired
    protected AuthService auth;

    @Autowired
    protected ProductDao productDao;

    @Autowired
    protected AttributeDao attributeDao;

    @Autowired
    protected GroupService groupService;

    @Autowired
    protected PurchaseDao purchaseDao;

    @Autowired
    protected WalletDao walletDao;

    protected void refreshDatetime(String tablename, Long dataId) throws Exception {
//        Entity entity = Entity.options().add("table_name", tablename).add("data_id", dataId).build();
//        String time = DateUtils.parseCalendar2String(Calendar.getInstance(),"yyyy-MM-dd HH:mm:ss");
//        List<Map<String,Object>> list = entityManagerDao.executeQuery("select * from common where table_name = :table_name and data_id = :data_id", entity, null, 0);
//        if(null != list && list.size() > 0){
//            Map<String,Object> map = list.get(0);
//            entity.add("modified", time);
//            if(map.get("created") == null){
//                entity.add("created", time);
//                entityManagerDao.executeUpdate("update common set created = :created,modified = :modified where table_name = :table_name and data_id = :data_id", entity);
//            }else{
//                entityManagerDao.executeUpdate("update common set modified = :modified where table_name = :table_name and data_id = :data_id", entity);
//            }
//        }else{
//            entity.add("created", time);
//            entity.add("modified", time);
//            entity.setTable("common");
//            entityManagerDao.executeInsert(entity);
//        }
    }

    /**
     * 保存订单操作日志
     *
     * @param operId
     * @param orderId
     * @param operation
     * @throws Exception
     */
    public void saveOrderOperateLog(Long operId, Long orderId, OrderOperation operation) throws Exception {
//        Entity entity = Entity.options().add("oper_id", operId)
//                .add("order_id", orderId)
//                .add("operation", operation.getOperation())
//                .add("created", DateUtils.parseCalendar2String(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"))
//                .build();
//        entity.setTable("order_operate_log");
//        this.entityManagerDao.executeInsert(entity);
    }


    public Double getPriceSale(double price, double percent) {
        double mul = Arith.mul(price, percent);
        return Arith.add(price, Arith.div(mul, 100));
    }

}
