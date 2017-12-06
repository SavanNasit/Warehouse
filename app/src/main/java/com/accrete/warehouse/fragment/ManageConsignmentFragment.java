package com.accrete.warehouse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.ViewConsignmentActivity;
import com.accrete.warehouse.adapter.ManageConsignmentAdapter;
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.ManageConsignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentFragment extends Fragment implements ManageConsignmentAdapter.ManageConsignmentAdapterListener {

    private static final String KEY_TITLE = "ManageConsignment";

    private SwipeRefreshLayout manageConsignmentSwipeRefreshLayout;
    private RecyclerView manageConsignmentRecyclerView;
    private TextView manageConsignmentEmptyView;
    private ManageConsignmentAdapter manageConsignmentAdapter;
    private List<ManageConsignment> manageConsignmentList = new ArrayList<>();
    private ManageConsignment manageConsignment = new ManageConsignment();

    public static ManageConsignmentFragment newInstance(String title) {
        ManageConsignmentFragment f = new ManageConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        manageConsignmentSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_consignment_swipe_refresh_layout);
        manageConsignmentRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_consignment_recycler_view);
        manageConsignmentEmptyView = (TextView) rootView.findViewById(R.id.manage_consignment_empty_view);

        manageConsignmentAdapter = new ManageConsignmentAdapter(getActivity(), manageConsignmentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        manageConsignmentRecyclerView.setLayoutManager(mLayoutManager);
        manageConsignmentRecyclerView.setHasFixedSize(true);
        manageConsignmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageConsignmentRecyclerView.setNestedScrollingEnabled(false);
        manageConsignmentRecyclerView.setAdapter(manageConsignmentAdapter);



        manageConsignment.setConsignmentID("RPDCNS000295");
        manageConsignment.setPurchaseOrder("NA");
        manageConsignment.setInvoiceNumber("Ms Poonam Kukreti");
        manageConsignment.setInvoiceDate("18 Sep, 2017");
        manageConsignment.setPurchaseOrderDate("04 Dec, 2017");
        manageConsignment.setVendor("rahul goyal");
        manageConsignment.setWarehouse("BTM WAREHOUSE");
        manageConsignment.setReceivedOn("04 Dec, 2017");
        manageConsignment.setStatus("Freezed");

        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);
        manageConsignmentList.add(manageConsignment);


    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_consignment_fragment));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intentView = new Intent(getActivity(),ViewConsignmentActivity.class);
        startActivity(intentView);
    }

    @Override
    public void onExecute() {

    }
}
