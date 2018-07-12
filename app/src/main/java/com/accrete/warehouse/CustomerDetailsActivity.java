package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
    private CardView cardViewBillingAddress, cardViewCurrentAddress;

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
        cardViewBillingAddress = (CardView) findViewById(R.id.card_view_billing_address);
        cardViewCurrentAddress = (CardView) findViewById(R.id.card_view_inner_quot_options);
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
            customerDetailsName.setVisibility(View.VISIBLE);
        } else {
            customerDetailsName.setVisibility(View.GONE);
        }
        if (customerInfo.getMobile() != null && !customerInfo.getMobile().isEmpty()) {
            customerDetailsMobile.setText(customerInfo.getMobile());
            customerDetailsMobile.setVisibility(View.VISIBLE);
        } else {
            customerDetailsMobile.setVisibility(View.GONE);

        }
        if (customerInfo.getEmail() != null && !customerInfo.getEmail().isEmpty()) {
            customerDetailsEmail.setText(customerInfo.getEmail());
            customerDetailsEmail.setVisibility(View.VISIBLE);
        } else {
            customerDetailsEmail.setVisibility(View.GONE);
        }

        //Shipping Address
        if ((customerInfo.getShippingAddrSitename() != null && !customerInfo.getShippingAddrSitename().isEmpty())
                || (customerInfo.getShippingAddrLine() != null && !customerInfo.getShippingAddrLine().isEmpty())
                || (customerInfo.getShippingAddrCity() != null && !customerInfo.getShippingAddrCity().isEmpty())
                || (customerInfo.getShippingAddrPincode() != null && !customerInfo.getShippingAddrPincode().isEmpty())
                || (customerInfo.getShippingAddrCountryName() != null && !customerInfo.getShippingAddrCountryName().isEmpty())) {

            String currentAddress = "";

            //Address Site Name
            if (customerInfo.getShippingAddrSitename() != null && !customerInfo.getShippingAddrSitename().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getShippingAddrSitename() + ", ";
            }

            //Address Line 1
            if (customerInfo.getShippingAddrLine() != null && !customerInfo.getShippingAddrLine().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getShippingAddrLine() + ", ";
            }

            //City & Zip Code
            if (customerInfo.getShippingAddrCity() != null &&
                    !customerInfo.getShippingAddrCity().toString().trim().isEmpty() &&
                    customerInfo.getShippingAddrPincode() != null &&
                    !customerInfo.getShippingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getShippingAddrCity().toString().trim() + " - " +
                        customerInfo.getShippingAddrPincode()
                                .toString().trim() + "," + "\n";
            } else if (customerInfo.getShippingAddrCity() != null &&
                    !customerInfo.getShippingAddrCity().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getShippingAddrCity().toString().trim() + ", ";
            } else if (customerInfo.getShippingAddrPincode() != null &&
                    !customerInfo.getShippingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getShippingAddrPincode()
                                .toString().trim() + ", ";
            }
            //State
            if (customerInfo.getShippingAddrStateName() != null &&
                    !customerInfo.getShippingAddrStateName().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getShippingAddrStateName() + ", ";
            }

            //Country
            if (customerInfo.getShippingAddrCountryName() != null &&
                    !customerInfo.getShippingAddrCountryName().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getShippingAddrCountryName() + " ";
            }

            customerDetailsShippingAddress.setText(currentAddress.trim() + "");

            if (currentAddress.equals("")) {
                cardViewCurrentAddress.setVisibility(View.GONE);
            } else {
                cardViewCurrentAddress.setVisibility(View.VISIBLE);
            }
        }

        //Billing Address
        if ((customerInfo.getBillingAddSitename() != null && !customerInfo.getBillingAddSitename().isEmpty())
                || (customerInfo.getBillingAddrLine() != null && !customerInfo.getBillingAddrLine().isEmpty())
                || (customerInfo.getBillingAddrCity() != null && !customerInfo.getBillingAddrCity().isEmpty())
                || (customerInfo.getBillingAddrPincode() != null && !customerInfo.getBillingAddrPincode().isEmpty())
                || (customerInfo.getBillingAddrCountryName() != null && !customerInfo.getBillingAddrCountryName().isEmpty())) {

            String currentAddress = "";

            //Address Site Name
            if (customerInfo.getBillingAddSitename() != null && !customerInfo.getBillingAddSitename().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getBillingAddSitename() + ", ";
            }

            //Address Line 1
            if (customerInfo.getBillingAddrLine() != null && !customerInfo.getBillingAddrLine().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getBillingAddrLine() + ", ";
            }

            //City & Zip Code
            if (customerInfo.getBillingAddrCity() != null &&
                    !customerInfo.getBillingAddrCity().toString().trim().isEmpty() &&
                    customerInfo.getBillingAddrPincode() != null &&
                    !customerInfo.getBillingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getBillingAddrCity().toString().trim() + " - " +
                        customerInfo.getBillingAddrPincode()
                                .toString().trim() + "," + "\n";
            } else if (customerInfo.getBillingAddrCity() != null &&
                    !customerInfo.getBillingAddrCity().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getBillingAddrCity().toString().trim() + ", ";
            } else if (customerInfo.getBillingAddrPincode() != null &&
                    !customerInfo.getBillingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        customerInfo.getBillingAddrPincode()
                                .toString().trim() + ", ";
            }
            //State
            if (customerInfo.getBillingAddrStateName() != null &&
                    !customerInfo.getBillingAddrStateName().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getBillingAddrStateName() + ", ";
            }

            //Country
            if (customerInfo.getBillingAddrCountryName() != null &&
                    !customerInfo.getBillingAddrCountryName().isEmpty()) {
                currentAddress = currentAddress + customerInfo.getBillingAddrCountryName() + " ";
            }
            customerDetailsBillingAddress.setText(currentAddress + "");

            if (currentAddress.equals("")) {
                cardViewBillingAddress.setVisibility(View.GONE);
            } else {
                cardViewBillingAddress.setVisibility(View.VISIBLE);
            }
        }
      /* *//* customerDetailsShippingAddress.setText(customerInfo.getShippingAddrSitename() + ",\n" + customerInfo.getShippingAddrLine() + ",\n" +
                customerInfo.getShippingAddrCity() + ",\n" + customerInfo.getShippingAddrStateName() + ",\n" + customerInfo.getShippingAddrCountryName() + ",\n"
                + customerInfo.getShippingAddrPincode());*//*
        customerDetailsBillingAddress.setText(customerInfo.getBillingAddSitename() + ",\n" + customerInfo.getBillingAddrLine() + ",\n" +
                customerInfo.getBillingAddrCity() + ",\n" + customerInfo.getBillingAddrStateName() + ",\n" + customerInfo.getBillingAddrCountryName() + ",\n"
                + customerInfo.getBillingAddrPincode());*/
    }
}
