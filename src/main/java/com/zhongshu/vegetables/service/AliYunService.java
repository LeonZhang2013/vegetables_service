package com.zhongshu.vegetables.service;

import com.zhongshu.vegetables.result.Code;
import com.zhongshu.vegetables.result.SingleResult;
import com.zhongshu.vegetables.config.AliYunSMS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;


@Service
public class AliYunService extends BaseService {



    @Autowired
    AliYunSMS aliYunSMS;

    private static final Logger logger = LoggerFactory.getLogger(BaseService.class);

    public SingleResult<Map<String, Object>> sendMassage(String phone) {
        //1、生成随机吗
        String verCode = getVerCodeNum(zsConfig.getVarCodelength());
        Date expirseDate = getExpiresIn(zsConfig.getVarExpiresIn());

        SingleResult<Map<String, Object>> result = new SingleResult<Map<String, Object>>();
        try {
            verCodeDao.saveCode(phone, verCode, expirseDate);
            String message = aliYunSMS.sendSms(phone, verCode).getMessage();
            result.setCode(Code.SUCCESS);
            result.setMessage("OK".equalsIgnoreCase(message)?"发送成功":message);
        } catch (Exception e) {
            result.setCode(Code.ERROR);
            result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    private Date getExpiresIn(long varExpiresIn) {
        long timeStamp = (System.currentTimeMillis() + varExpiresIn * 1000);
        return new Date(timeStamp);
    }

    private String getVerCodeNum(int varCodelength) {
        int verCode = 0;
        if (varCodelength == 6) {
            verCode = 1000000 - new Random().nextInt(899999);
        } else {
            verCode = 10000 - new Random().nextInt(8999);
        }
        return String.valueOf(verCode);
    }

}
