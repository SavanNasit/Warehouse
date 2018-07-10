package com.accrete.sixorbit.activity.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.OtpEmailFetch;
import com.accrete.sixorbit.model.OtpMobileFetch;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.version;

public class SettingsActivity extends AppCompatActivity {

    ProgressBar progressBar;

    EmailValidator emailValidator;
    String userId;
    String primaryEmail, primaryMobile;
    private Toolbar toolbar;
    private AlertDialog alertDialog;
    private DatabaseHandler db;
    private List<OtpEmailFetch> emailLists = new ArrayList<>();
    private List<OtpMobileFetch> phoneLists = new ArrayList<>();
    private LinearLayout twoStepVerificationLayout;

    private TextView textViewPasswordManangement, textViewProfile, textViewRecentLogin,
            textViewTwoStepVerification, textViewPrimaryEmail, textViewPrimaryMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = new DatabaseHandler(this);
        emailValidator = new EmailValidator();
        userId = AppPreferences.getUserId(SettingsActivity.this, AppUtils.USER_ID);
        initializeView();
        getDataFromDB();

        //Hide and show Primary email and number
        if (AppPreferences.getTwoStepVerificationStatus(SettingsActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS).equals("false")) {
            textViewPrimaryEmail.setVisibility(View.GONE);
            textViewPrimaryMobile.setVisibility(View.GONE);
        } else {
            if (primaryEmail != null && !primaryEmail.isEmpty() && !primaryEmail.equals("null")) {
                textViewPrimaryEmail.setVisibility(View.VISIBLE);
            } else {
                textViewPrimaryEmail.setVisibility(View.GONE);
            }
            if (primaryMobile != null && !primaryMobile.isEmpty() && !primaryMobile.equals("null")) {
                textViewPrimaryMobile.setVisibility(View.VISIBLE);
            } else {
                textViewPrimaryMobile.setVisibility(View.GONE);
            }
        }

    }

    //getting data from db
    private void getDataFromDB() {

        if (db != null) {
            emailLists = db.getAllTwoStepVerificationPrimaryEmails(userId, "1");
            phoneLists = db.getAllTwoStepVerificationPrimaryMobiles(userId, "1");
        }

        //displaying the primary email in settings activity

        if (emailLists.size() > 0) {
            primaryEmail = emailLists.get(0).getEmail();
            textViewPrimaryEmail.setText(primaryEmail);
            textViewPrimaryEmail.setVisibility(View.VISIBLE);
        } else {
            textViewPrimaryEmail.setVisibility(View.GONE);
        }

        //displaying the primary mobile in settings activity

        if (phoneLists.size() > 0) {
            primaryMobile = phoneLists.get(0).getMobile();
            textViewPrimaryMobile.setText(primaryMobile);
            textViewPrimaryMobile.setVisibility(View.VISIBLE);
        } else {
            textViewPrimaryMobile.setVisibility(View.GONE);
        }
    }

    private void initializeView() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewProfile = (TextView) findViewById(R.id.profile);
        textViewRecentLogin = (TextView) findViewById(R.id.recent_login_history);
        twoStepVerificationLayout = (LinearLayout) findViewById(R.id.two_step_verification_layout);
        textViewTwoStepVerification = (TextView) findViewById(R.id.two_step_verification);
        textViewPasswordManangement = (TextView) findViewById(R.id.text_password_mgmt);
        textViewPrimaryEmail = (TextView) findViewById(R.id.primary_email);
        textViewPrimaryMobile = (TextView) findViewById(R.id.primary_mobile);

        progressBar = (ProgressBar) findViewById(R.id.settings_progress_bar);

        toolbar.setTitle(getString(R.string.title_settings));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        textViewPasswordManangement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPasswordManagement();
            }
        });

        textViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToViewProfile();
            }
        });
        textViewRecentLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRecentLogin = new Intent(SettingsActivity.this, RecentLoginListActivity.class);
                startActivity(intentRecentLogin);
            }
        });
        twoStepVerificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTwoStepVerification = new Intent(SettingsActivity.this, TwoStepVerificationInfo.class);
                startActivity(intentTwoStepVerification);
            }
        });
    }

    private void sendToViewProfile() {
        Intent intentProfile = new Intent(this, EditProfileActivity.class);
        startActivity(intentProfile);
    }

    private void dialogPasswordManagement() {

        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_password_management, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextOldPassword, editTextNewPassword, editTextConfirmPassword;

        editTextOldPassword = (EditText) dialogView.findViewById(R.id.old_password);
        editTextNewPassword = (EditText) dialogView.findViewById(R.id.new_passsword);
        editTextConfirmPassword = (EditText) dialogView.findViewById(R.id.reenter_new_password);
        Button buttonChange = (Button) dialogView.findViewById(R.id.btn_change);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextOldPassword.getText().toString())) {
                    Toast.makeText(SettingsActivity.this, "Please enter Old Password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextNewPassword.getText().toString())) {
                    Toast.makeText(SettingsActivity.this, "Please enter New password", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(editTextConfirmPassword.getText().toString())) {
                    Toast.makeText(SettingsActivity.this, "Please re enter new password", Toast.LENGTH_SHORT).show();
                } else {
                    changePassword(editTextOldPassword.getText().toString(), editTextNewPassword.getText().toString(), editTextConfirmPassword.getText().toString());
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }


    private void changePassword(String oldPassword, String confirmPassword, String newPassword) {

        task = getString(R.string.task_change_password);
        userId = AppPreferences.getUserId(SettingsActivity.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(SettingsActivity.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(SettingsActivity.this, AppUtils.DOMAIN);


        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.changePassword(version, key, task, userId, accessToken, oldPassword, newPassword, confirmPassword);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(SettingsActivity.this, getString(R.string.password_change_success), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                    progressBar.setVisibility(View.GONE);
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    Constants.logoutWrongCredentials(SettingsActivity.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(SettingsActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SettingsActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppPreferences.getTwoStepVerificationStatus(SettingsActivity.this, AppUtils.TWO_STEP_VERIFICATION_STATUS).equals("false")) {
            textViewPrimaryEmail.setVisibility(View.GONE);
            textViewPrimaryMobile.setVisibility(View.GONE);
        } else {
            textViewPrimaryEmail.setVisibility(View.VISIBLE);
            textViewPrimaryMobile.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            getDataFromDB();
        }
    }

}
