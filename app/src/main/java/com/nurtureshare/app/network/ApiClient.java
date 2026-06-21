package com.nurtureshare.app.network;

import android.content.Context;

import com.nurtureshare.app.BuildConfig;

import java.util.Locale;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.util.TokenManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static ApiClient instance;
    private final ApiService apiService;

    private ApiClient(Context context) {
        TokenManager tokenManager = ((NurtureShareApp) context.getApplicationContext())
                .getTokenManager();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }

        String language = Locale.getDefault().getLanguage(); // e.g. "hu", "en"

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context.getApplicationContext(), tokenManager))
                .addInterceptor(chain -> chain.proceed(
                        chain.request().newBuilder()
                                .header("Accept-Language", language)
                                .build()))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized ApiClient getInstance(Context context) {
        if (instance == null) {
            instance = new ApiClient(context.getApplicationContext());
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}
