package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.PairRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.CoupleStatusResponse;
import com.nurtureshare.app.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoupleRepository {

    private final ApiService apiService;

    public CoupleRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<CoupleStatusResponse>> getCoupleStatus() {
        MutableLiveData<Resource<CoupleStatusResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getCoupleStatus().enqueue(new Callback<ApiResponse<CoupleStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CoupleStatusResponse>> call,
                                   Response<ApiResponse<CoupleStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CoupleStatusResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load couple status.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CoupleStatusResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<CoupleStatusResponse>> pairWithPartner(String pairingCode) {
        MutableLiveData<Resource<CoupleStatusResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        PairRequest request = new PairRequest(pairingCode);
        apiService.pairWithPartner(request).enqueue(new Callback<ApiResponse<CoupleStatusResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CoupleStatusResponse>> call,
                                   Response<ApiResponse<CoupleStatusResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CoupleStatusResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to pair. Please check the code.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CoupleStatusResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<String>> getPairingCode() {
        MutableLiveData<Resource<String>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getPairingCode().enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call,
                                   Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to get pairing code.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
