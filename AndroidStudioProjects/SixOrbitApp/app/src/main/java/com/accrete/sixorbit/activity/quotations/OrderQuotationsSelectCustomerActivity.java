package com.accrete.sixorbit.activity.quotations;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AddNewAddressActivity;
import com.accrete.sixorbit.activity.customers.AddCustomerActivity;
import com.accrete.sixorbit.activity.order.CreateOrderActivity;
import com.accrete.sixorbit.adapter.AddressAdapter;
import com.accrete.sixorbit.adapter.QuotationCustomersAdapter;
import com.accrete.sixorbit.adapter.SiteAddressAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.AddressList;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ContactPerson;
import com.accrete.sixorbit.model.ContactPersonTypeData;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.CustomerList;
import com.accrete.sixorbit.model.ItemData;
import com.accrete.sixorbit.model.Outlet;
import com.accrete.sixorbit.model.SearchRefferedDatum;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.AppUtils.ADD_QUOTATION_CUSTOMER_REQUEST_CODE;

public class OrderQuotationsSelectCustomerActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener,
        View.OnClickListener,
        QuotationCustomersAdapter.QuotationCustomersAdapterListener, AddressAdapter.AddressItemClickListener,
        SiteAddressAdapter.SiteAddressItemClickListener {
    private Toolbar toolbar;
    private CardView cardViewOuterOutlet;
    private CardView cardViewInnerOutlet;
    private CardView customerCardView;
    private TextView outletTitleTextView;
    private Spinner outletSpinner;
    private CardView cardViewOuterCustomer, cardViewCurrentAddress, cardViewSiteAddress;
    private CardView cardViewInnerCustomer;
    private TextView customerInfoTextView;
    private TextView customerNameTitleTextView;
    private EditText customerNameValueEditText;
    private ImageButton clearCustomerInfoImageButton;
    private LinearLayout mobileLayout;
    private TextView mobileTextView, currentAddressEmptyTextView, siteAddressEmptyTextView;
    private EditText mobileValueEditText;
    private LinearLayout emailLayout;
    private TextView emailTextView;
    private EditText emailValueEditText;
    private TextInputLayout addSearchQuotationItemContactPersonTextInput;
    private AutoCompleteTextView contactPersonAutoCompleteTextview;
    private LinearLayout currentAddressAddContact;
    private LinearLayout currentAddressLayout;
    private TextView textViewCurrentAddress;
    private CheckBox currentAddressCheckBox;
    private TextView currentAddressTextView;
    private LinearLayout linearLayoutAddAddress;
    private LinearLayout siteAddressLayout;
    private TextView textViewSiteAddress;
    private CheckBox sameSiteAddressCheckBox;
    private CheckBox noSiteAddressCheckBox;
    private TextView siteAddressTextView;
    private LinearLayout linearLayoutAddNewSiteAddress;
    private TextView addCustomerTextView;
    private ArrayList<Outlet> outletArrayList = new ArrayList<>();
    private String status;
    private String strCuid, strCodeId, qotemid, newCurrentAddressSaid, newSiteAddressSaid, strContactPerson,
            strSaid, strChkId, strBaid, orderTemId, strEditCuId;
    private int currentAddressIndex, siteAddressIndex;
    private Dialog dialog, dialogSiteAddress;
    private RecyclerView getCustomersProductsRecyclerView;
    private ArrayList<CustomerList> customerSearchArrayList = new ArrayList<>();
    private AutoCompleteTextView customerSearchEditText;
    private QuotationCustomersAdapter quotationCustomersAdapter;
    private List<ContactPerson> contactPersonListArrayList = new ArrayList<>();
    private List<ContactPersonTypeData> contactTypeDataArrayList = new ArrayList<>();
    private ArrayList<AddressList> currentAddressListArrayList = new ArrayList<>();
    private ArrayList<AddressList> siteAddressListArrayList = new ArrayList<>();
    private AlertDialog alertDialog;
    private EmailValidator emailValidator;
    private AddressAdapter addressAdapter;
    private SiteAddressAdapter siteAddressAdapter;
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    private ImageView loaderImageView;
    private RelativeLayout parentLayout;
    private ArrayList<ItemData> itemDataArrayList = new ArrayList<ItemData>();
    private TextView addNewCustomerTextView;
    private DatabaseHandler databaseHandler;

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loaderImageView.getVisibility() == View.GONE) {
                            loaderImageView.setVisibility(View.VISIBLE);
                        }
                        Ion.with(loaderImageView)
                                .animateGif(AnimateGifMode.ANIMATE)
                                .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                .withBitmapInfo();
                    }
                });
            }
        });
        thread.start();
    }

    private void hideLoader() {
        if (loaderImageView != null && loaderImageView.getVisibility() == View.VISIBLE) {
            loaderImageView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        //now getIntent() should always return the last received intent
        if (intent != null) {
            if (intent.hasExtra("cuId")) {
                strEditCuId = getIntent().getStringExtra("cuId");
            }
            if (intent.hasExtra("products")) {
                itemDataArrayList = getIntent().getParcelableArrayListExtra("products");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quotations);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        databaseHandler = new DatabaseHandler(OrderQuotationsSelectCustomerActivity.this);

        if (getIntent() != null) {
            if (getIntent().hasExtra(getString(R.string.quotation_id))) {
                qotemid = getIntent().getStringExtra(getString(R.string.quotation_id));
                getSupportActionBar().setTitle(getString(R.string.title_activity_add_quotations));
            } else if (getIntent().hasExtra(getString(R.string.order_id))) {
                orderTemId = getIntent().getStringExtra(getString(R.string.order_id));
                getSupportActionBar().setTitle(getString(R.string.title_activity_add_orders));
            }
        }

        cardViewOuterOutlet = (CardView) findViewById(R.id.card_view_outer_outlet);
        cardViewInnerOutlet = (CardView) findViewById(R.id.card_view_inner_outlet);
        outletTitleTextView = (TextView) findViewById(R.id.outlet_title_textView);
        outletSpinner = (Spinner) findViewById(R.id.outlet_spinner);
        cardViewOuterCustomer = (CardView) findViewById(R.id.card_view_outer_customer);
        cardViewInnerCustomer = (CardView) findViewById(R.id.card_view_inner_customer);
        customerInfoTextView = (TextView) findViewById(R.id.customer_info_textView);
        customerNameTitleTextView = (TextView) findViewById(R.id.customer_name_title_textView);
        customerNameValueEditText = (EditText) findViewById(R.id.customer_name_value_editText);
        clearCustomerInfoImageButton = (ImageButton) findViewById(R.id.clear_customerInfo_imageButton);
        mobileLayout = (LinearLayout) findViewById(R.id.mobile_layout);
        mobileTextView = (TextView) findViewById(R.id.mobile_textView);
        mobileValueEditText = (EditText) findViewById(R.id.mobile_value_editText);
        emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        emailTextView = (TextView) findViewById(R.id.email_textView);
        emailValueEditText = (EditText) findViewById(R.id.email_value_editText);
        addSearchQuotationItemContactPersonTextInput = (TextInputLayout) findViewById(R.id.add_search_quotation_item_contact_person_text_input);
        contactPersonAutoCompleteTextview = (AutoCompleteTextView) findViewById(R.id.add_search_quotation_item_contact_person);
        currentAddressAddContact = (LinearLayout) findViewById(R.id.current_address_add_contact);
        cardViewCurrentAddress = (CardView) findViewById(R.id.address_main_layout_current_address);
        cardViewSiteAddress = (CardView) findViewById(R.id.address_main_layout_site_address);
        currentAddressLayout = (LinearLayout) findViewById(R.id.current_address_layout);
        textViewCurrentAddress = (TextView) findViewById(R.id.textView_currentAddress);
        currentAddressCheckBox = (CheckBox) findViewById(R.id.currentAddress_checkBox);
        currentAddressTextView = (TextView) findViewById(R.id.currentAddress_textView);
        linearLayoutAddAddress = (LinearLayout) findViewById(R.id.current_address_add_address);
        siteAddressLayout = (LinearLayout) findViewById(R.id.site_address_layout);
        textViewSiteAddress = (TextView) findViewById(R.id.textView_siteAddress);
        sameSiteAddressCheckBox = (CheckBox) findViewById(R.id.sameSiteAddress_checkBox);
        noSiteAddressCheckBox = (CheckBox) findViewById(R.id.noSiteAddress_checkBox);
        siteAddressTextView = (TextView) findViewById(R.id.siteAddress_textView);
        linearLayoutAddNewSiteAddress = (LinearLayout) findViewById(R.id.site_address_add_address);
        addCustomerTextView = (TextView) findViewById(R.id.addCustomer_textView);
        loaderImageView = (ImageView) findViewById(R.id.loader_imageView);
        parentLayout = (RelativeLayout) findViewById(R.id.parent_layout);
        customerCardView = (CardView) findViewById(R.id.customer_cardView);

        parentLayout.setVisibility(View.GONE);
        addCustomerTextView.setVisibility(View.GONE);

        //Validator
        emailValidator = new EmailValidator();

        //Mandatory Outlet title
        String mainText = "Outlet";
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        outletTitleTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));

        emailValueEditText.setFocusable(false);
        mobileValueEditText.setFocusable(false);
        mobileValueEditText.setClickable(false);
        emailValueEditText.setClickable(false);
        mobileLayout.setVisibility(View.GONE);
        emailLayout.setVisibility(View.GONE);
        cardViewCurrentAddress.setVisibility(View.GONE);
        cardViewSiteAddress.setVisibility(View.GONE);

        //Hide textView and spinner of outlet
        outletTitleTextView.setVisibility(View.GONE);
        outletSpinner.setVisibility(View.GONE);

        //Get outlets
        status = NetworkUtil.getConnectivityStatusString(OrderQuotationsSelectCustomerActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            if (qotemid != null && !qotemid.isEmpty()) {
                getQuotationPrefilledData(OrderQuotationsSelectCustomerActivity.this);
            } else {
                getOrderPrefilledData(OrderQuotationsSelectCustomerActivity.this);
            }
        }

        //Click Listener
        customerNameValueEditText.setOnClickListener(this);
        clearCustomerInfoImageButton.setOnClickListener(this);
        linearLayoutAddAddress.setOnClickListener(this);
        linearLayoutAddNewSiteAddress.setOnClickListener(this);
        noSiteAddressCheckBox.setOnCheckedChangeListener(this);
        sameSiteAddressCheckBox.setOnCheckedChangeListener(this);
        currentAddressCheckBox.setOnCheckedChangeListener(this);
        currentAddressAddContact.setOnClickListener(this);
        //  currentAddressTextView.setOnClickListener(this);
        //  siteAddressTextView.setOnClickListener(this);
        addCustomerTextView.setOnClickListener(this);

        //Customer Focus
        customerNameValueEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (customerNameValueEditText.getText().toString().trim().length() == 0) {
                        openCustomerSearchDialog();
                    }
                }
            }
        });

    }

    public void getQuotationPrefilledData(final Activity activity) {
        task = getString(R.string.quotation_tax_charges_data);
        if (AppPreferences.getIsLogin(OrderQuotationsSelectCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(OrderQuotationsSelectCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(OrderQuotationsSelectCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(OrderQuotationsSelectCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                //   Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (outletArrayList != null && outletArrayList.size() > 0) {
                            outletArrayList.clear();
                        }

                        //One Static
                        Outlet outletStatic = new Outlet();
                        outletStatic.setName("Select Outlet");
                        outletArrayList.add(outletStatic);
                        if (apiResponse.getData().getOutlet() != null) {
                            for (final Outlet outlet : apiResponse.getData().getOutlet()) {
                                if (outlet != null) {
                                    outletArrayList.add(outlet);
                                }
                            }
                        }

                        //Hide/Show Outlet
                        if (apiResponse.getData().isOutletEnable()) {
                            if (outletArrayList != null && outletArrayList.size() > 1) {
                                //Show textview and spinner of outlet
                                outletTitleTextView.setVisibility(View.VISIBLE);
                                outletSpinner.setVisibility(View.VISIBLE);
                                cardViewOuterOutlet.setVisibility(View.VISIBLE);
                            } else {
                                //Hide textview and spinner of outlet
                                outletTitleTextView.setVisibility(View.GONE);
                                outletSpinner.setVisibility(View.GONE);
                                cardViewOuterOutlet.setVisibility(View.GONE);
                            }
                        } else {
                            //Hide textview and spinner of outlet
                            outletTitleTextView.setVisibility(View.GONE);
                            outletSpinner.setVisibility(View.GONE);
                            cardViewOuterOutlet.setVisibility(View.GONE);

                            //Remove margins from top
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            customerCardView.setLayoutParams(params);
                        }


                        //Outlet Adapter
                        ArrayAdapter<Outlet> outletArrayAdapter =
                                new ArrayAdapter<Outlet>(activity, R.layout.simple_spinner_item, outletArrayList);
                        outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        outletSpinner.setAdapter(outletArrayAdapter);
                        outletSpinner.setSelection(0);

                        hideLoader();
                        parentLayout.setVisibility(View.VISIBLE);
                        addCustomerTextView.setVisibility(View.VISIBLE);

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        hideLoader();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (OrderQuotationsSelectCustomerActivity.this != null) {
                    Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });

    }

    private void getOrderPrefilledData(final Activity activity) {
        task = getString(R.string.order_prefilled_data_task);
        if (AppPreferences.getIsLogin(OrderQuotationsSelectCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(OrderQuotationsSelectCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(OrderQuotationsSelectCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(OrderQuotationsSelectCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                //   Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (outletArrayList != null && outletArrayList.size() > 0) {
                            outletArrayList.clear();
                        }

                        //One Static
                        Outlet outletStatic = new Outlet();
                        outletStatic.setName("Select Outlet");
                        outletArrayList.add(outletStatic);
                        if (apiResponse.getData().getOrderFormData().getOutlet() != null) {
                            for (final Outlet outlet : apiResponse.getData().getOrderFormData().getOutlet()) {
                                if (outlet != null) {
                                    outletArrayList.add(outlet);
                                }
                            }
                        }

                        //Hide/Show Outlet
                        if (outletArrayList != null && outletArrayList.size() > 1) {
                            //Show textview and spinner of outlet
                            outletTitleTextView.setVisibility(View.VISIBLE);
                            outletSpinner.setVisibility(View.VISIBLE);
                            cardViewOuterOutlet.setVisibility(View.VISIBLE);
                        } else {
                            //Hide textview and spinner of outlet
                            outletTitleTextView.setVisibility(View.GONE);
                            outletSpinner.setVisibility(View.GONE);
                            cardViewOuterOutlet.setVisibility(View.GONE);

                            //Remove margins from top
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            params.setMargins(0, 0, 0, 0);
                            customerCardView.setLayoutParams(params);
                        }


                        //Outlet Adapter
                        ArrayAdapter<Outlet> outletArrayAdapter =
                                new ArrayAdapter<Outlet>(activity, R.layout.simple_spinner_item, outletArrayList);
                        outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        outletSpinner.setAdapter(outletArrayAdapter);
                        outletSpinner.setSelection(0);

                        hideLoader();
                        parentLayout.setVisibility(View.VISIBLE);
                        addCustomerTextView.setVisibility(View.VISIBLE);

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        hideLoader();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (OrderQuotationsSelectCustomerActivity.this != null) {
                    Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.customer_name_value_editText:
                if (customerNameValueEditText.getText().toString().trim().length() == 0) {
                    openCustomerSearchDialog();
                }
                break;
            case R.id.clear_customerInfo_imageButton:
                if (customerNameValueEditText.getText().toString().trim().length() != 0) {
                    customerNameValueEditText.setText("");
                    strCuid = "";
                    strCodeId = "";
                    newCurrentAddressSaid = null;
                    newSiteAddressSaid = null;
                    mobileValueEditText.setText("");
                    emailValueEditText.setText("");
                    mobileLayout.setVisibility(View.GONE);
                    emailLayout.setVisibility(View.GONE);
                    cardViewCurrentAddress.setVisibility(View.GONE);
                    cardViewSiteAddress.setVisibility(View.GONE);
                    contactPersonAutoCompleteTextview.setVisibility(View.GONE);
                    addSearchQuotationItemContactPersonTextInput.setVisibility(View.GONE);
                    contactPersonAutoCompleteTextview.setText("");
                    currentAddressAddContact.setVisibility(View.GONE);
                }
                break;
            case R.id.current_address_add_address:
                openCurrentAddressesFullScreenDialog(currentAddressListArrayList);
                break;
            case R.id.site_address_add_address:
                openSiteAddressesFullScreenDialog(siteAddressListArrayList);
                break;
            case R.id.current_address_add_contact:
                dialogAddContactPerson();
                break;
            case R.id.currentAddress_textView:
                break;
            case R.id.siteAddress_textView:
                break;
            case R.id.addCustomer_textView:

                if (strCuid != null && !strCuid.isEmpty() && getSelectedData() && qotemid != null && !qotemid.isEmpty()) {
                    Intent intent = new Intent(OrderQuotationsSelectCustomerActivity.this, QuotationsProductActivity.class);
                    intent.putExtra(getString(R.string.quotation_id), qotemid);
                    intent.putExtra("cuid", strCuid);
                    intent.putExtra("baid", strBaid);
                    intent.putExtra("said", strSaid);
                    intent.putExtra("chkid", strChkId);
                    intent.putExtra("cName", customerNameValueEditText.getText().toString().trim());
                    intent.putExtra("cEmail", emailValueEditText.getText().toString().trim());
                    intent.putExtra("cMobile", mobileValueEditText.getText().toString().trim());
                    intent.putExtra("codeId", strCodeId);

                    if (strEditCuId != null && strEditCuId.equals(strCuid)) {
                        if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                            intent.putParcelableArrayListExtra("products", itemDataArrayList);
                        }
                    }
                    intent.putExtra("finisher", new ResultReceiver(null) {
                        @Override
                        protected void onReceiveResult(int resultCode, Bundle resultData) {
                            OrderQuotationsSelectCustomerActivity.this.finish();
                        }
                    });
                    startActivityForResult(intent, 1);
                } else if (strCuid != null && !strCuid.isEmpty() && getSelectedData() && orderTemId != null && !orderTemId.isEmpty()) {
                    if (strSaid != null && !strSaid.isEmpty() && !strSaid.equals("0")) {
                        Intent intent = new Intent(OrderQuotationsSelectCustomerActivity.this, CreateOrderActivity.class);
                        intent.putExtra(getString(R.string.order_id), orderTemId);
                        intent.putExtra("cuid", strCuid);
                        intent.putExtra("baid", strBaid);
                        intent.putExtra("said", strSaid);
                        intent.putExtra("chkid", strChkId);
                        intent.putExtra("cName", customerNameValueEditText.getText().toString().trim());
                        intent.putExtra("cEmail", emailValueEditText.getText().toString().trim());
                        intent.putExtra("cMobile", mobileValueEditText.getText().toString().trim());
                        intent.putExtra("codeId", strCodeId);
                        if (strEditCuId != null && strEditCuId.equals(strCuid)) {
                            if (itemDataArrayList != null && itemDataArrayList.size() > 0) {
                                intent.putParcelableArrayListExtra("products", itemDataArrayList);
                            }
                        }
                        intent.putExtra("finisher", new ResultReceiver(null) {
                            @Override
                            protected void onReceiveResult(int resultCode, Bundle resultData) {
                                OrderQuotationsSelectCustomerActivity.this.finish();
                            }
                        });
                        startActivityForResult(intent, 1);
                    } else if (currentAddressListArrayList == null || currentAddressListArrayList.size() == 0) {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this,
                                "Please add a current address first", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this,
                                "Please provide current address details", Toast.LENGTH_SHORT).show();
                    }
                } else if (strCuid == null || strCuid.isEmpty()) {
                    Toast.makeText(OrderQuotationsSelectCustomerActivity.this,
                            "Please select a customer first", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private boolean getSelectedData() {
        if (outletSpinner.getVisibility() == View.VISIBLE) {
            if (outletSpinner.getSelectedItemPosition() == 0) {
                Toast.makeText(OrderQuotationsSelectCustomerActivity.this, "Please select outlet first.", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                strChkId = outletArrayList.get(outletSpinner.getSelectedItemPosition()).getId();
            }
        }
        if (strCuid == null || strCuid.isEmpty()) {
            Toast.makeText(OrderQuotationsSelectCustomerActivity.this, "Please select a customer first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        //Get IDs of both Addresses
        if (cardViewCurrentAddress.getVisibility() == View.VISIBLE && cardViewSiteAddress.getVisibility() == View.VISIBLE) {
            if (currentAddressListArrayList.size() > 0) {
                if (currentAddressTextView.getVisibility() == View.VISIBLE) {
                    for (int i = 0; i < currentAddressListArrayList.size(); i++) {
                        if (currentAddressListArrayList.get(i).isChecked())
                            strSaid = currentAddressListArrayList.get(i).getSaid();
                    }

                    if (sameSiteAddressCheckBox.isChecked()) {
                        strBaid = strSaid;
                    } else if (noSiteAddressCheckBox.isChecked()) {
                        strBaid = "0";
                    } else {
                        for (int i = 0; i < siteAddressListArrayList.size(); i++) {
                            if (siteAddressListArrayList.get(i).isChecked())
                                strBaid = siteAddressListArrayList.get(i).getSaid();
                        }
                    }

                } else {
                    strSaid = "0";
                    strBaid = "0";
                }
            }
        } else {
            strSaid = "0";
            strBaid = "0";
        }

        return true;
    }

    //Single Contact Person
    private void dialogAddContactPerson() {
        View dialogView = View.inflate(OrderQuotationsSelectCustomerActivity.this, R.layout.dialog_lead_add_contacts, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(OrderQuotationsSelectCustomerActivity.this);
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
                ContactPersonTypeData selectedContact = (ContactPersonTypeData) parent.getAdapter().getItem(position);
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

        contactPersonTypeArrayList.add(0, referralAddContact);*/

        ArrayAdapter<ContactPersonTypeData> contactPersonTypeArrayAdapter =
                new ArrayAdapter<ContactPersonTypeData>(OrderQuotationsSelectCustomerActivity.this, R.layout.simple_spinner_item, contactTypeDataArrayList);
        contactPersonTypeArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactTypeSpinner.setAdapter(contactPersonTypeArrayAdapter);

        //Default Selected Item
        contactTypeSpinner.setSelection(0);

        //Make visible this layout here only
        if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 1) {
            contactTypeLayout.setVisibility(View.VISIBLE);
        } else {
            contactTypeLayout.setVisibility(View.GONE);
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

                    contacts.setOwner("0");
                    contacts.setCodeid("");
                    contacts.setName(editTextName.getText().toString());
                    contacts.setDesignation(editTextDesignation.getText().toString());
                    contacts.setEmail(editTextEmail.getText().toString());
                    contacts.setPhoneNo(editTextPhone.getText().toString());
                    contacts.setSpecifyText(specifyEditText.getText().toString() + "");
                    if (contactTypeSpinner.getSelectedItemPosition() != 0) {
                        contacts.setContactTypeId(contactTypeDataArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getAttributeId());
                        contacts.setContactTypeValue(contactTypeDataArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getCusavid());
                    } else {
                        contacts.setContactTypeId("");
                        contacts.setContactTypeValue("");
                    }
                    contacts.setCuId(strCuid);

                    if (specifyEditText.getVisibility() == View.VISIBLE) {
                        contacts.setExtraAttribute(contactTypeDataArrayList.get(contactTypeSpinner.getSelectedItemPosition()).getExtraAttribute());
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
                    //linearLayoutContactDetails.setVisibility(View.VISIBLE);

                    status = NetworkUtil.getConnectivityStatusString(OrderQuotationsSelectCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        addContactPerson(contacts);
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }


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

    private void dialogAddNewAddress(String addressType) {
        Intent intentAddNewAddress = new Intent(OrderQuotationsSelectCustomerActivity.this, AddNewAddressActivity.class);
        intentAddNewAddress.putExtra("cuid", strCuid);
        intentAddNewAddress.putExtra("addressType", addressType);
        startActivityForResult(intentAddNewAddress, 300);
    }

    //Open Dialog to select customers
    public void openCustomerSearchDialog() {
        dialog = new Dialog(OrderQuotationsSelectCustomerActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_quotation_customer_search);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        customerSearchEditText = (AutoCompleteTextView) dialog.findViewById(R.id.customer_search_autoCompleteTextView);
        addNewCustomerTextView = (TextView) dialog.findViewById(R.id.addNewCustomer_textView);
        getCustomersProductsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        addNewCustomerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddCustomerActivity(OrderQuotationsSelectCustomerActivity.this, dialog);
            }
        });

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OrderQuotationsSelectCustomerActivity.this);
        quotationCustomersAdapter = new QuotationCustomersAdapter(OrderQuotationsSelectCustomerActivity.this,
                customerSearchArrayList, this);

        getCustomersProductsRecyclerView.setLayoutManager(mLayoutManager);
        getCustomersProductsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        getCustomersProductsRecyclerView.setAdapter(quotationCustomersAdapter);
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
                    status = NetworkUtil.getConnectivityStatusString(OrderQuotationsSelectCustomerActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        if (s.length() > 0) {
                            searchCustomer(s.toString().trim());
                        }
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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

    private void openAddCustomerActivity(Activity activity, Dialog dialog) {
        if (AppPreferences.getBoolean(activity, AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.customer_add_permission))) {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            Intent intent = new Intent(activity, AddCustomerActivity.class);
            intent.putExtra(getString(R.string.request_code_string),
                    ADD_QUOTATION_CUSTOMER_REQUEST_CODE);
            startActivityForResult(intent, ADD_QUOTATION_CUSTOMER_REQUEST_CODE);
        } else {
            Toast.makeText(activity, "Sorry, you've no permission to add a customer.", Toast.LENGTH_SHORT).show();
        }
    }

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void searchCustomer(String str) {
        task = getString(R.string.add_quotation_search_customer_task);
        if (AppPreferences.getIsLogin(OrderQuotationsSelectCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(OrderQuotationsSelectCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(OrderQuotationsSelectCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(OrderQuotationsSelectCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO Updated on 31st May. If we need to show balance with name of customer, we have to pass balance 1 else we need to send 0.
        //TODO We need to show in case of order and collections only.
        Call<ApiResponse> call = null;
        if (strCuid != null && !strCuid.isEmpty() && getSelectedData() && qotemid != null && !qotemid.isEmpty()) {
            call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "0");
        } else {
            call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "1");
        }
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (customerSearchArrayList != null && customerSearchArrayList.size() > 0) {
                        customerSearchArrayList.clear();
                    }
                    if (apiResponse.getSuccess()) {
                        for (final CustomerList customerList : apiResponse.getData().getCustomerList()) {
                            if (customerList != null) {
                                customerSearchArrayList.add(customerList);
                            }
                        }
                        refreshCustomerRecyclerView();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        //Refresh Adapter
                        quotationCustomersAdapter.notifyDataSetChanged();

                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Display button to add a new customer
                    if (customerSearchArrayList != null && customerSearchArrayList.size() > 0) {
                        getCustomersProductsRecyclerView.setVisibility(View.VISIBLE);
                        addNewCustomerTextView.setVisibility(View.GONE);
                    } else {
                        getCustomersProductsRecyclerView.setVisibility(View.GONE);
                        addNewCustomerTextView.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Refreshing data after getting input from openCustomerSearchDialog
    private void refreshCustomerRecyclerView() {
        quotationCustomersAdapter.notifyDataSetChanged();

        customerSearchEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                SearchRefferedDatum selected = (SearchRefferedDatum) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    customerNameValueEditText.setText(selected.getName().toString().trim());
                } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
                    customerNameValueEditText.setText(selected.getCompanyName().toString().trim());
                }
                if (selected.getEmail() != null && !selected.getEmail().toString().trim().isEmpty()) {
                    emailValueEditText.setText(selected.getEmail().toString().trim());
                    emailLayout.setVisibility(View.VISIBLE);
                } else {
                    emailLayout.setVisibility(View.GONE);
                }
                if (selected.getMobile() != null && !selected.getMobile().toString().trim().isEmpty()) {
                    mobileValueEditText.setText(selected.getMobile().toString().trim());
                    mobileLayout.setVisibility(View.VISIBLE);
                } else {
                    mobileLayout.setVisibility(View.GONE);
                }

                //currentAddressLayout.setVisibility(View.VISIBLE);
                customerNameValueEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
                    }
                });

                //Get CUID
                strCuid = selected.getId();

                //Call API for addresses
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    getCustomersAddress(selected.getId());
                } else {
                    Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    //Getting click from dialog after selecting customer
    @Override
    public void onCustomerClick(int position) {
        CustomerList selected = customerSearchArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getName().toString().trim());
        } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getCompanyName().toString().trim());
        }
        if (selected.getEmail() != null && !selected.getEmail().toString().trim().isEmpty()) {
            emailValueEditText.setText(selected.getEmail().toString().trim());
            emailLayout.setVisibility(View.VISIBLE);
        } else {
            emailLayout.setVisibility(View.GONE);
        }
        if (selected.getMobile() != null && !selected.getMobile().toString().trim().isEmpty()) {
            mobileValueEditText.setText(selected.getMobile().toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        cardViewSiteAddress.setVisibility(View.VISIBLE);
        cardViewCurrentAddress.setVisibility(View.VISIBLE);
        //currentAddressLayout.setVisibility(View.VISIBLE);
        customerNameValueEditText.post(new Runnable() {
            @Override
            public void run() {
                customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
            }
        });

        //Get CUID
        strCuid = selected.getId();

        //Call API for addresses
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getCustomersAddress(selected.getId());
        } else {
            Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    public void getCustomersAddress(String id) {
        task = getString(R.string.customer_address_list);
        if (AppPreferences.getIsLogin(OrderQuotationsSelectCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(OrderQuotationsSelectCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(OrderQuotationsSelectCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(OrderQuotationsSelectCustomerActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getCustomersData(version, key, task, userId, accessToken, id);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (currentAddressListArrayList != null && currentAddressListArrayList.size() > 0) {
                        currentAddressListArrayList.clear();
                    }

                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        siteAddressListArrayList.clear();
                    }
                    if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 0) {
                        contactTypeDataArrayList.clear();
                    }
                    if (contactPersonListArrayList != null && contactPersonListArrayList.size() > 0) {
                        contactPersonListArrayList.clear();
                    }
                    if (apiResponse.getSuccess()) {

                        //Get All Addresses
                        for (final AddressList addressList : apiResponse.getData().getAddressList()) {
                            if (addressList != null) {
                                currentAddressListArrayList.add(addressList);
                                siteAddressListArrayList.add(addressList);
                            }

                        }

                        if (apiResponse.getData().getContactPersonArr() != null) {
                            for (ContactPerson contactPerson : apiResponse.getData().getContactPersonArr()) {
                                if (contactPerson != null) {
                                    if (databaseHandler != null &&
                                            !databaseHandler.checkCustomersContactPersonResult(contactPerson.getCodeid())) {
                                        Contacts contacts = new Contacts();
                                        contacts.setCodeid(contactPerson.getCodeid());
                                        contacts.setName(contactPerson.getName());
                                        contacts.setCuId(strCuid);
                                        databaseHandler.insertCustomersContactPersons(contacts);
                                    }else if (databaseHandler != null &&
                                            databaseHandler.checkCustomersContactPersonResult(contactPerson.getCodeid())) {
                                        Contacts contacts = new Contacts();
                                        contacts.setCodeid(contactPerson.getCodeid());
                                        contacts.setName(contactPerson.getName());
                                        contacts.setCuId(strCuid);
                                        databaseHandler.updateCustomersContactPersons(contacts);
                                    }
                                }
                            }
                        }

                        //Get Customer Type
                        if (apiResponse.getData().getCustomerType().equals("1")) {

                            //Get All contact persons of customer
                            for (final ContactPerson contactPerson : apiResponse.getData().getContactPersonArr()) {
                                if (contactPerson != null) {
                                    contactPersonListArrayList.add(contactPerson);
                                    Log.d("contact Person", contactPerson.getName());
                                }
                            }

                            contactPersonAutoCompleteTextview.setVisibility(View.VISIBLE);
                            if (contactPersonListArrayList != null && contactPersonListArrayList.size() > 0) {
                                contactPersonAutoCompleteTextview.setText(contactPersonListArrayList.get(0).getName());
                                strCodeId = contactPersonListArrayList.get(0).getCodeid();
                            }
                            currentAddressAddContact.setVisibility(View.VISIBLE);
                            addSearchQuotationItemContactPersonTextInput.setVisibility(View.VISIBLE);

                        } else {
                            contactPersonAutoCompleteTextview.setVisibility(View.GONE);
                            addSearchQuotationItemContactPersonTextInput.setVisibility(View.GONE);
                            currentAddressAddContact.setVisibility(View.GONE);
                            contactPersonListArrayList.clear();
                        }

                        //Get Contact Person Types of customer
                        for (final ContactPersonTypeData contactPersonTypeData : apiResponse.getData().getContactPersonTypeData()) {
                            if (contactPersonTypeData != null) {
                                contactTypeDataArrayList.add(contactPersonTypeData);
                            }
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Change Text of current and site address
                    if (currentAddressListArrayList != null && currentAddressListArrayList.size() > 0) {
                        //By default we will show address of 0 pos
                        if (newCurrentAddressSaid != null && !newCurrentAddressSaid.isEmpty()) {
                            for (int i = 0; i < currentAddressListArrayList.size(); i++) {
                                if (currentAddressListArrayList.get(i).getSaid().equals(newCurrentAddressSaid)) {
                                    currentAddressListArrayList.get(i).setChecked(true);
                                    setCurrentAddressText(i);
                                }
                            }
                        } else {
                            currentAddressListArrayList.get(0).setChecked(true);
                            setCurrentAddressText(0);
                        }
                    } else {
                        currentAddressTextView.setText("No address");
                    }
                    //By default no site address will be selected
                    sameSiteAddressCheckBox.setChecked(true);


                    //Set default current and site addresses after adding a new address also
                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        if (newSiteAddressSaid != null && !newSiteAddressSaid.isEmpty()) {
                            for (int i = 0; i < siteAddressListArrayList.size(); i++) {
                                if (siteAddressListArrayList.get(i).getSaid().equals(newSiteAddressSaid)) {
                                    siteAddressListArrayList.get(i).setCheckedSite(true);
                                    setSiteAddressText(i);
                                }
                            }
                        } else {
                            siteAddressListArrayList.get(0).setCheckedSite(true);
                            setSiteAddressText(0);
                        }
                    } else {
                        siteAddressTextView.setText("No address");
                    }

                    if (contactTypeDataArrayList != null && contactTypeDataArrayList.size() > 0) {
                        ContactPersonTypeData contactPersonTypeData = new ContactPersonTypeData();
                        contactPersonTypeData.setValue("Select");
                        contactTypeDataArrayList.set(0, contactPersonTypeData);
                    }


                    //Make Visible to main layout of addresses
                    cardViewSiteAddress.setVisibility(View.VISIBLE);
                    cardViewCurrentAddress.setVisibility(View.VISIBLE);

                    if (contactPersonListArrayList.size() > 0) {
                        contactPersonData();
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setCurrentAddressText(int position) {
        String addressText = "";
        String siteNameStr = "";
        final AddressList addressList = currentAddressListArrayList.get(position);
        if (addressList.getSiteName() != null && !addressList.getSiteName().toString().trim().isEmpty()) {
            siteNameStr = addressList.getSiteName().toString().trim() + "\n";
        }
        if (addressList.getLine1() != null && !addressList.getLine1().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine1().toString().trim() + ", ";
        }
        if (addressList.getLine2() != null && !addressList.getLine2().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine2().toString().trim() + ", ";
        }
        if (addressList.getCity() != null && !addressList.getCity().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCity().toString().trim() + ", ";
        }
        /*if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()
                && (addressList.getCity() == null || !addressList.getCity().toString().trim().isEmpty())) {
            addressText = addressText + addressList.getZipCode().toString().trim() + "\n";
        } else {
            if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
                addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "\n";
            }
        }*/
        if (addressList.getState() != null && !addressList.getState().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getState().toString().trim() + ", ";
        }
        if (addressList.getCountry() != null && !addressList.getCountry().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCountry().toString().trim() + "";
        }
        if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
            addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "";
        }

        int textSize1 = getResources().getDimensionPixelSize(R.dimen._11sdp);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen._9sdp);
        SpannableString span1 = new SpannableString(siteNameStr);
        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, siteNameStr.length(), SPAN_INCLUSIVE_INCLUSIVE);
        span1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, siteNameStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(addressText);
        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, addressText.length(), SPAN_INCLUSIVE_INCLUSIVE);

        currentAddressTextView.setText(TextUtils.concat(span1, "", span2));
        for (int i = 0; i < currentAddressListArrayList.size(); i++) {
            if (currentAddressListArrayList.get(i).getSaid().equals(currentAddressListArrayList.get(position).getSaid())) {
                currentAddressListArrayList.get(i).setChecked(true);

            } else {
                currentAddressListArrayList.get(i).setChecked(false);
            }
        }

        if (dialog != null && dialog.isShowing()) {
            addressAdapter.notifyDataSetChanged();
            if (currentAddressListArrayList != null && currentAddressListArrayList.size() > 0) {
                currentAddressEmptyTextView.setVisibility(View.GONE);
            } else {
                currentAddressEmptyTextView.setVisibility(View.VISIBLE);
                currentAddressEmptyTextView.setText("No address is available");
            }

        }
    }

    public void setSiteAddressText(int position) {
        String addressText = "", siteNameStr = "";
        final AddressList addressList = siteAddressListArrayList.get(position);
        if (addressList.getSiteName() != null && !addressList.getSiteName().toString().trim().isEmpty()) {
            siteNameStr = addressList.getSiteName().toString().trim() + "\n";
        }
        if (addressList.getLine1() != null && !addressList.getLine1().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine1().toString().trim() + ", ";
        }
        if (addressList.getLine2() != null && !addressList.getLine2().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getLine2().toString().trim() + ", ";
        }
        if (addressList.getCity() != null && !addressList.getCity().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCity().toString().trim() + ", ";
        }
        /*if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()
                && (addressList.getCity() == null || !addressList.getCity().toString().trim().isEmpty())) {
            addressText = addressText + addressList.getZipCode().toString().trim() + "\n";
        } else {
            if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
                addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "\n";
            }
        }*/
        if (addressList.getState() != null && !addressList.getState().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getState().toString().trim() + ", ";
        }
        if (addressList.getCountry() != null && !addressList.getCountry().toString().trim().isEmpty()) {
            addressText = addressText + addressList.getCountry().toString().trim() + "";
        }
        if (addressList.getZipCode() != null && !addressList.getZipCode().toString().trim().isEmpty()) {
            addressText = addressText + " - " + addressList.getZipCode().toString().trim() + "";
        }
        int textSize1 = getResources().getDimensionPixelSize(R.dimen._11sdp);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen._9sdp);
        SpannableString span1 = new SpannableString(siteNameStr);
        span1.setSpan(new AbsoluteSizeSpan(textSize1), 0, siteNameStr.length(), SPAN_INCLUSIVE_INCLUSIVE);
        span1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, siteNameStr.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        SpannableString span2 = new SpannableString(addressText);
        span2.setSpan(new AbsoluteSizeSpan(textSize2), 0, addressText.length(), SPAN_INCLUSIVE_INCLUSIVE);


        siteAddressTextView.setText(TextUtils.concat(span1, "", span2));
        for (int i = 0; i < siteAddressListArrayList.size(); i++) {
            if (siteAddressListArrayList.get(i).getSaid().equals(siteAddressListArrayList.get(position).getSaid())) {
                siteAddressListArrayList.get(i).setCheckedSite(true);

            } else {
                siteAddressListArrayList.get(i).setCheckedSite(false);
            }
        }

        if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
            siteAddressAdapter.notifyDataSetChanged();
            if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                siteAddressEmptyTextView.setVisibility(View.GONE);
            } else {
                siteAddressEmptyTextView.setVisibility(View.VISIBLE);
                siteAddressEmptyTextView.setText("No address is available");
            }
        }
    }

    @Override
    public void currentAddressItemClick(int position) {
        setCurrentAddressText(position);
        /*if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }*/
    }

    @Override
    public void siteAddressItemClick(int position) {
        setSiteAddressText(position);
        /*if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
            dialogSiteAddress.dismiss();
        }*/
    }

    private void contactPersonData() {

        final ArrayAdapter<ContactPerson> contactPersonArrayAdapter = new ArrayAdapter<ContactPerson>(OrderQuotationsSelectCustomerActivity.this,
                R.layout.simple_spinner_item, contactPersonListArrayList);
        contactPersonArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        contactPersonAutoCompleteTextview.setAdapter(contactPersonArrayAdapter);


        contactPersonAutoCompleteTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactPersonAutoCompleteTextview.showDropDown();
            }
        });

        //when autocomplete is clicked
        contactPersonAutoCompleteTextview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = contactPersonArrayAdapter.getItem(position).getName();
                contactPersonAutoCompleteTextview.setText(selectedItem);
                int pos = -1;
                for (int i = 0; i < contactPersonListArrayList.size(); i++) {
                    if (contactPersonListArrayList.get(i).getName().equals(selectedItem)) {
                        pos = i;
                        break;
                    }
                }
                strContactPerson = contactPersonListArrayList.get(pos).getCodeid();
                strCodeId = contactPersonListArrayList.get(pos).getCodeid();
            }
        });

        contactPersonAutoCompleteTextview.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    strContactPerson = contactPersonAutoCompleteTextview.getText().toString();
                    for (int i = 0; i < contactPersonListArrayList.size(); i++) {
                        String temp = contactPersonListArrayList.get(i).getName();
                        if (strContactPerson.compareTo(temp) == 0) {
                            return;
                        }
                    }
                    contactPersonAutoCompleteTextview.setText("");
                    strContactPerson = "";
                    strCodeId = "";
                }
            }

        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == currentAddressCheckBox) {
            if (isChecked) {
                currentAddressTextView.setVisibility(View.GONE);
                cardViewSiteAddress.setVisibility(View.GONE);
                sameSiteAddressCheckBox.setVisibility(View.GONE);
                noSiteAddressCheckBox.setVisibility(View.GONE);
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
            } else {
                currentAddressTextView.setVisibility(View.VISIBLE);
                cardViewSiteAddress.setVisibility(View.VISIBLE);
                sameSiteAddressCheckBox.setVisibility(View.VISIBLE);
                noSiteAddressCheckBox.setVisibility(View.VISIBLE);
                if (!sameSiteAddressCheckBox.isChecked() && !noSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                } else {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                }
            }
        } else if (buttonView == noSiteAddressCheckBox) {
            if (isChecked) {
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                sameSiteAddressCheckBox.setChecked(false);
            } else {
                if (sameSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);

                } else {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                }
                if (siteAddressTextView.getText().toString().trim().length() == 0) {
                    if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
                        setSiteAddressText(0);
                    }
                }
            }
        } else if (buttonView == sameSiteAddressCheckBox) {
            if (isChecked) {
                noSiteAddressCheckBox.setChecked(false);
                siteAddressTextView.setVisibility(View.GONE);
                linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
            } else {
                if (noSiteAddressCheckBox.isChecked()) {
                    siteAddressTextView.setVisibility(View.GONE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.GONE);
                } else {
                    siteAddressTextView.setVisibility(View.VISIBLE);
                    linearLayoutAddNewSiteAddress.setVisibility(View.VISIBLE);
                    if (siteAddressTextView.getText().toString().trim().length() == 0) {
                        setSiteAddressText(0);
                    }
                }
            }
        }
    }

    private void getCurrentCheckedAddress(ArrayList<AddressList> addressLists) {
        for (int i = 0; i < addressLists.size(); i++) {
            if (addressLists.get(i).isChecked()) {
                currentAddressIndex = i;
            }
        }
    }

    private void getSiteCheckedAddress(ArrayList<AddressList> addressLists) {
        for (int i = 0; i < addressLists.size(); i++) {
            if (addressLists.get(i).isCheckedSite()) {
                siteAddressIndex = i;
            }
        }
    }

    //Current Address Full screen dialog
    public void openCurrentAddressesFullScreenDialog(ArrayList<AddressList> addressListArrayList) {
        dialog = new Dialog(OrderQuotationsSelectCustomerActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_addresses);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) dialog.findViewById(R.id.toolbar);
        RecyclerView recyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        ImageView backArrowImageButton = (ImageView) dialog.findViewById(R.id.back_arrow_imageButton);
        TextView selectAddressTextView = (TextView) dialog.findViewById(R.id.selectAddress_textView);
        CardView card_view = (CardView) dialog.findViewById(R.id.card_view);
        currentAddressEmptyTextView = (TextView) dialog.findViewById(R.id.empty_textView);

        backArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressListArrayList != null && addressListArrayList.size() > 0) {
                    addressListArrayList.get(currentAddressIndex).setChecked(true);
                    setCurrentAddressText(currentAddressIndex);
                }
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (addressListArrayList != null && addressListArrayList.size() > 0) {
                        addressListArrayList.get(currentAddressIndex).setChecked(true);
                        setCurrentAddressText(currentAddressIndex);
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                return true;
            }
        });

        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddNewAddress("current_address");
            }
        });

        getCurrentCheckedAddress(addressListArrayList);
        selectAddressTextView.setText("Current Address");
        selectAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });
        addressAdapter = new AddressAdapter(OrderQuotationsSelectCustomerActivity.this, addressListArrayList, this, "current");
        //Addresses RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OrderQuotationsSelectCustomerActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(addressAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        if (addressListArrayList != null && addressListArrayList.size() > 0) {
            currentAddressEmptyTextView.setVisibility(View.GONE);
        } else {
            currentAddressEmptyTextView.setVisibility(View.VISIBLE);
            currentAddressEmptyTextView.setText("No address is available");
        }

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Site Address Full screen dialog
    public void openSiteAddressesFullScreenDialog(ArrayList<AddressList> addressListArrayList) {
        dialogSiteAddress = new Dialog(OrderQuotationsSelectCustomerActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialogSiteAddress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSiteAddress.setContentView(R.layout.dialog_addresses);
        Window window = dialogSiteAddress.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        RecyclerView recyclerView = (RecyclerView) dialogSiteAddress.findViewById(R.id.recyclerView);
        TextView selectAddressTextView = (TextView) dialogSiteAddress.findViewById(R.id.selectAddress_textView);
        CardView card_view = (CardView) dialogSiteAddress.findViewById(R.id.card_view);
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) dialogSiteAddress.findViewById(R.id.toolbar);
        ImageView backArrowImageButton = (ImageView) dialogSiteAddress.findViewById(R.id.back_arrow_imageButton);
        siteAddressEmptyTextView = (TextView) dialogSiteAddress.findViewById(R.id.empty_textView);
        backArrowImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addressListArrayList != null && addressListArrayList.size() > 0) {
                    addressListArrayList.get(siteAddressIndex).setCheckedSite(true);
                    setSiteAddressText(siteAddressIndex);
                }
                if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
                    dialogSiteAddress.dismiss();
                }
            }
        });

        dialogSiteAddress.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (addressListArrayList != null && addressListArrayList.size() > 0) {
                        addressListArrayList.get(siteAddressIndex).setCheckedSite(true);
                        setSiteAddressText(siteAddressIndex);
                    }
                    if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
                        dialogSiteAddress.dismiss();
                    }
                }
                return true;
            }
        });

        card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddNewAddress("site_address");
            }
        });

        getSiteCheckedAddress(addressListArrayList);
        selectAddressTextView.setText("Site Address");
        selectAddressTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogSiteAddress != null && dialogSiteAddress.isShowing()) {
                    dialogSiteAddress.dismiss();
                }
            }
        });

        siteAddressAdapter = new SiteAddressAdapter(OrderQuotationsSelectCustomerActivity.this, addressListArrayList, this, "site");
        //Addresses RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(OrderQuotationsSelectCustomerActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(siteAddressAdapter);
        recyclerView.setNestedScrollingEnabled(false);

        if (siteAddressListArrayList != null && siteAddressListArrayList.size() > 0) {
            siteAddressEmptyTextView.setVisibility(View.GONE);
        } else {
            siteAddressEmptyTextView.setVisibility(View.VISIBLE);
            siteAddressEmptyTextView.setText("No address is available");
        }

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        dialogSiteAddress.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialogSiteAddress.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialogSiteAddress.show();
    }

    //Add Single Contact Person
    public void addContactPerson(final Contacts contacts) {
        task = getString(R.string.add_customer_add_contact_person);
        if (AppPreferences.getIsLogin(OrderQuotationsSelectCustomerActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(OrderQuotationsSelectCustomerActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(OrderQuotationsSelectCustomerActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(OrderQuotationsSelectCustomerActivity.this, AppUtils.DOMAIN);
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
            if (contacts.getContactTypeId() != null &&
                    !contacts.getContactTypeId().isEmpty()) {
                jsonObject.put("cp-attribute-" + contacts.getContactTypeId() + "",
                        contacts.getContactTypeValue());
            }
            if (contacts.getExtraAttribute() != null && !contacts.getExtraAttribute().isEmpty()) {
                jsonObject.put("attribute-" + contacts.getExtraAttribute() + "",
                        contacts.getSpecifyText());
            }
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
                        ContactPerson contactPerson = new ContactPerson();
                        contactPerson.setCodeid(apiResponse.getData().getCodeid());
                        contactPerson.setName(contacts.getName());
                        contactPersonListArrayList.add(contactPerson);
                        contactPersonAutoCompleteTextview.setText(contacts.getName());
                        contactPersonAutoCompleteTextview.setSelection(contactPerson.getName().length());
                        strCodeId = apiResponse.getData().getCodeid();
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage());
                    } else {
                        Toast.makeText(OrderQuotationsSelectCustomerActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 300) {
            getFlagToRefresh(data.getStringExtra("said"),
                    data.getStringExtra("cuid"), data.getStringExtra("addressType"));
        } else if (requestCode == ADD_QUOTATION_CUSTOMER_REQUEST_CODE) {
            if (data != null) {
                strCuid = data.getStringExtra("cuId");
                if (data.getStringExtra("contactPerson") != null) {
                    contactPersonAutoCompleteTextview.setText("" + data.getStringExtra("contactPerson"));
                }
                if (data.getStringExtra("codeid") != null) {
                    strCodeId = data.getStringExtra("codeid");
                } else {
                    strCodeId = "";
                }
                setNewCustomersDetails(data.getStringExtra("cuId"), data.getStringExtra("name"),
                        data.getStringExtra("email"), data.getStringExtra("mobile"));
                //setNewCustomersDetails(strCuid);
            }
        }
    }

    private void setNewCustomersDetails(String strCuid, String cName, String cEmail, String cMobile) {
        if (cName != null && !cName.toString().trim().isEmpty()) {
            customerNameValueEditText.setText(cName.toString().trim());
        }
        if (cEmail != null && !cEmail.toString().trim().isEmpty()) {
            emailValueEditText.setText(cEmail.toString().trim());
            emailLayout.setVisibility(View.VISIBLE);
        } else {
            emailLayout.setVisibility(View.GONE);
        }
        if (cMobile != null && !cMobile.toString().trim().isEmpty()) {
            mobileValueEditText.setText(cMobile.toString().trim());
            mobileLayout.setVisibility(View.VISIBLE);
        } else {
            mobileLayout.setVisibility(View.GONE);
        }

        cardViewSiteAddress.setVisibility(View.VISIBLE);
        cardViewCurrentAddress.setVisibility(View.VISIBLE);
        //currentAddressLayout.setVisibility(View.VISIBLE);
        customerNameValueEditText.post(new Runnable() {
            @Override
            public void run() {
                customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
            }
        });

        //Call API for addresses
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getCustomersAddress(strCuid);
        } else {
            Toast.makeText(OrderQuotationsSelectCustomerActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    public void getFlagToRefresh(String said, String cuid, String addressType) {
        if (addressType.equals("current_address")) {
            this.newCurrentAddressSaid = said;
        } else if (addressType.equals("site_address")) {
            this.newSiteAddressSaid = said;
        }
        getCustomersAddress(cuid);
    }
}