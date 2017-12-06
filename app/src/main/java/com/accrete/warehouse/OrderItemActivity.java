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

import com.accrete.warehouse.adapter.SelectOrderItemAdapter;
import com.accrete.warehouse.model.SelectOrderItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/29/17.
 */

public class OrderItemActivity extends AppCompatActivity implements SelectOrderItemAdapter.SelectOrderItemsAdapterListener {
    public Toolbar toolbar;
    SelectOrderItem selectOrderItem = new SelectOrderItem();
    private RecyclerView ordersItemRecyclerView;
    private TextView orderItemEmptyView;
    private List<SelectOrderItem> selectOrderItemList = new ArrayList<>();
    private SelectOrderItemAdapter selectOrderItemAdapter;

    private void findViews() {
        ordersItemRecyclerView = (RecyclerView) findViewById(R.id.orders_item_recycler_view);
        orderItemEmptyView = (TextView) findViewById(R.id.order_item_empty_view);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        selectOrderItemAdapter = new SelectOrderItemAdapter(this, selectOrderItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ordersItemRecyclerView.setLayoutManager(mLayoutManager);
        ordersItemRecyclerView.setHasFixedSize(true);
        ordersItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ordersItemRecyclerView.setNestedScrollingEnabled(false);
        ordersItemRecyclerView.setAdapter(selectOrderItemAdapter);

        toolbar.setTitle(getString(R.string.order_item));
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

        selectOrderItem.setInventory("12344");
        selectOrderItem.setVendor("Ms Poonam Kukreti");
        selectOrderItem.setPurchasedOn("08 Nov, 2017");
        selectOrderItem.setRemark("very low quality");
        selectOrderItem.setAvailableQuantity("5");
        selectOrderItem.setAllotQuantity("2");
        selectOrderItem.setUnit("Pcs");

        selectOrderItemList.add(selectOrderItem);
        selectOrderItemList.add(selectOrderItem);
        selectOrderItemList.add(selectOrderItem);
        selectOrderItemList.add(selectOrderItem);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);
        findViews();

    }

    @Override
    public void onItemRowClicked(int position) {

    }

    @Override
    public void onItemExecute() {
         finish();
    }
}
