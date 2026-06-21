package com.nurtureshare.app.network.model.request;

public class CreateNoteRequest {
    private String title;
    private String category;
    private Boolean sharedWithPartner;

    public CreateNoteRequest(String title, String category) {
        this.title = title;
        this.category = category;
    }

    public CreateNoteRequest(String title, String category, boolean sharedWithPartner) {
        this.title = title;
        this.category = category;
        this.sharedWithPartner = sharedWithPartner;
    }

    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public Boolean getSharedWithPartner() { return sharedWithPartner; }
}
