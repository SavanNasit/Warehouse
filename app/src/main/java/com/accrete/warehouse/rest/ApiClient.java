package com.accrete.warehouse.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //public static String BASE_URL = "";
    public static String BASE_URL = "";
    public static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .readTimeout(80, TimeUnit.SECONDS)
            .connectTimeout(80, TimeUnit.SECONDS)
            .build();

    public static OkHttpClient okHttpClientImages = new OkHttpClient.Builder()
            .readTimeout(200, TimeUnit.SECONDS)
            .connectTimeout(200, TimeUnit.SECONDS)
            .build();

    public static Retrofit getClientImages() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .client(okHttpClientImages)
                    .build();
        }
        return retrofit;
    }

    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit retrofit;


    // No need to instantiate this class.
    private ApiClient() {
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
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

