package com.accrete.sixorbit.activity.customers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
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
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.AddCustomerContactsAdapter;
import com.accrete.sixorbit.adapter.ReferenceByAutoCompleteAdapter;
import com.accrete.sixorbit.adapter.ReferredByCustomersAdapter;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.AccountGroup;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.AttributsPtc;
import com.accrete.sixorbit.model.CityName;
import com.accrete.sixorbit.model.ContactDetail;
import com.accrete.sixorbit.model.ContactPersonArr;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.CountryList;
import com.accrete.sixorbit.model.CustomerSalesType;
import com.accrete.sixorbit.model.CustomersType;
import com.accrete.sixorbit.model.ReferralAddContact;
import com.accrete.sixorbit.model.Salutation;
import com.accrete.sixorbit.model.SearchRefferedDatum;
import com.accrete.sixorbit.model.StateList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.service.ContactPersonsAPI;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.GSTINValidator;
import com.accrete.sixorbit.utils.PANValidator;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class AddCustomerActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener,
        CompoundButton.OnCheckedChangeListener, AddCustomerContactsAdapter.EditContactsViewInterface, PassDateToCounsellor,
        ReferredByCustomersAdapter.ReferredByCustomersAdapterListener {
    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private TextView userTypeTextView;
    private RadioGroup userTypeRadioGroup;
    private RadioButton companyRadioBtn;
    private Date startDate;
    private RadioButton individualRadioBtn;
    private LinearLayout topLayout;
    private LinearLayout companyLayout;
    private TextInputEditText companyNameTextInputEditText;
    private TextInputEditText websiteTextInputEditText;
    private LinearLayout individualLayout;
    private AutoCompleteTextView salutationAutoCompleteTextView;
    private TextInputEditText firstNameTextInputEditText;
    private TextInputEditText lastNameTextInputEditText;
    private TextView genderTextView;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioBtn;
    private RadioButton femaleRadioBtn;
    private LinearLayout numberLayout;
    private TextInputEditText telephoneTextInputEditText;
    private TextInputEditText mobileNumberTextInputEditText;
    private LinearLayout emailGstinLayout;
    private TextInputEditText emailTextInputEditText;
    private TextInputEditText gstinTextInputEditText;
    private LinearLayout softCreditLayout;
    private TextInputEditText softCreditLimitTextInputEditText;
    private TextInputEditText softCreditDaysTextInputEditText;
    private LinearLayout hardCreditLayout;
    private TextInputEditText hardCreditLimitTextInputEditText;
    private TextInputEditText hardCreditDaysTextInputEditText;
    private TextView collectionDateTextView;
    private TextView collectionDateValueTextView;
    private LinearLayout panTinLayout;
    private TextInputEditText panTextInputEditText;
    private TextInputEditText tinTextInputEditText;
    private TextInputEditText cstTextInputEditText;
    // private TextInputLayout referredByTextInputLayout;
    //private AutoCompleteTextView referredByAutoCompleteTextView;
    private TextView referredByTitleTextView;
    private EditText referredByValueEditText;
    private ImageButton clearReferredByImageButton;
    private TextView referenceContactPersonTextView;
    private Spinner referenceContactPersonSpinner;
    private TextView customerTypeTextView;
    private Spinner customerTypeSpinner;
    private TextView customerSalesTypeTextView;
    private Spinner customerSalesTypeSpinner;
    private LinearLayout addContactPersonLayout;
    private LinearLayout addContactDetailsLayout;
    private TextView nameTextView;
    private TextView detailsTextView;
    private RecyclerView addContactRecyclerView;
    private LinearLayout addContactLayout;
    private LinearLayout addressLayout;
    private LinearLayout addLeadAddressDetails;
    private TextInputEditText officeNameTextInputEditText;
    private TextView addressContactFNameTextView;
    private EditText addressContactFNameEditText;
    private TextView addressContactLNameTextView;
    private EditText addressContactLNameEditText;
    private TextView addressContactNumberTextView;
    private EditText addressContactNumberEditText;
    private TextInputEditText lineOneTextInputEditText;
    private TextInputEditText lineTwoTextInputEditText;
    private AutoCompleteTextView countryAutoCompleteTextView;
    private AutoCompleteTextView stateAutocompleteTextView;
    private AutoCompleteTextView cityTextAutoCompleteTextView;
    private TextInputEditText pincodeTextInputEditText;
    private TextView addressTypeTextView;
    private Spinner addressTypeSpinner;
    private CheckBox checkBoxSiteAddress;
    private LinearLayout siteAddressLayout;
    private TextView siteAddressTitleTextView;
    private TextInputEditText siteNameTextInputEditText;
    private TextView siteAddressContactFNameTextView;
    private EditText siteAddressContactFNameEditText;
    private TextView siteAddressContactLNameTextView;
    private EditText siteAddressContactLNameEditText;
    private TextView siteAddressContactNumberTextView;
    private EditText siteAddressContactNumberEditText;
    private TextInputEditText siteAddressLineOneTextInputEditText;
    private TextInputEditText siteAddressLineTwoTextInputEditText;
    private AutoCompleteTextView siteAddressCountryAutoCompleteTextView;
    private AutoCompleteTextView siteAddressStateAutocompleteTextView;
    private AutoCompleteTextView siteAddressCityAutoCompleteText;
    private TextInputEditText siteAddressPincodeTextInputEditText;
    private TextView siteAddressTypeTextView;
    private Spinner siteAddressTypeSpinner;
    private TextView saveTextView;
    private String[] addressTypeArray = new String[]{"Current Address", "Delivery Address", "Both"};
    private String[] balanceTypeArray = new String[]{"Debit", "Credit"};
    private AlertDialog alertDialog;
    private EmailValidator emailValidator;
    private GSTINValidator gstinValidator;
    private PANValidator panValidator;
    private TextView contactTypeTitleTextView;
    private List<Contacts> contactsList = new ArrayList<>();
    private List<String> leadContactsPersonList = new ArrayList<>();
    private AddCustomerContactsAdapter mAdapter;
    private String strMode = "", strCountries, strState, strSiteCountries, strSiteState, strUserType, strSalutation,
            strUserFName, strUserLName, strGender, strCompanyName, strCompanyWebsite, strTelephone, strMobile,
            strEmail, strGSTIN, strSoftCreditLimit, strSoftCreditDays, strHardCreditLimit, strHardCreditDays, strPan, strCST,
            strTin, strReferredBy, strReferenceContactPerson, strCustomerType, strCustomerSalesType, strLedgername,
            strLedgerAlias, strLedgerGroup, strOpeningBalance, strOpeningBalanceType, strOfficeName, strOfficePersonFName,
            strOfficePersonLName, strOfficePersonPhoneNumber, strOfficeLineOne, strOfficeLineTwo, strOfficeCountry,
            strOfficeState, strOfficeCity, strOfficePinCode, strOfficeType, strSiteName, strSitePersonFName,
            strSitePersonLName, strSitePersonPhoneNumber, strSiteLineOne, strSiteLineTwo, strSiteCountry,
            strSiteStates, strSiteCity, strSitePinCode, strSiteType, cuId = "", referredByName, strAddSiteAddress = "",
            strReferralType, strCollectionDate, stringStartDate, strStartDate,
    //Keys of PAN, CST & TIN are dynamic
    editCuId, defaultPANKey, defaultCSTKey, defaultTINKey;
    private int defaultGroupId;
    private String defaultAddressCountryCode, defaultAddressStateCode, defaultSiteAddressCountryCode,
            defaultSiteAddressStateCode, addressCountryCode, siteAddressCountryCode, addressStateCode, siteAddressStateCode;
    private HashMap<String, String> hashMapcountries = new HashMap<String, String>(),
            hashMapStates = new HashMap<String, String>();
    private ArrayList<String> countriesList;
    private ArrayList<String> statesList;
    private List<Salutation> salutationArrayList = new ArrayList<>();
    private List<CustomersType> customersTypeArrayList = new ArrayList<>();
    private List<CustomerSalesType> customerSalesTypeArrayList = new ArrayList<>();
    private List<AccountGroup> accountGroupArrayList = new ArrayList<>();
    private List<ContactDetail> contactDetailArrayList = new ArrayList<>();
    private List<CityName> cityNameArrayList = new ArrayList<>();
    private List<ReferralAddContact> contactPersonTypeArrayList = new ArrayList<>();
    private List<CountryList> countryListArrayList = new ArrayList<>();
    private List<StateList> stateListArrayList = new ArrayList<>();
    private ArrayList<SearchRefferedDatum> refferedDatumArrayList = new ArrayList<>();
    private String status;
    private ReferenceByAutoCompleteAdapter referenceByAutoCompleteAdapter;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ArrayAdapter<ContactDetail> contactDetailArrayAdapter;
    private ArrayAdapter<Salutation> salutationArrayAdapter;
    private ArrayAdapter<CityName> cityNameArrayAdapter, siteCityNameArrayAdapter;
    private DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
    private AllDatePickerFragment allDatePickerFragment;
    private TextInputLayout mobileTextInputLayout;
    private TextView mobileTitleTextView, countryTitleTextView, siteCountryTitleTextView, stateTitleTextView,
            siteStateTitleTextView;
    private LinearLayout addNewReferenceLayout;
    private RecyclerView getCustomersProductsRecyclerView;
    private Dialog dialog;
    private AutoCompleteTextView customerSearchEditText;
    private ReferredByCustomersAdapter referredByCustomersAdapter;
    private ArrayList<SearchRefferedDatum> customerSearchArrayList = new ArrayList<>();
    private int quotationRequestCode = 0;
    private DatabaseHandler databaseHandler;

    //Validate Website URL
    public static boolean isURL(String url) {
        Pattern p = Pattern.compile(AppUtils.WEBSITE_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(url.toLowerCase());
        return m.matches();
    }

    @Override
    public void passDate(String s) {
        stringStartDate = s;
        try {
            startDate = dateFormatter.parse(stringStartDate);
            System.out.println(startDate);
            String from = dateFormatter.format(startDate);
            collectionDateValueTextView.setText(from);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHandler = new DatabaseHandler(AddCustomerActivity.this);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.request_code_string))) {
            quotationRequestCode = getIntent().getIntExtra(getString(R.string.request_code_string), 0);
        }

        emailValidator = new EmailValidator();
        gstinValidator = new GSTINValidator();
        panValidator = new PANValidator();
        findViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        countryTitleTextView = (TextView) findViewById(R.id.country_title_textView);
        siteCountryTitleTextView = (TextView) findViewById(R.id.site_country_title_textView);
        stateTitleTextView = (TextView) findViewById(R.id.state_title_textView);
        siteStateTitleTextView = (TextView) findViewById(R.id.site_state_title_textView);
        mobileTitleTextView = (TextView) findViewById(R.id.mobile_title_textView);
        mobileTextInputLayout = (TextInputLayout) findViewById(R.id.mobile_textInputLayout);
        userTypeTextView = (TextView) findViewById(R.id.userType_textView);
        userTypeRadioGroup = (RadioGroup) findViewById(R.id.userType_radioGroup);
        companyRadioBtn = (RadioButton) findViewById(R.id.company_radioBtn);
        individualRadioBtn = (RadioButton) findViewById(R.id.individual_radioBtn);
        topLayout = (LinearLayout) findViewById(R.id.top_layout);
        companyLayout = (LinearLayout) findViewById(R.id.company_layout);
        companyNameTextInputEditText = (TextInputEditText) findViewById(R.id.companyName_textInputEditText);
        websiteTextInputEditText = (TextInputEditText) findViewById(R.id.website_textInputEditText);
        individualLayout = (LinearLayout) findViewById(R.id.individual_layout);
        salutationAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.salutation_autoCompleteTextView);
        firstNameTextInputEditText = (TextInputEditText) findViewById(R.id.firstName_textInputEditText);
        lastNameTextInputEditText = (TextInputEditText) findViewById(R.id.lastName_textInputEditText);
        genderTextView = (TextView) findViewById(R.id.gender_textView);
        genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radioGroup);
        maleRadioBtn = (RadioButton) findViewById(R.id.male_radioBtn);
        femaleRadioBtn = (RadioButton) findViewById(R.id.female_radioBtn);
        numberLayout = (LinearLayout) findViewById(R.id.number_layout);
        telephoneTextInputEditText = (TextInputEditText) findViewById(R.id.telephone_textInputEditText);
        mobileNumberTextInputEditText = (TextInputEditText) findViewById(R.id.mobile_number_textInputEditText);
        emailGstinLayout = (LinearLayout) findViewById(R.id.email_gstin_layout);
        emailTextInputEditText = (TextInputEditText) findViewById(R.id.email_textInputEditText);
        gstinTextInputEditText = (TextInputEditText) findViewById(R.id.gstin_textInputEditText);
        softCreditLayout = (LinearLayout) findViewById(R.id.soft_credit_layout);
        softCreditLimitTextInputEditText = (TextInputEditText) findViewById(R.id.softCreditLimit_textInputEditText);
        softCreditDaysTextInputEditText = (TextInputEditText) findViewById(R.id.softCreditDays_textInputEditText);
        hardCreditLayout = (LinearLayout) findViewById(R.id.hard_credit_layout);
        hardCreditLimitTextInputEditText = (TextInputEditText) findViewById(R.id.hardCreditLimit_textInputEditText);
        hardCreditDaysTextInputEditText = (TextInputEditText) findViewById(R.id.hardCreditDays_textInputEditText);
        collectionDateTextView = (TextView) findViewById(R.id.collection_date_textView);
        collectionDateValueTextView = (TextView) findViewById(R.id.collection_date_value_textView);
        panTinLayout = (LinearLayout) findViewById(R.id.pan_tin_layout);
        panTextInputEditText = (TextInputEditText) findViewById(R.id.pan_textInputEditText);
        tinTextInputEditText = (TextInputEditText) findViewById(R.id.tin_textInputEditText);
        cstTextInputEditText = (TextInputEditText) findViewById(R.id.cst_textInputEditText);
        // referredByTextInputLayout = (TextInputLayout) findViewById(R.id.referred_by_textInputLayout);
        // referredByAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.referred_by_autoCompleteTextView);
        referenceContactPersonTextView = (TextView) findViewById(R.id.reference_contact_person_textView);
        referenceContactPersonSpinner = (Spinner) findViewById(R.id.reference_contact_person_spinner);
        customerTypeTextView = (TextView) findViewById(R.id.customer_type_textView);
        customerTypeSpinner = (Spinner) findViewById(R.id.customer_type_spinner);
        customerSalesTypeTextView = (TextView) findViewById(R.id.customer_sales_type_textView);
        customerSalesTypeSpinner = (Spinner) findViewById(R.id.customer_sales_type_spinner);
        addContactPersonLayout = (LinearLayout) findViewById(R.id.add_contact_person_layout);
        addContactDetailsLayout = (LinearLayout) findViewById(R.id.add_contact_details_layout);
        nameTextView = (TextView) findViewById(R.id.name_textView);
        detailsTextView = (TextView) findViewById(R.id.details_textView);
        addContactRecyclerView = (RecyclerView) findViewById(R.id.add_contact_recyclerView);
        addContactLayout = (LinearLayout) findViewById(R.id.add_contact_layout);
        addressLayout = (LinearLayout) findViewById(R.id.address_layout);
        addLeadAddressDetails = (LinearLayout) findViewById(R.id.add_lead_address_details);
        officeNameTextInputEditText = (TextInputEditText) findViewById(R.id.office_name_textInputEditText);
        addressContactFNameTextView = (TextView) findViewById(R.id.address_contact_fName_textView);
        addressContactFNameEditText = (EditText) findViewById(R.id.address_contact_fName_editText);
        addressContactLNameTextView = (TextView) findViewById(R.id.address_contact_lName_textView);
        addressContactLNameEditText = (EditText) findViewById(R.id.address_contact_lName_editText);
        addressContactNumberTextView = (TextView) findViewById(R.id.address_contact_number_textView);
        addressContactNumberEditText = (EditText) findViewById(R.id.address_contact_number_editText);
        lineOneTextInputEditText = (TextInputEditText) findViewById(R.id.line_one_textInputEditText);
        lineTwoTextInputEditText = (TextInputEditText) findViewById(R.id.line_two_textInputEditText);
        countryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.country_autoCompleteTextView);
        stateAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.state_autocompleteTextView);
        cityTextAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.city_textInputEditText);
        pincodeTextInputEditText = (TextInputEditText) findViewById(R.id.pincode_textInputEditText);
        addressTypeTextView = (TextView) findViewById(R.id.address_type_textView);
        addressTypeSpinner = (Spinner) findViewById(R.id.address_type_spinner);
        checkBoxSiteAddress = (CheckBox) findViewById(R.id.checkBox_siteAddress);
        siteAddressLayout = (LinearLayout) findViewById(R.id.site_address_layout);
        siteAddressTitleTextView = (TextView) findViewById(R.id.site_address_title_textView);
        siteNameTextInputEditText = (TextInputEditText) findViewById(R.id.site_name_textInputEditText);
        siteAddressContactFNameTextView = (TextView) findViewById(R.id.site_address_contact_fName_textView);
        siteAddressContactFNameEditText = (EditText) findViewById(R.id.site_address_contact_fName_editText);
        siteAddressContactLNameTextView = (TextView) findViewById(R.id.site_address_contact_lName_textView);
        siteAddressContactLNameEditText = (EditText) findViewById(R.id.site_address_contact_lName_editText);
        siteAddressContactNumberTextView = (TextView) findViewById(R.id.site_address_contact_number_textView);
        siteAddressContactNumberEditText = (EditText) findViewById(R.id.site_address_contact_number_editText);
        siteAddressLineOneTextInputEditText = (TextInputEditText) findViewById(R.id.site_address_line_one_textInputEditText);
        siteAddressLineTwoTextInputEditText = (TextInputEditText) findViewById(R.id.site_address_line_two_textInputEditText);
        siteAddressCountryAutoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.site_address_country_autoCompleteTextView);
        siteAddressStateAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id.site_address_state_autocompleteTextView);
        siteAddressCityAutoCompleteText = (AutoCompleteTextView) findViewById(R.id.site_address_city_textInputEditText);
        siteAddressPincodeTextInputEditText = (TextInputEditText) findViewById(R.id.site_address_pincode_textInputEditText);
        siteAddressTypeTextView = (TextView) findViewById(R.id.site_address_type_textView);
        siteAddressTypeSpinner = (Spinner) findViewById(R.id.site_address_type_spinner);
        saveTextView = (TextView) findViewById(R.id.save_textView);
        contactTypeTitleTextView = (TextView) findViewById(R.id.contact_type_textView);
        addNewReferenceLayout = (LinearLayout) findViewById(R.id.add_new_reference_layout);
        referredByTitleTextView = (TextView) findViewById(R.id.referred_by_title_textView);
        referredByValueEditText = (EditText) findViewById(R.id.referred_by_value_editText);
        clearReferredByImageButton = (ImageButton) findViewById(R.id.clear_referredBy_imageButton);

        companyRadioBtn.setOnClickListener(this);
        individualRadioBtn.setOnClickListener(this);
        maleRadioBtn.setOnClickListener(this);
        femaleRadioBtn.setOnClickListener(this);
        addContactLayout.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        collectionDateValueTextView.setOnClickListener(this);
        addNewReferenceLayout.setOnClickListener(this);
        clearReferredByImageButton.setOnClickListener(this);
        referredByValueEditText.setOnClickListener(this);

        referredByValueEditText.clearFocus();
        addContactDetailsLayout.setVisibility(View.GONE);
        referredByValueEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (referredByValueEditText.getText().toString().trim().length() == 0) {
                        openCustomerSearchDialog();
                    }
                }
            }
        });


        //Site address's visibility
        siteAddressLayout.setVisibility(View.GONE);

        userTypeRadioGroup.setOnCheckedChangeListener(this);
        checkBoxSiteAddress.setOnCheckedChangeListener(this);

        //Mandatory collection date title
        String mainText = "Collection Date";
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        collectionDateTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));
        userTypeTextView.append(spannableStringBuilder);

        //Mobile
        mobileNumberTextInputEditText.setHint(TextUtils.concat("Mobile Number"));
        mobileTitleTextView.setText(TextUtils.concat("Mobile Number", spannableStringBuilder));

        //Country
        countryTitleTextView.setHint(TextUtils.concat("Country", spannableStringBuilder));
        siteCountryTitleTextView.setHint(TextUtils.concat("Country", spannableStringBuilder));
        siteAddressCountryAutoCompleteTextView.setHint(TextUtils.concat("Country"));
        countryAutoCompleteTextView.setHint(TextUtils.concat("Country"));

        //State
        stateTitleTextView.setHint(TextUtils.concat("State", spannableStringBuilder));
        siteStateTitleTextView.setHint(TextUtils.concat("State", spannableStringBuilder));
        siteAddressStateAutocompleteTextView.setHint(TextUtils.concat("State"));
        stateAutocompleteTextView.setHint(TextUtils.concat("State"));

        siteAddressTypeTextView.append(spannableStringBuilder);
        addressTypeTextView.append(spannableStringBuilder);

        long date = System.currentTimeMillis();
        collectionDateValueTextView.setText(dateFormatter.format(date) + "");

        //DatePicker
        allDatePickerFragment = new AllDatePickerFragment();
        allDatePickerFragment.setListener(this);


        if (getIntent() != null && getIntent().hasExtra(getString(R.string.cuid))) {
            editCuId = getIntent().getStringExtra(getString(R.string.cuid));
            getSupportActionBar().setTitle("Edit Customer");
            strMode = getString(R.string.edit);

            //Address can't get updateCount
            addressLayout.setVisibility(View.GONE);
            siteAddressLayout.setVisibility(View.GONE);
            addLeadAddressDetails.setVisibility(View.GONE);
            checkBoxSiteAddress.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle("Add Customer");
            strMode = getString(R.string.add);
            addressLayout.setVisibility(View.VISIBLE);
            siteAddressLayout.setVisibility(View.GONE);
            addLeadAddressDetails.setVisibility(View.VISIBLE);
            checkBoxSiteAddress.setVisibility(View.VISIBLE);
            userTypeRadioGroup.check(companyRadioBtn.getId());
            genderRadioGroup.check(maleRadioBtn.getId());
        }

        //RecyclerView
        mAdapter = new AddCustomerContactsAdapter(this, contactsList, contactPersonTypeArrayList,
                this, strMode);

        addContactRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        addContactRecyclerView.setLayoutManager(mLayoutManager);
        addContactRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addContactRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        addContactRecyclerView.setNestedScrollingEnabled(false);

        //Countries & states
        if (hashMapcountries.get(strCountries) != null) {
            strCountries = hashMapcountries.get(strCountries);
            strSiteCountries = hashMapcountries.get(strCountries);
        }

        //Call API
        getFieldsData();

    }

    private void getFieldsData() {
        status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getSalutations();
        } else {
            Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    private void setSpinnerAdapter() {
        //Address Type Adapter
        ArrayAdapter<String> addressArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.simple_spinner_item, addressTypeArray);
        addressArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        addressTypeSpinner.setAdapter(addressArrayAdapter);
        addressTypeSpinner.setSelection(addressTypeArray.length - 1);

        //Site Address Type Adapter
        ArrayAdapter<String> sAddressTypeArrayAdapter = new ArrayAdapter<String>
                (this, R.layout.simple_spinner_item, addressTypeArray);
        sAddressTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        siteAddressTypeSpinner.setAdapter(sAddressTypeArrayAdapter);
        siteAddressTypeSpinner.setSelection(addressTypeArray.length - 1);

        //Country & states
        //stateAdapter();
        //countryAdapter();

        //Salutation
        salutationAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = salutationArrayAdapter.getItem(position).toString();
                salutationAutoCompleteTextView.setText(selectedItem);
                salutationAutoCompleteTextView.setSelection(salutationAutoCompleteTextView.getText().toString().length());
                int pos = -1;
                for (int i = 0; i < salutationArrayList.size(); i++) {
                    if (salutationArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strSalutation = salutationArrayList.get(pos).getSalutationId();
            }
        });

        salutationAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (int i = 0; i < salutationArrayList.size(); i++) {
                        String temp = salutationArrayList.get(i).getName();
                        if (salutationAutoCompleteTextView.getText().toString().trim().compareTo(temp) == 0) {
                            return;
                        }
                    }
                    salutationAutoCompleteTextView.setText("");
                    salutationAutoCompleteTextView.setSelection(salutationAutoCompleteTextView.getText().toString().length());
                    strSalutation = "";
                }
            }

        });

        //Country

        countryAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryAutoCompleteTextView.showDropDown();
            }
        });

        final ArrayAdapter<CountryList> countryArrayAdapter = new ArrayAdapter<CountryList>
                (AddCustomerActivity.this, R.layout.simple_spinner_item, countryListArrayList);
        countryArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        countryAutoCompleteTextView.setAdapter(countryArrayAdapter);

        //when autocomplete is clicked
        countryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = countryArrayAdapter.getItem(position).toString();
                countryAutoCompleteTextView.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < countryListArrayList.size(); i++) {
                    if (countryListArrayList.get(i).getCountryName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressCountryCode = countryListArrayList.get(pos).getCountryId();
            }
        });

        siteAddressCountryAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteAddressCountryAutoCompleteTextView.showDropDown();
            }
        });

        countryAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strCountries = countryAutoCompleteTextView.getText().toString();
                    for (int i = 0; i < countryListArrayList.size(); i++) {
                        String temp = countryListArrayList.get(i).getCountryName();
                        if (strCountries.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    countryAutoCompleteTextView.setText("");
                    addressCountryCode = "";
                    setDefaultCountry();
                }
            }

        });

        siteAddressCountryAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strSiteCountries = siteAddressCountryAutoCompleteTextView.getText().toString();
                    for (int i = 0; i < countryListArrayList.size(); i++) {
                        String temp = countryListArrayList.get(i).getCountryName();
                        if (strSiteCountries.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    siteAddressCountryAutoCompleteTextView.setText("");
                    siteAddressCountryCode = "";
                    setDefaultSiteCountry();
                }
            }

        });

        stateAutocompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strState = stateAutocompleteTextView.getText().toString();
                    for (int i = 0; i < stateListArrayList.size(); i++) {
                        String temp = stateListArrayList.get(i).getStateName();
                        if (strState.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    stateAutocompleteTextView.setText("");
                    addressStateCode = "";
                    setDefaultState();
                }
            }

        });

        siteAddressStateAutocompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strSiteState = siteAddressStateAutocompleteTextView.getText().toString();
                    for (int i = 0; i < stateListArrayList.size(); i++) {
                        String temp = stateListArrayList.get(i).getStateName();
                        if (strSiteState.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    siteAddressStateAutocompleteTextView.setText("");
                    siteAddressStateCode = "";
                    setDefaultSiteState();
                }
            }

        });


        final ArrayAdapter<CountryList> siteCountryArrayAdapter = new ArrayAdapter<CountryList>
                (AddCustomerActivity.this, R.layout.simple_spinner_item, countryListArrayList);
        siteCountryArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        siteAddressCountryAutoCompleteTextView.setAdapter(siteCountryArrayAdapter);

        //when autocomplete is clicked
        siteAddressCountryAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = siteCountryArrayAdapter.getItem(position).toString();
                siteAddressCountryAutoCompleteTextView.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < countryListArrayList.size(); i++) {
                    if (countryListArrayList.get(i).getCountryName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                siteAddressCountryCode = countryListArrayList.get(pos).getCountryId();
            }
        });


        //State Address
        final ArrayAdapter<StateList> stateListArrayAdapter =
                new ArrayAdapter<StateList>(this, R.layout.simple_spinner_item, stateListArrayList);
        stateListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        stateAutocompleteTextView.setAdapter(stateListArrayAdapter);

        stateAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stateAutocompleteTextView.showDropDown();
            }
        });


        final ArrayAdapter<StateList> siteStateListArrayAdapter =
                new ArrayAdapter<StateList>(this, R.layout.simple_spinner_item, stateListArrayList);
        siteStateListArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        siteAddressStateAutocompleteTextView.setAdapter(siteStateListArrayAdapter);

        siteAddressStateAutocompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteAddressStateAutocompleteTextView.showDropDown();
            }
        });

        stateAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = stateListArrayAdapter.getItem(position).toString();
                stateAutocompleteTextView.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < stateListArrayList.size(); i++) {
                    if (stateListArrayList.get(i).getStateName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                addressStateCode = stateListArrayList.get(pos).getStateId();
            }
        });

        siteAddressStateAutocompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = siteStateListArrayAdapter.getItem(position).toString();
                siteAddressStateAutocompleteTextView.setText(selectedItem);
                siteAddressStateAutocompleteTextView.setSelection(siteAddressStateAutocompleteTextView.getText().toString().trim().length());
                int pos = -1;
                for (int i = 0; i < stateListArrayList.size(); i++) {
                    if (stateListArrayList.get(i).getStateName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                siteAddressStateCode = stateListArrayList.get(pos).getStateId();
            }
        });

        //City
        cityTextAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cityTextAutoCompleteTextView.showDropDown();
            }
        });

        //when autocomplete is clicked
        cityTextAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = cityNameArrayAdapter.getItem(position).toString();
                cityTextAutoCompleteTextView.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < cityNameArrayList.size(); i++) {
                    if (cityNameArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strOfficeCity = cityNameArrayList.get(pos).getId();
            }
        });

        cityTextAutoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (int i = 0; i < cityNameArrayList.size(); i++) {
                        String temp = cityNameArrayList.get(i).getName();
                        if (cityTextAutoCompleteTextView.getText().toString().compareTo(temp) == 0) {
                            return;
                        }
                    }
                    cityTextAutoCompleteTextView.setText("");
                    strOfficeCity = "";
                }
            }

        });

        siteAddressCityAutoCompleteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                siteAddressCityAutoCompleteText.showDropDown();
            }
        });

        //when autocomplete is clicked
        siteAddressCityAutoCompleteText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = siteCityNameArrayAdapter.getItem(position).toString();
                siteAddressCityAutoCompleteText.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < cityNameArrayList.size(); i++) {
                    if (cityNameArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strSiteCity = cityNameArrayList.get(pos).getId();
            }
        });

        siteAddressCityAutoCompleteText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    for (int i = 0; i < cityNameArrayList.size(); i++) {
                        String temp = cityNameArrayList.get(i).getName();
                        if (siteAddressCityAutoCompleteText.getText().toString().compareTo(temp) == 0) {
                            return;
                        }
                    }
                    siteAddressCityAutoCompleteText.setText("");
                    strSiteCity = "";
                }
            }

        });


        //Salutations
        Salutation salutation = new Salutation();
        salutation.setName("Select");
        salutation.setSalutationId("0");

        salutationArrayList.add(0, salutation);

        salutationArrayAdapter =
                new ArrayAdapter<Salutation>(this, R.layout.simple_spinner_item, salutationArrayList);
        salutationArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        salutationAutoCompleteTextView.setAdapter(salutationArrayAdapter);

        salutationAutoCompleteTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                salutationAutoCompleteTextView.showDropDown();
            }
        });

        //Default Selected Item
        salutationAutoCompleteTextView.setText(salutationArrayList.get(0).getName());
        salutationAutoCompleteTextView.setSelection(salutationAutoCompleteTextView.getText().toString().length());
        strSalutation = salutationArrayList.get(0).getSalutationId();


        //Customer Types
        CustomersType customersType = new CustomersType();
        customersType.setName("Select");

        customersTypeArrayList.add(0, customersType);

        ArrayAdapter<CustomersType> customersTypeArrayAdapter =
                new ArrayAdapter<CustomersType>(this, R.layout.simple_spinner_item, customersTypeArrayList);
        customersTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        customerTypeSpinner.setAdapter(customersTypeArrayAdapter);

        //Default Selected Item
        customerTypeSpinner.setSelection(0);


        //Customer Sale Types
        CustomerSalesType customerSalesType = new CustomerSalesType();
        customerSalesType.setName("Select");

        customerSalesTypeArrayList.add(0, customerSalesType);

        ArrayAdapter<CustomerSalesType> customerSalesTypeArrayAdapter =
                new ArrayAdapter<CustomerSalesType>(this, R.layout.simple_spinner_item, customerSalesTypeArrayList);
        customerSalesTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        customerSalesTypeSpinner.setAdapter(customerSalesTypeArrayAdapter);

        //Default Selected Item
        customerSalesTypeSpinner.setSelection(0);

        //Set City
        cityTextAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    if (addressCountryCode != null && !addressCountryCode.isEmpty() &&
                            addressStateCode != null && !addressStateCode.isEmpty()) {
                        getAddressCity(addressCountryCode, addressStateCode, s.toString(), "office");
                    } else {
                        getAddressCity(defaultAddressCountryCode, defaultAddressStateCode, s.toString(), "office");
                    }
                } else {
                    Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        siteAddressCityAutoCompleteText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    if (siteAddressCountryCode != null && !siteAddressCountryCode.isEmpty() &&
                            siteAddressStateCode != null && !siteAddressStateCode.isEmpty()) {
                        getAddressCity(siteAddressCountryCode, siteAddressStateCode, s.toString(), "site");
                    } else {
                        getAddressCity(defaultSiteAddressCountryCode, defaultSiteAddressStateCode, s.toString(), "site");
                    }
                } else {
                    Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //Set Countries
        setDefaultCountry();
        setDefaultSiteCountry();
        //Set States
        setDefaultState();
        setDefaultSiteState();


        if (editCuId != null && !editCuId.isEmpty()) {
            status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                getPrefilledUserData(editCuId);
            }
        }

    }

    private void setDefaultCountry() {
        for (int i = 0; i < countryListArrayList.size(); i++) {
            if (countryListArrayList.get(i).getCountryId().equals(defaultAddressCountryCode)) {
                countryAutoCompleteTextView.setText(countryListArrayList.get(i).getCountryName());
                addressCountryCode = defaultAddressCountryCode;
            }
        }
    }

    private void setDefaultSiteCountry() {
        for (int i = 0; i < countryListArrayList.size(); i++) {
            if (countryListArrayList.get(i).getCountryId().equals(defaultSiteAddressCountryCode)) {
                siteAddressCountryAutoCompleteTextView.setText(countryListArrayList.get(i).getCountryName());
                siteAddressCountryCode = defaultSiteAddressCountryCode;
            }
        }
    }

    private void setDefaultState() {
        for (int i = 0; i < stateListArrayList.size(); i++) {
            if (stateListArrayList.get(i).getStateId().equals(defaultAddressStateCode)) {
                stateAutocompleteTextView.setText(stateListArrayList.get(i).getStateName());
                addressStateCode = defaultAddressStateCode;
            }
        }
    }

    private void setDefaultSiteState() {
        for (int i = 0; i < stateListArrayList.size(); i++) {
            if (stateListArrayList.get(i).getStateId().equals(defaultSiteAddressStateCode)) {
                siteAddressStateAutocompleteTextView.setText(stateListArrayList.get(i).getStateName());
                siteAddressStateAutocompleteTextView.setSelection(siteAddressStateAutocompleteTextView.getText().toString().trim().length());
                siteAddressStateCode = defaultSiteAddressStateCode;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == companyRadioBtn) {
            // Handle clicks for companyRadioBtn
        } else if (v == individualRadioBtn) {
            // Handle clicks for individualRadioBtn
        } else if (v == maleRadioBtn) {
            // Handle clicks for maleRadioBtn
        } else if (v == femaleRadioBtn) {
            // Handle clicks for femaleRadioBtn
        } else if (v == addContactLayout) {
            dialogAddContacts(AddCustomerActivity.this);
        } else if (v == addNewReferenceLayout) {
            if (strReferredBy != null && !strReferredBy.isEmpty() && !strReferredBy.equals("")) {
                dialogAddReferenceContactPerson(AddCustomerActivity.this);
            } else {
                Toast.makeText(AddCustomerActivity.this, "Please select referred by user first.", Toast.LENGTH_SHORT).show();
            }
        } else if (v == saveTextView) {
            //addCustomer();

            //disable Button
            saveTextView.setEnabled(false);

            if (validateAndGetFormData()) {
                if (editCuId == null) {
                    status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        PostAddCustomerAsyncTask postAddCustomerAsyncTask = new PostAddCustomerAsyncTask(AddCustomerActivity.this);
                        postAddCustomerAsyncTask.execute();
                    } else {
                        Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                } else if (editCuId != null) {
                    status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        UpdateCustomerAsyncTask updateCustomerAsyncTask = new UpdateCustomerAsyncTask(AddCustomerActivity.this,
                                editCuId);
                        updateCustomerAsyncTask.execute();
                    } else {
                        Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    saveTextView.setEnabled(true);
                }
            }, 3000);

        } else if (v == collectionDateValueTextView) {
            allDatePickerFragment.show(getSupportFragmentManager(), getString(R.string.dailogue_from));
        } else if (v == referredByValueEditText) {
            if (referredByValueEditText.getText().toString().trim().length() == 0) {
                openCustomerSearchDialog();
            }
        } else if (v == clearReferredByImageButton) {
            if (referredByValueEditText.getText().toString().trim().length() != 0) {
                referredByValueEditText.setText("");
                strReferredBy = "";
                strReferenceContactPerson = "";
                strReferralType = "";
                if (contactDetailArrayList != null && contactDetailArrayList.size() > 0) {
                    contactDetailArrayList.clear();
                }
                addNewReferenceLayout.setVisibility(View.GONE);
                contactDetailArrayAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.company_radioBtn:
                companyLayout.setVisibility(View.VISIBLE);
                individualLayout.setVisibility(View.GONE);
                //   companyNameTextInputEditText.setText("");
                //  salutationAutoCompleteTextView.setText("");
                //addContactDetailsLayout.setVisibility(View.VISIBLE);
                addContactLayout.setVisibility(View.VISIBLE);
                addContactPersonLayout.setVisibility(View.VISIBLE);
                addContactRecyclerView.setVisibility(View.VISIBLE);
                if (contactsList != null && contactsList.size() > 0) {
                    addContactDetailsLayout.setVisibility(View.VISIBLE);
                    mAdapter.notifyDataSetChanged();
                    addContactRecyclerView.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.individual_radioBtn:
                companyLayout.setVisibility(View.GONE);
                individualLayout.setVisibility(View.VISIBLE);
                //     firstNameTextInputEditText.setText("");
                //     lastNameTextInputEditText.setText("");
                addContactDetailsLayout.setVisibility(View.GONE);
                addContactLayout.setVisibility(View.GONE);

                addContactPersonLayout.setVisibility(View.GONE);
                addContactRecyclerView.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            siteAddressLayout.setVisibility(View.VISIBLE);
        } else {
            siteAddressLayout.setVisibility(View.GONE);
        }
    }

    //Single Contact Person
    private void dialogAddReferenceContactPerson(Activity activity) {
        View dialogView = View.inflate(activity, R.layout.dialog_lead_add_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;
        final LinearLayout contactTypeLayout;
        TextView contactTypeTextView;
        final CheckBox checkBoxOwner;
        final Spinner contactTypeSpinner;

        editTextName = (EditText) dialogView.findViewById(R.id.name);
        editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        contactTypeLayout = (LinearLayout) dialogView.findViewById(R.id.contactType_layout);
        contactTypeTextView = (TextView) dialogView.findViewById(R.id.contact_type_textView);
        contactTypeSpinner = (Spinner) dialogView.findViewById(R.id.contact_type_spinner);
        checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.checkBox_owner);
        final TextView specifyTextView = (TextView) dialogView.findViewById(R.id.specify_textView);
        final EditText specifyEditText = (EditText) dialogView.findViewById(R.id.specify_editText);

        String simple = "Specify ";
        String colored = "*";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(simple);
        int start = spannableStringBuilder.length();
        spannableStringBuilder.append(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        specifyTextView.setText(spannableStringBuilder);

        contactTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReferralAddContact selectedContact = (ReferralAddContact) parent.getAdapter().getItem(position);
                if (selectedContact != null && selectedContact.getExtraAttribute() != null
                        && !selectedContact.getExtraAttribute().isEmpty()) {
                    specifyEditText.setVisibility(View.VISIBLE);
                    specifyTextView.setVisibility(View.VISIBLE);
                } else {
                    specifyEditText.setVisibility(View.GONE);
                    specifyTextView.setVisibility(View.GONE);
                    specifyEditText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Contacts contacts = new Contacts();


        ArrayAdapter<ReferralAddContact> contactPersonTypeArrayAdapter =
                new ArrayAdapter<ReferralAddContact>(this, R.layout.simple_spinner_item, contactPersonTypeArrayList);
        contactPersonTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactTypeSpinner.setAdapter(contactPersonTypeArrayAdapter);

        //Default Selected Item
        contactTypeSpinner.setSelection(0);

        //Make visible this layout here only
        if (contactPersonTypeArrayList != null && contactPersonTypeArrayList.size() > 1) {
            contactTypeLayout.setVisibility(View.VISIBLE);
            contactTypeTextView.setVisibility(View.VISIBLE);
        } else {
            contactTypeLayout.setVisibility(View.GONE);
            contactTypeTextView.setVisibility(View.GONE);
        }
        // checkBoxOwner.setVisibility(View.VISIBLE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().isEmpty()) {
                    editTextName.setError(getString(R.string.enter_name));
                } else if (editTextEmail.getText().toString().trim().isEmpty()) {
                    editTextEmail.setError(getString(R.string.enter_email));
                } else if (editTextEmail.getText().toString().trim().length() != 0 && !emailValidator.validateEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.valid_email_error));
                } else if (editTextPhone.getText().toString().trim().isEmpty()) {
                    editTextPhone.setError(getString(R.string.enter_phone_number));
                } else if (editTextPhone.getText().toString().trim().length() != 0 &&
                        editTextPhone.getText().toString().trim().length() != 10) {
                    editTextPhone.setError("Phone number should be of 10 digits");
                } else if (specifyEditText.getVisibility() == View.VISIBLE &&
                        specifyEditText.getText().toString().trim().length() == 0) {
                    specifyEditText.setError("Please enter specify contact type.");
                } else {
                    /*if (checkBoxOwner.isChecked()) {
                        contacts.setOwner("1");
                    } else {*/
                    contacts.setOwner("0");
                    //}
                    contacts.setCodeid("");
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                        contacts.setContactTypeId(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getAttributeId());
                        contacts.setContactTypeValue(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getCusavid());
                    } else {
                        contacts.setContactTypeId("");
                        contacts.setContactTypeValue("");
                    }
                    contacts.setCuId(strReferredBy);
                    //linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        addContactPerson(contacts);
                    } else {
                        Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }


                    // addContactDetailsLayout.setVisibility(View.VISIBLE);

                    // contactsList.add(contacts);
                    //   leadContactsPersonList.add(contacts.getName());

                    //  mAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
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

    @Override
    public void notifyToRemoveView() {
        addContactDetailsLayout.setVisibility(View.GONE);
    }

    @Override
    public void notifyToEditContact(Contacts contacts, int position) {
        //contactsList.add(position, contacts);
        //    leadContactsPersonList.add(position, contacts.getName());
        //mAdapter.notifyDataSetChanged();
        dialogEditContacts(AddCustomerActivity.this, contacts, position);
    }

    public boolean validateAndGetFormData() {
        if (companyRadioBtn.isChecked()) {
            strUserType = "1";
        } else if (individualRadioBtn.isChecked()) {
            strUserType = "2";
        }
        if (maleRadioBtn.isChecked()) {
            strGender = "1";
        } else if (femaleRadioBtn.isChecked()) {
            strGender = "2";
        }

        if (websiteTextInputEditText.getText().toString().trim() != null &&
                !websiteTextInputEditText.getText().toString().trim().isEmpty()) {
            if (!isURL(websiteTextInputEditText.getText().toString())) {
                Toast.makeText(AddCustomerActivity.this, "Please enter valid website", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        //  strSalutation = salutationAutoCompleteTextView.getText().toString().trim() + "";
        strUserFName = firstNameTextInputEditText.getText().toString().trim() + "";
        strUserLName = lastNameTextInputEditText.getText().toString().trim() + "";
        strCompanyName = companyNameTextInputEditText.getText().toString().trim() + "";
        strCompanyWebsite = websiteTextInputEditText.getText().toString().trim() + "";
        strTelephone = telephoneTextInputEditText.getText().toString().trim() + "";
        strMobile = mobileNumberTextInputEditText.getText().toString().trim() + "";
        strEmail = emailTextInputEditText.getText().toString().trim() + "";
        strGSTIN = gstinTextInputEditText.getText().toString().trim() + "";
        strSoftCreditLimit = softCreditLimitTextInputEditText.getText().toString().trim() + "";
        strSoftCreditDays = softCreditDaysTextInputEditText.getText().toString().trim() + "";
        strHardCreditLimit = hardCreditLimitTextInputEditText.getText().toString().trim() + "";
        strHardCreditDays = hardCreditDaysTextInputEditText.getText().toString().trim() + "";

        if (strHardCreditLimit.contains(",")) {
            strHardCreditLimit = strHardCreditLimit.replace(",", "");
        }
        if (strSoftCreditLimit.contains(",")) {
            strSoftCreditLimit = strSoftCreditLimit.replace(",", "");
        }

        strPan = panTextInputEditText.getText().toString().trim() + "";
        strCST = cstTextInputEditText.getText().toString().trim() + "";
        strTin = tinTextInputEditText.getText().toString().trim() + "";
        try {
            strCollectionDate = targetFormat.format(dateFormatter.parse(collectionDateValueTextView.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //strReferredBy = referredByAutoCompleteTextView.getText().toString().trim() + "";

        //strReferenceContactPerson = referenceContactPersonSpinner.getSelectedItem().toString().trim() + "";

        //Customer type and Sales type Id
        if (customerTypeSpinner.getSelectedItemPosition() != 0 && customerTypeSpinner.getSelectedItemPosition() > 0) {
            strCustomerType = customersTypeArrayList.get(customerTypeSpinner.getSelectedItemPosition()).getCutid() + "";
        } else {
            strCustomerType = "0";
        }

        if (customerSalesTypeSpinner.getSelectedItemPosition() > 0) {
            strCustomerSalesType = customerSalesTypeArrayList.get(customerSalesTypeSpinner.getSelectedItemPosition()).getCustid() + "";
        } else {
            strCustomerSalesType = "0";
        }

        //Mobile
        if (mobileNumberTextInputEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(AddCustomerActivity.this, "Please enter mobile number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobileNumberTextInputEditText.getText().toString().trim().length() != 10) {
            Toast.makeText(AddCustomerActivity.this, "Mobile number should be of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Email Address check
        if (emailTextInputEditText.getText().toString().trim().length() != 0 &&
                !emailValidator.validateEmail(emailTextInputEditText.getText().toString().trim())) {
            Toast.makeText(AddCustomerActivity.this,
                    "Please enter valid email address", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Check GSTIN is valid or not
        if (gstinTextInputEditText.getText().toString().trim().length() != 0 &&
                gstinTextInputEditText.getText().toString().trim().length() != 15) {
            Toast.makeText(AddCustomerActivity.this, "GSTIN should be of 15 digits", Toast.LENGTH_SHORT).show();
            return false;
        } else if (gstinTextInputEditText.getText().toString().trim().length() == 15 &&
                !gstinValidator.validateGSTIN(gstinTextInputEditText.getText().toString().trim())) {
            Toast.makeText(AddCustomerActivity.this, "Please enter valid GSTIN", Toast.LENGTH_SHORT).show();
            return false;
        }


        //Check PAN is valid or not
        if (panTextInputEditText.getText().toString().trim().length() != 0 &&
                panTextInputEditText.getText().toString().trim().length() != 10) {
            Toast.makeText(AddCustomerActivity.this, "PAN should be of 10 digits", Toast.LENGTH_SHORT).show();
            return false;
        } else if (panTextInputEditText.getText().toString().trim().length() == 10 &&
                !panValidator.validatePAN(panTextInputEditText.getText().toString().trim())) {
            Toast.makeText(AddCustomerActivity.this, "Please enter valid PAN", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editCuId == null) {
            strOfficeName = officeNameTextInputEditText.getText().toString().trim() + "";
            strOfficePersonFName = addressContactFNameEditText.getText().toString().trim() + "";
            strOfficePersonLName = addressContactLNameEditText.getText().toString().trim() + "";
            strOfficePersonPhoneNumber = addressContactNumberEditText.getText().toString().trim() + "";
            strOfficeLineOne = lineOneTextInputEditText.getText().toString().trim() + "";
            strOfficeLineTwo = lineTwoTextInputEditText.getText().toString().trim() + "";

            //Country
            if (addressCountryCode == null || addressCountryCode.isEmpty()) {
                strOfficeCountry = defaultAddressCountryCode;
            } else {
                strOfficeCountry = addressCountryCode;
            }

            if (siteAddressCountryCode == null || siteAddressCountryCode.isEmpty()) {
                strSiteCountry = defaultSiteAddressCountryCode;
            } else {
                strSiteCountry = siteAddressCountryCode;
            }

            //State
            if (addressStateCode == null || addressStateCode.isEmpty()) {
                strOfficeState = defaultAddressStateCode;
            } else {
                strOfficeState = addressStateCode;
            }

            if (siteAddressStateCode == null || siteAddressStateCode.isEmpty()) {
                strSiteStates = defaultSiteAddressStateCode;
            } else {
                strSiteStates = siteAddressStateCode;
            }

            //strOfficeState = stateAutocompleteTextView.getText().toString().trim() + "";
            //strOfficeCity = cityTextAutoCompleteTextView.getText().toString().trim() + "";
            strOfficePinCode = pincodeTextInputEditText.getText().toString().trim() + "";
            strOfficeType = (addressTypeSpinner.getSelectedItemPosition() + 1) + "";
            strSiteName = siteNameTextInputEditText.getText().toString().trim() + "";
            strSitePersonFName = siteAddressContactFNameEditText.getText().toString().trim() + "";
            strSitePersonLName = siteAddressContactLNameEditText.getText().toString().trim() + "";
            strSitePersonPhoneNumber = siteAddressContactNumberEditText.getText().toString().trim() + "";
            strSiteLineOne = siteAddressLineOneTextInputEditText.getText().toString().trim() + "";
            strSiteLineTwo = siteAddressLineTwoTextInputEditText.getText().toString().trim() + "";
            // strSiteCountry = siteAddressCountryAutoCompleteTextView.getText().toString().trim() + "";
            //  strSiteStates = siteAddressStateAutocompleteTextView.getText().toString().trim() + "";
            //strSiteCity = siteAddressCityAutoCompleteText.getText().toString().trim() + "";
            strSitePinCode = siteAddressPincodeTextInputEditText.getText().toString().trim() + "";

            //Office Country and state validations
            if (countryAutoCompleteTextView.getText().toString().trim().length() == 0) {
                Toast.makeText(AddCustomerActivity.this, "Please select country", Toast.LENGTH_SHORT).show();
                return false;
            } else if (stateAutocompleteTextView.getText().toString().trim().length() == 0) {
                Toast.makeText(AddCustomerActivity.this, "Please select state", Toast.LENGTH_SHORT).show();
                return false;
            }


            //Site Country and state validations
            if (checkBoxSiteAddress.isChecked()) {
                strSiteType = (siteAddressTypeSpinner.getSelectedItemPosition() + 1) + "";
                strAddSiteAddress = "1";
                if (siteAddressCountryAutoCompleteTextView.getText().toString().trim().length() == 0) {
                    Toast.makeText(AddCustomerActivity.this, "Please select site country", Toast.LENGTH_SHORT).show();
                    return false;
                } else if (siteAddressStateAutocompleteTextView.getText().toString().trim().length() == 0) {
                    Toast.makeText(AddCustomerActivity.this, "Please select site state", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } else {
                strSiteType = "";
                strAddSiteAddress = "";
            }

        }

        return true;
    }

    public void getSalutations() {
        task = getString(R.string.add_customer_dynamic_fields);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (final Salutation salutation : apiResponse.getData().getSalutations()) {
                            if (salutation != null) {
                                salutationArrayList.add(salutation);
                            }
                        }

                        for (final CustomersType customersType : apiResponse.getData().getCustomersTypes()) {
                            if (customersType != null) {
                                customersTypeArrayList.add(customersType);
                            }
                        }
                        for (final CustomerSalesType customerSalesType : apiResponse.getData().getCustomerSalesTypes()) {
                            if (customerSalesType != null) {
                                customerSalesTypeArrayList.add(customerSalesType);
                            }
                        }
                        for (final AccountGroup accountGroup : apiResponse.getData().getAccountGroups()) {
                            if (accountGroup != null) {
                                accountGroupArrayList.add(accountGroup);
                            }
                        }

                        for (final ReferralAddContact referralAddContact : apiResponse.getData().getRefereralAddContact()) {
                            if (referralAddContact != null) {
                                contactPersonTypeArrayList.add(referralAddContact);
                            }
                        }

                        for (final CountryList countryList : apiResponse.getData().getCountryList()) {
                            if (countryList != null) {
                                countryListArrayList.add(countryList);
                            }
                        }

                        for (final StateList stateList : apiResponse.getData().getStateList()) {
                            if (stateList != null) {
                                stateListArrayList.add(stateList);
                            }
                        }

                        //Adding select text in the contact type at initial position
                        ReferralAddContact referralAddContact = new ReferralAddContact();
                        referralAddContact.setValue("Select");
                        contactPersonTypeArrayList.add(0, referralAddContact);

                        if (contactPersonTypeArrayList != null && contactPersonTypeArrayList.size() > 1) {
                            contactTypeTitleTextView.setVisibility(View.VISIBLE);
                        } else {
                            contactTypeTitleTextView.setVisibility(View.GONE);
                        }

                        //defaultGroupId = apiResponse.getData().getDefaultAccountGroupId();
                        defaultAddressCountryCode = apiResponse.getData().getDefaultCountry();
                        defaultSiteAddressCountryCode = apiResponse.getData().getDefaultCountry();
                        defaultAddressStateCode = apiResponse.getData().getDefaultState();
                        defaultSiteAddressStateCode = apiResponse.getData().getDefaultState();

                        //Keys of PAN, CST, TIN
                        for (final AttributsPtc attributsPtc : apiResponse.getData().getAttributsPtc()) {
                            if (attributsPtc != null) {
                                if (attributsPtc.getName().equals("PAN")) {
                                    defaultPANKey = attributsPtc.getAttributeShowId();
                                } else if (attributsPtc.getName().contains("CST")) {
                                    defaultCSTKey = attributsPtc.getAttributeShowId();
                                } else if (attributsPtc.getName().equals("TIN")) {
                                    defaultTINKey = attributsPtc.getAttributeShowId();
                                }
                            }
                        }

                        //Spinners
                        setSpinnerAdapter();

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void searchCustomerReference(String str) {
        task = getString(R.string.add_customer_referrence_search);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO Updated on 31st May. If we need to show balance with name of customer, we have to pass balance 1 else we need to send 0
        Call<ApiResponse> call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "0");
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (refferedDatumArrayList != null && refferedDatumArrayList.size() > 0) {
                            refferedDatumArrayList.clear();
                        }
                        for (final SearchRefferedDatum searchRefferedDatum : apiResponse.getData().getSearchRefferedData()) {
                            if (searchRefferedDatum != null) {
                                refferedDatumArrayList.add(searchRefferedDatum);
                            }
                        }
                        //    refreshCustomerReferenceAutoCompleteTextView();

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void referenceContactPerson(String typeStr, String idStr) {
        task = getString(R.string.add_customer_reference_contact_person);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        if (contactDetailArrayList != null && contactDetailArrayList.size() > 0) {
            contactDetailArrayList.clear();
        }

        Call<ApiResponse> call = apiService.getReferenceContactPerson(version, key, task, userId, accessToken, typeStr, idStr);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (final ContactDetail contactDetail : apiResponse.getData().getContactDetails()) {
                            if (contactDetail != null) {
                                contactDetailArrayList.add(contactDetail);
                            }
                        }

                        //Contact Detail
                        ContactDetail contactDetail = new ContactDetail();
                        contactDetail.setName("Select");

                        contactDetailArrayList.add(0, contactDetail);

                        //Add new
                        /*if (editCuId == null) {
                            ContactDetail contactAddnew = new ContactDetail();
                            contactAddnew.setName("Add New");
                            contactDetailArrayList.add(contactDetailArrayList.size(), contactAddnew);
                        }*/
                        contactDetailArrayAdapter =
                                new ArrayAdapter<ContactDetail>(AddCustomerActivity.this,
                                        R.layout.simple_spinner_item, contactDetailArrayList);
                        contactDetailArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        referenceContactPersonSpinner.setAdapter(contactDetailArrayAdapter);

                        //Default Selected Item
                        if (editCuId == null) {
                            if (contactDetailArrayList.size() > 1) {
                                referenceContactPersonSpinner.setSelection(1);
                            } else {
                                referenceContactPersonSpinner.setSelection(0);
                            }
                        } else {
                            if (contactDetailArrayList.size() > 1) {
                                referenceContactPersonSpinner.setSelection(1);
                            } else {
                                referenceContactPersonSpinner.setSelection(0);
                            }
                        }
                        addNewReferenceLayout.setVisibility(View.VISIBLE);
                        contactDetailArrayAdapter.notifyDataSetChanged();
                        referenceContactItemClick();

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void referenceContactItemClick() {
        referenceContactPersonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editCuId == null) {
                  /*  if ((contactDetailArrayList.size() == 2 && position == 1) || (contactDetailArrayList.size() > 2
                            && position == contactDetailArrayList.size() - 1)) {
                        dialogAddReferenceContactPerson(AddCustomerActivity.this);
                    }
*/
                    if (position != 0) {
                        ContactDetail selected = (ContactDetail) parent.getAdapter().getItem(position);
                        strReferenceContactPerson = selected.getId();
                    } else {
                        strReferenceContactPerson = "";
                    }
                } else {
                    if (position == 0) {
                        strReferenceContactPerson = "";
                    } else {
                        ContactDetail selected = (ContactDetail) parent.getAdapter().getItem(position);
                        strReferenceContactPerson = selected.getId();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //Add Single Contact Person
    public void addContactPerson(final Contacts contacts) {
        task = getString(R.string.add_customer_add_contact_person);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        try {
            jsonObject = new JSONObject();
            jsonArray = new JSONArray();

            jsonObject.put("cd-name", contacts.getName());
            jsonObject.put("cd-email", contacts.getEmail());
            jsonObject.put("cd-phone-no", contacts.getPhoneNo());
            jsonObject.put("cd-id", contacts.getID());
            jsonObject.put("cd-designation", contacts.getDesignation());
            jsonObject.put("cd-is-owner", contacts.getOwner());
            jsonObject.put("cd-type-id", contacts.getContactTypeId());
            jsonObject.put("contact-type-value", contacts.getContactTypeValue());
            jsonArray.put(jsonObject);
        } catch (JSONException js) {
            js.printStackTrace();
        }
        Call<ApiResponse> call = apiService.addContactPerson(version, key, task, userId, accessToken, contacts.getCuId(),
                jsonObject.toString());
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        ContactDetail contactDetail = new ContactDetail();
                        contactDetail.setName(contacts.getName());
                        contactDetail.setId(apiResponse.getData().getCodeid());
                        contactDetailArrayList.add(contactDetail);
                        addNewReferenceLayout.setVisibility(View.VISIBLE);
                        contactDetailArrayAdapter.notifyDataSetChanged();
                        referenceContactPersonSpinner.setSelection(contactDetailArrayList.size() - 1);
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            //       searchCustomerReference(referredByAutoCompleteTextView.getText().toString().trim());
                        } else {
                            // Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Add Multiple Contact Person
    private void dialogAddContacts(Activity activity) {
        View dialogView = View.inflate(activity, R.layout.dialog_lead_add_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;
        LinearLayout contactTypeLayout;
        TextView contactTypeTextView;
        final CheckBox checkBoxOwner;
        final Spinner contactTypeSpinner;

        editTextName = (EditText) dialogView.findViewById(R.id.name);
        editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        contactTypeLayout = (LinearLayout) dialogView.findViewById(R.id.contactType_layout);
        contactTypeTextView = (TextView) dialogView.findViewById(R.id.contact_type_textView);
        contactTypeSpinner = (Spinner) dialogView.findViewById(R.id.contact_type_spinner);
        checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.contact_check_owner);
        final TextView specifyTextView = (TextView) dialogView.findViewById(R.id.specify_textView);
        final EditText specifyEditText = (EditText) dialogView.findViewById(R.id.specify_editText);

        String simple = "Specify ";
        String colored = "*";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(simple);
        int start = spannableStringBuilder.length();
        spannableStringBuilder.append(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        specifyTextView.setText(spannableStringBuilder);

        contactTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReferralAddContact selectedContact = (ReferralAddContact) parent.getAdapter().getItem(position);
                if (selectedContact != null && selectedContact.getExtraAttribute() != null
                        && !selectedContact.getExtraAttribute().isEmpty()) {
                    specifyEditText.setVisibility(View.VISIBLE);
                    specifyTextView.setVisibility(View.VISIBLE);
                } else {
                    specifyEditText.setVisibility(View.GONE);
                    specifyTextView.setVisibility(View.GONE);
                    specifyEditText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Contacts contacts = new Contacts();

        //Contact Person Types
       /* ReferralAddContact referralAddContact = new ReferralAddContact();
        referralAddContact.setValue("Select");

        if (contactPersonTypeArrayList.get(0).getCusavid() != null) {
            contactPersonTypeArrayList.add(0, referralAddContact);
        }*/

        ArrayAdapter<ReferralAddContact> contactPersonTypeArrayAdapter =
                new ArrayAdapter<ReferralAddContact>(this, R.layout.simple_spinner_item, contactPersonTypeArrayList);
        contactPersonTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactTypeSpinner.setAdapter(contactPersonTypeArrayAdapter);

        //Default Selected Item
        contactTypeSpinner.setSelection(0);

        //Make visible this layout here only
        if (contactPersonTypeArrayList != null && contactPersonTypeArrayList.size() > 1) {
            contactTypeLayout.setVisibility(View.VISIBLE);
            contactTypeTextView.setVisibility(View.VISIBLE);
        } else {
            contactTypeLayout.setVisibility(View.GONE);
            contactTypeTextView.setVisibility(View.GONE);
        }
        // checkBoxOwner.setVisibility(View.VISIBLE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().isEmpty()) {
                    editTextName.setError(getString(R.string.enter_name));
                } else if (editTextEmail.getText().toString().trim().length() != 0 &&
                        !emailValidator.validateEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.valid_email_error));
                } else if (editTextPhone.getText().toString().trim().length() != 0 &&
                        editTextPhone.getText().toString().trim().length() != 10) {
                    editTextPhone.setError("Phone number should be of 10 digits");
                } else if (specifyEditText.getVisibility() == View.VISIBLE &&
                        specifyEditText.getText().toString().trim().length() == 0) {
                    specifyEditText.setError("Please enter specify contact type.");
                } else {
                    /*if (checkBoxOwner.isChecked()) {
                        contacts.setOwner("1");
                        for (int j = 0; j < contactsList.size(); j++) {
                            contactsList.get(j).setOwner("0");
                        }
                    } else {*/
                    contacts.setOwner("0");
                    // }
                    if (checkBoxOwner.isChecked()) {
                        contacts.setCheckOwner(true);
                    } else {
                        contacts.setCheckOwner(false);
                    }
                    contacts.setCodeid("");
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                        contacts.setContactTypeId(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getAttributeId());
                        contacts.setContactTypeValue(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getCusavid());
                    } else {
                        contacts.setContactTypeId("");
                        contacts.setContactTypeValue("");
                    }
                    if (specifyEditText.getVisibility() == View.VISIBLE) {
                        contacts.setExtraAttribute(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getExtraAttribute());
                        contacts.setSpecifyText(specifyEditText.getText().toString().trim());
                        //contacts.setType(specifyEditText.getText().toString().trim());
                        contacts.setType(contactTypeSpinner.getSelectedItem().toString().trim());
                    } else {
                        if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                            contacts.setType(contactTypeSpinner.getSelectedItem().toString().trim());
                        } else {
                            contacts.setType("");
                        }
                    }
                    // contacts.setCuId(cuId);
                    //linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    addContactDetailsLayout.setVisibility(View.VISIBLE);
                    if (contacts.isCheckOwner()) {
                        for (int i = 0; i < contactsList.size(); i++) {
                            contactsList.get(i).setCheckOwner(false);
                        }
                    }
                    contactsList.add(contacts);
                    //   leadContactsPersonList.add(contacts.getName());
                    mAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
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

    //Edit Contact Person
    private void dialogEditContacts(Activity activity, final Contacts contacts, final int position) {
        View dialogView = View.inflate(activity, R.layout.dialog_lead_add_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        final EditText editTextName, editTextDesignation, editTextEmail, editTextPhone;
        LinearLayout contactTypeLayout;
        TextView contactTypeTextView, titleTextView;
        final CheckBox checkBoxOwner;
        final Spinner contactTypeSpinner;

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        editTextName = (EditText) dialogView.findViewById(R.id.name);
        editTextDesignation = (EditText) dialogView.findViewById(R.id.designation);
        editTextEmail = (EditText) dialogView.findViewById(R.id.email);
        editTextPhone = (EditText) dialogView.findViewById(R.id.phone_number);
        Button buttonAdd = (Button) dialogView.findViewById(R.id.btn_add);
        Button buttonCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        contactTypeLayout = (LinearLayout) dialogView.findViewById(R.id.contactType_layout);
        contactTypeTextView = (TextView) dialogView.findViewById(R.id.contact_type_textView);
        contactTypeSpinner = (Spinner) dialogView.findViewById(R.id.contact_type_spinner);
        checkBoxOwner = (CheckBox) dialogView.findViewById(R.id.contact_check_owner);
        final TextView specifyTextView = (TextView) dialogView.findViewById(R.id.specify_textView);
        final EditText specifyEditText = (EditText) dialogView.findViewById(R.id.specify_editText);
        titleTextView.setText("Edit Item");

        String simple = "Specify ";
        String colored = "*";
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(simple);
        int start = spannableStringBuilder.length();
        spannableStringBuilder.append(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        specifyTextView.setText(spannableStringBuilder);

        contactTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ReferralAddContact selectedContact = (ReferralAddContact) parent.getAdapter().getItem(position);
                if (selectedContact != null && selectedContact.getExtraAttribute() != null
                        && !selectedContact.getExtraAttribute().isEmpty()) {
                    specifyEditText.setVisibility(View.VISIBLE);
                    specifyTextView.setVisibility(View.VISIBLE);
                    if (contacts.getSpecifyText() != null) {
                        specifyEditText.setText(contacts.getSpecifyText() + "");
                    }
                } else {
                    specifyEditText.setVisibility(View.GONE);
                    specifyTextView.setVisibility(View.GONE);
                    specifyEditText.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextName.setText(contacts.getName());
        editTextDesignation.setText(contacts.getDesignation());
        editTextEmail.setText(contacts.getEmail());
        editTextPhone.setText(contacts.getPhoneNo());
        //Contact Person Types
       /* ReferralAddContact referralAddContact = new ReferralAddContact();
        referralAddContact.setValue("Select");

        if (contactPersonTypeArrayList.get(0).getCusavid() != null) {
            contactPersonTypeArrayList.add(0, referralAddContact);
        }*/

        ArrayAdapter<ReferralAddContact> contactPersonTypeArrayAdapter =
                new ArrayAdapter<ReferralAddContact>(this, R.layout.simple_spinner_item, contactPersonTypeArrayList);
        contactPersonTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactTypeSpinner.setAdapter(contactPersonTypeArrayAdapter);

        //Default Selected Item
        contactTypeSpinner.setSelection(0);
        for (int i = 1; i < contactPersonTypeArrayList.size(); i++) {
            if (contactPersonTypeArrayList.get(i).getCusavid().equals(contacts.getContactTypeValue())) {
                contactTypeSpinner.setSelection(i);
            }
        }

        //Make visible this layout here only
        if (contactPersonTypeArrayList != null && contactPersonTypeArrayList.size() > 1) {
            contactTypeLayout.setVisibility(View.VISIBLE);
            contactTypeTextView.setVisibility(View.VISIBLE);
        } else {
            contactTypeLayout.setVisibility(View.GONE);
            contactTypeTextView.setVisibility(View.GONE);
        }
        checkBoxOwner.setChecked(contacts.isCheckOwner());
        // checkBoxOwner.setVisibility(View.VISIBLE);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextName.getText().toString().trim().isEmpty()) {
                    editTextName.setError(getString(R.string.enter_name));
                } else if (editTextEmail.getText().toString().trim().length() != 0 &&
                        !emailValidator.validateEmail(editTextEmail.getText().toString())) {
                    editTextEmail.setError(getString(R.string.valid_email_error));
                } else if (editTextPhone.getText().toString().trim().length() != 0 &&
                        editTextPhone.getText().toString().trim().length() != 10) {
                    editTextPhone.setError("Phone number should be of 10 digits");
                } else if (specifyEditText.getVisibility() == View.VISIBLE &&
                        specifyEditText.getText().toString().trim().length() == 0) {
                    specifyEditText.setError("Please enter specify contact type.");
                } else {
                    if (checkBoxOwner.isChecked()) {
                        contacts.setCheckOwner(true);
                    } else {
                        contacts.setCheckOwner(false);
                    }
                    contacts.setOwner("0");
                    contacts.setCodeid("");
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                        contacts.setContactTypeId(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getAttributeId());
                        contacts.setContactTypeValue(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getCusavid());
                    } else {
                        contacts.setContactTypeId("");
                        contacts.setContactTypeValue("");
                    }
                    if (specifyEditText.getVisibility() == View.VISIBLE) {
                        contacts.setExtraAttribute(contactPersonTypeArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getExtraAttribute());
                        contacts.setSpecifyText(specifyEditText.getText().toString().trim());
                        //contacts.setType(specifyEditText.getText().toString().trim());
                        contacts.setType(contactTypeSpinner.getSelectedItem().toString().trim());
                    } else {
                        if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                            contacts.setType(contactTypeSpinner.getSelectedItem().toString().trim());
                        } else {
                            contacts.setType("");
                        }
                    }
                    // contacts.setCuId(cuId);
                    //linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    addContactDetailsLayout.setVisibility(View.VISIBLE);
                    if (checkBoxOwner.isChecked()) {
                        for (int i = 0; i < contactsList.size(); i++) {
                            if (i != position) {
                                contactsList.get(i).setCheckOwner(false);
                            }
                        }
                    }
                    contactsList.set(position, contacts);
                    //   leadContactsPersonList.add(contacts.getName());
                    mAdapter.notifyDataSetChanged();
                    alertDialog.dismiss();
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

    public void getAddressCity(String countryId, String stateId, String searchText, final String addressType) {
        task = getString(R.string.add_customer_get_cities);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCities(version, key, task, userId, accessToken, countryId,
                stateId, searchText);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (cityNameArrayList != null && cityNameArrayList.size() > 0) {
                            cityNameArrayList.clear();
                        }
                        for (final CityName cityName : apiResponse.getData().getCityNames()) {
                            if (cityName != null) {
                                cityNameArrayList.add(cityName);
                            }
                        }

                        if (addressType.equals("office")) {
                            cityNameArrayAdapter =
                                    new ArrayAdapter<CityName>(AddCustomerActivity.this,
                                            R.layout.simple_spinner_item, cityNameArrayList);
                            cityNameArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                            cityTextAutoCompleteTextView.setAdapter(cityNameArrayAdapter);
                        } else {
                            siteCityNameArrayAdapter =
                                    new ArrayAdapter<CityName>(AddCustomerActivity.this,
                                            R.layout.simple_spinner_item, cityNameArrayList);
                            siteCityNameArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                            siteAddressCityAutoCompleteText.setAdapter(siteCityNameArrayAdapter);
                        }

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Get Pre-filled Data of User
    public void getPrefilledUserData(String customerId) {
        task = getString(R.string.edit_customer_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCustomersData(version, key, task, userId, accessToken, customerId);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {

                        //User Type
                        if (apiResponse.getData().getCustomerInfo().getCustomerTypeRadio() != null &&
                                !apiResponse.getData().getCustomerInfo().getCustomerTypeRadio().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCustomerTypeRadio().equals("null")) {
                            if (apiResponse.getData().getCustomerInfo().getCustomerTypeRadio().equals("1")) {
                                companyRadioBtn.setChecked(true);

                                companyLayout.setVisibility(View.VISIBLE);
                                individualLayout.setVisibility(View.GONE);
                                // addContactDetailsLayout.setVisibility(View.VISIBLE);
                                addContactLayout.setVisibility(View.VISIBLE);

                                addContactPersonLayout.setVisibility(View.VISIBLE);
                                addContactRecyclerView.setVisibility(View.VISIBLE);

                            } else {
                                individualRadioBtn.setChecked(true);

                                companyLayout.setVisibility(View.GONE);
                                individualLayout.setVisibility(View.VISIBLE);
                                addContactDetailsLayout.setVisibility(View.GONE);
                                addContactLayout.setVisibility(View.GONE);

                                addContactPersonLayout.setVisibility(View.GONE);
                                addContactRecyclerView.setVisibility(View.GONE);
                            }
                            companyRadioBtn.setClickable(false);
                            companyRadioBtn.setFocusable(false);
                            companyRadioBtn.setFocusableInTouchMode(false);
                            individualRadioBtn.setClickable(false);
                            individualRadioBtn.setFocusableInTouchMode(false);
                            individualRadioBtn.setFocusable(false);
                            userTypeRadioGroup.setEnabled(false);
                        }

                        //EMail
                        if (apiResponse.getData().getCustomerInfo().getEmail() != null &&
                                !apiResponse.getData().getCustomerInfo().getEmail().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getEmail().equals("null")) {
                            emailTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getEmail());
                        }

                        //FName
                        if (apiResponse.getData().getCustomerInfo().getFname() != null &&
                                !apiResponse.getData().getCustomerInfo().getFname().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getFname().equals("null")) {
                            firstNameTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getFname());
                        }


                        //LName
                        if (apiResponse.getData().getCustomerInfo().getLname() != null &&
                                !apiResponse.getData().getCustomerInfo().getLname().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getLname().equals("null")) {
                            lastNameTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getLname());
                        }

                        //Telephone
                        if (apiResponse.getData().getCustomerInfo().getTelephone() != null &&
                                !apiResponse.getData().getCustomerInfo().getTelephone().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getTelephone().equals("null")) {
                            telephoneTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getTelephone());
                        }

                        //Mobile
                        if (apiResponse.getData().getCustomerInfo().getMobile() != null &&
                                !apiResponse.getData().getCustomerInfo().getMobile().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getMobile().equals("null")) {
                            mobileNumberTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getMobile());
                        }

                        //Soft credit limit
                        if (apiResponse.getData().getCustomerInfo().getSoftCreditLimit() != null &&
                                !apiResponse.getData().getCustomerInfo().getSoftCreditLimit().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getSoftCreditLimit().equals("null")) {
                            softCreditLimitTextInputEditText.setText(formatter.format(Double.parseDouble
                                    (apiResponse.getData().getCustomerInfo().getSoftCreditLimit())));
                        }

                        //Soft credit days
                        if (apiResponse.getData().getCustomerInfo().getSoftCreditDays() != null &&
                                !apiResponse.getData().getCustomerInfo().getSoftCreditDays().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getSoftCreditDays().equals("null")) {
                            softCreditDaysTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getSoftCreditDays());
                        }

                        //Hard credit limit
                        if (apiResponse.getData().getCustomerInfo().getHardCreditLimit() != null &&
                                !apiResponse.getData().getCustomerInfo().getHardCreditLimit().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getHardCreditLimit().equals("null")) {
                            hardCreditLimitTextInputEditText.setText(formatter.format(Double.parseDouble(
                                    apiResponse.getData().getCustomerInfo().getHardCreditLimit())));
                        }

                        //Hard credit days
                        if (apiResponse.getData().getCustomerInfo().getHardCreditDays() != null &&
                                !apiResponse.getData().getCustomerInfo().getHardCreditDays().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getHardCreditDays().equals("null")) {
                            hardCreditDaysTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getHardCreditDays());
                        }


                        //PAN
                        if (apiResponse.getData().getCustomerInfo().getPan() != null &&
                                !apiResponse.getData().getCustomerInfo().getPan().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getPan().equals("null")) {
                            panTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getPan());
                        }

                        //GSTIN
                        if (apiResponse.getData().getCustomerInfo().getGstin() != null &&
                                !apiResponse.getData().getCustomerInfo().getGstin().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getGstin().equals("null")) {
                            gstinTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getGstin());
                        }

                        //CST
                        if (apiResponse.getData().getCustomerInfo().getCst() != null &&
                                !apiResponse.getData().getCustomerInfo().getCst().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCst().equals("null")) {
                            cstTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getCst());
                        }

                        //TIN
                        if (apiResponse.getData().getCustomerInfo().getTin() != null &&
                                !apiResponse.getData().getCustomerInfo().getTin().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getTin().equals("null")) {
                            tinTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getTin());
                        }

                        //WebSite
                        if (apiResponse.getData().getCustomerInfo().getCompanyWebsite() != null &&
                                !apiResponse.getData().getCustomerInfo().getCompanyWebsite().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCompanyWebsite().equals("null")) {
                            websiteTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getCompanyWebsite());
                        }

                        //Company Name
                        if (apiResponse.getData().getCustomerInfo().getCompanyName() != null &&
                                !apiResponse.getData().getCustomerInfo().getCompanyName().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCompanyName().equals("null")) {
                            companyNameTextInputEditText.setText(apiResponse.getData().getCustomerInfo().getCompanyName());
                        }


                        //Gender
                        if (apiResponse.getData().getCustomerInfo().getGender() != null &&
                                !apiResponse.getData().getCustomerInfo().getGender().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getGender().equals("null")) {
                            if (apiResponse.getData().getCustomerInfo().getGender().equals("1")) {
                                maleRadioBtn.setChecked(true);
                            } else {
                                femaleRadioBtn.setChecked(true);
                            }
                        }

                        //Salutation
                        if (apiResponse.getData().getCustomerInfo().getSalutationId() != null &&
                                !apiResponse.getData().getCustomerInfo().getSalutationId().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getSalutationId().equals("null")) {
                            for (int i = 0; i < salutationArrayList.size(); i++) {
                                if (salutationArrayList.get(i).getSalutationId()
                                        .equals(apiResponse.getData().getCustomerInfo().getSalutationId())) {
                                    salutationAutoCompleteTextView.setText(salutationArrayList.get(i).getName());
                                    salutationAutoCompleteTextView.setSelection
                                            (salutationAutoCompleteTextView.getText().toString().length());
                                    strSalutation = salutationArrayList.get(i).getSalutationId();
                                }
                            }
                        }

                        //Customer Sales Type
                        if (apiResponse.getData().getCustomerInfo().getCustomerSaleType() != null &&
                                !apiResponse.getData().getCustomerInfo().getCustomerSaleType().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCustomerSaleType().equals("null")) {
                            for (int i = 0; i < customerSalesTypeArrayList.size(); i++) {
                                if (customerSalesTypeArrayList.get(i).getCustid() != null &&
                                        (customerSalesTypeArrayList.get(i).getCustid()
                                                .equals(apiResponse.getData().getCustomerInfo().getCustomerSaleType()))) {
                                    customerSalesTypeSpinner.setSelection(i);
                                }
                            }
                        }

                        //Customer Type
                        if (apiResponse.getData().getCustomerInfo().getCustomerTypeID() != null &&
                                !apiResponse.getData().getCustomerInfo().getCustomerTypeID().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCustomerTypeID().equals("null")) {
                            for (int i = 0; i < customersTypeArrayList.size(); i++) {
                                if (customersTypeArrayList.get(i).getCutid() != null &&
                                        (customersTypeArrayList.get(i).getCutid()
                                                .equals(apiResponse.getData().getCustomerInfo().getCustomerTypeID()))) {
                                    customerTypeSpinner.setSelection(i);
                                }
                            }
                        }

                        //Reference By
                        if (apiResponse.getData().getCustomerInfo().getReferalData() != null &&
                                !apiResponse.getData().getCustomerInfo().getReferalData().isEmpty() &&
                                apiResponse.getData().getCustomerInfo().getReferalData().get(0).getName() != null &&
                                !apiResponse.getData().getCustomerInfo().getReferalData().get(0).getName().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getReferalData().get(0).getName().equals("null")) {
                            referredByValueEditText.setText(apiResponse.getData().getCustomerInfo().getReferalData().get(0).getName());
                            strReferredBy = apiResponse.getData().getCustomerInfo().getReferalData().get(0).getId();
                            strReferralType = apiResponse.getData().getCustomerInfo().getReferalData().get(0).getType();

                            /*status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                                contactDetailArrayList.clear();
                                referenceContactPerson(strReferralType, strReferredBy);
                            } else {
                                // Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                            }*/

                            //Contact Person
                            ContactDetail contactDetail = new ContactDetail();
                            contactDetail.setName("Select");

                            contactDetailArrayList.add(0, contactDetail);
                            contactDetailArrayAdapter =
                                    new ArrayAdapter<ContactDetail>(AddCustomerActivity.this,
                                            R.layout.simple_spinner_item, contactDetailArrayList);
                            contactDetailArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                            referenceContactPersonSpinner.setAdapter(contactDetailArrayAdapter);

                            if (apiResponse.getData().getCustomerInfo().getContactPersonArr() != null) {
                                for (ContactPersonArr contactPersonArr :
                                        apiResponse.getData().getCustomerInfo().getContactPersonArr()) {
                                    ContactDetail newContactsDetail = new ContactDetail();
                                    newContactsDetail.setName(contactPersonArr.getName());
                                    newContactsDetail.setId(contactPersonArr.getId());
                                    newContactsDetail.setSelected(contactPersonArr.getSelected());
                                    contactDetailArrayList.add(newContactsDetail);

                                }
                                addNewReferenceLayout.setVisibility(View.VISIBLE);
                                contactDetailArrayAdapter.notifyDataSetChanged();
                                for (int i = 1; i < contactDetailArrayList.size(); i++)
                                    if (contactDetailArrayList.get(i).getSelected()) {
                                        referenceContactPersonSpinner.setSelection(i);
                                    }
                                referenceContactItemClick();
                            }
                        }

                        //Contacts
                        if (apiResponse.getData().getCustomerInfo().getContactDetails() != null &&
                                !apiResponse.getData().getCustomerInfo().getContactDetails().isEmpty()) {
                            if (!individualRadioBtn.isChecked()) {
                                for (final ContactDetail contactDetail : apiResponse.getData().getCustomerInfo().getContactDetails()) {
                                    if (contactDetail != null) {
                                        Contacts contacts = new Contacts();
                                        contacts.setOwner("0");
                                        contacts.setCodeid("");
                                        if (contactDetail.getName() != null && !contactDetail.getName().isEmpty()) {
                                            contacts.setName(contactDetail.getName());
                                        } else if (contactDetail.getContactName() != null && !contactDetail.getContactName().isEmpty()) {
                                            contacts.setName(contactDetail.getContactName());
                                        }
                                        contacts.setDesignation(contactDetail.getContactDesignation());
                                        contacts.setEmail(contactDetail.getContactEmail());
                                        contacts.setPhoneNo(contactDetail.getContactPhoneNo());

                                        if (contactDetail.getExtraAttributeId() == null ||
                                                contactDetail.getExtraAttributeId().isEmpty()) {
                                            contacts.setType(contactDetail.getContactTypeValue());
                                            contacts.setContactTypeId(contactDetail.getContactAttributeId());
                                            contacts.setContactTypeValue(contactDetail.getContactTypeId());
                                        } else {
                                            contacts.setContactTypeId(contactDetail.getContactAttributeId());
                                            contacts.setContactTypeValue(contactDetail.getContactTypeId());
                                            contacts.setExtraAttribute(contactDetail.getExtraAttributeId());
                                            contacts.setSpecifyText(contactDetail.getExtraAttributeValue());
                                            // contacts.setType(contactDetail.getExtraAttributeValue());
                                            contacts.setType(contactDetail.getContactTypeValue());
                                        }
                                        if (contactDetail.getOwner() != null) {
                                            contacts.setCheckOwner(contactDetail.getOwner());
                                        }
                                        addContactDetailsLayout.setVisibility(View.VISIBLE);
                                        contactsList.add(contacts);
                                    }
                                    if (contactsList != null && contactsList.size() > 0) {
                                        addContactDetailsLayout.setVisibility(View.VISIBLE);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    addContactRecyclerView.setVisibility(View.VISIBLE);
                                }
                            }
                        }

                        //Collection Date
                        if (apiResponse.getData().getCustomerInfo().getCollectionDate() != null &&
                                !apiResponse.getData().getCustomerInfo().getCollectionDate().isEmpty() &&
                                !apiResponse.getData().getCustomerInfo().getCollectionDate().contains("0000")) {
                            stringStartDate = apiResponse.getData().getCustomerInfo().getCollectionDate();
                            try {
                                startDate = targetFormat.parse(stringStartDate);
                                System.out.println(startDate);
                                String from = dateFormatter.format(startDate);
                                collectionDateValueTextView.setText(from);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    } else {
                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Open Dialog to select customers
    public void openCustomerSearchDialog() {
        dialog = new Dialog(AddCustomerActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quotation_customer_search);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        customerSearchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        getCustomersProductsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AddCustomerActivity.this);
        referredByCustomersAdapter = new ReferredByCustomersAdapter(AddCustomerActivity.this,
                customerSearchArrayList, this);

        getCustomersProductsRecyclerView.setLayoutManager(mLayoutManager);
        getCustomersProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getCustomersProductsRecyclerView.setAdapter(referredByCustomersAdapter);
        getCustomersProductsRecyclerView.setNestedScrollingEnabled(false);

        customerSearchEditText.setHint("Search Customer");
        customerSearchEditText.setThreshold(1);
        customerSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (customerSearchEditText.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchCustomer(s.toString().trim());
                    } else {
                        Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void searchCustomer(String str) {
        task = getString(R.string.add_customer_referrence_search);
        if (AppPreferences.getIsLogin(AddCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(AddCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(AddCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(AddCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO Updated on 31st May. If we need to show balance with name of customer, we have to pass balance 1 else we need to send 0
        Call<ApiResponse> call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "0");
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (customerSearchArrayList != null && customerSearchArrayList.size() > 0) {
                            customerSearchArrayList.clear();
                        }
                        for (final SearchRefferedDatum searchRefferedDatum : apiResponse.getData().getSearchRefferedData()) {
                            if (searchRefferedDatum != null) {
                                customerSearchArrayList.add(searchRefferedDatum);
                            }
                        }
                        refreshCustomerRecyclerView();

                    } else {

                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(AddCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(AddCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Refreshing data after getting input from openCustomerSearchDialog
    private void refreshCustomerRecyclerView() {
        referredByCustomersAdapter.notifyDataSetChanged();

        customerSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SearchRefferedDatum selected = (SearchRefferedDatum) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    referredByValueEditText.setText(selected.getName().toString().trim());
                } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
                    referredByValueEditText.setText(selected.getCompanyName().toString().trim());
                }

                //currentAddressLayout.setVisibility(View.VISIBLE);
                referredByValueEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        referredByValueEditText.setSelection(referredByValueEditText.getText().toString().length());
                    }
                });

                //Get CUID
                strReferredBy = selected.getId();
                strReferralType = selected.getType();

                status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    contactDetailArrayList.clear();
                    referenceContactPerson(selected.getType(), selected.getId());
                } else {
                    Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onCustomerClick(int position) {
        SearchRefferedDatum selected = customerSearchArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            referredByValueEditText.setText(selected.getName().toString().trim());
        } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
            referredByValueEditText.setText(selected.getCompanyName().toString().trim());
        }
        //currentAddressLayout.setVisibility(View.VISIBLE);
        referredByValueEditText.post(new Runnable() {
            @Override
            public void run() {
                referredByValueEditText.setSelection(referredByValueEditText.getText().toString().length());
            }
        });

        //Get CUID
        strReferredBy = selected.getId();
        strReferralType = selected.getType();

        status = NetworkUtil.getConnectivityStatusString(AddCustomerActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            contactDetailArrayList.clear();
            referenceContactPerson(selected.getType(), selected.getId());
        } else {
            Toast.makeText(AddCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    //AsyncTask to Post Data
    public class PostAddCustomerAsyncTask extends AsyncTask<Void, Void, String> {
        private Activity context;

        public PostAddCustomerAsyncTask(Activity context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return getString();
        }

        private String getString() {
            // TODO Auto-generated method stub

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("customer-type-radio", strUserType);
                jsonObject.put("email", strEmail);
                jsonObject.put("fname", strUserFName);
                jsonObject.put("lname", strUserLName);
                jsonObject.put("company_name", strCompanyName);
                jsonObject.put("mobile", strMobile);
                jsonObject.put("current-line1", strOfficeLineOne);
                jsonObject.put("current-line2", strOfficeLineTwo);
                jsonObject.put("current-ctid", strOfficeCountry);
                jsonObject.put("current-stid", strOfficeState);
                jsonObject.put("current-coverid", strOfficeCity);
                jsonObject.put("current-pincode", strOfficePinCode);
                jsonObject.put("site_addr", strAddSiteAddress);
                jsonObject.put("site-line1", strSiteLineOne);
                jsonObject.put("site-line2", strSiteLineTwo);
                jsonObject.put("site-ctid", strSiteCountry);
                jsonObject.put("site-stid", strSiteStates);
                jsonObject.put("site-coverid", strSiteCity);
                jsonObject.put("site-pincode", strSitePinCode);
                if (contactsList.size() > 0) {
                    jsonObject.put("contact-detail-check", "1");
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < contactsList.size(); i++) {
                        JSONObject contactsJsonObject = new JSONObject();
                        contactsJsonObject.put("cp-id", i);
                        contactsJsonObject.put("cp-name", contactsList.get(i).getName() + "");
                        contactsJsonObject.put("cp-designation", contactsList.get(i).getDesignation() + "");
                        contactsJsonObject.put("cp-email", contactsList.get(i).getEmail() + "");
                        contactsJsonObject.put("cp-phone-no", contactsList.get(i).getPhoneNo() + "");
                        contactsJsonObject.put("cp-contact-type-id", contactsList.get(i).getContactTypeId() + "");
                        contactsJsonObject.put("cp-contact-type-value", contactsList.get(i).getContactTypeValue() + "");
                        if (contactsList.get(i).isCheckOwner()) {
                            contactsJsonObject.put("cp-is-owner", "1");
                        } else {
                            contactsJsonObject.put("cp-is-owner", "0");
                        }
                        contactsJsonObject.put("cp-attribute-" + contactsList.get(i).getContactTypeId() + "",
                                contactsList.get(i).getContactTypeValue());
                        if (contactsList.get(i).getExtraAttribute() != null && !contactsList.get(i).getExtraAttribute().isEmpty()) {
                            contactsJsonObject.put("attribute-" + contactsList.get(i).getExtraAttribute() + "",
                                    contactsList.get(i).getSpecifyText());
                        }
                        jsonArray.put(contactsJsonObject);
                    }
                    jsonObject.put("contact-person-combo", jsonArray.toString());
                } else {
                    jsonObject.put("contact-detail-check", "0");
                    jsonObject.put("contact-person-combo", "");
                }
                jsonObject.put("gstin", strGSTIN);
                jsonObject.put("telephone", strTelephone);
                jsonObject.put("salutation", strSalutation);
                jsonObject.put("website", strCompanyWebsite);
                jsonObject.put("refer-codeid", strReferenceContactPerson);
                jsonObject.put("refer-cuid", strReferredBy);
                // jsonObject.put("refer-cuid", strReferenceContactPerson);
                jsonObject.put("soft_credit_limit", strSoftCreditLimit);
                jsonObject.put("hard_credit_limit", strHardCreditLimit);
                jsonObject.put("soft_credit_days", strSoftCreditDays);
                jsonObject.put("hard_credit_days", strHardCreditDays);
                jsonObject.put("customer-type", strCustomerType);
                jsonObject.put("customer-sales-type", strCustomerSalesType);
                jsonObject.put("current-add-mobile", strOfficePersonPhoneNumber);
                jsonObject.put("current-add-fname", strOfficePersonFName);
                jsonObject.put("current-add-lname", strOfficePersonLName);
                jsonObject.put("current-add-sitename", strOfficeName);
                jsonObject.put("current-satid", strOfficeType);
                jsonObject.put("site-add-fname", strSitePersonFName);
                jsonObject.put("site-add-lname", strSitePersonLName);
                jsonObject.put("site-add-mobile", strSitePersonPhoneNumber);
                jsonObject.put("site-add-sitename", strSiteName);
                jsonObject.put("site-satid", strSiteType);
                jsonObject.put("gender", strGender);
                jsonObject.put(defaultCSTKey, strCST);
                jsonObject.put(defaultTINKey, strTin);
                jsonObject.put(defaultPANKey, strPan);
                jsonObject.put("referal-type", strReferralType);
                jsonObject.put("collection_date", strCollectionDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String postParams = "data=" + jsonObject.toString();
            Log.d("POST", postParams);

            URL obj = null;
            HttpURLConnection con = null;
            try {
                obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=" + Constants.version + "&key=" + Constants.key + "&task=" + context.getString(R.string.add_customer)
                        + "&user_id=" + AppPreferences.getUserId(context, AppUtils.USER_ID)
                        + "&access_token=" + AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN));
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                // For POST only - BEGIN
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                // For POST only - END
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString();

                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (result != null) {
                Log.e("RESULT", result.toString().trim());
                JSONObject jsonObject, dataJsonObject;
                String cuId = "", name = "", mobile = "", email = "", codeId = "", contactPerson = "";
                try {
                    jsonObject = new JSONObject(result);
                    boolean status = jsonObject.optBoolean("success");
                    String message = jsonObject.optString("message");
                    dataJsonObject = jsonObject.optJSONObject("data");
                    if (dataJsonObject != null) {
                        if (dataJsonObject.has("cuid")) {
                            cuId = dataJsonObject.optString("cuid");
                        }
                        if (dataJsonObject.has("name")) {
                            name = dataJsonObject.optString("name");
                        }
                        if (dataJsonObject.has("mobile")) {
                            mobile = dataJsonObject.optString("mobile");
                        }
                        if (dataJsonObject.has("email")) {
                            email = dataJsonObject.optString("email");
                        }
                        if (dataJsonObject.has("codeid")) {
                            codeId = dataJsonObject.optString("codeid");
                        }
                        if (dataJsonObject.has("contactPersoneName")) {
                            contactPerson = dataJsonObject.optString("contactPersoneName");
                        }
                    }

                    if (status) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        if (quotationRequestCode != 0) {
                            Intent intent = new Intent();
                            intent.putExtra("cuId", cuId);
                            intent.putExtra("name", name);
                            intent.putExtra("mobile", mobile);
                            intent.putExtra("email", email);
                            intent.putExtra("codeid", codeId);
                            intent.putExtra("contactPerson", contactPerson);
                            setResult(AppUtils.ADD_QUOTATION_CUSTOMER_REQUEST_CODE, intent);
                        } else {
                            Intent intent = new Intent();
                            setResult(AppUtils.ADD_CUSTOMER_REQUEST_CODE, intent);
                        }
                        finish();
                    } else {
                        //Deleted User
                        if (jsonObject.getString("result_code").equals(Constants.WRONG_CREDENTIALS) ||
                                jsonObject.getString("result_code").equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, message);
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(context, context.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

    //AsyncTask to Update Customer's Data
    public class UpdateCustomerAsyncTask extends AsyncTask<Void, Void, String> {
        private Activity context;
        private String customerId;

        public UpdateCustomerAsyncTask(Activity context, String customerId) {
            this.context = context;
            this.customerId = customerId;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return getString();
        }

        public String getString() {
            // TODO Auto-generated method stub

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("cuid", customerId);
                jsonObject.put("customer-type-radio", strUserType);
                jsonObject.put("email", strEmail);
                jsonObject.put("fname", strUserFName);
                jsonObject.put("lname", strUserLName);
                jsonObject.put("company_name", strCompanyName);
                jsonObject.put("mobile", strMobile);
                if (contactsList.size() > 0) {
                    jsonObject.put("contact-detail-check", "1");
                    JSONArray jsonArray = new JSONArray();
                    for (int i = 0; i < contactsList.size(); i++) {
                        JSONObject contactsJsonObject = new JSONObject();
                        contactsJsonObject.put("cp-id", i);
                        contactsJsonObject.put("cp-name", contactsList.get(i).getName());
                        contactsJsonObject.put("cp-designation", contactsList.get(i).getDesignation());
                        contactsJsonObject.put("cp-email", contactsList.get(i).getEmail());
                        contactsJsonObject.put("cp-phone-no", contactsList.get(i).getPhoneNo());
                        if (contactsList.get(i).isCheckOwner()) {
                            contactsJsonObject.put("cp-is-owner", "1");
                        } else {
                            contactsJsonObject.put("cp-is-owner", "0");
                        }
                        contactsJsonObject.put("cp-contact-type-id", contactsList.get(i).getContactTypeId() + "");
                        contactsJsonObject.put("cp-contact-type-value", contactsList.get(i).getContactTypeValue() + "");
                        if (contactsList.get(i).getContactTypeId() != null &&
                                !contactsList.get(i).getContactTypeId().isEmpty()) {
                            contactsJsonObject.put("cp-attribute-" + contactsList.get(i).getContactTypeId() + "",
                                    contactsList.get(i).getContactTypeValue());
                        }
                        if (contactsList.get(i).getExtraAttribute() != null && !contactsList.get(i).getExtraAttribute().isEmpty()) {
                            contactsJsonObject.put("attribute-" + contactsList.get(i).getExtraAttribute() + "",
                                    contactsList.get(i).getSpecifyText());
                        }
                        jsonArray.put(contactsJsonObject);
                    }
                    jsonObject.put("contact-person-combo", jsonArray.toString());
                } else {
                    jsonObject.put("contact-detail-check", "0");
                    jsonObject.put("contact-person-combo", "");
                }
                jsonObject.put("gstin", strGSTIN);
                jsonObject.put("telephone", strTelephone);
                jsonObject.put("salutation", strSalutation);
                jsonObject.put("website", strCompanyWebsite);
                jsonObject.put("refer-codeid", strReferenceContactPerson);
                jsonObject.put("refer-cuid", strReferredBy);
                // jsonObject.put("refer-cuid", strReferenceContactPerson);
                jsonObject.put("soft_credit_limit", strSoftCreditLimit);
                jsonObject.put("hard_credit_limit", strHardCreditLimit);
                jsonObject.put("soft_credit_days", strSoftCreditDays);
                jsonObject.put("hard_credit_days", strHardCreditDays);
                jsonObject.put("customer-type", strCustomerType);
                jsonObject.put("customer-sales-type", strCustomerSalesType);
                jsonObject.put("gender", strGender);
                jsonObject.put(defaultCSTKey, strCST);
                jsonObject.put(defaultTINKey, strTin);
                jsonObject.put(defaultPANKey, strPan);
                jsonObject.put("referal-type", strReferralType);
                jsonObject.put("collection_date", strCollectionDate);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String postParams = "data=" + jsonObject.toString();
            Log.d("POST_UPDATE", postParams);

            URL obj = null;
            HttpURLConnection con = null;
            try {
                obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=" + Constants.version + "&key=" + Constants.key + "&task=" + context.getString(R.string.update_customer_details_task)
                        + "&user_id=" + AppPreferences.getUserId(context, AppUtils.USER_ID)
                        + "&access_token=" + AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN));
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                Log.d("POST_URL", obj.toString() + "");
                // For POST only - BEGIN
                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(postParams.getBytes());
                os.flush();
                os.close();
                // For POST only - END
                int responseCode = con.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // success
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    Log.e("RESPONSE", response.toString());
                    return response.toString();

                } else {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            if (result != null) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(result);
                    boolean status = jsonObject.getBoolean("success");
                    String message = jsonObject.getString("message");

                    if (status) {
                        if (!NetworkUtil.getConnectivityStatusString(context).equals
                                (context.getString(R.string.not_connected_to_internet))) {
                            ContactPersonsAPI contactPersonsAPI = new ContactPersonsAPI
                                    (context);
                            contactPersonsAPI.getCustomersContactPersons
                                    (context, "", "", "", "", customerId,
                                            "", "");
                        }

                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(getResources().getString(R.string.email), strEmail);
                        setResult(111, intent);
                        finish();
                    } else {
                        //Deleted User
                        if (jsonObject.getString("result_code").equals(Constants.WRONG_CREDENTIALS) ||
                                jsonObject.getString("result_code").equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(AddCustomerActivity.this, message);
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Toast.makeText(AddCustomerActivity.this, context.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }

            }
        }
    }

}