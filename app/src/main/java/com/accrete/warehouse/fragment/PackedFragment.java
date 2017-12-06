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
import com.accrete.warehouse.adapter.PackedAdapter;
import com.accrete.warehouse.model.Packed;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/30/17.
 */

public class PackedFragment extends Fragment implements PackedAdapter.PackedAdapterListener {

    private SwipeRefreshLayout packedSwipeRefreshLayout;
    private RecyclerView packedRecyclerView;
    private TextView packedEmptyView;
    private PackedAdapter packedAdapter;
    private List<Packed> packedList = new ArrayList<>();
    private Packed packed = new Packed();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_packed, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packedSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.packed_swipe_refresh_layout);
        packedRecyclerView = (RecyclerView) rootView.findViewById(R.id.packed_recycler_view);
        packedEmptyView = (TextView) rootView.findViewById(R.id.packed_empty_view);

        packedAdapter = new PackedAdapter(getActivity(), packedList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packedRecyclerView.setLayoutManager(mLayoutManager);
        packedRecyclerView.setHasFixedSize(true);
        packedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedRecyclerView.setNestedScrollingEnabled(false);
        packedRecyclerView.setAdapter(packedAdapter);

        packed.setPackageID("RPDORDG100138");
        packed.setOrderId("Wood");
        packed.setInvoiceNumber("44eddf34");
        packed.setInvoiceDate("4,December,2017");
        packed.setCustomerName("Poonam Kukreti");
        packed.setExpDOD("32");
        packed.setPincode("2323");

        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);
        packedList.add(packed);


    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }
}
