package com.nurtureshare.app.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentTasksBinding;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.network.model.response.TaskResponse;
import com.nurtureshare.app.util.Resource;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.TaskViewModel;

public class TasksFragment extends Fragment {

    private FragmentTasksBinding binding;
    private TaskViewModel viewModel;
    private TaskAdapter taskAdapter;
    private boolean isPartner;
    private String currentFilter = "ALL";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = ((NurtureShareApp) requireActivity().getApplication()).getTokenManager();
        isPartner = tokenManager.isPartner();
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        setupRecyclerView();
        setupFilterChips();

        binding.fabAddTask.setOnClickListener(v ->
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_tasks_to_addTask)
        );
    }

    @Override
    public void onResume() {
        super.onResume();
        // Always refresh both lists and progress when returning to this screen
        loadTasks();
        loadProgress();
    }

    private void setupRecyclerView() {
        taskAdapter = new TaskAdapter(new TaskAdapter.TaskActionListener() {
            @Override
            public void onStatusToggled(TaskResponse task, boolean completed) {
                String newStatus = completed ? "COMPLETED" : "PENDING";
                UpdateTaskRequest request = new UpdateTaskRequest(
                        task.getTitle(),
                        task.getDescription(),
                        task.getAssignedTo(),
                        task.getDueDate(),
                        newStatus
                );
                viewModel.updateTaskAndObserve(task.getId(), request).observe(
                        getViewLifecycleOwner(), resource -> {
                            if (resource.status == Resource.Status.SUCCESS) {
                                loadTasks();
                                loadProgress();
                            } else if (resource.status == Resource.Status.ERROR) {
                                Snackbar.make(requireView(), R.string.task_update_error, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                );
            }

            @Override
            public void onNudgeClicked(TaskResponse task) {
                viewModel.nudgePartnerAndObserve(task.getId()).observe(
                        getViewLifecycleOwner(), resource -> {
                            if (resource.status == Resource.Status.SUCCESS) {
                                Snackbar.make(requireView(), R.string.nudge_sent, Snackbar.LENGTH_SHORT).show();
                            } else if (resource.status == Resource.Status.ERROR) {
                                String msg = resource.message != null ? resource.message : "Failed to send nudge.";
                                Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                            }
                        }
                );
            }

            @Override
            public void onEditClicked(TaskResponse task) {
                Bundle args = new Bundle();
                args.putString("taskId", task.getId());
                args.putString("taskTitle", task.getTitle() != null ? task.getTitle() : "");
                args.putString("taskDescription", task.getDescription() != null ? task.getDescription() : "");
                args.putString("taskAssignedTo", task.getAssignedTo() != null ? task.getAssignedTo() : "ME");
                args.putString("taskDueDate", task.getDueDate() != null ? task.getDueDate() : "");
                args.putBoolean("taskSynced", task.isSynced());
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_tasks_to_addTask, args);
            }

            @Override
            public void onDetailClicked(TaskResponse task) {
                Bundle args = new Bundle();
                args.putString("taskId", task.getId());
                args.putString("taskTitle", task.getTitle() != null ? task.getTitle() : "");
                args.putString("taskDescription", task.getDescription() != null ? task.getDescription() : "");
                args.putString("taskAssignedTo", task.getAssignedTo() != null ? task.getAssignedTo() : "ME");
                args.putString("taskDueDate", task.getDueDate() != null ? task.getDueDate() : "");
                args.putString("taskStatus", task.getStatus() != null ? task.getStatus() : "PENDING");
                args.putBoolean("taskSynced", task.isSynced());
                androidx.navigation.Navigation.findNavController(requireView())
                        .navigate(R.id.action_tasks_to_taskDetail, args);
            }
        }, isPartner);

        binding.rvTasks.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTasks.setAdapter(taskAdapter);
    }

    private void setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int checkedId = checkedIds.get(0);

            if (checkedId == R.id.chip_all) {
                currentFilter = "ALL";
            } else if (checkedId == R.id.chip_me) {
                currentFilter = isPartner ? "PARTNER" : "ME";
            } else if (checkedId == R.id.chip_partner) {
                currentFilter = isPartner ? "ME" : "PARTNER";
            } else if (checkedId == R.id.chip_both) {
                currentFilter = "BOTH";
            } else if (checkedId == R.id.chip_completed) {
                currentFilter = "COMPLETED";
            }
            loadTasks();
        });
    }

    private void loadTasks() {
        viewModel.loadTasksAndObserve(currentFilter).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    if (resource.data != null) {
                        taskAdapter.setTasks(resource.data);
                        boolean empty = resource.data.isEmpty();
                        binding.rvTasks.setVisibility(empty ? View.GONE : View.VISIBLE);
                        binding.layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
                        binding.layoutHint.setVisibility(empty ? View.GONE : View.VISIBLE);
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String msg = resource.message != null ? resource.message : getString(R.string.tasks_error);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, v -> loadTasks())
                            .show();
                    break;
            }
        });
    }

    // Called on every onResume so the ring + labels always reflect the latest state
    private void loadProgress() {
        viewModel.loadProgressAndObserve().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                binding.progressRing.setProgressCompat(resource.data.getProgressPercent(), true);
                binding.tvProgressLabel.setText(getString(
                        R.string.tasks_progress_format,
                        resource.data.getCompletedTasks(),
                        resource.data.getTotalTasks()
                ));
                binding.tvProgressPercent.setText(resource.data.getProgressPercent() + "%");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
