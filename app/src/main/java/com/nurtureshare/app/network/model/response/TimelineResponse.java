package com.nurtureshare.app.network.model.response;

import java.util.List;

public class TimelineResponse {
    private int currentWeek;
    private String trimester;
    private int percentComplete;
    private int weeksLeft;
    private String appMode;
    private MilestoneResponse currentMilestone;
    private List<String> suggestedActions;
    private String partnerFocusTip;
    private String babyName;
    private String babyGender;
    private String checklistTitle;
    private List<String> checklistItems;
    private boolean partnerConnected;

    public int getCurrentWeek() { return currentWeek; }
    public String getTrimester() { return trimester; }
    public int getPercentComplete() { return percentComplete; }
    public int getWeeksLeft() { return weeksLeft; }
    public String getAppMode() { return appMode; }
    public boolean isBabyMode() { return "NEWBORN".equalsIgnoreCase(appMode); }
    public MilestoneResponse getCurrentMilestone() { return currentMilestone; }
    public List<String> getSuggestedActions() { return suggestedActions; }
    public String getPartnerFocusTip() { return partnerFocusTip; }
    public String getBabyName() { return babyName; }
    public String getBabyGender() { return babyGender; }
    public String getChecklistTitle() { return checklistTitle; }
    public List<String> getChecklistItems() { return checklistItems; }
    public boolean isPartnerConnected() { return partnerConnected; }
}
