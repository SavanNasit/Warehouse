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
            @Query("password") String password,
            @Query("app_flag") String flag
    );

    @GET("?urlq=service")
    Call<ApiResponse> getDataPerItemScan(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("vno")
                    String vno
    );

    @GET("?urlq=service")
    Call<ApiResponse> resetPassword(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("token")
                    String token,
            @Query("email")
                    String email,
            @Query("password")
                    String password,
            @Query("repassword")
                    String repassword);

    @GET("?urlq=service")
    Call<ApiResponse> verifyEmail(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("email")
                    String mobile
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
                                                  String chkid,
                                          @Query("last_fetch")
                                                  String lastFetch,
                                          @Query("traversal")
                                                  String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getPackageSelectionList(@Query("version")
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
    Call<ApiResponse> getGatepassList(@Query("version")
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
                                                  String chkoid);

    @GET("?urlq=service")
    Call<ApiResponse> executeRequestSelectedItem(@Query("version")
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
                                                 @Query("stockreqid")
                                                         String chkoid);

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

    @Multipart
    @POST("?urlq=service")
    Call<ApiResponse> createPackageMultipart(
            @Query("version")
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
                    String eSugam,
            @Part MultipartBody.Part[] image,
            @Query("order")
                    String order,
            @Query("distance")
                    String distance,
            @Query("vehicle")
                    String vehicle,
            @Query("rrno")
                    String rrno,
            @Query("rrdate")
                    String rrdate,
            @Query("mode")
                    String mode,
            @Query("transporter")
                    String transporter,
            @Query("charges")
                    JSONArray jsonCharges,
            @Query("e_sugam")
                    String strEwayNumber);

    @POST("?urlq=service")
    Call<ApiResponse> createPackageWithoutMultipart(
            @Query("version")
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
                    String eSugam,
            @Query("order")
                    String order,
            @Query("distance")
                    String distance,
            @Query("vehicle")
                    String vehicle,
            @Query("rrno")
                    String rrno,
            @Query("rrdate")
                    String rrdate,
            @Query("mode")
                    String mode,
            @Query("transporter")
                    String transporter,
            @Query("charges")
                    JSONArray jsonCharges,
            @Query("e_sugam")
                    String strEwayNumber,
            @Query("images")
                    JSONArray jsonImages
    );

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
    Call<ApiResponse> getDeliveryUserList(@Query("version")
                                                  String version,
                                          @Query("key")
                                                  String key,
                                          @Query("task")
                                                  String task,
                                          @Query("user_id")
                                                  String userid,
                                          @Query("access_token")
                                                  String accessToken,
                                          @Query("search")
                                                  String search);

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
                                                  String traversal,
                                          @Query("search")
                                                  String search,
                                          @Query("start_date")
                                                  String startDate,
                                          @Query("end_date")
                                                  String endDate);

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

    @GET("?urlq=service")
    Call<ApiResponse> getGatepassShippingInfo(@Query("version")
                                                      String version,
                                              @Query("key")
                                                      String key,
                                              @Query("task")
                                                      String task,
                                              @Query("user_id")
                                                      String userid,
                                              @Query("access_token")
                                                      String accessToken,
                                              @Query("pacshtid")
                                                      String pacshtid);


    @GET("?urlq=service")
    Call<ApiResponse> createGatePass(@Query("version")
                                             String version,
                                     @Query("key")
                                             String key,
                                     @Query("task")
                                             String task,
                                     @Query("user_id")
                                             String userid,
                                     @Query("access_token")
                                             String accessToken,
                                     @Query("uid")
                                             String uid,
                                     @Query("pacid")
                                             String pacid,
                                     @Query("pacshtid")
                                             String pacshtid,
                                     @Query("scompid")
                                             String scompid,
                                     @Query("chkid")
                                             String chkid,
                                     @Query("etid")
                                             String etid,
                                     @Query("smstid")
                                             String smstid,
                                     @Query("by")
                                             String by,
                                     @Query("vehicle")
                                             String vehicle,
                                     @Query("transporter")
                                             String transporter
    );

    @GET("?urlq=service")
    Call<ApiResponse> getInventoryList(@Query("version")
                                               String version,
                                       @Query("key")
                                               String key,
                                       @Query("task")
                                               String task,
                                       @Query("user_id")
                                               String userid,
                                       @Query("access_token")
                                               String accessToken,
                                       @Query("iscid")
                                               String iscid,
                                       @Query("last_fetch")
                                               String lastFetch,
                                       @Query("traversal")
                                               String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getOutForDeliveryList(@Query("version")
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
                                                    String traversal,
                                            @Query("status1")
                                                    String status1,
                                            @Query("status2")
                                                    String status2,
                                            @Query("search")
                                                    String search,
                                            @Query("start_date")
                                                    String startDate,
                                            @Query("end_date")
                                                    String endDate);

    @GET("?urlq=service")
    Call<ApiResponse> getPackedAgainstStock(@Query("version")
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
                                                    String traversal,
                                            @Query("type")
                                                    String type);

    @GET("?urlq=service")
    Call<ApiResponse> downloadConsignmentPDF(@Query("version")
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

    @GET("?urlq=service")
    Call<ApiResponse> viewOrderDetails(@Query("version")
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
                                       @Query("purorid")
                                               String purOrId);

    @GET("?urlq=service")
    Call<ApiResponse> fetchStockDetails(@Query("version")
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
                                        @Query("stockreqid")
                                                String stockReqId);


    @GET("?urlq=service")
    Call<ApiResponse> cancelGatepass(@Query("version")
                                             String version,
                                     @Query("key")
                                             String key,
                                     @Query("task")
                                             String task,
                                     @Query("user_id")
                                             String userid,
                                     @Query("access_token")
                                             String accessToken,
                                     @Query("pacdelgatid")
                                             String gatepassId);

    @GET("?urlq=service")
    Call<ApiResponse> cancelPackage(@Query("version")
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
                                    @Query("pacid")
                                            String pacid);

    @GET("?urlq=service")
    Call<ApiResponse> editInfoPackage(@Query("version")
                                              String version,
                                      @Query("key")
                                              String key,
                                      @Query("task")
                                              String task,
                                      @Query("user_id")
                                              String userid,
                                      @Query("access_token")
                                              String accessToken,
                                      @Query("pacid")
                                              String pacid);


    @GET("?urlq=service")
    Call<ApiResponse> viewGatepassPackages(@Query("version")
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
                                           @Query("pacdelgatid")
                                                   String pacdelgatid);

    @GET("?urlq=service")
    Call<ApiResponse> downloadPackageGatepassInvoice(@Query("version")
                                                             String version,
                                                     @Query("key")
                                                             String key,
                                                     @Query("task")
                                                             String task,
                                                     @Query("user_id")
                                                             String userid,
                                                     @Query("access_token")
                                                             String accessToken,
                                                     @Query("cuid")
                                                             String cuid,
                                                     @Query("invid")
                                                             String invid);

    @GET("?urlq=service")
    Call<ApiResponse> customerInfoInGatepassPackage(@Query("version")
                                                            String version,
                                                    @Query("key")
                                                            String key,
                                                    @Query("task")
                                                            String task,
                                                    @Query("user_id")
                                                            String userid,
                                                    @Query("access_token")
                                                            String accessToken,
                                                    @Query("pacid")
                                                            String pacid);

    @GET("?urlq=service")
    Call<ApiResponse> getPackageHistoryInGatepass(@Query("version")
                                                          String version,
                                                  @Query("key")
                                                          String key,
                                                  @Query("task")
                                                          String task,
                                                  @Query("user_id")
                                                          String userid,
                                                  @Query("access_token")
                                                          String accessToken,
                                                  @Query("pacid")
                                                          String pacid);

    @GET("?urlq=service")
    Call<ApiResponse> searchVendor(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("name")
                    String name
    );

    @GET("?urlq=service")
    Call<ApiResponse> searchItem(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("search")
                    String name
    );

    @GET("?urlq=service")
    Call<ApiResponse> getSearchedProductsDetails(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("id")
                    String id
    );

    @GET("?urlq=service")
    Call<ApiResponse> getShippedPackages(@Query("version")
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
    Call<ApiResponse> searchAuthorizedByUser(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("search")
                    String name
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadChallanPDF(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("pacid")
                    String pacid
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadInvoicePDF(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("cuid")
                    String cuId,
            @Query("invid")
                    String invid
    );


    @GET("?urlq=service")
    Call<ApiResponse> downloadGatePassPDF(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("pacdelgatid")
                    String pacdelGatid
    );


    @GET("?urlq=service")
    Call<ApiResponse> getCustomerInfoByPacId(@Query("version")
                                                     String version,
                                             @Query("key")
                                                     String key,
                                             @Query("task")
                                                     String task,
                                             @Query("user_id")
                                                     String userid,
                                             @Query("access_token")
                                                     String accessToken,
                                             @Query("pacid")
                                                     String pacid);

    @GET("?urlq=service")
    Call<ApiResponse> revertPackageDelivery(@Query("version")
                                                    String version,
                                            @Query("key")
                                                    String key,
                                            @Query("task")
                                                    String task,
                                            @Query("user_id")
                                                    String userid,
                                            @Query("access_token")
                                                    String accessToken,
                                            @Query("pacdelgatid")
                                                    String pacdelgatid,
                                            @Query("pacdelgatpacid")
                                                    String pacdelgatpacid);

    @GET("?urlq=service")
    Call<ApiResponse> getPackageStatus(@Query("version")
                                               String version,
                                       @Query("key")
                                               String key,
                                       @Query("task")
                                               String task,
                                       @Query("user_id")
                                               String userid,
                                       @Query("access_token")
                                               String accessToken,
                                       @Query("pacdelgatpacid")
                                               String pacdelgatpacid);

    @GET("?urlq=service")
    Call<ApiResponse> getAttemptFailedPackageStatus(@Query("version")
                                                            String version,
                                                    @Query("key")
                                                            String key,
                                                    @Query("task")
                                                            String task,
                                                    @Query("user_id")
                                                            String userid,
                                                    @Query("access_token")
                                                            String accessToken,
                                                    @Query("pacdelgatpacid")
                                                            String pacdelgatpacid,
                                                    @Query("statusFlag")
                                                            String status);

    @GET("?urlq=service")
    Call<ApiResponse> changeOutForDeliveryStatus(@Query("version")
                                                         String version,
                                                 @Query("key")
                                                         String key,
                                                 @Query("task")
                                                         String task,
                                                 @Query("user_id")
                                                         String userid,
                                                 @Query("access_token")
                                                         String accessToken,
                                                 @Query("pacdelgatpacid")
                                                         String pacdelgatpacid,
                                                 @Query("pacdelgatpacsid")
                                                         String pacStatusId,
                                                 @Query("narration") String narration);

    @GET("?urlq=service")
    Call<ApiResponse> reAttemptDeliveryFailedTask(@Query("version")
                                                          String version,
                                                  @Query("key")
                                                          String key,
                                                  @Query("task")
                                                          String task,
                                                  @Query("user_id")
                                                          String userid,
                                                  @Query("access_token")
                                                          String accessToken,
                                                  @Query("pacdelgatpacid")
                                                          String pacdelgatpacid,
                                                  @Query("narration") String narration);


    @GET("?urlq=service")
    Call<ApiResponse> getOrderPackages(@Query("version")
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
                                       @Query("chkoid") String chkoid);

    @GET("?urlq=service")
    Call<ApiResponse> getTransporterName(
            @Query("version")
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
            @Query("search")
                    String s,
            @Query("agid")
                    String s1);

    @GET("?urlq=service")
    Call<ApiResponse> editPackage(@Query("version")
                                          String version,
                                  @Query("key")
                                          String key,
                                  @Query("task")
                                          String task,
                                  @Query("user_id")
                                          String userid,
                                  @Query("access_token")
                                          String accessToken,
                                  @Query("pacid")
                                          String pacid,
                                  @Query("e_sugam")
                                          String s,
                                  @Query("invoice_date")
                                          String stringDateOfInvoice,
                                  @Query("invoice_no")
                                          String invoiceNo);

    @Multipart
    @POST("?urlq=service")
    Call<ApiResponse> createPackageUploadMultipleDoc(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Part MultipartBody.Part[] image
    );


    @GET("?urlq=service")
    Call<ApiResponse> getPackageLists(@Query("version")
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
                                              String traversal,
                                      @Query("search")
                                              String search,
                                      @Query("start_date")
                                              String startDate,
                                      @Query("end_date")
                                              String endDate,
                                      @Query("type")
                                              String type);


    @GET("?urlq=service")
    Call<ApiResponse> getAllocationConsignments(@Query("version")
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
                                                        String traversal,
                                                @Query("search")
                                                        String search,
                                                @Query("start_date")
                                                        String startDate,
                                                @Query("end_date")
                                                        String endDate,
                                                @Query("iscid")
                                                        String iscid);


    @GET("?urlq=service")
    Call<ApiResponse> cancelPackageStock(@Query("version")
                                                 String version,
                                         @Query("key")
                                                 String key,
                                         @Query("task")
                                                 String task,
                                         @Query("user_id")
                                                 String userid,
                                         @Query("access_token")
                                                 String accessToken,
                                         @Query("pacid")
                                                 String pacid);

    @GET("?urlq=service")
    Call<ApiResponse> getToBeApprovedConsignmentLists(@Query("version")
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
                                                              String traversal,
                                                      @Query("search")
                                                              String search,
                                                      @Query("start_date")
                                                              String startDate,
                                                      @Query("end_date")
                                                              String endDate,
                                                      @Query("status")
                                                              String status);

    @GET("?urlq=service")
    Call<ApiResponse> deleteConsignment(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String accessToken,
            @Query("iscid")
                    String iscId
    );

}