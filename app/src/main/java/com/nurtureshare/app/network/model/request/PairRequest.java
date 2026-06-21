package com.nurtureshare.app.network.model.request;

public class PairRequest {
    private String pairingCode;

    public PairRequest(String pairingCode) {
        this.pairingCode = pairingCode;
    }

    public String getPairingCode() { return pairingCode; }
}
