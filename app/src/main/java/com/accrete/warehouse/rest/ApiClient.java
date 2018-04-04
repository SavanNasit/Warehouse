package com.accrete.warehouse.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //public static String BASE_URL = "";
    public static String BASE_URL = "";
    static Gson gson = new GsonBuilder()
            .setLenient()
            .create();
    private static Retrofit retrofit;


    // No need to instantiate this class.
    private ApiClient() {
    }

    public static Retrofit getClient() {
      //  if (retrofit == null) {
     /*       HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(50000, TimeUnit.MILLISECONDS)
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .build();*/


            retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .baseUrl(BASE_URL)
                  //  .client(client)
                    .build();
       // }
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

