package com.nurtureshare.app.ui.auth;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.ActivityRegisterBinding;
import com.nurtureshare.app.ui.onboarding.OnboardingActivity;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private TokenManager tokenManager;
    private boolean isPartnerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tokenManager = ((NurtureShareApp) getApplication()).getTokenManager();
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        setupRolePicker();
        setupClickListeners();
    }

    private void setupRolePicker() {
        updateRoleCardAppearance();
        binding.cardRoleMother.setOnClickListener(v -> {
            isPartnerSelected = false;
            updateRoleCardAppearance();
        });
        binding.cardRolePartner.setOnClickListener(v -> {
            isPartnerSelected = true;
            updateRoleCardAppearance();
        });
    }

    private void updateRoleCardAppearance() {
        int primary      = getColor(R.color.primary);
        int primaryFixed = getColor(R.color.primary_fixed);
        int surface      = getColor(R.color.surface_container_lowest);
        int outline      = getColor(R.color.outline_variant);
        int onSurface    = getColor(R.color.on_surface_variant);

        if (isPartnerSelected) {
            setCardActive(binding.cardRoleMother, false, surface, outline, onSurface);
            binding.tvRoleMotherLabel.setTextColor(onSurface);

            setCardActive(binding.cardRolePartner, true, primaryFixed, primary, primary);
            binding.tvRolePartnerLabel.setTextColor(primary);
            binding.ivRolePartnerIcon.setImageTintList(ColorStateList.valueOf(primary));
        } else {
            setCardActive(binding.cardRoleMother, true, primaryFixed, primary, primary);
            binding.tvRoleMotherLabel.setTextColor(primary);

            setCardActive(binding.cardRolePartner, false, surface, outline, onSurface);
            binding.tvRolePartnerLabel.setTextColor(onSurface);
            binding.ivRolePartnerIcon.setImageTintList(ColorStateList.valueOf(onSurface));
        }
    }

    private void setCardActive(com.google.android.material.card.MaterialCardView card,
                               boolean active, int bg, int stroke, int unusedColor) {
        card.setCardBackgroundColor(bg);
        card.setStrokeColor(stroke);
        card.setStrokeWidth(active ? dpToPx(2) : dpToPx(1));
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setupClickListeners() {
        binding.btnRegister.setOnClickListener(v -> attemptRegister());
        binding.btnBackToLogin.setOnClickListener(v -> finish());
    }

    private void attemptRegister() {
        binding.tilName.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);

        String name = binding.etName.getText() != null
                ? binding.etName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null
                ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null
                ? binding.etPassword.getText().toString().trim() : "";
        String role = isPartnerSelected ? "PARTNER" : "MOTHER";

        boolean hasError = false;

        if (name.isEmpty()) {
            binding.tilName.setError(getString(R.string.error_name_empty));
            hasError = true;
        }

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

        authViewModel.registerAndObserve(email, password, name, role).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    showLoading(true);
                    break;
                case SUCCESS:
                    showLoading(false);
                    if (resource.data != null) {
                        tokenManager.saveToken(resource.data.getToken());
                        String savedRole = resource.data.getRole() != null
                                ? resource.data.getRole() : role;
                        tokenManager.saveRole(savedRole);
                        navigateAfterRegister();
                    }
                    break;
                case ERROR:
                    showLoading(false);
                    String message = resource.message != null
                            ? resource.message
                            : getString(R.string.error_register_failed);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void showLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnRegister.setEnabled(!loading);
        binding.btnBackToLogin.setEnabled(!loading);
    }

    private void navigateAfterRegister() {
        Intent intent = new Intent(this, OnboardingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
