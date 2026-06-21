package com.nurtureshare.app.network.model.request;

public class UpdateSettingsRequest {
    private boolean notificationsEnabled;
    private String appMode;

    public UpdateSettingsRequest(boolean notificationsEnabled, String appMode) {
        this.notificationsEnabled = notificationsEnabled;
        this.appMode = appMode;
    }

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public String getAppMode() { return appMode; }
}
