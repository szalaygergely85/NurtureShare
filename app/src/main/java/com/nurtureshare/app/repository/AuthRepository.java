package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.LoginRequest;
import com.nurtureshare.app.network.model.request.RegisterRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.AuthResponse;
import com.nurtureshare.app.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<AuthResponse>> login(LoginRequest request) {
        MutableLiveData<Resource<AuthResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.login(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Login failed. Please check your credentials.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<AuthResponse>> register(RegisterRequest request) {
        MutableLiveData<Resource<AuthResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.register(request).enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call,
                                   Response<ApiResponse<AuthResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Registration failed. Please try again.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
