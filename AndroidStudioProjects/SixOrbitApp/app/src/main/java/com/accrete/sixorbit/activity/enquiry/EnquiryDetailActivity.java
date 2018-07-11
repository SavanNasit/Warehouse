package com.accrete.sixorbit.activity.enquiry;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.order.EditOrderActivity;
import com.accrete.sixorbit.fragment.Drawer.DatePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.TimePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.enquiry.EnquiryDetailsMainTabFragment;
import com.accrete.sixorbit.fragment.Drawer.followups.FollowUpsFragment;
import com.accrete.sixorbit.fragment.Drawer.orders.OrderDetailsMainTabFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.interfaces.PassMobileListener;
import com.accrete.sixorbit.interfaces.PassSharingTextListener;
import com.accrete.sixorbit.interfaces.PassUsersEmailListener;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.service.ContactPersonsAPI;
import com.accrete.sixorbit.service.PushNotificationsTimeService;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by {Anshul} on 28/6/18.
 */

public class EnquiryDetailActivity extends AppCompatActivity implements View.OnClickListener,
        PassMobileListener, PassUsersEmailListener, PassDateToCounsellor,
        PassSharingTextListener {
    public List<Lead> leadList = new ArrayList<>();
    private AppBarLayout htabAppbar;
    private CollapsingToolbarLayout htabCollapseToolbar;
    private Toolbar toolbar;
    private ImageView imageViewInfoEdit;
    private FrameLayout container;
    private Toolbar toolbarBottom;
    private LinearLayout layoutIdFollowUp;
    private TextView textviewAddFollowUps;
    private TextView textAddFollowup;
    private LinearLayout layoutIdCall;
    private TextView textviewIconCall;
    private TextView textviewCall;
    private LinearLayout layoutIdShare;
    private TextView textviewShare;
    private TextView textShare;
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();
    private DatabaseHandler databaseHandler;
    private String enId, mobileNumber, emailAddress, strCommunicatedModeId, orderSId, strFromTime,
            strFollowUpAssignee, strToTime, strContactPerson, strScheduleDate, strAlertTime,
            strAlertModeChecked, strComMode, cuId;
    private String sharingTexts;
    private AutoCompleteTextView contactPersonAutocomplete, addLeadCommunicationModeAutoComplete,
            addLeadAssignFollowup;
    private AlertDialog alertDialog;
    private EditText addLeadScheduledTime, addLeadAlertTime;
    private CheckBox addLeadSendMail, addLeadSendSms;
    private ProgressBar progressBar;
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private List<String> followupAssigneecontacts = new ArrayList<>();
    private Date startDate, enddate;
    private LinkedHashMap<String, String> hashMapFollowUpAssigneecontacts = new LinkedHashMap<>();
    private FollowUp singleFollowUp = new FollowUp();
    private int progressStatus = 0;
    private List<Contacts> leadContactsPersonList = new ArrayList<>();

    @Override
    public void passMobileOnLoad(String mobile) {
        mobileNumber = mobile;
    }

    @Override
    public void passUsersEmailOnLoad(String email) {
        emailAddress = email;
    }

    @Override
    public void passSharingTextOnLoad(String str) {
        sharingTexts = str;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quotation_details);

        findViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            if (!NetworkUtil.getConnectivityStatusString(EnquiryDetailActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                refreshFollowUps(EnquiryDetailActivity.this);
            } else {
                updateFollowUps();
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
                    if (currentFragment instanceof OrderDetailsMainTabFragment) {
                        ((OrderDetailsMainTabFragment) currentFragment).viewPager.setCurrentItem(0);
                        ((OrderDetailsMainTabFragment) currentFragment).getOrdersInfo(enId);
                    }
                }
            }, 200);
        } else {
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void findViews() {
        htabAppbar = (AppBarLayout) findViewById(R.id.htab_appbar);
        htabCollapseToolbar = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        imageViewInfoEdit = (ImageView) findViewById(R.id.imageView_info_edit);
        container = (FrameLayout) findViewById(R.id.container);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        layoutIdFollowUp = (LinearLayout) findViewById(R.id.layout_id_followUp);
        textviewAddFollowUps = (TextView) findViewById(R.id.textview_addFollowUps);
        textAddFollowup = (TextView) findViewById(R.id.text_add_followup);
        layoutIdCall = (LinearLayout) findViewById(R.id.layout_id_call);
        textviewIconCall = (TextView) findViewById(R.id.textview_icon_call);
        textviewCall = (TextView) findViewById(R.id.textview_call);
        layoutIdShare = (LinearLayout) findViewById(R.id.layout_id_share);
        textviewShare = (TextView) findViewById(R.id.textview_share);
        textShare = (TextView) findViewById(R.id.text_share);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //TODO - Communication Modes
        if (AppPreferences.getModes(EnquiryDetailActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(EnquiryDetailActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra(getString(R.string.notify_id), -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        if (PushNotificationsTimeService.dictMap.size() <= 1) {
            notificationManager.cancel(1000);
        }
        PushNotificationsTimeService.dictMap.remove(AppPreferences.getNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER));
        notificationManager.cancel(AppPreferences.getNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER));

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.en_id))) {
            enId = getIntent().getStringExtra(getString(R.string.en_id));
        }

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.cuid))) {
            cuId = getIntent().getStringExtra(getString(R.string.cuid));
        }

        databaseHandler = new DatabaseHandler(EnquiryDetailActivity.this);

        if (AppPreferences.getBoolean(EnquiryDetailActivity.this, AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.enquiry_edit_permission))) {
            imageViewInfoEdit.setVisibility(View.VISIBLE);
            imageViewInfoEdit.setEnabled(true);
        } else {
            imageViewInfoEdit.setVisibility(View.GONE);
            imageViewInfoEdit.setEnabled(false);
            imageViewInfoEdit.setFocusable(false);
        }

        imageViewInfoEdit.setVisibility(View.GONE);
        imageViewInfoEdit.setEnabled(false);
        imageViewInfoEdit.setFocusable(false);

        imageViewInfoEdit.setOnClickListener(this);
        layoutIdFollowUp.setOnClickListener(this);
        layoutIdShare.setOnClickListener(this);
        layoutIdCall.setOnClickListener(this);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        textviewAddFollowUps.setTypeface(fontAwesomeFont);
        textviewIconCall.setTypeface(fontAwesomeFont);
        textviewShare.setTypeface(fontAwesomeFont);


        FragmentManager fragmentManager = getSupportFragmentManager();
        EnquiryDetailsMainTabFragment enquiryDetailsMainTabFragment = new EnquiryDetailsMainTabFragment();
        Bundle bundle = new Bundle();
        bundle.putString(getString(R.string.en_id), enId);
        // bundle.putString(getString(R.string.cuid), cuId);

        enquiryDetailsMainTabFragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, enquiryDetailsMainTabFragment);
        fragmentTransaction.commit();

        if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            ContactPersonsAPI contactPersonsAPI = new ContactPersonsAPI(EnquiryDetailActivity.this);
            contactPersonsAPI.getCustomersContactPersons(EnquiryDetailActivity.this, "",
                    "", enId, "", "", "", "");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.imageView_info_edit:
                    if (AppPreferences.getBoolean(EnquiryDetailActivity.this, AppUtils.ISADMIN) ||
                            databaseHandler.checkUsersPermission(getString(R.string.order_edit_permission))) {
                        Intent intent = new Intent(EnquiryDetailActivity.this, EditOrderActivity.class);
                        intent.putExtra(getString(R.string.order_id), enId);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EnquiryDetailActivity.this, "You have no permission to edit any order.",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.layout_id_followUp:
                    String enSId = ((EnquiryDetailsMainTabFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.container)).customerData.getEnsid();

                    if (!AppPreferences.getBoolean(EnquiryDetailActivity.this, AppUtils.ISADMIN) &&
                            !databaseHandler.checkUsersPermission(getString(R.string.followup_schedule_permission))) {
                        Toast.makeText(EnquiryDetailActivity.this, "You have no permission to add any follow up.", Toast.LENGTH_SHORT).show();
                    } else if (enSId != null && !enSId.isEmpty() && enSId.equals("5")) {
                        Toast.makeText(EnquiryDetailActivity.this,
                                "Sorry, this order is on hold so you can't add follow up.",
                                Toast.LENGTH_SHORT).show();
                    } else if (enSId != null && !enSId.isEmpty() && enSId.equals("1")) {
                        Toast.makeText(EnquiryDetailActivity.this,
                                "Sorry, this order is converted so you can't add follow up.",
                                Toast.LENGTH_SHORT).show();
                    } else if (enSId != null && !enSId.isEmpty() && enSId.equals("3")) {
                        Toast.makeText(EnquiryDetailActivity.this,
                                "Sorry, this order is cancelled so you can't add follow up.",
                                Toast.LENGTH_SHORT).show();
                    } else if (enId != null && !enId.isEmpty() && !enId.equals("null")) {
                        if (alertDialog == null || !alertDialog.isShowing()) {
                            dialogAddFollowUp();
                        }
                    } else {
                        Toast.makeText(EnquiryDetailActivity.this, "Quotation is not updated on server. Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }

                    break;
                case R.id.layout_id_call:
                    layoutIdCall.setEnabled(false);
                    if (mobileNumber != null && !mobileNumber.isEmpty()) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            callIntent();
                        } else {
                            callAction();
                        }
                    } else {
                        Toast.makeText(EnquiryDetailActivity.this, "This customer has no mobile number.", Toast.LENGTH_SHORT).show();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutIdCall.setEnabled(true);
                        }
                    }, 3000);

                    break;
                case R.id.layout_id_share:
                    layoutIdShare.setEnabled(false);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer's info");
                    if (sharingTexts != null) {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "" + sharingTexts.toString());
                    } else {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
                    }
                    startActivity(Intent.createChooser(shareIntent, "Share"));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            layoutIdShare.setEnabled(true);
                        }
                    }, 3000);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callIntent() {
        if (checkPermissionWithRationale(this, new FollowUpsFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }

    }

    @SuppressLint("MissingPermission")
    private void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber == null || mobileNumber == "") {
            Toast.makeText(getApplicationContext(), "No Number", Toast.LENGTH_SHORT).show();
        } else {
            startActivity(intentCall);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
//            Request code for handling the call action for lead info contacts
            case REQUEST_CODE_ASK_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callAction();

                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

                            //Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
            //            Request code for handling the call action for followup contact in lead info.

            case REQUEST_CODE_FOR_FOLLOW_UP_INFO_CALL_PERMISSIONS: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize  with both permissions
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results nameTextView user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callAction();
                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            //Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
        }
    }

    // Permission settings page request method
    public void askUserToAllowPermissionFromSetting() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(getString(R.string.permission_required));

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
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void getDataFromDB() {
        if (databaseHandler != null && databaseHandler.getAllAssignee().size() != 0) {
            List<ChatContacts> contacts2 = databaseHandler.getAllAssignee();
            for (ChatContacts cn : contacts2) {
                hashMapFollowUpAssigneecontacts.put(cn.getName(), String.valueOf(cn.getUid()));
                // Writing Contacts to log
            }
        }
    }

    private void dialogAddFollowUp() {
        try {
            View dialogView = View.inflate(EnquiryDetailActivity.this, R.layout.dialog_add_follow_up, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView).setCancelable(true);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(true);
            final ArrayList<Integer> listAlertModeChecked = new ArrayList<>();
            LinearLayout addLeadTitle;
            LinearLayout addLeadFollowUpDetails;

            final Button btnAdd;
            Button btnCancel;

            addLeadTitle = (LinearLayout) dialogView.findViewById(R.id.add_follow_up_title);
            addLeadFollowUpDetails = (LinearLayout) dialogView.findViewById(R.id.add_follow_up_details);

            contactPersonAutocomplete = (AutoCompleteTextView) dialogView.findViewById(R.id.add_follow_up_contact_person);
            addLeadCommunicationModeAutoComplete = (AutoCompleteTextView) dialogView.findViewById(R.id.add_follow_up_communication_mode);
            addLeadAssignFollowup = (AutoCompleteTextView) dialogView.findViewById(R.id.add_follow_up_assign_followup);

            addLeadScheduledTime = (EditText) dialogView.findViewById(R.id.add_follow_up_scheduled_time);
            addLeadAlertTime = (EditText) dialogView.findViewById(R.id.add_follow_up_alert_time);
            addLeadSendMail = (CheckBox) dialogView.findViewById(R.id.add_follow_up_send_mail);
            addLeadSendSms = (CheckBox) dialogView.findViewById(R.id.add_follow_up_send_sms);
            btnAdd = (Button) dialogView.findViewById(R.id.add_follow_up_btn_add);
            btnCancel = (Button) dialogView.findViewById(R.id.add_follow_up_btn_cancel);
            progressBar = (ProgressBar) dialogView.findViewById(R.id.add_follow_up_progress_bar);

            addLeadCommunicationModeAutoComplete.setThreshold(1);

            contactPersonAutocomplete.setFocusable(false);
            addLeadCommunicationModeAutoComplete.setFocusable(false);

            getDataFromDB();
            contactPersonAdapter();
            followupAssigneeAdapter();
            communicationModeAdapter();

            contactPersonAutocomplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactPersonAutocomplete.showDropDown();
                }
            });

            contactPersonAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    strContactPerson = leadContactsPersonList.get(i).getCodeid();
                }
            });

            addLeadCommunicationModeAutoComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLeadCommunicationModeAutoComplete.showDropDown();
                }
            });

            addLeadAssignFollowup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addLeadAssignFollowup.showDropDown();
                }
            });

            addLeadSendMail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        listAlertModeChecked.add(1);
                    } else {
                        if (listAlertModeChecked.size() == 1) {
                            listAlertModeChecked.remove(0);
                            addLeadSendMail.setChecked(false);
                        } else if (listAlertModeChecked.size() == 2) {
                            listAlertModeChecked.remove(1);
                            addLeadSendMail.setChecked(false);
                        } else {
                            listAlertModeChecked.clear();
                            addLeadSendMail.setChecked(false);
                        }
                    }
                }
            });


            addLeadSendSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listAlertModeChecked.add(2);
                    } else {
                        if (listAlertModeChecked.size() == 1) {
                            listAlertModeChecked.remove(0);
                            addLeadSendSms.setChecked(false);
                        } else if (listAlertModeChecked.size() == 2) {
                            listAlertModeChecked.remove(1);
                            addLeadSendSms.setChecked(false);
                        } else {
                            listAlertModeChecked.clear();
                            addLeadSendSms.setChecked(false);
                        }
                    }
                }
            });

            addLeadAssignFollowup.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strFollowUpAssignee = addLeadAssignFollowup.getText().toString();
                        for (int i = 0; i < followupAssigneecontacts.size(); i++) {
                            String temp = followupAssigneecontacts.get(i);
                            if (strFollowUpAssignee.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        addLeadAssignFollowup.setText("");
                    }
                }
            });
            addLeadAssignFollowup.setThreshold(1);

            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setListener(EnquiryDetailActivity.this);
            timePickerFragment = new TimePickerFragment();
            timePickerFragment.setListener(EnquiryDetailActivity.this);
            addLeadScheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_from));
                }
            });

            addLeadAlertTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Added in June
                    try {
                        if (addLeadScheduledTime.getText().toString() == null ||
                                addLeadScheduledTime.getText().toString().isEmpty()) {
                            datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                        } else {

                            String[] strDate = addLeadScheduledTime.getText().toString().split("\\s+");
                            String strYear = strDate[0].toString().substring(strDate[0].lastIndexOf("-") + 1,
                                    strDate[0].toString().length());
                            String strDayMonth = strDate[0].toString().substring(0, strDate[0].lastIndexOf("-"));
                            String strDay = strDayMonth.toString().substring(0, strDayMonth.lastIndexOf("-"));
                            String strMonth = strDayMonth.toString().substring(strDayMonth.lastIndexOf("-") + 1,
                                    strDayMonth.length());

                            /*//Calculating Time
                            String strSeconds = strDate[1].toString().substring(strDate[1].lastIndexOf(":") + 1,
                                    strDate[1].toString().length());
                            String strHourMinute = strDate[1].toString().substring(0, strDate[1].lastIndexOf(":"));
                            String strHour = strHourMinute.toString().substring(0, strHourMinute.lastIndexOf(":"));
                            String strMinute = strHourMinute.toString().substring(strHourMinute.lastIndexOf(":") + 1,
                                    strHourMinute.length());*/

                            Bundle bundle = new Bundle();

                            bundle.putString("day", strDay.toString());
                            bundle.putString("month", strMonth.toString());
                            bundle.putString("year", strYear.toString());
                            datePickerFragment.setArguments(bundle);
                            datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                }
            });


            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    btnAdd.setEnabled(false);
                    if (addLeadCommunicationModeAutoComplete.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(EnquiryDetailActivity.this, "Please select a communication mode").show();
                    } else if (addLeadAssignFollowup.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(EnquiryDetailActivity.this, "Please select assignee").show();
                    } else if (addLeadScheduledTime.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(EnquiryDetailActivity.this, "Please select scheduled date & time.").show();
                    } else if (addLeadAlertTime.getText().toString().trim().length() == 0) {
                        CustomisedToast.error(EnquiryDetailActivity.this, "Please select alert date & time.").show();
                    } else {
                        setDataInStrings();
                        dateValidation();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            btnAdd.setEnabled(true);
                        }
                    }, 3000);

                }
            });

            addLeadCommunicationModeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FollowupCommunicationMode selected = (FollowupCommunicationMode) parent.getAdapter().getItem(position);

                    //Get ID
                    strCommunicatedModeId = selected.getCommid();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contactPersonAdapter() {
        if (leadContactsPersonList.size() > 0) {
            leadContactsPersonList.clear();
        }
        Contacts contacts = new Contacts();
        contacts.setName(getString(R.string.nothing_selected));
        leadContactsPersonList.add(0, contacts);

        if (cuId != null) {
            leadContactsPersonList.addAll(databaseHandler.getCustomersContactPersonsList(cuId));
        }
        ArrayAdapter arrayAdapterContactPerson = new ArrayAdapter(this,
                R.layout.simple_spinner_item, leadContactsPersonList);
        arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contactPersonAutocomplete.setAdapter(arrayAdapterContactPerson);
    }

    private void followupAssigneeAdapter() {
        for (String key : hashMapFollowUpAssigneecontacts.keySet()) {
            followupAssigneecontacts.add(key);
        }
        ArrayAdapter arrayAdapterFollowUpAssignee = new ArrayAdapter(this,
                R.layout.simple_spinner_item, followupAssigneecontacts);
        arrayAdapterFollowUpAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addLeadAssignFollowup.setAdapter(arrayAdapterFollowUpAssignee);
    }

    private void communicationModeAdapter() {
        ArrayAdapter arrayAdapterContactPerson = new ArrayAdapter(this,
                R.layout.simple_spinner_item, communicationModeList);
        arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addLeadCommunicationModeAutoComplete.setAdapter(arrayAdapterContactPerson);
    }

    @Override
    public void passDate(String s) {
        String stringStartDate = null, stringEndDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (datePickerFragment.getTag().equals(getString(R.string.dailogue_from))) {
            try {
                stringStartDate = s;
                startDate = formatter.parse(stringStartDate);
                stringStartDate = targetFormat.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLeadScheduledTime.setText(s);
            timePickerFragment.show(getSupportFragmentManager(), getString(R.string.taken_from));

        } else {
            try {
                stringEndDate = s;
                enddate = formatter.parse(stringEndDate);
                stringEndDate = targetFormat.format(enddate);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLeadAlertTime.setText(s);

            //TODO Added in June
            try {
                if (addLeadScheduledTime.getText().toString() == null ||
                        addLeadScheduledTime.getText().toString().isEmpty()) {
                    timePickerFragment.show(getSupportFragmentManager(), getString(R.string.taken_to));
                } else {

                    //Calculating Time
                    String[] strDate = addLeadScheduledTime.getText().toString().split("\\s+");
                    String strSeconds = strDate[1].toString().substring(strDate[1].lastIndexOf(":") + 1,
                            strDate[1].toString().length());
                    String strHourMinute = strDate[1].toString().substring(0, strDate[1].lastIndexOf(":"));
                    String strHour = strHourMinute.toString().substring(0, strHourMinute.lastIndexOf(":"));
                    String strMinute = strHourMinute.toString().substring(strHourMinute.lastIndexOf(":") + 1,
                            strHourMinute.length());
                    Bundle bundle = new Bundle();
                    bundle.putString("hour", strHour.toString());
                    bundle.putString("minute", strMinute.toString());
                    timePickerFragment.setArguments(bundle);
                    timePickerFragment.show(getSupportFragmentManager(), getString(R.string.taken_to));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //timePickerFragment.show(getSupportFragmentManager(), getString(R.string.taken_to));
        }
    }

    @Override
    public void passTime(String s) {
        String stringStartTime = null, stringEndTime = null;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        DateFormat targetFormat = new SimpleDateFormat("HH:mm:ss");
        if (timePickerFragment.getTag().equals(getString(R.string.taken_from))) {
            try {
                Date startDate = formatter.parse(s);
                System.out.println(startDate);
                stringStartTime = targetFormat.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLeadScheduledTime.setText(addLeadScheduledTime.getText().toString() + " " + stringStartTime);
            strFromTime = stringStartTime;
        } else {
            try {
                Date endDate = formatter.parse(s);
                System.out.println(endDate);
                stringEndTime = targetFormat.format(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLeadAlertTime.setText(addLeadAlertTime.getText().toString() + " " + stringEndTime);
            strToTime = stringEndTime;
        }
    }

    private void setDataInStrings() {

        strFollowUpAssignee = addLeadAssignFollowup.getText().toString();
        if (hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee) != null) {
            strFollowUpAssignee = hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee);
        }

        //strContactPerson = contactPersonAutocomplete.getText().toString();
        if (contactPersonAutocomplete.getText().toString().equals(getString(R.string.nothing_selected))) {
            strContactPerson = "";
        }

        Log.d("follow up assignee1 ", strFollowUpAssignee);
        if (addLeadSendMail.isChecked() && !addLeadSendSms.isChecked()) {
            strAlertModeChecked = "[1]";
        } else if (!addLeadSendMail.isChecked() && addLeadSendSms.isChecked()) {
            strAlertModeChecked = "[2]";
        } else if (addLeadSendMail.isChecked() && addLeadSendSms.isChecked()) {
            strAlertModeChecked = "[1,2]";
        } else {
            strAlertModeChecked = "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date startDate = formatter.parse(addLeadScheduledTime.getText().toString());
            Date endDate = formatter.parse(addLeadAlertTime.getText().toString());
            strScheduleDate = targetFormat.format(startDate);
            strAlertTime = targetFormat.format(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (strCommunicatedModeId != null) {
            strComMode = communicationModeList.get(Integer.parseInt(strCommunicatedModeId) - 1).getName();
        }
    }

    private void dateValidation() {
        Date startDate = null, endDate = null, startTime = null, endTime = null;
        SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = formatterDate.parse(strScheduleDate);
            endDate = formatterDate.parse(strAlertTime);
            startTime = formatterTime.parse(strScheduleDate);
            endTime = formatterTime.parse(strAlertTime);
            Calendar c = Calendar.getInstance();
            String stringCurrentDate = formatterTime.format(c.getTime());
            Date currentDate = formatterDate.parse(stringCurrentDate);
            Date currentTime = formatterTime.parse(stringCurrentDate);
            System.out.println(startDate + " " + endDate);
            System.out.println(formatterDate.format(startDate));
            if (strCommunicatedModeId == null || strCommunicatedModeId.isEmpty()) {
                CustomisedToast.error(getApplicationContext(), "Please select a communication mode").show();
            } else if (strFollowUpAssignee == null || strFollowUpAssignee.isEmpty()) {
                CustomisedToast.error(getApplicationContext(), "Please select assignee").show();
            } else if (strScheduleDate != null && !strScheduleDate.matches("") && !strScheduleDate.matches("null null") && strAlertTime != null && !strAlertTime.matches("")
                    && !strAlertTime.matches("null null")) {
                if (startTime != null && endTime != null && startTime.before(endTime)) {
                    CustomisedToast.error(this, getString(R.string.scheduletime_more_than_alerttime)).show();
                } else if (new Date().compareTo(startTime) >= 0) {
                    CustomisedToast.error(this, getString(R.string.scheduletime_more_than_currenttime)).show();
                } else if (new Date().compareTo(endTime) >= 0) {
                    CustomisedToast.error(this, getString(R.string.alerttime_more_than_currenttime)).show();
                } else {

                    //TODO - We need to post data to server and insert into local DB as well
                    sendData();
                }
            } else {
                CustomisedToast.error(getApplicationContext(), getString(R.string.schedule_and_alert_time_cant_be_empty)).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
            CustomisedToast.error(getApplicationContext(), getString(R.string.select_date_and_time_properly)).show();
        } catch (Exception e) {
            e.printStackTrace();
            CustomisedToast.error(getApplicationContext(), getString(R.string.select_date_and_time_properly)).show();
        }

    }

    private void sendData() {
        long time_nano = System.nanoTime();
        long micro_seconds = time_nano / 1000;
        if (contactPersonAutocomplete.getText().toString().equals("Nothing Selected")) {
            //  singleFollowUp.setContactPerson(name);
        } else {
            singleFollowUp.setContactPerson(contactPersonAutocomplete.getText().toString());
        }
        singleFollowUp.setCommid(strCommunicatedModeId);
        singleFollowUp.setFollowupCommunicationMode(strComMode);
        singleFollowUp.setFosid("1");
        singleFollowUp.setAlertOn(strAlertTime);
        singleFollowUp.setScheduledDate(strScheduleDate);
        singleFollowUp.setCodeid("");
        singleFollowUp.setSyncId(String.valueOf(micro_seconds));
        //singleFollowUp.setLeadId(strLeadId);
        singleFollowUp.setEnid(enId);
        singleFollowUp.setAssignedUid(strFollowUpAssignee);
        for (Map.Entry<String, String> e : hashMapFollowUpAssigneecontacts.entrySet()) {
            if (e.getValue().equals(strFollowUpAssignee)) {
                singleFollowUp.setAssigned_user(e.getKey());
            }
        }
        //singleFollowUp.setContactPersonEmail(leadLists.getEmail());
        //singleFollowUp.setContactPersonMobile(leadLists.getMobile());
        singleFollowUp.setFollowupTypeStatus(getString(R.string.followup_status_progress));
        singleFollowUp.setCreatedUser(AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_NAME));
        // singleFollowUp.setLeadIdR(strLeadNr);
        if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            singleFollowUp.setSyncStatus(String.valueOf(false));
            databaseHandler.insertFollowUpData(singleFollowUp);
            alertDialog.dismiss();
        } else {
            addFollowUp();
        }
    }

    private void addFollowUp() {

        try {
            task = getString(R.string.follow_up_add);

            if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
            }

            progressBar.setMax(100);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(progressStatus);

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.addEnquiryFollowUp(version, key, task, userId, accessToken, enId,
                    strContactPerson, strCommunicatedModeId, strFollowUpAssignee, strScheduleDate,
                    strAlertTime, strAlertModeChecked, singleFollowUp.getSyncId(), "0");
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse != null && apiResponse.getSuccess() != null) {
                        if (apiResponse.getSuccess()) {

                            //Refresh Followups
                            if (!NetworkUtil.getConnectivityStatusString(EnquiryDetailActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                                refreshFollowUps(EnquiryDetailActivity.this);
                            }

                            progressBar.setVisibility(View.INVISIBLE);
                            singleFollowUp.setFoid(apiResponse.getData().getCurrentFollowUpId());
                            singleFollowUp.setSyncStatus(String.valueOf(true));
                            databaseHandler.insertFollowUpData(singleFollowUp);
                            //databaseHandler.updateLeadsReason("2", strLeadId, "true");
                            alertDialog.dismiss();

                        } else if (apiResponse.getSuccessCode().equals("10006")) {
                            if (EnquiryDetailActivity.this != null) {
                                if (apiResponse.getMessage().equals("Schedule and/or alert times can't be empty")) {
                                    CustomisedToast.error(EnquiryDetailActivity.this, getString(R.string.uncheck_followup_details)).show();
                                } else {
                                    CustomisedToast.error(EnquiryDetailActivity.this, apiResponse.getMessage()).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            if (EnquiryDetailActivity.this != null) {
                                Constants.logoutWrongCredentials(EnquiryDetailActivity.this, apiResponse.getMessage());
                            }
                        } else {
                            if (EnquiryDetailActivity.this != null) {
                                Toast.makeText(EnquiryDetailActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (EnquiryDetailActivity.this != null) {
                        Toast.makeText(EnquiryDetailActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Refresh Followups
    private void refreshFollowUps(Activity activity) {
        try {
            task = getString(R.string.follow_up_fetch);
            String lastUpdated;
            if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
            }

            if (databaseHandler.getSyncTime(task).getCallTime() != null) {
                lastUpdated = databaseHandler.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            String type = "";
            Call<ApiResponse> call = apiService.getFollowUp(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        ApiResponse apiResponse = (ApiResponse) response.body();
                        if (apiResponse.getSuccess()) {
                            for (FollowUp followUp : apiResponse.getData().getFollowups()) {

                                if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                    if (databaseHandler.checkFollowUpResult(followUp.getFoid())) {
                                        databaseHandler.updateFollowUp(followUp);
                                    } else {
                                        if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && databaseHandler.checkSyncIdFollowUp(followUp.getSyncId())) {
                                            databaseHandler.updateFollowUpDataSyncId(followUp);
                                        } else {

                                            databaseHandler.insertFollowUpData(followUp);
                                        }
                                    }
                                } else {
                                    databaseHandler.insertFollowUpData(followUp);
                                }


                                SyncCheck syncCheck = new SyncCheck();
                                syncCheck.setService(getString(R.string.follow_up_fetch));
                                if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                    syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                                } else {
                                    syncCheck.setCallTime(databaseHandler.getFollowUpUpdatedTs());
                                    Log.d("Last updated Ts", databaseHandler.getFollowUpUpdatedTs());
                                }
                                if (databaseHandler != null) {
                                    databaseHandler.updateSyncCheck(syncCheck);
                                }
                            }
                            //Load Data of Followups Fragment again
                            updateFollowUps();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (EnquiryDetailActivity.this != null) {
                        Toast.makeText(EnquiryDetailActivity.this,
                                getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateFollowUps() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment instanceof EnquiryDetailsMainTabFragment) {
            ((EnquiryDetailsMainTabFragment) currentFragment).updateEnquiryFollowUps();
        }
    }

}
