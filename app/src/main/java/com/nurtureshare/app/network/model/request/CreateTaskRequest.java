package com.nurtureshare.app.network.model.request;

import com.google.gson.annotations.SerializedName;

public class CreateTaskRequest {
    private String title;
    private String description;
    private String assignedTo;
    private String dueDate;
    @SerializedName("nudgeOnSave")
    private boolean nudgeOnSave;
    private Boolean synced;

    public CreateTaskRequest(String title, String description, String assignedTo,
                             String dueDate, boolean nudgeOnSave) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.nudgeOnSave = nudgeOnSave;
    }

    public CreateTaskRequest(String title, String description, String assignedTo,
                             String dueDate, boolean nudgeOnSave, boolean synced) {
        this.title = title;
        this.description = description;
        this.assignedTo = assignedTo;
        this.dueDate = dueDate;
        this.nudgeOnSave = nudgeOnSave;
        this.synced = synced;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getAssignedTo() { return assignedTo; }
    public String getDueDate() { return dueDate; }
    public boolean isNudgeOnSave() { return nudgeOnSave; }
    public Boolean getSynced() { return synced; }
}
