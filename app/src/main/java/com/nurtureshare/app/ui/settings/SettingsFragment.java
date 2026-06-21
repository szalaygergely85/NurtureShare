package com.nurtureshare.app.ui.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentSettingsBinding;
import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.request.PregnancySetupRequest;
import com.nurtureshare.app.network.model.request.UpdateBabyInfoRequest;
import com.nurtureshare.app.network.model.request.UpdateSettingsRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.CoupleStatusResponse;
import com.nurtureshare.app.network.model.response.SettingsResponse;
import com.nurtureshare.app.network.model.response.TimelineResponse;
import com.nurtureshare.app.network.model.response.UserResponse;
import com.nurtureshare.app.ui.auth.LoginActivity;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.SettingsViewModel;
import com.nurtureshare.app.NurtureShareApp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel viewModel;
    private TokenManager tokenManager;
    private boolean isPartner;
    private String currentPairingCode = null;

    private static final SimpleDateFormat DISPLAY_FORMAT_UTC =
            new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat ISO_FORMAT_UTC =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static {
        DISPLAY_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
        ISO_FORMAT_UTC.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tokenManager = ((NurtureShareApp) requireActivity().getApplication()).getTokenManager();
        isPartner = tokenManager.isPartner();
        viewModel = new ViewModelProvider(this).get(SettingsViewModel.class);

        applyRoleLayout();
        setupSaveButton();
        setupShareButton();
        setupPairButton();
        setupEditDueDateButton();
        setupSaveBabyInfoButton();
        setupLogoutButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshSettings();
    }

    private void applyRoleLayout() {
        if (isPartner) {
            // Partner cannot change the due date — that belongs to the mother
            binding.btnEditDueDate.setVisibility(View.GONE);
            // Relabel pregnancy section to clarify it's the partner's partner's data
            binding.tvPregnancySectionLabel.setText(R.string.settings_pregnancy_partner_label);
            // Role chip — secondary colour
            binding.chipRole.setText(R.string.settings_role_partner);
            binding.chipRole.setChipBackgroundColor(
                    ColorStateList.valueOf(requireContext().getColor(R.color.secondary_fixed)));
            binding.chipRole.setTextColor(requireContext().getColor(R.color.on_secondary_fixed));
        } else {
            // Role chip — primary colour
            binding.chipRole.setText(R.string.settings_role_mother);
            binding.chipRole.setChipBackgroundColor(
                    ColorStateList.valueOf(requireContext().getColor(R.color.primary_fixed)));
            binding.chipRole.setTextColor(requireContext().getColor(R.color.on_primary_fixed));
        }
    }

    /** Single place to load and populate settings — used by onResume and after pair/save. */
    private void refreshSettings() {
        viewModel.loadSettingsAndObserve().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) populateSettings(resource.data);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String msg = resource.message != null ? resource.message : getString(R.string.settings_error);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void populateSettings(SettingsResponse settings) {
        // Profile
        String name = settings.getUserName() != null ? settings.getUserName() : "";
        String email = settings.getUserEmail() != null ? settings.getUserEmail() : "";
        binding.tvUserName.setText(name);
        binding.tvUserEmail.setText(email);
        if (!name.isEmpty()) {
            binding.tvAvatarInitial.setText(String.valueOf(name.charAt(0)).toUpperCase());
        }

        // Pregnancy info
        if (settings.getDueDate() != null && !settings.getDueDate().isEmpty()) {
            binding.tvDueDate.setText(formatIsoDate(settings.getDueDate()));
        } else {
            binding.tvDueDate.setText("—");
        }
        if (settings.getCurrentWeek() != null) {
            String trimester = settings.getTrimester() != null ? " · " + settings.getTrimester() : "";
            binding.tvCurrentWeek.setText("Week " + settings.getCurrentWeek() + trimester);
        } else {
            binding.tvCurrentWeek.setText("—");
        }

        // Notifications
        binding.switchNotifications.setOnCheckedChangeListener(null);
        binding.switchNotifications.setChecked(settings.isNotificationsEnabled());

        // App mode
        if ("NEWBORN".equalsIgnoreCase(settings.getAppMode())) {
            binding.toggleAppMode.check(R.id.btn_mode_baby);
        } else {
            binding.toggleAppMode.check(R.id.btn_mode_pregnancy);
        }

        // Baby info
        String babyGender = settings.getBabyGender();
        if ("BOY".equalsIgnoreCase(babyGender)) {
            binding.toggleBabyGender.check(R.id.btn_gender_boy);
        } else if ("GIRL".equalsIgnoreCase(babyGender)) {
            binding.toggleBabyGender.check(R.id.btn_gender_girl);
        } else {
            binding.toggleBabyGender.check(R.id.btn_gender_unknown);
        }
        String babyName = settings.getBabyName();
        binding.etBabyName.setText(babyName != null ? babyName : "");

        // Couple / partner
        CoupleStatusResponse coupleStatus = settings.getCoupleStatus();
        if (coupleStatus != null) {
            currentPairingCode = coupleStatus.getPairingCode();
            String coupleId = coupleStatus.getCoupleId();
            if (coupleId != null && !coupleId.isEmpty()) {
                binding.tvCoupleId.setText("#" + coupleId.substring(0, Math.min(8, coupleId.length())));
            } else {
                binding.tvCoupleId.setText("#—");
            }

            if (coupleStatus.isConnected() && coupleStatus.getPartner() != null) {
                UserResponse partner = coupleStatus.getPartner();
                binding.tvPartnerName.setText(partner.getName() != null ? partner.getName() : "");
                binding.chipSynced.setVisibility(View.VISIBLE);
                if (partner.getAvatarUrl() != null && !partner.getAvatarUrl().isEmpty()) {
                    Glide.with(this).load(partner.getAvatarUrl())
                            .placeholder(R.drawable.ic_person).error(R.drawable.ic_person)
                            .into(binding.ivPartnerAvatar);
                }
            } else {
                binding.tvPartnerName.setText(R.string.settings_no_partner);
                binding.chipSynced.setVisibility(View.GONE);
            }
        }
    }

    private void setupSaveButton() {
        binding.btnSaveSettings.setOnClickListener(v -> {
            boolean notifications = binding.switchNotifications.isChecked();
            String appMode = binding.toggleAppMode.getCheckedButtonId() == R.id.btn_mode_baby
                    ? "NEWBORN" : "PREGNANCY";

            binding.btnSaveSettings.setEnabled(false);
            UpdateSettingsRequest request = new UpdateSettingsRequest(notifications, appMode);
            viewModel.updateSettingsAndObserve(request).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        binding.btnSaveSettings.setEnabled(true);
                        Snackbar.make(requireView(), R.string.settings_saved, Snackbar.LENGTH_SHORT).show();
                        if (resource.data != null) populateSettings(resource.data);
                        break;
                    case ERROR:
                        binding.btnSaveSettings.setEnabled(true);
                        String msg = resource.message != null ? resource.message : getString(R.string.settings_error);
                        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }

    private void setupShareButton() {
        binding.btnShareInvite.setOnClickListener(v -> {
            String code = currentPairingCode != null ? currentPairingCode : "";
            if (code.isEmpty()) {
                Snackbar.make(requireView(), R.string.settings_no_pairing_code, Snackbar.LENGTH_SHORT).show();
                return;
            }
            String message = getString(R.string.settings_share_invite_message, code);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, getString(R.string.btn_share_invite_code)));
        });
    }

    private void setupPairButton() {
        binding.btnPair.setOnClickListener(v -> {
            String code = binding.etPairingCode.getText() != null
                    ? binding.etPairingCode.getText().toString().trim() : "";
            if (code.isEmpty()) {
                binding.tilPairingCode.setError(getString(R.string.hint_pairing_code));
                return;
            }
            binding.tilPairingCode.setError(null);
            binding.btnPair.setEnabled(false);

            viewModel.pairWithPartnerAndObserve(code).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        break;
                    case SUCCESS:
                        binding.btnPair.setEnabled(true);
                        binding.etPairingCode.setText("");
                        Snackbar.make(requireView(), R.string.paired_success, Snackbar.LENGTH_SHORT).show();
                        refreshSettings();
                        break;
                    case ERROR:
                        binding.btnPair.setEnabled(true);
                        String msg = resource.message != null ? resource.message : getString(R.string.pair_error);
                        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        });
    }

    private void setupSaveBabyInfoButton() {
        binding.btnSaveBabyInfo.setOnClickListener(v -> {
            int checkedId = binding.toggleBabyGender.getCheckedButtonId();
            String gender;
            if (checkedId == R.id.btn_gender_boy) {
                gender = "BOY";
            } else if (checkedId == R.id.btn_gender_girl) {
                gender = "GIRL";
            } else {
                gender = "UNKNOWN";
            }

            String name = binding.etBabyName.getText() != null
                    ? binding.etBabyName.getText().toString().trim() : "";

            binding.btnSaveBabyInfo.setEnabled(false);
            UpdateBabyInfoRequest request = new UpdateBabyInfoRequest(gender, name);
            ApiClient.getInstance(requireContext()).getApiService()
                    .updateBabyInfo(request)
                    .enqueue(new Callback<ApiResponse<TimelineResponse>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<TimelineResponse>> call,
                                               Response<ApiResponse<TimelineResponse>> response) {
                            if (!isAdded()) return;
                            binding.btnSaveBabyInfo.setEnabled(true);
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Snackbar.make(requireView(), R.string.baby_info_updated, Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(requireView(), "Failed to save baby info.", Snackbar.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<TimelineResponse>> call, Throwable t) {
                            if (!isAdded()) return;
                            binding.btnSaveBabyInfo.setEnabled(true);
                            Snackbar.make(requireView(), "Network error.", Snackbar.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void setupEditDueDateButton() {
        binding.btnEditDueDate.setOnClickListener(v -> openDueDatePicker());
    }

    private void openDueDatePicker() {
        long tenMonthsMs = MaterialDatePicker.todayInUtcMilliseconds() + (304L * 24 * 60 * 60 * 1000);
        java.util.List<CalendarConstraints.DateValidator> validators = new java.util.ArrayList<>();
        validators.add(DateValidatorPointForward.now());
        validators.add(DateValidatorPointBackward.before(tenMonthsMs));
        CalendarConstraints constraints = new CalendarConstraints.Builder()
                .setEnd(tenMonthsMs)
                .setValidator(CompositeDateValidator.allOf(validators))
                .build();

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.settings_due_date_label))
                .setCalendarConstraints(constraints)
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            Date date = new Date(selection);
            String isoDate = ISO_FORMAT_UTC.format(date);
            saveDueDate(isoDate);
        });

        picker.show(getParentFragmentManager(), "SETTINGS_DUE_DATE");
    }

    private void saveDueDate(String isoDate) {
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance(requireContext()).getApiService()
                .setupPregnancy(new PregnancySetupRequest(isoDate, null))
                .enqueue(new Callback<ApiResponse<TimelineResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TimelineResponse>> call,
                                           Response<ApiResponse<TimelineResponse>> response) {
                        if (!isAdded()) return;
                        binding.progressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Snackbar.make(requireView(), R.string.due_date_updated, Snackbar.LENGTH_SHORT).show();
                            refreshSettings();
                        } else {
                            Snackbar.make(requireView(), "Failed to update due date.", Snackbar.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TimelineResponse>> call, Throwable t) {
                        if (!isAdded()) return;
                        binding.progressBar.setVisibility(View.GONE);
                        Snackbar.make(requireView(), "Network error.", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void setupLogoutButton() {
        binding.btnLogout.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.logout_confirm_title)
                    .setMessage(R.string.logout_confirm_message)
                    .setPositiveButton(R.string.btn_logout, (dialog, which) -> performLogout())
                    .setNegativeButton(R.string.btn_cancel, null)
                    .show();
        });
    }

    private void performLogout() {
        tokenManager.clearToken();
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private String formatIsoDate(String isoDate) {
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = parser.parse(isoDate);
            return DISPLAY_FORMAT_UTC.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
