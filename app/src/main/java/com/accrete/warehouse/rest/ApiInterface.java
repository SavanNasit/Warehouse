package com.accrete.warehouse.rest;

import com.accrete.warehouse.model.ApiResponse;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.GET;
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

    @GET("?urlq=service")
    Call<ApiResponse> getWarehouseList(@Query("version")
                                               String version,
                                       @Query("key")
                                               String key,
                                       @Query("task")
                                               String task,
                                       @Query("user_id")
                                               String userid,
                                       @Query("access_token")
                                               String accessToken);


    @GET("?urlq=service")
    Call<ApiResponse> getRunningOrderList(@Query("version")
                                                  String version,
                                          @Query("key")
                                                  String key,
                                          @Query("task")
                                                  String task,
                                          @Query("user_id")
                                                  String userid,
                                          @Query("access_token")
                                                  String accessToken,
                                          @Query("chkid")
                                                  String chkid);

    @GET("?urlq=service")
    Call<ApiResponse> executeSelectedItem(@Query("version")
                                                  String version,
                                          @Query("key")
                                                  String key,
                                          @Query("task")
                                                  String task,
                                          @Query("user_id")
                                                  String userid,
                                          @Query("access_token")
                                                  String accessToken,
                                          @Query("chkid")
                                                  String chkid,
                                          @Query("chkoid")
                                                  String chkoid,
                                          @Query("oiid")
                                                  String oiid,
                                          @Query("isid")
                                                  String isid);

    @GET("?urlq=service")
    Call<ApiResponse> createPackage(@Query("version")
                                            String version,
                                    @Query("key")
                                            String key,
                                    @Query("task")
                                            String task,
                                    @Query("user_id")
                                            String userid,
                                    @Query("access_token")
                                            String accessToken,
                                    @Query("email")
                                            String email,
                                    @Query("mobile")
                                            String mobile,
                                    @Query("shipping")
                                            String shippingAddress,
                                    @Query("billing")
                                            String billingAddress,
                                    @Query("products")
                                            JSONArray productList,
                                    @Query("chkid")
                                            String chkid,
                                    @Query("order")
                                            String orderId,
                                    @Query("type")
                                            String type,
                                    @Query("local")
                                            String local,
                                    @Query("invoice_date")
                                            String invoiceDate,
                                    @Query("invoice_no")
                                            String invoiceNumber,
                                    @Query("e_sugam")
                                            String eSugam);

    @GET("?urlq=service")
    Call<ApiResponse> getPackageDetails(@Query("version")
                                                String version,
                                        @Query("key")
                                                String key,
                                        @Query("task")
                                                String task,
                                        @Query("user_id")
                                                String userid,
                                        @Query("access_token")
                                                String accessToken,
                                        @Query("chkid")
                                                String chkid,
                                        @Query("type")
                                                String type,
                                        @Query("last_fetch")
                                                String lastFetch,
                                        @Query("traversal")
                                                String traversal);
    @GET("?urlq=service")
    Call<ApiResponse> getConsignmentLists(@Query("version")
                                                  String version,
                                          @Query("key")
                                                  String key,
                                          @Query("task")
                                                  String task,
                                          @Query("user_id")
                                                  String userid,
                                          @Query("access_token")
                                                  String accessToken,
                                          @Query("chkid")
                                                  String chkid,
                                          @Query("last_fetch")
                                                  String lastFetch,
                                          @Query("traversal")
                                                  String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getConsignmentDetails(@Query("version")
                                                  String version,
                                          @Query("key")
                                                  String key,
                                          @Query("task")
                                                  String task,
                                          @Query("user_id")
                                                  String userid,
                                          @Query("access_token")
                                                  String accessToken,
                                          @Query("chkid")
                                                  String chkid,
                                            @Query("iscid")
                                                    String iscid);
}