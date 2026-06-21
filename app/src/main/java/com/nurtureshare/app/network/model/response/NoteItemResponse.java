package com.nurtureshare.app.network.model.response;

public class NoteItemResponse {
    private String id;
    private String content;
    private String itemType;
    private boolean checked;
    private boolean urgent;
    private int orderIndex;

    public String getId() { return id; }
    public String getContent() { return content; }
    public String getItemType() { return itemType; }
    public boolean isChecked() { return checked; }
    public boolean isUrgent() { return urgent; }
    public int getOrderIndex() { return orderIndex; }
}
