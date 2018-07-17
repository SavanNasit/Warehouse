package com.accrete.sixorbit.activity.company;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Notify;
import com.accrete.sixorbit.model.OtpEmailFetch;
import com.accrete.sixorbit.model.OtpMobileFetch;
import com.accrete.sixorbit.model.Permission;
import com.accrete.sixorbit.model.Role;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.ContentProvider;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by {Anshul} on 25/6/18.
 */

public class SwitchingCompanyActivity extends AppCompatActivity {
    private ImageView imageViewLoader;
    private TextView textViewMessage;
    private String companyId, leadCount, followupCount, userName, imageUrl, companyCode, mobile = "", empId,
            reportTo, designation, realName, lastSeen, addressLine1, addressLine2, city, state, email,
            country, userBio, userLocality, userZipCode, otpSettings, joined, dateOfBirth,
            isAdmin, companyName;
    private DatabaseHandler databaseHandler;
    private List<Role> roles;
    private ArrayList<String> rolesArrayList = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_switching_company);

        if (getIntent() != null && getIntent().hasExtra("company_id")) {
            companyId = getIntent().getStringExtra("company_id");
        }

        databaseHandler = new DatabaseHandler(this);

        imageViewLoader = (ImageView) findViewById(R.id.imageView_loader);
        textViewMessage = (TextView) findViewById(R.id.textView_message);
        textViewMessage.setText("Switching Company");


        if (!NetworkUtil.getConnectivityStatusString(SwitchingCompanyActivity.this).equals
                (getString(R.string.not_connected_to_internet))) {
            showLoader();
            switchCompany(companyId);
        } else {
            Toast.makeText(SwitchingCompanyActivity.this, getString(R.string.network_error),
                    Toast.LENGTH_SHORT).show();
        }


    }

    private void hideLoader() {
        if (SwitchingCompanyActivity.this != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (SwitchingCompanyActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (SwitchingCompanyActivity.this != null) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public void switchCompany(String companyId) {
        try {
            task = getString(R.string.submit_switch_company);
            if (AppPreferences.getIsLogin(SwitchingCompanyActivity.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(SwitchingCompanyActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(SwitchingCompanyActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(SwitchingCompanyActivity.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.switchCompany(version, key, task, userId, accessToken,
                    companyId, "1", getMacAddr());
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {

                        if (apiResponse.getSuccess()) {

                            successfullyLogin(apiResponse);
                            //   Toast.makeText(SwitchingCompanyActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(SwitchingCompanyActivity.this, apiResponse.getMessage());
                        } else {
                            if (SwitchingCompanyActivity.this != null) {
                                Toast.makeText(SwitchingCompanyActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            hideLoader();
                            finish();
                        }

                        hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (SwitchingCompanyActivity.this != null) {
                        Toast.makeText(SwitchingCompanyActivity.this, getString(R.string.connect_server_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                    hideLoader();
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
            finish();
        }
    }

    public String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(Integer.toHexString(b & 0xFF) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void successfullyLogin(final ApiResponse apiResponse) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Constants.logoutWrongCredentials(SwitchingCompanyActivity.this, "");

                //Hide Keypad
                hideKeyboard(SwitchingCompanyActivity.this);

                leadCount = apiResponse.getData().getLeadNewcount();
                followupCount = apiResponse.getData().getFollowupPendingcount();
                Log.e("leadscount&&followupsC", leadCount + "   " + followupCount + "");
                companyCode = apiResponse.getData().getCompanyCode();
                userName = apiResponse.getData().getName();
                if (apiResponse.getData().getProfile().getMobile() != null) {
                    mobile = apiResponse.getData().getProfile().getMobile();
                }
                dateOfBirth = apiResponse.getData().getProfile().getDateOfBirth();
                email = apiResponse.getData().getProfile().getEmail();
                empId = apiResponse.getData().getProfile().getEmpid();
                designation = apiResponse.getData().getProfile().getDesignation();
                reportTo = apiResponse.getData().getProfile().getReportTo();
                joined = apiResponse.getData().getProfile().getDateOfJoining();
                lastSeen = apiResponse.getData().getProfile().getLastseen();
                realName = apiResponse.getData().getProfile().getName();
                roles = apiResponse.getData().getProfile().getRoles();
                addressLine1 = apiResponse.getData().getProfile().getAddressLine1();
                addressLine2 = apiResponse.getData().getProfile().getAddressLine2();
                city = apiResponse.getData().getProfile().getCity();
                state = apiResponse.getData().getProfile().getState();
                country = apiResponse.getData().getProfile().getCountry();
                userBio = apiResponse.getData().getProfile().getDescription();
                userLocality = apiResponse.getData().getProfile().getLocalityName();
                userZipCode = apiResponse.getData().getProfile().getZipCode();
                otpSettings = String.valueOf(apiResponse.getData().getOtpSettings());
                isAdmin = apiResponse.getData().getProfile().getIsAdmin();
                //TODO Added on 25th June
                companyName = apiResponse.getData().getCompanyName();
                companyId = apiResponse.getData().getCompanyId();


                if (isAdmin != null && isAdmin.equals("1")) {
                    AppPreferences.setBoolean(SwitchingCompanyActivity.this, AppUtils.ISADMIN, true);
                } else {
                    AppPreferences.setBoolean(SwitchingCompanyActivity.this, AppUtils.ISADMIN, false);
                }

                if (otpSettings.equals("1")) {
                    AppPreferences.setTwoStepVerificationStatus(SwitchingCompanyActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, "true");
                } else {
                    AppPreferences.setTwoStepVerificationStatus(SwitchingCompanyActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, "false");
                }
                //  addUserAddress();

                for (int i = 0; i < roles.size(); i++) {
                    if (!rolesArrayList.contains(roles.get(i).getName())) {
                        rolesArrayList.add(roles.get(i).getName());
                    }
                }

                imageUrl = apiResponse.getData().getPhoto();
                Log.v("Request", String.valueOf(apiResponse.getData().getUserId()) + " " + apiResponse.getData().getAccessToken());
                userId = apiResponse.getData().getUserId();
                accessToken = apiResponse.getData().getAccessToken();

                //Insert Data into Feeds Notify Table
                if (apiResponse != null && apiResponse.getData().getNotify() != null) {
                    for (final Notify notify : apiResponse.getData().getNotify()) {
                        databaseHandler.insertFeedsNotifications(notify);
                    }
                }
                //Insert Data into Otp emails table
                if (apiResponse != null && apiResponse.getData().getOtpEmailfetch() != null) {
                    for (final OtpEmailFetch otpEmailFetch : apiResponse.getData().getOtpEmailfetch()) {
                        if (!databaseHandler.checkEmailAddress(otpEmailFetch.getEmail())) {
                            databaseHandler.insertTwoStepVerificationEmails(otpEmailFetch);
                        }

                    }
                }
                //Insert Data into otp mobiles table
                if (apiResponse != null && apiResponse.getData().getOtpMobilefetch() != null) {
                    for (final OtpMobileFetch otpMobileFetch : apiResponse.getData().getOtpMobilefetch()) {
                        if (!databaseHandler.checkMobileNumber(otpMobileFetch.getMobile())) {
                            databaseHandler.insertTwoStepVerificationMobiles(otpMobileFetch);
                        }
                    }
                }
                //Insert Data into Permission table
                if (apiResponse != null && apiResponse.getData().getPermission() != null) {
                    for (final Permission permission : apiResponse.getData().getPermission()) {
                        databaseHandler.insertUsersPermission(permission);
                    }
                }

                //Communications Mode
                if (apiResponse != null && apiResponse.getData().getFollowupCommunicationModes() != null) {
                    AppPreferences.setModesList(SwitchingCompanyActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE,
                            apiResponse.getData().getFollowupCommunicationModes());
                }

                //Global Shared Preferences
                //AppPreferences.setIsLogin(SwitchingCompanyActivity.this, AppUtils.ISLOGIN, true);
                AppPreferences.setUserId(SwitchingCompanyActivity.this, AppUtils.USER_ID, userId);
                AppPreferences.setAccessToken(SwitchingCompanyActivity.this, AppUtils.ACCESS_TOKEN, accessToken);
                AppPreferences.setUserSessionId(SwitchingCompanyActivity.this, AppUtils.USER_SESSION_ID, apiResponse.getData().getUserSessionId());
                AppPreferences.setEmail(SwitchingCompanyActivity.this, AppUtils.USER_EMAIL, email);
                AppPreferences.setUserName(SwitchingCompanyActivity.this, AppUtils.USER_NAME, userName);
                AppPreferences.setPhoto(SwitchingCompanyActivity.this, AppUtils.USER_PHOTO, imageUrl);
                AppPreferences.setLeadsCount(SwitchingCompanyActivity.this, AppUtils.LEADS_COUNT, leadCount);
                AppPreferences.setFollowupsCount(SwitchingCompanyActivity.this, AppUtils.FOLLOWUPS_COUNT, followupCount);

                AppPreferences.setUserDateOfBirth(SwitchingCompanyActivity.this, AppUtils.USER_DOB, dateOfBirth);
                AppPreferences.setUserMobile(SwitchingCompanyActivity.this, AppUtils.USER_MOBILE, mobile);
                AppPreferences.setUserEmployeeId(SwitchingCompanyActivity.this, AppUtils.USER_EMP_ID, empId);
                AppPreferences.setUserDesignation(SwitchingCompanyActivity.this, AppUtils.USER_DESIGNATION, designation);
                AppPreferences.setUserReportTo(SwitchingCompanyActivity.this, AppUtils.USER_REPORT_TO, reportTo);
                AppPreferences.setUserJoined(SwitchingCompanyActivity.this, AppUtils.USER_JOINED, joined);
                AppPreferences.setUserLastseen(SwitchingCompanyActivity.this, AppUtils.USER_LAST_SEEN, lastSeen);
                AppPreferences.setUserRealName(SwitchingCompanyActivity.this, AppUtils.USER_REAL_NAME, realName);
                // AppPreferences.setUserAddress(SwitchingCompanyActivity.this, AppUtils.USER_ADDRESS, address);

                AppPreferences.setProfileRoles(SwitchingCompanyActivity.this, AppUtils.USER_ROLES, rolesArrayList);

                AppPreferences.setUserCountryCode(SwitchingCompanyActivity.this, AppUtils.USER_COUNTRY_CODE,
                        apiResponse.getData().getProfile().getCtid());
                AppPreferences.setUserStateCode(SwitchingCompanyActivity.this, AppUtils.USER_STATE_CODE,
                        apiResponse.getData().getProfile().getStid());
                AppPreferences.setUserCityCode(SwitchingCompanyActivity.this, AppUtils.USER_CITY_CODE,
                        apiResponse.getData().getProfile().getCoverid());

                AppPreferences.setuserBio(SwitchingCompanyActivity.this, AppUtils.USER_BIO, userBio);
                AppPreferences.setUserCountry(SwitchingCompanyActivity.this, AppUtils.USER_COUNTRY, country);
                AppPreferences.setUserCity(SwitchingCompanyActivity.this, AppUtils.USER_CITY, city);
                AppPreferences.setUserLocality(SwitchingCompanyActivity.this, AppUtils.USER_LOCALITY, userLocality);
                AppPreferences.setUserZipCode(SwitchingCompanyActivity.this, AppUtils.USER_ZIP_CODE, userZipCode);
                AppPreferences.setUserState(SwitchingCompanyActivity.this, AppUtils.USER_STATE, state);
                AppPreferences.setUserAddressLine1(SwitchingCompanyActivity.this, AppUtils.USER_ADDRESS_LINE_ONE, addressLine1);
                AppPreferences.setUserAddressLine2(SwitchingCompanyActivity.this, AppUtils.USER_ADDRESS_LINE_TWO, addressLine2);
                AppPreferences.setCompanyCode(SwitchingCompanyActivity.this, AppUtils.COMPANY_CODE, companyCode);
                AppPreferences.setString(SwitchingCompanyActivity.this, AppUtils.COMPANY_NAME, companyName);
                AppPreferences.setString(SwitchingCompanyActivity.this, AppUtils.COMPANY_ID, companyId);


                //Redirect to Main Activity
                AppPreferences.setIsLogin(SwitchingCompanyActivity.this, AppUtils.ISLOGIN, true);
                navigateToHome();

                ContentValues values = new ContentValues();
                values.put(ContentProvider.login, "true");
                getContentResolver().insert(ContentProvider.CONTENT_URI, values);

                //Update UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideLoader();
                    }
                });
            }
        });
        thread.start();
    }

    public void navigateToHome() {
        Intent intentLogin = new Intent(SwitchingCompanyActivity.this, DrawerActivity.class);
        intentLogin.putExtra(getString(R.string.intent), getString(R.string.password));
        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
        startActivity(intentLogin);
        finish();
    }
}
