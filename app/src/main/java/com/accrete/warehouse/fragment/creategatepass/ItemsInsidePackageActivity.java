package com.accrete.warehouse.fragment.creategatepass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ItemsInsidePackageAdapter;
import com.accrete.warehouse.model.ItemsInsidePackage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ItemsInsidePackageActivity extends AppCompatActivity implements ItemsInsidePackageAdapter.ItemsInsidePackageAdapterListener {

    private ProgressBar itemsInsidePackageProgressBar;
    private RecyclerView itemsInsidePackageRecyclerView;
    private ItemsInsidePackageAdapter itemsInsidePackageAdapter;
    private List<ItemsInsidePackage> itemsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_inside_package);
        findViews();
    }

    private void findViews() {
        itemsInsidePackageProgressBar = (ProgressBar) findViewById(R.id.items_inside_package_progress_bar);
        itemsInsidePackageRecyclerView = (RecyclerView) findViewById(R.id.items_inside_package_recycler_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.items_inside_package));
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


        itemsList = getIntent().getParcelableArrayListExtra("itemsList");

        itemsInsidePackageAdapter = new ItemsInsidePackageAdapter(this, itemsList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        itemsInsidePackageRecyclerView.setLayoutManager(mLayoutManager);
        itemsInsidePackageRecyclerView.setHasFixedSize(true);
        itemsInsidePackageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        itemsInsidePackageRecyclerView.setNestedScrollingEnabled(false);
        itemsInsidePackageRecyclerView.setAdapter(itemsInsidePackageAdapter);


/*
        itemsInsidePackage.setQuantity("1 Piece");
        itemsInsidePackage.setItemName("100 Pipers (100 Pipers)");
        itemsInsidePackage.setDescription("10 pipes got damaged");

        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);
        itemsList.add(itemsInsidePackage);*/

    }


    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }

}
