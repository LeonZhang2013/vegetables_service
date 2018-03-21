package com.zhongshu.vegetables.bean;

public class User {
    private Long id;
    private String username;
    private String password;
    private String nickname;
    private String mobile;
    private String token;
    private Integer enable;
    private Long parent_id;
    private Long address_id;
    private Long role_id;
    private Float percent_price;
    private Integer is_multi;

    public Float getPercent_price() {
        return percent_price;
    }

    public void setPercent_price(Float percent_price) {
        this.percent_price = percent_price;
    }

    public Integer getIs_multi() {
        return is_multi;
    }

    public void setIs_multi(Integer is_multi) {
        this.is_multi = is_multi;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
    }

    public Long getAddress_id() {
        return address_id;
    }

    public void setAddress_id(Long address_id) {
        this.address_id = address_id;
    }

    public Long getRole_id() {
        return role_id;
    }

    public void setRole_id(Long role_id) {
        this.role_id = role_id;
    }
}