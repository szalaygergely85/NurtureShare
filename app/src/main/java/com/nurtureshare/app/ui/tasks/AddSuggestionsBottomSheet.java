package com.nurtureshare.app.ui.tasks;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.databinding.BottomSheetAddSuggestionsBinding;
import com.nurtureshare.app.network.model.request.CreateTaskRequest;
import com.nurtureshare.app.util.Resource;
import com.nurtureshare.app.viewmodel.TaskViewModel;

import java.util.ArrayList;
import java.util.List;

public class AddSuggestionsBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_SUGGESTIONS = "suggestions";

    private BottomSheetAddSuggestionsBinding binding;
    private TaskViewModel viewModel;
    private final List<CheckBox> checkBoxes = new ArrayList<>();

    public static AddSuggestionsBottomSheet newInstance(List<String> suggestions) {
        AddSuggestionsBottomSheet sheet = new AddSuggestionsBottomSheet();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_SUGGESTIONS, new ArrayList<>(suggestions));
        sheet.setArguments(args);
        return sheet;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomSheetAddSuggestionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        ArrayList<String> suggestions = getArguments() != null
                ? getArguments().getStringArrayList(ARG_SUGGESTIONS)
                : new ArrayList<>();

        buildCheckBoxList(suggestions);
        setupConfirmButton(suggestions);
    }

    private void buildCheckBoxList(List<String> suggestions) {
        for (String text : suggestions) {
            CheckBox cb = new CheckBox(requireContext());
            cb.setText(text);
            cb.setTextSize(14f);
            cb.setTextColor(requireContext().getColor(
                    com.nurtureshare.app.R.color.on_surface_variant));
            int pad = (int) (8 * getResources().getDisplayMetrics().density);
            cb.setPadding(cb.getPaddingLeft(), pad, 0, pad);
            cb.setOnCheckedChangeListener((btn, checked) -> updateConfirmButton());
            checkBoxes.add(cb);
            binding.llSuggestionItems.addView(cb);
        }
    }

    private void setupConfirmButton(List<String> suggestions) {
        binding.btnConfirmAdd.setOnClickListener(v -> submitSelected());
    }

    private void updateConfirmButton() {
        int count = countChecked();
        binding.btnConfirmAdd.setEnabled(count > 0);
        if (count == 0) {
            binding.btnConfirmAdd.setText("Select actions");
        } else {
            binding.btnConfirmAdd.setText(count == 1 ? "Add 1 task" : "Add " + count + " tasks");
        }
    }

    private int countChecked() {
        int count = 0;
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) count++;
        }
        return count;
    }

    private void submitSelected() {
        List<String> selected = new ArrayList<>();
        for (CheckBox cb : checkBoxes) {
            if (cb.isChecked()) selected.add(cb.getText().toString());
        }
        if (selected.isEmpty()) return;

        binding.btnConfirmAdd.setEnabled(false);

        int total = selected.size();
        int[] remaining = { total };
        int[] successes = { 0 };

        for (String title : selected) {
            CreateTaskRequest req = new CreateTaskRequest(title, null, "ME", null, false);
            viewModel.createTaskAndObserve(req).observe(getViewLifecycleOwner(), resource -> {
                if (resource.status == Resource.Status.LOADING) return;
                if (resource.status == Resource.Status.SUCCESS) successes[0]++;
                remaining[0]--;
                if (remaining[0] == 0) onAllDone(successes[0], total);
            });
        }
    }

    private void onAllDone(int successes, int total) {
        View anchor = getView();
        if (anchor != null) {
            String msg = successes == total
                    ? (total == 1 ? "Task added" : successes + " tasks added")
                    : (successes + " of " + total + " tasks added");
            Snackbar.make(anchor, msg, Snackbar.LENGTH_SHORT).show();
        }
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
