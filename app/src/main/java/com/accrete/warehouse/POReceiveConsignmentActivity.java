package com.accrete.warehouse;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.adapter.VendorsAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ConsignmentItem;
import com.accrete.warehouse.model.PurchaseDetails;
import com.accrete.warehouse.model.PurchaseOrderData;
import com.accrete.warehouse.model.TransportationData;
import com.accrete.warehouse.model.Vendor;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

public class POReceiveConsignmentActivity extends AppCompatActivity implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, VendorsAdapter.VendorsAdapterListener, PassDateToCounsellor {
    private NestedScrollView nestedScrollView;
    private TextView purchaseOrderDetailsTitleTextView;
    private TextView purchaseOrderDetailsSubTitleTextView;
    private View purchaseOrderDetailsView;
    private LinearLayout poIdLayout;
    private TextView poIdTextView;
    private LinearLayout authorizedByLayout;
    private TextView authorizedByTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView vendorDetailsTitleTextView;
    private TextView vendorDetailsSubTitleTextView;
    private View vendorDetailsView;
    private TextView vendorTitleTextView;
    private EditText vendorValueEditText;
    private ImageButton clearVendorInfoImageButton;
    private TextView purchaseDetailsTitleTextView;
    private TextView purchaseDetailsSubTitleTextView;
    private View purchaseDetailsView;
    private TextView receiveDateTitleTextView;
    private TextView receiveDateValueTextView;
    private TextView invoiceNumberTitleTextView;
    private TextView invoiceNumberValueTextView;
    private TextView invoiceDateTitleTextView;
    private TextView invoiceDateValueTextView;
    private TextView consignmentItemsListTitleTextView;
    private TextView consignmentItemsListSubTitleTextView;
    private View consignmentItemsListView;
    private EditText searchItemValueEditText;
    private RecyclerView itemsRecyclerView;
    private CheckBox addTransportationCheckBoxTextView;
    private LinearLayout transportationLayout;
    private TextView transportationDetailsTitleTextView;
    private TextView transportationDetailsSubTitleTextView;
    private View transportationDetailsView;
    private TextView transporterTitleTextView;
    private EditText transporterValueEditText;
    private ImageButton clearTransporterInfoImageButton;
    private EditText weightValueEditText;
    private EditText lrNumberValueEditText;
    private EditText vehicleNumberValueEditText;
    private TextView expectedDateTitleTextView;
    private TextView expectedDateValueTextView;
    private TextView saveTextView;
    private String purOrId, status, vendorId, transporterId;
    private Dialog dialog;
    private AutoCompleteTextView vendorSearchAutoCompleteTextView;
    private RecyclerView vendorsRecyclerView;
    private ArrayList<Vendor> vendorArrayList = new ArrayList<>();
    private VendorsAdapter vendorsAdapter;
    private AllDatePickerFragment datePickerFragment;
    private String strDate;
    private List<ConsignmentItem> consignmentItemList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poreceive_consignment);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.purOrId))) {
            purOrId = getIntent().getStringExtra(getString(R.string.purOrId));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);

        findViews();
    }

    private void findViews() {
        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollView);
        purchaseOrderDetailsTitleTextView = (TextView) findViewById(R.id.purchaseOrderDetailsTitle_textView);
        purchaseOrderDetailsSubTitleTextView = (TextView) findViewById(R.id.purchaseOrderDetailsSubTitle_textView);
        purchaseOrderDetailsView = (View) findViewById(R.id.purchaseOrderDetails_view);
        poIdLayout = (LinearLayout) findViewById(R.id.poId_layout);
        poIdTextView = (TextView) findViewById(R.id.poId_textView);
        authorizedByLayout = (LinearLayout) findViewById(R.id.authorizedBy_layout);
        authorizedByTextView = (TextView) findViewById(R.id.authorizedBy_textView);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        statusTextView = (TextView) findViewById(R.id.status_textView);
        vendorDetailsTitleTextView = (TextView) findViewById(R.id.vendorDetailsTitle_textView);
        vendorDetailsSubTitleTextView = (TextView) findViewById(R.id.vendorDetailsSubTitle_textView);
        vendorDetailsView = (View) findViewById(R.id.vendorDetails_view);
        vendorTitleTextView = (TextView) findViewById(R.id.vendor_title_textView);
        vendorValueEditText = (EditText) findViewById(R.id.vendor_value_editText);
        clearVendorInfoImageButton = (ImageButton) findViewById(R.id.clear_vendorInfo_imageButton);
        purchaseDetailsTitleTextView = (TextView) findViewById(R.id.purchaseDetailsTitle_textView);
        purchaseDetailsSubTitleTextView = (TextView) findViewById(R.id.purchaseDetailsSubTitle_textView);
        purchaseDetailsView = (View) findViewById(R.id.purchaseDetails_view);
        receiveDateTitleTextView = (TextView) findViewById(R.id.receive_date_title_textView);
        receiveDateValueTextView = (TextView) findViewById(R.id.receive_date_value_textView);
        invoiceNumberTitleTextView = (TextView) findViewById(R.id.invoice_number_title_textView);
        invoiceNumberValueTextView = (TextView) findViewById(R.id.invoice_number_value_textView);
        invoiceDateTitleTextView = (TextView) findViewById(R.id.invoice_date_title_textView);
        invoiceDateValueTextView = (TextView) findViewById(R.id.invoice_date_value_textView);
        consignmentItemsListTitleTextView = (TextView) findViewById(R.id.consignmentItemsListTitle_textView);
        consignmentItemsListSubTitleTextView = (TextView) findViewById(R.id.consignmentItemsListSubTitle_textView);
        consignmentItemsListView = (View) findViewById(R.id.consignmentItemsList_view);
        searchItemValueEditText = (EditText) findViewById(R.id.search_item_value_editText);
        itemsRecyclerView = (RecyclerView) findViewById(R.id.items_recyclerView);
        addTransportationCheckBoxTextView = (CheckBox) findViewById(R.id.add_transportation_checkBox_textView);
        transportationLayout = (LinearLayout) findViewById(R.id.transportation_layout);
        transportationDetailsTitleTextView = (TextView) findViewById(R.id.transportationDetailsTitle_textView);
        transportationDetailsSubTitleTextView = (TextView) findViewById(R.id.transportationDetailsSubTitle_textView);
        transportationDetailsView = (View) findViewById(R.id.transportationDetails_view);
        transporterTitleTextView = (TextView) findViewById(R.id.transporter_title_textView);
        transporterValueEditText = (EditText) findViewById(R.id.transporter_value_editText);
        clearTransporterInfoImageButton = (ImageButton) findViewById(R.id.clear_transporterInfo_imageButton);
        weightValueEditText = (EditText) findViewById(R.id.weight_value_editText);
        lrNumberValueEditText = (EditText) findViewById(R.id.lr_number_value_editText);
        vehicleNumberValueEditText = (EditText) findViewById(R.id.vehicle_number_value_editText);
        expectedDateTitleTextView = (TextView) findViewById(R.id.expected_date_title_textView);
        expectedDateValueTextView = (TextView) findViewById(R.id.expected_date_value_textView);
        saveTextView = (TextView) findViewById(R.id.save_textView);

        //Items RecyclerView
    /*    purchaseOrderAdapter = new PurchaseOrderAdapter(getActivity(), purchaseOrderList);
        mLayoutManager = new LinearLayoutManager(getActivity());
        receiveAgainstPurchaseOrderRecyclerView.setLayoutManager(mLayoutManager);
        receiveAgainstPurchaseOrderRecyclerView.setHasFixedSize(true);
        receiveAgainstPurchaseOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        receiveAgainstPurchaseOrderRecyclerView.setNestedScrollingEnabled(false);
        receiveAgainstPurchaseOrderRecyclerView.setAdapter(purchaseOrderAdapter);*/


        //Mandatory Fields
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveDateTitleTextView.setText(TextUtils.concat(getString(R.string.receive_date_title), spannableStringBuilder));
        invoiceNumberTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
        invoiceDateTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_date_title), spannableStringBuilder));

        clearVendorInfoImageButton.setOnClickListener(this);
        clearTransporterInfoImageButton.setOnClickListener(this);
        addTransportationCheckBoxTextView.setOnCheckedChangeListener(this);
        vendorValueEditText.setOnClickListener(this);
        transporterValueEditText.setOnClickListener(this);
        receiveDateValueTextView.setOnClickListener(this);
        invoiceDateValueTextView.setOnClickListener(this);
        expectedDateValueTextView.setOnClickListener(this);
        transportationLayout.setVisibility(View.GONE);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        if (!NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getReceiveConsignmentPrefilledData(POReceiveConsignmentActivity.this, purOrId);
                }
            }, 200);
        } else {
            Toast.makeText(POReceiveConsignmentActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_vendorInfo_imageButton:
                vendorValueEditText.setText("");
                vendorId = "";
                break;
            case R.id.clear_transporterInfo_imageButton:
                transporterValueEditText.setText("");
                transporterId = "";
                break;
            case R.id.vendor_value_editText:
                if (vendorValueEditText.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("vendor");
                }
                break;
            case R.id.transporter_value_editText:
                if (transporterValueEditText.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("transporter");
                }
                break;
            case R.id.expected_date_value_textView:
                datePickerFragment.show(getSupportFragmentManager(), "expected");
                break;
            case R.id.receive_date_value_textView:
                datePickerFragment.show(getSupportFragmentManager(), "receive");
                break;
            case R.id.invoice_date_value_textView:
                datePickerFragment.show(getSupportFragmentManager(), "invoice");
                break;
        }
    }

    public void getReceiveConsignmentPrefilledData(final Activity activity, String purOrId) {
        task = getString(R.string.fetch_receive_consignment_prefilled_data);
        if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.viewOrderDetails(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(activity, AppUtils.WAREHOUSE_CHK_ID), purOrId);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        setData(apiResponse.getData().getPurchaseOrderData(),
                                apiResponse.getData().getPurchaseDetails(),
                                apiResponse.getData().getTransportationData(),
                                apiResponse.getData().getIsExistTransportationDetails());

                        for (final ConsignmentItem consignmentItem : apiResponse.getData().getConsignmentItems()) {
                            if (consignmentItem != null) {
                                consignmentItemList.add(consignmentItem);
                            }
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
            }
        });

    }

    public void setData(PurchaseOrderData purchaseOrderData, PurchaseDetails purchaseDetails, TransportationData transportationData,
                        String transportationCheck) {

        //PO ID
        if (purchaseOrderData.getPoid() != null && !purchaseOrderData.getPoid().isEmpty()) {
            poIdLayout.setVisibility(View.VISIBLE);
            poIdTextView.setText(purchaseOrderData.getPoid());
        } else {
            poIdLayout.setVisibility(View.GONE);
        }

        //Authorized By
        if (purchaseOrderData.getAuthorizedBy() != null && !purchaseOrderData.getAuthorizedBy().isEmpty()) {
            authorizedByLayout.setVisibility(View.VISIBLE);
            authorizedByTextView.setText(purchaseOrderData.getAuthorizedBy());
        } else {
            authorizedByLayout.setVisibility(View.GONE);
        }

        //Status
        if (purchaseOrderData.getStatus() != null && !purchaseOrderData.getStatus().isEmpty()) {
            statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) statusTextView.getBackground();

            if (purchaseOrderData.getPurorsid().equals("1")) {
                drawable.setColor(getResources().getColor(R.color.green_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("2")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("3")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("4")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("5")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("6")) {
                drawable.setColor(getResources().getColor(R.color.orange_purchase_order));
            } else if (purchaseOrderData.getPurorsid().equals("7")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
            }

            statusLayout.setVisibility(View.VISIBLE);
            statusTextView.setText(purchaseOrderData.getStatus());
        } else {
            statusLayout.setVisibility(View.GONE);
        }

        //Vendor Name
        if (purchaseOrderData.getVendorName() != null && !purchaseOrderData.getVendorName().isEmpty()) {
            vendorValueEditText.setText(purchaseOrderData.getVendorName());
        }

        //Transporter
        if (transportationData.getVendorName() != null && !transportationData.getVendorName().isEmpty()) {
            transporterValueEditText.setText(transportationData.getVendorName());
        }

        //Weight
        if (transportationData.getWeight() != null && !transportationData.getWeight().isEmpty()) {
            weightValueEditText.setText(transportationData.getWeight());
        }

        //LR Number
        if (transportationData.getLrNumber() != null && !transportationData.getLrNumber().isEmpty()) {
            lrNumberValueEditText.setText(transportationData.getLrNumber());
        }

        //Vehicle Number
        if (transportationData.getVehicleNumber() != null && !transportationData.getVehicleNumber().isEmpty()) {
            vehicleNumberValueEditText.setText(transportationData.getVehicleNumber());
        }

        //Expected Date
        if (transportationData.getExpectedDate() != null && !transportationData.getExpectedDate().isEmpty()) {
            expectedDateValueTextView.setText(transportationData.getExpectedDate());
        }

        //Check/Uncheck Checkbox
        if (transportationCheck != null && transportationCheck.equals("1")) {
            addTransportationCheckBoxTextView.setChecked(true);
        } else {
            addTransportationCheckBoxTextView.setChecked(false);
        }

        //Receive Date
        if (purchaseDetails.getReceiveDate() != null && !purchaseDetails.getReceiveDate().isEmpty()) {
            receiveDateValueTextView.setText(purchaseDetails.getReceiveDate());
        }

        //Invoice Date
        if (purchaseDetails.getInvoiceDate() != null && !purchaseDetails.getInvoiceDate().isEmpty()) {
            invoiceDateValueTextView.setText(purchaseDetails.getInvoiceDate());
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            transportationLayout.setVisibility(View.VISIBLE);
        } else {
            transportationLayout.setVisibility(View.GONE);
        }
    }

    //Open Dialog to select vendors
    public void openVendorSearchDialog(final String userType) {
        dialog = new Dialog(POReceiveConsignmentActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        vendorSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.vendor_search_autoCompleteTextView);
        vendorsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(POReceiveConsignmentActivity.this);
        vendorsAdapter = new VendorsAdapter(POReceiveConsignmentActivity.this,
                vendorArrayList, this, userType);

        vendorsRecyclerView.setLayoutManager(mLayoutManager);
        vendorsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        vendorsRecyclerView.setAdapter(vendorsAdapter);
        vendorsRecyclerView.setNestedScrollingEnabled(false);

        if (userType.equals("vendor")) {
            vendorSearchAutoCompleteTextView.setHint("Search Vendor");
        } else {
            vendorSearchAutoCompleteTextView.setHint("Search Transporter");
        }
        vendorSearchAutoCompleteTextView.setThreshold(1);
        vendorSearchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (vendorSearchAutoCompleteTextView.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchVendor(s.toString().trim(), userType);
                    } else {
                        Toast.makeText(POReceiveConsignmentActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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

    //Searching Vendor from API after getting input from openVendorSearchDialog
    public void searchVendor(String str, final String userType) {
        task = getString(R.string.vendor_search_task);
        if (AppPreferences.getIsLogin(POReceiveConsignmentActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(POReceiveConsignmentActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(POReceiveConsignmentActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(POReceiveConsignmentActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.searchVendor(version, key, task, userId, accessToken, str);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (vendorArrayList != null && vendorArrayList.size() > 0) {
                            vendorArrayList.clear();
                        }
                        for (final Vendor vendor : apiResponse.getData().getVendors()) {
                            if (vendor != null) {
                                vendorArrayList.add(vendor);
                            }
                        }
                        refreshVendorRecyclerView(userType);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
            }
        });

    }

    //Refreshing data after getting input from openVendorSearchDialog
    private void refreshVendorRecyclerView(final String userType) {
        vendorsAdapter.notifyDataSetChanged();

        vendorSearchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Vendor selected = (Vendor) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    if (userType.equals("vendor")) {
                        vendorValueEditText.setText(selected.getName().toString().trim());
                        vendorValueEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                vendorValueEditText.setSelection(vendorValueEditText.getText().toString().length());
                            }
                        });
                        //Get Vendor Id
                        vendorId = selected.getId();
                    } else {
                        transporterValueEditText.setText(selected.getName().toString().trim());
                        transporterValueEditText.post(new Runnable() {
                            @Override
                            public void run() {
                                transporterValueEditText.setSelection(transporterValueEditText.getText().toString().length());
                            }
                        });
                        //Get Transporter Id
                        transporterId = selected.getId();
                    }
                }

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onVendorClick(int position, String userType) {
        Vendor selected = vendorArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            if (userType.equals("vendor")) {
                vendorValueEditText.setText(selected.getName().toString().trim());
                //Get Vendor's ID
                vendorId = selected.getId();
            } else {
                transporterValueEditText.setText(selected.getName().toString().trim());
                //Get Transporter's ID
                transporterId = selected.getId();
            }
        }


        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            strDate = s;
            Date startDate = formatter.parse(strDate);
            strDate = targetFormat.format(startDate);
            if (datePickerFragment.getTag().equals("expected")) {
                expectedDateValueTextView.setText(s);
            } else if (datePickerFragment.getTag().equals("receive")) {
                receiveDateValueTextView.setText(s);
            } else if (datePickerFragment.getTag().equals("invoice")) {
                invoiceDateValueTextView.setText(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void passTime(String s) {

    }
}
