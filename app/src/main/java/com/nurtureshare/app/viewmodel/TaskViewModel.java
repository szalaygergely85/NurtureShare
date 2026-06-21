package com.nurtureshare.app.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nurtureshare.app.network.ApiClient;
import com.nurtureshare.app.network.model.request.CreateTaskCommentRequest;
import com.nurtureshare.app.network.model.request.CreateTaskRequest;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.network.model.response.TaskCommentResponse;
import com.nurtureshare.app.network.model.response.TaskProgressResponse;
import com.nurtureshare.app.network.model.response.TaskResponse;
import com.nurtureshare.app.repository.TaskRepository;
import com.nurtureshare.app.util.Resource;

import java.util.List;

public class TaskViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;

    private MutableLiveData<Resource<List<TaskResponse>>> tasks;
    private MutableLiveData<Resource<TaskProgressResponse>> progress;
    private MutableLiveData<Resource<TaskResponse>> createResult;
    private MutableLiveData<Resource<TaskResponse>> updateResult;
    private MutableLiveData<Resource<Void>> deleteResult;
    private MutableLiveData<Resource<Void>> nudgeResult;
    private MutableLiveData<Resource<List<TaskCommentResponse>>> comments;
    private MutableLiveData<Resource<TaskCommentResponse>> addCommentResult;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        taskRepository = new TaskRepository(
                ApiClient.getInstance(application).getApiService()
        );
        tasks = new MutableLiveData<>();
        progress = new MutableLiveData<>();
        createResult = new MutableLiveData<>();
        updateResult = new MutableLiveData<>();
        deleteResult = new MutableLiveData<>();
        nudgeResult = new MutableLiveData<>();
        comments = new MutableLiveData<>();
        addCommentResult = new MutableLiveData<>();
    }

    public LiveData<Resource<List<TaskResponse>>> getTasks() { return tasks; }
    public LiveData<Resource<TaskProgressResponse>> getProgress() { return progress; }
    public LiveData<Resource<TaskResponse>> getCreateResult() { return createResult; }
    public LiveData<Resource<TaskResponse>> getUpdateResult() { return updateResult; }
    public LiveData<Resource<Void>> getDeleteResult() { return deleteResult; }
    public LiveData<Resource<Void>> getNudgeResult() { return nudgeResult; }
    public LiveData<Resource<List<TaskCommentResponse>>> getComments() { return comments; }
    public LiveData<Resource<TaskCommentResponse>> getAddCommentResult() { return addCommentResult; }

    public void loadTasks(String filter) {
        tasks = taskRepository.getTasks(filter);
    }

    public void createTask(CreateTaskRequest request) {
        createResult = taskRepository.createTask(request);
    }

    public void updateTask(String id, UpdateTaskRequest request) {
        updateResult = taskRepository.updateTask(id, request);
    }

    public void deleteTask(String id) {
        deleteResult = taskRepository.deleteTask(id);
    }

    public void nudgePartner(String id) {
        nudgeResult = taskRepository.nudgePartner(id);
    }

    public void loadProgress() {
        progress = taskRepository.getProgress();
    }

    public void loadComments(String taskId) {
        comments = taskRepository.getComments(taskId);
    }

    public void addComment(String taskId, String content) {
        CreateTaskCommentRequest request = new CreateTaskCommentRequest(content);
        addCommentResult = taskRepository.addComment(taskId, request);
    }

    public LiveData<Resource<List<TaskCommentResponse>>> loadCommentsAndObserve(String taskId) {
        comments = taskRepository.getComments(taskId);
        return comments;
    }

    public LiveData<Resource<TaskCommentResponse>> addCommentAndObserve(String taskId, String content) {
        CreateTaskCommentRequest request = new CreateTaskCommentRequest(content);
        addCommentResult = taskRepository.addComment(taskId, request);
        return addCommentResult;
    }

    // Methods that return LiveData directly for immediate observation
    public LiveData<Resource<List<TaskResponse>>> loadTasksAndObserve(String filter) {
        tasks = taskRepository.getTasks(filter);
        return tasks;
    }

    public LiveData<Resource<TaskProgressResponse>> loadProgressAndObserve() {
        progress = taskRepository.getProgress();
        return progress;
    }

    public LiveData<Resource<TaskResponse>> createTaskAndObserve(CreateTaskRequest request) {
        createResult = taskRepository.createTask(request);
        return createResult;
    }

    public LiveData<Resource<TaskResponse>> updateTaskAndObserve(String id, UpdateTaskRequest request) {
        updateResult = taskRepository.updateTask(id, request);
        return updateResult;
    }

    public LiveData<Resource<Void>> nudgePartnerAndObserve(String id) {
        nudgeResult = taskRepository.nudgePartner(id);
        return nudgeResult;
    }

    public LiveData<Resource<Void>> deleteTaskAndObserve(String id) {
        deleteResult = taskRepository.deleteTask(id);
        return deleteResult;
    }
}
