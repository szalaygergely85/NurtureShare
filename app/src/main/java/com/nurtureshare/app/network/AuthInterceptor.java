package com.nurtureshare.app.network;

import android.content.Context;
import android.content.Intent;

import com.nurtureshare.app.ui.auth.LoginActivity;
import com.nurtureshare.app.util.TokenManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final Context appContext;
    private final TokenManager tokenManager;

    public AuthInterceptor(Context appContext, TokenManager tokenManager) {
        this.appContext = appContext;
        this.tokenManager = tokenManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = tokenManager.getToken();

        Request request = original;
        if (token != null && !token.isEmpty()) {
            request = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
        }

        Response response = chain.proceed(request);

        if ((response.code() == 401 || response.code() == 403) && tokenManager.getToken() != null) {
            tokenManager.clearToken();
            Intent intent = new Intent(appContext, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            appContext.startActivity(intent);
        }

        return response;
    }
}
