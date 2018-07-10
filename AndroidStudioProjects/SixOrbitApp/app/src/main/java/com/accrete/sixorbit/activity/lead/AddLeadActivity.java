package com.accrete.sixorbit.activity.lead;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.LeadsSubContactsAdapter;
import com.accrete.sixorbit.fragment.Drawer.DatePickerFragment;
import com.accrete.sixorbit.fragment.Drawer.TimePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.FollowupCommunicationMode;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.LeadShippingAddress;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.activity.domain.DomainActivity.PREFS_NAME;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 10/7/17
 * /**
 * <p>
 * Modified by poonam on 21/09/17
 */

public class AddLeadActivity extends AppCompatActivity implements View.OnClickListener,
        LeadsSubContactsAdapter.EditSubContactsViewInterface, PassDateToCounsellor {
    public Toolbar toolbar;
    public LinearLayout linearLayoutAddress, linearLayoutContactDetails, imageViewAddContact, linearLayoutTitleAddress, linearLayoutTitleAssignee;
    List<Contacts> contactsList = new ArrayList<>();
    List<String> leadContactsPersonList = new ArrayList<>();
    SharedPreferences sharedpreferences;
    DatabaseHandler db;
    ProgressBar progressBar;
    Lead lead = new Lead();
    String strId;
    String strComMode;
    EmailValidator emailValidator;
    private TextInputLayout textInputLayoutCompany, textInputLayoutWebsite, textInputLayoutGender, attendeeTextInputLayout;
    private ArrayList<String> genderList = new ArrayList<>();
    //private String[] communicationMode = {"Mail", "Phone Call", "Skype Call"};
    private String TAG = "AddLeadActivity";
    private SharedPreferences settings;
    private TextView textViewAdd;
    private AlertDialog alertDialog;
    private RecyclerView recyclerView;
    private TextInputEditText editTextCompany, editTextWebsite, editTextContactNumber, editTextContactEmail,
            editTextOfficeName, editTextLineOne, editTextLineTwo, editTextCity, editTextPin, editTextFirstName,
            editTextLastName;
    private EditText editTextSpecialInstructions, editTextScheduledTime, editTextAlertTime;
    private LeadsSubContactsAdapter mAdapter;
    private String strMode;
    private LinearLayout linearLayoutIndividual, linearLayoutAssignee;
    private AutoCompleteTextView spinnerAssignee, spinnerCountry, spinnerState, spinnerGender;
    private HashMap<String, String> hashMapcountries = new HashMap<String, String>(),
            hashMapStates = new HashMap<String, String>();
    private String strCompanyName, strEmail, strWebsite, strFirstName, strLastName, strOfficeName,
            strLineOne, strLineTwo, strPincode, strCountries, strState, strAssignee, strCity,
            strSpecialInstructions, strSchedule, strSAddressId, strFromTime, strScheduleDate,
            strContactPerson, strCommunicatedMode, strFollowUpAssignee, strToTime, strAlertTime,
            strStateCode;
    private String strGender;
    private String strPhonebookNumber, strPhonebookEmail, strPhonebookFirstName, strPhonebookSecondName;
    private String MyPREFERENCES = "MyPrefs";
    private Lead listEditLeadData = new Lead();
    private ArrayAdapter arrayAdapterAssignee, arrayAdapterGender;
    private CheckBox checkBoxFollowUp, checkBoxEmail, checkBoxSms;
    private LinearLayout linearLayoutFillDetails, linearLayoutFollowUp, linearLayoutFollowUpTitle;
    private DatePickerFragment datePickerFragment;
    private Date startDate, enddate;
    private TimePickerFragment timePickerFragment;
    private String strScheduleChecked, strAlertModeChecked;
    private ArrayList<Integer> listAlertModeChecked = new ArrayList<>();
    private ArrayList<String> countriesList;
    private ArrayList<String> listStates, listStatesCode;
    private AutoCompleteTextView spinnerContactPerson, spinnerCommunicationMode, spinnerFollowupAssignee;
    private String strSyncId;
    private String strLeadId, strName;
    private List<String> followupAssigneecontacts = new ArrayList<>();
    private List<String> assigneecontacts = new ArrayList<String>();
    private LinkedHashMap<String, String> hashMapAssigneecontacts = new LinkedHashMap<>();
    private LinkedHashMap<String, String> hashMapFollowUpAssigneecontacts = new LinkedHashMap<>();
    private LeadShippingAddress shippingAddresses = new LeadShippingAddress();
    private FollowUp leadFollowUp = new FollowUp();
    private List<LeadShippingAddress> leadShippingAddresses = new ArrayList<>();
    private List<FollowUp> leadFollowUps = new ArrayList<>();
    private String strCodeid, strRepresentableLeadid;
    private String userName;
    private TextView addressCityTitle;
    private TextView addressZipCodeTitle;
    private TextView addressCountryTitle;
    private TextView addressStateTitle;
    private TextView addressLineOneTitle;
    private List<FollowupCommunicationMode> communicationModeList = new ArrayList<>();
    private ArrayAdapter arrayAdapterCommMode;
    private ArrayAdapter arrayAdapterContactPerson;
    private boolean isCheckboxSelected;
    private int positionChecked;
    private LinearLayout contactsMainLayout;

    public AddLeadActivity() {
    }

    //Validate Website URL
    public static boolean isURL(String url) {
        Pattern p = Pattern.compile(AppUtils.WEBSITE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lead);

        emailValidator = new EmailValidator();
        userName = AppPreferences.getUserName(getApplicationContext(), AppUtils.USER_NAME);
        Log.e("Log Name", userName);
        strMode = getIntent().getStringExtra(getString(R.string.mode));
        strPhonebookEmail = getIntent().getStringExtra(getString(R.string.email));
        strPhonebookNumber = getIntent().getStringExtra(getString(R.string.phoneNumber));
        strPhonebookFirstName = getIntent().getStringExtra(getString(R.string.fName));
        strPhonebookSecondName = getIntent().getStringExtra(getString(R.string.lName));
        strId = getIntent().getStringExtra(getString(R.string.id));
        strLeadId = getIntent().getStringExtra(getString(R.string.leaid));
        strName = getIntent().getExtras().getString(getString(R.string.name));
        if (strMode.equals(getString(R.string.edit_mode))) {
            strSyncId = strId;
            strRepresentableLeadid = getIntent().getExtras().getString(getString(R.string.lead_id));
        }

        if (AppPreferences.getModes(AddLeadActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE) != null) {
            communicationModeList.addAll(AppPreferences.getModes(AddLeadActivity.this, AppUtils.FOLLOWUPS_COMMUNICATION_MODE));
        }

//        editLeadEmail = getIntent().getStringExtra("editemail");
//        editLeadName = getIntent().getStringExtra("editfirstname");
//        editLeadMobileNumber = getIntent().getStringExtra("editmobilenumber");
        db = new DatabaseHandler(getApplicationContext());
        initalizeView();

    }

    private void getDataFromDB() {
        if (db != null && db.getAllAssignee().size() != 0) {
            List<ChatContacts> contacts2 = db.getAllAssignee();
            for (ChatContacts cn : contacts2) {
                hashMapAssigneecontacts.put(cn.getName(), String.valueOf(cn.getUid()));
                hashMapFollowUpAssigneecontacts.put(cn.getName(), String.valueOf(cn.getUid()));
            }
        }
    }

    private void initalizeView() {
        try {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            progressBar = (ProgressBar) findViewById(R.id.add_lead_progress_bar);
            imageViewAddContact = (LinearLayout) findViewById(R.id.add_lead_add_contact);
            linearLayoutTitleAddress = (LinearLayout) findViewById(R.id.add_lead_expand);
            linearLayoutTitleAssignee = (LinearLayout) findViewById(R.id.add_lead_assignee);
            linearLayoutAddress = (LinearLayout) findViewById(R.id.add_lead_address_details);
            linearLayoutContactDetails = (LinearLayout) findViewById(R.id.add_lead_contact_details);
            linearLayoutIndividual = (LinearLayout) findViewById(R.id.add_lead_individual_layout);
            linearLayoutAssignee = (LinearLayout) findViewById(R.id.add_lead_assignee_hide);
            linearLayoutFillDetails = (LinearLayout) findViewById(R.id.add_lead_follow_up_details);
            linearLayoutFollowUp = (LinearLayout) findViewById(R.id.add_lead_follow_up);
            linearLayoutFollowUpTitle = (LinearLayout) findViewById(R.id.add_lead_title);
            textViewAdd = (TextView) findViewById(R.id.add_lead_add);
            recyclerView = (RecyclerView) findViewById(R.id.add_lead_recycler_view);

            editTextFirstName = (TextInputEditText) findViewById(R.id.add_lead_first_name);
            editTextLastName = (TextInputEditText) findViewById(R.id.add_lead_last_name);
            editTextCompany = (TextInputEditText) findViewById(R.id.add_lead_company);
            editTextWebsite = (TextInputEditText) findViewById(R.id.add_lead_website);
            editTextContactNumber = (TextInputEditText) findViewById(R.id.add_lead_contact_number);
            editTextContactEmail = (TextInputEditText) findViewById(R.id.add_lead_contact_email);
            editTextOfficeName = (TextInputEditText) findViewById(R.id.add_lead_office_name);
            addressLineOneTitle = (TextView) findViewById(R.id.address_line_one_title);
            editTextLineOne = (TextInputEditText) findViewById(R.id.add_lead_line_one);
            editTextLineTwo = (TextInputEditText) findViewById(R.id.add_lead_line_two);
            editTextCity = (TextInputEditText) findViewById(R.id.add_lead_city);
            editTextPin = (TextInputEditText) findViewById(R.id.add_lead_pincode);
            editTextSpecialInstructions = (EditText) findViewById(R.id.add_lead_special_instruction);
            editTextScheduledTime = (EditText) findViewById(R.id.add_lead_scheduled_time);
            editTextAlertTime = (EditText) findViewById(R.id.add_lead_alert_time);
            addressCityTitle = (TextView) findViewById(R.id.address_city_title);
            addressZipCodeTitle = (TextView) findViewById(R.id.address_zipCode_title);
            addressCountryTitle = (TextView) findViewById(R.id.address_country_title);
            addressStateTitle = (TextView) findViewById(R.id.address_state_title);

            spinnerCountry = (AutoCompleteTextView) findViewById(R.id.add_lead_spinner_country);
            spinnerState = (AutoCompleteTextView) findViewById(R.id.add_lead_spinner_state);
            spinnerGender = (AutoCompleteTextView) findViewById(R.id.add_lead_spinner_gender);
            spinnerAssignee = (AutoCompleteTextView) findViewById(R.id.add_lead_spinner_assignee_name);

            spinnerContactPerson = (AutoCompleteTextView) findViewById(R.id.add_lead_contact_person);
            spinnerCommunicationMode = (AutoCompleteTextView) findViewById(R.id.add_lead_communication_mode);
            spinnerFollowupAssignee = (AutoCompleteTextView) findViewById(R.id.add_lead_assign_followup);


            textInputLayoutCompany = (TextInputLayout) findViewById(R.id.text_input_layout_company);
            textInputLayoutWebsite = (TextInputLayout) findViewById(R.id.text_input_layout_website);
            textInputLayoutGender = (TextInputLayout) findViewById(R.id.text_input_layout_gender);
            attendeeTextInputLayout = (TextInputLayout) findViewById(R.id.attendee_textInputLayout);
            checkBoxFollowUp = (CheckBox) findViewById(R.id.add_lead_add_details);
            checkBoxEmail = (CheckBox) findViewById(R.id.add_lead_send_mail);
            checkBoxSms = (CheckBox) findViewById(R.id.add_lead_send_sms);

            contactsMainLayout = (LinearLayout) findViewById(R.id.contacts_main_layout);

            String colored = " *";

            Spannable spannableStringBuilder = new SpannableString(colored);
            int end = spannableStringBuilder.length();
            spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                    end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //Country
            addressCountryTitle.setHint(TextUtils.concat("Country", spannableStringBuilder));

            //State
            addressStateTitle.setHint(TextUtils.concat("State"));

            //City
            addressCityTitle.setHint(TextUtils.concat("City", spannableStringBuilder));

            //ZipCode
            addressZipCodeTitle.setHint(TextUtils.concat("PinCode", spannableStringBuilder));

            //Line One
            addressLineOneTitle.setHint(TextUtils.concat("Line 1", spannableStringBuilder));


            toolbar.setTitle(getString(R.string.add_lead_title));
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //do something you want
                    if (strMode.equals(getString(R.string.edit_mode))) {
                        goBackToLeadInfo();
                    } else {
                        finish();
                    }

                }
            });
            strScheduleChecked = "2";
            checkBoxFollowUp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        linearLayoutFillDetails.setVisibility(View.VISIBLE);
                        Animation slide_down = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_down);
                        linearLayoutFillDetails.startAnimation(slide_down);
                        strScheduleChecked = "1";
                    } else {
                        linearLayoutFillDetails.setVisibility(View.GONE);
                        Animation slide_up = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.slide_up);
                        linearLayoutFillDetails.startAnimation(slide_up);
                        strScheduleChecked = "2";
                    }
                }
            });

            checkBoxEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listAlertModeChecked.add(1);
                    } else {
                        if (listAlertModeChecked.size() == 1) {
                            listAlertModeChecked.remove(0);
                            checkBoxEmail.setChecked(false);
                        } else if (listAlertModeChecked.size() == 2) {
                            listAlertModeChecked.remove(1);
                            checkBoxEmail.setChecked(false);
                        } else {
                            listAlertModeChecked.clear();
                            checkBoxEmail.setChecked(false);
                        }
                    }
                }
            });

            checkBoxSms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        listAlertModeChecked.add(2);
                    } else {
                        if (listAlertModeChecked.size() == 1) {
                            listAlertModeChecked.remove(0);
                            checkBoxSms.setChecked(false);
                        } else if (listAlertModeChecked.size() == 2) {
                            listAlertModeChecked.remove(1);
                            checkBoxSms.setChecked(false);
                        } else {
                            listAlertModeChecked.clear();
                            checkBoxSms.setChecked(false);
                        }
                    }
                }
            });

            linearLayoutTitleAddress.setOnClickListener(this);
            imageViewAddContact.setOnClickListener(this);
            textViewAdd.setOnClickListener(this);
            linearLayoutTitleAssignee.setOnClickListener(this);
            stateAdapter();
            countryAdapter();
            getDataFromDB();

            if (strMode.equals(getString(R.string.individual_mode))) {
                linearLayoutIndividual.setVisibility(View.VISIBLE);
                editTextCompany.setVisibility(View.GONE);
                editTextWebsite.setVisibility(View.GONE);
                textInputLayoutGender.setVisibility(View.VISIBLE);
                textInputLayoutWebsite.setVisibility(View.GONE);
                textInputLayoutCompany.setVisibility(View.GONE);
                contactsMainLayout.setVisibility(View.GONE);
            } else if (strMode.equals(getString(R.string.company_mode))) {
                linearLayoutIndividual.setVisibility(View.GONE);
                editTextCompany.setVisibility(View.VISIBLE);
                editTextWebsite.setVisibility(View.VISIBLE);
                textInputLayoutWebsite.setVisibility(View.VISIBLE);
                textInputLayoutCompany.setVisibility(View.VISIBLE);
                textInputLayoutGender.setVisibility(View.GONE);
                contactsMainLayout.setVisibility(View.VISIBLE);
            } else if (strMode.equals(getString(R.string.edit_mode))) {
                // method to get the data for edit lead
                getUserDataFromDB();
                setDataForEditLead();
                if (listEditLeadData.getLeadPersonType() != null && listEditLeadData.getLeadPersonType().equals("2")) {
                    linearLayoutIndividual.setVisibility(View.VISIBLE);
                    editTextCompany.setVisibility(View.GONE);
                    editTextWebsite.setVisibility(View.GONE);
                    textInputLayoutGender.setVisibility(View.VISIBLE);
                    textInputLayoutWebsite.setVisibility(View.GONE);
                    textInputLayoutCompany.setVisibility(View.GONE);
                    contactsMainLayout.setVisibility(View.GONE);
                } else if (listEditLeadData.getLeadPersonType()
                        != null && listEditLeadData.getLeadPersonType().equals("1")) {
                    linearLayoutIndividual.setVisibility(View.GONE);
                    editTextCompany.setVisibility(View.VISIBLE);
                    editTextWebsite.setVisibility(View.VISIBLE);
                    textInputLayoutWebsite.setVisibility(View.VISIBLE);
                    textInputLayoutCompany.setVisibility(View.VISIBLE);
                    textInputLayoutGender.setVisibility(View.GONE);
                    contactsMainLayout.setVisibility(View.VISIBLE);
                }

                if (listEditLeadData.getContacts().size() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    contactsList = listEditLeadData.getContacts();
                }

                toolbar.setTitle(getString(R.string.edit_lead_title));
                linearLayoutFillDetails.setVisibility(View.GONE);
                linearLayoutFollowUp.setVisibility(View.GONE);
                linearLayoutFollowUpTitle.setVisibility(View.GONE);


            } else {
                linearLayoutIndividual.setVisibility(View.VISIBLE);
                editTextCompany.setVisibility(View.GONE);
                editTextWebsite.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                textInputLayoutWebsite.setVisibility(View.GONE);
                textInputLayoutCompany.setVisibility(View.GONE);
                textInputLayoutGender.setVisibility(View.VISIBLE);
                editTextContactNumber.setText(strPhonebookNumber);
                editTextContactEmail.setText(strPhonebookEmail);
                editTextFirstName.setText(strPhonebookFirstName);
                editTextLastName.setText(strPhonebookSecondName);
            }

            mAdapter = new LeadsSubContactsAdapter(this, listEditLeadData, contactsList, this, strMode);
            recyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

            genderAdapter();
            contactPersonAdapter();
            followupAssigneeAdapter();
            assigneeContactsAdapter();
            communicationModeAdapter();
            spinnerCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerCountry.showDropDown();
                }
            });
            spinnerGender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerGender.showDropDown();
                }
            });

            spinnerState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerState.showDropDown();
                }
            });

            spinnerAssignee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    spinnerAssignee.showDropDown();
                }
            });


            spinnerContactPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editTextFirstName.clearFocus();
                    spinnerContactPerson.showDropDown();
                }
            });

            spinnerCommunicationMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerCommunicationMode.showDropDown();
                }
            });
            spinnerFollowupAssignee.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    spinnerFollowupAssignee.showDropDown();
                }
            });

            spinnerCountry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strCountries = spinnerCountry.getText().toString();
                        for (int i = 0; i < countriesList.size(); i++) {
                            String temp = countriesList.get(i);
                            if (strCountries.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        spinnerCountry.setText("");
                    }
                }

            });

            spinnerState.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strState = spinnerState.getText().toString();
                        for (int i = 0; i < listStates.size(); i++) {

                            /*String tempStateCode = listStatesCode.get(i);
                            if (strStateCode.compareTo(tempStateCode) == 0) {
                                return;
                            }*/

                            String temp = listStates.get(i);
                            if (strState.compareTo(temp) == 0) {
                                strStateCode = listStatesCode.get(i).toString();
                                return;
                            }
                        }
                        spinnerState.setText("");

                    }
                }
            });


            spinnerGender.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strGender = spinnerGender.getText().toString();
                        for (int i = 0; i < genderList.size(); i++) {
                            String temp = genderList.get(i);
                            if (strGender.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        spinnerGender.setText("");
                    }
                }
            });

            spinnerAssignee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strAssignee = spinnerAssignee.getText().toString();
                        for (int i = 0; i < assigneecontacts.size(); i++) {
                            String temp = assigneecontacts.get(i);
                            if (strAssignee.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        spinnerAssignee.setText("");
                    }
                }
            });

            spinnerFollowupAssignee.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        strFollowUpAssignee = spinnerFollowupAssignee.getText().toString();
                        for (int i = 0; i < followupAssigneecontacts.size(); i++) {
                            String temp = followupAssigneecontacts.get(i);
                            if (strFollowUpAssignee.compareTo(temp) == 0) {
                                return;
                            }
                        }
                        spinnerFollowupAssignee.setText("");
                    }
                }
            });

            datePickerFragment = new DatePickerFragment();
            datePickerFragment.setListener(this);
            timePickerFragment = new TimePickerFragment();
            timePickerFragment.setListener(AddLeadActivity.this);
            editTextScheduledTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (datePickerFragment != null && !datePickerFragment.isAdded()) {
                        datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_from));
                    }
                }
            });

            editTextAlertTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Added in June
                    try {
                        if (editTextScheduledTime.getText().toString() == null ||
                                editTextScheduledTime.getText().toString().isEmpty()) {
                            if (datePickerFragment != null && !datePickerFragment.isAdded()) {
                                datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                            }
                        } else {

                            String[] strDate = editTextScheduledTime.getText().toString().split("\\s+");
                            String strYear = strDate[0].toString().substring(strDate[0].lastIndexOf("-") + 1,
                                    strDate[0].toString().length());
                            String strDayMonth = strDate[0].toString().substring(0, strDate[0].lastIndexOf("-"));
                            String strDay = strDayMonth.toString().substring(0, strDayMonth.lastIndexOf("-"));
                            String strMonth = strDayMonth.toString().substring(strDayMonth.lastIndexOf("-") + 1,
                                    strDayMonth.length());

                          /*  //Calculating Time
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
                            if (datePickerFragment != null && !datePickerFragment.isAdded()) {
                                datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_to));
                }
            });

            spinnerCommunicationMode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedItem = arrayAdapterCommMode.getItem(position).toString();
                    // spinnerCommunicationMode.setText(selectedItem);
                    int pos = -1;
                    for (int i = 0; i < communicationModeList.size(); i++) {
                        if (communicationModeList.get(i).getName().equals(selectedItem)) {
                            pos = i;
                            break;
                        }
                    }
                    strComMode = communicationModeList.get(pos).getName();
                    strCommunicatedMode = communicationModeList.get(pos).getCommid();
                }
            });

            spinnerCommunicationMode.setThreshold(1);

            spinnerState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    strState = spinnerState.getText().toString();
                    for (int i = 0; i < listStates.size(); i++) {
                       /* String tempStateCode = listStatesCode.get(i);
                        if (strStateCode.compareTo(tempStateCode) == 0) {
                            return;
                        }

                        String temp = listStates.get(i);
                        if (strState.compareTo(temp) == 0) {
                            return;
                        }*/
                        String temp = listStates.get(i);
                        if (strState.compareTo(temp) == 0) {
                            strStateCode = listStatesCode.get(i).toString();
                            return;
                        }


                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkStateOnFocus() {
        try {        //Clear State
            for (int i = 0; i < listStates.size(); i++) {

                String tempStateCode = listStatesCode.get(i);
                if (strStateCode.compareTo(tempStateCode) == 0) {
                    return true;
                }

                String temp = listStates.get(i);
                if (spinnerState.getText().toString().trim().compareTo(temp) == 0) {
                    return true;
                }
            }
            spinnerState.setText("");
            strStateCode = "0";
            return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setDataInStrings() {
        if (strMode.equals(getString(R.string.individual_mode))) {
            strFirstName = editTextFirstName.getText().toString().trim();
        } else if (strMode.equals(getString(R.string.phonebook_mode))) {
            strFirstName = editTextFirstName.getText().toString().trim();
        } else if (strMode.equals(getString(R.string.edit_mode))) {
            strFirstName = editTextFirstName.getText().toString().trim();
        } else {
            strFirstName = " ";
        }

        strLastName = editTextLastName.getText().toString().trim();
        strOfficeName = editTextOfficeName.getText().toString().trim().replaceAll("'", "''");
        strPincode = editTextPin.getText().toString().trim();
        strLineOne = editTextLineOne.getText().toString().trim().replaceAll("'", "''");
        strLineTwo = editTextLineTwo.getText().toString().trim().replaceAll("'", "''");
        strCompanyName = editTextCompany.getText().toString().trim();
        strPhonebookNumber = editTextContactNumber.getText().toString();
        strEmail = editTextContactEmail.getText().toString();
        strWebsite = editTextWebsite.getText().toString();
        strGender = spinnerGender.getText().toString();
        if (strGender.equals(getString(R.string.male))) {
            strGender = "1";
        } else if (strGender.equals(getString(R.string.female))) {
            strGender = "2";
        }
        strAssignee = spinnerAssignee.getText().toString();
        lead.setAssignedUid(strAssignee);
        if (hashMapAssigneecontacts.get(strAssignee) != null) {
            strAssignee = hashMapAssigneecontacts.get(strAssignee);
        }
        strCity = editTextCity.getText().toString().trim();
        strSpecialInstructions = editTextSpecialInstructions.getText().toString();
        strCountries = spinnerCountry.getText().toString().trim();
        strState = spinnerState.getText().toString().trim();
        if (hashMapcountries.get(strCountries) != null) {
            strCountries = hashMapcountries.get(strCountries);
        }
        if (hashMapStates.get(strState) != null) {
            strState = hashMapStates.get(strState);
        }
        if (hashMapStates.get(strStateCode) != null) {
            strStateCode = hashMapStates.get(strStateCode);
        }
        strSchedule = strScheduleChecked;

        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (checkBoxFollowUp.isChecked()) {
            try {
                Date startDate = formatter.parse(editTextScheduledTime.getText().toString());
                Date endDate = formatter.parse(editTextAlertTime.getText().toString());
                strScheduleDate = targetFormat.format(startDate);
                strAlertTime = targetFormat.format(endDate);
            } catch (ParseException e) {
                strScheduleDate = "";
                strAlertTime = "";
                e.printStackTrace();
            }
        }
        // strScheduleDate = strScheduleOnlyDate + " " + strFromTime;
        // strAlertTime = strAlertOnlyDate + " " + strToTime;

        strFollowUpAssignee = spinnerFollowupAssignee.getText().toString();
        if (hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee) != null) {
            strFollowUpAssignee = hashMapFollowUpAssigneecontacts.get(strFollowUpAssignee);
        }
        Log.d("follow up assignee1 ", strFollowUpAssignee);

        strContactPerson = spinnerContactPerson.getText().toString();
        if (strContactPerson.equals(getString(R.string.nothing_selected))) {
            strContactPerson = "";
        }
        //  strCommunicatedMode = spinnerCommunicationMode.getText().toString();
        /*if (strCommunicatedMode.equals(getString(R.string.phone_call))) {
            strCommunicatedMode = "1";
        } else if (strCommunicatedMode.equals(getString(R.string.skype_call))) {
            strCommunicatedMode = "3";
        } else if (strCommunicatedMode.equals(getString(R.string.person_meeting))) {
            strCommunicatedMode = "2";
        }*/

        if (!strMode.equals(getString(R.string.edit_mode))) {
            if (strMode.equals(getString(R.string.company_mode))) {
                lead.setName(strCompanyName);
                lead.setCompanyName(strCompanyName);
                lead.setLeadPersonType("1");
                lead.setFname("");
                lead.setLname("");
            } else {
                lead.setName(strFirstName + " " + strLastName);
                lead.setLeadPersonType("2");
                lead.setCompanyName("");
                lead.setFname(strFirstName);
                lead.setLname(strLastName);
            }
        }

    }

    // method to get the data for edit lead
    private void getUserDataFromDB() {
        if (strLeadId == null || strLeadId.equals("null") || strLeadId.isEmpty()) {
            listEditLeadData = db.getLead(strId, getString(R.string.syncID));
        } else {
            listEditLeadData = db.getLead(strLeadId, getString(R.string.leaid));
        }
    }

    // method to set the data for edit lead
    private void setDataForEditLead() {
        try {
            strFirstName = listEditLeadData.getFname();
            strLastName = listEditLeadData.getLname();
            strCompanyName = listEditLeadData.getName();
            strEmail = listEditLeadData.getEmail();
            strWebsite = listEditLeadData.getWebsite();
            strPhonebookNumber = listEditLeadData.getMobile();

            if (listEditLeadData.getLeadPersonType() != null) {
                if (listEditLeadData.getLeadPersonType().equals("2")) {
                    editTextFirstName.setText(strFirstName);
                    editTextLastName.setText(strLastName);
                    lead.setName(editTextFirstName.getText().toString() + " " + editTextLastName.getText().toString());
                    lead.setLeadPersonType("2");
                    lead.setFname(strFirstName);
                    lead.setLname(strLastName);

                } else if (listEditLeadData.getLeadPersonType().equals("1")) {
                    editTextCompany.setText(strCompanyName);
                    editTextWebsite.setText(strWebsite);
                    lead.setName(strCompanyName);
                    lead.setLeadPersonType("1");
                }


                editTextContactNumber.setText(listEditLeadData.getMobile());
                editTextContactEmail.setText(strEmail);


                if (listEditLeadData.getGenderid() != null)
                    if (listEditLeadData.getGenderid().equals("1")) {
                        strGender = getString(R.string.male);
                    } else if (listEditLeadData.getGenderid().equals("2")) {
                        strGender = getString(R.string.female);
                    }
                spinnerGender.setText((strGender));

                editTextSpecialInstructions.setText((listEditLeadData.getSpecialInstructions()));
                if (listEditLeadData.getShippingAddress().size() > 0) {
                    editTextLineOne.setText(listEditLeadData.getShippingAddress().get(0).getLine1());
                    editTextLineTwo.setText(listEditLeadData.getShippingAddress().get(0).getLine2());
                    if (listEditLeadData.getShippingAddress().get(0).getZipCode() != null &&
                            !listEditLeadData.getShippingAddress().get(0).getZipCode().isEmpty() &&
                            !listEditLeadData.getShippingAddress().get(0).getZipCode().equals("0")) {
                        editTextPin.setText(listEditLeadData.getShippingAddress().get(0).getZipCode());
                    }
                    editTextCity.setText(listEditLeadData.getShippingAddress().get(0).getCity());
                    strState = listEditLeadData.getShippingAddress().get(0).getState();
                    strStateCode = listEditLeadData.getShippingAddress().get(0).getStid();
                    strCountries = listEditLeadData.getShippingAddress().get(0).getCountry();
                    strOfficeName = listEditLeadData.getShippingAddress().get(0).getSiteName();
                    //Get Shipping Address Id
                    strSAddressId = listEditLeadData.getShippingAddress().get(0).getSaid();

                    editTextOfficeName.setText(strOfficeName);

                    for (Map.Entry<String, String> e : hashMapStates.entrySet()) {
                        if (e.getValue().equals(strState)) {
                            strStateCode = e.getValue();
                        }
                    }

                    for (Map.Entry<String, String> e : hashMapStates.entrySet()) {
                        if (e.getValue().equals(strState)) {
                            strState = e.getKey();
                        }
                    }

                    spinnerState.setText(strState);

                    for (Map.Entry<String, String> e : hashMapcountries.entrySet()) {
                        if (e.getValue().equals(strCountries)) {
                            strCountries = e.getKey();
                        }
                    }
                    spinnerCountry.setText(strCountries);

                    // strState = listEditLeadData.getShippingAddress().get(0).getState();
                    //strStateCode = listEditLeadData.getShippingAddress().get(0).getStid();
                    strCountries = listEditLeadData.getShippingAddress().get(0).getCountry();
                }


                strAssignee = listEditLeadData.getAssignedUid();
                for (Map.Entry<String, String> e : hashMapAssigneecontacts.entrySet()) {
                    if (e.getValue().equals(strAssignee)) {
                        strAssignee = e.getKey();
                    }
                }

                if (strAssignee != null) {
                    if (strAssignee.equals("0")) {
                        strAssignee = "";
                    }
                }
                if (strAssignee != null && !strAssignee.equals("null")) {
                    spinnerAssignee.setText(strAssignee);
                }
                if (listEditLeadData.getFollowups().size() > 0) {
              /*  if (listEditLeadData.getFollowups().get(0).getScheduledDate() != null) {
                    //checkBoxFollowUp.setChecked(true);
                    strScheduleChecked = "1";
                }*/

                    if (listEditLeadData.getFollowups().get(0).getContactedPerson() != null) {
                        spinnerContactPerson.setText((listEditLeadData.getFollowups().get(0).getContactedPerson()));
                    } else {
                        if (listEditLeadData.getContacts().size() > 0 && listEditLeadData.getContacts() != null)
                            for (int i = 0; i < listEditLeadData.getContacts().size(); i++) {
                                if (listEditLeadData.getFollowups().get(0).getCodeid() != null) {
                                    if (listEditLeadData.getFollowups().get(0).getCodeid().equals(listEditLeadData.getContacts().get(i).getCodeid())) {
                                        spinnerContactPerson.setText((listEditLeadData.getContacts().get(i).getName()));
                                    }
                                }
                            }
                    }

                    strFollowUpAssignee = listEditLeadData.getFollowups().get(0).getAssignedUid();
                    for (Map.Entry<String, String> e : hashMapFollowUpAssigneecontacts.entrySet()) {
                        if (e.getValue().equals(strFollowUpAssignee)) {
                            strFollowUpAssignee = e.getKey();
                        }
                    }

                    spinnerFollowupAssignee.setText(strFollowUpAssignee);

                    if (listEditLeadData.getFollowups().get(0).getCommid() != null) {
                   /* if (listEditLeadData.getFollowups().get(0).getCommid().equals("1")) {
                        strCommunicatedMode = getString(R.string.phone_call);
                    } else if (listEditLeadData.getFollowups().get(0).getCommid().equals("2")) {
                        strCommunicatedMode = getString(R.string.person_meeting);
                    } else if (listEditLeadData.getFollowups().get(0).getCommid().equals("3")) {
                        strCommunicatedMode = getString(R.string.skype_call);
                    }*/
                        strCommunicatedMode = communicationModeList.get(Integer.parseInt(listEditLeadData.getFollowups().get(0).getCommid()) - 1)
                                .getCommid();
                    }

                    // spinnerCommunicationMode.setText(strCommunicatedMode);
                    if (strScheduleDate != null) {
                        strScheduleChecked = "1";
                        leadFollowUp.setFoid(listEditLeadData.getFollowups().get(0).getFoid());
                    }
                    strAlertModeChecked = listEditLeadData.getFollowups().get(0).getAlertMode();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    String stringSchDate = null, stringAlTime = null;
                    try {
                        Date startDate = formatter.parse(listEditLeadData.getFollowups().get(0).getScheduledDate());
                        Date endDate = formatter.parse(listEditLeadData.getFollowups().get(0).getAlertOn());
                        System.out.println(startDate);
                        stringSchDate = targetFormat.format(startDate);
                        stringAlTime = targetFormat.format(endDate);

                        if (listEditLeadData.getFollowups().get(0).getScheduledDate().equals("null null") || listEditLeadData.getFollowups().get(0).getAlertOn().equals("null null")) {
                            editTextScheduledTime.setText("");
                            editTextAlertTime.setText("");
                        } else if (listEditLeadData.getFollowups().get(0).getScheduledDate().equals(" null") || listEditLeadData.getFollowups().get(0).getAlertOn().equals(" null")) {
                            editTextScheduledTime.setText("");
                            editTextAlertTime.setText("");
                        } else {
                            editTextScheduledTime.setText(stringSchDate);
                            editTextAlertTime.setText(stringAlTime);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (NullPointerException ex) {
                        ex.printStackTrace();
                    }

                    if (strAlertModeChecked != null) {
                        if (strAlertModeChecked.equals("1")) {
                            if (!checkBoxEmail.isChecked()) {
                                checkBoxEmail.setChecked(true);
                            }
                        } else if (strAlertModeChecked.equals("2")) {
                            if (!checkBoxSms.isChecked()) {
                                checkBoxSms.setChecked(true);
                            }
                        } else if (strAlertModeChecked.equals("[1,2]")) {
                            if (!checkBoxSms.isChecked() && !checkBoxEmail.isChecked()) {
                                checkBoxSms.setChecked(true);
                                checkBoxEmail.setChecked(true);
                            }
                        } else {
                            checkBoxSms.setChecked(false);
                            checkBoxEmail.setChecked(false);
                        }
                    }
                }

                for (int i = 0; i < listEditLeadData.getContacts().size(); i++) {
                    contactsList.add(listEditLeadData.getContacts().get(i));
                    leadContactsPersonList.add(contactsList.get(i).getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendData() {
        long time_nano = System.nanoTime();
        long micro_seconds = time_nano / 1000;
        lead.setSyncId(strId);
        lead.setWebsite(strWebsite);
        lead.setGenderid(strGender);
        lead.setMobile(strPhonebookNumber);
        lead.setEmail(strEmail);
        Log.e("lead", strAssignee);
        if (!strMode.equals(getString(R.string.edit_mode))) {
            if (checkBoxFollowUp.isChecked()) {
                lead.setLeasid("1");
            } else {
                lead.setLeasid("1");
            }
        }

       /* if (strMode.equals(getString(R.string.edit_mode))) {
            if (lead.getLeaid() != null && db.getFollowupsCount(lead.getLeaid()) == 0) {
                lead.setLeasid("1");
            } else {
                lead.setLeasid("2");
            }
        } else {
            lead.setLeasid("1");
        }*/
        if ((strLineTwo != null && !strLineTwo.isEmpty()) || (strLineOne != null) && !strLineOne.isEmpty()) {
            lead.setOffAddr(strLineOne + ", " + strLineTwo);
        } else {
            lead.setOffAddr("");
        }

        String specialInstruction = strSpecialInstructions.replaceAll("'", "''");
        lead.setSpecialInstructions(specialInstruction);

        if (listEditLeadData.getLeadPersonType() != null) {
            if (listEditLeadData.getLeadPersonType().equals("2")) {
                strFirstName = editTextFirstName.getText().toString();
                strLastName = editTextLastName.getText().toString();
                lead.setName(strFirstName + " " + strLastName);
                lead.setFname(strFirstName);
                lead.setLname(strLastName);
            } else if (listEditLeadData.getLeadPersonType().equals("1")) {
                strCompanyName = editTextCompany.getText().toString();
                lead.setName(strCompanyName);
                lead.setFname("");
                lead.setLname("");
            }
        }

        shippingAddresses.setCity(strCity);
        shippingAddresses.setSyncID(strSyncId);
        shippingAddresses.setLeaid(strLeadId);
        shippingAddresses.setSiteName(strOfficeName);
        shippingAddresses.setState(strState);
        shippingAddresses.setCountry(strCountries);
        shippingAddresses.setZipCode(strPincode);
        shippingAddresses.setLine1(strLineOne);
        shippingAddresses.setLine2(strLineTwo);
        if (strSAddressId != null) {
            shippingAddresses.setSaid(strSAddressId);
        }
        leadShippingAddresses.add(shippingAddresses);

        leadFollowUp.setContactPerson(spinnerContactPerson.getText().toString());
        leadFollowUp.setPerson_type(lead.getLeadPersonType());
        leadFollowUp.setCommid(strCommunicatedMode);

        leadFollowUp.setFollowupCommunicationMode(strComMode);
        leadFollowUp.setAlertOn(strAlertTime);
        leadFollowUp.setScheduledDate(strScheduleDate);
        leadFollowUp.setCodeid(strCodeid);
        leadFollowUp.setAssigned_user(strFollowUpAssignee);
        Log.e("leadfollowup", strFollowUpAssignee);
        leadFollowUp.setContactPersonEmail(lead.getEmail());
        leadFollowUp.setContactPersonMobile(lead.getMobile());
        leadFollowUp.setFollowupTypeStatus(getString(R.string.followup_status_progress));
        leadFollowUp.setCreatedUser(userName);

        if (strContactPerson.equals("")) {
            leadFollowUp.setContactedPerson(getString(R.string.nothing_selected));
        } else {
            leadFollowUp.setContactedPerson(strContactPerson);
        }
        leadFollowUp.setAssignedUid(strFollowUpAssignee);
        leadFollowUp.setSyncId(strSyncId);
        leadFollowUp.setName(lead.getName());
        // leadFollowUp.setLeaid(strLeadId);
        leadFollowUp.setFosid("1");

        if (checkBoxEmail.isChecked() && !checkBoxSms.isChecked()) {
            strAlertModeChecked = "[1]";
        } else if (!checkBoxEmail.isChecked() && checkBoxSms.isChecked()) {
            strAlertModeChecked = "[2]";
        } else if (checkBoxEmail.isChecked() && checkBoxSms.isChecked()) {
            strAlertModeChecked = "[1,2]";
        } else {
            strAlertModeChecked = "";
        }
        leadFollowUp.setAlertMode(strAlertModeChecked);
        // leadFollowUp.setFollowUpAssignee(strFollowUpAssignee);

        lead.setShippingAddress(leadShippingAddresses);

        if (contactsList.size() > 0) {
            if (lead.getContacts() != null && lead.getContacts().size() > 0) {
                lead.getContacts().clear();
            }
            lead.setContacts(contactsList);

        }


        if (strMode.equals(getString(R.string.edit_mode))) {
            //Lead Sync false and Lead id is null in edit mode

            if (listEditLeadData.getLeadSync() != null && listEditLeadData.getLeadSync().equals("false") && listEditLeadData.getLeaid() == null
                    || listEditLeadData.getLeaid().equals("null")) {
                if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    lead.setSyncId(listEditLeadData.getSyncId());
                    strSyncId = listEditLeadData.getSyncId();
                    lead.setLeadSync("false");
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                    lead.setLeadId(strRepresentableLeadid);
                    if (strScheduleChecked != null && strScheduleChecked.equals("1")) {
                        leadFollowUp.setSyncId(strSyncId);
                        leadFollowUps.add(leadFollowUp);
                        lead.setFollowups(leadFollowUps);
                        db.deleteSyncLeadFollowUp(strSyncId);
                    }

                    db.updateLead(lead, getString(R.string.syncID));
                    db.deleteSyncLeadContacts(strSyncId);
                    //  db.deleteLeadAddress(strSyncId);
                    int id = 0;
                    if (lead.getContacts() != null) {
                        for (int i = 0; i < lead.getContacts().size(); i++) {
                            if (lead.getContacts().get(i).getName().equals(strContactPerson)) {
                                //TODO - Added on 20th April
                                //  if (!db.checkContactPersonExistOrNot(lead.getContacts().get(i))) {
                                if (lead.getContacts().get(i).getCodeid() != null &&
                                        !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                    List<FollowUp> upList = new ArrayList<>();
                                    upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                    if (upList != null && upList.size() > 0) {
                                        for (int j = 0; j < upList.size(); j++) {
                                            if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                                db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                        lead.getContacts().get(i).getPhoneNo(),
                                                        lead.getContacts().get(i).getEmail(),
                                                        lead.getContacts().get(i).getCodeid());
                                            }
                                        }
                                    }
                                }
                                id = db.addLeadContacts(lead.getContacts().get(i), "", strSyncId);

                            } else {
                                //TODO - Added on 20th April
                                //   if (!db.checkContactPersonExistOrNot(lead.getContacts().get(i))) {
                                if (lead.getContacts().get(i).getCodeid() != null &&
                                        !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                    List<FollowUp> upList = new ArrayList<>();
                                    upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                    if (upList != null && upList.size() > 0) {
                                        for (int j = 0; j < upList.size(); j++) {
                                            if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                                db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                        lead.getContacts().get(i).getPhoneNo(),
                                                        lead.getContacts().get(i).getEmail(),
                                                        lead.getContacts().get(i).getCodeid());
                                            }
                                        }
                                    }
                                }
                                db.addLeadContacts(lead.getContacts().get(i), "", strSyncId);

                                //   }
                            }
                        }
                    }
                    leadFollowUp.setLeadLocalId(String.valueOf(listEditLeadData.getID()));
                    leadFollowUp.setContactsId(String.valueOf(id));
                    db.updateLeadAddress(shippingAddresses, strSyncId, getString(R.string.syncID));
                    //db.addLeadAddress(shippingAddresses, strSyncId);
                    if (checkBoxFollowUp.isChecked()) {
                        db.insertFollowUpData(leadFollowUp);
                    }
                    goBackToLeadInfo();                    //setCallback();
                } else {
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                    strSyncId = listEditLeadData.getSyncId();
                    lead.setSyncId(strSyncId);
                    sendAddLeadData();
                }

            } else if (listEditLeadData.getLeadSync() != null && listEditLeadData.getLeadSync().equals("false")
                    && listEditLeadData.getLeaid() != null) {

                if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    lead.setLeadSync("false");
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                  /*  leadFollowUp.setFoid(listEditLeadData.getFollowups().get(0).getFoid());
                    leadFollowUp.setLeaid(listEditLeadData.getLeaid());
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);*/
                    lead.setLeadId(strRepresentableLeadid);
                    db.updateLead(lead, getString(R.string.leaid));
                    db.deleteLeadContacts(strLeadId);
                    //db.deleteSyncLeadAddress(strSyncId);
                    // db.deleteSyncLeadFollowUp(lead.getLeaid());
                    if (lead.getContacts() != null) {
                        for (int i = 0; i < lead.getContacts().size(); i++) {
                            //TODO - Added on 20th April
                            //   if (!db.checkContactPersonExistOrNot(lead.getContacts().get(i))) {

                            //TODO - Added on 2nd May
                            if (lead.getContacts().get(i).getCodeid() != null &&
                                    !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                List<FollowUp> upList = new ArrayList<>();
                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                if (upList != null && upList.size() > 0) {
                                    for (int k = 0; k < upList.size(); k++) {
                                        if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                            db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                    lead.getContacts().get(i).getPhoneNo(),
                                                    lead.getContacts().get(i).getEmail(),
                                                    lead.getContacts().get(i).getCodeid());
                                        }
                                    }
                                }
                            }
                            db.addLeadContacts(lead.getContacts().get(i), strLeadId, strSyncId);
                            //   }
                        }
                    }
                    db.updateLeadAddress(shippingAddresses, strLeadId, getString(R.string.leaid));
                    //db.addLeadAddress(shippingAddresses, strSyncId);
                    // db.insertFollowUpData(leadFollowUp);
                    //setCallback();
                    goBackToLeadInfo();
                    // CustomisedToast.error(getApplicationContext(),"You're not connected to internet, Please sync your lead username Lead List").show();
                } else {
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                    leadFollowUp.setLeadId(strLeadId);
                    strSyncId = listEditLeadData.getSyncId();
                    lead.setSyncId(strSyncId);
                    sendEditLeadData();
                }
            }
            //comes here in offline edit
            else if (listEditLeadData.getLeadSync() != null && listEditLeadData.getLeadSync().equals("true")
                    && listEditLeadData.getLeaid() != null) {
                if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    lead.setLeadSync("false");
                    lead.setLeaid(listEditLeadData.getLeaid());
                    lead.setLeasid(listEditLeadData.getLeasid());
                   /* leadFollowUp.setLeaid(lead.getLeaid());
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);*/
                    lead.setLeadId(strRepresentableLeadid);
                    db.updateLead(lead, getString(R.string.leaid));
                    db.deleteLeadContacts(lead.getLeaid());
                    // db.deleteSyncLeadAddress(lead.getLeaid());
                    //db.deleteSyncLeadFollowUp(lead.getLeaid());
                    //   Log.d("lead contact size", String.valueOf(lead.getContacts().size()));
                    if (lead.getContacts() != null) {
                        for (int i = 0; i < lead.getContacts().size(); i++) {
                            //TODO - Added on 20th April
                            //    if (!db.checkContactPersonExistOrNot(lead.getContacts().get(i))) {

                            //TODO - Added on 2nd May
                            if (lead.getContacts().get(i).getCodeid() != null &&
                                    !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                List<FollowUp> upList = new ArrayList<>();
                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                if (upList != null && upList.size() > 0) {
                                    for (int j = 0; j < upList.size(); j++) {
                                        if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                            db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                    lead.getContacts().get(i).getPhoneNo(),
                                                    lead.getContacts().get(i).getEmail(),
                                                    lead.getContacts().get(i).getCodeid());
                                        }
                                    }
                                }
                            }

                            db.addLeadContacts(lead.getContacts().get(i), strLeadId, strSyncId);
                            //    }
                        }
                    }
                    db.updateLeadAddress(shippingAddresses, lead.getLeaid(), getString(R.string.lead_id));
                    //  db.addLeadAddress(shippingAddresses, strLeadId);
                    //  db.insertFollowUpData(leadFollowUp);
                    // setCallback();
                    goBackToLeadInfo();
                    // CustomisedToast.error(getApplicationContext(),"You're not connected to internet, Please sync your lead username Lead List").show();
                } else {
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                    strSyncId = listEditLeadData.getSyncId();
                    //  leadFollowUp.setLeaid(lead.getLeaid());
                    lead.setSyncId(strSyncId);
                    sendEditLeadData();
                }
            } else {
                if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    lead.setLeadSync("false");
                    lead.setLeaid(listEditLeadData.getLeaid());
                    lead.setLeasid(listEditLeadData.getLeasid());
                    lead.setLeadId(strRepresentableLeadid);
                    shippingAddresses.setSyncID(strSyncId);
                    /*leadFollowUp.setSync_id(strSyncId);
                    leadFollowUp.setLeaid(lead.getLeaid());
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);*/
                    db.updateLead(lead, getString(R.string.leaid));
                    db.deleteSyncLeadContacts(lead.getLeaid());
                    //db.deleteSyncLeadAddress(lead.getLeaid());
                    // db.deleteSyncLeadFollowUp(lead.getLeaid());
                    if (lead.getContacts() != null) {
                        for (int i = 0; i < lead.getContacts().size(); i++) {
                            //TODO - Added on 20th April
                            //   if (!db.checkContactPersonExistOrNot(lead.getContacts().get(i))) {

                            //TODO - Added on 2nd May
                            if (lead.getContacts().get(i).getCodeid() != null &&
                                    !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                List<FollowUp> upList = new ArrayList<>();
                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                if (upList != null && upList.size() > 0) {
                                    for (int k = 0; k < upList.size(); k++) {
                                        if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                            db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                    lead.getContacts().get(i).getPhoneNo(),
                                                    lead.getContacts().get(i).getEmail(),
                                                    lead.getContacts().get(i).getCodeid());
                                        }
                                    }
                                }
                            }
                            db.addLeadContacts(lead.getContacts().get(i), strLeadId, strSyncId);
                            //   }
                        }
                    }
                    db.updateLeadAddress(shippingAddresses, lead.getLeaid(), getString(R.string.lead_id));
                    //db.addLeadAddress(shippingAddresses, strLeadId);
                    //db.insertFollowUpData(leadFollowUp);
                    // setCallback();
                    goBackToLeadInfo();
                    // CustomisedToast.error(getApplicationContext(),"You're not connected to internet, Please sync your lead username Lead List").show();
                } else {
                    lead.setLeaid(strLeadId);
                    lead.setLeasid(listEditLeadData.getLeasid());
                    /*leadFollowUp.setLeaid(lead.getLeaid());
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);*/
                    strSyncId = listEditLeadData.getSyncId();
                    lead.setSyncId(strSyncId);
                    sendEditLeadData();
                }
            }
            //CustomisedToast.error(getApplicationContext(),"wrong").show();
        } else {
            //first time add lead when not connected to internet
            if (NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                strSyncId = String.valueOf(micro_seconds);
                lead.setSyncId(strSyncId);
                lead.setLeadSync("false");
                shippingAddresses.setSyncID(strSyncId);
                int id = 0, localLeadId = 0;
                if (strScheduleChecked != null && strScheduleChecked.equals("1")) {
                    leadFollowUp.setSyncId(strSyncId);
                    leadFollowUp.setLeadId(lead.getLeaid());
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);
                    db.insertFollowUpData(leadFollowUp);
                }

                localLeadId = db.addLeads(lead);
                if (lead.getContacts() != null) {
                    for (int i = 0; i < lead.getContacts().size(); i++) {
                        if (lead.getContacts().get(i).getName().equals(strContactPerson)) {

                            //TODO - Added on 2nd May
                            if (lead.getContacts().get(i).getCodeid() != null &&
                                    !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                List<FollowUp> upList = new ArrayList<>();
                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                if (upList != null && upList.size() > 0) {
                                    for (int j = 0; j < upList.size(); j++) {
                                        if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                            db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                    lead.getContacts().get(i).getPhoneNo(),
                                                    lead.getContacts().get(i).getEmail(),
                                                    lead.getContacts().get(i).getCodeid());
                                        }
                                    }
                                }
                            }
                            id = db.addLeadContacts(lead.getContacts().get(i), "", strSyncId);
                        } else {

                            //TODO - Added on 2nd May
                            if (lead.getContacts().get(i).getCodeid() != null &&
                                    !lead.getContacts().get(i).getCodeid().isEmpty()) {
                                List<FollowUp> upList = new ArrayList<>();
                                upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                if (upList != null && upList.size() > 0) {
                                    for (int j = 0; j < upList.size(); j++) {
                                        if (db.checkFollowUpContactResult(lead.getContacts().get(i).getCodeid())) {
                                            db.updateFollowUpContacts(lead.getContacts().get(i).getName(),
                                                    lead.getContacts().get(i).getPhoneNo(),
                                                    lead.getContacts().get(i).getEmail(),
                                                    lead.getContacts().get(i).getCodeid());
                                        }
                                    }
                                }
                            }
                            db.addLeadContacts(lead.getContacts().get(i), "", strSyncId);
                        }
                    }
                }
                leadFollowUp.setLeadLocalId(String.valueOf(localLeadId));
                leadFollowUp.setContactsId(String.valueOf(id));
                db.addLeadAddress(shippingAddresses, strSyncId);

                setCallback();
            } else {
                strSyncId = String.valueOf(micro_seconds);
                lead.setSyncId(strSyncId);
                shippingAddresses.setSyncID(strSyncId);
                leadFollowUp.setSyncId(strSyncId);
                sendAddLeadData();
            }
        }

    }

    private void sendEditLeadData() {
        task = getString(R.string.lead_edit);
        if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        }
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        JSONArray jsonArrayContactList = new JSONArray();


        try {
            Log.e("Lead Contact List", contactsList.toString());
            if (contactsList != null && contactsList.size() > 0) {
                for (int i = 0; i < contactsList.size(); i++) {
                    JSONObject contactList = new JSONObject();
                    contactList.put(getString(R.string.codeid), contactsList.get(i).getCodeid());
                    contactList.put(getString(R.string.name), contactsList.get(i).getName());
                    contactList.put(getString(R.string.email), contactsList.get(i).getEmail());
                    contactList.put(getString(R.string.phone), contactsList.get(i).getPhoneNo());
                    contactList.put(getString(R.string.designation), contactsList.get(i).getDesignation());
                    contactList.put("is_owner", contactsList.get(i).getIsOwner());
                    //TODO Added new param for sync id
                    contactList.put(getString(R.string.sync_id), contactsList.get(i).getSyncID());
                    Log.e("name ", contactsList.toString());
                    jsonArrayContactList.put(contactList);
                    // String json = new Gson().toJson(contactsList);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ApiResponse> call;
     /*   if (strLeadId == null || strLeadId.equals("")) {
            call = apiService.editLead(version, key, task, userId, accessToken, strFirstName, strLastName, strCompanyName,
                    strPhonebookNumber, strEmail, strWebsite, strGender, strOfficeName, strLineOne, strLineTwo, strCountries, strState, strCity, strPincode,
                    strAssignee, strSpecialInstructions, jsonArrayContactList, strSchedule, strContactPerson, strCommunicatedMode, strFollowUpAssignee, strScheduleDate, strAlertTime, strAlertModeChecked, "", strSyncId, lead.getLeadPersonType());
        } else {*/
        call = apiService.editLead(version, key, task, userId, accessToken, strFirstName, strLastName, strCompanyName,
                strPhonebookNumber, strEmail, strWebsite, strGender, strOfficeName, strLineOne, strLineTwo, strCountries,
                strState, strCity, strPincode, strAssignee, strSpecialInstructions, jsonArrayContactList, strSchedule,
                strContactPerson, strCommunicatedMode, strFollowUpAssignee, strScheduleDate, strAlertTime, strAlertModeChecked,
                strLeadId, strSyncId, lead.getLeadPersonType(), strSAddressId);
        // }

        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse != null && apiResponse.getSuccess() != null) {
                    if (apiResponse.getSuccess()) {
                        progressBar.setVisibility(View.INVISIBLE);
                   /* if(apiResponse.getData().getFoid()!=null) {
                        leadFollowUp.setFoid(apiResponse.getData().getFoid());
                    }
                    leadFollowUps.add(leadFollowUp);
                    lead.setFollowups(leadFollowUps);*/
                        lead.setLeadSync("true");
                        lead.setLeadId(strRepresentableLeadid);
                        db.updateLead(lead, getString(R.string.leaid));
                        db.deleteLeadContacts(strLeadId);
                        //    db.deleteLeadAddress(strLeadId);
                        if ((strLastName == null || strLastName.isEmpty()) &&
                                (strFirstName != null && !strFirstName.isEmpty())) {
                            db.updateFollowUpName(strFirstName + "", strLeadId);
                        } else if ((strFirstName == null || strFirstName.isEmpty()) &&
                                (strLastName != null && !strLastName.isEmpty())) {
                            db.updateFollowUpName(strLastName + "", strLeadId);
                        } else if ((strLastName != null && !strLastName.isEmpty()) &&
                                (strFirstName != null && !strFirstName.isEmpty())) {
                            db.updateFollowUpName(strFirstName + " " + strLastName, strLeadId);
                        }
                        //  db.deleteLeadFollowUp(strLeadId);

                        for (Contacts contacts : apiResponse.getData().getContacts()) {
                            if (lead.getContacts() != null) {

                                //TODO - Added on 2nd May - Updated on 6th July 2k18
                                if (contacts.getCodeid() != null &&
                                        !contacts.getCodeid().isEmpty()) {
                                    List<FollowUp> upList = new ArrayList<>();
                                    upList.addAll(db.getSameContactPersonsFollowUps(contacts.getCodeid()));
                                    if (upList != null && upList.size() > 0) {
                                        for (int i = 0; i < upList.size(); i++) {
                                            if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                db.updateFollowUpContacts(contacts.getName(),
                                                        contacts.getPhoneNo(),
                                                        contacts.getEmail(),
                                                        contacts.getCodeid());
                                            }
                                        }
                                    }
                                }
                                if (contacts != null) {
                                    db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId());
                                }
                            }
                        }

                        db.updateLeadAddress(shippingAddresses, lead.getLeaid(), getString(R.string.lead_id));
                        // db.addLeadAddress(shippingAddresses, lead.getLeaid());
                        //db.insertFollowUpData(leadFollowUp);
                        // setCallback();
                        //finish();
                        goBackToLeadInfo();

                        //Refresh Followups
                        if (!NetworkUtil.getConnectivityStatusString(AddLeadActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            refreshFollowUps(AddLeadActivity.this);
                        }

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
                    else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddLeadActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddLeadActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddLeadActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("errorInLead", t.getMessage() + "");
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void goBackToLeadInfo() {
        Intent intentLeadInfo = new Intent(this, LeadInfoActivity.class);
        if (lead.getLeaid() != null && !lead.getLeaid().equals("") || lead.getSyncId() != null && !lead.getSyncId().equals("")) {
            intentLeadInfo.putExtra(getString(R.string.id), lead.getSyncId());
            intentLeadInfo.putExtra(getString(R.string.leaid), lead.getLeaid());
            intentLeadInfo.putExtra(getString(R.string.name), lead.getName());
            intentLeadInfo.putExtra(getString(R.string.lead_id), lead.getLeadId());
            //intentLeadInfo.putExtra("mobile", lead.getName());
            // intentLeadInfo.putExtra("email", lead.getName());
        } else {
            if (strSyncId != null) {
                intentLeadInfo.putExtra(getString(R.string.id), strSyncId);
            } else {
                intentLeadInfo.putExtra(getString(R.string.id), strId);
            }
            intentLeadInfo.putExtra(getString(R.string.lead_id), lead.getLeadId());
            intentLeadInfo.putExtra(getString(R.string.leaid), strLeadId);
            intentLeadInfo.putExtra(getString(R.string.name), strName);
            //intentLeadInfo.putExtra("mobile", strPhonebookNumber);
            // intentLeadInfo.putExtra("email", strEmail);
        }
        startActivity(intentLeadInfo);
        finish();
    }

    private void sendAddLeadData() {
        task = getString(R.string.lead_add);
        settings = getSharedPreferences(MyPREFERENCES, 0);
        sharedpreferences = getSharedPreferences(PREFS_NAME, 0);
        if (AppPreferences.getIsLogin(getApplicationContext(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getApplicationContext(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getApplicationContext(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        }
        progressBar.setMax(100);
        progressBar.setVisibility(View.VISIBLE);

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        JSONArray jsonArrayContactList = new JSONArray();

        try {
            Log.e("Lead Contact List", contactsList.toString());
            if (contactsList != null && contactsList.size() > 0) {
                for (int i = 0; i < contactsList.size(); i++) {
                    JSONObject contactList = new JSONObject();
                    Log.e("name ", contactsList.toString());
                    contactList.put(getString(R.string.codeid), contactsList.get(i).getCodeid());
                    contactList.put(getString(R.string.name), contactsList.get(i).getName());
                    contactList.put(getString(R.string.email), contactsList.get(i).getEmail());
                    contactList.put(getString(R.string.phone), contactsList.get(i).getPhoneNo());
                    contactList.put(getString(R.string.designation), contactsList.get(i).getDesignation());
                    contactList.put("is_owner", contactsList.get(i).getIsOwner());
                    //TODO Added new param for sync id
                    contactList.put(getString(R.string.sync_id), contactsList.get(i).getSyncID());
                    jsonArrayContactList.put(contactList);
                    // String json = new Gson().toJson(contactsList);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Call<ApiResponse> call = apiService.addLead(version, key, task, userId, accessToken, strFirstName, strLastName, strCompanyName,
                strPhonebookNumber, strEmail, strWebsite, strGender, strOfficeName, strLineOne, strLineTwo, strCountries, strState, strCity, strPincode,
                strAssignee, strSpecialInstructions, jsonArrayContactList, strSchedule, strContactPerson,
                strCommunicatedMode, strFollowUpAssignee, strScheduleDate, strAlertTime, strAlertModeChecked, strSyncId, lead.getLeadPersonType());
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (AddLeadActivity.this != null && apiResponse != null
                        && apiResponse.getSuccess() != null) {
                    if (apiResponse.getSuccess()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        lead.setLeaid(apiResponse.getData().getLeaid());
                        lead.setLeadId(apiResponse.getData().getLeadId());
                        lead.setLeadNumber(apiResponse.getData().getLeadNumber());
                        lead.setLeadSync("true");

                      /*  if (strScheduleChecked != null && strScheduleChecked.equals("1")) {
                            leadFollowUp.setLeaid(lead.getLeaid());
                            leadFollowUp.setFoid(apiResponse.getData().getFoid());
                      *//*      if (strMode.equals(getString(R.string.edit_mode))) {
                                if (lead.getLeaid() != null && db.getFollowupsCount(lead.getLeaid()) == 0) {
                                    lead.setLeasid("1");
                                } else {
                                    lead.setLeasid("2");
                                }
                            } else {
                                lead.setLeasid("1");
                            }*//*

                            lead.setLeasid("1");
                            leadFollowUps.add(leadFollowUp);
                            lead.setFollowups(leadFollowUps);
                        }*/
                        //  lead.setLeasid("1");

                        int id = 0, localLeadId = 0;
                        Log.d("leadid", lead.getLeaid());
                        boolean flagSyncForUpdate = db.checkSyncIdInLead(lead.getSyncId());
                        if (flagSyncForUpdate) {
                            //lead.setLeadId(strRepresentableLeadid);
                            db.updateLead(lead, getString(R.string.syncID));
                            db.deleteSyncLeadContacts(lead.getSyncId());
                            db.deleteSyncLeadAddress(lead.getSyncId());
                            db.deleteSyncLeadFollowUp(lead.getSyncId());
                        } else {
                            localLeadId = db.addLeads(lead);
                            //db.deleteSyncLeadContacts(lead.getLeaid());
                            //db.deleteSyncLeadAddress(lead.getLeaid());
                            //db.deleteSyncLeadFollowUp(lead.getLeaid());
                            //db.deleteLeadContacts(lead.getLeadId());
                            //  db.deleteLeadAddress(lead.getLeadId());
                            //  db.deleteLeadFollowUp(lead.getLeadId());
                        }


                        if (apiResponse.getData().getContacts() != null) {
                            for (Contacts contacts : apiResponse.getData().getContacts()) {
                                if (contacts != null) {
                                    contacts.setSyncID(strSyncId);
                                    if (contacts.getName().equals(strContactPerson)) {

                                        //TODO - Added on 2nd May
                                        if (contacts.getCodeid() != null &&
                                                !contacts.getCodeid().isEmpty()) {
                                            List<FollowUp> upList = new ArrayList<>();
                                            upList.addAll(db.getSameContactPersonsFollowUps(contacts.getCodeid()));
                                            if (upList != null && upList.size() > 0) {
                                                for (int i = 0; i < upList.size(); i++) {
                                                    if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                        db.updateFollowUpContacts(contacts.getName(),
                                                                contacts.getPhoneNo(),
                                                                contacts.getEmail(),
                                                                contacts.getCodeid());
                                                    }
                                                }
                                            }
                                        }
                                        id = db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId());
                                    } else {

                                        //TODO - Added on 2nd May
                                        if (contacts.getCodeid() != null &&
                                                !contacts.getCodeid().isEmpty()) {
                                            List<FollowUp> upList = new ArrayList<>();
                                            upList.addAll(db.getSameContactPersonsFollowUps(contacts.getCodeid()));
                                            if (upList != null && upList.size() > 0) {
                                                for (int i = 0; i < upList.size(); i++) {
                                                    if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                        db.updateFollowUpContacts(contacts.getName(),
                                                                contacts.getPhoneNo(),
                                                                contacts.getEmail(),
                                                                contacts.getCodeid());
                                                    }
                                                }
                                            }
                                        }
                                        db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId());
                                    }
                                }
                            }
                        }
                        db.addLeadAddress(shippingAddresses, lead.getLeaid());
                        leadFollowUp.setLeadLocalId(String.valueOf(localLeadId));
                        leadFollowUp.setContactsId(String.valueOf(id));
                        if (checkBoxFollowUp.isChecked()) {
                            db.insertFollowUpData(leadFollowUp);
                        }

                        //TODO Added on 15th June
                        if (apiResponse.getData().getFollowupCodeid() != null) {
                            if (db.checkFollowUpLeadResult(lead.getSyncId())) {
                                db.updateFollowUpContactsCodeId(apiResponse.getData().getFollowupCodeid(),
                                        lead.getSyncId());
                            }
                        }

                        setCallback();

                        //Refresh Followups
                        if (!NetworkUtil.getConnectivityStatusString(AddLeadActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            refreshFollowUps(AddLeadActivity.this);
                        }

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
                    else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            if (AddLeadActivity.this != null) {
                                Constants.logoutWrongCredentials(AddLeadActivity.this, apiResponse.getMessage());
                            }
                        } else {
                            if (AddLeadActivity.this != null) {
                                Toast.makeText(AddLeadActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (AddLeadActivity.this != null) {
                    Toast.makeText(AddLeadActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    Log.d("errorInLead", t.getMessage() + "");
                    progressBar.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void notifyToRemoveView(int position) {
        if (contactsList.size() == 1) {
            linearLayoutContactDetails.setVisibility(View.GONE);
        }
        if (spinnerContactPerson.getText().toString().equals(leadContactsPersonList.get(position + 1))) {
            spinnerContactPerson.setText("");
        }
        leadContactsPersonList.remove(position + 1);
        Log.d("position to remove ", String.valueOf(position + 1));
        //arrayAdapterContactPerson.notifyDataSetChanged();
        arrayAdapterContactPerson = new ArrayAdapter(this, R.layout.simple_spinner_item, leadContactsPersonList);
        arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContactPerson.setAdapter(arrayAdapterContactPerson);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyToEditContact(Contacts leadContact, int position) {

        if (contactsList != null && contactsList.size() > 0 && leadContact.getIsOwner().equals("1")) {
            for (int i = 0; i < contactsList.size(); i++) {
                contactsList.get(i).setIsOwner("0");
            }

        }

        contactsList.add(position, leadContact);
        if (leadContactsPersonList.size() > 0) {
            leadContactsPersonList.clear();
        }
        leadContactsPersonList.add(getString(R.string.nothing_selected));
        for (int i = 0; i < contactsList.size(); i++) {
            leadContactsPersonList.add(contactsList.get(i).getName());
        }


        // arrayAdapterContactPerson.notifyDataSetChanged();
        arrayAdapterContactPerson = new ArrayAdapter(this, R.layout.simple_spinner_item, leadContactsPersonList);
        arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContactPerson.setAdapter(arrayAdapterContactPerson);
        mAdapter.notifyDataSetChanged();
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
            editTextScheduledTime.setText(s);
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
            editTextAlertTime.setText(s);
            //  strAlertTime = stringEndDate;
            //TODO Added in June
            try {
                if (editTextScheduledTime.getText().toString() == null ||
                        editTextScheduledTime.getText().toString().isEmpty()) {
                    timePickerFragment.show(getSupportFragmentManager(), getString(R.string.taken_to));
                } else {

                    //Calculating Time
                    String[] strDate = editTextScheduledTime.getText().toString().split("\\s+");
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
            editTextScheduledTime.setText(editTextScheduledTime.getText().toString() + " " + stringStartTime);
            strFromTime = stringStartTime;
        } else {
            try {
                Date endDate = formatter.parse(s);
                System.out.println(endDate);
                stringEndTime = targetFormat.format(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            editTextAlertTime.setText(editTextAlertTime.getText().toString() + " " + stringEndTime);
            strToTime = stringEndTime;
        }
    }

    public void setCallback() {
        Intent resultIntent = new Intent();
        setResult(1000, resultIntent);
        finish();
    }

    //    adapter method for Assigne Contacts Adapter
    private void assigneeContactsAdapter() {
        for (String key : hashMapAssigneecontacts.keySet()) {
            assigneecontacts.add(key);

        }
        arrayAdapterAssignee = new ArrayAdapter(this, R.layout.simple_spinner_item, assigneecontacts);
        arrayAdapterAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssignee.setAdapter(arrayAdapterAssignee);
    }

    private void contactPersonAdapter() {
        leadContactsPersonList.add(getString(R.string.nothing_selected));
        arrayAdapterContactPerson = new ArrayAdapter(this, R.layout.simple_spinner_item, leadContactsPersonList);
        arrayAdapterContactPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContactPerson.setAdapter(arrayAdapterContactPerson);
    }

    private void followupAssigneeAdapter() {
        for (String key : hashMapFollowUpAssigneecontacts.keySet()) {
            followupAssigneecontacts.add(key);
        }
        ArrayAdapter arrayAdapterFollowUpAssignee = new ArrayAdapter(this, R.layout.simple_spinner_item, followupAssigneecontacts);
        arrayAdapterFollowUpAssignee.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFollowupAssignee.setAdapter(arrayAdapterFollowUpAssignee);
    }

    private void communicationModeAdapter() {
        arrayAdapterCommMode = new ArrayAdapter(this, R.layout.simple_spinner_item, communicationModeList);
        arrayAdapterCommMode.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCommunicationMode.setAdapter(arrayAdapterCommMode);
    }

    private void genderAdapter() {
        genderList.add(getString(R.string.male));
        genderList.add(getString(R.string.female));
        arrayAdapterGender = new ArrayAdapter(this, R.layout.simple_spinner_item, genderList);
        arrayAdapterGender.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(arrayAdapterGender);
    }

    private void stateAdapter() {
        try {
            listStates = new ArrayList<>();
            listStatesCode = new ArrayList<>();
            setStatesCodeData();
            for (String key : hashMapStates.keySet()) {
                listStates.add(key);
            }
            for (String value : hashMapStates.values()) {
                listStatesCode.add(value);
            }
            ArrayAdapter aa = new ArrayAdapter(this, R.layout.simple_spinner_item, listStates);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerState.setAdapter(aa);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setStatesCodeData() {
        try {
            hashMapStates.put(getString(R.string.state_andhra_pradesh), "1");
            hashMapStates.put(getString(R.string.state_arunachala_pradesh), "2");
            hashMapStates.put(getString(R.string.state_assam), "3");
            hashMapStates.put(getString(R.string.state_bihar), "4");
            hashMapStates.put(getString(R.string.state_chattisgarh), "5");
            hashMapStates.put(getString(R.string.state_goa), "6");
            hashMapStates.put(getString(R.string.state_gujarat), "7");
            hashMapStates.put(getString(R.string.state_haryana), "8");
            hashMapStates.put(getString(R.string.state_himachal_pradesh), "9");
            hashMapStates.put(getString(R.string.state_jammu_and_kashmir), "10");
            hashMapStates.put(getString(R.string.state_jarkhand), "11");
            hashMapStates.put(getString(R.string.state_andhra_pradesh), "12");
            hashMapStates.put(getString(R.string.state_kerala), "13");
            hashMapStates.put(getString(R.string.state_madya_pradesh), "14");

            hashMapStates.put(getString(R.string.state_maharashta), "15");
            hashMapStates.put(getString(R.string.state_manipur), "16");
            hashMapStates.put(getString(R.string.state_meghalaya), "17");
            hashMapStates.put(getString(R.string.state_mizoram), "18");
            hashMapStates.put(getString(R.string.state_nagaland), "19");
            hashMapStates.put(getString(R.string.state_orissa), "20");
            hashMapStates.put(getString(R.string.state_punjab), "21");
            hashMapStates.put(getString(R.string.state_sikkim), "23");
            hashMapStates.put(getString(R.string.state_tamilnadu), "24");
            hashMapStates.put(getString(R.string.state_tripura), "25");
            hashMapStates.put(getString(R.string.state_uttaranchal), "26");

            hashMapStates.put(getString(R.string.state_uttar_pradesh), "27");
            hashMapStates.put(getString(R.string.state_west_bengal), "28");
            hashMapStates.put(getString(R.string.state_andaman_and_nicobar), "29");
            hashMapStates.put(getString(R.string.state_chandigarh), "30");
            hashMapStates.put(getString(R.string.state_dadar_and_nagar), "31");
            hashMapStates.put(getString(R.string.state_daman_diu), "32");
            hashMapStates.put(getString(R.string.state_delhi), "33");
            hashMapStates.put(getString(R.string.state_lakshadeep), "34");
            hashMapStates.put(getString(R.string.state_pondicherry), "35");
            hashMapStates.put(getString(R.string.state_karnataka), "36");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void countryAdapter() {
        try {
            Locale[] locale = Locale.getAvailableLocales();
            countriesList = new ArrayList<>();
            String country, countryIso;
            for (Locale loc : locale) {
                country = loc.getDisplayCountry();
                countryIso = loc.getCountry();
                if (country.length() > 0 && !countriesList.equals(country)) {
                    hashMapcountries.put(country, countryIso);
                    countriesList.add(country);
                }
            }
            Set<String> hs = new HashSet<>();
            hs.addAll(countriesList);
            countriesList.clear();
            countriesList.addAll(hs);
            Collections.sort(countriesList, String.CASE_INSENSITIVE_ORDER);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_spinner_item, countriesList);
            spinnerCountry.setAdapter(adapter);
            spinnerCountry.setText(countriesList.get(101));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.add_lead_add_contact:
                    if (alertDialog == null || !alertDialog.isShowing()) {
                        dialogAddLeadContact();
                    }
                    break;
                case R.id.add_lead_add:
                    textViewAdd.setEnabled(false);
                    setDataInStrings();
                    if (editTextWebsite.getVisibility() == View.VISIBLE &&
                            editTextWebsite.getText().toString().trim() != null &&
                            !editTextWebsite.getText().toString().trim().isEmpty() &&
                            !isURL(editTextWebsite.getText().toString())) {
                        Toast.makeText(AddLeadActivity.this, "Please enter valid website", Toast.LENGTH_SHORT).show();
                    } else if (strEmail != null && !strEmail.isEmpty() && (!emailValidator.validateEmail(strEmail))) {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.valid_email_error)).show();
                    } else if (spinnerCountry.getText().toString().trim() == null ||
                            spinnerCountry.getText().toString().trim().isEmpty()) {
                        Toast.makeText(AddLeadActivity.this, "Please select a country", Toast.LENGTH_SHORT).show();
                    } else if ((editTextLineOne.getText().toString().trim() != null &&
                            editTextLineOne.getText().toString().trim().length() != 0) ||
                            (editTextLineTwo.getText().toString().trim() != null &&
                                    editTextLineTwo.getText().toString().trim().length() != 0) ||
                            (spinnerState.getText().toString().trim() != null &&
                                    spinnerState.getText().toString().trim().length() != 0) ||
                            (editTextCity.getText().toString().trim() != null &&
                                    editTextCity.getText().toString().trim().length() != 0) ||
                            (editTextPin.getText().toString().trim() != null &&
                                    editTextPin.getText().toString().trim().length() != 0)) {
                        if (editTextLineOne.getText().toString().trim() == null ||
                                editTextLineOne.getText().toString().trim().isEmpty()) {
                            Toast.makeText(AddLeadActivity.this, "Please enter line one address", Toast.LENGTH_SHORT).show();
                        } /*else if (editTextLineTwo.getText().toString().trim() == null ||
                            editTextLineTwo.getText().toString().trim().isEmpty()) {
                        Toast.makeText(AddLeadActivity.this, "Please enter line two address", Toast.LENGTH_SHORT).show();
                    }*/ else if (spinnerState.getText().toString().trim() == null ||
                                spinnerState.getText().toString().trim().isEmpty() || !checkStateOnFocus()) {
                            Toast.makeText(AddLeadActivity.this, "Please enter your state", Toast.LENGTH_SHORT).show();
                        } else if (editTextCity.getText().toString().trim() == null ||
                                editTextCity.getText().toString().trim().isEmpty()) {
                            Toast.makeText(AddLeadActivity.this, "Please enter city", Toast.LENGTH_SHORT).show();
                        } /*else if (editTextPin.getText().toString().trim() == null ||
                            editTextPin.getText().toString().trim().isEmpty()) {
                        Toast.makeText(AddLeadActivity.this, "Please enter pin code", Toast.LENGTH_SHORT).show();
                    }*/ else if (editTextPin.getText().toString().trim().length() != 0 &&
                                editTextPin.getText().toString().trim().length() != 6) {
                            Toast.makeText(AddLeadActivity.this, "Please enter 6 digits of pin code", Toast.LENGTH_SHORT).show();
                        } else {
                            dateValidation();
                        }
                    } else {
                        dateValidation();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            textViewAdd.setEnabled(true);
                        }
                    }, 6000);
                    break;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dateValidation() {
        try {
            if (strScheduleChecked != null && strScheduleChecked.equals("1")) {
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
                    if (strScheduleDate != null && !strScheduleDate.matches("") &&
                            !strScheduleDate.matches("null null") && strAlertTime != null
                            && !strAlertTime.matches("")
                            && !strAlertTime.matches("null null")) {
                        if (startDate.before(endDate)) {
                            CustomisedToast.error(getApplicationContext(), getString(R.string.alert_date_less_than_schedule_date)).show();
                        } else if (startDate.equals(endDate)) {
                            if (startTime.before(endTime)) {
                                CustomisedToast.error(getApplicationContext(), getString(R.string.alert_time_less_than_schedule_time)).show();
                            } else {
                                validationName();
                            }
                        } else if (startDate.before(currentDate) || endDate.before(currentDate)) {
                            CustomisedToast.error(getApplicationContext(), getString(R.string.date_greater_than_curren_date)).show();
                        } else if (startDate.equals(currentDate) || endDate.equals(currentDate)) {
                            if (startTime.before(currentTime) || endTime.before(currentTime)) {
                                CustomisedToast.error(getApplicationContext(), getString(R.string.select_time_greater_than_curren_time)).show();
                            } else {
                                validationName();
                            }
                        } else {
                            validationName();
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

            } else {
                validationName();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void validationName() {
        try {
            if (strMode.equals(getString(R.string.edit_mode))) {
                if (listEditLeadData.getLeadPersonType().equals("1")) {
                    if (!editTextCompany.getText().toString().trim().isEmpty()) {
                        sendData();
                    } else {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.enter_company_name)).show();
                    }
                } else {
                    Log.d("value", String.valueOf(editTextFirstName.getText().toString().trim().length()));
                    if (!editTextFirstName.getText().toString().trim().isEmpty()) {
                        sendData();
                    } else {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.enter_first_name)).show();
                    }

                }
            } else {
                if (strMode.equals(getString(R.string.company_mode))) {
                    if (!editTextCompany.getText().toString().trim().isEmpty()) {
                        sendData();
                    } else {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.enter_company_name)).show();
                    }
                } else {
                    if (!editTextFirstName.getText().toString().trim().isEmpty()) {
                        sendData();
                    } else {
                        CustomisedToast.error(getApplicationContext(), getString(R.string.enter_first_name)).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogAddLeadContact() {
        try {
            View dialogView = View.inflate(getApplicationContext(), R.layout.dialog_lead_add_contacts, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(dialogView)
                    .setCancelable(true);
            alertDialog = builder.create();
            final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;

            editTextName = (EditText) dialogView.findViewById(R.id.name);
            editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
            editTextEmail = (EditText) dialogView.findViewById(R.id.email);
            editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
            Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
            Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
            final CheckBox checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.contact_check_owner);
            final Contacts contacts = new Contacts();

            checkBoxOwner.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        contacts.setIsOwner("1");
                    } else {
                        contacts.setIsOwner("0");
                    }
                }
            });


            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long time_nano = System.nanoTime();
                    long micro_seconds = time_nano / 1000;

                    if (editTextName.getText().toString().trim().isEmpty()) {
                        editTextName.setError(getString(R.string.enter_name));
                    } /*else if (editTextEmail.getText().toString().trim().isEmpty()) {
                    editTextEmail.setError(getString(R.string.enter_email));
                }*/ else if (editTextEmail.getText().toString().trim().length() != 0
                            && !emailValidator.validateEmail(editTextEmail.getText().toString())) {
                        editTextEmail.setError(getString(R.string.valid_email_error));
                    } /*else if (editTextPhone.getText().toString().trim().isEmpty()) {
                    editTextPhone.setError(getString(R.string.enter_phone_number));
                }*/ else if (editTextPhone.getText().toString().trim().length() != 0 &&
                            editTextPhone.getText().toString().trim().length() != 10) {
                        editTextPhone.setError("Phone number should be of 10 digits");
                    } else {
                        contacts.setCodeid("");
                        contacts.setName(editTextName.getText().toString());
                        contacts.setDesignation(editTextDesignation.getText().toString().trim());
                        contacts.setEmail(editTextEmail.getText().toString() + "");
                        contacts.setPhoneNo(editTextPhone.getText().toString() + "");

                        //TODO Added on 28th May
                        contacts.setSyncID(String.valueOf(micro_seconds));

                        linearLayoutContactDetails.setVisibility(View.VISIBLE);
                        leadContactsPersonList.add(contacts.getName());

                        if (checkBoxOwner.isChecked()) {
                            contacts.setIsOwner("1");
                        } else {
                            contacts.setIsOwner("0");
                        }

                        if (contactsList != null && contactsList.size() > 0 && contacts.getIsOwner().equals("1")) {
                            for (int i = 0; i < contactsList.size(); i++) {
                                contactsList.get(i).setIsOwner("0");
                            }
                            contacts.setIsOwner("1");
                        }
                        contactsList.add(contacts);
                        mAdapter.notifyDataSetChanged();
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
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
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (strMode.equals(getString(R.string.edit_mode))) {
                goBackToLeadInfo();
            } else {
                super.onBackPressed();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
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

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (AddLeadActivity.this != null) {
                        Toast.makeText(AddLeadActivity.this, getString(R.string.connect_server_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
