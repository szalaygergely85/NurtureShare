package com.nurtureshare.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.MainActivity;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.ActivityOnboardingBinding;
import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.PairRequest;
import com.nurtureshare.app.network.model.request.PregnancySetupRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.CoupleStatusResponse;
import com.nurtureshare.app.network.model.response.TimelineResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private ApiService apiService;

    private String selectedDate = null;
    private boolean isBabyMode = false;

    private static final SimpleDateFormat DISPLAY_FORMAT =
            new SimpleDateFormat("MMMM d, yyyy", Locale.getDefault());
    private static final SimpleDateFormat ISO_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    static {
        DISPLAY_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = ApiClient.getInstance(this).getApiService();

        binding.btnBegin.setEnabled(false);
        binding.toggleMode.check(R.id.btn_mode_direct);

        setupModeCards();
        setupPregnancyInputs();
        setupBabyInputs();

        binding.btnBegin.setOnClickListener(v -> submitSetup());
        binding.btnHaveCode.setOnClickListener(v -> showJoinState());
        binding.btnBackToSetup.setOnClickListener(v -> showSetupState());
        binding.btnJoin.setOnClickListener(v -> submitJoin());
        binding.btnContinue.setOnClickListener(v -> navigateToMain());
        binding.btnShareCode.setOnClickListener(v -> shareCode());
    }

    // ─── State switching ──────────────────────────────────────────────────────

    private void showSetupState() {
        binding.stateSetup.setVisibility(View.VISIBLE);
        binding.stateJoin.setVisibility(View.GONE);
        binding.stateInvite.setVisibility(View.GONE);
        binding.tvHeroTitle.setText("What's your situation?");
        binding.tvHeroSubtitle.setText("We'll personalise everything for you");
    }

    private void showJoinState() {
        binding.stateSetup.setVisibility(View.GONE);
        binding.stateJoin.setVisibility(View.VISIBLE);
        binding.stateInvite.setVisibility(View.GONE);
        binding.tvHeroTitle.setText("Join your partner");
        binding.tvHeroSubtitle.setText("Enter the invite code from their Settings screen");
    }

    private void showInviteState(String code) {
        binding.stateSetup.setVisibility(View.GONE);
        binding.stateJoin.setVisibility(View.GONE);
        binding.stateInvite.setVisibility(View.VISIBLE);
        binding.tvHeroTitle.setText("You're all set! 🎉");
        binding.tvHeroSubtitle.setText("Invite your partner to join you");
        binding.tvInviteCode.setText(code);
    }

    // ─── Mode selector cards ─────────────────────────────────────────────────

    private void setupModeCards() {
        updateModeCards();

        binding.cardModePregnancy.setOnClickListener(v -> {
            if (isBabyMode) {
                isBabyMode = false;
                selectedDate = null;
                binding.btnBegin.setEnabled(false);
                updateModeCards();
                binding.layoutPregnancy.setVisibility(View.VISIBLE);
                binding.layoutBaby.setVisibility(View.GONE);
                binding.tvHeroTitle.setText("When is your baby due?");
                binding.tvHeroSubtitle.setText("We'll track milestones and personalise everything for you");
                binding.btnBegin.setText("Let's Begin");
            }
        });

        binding.cardModeBaby.setOnClickListener(v -> {
            if (!isBabyMode) {
                isBabyMode = true;
                selectedDate = null;
                binding.btnBegin.setEnabled(false);
                updateModeCards();
                binding.layoutPregnancy.setVisibility(View.GONE);
                binding.layoutBaby.setVisibility(View.VISIBLE);
                binding.tvHeroTitle.setText("Welcome to the baby stage!");
                binding.tvHeroSubtitle.setText("Tell us when your little one arrived");
                binding.btnBegin.setText("Get Started");
            }
        });
    }

    private void updateModeCards() {
        if (isBabyMode) {
            binding.cardModePregnancy.setCardBackgroundColor(getColor(R.color.surface_container_lowest));
            binding.cardModePregnancy.setStrokeColor(getColor(R.color.outline_variant));
            binding.cardModePregnancy.setStrokeWidth(1);
            binding.cardModeBaby.setCardBackgroundColor(getColor(R.color.primary_container));
            binding.cardModeBaby.setStrokeColor(getColor(R.color.primary));
            binding.cardModeBaby.setStrokeWidth(dpToPx(2));
        } else {
            binding.cardModePregnancy.setCardBackgroundColor(getColor(R.color.primary_container));
            binding.cardModePregnancy.setStrokeColor(getColor(R.color.primary));
            binding.cardModePregnancy.setStrokeWidth(dpToPx(2));
            binding.cardModeBaby.setCardBackgroundColor(getColor(R.color.surface_container_lowest));
            binding.cardModeBaby.setStrokeColor(getColor(R.color.outline_variant));
            binding.cardModeBaby.setStrokeWidth(1);
        }
    }

    // ─── Pregnancy inputs ─────────────────────────────────────────────────────

    private void setupPregnancyInputs() {
        binding.toggleMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) return;
            if (checkedId == binding.btnModeDirect.getId()) {
                binding.layoutDirect.setVisibility(View.VISIBLE);
                binding.layoutCalculator.setVisibility(View.GONE);
            } else {
                binding.layoutDirect.setVisibility(View.GONE);
                binding.layoutCalculator.setVisibility(View.VISIBLE);
            }
            selectedDate = null;
            binding.btnBegin.setEnabled(false);
        });

        binding.cardDate.setOnClickListener(v -> {
            long tenMonthsMs = MaterialDatePicker.todayInUtcMilliseconds() + (304L * 24 * 60 * 60 * 1000);
            java.util.List<CalendarConstraints.DateValidator> validators = new java.util.ArrayList<>();
            validators.add(DateValidatorPointForward.now());
            validators.add(DateValidatorPointBackward.before(tenMonthsMs));
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select due date")
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setEnd(tenMonthsMs)
                            .setValidator(CompositeDateValidator.allOf(validators)).build())
                    .build();
            picker.addOnPositiveButtonClickListener(ms -> {
                Date date = new Date(ms);
                selectedDate = ISO_FORMAT.format(date);
                binding.tvSelectedDate.setText(DISPLAY_FORMAT.format(date));
                binding.btnBegin.setEnabled(true);
            });
            picker.show(getSupportFragmentManager(), "DUE_DATE");
        });

        binding.cardLmp.setOnClickListener(v -> {
            long twoYearsAgo = MaterialDatePicker.todayInUtcMilliseconds() - (2L * 365 * 24 * 60 * 60 * 1000);
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("First day of last period")
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setStart(twoYearsAgo)
                            .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                            .setValidator(DateValidatorPointBackward.now()).build())
                    .build();
            picker.addOnPositiveButtonClickListener(lmpMs -> {
                Date lmp = new Date(lmpMs);
                Date due = new Date(lmpMs + 280L * 24 * 60 * 60 * 1000);
                selectedDate = ISO_FORMAT.format(due);
                binding.tvLmpDate.setText(DISPLAY_FORMAT.format(lmp));
                binding.tvCalculatedDueDate.setText(DISPLAY_FORMAT.format(due));
                int weeks = (int) Math.max(0, Math.min(40,
                        (System.currentTimeMillis() - lmpMs) / (7L * 24 * 60 * 60 * 1000)));
                binding.tvWeeksAlong.setText(getString(R.string.onboarding_weeks_along, weeks));
                binding.cardCalculatedResult.setVisibility(View.VISIBLE);
                binding.btnBegin.setEnabled(true);
            });
            picker.show(getSupportFragmentManager(), "LMP");
        });
    }

    // ─── Baby inputs ──────────────────────────────────────────────────────────

    private void setupBabyInputs() {
        binding.cardBirthDate.setOnClickListener(v -> {
            long threeYearsAgo = MaterialDatePicker.todayInUtcMilliseconds() - (3L * 365 * 24 * 60 * 60 * 1000);
            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select birth date")
                    .setCalendarConstraints(new CalendarConstraints.Builder()
                            .setStart(threeYearsAgo)
                            .setEnd(MaterialDatePicker.todayInUtcMilliseconds())
                            .setValidator(DateValidatorPointBackward.now()).build())
                    .build();
            picker.addOnPositiveButtonClickListener(ms -> {
                Date date = new Date(ms);
                selectedDate = ISO_FORMAT.format(date);
                binding.tvBirthDate.setText(DISPLAY_FORMAT.format(date));

                long daysSince = (System.currentTimeMillis() - ms) / (24L * 60 * 60 * 1000);
                int weeks = (int) (daysSince / 7);
                int days = (int) (daysSince % 7);
                String age = weeks > 0
                        ? weeks + " week" + (weeks != 1 ? "s" : "")
                          + (days > 0 ? " and " + days + " day" + (days != 1 ? "s" : "") : "")
                          + " old"
                        : days + " day" + (days != 1 ? "s" : "") + " old";
                binding.tvBabyAge.setText(age);
                binding.cardBabyAgeResult.setVisibility(View.VISIBLE);
                binding.btnBegin.setEnabled(true);
            });
            picker.show(getSupportFragmentManager(), "BIRTH");
        });
    }

    // ─── Submit setup ─────────────────────────────────────────────────────────

    private void submitSetup() {
        if (selectedDate == null) return;
        setSetupLoading(true);

        String appMode = isBabyMode ? "NEWBORN" : "PREGNANCY";
        apiService.setupPregnancy(new PregnancySetupRequest(selectedDate, appMode))
                .enqueue(new Callback<ApiResponse<TimelineResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<TimelineResponse>> call,
                                           Response<ApiResponse<TimelineResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            fetchAndShowInviteCode();
                        } else {
                            setSetupLoading(false);
                            showError("Failed to save. Please try again.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<TimelineResponse>> call, Throwable t) {
                        setSetupLoading(false);
                        showError(t.getMessage());
                    }
                });
    }

    private void fetchAndShowInviteCode() {
        apiService.getPairingCode().enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call,
                                   Response<ApiResponse<String>> response) {
                setSetupLoading(false);
                String code = (response.isSuccessful() && response.body() != null)
                        ? response.body().getData() : "—";
                showInviteState(code != null ? code : "—");
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                setSetupLoading(false);
                showInviteState("—");
            }
        });
    }

    // ─── Submit join ──────────────────────────────────────────────────────────

    private void submitJoin() {
        String code = binding.etPairingCode.getText() != null
                ? binding.etPairingCode.getText().toString().trim().toUpperCase() : "";
        if (code.length() != 5) {
            binding.tilPairingCode.setError("Please enter the 5-character invite code");
            return;
        }
        binding.tilPairingCode.setError(null);
        setJoinLoading(true);

        apiService.pairWithPartner(new PairRequest(code))
                .enqueue(new Callback<ApiResponse<CoupleStatusResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CoupleStatusResponse>> call,
                                           Response<ApiResponse<CoupleStatusResponse>> response) {
                        setJoinLoading(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            navigateToMain();
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : null;
                            binding.tilPairingCode.setError(
                                    msg != null ? msg : "Invalid code. Check with your partner.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CoupleStatusResponse>> call, Throwable t) {
                        setJoinLoading(false);
                        showError(t.getMessage());
                    }
                });
    }

    // ─── Share code ───────────────────────────────────────────────────────────

    private void shareCode() {
        String code = binding.tvInviteCode.getText().toString();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT,
                "Join me on NurtureShare! Use invite code: " + code);
        startActivity(Intent.createChooser(share, "Share invite code"));
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private void setSetupLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnBegin.setEnabled(!loading);
    }

    private void setJoinLoading(boolean loading) {
        binding.progressBarJoin.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnJoin.setEnabled(!loading);
        binding.btnBackToSetup.setEnabled(!loading);
    }

    private void showError(String msg) {
        Snackbar.make(binding.getRoot(),
                msg != null ? msg : "Something went wrong.",
                Snackbar.LENGTH_LONG).show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}
