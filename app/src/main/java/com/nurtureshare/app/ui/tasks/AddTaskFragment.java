package com.nurtureshare.app.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentAddTaskBinding;
import com.nurtureshare.app.network.model.request.CreateTaskRequest;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.TaskViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;
    private TaskViewModel viewModel;
    private String selectedDueDate = null;
    private String editTaskId = null;
    private boolean isSaving = false;
    private boolean isPartner = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = ((NurtureShareApp) requireActivity().getApplication()).getTokenManager();
        isPartner = tokenManager.isPartner();
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupDueDatePicker();
        setupSaveButton();
        setupBackButton();
        setupDeleteButton();

        // Check for edit mode args
        Bundle args = getArguments();
        if (args != null && !args.getString("taskId", "").isEmpty()) {
            editTaskId = args.getString("taskId");
            binding.tvFormTitle.setText(getString(R.string.edit_task_title));
            binding.btnSaveTask.setText(getString(R.string.btn_save_changes));
            binding.btnDeleteTask.setVisibility(View.VISIBLE);
            prefillFields(args);
        }

        // Restore selected due date across rotation (overrides args value if user changed it)
        if (savedInstanceState != null && savedInstanceState.containsKey("selectedDueDate")) {
            selectedDueDate = savedInstanceState.getString("selectedDueDate");
            if (selectedDueDate != null && !selectedDueDate.isEmpty()) {
                binding.tvDueDate.setText(selectedDueDate);
                binding.tvDueDate.setTextColor(requireContext().getColor(R.color.on_surface));
            } else {
                binding.tvDueDate.setText(R.string.hint_select_date);
                binding.tvDueDate.setTextColor(requireContext().getColor(R.color.outline));
                selectedDueDate = null;
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("selectedDueDate", selectedDueDate != null ? selectedDueDate : "");
    }

    private void prefillFields(Bundle args) {
        binding.etTaskTitle.setText(args.getString("taskTitle", ""));
        binding.etTaskDescription.setText(args.getString("taskDescription", ""));

        // Show the assignment from the current user's perspective.
        // Backend stores "ME" = mother, "PARTNER" = partner.
        // A PARTNER user sees their own tasks stored as "PARTNER" → show "Me" button.
        // A PARTNER user sees the mother's tasks stored as "ME" → show "Partner" button.
        String assignedTo = args.getString("taskAssignedTo", "ME");
        if ("BOTH".equals(assignedTo)) {
            binding.toggleAssign.check(R.id.btn_assign_both);
        } else if (isPartner) {
            binding.toggleAssign.check(
                    "PARTNER".equals(assignedTo) ? R.id.btn_assign_me : R.id.btn_assign_partner);
        } else {
            binding.toggleAssign.check(
                    "PARTNER".equals(assignedTo) ? R.id.btn_assign_partner : R.id.btn_assign_me);
        }
        String dueDate = args.getString("taskDueDate", "");
        if (!dueDate.isEmpty()) {
            selectedDueDate = dueDate;
            binding.tvDueDate.setText(dueDate);
            binding.tvDueDate.setTextColor(requireContext().getColor(R.color.on_surface));
        }

        binding.switchShareTask.setChecked(args.getBoolean("taskSynced", false));
    }

    private void setupBackButton() {
        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );
    }

    private void setupDueDatePicker() {
        binding.cardDueDate.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.label_due_date))
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = sdf.format(new Date(selection));
                selectedDueDate = formatted;
                binding.tvDueDate.setText(formatted);
                binding.tvDueDate.setTextColor(requireContext().getColor(R.color.on_surface));
            });

            datePicker.show(getParentFragmentManager(), "DATE_PICKER");
        });
    }

    private void setupSaveButton() {
        binding.btnSaveTask.setOnClickListener(v -> saveTask());
    }

    private void setupDeleteButton() {
        binding.btnDeleteTask.setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.task_delete_confirm_title)
                    .setMessage(R.string.task_delete_confirm_message)
                    .setNegativeButton(R.string.btn_cancel, null)
                    .setPositiveButton(R.string.btn_delete, (dialog, which) -> {
                        viewModel.deleteTaskAndObserve(editTaskId).observe(getViewLifecycleOwner(), resource -> {
                            switch (resource.status) {
                                case LOADING:
                                    setLoading(true);
                                    break;
                                case SUCCESS:
                                    setLoading(false);
                                    Snackbar.make(requireView(), R.string.task_deleted_success, Snackbar.LENGTH_SHORT).show();
                                    Navigation.findNavController(requireView()).navigateUp();
                                    break;
                                case ERROR:
                                    setLoading(false);
                                    String msg = resource.message != null ? resource.message : getString(R.string.task_delete_error);
                                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                                    break;
                            }
                        });
                    })
                    .show();
        });
    }

    private void saveTask() {
        if (isSaving) return;

        String title = binding.etTaskTitle.getText() != null
                ? binding.etTaskTitle.getText().toString().trim() : "";

        if (title.isEmpty()) {
            binding.tilTaskTitle.setError(getString(R.string.error_task_title_empty));
            return;
        }
        binding.tilTaskTitle.setError(null);

        String description = binding.etTaskDescription.getText() != null
                ? binding.etTaskDescription.getText().toString().trim() : "";

        // Translate button selection to backend values.
        // Backend stores "ME" = mother's task, "PARTNER" = partner's task.
        // A PARTNER user clicking "Me" wants their own task → stored as "PARTNER".
        // A PARTNER user clicking "Partner" means the mother → stored as "ME".
        String assignedTo;
        int checkedId = binding.toggleAssign.getCheckedButtonId();
        if (checkedId == R.id.btn_assign_partner) {
            assignedTo = isPartner ? "ME" : "PARTNER";
        } else if (checkedId == R.id.btn_assign_both) {
            assignedTo = "BOTH";
        } else {
            assignedTo = isPartner ? "PARTNER" : "ME";
        }

        boolean shareWithPartner = binding.switchShareTask.isChecked();

        if (editTaskId != null) {
            UpdateTaskRequest updateRequest = new UpdateTaskRequest(
                    title,
                    description.isEmpty() ? null : description,
                    assignedTo,
                    selectedDueDate,
                    "PENDING",
                    shareWithPartner
            );
            viewModel.updateTaskAndObserve(editTaskId, updateRequest).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        setLoading(true);
                        break;
                    case SUCCESS:
                        setLoading(false);
                        Snackbar.make(requireView(), R.string.task_updated_success, Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                        break;
                    case ERROR:
                        setLoading(false);
                        String msg = resource.message != null ? resource.message : getString(R.string.task_update_error);
                        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        } else {
            boolean nudge = binding.switchNudge.isChecked();
            CreateTaskRequest request = new CreateTaskRequest(
                    title,
                    description.isEmpty() ? null : description,
                    assignedTo,
                    selectedDueDate,
                    nudge,
                    shareWithPartner
            );
            viewModel.createTaskAndObserve(request).observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        setLoading(true);
                        break;
                    case SUCCESS:
                        setLoading(false);
                        Snackbar.make(requireView(), R.string.task_created_success, Snackbar.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                        break;
                    case ERROR:
                        setLoading(false);
                        String msg = resource.message != null ? resource.message : getString(R.string.task_create_error);
                        Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        }
    }

    private void setLoading(boolean loading) {
        isSaving = loading;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSaveTask.setEnabled(!loading);
        binding.btnDeleteTask.setEnabled(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
