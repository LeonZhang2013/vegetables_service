package com.zhongshu.vegetables.bean;

import java.util.Date;

public class Order {


    private String car_free;
    private String oper_id;
    private Date create_time;
    private Long id;

    private Long user_id;

    private Long address_id;

    private Integer status;

    private Double current_price;

    private Double freight;

    private Double payed_price;

    private Double carFree;

    private Long operId;

    private String comment;

    public String getCar_free() {
        return car_free;
    }

    public void setCar_free(String car_free) {
        this.car_free = car_free;
    }

    public String getOper_id() {
        return oper_id;
    }

    public void setOper_id(String oper_id) {
        this.oper_id = oper_id;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getAddress_id() {
        return address_id;
    }

    public void setAddress_id(Long address_id) {
        this.address_id = address_id;
    }

    public Double getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(Double current_price) {
        this.current_price = current_price;
    }

    public Double getPayed_price() {
        return payed_price;
    }

    public void setPayed_price(Double payed_price) {
        this.payed_price = payed_price;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


    public Double getFreight() {
        return freight;
    }

    public void setFreight(Double freight) {
        this.freight = freight;
    }


    public Double getCarFree() {
        return carFree;
    }

    public void setCarFree(Double carFree) {
        this.carFree = carFree;
    }

    public Long getOperId() {
        return operId;
    }

    public void setOperId(Long operId) {
        this.operId = operId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }
}