package com.nurtureshare.app.network.model.request;

public class CreateNoteItemRequest {
    private String content;
    private String itemType;
    private boolean urgent;
    private int orderIndex;

    public CreateNoteItemRequest(String content, String itemType, boolean urgent, int orderIndex) {
        this.content = content;
        this.itemType = itemType;
        this.urgent = urgent;
        this.orderIndex = orderIndex;
    }

    public String getContent() { return content; }
    public String getItemType() { return itemType; }
    public boolean isUrgent() { return urgent; }
    public int getOrderIndex() { return orderIndex; }
}
