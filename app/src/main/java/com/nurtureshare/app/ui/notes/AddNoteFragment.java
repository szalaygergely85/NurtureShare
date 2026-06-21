package com.nurtureshare.app.ui.notes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.snackbar.Snackbar;
import com.nurtureshare.app.R;
import com.nurtureshare.app.databinding.FragmentAddNoteBinding;
import com.nurtureshare.app.network.model.request.CreateNoteRequest;
import com.nurtureshare.app.viewmodel.NoteViewModel;

public class AddNoteFragment extends Fragment {

    private FragmentAddNoteBinding binding;
    private NoteViewModel viewModel;
    private boolean isSaving = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddNoteBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        binding.btnBack.setOnClickListener(v ->
                Navigation.findNavController(requireView()).navigateUp()
        );

        binding.switchSharePartner.setOnCheckedChangeListener((buttonView, isChecked) ->
                binding.llSyncIndicator.setVisibility(isChecked ? View.VISIBLE : View.GONE)
        );

        binding.btnSaveNote.setOnClickListener(v -> saveNote());
    }

    private void saveNote() {
        if (isSaving) return;

        String title = binding.etNoteTitle.getText() != null
                ? binding.etNoteTitle.getText().toString().trim() : "";

        if (title.isEmpty()) {
            binding.etNoteTitle.setError(getString(R.string.error_note_title_empty));
            return;
        }
        binding.etNoteTitle.setError(null);

        String category = getSelectedCategory();
        boolean shareWithPartner = binding.switchSharePartner.isChecked();

        CreateNoteRequest request = new CreateNoteRequest(title, category, shareWithPartner);

        viewModel.createNoteAndObserve(request).observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Snackbar.make(requireView(), R.string.note_created_success, Snackbar.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                    break;
                case ERROR:
                    setLoading(false);
                    String msg = resource.message != null ? resource.message : getString(R.string.note_create_error);
                    Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private String getSelectedCategory() {
        int checkedId = binding.chipGroupCategory.getCheckedChipId();
        if (checkedId == R.id.chip_shopping) return "SHOPPING";
        if (checkedId == R.id.chip_questions) return "QUESTIONS";
        if (checkedId == R.id.chip_medical) return "MEDICAL";
        return "IDEAS";
    }

    private void setLoading(boolean loading) {
        isSaving = loading;
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.btnSaveNote.setEnabled(!loading);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
