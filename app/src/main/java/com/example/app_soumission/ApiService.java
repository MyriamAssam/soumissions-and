package com.example.app_soumission;

import com.example.app_soumission.LoginRequest;
import com.example.app_soumission.UserResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("/users/login")
    Call<UserResponse> login(@Body LoginRequest loginRequest);

    @POST("/users/register")
    Call<UserResponse> register(@Body RegisterRequest registerRequest);


    @GET("/soumissions/client/{id}")
    Call<SoumissionListResponse> getClientSoumissions(@Path("id") String clientId, @Header("Authorization") String token);
    @GET("/soumissions")
    Call<SoumissionListResponse> getAllSoumissions(@Header("Authorization") String token);
    @DELETE("/soumissions/{id}")
    Call<Void> deleteSoumission(@Path("id") String soumissionId, @Header("Authorization") String token);
    @PATCH("soumissions/{oId}/note")
    Call<Void> addNote(@Path("oId") String oId, @Body Map<String,Object> data, @Header("Authorization") String token);


    @PUT("/soumissions/{id}")
    Call<SoumissionResponse> updateSoumission(
            @Path("id") String soumissionId,
            @Body Map<String, Object> updates,
            @Header("Authorization") String token
    );
    @GET("/soumissions/{oId}/notes")
    Call<NotesListResponse> getNotes(@Path("oId") String oId, @Query("role") String role, @Header("Authorization") String token);





    @PATCH("/soumissions/{id}/note/{noteId}")
    Call<Void> updateNote(
            @Path("id") String soumissionId,
            @Path("noteId") String noteId,
            @Body Map<String, Object> updates,
            @Header("Authorization") String token
    );


    @DELETE("/soumissions/{id}/notes/{noteId}")
    Call<Void> deleteNote(
            @Path("id") String soumissionId,
            @Path("noteId") String noteId,
            @Query("role") String role, // ⬅️ ajouter ceci
            @Header("Authorization") String token
    );


    @GET("/soumissions/employe/{id}")
    Call<SoumissionListResponse> getEmployeSoumissions(@Path("id") String employeId, @Header("Authorization") String token);
    @POST("/soumissions")
    Call<SoumissionResponse> addSoumission(
            @Body SoumissionRequest request,
            @Header("Authorization") String token
    );

}

