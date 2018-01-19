package com.accrete.warehouse.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.GatepassList;
import com.accrete.warehouse.model.ManageGatepass;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageGatePassFragment extends Fragment implements ManageGatepassAdapter.ManageGatepassAdapterrListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_TITLE = "ManageGatePass";
    private SwipeRefreshLayout manageGatepassSwipeRefreshLayout;
    private RecyclerView manageGatepassRecyclerView;
    private TextView manageGatepassEmptyView;
    private ManageGatepassAdapter manageGatePassAdapter;
    private List<GatepassList> gatepassList = new ArrayList<>();
    private ManageGatepass manageGatepass = new ManageGatepass();
    private AlertDialog dialogSelectEvent;
    private String status;

    public static ManageGatePassFragment newInstance(String title) {
        ManageGatePassFragment f = new ManageGatePassFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_gatepass, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        manageGatepassSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_gatepass_swipe_refresh_layout);
        manageGatepassRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_gatepass_recycler_view);
        manageGatepassEmptyView = (TextView) rootView.findViewById(R.id.manage_gatepass__empty_view);

        manageGatePassAdapter = new ManageGatepassAdapter(getActivity(), gatepassList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        manageGatepassRecyclerView.setLayoutManager(mLayoutManager);
        manageGatepassRecyclerView.setHasFixedSize(true);
        manageGatepassRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageGatepassRecyclerView.setNestedScrollingEnabled(false);
        manageGatepassRecyclerView.setAdapter(manageGatePassAdapter);

        manageGatepassSwipeRefreshLayout.setOnRefreshListener(this);
        getManageGatepassList();

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_gatepass_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_gatepass_fragment));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(final int position) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_select_actions_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectEvent = builder.create();
        dialogSelectEvent.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        LinearLayout actionsFilter;
        LinearLayout actionsViewPackage;
        LinearLayout actionsPrintPackage;
        LinearLayout actionsCancelGatepass;
        ImageView imageBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsFilter = (LinearLayout) dialogView.findViewById(R.id.actions_filter);
        actionsViewPackage = (LinearLayout) dialogView.findViewById(R.id.actions_view_package);
        actionsPrintPackage = (LinearLayout) dialogView.findViewById(R.id.actions_print_package);
        actionsCancelGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_cancel_gatepass);
        imageBack = (ImageView) dialogView.findViewById(R.id.image_back);

        actionsCancelGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass(position, dialogSelectEvent);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });


        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }

    private void dialogCancelGatepass(final int position, final AlertDialog dialogSelectEvent) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogCancelGatepass = builder.create();
        dialogCancelGatepass.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        Button btnOk;
        final ProgressBar cancelGatepassProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelGatepass(dialogCancelGatepass, gatepassList.get(position).getPacdelgatid(), dialogSelectEvent);
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass.dismiss();
            }
        });


        dialogCancelGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogCancelGatepass.isShowing()) {
            dialogCancelGatepass.show();
        }

    }

    private void getManageGatepassList() {
        task = getString(R.string.task_manage_gatepass);
        String chkid = null;
        if (gatepassList != null && gatepassList.size() > 0) {
            gatepassList.clear();
        }
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getRunningOrderList(version, key, task, userId, accessToken, chkid);
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
                        manageGatepassRecyclerView.setVisibility(View.VISIBLE);
                        manageGatepassEmptyView.setVisibility(View.GONE);

                        for (GatepassList gatepassLists : apiResponse.getData().getGatepassList()) {
                            gatepassList.add(gatepassLists);
                        }

                        manageGatePassAdapter.notifyDataSetChanged();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            manageGatepassEmptyView.setText(getString(R.string.no_data_available));
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);

                        }
                    }
                    if (manageGatepassSwipeRefreshLayout != null && manageGatepassSwipeRefreshLayout.isRefreshing()) {
                        manageGatepassSwipeRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                manageGatepassSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });
    }


    private void cancelGatepass(final AlertDialog dialogCancelGatepass, String gatepassId, final AlertDialog dialogSelectEvent) {
        task = getString(R.string.task_cancel_gatepass);
        if (gatepassList != null && gatepassList.size() > 0) {
            gatepassList.clear();
        }
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.cancelGatepass(version, key, task, userId, accessToken, gatepassId);
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
                        dialogCancelGatepass.dismiss();
                        dialogSelectEvent.dismiss();
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            getManageGatepassList();
                        } else {
                            manageGatepassRecyclerView.setVisibility(View.GONE);
                            manageGatepassEmptyView.setVisibility(View.VISIBLE);
                            manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
                            manageGatepassSwipeRefreshLayout.setRefreshing(false);
                        }

                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        dialogCancelGatepass.dismiss();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                manageGatepassSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            getManageGatepassList();
        } else {
            manageGatepassRecyclerView.setVisibility(View.GONE);
            manageGatepassEmptyView.setVisibility(View.VISIBLE);
            manageGatepassEmptyView.setText(getString(R.string.no_internet_try_later));
            manageGatepassSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
