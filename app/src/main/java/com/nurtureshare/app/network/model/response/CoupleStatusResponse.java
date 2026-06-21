package com.nurtureshare.app.network.model.response;

public class CoupleStatusResponse {
    private String coupleId;
    private String pairingCode;
    private boolean connected;
    private UserResponse partner;
    private String syncedAt;

    public String getCoupleId() { return coupleId; }
    public String getPairingCode() { return pairingCode; }
    public boolean isConnected() { return connected; }
    public UserResponse getPartner() { return partner; }
    public String getSyncedAt() { return syncedAt; }
}
