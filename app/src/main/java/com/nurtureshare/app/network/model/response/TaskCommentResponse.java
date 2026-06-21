package com.nurtureshare.app.network.model.response;

public class TaskCommentResponse {
    private String id;
    private String content;
    private UserResponse author;
    private String createdAt;

    public String getId() { return id; }
    public String getContent() { return content; }
    public UserResponse getAuthor() { return author; }
    public String getCreatedAt() { return createdAt; }
}
