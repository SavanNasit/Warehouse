package com.accrete.warehouse.navigationView;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.fragment.creategatepass.CreateGatepassActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.SelectWarehouseAdapter;
import com.accrete.warehouse.fragment.manageConsignment.ManageConsignmentFragment;
import com.accrete.warehouse.fragment.managePackages.ManagePackagesFragment;
import com.accrete.warehouse.fragment.managegatepass.ManageGatePassFragment;
import com.accrete.warehouse.fragment.receiveConsignment.ReceiveConsignmentFragment;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersExecuteActivity;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersFragment;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersTabFragment;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.WarehouseList;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;
import com.mikepenz.materialdrawer.Drawer;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.navigationView.DrawerActivity.drawer;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;


/**
 * Created by poonam on 11/24/17.
 */

public class HomeFragment extends Fragment implements View.OnClickListener, DrawerInterface, SelectWarehouseAdapter.SelectWarehouseAdapterListener {
    private static final String KEY_TITLE = "HomeFragment";
    public boolean flagToOpenDialog = false;
    Drawer drawerSelection;
    FrameLayout frameLayout;
    List<WarehouseList> warehouseArrayList = new ArrayList<>();
    boolean selectedPosition = false;
    private TextView textViewWarehouseTitle;
    private LinearLayout linearLayoutHomeHeader;
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
    private AlertDialog dialogSelectWarehouse;
    private int selectedWareHousePosition = 0;
    private SelectWarehouseAdapter mAdapter;
    private ProgressBar progressBarSelectWarehouse;
    private ImageView imageViewLoader;

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

        ((DrawerActivity) getActivity()).setFragmentRefreshListener(new DrawerActivity.FragmentRefreshListener() {
            @Override
            public void onRefresh(String warehouseName) {
                // Refresh Your Fragment
                textViewWarehouseTitle.setText(warehouseName);
            }
        });

        drawerActivity = new DrawerActivity();
        drawerActivity.setCallback(this);

        String stringLoginSuccess = getActivity().getIntent().getStringExtra(getString(R.string.intent));
        frameLayout = (FrameLayout) rootView.findViewById(R.id.home_container);

        if (stringLoginSuccess != null && !stringLoginSuccess.isEmpty() && stringLoginSuccess.equals(getString(R.string.password))) {
            Snackbar snackbar = Snackbar
                    .make(frameLayout, getString(R.string.login_success), Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundResource(R.color.green);
            TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
            getActivity().getIntent().removeExtra(getString(R.string.intent));
        }

        //Enable Touch Back
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME) != null &&
                !AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME).isEmpty()) {
            textViewWarehouseTitle.setText(AppPreferences.getWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME));
            homeRunningOrdersCount.setText(AppPreferences.getWarehouseOrderCount(getActivity(), AppUtils.WAREHOUSE_ORDER_COUNT));
            homeManagePackagesCount.setText(AppPreferences.getWarehousePackageCount(getActivity(), AppUtils.WAREHOUSE_PACKAGE_COUNT));
            homeManageGatePassCount.setText(AppPreferences.getWarehouseGatepassCount(getActivity(), AppUtils.WAREHOUSE_GATEPASS_COUNT));
            homeManageConsignmentCount.setText(AppPreferences.getWarehouseConsignmentCount(getActivity(), AppUtils.WAREHOUSE_CONSIGNMENT_COUNT));
            homeReceiveConsignmentCount.setText(AppPreferences.getWarehouseReceiveConsignmentCount(getActivity(), AppUtils.WAREHOUSE_RECEIVE_CONSIGNMENT));
        }

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
        linearLayoutHomeHeader = (LinearLayout) rootview.findViewById(R.id.home_header);
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
        imageViewLoader = (ImageView) rootview.findViewById(R.id.imageView_loader);

        homeRunningOrdersLayout.setOnClickListener(this);
        homeManagePackagesLayout.setOnClickListener(this);
        homeManageGatePassLayout.setOnClickListener(this);
        homeManageConsignmentLayout.setOnClickListener(this);
        homeReceiveConsignmentLayout.setOnClickListener(this);
        homeCreateGatepassLayout.setOnClickListener(this);
        linearLayoutHomeHeader.setOnClickListener(this);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, new IntentFilter("notifyCount"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_running_orders_layout:
                // Fragment runningOrdersFragment = RunningOrdersFragment.newInstance(getString(R.string.running_orders_fragment));
                // getFragmentManager().beginTransaction().replace(R.id.home_container, runningOrdersFragment).commitAllowingStateLoss();
                RunningOrdersTabFragment runningOrdersTabFragment = (RunningOrdersTabFragment) getFragmentManager().findFragmentByTag(getString(R.string.running_orders_tab_title));
                if (runningOrdersTabFragment != null && runningOrdersTabFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = RunningOrdersTabFragment.newInstance(getString(R.string.running_orders_tab_title));
                    getFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

                if (drawerSelection != null) {
                    drawerSelection.setSelection(2);
                } else {
                    drawer.setSelection(2);
                }

                break;
            case R.id.home_manage_packages_layout:
                ManagePackagesFragment managePackagesFragment = (ManagePackagesFragment) getFragmentManager().findFragmentByTag(getString(R.string.manage_packages_fragment));
                if (managePackagesFragment != null && managePackagesFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = ManagePackagesFragment.newInstance(getString(R.string.manage_packages_fragment));
                    getFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

                if (drawerSelection != null) {
                    drawerSelection.setSelection(3);
                } else {
                    drawer.setSelection(3);
                }
                break;
            case R.id.home_manage_gate_pass_layout:
                ManageGatePassFragment manageGatePassFragment = (ManageGatePassFragment) getFragmentManager().findFragmentByTag(getString(R.string.manage_gatepass_fragment));
                if (manageGatePassFragment != null && manageGatePassFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = ManageGatePassFragment.newInstance(getString(R.string.manage_gatepass_fragment));
                    getFragmentManager().beginTransaction().add(R.id.home_container, f).commitAllowingStateLoss();
                }

                if (drawerSelection != null) {
                    drawerSelection.setSelection(4);
                } else {
                    drawer.setSelection(4);
                }
                break;
            case R.id.home_manage_consignment_layout:

                ManageConsignmentFragment manageConsignmentFragment = (ManageConsignmentFragment) getFragmentManager().findFragmentByTag(getString(R.string.manage_consignment_fragment));
                if (manageConsignmentFragment != null && manageConsignmentFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = ManagePackagesFragment.newInstance(getString(R.string.manage_consignment_fragment));
                    getFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }

                if (drawerSelection != null) {
                    drawerSelection.setSelection(5);
                } else {
                    drawer.setSelection(5);
                }
                break;
            case R.id.home_receive_consignment_layout:

                ReceiveConsignmentFragment receiveConsignmentFragment = (ReceiveConsignmentFragment) getFragmentManager().findFragmentByTag(getString(R.string.receive_consignment_fragment));
                if (receiveConsignmentFragment != null && receiveConsignmentFragment.isVisible()) {
                    //DO STUFF
                } else {
                    Fragment f = ManagePackagesFragment.newInstance(getString(R.string.receive_consignment_fragment));
                    getFragmentManager().beginTransaction().add(R.id.frame_container, f).commitAllowingStateLoss();
                }


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
            case R.id.home_header:
                flagToOpenDialog = true;
                linearLayoutHomeHeader.setEnabled(false);
                if (getActivity() != null) {
                    showLoader();
                    getWarehouseList();
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        linearLayoutHomeHeader.setEnabled(true);
                    }
                }, 1000);
                break;
        }
    }

    @Override
    public void sendDrawer(Drawer drawer) {

    }

    private void getWarehouseList() {
        if (warehouseArrayList.size() > 0) {
            warehouseArrayList.clear();
        }

        task = getString(R.string.warehouse_list_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getWarehouseList(version, key, task, userId, accessToken);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {

            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (WarehouseList warehouseList : apiResponse.getData().getWarehouseList()) {
                            if (apiResponse.getData().getWarehouseList() != null) {
                                warehouseArrayList.add(warehouseList);
                            }
                        }

                        if (flagToOpenDialog) {
                            dialogSelectWarehouse();
                            flagToOpenDialog = false;
                        }

                        if (getActivity() != null && isAdded()) {
                            hideLoader();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        hideLoader();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("warehouse:password", t.getMessage());
                if (getActivity() != null) {
                    hideLoader();
                }


            }
        });
    }


    private void dialogSelectWarehouse() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_select_warehouse, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        dialogSelectWarehouse = builder.create();
        dialogSelectWarehouse.setCanceledOnTouchOutside(false);
        TextView textViewOk = (TextView) dialogView.findViewById(R.id.btn_ok);
        TextView textViewCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBarSelectWarehouse = (ProgressBar) dialogView.findViewById(R.id.dialog_select_warehouse_progress_bar);
        RecyclerView recyclerView = (RecyclerView) dialogView.findViewById(R.id.select_dialog_recycler_view);

        for (int i = 0; i < warehouseArrayList.size(); i++) {
            if (AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID).
                    equals(warehouseArrayList.get(i).getChkid())) {
                warehouseArrayList.get(i).setSelected(true);
                selectedWareHousePosition = i;
            } else {
                warehouseArrayList.get(i).setSelected(false);
            }
        }

        textViewOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Toast.makeText(getActivity(), " You have selected Warehouse: " +
                            warehouseArrayList.get(selectedWareHousePosition).getName(), Toast.LENGTH_SHORT).show();
                    AppPreferences.setWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME,
                            warehouseArrayList.get(selectedWareHousePosition).getName());
                    AppPreferences.setWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID,
                            warehouseArrayList.get(selectedWareHousePosition).getChkid());
                    AppPreferences.setWarehouseOrderCount(getActivity(), AppUtils.WAREHOUSE_ORDER_COUNT,
                            warehouseArrayList.get(selectedWareHousePosition).getOrderCount());
                    AppPreferences.setWarehousePackageCount(getActivity(), AppUtils.WAREHOUSE_PACKAGE_COUNT,
                            warehouseArrayList.get(selectedWareHousePosition).getPackageCount());
                    AppPreferences.setWarehouseGatepassCount(getActivity(), AppUtils.WAREHOUSE_GATEPASS_COUNT,
                            warehouseArrayList.get(selectedWareHousePosition).getGatepassCount());
                    AppPreferences.setWarehouseConsignmentCount(getActivity(), AppUtils.WAREHOUSE_CONSIGNMENT_COUNT,
                            warehouseArrayList.get(selectedWareHousePosition).getConsignmentCount());
                    AppPreferences.setWarehouseReceiveConsignmentCount(getActivity(), AppUtils.WAREHOUSE_RECEIVE_CONSIGNMENT,
                            warehouseArrayList.get(selectedWareHousePosition).getReceiveConsignmentCount());

                    dialogSelectWarehouse.dismiss();

                    Fragment f = HomeFragment.newInstance(getString(R.string.home_fragment));
                    getFragmentManager().beginTransaction().replace(R.id.frame_container, f).commitAllowingStateLoss();

                    drawer.setSelection(0);

                    hideSoftKeyboard(getActivity());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectWarehouse.dismiss();
                try {
                    //TODO Close dialog and set selection in drawer
                    Fragment currentFragment = getFragmentManager().findFragmentById(R.id.frame_container);
                    if (currentFragment instanceof HomeFragment) {
                        drawer.setSelection(0);
                        hideSoftKeyboard(getActivity());
                    } else if (currentFragment instanceof RunningOrdersFragment) {
                        drawer.setSelection(2);
                        hideSoftKeyboard(getActivity());
                    } else if (currentFragment instanceof ManagePackagesFragment) {
                        drawer.setSelection(3);
                        hideSoftKeyboard(getActivity());
                    } else if (currentFragment instanceof ManageGatePassFragment) {
                        drawer.setSelection(4);
                        hideSoftKeyboard(getActivity());
                    } else if (currentFragment instanceof ManageConsignmentFragment) {
                        drawer.setSelection(5);
                        hideSoftKeyboard(getActivity());
                    } else if (currentFragment instanceof ReceiveConsignmentFragment) {
                        drawer.setSelection(6);
                        hideSoftKeyboard(getActivity());
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mAdapter = new SelectWarehouseAdapter(getActivity(), warehouseArrayList, this,
                selectedPosition, AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID));
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        dialogSelectWarehouse.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectWarehouse.isShowing()) {
            dialogSelectWarehouse.show();
        }
    }

    private void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    public void onMessageRowClicked(int position) {
        selectedPosition = true;
        selectedWareHousePosition = position;

        for (int i = 0; i < warehouseArrayList.size(); i++) {
            if (position == i) {
                warehouseArrayList.get(i).setSelected(true);
               /* if (warehouseArrayList.size() > 0 &&
                        (AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID) != null &&
                                !AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID).isEmpty())) {
                    AppPreferences.setWarehouseDefaultName(getActivity(), AppUtils.WAREHOUSE_DEFAULT_NAME, warehouseArrayList.get(i).getName());
                    AppPreferences.setWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID, warehouseArrayList.get(i).getChkid());
                    AppPreferences.setWarehouseOrderCount(getActivity(), AppUtils.WAREHOUSE_ORDER_COUNT, warehouseArrayList.get(i).getOrderCount());
                    AppPreferences.setWarehousePackageCount(getActivity(), AppUtils.WAREHOUSE_PACKAGE_COUNT, warehouseArrayList.get(i).getPackageCount());
                    AppPreferences.setWarehouseGatepassCount(getActivity(), AppUtils.WAREHOUSE_GATEPASS_COUNT, warehouseArrayList.get(i).getGatepassCount());
                    AppPreferences.setWarehouseConsignmentCount(getActivity(), AppUtils.WAREHOUSE_CONSIGNMENT_COUNT, warehouseArrayList.get(i).getConsignmentCount());
                    AppPreferences.setWarehouseReceiveConsignmentCount(getActivity(), AppUtils.WAREHOUSE_RECEIVE_CONSIGNMENT, warehouseArrayList.get(i).getReceiveConsignmentCount());

                }*/
            } else {
                warehouseArrayList.get(i).setSelected(false);
            }
        }
        //mAdapter.notifyDataSetChanged();
    }


    private void hideLoader() {
        if (getActivity() != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (warehouseArrayList != null && warehouseArrayList.size() == 0) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    }
                });
            }
        });

        thread.start();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity()!=null) {
                String status = NetworkUtil.getConnectivityStatusString(getActivity());
                if (getActivity() != null && !status.equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    getWarehouseList();
                }
            }
        }
    };
}

