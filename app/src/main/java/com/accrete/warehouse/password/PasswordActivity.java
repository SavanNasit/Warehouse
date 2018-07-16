package com.accrete.warehouse.password;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.login.LoginActivity;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Permission;
import com.accrete.warehouse.model.WarehouseList;
import com.accrete.warehouse.navigationView.DrawerActivity;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.EmailValidator;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;


/**
 * Created by poonam on 7/4/17.
 */

public class PasswordActivity extends Activity implements View.OnClickListener, PasswordView {
    ProgressBar progressBar;
    String userName, imageUrl;
    String companyCode;
    private PasswordPresenter presenter;
    private TextView textViewNext, textViewResetPassword;
    private EditText editTextpassword;
    private String email;
    private String password;
    private String status;
    private int progressStatus = 0;
    private AlertDialog dialogSendLinkToEmailForResetPassword;
    private Button buttonSendLink;
    private ProgressBar progressBarSendLink;
    private EmailValidator emailValidator;
    private AlertDialog alertDialog;
    private Uri dataUri;
    private String dataUriString, uriAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.lightViolet));
        }

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

        email = getIntent().getExtras().getString(getString(R.string.email));

        status = NetworkUtil.getConnectivityStatusString(this);
        textViewNext = (TextView) findViewById(R.id.password_txt_next);
        textViewResetPassword = (TextView) findViewById(R.id.password_txt_reset_password);
        editTextpassword = (EditText) findViewById(R.id.login_edittext_password);
        progressBar = (ProgressBar) findViewById(R.id.password_progress_bar);
        textViewNext.setOnClickListener(this);

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
        textViewResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendLinkToEmailForResetPassword();
            }
        });

       /* LocalBroadcastManager.getInstance(this).registerReceiver(mOTPReceiver,
                new IntentFilter(getString(R.string.send_mobile_otp)));*/
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
                Toast.makeText(PasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
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
                    } else if (apiResponse.getSuccessCode().equals("10006")) {
                        Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    Toast.makeText(PasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        presenter.onDestroy();
        super.onDestroy();
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
        ActivityOptions options = ActivityOptions.makeCustomAnimation(getApplicationContext(), R.anim.slide_in_left, R.anim.slide_in_right);
        startActivity(intentLogin, options.toBundle());
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_in_right);
        finish();
    }


    private void validateLogin() {
        task = getString(R.string.login);
        userId = AppPreferences.getUserId(PasswordActivity.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getDomain(PasswordActivity.this, AppUtils.DOMAIN);

        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progressStatus);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        //TODO Added on 12th July 2k18. Its static for warehouse app
        Call call = apiService.doValidateLogin(version, key, task, email, password,"2");
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    //Global Shared Preferences
                    //AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                    AppPreferences.setUserId(PasswordActivity.this, AppUtils.USER_ID, apiResponse.getData().getUserId());
                    AppPreferences.setAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN, apiResponse.getData().getAccessToken());
                    AppPreferences.setEmail(PasswordActivity.this, AppUtils.USER_EMAIL, apiResponse.getData().getProfile().getEmail());
                    AppPreferences.setUserName(PasswordActivity.this, AppUtils.USER_NAME, apiResponse.getData().getName());
                    AppPreferences.setPhoto(PasswordActivity.this, AppUtils.USER_PHOTO, apiResponse.getData().getProfile().getPhoto());
                    AppPreferences.setCompanyCode(PasswordActivity.this, AppUtils.COMPANY_CODE, apiResponse.getData().getCompanyCode());

                    //TODO Added on 12th July 2k18
                    try {
                        if (apiResponse != null && apiResponse.getData().getPermission() != null) {
                            for (final Permission permission : apiResponse.getData().getPermission()) {
                                if (permission.getName().equals(getString(R.string.permission_consignment_approve_str))) {
                                    AppPreferences.setBoolean(PasswordActivity.this,
                                            AppUtils.CONSIGNMENT_APPROVE_PERMISSION, true);
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                  /*  ContentValues values = new ContentValues();
                    values.put(ContentProvider.login, "true");
                    getContentResolver().insert(ContentProvider.CONTENT_URI, values);*/
                    progressBar.setVisibility(View.GONE);
                    //Enable Button again
                    textViewNext.setEnabled(true);
                    AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWarehouseList();
                        }
                    }, 1000);

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
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
                progressBar.setVisibility(View.GONE);
                textViewNext.setEnabled(false);
                Toast.makeText(PasswordActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        textViewNext.setEnabled(true);
                    }
                }, 3000);

            }

        });

    }

    private void getWarehouseList() {
        task = getString(R.string.warehouse_list_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getWarehouseList(version, key, task, userId, accessToken);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (WarehouseList warehouseList : apiResponse.getData().getWarehouseList()) {
                            if (apiResponse.getData().getWarehouseList() != null) {
                                if (apiResponse.getData().getWarehouseList().size() > 0) {
                                    AppPreferences.setWarehouseDefaultName(PasswordActivity.this, AppUtils.WAREHOUSE_DEFAULT_NAME, apiResponse.getData().getWarehouseList().get(0).getName());
                                    AppPreferences.setWarehouseDefaultCheckId(PasswordActivity.this, AppUtils.WAREHOUSE_CHK_ID, apiResponse.getData().getWarehouseList().get(0).getChkid());
                                    AppPreferences.setWarehouseOrderCount(PasswordActivity.this, AppUtils.WAREHOUSE_ORDER_COUNT, apiResponse.getData().getWarehouseList().get(0).getOrderCount());
                                    AppPreferences.setWarehousePackageCount(PasswordActivity.this, AppUtils.WAREHOUSE_PACKAGE_COUNT, apiResponse.getData().getWarehouseList().get(0).getPackageCount());
                                    AppPreferences.setWarehouseGatepassCount(PasswordActivity.this, AppUtils.WAREHOUSE_GATEPASS_COUNT, apiResponse.getData().getWarehouseList().get(0).getGatepassCount());
                                    AppPreferences.setWarehouseConsignmentCount(PasswordActivity.this, AppUtils.WAREHOUSE_CONSIGNMENT_COUNT, apiResponse.getData().getWarehouseList().get(0).getConsignmentCount());
                                    AppPreferences.setWarehouseReceiveConsignmentCount(PasswordActivity.this, AppUtils.WAREHOUSE_RECEIVE_CONSIGNMENT, apiResponse.getData().getWarehouseList().get(0).getReceiveConsignmentCount());
                                }
                            }
                        }
                        navigateToHome();

                    } else {
                        Toast.makeText(PasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (PasswordActivity.this != null) {
                    if (t instanceof SocketTimeoutException) {
                        String message = "Socket Time out. Please try again.";
                        Toast.makeText(PasswordActivity.this, "Unable to fetch json: " + message, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


}