package com.accrete.warehouse;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.accrete.warehouse.adapter.PackageStatusAdapter;
import com.accrete.warehouse.model.PackageStatusList;

import java.util.ArrayList;
import java.util.List;

public class PackageHistoryActivity extends AppCompatActivity implements PackageStatusAdapter.PackageStatusAdapterListener {
    TextView packageHistoryId;
    TextView packageHistoryPoonam;
    TextView packageHistoryInoviceNum;
    TextView packageHistoryDate;
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
        packageHistoryPoonam = (TextView) findViewById(R.id.package_history_poonam);
        packageHistoryInoviceNum = (TextView) findViewById(R.id.package_history_inovice_num);
        packageHistoryDate = (TextView) findViewById(R.id.package_history_date);
        packageHistoryRecyclerView = (RecyclerView) findViewById(R.id.package_history_recycler_view);
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
        packageStatusList.add(packageStatus);

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }
}
