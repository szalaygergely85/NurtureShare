package com.nurtureshare.app.ui.timeline;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentTimelineBinding;
import com.nurtureshare.app.network.model.response.MilestoneResponse;
import com.nurtureshare.app.network.model.response.TimelineResponse;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.TimelineViewModel;

import com.nurtureshare.app.ui.tasks.AddSuggestionsBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class TimelineFragment extends Fragment {

    private FragmentTimelineBinding binding;
    private TimelineViewModel viewModel;
    private boolean isPartner;
    private List<String> currentSuggestions = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTimelineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = ((NurtureShareApp) requireActivity().getApplication()).getTokenManager();
        isPartner = tokenManager.isPartner();

        viewModel = new ViewModelProvider(this).get(TimelineViewModel.class);

        applyRoleLayout();
        observeTimeline();
        setupButtons();
    }

    private void applyRoleLayout() {
        if (isPartner) {
            // Move partner focus card above the baby growth card
            ViewGroup parent = (ViewGroup) binding.cardPartnerFocus.getParent();
            parent.removeView(binding.cardPartnerFocus);
            parent.addView(binding.cardPartnerFocus, 5);
            binding.tvSuggestedActionsLabel.setText("FOR YOU THIS WEEK");
        } else {
            binding.cardPartnerFocus.setVisibility(View.GONE);
        }
    }

    private void setupButtons() {
        binding.btnAddAsTask.setOnClickListener(v -> {
            if (currentSuggestions.isEmpty()) return;
            AddSuggestionsBottomSheet.newInstance(currentSuggestions)
                    .show(getParentFragmentManager(), "add_suggestions");
        });
        binding.btnInvitePartner.setOnClickListener(v -> {
            com.google.android.material.bottomnavigation.BottomNavigationView nav =
                    requireActivity().findViewById(R.id.bottom_nav);
            nav.setSelectedItemId(R.id.settingsFragment);
        });
    }

    private void observeTimeline() {
        viewModel.getTimeline().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        populateTimeline(resource.data);
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String message = resource.message != null
                            ? resource.message
                            : getString(R.string.timeline_error);
                    Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, v -> viewModel.loadTimeline())
                            .show();
                    break;
            }
        });
    }

    private void populateTimeline(TimelineResponse data) {
        binding.cardInvitePartner.setVisibility(
                !data.isPartnerConnected() && !isPartner ? View.VISIBLE : View.GONE);

        boolean babyMode = data.isBabyMode();

        if (babyMode) {
            binding.tvWeek.setText(formatBabyAge(data.getCurrentWeek(), data.getBabyName(), data.getBabyGender()));
        } else {
            binding.tvWeek.setText(
                    String.format(getString(R.string.timeline_week_format), data.getCurrentWeek()));
        }

        if (babyMode) {
            binding.tvTrimester.setVisibility(View.GONE);
            binding.progressWeek.setVisibility(View.GONE);
            binding.tvPercentComplete.setVisibility(View.GONE);
            binding.tvWeeksLeft.setVisibility(View.GONE);
        } else {
            binding.tvTrimester.setVisibility(View.VISIBLE);
            binding.progressWeek.setVisibility(View.VISIBLE);
            binding.tvPercentComplete.setVisibility(View.VISIBLE);
            binding.tvWeeksLeft.setVisibility(View.VISIBLE);
            binding.tvTrimester.setText(data.getTrimester() != null ? data.getTrimester() : "");
            binding.progressWeek.setProgress(data.getPercentComplete());
            binding.tvPercentComplete.setText(
                    String.format(getString(R.string.timeline_progress_format), data.getPercentComplete()));
            binding.tvWeeksLeft.setText(
                    String.format(getString(R.string.timeline_weeks_left_format), data.getWeeksLeft()));
        }

        // Baby growth card
        MilestoneResponse milestone = data.getCurrentMilestone();
        if (milestone != null) {
            String sizeText = milestone.getBabySize() != null ? milestone.getBabySize() : "";
            binding.tvBabySize.setText(sizeText);
            binding.tvBabyDescription.setText(milestone.getDescription() != null ? milestone.getDescription() : "");
            binding.tvSizeTag.setText(sizeText.isEmpty() ? "Size reference" : "Size of a " + sizeText);
            loadMilestoneImage(milestone.getWeek());
            binding.frameBabyImage.setVisibility(View.VISIBLE);
        } else if (babyMode) {
            binding.tvBabySize.setText("");
            binding.tvBabyDescription.setText("Track your baby's milestones week by week.");
            binding.frameBabyImage.setVisibility(View.GONE);
        }

        // Checklist card
        List<String> checklist = data.getChecklistItems();
        if (checklist != null && !checklist.isEmpty()) {
            binding.cardChecklist.setVisibility(View.VISIBLE);
            binding.tvChecklistTitle.setText(
                    data.getChecklistTitle() != null ? data.getChecklistTitle().toUpperCase() : "CHECKLIST");
            binding.llChecklistItems.removeAllViews();
            for (String item : checklist) {
                CheckBox checkBox = new CheckBox(requireContext());
                checkBox.setText(item);
                checkBox.setTextColor(requireContext().getColor(R.color.on_surface_variant));
                checkBox.setTextSize(14f);
                int padding = (int) (6 * getResources().getDisplayMetrics().density);
                checkBox.setPadding(checkBox.getPaddingLeft(), padding, 0, padding);
                checkBox.setOnCheckedChangeListener((btn, isChecked) -> {
                    if (isChecked) {
                        btn.setPaintFlags(btn.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    } else {
                        btn.setPaintFlags(btn.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                });
                binding.llChecklistItems.addView(checkBox);
            }
        } else {
            binding.cardChecklist.setVisibility(View.GONE);
        }

        // Suggested actions (partner-specific actions come from the backend already)
        binding.llSuggestedActions.removeAllViews();
        List<String> actions = data.getSuggestedActions();
        currentSuggestions = actions != null ? new ArrayList<>(actions) : new ArrayList<>();
        if (actions != null) {
            for (String action : actions) {
                TextView actionView = new TextView(requireContext());
                actionView.setText("• " + action);
                actionView.setTextColor(requireContext().getColor(R.color.on_surface_variant));
                actionView.setTextSize(14f);
                int padding = (int) (8 * getResources().getDisplayMetrics().density);
                actionView.setPadding(0, padding / 2, 0, padding / 2);
                binding.llSuggestedActions.addView(actionView);
            }
        }

        if (isPartner) {
            binding.tvPartnerFocusTip.setText(
                    data.getPartnerFocusTip() != null ? data.getPartnerFocusTip() : "");
        }
    }

    private String formatBabyAge(int weeks, String babyName, String babyGender) {
        String age;
        if (weeks >= 52) {
            int years = weeks / 52;
            age = String.format(getString(R.string.timeline_baby_age_years_format), years);
        } else if (weeks >= 13) {
            int months = Math.round(weeks / 4.33f);
            age = String.format(getString(R.string.timeline_baby_age_months_format), months);
        } else {
            age = String.format(getString(R.string.timeline_baby_age_format), weeks);
        }
        if (babyName != null && !babyName.isBlank()) {
            return babyName + " is " + age;
        }
        if ("BOY".equalsIgnoreCase(babyGender)) {
            return "He is " + age;
        }
        if ("GIRL".equalsIgnoreCase(babyGender)) {
            return "She is " + age;
        }
        return age;
    }

    private void loadMilestoneImage(int week) {
        String name = String.format("milestone_week_%02d", week);
        int resId = getResources().getIdentifier(name, "drawable", requireContext().getPackageName());
        if (resId != 0) {
            binding.ivBabySize.setImageResource(resId);
            binding.ivBabySize.clearColorFilter();
        } else {
            binding.ivBabySize.setImageResource(R.drawable.ic_baby);
            binding.ivBabySize.setColorFilter(
                    requireContext().getColor(R.color.primary_fixed_dim),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
