package com.accrete.sixorbit.fragment.Drawer.vendor;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.ConsignmentInventoryAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ConsignmentItem;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 8/12/17.
 */

public class InventoryTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ConsignmentInventoryAdapter.ConsignmentAdapterListener {
    private String venid, dataChanged, consignmentId, status;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private ConsignmentInventoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayoutManager mLayoutManager;
    private List<ConsignmentItem> consignmentItemArrayList = new ArrayList<>();
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private ImageView imageView;

    private void hideLoader() {
        if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        venid = bundle.getString(getString(R.string.venid));
        consignmentId = bundle.getString(getString(R.string.consignment_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchase_order_history, container, false);
        //Find Views
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        adapter = new ConsignmentInventoryAdapter(getActivity(), consignmentItemArrayList, venid, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && consignmentItemArrayList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getOrdersList(consignmentItemArrayList.get(totalItemCount - 1).getCreatedTs(), "2", consignmentId);
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void doRefresh() {
        if (getActivity() != null && isAdded()) {
            if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                status = NetworkUtil.getConnectivityStatusString(getActivity());
                if (!status.equals(getString(R.string.not_connected_to_internet))) {
                    loading = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                                showLoader();
                            }
                            getOrdersList(getString(R.string.last_updated_date), "1", consignmentId);
                        }
                    }, 200);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText(getString(R.string.no_internet_try_later));
                }
            }
        }
    }

    private void getOrdersList(final String time, final String traversalValue, final String consignmentId) {
        task = getString(R.string.vendor_inventory_items);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getVendorInventoryItems(version, key, task, userId, accessToken, consignmentId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (final ConsignmentItem consignmentItem : apiResponse.getData().getConsignmentItems()) {
                            if (apiResponse.getData().getConsignmentItems() != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(consignmentItem.getCreatedTs())) {
                                        consignmentItemArrayList.add(consignmentItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(consignmentItem.getCreatedTs())) {
                                            consignmentItemArrayList.add(0, consignmentItem);
                                        }
                                    } else {
                                        if (!time.equals(consignmentItem.getCreatedTs())) {
                                            consignmentItemArrayList.add(consignmentItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            adapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            //       customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //       customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            adapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                adapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }

                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                    }
                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                }
            }
        });

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (consignmentItemArrayList != null && consignmentItemArrayList.size() > 0) {
                getOrdersList(consignmentItemArrayList.get(0).getCreatedTs(), "1", consignmentId);
            } else {
                if (consignmentItemArrayList != null && consignmentItemArrayList.size() == 0) {
                    showLoader();
                }
                getOrdersList(getString(R.string.last_updated_date), "1", consignmentId);
            }
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_internet_try_later));
            swipeRefreshLayout.setRefreshing(false);
        }
    }
}
