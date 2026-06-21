package com.nurtureshare.app.network.model.request;

public class PregnancySetupRequest {
    private String dueDate;
    private String appMode;

    public PregnancySetupRequest(String dueDate, String appMode) {
        this.dueDate = dueDate;
        this.appMode = appMode;
    }

    public String getDueDate() { return dueDate; }
    public String getAppMode() { return appMode; }
}
