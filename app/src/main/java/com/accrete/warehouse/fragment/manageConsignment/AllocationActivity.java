package com.accrete.warehouse.fragment.manageConsignment;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.AllocationItemAdapter;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersTabFragment;
import com.accrete.warehouse.model.AllocateConsignment;
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
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 7/3/18.
 */

public class AllocationActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AllocationItemAdapter.AllocationItemAdapterListener {

    private Toolbar toolbar;
    private SwipeRefreshLayout activityAllocationSwipeRefreshLayout;
    private RecyclerView activityAllocationRecyclerView;
    private TextView activityAllocationEmptyView;
    private ImageView activityAllocationImageViewLoader;
    private AllocationItemAdapter allocationAdapter;
    private LinearLayoutManager mLayoutManager;
    private List<AllocateConsignment> allocationItemList = new ArrayList<>();
    private String chkId;
    private String stringSearchText;
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private String dataChanged, status;
    private Timer timer;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private String iscId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allocation);
        chkId = AppPreferences.getWarehouseDefaultCheckId(getApplicationContext(), AppUtils.WAREHOUSE_CHK_ID);
        if (getIntent() != null && getIntent().hasExtra("iscid")) {
            iscId = getIntent().getStringExtra("iscid");
        }
        findViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.search_view, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

            if (searchTextView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
                searchEditText.setTextColor(getResources().getColor(R.color.white));
                searchEditText.setHintTextColor(getResources().getColor(R.color.white));
                searchEditText.setHint("Search...");
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
                            stringSearchText = searchText;
                            callAPI(searchText);
                        }

                    }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask
                    searchView.setOnQueryTextListener(queryTextListener);
                    return false;
                }

            };
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;

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

    private void findViews() {
        toolbar = (Toolbar)findViewById( R.id.toolbar );
        activityAllocationSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.activity_allocation_swipe_refresh_layout);
        activityAllocationRecyclerView = (RecyclerView)findViewById(R.id.activity_allocation_recycler_view);
        activityAllocationEmptyView = (TextView)findViewById(R.id.activity_allocation_empty_view);
        activityAllocationImageViewLoader = (ImageView)findViewById(R.id.activity_allocation_imageView_loader);

        toolbar.setTitle(getString(R.string.allocation));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do something you want
                finish();
            }
        });


        allocationAdapter = new AllocationItemAdapter(this, allocationItemList, this);
        mLayoutManager = new LinearLayoutManager(this);
        activityAllocationRecyclerView.setLayoutManager(mLayoutManager);
        activityAllocationRecyclerView.setHasFixedSize(true);
        //   DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(runningOrdersRecyclerView.getContext(),
        // mLayoutManager.getOrientation());
        //   runningOrdersRecyclerView.addItemDecoration(dividerItemDecoration);
        activityAllocationRecyclerView.setNestedScrollingEnabled(false);
        activityAllocationRecyclerView.setAdapter(allocationAdapter);
        activityAllocationSwipeRefreshLayout.setOnRefreshListener(this);
        /*runningOrdersSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                doRefresh();
            }

        });*/


        //Scroll Listener
        activityAllocationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && allocationItemList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getApplicationContext()).equals(getString(R.string.not_connected_to_internet))) {
                            showLoader();
                            //  getAllocationList(runningOrderList.get(totalItemCount - 1).getCreatedTs(), "2");
                            getAllocationList(chkId, allocationItemList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");

                    } else {
                        if (activityAllocationSwipeRefreshLayout.isRefreshing()) {
                            activityAllocationSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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
    }




    private void hideLoader() {

            if (activityAllocationImageViewLoader != null && activityAllocationImageViewLoader.getVisibility() == View.VISIBLE) {
                activityAllocationImageViewLoader.setVisibility(View.GONE);
                //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            if (allocationItemList != null && allocationItemList.size() == 0) {
                                if (activityAllocationImageViewLoader.getVisibility() == View.GONE) {
                                    if (activityAllocationEmptyView.getVisibility() != View.VISIBLE)
                                        activityAllocationImageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Ion.with(activityAllocationImageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }

                    }
                });
            }
        });
        thread.start();
    }


    public void doRefresh() {
        if (allocationItemList != null && allocationItemList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(this);
            if (!loading && !status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (allocationItemList != null && allocationItemList.size() == 0) {
                        }

                            showLoader();
                            getAllocationList(chkId, getString(R.string.last_updated_date), "1", stringSearchText, "", "");

                    }
                }, 200);
            } else {
                activityAllocationRecyclerView.setVisibility(View.GONE);
                activityAllocationEmptyView.setVisibility(View.VISIBLE);
                activityAllocationEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getAllocationList(String chkId, final String time, final String traversalValue,
                                   String searchValue, String startDate, String endDate) {
        try {
            task = getString(R.string.task_allocation);
            String chkid = null;
            activityAllocationSwipeRefreshLayout.setRefreshing(false);
            if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
                chkid = AppPreferences.getWarehouseDefaultCheckId(this, AppUtils.WAREHOUSE_CHK_ID);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getAllocationConsignments(version, key, task, userId, accessToken, chkId,
                    time, traversalValue, searchValue, startDate, endDate,iscId);
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

                            for (final AllocateConsignment allocateConsignment : apiResponse.getData().getAllocateConsignment()) {
                                if (allocateConsignment != null) {
                                    if (traversalValue.equals("2")) {
                                        if (!time.equals(allocateConsignment.getCreatedTs())) {
                                            allocationItemList.add(allocateConsignment);
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

                                        allocationItemList.add(allocateConsignment);
                                        dataChanged = "yes";
                                    }
                                }
                            }

                            loading = false;
                            if (allocationItemList != null && allocationItemList.size() == 0) {
                                activityAllocationEmptyView.setVisibility(View.VISIBLE);
                                activityAllocationEmptyView.setText("No data available");
                                activityAllocationRecyclerView.setVisibility(View.VISIBLE);
                                activityAllocationSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                // runningOrdersCount.setVisibility(View.GONE);
                            } else {
                                activityAllocationEmptyView.setVisibility(View.GONE);
                                activityAllocationSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                activityAllocationRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (activityAllocationSwipeRefreshLayout != null &&
                                    activityAllocationSwipeRefreshLayout.isRefreshing()) {
                                activityAllocationSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversalValue.equals("2")) {
                                allocationAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    allocationAdapter.notifyDataSetChanged();
                                    activityAllocationRecyclerView.smoothScrollToPosition(0);
                                }
                            }
                            if (allocationItemList != null && allocationItemList.size() > 0) {
                                // runningOrdersCount.setVisibility(View.VISIBLE);
                                if (allocationItemList.size() == 1) {
                                    // runningOrdersCount.setText(runningOrderList.size() + " Running Order");
                                } else {
                                    //  runningOrdersCount.setText(runningOrderList.size() + " Running Orders");
                                }
                            }

                        } else {
                            if (allocationItemList != null && allocationItemList.size() == 0) {
                                activityAllocationEmptyView.setVisibility(View.VISIBLE);
                                activityAllocationEmptyView.setText("No data available");
                                activityAllocationRecyclerView.setVisibility(View.VISIBLE);
                                activityAllocationSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                //  runningOrdersCount.setVisibility(View.GONE);
                            } else {
                                activityAllocationEmptyView.setVisibility(View.GONE);
                                activityAllocationSwipeRefreshLayout.setVisibility(View.VISIBLE);
                                activityAllocationRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (activityAllocationSwipeRefreshLayout != null &&
                                    activityAllocationSwipeRefreshLayout.isRefreshing()) {
                                activityAllocationSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversalValue.equals("2")) {
                                allocationAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    allocationAdapter.notifyDataSetChanged();
                                    activityAllocationRecyclerView.smoothScrollToPosition(0);
                                }
                            }

                            if (apiResponse.getSuccessCode().equals("10001")) {
                                activityAllocationEmptyView.setText(getString(R.string.no_data_available));
                                activityAllocationRecyclerView.setVisibility(View.GONE);
                                activityAllocationEmptyView.setVisibility(View.VISIBLE);
                                //   runningOrdersCount.setVisibility(View.GONE);

                            } else if (apiResponse.getSuccessCode().equals("20004")) {

                            }
                        }
                        if (activityAllocationSwipeRefreshLayout != null && activityAllocationSwipeRefreshLayout.isRefreshing()) {
                            activityAllocationSwipeRefreshLayout.setRefreshing(false);
                        }

                            hideLoader();

                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                        activityAllocationSwipeRefreshLayout.setRefreshing(false);
                        hideLoader();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(this);
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
         /*   if (runningOrderList != null && runningOrderList.size() > 0) {
                getAllocationList(runningOrderList.get(0).getCreatedTs(), "1");
            } else {
                getAllocationList(getString(R.string.last_updated_date), "1");
            }*/
          /*  runningOrdersRecyclerView.setVisibility(View.VISIBLE);
            runningOrdersEmptyView.setVisibility(View.GONE);
            runningOrdersSwipeRefreshLayout.setVisibility(View.VISIBLE);
            runningOrdersSwipeRefreshLayout.setRefreshing(true);*/


            if (allocationItemList != null && allocationItemList.size() > 0) {
                allocationItemList.clear();
                allocationAdapter.notifyDataSetChanged();
            }
            activityAllocationSwipeRefreshLayout.setRefreshing(true);
            getAllocationList(chkId, getString(R.string.last_updated_date), "1",stringSearchText,"","");

        } else {

            activityAllocationRecyclerView.setVisibility(View.VISIBLE);
            activityAllocationEmptyView.setVisibility(View.VISIBLE);
            activityAllocationSwipeRefreshLayout.setVisibility(View.VISIBLE);
            activityAllocationEmptyView.setText(getString(R.string.no_internet_try_later));
            activityAllocationSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public void callAPI(final String searchText) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                    if (allocationItemList != null) {
                        if (allocationItemList.size() > 0) {
                            allocationItemList.clear();
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    allocationAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        status = NetworkUtil.getConnectivityStatusString(getApplicationContext());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            //  loading = true;
                            showLoader();
                            getAllocationList(chkId, getString(R.string.last_updated_date), "1", searchText, "", "");
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }

            }
        });
        thread.start();

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

}
