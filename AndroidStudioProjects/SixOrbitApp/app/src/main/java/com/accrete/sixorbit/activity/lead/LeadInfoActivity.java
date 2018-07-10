package com.accrete.sixorbit.activity.lead;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.adapter.FollowUpAdapter;
import com.accrete.sixorbit.adapter.LeadContactsAdapter;
import com.accrete.sixorbit.fragment.Drawer.DatePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.TimePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadContactsFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadFollowUpsFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadFragment;
import com.accrete.sixorbit.fragment.Drawer.leads.LeadInfoFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.interfaces.SendFollowUpMobileInterface;
import com.accrete.sixorbit.interfaces.SendMobileNumberInterface;
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
import static com.accrete.sixorbit.helper.Constants.validCellPhone;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_FOR_LEAD_CALL_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

public class LeadInfoActivity extends AppCompatActivity implements View.OnClickListener, SendMobileNumberInterface,
        SendFollowUpMobileInterface, FollowUpAdapter.FollowUpAdapterListener, PassDateToCounsellor {

    private static final String TAG = "LeadInfoActivity";
    public List<Lead> leadList = new ArrayList<>();
    private String copiedText, mobilenumber;
    private TextView textViewCopy, textViewCall, textViewShare, textViewFollowUpEmpty,
            textViewContactsEmpty, textViewTitleFollowUp;
    private LinearLayout linearLayoutCopy, linearLayoutCall, linearLayoutShare,
            linearLayoutContacts, linearLayoutMainCall, linearLayoutMainEmail;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recyclerViewFollowUp;
    private List<Contacts> leadContactsPersonList = new ArrayList<>();
    private AutoCompleteTextView contactPersonAutoComplete;
    private AutoCompleteTextView addLeadCommunicationModeAutoComplete;
    private AutoCompleteTextView addLeadAssignFollowup;
    private Toolbar toolbar, toolbarBottom;
    private LeadContactsAdapter contactAdapter;
    private FollowUpAdapter followUpAdapter;
    private List<FollowUp> followUps;
    private List<Contacts> contacts;
    private FragmentManager fragmentManager;
    private LeadContactsAdapter.LeadContactsAdapterListener leadContactsAdapterListener;
    private Context mContext;
    private DatabaseHandler db;
    private Lead leadLists = new Lead();
    private Paint p = new Paint();
    private ItemTouchHelper itemTouchHelper;
    private int swipedPosition;
    private String strId, strLeadId, strLeasId, strLeadNr;
    private String followupMobileNumber;
    private NestedScrollView nestedScrollView;
    private TabLayout tabLayout;
    private ViewPager pager;
    private AlertDialog alertDialog;
    private LinkedHashMap<String, String> hashMapFollowUpAssigneecontacts = new LinkedHashMap<>();
    private List<String> followupAssigneecontacts = new ArrayList<>();
    //private String[] communicationMode = {"Mail", "Phone Call", "Skype Call"};
    private DatePickerFragment datePickerFragment;
    private TimePickerFragment timePickerFragment;
    private Date startDate, enddate;
    private EditText addLeadScheduledTime, addLeadAlertTime;
    private String strFromTime;
    private String strToTime;
    private String strScheduleDate;
    private String strAlertTime, strContactPerson, strFollowUpAssignee, strAlertModeChecked;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private CheckBox addLeadSendMail;
    private CheckBox addLeadSendSms;
    private FollowUp leadFollowUp = new FollowUp();
    private String strComMode, strCommunicatedModeId;
    private String name;
    private ViewPagerAdapter viewPagerAdapter;
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();
    private ImageView editToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(this);
        setContentView(R.layout.activity_lead_info);

        if (AppPreferences.getModes(LeadInfoActivity.this,
                AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(LeadInfoActivity.this,
                    AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        toolbarBottom = (Toolbar) findViewById(R.id.toolbar_bottom);
        editToolbar = (ImageView) findViewById(R.id.imageView_lead_info_edit);

        linearLayoutCopy = (LinearLayout) findViewById(R.id.layout_id_copy);
        linearLayoutCall = (LinearLayout) findViewById(R.id.layout_id_call);
        linearLayoutShare = (LinearLayout) findViewById(R.id.layout_id_share);
        fragmentManager = getSupportFragmentManager();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setCallback();
            }
        });

        strId = getIntent().getExtras().getString(getString(R.string.id));
        strLeadId = getIntent().getExtras().getString(getString(R.string.leaid));
        name = getIntent().getExtras().getString(getString(R.string.name));
        strLeasId = getIntent().getExtras().getString(getString(R.string.leasid));
        strLeadNr = getIntent().getExtras().getString(getString(R.string.lead_id));

        final Intent intent = getIntent();
        int notificationId = intent.getIntExtra(getString(R.string.notify_id), -1);
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        if (PushNotificationsTimeService.dictMap.size() <= 1) {
            notificationManager.cancel(1000);
        }

        //         layout buttons view
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        pager = (ViewPager) findViewById(R.id.pager);
        setupViewPager(pager);
        tabLayout.setupWithViewPager(pager);

        //         font awesome icons
        nestedScrollView = (NestedScrollView) findViewById(R.id.nestedScrollView);
        textViewCopy = (TextView) findViewById(R.id.textview_copy);
        textViewCall = (TextView) findViewById(R.id.textview_icon_call);
        textViewShare = (TextView) findViewById(R.id.textview_share);

        Typeface fontAwesomeFont = Typeface.createFromAsset(getAssets(), "font/fontawesome-webfont.ttf");
        textViewCopy.setTypeface(fontAwesomeFont);
        textViewCall.setTypeface(fontAwesomeFont);
        textViewShare.setTypeface(fontAwesomeFont);
        mContext = getApplicationContext();
        contacts = new ArrayList<>();
        followUps = new ArrayList<>();

        if (name != null && !name.isEmpty()) {
            collapsingToolbarLayout.setBackgroundColor(FollowUpAdapter.
                    getColor(name.toUpperCase().substring(0, 1), mContext));
            collapsingToolbarLayout.setContentScrimColor(FollowUpAdapter.
                    getColor(name.toUpperCase().substring(0, 1), mContext));
            collapsingToolbarLayout.setTitle(name);
        }

        checkLeadStatusAndMakeEditable();

        //Permission Check and disable edit of lead
        if (AppPreferences.getBoolean(mContext, AppUtils.ISADMIN) ||
                db.checkUsersPermission(getString(R.string.lead_edit_permission))) {
            editToolbar.setVisibility(View.VISIBLE);
            editToolbar.setEnabled(true);
        } else {
            editToolbar.setVisibility(View.GONE);
            editToolbar.setEnabled(false);
        }

        displayAndCopyData();

        if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            ContactPersonsAPI contactPersonsAPI = new ContactPersonsAPI(LeadInfoActivity.this);
            contactPersonsAPI.getCustomersContactPersons(LeadInfoActivity.this, "",
                    strLeadId, "", "", "", "", "");
        }

    }

    private void displayAndCopyData() {
        try {
            if (leadLists != null) {
                leadLists = null;
                leadLists = new Lead();
            }

            if (leadList != null && leadList.size() > 0) {
                leadList.clear();
            }

            getDataFromDB(strId);

            if (copiedText != null) {
                copiedText = "";
            }

            if (leadLists.getLeadId() != null && !leadLists.getLeadId().isEmpty() &&
                    !leadLists.getLeadId().equals("null") &&
                    strLeadNr != null) {
                copiedText = "Lead Id :" + " " + strLeadNr;
            }
            if (leadLists.getName() != null && !leadLists.getName().isEmpty()) {
                copiedText = copiedText + "\n" + getString(R.string.lead_name) + " " + leadLists.getName();
            }
            if (leadLists.getEmail() != null && !leadLists.getEmail().isEmpty()) {
                copiedText = copiedText + "\n" + getString(R.string.customer_email) + " " +
                        leadLists.getEmail();
            }
            if (leadLists.getMobile() != null && !leadLists.getMobile().isEmpty()) {
                copiedText = copiedText + "\n" + getString(R.string.customer_mobile) + " " +
                        leadLists.getMobile();
            }
            if (leadLists.getOffAddr() != null && !leadLists.getOffAddr().isEmpty()) {
                copiedText = copiedText + "\n" + getString(R.string.address_title) + " " + leadLists.getOffAddr();
            }

            try {
                if (leadLists.getShippingAddress() != null && leadLists.getShippingAddress().get(0).getCity() != null &&
                        !leadLists.getShippingAddress().get(0).getCity().isEmpty()) {
                    copiedText = copiedText + "\n" + getString(R.string.city_title) + " " + leadLists.getShippingAddress().get(0).getCity();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (leadLists.getShippingAddress() != null && leadLists.getShippingAddress().size() > 0 && leadLists.getShippingAddress().get(0).getCity() != null &&
                        !leadLists.getShippingAddress().get(0).getCity().isEmpty()) {
                    copiedText = copiedText + "\n" + getString(R.string.city_title) + " " + leadLists.getShippingAddress().get(0).getCity();
                }

            }

            if (leadLists.getLeasid() != null && !leadLists.getLeasid().isEmpty()) {

                if (leadLists.getLeasid() != null && !leadLists.getLeasid().isEmpty()) {
                    if (leadLists.getLeasid().equals("1")) {
                        copiedText = copiedText + "\n" + "Status : " + "New";
                    } else if (leadLists.getLeasid().equals("2")) {
                        copiedText = copiedText + "\n" + "Status : " + "Pending";
                    } else if (leadLists.getLeasid().equals("3")) {
                        copiedText = copiedText + "\n" + "Status : " + "Converted";
                    } else if (leadLists.getLeasid().equals("4")) {
                        copiedText = copiedText + "\n" + "Status : " + "Cancelled";
                    }
                }

                linearLayoutCopy.setOnClickListener(this);
                linearLayoutCall.setOnClickListener(this);
                linearLayoutShare.setOnClickListener(this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkLeadStatusAndMakeEditable() {
        try {
            editToolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do something you want
                    if ((leadLists.getLeasid() != null && leadLists.getLeasid().equals("3"))) {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.converted_toast)).show();
                    } else if ((leadLists.getLeasid() != null && leadLists.getLeasid().equals("4"))) {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.cancelled_toast)).show();
                    } else {
                        Intent intentEditLead = new Intent(LeadInfoActivity.this, AddLeadActivity.class);
                        intentEditLead.putExtra(getString(R.string.mode), getString(R.string.edit_mode));
                        intentEditLead.putExtra(getString(R.string.id), strId);
                        intentEditLead.putExtra(getString(R.string.leaid), strLeadId);
                        intentEditLead.putExtra(getString(R.string.name), name);
                        intentEditLead.putExtra(getString(R.string.lead_id), strLeadNr);
                        startActivityForResult(intentEditLead, 1000);
                        Intent resultIntent = new Intent();
                        setResult(1000, resultIntent);
                        finish();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setCallback() {
        try {
            Intent resultIntent = new Intent();
            setResult(1000, resultIntent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromDB(String id) {
        try {
            if (db != null) {
                if (strLeadId == null || strLeadId.equals("null") || strLeadId.isEmpty()) {
                    leadLists = db.getLead(strId, getString(R.string.syncID));
                } else {
                    leadLists = db.getLead(strLeadId, getString(R.string.leaid));
                }
                leadList.add(leadLists);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1000) {
            getDataFromDB("");
            //Refresh Followups
            if (!NetworkUtil.getConnectivityStatusString(LeadInfoActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                refreshFollowUps(LeadInfoActivity.this);
            }
            if (pager.getCurrentItem() == 2) {
                LeadFollowUpsFragment leadFollowUpsFragment =
                        (LeadFollowUpsFragment) pager.getAdapter().instantiateItem(pager, pager.getCurrentItem());
                leadFollowUpsFragment.getDataFromDB();
            }

        }
    }

    /* copy ,share and paste on click*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    call intent for bottom bar call option

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.layout_id_copy:
              /*  CopyText = copiedText;
                copyToClipboard(CopyText);*/
                    //Check Permission to view details of lead
                    if (!AppPreferences.getBoolean(LeadInfoActivity.this, AppUtils.ISADMIN) && !db.checkUsersPermission(getString(R.string.followup_schedule_permission))) {
                        Toast.makeText(LeadInfoActivity.this, "You have no permission to add any follow up.", Toast.LENGTH_SHORT).show();
                    } else if (leadLists.getLeasid().equals("4")) {
                        Toast.makeText(LeadInfoActivity.this, "Sorry, this lead is cancelled so you can't add follow up.",
                                Toast.LENGTH_SHORT).show();
                    } else if (leadLists.getLeasid().equals("3")) {
                        Toast.makeText(LeadInfoActivity.this, "Sorry, this lead is in converted state so you can't add follow up.",
                                Toast.LENGTH_SHORT).show();
                    } else if (strLeadId != null && !strLeadId.isEmpty() && !strLeadId.equals("null")) {
                        if (alertDialog == null || !alertDialog.isShowing()) {
                            dialogAddFollowUp();
                        }
                    } else {
                        Toast.makeText(mContext, "Lead is not updated on server. Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }

                    break;

                case R.id.layout_id_call:
                    if (validCellPhone(leadLists.getMobile())) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            callIntent();
                        } else {
                            callActionLead();
                        }
                    } else {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.phone_number_not_valid_error)).show();
                    }
                    break;

                case R.id.layout_id_share:
                    displayAndCopyData();
                    if (leadLists.getMobile() != null && leadLists.getEmail() != null) {
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, copiedText);
                        startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_contact_using)));
                    }
                    break;

            /*case R.id.lead_number:
                if (validCellPhone(leadLists.getMobile())) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        callIntent();
                    } else {
                        callActionLead();
                    }
                } else {
                    CustomisedToast.error(getApplicationContext(), getString(R.string.phone_number_not_valid_error)).show();
                }
                break;*/
                case R.id.lead_email:
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{leadLists.getEmail()});
                    email.setType("plain/text");
                    startActivity(Intent.createChooser(email, getString(R.string.choose_email_client)));
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogAddFollowUp() {
        try {
            View dialogView = View.inflate(LeadInfoActivity.this, R.layout.dialog_add_follow_up, null);
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

            contactPersonAutoComplete = (AutoCompleteTextView) dialogView.findViewById(R.id.add_follow_up_contact_person);
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

            getDataFromDB();
            contactPersonAdapter();
            followupAssigneeAdapter();
            communicationModeAdapter();


            contactPersonAutoComplete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    contactPersonAutoComplete.showDropDown();
                }
            });

            contactPersonAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
            datePickerFragment.setListener(LeadInfoActivity.this);
            timePickerFragment = new TimePickerFragment();
            timePickerFragment.setListener(LeadInfoActivity.this);
            addLeadScheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_from));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    try {
                        btnAdd.setEnabled(false);

                        if (addLeadCommunicationModeAutoComplete.getText().toString().trim().length() == 0) {
                            CustomisedToast.error(LeadInfoActivity.this, "Please select a communication mode").show();
                        } else if (addLeadAssignFollowup.getText().toString().trim().length() == 0) {
                            CustomisedToast.error(LeadInfoActivity.this, "Please select assignee").show();
                        } else if (addLeadScheduledTime.getText().toString().trim().length() == 0) {
                            CustomisedToast.error(LeadInfoActivity.this, "Please select scheduled date & time.").show();
                        } else if (addLeadAlertTime.getText().toString().trim().length() == 0) {
                            CustomisedToast.error(LeadInfoActivity.this, "Please select alert date & time.").show();
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            addLeadCommunicationModeAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FollowupCommunicationMode selected = (FollowupCommunicationMode) parent.getAdapter().getItem(position);

                    //  addLeadCommunicationModeAutoComplete.setText(selected.getName());

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

    private void setDataInStrings() {
        try {
            strFollowUpAssignee = addLeadAssignFollowup.getText().toString();
            if (hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee) != null) {
                strFollowUpAssignee = hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee);
            }

            //strContactPerson = contactPersonAutoComplete.getText().toString();
            if (strContactPerson.equals(getString(R.string.nothing_selected))) {
                strContactPerson = "";
            }
            //    strCommunicatedModeId = addLeadCommunicationModeAutoComplete.getText().toString();
        /*if (strCommunicatedModeId.equals(getString(R.string.phone_call))) {
            strCommunicatedModeId = "1";
        } else if (strCommunicatedModeId.equals(getString(R.string.skype_call))) {
            strCommunicatedModeId = "3";
        } else if (strCommunicatedModeId.equals(getString(R.string.person_meeting))) {
            strCommunicatedModeId = "2";
        }*/

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

            if (strCommunicatedModeId != null &&
                    Constants.ParseDouble(strCommunicatedModeId) < communicationModeList.size()) {
                strComMode = communicationModeList.get(Integer.parseInt(strCommunicatedModeId) - 1).getName();
            /*if (strCommunicatedModeId.equals("1")) {
                strComMode = getString(R.string.phone_call);
            } else if (strCommunicatedModeId.equals("2")) {
                strComMode = getString(R.string.person_meeting);
            } else if (strCommunicatedModeId.equals("3")) {
                strComMode = getString(R.string.skype_call);
            }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dateValidation() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        try {
            long time_nano = System.nanoTime();
            long micro_seconds = time_nano / 1000;
            if (contactPersonAutoComplete.getText().toString().equals("Nothing Selected")) {
                leadFollowUp.setContactPerson(name);
            } else {
                leadFollowUp.setContactPerson(contactPersonAutoComplete.getText().toString());
            }
            leadFollowUp.setCommid(strCommunicatedModeId);
            leadFollowUp.setFollowupCommunicationMode(strComMode);
            leadFollowUp.setFosid("1");
            leadFollowUp.setAlertOn(strAlertTime);
            leadFollowUp.setScheduledDate(strScheduleDate);
            leadFollowUp.setCodeid("");
            leadFollowUp.setSyncId(String.valueOf(micro_seconds));
            leadFollowUp.setLeadId(strLeadId);
            leadFollowUp.setAssignedUid(strFollowUpAssignee);
            for (Map.Entry<String, String> e : hashMapFollowUpAssigneecontacts.entrySet()) {
                if (e.getValue().equals(strFollowUpAssignee)) {
                    leadFollowUp.setAssigned_user(e.getKey());
                }
            }
            leadFollowUp.setContactPersonEmail(leadLists.getEmail());
            leadFollowUp.setContactPersonMobile(leadLists.getMobile());
            leadFollowUp.setFollowupTypeStatus(getString(R.string.followup_status_progress));
            leadFollowUp.setCreatedUser(AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_NAME));
            leadFollowUp.setLeadIdR(strLeadNr);
            if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                leadFollowUp.setSyncStatus(String.valueOf(false));
                db.insertFollowUpData(leadFollowUp);
                db.updateLeadsReason("2", strLeadId, "false");

                if (pager.getCurrentItem() == 2) {
                    reload();
                } else {
                    pager.setCurrentItem(2);
                }

                alertDialog.dismiss();
            } else {
                addFollowUp();
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            Call<ApiResponse> call = apiService.addFollowUp(version, key, task, userId, accessToken, strLeadId,
                    strContactPerson, strCommunicatedModeId, strFollowUpAssignee, strScheduleDate, strAlertTime,
                    strAlertModeChecked, leadFollowUp.getSyncId(), "0");
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
                            if (!NetworkUtil.getConnectivityStatusString(LeadInfoActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                                refreshFollowUps(LeadInfoActivity.this);
                            }

                            progressBar.setVisibility(View.INVISIBLE);
                            leadFollowUp.setFoid(apiResponse.getData().getCurrentFollowUpId());
                            leadFollowUp.setSyncStatus(String.valueOf(true));
                            db.insertFollowUpData(leadFollowUp);
                            db.updateLeadsReason("2", strLeadId, "true");
                            if (pager.getCurrentItem() == 2) {
                                reload();
                            } else {
                                pager.setCurrentItem(2);
                            }

                       /*  if(LeadFollowUpsFragment.followUpAdapter!=null) {
                             LeadFollowUpsFragment.followUpAdapter.notifyDataSetChanged();
                         }*/
                            // Toast.makeText(LeadInfoActivity.this, apiResponse.getData().getCurrentFollowUpId(), Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();

                        }
                        //10006 is the error code of wrong access token
                    /*else if (apiResponse.getSuccessCode().equals("10006")) {
                        if (apiResponse.getMessage().equals("Schedule and/or alert times can't be empty")) {
                            CustomisedToast.error(getApplicationContext(), getString(R.string.uncheck_followup_details)).show();
                        } else {
                            CustomisedToast.error(getApplicationContext(), apiResponse.getMessage()).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }*/
                        //Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            progressBar.setVisibility(View.GONE);
                            //Logout
                            Constants.logoutWrongCredentials(LeadInfoActivity.this, apiResponse.getMessage());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LeadInfoActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(LeadInfoActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    Log.d("errorInAddFollowUp", t.getMessage() + "");
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reload() {
        viewPagerAdapter.notifyDataSetChanged();
        pager.invalidate();

        //pager.setCurrentItem(0);
    }

    private void callIntent() {
        if (checkPermissionWithRationale(this, new LeadFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_LEAD_CALL_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
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
// Run Time permissions call back for Lead contacts  call action

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
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
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callActionLeadInfoContacts();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

//                       Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
            //            Request code for handling the call action for lead in bottom bar.

            case REQUEST_CODE_FOR_LEAD_CALL_PERMISSIONS: {
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
                        Log.d(TAG, "Call and Phone services permission granted");

                        // process the normal flow
                        //else any one or both the permissions are not granted
                        callActionLead();

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

//                       Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;

            //            Request code for handling the call action for followup contact in lead info.

            case REQUEST_CODE_FOR_FOLLOW_UP_CALL_PERMISSIONS: {

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
                        //callActionLeadInfoFollowups();

                        if (pager.getCurrentItem() == 2) {
                            LeadFollowUpsFragment leadFollowUpsFragment = (LeadFollowUpsFragment) pager.getAdapter().instantiateItem(pager, pager.getCurrentItem());
                            leadFollowUpsFragment.callAction();

                        }

                    } else {
                        //permission is denied (this is the first timeTextView, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {

                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {

//                       Requesting Settings Page On Never ASk Again Click
                            askUserToAllowPermissionFromSetting();

                        }
                    }

                }
            }
            break;
        }
    }

    private void setSwipeForRecyclerView() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof FollowUpAdapter.MyViewHolder
                        && ((FollowUpAdapter.MyViewHolder) viewHolder).isSwipeToRecord) {
                    if ((strLeasId != null && strLeasId.equals("3"))) {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.converted_toast)).show();

                    } else if ((strLeasId != null && strLeasId.equals("4"))) {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.cancelled_toast)).show();
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder);
                } else {
                    return 0;
                }
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                swipedPosition = viewHolder.getAdapterPosition();

                if ((strLeasId != null && strLeasId.equals("3"))) {
//                    CustomisedToast.error(getApplicationContext(), getString(R.string.converted_toast)).show();
                } else if ((strLeasId != null && strLeasId.equals("4"))) {
//                    CustomisedToast.error(getApplicationContext(), getString(R.string.cancelled_toast)).show();
                } else {
                    Intent intent = new Intent(LeadInfoActivity.this, RecordFollowUpActivity.class);
                    if (followUps.get(swipedPosition).getLeadId() != null && !followUps.get(swipedPosition).getLeadId().isEmpty()) {
                        intent.putExtra(mContext.getString(R.string.lead_id), followUps.get(swipedPosition).getLeadId());
                    }
                    if (followUps.get(swipedPosition).getFoid() != null && !followUps.get(swipedPosition).getFoid().isEmpty()) {
                        intent.putExtra(mContext.getString(R.string.foid), followUps.get(swipedPosition).getFoid());
                        intent.putExtra(getString(R.string.leasid), strLeasId);
                    } else if (followUps.get(swipedPosition).getSyncId() != null && !followUps.get(swipedPosition).getSyncId().isEmpty()) {
                        intent.putExtra(mContext.getString(R.string.sync_id), followUps.get(swipedPosition).getSyncId());
                    }
                    startActivityForResult(intent, 1000);
                    followUpAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildDraw(final Canvas c, RecyclerView recyclerVw, RecyclerView.ViewHolder viewHolder, float dX, float dY, final int actionState, boolean isCurrentlyActive) {
                try {
                    Bitmap iconCall, iconEmail;
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        View itemView = viewHolder.itemView;
                        float height = (float) itemView.getBottom() - (float) itemView.getTop();
                        float width = height / 3;

                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        iconCall = BitmapFactory.decodeResource(getResources(), R.drawable.justify_left);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(iconCall, null, icon_dest, p);

                    }

                    super.onChildDraw(c, recyclerViewFollowUp, viewHolder, dX, dY, actionState, isCurrentlyActive);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


        };

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerViewFollowUp);

    }

    /* Method for copying the string data*/

    public void copyToClipboard(String copyText) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(copyText);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData
                    .newPlainText(getString(R.string.your_data), copyText);
            clipboard.setPrimaryClip(clip);
        }
        Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.data_copied), Toast.LENGTH_SHORT);
//        Log.e("Copied data", copyText);

        toast.show();

    }

    //    Lead contacts call action method  to call the lead info contacts

    @SuppressLint("MissingPermission")
    private void callActionLeadInfoContacts() {
        try {
            Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobilenumber));
            Log.e("Leadinfo L mnumber", mobilenumber + "");
            startActivity(intentCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Lead contacts call action method  to call the lead

    @SuppressLint("MissingPermission")
    private void callActionLead() {
        try {
            String leadlistsmobile = leadLists.getMobile();
            if (leadlistsmobile == null || leadlistsmobile == "") {
                CustomisedToast.error(mContext, getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
            } else {
                Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + leadlistsmobile));
                Log.e("Leadcontact mNumber", leadLists.getMobile() + "");
                startActivity(intentCall);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //    Lead info call action method  to call the followup contact number

    @SuppressLint("MissingPermission")
    private void callActionLeadInfoFollowups() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + followupMobileNumber));
        Log.e("Leadinfo FU mnumber", followupMobileNumber + "");
        startActivity(intentCall);
    }

    public void askUserToAllowPermissionFromSetting() {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Permission settings page request method

    @Override
    public void onBackPressed() {
        setCallback();
    }

    @Override
    public void sendMobileNumberLead(String mnumber) {
        try {
            this.mobilenumber = mnumber;
            Log.e(" mNumber in interface", mobilenumber + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    Interface method for mobile number  in Lead contacts

    @Override
    public void sendMobileNumber(String mnumber) {
        try {
            this.followupMobileNumber = mnumber;
            Log.e(" mNumberinterface", followupMobileNumber + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLeadMobileNumber(String lead_mobile_number) {

    }

    @Override
    public void onMessageRowClicked(int position) {
        try {
            nestedScrollView.fullScroll(View.FOCUS_DOWN);
            followUpAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        try {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.leaid), strLeadId);
            bundle.putString(getString(R.string.leasid), strLeasId);
            bundle.putString(getString(R.string.id), strId);

            LeadInfoFragment leadInfoFragment = new LeadInfoFragment();
            leadInfoFragment.setArguments(bundle);

            LeadContactsFragment leadContactsFragment = new LeadContactsFragment();
            leadContactsFragment.setArguments(bundle);

            LeadFollowUpsFragment leadFollowUpsFragment = new LeadFollowUpsFragment();
            leadFollowUpsFragment.setArguments(bundle);

            viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
            viewPagerAdapter.addFragment(leadInfoFragment, "Info");
            viewPagerAdapter.addFragment(leadContactsFragment, "Contacts");
            viewPagerAdapter.addFragment(leadFollowUpsFragment, "Follow ups");
            viewPager.setAdapter(viewPagerAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void contactPersonAdapter() {
        try {
            if (leadContactsPersonList.size() > 0) {
                leadContactsPersonList.clear();
            }

            Contacts contacts = new Contacts();
            contacts.setName(getString(R.string.nothing_selected));
            leadContactsPersonList.add(0, contacts);

            for (int i = 0; i < leadList.size(); i++) {
                leadContactsPersonList.addAll(leadList.get(i).getContacts());
            }
            ArrayAdapter arrayAdapterContactPerson = new ArrayAdapter(this, R.layout.simple_spinner_item, leadContactsPersonList);
            arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            contactPersonAutoComplete.setAdapter(arrayAdapterContactPerson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void followupAssigneeAdapter() {
        try {
            for (String key : hashMapFollowUpAssigneecontacts.keySet()) {
                followupAssigneecontacts.add(key);
            }
            ArrayAdapter arrayAdapterFollowUpAssignee = new ArrayAdapter(this, R.layout.simple_spinner_item, followupAssigneecontacts);
            arrayAdapterFollowUpAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            addLeadAssignFollowup.setAdapter(arrayAdapterFollowUpAssignee);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void communicationModeAdapter() {
        try {
            if (communicationModeList != null) {
                ArrayAdapter arrayAdapterContactPerson = new ArrayAdapter(this, R.layout.simple_spinner_item, communicationModeList);
                arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                addLeadCommunicationModeAutoComplete.setAdapter(arrayAdapterContactPerson);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDataFromDB() {
        try {
            if (db != null && db.getAllAssignee().size() != 0) {
                List<ChatContacts> contacts2 = db.getAllAssignee();
                for (ChatContacts cn : contacts2) {
                    hashMapFollowUpAssigneecontacts.put(cn.getName(), String.valueOf(cn.getUid()));
                    // Writing Contacts to log
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            // strScheduleOnlyDate = stringStartDate;
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
            //  strAlertTime = stringEndDate;
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
        try {
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
        } catch (Exception ex) {
            ex.printStackTrace();
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

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
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
                                    if (db.checkFollowUpResult(followUp.getFoid())) {
                                        db.updateFollowUp(followUp);
                                    } else {
                                        if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && db.checkSyncIdFollowUp(followUp.getSyncId())) {
                                            db.updateFollowUpDataSyncId(followUp);
                                        } else {
                                            if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                                    && db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                                    ) {

                                                for (int i = 0; i < db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                                    if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                            .equals(followUp.getContactedPerson())) {
                                                        followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                                    }
                                                }
                                            }
                                            db.insertFollowUpData(followUp);
                                            Log.d("Follow up ID 1", String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                                        }
                                    }
                                } else {
                                    if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                            && db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                            ) {

                                        for (int i = 0; i < db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                            if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                    .equals(followUp.getContactedPerson())) {
                                                followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                            }
                                        }
                                    }
                                    db.insertFollowUpData(followUp);
                                    Log.d("Follow up ID 2", String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                                }


                                SyncCheck syncCheck = new SyncCheck();
                                syncCheck.setService(getString(R.string.follow_up_fetch));
                                if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                    syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                                } else {
                                    syncCheck.setCallTime(db.getFollowUpUpdatedTs());
                                    Log.d("Last updated Ts", db.getFollowUpUpdatedTs());
                                }
                                if (db != null) {
                                    db.updateSyncCheck(syncCheck);
                                }

                                //Load Data of Followups Fragment again
                                if (pager.getCurrentItem() == 2) {
                                    reload();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(LeadInfoActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface UpdatableFragment {
        void reload();
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof UpdatableFragment) {
                ((UpdatableFragment) object).reload();
            }
            //don't return POSITION_NONE, avoid fragment recreation.
            return super.getItemPosition(object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
