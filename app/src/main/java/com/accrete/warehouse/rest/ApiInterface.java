package com.accrete.warehouse.rest;

import com.accrete.warehouse.model.ApiResponse;

import org.json.JSONArray;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {


    @GET("?urlq=service")
    Call<ApiResponse> doGetDomain(
            @Query("version") String version,
            @Query("key") String key,
            @Query("task") String task
    );

    @GET("?urlq=service")
    Call<ApiResponse> doValidateLogin(
            @Query("version") String version,
            @Query("key") String key,
            @Query("task") String task,
            @Query("email") String email,
            @Query("password") String password
    );


    @GET("?urlq=service")
    Call<ApiResponse> isAccessTokenValid(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken
    );



}