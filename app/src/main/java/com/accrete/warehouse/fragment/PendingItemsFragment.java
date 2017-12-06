package com.accrete.warehouse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.accrete.warehouse.OrderItemActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.ScannerActivity;
import com.accrete.warehouse.adapter.PendingItemsAdapter;
import com.accrete.warehouse.model.PendingItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/28/17.
 */

public class PendingItemsFragment extends Fragment implements PendingItemsAdapter.PendingItemsAdapterListener, View.OnClickListener{
    PendingItems pendingItems = new PendingItems();
    private EditText pendingItemsEdtScan;
    private ImageView pendingItemsImgScan;
    private SwipeRefreshLayout pendingItemsSwipeRefreshLayout;
    private RecyclerView pendingItemsRecyclerView;
    private TextView pendingItemsEmptyView;
    private PendingItemsAdapter pendingItemsAdapter;
    private List<PendingItems> pendingItemList = new ArrayList<>();

    public void getData(String str) {
        // Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
        pendingItemsEdtScan.setText(str);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_items, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        pendingItemsEdtScan = (EditText) rootView.findViewById(R.id.pending_items_edt_scan);
        pendingItemsImgScan = (ImageView) rootView.findViewById(R.id.pending_items_img_scan);
        pendingItemsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.pending_items_swipe_refresh_layout);
        pendingItemsRecyclerView = (RecyclerView) rootView.findViewById(R.id.pending_items_recycler_view);
        pendingItemsEmptyView = (TextView) rootView.findViewById(R.id.pending_items_empty_view);


        pendingItemsAdapter = new PendingItemsAdapter(getActivity(), pendingItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        pendingItemsRecyclerView.setLayoutManager(mLayoutManager);
        pendingItemsRecyclerView.setHasFixedSize(true);
        pendingItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingItemsRecyclerView.setNestedScrollingEnabled(false);
        pendingItemsRecyclerView.setAdapter(pendingItemsAdapter);

        pendingItems.setItem("50 Shades of Colour");
        pendingItems.setSKUCode("1343434");
        pendingItems.setBatchNumber("1853");
        pendingItems.setQuantity("2 pieces");
        pendingItems.setStatus("Partial Executed");

        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);
        pendingItemList.add(pendingItems);

        pendingItemsImgScan.setOnClickListener(this);
    }

    @Override
    public void onMessageRowClicked(int position) {

    }


    @Override
    public void onExecute() {
        Intent intentOrderItem = new Intent(getActivity(),OrderItemActivity.class);
        startActivity(intentOrderItem);
    }

    @Override
    public void onClick(View v) {
        scanBarcode();
    }

    private void scanBarcode() {
        Intent intentScan = new Intent(getActivity(), ScannerActivity.class);
        startActivityForResult(intentScan, 1000);
    }
}
