package com.accrete.warehouse.fragment.receiveConsignment;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.PurchaseOrderAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PurchaseOrder;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.RecyclerItemForReceiveAgainstPO;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/8/17.
 */

public class ReceiveAgainstPurchaseOrderFragment extends Fragment implements PurchaseOrderAdapter.PurchaseOrderAdapterListener,
        SwipeRefreshLayout.OnRefreshListener, RecyclerItemForReceiveAgainstPO.RecyclerItemTouchHelperListener {
    private static String KEY_TITLE = "receive_po";
    private SwipeRefreshLayout receiveAgainstPurchaseOrderSwipeRefreshLayout;
    private RecyclerView receiveAgainstPurchaseOrderRecyclerView;
    private TextView receiveAgainstPurchaseOrderContainerEmptyView;
    private PurchaseOrderAdapter purchaseOrderAdapter;
    private List<PurchaseOrder> purchaseOrderList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String status, dataChanged;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;

    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;
    public String stringSearchText;

    public static ReceiveAgainstPurchaseOrderFragment newInstance(String title) {
        ReceiveAgainstPurchaseOrderFragment f = new ReceiveAgainstPurchaseOrderFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            menu.clear();
            inflater.inflate(R.menu.search_view, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            if (searchTextView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchTextView, R.drawable.cursor_white); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //  Log.e("OnTextChange", query);
                    return false;
                }

                @Override
                public boolean onQueryTextChange(final String searchText) {
                    //Log.e("OnTextChangeSubmit", newText);
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {


                        @Override
                        public void run() {
                            // do your actual work here
                            if (getActivity() != null && isAdded()) {

                                if (purchaseOrderList != null && purchaseOrderList.size() > 0) {
                                    purchaseOrderList.clear();
                                }
                                stringSearchText = searchText;
                                getPurchaseOrderList(AppUtils.WAREHOUSE_CHK_ID, getString(R.string.last_updated_date), "1",searchText,"","");

                                //   searchInFragment(newText);
                            }
                        }
                    }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask

                    return false;
                }

            };

            searchView.setOnQueryTextListener(queryTextListener);
            super.onCreateOptionsMenu(menu, inflater);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_against_purchase_order, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        receiveAgainstPurchaseOrderSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.receive_against_purchase_order_swipe_refresh_layout);
        receiveAgainstPurchaseOrderRecyclerView = (RecyclerView) rootView.findViewById(R.id.receive_against_purchase_order_recycler_view);
        receiveAgainstPurchaseOrderContainerEmptyView = (TextView) rootView.findViewById(R.id.receive_against_purchase_order_container_empty_view);
        purchaseOrderAdapter = new PurchaseOrderAdapter(getActivity(), purchaseOrderList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        receiveAgainstPurchaseOrderRecyclerView.setLayoutManager(mLayoutManager);
        receiveAgainstPurchaseOrderRecyclerView.setHasFixedSize(true);
        receiveAgainstPurchaseOrderRecyclerView.setItemAnimator(new DefaultItemAnimator());
        receiveAgainstPurchaseOrderRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        receiveAgainstPurchaseOrderRecyclerView.setNestedScrollingEnabled(false);
        receiveAgainstPurchaseOrderRecyclerView.setAdapter(purchaseOrderAdapter);

        receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);

        doRefresh();

        //Scroll Listener
        receiveAgainstPurchaseOrderRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && purchaseOrderList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getPurchaseOrderList(AppUtils.WAREHOUSE_CHK_ID, purchaseOrderList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");

                    } else {
                        if (receiveAgainstPurchaseOrderSwipeRefreshLayout.isRefreshing()) {
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        receiveAgainstPurchaseOrderSwipeRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        receiveAgainstPurchaseOrderContainerEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (receiveAgainstPurchaseOrderContainerEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });

        setSwipeForRecyclerView();
    }

    @Override
    public void onMessageRowClicked(int position, String orderId, String orderText) {
        Intent intent = new Intent(getActivity(), ViewOrderItemsActivity.class);
        intent.putExtra(getString(R.string.purOrId), purchaseOrderList.get(position).getPurorid());
        startActivity(intent);
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_po);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_po));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_po));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_po));
        }
    }

    public void doRefresh() {
        if (purchaseOrderList != null && purchaseOrderList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        receiveAgainstPurchaseOrderContainerEmptyView.setText(getString(R.string.no_data_available));
                        getPurchaseOrderList(AppUtils.WAREHOUSE_CHK_ID, getString(R.string.last_updated_date), "1",stringSearchText,"","");
                    }
                }, 200);
            } else {
                receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.GONE);
                receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.VISIBLE);
                receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.GONE);
                receiveAgainstPurchaseOrderContainerEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getPurchaseOrderList(String chkId, final String time, final String traversalValue, String searchValue, String startDate, String endDate) {        String task = getString(R.string.fetch_purchase_orders);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getConsignmentLists(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID),
                time, traversalValue,searchValue,"","");
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
                        for (final PurchaseOrder purchaseOrder : apiResponse.getData().getPurchaseOrders()) {
                            if (purchaseOrder != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(purchaseOrder.getCreatedTs())) {
                                        if(!purchaseOrder.getPurorsid().equals("3")) {
                                            purchaseOrderList.add(purchaseOrder);
                                        }

                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                   /* if (receiveAgainstPurchaseOrderSwipeRefreshLayout != null &&
                                            receiveAgainstPurchaseOrderSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(purchaseOrder.getCreatedTs())) {
                                            if(!purchaseOrder.getPurorsid().equals("3")) {
                                                purchaseOrderList.add(0, purchaseOrder);

                                            }
                                        }
                                    } else {
                                        if (!time.equals(purchaseOrder.getCreatedTs())) {
                                            if(!purchaseOrder.getPurorsid().equals("3")) {
                                                purchaseOrderList.add(purchaseOrder);
                                            }                                        }
                                    }*/
                                    purchaseOrderList.add(purchaseOrder);
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (purchaseOrderList != null && purchaseOrderList.size() == 0) {
                            receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.VISIBLE);
                            receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.GONE);
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.GONE);
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (receiveAgainstPurchaseOrderSwipeRefreshLayout != null &&
                                receiveAgainstPurchaseOrderSwipeRefreshLayout.isRefreshing()) {
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            purchaseOrderAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                purchaseOrderAdapter.notifyDataSetChanged();
                                receiveAgainstPurchaseOrderRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (purchaseOrderList != null && purchaseOrderList.size() == 0) {
                            receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.VISIBLE);
                            receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.GONE);
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.GONE);
                        } else {
                            receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.GONE);
                            receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.VISIBLE);
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        if (receiveAgainstPurchaseOrderSwipeRefreshLayout != null && receiveAgainstPurchaseOrderSwipeRefreshLayout.isRefreshing()) {
                            receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            purchaseOrderAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                purchaseOrderAdapter.notifyDataSetChanged();
                                receiveAgainstPurchaseOrderRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (receiveAgainstPurchaseOrderSwipeRefreshLayout != null && receiveAgainstPurchaseOrderSwipeRefreshLayout.isRefreshing()) {
                        receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            /*if (purchaseOrderList != null && purchaseOrderList.size() > 0) {
                getPurchaseOrderList(purchaseOrderList.get(0).getCreatedTs(), "1");
            } else {
                getPurchaseOrderList(getString(R.string.last_updated_date), "1");
            }*/
          //receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.VISIBLE);
           // receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.GONE);
            if (purchaseOrderList!= null && purchaseOrderList.size() > 0) {
                purchaseOrderList.clear();
                purchaseOrderAdapter.notifyDataSetChanged();
            }
            receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(true);
            getPurchaseOrderList(AppUtils.WAREHOUSE_CHK_ID, getString(R.string.last_updated_date), "1",stringSearchText,"","");

            //  receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.VISIBLE);

        } else {
            receiveAgainstPurchaseOrderRecyclerView.setVisibility(View.GONE);
            receiveAgainstPurchaseOrderContainerEmptyView.setVisibility(View.VISIBLE);
            receiveAgainstPurchaseOrderSwipeRefreshLayout.setVisibility(View.GONE);
            receiveAgainstPurchaseOrderContainerEmptyView.setText(getString(R.string.no_internet_try_later));
            receiveAgainstPurchaseOrderSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void setSwipeForRecyclerView() {
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemForReceiveAgainstPO(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(receiveAgainstPurchaseOrderRecyclerView);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
       // Toast.makeText(getActivity(), "Print coming soon..", Toast.LENGTH_SHORT).show();
        if(!purchaseOrderList.get(position).getPurorsid().equals("3")) {
            Intent intent = new Intent(getActivity(), POReceiveConsignmentActivity.class);
            intent.putExtra(getString(R.string.purOrId), purchaseOrderList.get(position).getPurorid());
            intent.putExtra(getString(R.string.flag_stock),"po");
            startActivity(intent);
            purchaseOrderAdapter.notifyDataSetChanged();
        }else {
            Toast.makeText(getActivity(), "Consignment already received", Toast.LENGTH_SHORT).show();
        }
    }
}
