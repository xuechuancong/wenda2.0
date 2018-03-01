package com.nowcoder.model;

import java.util.Date;

public class Question {
    private int id;
    private String title;
    private String content;
    private Date createdDate;
    private int userId;
    private int commentCount;

    public int getId() {
        return id;
    }

    public Question setId(int id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Question setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Question setContent(String content) {
        this.content = content;
        return this;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public Question setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Question setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public Question setCommentCount(int commentCount) {
        this.commentCount = commentCount;
        return this;
    }
}
