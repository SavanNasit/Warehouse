package com.accrete.warehouse;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.accrete.warehouse.adapter.SelectOrderItemAdapter;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.SelectOrderItem;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/29/17.
 */

public class OrderItemActivity extends AppCompatActivity implements SelectOrderItemAdapter.SelectOrderItemsAdapterListener {
    public Toolbar toolbar;
    private RecyclerView ordersItemRecyclerView;
    private TextView orderItemEmptyView;
    private List<SelectOrderItem> selectOrderItemList;
    private SelectOrderItemAdapter selectOrderItemAdapter;
    private String chkoid;
    private String maximumQuantity;
    private List<PendingItems> pendingItemList = new ArrayList<>();
    private int position;
    private LinearLayout orderItemToAddPackage;

    private void findViews() {
        ordersItemRecyclerView = (RecyclerView) findViewById(R.id.orders_item_recycler_view);
        orderItemEmptyView = (TextView) findViewById(R.id.order_item_empty_view);
        orderItemToAddPackage =(LinearLayout)findViewById(R.id.order_item_add_packages);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        selectOrderItemAdapter = new SelectOrderItemAdapter(this, selectOrderItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        ordersItemRecyclerView.setLayoutManager(mLayoutManager);
        ordersItemRecyclerView.setHasFixedSize(true);
        ordersItemRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ordersItemRecyclerView.setNestedScrollingEnabled(false);
        ordersItemRecyclerView.setAdapter(selectOrderItemAdapter);

        toolbar.setTitle(getString(R.string.running_orders_execute_fragment) + " " + AppPreferences.getCompanyCode(getApplicationContext(), AppUtils.COMPANY_CODE) + chkoid);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (selectOrderItemList.size() > 0) {
            ordersItemRecyclerView.setVisibility(View.VISIBLE);
            orderItemEmptyView.setVisibility(View.GONE);
        } else {
            ordersItemRecyclerView.setVisibility(View.GONE);
            orderItemEmptyView.setVisibility(View.VISIBLE);
            orderItemEmptyView.setText(getString(R.string.no_data_available));
        }

        orderItemToAddPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  listener.onItemExecute(selectOrderItem.getInventoryName(),selectOrderItem.getAllocatedQuantity(),
                        holder.listRowOrderItemEdtAllotQuantity.getText().toString(),selectOrderItem.getUnit(),position);*/
              finish();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_item);
        selectOrderItemList = getIntent().getParcelableArrayListExtra("selectOrderItemList");
        pendingItemList = getIntent().getParcelableArrayListExtra("pendingItemsList");
        chkoid = getIntent().getStringExtra("chkoid");
         position = getIntent().getIntExtra("position",0);
       // maximumQuantity = getIntent().getStringExtra("quantity");
        Log.d("selectOrderList size", String.valueOf(selectOrderItemList.size()));
        findViews();
    }

    @Override
    public void onItemRowClicked(int position) {

    }

    @Override
    public void onItemExecute(String inventoryName, String allocatedQuantity, String quantity, String unit,int position) {

            if (quantity == null || quantity.isEmpty()) {
                Toast.makeText(this, "Please enter quantity", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(quantity) == 0) {
                Toast.makeText(this, "Quantity must be greater than 0", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(quantity) > Integer.parseInt(allocatedQuantity)) {
                Toast.makeText(this, "Low stock inventory", Toast.LENGTH_SHORT).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putParcelableArrayListExtra("selectOrderItem", (ArrayList<? extends Parcelable>) selectOrderItemList);
                resultIntent.putExtra("chkoid", chkoid);
                resultIntent.putExtra("pos", position);
                resultIntent.putExtra("qty",Integer.valueOf(quantity));
                resultIntent.putParcelableArrayListExtra("pendingItemsList", (ArrayList<? extends Parcelable>) pendingItemList);
                setResult(1001, resultIntent);

                //finish();
               // RunningOrdersExecuteFragment.viewPagerExecute.setCurrentItem(1);
            }
    }
}
