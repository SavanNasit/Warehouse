package com.accrete.warehouse.fragment.manageConsignment;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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

import com.accrete.warehouse.ManageConsignmentDetailsActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ManageConsignmentAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Consignment;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
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
 * A simple {@link Fragment} subclass.
 */
public class ToBeApprovedConsignmentFragment extends Fragment implements
        ManageConsignmentAdapter.ManageConsignmentAdapterListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String KEY_TITLE = "ManageConsignment";
    private SwipeRefreshLayout manageConsignmentSwipeRefreshLayout;
    private RecyclerView manageConsignmentRecyclerView;
    private TextView manageConsignmentEmptyView;
    private ManageConsignmentAdapter manageConsignmentAdapter;
    private List<Consignment> consignmentList = new ArrayList<>();
    private String status, chkId, dataChanged;
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;
    private String stringSearchText;

    public static ToBeApprovedConsignmentFragment newInstance(String title) {
        ToBeApprovedConsignmentFragment f = new ToBeApprovedConsignmentFragment();
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

                                if (consignmentList != null && consignmentList.size() > 0) {
                                    consignmentList.clear();
                                }
                                stringSearchText = searchText;
                                getConsignmentsList(chkId, getString(R.string.last_updated_date), "1", searchText, "", "");

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
        View rootView = inflater.inflate(R.layout.fragment_manage_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        manageConsignmentSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_consignment_swipe_refresh_layout);
        manageConsignmentRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_consignment_recycler_view);
        manageConsignmentEmptyView = (TextView) rootView.findViewById(R.id.manage_consignment_empty_view);


        manageConsignmentAdapter = new ManageConsignmentAdapter(getActivity(), consignmentList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        manageConsignmentRecyclerView.setLayoutManager(mLayoutManager);
        manageConsignmentRecyclerView.setHasFixedSize(true);
        manageConsignmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageConsignmentRecyclerView.setNestedScrollingEnabled(false);
        manageConsignmentRecyclerView.setAdapter(manageConsignmentAdapter);

        chkId = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);


        //Scroll Listener
        manageConsignmentRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && consignmentList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getConsignmentsList(chkId, consignmentList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");
                    } else {
                        if (manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                            manageConsignmentSwipeRefreshLayout.setRefreshing(false);
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

        manageConsignmentSwipeRefreshLayout.setOnRefreshListener(this);
        doRefresh();

        //Load data after getting connection
        manageConsignmentEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manageConsignmentEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });
    }

    public void doRefresh() {
        if (consignmentList != null && consignmentList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        manageConsignmentEmptyView.setText(getString(R.string.no_data_available));
                        getConsignmentsList(chkId, getString(R.string.last_updated_date), "1", "", "", "");

                    }
                }, 200);
            } else {
                manageConsignmentRecyclerView.setVisibility(View.GONE);
                manageConsignmentEmptyView.setVisibility(View.VISIBLE);
                manageConsignmentEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intentView = new Intent(getActivity(), ManageConsignmentDetailsActivity.class);
        intentView.putExtra("iscid", consignmentList.get(position).getIscid());
        intentView.putExtra("iscsid", consignmentList.get(position).getIscsid());
        startActivity(intentView);
        /*ChooseEventsForManageConsignmentFragment chooseEventsForManageConsignmentFragment =
                new ChooseEventsForManageConsignmentFragment();
        //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.manage_consignment_container, chooseEventsForManageConsignmentFragment)
                .addToBackStack(null).commit();

        Bundle bundle = new Bundle();
        bundle.putString("iscid", consignmentList.get(position).getIscid());
        bundle.putString("iscsid", consignmentList.get(position).getIscsid());
        chooseEventsForManageConsignmentFragment.setArguments(bundle);*/
    }

    @Override
    public void refreshData() {
        if (consignmentList != null && consignmentList.size() > 0) {
            consignmentList.clear();
            manageConsignmentAdapter.notifyDataSetChanged();
        }
        doRefresh();
    }

    private void getConsignmentsList(String chkId, final String time, final String traversalValue,
                                     String searchValue, String startDate, String endDate) {
        String task = getString(R.string.task_to_be_approved_consignments);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        //TODO Here we're passing 6 as status to return to be approved consignments
        Call<ApiResponse> call = apiService.getToBeApprovedConsignmentLists(version, key, task, userId, accessToken, chkId,
                time, traversalValue, searchValue, startDate, endDate, "6");
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
                        for (final Consignment consignment : apiResponse.getData().getConsignments()) {
                            if (consignment != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(consignment.getCreatedTs())) {
                                        consignmentList.add(consignment);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    consignmentList.add(consignment);
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (consignmentList != null && consignmentList.size() == 0) {
                            manageConsignmentEmptyView.setVisibility(View.VISIBLE);
                            manageConsignmentRecyclerView.setVisibility(View.GONE);
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            manageConsignmentEmptyView.setVisibility(View.GONE);
                            manageConsignmentRecyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (manageConsignmentSwipeRefreshLayout != null &&
                                manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                            manageConsignmentSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            manageConsignmentAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                manageConsignmentAdapter.notifyDataSetChanged();
                                manageConsignmentRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (consignmentList != null && consignmentList.size() == 0) {
                            manageConsignmentEmptyView.setVisibility(View.VISIBLE);
                            manageConsignmentRecyclerView.setVisibility(View.GONE);
                            //       customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            manageConsignmentEmptyView.setVisibility(View.GONE);
                            manageConsignmentRecyclerView.setVisibility(View.VISIBLE);
                            //       customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (manageConsignmentSwipeRefreshLayout != null && manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                            manageConsignmentSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            manageConsignmentAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                //  manageConsignmentRecyclerView.smoothScrollToPosition(manageConsignmentAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                manageConsignmentAdapter.notifyDataSetChanged();
                                manageConsignmentRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (manageConsignmentSwipeRefreshLayout != null && manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                        manageConsignmentSwipeRefreshLayout.setRefreshing(false);
                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("MC :manage consignment", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            //  if (consignmentList != null && consignmentList.size() > 0) {
            //  getConsignmentsList(chkId, consignmentList.get(0).getCreatedTs(), "1","");
            //   } else {
            if (consignmentList != null && consignmentList.size() > 0) {
                consignmentList.clear();
                manageConsignmentAdapter.notifyDataSetChanged();
            }
            manageConsignmentSwipeRefreshLayout.setRefreshing(true);
            getConsignmentsList(chkId, getString(R.string.last_updated_date), "1", stringSearchText, "", "");
            //  }
            // manageConsignmentRecyclerView.setVisibility(View.VISIBLE);
            //  manageConsignmentEmptyView.setVisibility(View.GONE);
            //  manageConsignmentSwipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);


        } else {
            manageConsignmentRecyclerView.setVisibility(View.GONE);
            manageConsignmentEmptyView.setVisibility(View.VISIBLE);
            manageConsignmentEmptyView.setText(getString(R.string.no_internet_try_later));
            manageConsignmentSwipeRefreshLayout.setRefreshing(false);
        }
    }
}

