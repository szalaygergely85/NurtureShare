package com.nurtureshare.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.MainActivity;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.ActivityLoginBinding;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private AuthViewModel authViewModel;
    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = ((NurtureShareApp) getApplication()).getTokenManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (tokenManager.isLoggedIn()) {
            navigateToMain();
        }
    }

    private void setupClickListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.btnCreateAccount.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));
        binding.btnForgotPassword.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(), R.string.coming_soon, Snackbar.LENGTH_SHORT).show());
        binding.btnGoogle.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(), R.string.coming_soon, Snackbar.LENGTH_SHORT).show());
        binding.btnApple.setOnClickListener(v ->
                Snackbar.make(binding.getRoot(), R.string.coming_soon, Snackbar.LENGTH_SHORT).show());
    }

    private void attemptLogin() {
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";

        boolean hasError = false;

        if (email.isEmpty()) {
            binding.tilEmail.setError(getString(R.string.error_email_empty));
            hasError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.setError(getString(R.string.error_email_invalid));
            hasError = true;
        }

        if (password.isEmpty()) {
            binding.tilPassword.setError(getString(R.string.error_password_empty));
            hasError = true;
        } else if (password.length() < 6) {
            binding.tilPassword.setError(getString(R.string.error_password_too_short));
            hasError = true;
        }

        if (hasError) return;

        authViewModel.loginAndObserve(email, password).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (resource.data != null) {
                        tokenManager.saveToken(resource.data.getToken());
                        if (resource.data.getRole() != null) {
                            tokenManager.saveRole(resource.data.getRole());
                        }
                        navigateToMain();
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    String message = resource.message != null
                            ? resource.message
                            : getString(R.string.error_login_failed);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void showLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnLogin.setEnabled(!loading);
        binding.btnCreateAccount.setEnabled(!loading);
        binding.btnForgotPassword.setEnabled(!loading);
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
