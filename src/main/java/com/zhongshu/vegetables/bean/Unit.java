package com.zhongshu.vegetables.bean;

public class Unit {
    private Long id;

    private String name;

    private Double calc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Double getCalc() {
        return calc;
    }

    public void setCalc(Double calc) {
        this.calc = calc;
    }
}