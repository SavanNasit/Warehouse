package com.accrete.warehouse.fragment.manageConsignment;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentFragment extends Fragment implements ManageConsignmentAdapter.ManageConsignmentAdapterListener,
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

    public static ManageConsignmentFragment newInstance(String title) {
        ManageConsignmentFragment f = new ManageConsignmentFragment();
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



   /* private void downloadPdf(final AlertDialog alertDialog, final String cuId, final String avid, final String date,
                            final String fileName) {
        task = mContext.getString(R.string.customer_wallet_download_voucher);
        if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadCustomerWalletVoucher(version, key, task, userId, accessToken, cuId, avid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_purchase" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_purchase" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(context, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }
*/




   /* public void downloadDialog(final String cuId, final String avid, final String date, final String fileName) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
      final AlertDialog  alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
              //  btnYes.setEnabled(false);
              //  progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, cuId, avid, date, fileName);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }
*/

    @Override
    public void onMessageRowClicked(int position) {
    /*    Intent intentView = new Intent(getActivity(), ViewConsignmentActivity.class);
        intentView.putExtra("iscid", consignmentList.get(position).getIscid());
        startActivity(intentView);*/
        ChooseEventsForManageConsignmentFragment chooseEventsForManageConsignmentFragment = new ChooseEventsForManageConsignmentFragment();
        //  getFragmentManager().beginTransaction().replace(R.id.receive_consignment_container, receiveDirectlyFragment).commitAllowingStateLoss();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.manage_consignment_container, chooseEventsForManageConsignmentFragment).addToBackStack(null).commit();

        Bundle bundle = new Bundle();
        bundle.putString("iscid", consignmentList.get(position).getIscid());
        bundle.putString("iscsid", consignmentList.get(position).getIscsid());
        chooseEventsForManageConsignmentFragment.setArguments(bundle);
    }

    @Override
    public void onExecute() {
    }

    private void getConsignmentsList(String chkId, final String time, final String traversalValue, String searchValue, String startDate, String endDate) {
        String task = getString(R.string.fetch_consignments);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getConsignmentLists(version, key, task, userId, accessToken, chkId,
                time, traversalValue, searchValue, startDate, endDate);
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
                                    //  if (manageConsignmentSwipeRefreshLayout != null &&
                                    // manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                                    // To remove duplicacy of a new item
                                    // if (!time.equals(consignment.getCreatedTs())) {
                                    //  consignmentList.add(0, consignment);
                                    //  }
                                    //  } else {
                                    //  if (!time.equals(consignment.getCreatedTs())) {
                                    consignmentList.add(consignment);
                                    //  }
                                    //   }
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
