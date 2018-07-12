package com.accrete.warehouse.fragment.runningorders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
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
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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
    private ImageView imageViewLoader;
    private String chkId;
    private String stringSearchText;


    /* public static RunningOrdersFragment newInstance(String title) {
         RunningOrdersFragment f = new RunningOrdersFragment();
         Bundle args = new Bundle();
         args.putString(KEY_TITLE, title);
         f.setArguments(args);
         return (f);
     }
 */


    private void findViews(View rootview) {
        runningOrdersSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.running_orders_swipe_refresh_layout);
        runningOrdersRecyclerView = (RecyclerView) rootview.findViewById(R.id.running_orders_recycler_view);
        runningOrdersEmptyView = (TextView) rootview.findViewById(R.id.running_orders_empty_view);
        runningOrdersCount = (TextView) rootview.findViewById(R.id.running_orders_text_count);
        runningOrdersProgressBar = (ProgressBar) rootview.findViewById(R.id.running_orders_progress_bar);
        imageViewLoader = (ImageView) rootview.findViewById(R.id.imageView_loader);
        runningOrdersAdapter = new RunningOrdersAdapter(getActivity(), runningOrderList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        runningOrdersRecyclerView.setLayoutManager(mLayoutManager);
        runningOrdersRecyclerView.setHasFixedSize(true);
        //   DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(runningOrdersRecyclerView.getContext(),
        // mLayoutManager.getOrientation());
        //   runningOrdersRecyclerView.addItemDecoration(dividerItemDecoration);
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

                        if (getActivity() != null) {
                            showLoader();
                            //  getRunningOrderList(runningOrderList.get(totalItemCount - 1).getCreatedTs(), "2");
                            getRunningOrderList(chkId, runningOrderList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");

                        }

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

        //  runningOrdersSwipeRefreshLayout.setOnRefreshListener(this);

        doRefresh();
        setSwipeForRecyclerView();
    }

    public void doRefresh() {
        if (runningOrderList != null && runningOrderList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!loading && !status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (runningOrderList != null && runningOrderList.size() == 0) {
                        }
                        if (getActivity() != null && isAdded()) {
                            showLoader();
                            getRunningOrderList(chkId, getString(R.string.last_updated_date), "1", stringSearchText, "", "");
                        }
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
        chkId = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
        findViews(rootView);
        //  Log.e("onCreate","jkjjkjjk");

        ((RunningOrdersTabFragment) getParentFragment()).updateCount(AppPreferences.getWarehouseOrderCount(getActivity(), AppUtils.WAREHOUSE_ORDER_COUNT), 0);
        return rootView;
    }


/*

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
*/


    @Override
    public void onMessageRowClicked(int position) {
        runningOrdersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onExecute(List<Packages> packages, List<OrderData> pendingItems, String chkid, String chkoid, int position) {
        String currentAddress = "";
        String shippingAddress = "";

        //Shipping Address
        if ((runningOrderList.get(position).getCustomerInfo().getShippingAddrSitename() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrSitename().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getShippingAddrLine() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrLine().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getShippingAddrCity() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrCity().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getShippingAddrCountryName() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrCountryName().isEmpty())) {


            //Address Site Name
            if (runningOrderList.get(position).getCustomerInfo().getShippingAddrSitename() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrSitename().isEmpty()) {
                shippingAddress = shippingAddress + runningOrderList.get(position).getCustomerInfo().getShippingAddrSitename() + ", ";
            }

            //Address Line 1
            if (runningOrderList.get(position).getCustomerInfo().getShippingAddrLine() != null && !runningOrderList.get(position).getCustomerInfo().getShippingAddrLine().isEmpty()) {
                shippingAddress = shippingAddress + runningOrderList.get(position).getCustomerInfo().getShippingAddrLine() + ", ";
            }

            //City & Zip Code
            if (runningOrderList.get(position).getCustomerInfo().getShippingAddrCity() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrCity().toString().trim().isEmpty() &&
                    runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode().toString().trim().isEmpty()) {
                shippingAddress = shippingAddress +
                        runningOrderList.get(position).getCustomerInfo().getShippingAddrCity().toString().trim() + " - " +
                        runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode()
                                .toString().trim() + "," + "\n";
            } else if (runningOrderList.get(position).getCustomerInfo().getShippingAddrCity() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrCity().toString().trim().isEmpty()) {
                shippingAddress = shippingAddress +
                        runningOrderList.get(position).getCustomerInfo().getShippingAddrCity().toString().trim() + ", ";
            } else if (runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode().toString().trim().isEmpty()) {
                shippingAddress = shippingAddress +
                        runningOrderList.get(position).getCustomerInfo().getShippingAddrPincode()
                                .toString().trim() + ", ";
            }
            //State
            if (runningOrderList.get(position).getCustomerInfo().getShippingAddrStateName() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrStateName().isEmpty()) {
                shippingAddress = shippingAddress + runningOrderList.get(position).getCustomerInfo().getShippingAddrStateName() + ", ";
            }

            //Country
            if (runningOrderList.get(position).getCustomerInfo().getShippingAddrCountryName() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getShippingAddrCountryName().isEmpty()) {
                shippingAddress = shippingAddress + runningOrderList.get(position).getCustomerInfo().getShippingAddrCountryName() + " ";
            }

        }

        //Billing Address
        if ((runningOrderList.get(position).getCustomerInfo().getBillingAddSitename() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddSitename().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getBillingAddrLine() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddrLine().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getBillingAddrCity() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddrCity().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode().isEmpty())
                || (runningOrderList.get(position).getCustomerInfo().getBillingAddrCountryName() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddrCountryName().isEmpty())) {


            //Address Site Name
            if (runningOrderList.get(position).getCustomerInfo().getBillingAddSitename() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddSitename().isEmpty()) {
                currentAddress = currentAddress + runningOrderList.get(position).getCustomerInfo().getBillingAddSitename() + ", ";
            }

            //Address Line 1
            if (runningOrderList.get(position).getCustomerInfo().getBillingAddrLine() != null && !runningOrderList.get(position).getCustomerInfo().getBillingAddrLine().isEmpty()) {
                currentAddress = currentAddress + runningOrderList.get(position).getCustomerInfo().getBillingAddrLine() + ", ";
            }

            //City & Zip Code
            if (runningOrderList.get(position).getCustomerInfo().getBillingAddrCity() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrCity().toString().trim().isEmpty() &&
                    runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        runningOrderList.get(position).getCustomerInfo().getBillingAddrCity().toString().trim() + " - " +
                        runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode()
                                .toString().trim() + "," + "\n";
            } else if (runningOrderList.get(position).getCustomerInfo().getBillingAddrCity() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrCity().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        runningOrderList.get(position).getCustomerInfo().getBillingAddrCity().toString().trim() + ", ";
            } else if (runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode().toString().trim().isEmpty()) {
                currentAddress = currentAddress +
                        runningOrderList.get(position).getCustomerInfo().getBillingAddrPincode()
                                .toString().trim() + ", ";
            }
            //State
            if (runningOrderList.get(position).getCustomerInfo().getBillingAddrStateName() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrStateName().isEmpty()) {
                currentAddress = currentAddress + runningOrderList.get(position).getCustomerInfo().getBillingAddrStateName() + ", ";
            }

            //Country
            if (runningOrderList.get(position).getCustomerInfo().getBillingAddrCountryName() != null &&
                    !runningOrderList.get(position).getCustomerInfo().getBillingAddrCountryName().isEmpty()) {
                currentAddress = currentAddress + runningOrderList.get(position).getCustomerInfo().getBillingAddrCountryName() + " ";
            }

        }

        Intent intentExecute = new Intent(getActivity(), RunningOrdersExecuteActivity.class);
        intentExecute.putParcelableArrayListExtra("packages", (ArrayList<? extends Parcelable>) packages);
        intentExecute.putParcelableArrayListExtra("pendingItems", (ArrayList<? extends Parcelable>) pendingItems);
        intentExecute.putExtra("customerName", runningOrderList.get(position).getCustomerInfo().getName());
        intentExecute.putExtra("customerEmail", runningOrderList.get(position).getCustomerInfo().getEmail());
        intentExecute.putExtra("customerMobile", runningOrderList.get(position).getCustomerInfo().getMobile());
        intentExecute.putExtra("customerBillingAddress", currentAddress);
        intentExecute.putExtra("customerDeliveryAddress", shippingAddress);
        intentExecute.putExtra("chkid", chkid);
        intentExecute.putExtra("chkoid", chkoid);
        intentExecute.putExtra("orderId", runningOrderList.get(position).getCheckpointOrderID());
        intentExecute.putExtra("flag", "runningOrder");
        startActivity(intentExecute);
    }

    @Override
    public void onCall(String contact) {
        mobileNumber = contact;
    }


    private void getRunningOrderList(String chkId, final String time, final String traversalValue,
                                     String searchValue, String startDate, String endDate) {
        try {
            task = getString(R.string.running_order_list_task);
            String chkid = null;
         /*   if (searchValue != null && searchValue.length() > 0 &&
                    runningOrderList != null && runningOrderList.size() > 0) {
                runningOrderList.clear();
                runningOrdersAdapter.notifyDataSetChanged();
            }*/
            runningOrdersProgressBar.setVisibility(View.GONE);
  /*          runningOrdersProgressBar.setMax(100);
            runningOrdersProgressBar.setVisibility(View.VISIBLE);
            runningOrdersProgressBar.setProgress(progressStatus);*/

            runningOrdersSwipeRefreshLayout.setRefreshing(false);
            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getConsignmentLists(version, key, task, userId, accessToken, chkId,
                    time, traversalValue, searchValue, startDate, endDate);
            //  Log.d("Request", String.valueOf(call));
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
                                        if (!time.equals(runningOrder.getCreatedTs())) {
                                            runningOrderList.add(runningOrder);
                                        }
                                        dataChanged = "yes";
                                    } else if (traversalValue.equals("1")) {
                                       /* if (runningOrdersSwipeRefreshLayout != null &&
                                                runningOrdersSwipeRefreshLayout.isRefreshing()) {
                                            // To remove duplicacy of a new item
                                            if (!time.equals(runningOrder.getCreatedTs())) {
                                                runningOrderList.add(0, runningOrder);
                                            }
                                        } else {
                                            if (!time.equals(runningOrder.getCreatedTs())) {
                                                runningOrderList.add(runningOrder);
                                            }
                                        }*/

                                        runningOrderList.add(runningOrder);
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
                                // runningOrdersCount.setVisibility(View.GONE);
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
                            if (runningOrderList != null && runningOrderList.size() > 0) {
                                // runningOrdersCount.setVisibility(View.VISIBLE);
                                if (runningOrderList.size() == 1) {
                                    // runningOrdersCount.setText(runningOrderList.size() + " Running Order");
                                } else {
                                    //  runningOrdersCount.setText(runningOrderList.size() + " Running Orders");
                                }
                            }

                        } else {
                            if (runningOrderList != null && runningOrderList.size() == 0) {
                                runningOrdersEmptyView.setVisibility(View.VISIBLE);
                                runningOrdersEmptyView.setText("No data available");
                                runningOrdersRecyclerView.setVisibility(View.VISIBLE);
                                runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                //  runningOrdersCount.setVisibility(View.GONE);
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
                                //   runningOrdersCount.setVisibility(View.GONE);

                            } else if (apiResponse.getSuccessCode().equals("20004")) {

                            }
                        }
                        if (runningOrdersSwipeRefreshLayout != null && runningOrdersSwipeRefreshLayout.isRefreshing()) {
                            runningOrdersSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (runningOrdersProgressBar != null && runningOrdersProgressBar.getVisibility() == View.VISIBLE) {
                            runningOrdersProgressBar.setVisibility(View.GONE);
                        }
                        if (getActivity() != null && isAdded()) {
                            hideLoader();
                        }

                        ((RunningOrdersTabFragment) getParentFragment()).updateCount(AppPreferences.getWarehouseOrderCount(getActivity(), AppUtils.WAREHOUSE_ORDER_COUNT), 0);

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        runningOrdersSwipeRefreshLayout.setRefreshing(false);
                        hideLoader();
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
         /*   if (runningOrderList != null && runningOrderList.size() > 0) {
                getRunningOrderList(runningOrderList.get(0).getCreatedTs(), "1");
            } else {
                getRunningOrderList(getString(R.string.last_updated_date), "1");
            }*/
          /*  runningOrdersRecyclerView.setVisibility(View.VISIBLE);
            runningOrdersEmptyView.setVisibility(View.GONE);
            runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
            runningOrdersSwipeRefreshLayout.setRefreshing(true);*/


            if (runningOrderList != null && runningOrderList.size() > 0) {
                runningOrderList.clear();
                runningOrdersAdapter.notifyDataSetChanged();
            }
            runningOrdersSwipeRefreshLayout.setRefreshing(true);
            getRunningOrderList(chkId, getString(R.string.last_updated_date), "1",stringSearchText,"","");

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
                            if (runningOrderList != null && runningOrderList.size() == 0) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    if (runningOrdersEmptyView.getVisibility() != View.VISIBLE)
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

    public void callAPI(final String searchText) {

        stringSearchText = searchText;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {

                    if (runningOrderList != null) {
                        if (runningOrderList.size() > 0) {
                            runningOrderList.clear();
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    runningOrdersAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                          //  loading = true;
                            showLoader();
                            getRunningOrderList(chkId, getString(R.string.last_updated_date), "1", searchText, "", "");
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
            }
        });
        thread.start();

    }
}

