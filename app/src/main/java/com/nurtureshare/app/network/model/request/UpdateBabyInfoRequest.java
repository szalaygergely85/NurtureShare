package com.nurtureshare.app.network.model.request;

public class UpdateBabyInfoRequest {
    private String babyGender;
    private String babyName;

    public UpdateBabyInfoRequest(String babyGender, String babyName) {
        this.babyGender = babyGender;
        this.babyName = babyName;
    }

    public String getBabyGender() { return babyGender; }
    public String getBabyName() { return babyName; }
}
