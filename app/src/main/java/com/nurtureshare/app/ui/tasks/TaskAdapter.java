package com.nurtureshare.app.ui.tasks;

import android.content.res.ColorStateList;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.ItemTaskBinding;
import com.nurtureshare.app.network.model.response.TaskResponse;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskActionListener {
        void onStatusToggled(TaskResponse task, boolean completed);
        void onNudgeClicked(TaskResponse task);
        void onEditClicked(TaskResponse task);
        void onDetailClicked(TaskResponse task);
    }

    private List<TaskResponse> tasks = new ArrayList<>();
    private final TaskActionListener listener;
    private final boolean isPartner;

    public TaskAdapter(TaskActionListener listener, boolean isPartner) {
        this.listener = listener;
        this.isPartner = isPartner;
    }

    public void setTasks(List<TaskResponse> tasks) {
        this.tasks = tasks != null ? tasks : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTaskBinding binding = ItemTaskBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new TaskViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(tasks.get(position));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ItemTaskBinding binding;

        TaskViewHolder(ItemTaskBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TaskResponse task) {
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(task.getStatus());
            String assignedTo = task.getAssignedTo() != null ? task.getAssignedTo() : "ME";
            int commentCount = task.getCommentCount();

            // Card tap → detail view; long press → edit
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onDetailClicked(task);
            });
            itemView.setOnLongClickListener(v -> {
                if (listener != null) listener.onEditClicked(task);
                return true;
            });

            // Title
            binding.tvTitle.setText(task.getTitle() != null ? task.getTitle() : "");
            if (isCompleted) {
                binding.tvTitle.setPaintFlags(binding.tvTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTitle.setAlpha(0.55f);
            } else {
                binding.tvTitle.setPaintFlags(binding.tvTitle.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                binding.tvTitle.setAlpha(1.0f);
            }

            // Card alpha for completed
            itemView.setAlpha(isCompleted ? 0.75f : 1.0f);

            // Checkbox
            binding.cbComplete.setOnCheckedChangeListener(null);
            binding.cbComplete.setChecked(isCompleted);
            binding.cbComplete.setOnCheckedChangeListener((btn, checked) -> {
                if (listener != null) listener.onStatusToggled(task, checked);
            });

            // Assignment pill badge
            // For a PARTNER user, "ME" tasks belong to the mother and "PARTNER" are theirs.
            // We flip the label so it reads correctly from the current user's perspective.
            int badgeBg, badgeFg, badgeIconRes;
            String badgeText;
            switch (assignedTo.toUpperCase()) {
                case "PARTNER":
                    if (isPartner) {
                        // This task is assigned to the PARTNER (current user) — show as "ME"
                        badgeBg  = itemView.getContext().getColor(R.color.primary_fixed);
                        badgeFg  = itemView.getContext().getColor(R.color.on_primary_fixed);
                        badgeIconRes = R.drawable.ic_person;
                        badgeText = "ME";
                    } else {
                        badgeBg  = itemView.getContext().getColor(R.color.secondary_fixed);
                        badgeFg  = itemView.getContext().getColor(R.color.on_secondary_fixed_variant);
                        badgeIconRes = R.drawable.ic_person;
                        badgeText = "PARTNER";
                    }
                    break;
                case "BOTH":
                    badgeBg  = itemView.getContext().getColor(R.color.primary_fixed);
                    badgeFg  = itemView.getContext().getColor(R.color.on_primary_fixed_variant);
                    badgeIconRes = R.drawable.ic_group;
                    badgeText = "BOTH";
                    break;
                default: // ME — mother's task
                    if (isPartner) {
                        // PARTNER sees a task assigned to "ME" (the mother) — show as "PARTNER"
                        badgeBg  = itemView.getContext().getColor(R.color.secondary_fixed);
                        badgeFg  = itemView.getContext().getColor(R.color.on_secondary_fixed_variant);
                        badgeIconRes = R.drawable.ic_person;
                        badgeText = "PARTNER";
                    } else {
                        badgeBg  = itemView.getContext().getColor(R.color.primary_fixed);
                        badgeFg  = itemView.getContext().getColor(R.color.on_primary_fixed);
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

            // Meta row
            if (isCompleted) {
                binding.ivDateIcon.setVisibility(View.GONE);
                binding.tvDueDate.setVisibility(View.GONE);
                binding.viewMetaDot.setVisibility(View.GONE);
                binding.ivSyncIcon.setVisibility(View.GONE);
                binding.tvSynced.setVisibility(View.GONE);
                binding.ivCompletedIcon.setVisibility(View.VISIBLE);
                binding.tvCompletedLabel.setVisibility(View.VISIBLE);
                binding.llMeta.setVisibility(View.VISIBLE);
            } else {
                binding.ivCompletedIcon.setVisibility(View.GONE);
                binding.tvCompletedLabel.setVisibility(View.GONE);

                boolean hasDate = task.getDueDate() != null && !task.getDueDate().isEmpty();
                boolean hasSynced = task.isSynced();

                if (hasDate) {
                    binding.tvDueDate.setText(formatDate(task.getDueDate()));
                    binding.ivDateIcon.setVisibility(View.VISIBLE);
                    binding.tvDueDate.setVisibility(View.VISIBLE);
                } else {
                    binding.ivDateIcon.setVisibility(View.GONE);
                    binding.tvDueDate.setVisibility(View.GONE);
                }

                binding.viewMetaDot.setVisibility(hasDate && hasSynced ? View.VISIBLE : View.GONE);

                if (hasSynced) {
                    binding.ivSyncIcon.setVisibility(View.VISIBLE);
                    binding.tvSynced.setVisibility(View.VISIBLE);
                } else {
                    binding.ivSyncIcon.setVisibility(View.GONE);
                    binding.tvSynced.setVisibility(View.GONE);
                }

                binding.llMeta.setVisibility(hasDate || hasSynced || commentCount > 0 ? View.VISIBLE : View.GONE);
            }

            // Nudge — show when this task belongs to the OTHER person (so current user can remind them)
            // MOTHER: nudge for PARTNER or BOTH tasks
            // PARTNER: nudge for ME (mother) or BOTH tasks
            boolean showNudge;
            if (isPartner) {
                showNudge = ("ME".equalsIgnoreCase(assignedTo) || "BOTH".equalsIgnoreCase(assignedTo)) && !isCompleted;
            } else {
                showNudge = ("PARTNER".equalsIgnoreCase(assignedTo) || "BOTH".equalsIgnoreCase(assignedTo)) && !isCompleted;
            }
            binding.btnNudge.setVisibility(showNudge ? View.VISIBLE : View.GONE);
            if (showNudge) {
                binding.btnNudge.setOnClickListener(v -> {
                    if (listener != null) listener.onNudgeClicked(task);
                });
            }

            // Comments
            if (commentCount > 0) {
                binding.tvComments.setText(String.valueOf(commentCount));
                binding.tvComments.setVisibility(View.VISIBLE);
            } else {
                binding.tvComments.setVisibility(View.GONE);
            }
        }

        private String formatDate(String isoDate) {
            if (isoDate != null && isoDate.length() >= 10) {
                return isoDate.substring(0, 10);
            }
            return isoDate != null ? isoDate : "";
        }
    }
}
