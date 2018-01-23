package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.CustomerInfo;
import com.accrete.warehouse.model.Status;
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

/**
 * Created by poonam on 12/5/17.
 */

public class ChangePackageStatusActivity extends AppCompatActivity {
    private List<Status> statusArrayList = new ArrayList<>();
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView mobileTextView;
    private Spinner statusSpinner;
    private TextInputEditText narrationEditText;
    private TextView submitTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_order_status);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.package_order_status));
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

        nameTextView = (TextView) findViewById(R.id.name_textView);
        emailTextView = (TextView) findViewById(R.id.email_textView);
        mobileTextView = (TextView) findViewById(R.id.mobile_textView);
        statusSpinner = (Spinner) findViewById(R.id.status_spinner);
        narrationEditText = (TextInputEditText) findViewById(R.id.narration_editText);
        submitTextView = (TextView) findViewById(R.id.submit_textView);

        if (getIntent() != null && getIntent().hasExtra(getString(R.string.pacdelgatpacid))) {
            if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                getPrefilledData(getIntent().getStringExtra(getString(R.string.pacdelgatpacid)));
            } else {
                Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }

        submitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        Call<ApiResponse> call = apiService.getPackageStatus(version, key, task, userId, accessToken, pacid);
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

                        for (Status status : apiResponse.getData().getStatuses()) {
                            statusArrayList.add(status);
                        }

                        setData(apiResponse.getData().getCustomerInfo());

                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(ChangePackageStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ChangePackageStatusActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
        //Status Adapter
        ArrayAdapter<Status> statusArrayAdapter =
                new ArrayAdapter<Status>(this, R.layout.simple_spinner_item, statusArrayList);
        statusArrayAdapter.setDropDownViewResource(R.layout.spinner_common_item);
        statusSpinner.setAdapter(statusArrayAdapter);

        if (customerInfo.getName() != null && !customerInfo.getName().isEmpty()) {
            nameTextView.setText(customerInfo.getName());
        }
        if (customerInfo.getMobile() != null && !customerInfo.getMobile().isEmpty()) {
            mobileTextView.setText(customerInfo.getMobile());
        }
        if (customerInfo.getEmail() != null && !customerInfo.getEmail().isEmpty()) {
            emailTextView.setText(customerInfo.getEmail());
        }
    }
}
