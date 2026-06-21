package com.nurtureshare.app.ui.tasks;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nurtureshare.app.databinding.ItemCommentBinding;
import com.nurtureshare.app.network.model.response.TaskCommentResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<TaskCommentResponse> comments = new ArrayList<>();

    public void setComments(List<TaskCommentResponse> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCommentBinding binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new CommentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {

        private final ItemCommentBinding binding;

        CommentViewHolder(ItemCommentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(TaskCommentResponse comment) {
            String authorName = comment.getAuthor() != null && comment.getAuthor().getName() != null
                    ? comment.getAuthor().getName() : "?";

            binding.tvAuthorName.setText(authorName);
            binding.tvAvatarInitials.setText(getInitials(authorName));
            binding.tvCommentContent.setText(comment.getContent() != null ? comment.getContent() : "");
            binding.tvCommentTime.setText(formatRelativeTime(comment.getCreatedAt()));
        }

        private String getInitials(String name) {
            if (name == null || name.isEmpty()) return "?";
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) {
                return String.valueOf(parts[0].charAt(0)).toUpperCase()
                        + String.valueOf(parts[1].charAt(0)).toUpperCase();
            }
            return String.valueOf(parts[0].charAt(0)).toUpperCase();
        }

        private String formatRelativeTime(String isoDate) {
            if (isoDate == null || isoDate.isEmpty()) return "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = sdf.parse(isoDate);
                if (date == null) return "";
                long diff = System.currentTimeMillis() - date.getTime();
                long minutes = diff / 60_000;
                if (minutes < 1) return "just now";
                if (minutes < 60) return minutes + "m ago";
                long hours = minutes / 60;
                if (hours < 24) return hours + "h ago";
                long days = hours / 24;
                if (days < 7) return days + "d ago";
                return (days / 7) + "w ago";
            } catch (ParseException e) {
                return "";
            }
        }
    }
}
