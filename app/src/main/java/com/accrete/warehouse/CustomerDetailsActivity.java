package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.model.CustomerInfo;

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


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            customerInfo = extras.getParcelable("customerInfo");
            //Toast.makeText(this, customerInfo.getMobile(), Toast.LENGTH_SHORT).show();
        }
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        customerDetailsName = (TextView) findViewById(R.id.customer_details_name);
        customerDetailsEmail = (TextView) findViewById(R.id.customer_details_email);
        customerDetailsMobile = (TextView) findViewById(R.id.customer_details_mobile);
        customerDetailsShippingAddress = (TextView) findViewById(R.id.customer_details_shipping_address);
        customerDetailsBillingAddress = (TextView) findViewById(R.id.customer_details_billing_address);

        customerDetailsName.setText(customerInfo.getName());
        customerDetailsMobile.setText(customerInfo.getMobile());
        customerDetailsEmail.setText(customerInfo.getEmail());

        customerDetailsShippingAddress.setText(customerInfo.getShippingAddrName() + ",\n" + customerInfo.getShippingAddrLine() + ",\n" +
                customerInfo.getShippingAddrCity() + ",\n" + customerInfo.getShippingAddrStateName() + ",\n" + customerInfo.getShippingAddrCountryName() + ",\n"
                + customerInfo.getShippingAddrPincode());
        customerDetailsBillingAddress.setText(customerInfo.getBillingAddrName() + ",\n" + customerInfo.getBillingAddrLine() + ",\n" +
                customerInfo.getBillingAddrCity() + ",\n" + customerInfo.getBillingAddrStateName() + ",\n" + customerInfo.getBillingAddrCountryName() + ",\n"
                + customerInfo.getBillingAddrPincode());
    }
}
