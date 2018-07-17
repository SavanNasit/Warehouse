package com.accrete.sixorbit.activity.password;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.login.LoginActivity;
import com.accrete.sixorbit.activity.navigationView.DrawerActivity;
import com.accrete.sixorbit.fragment.Drawer.HomeFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Notify;
import com.accrete.sixorbit.model.OtpEmailFetch;
import com.accrete.sixorbit.model.OtpMobileFetch;
import com.accrete.sixorbit.model.Permission;
import com.accrete.sixorbit.model.Role;
import com.accrete.sixorbit.receiver.IncomingSmsReceiver;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.ContentProvider;
import com.accrete.sixorbit.utils.EmailValidator;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_RECEIVE_READ_SMS_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 7/4/17.
 */

public class PasswordActivity extends Activity implements View.OnClickListener, PasswordView {
    private ProgressBar progressBar;
    private boolean twoStepVerificationEnabled = false;
    private List<Role> roles;
    private ArrayList<String> rolesArrayList = new ArrayList<String>();
    private String leadCount, followupCount, userName, imageUrl, companyCode, mobile = "", empId,
            reportTo, designation, realName, lastSeen, addressLine1, addressLine2, city, state,
            country, userBio, userLocality, userZipCode, otpSettings, joined, dateOfBirth,
            isAdmin, companyName, companyId;
    private int progressStatus = 0;
    private TextView textViewNext, textViewResetPassword;
    private EditText editTextpassword;
    private String email;
    private String password;
    private AlertDialog dialogSendLinkToEmailForResetPassword;
    private AlertDialog alertDialog, alertDialogVerify;
    private PasswordPresenter presenter;
    private String status;
    private DatabaseHandler databaseHandler;
    private Uri dataUri;
    private String dataUriString, uriAccessToken;
    private IncomingSmsReceiver incomingSmsReceiver;
    private EditText editTextOtp;
    private Button buttonSendLink;
    private ProgressBar progressBarSendLink;
    private EmailValidator emailValidator;
    private BroadcastReceiver mOTPReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra(getString(R.string.send_mobile_otp));
            if (alertDialogVerify != null && alertDialogVerify.isShowing()) {
                editTextOtp.setText(message);
                if (!NetworkUtil.getConnectivityStatusString(PasswordActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    verifyPhoneOTP(editTextOtp.getText().toString().trim(), alertDialogVerify);
                } else {
                    Toast.makeText(PasswordActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        databaseHandler = new DatabaseHandler(this);
        email = getIntent().getExtras().getString(getString(R.string.email));
        if (getIntent() != null) {
            dataUri = getIntent().getData();
            if (dataUri != null) {
                dataUriString = dataUri.toString();
                String[] split = dataUriString.split("token=");
                uriAccessToken = split[1];
                dialogResetPassword(uriAccessToken);
            }
        }
        //Register Receiver
        incomingSmsReceiver = new IncomingSmsReceiver();
        IntentFilter filter = new IntentFilter();
        registerReceiver(incomingSmsReceiver, filter);

        status = NetworkUtil.getConnectivityStatusString(this);
        textViewNext = (TextView) findViewById(R.id.password_txt_next);
        textViewResetPassword = (TextView) findViewById(R.id.password_txt_reset_password);
        editTextpassword = (EditText) findViewById(R.id.login_edittext_password);
        progressBar = (ProgressBar) findViewById(R.id.password_progress_bar);
        textViewNext.setOnClickListener(this);
        textViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendLinkToEmailForResetPassword();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.lightViolet));
        }
        presenter = new PasswordPresenterImpl(this);

        editTextpassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    password = editTextpassword.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        editTextpassword.setEnabled(false);
                        setPasswordError();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                editTextpassword.setEnabled(true);
                            }
                        }, 3000);
                    } else {
                        if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                            validateLogin();
                        } else {
                            editTextpassword.setEnabled(false);
                            Toast.makeText(getApplicationContext(), R.string.not_connected_to_internet, Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    editTextpassword.setEnabled(true);
                                }
                            }, 3000);
                        }
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        editTextpassword.setTransformationMethod(new PasswordTransformationMethod());

        editTextpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    editTextpassword.setHint("Password");
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mOTPReceiver,
                new IntentFilter(getString(R.string.send_mobile_otp)));
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

    private void validateLogin() {
        task = getString(R.string.login);
        userId = AppPreferences.getUserId(PasswordActivity.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(PasswordActivity.this, AppUtils.DOMAIN);

        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progressStatus);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        //TODO - flag is 1 static for SixOrbit
        Call call = apiService.doValidateLogin(version, key, task, email, password, "1");
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    successfullyLogin(apiResponse);
                } else if (apiResponse.getSuccessCode().equals("10005")) {
                    progressBar.setVisibility(View.GONE);
                    textViewNext.setEnabled(false);
                    //Enable Button again
                    setCredentialError();
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            textViewNext.setEnabled(true);
                        }
                    }, 3000);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
                textViewNext.setEnabled(false);
                Toast.makeText(PasswordActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewNext.setEnabled(true);
                    }
                }, 3000);

            }

        });

    }

    private void successfullyLogin(final ApiResponse apiResponse) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                //Hide Keypad
                hideKeyboard(PasswordActivity.this);

                leadCount = apiResponse.getData().getLeadNewcount();
                followupCount = apiResponse.getData().getFollowupPendingcount();
                Log.e("leadscount&&followupsC", leadCount + "   " + followupCount + "");
                companyCode = apiResponse.getData().getCompanyCode();
                userName = apiResponse.getData().getName();
                if (apiResponse.getData().getProfile().getMobile() != null) {
                    mobile = apiResponse.getData().getProfile().getMobile();
                }
                dateOfBirth = apiResponse.getData().getProfile().getDateOfBirth();
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
                    AppPreferences.setBoolean(PasswordActivity.this, AppUtils.ISADMIN, true);
                } else {
                    AppPreferences.setBoolean(PasswordActivity.this, AppUtils.ISADMIN, false);
                }

                if (otpSettings.equals("1")) {
                    AppPreferences.setTwoStepVerificationStatus(PasswordActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, "true");
                } else {
                    AppPreferences.setTwoStepVerificationStatus(PasswordActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, "false");
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
                try {
                    if (apiResponse != null && apiResponse.getData().getNotify() != null) {
                        for (final Notify notify : apiResponse.getData().getNotify()) {
                            databaseHandler.insertFeedsNotifications(notify);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Insert Data into Otp emails table
                try {
                    if (apiResponse != null && apiResponse.getData().getOtpEmailfetch() != null) {
                        for (final OtpEmailFetch otpEmailFetch : apiResponse.getData().getOtpEmailfetch()) {
                            if (!databaseHandler.checkEmailAddress(otpEmailFetch.getEmail())) {
                                databaseHandler.insertTwoStepVerificationEmails(otpEmailFetch);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Insert Data into otp mobiles table
                try {
                    if (apiResponse != null && apiResponse.getData().getOtpMobilefetch() != null) {
                        for (final OtpMobileFetch otpMobileFetch : apiResponse.getData().getOtpMobilefetch()) {
                            if (!databaseHandler.checkMobileNumber(otpMobileFetch.getMobile())) {
                                databaseHandler.insertTwoStepVerificationMobiles(otpMobileFetch);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Insert Data into Permission table
                try {
                    if (apiResponse != null && apiResponse.getData().getPermission() != null) {
                        for (final Permission permission : apiResponse.getData().getPermission()) {
                            databaseHandler.insertUsersPermission(permission);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Communications Mode
                try {
                    if (apiResponse != null && apiResponse.getData().getFollowupCommunicationModes() != null) {
                        AppPreferences.setModesList(PasswordActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE,
                                apiResponse.getData().getFollowupCommunicationModes());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Global Shared Preferences
                //AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                AppPreferences.setUserId(PasswordActivity.this, AppUtils.USER_ID, userId);
                AppPreferences.setAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN, accessToken);
                AppPreferences.setUserSessionId(PasswordActivity.this, AppUtils.USER_SESSION_ID, apiResponse.getData().getUserSessionId());
                AppPreferences.setEmail(PasswordActivity.this, AppUtils.USER_EMAIL, email);
                AppPreferences.setUserName(PasswordActivity.this, AppUtils.USER_NAME, userName);
                AppPreferences.setPhoto(PasswordActivity.this, AppUtils.USER_PHOTO, imageUrl);
                AppPreferences.setLeadsCount(PasswordActivity.this, AppUtils.LEADS_COUNT, leadCount);
                AppPreferences.setFollowupsCount(PasswordActivity.this, AppUtils.FOLLOWUPS_COUNT, followupCount);

                AppPreferences.setUserDateOfBirth(PasswordActivity.this, AppUtils.USER_DOB, dateOfBirth);
                AppPreferences.setUserMobile(PasswordActivity.this, AppUtils.USER_MOBILE, mobile);
                AppPreferences.setUserEmployeeId(PasswordActivity.this, AppUtils.USER_EMP_ID, empId);
                AppPreferences.setUserDesignation(PasswordActivity.this, AppUtils.USER_DESIGNATION, designation);
                AppPreferences.setUserReportTo(PasswordActivity.this, AppUtils.USER_REPORT_TO, reportTo);
                AppPreferences.setUserJoined(PasswordActivity.this, AppUtils.USER_JOINED, joined);
                AppPreferences.setUserLastseen(PasswordActivity.this, AppUtils.USER_LAST_SEEN, lastSeen);
                AppPreferences.setUserRealName(PasswordActivity.this, AppUtils.USER_REAL_NAME, realName);
                // AppPreferences.setUserAddress(PasswordActivity.this, AppUtils.USER_ADDRESS, address);

                AppPreferences.setProfileRoles(PasswordActivity.this, AppUtils.USER_ROLES, rolesArrayList);

                AppPreferences.setUserCountryCode(PasswordActivity.this, AppUtils.USER_COUNTRY_CODE,
                        apiResponse.getData().getProfile().getCtid());
                AppPreferences.setUserStateCode(PasswordActivity.this, AppUtils.USER_STATE_CODE,
                        apiResponse.getData().getProfile().getStid());
                AppPreferences.setUserCityCode(PasswordActivity.this, AppUtils.USER_CITY_CODE,
                        apiResponse.getData().getProfile().getCoverid());

                AppPreferences.setuserBio(PasswordActivity.this, AppUtils.USER_BIO, userBio);
                AppPreferences.setUserCountry(PasswordActivity.this, AppUtils.USER_COUNTRY, country);
                AppPreferences.setUserCity(PasswordActivity.this, AppUtils.USER_CITY, city);
                AppPreferences.setUserLocality(PasswordActivity.this, AppUtils.USER_LOCALITY, userLocality);
                AppPreferences.setUserZipCode(PasswordActivity.this, AppUtils.USER_ZIP_CODE, userZipCode);
                AppPreferences.setUserState(PasswordActivity.this, AppUtils.USER_STATE, state);
                AppPreferences.setUserAddressLine1(PasswordActivity.this, AppUtils.USER_ADDRESS_LINE_ONE, addressLine1);
                AppPreferences.setUserAddressLine2(PasswordActivity.this, AppUtils.USER_ADDRESS_LINE_TWO, addressLine2);
                Log.e("leadscount&&followupsC", leadCount + "   " + followupCount + "");
                AppPreferences.setCompanyCode(PasswordActivity.this, AppUtils.COMPANY_CODE, companyCode);
                AppPreferences.setString(PasswordActivity.this, AppUtils.COMPANY_NAME, companyName);
                AppPreferences.setString(PasswordActivity.this, AppUtils.COMPANY_ID, companyId);

                if (apiResponse.getData().getOtpSettings() == 1) {
                    twoStepVerificationEnabled = true;
                    if (android.os.Build.VERSION.SDK_INT >= 23) {
                        askReadSmsPermission();
                    } else {
                        sendMobileOTP();
                    }

                } else {
                    //Redirect to Main Activity
                    AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                    navigateToHome();
                }

                ContentValues values = new ContentValues();
                values.put(ContentProvider.login, "true");
                getContentResolver().insert(ContentProvider.CONTENT_URI, values);

                //Update UI
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                        //Enable Button again
                        textViewNext.setEnabled(true);
                    }
                });
            }
        });
        thread.start();
    }

    private void dialogSendLinkToEmailForResetPassword() {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_send_link_to_email_for_reset_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSendLinkToEmailForResetPassword = builder.create();
        dialogSendLinkToEmailForResetPassword.setCanceledOnTouchOutside(true);
        final EditText editTextEmail;
        final TextView textViewsignIn;
        LinearLayout linearLayoutSignIn = (LinearLayout) dialogView.findViewById(R.id.already_account_sign_in);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        textViewsignIn = (TextView) dialogView.findViewById(R.id.already_account_sign_in_text_view);
        progressBarSendLink = (ProgressBar) dialogView.findViewById(R.id.dialog_send_link_progress_bar);

        buttonSendLink = (Button) dialogView.findViewById(R.id.button_send_link);
        if (email != null && !email.isEmpty()) {
            editTextEmail.setText(email);
        }

        textViewsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSignIn = new Intent(PasswordActivity.this, LoginActivity.class);
                intentSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentSignIn);
            }
        });

        buttonSendLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.username_error));
                } else {
                    AppPreferences.setEmail(PasswordActivity.this, AppUtils.USER_EMAIL, editTextEmail.getText().toString());
                    emailValidator = new EmailValidator();
                    boolean valid = emailValidator.validateEmail(editTextEmail.getText().toString().trim());
                    if (valid) {
                        if (!NetworkUtil.getConnectivityStatusString(PasswordActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            sendLinkToEmailForResetPassword(editTextEmail.getText().toString().trim());
                        } else {
                            buttonSendLink.setEnabled(false);
                            Toast.makeText(PasswordActivity.this, R.string.not_connected_to_internet, Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    buttonSendLink.setEnabled(true);
                                }
                            }, 3000);
                        }
                    } else {
                        buttonSendLink.setEnabled(false);
                        Toast.makeText(PasswordActivity.this, getString(R.string.valid_email_error), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                buttonSendLink.setEnabled(true);
                            }
                        }, 3000);
                    }

                }

            }
        });
        dialogSendLinkToEmailForResetPassword.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSendLinkToEmailForResetPassword.show();
    }

    private void dialogResetPassword(final String uriAccessToken) {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_reset_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextNewPassword, editTextConfirmPassword;
        final TextView textViewsignIn;
        alertDialog.setCanceledOnTouchOutside(true);
        LinearLayout linearLayoutSignIn = (LinearLayout) dialogView.findViewById(R.id.dialog_reset_already_account_sign_in);
        editTextNewPassword = (EditText) dialogView.findViewById(R.id.new_password);
        editTextConfirmPassword = (EditText) dialogView.findViewById(R.id.confirm_password);
        Button buttonResetPassword = (Button) dialogView.findViewById(R.id.reset_password);
        textViewsignIn = (TextView) dialogView.findViewById(R.id.already_account_sign_in_text_view);

        textViewsignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToLogin();
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextNewPassword.getText().toString())) {
                    Toast.makeText(PasswordActivity.this, "Please enter New password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextConfirmPassword.getText().toString())) {
                    Toast.makeText(PasswordActivity.this, "Please re enter new password", Toast.LENGTH_SHORT).show();
                } else if (!editTextNewPassword.getText().toString().equals(editTextConfirmPassword.getText().toString())) {
                    Toast.makeText(PasswordActivity.this, "New Password does not match the confirm password.", Toast.LENGTH_SHORT).show();
                } else {
                    resetPassword(uriAccessToken, editTextNewPassword.getText().toString(), editTextConfirmPassword.getText().toString(),
                            AppPreferences.getEmail(PasswordActivity.this, AppUtils.USER_EMAIL));
                }
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();

    }


    @Override
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
        //Unregister broadcast receiver
        unregisterReceiver(incomingSmsReceiver);
        //   LocalBroadcastManager.getInstance(this).unregisterReceiver(mOTPReceiver);
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setPasswordError() {
        //  CustomisedToast.error(PasswordActivity.this, getString(R.string.password_error), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, getString(R.string.password_error), Toast.LENGTH_SHORT).show();
    }

    public void setCredentialError() {
        //   CustomisedToast.error(PasswordActivity.this, getString(R.string.credential_error), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, getString(R.string.credential_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigateToHome() {
        Intent intentLogin = new Intent(PasswordActivity.this, DrawerActivity.class);
        intentLogin.putExtra(getString(R.string.intent), getString(R.string.password));
        intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
        startActivity(intentLogin);
        finish();
    }

    @Override
    public void onClick(View v) {
        password = editTextpassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            textViewNext.setEnabled(false);
            setPasswordError();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    textViewNext.setEnabled(true);
                }
            }, 3000);
        } else {
            if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                validateLogin();
            } else {
                textViewNext.setEnabled(false);
                Toast.makeText(getApplicationContext(), R.string.not_connected_to_internet, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        textViewNext.setEnabled(true);
                    }
                }, 3000);
            }

        }
    }

    private void backToLogin() {
        Intent intentSignIn = new Intent(PasswordActivity.this, LoginActivity.class);
        intentSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentSignIn);
    }


    public void sendLinkToEmailForResetPassword(String email) {
        task = getString(R.string.verify_email_to_forgot_password);

        ApiClient.BASE_URL = AppPreferences.getLastDomain(PasswordActivity.this, AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.verifyEmail(version, key, task, email);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));
        buttonSendLink.setEnabled(false);
        progressBarSendLink.setMax(100);
        progressBarSendLink.setVisibility(View.VISIBLE);
        progressBarSendLink.setProgress(progressStatus);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(PasswordActivity.this, getString(R.string.email_link_success), Toast.LENGTH_SHORT).show();
                    dialogSendLinkToEmailForResetPassword.dismiss();
                    progressBarSendLink.setVisibility(View.GONE);
                    //Enable Button again
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            buttonSendLink.setEnabled(true);
                        }
                    }, 3000);

                } else if (apiResponse.getSuccessCode().equals("10006")) {
                    Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogSendLinkToEmailForResetPassword.dismiss();
                    progressBarSendLink.setVisibility(View.GONE);
                    //Enable Button again
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            buttonSendLink.setEnabled(true);
                        }
                    }, 3000);

                } else if (apiResponse.getSuccessCode().equals("20003")) {
                    Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogSendLinkToEmailForResetPassword.dismiss();
                    progressBarSendLink.setVisibility(View.GONE);
                    //Enable Button again

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            buttonSendLink.setEnabled(true);
                        }
                    }, 3000);

                } else if (apiResponse.getSuccessCode().equals("10003")) {
                    Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    dialogSendLinkToEmailForResetPassword.dismiss();
                    progressBarSendLink.setVisibility(View.GONE);
                    //Enable Button again

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            buttonSendLink.setEnabled(true);
                        }
                    }, 3000);

                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBarSendLink.setVisibility(View.GONE);
                Toast.makeText(PasswordActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        buttonSendLink.setEnabled(true);
                    }
                }, 3000);

            }

        });
    }

    public void resetPassword(String uriAccessToken, String password, String confirmPassword, String changePasswordForThisEmail) {
        try {
            task = getString(R.string.reset_password);
            userId = AppPreferences.getUserId(PasswordActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(PasswordActivity.this, AppUtils.DOMAIN);
            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call call = apiService.resetPassword(version, key, task, userId, uriAccessToken, changePasswordForThisEmail, password, confirmPassword);
            Log.v("Request", String.valueOf(call));
            Log.v("url", String.valueOf(call.request().url()));

            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        Toast.makeText(PasswordActivity.this, getString(R.string.password_change_success), Toast.LENGTH_SHORT).show();
                        backToLogin();
                        alertDialog.dismiss();
                    } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(PasswordActivity.this, apiResponse.getMessage());
                        alertDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(PasswordActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                    t.printStackTrace();
                }

            });
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), R.string.something_wrong, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        finish();
    }

    public void askReadSmsPermission() {
        if (checkPermissionWithRationale(this, new HomeFragment(), new String[]{Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_SMS}, REQUEST_CODE_RECEIVE_READ_SMS_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_RECEIVE_READ_SMS_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for contacts permissions
                    if (perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("TAG", "Read Contact permission granted");
                        //permissions are  granted
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        sendMobileOTP();
                    } else {
                        Log.d("TAG", "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            // handling never ask again and re directing to settings page.
                            askUserToAllowPermissionFromSetting();
                        }
                    }
                }
            }
            break;
        }
    }

    public void askUserToAllowPermissionFromSetting() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(R.string.permission_required);
        // set dialog messageTextView
        alertDialogBuilder
                .setMessage(getString(R.string.request_permission_from_settings))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });
        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void openVerifyMobileDialog() {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_verify, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialogVerify = builder.create();
        final TextView titleTextView;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        editTextOtp = (EditText) dialogView.findViewById(R.id.edit_text_otp);
        editTextOtp.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        TextView textViewContactSupport = (TextView) dialogView.findViewById(R.id.text_view_problem_receiving_otp);
        titleTextView.setText(getString(R.string.title_login));
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_verify);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        TextView buttonResend = (TextView) dialogView.findViewById(R.id.btn_resend);
        buttonCancel.setVisibility(View.GONE);
//        buttonAdd.setText("VERIFY");

        textViewContactSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PasswordActivity.this, "Please contact support", Toast.LENGTH_SHORT).show();
            }
        });


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextOtp.getText().toString().length() != 0) {
                    verifyPhoneOTP(editTextOtp.getText().toString().trim(), alertDialogVerify);
                } else {
                    Toast.makeText(PasswordActivity.this, "Please enter otp", Toast.LENGTH_SHORT).show();
                }
            }
        });
        buttonResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogVerify.dismiss();
                sendMobileOTP();
            }
        });
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogVerify.dismiss();
            }
        });
        alertDialogVerify.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogVerify.show();
    }

    private void verifyPhoneOTP(String mobileOtp, final AlertDialog alertDialog) {
        task = getString(R.string.otp_verify_confirm);
        userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.LAST_DOMAIN);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.confirmMobileOTP(version, key, task, accessToken, userId, "2", mobileOtp);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
//                    Toast.makeText(PasswordActivity.this, getString(R.string.email_verified_successfully), Toast.LENGTH_SHORT).show();
                    //       Toast.makeText(PasswordActivity.this, getString(R.string.email_verified_successfully), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    //Redirect to Main Activity
                    AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                    navigateToHome();
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    Constants.logoutWrongCredentials(PasswordActivity.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PasswordActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    public void sendMobileOTP() {
        task = getString(R.string.send_mobile_otp);
        userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.LAST_DOMAIN);
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.sendMobileOTP(version, key, task, accessToken, userId, "2");
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    //          Toast.makeText(PasswordActivity.this, getString(R.string.email_verified_successfully), Toast.LENGTH_SHORT).show();
//                    Toast.makeText(PasswordActivity.this, getString(R.string.email_verified_successfully), Toast.LENGTH_SHORT).show();
//                    alertDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                    //Redirect to Main Activity
                    openVerifyMobileDialog();
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    Constants.logoutWrongCredentials(PasswordActivity.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PasswordActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

}