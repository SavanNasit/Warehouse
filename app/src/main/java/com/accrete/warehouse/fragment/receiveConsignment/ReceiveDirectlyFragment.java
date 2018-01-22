package com.accrete.warehouse.fragment.receiveConsignment;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.accrete.warehouse.model.Measurement;
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
import java.net.HttpURLConnection;
import java.net.URL;
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

/**
 * Created by poonam on 12/8/17.
 */

public class ReceiveDirectlyFragment extends Fragment implements View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, VendorsAdapter.VendorsAdapterListener, PassDateToCounsellor,
        ReceiveConsignmentItemsAdapter.ReceiveConsignmentItemsAdapterListener,
        ItemsVariationAdapter.ItemsVariationAdapterListener, AuthorizedByUserAdapter.AuthorizedByUserAdapterListener {
    private static String KEY_TITLE = "receive_directly";
    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private TextView vendorDetailsSubTitleTextView;
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
    private TextView authorizedByTitleTextView;
    private EditText authorizedByValueEditText;
    private ImageButton clearAuthorizedByImageButton;
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
    private LinearLayoutManager mLayoutManager;
    private ReceiveConsignmentItemsAdapter receiveConsignmentItemsAdapter;
    private List<ConsignmentItem> consignmentItemList = new ArrayList<>();
    private String purOrId, status, vendorId, transporterId;
    private AllDatePickerFragment datePickerFragment;
    private ArrayList<ItemList> itemListArrayList = new ArrayList<>();
    private TextView expiryDateValueTextView;
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

    public static ReceiveDirectlyFragment newInstance(String title) {
        ReceiveDirectlyFragment f = new ReceiveDirectlyFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    private void findViews(View rootView) {
        vendorDetailsSubTitleTextView = (TextView) rootView.findViewById(R.id.vendorDetailsSubTitle_textView);
        vendorTitleTextView = (TextView) rootView.findViewById(R.id.vendor_title_textView);
        vendorValueEditText = (EditText) rootView.findViewById(R.id.vendor_value_editText);
        clearVendorInfoImageButton = (ImageButton) rootView.findViewById(R.id.clear_vendorInfo_imageButton);
        purchaseDetailsTitleTextView = (TextView) rootView.findViewById(R.id.purchaseDetailsTitle_textView);
        purchaseDetailsSubTitleTextView = (TextView) rootView.findViewById(R.id.purchaseDetailsSubTitle_textView);
        purchaseDetailsView = (View) rootView.findViewById(R.id.purchaseDetails_view);
        receiveDateTitleTextView = (TextView) rootView.findViewById(R.id.receive_date_title_textView);
        receiveDateValueTextView = (TextView) rootView.findViewById(R.id.receive_date_value_textView);
        invoiceNumberTitleTextView = (TextView) rootView.findViewById(R.id.invoice_number_title_textView);
        invoiceNumberValueTextView = (EditText) rootView.findViewById(R.id.invoice_number_value_textView);
        invoiceDateTitleTextView = (TextView) rootView.findViewById(R.id.invoice_date_title_textView);
        invoiceDateValueTextView = (TextView) rootView.findViewById(R.id.invoice_date_value_textView);
        authorizedByTitleTextView = (TextView) rootView.findViewById(R.id.authorizedBy_title_textView);
        authorizedByValueEditText = (EditText) rootView.findViewById(R.id.authorizedBy_value_editText);
        clearAuthorizedByImageButton = (ImageButton) rootView.findViewById(R.id.clear_authorizedBy_imageButton);
        consignmentItemsListTitleTextView = (TextView) rootView.findViewById(R.id.consignmentItemsListTitle_textView);
        consignmentItemsListSubTitleTextView = (TextView) rootView.findViewById(R.id.consignmentItemsListSubTitle_textView);
        consignmentItemsListView = (View) rootView.findViewById(R.id.consignmentItemsList_view);
        searchItemValueEditText = (EditText) rootView.findViewById(R.id.search_item_value_editText);
        itemsRecyclerView = (RecyclerView) rootView.findViewById(R.id.items_recyclerView);
        addTransportationCheckBoxTextView = (CheckBox) rootView.findViewById(R.id.add_transportation_checkBox_textView);
        transportationLayout = (LinearLayout) rootView.findViewById(R.id.transportation_layout);
        transportationDetailsTitleTextView = (TextView) rootView.findViewById(R.id.transportationDetailsTitle_textView);
        transportationDetailsSubTitleTextView = (TextView) rootView.findViewById(R.id.transportationDetailsSubTitle_textView);
        transportationDetailsView = (View) rootView.findViewById(R.id.transportationDetails_view);
        transporterTitleTextView = (TextView) rootView.findViewById(R.id.transporter_title_textView);
        transporterValueEditText = (TextView) rootView.findViewById(R.id.transporter_value_editText);
        clearTransporterInfoImageButton = (ImageButton) rootView.findViewById(R.id.clear_transporterInfo_imageButton);
        weightValueEditText = (EditText) rootView.findViewById(R.id.weight_value_editText);
        lrNumberValueEditText = (EditText) rootView.findViewById(R.id.lr_number_value_editText);
        vehicleNumberValueEditText = (EditText) rootView.findViewById(R.id.vehicle_number_value_editText);
        expectedDateTitleTextView = (TextView) rootView.findViewById(R.id.expected_date_title_textView);
        expectedDateValueTextView = (TextView) rootView.findViewById(R.id.expected_date_value_textView);
        saveTextView = (TextView) rootView.findViewById(R.id.save_textView);
        weightTitleTextView = (TextView) rootView.findViewById(R.id.weight_title_textView);

        //Items RecyclerView
        receiveConsignmentItemsAdapter = new ReceiveConsignmentItemsAdapter(getActivity(), consignmentItemList,
                this, "Directly");
        mLayoutManager = new LinearLayoutManager(getActivity());
        itemsRecyclerView.setLayoutManager(mLayoutManager);
        itemsRecyclerView.setHasFixedSize(true);
        itemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsRecyclerView.setNestedScrollingEnabled(false);
        itemsRecyclerView.setAdapter(receiveConsignmentItemsAdapter);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Mandatory Fields
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        receiveDateTitleTextView.setText(TextUtils.concat(getString(R.string.receive_date_title), spannableStringBuilder));
        invoiceNumberTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_number_title), spannableStringBuilder));
        invoiceDateTitleTextView.setText(TextUtils.concat(getString(R.string.invoice_date_title), spannableStringBuilder));
        vendorTitleTextView.setText(TextUtils.concat(getString(R.string.vendor_title), spannableStringBuilder));
        transporterTitleTextView.setText(TextUtils.concat(getString(R.string.transporter_title), spannableStringBuilder));
        weightTitleTextView.setText(TextUtils.concat(getString(R.string.weight), spannableStringBuilder));
        expectedDateTitleTextView.setText(TextUtils.concat(getString(R.string.expected_date_title), spannableStringBuilder));

        clearVendorInfoImageButton.setOnClickListener(this);
        clearTransporterInfoImageButton.setOnClickListener(this);
        clearAuthorizedByImageButton.setOnClickListener(this);
        addTransportationCheckBoxTextView.setOnCheckedChangeListener(this);
        vendorValueEditText.setOnClickListener(this);
        authorizedByValueEditText.setOnClickListener(this);
        transporterValueEditText.setOnClickListener(this);
        receiveDateValueTextView.setOnClickListener(this);
        invoiceDateValueTextView.setOnClickListener(this);
        expectedDateValueTextView.setOnClickListener(this);
        searchItemValueEditText.setOnClickListener(this);
        saveTextView.setOnClickListener(this);
        transportationLayout.setVisibility(View.GONE);

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_directly, container, false);
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_directly);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_directly));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_directly));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_directly));
        }
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
            case R.id.clear_authorizedBy_imageButton:
                authorizedByValueEditText.setText("");
                strAuthorizedById = "";
                break;
            case R.id.vendor_value_editText:
                if (vendorValueEditText.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("vendor");
                }
                break;
            case R.id.authorizedBy_value_editText:
                if (authorizedByValueEditText.getText().toString().trim().length() == 0) {
                    openAuthorizedBySearchDialog("authorizedBy");
                }
                break;
            case R.id.transporter_value_editText:
                if (transporterValueEditText.getText().toString().trim().length() == 0) {
                    openVendorSearchDialog("transporter");
                }
                break;
            case R.id.expected_date_value_textView:
                datePickerFragment.show(getChildFragmentManager(), "expected");
                break;
            case R.id.receive_date_value_textView:
                datePickerFragment.show(getChildFragmentManager(), "receive");
                break;
            case R.id.invoice_date_value_textView:
                datePickerFragment.show(getChildFragmentManager(), "invoice");
                break;
            case R.id.search_item_value_editText:
                openItemSearchDialog();
                break;
            case R.id.save_textView:
                //Disable Button
                saveTextView.setEnabled(false);

                if (getPostData()) {
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        ReceiveItemsAsyncTask receiveItemsAsyncTask =
                                new ReceiveItemsAsyncTask(getActivity());
                        receiveItemsAsyncTask.execute();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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

    //Get data from fields and post
    public boolean getPostData() {
        if (receiveDateValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please select receive date.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (invoiceNumberValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please enter Invoice number.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (invoiceDateValueTextView.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), "Please select Invoice date.", Toast.LENGTH_SHORT).show();
            return false;
        }

        strInvoiceNumber = invoiceNumberValueTextView.getText().toString().trim();
        strChkId = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
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

        if (itemListArrayList == null || itemListArrayList.size() == 0) {
            Toast.makeText(getActivity(), "Please add atleast one item first.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (addTransportationCheckBoxTextView.isChecked()) {
            strTransporatationCheckBoxValue = "1";
            strLRNumber = lrNumberValueEditText.getText().toString().trim();
            strVehicleNumber = vehicleNumberValueEditText.getText().toString().trim();

            if (transporterId == null || transporterId.isEmpty()) {
                Toast.makeText(getActivity(), "Please select transporter first.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (weightValueEditText.getText().toString().trim().length() == 0) {
                Toast.makeText(getActivity(), "Please enter weight.", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (expectedDateValueTextView.getText().toString().trim().length() == 0) {
                Toast.makeText(getActivity(), "Please select expected date.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            strTransporatationCheckBoxValue = "0";
        }
        return true;
    }

    //Open Dialog to select vendors
    public void openVendorSearchDialog(final String userType) {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        vendorSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
        vendorsRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        vendorsAdapter = new VendorsAdapter(getActivity(),
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
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchVendor(s.toString().trim(), userType);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
        authorizedByDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        authorizedByDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        authorizedByDialog.setContentView(R.layout.dialog_search_vendor);
        Window window = authorizedByDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        authorizedBySearchAutoCompleteTextView = (AutoCompleteTextView) authorizedByDialog.findViewById(R.id.search_autoCompleteTextView);
        authorizedByRecyclerView = (RecyclerView) authorizedByDialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        authorizedByUserAdapter = new AuthorizedByUserAdapter(getActivity(),
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
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchAuthorizedUser(s.toString().trim(), userType);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
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

    //Searching Authorized By User from API after getting input from openAuthorizedBySearchDialog
    public void searchAuthorizedUser(String str, final String userType) {
        task = getString(R.string.authorized_user_search_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
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
            authorizedByValueEditText.setText(selected.getName().toString().trim());
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
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            searchedProductDetails(selected.getId(), "API");
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }
    }

    //Get Details of clicked items after selecting from a dialog
    public void searchedProductDetails(String id, final String sourceType) {
        task = getString(R.string.task_products_details);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
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
                        openDialogAddEditItems(getActivity(), "add", 0, consignmentItem);

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
        productsDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        productsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        productsDialog.setContentView(R.layout.dialog_add_item);
        Window window = productsDialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        try {
            final ArrayList<Measurement> measurementArrayList = new ArrayList<>();
            final LinearLayout linearLayout = (LinearLayout) productsDialog.findViewById(R.id.linearLayout);
            final TextView titleTextView = (TextView) productsDialog.findViewById(R.id.title_textView);
            final EditText productNameEdittext = (EditText) productsDialog.findViewById(R.id.product_name_edittext);
            final EditText skuCodeEdittext = (EditText) productsDialog.findViewById(R.id.skuCode_edittext);
            final EditText orderQuantityEdittext = (EditText) productsDialog.findViewById(R.id.orderQuantity_edittext);
            final EditText receiveQuantityEdittext = (EditText) productsDialog.findViewById(R.id.receiveQuantity_edittext);
            final TextView unitTitleTextView = (TextView) productsDialog.findViewById(R.id.unit_title_textView);
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

            productNameEdittext.setText(consignmentItem.getName());
            skuCodeEdittext.setText(consignmentItem.getInternalCode());
            orderQuantityEdittext.setText(consignmentItem.getOrderQuantity());
            receiveQuantityEdittext.setText("");

            //TODO Order Quantity and SKU Code fields will be hidden
            skuCodeEdittext.setVisibility(View.GONE);
            orderQuantityEdittext.setVisibility(View.GONE);

            measurementArrayList.addAll(consignmentItem.getMeasurements());
            ArrayAdapter<Measurement> measurementArrayAdapter =
                    new ArrayAdapter<Measurement>(activity, R.layout.simple_spinner_item, measurementArrayList);
            measurementArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
            unitsTypeSpinner.setAdapter(measurementArrayAdapter);

            expiryDateValueTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePickerFragment.show(getChildFragmentManager(), "expiryDate");
                }
            });

            //Add Item
            textViewAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    consignmentItem.setPrice(priceEdittext.getText().toString().trim());
                    consignmentItem.setComment(commentEdittext.getText().toString().trim());
                    consignmentItem.setExpiryDate(expiryDateValueTextView.getText().toString().trim());
                    consignmentItem.setRejectedQuantity(rejectedQuantityEdittext.getText().toString().trim());
                    consignmentItem.setReasonRejection(reasonRejectionEdittext.getText().toString().trim());
                    consignmentItem.setUnit(measurementArrayList.get(unitsTypeSpinner.getSelectedItemPosition()).getName());
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
    public void editItemAndOpenDialog(int position) {

    }

    @Override
    public void removeItemAndNotify(int position) {
        if (consignmentItemList != null && consignmentItemList.size() > 0) {
            consignmentItemList.remove(position);
            receiveConsignmentItemsAdapter.notifyDataSetChanged();
        }
    }

    //Open Dialog to select Items
    public void openItemSearchDialog() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_search_vendor);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        itemSearchAutoCompleteTextView = (AutoCompleteTextView) dialog.findViewById(R.id.search_autoCompleteTextView);
        itemsListRecyclerView = (RecyclerView) dialog.findViewById(R.id.recyclerView);

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        itemsVariationAdapter = new ItemsVariationAdapter(getActivity(),
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
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        searchItems(s.toString().trim());
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
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
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
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
                        productsItemJsonObject.put("item-mrp", consignmentItemList.get(i).getPrice());
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
                        //    finish();
                        getActivity().getSupportFragmentManager().popBackStack();
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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
