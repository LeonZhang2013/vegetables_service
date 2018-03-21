package com.zhongshu.vegetables.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zhongshu.vegetables.bean.Product;
import com.zhongshu.vegetables.utils.MD5;
import com.zhongshu.vegetables.utils.NetUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.*;
import java.net.InetAddress;
import java.util.*;

@Configuration
@EnableScheduling
public class TianYuService extends BaseService {

    private String secret = "24c6aaf9cb97b9e0e0b96e8fd8d8f9ff";
    private String url = "http://www.tysp.com/home/U8connecttysc/ty_thirdparty_goodslists";

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);



    //@Scheduled(cron = "0/5 * * * * ?") //五秒钟执行一次。
    @Scheduled(cron = "0 0 2 * * ?") //每天凌晨2点同步天宇数据
    private void loadingData() throws IOException {
        //用 getLocalHost() 方法创建的InetAddress的对象
        InetAddress address = InetAddress.getLocalHost();
        if(address.getHostAddress().equals("47.96.168.74")){//如果是 主机服务器就执行定时任务。
            String time = String.valueOf(System.currentTimeMillis() / 1000L);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("timestamp", time));
            params.add(new BasicNameValuePair("data", "[{\"sname\":\"tyzs\"}]"));
            params.add(new BasicNameValuePair("sign", getSign("TIMESTAMP" + time + "DATASNAMETYZSSECRET24C6AAF9CB97B9E0E0B96E8FD8D8F9FF")));
            String content = NetUtils.post(params, url);
            parseData(content);
        }
    }

    private static String getSign(String str) {
        str = sort(str);
        str = MD5.getMD5(str);
        return str.toUpperCase();
    }

    private static String sort(String str) {
        char chs[] = str.toCharArray();
        Arrays.sort(chs);
        StringBuffer sb = new StringBuffer();
        for (int i = chs.length; i > 0; i--) {
            sb.append(chs[i - 1]);
        }
        return sb.toString();
    }


    public void parseData(String content) throws IOException {
        JSONObject parse = JSON.parseObject(content);
        JSONArray data = parse.getJSONArray("data");

        List<Product> items = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            Product product = new Product();
            product.setId(jsonObject.getLong("id"));
            product.setTitle(jsonObject.getString("goodsname"));
            product.setPrice(jsonObject.getDouble("price"));
            product.setPercent_price(3f);
            product.setPrice_sale(getPriceSale(product.getPrice(), product.getPercent_price()));
            product.setPic_url(jsonObject.getString("img"));
            product.setUnit(jsonObject.getString("sku"));
            product.setBuy_unit(jsonObject.getString("sku"));
            product.setNote(jsonObject.getString("format"));
            product.setBrand_name(jsonObject.getString("brandname"));
            product.setStorage_type(jsonObject.getInteger("storage_type"));
            items.add(product);
        }


        for (Product product : items) {
            int effect = productDao.updateProduct(product);
            if (effect == 0) {
                effect = productDao.addProduct(product);
                if (effect == 1) {
                    logger.debug("新增成功" + product.getTitle());
                } else {
                    logger.error("新增失败 ++++ " + product.getTitle());
                }
            } else {
                logger.debug("更新成功" + product.getTitle());
            }
        }
    }


}




