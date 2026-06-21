package com.nurtureshare.app.ui.notes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.ItemNoteBinding;
import com.nurtureshare.app.network.model.response.NoteItemResponse;
import com.nurtureshare.app.network.model.response.NoteResponse;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface NoteActionListener {
        void onNoteLongPressed(NoteResponse note);
        void onNoteItemChecked(String noteId, NoteItemResponse item, boolean checked);
        void onAddItemClicked(NoteResponse note);
    }

    private List<NoteResponse> notes = new ArrayList<>();
    private final NoteActionListener listener;

    public NoteAdapter(NoteActionListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<NoteResponse> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNoteBinding binding = ItemNoteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new NoteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private final ItemNoteBinding binding;
        private final NoteItemAdapter itemAdapter;
        private NoteResponse boundNote;

        NoteViewHolder(ItemNoteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Create once; reuse across bind() calls to avoid flicker
            itemAdapter = new NoteItemAdapter((item, checked) -> {
                if (listener != null && boundNote != null) {
                    listener.onNoteItemChecked(boundNote.getId(), item, checked);
                }
            });
            binding.rvNoteItems.setLayoutManager(
                    new LinearLayoutManager(binding.getRoot().getContext()));
            binding.rvNoteItems.setAdapter(itemAdapter);
            binding.rvNoteItems.setNestedScrollingEnabled(false);
        }

        void bind(NoteResponse note) {
            boundNote = note;

            binding.tvNoteTitle.setText(note.getTitle() != null ? note.getTitle() : "");

            // Category color bar
            int colorResId;
            String category = note.getCategory();
            if ("SHOPPING".equalsIgnoreCase(category)) {
                colorResId = R.color.secondary;
            } else if ("QUESTIONS".equalsIgnoreCase(category)) {
                colorResId = R.color.tertiary;
            } else {
                colorResId = R.color.primary;
            }
            binding.colorBar.setBackgroundResource(colorResId);

            // Synced chip
            binding.chipSynced.setVisibility(note.isSyncedWithPartner() ? View.VISIBLE : View.GONE);

            // Note items
            List<NoteItemResponse> items = note.getItems();
            itemAdapter.setItems(items != null ? items : new ArrayList<>());

            // Add item button
            binding.btnAddItem.setOnClickListener(v -> {
                if (listener != null) listener.onAddItemClicked(note);
            });

            // Long press to delete
            binding.getRoot().setOnLongClickListener(v -> {
                if (listener != null) listener.onNoteLongPressed(note);
                return true;
            });
        }
    }
}
