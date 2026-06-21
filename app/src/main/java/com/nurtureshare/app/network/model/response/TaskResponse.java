package com.nurtureshare.app.network.model.response;

public class TaskResponse {
    private String id;
    private String title;
    private String description;
    private String assignedTo;
    private String dueDate;
    private String status;
    private boolean nudgeOnSave;
    private int commentCount;
    private boolean synced;
    private UserResponse createdBy;
    private String createdAt;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignedTo() { return assignedTo; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public boolean isNudgeOnSave() { return nudgeOnSave; }
    public int getCommentCount() { return commentCount; }
    public boolean isSynced() { return synced; }
    public UserResponse getCreatedBy() { return createdBy; }
    public String getCreatedAt() { return createdAt; }
}
