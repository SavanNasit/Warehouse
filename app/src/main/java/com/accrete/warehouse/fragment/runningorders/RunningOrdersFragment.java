package com.accrete.warehouse.fragment.runningorders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.RecyclerItemTouchHelper;
import com.accrete.warehouse.adapter.RunningOrdersAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.OrderData;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.RunningOrder;
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
 * Created by poonam on 11/24/17.
 */

public class RunningOrdersFragment extends Fragment implements RunningOrdersAdapter.RunningOrdersAdapterListener, SwipeRefreshLayout.OnRefreshListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {
    private static final String KEY_TITLE = "RunningOrdersFragment";
    RunningOrder runningOrders = new RunningOrder();
    private SwipeRefreshLayout runningOrdersSwipeRefreshLayout;
    private RecyclerView runningOrdersRecyclerView;
    private TextView runningOrdersEmptyView, runningOrdersCount;
    private RunningOrdersAdapter runningOrdersAdapter;
    private ProgressBar runningOrdersProgressBar;
    private List<RunningOrder> runningOrderList = new ArrayList<>();
    private String mobileNumber;
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private String dataChanged, status;
    private int progressStatus = 0;
    private LinearLayoutManager mLayoutManager;

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
        runningOrdersProgressBar = (ProgressBar) rootview.findViewById(R.id.running_orders_progress_bar);
        runningOrdersAdapter = new RunningOrdersAdapter(getActivity(), runningOrderList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        runningOrdersRecyclerView.setLayoutManager(mLayoutManager);
        runningOrdersRecyclerView.setHasFixedSize(true);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(runningOrdersRecyclerView.getContext(),
                mLayoutManager.getOrientation());
        runningOrdersRecyclerView.addItemDecoration(dividerItemDecoration);
        runningOrdersRecyclerView.setNestedScrollingEnabled(false);
        runningOrdersRecyclerView.setAdapter(runningOrdersAdapter);
        runningOrdersSwipeRefreshLayout.setOnRefreshListener(this);
        /*runningOrdersSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                doRefresh();
            }

        });*/


        //Scroll Listener
        runningOrdersRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && runningOrderList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getRunningOrderList(runningOrderList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (runningOrdersSwipeRefreshLayout.isRefreshing()) {
                            runningOrdersSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //   customerOrderFabAdd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        runningOrdersSwipeRefreshLayout.setOnRefreshListener(this);
        doRefresh();

        setSwipeForRecyclerView();

    }

    public void doRefresh() {
        if (runningOrderList != null && runningOrderList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (runningOrderList != null && runningOrderList.size() == 0) {
                        }
                        getRunningOrderList(getString(R.string.last_updated_date), "1");
                    }
                }, 200);
            } else {
                runningOrdersRecyclerView.setVisibility(View.GONE);
                runningOrdersEmptyView.setVisibility(View.VISIBLE);
                runningOrdersEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void setSwipeForRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(runningOrdersRecyclerView);
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
    public void onExecute(List<Packages> packages, List<OrderData> pendingItems, String chkid, String chkoid, int position) {
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


    private void getRunningOrderList(final String updatedDate, final String traversalValue) {
        try {
            task = getString(R.string.running_order_list_task);
            String chkid = null;
           /* if (runningOrderList != null && runningOrderList.size() > 0) {
                runningOrderList.clear();
            }*/

            runningOrdersProgressBar.setMax(100);
            runningOrdersProgressBar.setVisibility(View.VISIBLE);
            runningOrdersProgressBar.setProgress(progressStatus);

            runningOrdersSwipeRefreshLayout.setRefreshing(false);
            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getRunningOrderList(version, key, task, userId, accessToken, chkid,
                    updatedDate, traversalValue);
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

                            for (final RunningOrder runningOrder : apiResponse.getData().getRunningOrders()) {
                                if (runningOrder != null) {
                                    if (traversalValue.equals("2")) {
                                        if (!updatedDate.equals(runningOrder.getCreatedTs())) {
                                            runningOrderList.add(runningOrder);
                                        }
                                        dataChanged = "yes";
                                    } else if (traversalValue.equals("1")) {
                                        if (runningOrdersSwipeRefreshLayout != null &&
                                                runningOrdersSwipeRefreshLayout.isRefreshing()) {
                                            // To remove duplicacy of a new item
                                            if (!updatedDate.equals(runningOrder.getCreatedTs())) {
                                                runningOrderList.add(0, runningOrder);
                                            }
                                        } else {
                                            if (!updatedDate.equals(runningOrder.getCreatedTs())) {
                                                runningOrderList.add(runningOrder);
                                            }
                                        }
                                        dataChanged = "yes";
                                    }
                                }
                            }

                            loading = false;
                            if (runningOrderList != null && runningOrderList.size() == 0) {
                                runningOrdersEmptyView.setVisibility(View.VISIBLE);
                                runningOrdersEmptyView.setText("No data available");
                                runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                                runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                runningOrdersEmptyView.setVisibility(View.GONE);
                                runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (runningOrdersSwipeRefreshLayout != null &&
                                    runningOrdersSwipeRefreshLayout.isRefreshing()) {
                                runningOrdersSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversalValue.equals("2")) {
                                runningOrdersAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    runningOrdersAdapter.notifyDataSetChanged();
                                    runningOrdersRecyclerView.smoothScrollToPosition(0);
                                }
                            }


                        } else {
                            if (runningOrderList != null && runningOrderList.size() == 0) {
                                runningOrdersEmptyView.setVisibility(View.VISIBLE);
                                runningOrdersEmptyView.setText("No data available");
                                runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                                runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            } else {
                                runningOrdersEmptyView.setVisibility(View.GONE);
                                runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (runningOrdersSwipeRefreshLayout != null &&
                                    runningOrdersSwipeRefreshLayout.isRefreshing()) {
                                runningOrdersSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversalValue.equals("2")) {
                                runningOrdersAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    runningOrdersAdapter.notifyDataSetChanged();
                                    runningOrdersRecyclerView.smoothScrollToPosition(0);
                                }
                            }

                            if (apiResponse.getSuccessCode().equals("10001")) {
                                runningOrdersEmptyView.setText(getString(R.string.no_data_available));
                                runningOrdersRecyclerView.setVisibility(View.GONE);
                                runningOrdersEmptyView.setVisibility(View.VISIBLE);
                                runningOrdersCount.setVisibility(View.GONE);

                            } else if (apiResponse.getSuccessCode().equals("20004")) {

                            }
                        }
                        if (runningOrdersSwipeRefreshLayout != null && runningOrdersSwipeRefreshLayout.isRefreshing()) {
                            runningOrdersSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (runningOrdersProgressBar != null && runningOrdersProgressBar.getVisibility() == View.VISIBLE) {
                            runningOrdersProgressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        runningOrdersSwipeRefreshLayout.setRefreshing(false);
                        if (runningOrdersProgressBar != null && runningOrdersProgressBar.getVisibility() == View.VISIBLE) {
                            runningOrdersProgressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (runningOrderList != null && runningOrderList.size() > 0) {
                getRunningOrderList(runningOrderList.get(0).getCreatedTs(), "1");
            } else {
                getRunningOrderList(getString(R.string.last_updated_date), "1");
            }
            runningOrdersRecyclerView.setVisibility(View.VISIBLE);
            runningOrdersEmptyView.setVisibility(View.GONE);
            runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
            runningOrdersSwipeRefreshLayout.setRefreshing(true);

        } else {

            runningOrdersRecyclerView.setVisibility(View.VISIBLE);
            runningOrdersEmptyView.setVisibility(View.VISIBLE);
            runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
            runningOrdersEmptyView.setText(getString(R.string.no_internet_try_later));
            runningOrdersSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void callAction() {
        Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber));
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            startActivity(intentCall);
        } else {
            Toast.makeText(getContext(), getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        int swipedPosition = viewHolder.getAdapterPosition();
        runningOrdersAdapter.next(swipedPosition, runningOrderList.get(position));

    }
}

