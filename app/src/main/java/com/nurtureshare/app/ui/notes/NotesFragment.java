package com.nurtureshare.app.ui.notes;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentNotesBinding;
import com.nurtureshare.app.network.model.request.CreateNoteItemRequest;
import com.nurtureshare.app.network.model.request.UpdateNoteItemRequest;
import com.nurtureshare.app.network.model.response.NoteItemResponse;
import com.nurtureshare.app.network.model.response.NoteResponse;
import com.nurtureshare.app.viewmodel.NoteViewModel;

public class NotesFragment extends Fragment {

    private FragmentNotesBinding binding;
    private NoteViewModel viewModel;
    private NoteAdapter noteAdapter;
    private TabLayout.OnTabSelectedListener tabListener;

    private String currentCategory = "IDEAS";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        setupRecyclerView();
        setupTabs();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNotes();
    }

    private void setupRecyclerView() {
        noteAdapter = new NoteAdapter(new NoteAdapter.NoteActionListener() {
            @Override
            public void onNoteLongPressed(NoteResponse note) {
                showDeleteNoteDialog(note);
            }

            @Override
            public void onNoteItemChecked(String noteId, NoteItemResponse item, boolean checked) {
                UpdateNoteItemRequest request = new UpdateNoteItemRequest(
                        item.getContent(),
                        checked,
                        item.isUrgent()
                );
                viewModel.updateNoteItemAndObserve(noteId, item.getId(), request)
                        .observe(getViewLifecycleOwner(), resource -> {
                            if (resource.status == com.nurtureshare.app.util.Resource.Status.SUCCESS) {
                                loadNotes();
                            }
                        });
            }

            @Override
            public void onAddItemClicked(NoteResponse note) {
                showAddItemDialog(note);
            }
        });

        binding.rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvNotes.setAdapter(noteAdapter);
    }

    private void setupTabs() {
        tabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentCategory = "IDEAS"; break;
                    case 1: currentCategory = "SHOPPING"; break;
                    case 2: currentCategory = "QUESTIONS"; break;
                    case 3: currentCategory = "MEDICAL"; break;
                    default: currentCategory = null;
                }
                loadNotes();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                loadNotes();
            }
        };
        binding.tabLayout.addOnTabSelectedListener(tabListener);
    }

    private void loadNotes() {
        viewModel.loadNotesAndObserve(currentCategory).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    java.util.List<NoteResponse> notes = resource.data != null
                            ? resource.data : new java.util.ArrayList<>();
                    noteAdapter.setNotes(notes);
                    boolean empty = notes.isEmpty();
                    binding.rvNotes.setVisibility(empty ? View.GONE : View.VISIBLE);
                    binding.tvEmptyNotes.setVisibility(empty ? View.VISIBLE : View.GONE);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    String msg = resource.message != null ? resource.message : getString(R.string.notes_error);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG)
                            .setAction(R.string.retry, v -> loadNotes())
                            .show();
                    break;
            }
        });
    }

    private void showAddItemDialog(NoteResponse note) {
        EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setHint(getString(R.string.hint_note_item));
        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(padding, padding, padding, padding);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.add_note_item)
                .setView(input)
                .setPositiveButton(R.string.btn_save_task, (dialog, which) -> {
                    String content = input.getText().toString().trim();
                    if (content.isEmpty()) return;
                    int orderIndex = note.getItems() != null ? note.getItems().size() : 0;
                    CreateNoteItemRequest request = new CreateNoteItemRequest(
                            content, "CHECKLIST", false, orderIndex);
                    viewModel.addNoteItemAndObserve(note.getId(), request)
                            .observe(getViewLifecycleOwner(), resource -> {
                                if (resource.status == com.nurtureshare.app.util.Resource.Status.SUCCESS) {
                                    Snackbar.make(requireView(), R.string.note_item_added, Snackbar.LENGTH_SHORT).show();
                                    loadNotes();
                                } else if (resource.status == com.nurtureshare.app.util.Resource.Status.ERROR) {
                                    String msg = resource.message != null ? resource.message : getString(R.string.notes_add_item_error);
                                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void showDeleteNoteDialog(NoteResponse note) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.note_delete_confirm_title)
                .setMessage(R.string.note_delete_confirm_message)
                .setPositiveButton(R.string.btn_delete, (dialog, which) -> deleteNote(note))
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void deleteNote(NoteResponse note) {
        viewModel.deleteNoteAndObserve(note.getId()).observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == com.nurtureshare.app.util.Resource.Status.SUCCESS) {
                Snackbar.make(requireView(), R.string.note_deleted, Snackbar.LENGTH_SHORT).show();
                loadNotes();
            } else if (resource.status == com.nurtureshare.app.util.Resource.Status.ERROR) {
                String msg = resource.message != null ? resource.message : getString(R.string.notes_delete_error);
                Snackbar.make(requireView(), msg, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (binding != null && tabListener != null) {
            binding.tabLayout.removeOnTabSelectedListener(tabListener);
        }
        super.onDestroyView();
        binding = null;
    }
}
