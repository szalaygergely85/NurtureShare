package com.nurtureshare.app.network.model.response;

import com.google.gson.annotations.SerializedName;

public class TaskProgressResponse {
    @SerializedName("total")
    private int totalTasks;
    @SerializedName("completed")
    private int completedTasks;
    @SerializedName("pending")
    private int pendingTasks;
    @SerializedName("percentComplete")
    private int progressPercent;

    public int getTotalTasks() { return totalTasks; }
    public int getCompletedTasks() { return completedTasks; }
    public int getPendingTasks() { return pendingTasks; }
    public int getProgressPercent() { return progressPercent; }
}
