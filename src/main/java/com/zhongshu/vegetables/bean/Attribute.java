package com.zhongshu.vegetables.bean;

import ch.qos.logback.classic.db.names.TableName;

public class Attribute {
    private Long id;
    private String name;
    private int islist;
    private Long parent_id;

    public static final String TableName="attribute";

    public Long getId() {
        return id;
    }

    public int getIslist() {
        return islist;
    }

    public void setIslist(int islist) {
        this.islist = islist;
    }

    public Long getParent_id() {
        return parent_id;
    }

    public void setParent_id(Long parent_id) {
        this.parent_id = parent_id;
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


}