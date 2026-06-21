package com.nurtureshare.app.network.model.response;

public class SettingsResponse {
    private boolean notificationsEnabled;
    private String appMode;
    private CoupleStatusResponse coupleStatus;
    private String userName;
    private String userEmail;
    private String dueDate;
    private Integer currentWeek;
    private String trimester;
    private String babyGender;
    private String babyName;

    public boolean isNotificationsEnabled() { return notificationsEnabled; }
    public String getAppMode() { return appMode; }
    public CoupleStatusResponse getCoupleStatus() { return coupleStatus; }
    public String getUserName() { return userName; }
    public String getUserEmail() { return userEmail; }
    public String getDueDate() { return dueDate; }
    public Integer getCurrentWeek() { return currentWeek; }
    public String getTrimester() { return trimester; }
    public String getBabyGender() { return babyGender; }
    public String getBabyName() { return babyName; }
}
