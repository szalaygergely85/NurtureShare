package com.nurtureshare.app.repository;

import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiService;
import com.nurtureshare.app.network.model.request.CreateTaskCommentRequest;
import com.nurtureshare.app.network.model.request.CreateTaskRequest;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.TaskCommentResponse;
import com.nurtureshare.app.network.model.response.TaskProgressResponse;
import com.nurtureshare.app.network.model.response.TaskResponse;
import com.nurtureshare.app.util.Resource;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskRepository {

    private final ApiService apiService;

    public TaskRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public MutableLiveData<Resource<List<TaskResponse>>> getTasks(String filter) {
        MutableLiveData<Resource<List<TaskResponse>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getTasks(filter).enqueue(new Callback<ApiResponse<List<TaskResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TaskResponse>>> call,
                                   Response<ApiResponse<List<TaskResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TaskResponse>> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load tasks.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TaskResponse>>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<TaskResponse>> createTask(CreateTaskRequest request) {
        MutableLiveData<Resource<TaskResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.createTask(request).enqueue(new Callback<ApiResponse<TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TaskResponse>> call,
                                   Response<ApiResponse<TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TaskResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to create task.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TaskResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<TaskResponse>> updateTask(String id, UpdateTaskRequest request) {
        MutableLiveData<Resource<TaskResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.updateTask(id, request).enqueue(new Callback<ApiResponse<TaskResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TaskResponse>> call,
                                   Response<ApiResponse<TaskResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TaskResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to update task.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TaskResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<Void>> deleteTask(String id) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.deleteTask(id).enqueue(new Callback<ApiResponse<Void>>() {
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
                    liveData.setValue(Resource.error("Failed to delete task.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<Void>> nudgePartner(String id) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.nudgePartner(id).enqueue(new Callback<ApiResponse<Void>>() {
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
                    liveData.setValue(Resource.error("Failed to send nudge.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<TaskProgressResponse>> getProgress() {
        MutableLiveData<Resource<TaskProgressResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getTaskProgress().enqueue(new Callback<ApiResponse<TaskProgressResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TaskProgressResponse>> call,
                                   Response<ApiResponse<TaskProgressResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TaskProgressResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load progress.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TaskProgressResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<List<TaskCommentResponse>>> getComments(String taskId) {
        MutableLiveData<Resource<List<TaskCommentResponse>>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.getComments(taskId).enqueue(new Callback<ApiResponse<List<TaskCommentResponse>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TaskCommentResponse>>> call,
                                   Response<ApiResponse<List<TaskCommentResponse>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<TaskCommentResponse>> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to load comments.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TaskCommentResponse>>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }

    public MutableLiveData<Resource<TaskCommentResponse>> addComment(String taskId,
                                                                      CreateTaskCommentRequest request) {
        MutableLiveData<Resource<TaskCommentResponse>> liveData = new MutableLiveData<>();
        liveData.setValue(Resource.loading(null));

        apiService.addComment(taskId, request).enqueue(new Callback<ApiResponse<TaskCommentResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<TaskCommentResponse>> call,
                                   Response<ApiResponse<TaskCommentResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<TaskCommentResponse> body = response.body();
                    if (body.isSuccess()) {
                        liveData.setValue(Resource.success(body.getData()));
                    } else {
                        liveData.setValue(Resource.error(body.getMessage(), null));
                    }
                } else {
                    liveData.setValue(Resource.error("Failed to add comment.", null));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<TaskCommentResponse>> call, Throwable t) {
                liveData.setValue(Resource.error(t.getMessage(), null));
            }
        });

        return liveData;
    }
}
