package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.UpdateSettingsRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.SettingsResponse;
import com.nurtureshare.app.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsRepository {

    private final ApiService apiService;

    public SettingsRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<SettingsResponse>> getSettings() {
        MutableLiveData<Resource<SettingsResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getSettings().enqueue(new Callback<ApiResponse<SettingsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SettingsResponse>> call,
                                   Response<ApiResponse<SettingsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<SettingsResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load settings.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SettingsResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<SettingsResponse>> updateSettings(UpdateSettingsRequest request) {
        MutableLiveData<Resource<SettingsResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.updateSettings(request).enqueue(new Callback<ApiResponse<SettingsResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<SettingsResponse>> call,
                                   Response<ApiResponse<SettingsResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<SettingsResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to update settings.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<SettingsResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
