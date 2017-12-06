package com.accrete.warehouse.password;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ContentValues;
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
import com.accrete.warehouse.navigationView.DrawerActivity;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.ContentProvider;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private PasswordPresenter presenter;
    ProgressBar progressBar;
    private TextView textViewNext, textViewResetPassword;
    private EditText editTextpassword;
    private String email;
    private String password;
    private String status;
    private int progressStatus = 0;
    String userName, imageUrl;
    String companyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.lightViolet));
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

       /* LocalBroadcastManager.getInstance(this).registerReceiver(mOTPReceiver,
                new IntentFilter(getString(R.string.send_mobile_otp)));*/
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
        ApiClient.BASE_URL = AppPreferences.getLastDomain(PasswordActivity.this, AppUtils.LAST_DOMAIN);

        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(progressStatus);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.doValidateLogin(version, key, task, email, password);
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
                    AppPreferences.setUserId(PasswordActivity.this, AppUtils.USER_ID, userId);
                    AppPreferences.setAccessToken(PasswordActivity.this, AppUtils.ACCESS_TOKEN, accessToken);
                    AppPreferences.setEmail(PasswordActivity.this, AppUtils.USER_EMAIL, email);
                    AppPreferences.setUserName(PasswordActivity.this, AppUtils.USER_NAME, userName);
                    AppPreferences.setPhoto(PasswordActivity.this, AppUtils.USER_PHOTO, imageUrl);
                    AppPreferences.setCompanyCode(PasswordActivity.this, AppUtils.COMPANY_CODE, companyCode);

                  /*  ContentValues values = new ContentValues();
                    values.put(ContentProvider.login, "true");
                    getContentResolver().insert(ContentProvider.CONTENT_URI, values);*/
                    progressBar.setVisibility(View.GONE);
                    //Enable Button again
                    textViewNext.setEnabled(true);
                    AppPreferences.setIsLogin(PasswordActivity.this, AppUtils.ISLOGIN, true);
                    navigateToHome();
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




}