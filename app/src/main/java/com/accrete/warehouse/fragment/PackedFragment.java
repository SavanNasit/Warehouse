package com.accrete.warehouse.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
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
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PackedItem;
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
 * Created by poonam on 11/30/17.
 */

public class PackedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, PackedItemAdapter.PackedItemAdapterListener {

    private SwipeRefreshLayout packedSwipeRefreshLayout;
    private RecyclerView packedRecyclerView;
    private TextView packedEmptyView, packedAdd, packedDeliver;
    private PackedItemAdapter packedItemAdapter;
    private List<PackedItem> packedList = new ArrayList<>();
    private Packages packages = new Packages();
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;


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
        packedAdd = (TextView) rootView.findViewById(R.id.packed_text_add);
        packedDeliver = (TextView) rootView.findViewById(R.id.packed_text_deliver);

        packedItemAdapter = new PackedItemAdapter(getActivity(), packedList, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packedRecyclerView.setLayoutManager(mLayoutManager);
        packedRecyclerView.setHasFixedSize(true);
        packedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedRecyclerView.setNestedScrollingEnabled(false);
        packedRecyclerView.setAdapter(packedItemAdapter);

        packedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedAdd.setBackgroundColor(getResources().getColor(R.color.add_dark_blue));
            }
        });
        packedDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedDeliver.setBackgroundColor(getResources().getColor(R.color.add_dark_red));
            }
        });

        apiCall();
     /*  packedSwipeRefreshLayout.post(new Runnable() {
               @Override
               public void run() {
                   String  status = NetworkUtil.getConnectivityStatusString(getActivity());
                   if (!status.equals(getString(R.string.not_connected_to_internet))) {
                       loading = true;
                       new Handler().postDelayed(new Runnable() {
                           @Override
                           public void run() {
                               getPackageDetailsList(getString(R.string.last_updated_date), "1");
                           }
                       }, 00);
                   } else {
                       packedRecyclerView.setVisibility(View.GONE);
                       packedEmptyView.setVisibility(View.VISIBLE);
                       packedEmptyView.setText(getString(R.string.no_internet_try_later));
                   }
               }
    });*/

        //Scroll Listener
        packedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && packedList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        packedSwipeRefreshLayout.setOnRefreshListener(this);


    }

    private void apiCall() {
        String status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            loading = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPackageDetailsList(getString(R.string.last_updated_date), "1");
                    packedRecyclerView.setVisibility(View.VISIBLE);
                    packedEmptyView.setVisibility(View.GONE);
                }
            }, 00);
        } else {
            packedRecyclerView.setVisibility(View.GONE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }


    private void getPackageDetailsList(String updatedDate, String traversalValue) {
        task = getString(R.string.packed_packages_list_task);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageDetails(version, key, task, userId, accessToken, chkid, updatedDate,
                traversalValue);
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
                        packedRecyclerView.setVisibility(View.VISIBLE);
                        packedEmptyView.setVisibility(View.GONE);

                        for (PackedItem packedItem : apiResponse.getData().getPackedItems()) {
                            packedList.add(packedItem);
                        }
                        packedItemAdapter.notifyDataSetChanged();
                    } else {
                        if (apiResponse.getSuccessCode().equals("10001")) {
                            packedEmptyView.setText(getString(R.string.no_data_available));
                            packedRecyclerView.setVisibility(View.GONE);
                            packedEmptyView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (packedSwipeRefreshLayout != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("wh:packageDetails", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        String status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            loading = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPackageDetailsList(getString(R.string.last_updated_date), "1");
                }
            }, 00);
        } else {
            packedRecyclerView.setVisibility(View.GONE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
        }

    }
}
