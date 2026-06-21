package com.nurtureshare.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.request.CreateNoteItemRequest;
import com.nurtureshare.app.network.model.request.CreateNoteRequest;
import com.nurtureshare.app.network.model.request.UpdateNoteItemRequest;
import com.nurtureshare.app.network.model.response.NoteItemResponse;
import com.nurtureshare.app.network.model.response.NoteResponse;
import com.nurtureshare.app.repository.NoteRepository;
import com.nurtureshare.app.util.Resource;

import java.util.List;

public class NoteViewModel extends AndroidViewModel {

    private final NoteRepository noteRepository;

    private MutableLiveData<Resource<List<NoteResponse>>> notes;
    private MutableLiveData<Resource<NoteResponse>> createResult;
    private MutableLiveData<Resource<Void>> deleteResult;
    private MutableLiveData<Resource<NoteItemResponse>> addItemResult;
    private MutableLiveData<Resource<NoteItemResponse>> updateItemResult;
    private MutableLiveData<Resource<Void>> deleteItemResult;

    public NoteViewModel(@NonNull Application application) {
        super(application);
        noteRepository = new NoteRepository(
                ApiClient.getInstance(application).getApiService()
        );
        notes = new MutableLiveData<>();
        createResult = new MutableLiveData<>();
        deleteResult = new MutableLiveData<>();
        addItemResult = new MutableLiveData<>();
        updateItemResult = new MutableLiveData<>();
        deleteItemResult = new MutableLiveData<>();
    }

    public LiveData<Resource<List<NoteResponse>>> getNotes() { return notes; }
    public LiveData<Resource<NoteResponse>> getCreateResult() { return createResult; }
    public LiveData<Resource<Void>> getDeleteResult() { return deleteResult; }
    public LiveData<Resource<NoteItemResponse>> getAddItemResult() { return addItemResult; }
    public LiveData<Resource<NoteItemResponse>> getUpdateItemResult() { return updateItemResult; }
    public LiveData<Resource<Void>> getDeleteItemResult() { return deleteItemResult; }

    public LiveData<Resource<List<NoteResponse>>> loadNotesAndObserve(String category) {
        notes = noteRepository.getNotes(category);
        return notes;
    }

    public void loadNotes(String category) {
        notes = noteRepository.getNotes(category);
    }

    public LiveData<Resource<NoteResponse>> createNoteAndObserve(CreateNoteRequest request) {
        createResult = noteRepository.createNote(request);
        return createResult;
    }

    public void createNote(CreateNoteRequest request) {
        createResult = noteRepository.createNote(request);
    }

    public LiveData<Resource<Void>> deleteNoteAndObserve(String id) {
        deleteResult = noteRepository.deleteNote(id);
        return deleteResult;
    }

    public void deleteNote(String id) {
        deleteResult = noteRepository.deleteNote(id);
    }

    public LiveData<Resource<NoteItemResponse>> addNoteItemAndObserve(String noteId,
                                                                       CreateNoteItemRequest request) {
        addItemResult = noteRepository.addNoteItem(noteId, request);
        return addItemResult;
    }

    public void addNoteItem(String noteId, CreateNoteItemRequest request) {
        addItemResult = noteRepository.addNoteItem(noteId, request);
    }

    public LiveData<Resource<NoteItemResponse>> updateNoteItemAndObserve(String noteId,
                                                                          String itemId,
                                                                          UpdateNoteItemRequest request) {
        updateItemResult = noteRepository.updateNoteItem(noteId, itemId, request);
        return updateItemResult;
    }

    public void updateNoteItem(String noteId, String itemId, UpdateNoteItemRequest request) {
        updateItemResult = noteRepository.updateNoteItem(noteId, itemId, request);
    }

    public LiveData<Resource<Void>> deleteNoteItemAndObserve(String noteId, String itemId) {
        deleteItemResult = noteRepository.deleteNoteItem(noteId, itemId);
        return deleteItemResult;
    }

    public void deleteNoteItem(String noteId, String itemId) {
        deleteItemResult = noteRepository.deleteNoteItem(noteId, itemId);
    }
}
