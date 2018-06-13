package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AllDatePickerFragment;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.PassDateToCounsellor;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

public class EditPackageActivity extends AppCompatActivity implements PassDateToCounsellor {
    public Toolbar toolbar;
    private TextInputLayout invoiceSerialNoTextInputLayout;
    private TextInputEditText editPackageTaxInvoiceSerialNo;
    private TextInputEditText editPackageInvoiceDate;
    private TextInputEditText editPackageEWayNumber;
    private TextView editPackageEvent;
    private AllDatePickerFragment datePickerFragment;
    private String stringDateOfInvoice;
    private String pacid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_package);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        invoiceSerialNoTextInputLayout = (TextInputLayout) findViewById(R.id.invoice_serial_no_textInputLayout);
        editPackageTaxInvoiceSerialNo = (TextInputEditText) findViewById(R.id.edit_package_tax_invoice_serial_no);
        editPackageInvoiceDate = (TextInputEditText) findViewById(R.id.edit_package_invoice_date);
        editPackageEWayNumber = (TextInputEditText) findViewById(R.id.edit_package_e_way_number);
        editPackageEvent = (TextView) findViewById(R.id.package_details_edit_package);

        String packageId = getIntent().getStringExtra("packageId");
        pacid = getIntent().getStringExtra("pacid");
        toolbar.setTitle(getString(R.string.edit_package) + " " + packageId);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);

        invoiceSerialNoTextInputLayout.setHint("Tax Invoice Serial No.: ");
        editPackageTaxInvoiceSerialNo.setEnabled(false);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        datePickerFragment = new AllDatePickerFragment();
        datePickerFragment.setListener(this);

        editPackageInvoiceDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getSupportFragmentManager(), getString(R.string.dialog_from));
            }
        });

        fetchInfoForEditPackage(pacid);

        editPackageEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editPackage();
            }
        });
    }

    private void editPackage() {

        String task = getString(R.string.edit_packages_task);

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        if (stringDateOfInvoice == null || stringDateOfInvoice.equals("")) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date startDate = formatter.parse(editPackageInvoiceDate.getText().toString());
                stringDateOfInvoice = targetFormat.format(startDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.editPackage(version, key, task, userId, accessToken, pacid, editPackageEWayNumber.getText().toString(), stringDateOfInvoice, editPackageTaxInvoiceSerialNo.getText().toString());
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(EditPackageActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            Toast.makeText(EditPackageActivity.this, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditPackageActivity.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    private void fetchInfoForEditPackage(String pacid) {
        String task = getString(R.string.fetch_info_for_edit_packages_task);

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.editInfoPackage(version, key, task, userId, accessToken, pacid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(EditPackageActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                            editPackageTaxInvoiceSerialNo.setText(apiResponse.getData().getEditPackageFormdata().getInvoiceNumber());
                            editPackageEWayNumber.setText(apiResponse.getData().getEditPackageFormdata().getEwayNo());
                            editPackageInvoiceDate.setText(apiResponse.getData().getEditPackageFormdata().getInvoiceDate());

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                            DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                Date startDate = formatter.parse(apiResponse.getData().getEditPackageFormdata().getInvoiceDate());
                                String stringDateOfInvoice = targetFormat.format(startDate);
                                editPackageInvoiceDate.setText(stringDateOfInvoice);

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            invoiceSerialNoTextInputLayout.setHint("Tax Invoice Serial No.: " +
                                    apiResponse.getData().getEditPackageFormdata().getInvoiceNumberLabel());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditPackageActivity.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void passDate(String s) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            stringDateOfInvoice = s;
            Date startDate = formatter.parse(stringDateOfInvoice);
            stringDateOfInvoice = targetFormat.format(startDate);
            editPackageInvoiceDate.setText(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void passTime(String s) {

    }
}
