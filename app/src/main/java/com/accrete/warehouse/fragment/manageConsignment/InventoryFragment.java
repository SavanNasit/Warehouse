package com.accrete.warehouse.fragment.manageConsignment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.InventoryAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Inventory;
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
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/5/17.
 */

public class InventoryFragment extends Fragment implements InventoryAdapter.InventoryAdapterListener,
        SwipeRefreshLayout.OnRefreshListener {
    LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout inventorySwipeRefreshLayout;
    private RecyclerView inventoryRecyclerView;
    private TextView inventoryEmptyView;
    private InventoryAdapter inventoryAdapter;
    private List<Inventory> inventoryList = new ArrayList<>();
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private String status, dataChanged;
    private String iscId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        iscId = bundle.getString(getString(R.string.iscid));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_inventory_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        inventorySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.inventory_swipe_refresh_layout);
        inventoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.inventory_recycler_view);
        inventoryEmptyView = (TextView) rootView.findViewById(R.id.inventory_empty_view);

        inventoryAdapter = new InventoryAdapter(getActivity(), inventoryList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        inventoryRecyclerView.setLayoutManager(mLayoutManager);
        inventoryRecyclerView.setHasFixedSize(true);
        inventoryRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //inventoryRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        inventoryRecyclerView.setNestedScrollingEnabled(false);
        inventoryRecyclerView.setAdapter(inventoryAdapter);

        doRefresh();

        //Scroll Listener
        inventoryRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && inventoryList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getInventoryList(inventoryList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (inventorySwipeRefreshLayout.isRefreshing()) {
                            inventorySwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        inventorySwipeRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        inventoryEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inventoryEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });

    }

    public void doRefresh() {
        if (inventoryList != null && inventoryList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isAdded()) {
                            inventoryEmptyView.setText(getString(R.string.no_data_available));
                            getInventoryList(getString(R.string.last_updated_date), "1");
                        }
                    }
                }, 200);
            } else {
                inventoryRecyclerView.setVisibility(View.GONE);
                inventoryEmptyView.setVisibility(View.VISIBLE);
                inventoryEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getInventoryList(final String time, final String traversalValue) {
        String task = getString(R.string.fetch_consignment_inventories);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getInventoryList(version, key, task, userId, accessToken, iscId,
                time, traversalValue);
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
                        for (final Inventory inventory : apiResponse.getData().getInventoryData()) {
                            if (inventory != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(inventory.getCreatedTs())) {
                                        inventoryList.add(inventory);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (inventorySwipeRefreshLayout != null &&
                                            inventorySwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(inventory.getCreatedTs())) {
                                            inventoryList.add(0, inventory);
                                        }
                                    } else {
                                        if (!time.equals(inventory.getCreatedTs())) {
                                            inventoryList.add(inventory);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (inventoryList != null && inventoryList.size() == 0) {
                            inventoryEmptyView.setVisibility(View.VISIBLE);
                            inventoryRecyclerView.setVisibility(View.GONE);
                        } else {
                            inventoryEmptyView.setVisibility(View.GONE);
                            inventoryRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (inventorySwipeRefreshLayout != null &&
                                inventorySwipeRefreshLayout.isRefreshing()) {
                            inventorySwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            inventoryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                inventoryAdapter.notifyDataSetChanged();
                                inventoryRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (inventoryList != null && inventoryList.size() == 0) {
                            inventoryEmptyView.setVisibility(View.VISIBLE);
                            inventoryRecyclerView.setVisibility(View.GONE);
                        } else {
                            inventoryEmptyView.setVisibility(View.GONE);
                            inventoryRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (inventorySwipeRefreshLayout != null && inventorySwipeRefreshLayout.isRefreshing()) {
                            inventorySwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            inventoryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                inventoryAdapter.notifyDataSetChanged();
                                inventoryRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (inventorySwipeRefreshLayout != null && inventorySwipeRefreshLayout.isRefreshing()) {
                        inventorySwipeRefreshLayout.setRefreshing(false);
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("SO :cancelled quotation", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (inventoryList != null && inventoryList.size() > 0) {
                getInventoryList(inventoryList.get(0).getCreatedTs(), "1");
            } else {
                getInventoryList(getString(R.string.last_updated_date), "1");
            }
            inventoryRecyclerView.setVisibility(View.VISIBLE);
            inventoryEmptyView.setVisibility(View.GONE);
            inventorySwipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            inventoryRecyclerView.setVisibility(View.GONE);
            inventoryEmptyView.setVisibility(View.VISIBLE);
            inventoryEmptyView.setText(getString(R.string.no_internet_try_later));
            inventorySwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
    }

    @Override
    public void onExecute() {
    }
}