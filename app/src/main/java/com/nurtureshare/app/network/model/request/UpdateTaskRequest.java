package com.nurtureshare.app.network.model.request;

public class UpdateTaskRequest {
    private String title;
    private String description;
    private String assignedTo;
    private String dueDate;
    private String status;
    private Boolean synced;

    public UpdateTaskRequest(String title, String description, String assignedTo,
                             String dueDate, String status) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.status = status;
    }

    public UpdateTaskRequest(String title, String description, String assignedTo,
                             String dueDate, String status, boolean synced) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.status = status;
        this.synced = synced;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignedTo() { return assignedTo; }
    public String getDueDate() { return dueDate; }
    public String getStatus() { return status; }
    public Boolean getSynced() { return synced; }
}
