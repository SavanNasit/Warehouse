package com.accrete.sixorbit.fragment.Drawer.collections;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.collections.CreateCollectionsInvoiceActivity;
import com.accrete.sixorbit.activity.customers.AddCustomerActivity;
import com.accrete.sixorbit.adapter.PaymentModesAdapter;
import com.accrete.sixorbit.adapter.QuotationCustomersAdapter;
import com.accrete.sixorbit.fragment.Drawer.AllDatePickerFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerList;
import com.accrete.sixorbit.model.Outlet;
import com.accrete.sixorbit.model.PaymentModeData;
import com.accrete.sixorbit.model.PaymentOptionAttribute;
import com.accrete.sixorbit.model.SearchRefferedDatum;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;
import static com.accrete.sixorbit.utils.AppUtils.ADD_QUOTATION_CUSTOMER_REQUEST_CODE;

/**
 * Created by {Anshul} on 15/5/18.
 */

public class CreateCollectionSelectCustomerFragment extends Fragment implements View.OnClickListener,
        QuotationCustomersAdapter.QuotationCustomersAdapterListener,
        PaymentModesAdapter.PaymentModesAdapterListener, PassDateToCounsellor {
    private RelativeLayout parentLayout;
    private CardView customerCardView;
    private TextView customerInfoTextView;
    private TextView customerNameTitleTextView;
    private EditText customerNameValueEditText;
    private ImageButton clearCustomerInfoImageButton;
    private LinearLayout receiveAmountLayout;
    private TextView receiveAmountTextView;
    private EditText receiveAmountValueEditText;
    private LinearLayout dateLayout;
    private TextView dateTextView;
    private EditText dateValueEditText;
    private CardView paymentTypeCardview;
    private TextView paymentTypeTitleTextView;
    private Spinner paymentTypeSpinner;
    private RecyclerView recyclerViewAdvancePaymentType;
    private CardView outletCardview;
    private TextView outletTitleTextView;
    private Spinner outletSpinner;
    private TextView addCustomerTextView;
    private ImageView loaderImageView;
    private Dialog dialog;
    private AutoCompleteTextView customerSearchEditText;
    private QuotationCustomersAdapter quotationCustomersAdapter;
    private TextView addNewCustomerTextView;
    private RecyclerView getCustomersProductsRecyclerView;
    private DatabaseHandler databaseHandler;
    private ArrayList<CustomerList> customerSearchArrayList = new ArrayList<>();
    private String status, strCuid, strAlid, stringStartDate;
    private List<PaymentModeData> paymentModeDataList = new ArrayList<>();
    private List<PaymentOptionAttribute> paymentAttributesList = new ArrayList<>();
    private PaymentModesAdapter paymentModesAdapter;
    private AllDatePickerFragment allDatePickerFragment;
    private Date startDate;
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
    private ArrayList<Outlet> outletArrayList = new ArrayList<>();
    private int clickedItemPos;

    public static CreateCollectionSelectCustomerFragment newInstance(String title) {
        CreateCollectionSelectCustomerFragment f = new CreateCollectionSelectCustomerFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        f.setArguments(args);
        return (f);
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.create_collection));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.create_collection));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.create_collection));
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.create_collection));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collection_select_customer, container, false);
        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        databaseHandler = new DatabaseHandler(getActivity());
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        parentLayout = (RelativeLayout) view.findViewById(R.id.parent_layout);
        customerCardView = (CardView) view.findViewById(R.id.customer_cardView);
        customerInfoTextView = (TextView) view.findViewById(R.id.customer_info_textView);
        customerNameTitleTextView = (TextView) view.findViewById(R.id.customer_name_title_textView);
        customerNameValueEditText = (EditText) view.findViewById(R.id.customer_name_value_editText);
        clearCustomerInfoImageButton = (ImageButton) view.findViewById(R.id.clear_customerInfo_imageButton);
        receiveAmountLayout = (LinearLayout) view.findViewById(R.id.receive_amount_layout);
        receiveAmountTextView = (TextView) view.findViewById(R.id.receive_amount_textView);
        receiveAmountValueEditText = (EditText) view.findViewById(R.id.receive_amount_value_editText);
        dateLayout = (LinearLayout) view.findViewById(R.id.date_layout);
        dateTextView = (TextView) view.findViewById(R.id.date_textView);
        dateValueEditText = (EditText) view.findViewById(R.id.date_value_editText);
        paymentTypeCardview = (CardView) view.findViewById(R.id.payment_type_cardview);
        paymentTypeTitleTextView = (TextView) view.findViewById(R.id.payment_type_title_textView);
        paymentTypeSpinner = (Spinner) view.findViewById(R.id.payment_type_spinner);
        recyclerViewAdvancePaymentType = (RecyclerView) view.findViewById(R.id.recyclerView_payment_type);
        outletCardview = (CardView) view.findViewById(R.id.outlet_cardview);
        outletTitleTextView = (TextView) view.findViewById(R.id.outlet_title_textView);
        outletSpinner = (Spinner) view.findViewById(R.id.outlet_spinner);
        addCustomerTextView = (TextView) view.findViewById(R.id.addCustomer_textView);
        loaderImageView = (ImageView) view.findViewById(R.id.loader_imageView);

        //DatePicker
        allDatePickerFragment = new AllDatePickerFragment();
        allDatePickerFragment.setListener(this);

        paymentModesAdapter = new PaymentModesAdapter(getActivity(), paymentAttributesList, this);

        //Payment Types RecyclerView
        RecyclerView.LayoutManager paymentModeLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdvancePaymentType.setLayoutManager(paymentModeLayoutManager);
        recyclerViewAdvancePaymentType.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAdvancePaymentType.setAdapter(paymentModesAdapter);
        recyclerViewAdvancePaymentType.setNestedScrollingEnabled(false);


        //Mandatory Outlet title
        String mainText = "Outlet";
        String colored = " *";
        Spannable spannableStringBuilder = new SpannableString(colored);
        int end = spannableStringBuilder.length();
        spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.RED), 0,
                end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        outletTitleTextView.setText(TextUtils.concat(mainText, spannableStringBuilder));

        //Mandatory Payment Type title
        String paymentTypeText = "Payment Type";
        paymentTypeTitleTextView.setText(TextUtils.concat(paymentTypeText, spannableStringBuilder));

        //Click Listener
        customerNameValueEditText.setOnClickListener(this);
        clearCustomerInfoImageButton.setOnClickListener(this);
        addCustomerTextView.setOnClickListener(this);
        dateValueEditText.setOnClickListener(this);

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

        //TODO - Date for cheques
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);
        dateValueEditText.setText(formattedDate);

        //TODO - Payment Mode Spinner Selection
        paymentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.clear();
                }
                if (paymentModeDataList.get(position).getPaymentOptionAttribute() != null &&
                        paymentModeDataList.get(position).getPaymentOptionAttribute().size() > 0) {
                    paymentAttributesList.addAll(paymentModeDataList.get(position).getPaymentOptionAttribute());
                }
                paymentModesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getPaymentModeAndOutlets();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
        }

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

    //Open Dialog to select customers
    public void openCustomerSearchDialog() {
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
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
                openAddCustomerActivity(getActivity(), dialog);
            }
        });

        //Customers RecyclerView
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quotationCustomersAdapter = new QuotationCustomersAdapter(getActivity(),
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
                    status = NetworkUtil.getConnectivityStatusString(getActivity());
                    if (!status.equals(getString(R.string.not_connected_to_internet))) {
                        if (s.length() > 0) {
                            searchCustomer(s.toString().trim());
                        }
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

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void searchCustomer(String str) {
        task = getString(R.string.add_quotation_search_customer_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO Updated on 31st May. If we need to show balance with name of customer, we have to pass balance 1 else we need to send 0
        Call<ApiResponse> call = apiService.getSearchedCustomerReference(version, key, task, userId, accessToken, str, "1");
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
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        //Refresh Adapter
                        quotationCustomersAdapter.notifyDataSetChanged();

                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    //Display button to add a new customer
                    if (customerSearchArrayList != null && customerSearchArrayList.size() > 0) {
                        getCustomersProductsRecyclerView.setVisibility(View.VISIBLE);
                        addNewCustomerTextView.setVisibility(View.GONE);
                    } else {
                        getCustomersProductsRecyclerView.setVisibility(View.GONE);
                        addNewCustomerTextView.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
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

                //currentAddressLayout.setVisibility(View.VISIBLE);
                customerNameValueEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
                    }
                });

                //Get CUID
                strCuid = selected.getId();
                strAlid = selected.getAlid();

                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        try {
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
                        strAlid = "";
                    }
                    break;
                case R.id.addCustomer_textView:
                    addCustomerTextView.setEnabled(false);

                    boolean isPayment = false;
                    String paymentField = "";
                    ArrayList<String> stringArray = new ArrayList<String>();

                    if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                        for (int q = 0; q < paymentAttributesList.size(); q++) {
                            if (paymentAttributesList.get(q).getRequired() != null &&
                                    !paymentAttributesList.get(q).getRequired().isEmpty() &&
                                    paymentAttributesList.get(q).getRequired().equals("1")) {
                                if (paymentAttributesList.get(q).getValue() == null ||
                                        paymentAttributesList.get(q).getValue().isEmpty() ||
                                        paymentAttributesList.get(q).getValue().toString().length() == 0) {
                                    isPayment = true;
                                    paymentField = paymentAttributesList.get(q).getName();
                                }
                            }
                        }
                        //TODO - Payment Modes Attributes
                        for (int i = 0; i < paymentAttributesList.size(); i++) {
                            JSONObject paymentModesAttributesJsonObject = new JSONObject();

                            //TODO Updated on 8th June
                            if (paymentAttributesList.get(i).getName().toLowerCase().equals("date")) {
                                //Date Format
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                                String chequeDate = "";
                                if (paymentAttributesList.get(i).getValue().toString().trim() != null &&
                                        !paymentAttributesList.get(i).getValue().toString().trim().isEmpty() &&
                                        !paymentAttributesList.get(i).getValue().toString().trim().contains("0000")
                                && paymentAttributesList.get(i).getValue().toString().charAt(7)!='-')
                                {
                                    try {
                                        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = simpleDateFormat.parse(paymentAttributesList.get(i).getValue().toString());
                                        paymentAttributesList.get(i).setValue(outputFormat.format(date).toString().trim());
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            paymentModesAttributesJsonObject.put(paymentAttributesList.get(i).getCpoaid(),
                                    paymentAttributesList.get(i).getValue());
                            stringArray.add(paymentModesAttributesJsonObject.toString());
                        }
                    }

                    if (customerNameValueEditText.getText().toString().length() == 0 || strCuid == null || strCuid.isEmpty()) {
                        Toast.makeText(getActivity(), "Please select a customer.", Toast.LENGTH_SHORT).show();
                    } else if (paymentTypeSpinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(getActivity(), "Please select payment type.", Toast.LENGTH_SHORT).show();
                    } else if (isPayment) {
                        Toast.makeText(getActivity(), "Please enter " + paymentField + "", Toast.LENGTH_SHORT).show();
                    } else if (outletCardview.getVisibility() == View.VISIBLE && outletSpinner.getSelectedItemPosition() == 0) {
                        Toast.makeText(getActivity(), "Please select an outlet.", Toast.LENGTH_SHORT).show();
                    } else if (receiveAmountValueEditText.getText().toString() == null ||
                            receiveAmountValueEditText.getText().toString().isEmpty() ||
                            receiveAmountValueEditText.getText().toString().length() == 0 ||
                            roundTwoDecimals(Constants.ParseDouble(receiveAmountValueEditText.getText().toString())) == 0.00) {
                        Toast.makeText(getActivity(), "Please enter valid receive amount.", Toast.LENGTH_SHORT).show();
                    } else {
                        //Date Format
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String collectionDate = "";
                        if (dateValueEditText.getText().toString().trim() != null &&
                                !dateValueEditText.getText().toString().trim().isEmpty() &&
                                !dateValueEditText.getText().toString().trim().contains("0000")) {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date date = simpleDateFormat.parse(dateValueEditText.getText().toString());
                                collectionDate = outputFormat.format(date).toString().trim();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(getActivity(), CreateCollectionsInvoiceActivity.class);
                        intent.putExtra("payment_type", paymentModeDataList.get(paymentTypeSpinner.getSelectedItemPosition())
                                .getCpoid());
                        intent.putExtra("payment_type_text", paymentModeDataList.get(paymentTypeSpinner.getSelectedItemPosition())
                                .getName());
                        intent.putStringArrayListExtra("attributes_array", stringArray);
                        intent.putExtra("date", collectionDate);
                        intent.putExtra("cuId", strCuid);
                        intent.putExtra("customer_name", customerNameValueEditText.getText().toString());
                        intent.putExtra("alId", strAlid);
                        intent.putExtra("amount", receiveAmountValueEditText.getText().toString());
                        intent.putExtra("outletId",
                                outletArrayList.get(outletSpinner.getSelectedItemPosition()).getChkid());
                        startActivity(intent);
                    }

                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            addCustomerTextView.setEnabled(true);
                        }
                    }, 3000);
                    break;
                case R.id.date_value_editText:
                    //dateValueEditText.setEnabled(false);
                    allDatePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.select_date));
                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dateValueEditText.setEnabled(true);
                        }
                    }, 4000);
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCustomerClick(int position) {
        CustomerList selected = customerSearchArrayList.get(position);
        if (selected.getName() != null && !selected.getName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getName().toString().trim());
        } else if (selected.getCompanyName() != null && !selected.getCompanyName().toString().trim().isEmpty()) {
            customerNameValueEditText.setText(selected.getCompanyName().toString().trim());
        }

        customerNameValueEditText.post(new Runnable() {
            @Override
            public void run() {
                customerNameValueEditText.setSelection(customerNameValueEditText.getText().toString().length());
            }
        });

        //Get CUID
        strCuid = selected.getId();
        strAlid = selected.getAlid();

        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }

    //Searching customer from API after getting input from openCustomerSearchDialog
    public void getPaymentModeAndOutlets() {
        task = getString(R.string.collection_fetch_form_data);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getDataWithoutId(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {

                    if (apiResponse.getSuccess()) {

                        //TODO - Payment Mode Data Type
                        PaymentModeData paymentModeData = new PaymentModeData();
                        paymentModeData.setName("Select");
                        paymentModeDataList.add(paymentModeData);
                        if (apiResponse.getData().getPaymentModeData() != null &&
                                !apiResponse.getData().getPaymentModeData().isEmpty() &&
                                !apiResponse.getData().getPaymentModeData().equals("null")) {
                            for (PaymentModeData type : apiResponse.getData().getPaymentModeData()) {
                                paymentModeDataList.add(type);
                            }
                        }

                        ArrayAdapter<PaymentModeData> modeDataArrayAdapter =
                                new ArrayAdapter<PaymentModeData>(getActivity(),
                                        R.layout.simple_spinner_item, paymentModeDataList);
                        modeDataArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        paymentTypeSpinner.setAdapter(modeDataArrayAdapter);
                        paymentTypeSpinner.setSelection(0);

                        //TODO Outlet
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
                        //Outlet Adapter
                        ArrayAdapter<Outlet> outletArrayAdapter =
                                new ArrayAdapter<Outlet>(getActivity(), R.layout.simple_spinner_item, outletArrayList);
                        outletArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
                        outletSpinner.setAdapter(outletArrayAdapter);
                        outletSpinner.setSelection(0);

                        if (apiResponse.getData().isOutletEnable()) {
                            outletCardview.setVisibility(View.VISIBLE);
                        } else {
                            outletCardview.setVisibility(View.GONE);
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void updateAttributes(int position, String value) {
        if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
            if (value != null) {
                paymentAttributesList.get(position).setValue(value);
            } else {
                paymentAttributesList.get(position).setValue("");
            }
        }
    }

    @Override
    public void updateDate(int position) {
        allDatePickerFragment.show(getActivity().getSupportFragmentManager(), getString(R.string.cheque_date));
        this.clickedItemPos = position;
    }

    @Override
    public void passDate(String s) {
        stringStartDate = s;
        if (allDatePickerFragment.getTag().equals(getString(R.string.cheque_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                //Date Of cheque
                if (paymentAttributesList != null && paymentAttributesList.size() > 0) {
                    paymentAttributesList.get(clickedItemPos).setValue(from);
                    paymentModesAdapter.notifyDataSetChanged();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (allDatePickerFragment.getTag().equals(getString(R.string.select_date))) {
            try {
                startDate = dateFormatter.parse(stringStartDate);
                String from = dateFormatter.format(startDate);
                dateValueEditText.setText(from);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void passTime(String s) {

    }
}
