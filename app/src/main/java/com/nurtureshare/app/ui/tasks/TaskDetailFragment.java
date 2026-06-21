package com.nurtureshare.app.ui.tasks;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.NurtureShareApp;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentTaskDetailBinding;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.util.Resource;
import com.nurtureshare.app.util.TokenManager;
import com.nurtureshare.app.viewmodel.TaskViewModel;

public class TaskDetailFragment extends Fragment {

    private FragmentTaskDetailBinding binding;
    private TaskViewModel viewModel;
    private CommentAdapter commentAdapter;
    private boolean isPartner;

    private String taskId;
    private String taskTitle;
    private String taskDescription;
    private String taskAssignedTo;
    private String taskDueDate;
    private String taskStatus;
    private boolean taskSynced;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTaskDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TokenManager tokenManager = ((NurtureShareApp) requireActivity().getApplication()).getTokenManager();
        isPartner = tokenManager.isPartner();
        viewModel = new ViewModelProvider(this).get(TaskViewModel.class);

        readArgs();
        populateTaskHeader();
        setupCommentsList();
        setupButtons();
        loadComments();
    }

    private void readArgs() {
        Bundle args = getArguments();
        if (args != null) {
            taskId          = args.getString("taskId", "");
            taskTitle       = args.getString("taskTitle", "");
            taskDescription = args.getString("taskDescription", "");
            taskAssignedTo  = args.getString("taskAssignedTo", "ME");
            taskDueDate     = args.getString("taskDueDate", "");
            taskStatus      = args.getString("taskStatus", "PENDING");
            taskSynced      = args.getBoolean("taskSynced", false);
        }
    }

    private void populateTaskHeader() {
        boolean isCompleted = "COMPLETED".equalsIgnoreCase(taskStatus);

        // Title with strikethrough when done
        binding.tvTaskTitle.setText(taskTitle);
        if (isCompleted) {
            binding.tvTaskTitle.setPaintFlags(
                    binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            binding.tvTaskTitle.setAlpha(0.55f);
        }

        // Checkbox
        binding.cbComplete.setChecked(isCompleted);
        binding.cbComplete.setOnCheckedChangeListener((btn, checked) -> toggleStatus(checked));

        // Description
        if (taskDescription != null && !taskDescription.isEmpty()) {
            binding.tvDescription.setText(taskDescription);
            binding.cardDescription.setVisibility(View.VISIBLE);
        }

        // Badge
        applyBadge();

        // Due date
        if (taskDueDate != null && !taskDueDate.isEmpty()) {
            binding.tvDueDate.setText(taskDueDate);
            binding.ivDateIcon.setVisibility(View.VISIBLE);
            binding.tvDueDate.setVisibility(View.VISIBLE);
        }
    }

    private void applyBadge() {
        String assignedTo = taskAssignedTo != null ? taskAssignedTo : "ME";
        int badgeBg, badgeFg, badgeIconRes;
        String badgeText;
        switch (assignedTo.toUpperCase()) {
            case "PARTNER":
                if (isPartner) {
                    badgeBg  = requireContext().getColor(R.color.primary_fixed);
                    badgeFg  = requireContext().getColor(R.color.on_primary_fixed);
                    badgeIconRes = R.drawable.ic_person;
                    badgeText = "ME";
                } else {
                    badgeBg  = requireContext().getColor(R.color.secondary_fixed);
                    badgeFg  = requireContext().getColor(R.color.on_secondary_fixed_variant);
                    badgeIconRes = R.drawable.ic_person;
                    badgeText = "PARTNER";
                }
                break;
            case "BOTH":
                badgeBg  = requireContext().getColor(R.color.primary_fixed);
                badgeFg  = requireContext().getColor(R.color.on_primary_fixed_variant);
                badgeIconRes = R.drawable.ic_group;
                badgeText = "BOTH";
                break;
            default:
                if (isPartner) {
                    badgeBg  = requireContext().getColor(R.color.secondary_fixed);
                    badgeFg  = requireContext().getColor(R.color.on_secondary_fixed_variant);
                    badgeIconRes = R.drawable.ic_person;
                    badgeText = "PARTNER";
                } else {
                    badgeBg  = requireContext().getColor(R.color.primary_fixed);
                    badgeFg  = requireContext().getColor(R.color.on_primary_fixed);
                    badgeIconRes = R.drawable.ic_person;
                    badgeText = "ME";
                }
                break;
        }
        binding.llBadge.setBackgroundTintList(ColorStateList.valueOf(badgeBg));
        binding.tvBadgeLabel.setText(badgeText);
        binding.tvBadgeLabel.setTextColor(badgeFg);
        binding.ivBadgeIcon.setImageResource(badgeIconRes);
        binding.ivBadgeIcon.setColorFilter(badgeFg);
    }

    private void setupCommentsList() {
        commentAdapter = new CommentAdapter();
        binding.rvComments.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvComments.setAdapter(commentAdapter);
        binding.rvComments.setNestedScrollingEnabled(false);
    }

    private void setupButtons() {
        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );

        binding.btnEdit.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("taskId", taskId);
            args.putString("taskTitle", taskTitle);
            args.putString("taskDescription", taskDescription);
            args.putString("taskAssignedTo", taskAssignedTo);
            args.putString("taskDueDate", taskDueDate);
            args.putBoolean("taskSynced", taskSynced);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_taskDetail_to_addTask, args);
        });

        binding.btnSendComment.setOnClickListener(v -> sendComment());
    }

    private void loadComments() {
        binding.progressComments.setVisibility(View.VISIBLE);
        viewModel.loadCommentsAndObserve(taskId).observe(getViewLifecycleOwner(), resource -> {
            binding.progressComments.setVisibility(View.GONE);
            if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                commentAdapter.setComments(resource.data);
                boolean empty = resource.data.isEmpty();
                binding.rvComments.setVisibility(empty ? View.GONE : View.VISIBLE);
                binding.tvNoComments.setVisibility(empty ? View.VISIBLE : View.GONE);
                updateCommentsHeader(resource.data.size());
            } else if (resource.status == Resource.Status.ERROR) {
                binding.tvNoComments.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updateCommentsHeader(int count) {
        String label = count > 0
                ? getString(R.string.task_comments_count, count)
                : getString(R.string.task_comments);
        binding.tvCommentsHeader.setText(label);
    }

    private void sendComment() {
        String content = binding.etComment.getText() != null
                ? binding.etComment.getText().toString().trim() : "";
        if (content.isEmpty()) return;

        binding.btnSendComment.setEnabled(false);
        viewModel.addCommentAndObserve(taskId, content).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    break;
                case SUCCESS:
                    binding.btnSendComment.setEnabled(true);
                    binding.etComment.setText("");
                    hideKeyboard();
                    loadComments();
                    break;
                case ERROR:
                    binding.btnSendComment.setEnabled(true);
                    String msg = resource.message != null ? resource.message
                            : getString(R.string.comment_add_error);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    private void toggleStatus(boolean completed) {
        String newStatus = completed ? "COMPLETED" : "PENDING";
        UpdateTaskRequest request = new UpdateTaskRequest(
                taskTitle,
                taskDescription.isEmpty() ? null : taskDescription,
                taskAssignedTo,
                taskDueDate.isEmpty() ? null : taskDueDate,
                newStatus
        );
        viewModel.updateTaskAndObserve(taskId, request).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.SUCCESS) {
                taskStatus = newStatus;
                if (completed) {
                    binding.tvTaskTitle.setPaintFlags(
                            binding.tvTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.tvTaskTitle.setAlpha(0.55f);
                } else {
                    binding.tvTaskTitle.setPaintFlags(
                            binding.tvTaskTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.tvTaskTitle.setAlpha(1.0f);
                }
            } else if (resource.status == Resource.Status.ERROR) {
                // Revert checkbox on failure
                binding.cbComplete.setOnCheckedChangeListener(null);
                binding.cbComplete.setChecked(!completed);
                binding.cbComplete.setOnCheckedChangeListener((btn, c) -> toggleStatus(c));
                Snackbar.make(requireView(), R.string.task_update_error, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext()
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null && binding.etComment.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(binding.etComment.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
