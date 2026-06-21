package com.nurtureshare.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.response.TimelineResponse;
import com.nurtureshare.app.repository.TimelineRepository;
import com.nurtureshare.app.util.Resource;

public class TimelineViewModel extends AndroidViewModel {

    private final TimelineRepository timelineRepository;
    private MutableLiveData<Resource<TimelineResponse>> timeline;

    public TimelineViewModel(@NonNull Application application) {
        super(application);
        timelineRepository = new TimelineRepository(
                ApiClient.getInstance(application).getApiService()
        );
        loadTimeline();
    }

    public LiveData<Resource<TimelineResponse>> getTimeline() {
        if (timeline == null) {
            timeline = new MutableLiveData<>();
        }
        return timeline;
    }

    public void loadTimeline() {
        timeline = timelineRepository.getTimeline();
    }
}
