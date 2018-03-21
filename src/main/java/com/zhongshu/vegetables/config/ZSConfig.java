package com.zhongshu.vegetables.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@ConfigurationProperties
@PropertySource("classpath:/my.properties")
public class ZSConfig {

    private String varCodelength;
    private String varExpiresIn;
    private String limitAmount;

    public BigDecimal getLimitAmount() {
        BigDecimal limit = null;
        try {
            limit = new BigDecimal(limitAmount);
        }catch (Exception e){
            limit = new BigDecimal("100");
        }
        return limit;
    }

    public void setLimitAmount(String limitAmount) {
        this.limitAmount = limitAmount;
    }

    public int getVarCodelength() {
        int temp = 4;
        try{
            temp = Integer.parseInt(varCodelength);
        }catch (Exception e){

        }
        return temp;
    }

    public void setVarCodelength(String varCodelength) {
        this.varCodelength = varCodelength;
    }

    public int getVarExpiresIn() {
        int temp = 300;
        try{
            temp = Integer.parseInt(varExpiresIn);
        }catch (Exception e){

        }
        return temp;
    }

    public void setVarExpiresIn(String varExpiresIn) {
        this.varExpiresIn = varExpiresIn;
    }
}