package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.CustomerInfo;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/4/17.
 */

public class CustomerDetailsActivity extends AppCompatActivity {
    CustomerInfo customerInfo = new CustomerInfo();
    private Toolbar toolbar;
    private TextView customerDetailsName;
    private TextView customerDetailsEmail;
    private TextView customerDetailsMobile;
    private TextView customerDetailsShippingAddress;
    private TextView customerDetailsBillingAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);
        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.customer_details));
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

        findViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (getIntent().hasExtra("customerInfo")) {
                customerInfo = extras.getParcelable("customerInfo");
            } else if (getIntent().hasExtra(getString(R.string.pacId))) {
                if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
                    getCustomerInfo(getIntent().getStringExtra(getString(R.string.pacId)));
                } else {
                    Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        }

        setDataIntoView(customerInfo);
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        customerDetailsName = (TextView) findViewById(R.id.customer_details_name);
        customerDetailsEmail = (TextView) findViewById(R.id.customer_details_email);
        customerDetailsMobile = (TextView) findViewById(R.id.customer_details_mobile);
        customerDetailsShippingAddress = (TextView) findViewById(R.id.customer_details_shipping_address);
        customerDetailsBillingAddress = (TextView) findViewById(R.id.customer_details_billing_address);
    }

    private void getCustomerInfo(String pacId) {
        task = getString(R.string.customer_info_gatepass_task);
        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getCustomerInfoByPacId(version, key, task, userId, accessToken, pacId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        CustomerInfo customerInfo = apiResponse.getData().getCustomerInfo();
                        setDataIntoView(customerInfo);
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        Toast.makeText(getApplicationContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setDataIntoView(CustomerInfo customerInfo) {
        if (customerInfo.getName() != null && !customerInfo.getName().isEmpty()) {
            customerDetailsName.setText(customerInfo.getName());
        }
        if (customerInfo.getMobile() != null && !customerInfo.getMobile().isEmpty()) {
            customerDetailsMobile.setText(customerInfo.getMobile());
        }
        if (customerInfo.getEmail() != null && !customerInfo.getEmail().isEmpty()) {
            customerDetailsEmail.setText(customerInfo.getEmail());
        }

        customerDetailsShippingAddress.setText(customerInfo.getShippingAddrName() + ",\n" + customerInfo.getShippingAddrSitename()+ ",\n" + customerInfo.getShippingAddrLine() + ",\n" +
                customerInfo.getShippingAddrCity() + ",\n" + customerInfo.getShippingAddrStateName() + ",\n" + customerInfo.getShippingAddrCountryName() + ",\n"
                + customerInfo.getShippingAddrPincode());
        customerDetailsBillingAddress.setText(customerInfo.getBillingAddrName() + ",\n" + customerInfo.getBillingAddSitename() + ",\n" + customerInfo.getBillingAddrLine() + ",\n" +
                customerInfo.getBillingAddrCity() + ",\n" + customerInfo.getBillingAddrStateName() + ",\n" + customerInfo.getBillingAddrCountryName() + ",\n"
                + customerInfo.getBillingAddrPincode());
    }
}
