package com.nurtureshare.app.network.model.request;

public class CreateTaskCommentRequest {
    private String content;

    public CreateTaskCommentRequest(String content) {
        this.content = content;
    }

    public String getContent() { return content; }
}
