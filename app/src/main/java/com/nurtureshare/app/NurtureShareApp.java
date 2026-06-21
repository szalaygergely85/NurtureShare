package com.nurtureshare.app;

import android.app.Application;

import com.nurtureshare.app.util.TokenManager;

public class NurtureShareApp extends Application {

    private TokenManager tokenManager;

    @Override
    public void onCreate() {
        super.onCreate();
        tokenManager = new TokenManager(this);
    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }
}
