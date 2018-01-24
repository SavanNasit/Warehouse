package com.accrete.warehouse;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.adapter.ChangeStatusAttemptFailedAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.CustomerInfo;
import com.accrete.warehouse.model.LogHistoryDatum;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

public class ChangePackageAttemptFailedStatusActivity extends AppCompatActivity {
    private LinearLayout packageIdLayout;
    private TextView pkgIdTextView;
    private LinearLayout customerNameLayout;
    private TextView customerNameTextView;
    private LinearLayout invoiceNoLayout;
    private TextView invoiceNoTextView;
    private LinearLayout invoiceDateLayout;
    private TextView invoiceDateTextView;
    private RecyclerView recyclerView;
    private TextInputEditText narrationEditText;
    private TextView reAttemptTextView;
    private TextView deliveryFailedTextView;
    private ChangeStatusAttemptFailedAdapter mAdapter;
    private List<LogHistoryDatum> logHistoryList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String pacdelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_package_attempt_failed_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Change Package Status");
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

        packageIdLayout = (LinearLayout) findViewById(R.id.packageId_layout);
        pkgIdTextView = (TextView) findViewById(R.id.pkgId_textView);
        customerNameLayout = (LinearLayout) findViewById(R.id.customerName_layout);
        customerNameTextView = (TextView) findViewById(R.id.customerName_textView);
        invoiceNoLayout = (LinearLayout) findViewById(R.id.invoiceNo_layout);
        invoiceNoTextView = (TextView) findViewById(R.id.invoiceNo_textView);
        invoiceDateLayout = (LinearLayout) findViewById(R.id.invoiceDate_layout);
        invoiceDateTextView = (TextView) findViewById(R.id.invoiceDate_textView);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        narrationEditText = (TextInputEditText) findViewById(R.id.narration_editText);
        reAttemptTextView = (TextView) findViewById(R.id.reAttempt_textView);
        deliveryFailedTextView = (TextView) findViewById(R.id.deliveryFailed_textView);

        mAdapter = new ChangeStatusAttemptFailedAdapter(this, logHistoryList);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(mAdapter);


        if (getIntent() != null && getIntent().hasExtra(getString(R.string.pacdelgatpacid))) {
            pacdelId = getIntent().getStringExtra(getString(R.string.pacdelgatpacid));
            if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                getPrefilledData(pacdelId);
            } else {
                Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }


        reAttemptTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(ChangePackageAttemptFailedStatusActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    reAttemptTask(pacdelId,
                            narrationEditText.getText().toString().trim() + "");
                } else {
                    Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        deliveryFailedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(ChangePackageAttemptFailedStatusActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                    deliveryFailedTask(pacdelId,
                            narrationEditText.getText().toString().trim() + "");
                } else {
                    Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getPrefilledData(String pacid) {
        task = getString(R.string.fetch_package_status_form_data_task);

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getAttemptFailedPackageStatus(version, key, task, userId, accessToken, pacid, "2");
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (LogHistoryDatum logHistoryDatum : apiResponse.getData().getLogHistoryData()) {
                            logHistoryList.add(logHistoryDatum);
                        }
                        mAdapter.notifyDataSetChanged();
                        setData(apiResponse.getData().getCustomerInfo());

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("packageOrderStatus", t.getMessage());
            }
        });
    }

    public void setData(CustomerInfo customerInfo) {
        if (customerInfo.getName() != null && !customerInfo.getName().isEmpty()) {
            customerNameTextView.setText(customerInfo.getName());
        }
        if (customerInfo.getInvoiceDate() != null && !customerInfo.getInvoiceDate().isEmpty()) {
            invoiceDateTextView.setText(customerInfo.getInvoiceDate());
        }
        if (customerInfo.getInvoiceNo() != null && !customerInfo.getInvoiceNo().isEmpty()) {
            invoiceNoTextView.setText(customerInfo.getInvoiceNo());
        }
        if (customerInfo.getPackageId() != null && !customerInfo.getPackageId().isEmpty()) {
            pkgIdTextView.setText(customerInfo.getPackageId());
            pkgIdTextView.setPaintFlags(pkgIdTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    private void reAttemptTask(String pacid, String narration) {
        task = getString(R.string.reattempt_task);

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.reAttemptDeliveryFailedTask(version, key, task, userId, accessToken, pacid,
                narration);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Intent resultIntent = new Intent();
                        setResult(456, resultIntent);
                        finish();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("packageOrderStatus", t.getMessage());
            }
        });
    }


    private void deliveryFailedTask(String pacid, String narration) {
        task = getString(R.string.delivery_failed_task);

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.reAttemptDeliveryFailedTask(version, key, task, userId, accessToken, pacid,
                narration);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Intent resultIntent = new Intent();
                        setResult(456, resultIntent);
                        finish();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChangePackageAttemptFailedStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("packageOrderStatus", t.getMessage());
            }
        });
    }

}
