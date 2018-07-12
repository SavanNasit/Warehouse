package com.accrete.sixorbit.activity.Settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.SettingsEmailAdapter;
import com.accrete.sixorbit.adapter.SettingsMobileAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
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
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by amp on 26/10/17.
 */

public class TwoStepVerificationInfo extends AppCompatActivity implements SettingsMobileAdapter.SettingsMobileAdapterListener,
        SettingsEmailAdapter.SettingsEmailAdapterListener, SettingsEmailAdapter.UpdateListener, SettingsMobileAdapter.UpdateListener {

    public FloatingActionButton floatingActionButtonAdd, floatingActionButtonMobile, floatingActionButtonEmail;
    public LinearLayout fabLayoutEmail, fabLayoutMobile;
    public OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
    public OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
    public boolean flagAddMobileToVerify = false;
    private String email, mobile, phoneOtp, emailOtp;
    private ProgressBar progressBar;
    private EmailValidator emailValidator;
    private View viewEmails, viewMobiles;
    private TextView textViewEmailTitle, textViewMobileTitle, textViewFabAddEmail, textViewFabAddMobile;
    private RecyclerView recyclerViewPhones;
    private SettingsEmailAdapter emailAdapter;
    private SettingsMobileAdapter mobileAdapter;
    private Toolbar toolbar;
    private SwitchCompat switchToggleButton;
    private AlertDialog alertDialog;
    private RecyclerView recyclerViewEmails;
    private DatabaseHandler databaseHandler;
    private List<OtpEmailFetch> emailLists = new ArrayList<>();
    private List<OtpMobileFetch> phoneLists = new ArrayList<>();
    private Boolean isFabOpen = false;
    private Boolean isTwoStepVerificationEnabled = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private TextView textViewAddedEmail, textViewAddedPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_step_verification_info);
        emailValidator = new EmailValidator();
        databaseHandler = new DatabaseHandler(this);

        initializeView();

        //Mobile
        mobileAdapter = new SettingsMobileAdapter(this, phoneLists, this, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewPhones.setLayoutManager(mLayoutManager);
        recyclerViewPhones.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewPhones.setAdapter(mobileAdapter);

        //Email
        emailAdapter = new SettingsEmailAdapter(this, emailLists, this, this);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewEmails.setLayoutManager(mLayoutManager2);
        recyclerViewEmails.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewEmails.setAdapter(emailAdapter);

        //Refresh Email
        if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
            updateEmails(TwoStepVerificationInfo.this);
        } else {

            //Display data from DB
            displayDataInView();

            //Switch Key Listener
            switchChangeListener();
        }


    }

    private void setEmailPrimary() {
        if (emailLists.size() > 1) {
            for (int i = 1; i < emailLists.size(); i++) {
                if (emailLists.get(i).getPrimaryEmail().equals("1")) {
                    emailLists.get(0).setPrimaryEmail("0");
                    return;
                } else {
                    emailLists.get(0).setPrimaryEmail("1");
                }
            }
        } else {
            emailLists.get(0).setPrimaryEmail("1");
        }
    }

    private void setPhonePrimary() {
        if (phoneLists.size() > 1) {
            for (int i = 1; i < phoneLists.size(); i++) {
                if (phoneLists.get(i).getPrimaryNo().equals("1")) {
                    phoneLists.get(0).setPrimaryNo("0");
                    return;
                } else {
                    phoneLists.get(0).setPrimaryNo("1");
                }
            }
        } else {
            phoneLists.get(0).setPrimaryNo("1");
        }
    }

    private void getDataFromDB() {
        emailLists.clear();
        phoneLists.clear();
        if (databaseHandler != null) {

            //Adding User's Primary Email
            OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
            otpEmailFetch.setEmail(AppPreferences.getEmail(TwoStepVerificationInfo.this, AppUtils.USER_EMAIL));
            emailLists.add(otpEmailFetch);

            emailLists.addAll(databaseHandler.getAllTwoStepVerificationEmails());
            setEmailPrimary();

            //Add User's Primary Mobile
            OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
            otpMobileFetch.setMobile(AppPreferences.getUserMobile(TwoStepVerificationInfo.this, AppUtils.USER_MOBILE));
            phoneLists.add(otpMobileFetch);

            phoneLists.addAll(databaseHandler.getAllTwoStepVerificationMobiles());
            setPhonePrimary();

        }
        emailAdapter.notifyDataSetChanged();
        mobileAdapter.notifyDataSetChanged();
    }

    public void displayDataInView() {
        getDataFromDB();

        if (AppPreferences.getTwoStepVerificationStatus(TwoStepVerificationInfo.this, AppUtils.TWO_STEP_VERIFICATION_STATUS).equalsIgnoreCase("true")) {

            if (phoneLists != null && phoneLists.size() > 0) {
                recyclerViewPhones.setVisibility(View.VISIBLE);
                textViewMobileTitle.setVisibility(View.VISIBLE);
                viewMobiles.setVisibility(View.VISIBLE);
            } else {
                recyclerViewPhones.setVisibility(View.GONE);
                textViewMobileTitle.setVisibility(View.GONE);
                viewMobiles.setVisibility(View.GONE);
            }

            if (emailLists != null && emailLists.size() > 0) {
                textViewEmailTitle.setVisibility(View.VISIBLE);
                recyclerViewEmails.setVisibility(View.VISIBLE);
                viewEmails.setVisibility(View.VISIBLE);
            } else {
                textViewEmailTitle.setVisibility(View.GONE);
                recyclerViewEmails.setVisibility(View.GONE);
                viewEmails.setVisibility(View.GONE);
            }
            switchToggleButton.setChecked(true);
            floatingActionButtonAdd.setVisibility(View.VISIBLE);
            isTwoStepVerificationEnabled = true;
            AppPreferences.setTwoStepVerificationStatus(TwoStepVerificationInfo.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, String.valueOf(isTwoStepVerificationEnabled));

        } else {
            switchToggleButton.setChecked(false);
            textViewAddedEmail.setVisibility(View.GONE);
            textViewAddedPhone.setVisibility(View.GONE);
            recyclerViewEmails.setVisibility(View.GONE);
            recyclerViewPhones.setVisibility(View.GONE);
            textViewEmailTitle.setVisibility(View.GONE);
            textViewMobileTitle.setVisibility(View.GONE);
            viewEmails.setVisibility(View.GONE);
            viewMobiles.setVisibility(View.GONE);
            floatingActionButtonAdd.setVisibility(View.GONE);
            isTwoStepVerificationEnabled = false;
            AppPreferences.setTwoStepVerificationStatus(TwoStepVerificationInfo.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, "false");
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            floatingActionButtonAdd.startAnimation(rotate_backward);
            floatingActionButtonMobile.startAnimation(fab_close);
            floatingActionButtonEmail.startAnimation(fab_close);

            textViewFabAddEmail.startAnimation(fab_close);
            textViewFabAddMobile.startAnimation(fab_close);

            floatingActionButtonMobile.setClickable(false);
            floatingActionButtonEmail.setClickable(false);

            fabLayoutMobile.setVisibility(View.GONE);
            fabLayoutEmail.setVisibility(View.GONE);

            floatingActionButtonEmail.setEnabled(false);
            floatingActionButtonMobile.setEnabled(false);
            textViewFabAddEmail.setEnabled(false);
            textViewFabAddMobile.setEnabled(false);

            fabLayoutEmail.setEnabled(false);
            fabLayoutMobile.setEnabled(false);

            fabLayoutEmail.setFocusable(false);
            fabLayoutMobile.setFocusable(false);
            isFabOpen = false;

            Log.d("Fab", "close");
        } else {
            floatingActionButtonAdd.startAnimation(rotate_forward);
            floatingActionButtonMobile.startAnimation(fab_open);
            floatingActionButtonEmail.startAnimation(fab_open);

            textViewFabAddEmail.startAnimation(fab_open);
            textViewFabAddMobile.startAnimation(fab_open);

            fabLayoutMobile.setVisibility(View.VISIBLE);
            fabLayoutEmail.setVisibility(View.VISIBLE);
            floatingActionButtonEmail.setEnabled(true);
            floatingActionButtonMobile.setEnabled(true);
            textViewFabAddEmail.setEnabled(true);
            textViewFabAddMobile.setEnabled(true);

            floatingActionButtonMobile.setClickable(true);
            floatingActionButtonEmail.setClickable(true);
            fabLayoutEmail.setEnabled(true);
            fabLayoutMobile.setEnabled(true);

            isFabOpen = true;
            Log.d("Fab", "open");
        }
    }


    private void initializeView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        textViewEmailTitle = (TextView) findViewById(R.id.textview_email_title);
        textViewMobileTitle = (TextView) findViewById(R.id.textview_mobile_title);
        viewEmails = findViewById(R.id.email_view);
        viewMobiles = findViewById(R.id.mobile_view);

        textViewFabAddEmail = (TextView) findViewById(R.id.text_view_add_email);

        textViewFabAddMobile = (TextView) findViewById(R.id.textview_add_mobile);

        recyclerViewEmails = (RecyclerView) findViewById(R.id.recycler_view_email);
        recyclerViewPhones = (RecyclerView) findViewById(R.id.recycler_view_phones);
        textViewAddedEmail = (TextView) findViewById(R.id.textview_added_email);
        textViewAddedPhone = (TextView) findViewById(R.id.textview_added_phone);
        switchToggleButton = (SwitchCompat) findViewById(R.id.switch_button);
        progressBar = (ProgressBar) findViewById(R.id.settings_progress_bar);

        fabLayoutEmail = (LinearLayout) findViewById(R.id.fab_email_layout);

        fabLayoutMobile = (LinearLayout) findViewById(R.id.fab_mobile_layout);

        floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.two_step_fab);
        floatingActionButtonMobile = (FloatingActionButton) findViewById(R.id.fab_add_phone);
        floatingActionButtonEmail = (FloatingActionButton) findViewById(R.id.fab_add_email);

        fab_open = AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        recyclerViewEmails.setVisibility(View.GONE);
        recyclerViewPhones.setVisibility(View.GONE);
        textViewEmailTitle.setVisibility(View.GONE);
        textViewMobileTitle.setVisibility(View.GONE);

        viewEmails.setVisibility(View.GONE);
        viewMobiles.setVisibility(View.GONE);
        floatingActionButtonAdd.setVisibility(View.GONE);
        toolbar.setTitle(getString(R.string.two_step_verification));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallBack();
            }
        });

        floatingActionButtonMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMobileClickEvent();
            }
        });

        floatingActionButtonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailClickEvent();
            }
        });

        fabLayoutMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMobileClickEvent();
            }
        });

        fabLayoutEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEmailClickEvent();
            }
        });

        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFAB();
            }
        });
    }

    private void addEmailClickEvent() {
        dialogAddEmail();
        isFabOpen = true;
        animateFAB();
    }

    private void addMobileClickEvent() {
        dialogAddPhone();
        isFabOpen = true;
        animateFAB();
    }

    private void dialogAddPhone() {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_two_step_verification_phone, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextPhone;

        editTextPhone = (EditText) dialogView.findViewById(R.id.phone);

        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPhone.getText().toString().trim().length() == 0) {
                    Toast.makeText(TwoStepVerificationInfo.this, "Please enter your mobile number.", Toast.LENGTH_SHORT).show();
                } else if (editTextPhone.getText().toString().trim().length() != 10) {
                    Toast.makeText(TwoStepVerificationInfo.this, "Mobile number should be of 10 digits.", Toast.LENGTH_SHORT).show();
                } else if (databaseHandler.checkMobileNumber(editTextPhone.getText().toString().trim())) {
                    Toast.makeText(TwoStepVerificationInfo.this, "This mobile number is already added.", Toast.LENGTH_SHORT).show();
                } else {
                    mobile = editTextPhone.getText().toString().trim();
                    if (!NetworkUtil.getConnectivityStatusString(TwoStepVerificationInfo.this).equals(getString(R.string.not_connected_to_internet))) {
                        addMobile(mobile, alertDialog);
                    } else {
                        Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
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

    private void addMobile(final String mobile, final AlertDialog alertDialog) {
        task = getString(R.string.add_phone);
        userId = AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(TwoStepVerificationInfo.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(TwoStepVerificationInfo.this, AppUtils.DOMAIN);

        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.addPhone(version, key, task, userId, accessToken, mobile);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {


            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.phone_number_added_successfully), Toast.LENGTH_SHORT).show();
//                    textViewAddedPhone.setVisibility(View.VISIBLE);
                    otpMobileFetch.setAumvsid("1");
                    otpMobileFetch.setMobile(mobile);
                    otpMobileFetch.setPrimaryNo("0");
                    otpMobileFetch.setAumvid(apiResponse.getData().getAumvid());
                    otpMobileFetch.setUid(AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID));
                    databaseHandler.insertTwoStepVerificationMobiles(otpMobileFetch);
                    displayDataInView();
                    textViewAddedPhone.setText(mobile);
                    progressBar.setVisibility(View.GONE);
                    flagAddMobileToVerify = true;
                    sendOTPToVerify(apiResponse.getData().getAumvid());
                    alertDialog.dismiss();
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    private void dialogAddEmail() {

        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_two_step_verification_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextEmail;

        editTextEmail = (EditText) dialogView.findViewById(R.id.email);

        final Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAdd.setEnabled(false);
                email = editTextEmail.getText().toString();
                if (!emailValidator.validateEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.valid_email_error));
                } else if (databaseHandler.checkEmailAddress(email)) {
                    Toast.makeText(TwoStepVerificationInfo.this, "This email address is already added.", Toast.LENGTH_SHORT).show();
                } else {
                    addEmail(email, alertDialog);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonAdd.setEnabled(true);
                    }
                }, 3000);
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

    @Override
    public void updatedEmail(int position, String auevId, String emailAddress) {
        emailLists.clear();
        if (databaseHandler != null) {
            //Adding User's Primary Email
            OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
            otpEmailFetch.setEmail(AppPreferences.getEmail(TwoStepVerificationInfo.this, AppUtils.USER_EMAIL));
            emailLists.add(otpEmailFetch);

            emailLists.addAll(databaseHandler.getAllTwoStepVerificationEmails());
            setEmailPrimary();
        }
        emailAdapter.notifyDataSetChanged();

        if (emailLists != null && emailLists.size() > 0) {
            textViewEmailTitle.setVisibility(View.VISIBLE);
            recyclerViewEmails.setVisibility(View.VISIBLE);
            viewEmails.setVisibility(View.VISIBLE);
        } else {
            textViewEmailTitle.setVisibility(View.GONE);
            recyclerViewEmails.setVisibility(View.GONE);
            viewEmails.setVisibility(View.GONE);
        }
        flagAddMobileToVerify = false;

        otpEmailFetch = null;
        otpEmailFetch = new OtpEmailFetch();
        otpEmailFetch.setAuevsid("1");
        otpEmailFetch.setEmail(emailAddress);
        otpEmailFetch.setPrimaryEmail("0");
        otpEmailFetch.setAuevid(auevId);
        otpEmailFetch.setUid(AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID));

        sendOTPToVerify(auevId);
    }

    private void addEmail(final String email, final AlertDialog alertDialog) {

        task = getString(R.string.add_email);
        userId = AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(TwoStepVerificationInfo.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(TwoStepVerificationInfo.this, AppUtils.DOMAIN);
        progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.addEmail(version, key, task, userId, accessToken, email);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.email_added_successfully), Toast.LENGTH_SHORT).show();
//                    textViewAddedEmail.setVisibility(View.VISIBLE);
                    otpEmailFetch.setAuevsid("1");
                    otpEmailFetch.setEmail(email);
                    otpEmailFetch.setPrimaryEmail("0");
                    otpEmailFetch.setAuevid(apiResponse.getData().getAuevid());
                    otpEmailFetch.setUid(AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID));
                    databaseHandler.insertTwoStepVerificationEmails(otpEmailFetch);
                    displayDataInView();
                    alertDialog.dismiss();
                    textViewAddedEmail.setText(email);
                    sendOTPToVerify(apiResponse.getData().getAuevid());
                    progressBar.setVisibility(View.GONE);
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    progressBar.setVisibility(View.GONE);
                    Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    private void sendTwoStepVerificationStatus(final String status) {
        task = getString(R.string.send_two_step_verification_status);
        userId = AppPreferences.getUserId(TwoStepVerificationInfo.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(TwoStepVerificationInfo.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(TwoStepVerificationInfo.this, AppUtils.DOMAIN);
        //progressBar.setVisibility(View.VISIBLE);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.sendTwoStepVerificationStatus(version, key, task, userId, accessToken, status);
        //    Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.two_step_verification_enabled), Toast.LENGTH_SHORT).show();
                }                 //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //  displayDataInView();
    }

    @Override
    public void onMessageRowClicked(int position) {
    }

    @Override
    public void updateRow(int position) {
        emailLists.clear();
        if (databaseHandler != null) {
            //Adding User's Primary Email
            OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
            otpEmailFetch.setEmail(AppPreferences.getEmail(TwoStepVerificationInfo.this, AppUtils.USER_EMAIL));
            emailLists.add(otpEmailFetch);

            emailLists.addAll(databaseHandler.getAllTwoStepVerificationEmails());
            setEmailPrimary();
        }
        emailAdapter.notifyDataSetChanged();

        if (emailLists != null && emailLists.size() > 0) {
            textViewEmailTitle.setVisibility(View.VISIBLE);
            recyclerViewEmails.setVisibility(View.VISIBLE);
            viewEmails.setVisibility(View.VISIBLE);
        } else {
            textViewEmailTitle.setVisibility(View.GONE);
            recyclerViewEmails.setVisibility(View.GONE);
            viewEmails.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateMobileRow(int position) {
        phoneLists.clear();
        if (databaseHandler != null) {
            //Add User's Primary Mobile
            OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
            otpMobileFetch.setMobile(AppPreferences.getUserMobile(TwoStepVerificationInfo.this, AppUtils.USER_MOBILE));
            phoneLists.add(otpMobileFetch);

            phoneLists.addAll(databaseHandler.getAllTwoStepVerificationMobiles());
            setPhonePrimary();
        }
        mobileAdapter.notifyDataSetChanged();

        if (phoneLists != null && phoneLists.size() > 0) {
            recyclerViewPhones.setVisibility(View.VISIBLE);
            textViewMobileTitle.setVisibility(View.VISIBLE);
            viewMobiles.setVisibility(View.VISIBLE);
        } else {
            recyclerViewPhones.setVisibility(View.GONE);
            textViewMobileTitle.setVisibility(View.GONE);
            viewMobiles.setVisibility(View.GONE);
        }


    }

    public void sendOTPToVerify(final String verifyId) {
        task = getString(R.string.send_mobile_otp);
        userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call;
        if (flagAddMobileToVerify) {
            call = apiService.sendMobileOTPToVerifyMobile(version, key, task, accessToken, userId, "1", "2", verifyId);
        } else {
            call = apiService.sendMobileOTPToVerifyEmail(version, key, task, accessToken, userId, "1", "1", verifyId);

        }
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Toast.makeText(getApplicationContext(), getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
//                    alertDialog.dismiss();
                    //Redirect to Main Activity
                    openMobileDialog(verifyId);
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    public void openMobileDialog(final String verifyId) {
        View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_verify, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final TextView titleTextView;
        final EditText editTextOtp;
        titleTextView = (TextView) dialogView.findViewById(R.id.text_view_title);
        editTextOtp = (EditText) dialogView.findViewById(R.id.edit_text_otp);
        editTextOtp.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        if (flagAddMobileToVerify) {
            titleTextView.setText(getString(R.string.title_verify_phone));
        } else {
            titleTextView.setText(getString(R.string.title_verify_email));
        }

        final Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_verify);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        buttonCancel.setVisibility(View.GONE);

        final TextView textViewResend = (TextView) dialogView.findViewById(R.id.btn_resend);
        textViewResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewResend.setEnabled(false);
                alertDialog.dismiss();
                if (!NetworkUtil.getConnectivityStatusString(TwoStepVerificationInfo.this).equals(getString(R.string.not_connected_to_internet))) {
                    sendOTPToVerify(verifyId);
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        textViewResend.setEnabled(true);
                    }
                }, 3000);
            }
        });
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonAdd.setEnabled(false);
                if (editTextOtp.getText().toString().trim().length() == 0) {
                    Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.title_verify_phone), Toast.LENGTH_SHORT).show();
                } else {
                    if (!NetworkUtil.getConnectivityStatusString(TwoStepVerificationInfo.this).equals(getString(R.string.not_connected_to_internet))) {
                        verifyOTP(verifyId, editTextOtp.getText().toString().trim(), alertDialog);
                    } else {
                        Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        buttonAdd.setEnabled(true);
                    }
                }, 3000);
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

    private void verifyOTP(final String verifyId, String mobileOtp, final AlertDialog alertDialog) {
        task = getString(R.string.otp_verify_confirm);
        userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call;
        if (flagAddMobileToVerify) {
            call = apiService.confirmVerifyPhoneOTP(version, key, task, accessToken, userId, "1", "2", verifyId, mobileOtp);
        } else {
            call = apiService.confirmVerifyEmailOTP(version, key, task, accessToken, userId, "1", "1", verifyId, mobileOtp);

        }
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {

                    if (flagAddMobileToVerify) {
                        flagAddMobileToVerify = false;
                        otpMobileFetch.setAumvsid("2");
                        otpMobileFetch.setAumvid(verifyId);
                        databaseHandler.updateVerifyStatusMobile(otpMobileFetch);
                        phoneLists.clear();
                        if (databaseHandler != null) {
                            //Add User's Primary Mobile
                            OtpMobileFetch otpMobileFetch = new OtpMobileFetch();
                            otpMobileFetch.setMobile(AppPreferences.getUserMobile(TwoStepVerificationInfo.this, AppUtils.USER_MOBILE));
                            phoneLists.add(otpMobileFetch);
                            phoneLists.addAll(databaseHandler.getAllTwoStepVerificationMobiles());
                            setPhonePrimary();
                        }
                        mobileAdapter.notifyDataSetChanged();
                    } else {
                        otpEmailFetch.setAuevsid("2");
                        otpMobileFetch.setAumvid("1");
                        databaseHandler.updateEmailVerifyStatus(otpEmailFetch);
                        emailLists.clear();
                        if (databaseHandler != null) {
                            //Adding User's Primary Email
                            OtpEmailFetch otpEmailFetch = new OtpEmailFetch();
                            otpEmailFetch.setEmail(AppPreferences.getEmail(TwoStepVerificationInfo.this, AppUtils.USER_EMAIL));
                            emailLists.add(otpEmailFetch);

                            emailLists.addAll(databaseHandler.getAllTwoStepVerificationEmails());
                            setEmailPrimary();
                        }
                        emailAdapter.notifyDataSetChanged();

                    }

                    alertDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.otp_verified), Toast.LENGTH_SHORT).show();


                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setCallBack();
    }

    public void setCallBack() {
        Intent intentUpdate = new Intent(this, SettingsActivity.class);
        intentUpdate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intentUpdate, 1000);
        Intent resultIntent = new Intent();
        setResult(1000, resultIntent);
        finish();
    }

    public void switchChangeListener() {
        switchToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    isTwoStepVerificationEnabled = true;
                    textViewFabAddMobile.setVisibility(View.VISIBLE);
                    textViewFabAddEmail.setVisibility(View.VISIBLE);
                    floatingActionButtonEmail.setVisibility(View.VISIBLE);
                    floatingActionButtonMobile.setVisibility(View.VISIBLE);
                    floatingActionButtonAdd.setVisibility(View.VISIBLE);
                    sendTwoStepVerificationStatus("1");
                    AppPreferences.setTwoStepVerificationStatus(TwoStepVerificationInfo.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, String.valueOf(isTwoStepVerificationEnabled));

                    if (phoneLists != null && phoneLists.size() > 0) {
                        recyclerViewPhones.setVisibility(View.VISIBLE);
                        textViewMobileTitle.setVisibility(View.VISIBLE);
                        viewMobiles.setVisibility(View.VISIBLE);
                    } else {
                        recyclerViewPhones.setVisibility(View.GONE);
                        textViewMobileTitle.setVisibility(View.GONE);
                        viewMobiles.setVisibility(View.GONE);
                    }

                    if (emailLists != null && emailLists.size() > 0) {
                        textViewEmailTitle.setVisibility(View.VISIBLE);
                        recyclerViewEmails.setVisibility(View.VISIBLE);
                        viewEmails.setVisibility(View.VISIBLE);
                    } else {
                        textViewEmailTitle.setVisibility(View.GONE);
                        recyclerViewEmails.setVisibility(View.GONE);
                        viewEmails.setVisibility(View.GONE);
                    }
                } else {
                    isTwoStepVerificationEnabled = false;
                    sendTwoStepVerificationStatus("2");
                    AppPreferences.setTwoStepVerificationStatus(TwoStepVerificationInfo.this, AppUtils.TWO_STEP_VERIFICATION_STATUS, String.valueOf(isTwoStepVerificationEnabled));
                    textViewAddedEmail.setVisibility(View.GONE);
                    textViewAddedPhone.setVisibility(View.GONE);
                    recyclerViewEmails.setVisibility(View.GONE);
                    recyclerViewPhones.setVisibility(View.GONE);
                    textViewEmailTitle.setVisibility(View.GONE);
                    textViewMobileTitle.setVisibility(View.GONE);
                    viewEmails.setVisibility(View.GONE);
                    viewMobiles.setVisibility(View.GONE);
                    textViewFabAddEmail.setVisibility(View.GONE);
                    textViewFabAddMobile.setVisibility(View.GONE);
                    floatingActionButtonEmail.setVisibility(View.GONE);
                    floatingActionButtonMobile.setVisibility(View.GONE);
                    floatingActionButtonAdd.setVisibility(View.GONE);
                }
            }
        });
    }

    private void updateEmails(Activity activity) {
        task = getString(R.string.fetch_email_addresses);
        if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        if (apiResponse != null && apiResponse.getData().getOtpEmail() != null) {
                            //clear table and inserting new emails
                            databaseHandler.clearEmailTable();
                            for (final OtpEmailFetch otpEmailFetch : apiResponse.getData().getOtpEmail()) {
                                if (!databaseHandler.checkEmailAddress(otpEmailFetch.getEmail())) {
                                    databaseHandler.insertTwoStepVerificationEmails(otpEmailFetch);
                                }

                            }
                        }

                        //Refresh Mobiles
                        if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                            if (TwoStepVerificationInfo.this != null) {
                                updateMobiles(TwoStepVerificationInfo.this);
                            }
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateMobiles(Activity activity) {
        task = getString(R.string.fetch_mobile_numbers);
        if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {
                        if (apiResponse != null && apiResponse.getData().getOtpMobile() != null) {
                            //clear table and inserting new mobile numbers
                            databaseHandler.clearMobilesTable();
                            for (final OtpMobileFetch otpMobileFetch : apiResponse.getData().getOtpMobile()) {
                                if (!databaseHandler.checkMobileNumber(otpMobileFetch.getMobile())) {
                                    databaseHandler.insertTwoStepVerificationMobiles(otpMobileFetch);
                                }

                            }
                        }

                        //Display data from DB
                        displayDataInView();

                        //Switch Key Listener
                        switchChangeListener();

                    }

                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(TwoStepVerificationInfo.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(TwoStepVerificationInfo.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(TwoStepVerificationInfo.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

}


