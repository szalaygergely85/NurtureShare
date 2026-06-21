package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.TimelineResponse;
import com.nurtureshare.app.util.Resource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelineRepository {

    private final ApiService apiService;

    public TimelineRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<TimelineResponse>> getTimeline() {
        MutableLiveData<Resource<TimelineResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getTimeline().enqueue(new Callback<ApiResponse<TimelineResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TimelineResponse>> call,
                                   Response<ApiResponse<TimelineResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TimelineResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load timeline.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TimelineResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
