package com.nurtureshare.app.network;

import com.nurtureshare.app.network.model.request.CreateNoteItemRequest;
import com.nurtureshare.app.network.model.request.PregnancySetupRequest;
import com.nurtureshare.app.network.model.request.CreateNoteRequest;
import com.nurtureshare.app.network.model.request.CreateTaskCommentRequest;
import com.nurtureshare.app.network.model.request.CreateTaskRequest;
import com.nurtureshare.app.network.model.request.LoginRequest;
import com.nurtureshare.app.network.model.request.PairRequest;
import com.nurtureshare.app.network.model.request.RegisterRequest;
import com.nurtureshare.app.network.model.request.UpdateBabyInfoRequest;
import com.nurtureshare.app.network.model.request.UpdateNoteItemRequest;
import com.nurtureshare.app.network.model.request.UpdateSettingsRequest;
import com.nurtureshare.app.network.model.request.UpdateTaskRequest;
import com.nurtureshare.app.network.model.response.ApiResponse;
import com.nurtureshare.app.network.model.response.AuthResponse;
import com.nurtureshare.app.network.model.response.CoupleStatusResponse;
import com.nurtureshare.app.network.model.response.NoteItemResponse;
import com.nurtureshare.app.network.model.response.NoteResponse;
import com.nurtureshare.app.network.model.response.SettingsResponse;
import com.nurtureshare.app.network.model.response.TaskCommentResponse;
import com.nurtureshare.app.network.model.response.TaskProgressResponse;
import com.nurtureshare.app.network.model.response.TaskResponse;
import com.nurtureshare.app.network.model.response.TimelineResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("api/auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @GET("api/timeline")
    Call<ApiResponse<TimelineResponse>> getTimeline();

    @GET("api/tasks")
    Call<ApiResponse<List<TaskResponse>>> getTasks(@Query("filter") String filter);

    @POST("api/tasks")
    Call<ApiResponse<TaskResponse>> createTask(@Body CreateTaskRequest request);

    @PUT("api/tasks/{id}")
    Call<ApiResponse<TaskResponse>> updateTask(@Path("id") String id,
                                               @Body UpdateTaskRequest request);

    @DELETE("api/tasks/{id}")
    Call<ApiResponse<Void>> deleteTask(@Path("id") String id);

    @POST("api/tasks/{id}/nudge")
    Call<ApiResponse<Void>> nudgePartner(@Path("id") String id);

    @GET("api/tasks/progress")
    Call<ApiResponse<TaskProgressResponse>> getTaskProgress();

    @GET("api/tasks/{id}/comments")
    Call<ApiResponse<List<TaskCommentResponse>>> getComments(@Path("id") String id);

    @POST("api/tasks/{id}/comments")
    Call<ApiResponse<TaskCommentResponse>> addComment(@Path("id") String id,
                                                      @Body CreateTaskCommentRequest request);

    @GET("api/notes")
    Call<ApiResponse<List<NoteResponse>>> getNotes(@Query("category") String category);

    @POST("api/notes")
    Call<ApiResponse<NoteResponse>> createNote(@Body CreateNoteRequest request);

    @DELETE("api/notes/{id}")
    Call<ApiResponse<Void>> deleteNote(@Path("id") String id);

    @POST("api/notes/{id}/items")
    Call<ApiResponse<NoteItemResponse>> addNoteItem(@Path("id") String id,
                                                    @Body CreateNoteItemRequest request);

    @PUT("api/notes/{id}/items/{itemId}")
    Call<ApiResponse<NoteItemResponse>> updateNoteItem(@Path("id") String id,
                                                       @Path("itemId") String itemId,
                                                       @Body UpdateNoteItemRequest request);

    @DELETE("api/notes/{id}/items/{itemId}")
    Call<ApiResponse<Void>> deleteNoteItem(@Path("id") String id,
                                           @Path("itemId") String itemId);

    @GET("api/couple/status")
    Call<ApiResponse<CoupleStatusResponse>> getCoupleStatus();

    @POST("api/couple/pair")
    Call<ApiResponse<CoupleStatusResponse>> pairWithPartner(@Body PairRequest request);

    @GET("api/couple/pairing-code")
    Call<ApiResponse<String>> getPairingCode();

    @PUT("api/pregnancy/setup")
    Call<ApiResponse<TimelineResponse>> setupPregnancy(@Body PregnancySetupRequest request);

    @PUT("api/pregnancy/baby-info")
    Call<ApiResponse<TimelineResponse>> updateBabyInfo(@Body UpdateBabyInfoRequest request);

    @GET("api/settings")
    Call<ApiResponse<SettingsResponse>> getSettings();

    @PUT("api/settings")
    Call<ApiResponse<SettingsResponse>> updateSettings(@Body UpdateSettingsRequest request);
}
