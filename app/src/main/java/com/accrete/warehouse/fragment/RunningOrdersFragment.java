package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.RunningOrders;
import com.accrete.warehouse.navigationView.RunningOrdersAdapter;


import java.util.ArrayList;
import java.util.List;


/**
 * Created by poonam on 11/24/17.
 */

public class RunningOrdersFragment extends Fragment implements RunningOrdersAdapter.RunningOrdersAdapterListener {
    private static final String KEY_TITLE = "RunningOrdersFragment";

    private SwipeRefreshLayout runningOrdersSwipeRefreshLayout;
    private RecyclerView runningOrdersRecyclerView;
    private TextView runningOrdersEmptyView;
    private RunningOrdersAdapter runningOrdersAdapter;
    private List<RunningOrders> runningOrdersList = new ArrayList<>();
    RunningOrders runningOrders = new RunningOrders();

    private void findViews(View rootview) {
        runningOrdersSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.running_orders_swipe_refresh_layout);
        runningOrdersRecyclerView = (RecyclerView) rootview.findViewById(R.id.running_orders_recycler_view);
        runningOrdersEmptyView = (TextView) rootview.findViewById(R.id.running_orders_empty_view);
        runningOrdersAdapter = new RunningOrdersAdapter(getActivity(), runningOrdersList,this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        runningOrdersRecyclerView.setLayoutManager(mLayoutManager);
        runningOrdersRecyclerView.setHasFixedSize(true);
        runningOrdersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        runningOrdersRecyclerView.setNestedScrollingEnabled(false);
        runningOrdersRecyclerView.setAdapter(runningOrdersAdapter);

        runningOrders.setOrderID("RPDORDG100138");
        runningOrders.setDate("27-11-2017");
        runningOrders.setCustomer("Ms Poonam Kukreti");
        runningOrders.setMobile("9590430437");

        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
        runningOrdersList.add(runningOrders);
    }

    public void getData(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
    }

    public static RunningOrdersFragment newInstance(String title) {
        RunningOrdersFragment f = new RunningOrdersFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_running_orders, container, false);
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.running_orders_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.running_orders_fragment));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.running_orders_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.running_orders_fragment));
        }
    }


    @Override
    public void onMessageRowClicked(int position) {
        runningOrdersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExecute() {
        RunningOrdersExecuteFragment fragment = new RunningOrdersExecuteFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.running_orders_container, fragment, getString(R.string.running_orders_execute_fragment));
        ft.addToBackStack(null).commit();
    }
}

