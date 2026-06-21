package com.nurtureshare.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.request.LoginRequest;
import com.nurtureshare.app.network.model.request.RegisterRequest;
import com.nurtureshare.app.network.model.response.AuthResponse;
import com.nurtureshare.app.repository.AuthRepository;
import com.nurtureshare.app.util.Resource;

public class AuthViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private MutableLiveData<Resource<AuthResponse>> loginResult;
    private MutableLiveData<Resource<AuthResponse>> registerResult;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(
                ApiClient.getInstance(application).getApiService()
        );
        loginResult = new MutableLiveData<>();
        registerResult = new MutableLiveData<>();
    }

    public LiveData<Resource<AuthResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Resource<AuthResponse>> getRegisterResult() {
        return registerResult;
    }

    public void login(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        loginResult = authRepository.login(request);
        loginResult.observeForever(result -> {
            // observation handled by activity
        });
    }

    public void register(String email, String password, String name, String role) {
        RegisterRequest request = new RegisterRequest(email, password, name, role);
        registerResult = authRepository.register(request);
        registerResult.observeForever(result -> {
            // observation handled by activity
        });
    }

    // Convenience methods that return the LiveData directly
    public LiveData<Resource<AuthResponse>> loginAndObserve(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        loginResult = authRepository.login(request);
        return loginResult;
    }

    public LiveData<Resource<AuthResponse>> registerAndObserve(String email, String password, String name, String role) {
        RegisterRequest request = new RegisterRequest(email, password, name, role);
        registerResult = authRepository.register(request);
        return registerResult;
    }
}
