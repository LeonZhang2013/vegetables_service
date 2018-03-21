package com.zhongshu.vegetables.bean;

public class Product {
    private Long id;
    private String title;
    private String pic_url;
    private Double price;
    private String unit;
    private String code;
    private int islist;
    private String attribute;
    private String buy_unit;
    private Integer num;
    private Long oper_id;
    private Double price_sale;
    private String note;
    private Float percent_price;
    private String brand_name;
    private Integer storage_type;
    private long stock;
    private Float weight;
    private Double other_charge;
    private Long group_id; // 采购分组
    private int promotion; // 促销 1 爆款，0 不是。
    private String alias;  //别名，
    private String alias_pinyin;//别名拼音。

    public int getIslist() {
        return islist;
    }

    public void setIslist(int islist) {
        this.islist = islist;
    }

    public Long getGroup_id() {
        return group_id;
    }

    public void setGroup_id(Long group_id) {
        this.group_id = group_id;
    }

    public Double getOther_charge() {
        return other_charge;
    }

    public void setOther_charge(Double other_charge) {
        this.other_charge = other_charge;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public int getPromotion() {
        return promotion;
    }

    public void setPromotion(int promotion) {
        this.promotion = promotion;
    }

    public long getStock() {
        return stock;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Integer getStorage_type() {
        return storage_type;
    }

    public void setStorage_type(Integer storage_type) {
        this.storage_type = storage_type;
    }

    public String getBrand_name() {
        return brand_name;
    }

    public void setBrand_name(String brand_name) {
        this.brand_name = brand_name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }


    public String getPic_url() {
        return pic_url;
    }

    public void setPic_url(String pic_url) {
        this.pic_url = pic_url;
    }


    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getBuy_unit() {
        return buy_unit;
    }

    public void setBuy_unit(String buy_unit) {
        this.buy_unit = buy_unit;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getAlias_pinyin() {
        return alias_pinyin;
    }

    public void setAlias_pinyin(String alias_pinyin) {
        this.alias_pinyin = alias_pinyin;
    }

    public Float getPercent_price() {
        return percent_price;
    }

    public void setPercent_price(Float percent_price) {
        this.percent_price = percent_price;
    }


    public Long getOper_id() {
        return oper_id;
    }

    public void setOper_id(Long oper_id) {
        this.oper_id = oper_id;
    }

    public Double getPrice_sale() {
        return price_sale;
    }

    public void setPrice_sale(Double price_sale) {
        this.price_sale = price_sale;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }


}