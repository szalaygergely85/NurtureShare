package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.CreateNoteItemRequest;
import com.nurtureshare.app.network.model.request.CreateNoteRequest;
import com.nurtureshare.app.network.model.request.UpdateNoteItemRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.NoteItemResponse;
import com.nurtureshare.app.network.model.response.NoteResponse;
import com.nurtureshare.app.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NoteRepository {

    private final ApiService apiService;

    public NoteRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<List<NoteResponse>>> getNotes(String category) {
        MutableLiveData<Resource<List<NoteResponse>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getNotes(category).enqueue(new Callback<ApiResponse<List<NoteResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<NoteResponse>>> call,
                                   Response<ApiResponse<List<NoteResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<NoteResponse>> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load notes.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<NoteResponse>>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<NoteResponse>> createNote(CreateNoteRequest request) {
        MutableLiveData<Resource<NoteResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.createNote(request).enqueue(new Callback<ApiResponse<NoteResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<NoteResponse>> call,
                                   Response<ApiResponse<NoteResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NoteResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to create note.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NoteResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<Void>> deleteNote(String id) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.deleteNote(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(null));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to delete note.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<NoteItemResponse>> addNoteItem(String noteId,
                                                                    CreateNoteItemRequest request) {
        MutableLiveData<Resource<NoteItemResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.addNoteItem(noteId, request).enqueue(new Callback<ApiResponse<NoteItemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<NoteItemResponse>> call,
                                   Response<ApiResponse<NoteItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<NoteItemResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to add note item.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<NoteItemResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<NoteItemResponse>> updateNoteItem(String noteId,
                                                                       String itemId,
                                                                       UpdateNoteItemRequest request) {
        MutableLiveData<Resource<NoteItemResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.updateNoteItem(noteId, itemId, request).enqueue(
                new Callback<ApiResponse<NoteItemResponse>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<NoteItemResponse>> call,
                                           Response<ApiResponse<NoteItemResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ApiResponse<NoteItemResponse> body = response.body();
                            if (body.isSuccess()) {
                                liveData.setValue(Resource.success(body.getData()));
                            } else {
                                liveData.setValue(Resource.error(body.getMessage(), null));
                            }
                        } else {
                            liveData.setValue(Resource.error("Failed to update note item.", null));
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<NoteItemResponse>> call, Throwable t) {
                        liveData.setValue(Resource.error(t.getMessage(), null));
                    }
                });

        return liveData;
    }

    public MutableLiveData<Resource<Void>> deleteNoteItem(String noteId, String itemId) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.deleteNoteItem(noteId, itemId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call,
                                   Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(null));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to delete note item.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
