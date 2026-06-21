package com.nurtureshare.app.network.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NoteResponse {
    private String id;
    private String title;
    private String category;
    @SerializedName("sharedWithPartner")
    private boolean syncedWithPartner;
    private List<NoteItemResponse> items;
    private String createdAt;

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public boolean isSyncedWithPartner() { return syncedWithPartner; }
    public List<NoteItemResponse> getItems() { return items; }
    public String getCreatedAt() { return createdAt; }
}
