package com.accrete.sixorbit.rest;


import com.accrete.sixorbit.model.ApiResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("?urlq=service")
    Call<ApiResponse> getFollowUp(
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
            @Query("last_fetch")
                    String lastFetch
           /* ,@Query("type")
             String type*/);

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
    Call<ApiResponse> getInDetailFollowUp(
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
            @Query("foid")
                    String foid
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

    //TODO - Common method to get all values without any mapping
    @GET("?urlq=service")
    Call<ApiResponse> getNotification(
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
            @Query("last_fetch")
                    String last_fetch,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal

    );

    @GET("?urlq=service")
    Call<ApiResponse> getNotificationRead(
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
    Call<ApiResponse> updateNotificationReadTime(
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
            @Query("uid")
                    String uid,
            @Query("time")
                    String time

    );

    @GET("?urlq=service")
    Call<ApiResponse> sendChatMessages(
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
            @Query("uid")
                    String receiver_uid,
            @Query("message")
                    String message

    );

    @GET("?urlq=service")
    Call<ApiResponse> getChatMessages(
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
            @Query("uid")
                    String receiver_uid,
            @Query("last_fetch")
                    String last_fetch,
            @Query("traversal")
                    String travseral
    );


    @GET("?urlq=service")
    Call<ApiResponse> getLead(
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
            @Query("last_updated")
                    String lastUpdated
    );


    @GET("?urlq=service")
    Call<ApiResponse> editLead(
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
            @Query("fname")
                    String firstName,
            @Query("lname")
                    String lastName,
            @Query("company-name")
                    String companyName,
            @Query("company-mobile")
                    String companyMobile,
            @Query("company-email")
                    String companyEmail,
            @Query("website")
                    String website,
            @Query("gender")
                    String gender,
            @Query("current-add-sitename")
                    String office,
            @Query("current-line1")
                    String lineOne,
            @Query("current-line2")
                    String lineTwo,
            @Query("current-ctid")
                    String countryId,
            @Query("current-stid")
                    String stid,
            @Query("city")
                    String city,
            @Query("current-pincode")
                    String pincode,
            @Query("assignee")
                    String assignee,
            @Query("special-instructions")
                    String specialInstruction,
            @Query("contact")
                    JSONArray contact,
            @Query("schedule")
                    String schedule,
            @Query("contact-person")
                    String contactPerson,
            @Query("communication-mode")
                    String communicationMode,
            @Query("followup-assignee")
                    String followUpAssignee,
            @Query("scheduled-time")
                    String scheduleTime,
            @Query("alert-time")
                    String alertTime,
            @Query("alert-mode")
                    String alertMode,
            @Query("lead")
                    String leaId,
            @Query("sync_id")
                    String syncID,
            @Query("lead-person-type")
                    String leadPersonType,
            @Query("said")
                    String shippingAddressId
    );

    @GET("?urlq=service")
    Call<ApiResponse> addLead(
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
            @Query("fname")
                    String firstName,
            @Query("lname")
                    String lastName,
            @Query("company-name")
                    String companyName,
            @Query("company-mobile")
                    String companyMobile,
            @Query("company-email")
                    String companyEmail,
            @Query("website")
                    String website,
            @Query("gender")
                    String gender,
            @Query("current-add-sitename")
                    String office,
            @Query("current-line1")
                    String lineOne,
            @Query("current-line2")
                    String lineTwo,
            @Query("current-ctid")
                    String countryId,
            @Query("current-stid")
                    String stid,
            @Query("city")
                    String city,
            @Query("current-pincode")
                    String pincode,
            @Query("assignee")
                    String assignee,
            @Query("special-instructions")
                    String specialInstruction,
            @Query("contact")
                    JSONArray contact,
            @Query("schedule")
                    String schedule,
            @Query("contact-person")
                    String contactPerson,
            @Query("communication-mode")
                    String communicationMode,
            @Query("followup-assignee")
                    String followUpAssignee,
            @Query("scheduled-time")
                    String scheduleTime,
            @Query("alert-time")
                    String alertTime,
            @Query("alert-mode")
                    String alertMode,
            @Query("sync_id")
                    String syncID,
            @Query("lead-person-type")
                    String leadPersonType
    );

    @GET("?urlq=service")
    Call<ApiResponse> recordFollowUp(
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
            @Query("leaid")
                    String leaid,
            @Query("enid")
                    String enid,
            @Query("qoid")
                    String qoid,
            @Query("chkoid")
                    String chkoid,
            @Query("cuid")
                    String cuid,
            @Query("jocaid")
                    String jocaid,
            @Query("schedule-radio")
                    String selectedRadioButton,
            @Query("scheduled-time")
                    String scheduledTime,
            @Query("alert-time")
                    String alertTime,
           /* @Query("from_time")
                    String fromTime,
            @Query("to_time")
                    String toTime,*/
            @Query("communicated-mode")
                    String communicatedMode,
            @Query("outcome")
                    String outcome,
            @Query("description")
                    String description,
            @Query("reason")
                    String reason,
            @Query("assignee")
                    String assignee,
            @Query("contact-person")
                    String contactPerson,
            @Query("alert-mode")
                    String alertMode,
            @Query("comment")
                    String comment,
            @Query("contacted-person")
                    String contacted_person,
            @Query("communication-mode")
                    String communication_mode,
            @Query("sync_id")
                    String sync_id,
            @Query("foid")
                    String foid,
            @Query("checked")
                    String nextFollowUp
    );

    @GET("?urlq=service")
    Call<ApiResponse> postFeedsComments(
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
            @Query("uid")
                    String uid,
            @Query("uaid")
                    String uaid,
            @Query("comment")
                    String comment,
            @Query("sid")
                    String sid,
            @Query("sync_id")
                    String syncId

    );

    @GET("?urlq=service")
    Call<ApiResponse> profileFetch(
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
            @Query("last_updated")
                    String lastUpdated

    );

    @GET("?urlq=service")
    Call<ApiResponse> addEmail(
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
            @Query("user-email")
                    String email

    );


    @GET("?urlq=service")
    Call<ApiResponse> getCustomerInfo(
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
                    String cuid

    );

    @GET("?urlq=service")
    Call<ApiResponse> sendTwoStepVerificationStatus(
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
            @Query("value")
                    String value

    );

    @GET("?urlq=service")
    Call<ApiResponse> addPhone(
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
            @Query("user-mobile")
                    String mobile

    );

    @GET("?urlq=service")
    Call<ApiResponse> otpConfirm(
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
            @Query("otp-code")
                    String otp

    );

    @Multipart
    @POST("/")
    Call<ApiResponse> postChatFiles(
            @Part("urlq")
                    String service,
            @Part("version")
                    String version,
            @Part("key")
                    String key,
            @Part("task")
                    String task,
            @Part("user_id")
                    String userId,
            @Part("access_token")
                    String accessToken,
            @Part("receiver_uid")
                    String receiverUId,
            @Part("sync_id")
                    String syncId,
            @Part("message")
                    String message,
            @Part("image")
                    MultipartBody.Part image

    );


    @GET("?urlq=service")
    Call<ApiResponse> editProfile(
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
            @Query("user-name")
                    String username,
            @Query("user-dob")
                    String userDob,
            @Query("user-bio")
                    String userBio,
            @Query("line1")
                    String doorNumber,
            @Query("line2")
                    String street,
            @Query("coverlid")
                    String locality,
            @Query(" zip-code")
                    String zipCode,
            @Query("stid")
                    String stid,
            @Query("ctid")
                    String ctid,
            @Query("city")
                    String city
    );


    @Multipart
    @POST("?urlq=service")
    Call<ApiResponse> editProfileWithMultipart(
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
            @Query("user-name")
                    String username,
            @Query("user-dob")
                    String userDob,
            @Query("user-bio")
                    String userBio,
            @Query("line1")
                    String doorNumber,
            @Query("line2")
                    String street,
            @Query("coverlid")
                    String locality,
            @Query(" zip-code")
                    String zipCode,
            @Query("stid")
                    String stid,
            @Query("ctid")
                    String ctid,
            @Query("city")
                    String city,
            @Part MultipartBody.Part image
    );


    @GET("?urlq=service")
    Call<ApiResponse> changePassword(
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
            @Query(" old-pass")
                    String oldPass,
            @Query("new-pass")
                    String newPass,
            @Query("confirm-pass")
                    String confirmPass

    );

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
    Call<ApiResponse> sendMobileOTP(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type
    );

    @GET("?urlq=service")
    Call<ApiResponse> sendMobileOTPToVerifyEmail(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type,
            @Query("send-type")
                    String sendType,
            @Query("auevid")
                    String auevid

    );

    @GET("?urlq=service")
    Call<ApiResponse> sendMobileOTPToVerifyMobile(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type,
            @Query("send-type")
                    String sendType,
            @Query("aumvid")
                    String aumvid

    );

    @GET("?urlq=service")
    Call<ApiResponse> confirmMobileOTP(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type,
            @Query("otp-code")
                    String otp_code
    );

    @GET("?urlq=service")
    Call<ApiResponse> confirmVerifyEmailOTP(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type,
            @Query("send-type")
                    String sendType,
            @Query("auevid")
                    String aumvid,
            @Query("otp-code")
                    String otp_code
    );

    @GET("?urlq=service")
    Call<ApiResponse> confirmVerifyPhoneOTP(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("access_token")
                    String accessToken,
            @Query("user_id")
                    String user_id,
            @Query("type")
                    String type,
            @Query("send-type")
                    String sendType,
            @Query("aumvid")
                    String aumvid,
            @Query("otp-code")
                    String otp_code
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
                    String repassword

    );

    @GET("?urlq=service")
    Call<ApiResponse> getEnquiry(
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
            @Query("last_updated")
                    String lastUpdated
    );

    @GET("?urlq=service")
    Call<ApiResponse> deleteEmail(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("email")
                    String email

    );

    @GET("?urlq=service")
    Call<ApiResponse> deleteMobile(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("mobile")
                    String mobile

    );

    @GET("?urlq=service")
    Call<ApiResponse> editEmail(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("email")
                    String email,
            @Query("auevid")
                    String auevid

    );

    @GET("?urlq=service")
    Call<ApiResponse> editMobile(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("mobile")
                    String email,
            @Query("aumvid")
                    String auevid

    );

    @GET("?urlq=service")
    Call<ApiResponse> primaryEmail(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("auevid")
                    String auevid,
            @Query("primary")
                    String primary

    );

    @GET("?urlq=service")
    Call<ApiResponse> primaryMobile(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userid,
            @Query("access_token")
                    String token,
            @Query("aumvid")
                    String auevid,
            @Query("primary")
                    String primary

    );

    @GET("?urlq=service")
    Call<ApiResponse> getQuotation(
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
            @Query("time")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal
    );

    @GET("?urlq=service")
    Call<ApiResponse> getCustomersPendingInvoice(
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
                    String cuid,
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );


    @GET("?urlq=service")
    Call<ApiResponse> getCustomersList(
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
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal,
            @Query("search")
                    String search,
            @Query("order_flag")
                    int flag,
            @Query("start_limit")
                    int start_limit,
            @Query("cno")
                    String cNo
    );

    @GET("?urlq=service")
    Call<ApiResponse> getWalletTransaction(
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
                    String cuid,
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal
    );

    //TODO Updated on 9th July 2k18
    @GET("?urlq=service")
    Call<ApiResponse> invoiceInfoSendEmail(
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
                    String cuid,
            @Query("send_email")
                    String sendEmail,
            @Query("send_mail")
                    String eventSendEmail,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate,
            @Query("pending_flag")
                    String pendingFlag
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadCustomerWalletPDF(
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
                    String cuid,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );

    @GET("?urlq=service")
    Call<ApiResponse> getVendorsList(
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
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal,
            @Query("search")
                    String search,
            @Query("order_flag")
                    int flag,
            @Query("start_limit")
                    int start_limit,
            @Query("vno")
                    String vNo
    );

    @GET("?urlq=service")
    Call<ApiResponse> getVendorsPendingInvoice(
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
            @Query("venid")
                    String venid,
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );


    @GET("?urlq=service")
    Call<ApiResponse> getVendorInfo(
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
            @Query("venid")
                    String venId

    );


    @GET("?urlq=service")
    Call<ApiResponse> getVendorRecentTransaction(
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
            @Query("venid")
                    String venId,
            @Query("last_fetch")
                    String time,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal
    );

    @GET("?urlq=service")
    Call<ApiResponse> vendorWalletSendEmail(
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
            @Query("venid")
                    String venId,
            @Query("send_email")
                    String sendEmail,
            @Query("send_mail")
                    String eventSendEmail,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadVendorTransactionPDF(
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
            @Query("venid")
                    String venId,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );

    @GET("?urlq=service")
    Call<ApiResponse> addFollowUp(
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
            @Query("leaid")
                    String strLeadId,
            @Query("contact-person")
                    String strContactPerson,
            @Query("communicated-mode")
                    String strCommunicatedMode,
            @Query("assignee")
                    String strFollowUpAssignee,
            @Query("scheduled-time")
                    String strScheduleDate,
            @Query("alert-time")
                    String strAlertTime,
            @Query("alert-mode")
                    String strAlertModeChecked,
            @Query("sync_id")
                    String strSyncId,
            @Query("schedule-radio")
                    String strScheduleRadio);

    @GET("?urlq=service")
    Call<ApiResponse> addQuotationFollowUp(
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
            @Query("qoid")
                    String strLeadId,
            @Query("contact-person")
                    String strContactPerson,
            @Query("communicated-mode")
                    String strCommunicatedMode,
            @Query("assignee")
                    String strFollowUpAssignee,
            @Query("scheduled-time")
                    String strScheduleDate,
            @Query("alert-time")
                    String strAlertTime,
            @Query("alert-mode")
                    String strAlertModeChecked,
            @Query("sync_id")
                    String strSyncId,
            @Query("schedule-radio")
                    String strScheduleRadio);


    @GET("?urlq=service")
    Call<ApiResponse> addOrderFollowUp(
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
            @Query("chkoid")
                    String strLeadId,
            @Query("contact-person")
                    String strContactPerson,
            @Query("communicated-mode")
                    String strCommunicatedMode,
            @Query("assignee")
                    String strFollowUpAssignee,
            @Query("scheduled-time")
                    String strScheduleDate,
            @Query("alert-time")
                    String strAlertTime,
            @Query("alert-mode")
                    String strAlertModeChecked,
            @Query("sync_id")
                    String strSyncId,
            @Query("schedule-radio")
                    String strScheduleRadio);


    @GET("?urlq=service")
    Call<ApiResponse> deleteProfileImage(
            @Query("version")
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
    Call<ApiResponse> downloadVendorWalletVoucher(
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
            @Query("venid")
                    String venId,
            @Query("avid")
                    String avid);

    @GET("?urlq=service")
    Call<ApiResponse> downloadVendorPendingInvoiceVoucher(
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
            @Query("venid")
                    String venId,
            @Query("pinvid")
                    String pinvid);

    @GET("?urlq=service")
    Call<ApiResponse> downloadCustomerWalletVoucher(
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
            @Query("avid")
                    String avid);

    @GET("?urlq=service")
    Call<ApiResponse> downloadCustomerPendingInvoiceVoucher(
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
                    String pinvid);

    @GET("?urlq=service")
    Call<ApiResponse> getCustomerOrders(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getVendorOrderHistory(
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
            @Query("venid")
                    String venid,
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getVendorConsignment(
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
            @Query("venid")
                    String venid,
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal);


    @GET("?urlq=service")
    Call<ApiResponse> getCustomerOrderInfo(
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
            @Query("order_id")
                    String orderId
    );

    @GET("?urlq=service")
    Call<ApiResponse> getCustomersPackages(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("order_id")
                    String orderId);

    @GET("?urlq=service")
    Call<ApiResponse> getSearchedCustomerReference(
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
                    String searchStr,
            @Query("balance")
                    String balance);

    @GET("?urlq=service")
    Call<ApiResponse> getSearchedItem(
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
                    String searchStr);

    @GET("?urlq=service")
    Call<ApiResponse> getReferenceContactPerson(
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
            @Query("type")
                    String typeStr,
            @Query("id")
                    String idStr

    );

    @GET("?urlq=service")
    Call<ApiResponse> addContactPerson(
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
            @Query("contact-detail")
                    String contactArray

    );

    @GET("?urlq=service")
    Call<ApiResponse> getCities(
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
            @Query("ctid")
                    String countryCode,
            @Query("stid")
                    String stateId,
            @Query("search")
                    String search

    );

    @GET("?urlq=service")
    Call<ApiResponse> getCustomerQuotations(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal);

    @GET("?urlq=service")
    Call<ApiResponse> getCustomerQuotationInfo(
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
            @Query("qoid")
                    String qoId
    );


    @GET("?urlq=service")
    Call<ApiResponse> getCustomerProductQuotationInfo(
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
            @Query("qoid")
                    String qoId);

    @GET("?urlq=service")
    Call<ApiResponse> getCustomerOrderProducts(
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
            @Query("order_id")
                    String orderId,
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal
    );


    @GET("?urlq=service")
    Call<ApiResponse> getVendorConsignmentInfo(
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
                    String iscid
    );

    @GET("?urlq=service")
    Call<ApiResponse> getVendorInventoryItems(
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
                    String iscid
    );


    @GET("?urlq=service")
    Call<ApiResponse> getCustomersData(
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
                    String cuId
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
                    String id,
            @Query("cuid")
                    String cuId

    );

    @GET("?urlq=service")
    Call<ApiResponse> getDataWithoutId(
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
    Call<ApiResponse> requestAddNewAddress(
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
                    String strCuid,
            @Query("current-line1")
                    String strLineOne,
            @Query("current-line2")
                    String strLineTwo,
            @Query("current-ctid")
                    String strCountryId,
            @Query("current-stid")
                    String strStateId,
            @Query("current-coverid")
                    String strCityId,
            @Query("current-pincode")
                    String strPincode,
            @Query("current-add-fname")
                    String strFirstName,
            @Query("current-add-lname")
                    String strLastName,
            @Query("current-add-mobile")
                    String strMobile,
            @Query("current-add-sitename")
                    String strOfficeName,
            @Query("current-satid")
                    String strType
    );

    @GET("?urlq=service")
    Call<ApiResponse> getPendingQuotationList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("qosid")
                    String qosId,
            @Query("search")
                    String searchText);

    @GET("?urlq=service")
    Call<ApiResponse> downloadQuotations(
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
            @Query("qoid")
                    String qoId);

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
            @Query("cuid")
                    String cuid,
            @Query("pacid")
                    String pacid);


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
                    String cuid,
            @Query("invid")
                    String invId);

    @GET("?urlq=service")
    Call<ApiResponse> fetchExistsQuotationData(
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
            @Query("qoid")
                    String qoid
    );

    @GET("?urlq=service")
    Call<ApiResponse> fetchSalesLedgerWithAgId(
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
                    String search,
            @Query("agid")
                    String agId
    );

    @GET("?urlq=service")
    Call<ApiResponse> fetchSalesLedgerWithOutAgId(
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
                    String search
    );

    @GET("?urlq=service")
    Call<ApiResponse> fetchExistingOrderData(
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
            @Query("chkoid")
                    String chkoid
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadOrder(
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
            @Query("chkoid")
                    String chkOId,
            @Query("cuid")
                    String cuId);

    @GET("?urlq=service")
    Call<ApiResponse> cancelQuotation(
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
            @Query("qoid")
                    String qoId,
            @Query("data")
                    String data);

    @GET("?urlq=service")
    Call<ApiResponse> getScannedProductsDetails(
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
                    String vNo,
            @Query("cuid")
                    String cuId);

    @FormUrlEncoded
    @POST("?urlq=service")
    Call<ApiResponse> sendJSONData(
            @Query("version")
                    String version,
            @Query("key")
                    String key,
            @Query("task")
                    String task,
            @Query("user_id")
                    String userId,
            @Query("access_token")
                    String accessToken,
            @Field("data")
                    JSONObject data);

    @GET("?urlq=service")
    Call<ApiResponse> getCreateCollectionsInvoicesList(
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
            @Query("alid")
                    String alId,
            @Query("invid")
                    String invid,
            @Query("chkoid")
                    String chkoid);

    @GET("?urlq=service")
    Call<ApiResponse> getCollectionsInvoicesList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("status")
                    String status,
            @Query("chkid")
                    String chkid,
            @Query("search")
                    String searchQuery);

    @GET("?urlq=service")
    Call<ApiResponse> getCollectionsMyTransactions(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("status")
                    String status);

    @GET("?urlq=service")
    Call<ApiResponse> getCollectionsTransactionsHistoryList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("chkoid")
                    String chkoid,
            @Query("jocaid")
                    String jocaid,
            @Query("invid")
                    String invid);

    @GET("?urlq=service")
    Call<ApiResponse> downloadCollectionsMyTransactionsVoucher(
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
            @Query("tid")
                    String tid);

    @GET("?urlq=service")
    Call<ApiResponse> collectionInvoiceDetail(
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
            @Query("invid")
                    String invid);

    @GET("?urlq=service")
    Call<ApiResponse> getCollectionsInvoicesItemsList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("invid")
                    String invid);

    @GET("?urlq=service")
    Call<ApiResponse> getCollectionsPaymentHistoryList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("invttid")
                    String invttid,
            @Query("invid")
                    String invid);

    @GET("?urlq=service")
    Call<ApiResponse> getOrdersCollectionsHistoryList(
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
            @Query("chkoid")
                    String chkoid);

    @GET("?urlq=service")
    Call<ApiResponse> getOrdersReferenceList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("type")
                    String type,
            @Query("chkoid")
                    String chkoid);

    @GET("?urlq=service")
    Call<ApiResponse> deleteTransaction(
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
            @Query("invtid")
                    String invtid);

    @Multipart
    @POST("?urlq=service")
    Call<ApiResponse> updateProductsImageWithMultipart(
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
            @Query("isvid")
                    String isvid,
            @Part MultipartBody.Part image
    );


    //TODO Added on 12th June 2k18
    @GET("?urlq=service")
    Call<ApiResponse> fetchOrderDataWithSiteName(
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
            @Query("chkoid")
                    String chkoid,
            @Query("sitename")
                    String sitename
    );

    @GET("?urlq=service")
    Call<ApiResponse> fetchExistingOrderDataByQoid(
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
            @Query("qoid")
                    String qoid
    );

    @GET("?urlq=service")
    Call<ApiResponse> downloadVendorPurchaseOrder(
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
            @Query("venid")
                    String venId,
            @Query("purorid")
                    String purorid);

    //TODO - Common method to get all values without any mapping
    @GET("?urlq=service")
    Call<ApiResponse> getOrdersList(
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
            @Query("last_fetch")
                    String last_fetch,
            //Pass 1/2 to get items of forward/backward date
            @Query("traversal")
                    String traversal,
            @Query("search")
                    String searchText

    );

    @GET("?urlq=service")
    Call<ApiResponse> switchCompany(
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
            @Query("company_id")
                    String companyId,
            @Query("app_flag")
                    String appFlag,
            @Query("mac_addr")
                    String macAddr

    );

    @GET("?urlq=service")
    Call<ApiResponse> undoCollection(
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
            @Query("tid")
                    String tId

    );

    @GET("?urlq=service")
    Call<ApiResponse> getCustomerwiseOutstanding(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("search")
                    String searchQuery);

    @GET("?urlq=service")
    Call<ApiResponse> getEnquiryList(
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
            @Query("last_fetch")
                    String lastFetch,
            @Query("traversal")
                    String traversal,
            @Query("search")
                    String searchQuery,
            @Query("ensid")
                    String ensId);

    @GET("?urlq=service")
    Call<ApiResponse> addEnquiryFollowUp(
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
            @Query("enid")
                    String strEnId,
            @Query("contact-person")
                    String strContactPerson,
            @Query("communicated-mode")
                    String strCommunicatedMode,
            @Query("assignee")
                    String strFollowUpAssignee,
            @Query("scheduled-time")
                    String strScheduleDate,
            @Query("alert-time")
                    String strAlertTime,
            @Query("alert-mode")
                    String strAlertModeChecked,
            @Query("sync_id")
                    String strSyncId,
            @Query("schedule-radio")
                    String strScheduleRadio);

    //TODO Added on 29th June 2k18
    @GET("?urlq=service")
    Call<ApiResponse> fetchEnquiryInfo(
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
            @Query("enid")
                    String enid);

    //TODO Added on 5th July 2k18
    @GET("?urlq=service")
    Call<ApiResponse> getCustomersContactPersons(
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
            @Query("qoid")
                    String qoId,
            @Query("leaid")
                    String leaId,
            @Query("enid")
                    String enId,
            @Query("chkoid")
                    String chkOId,
            @Query("cuid")
                    String cuId,
            @Query("jocaid")
                    String jocaId,
            @Query("purorid")
                    String purOrId
    );

    //TODO added on 9th July 2k18
    @GET("?urlq=service")
    Call<ApiResponse> walletOrderSendEmail(
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
                    String cuid,
            @Query("send_email")
                    String sendEmail,
            @Query("send_mail")
                    String eventSendEmail,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate
    );

    @GET("?urlq=service")
    Call<ApiResponse> getEnquiryProductsList(
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
            @Query("enid")
                    String enid);


    @GET("?urlq=service")
    Call<ApiResponse> downloadCustomerInvoicePDF(
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
                    String cuid,
            @Query("start_date")
                    String startDate,
            @Query("end_date")
                    String endDate,
            @Query("pending_flag")
                    String pending_flag
    );

}