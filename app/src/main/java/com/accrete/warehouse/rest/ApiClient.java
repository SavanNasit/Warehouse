package com.accrete.warehouse.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //public static String BASE_URL = "";
    public static String BASE_URL = "";
    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit retrofit;
    private static OkHttpClient.Builder httpClient =
            new OkHttpClient.Builder();


    // No need to instantiate this class.
    private ApiClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .build();
        }
        return retrofit;
    }

    public static void changeApiBaseUrl(String newApiBaseUrl) {
        BASE_URL = newApiBaseUrl;

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(BASE_URL)
                .build();
    }



}

