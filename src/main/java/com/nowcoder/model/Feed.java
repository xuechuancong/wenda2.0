package com.nowcoder.model;

import java.util.Date;

public class Feed {
    private int id;
    private Date createdDate;
    private int userId;
    private String data;
    private int type;




    public int getId() {
        return id;
    }

    public Feed setId(int id) {
        this.id = id;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Feed setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Feed setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public String getData() {
        return data;
    }

    public Feed setData(String data) {
        this.data = data;
        return this;
    }

    public int getType() {
        return type;
    }

    public Feed setType(int type) {
        this.type = type;
        return this;
    }
}
