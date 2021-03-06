package com.accrete.warehouse.fragment.receiveConsignment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.AuthorizedByUserAdapter;
import com.accrete.warehouse.adapter.ItemsVariationAdapter;
import com.accrete.warehouse.adapter.ReceiveConsignmentItemsAdapter;
import com.accrete.warehouse.adapter.VendorsAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ConsignmentItem;
import com.accrete.warehouse.model.ItemLabels;
import com.accrete.warehouse.model.ItemList;
import com.accrete.warehouse.model.Measurements;
import com.accrete.warehouse.model.PurchaseData;
import com.accrete.warehouse.model.PurchaseDetails;
import com.accrete.warehouse.model.PurchaseOrderData;
import com.accrete.warehouse.model.TransportationData;
import com.accrete.warehouse.model.User;
import com.accrete.warehouse.model.Vendor;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
        CompoundButton.OnCheckedChangeListener, VendorsAdapter.VendorsAdapterListener, PassDateToCounsellor,
        ReceiveConsignmentItemsAdapter.ReceiveConsignmentItemsAdapterListener,
        ItemsVariationAdapter.ItemsVariationAdapterListener, AuthorizedByUserAdapter.AuthorizedByUserAdapterListener {

    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private LinearLayout poIdDateLayout;
    private TextView poIdDateTextView;
    private LinearLayout vendorNameLayout;
    private TextView vendorNameTextView;
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
    private EditText invoiceNumberValueTextView;
    private TextView invoiceDateTitleTextView;
    private TextView invoiceDateValueTextView;
    private TextView consignmentItemsListTitleTextView;
    private TextView consignmentItemsListSubTitleTextView;
    private View consignmentItemsListView;
    private EditText searchItemValueEditText;
    private CheckBox addTransportationCheckBoxTextView;
    private LinearLayout transportationLayout;
    private TextView transportationDetailsTitleTextView;
    private TextView transportationDetailsSubTitleTextView;
    private View transportationDetailsView;
    private TextView transporterTitleTextView;
    private TextView transporterValueEditText;
    private ImageButton clearTransporterInfoImageButton;
    private EditText weightValueEditText;
    private EditText lrNumberValueEditText;
    private EditText vehicleNumberValueEditText;
    private TextView expectedDateTitleTextView;
    private TextView expectedDateValueTextView;
    private TextView saveTextView;
    private TextView authorizedByTitleTextView;
    private EditText authorizedByValueEditText;
    private ImageButton clearAuthorizedByImageButton;
    private String purOrId, status, vendorId, transporterId;
    private Dialog dialog, productsDialog, authorizedByDialog;
    private AutoCompleteTextView vendorSearchAutoCompleteTextView, itemSearchAutoCompleteTextView, authorizedBySearchAutoCompleteTextView;
    private RecyclerView vendorsRecyclerView, itemsRecyclerView, itemsListRecyclerView, authorizedByRecyclerView;
    private ArrayList<Vendor> vendorArrayList = new ArrayList<>();
    private ArrayList<ItemList> itemListArrayList = new ArrayList<>();
    private ItemLabels itemLabels = new ItemLabels();
    private VendorsAdapter vendorsAdapter;
    private AllDatePickerFragment datePickerFragment;
    private String strDate;
    private List<ConsignmentItem> consignmentItemList = new ArrayList<>();
    private List<ItemLabels> itemLabelsList = new ArrayList<>();
    private List<ItemList> itemLists = new ArrayList<>();
    private ReceiveConsignmentItemsAdapter receiveConsignmentItemsAdapter;
    private LinearLayoutManager mLayoutManager;
    private ItemsVariationAdapter itemsVariationAdapter;
    private String strInvoiceNumber, strVendor, strPurchaseOrderId, strChkId, strWeight, strExpectedDate, strPurchaseDate,
            strInvoiceDate, strTransportationCheckBoxValue, strLRNumber, strVehicleNumber, strHsn, strAuthorizedById;
    private TextView expiryDateValueTextView;
    private String flagForInvoiceNumber;
    private String flagToShow;
    private AuthorizedByUserAdapter authorizedByUserAdapter;
    private ArrayList<User> userArrayList = new ArrayList<>();
    private TextView manufactureDateValueTextView;
    private TextView barcodeEdittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poreceive_consignment);

        if (getIntent() != null && !getIntent().getStringExtra(getString(R.string.purOrId)).isEmpty()) {
            purOrId = getIntent().getStringExtra(getString(R.string.purOrId));
        }

        if (getIntent() != null && !getIntent().getStringExtra(getString(R.string.flag_stock)).isEmpty()) {
            flagToShow = getIntent().getStringExtra(getString(R.string.flag_stock));
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
        authorizedByTitleTextView = (TextView) findViewById(R.id.authorizedBy_title_textView);
        authorizedByValueEditText = (EditText) findViewById(R.id.authorizedBy_value_editText);
        clearAuthorizedByImageButton = (ImageButton) findViewById(R.id.clear_authorizedBy_imageButton);
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
        invoiceNumberValueTextView = (EditText) findViewById(R.id.invoice_number_value_textView);
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
        transporterValueEditText = (TextView) findViewById(R.id.transporter_value_editText);
        clearTransporterInfoImageButton = (ImageButton) findViewById(R.id.clear_transporterInfo_imageButton);
        weightValueEditText = (EditText) findViewById(R.id.weight_value_editText);
        lrNumberValueEditText = (EditText) findViewById(R.id.lr_number_value_editText);
        vehicleNumberValueEditText = (EditText) findViewById(R.id.vehicle_number_value_editText);
        expectedDateTitleTextView = (TextView) findViewById(R.id.expected_date_title_textView);
        expectedDateValueTextView = (TextView) findViewById(R.id.expected_date_value_textView);
        saveTextView = (TextView) findViewById(R.id.save_textView);

        poIdDateLayout = (LinearLayout) findViewById(R.id.poId_date_layout);
        poIdDateTextView = (TextView) findViewById(R.id.poId_date_textView);
        vendorNameLayout = (LinearLayout) findViewById(R.id.vendor_name_layout);
        vendorNameTextView = (TextView) findViewById(R.id.vendor_name_textView);

        //Items RecyclerView
        /*if (flagToShow.equals("stockRequest")) {
            receiveConsignmentItemsAdapter = new ReceiveConsignmentItemsAdapter(this, consignmentItemList,
                    this, "stockRequest");*/
        //  } else {
        receiveConsignmentItemsAdapter = new ReceiveConsignmentItemsAdapter(this, consignmentItemList,
                this, "PurchaseOrder");
        // }

        mLayoutManager = new LinearLayoutManager(this);
        itemsRecyclerView.setLayoutManager(mLayoutManager);
        itemsRecyclerView.setHasFixedSize(true);
        itemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsRecyclerView.setNestedScrollingEnabled(false);
        itemsRecyclerView.setAdapter(receiveConsignmentItemsAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        clearVendorInfoImageButton.setOnClickListener(this);
        clearTransporterInfoImageButton.setOnClickListener(this);
        addTransportationCheckBoxTextView.setOnCheckedChangeListener(this);
        vendorValueEditText.setOnClickListener(this);
        authorizedByValueEditText.setOnClickListener(this);
        clearAuthorizedByImageButton.setOnClickListener(this);
        transporterValueEditText.setOnClickListener(this);
        receiveDateValueTextView.setOnClickListener(this);
        invoiceDateValueTextView.setOnClickListener(this);
        expectedDateValueTextView.setOnClickListener(this);
        searchItemValueEditText.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        transportationLayout.setVisibility(View.GONE);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        if (flagToShow.equals("stockRequest")) {
            authorizedByTitleTextView.setVisibility(View.VISIBLE);
            authorizedByValueEditText.setVisibility(View.VISIBLE);
            clearAuthorizedByImageButton.setVisibility(View.VISIBLE);


            if (!NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getStockRequestConsignmentPrefilledData(POReceiveConsignmentActivity.this, purOrId);
                    }
                }, 200);
            } else {
                Toast.makeText(POReceiveConsignmentActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        } else {

            authorizedByTitleTextView.setVisibility(View.GONE);
            authorizedByValueEditText.setVisibility(View.GONE);
            clearAuthorizedByImageButton.setVisibility(View.GONE);

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

        //Mandatory Fields
        String colored = " *";
        final Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveDateTitleTextView.setText(TextUtils.concat(getString(R.string.receive_date_title), spannableStringBuilder));
        // invoiceDateTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_date_title), spannableStringBuilder));

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clear_authorizedBy_imageButton:
                authorizedByValueEditText.setText("");
                strAuthorizedById = "";
                break;
            case R.id.authorizedBy_value_editText:
                if (authorizedByValueEditText.getText().toString().trim().length() == 0) {
                    openAuthorizedBySearchDialog("authorizedBy");
                }
                break;
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
            case R.id.search_item_value_editText:
                openItemSearchDialog();
                break;
            case R.id.save_textView:
                //Disable Button
                saveTextView.setEnabled(false);
                if (getPostData()) {
                    status = NetworkUtil.getConnectivityStatusString(this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        if (invoiceNumberValueTextView.getText().toString() == null
                                || invoiceNumberValueTextView.getText().toString().isEmpty() && flagForInvoiceNumber != null && flagForInvoiceNumber.equals("1")) {
                            Toast.makeText(this, "Please enter invoice number", Toast.LENGTH_SHORT).show();
                        } else if (consignmentItemList != null && consignmentItemList.size() > 0) {
                            ReceiveItemsAsyncTask receiveItemsAsyncTask = new ReceiveItemsAsyncTask(this);
                            receiveItemsAsyncTask.execute();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }
                //Enable Button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        saveTextView.setEnabled(true);
                    }
                }, 3000);
                break;
        }
    }

    //Get data from fields and post
    public boolean getPostData() {
      /*  if (invoiceNumberValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter Invoice number.", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        strInvoiceNumber = invoiceNumberValueTextView.getText().toString().trim();
        strChkId = AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID);
        strWeight = weightValueEditText.getText().toString().trim();

        //Expected Date
        try {
            strExpectedDate = targetFormat.format(dateFormatter.parse(expectedDateValueTextView.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strExpectedDate = "";
        }

        //Receive Date
        try {
            strPurchaseDate = targetFormat.format(dateFormatter.parse(receiveDateValueTextView.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strPurchaseDate = "";
        }

        //Invoice Date
        try {
            strInvoiceDate = targetFormat.format(dateFormatter.parse(invoiceDateValueTextView.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strInvoiceDate = "";
        }

        if (addTransportationCheckBoxTextView.isChecked()) {
            strTransportationCheckBoxValue = "1";
            strLRNumber = lrNumberValueEditText.getText().toString().trim();
            strVehicleNumber = vehicleNumberValueEditText.getText().toString().trim();
        } else {
            strTransportationCheckBoxValue = "0";
        }
        return true;
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
                        itemLabels = apiResponse.getData().getItemLabels();


                        for (final ConsignmentItem consignmentItem : apiResponse.getData().getConsignmentItems()) {
                            if (consignmentItem != null) {
                                consignmentItemList.add(consignmentItem);
                            }
                        }

                        flagForInvoiceNumber = apiResponse.getData().getIsInvoiceNumberEnabled();
                        //Mandatory Fields
                        String colored = " *";
                        final Spannable spannableStringBuilder = new SpannableString(colored);
                        int end = spannableStringBuilder.length();
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (flagForInvoiceNumber != null && flagForInvoiceNumber.equals("1")) {
                            invoiceNumberTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
                        }

                        receiveConsignmentItemsAdapter.notifyDataSetChanged();
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


    public void getStockRequestConsignmentPrefilledData(final Activity activity, String stockreqid) {
        task = getString(R.string.fetch_stock_receive_consignment_prefilled_data);
        if (AppPreferences.getIsLogin(activity, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(activity, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(activity, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(activity, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.fetchStockDetails(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(activity, AppUtils.WAREHOUSE_CHK_ID), stockreqid);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        setDataForStock(apiResponse.getData().getPurchaseData());
                        itemLabels = apiResponse.getData().getItemLabels();

                        for (final ConsignmentItem consignmentItem : apiResponse.getData().getConsignmentItems()) {
                            if (consignmentItem != null) {
                                consignmentItemList.add(consignmentItem);
                            }
                        }

                        flagForInvoiceNumber = apiResponse.getData().getIsInvoiceNumberEnabled();
                        //Mandatory Fields
                        String colored = " *";
                        final Spannable spannableStringBuilder = new SpannableString(colored);
                        int end = spannableStringBuilder.length();
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (flagForInvoiceNumber != null && flagForInvoiceNumber.equals("1")) {
                            invoiceNumberTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
                        }

                        receiveConsignmentItemsAdapter.notifyDataSetChanged();
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
                        String isExistTransportationDetails) {

        strPurchaseOrderId = purchaseOrderData.getPurorid();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

        //PO ID
        if (purchaseOrderData.getPoid() != null && !purchaseOrderData.getPoid().isEmpty()) {
            poIdLayout.setVisibility(View.VISIBLE);
            poIdTextView.setText(purchaseOrderData.getPoid());
        } else {
            poIdLayout.setVisibility(View.GONE);
        }

        //PO Date
      /*  if (purchaseOrderData.() != null && !purchaseOrderData.getPoDate().isEmpty()) {
            poIdDateLayout.setVisibility(View.VISIBLE);
            try {
                poIdDateTextView.setText(formatter.format(targetFormat.parse(purchaseOrderData.getPoDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            poIdDateLayout.setVisibility(View.GONE);
        }
*/
        //Vendor
        if (purchaseOrderData.getVendorName() != null && !purchaseOrderData.getVendorName().isEmpty()) {
            vendorNameLayout.setVisibility(View.VISIBLE);
            vendorNameTextView.setText(purchaseOrderData.getVendorName());
        } else {
            vendorNameLayout.setVisibility(View.GONE);
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
            vendorId = purchaseOrderData.getVendorId();

        }

        //Transporter
        if (transportationData.getVendorName() != null && !transportationData.getVendorName().isEmpty()) {
            transporterValueEditText.setText(transportationData.getVendorName());
            transporterId = transportationData.getVendorId();
        }

        //Weight
        if (transportationData.getWeight() != null && !transportationData.getWeight().isEmpty()) {
            DecimalFormat quantityFormat = new DecimalFormat("######");
            weightValueEditText.setText(String.valueOf(quantityFormat.format(Double.valueOf(transportationData.getWeight()))));
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
            try {
                Date startDate = targetFormat.parse(transportationData.getExpectedDate());
                expectedDateValueTextView.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Check/Uncheck Checkbox
        if (isExistTransportationDetails != null && isExistTransportationDetails.equals("1")) {
            addTransportationCheckBoxTextView.setChecked(true);
        } else {
            addTransportationCheckBoxTextView.setChecked(false);
        }

        //Receive Date
        if (purchaseDetails.getReceiveDate() != null && !purchaseDetails.getReceiveDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseDetails.getReceiveDate());
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                receiveDateValueTextView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Invoice Date
        if (purchaseDetails.getInvoiceDate() != null && !purchaseDetails.getInvoiceDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseDetails.getInvoiceDate());
                invoiceDateValueTextView.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }


    public void setDataForStock(PurchaseData purchaseOrderData) {


        //   strPurchaseOrderId = purchaseOrderData.getPurorid();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");

        //Invoice Date
        if (purchaseOrderData.getInvoiceDate() != null && !purchaseOrderData.getInvoiceDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseOrderData.getInvoiceDate());
                invoiceDateValueTextView.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

      /*  //Authorized By
        if (purchaseOrderData.getAuthorizedBy() != null && !purchaseOrderData.getAuthorizedBy().isEmpty()) {
            authorizedByLayout.setVisibility(View.VISIBLE);
            authorizedByTextView.setText(purchaseOrderData.getAuthorizedBy());
        } else {
            authorizedByLayout.setVisibility(View.GONE);
        }*/


        //Receive Date
        if (purchaseOrderData.getReceiveDate() != null && !purchaseOrderData.getReceiveDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseOrderData.getReceiveDate());
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                receiveDateValueTextView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
   /*     //PO ID
        if (purchaseOrderData.getPoid() != null && !purchaseOrderData.getPoid().isEmpty()) {
            poIdLayout.setVisibility(View.VISIBLE);
            poIdTextView.setText(purchaseOrderData.getPoid());
        } else {
            poIdLayout.setVisibility(View.GONE);
        }

        //PO Date
        if (purchaseOrderData.getPoDate() != null && !purchaseOrderData.getPoDate().isEmpty()) {
            poIdDateLayout.setVisibility(View.VISIBLE);
            try {
                poIdDateTextView.setText(formatter.format(targetFormat.parse(purchaseOrderData.getPoDate())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            poIdDateLayout.setVisibility(View.GONE);
        }
*/
        //Vendor
     /*   if (purchaseOrderData.getVendorName() != null && !purchaseOrderData.getVendorName().isEmpty()) {
            vendorNameLayout.setVisibility(View.VISIBLE);
            vendorNameTextView.setText(purchaseOrderData.getVendorName());
        } else {
            vendorNameLayout.setVisibility(View.GONE);
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
*/
        //Vendor Name
        if (purchaseOrderData.getName() != null && !purchaseOrderData.getName().isEmpty()) {
            vendorValueEditText.setText(purchaseOrderData.getName());
            vendorId = purchaseOrderData.getVenid();
        }

     /*   //Transporter
        if (transportationData.getVendorName() != null && !transportationData.getVendorName().isEmpty()) {
            transporterValueEditText.setText(transportationData.getVendorName());
            transporterId = transportationData.getVendorId();
        }

        //Weight
        if (transportationData.getWeight() != null && !transportationData.getWeight().isEmpty()) {
            DecimalFormat quantityFormat = new DecimalFormat("######");
            weightValueEditText.setText(String.valueOf(quantityFormat.format(Double.valueOf(transportationData.getWeight()))));
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
            try {
                Date startDate = targetFormat.parse(transportationData.getExpectedDate());
                expectedDateValueTextView.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/

       /* //Check/Uncheck Checkbox
        if (isExistTransportationDetails != null && isExistTransportationDetails.equals("1")) {
            addTransportationCheckBoxTextView.setChecked(true);
        } else {
            addTransportationCheckBoxTextView.setChecked(false);
        }

        //Receive Date
        if (purchaseDetails.getReceiveDate() != null && !purchaseDetails.getReceiveDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseDetails.getReceiveDate());
                Calendar c = Calendar.getInstance();
                System.out.println("Current time => " + c.getTime());
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                String formattedDate = df.format(c.getTime());
                receiveDateValueTextView.setText(formattedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        //Invoice Date
        if (purchaseDetails.getInvoiceDate() != null && !purchaseDetails.getInvoiceDate().isEmpty()) {
            try {
                Date startDate = targetFormat.parse(purchaseDetails.getInvoiceDate());
                invoiceDateValueTextView.setText(formatter.format(startDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            transportationLayout.setVisibility(View.VISIBLE);
            invoiceNumberValueTextView.clearFocus();
        } else {
            invoiceNumberValueTextView.clearFocus();
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

        vendorSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
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
                                // transporterValueEditText.setSelection(transporterValueEditText.getText().toString().length());
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
            } else if (datePickerFragment.getTag().equals("expiryDate")) {
                expiryDateValueTextView.setText(s);
            } else if (datePickerFragment.getTag().equals("manufactureDate")) {
                manufactureDateValueTextView.setText(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void passTime(String s) {

    }

    @Override
    public void editItemAndOpenDialog(int position, ConsignmentItem consignmentItem) {
        openDialogAddEditItems(this, "edit", position, consignmentItem);
    }

    @Override
    public void removeItemAndNotify(int position) {
        if (consignmentItemList != null && consignmentItemList.size() > 0) {
            if (consignmentItemList.size() == 1) {
                Toast.makeText(this, "Please add atleast one item first.", Toast.LENGTH_SHORT).show();
            } else {
                consignmentItemList.remove(position);
                receiveConsignmentItemsAdapter.notifyDataSetChanged();
            }
        }
    }

    //Open Dialog to select Items
    public void openItemSearchDialog() {
        dialog = new Dialog(POReceiveConsignmentActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        itemSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
        itemsListRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(POReceiveConsignmentActivity.this);
        itemsVariationAdapter = new ItemsVariationAdapter(POReceiveConsignmentActivity.this,
                itemListArrayList, this);

        itemsListRecyclerView.setLayoutManager(mLayoutManager);
        itemsListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsListRecyclerView.setAdapter(itemsVariationAdapter);
        itemsListRecyclerView.setNestedScrollingEnabled(false);

        itemSearchAutoCompleteTextView.setHint("Search Product");

        itemSearchAutoCompleteTextView.setThreshold(1);
        itemSearchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (itemSearchAutoCompleteTextView.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchItems(s.toString().trim());
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

    //Searching Product from API after getting input from openItemSearchDialog
    public void searchItems(String str) {
        task = getString(R.string.quotation_search_item);
        if (AppPreferences.getIsLogin(POReceiveConsignmentActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(POReceiveConsignmentActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(POReceiveConsignmentActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(POReceiveConsignmentActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.searchItem(version, key, task, userId, accessToken, str);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (itemListArrayList != null && itemListArrayList.size() > 0) {
                            itemListArrayList.clear();
                        }
                        for (final ItemList itemList : apiResponse.getData().getItemList()) {
                            if (itemList != null) {
                                itemListArrayList.add(itemList);
                            }
                        }
                        itemsVariationAdapter.notifyDataSetChanged();
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

    public void openDialogAddEditItems(Activity activity, final String operationType, final int positionToEdit,
                                       final ConsignmentItem consignmentItem) {
        productsDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        productsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productsDialog.setContentView(R.layout.dialog_add_item);
        Window window = productsDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        try {
            final ArrayList<Measurements> measurementArrayList = new ArrayList<>();
            final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
            final TextView titleTextView = (TextView) productsDialog.findViewById(R.id.title_textView);
            final EditText productNameEdittext = (EditText) productsDialog.findViewById(R.id.product_name_edittext);
            final EditText skuCodeEdittext = (EditText) productsDialog.findViewById(R.id.skuCode_edittext);
            final TextView orderQuantityEdittext = (TextView) productsDialog.findViewById(R.id.orderQuantity_edittext);
            final EditText receiveQuantityEdittext = (EditText) productsDialog.findViewById(R.id.receiveQuantity_edittext);
            //   final TextView unitTitleTextView = (TextView) productsDialog.findViewById(R.id.unit_title_textView);
            final Spinner unitsTypeSpinner = (Spinner) productsDialog.findViewById(R.id.units_type_spinner);
            final ImageView balanceTypeImageView = (ImageView) productsDialog.findViewById(R.id.balance_type_imageView);
            final EditText priceEdittext = (EditText) productsDialog.findViewById(R.id.price_edittext);
            final EditText commentEdittext = (EditText) productsDialog.findViewById(R.id.comment_edittext);
            final TextView expiryDateTitleTextView = (TextView) productsDialog.findViewById(R.id.expiry_date_title_textView);
            final TextView manufactureDateTitleTextView = (TextView) productsDialog.findViewById(R.id.manufacturing_date_title_textView);
            expiryDateValueTextView = (TextView) productsDialog.findViewById(R.id.expiry_date_value_textView);
            manufactureDateValueTextView = (TextView) productsDialog.findViewById(R.id.manufacturing_date_value_textView);
            barcodeEdittext = (TextView) productsDialog.findViewById(R.id.barcode_edittext);
            final EditText reasonRejectionEdittext = (EditText) productsDialog.findViewById(R.id.reasonRejection_edittext);
            final EditText rejectedQuantityEdittext = (EditText) productsDialog.findViewById(R.id.rejectedQuantity_edittext);
            final TextView textViewAdd = (TextView) productsDialog.findViewById(R.id.textView_add);
            final TextView textViewBack = (TextView) productsDialog.findViewById(R.id.textView_back);
            final EditText editTextHSNCode = (EditText) productsDialog.findViewById(R.id.hsn_edittext);
            DecimalFormat df2 = new DecimalFormat(".###");
            df2.setRoundingMode(RoundingMode.UP);
            String value = "";
            if (consignmentItem != null && consignmentItem.getReceiveQuantity() != null && !consignmentItem.getReceiveQuantity().isEmpty()) {
                value = df2.format(Double.valueOf(consignmentItem.getReceiveQuantity()));
            }


            if (itemLabels != null) {
                if (itemLabels.getItemVariation() != null &&
                        !itemLabels.getItemVariation()) {
                    productNameEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getHsnCode() != null &&
                        !itemLabels.getHsnCode()) {
                    editTextHSNCode.setVisibility(View.GONE);
                } else if (itemLabels.getSkuCode() != null &&
                        !itemLabels.getSkuCode()) {
                    skuCodeEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getOrderQuantity() != null &&
                        !itemLabels.getOrderQuantity()) {
                    orderQuantityEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getReceivingQuantity() != null &&
                        !itemLabels.getReceivingQuantity()) {
                    receiveQuantityEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getUnit() != null &&
                        !itemLabels.getUnit()) {
                    unitsTypeSpinner.setVisibility(View.GONE);
                } else if (itemLabels.getPrice() != null &&
                        !itemLabels.getPrice()) {
                    priceEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getComment() != null &&
                        !itemLabels.getComment()) {
                    commentEdittext.setVisibility(View.GONE);
                } else if (itemLabels.getBoxQty() != null &&
                        !itemLabels.getBoxQty()) {

                } else if (itemLabels.getManufacturingDate() != null &&
                        !itemLabels.getManufacturingDate()) {
                    manufactureDateValueTextView.setVisibility(View.GONE);
                    manufactureDateTitleTextView.setVisibility(View.GONE);

                } else if (itemLabels.getExpiryDate() != null &&
                        !itemLabels.getExpiryDate()) {
                    expiryDateValueTextView.setVisibility(View.GONE);
                    expiryDateTitleTextView.setVisibility(View.GONE);
                } else if (itemLabels.getBarcodeTitle() != null &&
                        !itemLabels.getBarcodeTitle()) {
                    barcodeEdittext.setVisibility(View.GONE);

                } else if (itemLabels.getReasonForRejection() != null &&
                        !itemLabels.getReasonForRejection()) {
                    reasonRejectionEdittext.setVisibility(View.GONE);

                } else if (itemLabels.getRejectedQuantity() != null &&
                        !itemLabels.getRejectedQuantity()) {
                    rejectedQuantityEdittext.setVisibility(View.GONE);
                }
            }

            productNameEdittext.setText(consignmentItem.getName());
            skuCodeEdittext.setText(consignmentItem.getInternalCode());
            orderQuantityEdittext.setText(consignmentItem.getOrderQuantity());
            editTextHSNCode.setText(consignmentItem.getHsnCode());
            receiveQuantityEdittext.setText(consignmentItem.getReceiveQuantity());
            barcodeEdittext.setHint(itemLabels.getBarcodeTitleName());

            if (editTextHSNCode.getText().toString() != null && !editTextHSNCode.getText().toString().isEmpty()) {
                editTextHSNCode.setEnabled(false);
            } else {
                editTextHSNCode.setEnabled(true);
            }

            if (operationType.equals("edit")) {
                receiveQuantityEdittext.setText(value);
                commentEdittext.setText(consignmentItem.getComment());
                reasonRejectionEdittext.setText(consignmentItem.getReasonRejection());
                expiryDateValueTextView.setText(consignmentItem.getExpiryDate());
                rejectedQuantityEdittext.setText(consignmentItem.getRejectedQuantity());

                // unitsTypeSpinner.setSelection(consignmentItem.getUnit());
            }

            if (consignmentItem.getMeasurements().size() > 0) {
                if (measurementArrayList != null && measurementArrayList.size() > 0) {
                    measurementArrayList.clear();
                }
                measurementArrayList.addAll(consignmentItem.getMeasurements());
            } else {
                Measurements measurement = new Measurements();
                measurement.setName(consignmentItem.getMeasurementUnit());
                measurement.setId("");
                measurementArrayList.add(measurement);
            }

            ArrayAdapter<Measurements> measurementArrayAdapter =
                    new ArrayAdapter<Measurements>(activity, R.layout.simple_spinner_item, measurementArrayList);
            measurementArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            unitsTypeSpinner.setAdapter(measurementArrayAdapter);

            expiryDateValueTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getSupportFragmentManager(), "expiryDate");
                }
            });

            manufactureDateValueTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getSupportFragmentManager(), "manufactureDate");
                }
            });


            //Add Item
            textViewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (receiveQuantityEdittext.getText().toString() == null || receiveQuantityEdittext.getText().toString().isEmpty()) {
                        Toast.makeText(POReceiveConsignmentActivity.this, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        consignmentItem.setPrice(priceEdittext.getText().toString().trim());
                        consignmentItem.setComment(commentEdittext.getText().toString().trim());
                        consignmentItem.setExpiryDate(expiryDateValueTextView.getText().toString().trim());
                        consignmentItem.setManufacturingDate(manufactureDateValueTextView.getText().toString().trim());
                        consignmentItem.setRejectedQuantity(rejectedQuantityEdittext.getText().toString().trim());
                        consignmentItem.setReasonRejection(reasonRejectionEdittext.getText().toString().trim());
                        consignmentItem.setMeasurementUnit(measurementArrayList.get(unitsTypeSpinner.getSelectedItemPosition()).getName());
                        consignmentItem.setUnitId(measurementArrayList.get(unitsTypeSpinner.getSelectedItemPosition()).getId());
                        consignmentItem.setReceiveQuantity(receiveQuantityEdittext.getText().toString().trim());
                        if (operationType.equals("edit")) {
                            consignmentItemList.set(positionToEdit, consignmentItem);
                        } else {
                            consignmentItemList.add(consignmentItem);
                        }

                        receiveConsignmentItemsAdapter.notifyDataSetChanged();
                        productsDialog.dismiss();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }

                }
            });

            //Back to selection
            textViewBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    productsDialog.dismiss();
                }
            });

            productNameEdittext.setEnabled(false);
            productNameEdittext.setFocusable(false);

            //Check Item is going to edit or add
            //Check Item is going to edit or

            if (operationType.equals("add")) {
                titleTextView.setText("Add Product");
            } else {
                titleTextView.setText("Edit Product");
                textViewAdd.setText("Edit item");
            }


        } catch (NumberFormatException ex) { // handle your exception
            ex.printStackTrace();
        }


        //If SourceType is edit

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_BLUR_BEHIND;
        window.setAttributes(wlp);
        productsDialog.getWindow().setLayout(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        productsDialog.show();
    }

    @Override
    public void onProductClick(int position) {
        ItemList selected = itemListArrayList.get(position);
        status = NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            searchedProductDetails(selected.getId(), "API");
        } else {
            Toast.makeText(POReceiveConsignmentActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    //Get Details of clicked items after selecting from a dialog
    public void searchedProductDetails(String id, final String sourceType) {
        task = getString(R.string.task_products_details);
        if (AppPreferences.getIsLogin(POReceiveConsignmentActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(POReceiveConsignmentActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(POReceiveConsignmentActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(POReceiveConsignmentActivity.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getSearchedProductsDetails(version, key, task, userId, accessToken, id);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        ConsignmentItem consignmentItem = apiResponse.getData().getConsignmentItem();
                        openDialogAddEditItems(POReceiveConsignmentActivity.this, "add", 0, consignmentItem);

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

    @Override
    public void onAuthorizedUserClick(int position, String userType) {

        User selected = userArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            authorizedByValueEditText.setText(selected.getName().toString().trim());
            //Get Authorized User's ID
            strAuthorizedById = selected.getId();
        }

        if (authorizedByDialog != null && authorizedByDialog.isShowing()) {
            authorizedByDialog.dismiss();
        }

    }

    //Open Dialog to select Authorized By
    public void openAuthorizedBySearchDialog(final String userType) {
        authorizedByDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        authorizedByDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        authorizedByDialog.setContentView(R.layout.dialog_search_vendor);
        Window window = authorizedByDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        authorizedBySearchAutoCompleteTextView = (AutoCompleteTextView) authorizedByDialog.findViewById(R.id.search_autoCompleteTextView);
        authorizedByRecyclerView = (RecyclerView) authorizedByDialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        authorizedByUserAdapter = new AuthorizedByUserAdapter(this,
                userArrayList, this, userType);

        authorizedByRecyclerView.setLayoutManager(mLayoutManager);
        authorizedByRecyclerView.setItemAnimator(new DefaultItemAnimator());
        authorizedByRecyclerView.setAdapter(authorizedByUserAdapter);
        authorizedByRecyclerView.setNestedScrollingEnabled(false);

        authorizedBySearchAutoCompleteTextView.setHint("Authorized by");
        authorizedBySearchAutoCompleteTextView.setThreshold(1);
        authorizedBySearchAutoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (authorizedBySearchAutoCompleteTextView.isPerformingCompletion()) {

                } else {
                    status = NetworkUtil.getConnectivityStatusString(POReceiveConsignmentActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchAuthorizedUser(s.toString().trim(), userType);
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
        authorizedByDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        authorizedByDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        authorizedByDialog.show();
    }

    //Refreshing data after getting input from openAuthorizedBySearchDialog
    private void refreshAuthorizedRecyclerView(final String userType) {
        authorizedByUserAdapter.notifyDataSetChanged();
        authorizedBySearchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                User selected = (User) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    authorizedByValueEditText.setText(selected.getName().toString().trim());

                    //Get Authorized By User Id
                    strAuthorizedById = selected.getId();
                }
                if (authorizedByDialog != null && authorizedByDialog.isShowing()) {
                    authorizedByDialog.dismiss();
                }
            }
        });

    }

    //Searching Authorized By User from API after getting input from openAuthorizedBySearchDialog
    public void searchAuthorizedUser(String str, final String userType) {
        task = getString(R.string.authorized_user_search_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.searchAuthorizedByUser(version, key, task, userId, accessToken, str);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (userArrayList != null && userArrayList.size() > 0) {
                            userArrayList.clear();
                        }
                        for (final User user : apiResponse.getData().getUsers()) {
                            if (user != null) {
                                userArrayList.add(user);
                            }
                        }
                        refreshAuthorizedRecyclerView(userType);

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

    //AsyncTask to Receive Items
    public class ReceiveItemsAsyncTask extends AsyncTask<Void, Void, String> {
        private Activity context;

        public ReceiveItemsAsyncTask(Activity context) {
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
                jsonObject.put("invoice", strInvoiceNumber);
                jsonObject.put("vendor", vendorId);

                if (flagToShow.equals("stockRequest")) {
                    jsonObject.put("request", purOrId);
                    jsonObject.put("user", strAuthorizedById);
                } else {
                    jsonObject.put("purorid", strPurchaseOrderId);
                }

                jsonObject.put("purchase_date", strPurchaseDate);
                jsonObject.put("chkid", strChkId);
                jsonObject.put("vendor-transportation-check_new", strTransportationCheckBoxValue);
                jsonObject.put("venid", transporterId + "");
                jsonObject.put("weight", strWeight + "");
                jsonObject.put("expected_date", strExpectedDate + "");
                jsonObject.put("invoice-date", strInvoiceDate + "");
                jsonObject.put("isctrandid", "");
                jsonObject.put("lr_number", strLRNumber + "");
                jsonObject.put("vehicle_number", strVehicleNumber + "");

                //TODO Products Items
                JSONArray productItemJsonArray = new JSONArray();
                for (int i = 0; i < consignmentItemList.size(); i++) {
                    JSONObject productsItemJsonObject = new JSONObject();
                    if (consignmentItemList.get(i).getRejectedQuantity() != null &&
                            !consignmentItemList.get(i).getRejectedQuantity().isEmpty()) {
                        productsItemJsonObject.put("rejected-qty", consignmentItemList.get(i).getRejectedQuantity());
                    } else {
                        productsItemJsonObject.put("rejected-qty", "0");
                    }

                    if (consignmentItemList.get(i).getHsnCode() != null &&
                            !consignmentItemList.get(i).getHsnCode().isEmpty()) {
                        productsItemJsonObject.put("item-hsn-code", consignmentItemList.get(i).getHsnCode());
                    } else {
                        productsItemJsonObject.put("item-hsn-code", "0");
                    }

                    if (consignmentItemList.get(i).getOrderQuantity() != null &&
                            !consignmentItemList.get(i).getOrderQuantity().isEmpty()) {
                        productsItemJsonObject.put("item-qty-origin", consignmentItemList.get(i).getOrderQuantity());
                    } else {
                        productsItemJsonObject.put("item-qty-origin", "0");
                    }


                    if (consignmentItemList.get(i).getOiid() != null &&
                            !consignmentItemList.get(i).getOiid().isEmpty()) {
                        productsItemJsonObject.put("oiid", consignmentItemList.get(i).getOiid());
                    } else {
                        productsItemJsonObject.put("oiid", "0");
                    }

                    if (consignmentItemList.get(i).getManufacturingDate() != null &&
                            !consignmentItemList.get(i).getManufacturingDate().isEmpty()) {
                        productsItemJsonObject.put("manufacturing-date", consignmentItemList.get(i).getManufacturingDate());
                    } else {
                        productsItemJsonObject.put("manufacturing-date", "0");
                    }

                    if (consignmentItemList.get(i).getBarcode() != null &&
                            !consignmentItemList.get(i).getBarcode().isEmpty()) {
                        productsItemJsonObject.put("barcode", consignmentItemList.get(i).getBarcode());
                    } else {
                        productsItemJsonObject.put("barcode", "0");
                    }

                    if (consignmentItemList.get(i).getReceiveQuantity() != null &&
                            !consignmentItemList.get(i).getReceiveQuantity().isEmpty()) {
                        productsItemJsonObject.put("item-qty", consignmentItemList.get(i).getReceiveQuantity());
                    } else {
                        productsItemJsonObject.put("item-qty", "0");
                    }

                    if (consignmentItemList.get(i).getUnitId() != null &&
                            !consignmentItemList.get(i).getUnitId().isEmpty()) {
                        productsItemJsonObject.put("measurement", consignmentItemList.get(i).getUnitId());
                    } else {
                        productsItemJsonObject.put("measurement", "0");
                    }

                    if (consignmentItemList.get(i).getUnitPrice() != null &&
                            !consignmentItemList.get(i).getUnitPrice().isEmpty()) {
                        productsItemJsonObject.put("item-mrp", consignmentItemList.get(i).getUnitPrice());
                    } else {
                        productsItemJsonObject.put("item-mrp", "0");
                    }

                    if (consignmentItemList.get(i).getIsvid() != null &&
                            !consignmentItemList.get(i).getIsvid().isEmpty()) {
                        productsItemJsonObject.put("variations", consignmentItemList.get(i).getIsvid());
                    } else {
                        productsItemJsonObject.put("variations", "0");
                    }

                    if (consignmentItemList.get(i).getComment() != null &&
                            !consignmentItemList.get(i).getComment().isEmpty()) {
                        productsItemJsonObject.put("item-comment", consignmentItemList.get(i).getComment());
                    } else {
                        productsItemJsonObject.put("item-comment", "");
                    }

                    if (consignmentItemList.get(i).getReasonRejection() != null &&
                            !consignmentItemList.get(i).getReasonRejection().isEmpty()) {
                        productsItemJsonObject.put("rejected-reason", consignmentItemList.get(i).getReasonRejection());
                    } else {
                        productsItemJsonObject.put("rejected-reason", "");
                    }

                    if (consignmentItemList.get(i).getExpiryDate() != null &&
                            !consignmentItemList.get(i).getExpiryDate().isEmpty()) {
                        try {
                            productsItemJsonObject.put("expiry-date",
                                    targetFormat.format(dateFormatter.parse(consignmentItemList.get(i).getExpiryDate().toString().trim())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        productsItemJsonObject.put("expiry-date", "");
                    }

                    productItemJsonArray.put(productsItemJsonObject);
                }

               /* JSONArray productsJsonArray = new JSONArray();
                JSONObject productsJsonObject = new JSONObject();
                productsJsonObject.put("trooid", "1");
                productsJsonObject.put("items", productItemJsonArray);

                productsJsonArray.put(productsJsonObject);*/

                //Array
                jsonObject.put("received-item-grp", productItemJsonArray);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            String postParams = "data=" + jsonObject.toString();
            Log.d("POST_UPDATE", postParams);

            URL obj = null;
            HttpURLConnection con = null;
            String task;

            if (flagToShow.equals("stockRequest")) {
                task = context.getString(R.string.task_stock_submit_consignment);
            } else {
                task = context.getString(R.string.task_receive_consignment);
            }

            try {
                obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=1.0&key=123&task=" + task
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

                Log.d("POST_URL", obj.toString());
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
                    Log.e("REQUEST", response.toString());
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
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    }

}
