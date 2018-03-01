package com.nowcoder.model;

import java.util.Date;

public class Comment {
    private int id;
    private String content;
    private int userId;
    private int entityId;
    private int entityType;
    private Date createdDate;
    private int status;

    public int getId() {
        return id;
    }

    public Comment setId(int id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Comment setContent(String content) {
        this.content = content;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Comment setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Comment setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Comment setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Comment setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public Comment setStatus(int status) {
        this.status = status;
        return this;
    }
}
