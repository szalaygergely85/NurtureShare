package com.nurtureshare.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.request.UpdateSettingsRequest;
import com.nurtureshare.app.network.model.response.CoupleStatusResponse;
import com.nurtureshare.app.network.model.response.SettingsResponse;
import com.nurtureshare.app.repository.CoupleRepository;
import com.nurtureshare.app.repository.SettingsRepository;
import com.nurtureshare.app.util.Resource;

public class SettingsViewModel extends AndroidViewModel {

    private final SettingsRepository settingsRepository;
    private final CoupleRepository coupleRepository;

    private MutableLiveData<Resource<SettingsResponse>> settings;
    private MutableLiveData<Resource<SettingsResponse>> updateResult;
    private MutableLiveData<Resource<CoupleStatusResponse>> pairResult;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        settingsRepository = new SettingsRepository(
                ApiClient.getInstance(application).getApiService()
        );
        coupleRepository = new CoupleRepository(
                ApiClient.getInstance(application).getApiService()
        );
        settings = new MutableLiveData<>();
        updateResult = new MutableLiveData<>();
        pairResult = new MutableLiveData<>();
        loadSettings();
    }

    public LiveData<Resource<SettingsResponse>> getSettings() { return settings; }
    public LiveData<Resource<SettingsResponse>> getUpdateResult() { return updateResult; }
    public LiveData<Resource<CoupleStatusResponse>> getPairResult() { return pairResult; }

    public void loadSettings() {
        settings = settingsRepository.getSettings();
    }

    public LiveData<Resource<SettingsResponse>> loadSettingsAndObserve() {
        settings = settingsRepository.getSettings();
        return settings;
    }

    public void updateSettings(UpdateSettingsRequest request) {
        updateResult = settingsRepository.updateSettings(request);
    }

    public LiveData<Resource<SettingsResponse>> updateSettingsAndObserve(UpdateSettingsRequest request) {
        updateResult = settingsRepository.updateSettings(request);
        return updateResult;
    }

    public void pairWithPartner(String pairingCode) {
        pairResult = coupleRepository.pairWithPartner(pairingCode);
    }

    public LiveData<Resource<CoupleStatusResponse>> pairWithPartnerAndObserve(String pairingCode) {
        pairResult = coupleRepository.pairWithPartner(pairingCode);
        return pairResult;
    }
}
