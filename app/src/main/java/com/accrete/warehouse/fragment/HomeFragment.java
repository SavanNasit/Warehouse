package com.accrete.warehouse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.CreateGatepassActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.navigationView.DrawerActivity;
import com.accrete.warehouse.navigationView.DrawerInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.mikepenz.materialdrawer.Drawer;

import static com.accrete.warehouse.navigationView.DrawerActivity.drawer;


/**
 * Created by poonam on 11/24/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerInterface {
    private static final String KEY_TITLE = "HomeFragment";
    Drawer drawerSelection;
    private TextView textViewWarehouseTitle;
    private String warehouseName;
    private DrawerActivity drawerActivity;
    private LinearLayout drawerLinearLayoutMain;
    private TextView activityDrawerWarehouseName;
    private LinearLayout homeRunningOrdersLayout;
    private LinearLayout homeManagePackagesLayout;
    private LinearLayout homeManageGatePassLayout;
    private LinearLayout homeManageConsignmentLayout;
    private LinearLayout homeReceiveConsignmentLayout;
    private LinearLayout homeCreateGatepassLayout;
    private TextView homeRunningOrdersCount;
    private TextView homeManagePackagesCount;
    private TextView homeManageGatePassCount;
    private TextView homeManageConsignmentCount;
    private TextView homeReceiveConsignmentCount;


    public static HomeFragment newInstance(String title) {
        HomeFragment f = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        initializeView(rootView);
        if (AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME) != null &&
                !AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME).isEmpty()) {
            textViewWarehouseTitle.setText(AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME));
        }

        ((DrawerActivity) getActivity()).setFragmentRefreshListener(new DrawerActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh(String warehouseName) {
                // Refresh Your Fragment
                textViewWarehouseTitle.setText(warehouseName);
            }
        });

        drawerActivity = new DrawerActivity();
        drawerActivity.setCallback(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.home_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.home_fragment));

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.home_fragment));
    }


    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.running_orders_fragment));
        }
    }

    private void initializeView(View rootview) {
        textViewWarehouseTitle = (TextView) rootview.findViewById(R.id.activity_drawer_warehouse_name);
        drawerLinearLayoutMain = (LinearLayout) rootview.findViewById(R.id.drawer_linear_layout_main);
        activityDrawerWarehouseName = (TextView) rootview.findViewById(R.id.activity_drawer_warehouse_name);
        homeRunningOrdersLayout = (LinearLayout) rootview.findViewById(R.id.home_running_orders_layout);
        homeManagePackagesLayout = (LinearLayout) rootview.findViewById(R.id.home_manage_packages_layout);
        homeManageGatePassLayout = (LinearLayout) rootview.findViewById(R.id.home_manage_gate_pass_layout);
        homeManageConsignmentLayout = (LinearLayout) rootview.findViewById(R.id.home_manage_consignment_layout);
        homeReceiveConsignmentLayout = (LinearLayout) rootview.findViewById(R.id.home_receive_consignment_layout);
        homeRunningOrdersCount = (TextView) rootview.findViewById(R.id.home_running_orders_count);
        homeManagePackagesCount = (TextView) rootview.findViewById(R.id.home_manage_packages_count);
        homeManageGatePassCount = (TextView) rootview.findViewById(R.id.home_manage_gate_pass_count);
        homeManageConsignmentCount = (TextView) rootview.findViewById(R.id.home_manage_consignment_count);
        homeReceiveConsignmentCount = (TextView) rootview.findViewById(R.id.home_receive_consignment_count);
        homeCreateGatepassLayout = (LinearLayout) rootview.findViewById(R.id.home_manage_create_gatepass);

        homeRunningOrdersLayout.setOnClickListener(this);
        homeManagePackagesLayout.setOnClickListener(this);
        homeManageGatePassLayout.setOnClickListener(this);
        homeManageConsignmentLayout.setOnClickListener(this);
        homeReceiveConsignmentLayout.setOnClickListener(this);
        homeCreateGatepassLayout.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_running_orders_layout:
                Fragment runningOrdersFragment = RunningOrdersFragment.newInstance(getString(R.string.running_orders_fragment));
                getFragmentManager().beginTransaction().replace(R.id.home_container, runningOrdersFragment).commitAllowingStateLoss();
                if (drawerSelection != null) {
                    drawerSelection.setSelection(2);
                } else {
                    drawer.setSelection(2);
                }

                break;
            case R.id.home_manage_packages_layout:
                Fragment managePackagesFragment = ManagePackagesFragment.newInstance(getString(R.string.manage_packages_fragment));
                getFragmentManager().beginTransaction().replace(R.id.home_container, managePackagesFragment).commitAllowingStateLoss();
                if (drawerSelection != null) {
                    drawerSelection.setSelection(3);
                } else {
                    drawer.setSelection(3);
                }
                break;
            case R.id.home_manage_gate_pass_layout:
                Fragment manageGatePassFragment = ManageGatePassFragment.newInstance(getString(R.string.manage_gatepass_fragment));
                getFragmentManager().beginTransaction().replace(R.id.home_container, manageGatePassFragment).commitAllowingStateLoss();
                if (drawerSelection != null) {
                    drawerSelection.setSelection(4);
                } else {
                    drawer.setSelection(4);
                }
                break;
            case R.id.home_manage_consignment_layout:
                Fragment manageConsignmentFragment = ManageConsignmentFragment.newInstance(getString(R.string.manage_consignment_fragment));
                getFragmentManager().beginTransaction().replace(R.id.home_container, manageConsignmentFragment).commitAllowingStateLoss();
                if (drawerSelection != null) {
                    drawerSelection.setSelection(5);
                } else {
                    drawer.setSelection(5);
                }
                break;
            case R.id.home_receive_consignment_layout:
                Fragment receiveConsignmentFragment = ReceiveConsignmentFragment.newInstance(getString(R.string.receive_consignment_fragment));
                getFragmentManager().beginTransaction().replace(R.id.home_container, receiveConsignmentFragment).commitAllowingStateLoss();
                if (drawerSelection != null) {
                    drawerSelection.setSelection(6);
                } else {
                    drawer.setSelection(6);
                }
                break;

            case R.id.home_manage_create_gatepass:
                Intent intentCreateGatepass = new Intent(getActivity(), CreateGatepassActivity.class);
                startActivity(intentCreateGatepass);
                break;
        }
    }

    @Override
    public void sendDrawer(Drawer drawer) {

    }
}

