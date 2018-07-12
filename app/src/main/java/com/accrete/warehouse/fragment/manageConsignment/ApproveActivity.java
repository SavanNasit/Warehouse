package com.accrete.warehouse.fragment.manageConsignment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
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
import com.accrete.warehouse.model.ItemList;
import com.accrete.warehouse.model.Measurements;
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

/**
 * Created by poonam on 7/5/18.
 */

public class ApproveActivity extends AppCompatActivity implements View.OnClickListener, PassDateToCounsellor, CompoundButton.OnCheckedChangeListener, ReceiveConsignmentItemsAdapter.ReceiveConsignmentItemsAdapterListener, VendorsAdapter.VendorsAdapterListener, AuthorizedByUserAdapter.AuthorizedByUserAdapterListener, ItemsVariationAdapter.ItemsVariationAdapterListener {
    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private String chkId;
    private String iscId;
    private Toolbar toolbar;
    private NestedScrollView nestedScrollView;
    private TextView editConsignmentTitle;
    private View editConsignmentView;
    private LinearLayout editConsignmentLayoutContainerId;
    private TextView editConsignmentTextContainerId;
    private LinearLayout editConsignmentLayoutDate;
    private TextView editConsignmentTextDate;
    private LinearLayout editConsignmentLayoutVendor;
    private TextView editConsignmentTextVendor;
    private LinearLayout editConsignmentLayoutAuthorizedBy;
    private TextView editConsignmentTextAuthorizedBy;
    private LinearLayout editConsignmentLayoutStatus;
    private TextView editConsignmentTextStatus;
    private TextView editConsignmentLayoutTitleVendor;
    private TextView editConsignmentSubTitleVendor;
    private View editConsignmentVendorView;
    private TextView editConsignmentTitleVendor;
    private EditText editConsignmentEdittextVendor;
    private ImageButton editConsignmentImgBtnClear;
    private TextView editConsignmentTitlePurchaseDetails;
    private TextView editConsignmentSubTitlePurchaseDetails;
    private View editConsignmentViewPurchaseDetails;
    private TextView editConsignmentTextTitleReceiveDate;
    private TextView editConsignmentTextReceiveDate;
    private TextView editConsignmentTextTitleInvoiceNumber;
    private EditText editConsignmentTextInvoiceNumber;
    private TextView editConsignmentTextTitleInvoiceDate;
    private TextView editConsignmentTextInvoiceDate;
    private TextView editConsignmentLayoutTitleConsignment;
    private TextView editConsignmentLayoutSubTitleConsignment;
    private View editConsignmentViewConsignment;
    private EditText editConsignmentEditTextSearchItemConsignment;
    private RecyclerView editConsignmentItemRecyclerview;
    private CheckBox editConsignmentCheckboxTransportation;
    private LinearLayout editConsignmentLayoutTransportation;
    private TextView editConsignmentTitleTransportation;
    private TextView editConsignmentSubTitleTransportation;
    private View editConsignmentViewTransportation;
    private TextView editConsignmentTitleTransporter;
    private TextView editConsignmentTextTransporter;
    private ImageButton editConsignmentImgBtnClearTransporter;
    private EditText editConsignmentEdittextWeight;
    private EditText editConsignmentEdittextLrNumber;
    private EditText editConsignmentEdittextVehicleNumber;
    private TextView editConsignmentTitleExpectedDate;
    private TextView editConsignmentEdittextExpectedDate;
    private TextView editConsignmentSubmit;
    private TextView editConsignmentTitleAuthorizedBy;
    private TextView editConsignmentEdittextAuthorizedBy;
    private ImageButton imageButtonClearAuthorizedBy;
    private List<ConsignmentItem> consignmentItemList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private ReceiveConsignmentItemsAdapter consignmentItemsAdapter;
    private AllDatePickerFragment datePickerFragment;
    private Dialog dialog, productsDialog, authorizedByDialog;
    private ArrayList<Vendor> vendorArrayList = new ArrayList<>();
    private ArrayList<User> userArrayList = new ArrayList<>();
    private String strDate;
    private AutoCompleteTextView vendorSearchAutoCompleteTextView, itemSearchAutoCompleteTextView, authorizedBySearchAutoCompleteTextView;
    private RecyclerView vendorsRecyclerView, itemsRecyclerView, itemsListRecyclerView, authorizedByRecyclerView;
    private VendorsAdapter vendorsAdapter;
    private String strInvoiceNumber, strAuthorizedById, strPurchaseOrderId, strChkId, strWeight, strExpectedDate, strPurchaseDate,
            strInvoiceDate, strTransporatationCheckBoxValue, strLRNumber, strVehicleNumber;
    private ItemsVariationAdapter itemsVariationAdapter;
    private AuthorizedByUserAdapter authorizedByUserAdapter;
    private TextView weightTitleTextView;
    private String flagForInvoiceNumber;
    private String purOrId, status, vendorId, transporterId;
    private ArrayList<ItemList> itemListArrayList = new ArrayList<>();
    private TextView expiryDateValueTextView;
    private TextView editConsignmentTitleWeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_consignment);
        chkId = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
        if (getIntent() != null && getIntent().hasExtra("iscid")) {
            iscId = getIntent().getStringExtra("iscid");
        }

        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.approve_consignment));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });

        nestedScrollView = (NestedScrollView) findViewById(R.id.nested_scrollView);
        editConsignmentTitle = (TextView) findViewById(R.id.edit_consignment_title);
        editConsignmentView = (View) findViewById(R.id.edit_consignment_view);
        editConsignmentLayoutContainerId = (LinearLayout) findViewById(R.id.edit_consignment_layout_container_id);
        editConsignmentTextContainerId = (TextView) findViewById(R.id.edit_consignment_text_container_id);
        editConsignmentLayoutDate = (LinearLayout) findViewById(R.id.edit_consignment_layout_date);
        editConsignmentTextDate = (TextView) findViewById(R.id.edit_consignment_text_date);
        editConsignmentLayoutVendor = (LinearLayout) findViewById(R.id.edit_consignment_layout_vendor);
        editConsignmentTextVendor = (TextView) findViewById(R.id.edit_consignment_text_vendor);
        editConsignmentLayoutAuthorizedBy = (LinearLayout) findViewById(R.id.edit_consignment_layout_authorized_by);
        editConsignmentTextAuthorizedBy = (TextView) findViewById(R.id.edit_consignment_text_authorized_by);
        editConsignmentLayoutStatus = (LinearLayout) findViewById(R.id.edit_consignment_layout_status);
        editConsignmentTextStatus = (TextView) findViewById(R.id.edit_consignment_text_status);
        editConsignmentLayoutTitleVendor = (TextView) findViewById(R.id.edit_consignment_layout_title_vendor);
        editConsignmentSubTitleVendor = (TextView) findViewById(R.id.edit_consignment_sub_title_vendor);
        editConsignmentVendorView = (View) findViewById(R.id.edit_consignment_vendor_view);
        editConsignmentTitleVendor = (TextView) findViewById(R.id.edit_consignment_title_vendor);
        editConsignmentEdittextVendor = (EditText) findViewById(R.id.edit_consignment_edittext_vendor);
        editConsignmentImgBtnClear = (ImageButton) findViewById(R.id.edit_consignment_img_btn_clear);
        editConsignmentTitlePurchaseDetails = (TextView) findViewById(R.id.edit_consignment_title_purchase_details);
        editConsignmentSubTitlePurchaseDetails = (TextView) findViewById(R.id.edit_consignment_sub_title_purchase_details);
        editConsignmentViewPurchaseDetails = (View) findViewById(R.id.edit_consignment_view_purchase_details);
        editConsignmentTextTitleReceiveDate = (TextView) findViewById(R.id.edit_consignment_text_title_receive_date);
        editConsignmentTextReceiveDate = (TextView) findViewById(R.id.edit_consignment_text_receive_date);
        editConsignmentTextTitleInvoiceNumber = (TextView) findViewById(R.id.edit_consignment_text_title_invoice_number);
        editConsignmentTextInvoiceNumber = (EditText) findViewById(R.id.edit_consignment_text_invoice_number);
        editConsignmentTextTitleInvoiceDate = (TextView) findViewById(R.id.edit_consignment_text_title_invoice_date);
        editConsignmentTextInvoiceDate = (TextView) findViewById(R.id.edit_consignment_text_invoice_date);
        editConsignmentLayoutTitleConsignment = (TextView) findViewById(R.id.edit_consignment_layout_title_consignment);
        editConsignmentLayoutSubTitleConsignment = (TextView) findViewById(R.id.edit_consignment_layout_sub_title_consignment);
        editConsignmentViewConsignment = (View) findViewById(R.id.edit_consignment_view_consignment);
        editConsignmentEditTextSearchItemConsignment = (EditText) findViewById(R.id.edit_consignment_edit_text_search_item_consignment);
        editConsignmentItemRecyclerview = (RecyclerView) findViewById(R.id.edit_consignment_item_recyclerview);
        editConsignmentCheckboxTransportation = (CheckBox) findViewById(R.id.edit_consignment_checkbox_transportation);
        editConsignmentLayoutTransportation = (LinearLayout) findViewById(R.id.edit_consignment_layout_transportation);
        editConsignmentTitleTransportation = (TextView) findViewById(R.id.edit_consignment_title_transportation);
        editConsignmentSubTitleTransportation = (TextView) findViewById(R.id.edit_consignment_sub_title_transportation);
        editConsignmentViewTransportation = (View) findViewById(R.id.edit_consignment_view_transportation);
        editConsignmentTitleTransporter = (TextView) findViewById(R.id.edit_consignment_title_transporter);
        editConsignmentTextTransporter = (TextView) findViewById(R.id.edit_consignment_text_transporter);
        editConsignmentImgBtnClearTransporter = (ImageButton) findViewById(R.id.edit_consignment_img_btn_clear_transporter);
        editConsignmentEdittextWeight = (EditText) findViewById(R.id.edit_consignment_edittext_weight);
        editConsignmentEdittextLrNumber = (EditText) findViewById(R.id.edit_consignment_edittext_lr_number);
        editConsignmentEdittextVehicleNumber = (EditText) findViewById(R.id.edit_consignment_edittext_vehicle_number);
        editConsignmentTitleExpectedDate = (TextView) findViewById(R.id.edit_consignment_title_expected_date);
        editConsignmentEdittextExpectedDate = (TextView) findViewById(R.id.edit_consignment_edittext_expected_date);
        editConsignmentSubmit = (TextView) findViewById(R.id.edit_consignment_submit);
        editConsignmentTitleAuthorizedBy = (TextView) findViewById(R.id.edit_consignment_title_authorized_by);
        editConsignmentEdittextAuthorizedBy = (TextView) findViewById(R.id.edit_consignment_edit_text_authorized_by);
        imageButtonClearAuthorizedBy = (ImageButton) findViewById(R.id.edit_consignment_img_btn_clear_authorized_by);
        editConsignmentTitleWeight = (TextView) findViewById(R.id.weight_title_textView);


        //Items RecyclerView
        consignmentItemsAdapter = new ReceiveConsignmentItemsAdapter(this, consignmentItemList,
                this, "Edit");
        mLayoutManager = new LinearLayoutManager(this);
        editConsignmentItemRecyclerview.setLayoutManager(mLayoutManager);
        editConsignmentItemRecyclerview.setHasFixedSize(true);
        editConsignmentItemRecyclerview.setItemAnimator(new DefaultItemAnimator());
        editConsignmentItemRecyclerview.setNestedScrollingEnabled(false);
        editConsignmentItemRecyclerview.setAdapter(consignmentItemsAdapter);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Mandatory Fields
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        editConsignmentTextReceiveDate.setText(TextUtils.concat(getString(R.string.receive_date_title), spannableStringBuilder));
        editConsignmentTextTitleInvoiceNumber.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
        editConsignmentTextInvoiceDate.setText(TextUtils.concat(getString(R.string.invoice_date_title), spannableStringBuilder));
        editConsignmentLayoutTitleVendor.setText(TextUtils.concat(getString(R.string.vendor_title), spannableStringBuilder));
        editConsignmentTitleTransportation.setText(TextUtils.concat(getString(R.string.transporter_title), spannableStringBuilder));
        editConsignmentTitleWeight.setText(TextUtils.concat(getString(R.string.weight), spannableStringBuilder));
        editConsignmentTitleExpectedDate.setText(TextUtils.concat(getString(R.string.expected_date_title), spannableStringBuilder));
        editConsignmentImgBtnClear.setOnClickListener(this);
        editConsignmentImgBtnClearTransporter.setOnClickListener(this);
        imageButtonClearAuthorizedBy.setOnClickListener(this);
        editConsignmentCheckboxTransportation.setOnCheckedChangeListener(this);
        editConsignmentEdittextVendor.setOnClickListener(this);
        editConsignmentEdittextAuthorizedBy.setOnClickListener(this);
        editConsignmentTextTransporter.setOnClickListener(this);
        editConsignmentTextReceiveDate.setOnClickListener(this);
        editConsignmentTextInvoiceDate.setOnClickListener(this);
        editConsignmentEdittextExpectedDate.setOnClickListener(this);
        editConsignmentEditTextSearchItemConsignment.setOnClickListener(this);
        editConsignmentSubmit.setOnClickListener(this);
        editConsignmentLayoutTransportation.setVisibility(View.GONE);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        //Receive Date
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c.getTime());
        editConsignmentTextReceiveDate.setText(formattedDate);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_consignment_img_btn_clear:
                editConsignmentEdittextVendor.setText("");
                vendorId = "";
                break;
            case R.id.edit_consignment_img_btn_clear_transporter:
                editConsignmentTextTransporter.setText("");
                transporterId = "";
                break;
            case R.id.edit_consignment_img_btn_clear_authorized_by:
                editConsignmentEdittextAuthorizedBy.setText("");
                strAuthorizedById = "";
                break;
            case R.id.edit_consignment_edittext_vendor:
                if (editConsignmentEdittextVendor.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("vendor");
                }
                break;
            case R.id.edit_consignment_edit_text_authorized_by:
                if (editConsignmentEdittextAuthorizedBy.getText().toString().trim().length() == 0) {
                    openAuthorizedBySearchDialog("authorizedBy");
                }
                break;
            case R.id.edit_consignment_text_transporter:
                if (editConsignmentTextTransporter.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("transporter");
                }
                break;
            case R.id.edit_consignment_edittext_expected_date:
                datePickerFragment.show(getSupportFragmentManager(), "expected");
                break;
            case R.id.edit_consignment_text_receive_date:
                datePickerFragment.show(getSupportFragmentManager(), "receive");
                break;
            case R.id.edit_consignment_text_invoice_date:
                datePickerFragment.show(getSupportFragmentManager(), "invoice");
                break;
            case R.id.edit_consignment_edit_text_search_item_consignment:
                openItemSearchDialog();
                break;
            case R.id.edit_consignment_submit:
                //Disable Button
                editConsignmentSubmit.setEnabled(false);
                if (getPostData()) {
                    status = NetworkUtil.getConnectivityStatusString(this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        if (editConsignmentEdittextVendor.getText().toString() == null
                                || editConsignmentEdittextVendor.getText().toString().isEmpty()) {
                            Toast.makeText(ApproveActivity.this, "Please add vendor", Toast.LENGTH_SHORT).show();
                        } else if (editConsignmentTextInvoiceNumber.getText().toString() == null
                                || editConsignmentTextInvoiceNumber.getText().toString().isEmpty() && flagForInvoiceNumber != null && flagForInvoiceNumber.equals("1")) {
                            Toast.makeText(ApproveActivity.this, "Please enter invoice number", Toast.LENGTH_SHORT).show();
                        } else if (consignmentItemList != null && consignmentItemList.size() > 0) {
                            ReceiveItemsAsyncTask receiveItemsAsyncTask =
                                    new ReceiveItemsAsyncTask(this);
                            receiveItemsAsyncTask.execute();
                        } else {
                            Toast.makeText(ApproveActivity.this, "Please add at least one item", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ApproveActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
                    }
                }

                //Enable Button
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editConsignmentSubmit.setEnabled(true);
                    }
                }, 1000);
                break;
        }
    }

    @Override
    public void onVendorClick(int position, String userType) {
        Vendor selected = vendorArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            if (userType.equals("vendor")) {
                editConsignmentEdittextVendor.setText(selected.getName().toString().trim());
                //Get Vendor's ID
                vendorId = selected.getId();
            } else {
                editConsignmentTextTransporter.setText(selected.getName().toString().trim());
                //Get Transporter's ID
                transporterId = selected.getId();
            }
        }

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    //Get data from fields and post
    public boolean getPostData() {
        if (editConsignmentTextReceiveDate.getText().toString().trim().length() == 0) {
            Toast.makeText(ApproveActivity.this, "Please select receive date.", Toast.LENGTH_SHORT).show();
            return false;
        }
       /* if (invoiceNumberValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please enter Invoice number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (invoiceDateValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(this, "Please select Invoice date.", Toast.LENGTH_SHORT).show();
            return false;
        }*/

        strInvoiceNumber = editConsignmentTextInvoiceNumber.getText().toString().trim();
        strChkId = AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID);
        strWeight = editConsignmentEdittextWeight.getText().toString().trim();

        //Expected Date
        try {
            strExpectedDate = targetFormat.format(dateFormatter.parse(editConsignmentEdittextExpectedDate.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strExpectedDate = "";
        }

        //Receive Date
        try {
            strPurchaseDate = targetFormat.format(dateFormatter.parse(editConsignmentTextReceiveDate.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strPurchaseDate = "";
        }

        //Invoice Date
        try {
            strInvoiceDate = targetFormat.format(dateFormatter.parse(editConsignmentTextTitleInvoiceDate.getText().toString().trim()));
        } catch (ParseException e) {
            e.printStackTrace();
            strInvoiceDate = "";
        }

        if (itemListArrayList == null || itemListArrayList.size() == 0) {
            Toast.makeText(this, "Please add atleast one item first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (editConsignmentCheckboxTransportation.isChecked()) {
            strTransporatationCheckBoxValue = "1";
            strLRNumber = editConsignmentEdittextLrNumber.getText().toString().trim();
            strVehicleNumber = editConsignmentEdittextVehicleNumber.getText().toString().trim();

            if (transporterId == null || transporterId.isEmpty()) {
                Toast.makeText(this, "Please select transporter first.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (editConsignmentEdittextWeight.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Please enter weight.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (editConsignmentEdittextExpectedDate.getText().toString().trim().length() == 0) {
                Toast.makeText(this, "Please select expected date.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            strTransporatationCheckBoxValue = "0";
        }
        return true;
    }

    //Open Dialog to select vendors
    public void openVendorSearchDialog(final String userType) {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        vendorSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
        vendorsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        vendorsAdapter = new VendorsAdapter(this,
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
                    status = NetworkUtil.getConnectivityStatusString(ApproveActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchVendor(s.toString().trim(), userType);
                    } else {
                        Toast.makeText(ApproveActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
                    status = NetworkUtil.getConnectivityStatusString(ApproveActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchAuthorizedUser(s.toString().trim(), userType);
                    } else {
                        Toast.makeText(ApproveActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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

    //Searching Vendor from API after getting input from openVendorSearchDialog
    public void searchVendor(String str, final String userType) {
        task = getString(R.string.vendor_search_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
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

                        flagForInvoiceNumber = apiResponse.getData().getIsInvoiceNumberEnabled();
                        //Mandatory Fields
                        String colored = " *";
                        final Spannable spannableStringBuilder = new SpannableString(colored);
                        int end = spannableStringBuilder.length();
                        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        if (flagForInvoiceNumber != null && flagForInvoiceNumber.equals("1")) {
                            editConsignmentTextTitleInvoiceNumber.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
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
                        editConsignmentEdittextVendor.setText(selected.getName().toString().trim());
                        editConsignmentEdittextVendor.post(new Runnable() {
                            @Override
                            public void run() {
                                editConsignmentEdittextVendor.setSelection(editConsignmentEdittextVendor.getText().toString().length());
                            }
                        });
                        //Get Vendor Id
                        vendorId = selected.getId();
                    } else {
                        editConsignmentTextTransporter.setText(selected.getName().toString().trim());
                        editConsignmentTextTransporter.post(new Runnable() {
                            @Override
                            public void run() {
                                // editConsignmentTextTransporter.setSelection(editConsignmentTextTransporter.getText().toString().length());
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

    //Refreshing data after getting input from openAuthorizedBySearchDialog
    private void refreshAuthorizedRecyclerView(final String userType) {
        authorizedByUserAdapter.notifyDataSetChanged();
        authorizedBySearchAutoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                User selected = (User) arg0.getAdapter().getItem(arg2);
                if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
                    editConsignmentEdittextAuthorizedBy.setText(selected.getName().toString().trim());

                    //Get Authorized By User Id
                    strAuthorizedById = selected.getId();
                }
                if (authorizedByDialog != null && authorizedByDialog.isShowing()) {
                    authorizedByDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            editConsignmentLayoutTransportation.setVisibility(View.VISIBLE);
            editConsignmentTextInvoiceNumber.clearFocus();
        } else {
            editConsignmentTextInvoiceNumber.clearFocus();
            editConsignmentLayoutTransportation.setVisibility(View.GONE);
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
                editConsignmentEdittextExpectedDate.setText(s);
            } else if (datePickerFragment.getTag().equals("receive")) {
                editConsignmentTextReceiveDate.setText(s);
            } else if (datePickerFragment.getTag().equals("invoice")) {
                editConsignmentTextInvoiceDate.setText(s);
            } else if (datePickerFragment.getTag().equals("expiryDate")) {
                expiryDateValueTextView.setText(s);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void passTime(String s) {

    }

    @Override
    public void onAuthorizedUserClick(int position, String userType) {
        User selected = userArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            editConsignmentEdittextAuthorizedBy.setText(selected.getName().toString().trim());
            //Get Authorized User's ID
            strAuthorizedById = selected.getId();
        }

        if (authorizedByDialog != null && authorizedByDialog.isShowing()) {
            authorizedByDialog.dismiss();
        }
    }

    @Override
    public void onProductClick(int position) {
        ItemList selected = itemListArrayList.get(position);
        status = NetworkUtil.getConnectivityStatusString(this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            searchedProductDetails(selected.getId(), "API");
        } else {
            Toast.makeText(this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    //Get Details of clicked items after selecting from a dialog
    public void searchedProductDetails(String id, final String sourceType) {
        task = getString(R.string.task_products_details);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
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
                        openDialogAddEditItems(ApproveActivity.this, "add", 0, consignmentItem);

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

    public void openDialogAddEditItems(final Activity activity, final String operationType, final int positionToEdit,
                                       final ConsignmentItem consignmentItem) {
        productsDialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        productsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productsDialog.setContentView(R.layout.dialog_add_item);
        Window window = productsDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
        DecimalFormat df2 = new DecimalFormat(".###");
        df2.setRoundingMode(RoundingMode.UP);
        String value = "";
        if (consignmentItem != null && consignmentItem.getReceiveQuantity() != null && !consignmentItem.getReceiveQuantity().isEmpty()) {
            value = df2.format(Double.valueOf(consignmentItem.getReceiveQuantity()));
        }
        try {
            final ArrayList<Measurements> measurementArrayList = new ArrayList<>();
            final LinearLayout linearLayout = (LinearLayout) productsDialog.findViewById(R.id.linearLayout);
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
            expiryDateValueTextView = (TextView) productsDialog.findViewById(R.id.expiry_date_value_textView);
            final EditText reasonRejectionEdittext = (EditText) productsDialog.findViewById(R.id.reasonRejection_edittext);
            final EditText rejectedQuantityEdittext = (EditText) productsDialog.findViewById(R.id.rejectedQuantity_edittext);
            final TextView textViewAdd = (TextView) productsDialog.findViewById(R.id.textView_add);
            final TextView textViewBack = (TextView) productsDialog.findViewById(R.id.textView_back);
            final EditText editTextHSNCode = (EditText) productsDialog.findViewById(R.id.hsn_edittext);

            productNameEdittext.setText(consignmentItem.getName());
            skuCodeEdittext.setText(consignmentItem.getInternalCode());
            orderQuantityEdittext.setText(consignmentItem.getOrderQuantity());
            editTextHSNCode.setText(consignmentItem.getHsnCode());
            receiveQuantityEdittext.setText("");

            if (operationType.equals("edit")) {
                receiveQuantityEdittext.setText(value);
                commentEdittext.setText(consignmentItem.getComment());
                reasonRejectionEdittext.setText(consignmentItem.getReasonRejection());
                expiryDateValueTextView.setText(consignmentItem.getExpiryDate());
                rejectedQuantityEdittext.setText(consignmentItem.getRejectedQuantity());
                // unitsTypeSpinner.setSelection(consignmentItem.getUnit());
            }

            //TODO Order Quantity and SKU Code fields will be hidden
            skuCodeEdittext.setVisibility(View.VISIBLE);
            orderQuantityEdittext.setVisibility(View.GONE);

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

            //Add Item
            textViewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (receiveQuantityEdittext.getText().toString() == null || receiveQuantityEdittext.getText().toString().isEmpty()) {
                        Toast.makeText(activity, "Please enter quantity", Toast.LENGTH_SHORT).show();
                    } else {
                        consignmentItem.setPrice(priceEdittext.getText().toString().trim());
                        consignmentItem.setComment(commentEdittext.getText().toString().trim());
                        consignmentItem.setExpiryDate(expiryDateValueTextView.getText().toString().trim());
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

                        consignmentItemsAdapter.notifyDataSetChanged();
                        productsDialog.dismiss();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }


                }
            });


            rejectedQuantityEdittext.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {


                }

                @Override
                public void afterTextChanged(Editable s) {

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
    public void editItemAndOpenDialog(int position, ConsignmentItem consignmentItem) {
        openDialogAddEditItems(this, "edit", 0, consignmentItem);
    }

    @Override
    public void removeItemAndNotify(int position) {
        if (consignmentItemList != null && consignmentItemList.size() > 0) {
            consignmentItemList.remove(position);
            consignmentItemsAdapter.notifyDataSetChanged();
        }
    }


    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    //Open Dialog to select Items
    public void openItemSearchDialog() {
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        itemSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
        itemsListRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        itemsVariationAdapter = new ItemsVariationAdapter(this,
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
                    status = NetworkUtil.getConnectivityStatusString(ApproveActivity.this);
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchItems(s.toString().trim());
                    } else {
                        Toast.makeText(ApproveActivity.this, getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.show();
    }

    //Searching Product from API after getting input from openItemSearchDialog
    public void searchItems(String str) {
        task = getString(R.string.quotation_search_item);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
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
                jsonObject.put("purorid", "");
                if (strAuthorizedById != null && !strAuthorizedById.isEmpty()) {
                    jsonObject.put("user", strAuthorizedById);
                } else {
                    jsonObject.put("user", "");
                }
                jsonObject.put("purchase_date", strPurchaseDate);
                jsonObject.put("chkid", strChkId);
                jsonObject.put("vendor-transportation-check_new", strTransporatationCheckBoxValue);
                jsonObject.put("venid", transporterId);
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

                    if (consignmentItemList.get(i).getPrice() != null &&
                            !consignmentItemList.get(i).getPrice().isEmpty()) {
                        productsItemJsonObject.put("item-mrp", "10");
                    } else {
                        productsItemJsonObject.put("item-mrp", "10");
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
            try {
                obj = new URL(AppPreferences.getLastDomain(context, AppUtils.DOMAIN)
                        + "?urlq=service" + "&version=1.0&key=123&task=" + context.getString(R.string.task_receive_consignment)
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
                        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
