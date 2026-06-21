package com.nurtureshare.app.network.model.request;

public class UpdateNoteItemRequest {
    private String content;
    private boolean checked;
    private boolean urgent;

    public UpdateNoteItemRequest(String content, boolean checked, boolean urgent) {
        this.content = content;
        this.checked = checked;
        this.urgent = urgent;
    }

    public String getContent() { return content; }
    public boolean isChecked() { return checked; }
    public boolean isUrgent() { return urgent; }
}
