package com.accrete.warehouse.fragment.runningorders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.RunningOrdersAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.RunningOrder;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
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
 * Created by poonam on 11/24/17.
 */

public class RunningOrdersFragment extends Fragment implements RunningOrdersAdapter.RunningOrdersAdapterListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String KEY_TITLE = "RunningOrdersFragment";
    RunningOrder runningOrders = new RunningOrder();
    private SwipeRefreshLayout runningOrdersSwipeRefreshLayout;
    private RecyclerView runningOrdersRecyclerView;
    private TextView runningOrdersEmptyView, runningOrdersCount;
    private RunningOrdersAdapter runningOrdersAdapter;
    private List<RunningOrder> runningOrderList = new ArrayList<>();
    private String mobileNumber;

    public static RunningOrdersFragment newInstance(String title) {
        RunningOrdersFragment f = new RunningOrdersFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    private void findViews(View rootview) {
        runningOrdersSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.running_orders_swipe_refresh_layout);
        runningOrdersRecyclerView = (RecyclerView) rootview.findViewById(R.id.running_orders_recycler_view);
        runningOrdersEmptyView = (TextView) rootview.findViewById(R.id.running_orders_empty_view);
        runningOrdersCount = (TextView) rootview.findViewById(R.id.running_orders_text_count);
        runningOrdersAdapter = new RunningOrdersAdapter(getActivity(), runningOrderList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        runningOrdersRecyclerView.setLayoutManager(mLayoutManager);
        runningOrdersRecyclerView.setHasFixedSize(true);
        runningOrdersRecyclerView.setItemAnimator(new DefaultItemAnimator());
        runningOrdersRecyclerView.setNestedScrollingEnabled(false);
        runningOrdersRecyclerView.setAdapter(runningOrdersAdapter);
        runningOrdersSwipeRefreshLayout.setOnRefreshListener(this);
        runningOrdersSwipeRefreshLayout.setRefreshing(true);
        runningOrdersSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                getRunningOrderList();
            }

        });

    }

    public void getData(String str) {
        Toast.makeText(getActivity(), str, Toast.LENGTH_SHORT).show();
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
    public void onExecute(List<Packages> packages, List<PendingItems> pendingItems, String chkid, String chkoid) {
        RunningOrdersExecuteFragment fragment = new RunningOrdersExecuteFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("packages", (ArrayList<? extends Parcelable>) packages);
        bundle.putParcelableArrayList("pendingItems", (ArrayList<? extends Parcelable>) pendingItems);
        bundle.putString("chkid", chkid);
        bundle.putString("chkoid", chkoid);
        fragment.setArguments(bundle);
        ft.replace(R.id.running_orders_container, fragment, getString(R.string.running_orders_execute_fragment));
        ft.addToBackStack(null).commit();

    }

    @Override
    public void onCall(String contact) {
        mobileNumber = contact;
    }


    private void getRunningOrderList() {
        task = getString(R.string.running_order_list_task);
        String chkid = null;
        if (runningOrderList != null && runningOrderList.size() > 0) {
            runningOrderList.clear();
        }

        runningOrdersSwipeRefreshLayout.setRefreshing(false);
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
                        runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                        runningOrdersEmptyView.setVisibility(View.GONE);

                        for (RunningOrder runningOrder : apiResponse.getData().getRunningOrders()) {
                            runningOrderList.add(runningOrder);
                            runningOrdersCount.setText(apiResponse.getData().getRunningOrderCount() + " Running Orders");
                            runningOrdersCount.setVisibility(View.VISIBLE);
                        }
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            runningOrdersEmptyView.setText(getString(R.string.no_data_available));
                            runningOrdersRecyclerView.setVisibility(View.GONE);
                            runningOrdersEmptyView.setVisibility(View.VISIBLE);
                            runningOrdersCount.setVisibility(View.GONE);
                        }
                    }
                    if (runningOrdersSwipeRefreshLayout != null && runningOrdersSwipeRefreshLayout.isRefreshing()) {
                        runningOrdersSwipeRefreshLayout.setRefreshing(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                runningOrdersSwipeRefreshLayout.setRefreshing(false);
                Log.d("warehouse:runningOrders", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        getRunningOrderList();
    }

    public void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            startActivity(intentCall);
        } else {
            Toast.makeText(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        }
    }
}

