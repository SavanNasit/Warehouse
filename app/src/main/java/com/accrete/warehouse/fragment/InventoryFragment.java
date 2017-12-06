package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.InventoryAdapter;
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.Inventory;
import com.accrete.warehouse.model.ManageGatepass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class InventoryFragment extends Fragment implements InventoryAdapter.InventoryAdapterListener {
    private SwipeRefreshLayout inventorySwipeRefreshLayout;
    private RecyclerView inventoryRecyclerView;
    private TextView inventoryEmptyView;
    private InventoryAdapter inventoryAdapter;
    private List<Inventory> inventoryList = new ArrayList<>();
    private Inventory inventory = new Inventory();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        inventorySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.inventory_swipe_refresh_layout);
        inventoryRecyclerView = (RecyclerView)rootView.findViewById(R.id.inventory_recycler_view);
        inventoryEmptyView = (TextView)rootView.findViewById(R.id.inventory_empty_view);

        inventoryAdapter = new InventoryAdapter(getActivity(),inventoryList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        inventoryRecyclerView.setLayoutManager(mLayoutManager);
        inventoryRecyclerView.setHasFixedSize(true);
        inventoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        inventoryRecyclerView.setNestedScrollingEnabled(false);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        inventory.setInventoryID("RPDORDG100138");
        inventory.setItem("Wood");
        inventory.setSKUCode("44eddf34");
        inventory.setReceivedQuantity("32");
        inventory.setAvailableStock("2323");

        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);
        inventoryList.add(inventory);

    }


    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }
}