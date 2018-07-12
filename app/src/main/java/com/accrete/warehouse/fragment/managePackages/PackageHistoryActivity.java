package com.accrete.warehouse.fragment.managePackages;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PackageStatusAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageStatusList;
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

public class PackageHistoryActivity extends AppCompatActivity implements PackageStatusAdapter.PackageStatusAdapterListener {
    TextView packageHistoryId;
    TextView packageHistoryName;
    TextView packageHistoryInoviceNum;
    TextView packageHistoryDate,packageEmptyView;
    RecyclerView packageHistoryRecyclerView;
    private PackageStatusAdapter packageStatusAdapter;
    private List<PackageStatusList> packageStatusList = new ArrayList<>();
    private PackageStatusList packageStatus = new PackageStatusList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_history);
        findViews();
    }

    private void findViews() {
        packageHistoryId = (TextView) findViewById(R.id.package_history_id);
        packageHistoryName = (TextView) findViewById(R.id.package_history_poonam);
        packageHistoryInoviceNum = (TextView) findViewById(R.id.package_history_inovice_num);
        packageHistoryDate = (TextView) findViewById(R.id.package_history_date);
        packageHistoryRecyclerView = (RecyclerView) findViewById(R.id.package_history_recycler_view);
        packageEmptyView = (TextView)findViewById(R.id.package_history_empty_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.package_history));
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


        packageStatusAdapter = new PackageStatusAdapter(this, packageStatusList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        packageHistoryRecyclerView.setLayoutManager(mLayoutManager);
        packageHistoryRecyclerView.setHasFixedSize(true);
        packageHistoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packageHistoryRecyclerView.setNestedScrollingEnabled(false);
        packageHistoryRecyclerView.setAdapter(packageStatusAdapter);

        if (!NetworkUtil.getConnectivityStatusString(this).equals(getString(R.string.not_connected_to_internet))) {
            getPackageGatepassList(getIntent().getStringExtra("packageid") + "");
        } else {
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
/*
        packageStatus.setPackageStatus("Out For Delivery");
        packageStatus.setDate("November 28, 2017, 6:13 pm");
        packageStatus.setNarration("charger got damaged");
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);
        packageStatusList.add(packageStatus);*/

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }


    private void getPackageGatepassList(String pacid) {
        task = getString(R.string.package_history_gatepass_list_task);
        String chkid = null;
        if (packageStatusList != null && packageStatusList.size() > 0) {
            packageStatusList.clear();
        }

        if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageHistoryInGatepass(version, key, task, userId, accessToken, pacid);
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
                        packageHistoryRecyclerView.setVisibility(View.VISIBLE);
                        packageEmptyView.setVisibility(View.GONE);

                        if(apiResponse.getData().getPackageData().getHistoryData()!=null && apiResponse.getData().getPackageData().getHistoryData().size()>0){
                            for (PackageStatusList packageStatusLists : apiResponse.getData().getPackageData().getHistoryData()){
                                packageStatusList.add(packageStatusLists);
                            }
                        }else {
                            packageHistoryRecyclerView.setVisibility(View.GONE);
                            packageEmptyView.setVisibility(View.VISIBLE);
                        }
                            packageHistoryDate.setText(apiResponse.getData().getPackageData().getInvoiceDate());
                            packageHistoryId.setText(apiResponse.getData().getPackageData().getPackageId());
                            packageHistoryName.setText(apiResponse.getData().getPackageData().getCustomerName());
                            packageHistoryInoviceNum.setText(apiResponse.getData().getPackageData().getInvoiceNo());
                            packageStatusAdapter.notifyDataSetChanged();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            Toast.makeText(PackageHistoryActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PackageHistoryActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("packageHistory", t.getMessage());
            }
        });
    }
}
