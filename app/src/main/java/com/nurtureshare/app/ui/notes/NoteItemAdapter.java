package com.nurtureshare.app.ui.notes;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nurtureshare.app.databinding.ItemNoteItemBinding;
import com.nurtureshare.app.network.model.response.NoteItemResponse;

import java.util.ArrayList;
import java.util.List;

public class NoteItemAdapter extends RecyclerView.Adapter<NoteItemAdapter.NoteItemViewHolder> {

    public interface NoteItemActionListener {
        void onCheckedChanged(NoteItemResponse item, boolean checked);
    }

    private List<NoteItemResponse> items = new ArrayList<>();
    private final NoteItemActionListener listener;

    public NoteItemAdapter(NoteItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<NoteItemResponse> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteItemBinding binding = ItemNoteItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new NoteItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteItemViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class NoteItemViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteItemBinding binding;

        NoteItemViewHolder(ItemNoteItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NoteItemResponse item) {
            binding.tvItemContent.setText(item.getContent() != null ? item.getContent() : "");

            boolean isChecklist = "CHECKLIST".equalsIgnoreCase(item.getItemType());

            if (isChecklist) {
                binding.cbItem.setVisibility(View.VISIBLE);
                binding.ivBullet.setVisibility(View.GONE);

                binding.cbItem.setOnCheckedChangeListener(null);
                binding.cbItem.setChecked(item.isChecked());
                binding.cbItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (listener != null) {
                        listener.onCheckedChanged(item, isChecked);
                    }
                });
            } else {
                binding.cbItem.setVisibility(View.GONE);
                binding.ivBullet.setVisibility(View.VISIBLE);
            }

            // Strike-through if checked
            if (item.isChecked()) {
                binding.tvItemContent.setPaintFlags(
                        binding.tvItemContent.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                );
                binding.tvItemContent.setAlpha(0.6f);
            } else {
                binding.tvItemContent.setPaintFlags(
                        binding.tvItemContent.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG
                );
                binding.tvItemContent.setAlpha(1.0f);
            }

            // Urgent chip
            if (item.isUrgent()) {
                binding.chipUrgent.setVisibility(View.VISIBLE);
            } else {
                binding.chipUrgent.setVisibility(View.GONE);
            }
        }
    }
}
