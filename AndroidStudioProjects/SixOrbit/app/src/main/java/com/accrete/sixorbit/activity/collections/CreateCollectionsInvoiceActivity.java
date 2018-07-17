package com.accrete.sixorbit.activity.collections;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CollectionInvoiceListAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.InvoiceList;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class CreateCollectionsInvoiceActivity extends AppCompatActivity implements
        CollectionInvoiceListAdapter.CollectionsInvoiceListListener,
        View.OnClickListener {
    private Toolbar toolbar;
    private CardView cardViewCustomer;
    private TextView customerNameTitleTextView;
    private TextView paymentTypeTextView;
    private TextView receiveAmountTextView;
    private TextView changeDetailsTextView;
    private RecyclerView recyclerViewInvoices;
    private ImageView loaderImageView;
    private TextView emptyTextView, submitTextView, totalPendingAmountTextView,
            selectedAmountTextView;
    private List<InvoiceList> invoiceLists = new ArrayList<>();
    private CollectionInvoiceListAdapter invoiceListAdapter;
    private String strCuid, strChkId, strAlId, status, receiveAmount, strDate, paymentType,
            strCustomerName, strPaymentType, chkoId, invId;
    private CardView advanceReferenceCardview;
    private TextView advanceReferenceTitleTextView;
    private LinearLayout advanceReferenceLayout;
    private TextInputEditText advanceReferenceNumberTextInputEditText;
    private TextInputEditText advanceReferenceAmountTextInputEditText;
    private TextInputEditText remarksTextInputEditText;
    private double selectedAmount = 0.00, enteredReceivingAmount = 0.00;
    private ArrayList<String> attributesArray = new ArrayList<String>();
    private int selectedCount = 0;

    private void updateAdvanceAmount() {
        enteredReceivingAmount = 0.0;

        for (int i = 0; i < invoiceLists.size(); i++) {
            if (invoiceLists.get(i).isSelectedInvoice()) {
                enteredReceivingAmount += Constants.ParseDouble(invoiceLists.get(i).getReceiveAmount());
            }
        }
        if (enteredReceivingAmount <= Constants.ParseDouble(receiveAmount)) {
            advanceReferenceAmountTextInputEditText.setText(new BigDecimal(Constants.ParseDouble(receiveAmount)
                    - enteredReceivingAmount).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }
    }

    @Override
    public void onCheckUncheckItem(int position, boolean status) {
        try {
            if (status) {
                selectedAmount += Constants.ParseDouble(invoiceLists.get(position).getPendingAmount());
                selectedCount++;
            } else {
                selectedAmount -= Constants.ParseDouble(invoiceLists.get(position).getPendingAmount());
                selectedCount--;
                // invoiceLists.get(position).setReceiveAmount("0.0");
            }
            invoiceLists.get(position).setSelectedInvoice(status);
            selectedAmountTextView.setText("Selected Amount : " + new BigDecimal(selectedAmount)
                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
            selectedAmountTextView.append(" [" + selectedCount + "]");
            updateAdvanceAmount();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateAdvanceReference(String value) {
        advanceReferenceAmountTextInputEditText.setText(new BigDecimal(Constants.ParseDouble(receiveAmount)
                - Constants.ParseDouble(value)).setScale(2, RoundingMode.HALF_UP).toPlainString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_collections_invoice);

        if (getIntent() != null) {
            if (getIntent().hasExtra("cuId")) {
                strCuid = getIntent().getStringExtra("cuId");
            }
            if (getIntent().hasExtra("alId")) {
                strAlId = getIntent().getStringExtra("alId");
            }
            if (getIntent().hasExtra("outletId")) {
                strChkId = getIntent().getStringExtra("outletId");
            }
            if (getIntent().hasExtra("amount")) {
                receiveAmount = getIntent().getStringExtra("amount");
            }
            if (getIntent().hasExtra("payment_type")) {
                paymentType = getIntent().getStringExtra("payment_type");
            }
            if (getIntent().hasExtra("date")) {
                strDate = getIntent().getStringExtra("date");
            }
            if (getIntent().hasExtra("payment_type_text")) {
                strPaymentType = getIntent().getStringExtra("payment_type_text");
            }
            if (getIntent().hasExtra("customer_name")) {
                strCustomerName = getIntent().getStringExtra("customer_name");
            }
            if (getIntent().hasExtra("attributes_array")) {
                attributesArray = getIntent().getStringArrayListExtra("attributes_array");
            }
            if (getIntent().hasExtra(getString(R.string.chkoid))) {
                chkoId = getIntent().getStringExtra(getString(R.string.chkoid));
            }
            if (getIntent().hasExtra(getString(R.string.invid))) {
                invId = getIntent().getStringExtra(getString(R.string.invid));
            }
        }

        cardViewCustomer = (CardView) findViewById(R.id.card_view_customer);
        customerNameTitleTextView = (TextView) findViewById(R.id.customerName_title_textView);
        paymentTypeTextView = (TextView) findViewById(R.id.payment_type_textView);
        receiveAmountTextView = (TextView) findViewById(R.id.receive_amount_textView);
        changeDetailsTextView = (TextView) findViewById(R.id.change_details_textView);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerViewInvoices = (RecyclerView) findViewById(R.id.recyclerView_invoices);
        loaderImageView = (ImageView) findViewById(R.id.loader_imageView);
        emptyTextView = (TextView) findViewById(R.id.empty_textView);
        submitTextView = (TextView) findViewById(R.id.submit_textView);
        totalPendingAmountTextView = (TextView) findViewById(R.id.total_pending_amount_textView);
        advanceReferenceCardview = (CardView) findViewById(R.id.advance_reference_cardview);
        advanceReferenceTitleTextView = (TextView) findViewById(R.id.advance_reference_title_textView);
        advanceReferenceLayout = (LinearLayout) findViewById(R.id.advance_reference_layout);
        advanceReferenceNumberTextInputEditText = (TextInputEditText) findViewById(R.id.advance_reference_number_textInputEditText);
        advanceReferenceAmountTextInputEditText = (TextInputEditText) findViewById(R.id.advance_reference_amount_textInputEditText);
        selectedAmountTextView = (TextView) findViewById(R.id.selected_amount_textView);
        remarksTextInputEditText = (TextInputEditText) findViewById(R.id.remarks_textInputEditText);

        selectedAmountTextView.setText("Selected Amount : 0.00");
        paymentTypeTextView.setText("Payment Type: " + strPaymentType);
        customerNameTitleTextView.setText("" + strCustomerName);
        receiveAmountTextView.setText("Receive Amount: " + roundTwoDecimals(Constants.ParseDouble(receiveAmount)));
        changeDetailsTextView.setText("Edit details");

        changeDetailsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if (receiveAmount != null && !receiveAmount.isEmpty()) {
            advanceReferenceAmountTextInputEditText.setText(new BigDecimal(Constants.ParseDouble(
                    receiveAmount)).setScale(2, RoundingMode.HALF_UP).toPlainString());
        }

        invoiceListAdapter = new CollectionInvoiceListAdapter(CreateCollectionsInvoiceActivity.this,
                invoiceLists, this, receiveAmount);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewInvoices.setLayoutManager(mLayoutManager);
        recyclerViewInvoices.setNestedScrollingEnabled(false);
        recyclerViewInvoices.setAdapter(invoiceListAdapter);

        if (getIntent().hasExtra("title")) {
            toolbar.setTitle(getIntent().getStringExtra("title"));
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        advanceReferenceAmountTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    enteredReceivingAmount = 0.0;
                    for (int i = 0; i < invoiceLists.size(); i++) {
                        if (invoiceLists.get(i).isSelectedInvoice()) {
                            enteredReceivingAmount += Constants.ParseDouble(invoiceLists.get(i).getReceiveAmount());
                        }
                    }
                    if (enteredReceivingAmount <= Constants.ParseDouble(receiveAmount)) {
                        advanceReferenceAmountTextInputEditText.setText(new BigDecimal(Constants.ParseDouble(receiveAmount)
                                - enteredReceivingAmount).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                }
            }
        });

        remarksTextInputEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    enteredReceivingAmount = 0.0;
                    for (int i = 0; i < invoiceLists.size(); i++) {
                        if (invoiceLists.get(i).isSelectedInvoice()) {
                            enteredReceivingAmount += Constants.ParseDouble(invoiceLists.get(i).getReceiveAmount());
                        }
                    }
                    if (enteredReceivingAmount <= Constants.ParseDouble(receiveAmount)) {
                        advanceReferenceAmountTextInputEditText.setText(new BigDecimal(Constants.ParseDouble(receiveAmount)
                                - enteredReceivingAmount).setScale(2, RoundingMode.HALF_UP).toPlainString());
                    }
                }
            }
        });

        status = NetworkUtil.getConnectivityStatusString(CreateCollectionsInvoiceActivity.this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            getCollectionInvoicesList(strAlId);
        } else {
            Toast.makeText(CreateCollectionsInvoiceActivity.this, getString(R.string.no_internet_try_later),
                    Toast.LENGTH_SHORT).show();
        }

        submitTextView.setOnClickListener(this);
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (CreateCollectionsInvoiceActivity.this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CreateCollectionsInvoiceActivity.this != null) {
                                if (invoiceLists != null && invoiceLists.size() == 0) {
                                    if (loaderImageView.getVisibility() == View.GONE) {
                                        loaderImageView.setVisibility(View.VISIBLE);
                                    }
                                    //Disable Touch
                                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Ion.with(loaderImageView)
                                            .animateGif(AnimateGifMode.ANIMATE)
                                            .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                            .withBitmapInfo();
                                }
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    private void hideLoader() {
        if (CreateCollectionsInvoiceActivity.this != null) {
            if (loaderImageView.getVisibility() == View.VISIBLE) {
                loaderImageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    private void getCollectionInvoicesList(String alId) {
        String task = getString(R.string.collection_get_all_invoices);

        if (AppPreferences.getIsLogin(CreateCollectionsInvoiceActivity.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(CreateCollectionsInvoiceActivity.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(CreateCollectionsInvoiceActivity.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(CreateCollectionsInvoiceActivity.this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getCreateCollectionsInvoicesList(version, key, task, userId, accessToken,
                alId, invId, chkoId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())) + "");
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        recyclerViewInvoices.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);

                        for (InvoiceList invoiceList : apiResponse.getData().getInvoiceList()) {
                            if (invoiceList != null) {
                                invoiceLists.add(invoiceList);
                            }
                        }

                        if (invoiceLists != null && invoiceLists.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerViewInvoices.setVisibility(View.GONE);
                            emptyTextView.setText("No data available.");
                            submitTextView.setVisibility(View.GONE);
                            totalPendingAmountTextView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerViewInvoices.setVisibility(View.VISIBLE);
                            invoiceListAdapter.notifyDataSetChanged();
                            submitTextView.setVisibility(View.VISIBLE);
                            totalPendingAmountTextView.setVisibility(View.VISIBLE);
                            totalPendingAmountTextView.setText("Total Pending Amount : " +
                                    getString(R.string.Rs) + " " + apiResponse.getData().getTotalPending());
                        }
                        hideLoader();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            emptyTextView.setText(getString(R.string.no_data_available));
                            recyclerViewInvoices.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.VISIBLE);
                        }//Deleted User
                        else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(CreateCollectionsInvoiceActivity.this, apiResponse.getMessage());
                        }
                        if (invoiceLists != null && invoiceLists.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            emptyTextView.setText(getString(R.string.no_data_available));
                            recyclerViewInvoices.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerViewInvoices.setVisibility(View.VISIBLE);
                        }
                        hideLoader();
                    }
                    changeDetailsTextView.requestFocus();
                    hideLoader();
                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (CreateCollectionsInvoiceActivity.this != null) {
                    Toast.makeText(CreateCollectionsInvoiceActivity.this,
                            getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit_textView:
                submitTextView.setEnabled(false);

                remarksTextInputEditText.requestFocus();
                status = NetworkUtil.getConnectivityStatusString(CreateCollectionsInvoiceActivity.this);
                if (advanceReferenceAmountTextInputEditText.getText().toString().trim() != null &&
                        !advanceReferenceAmountTextInputEditText.getText().toString().trim().isEmpty() &&
                        roundTwoDecimals(ParseDouble(advanceReferenceAmountTextInputEditText.getText().toString().trim())) != 0.00 &&
                        (advanceReferenceNumberTextInputEditText.getText().toString() == null ||
                                advanceReferenceNumberTextInputEditText.getText().toString().isEmpty() ||
                                advanceReferenceNumberTextInputEditText.getText().toString().length() == 0)) {
                    Toast.makeText(CreateCollectionsInvoiceActivity.this,
                            "Please provide valid Advance reference Number.", Toast.LENGTH_SHORT).show();
                    advanceReferenceNumberTextInputEditText.requestFocus();

                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            submitTextView.setEnabled(true);
                        }
                    }, 3500);

                } else if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    createCollection();
                } else {
                    Toast.makeText(CreateCollectionsInvoiceActivity.this, getString(R.string.no_internet_try_later),
                            Toast.LENGTH_SHORT).show();

                    //Enable Again
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            submitTextView.setEnabled(true);
                        }
                    }, 3500);

                }


                break;
        }
    }

    @Override
    public void updateReceiveAmountValues(String value, int position) {
        invoiceLists.get(position).setReceiveAmount(value);
    }

    public void createCollection() {
        JSONObject dataJSONObject = new JSONObject();
        try {
            dataJSONObject.put("customer", strCuid);
            dataJSONObject.put("date", strDate);
            dataJSONObject.put("payment_type", paymentType);
            dataJSONObject.put("paying_amount", receiveAmount);
            dataJSONObject.put("outlet", strChkId);
            dataJSONObject.put("remarks", remarksTextInputEditText.getText().toString());
            if (attributesArray != null && attributesArray.size() > 0) {
                dataJSONObject.put("cheque_payment", attributesArray);
            } else {
                dataJSONObject.put("cheque_payment", "");
            }

            //TODO Invoice List Items
            JSONArray invoiceItemJsonArray = new JSONArray();
            double receivingAmount = 0.0;
            if (invoiceLists != null && invoiceLists.size() > 0) {
                for (int i = 0; i < invoiceLists.size(); i++) {
                    JSONObject invoiceItemJsonObject = new JSONObject();
                    invoiceItemJsonObject.put("invoice", invoiceLists.get(i).getInvid());
                    if (invoiceLists.get(i).getReceiveAmount() != null &&
                            !invoiceLists.get(i).getReceiveAmount().isEmpty() &&
                            invoiceLists.get(i).isSelectedInvoice()) {
                        invoiceItemJsonObject.put("invoice-paying-amount", invoiceLists.get(i).getReceiveAmount() + "");
                        receivingAmount += Constants.ParseDouble(invoiceLists.get(i).getReceiveAmount());
                    } else {
                        invoiceItemJsonObject.put("invoice-paying-amount", "0.00");
                    }
                    invoiceItemJsonArray.put(invoiceItemJsonObject);
                }
            }
           /* if (receivingAmount == 0) {
                Toast.makeText(CreateCollectionsInvoiceActivity.this,
                        "Please select atleast one invoice.", Toast.LENGTH_SHORT).show();
                hideLoader();
                return;
            }*/
            //TODO Add details of advance reference number and amount
            JSONObject advanceReferenceJsonObject = new JSONObject();
            advanceReferenceJsonObject.put("r-no",
                    advanceReferenceNumberTextInputEditText.getText().toString() + "");
            advanceReferenceJsonObject.put("r-amount",
                    advanceReferenceAmountTextInputEditText.getText().toString() + "");
            invoiceItemJsonArray.put(advanceReferenceJsonObject);

            //Array
            dataJSONObject.put("co", invoiceItemJsonArray);
            Log.d("JSON", dataJSONObject.toString());

            task = getString(R.string.collection_create);
            if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.sendJSONData(version, key, task, userId, accessToken, dataJSONObject);
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    //leadList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            Toast.makeText(CreateCollectionsInvoiceActivity.this,
                                    apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            //Enable Again
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    submitTextView.setEnabled(true);
                                }
                            }, 3500);


                            Intent intent = new Intent(CreateCollectionsInvoiceActivity.this,
                                    SuccessfulCollectionActivity.class);
                            intent.putExtra("amount", apiResponse.getData().getPayingAmount() + "");
                            intent.putExtra("txn_id", apiResponse.getData().getTransactionID() + "");
                            intent.putExtra("t_id", apiResponse.getData().getTid() + "");
                            intent.putExtra("transaction_time",
                                    apiResponse.getData().getTransactionTime() + "");
                            intent.putExtra("name", customerNameTitleTextView.getText().toString().trim());
                            startActivity(intent);
                            //finish();

                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(CreateCollectionsInvoiceActivity.this, apiResponse.getMessage());
                        } else {
                            Toast.makeText(CreateCollectionsInvoiceActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                            //Enable Again
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    submitTextView.setEnabled(true);
                                }
                            }, 3500);

                        }

                        hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                        //Enable Again
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                submitTextView.setEnabled(true);
                            }
                        }, 3500);

                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (CreateCollectionsInvoiceActivity.this != null) {
                        Toast.makeText(CreateCollectionsInvoiceActivity.this,
                                getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        hideLoader();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();

            //Enable Again
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    submitTextView.setEnabled(true);
                }
            }, 3500);

        }
    }
}
