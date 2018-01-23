package com.accrete.warehouse.fragment.manageConsignment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
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
import com.accrete.warehouse.ViewConsignmentActivity;
import com.accrete.warehouse.adapter.ManageConsignmentAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Consignment;
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

public class ManageConsignmentFragment extends Fragment implements ManageConsignmentAdapter.ManageConsignmentAdapterListener,
        SwipeRefreshLayout.OnRefreshListener {
    private static final String KEY_TITLE = "ManageConsignment";
    private SwipeRefreshLayout manageConsignmentSwipeRefreshLayout;
    private RecyclerView manageConsignmentRecyclerView;
    private TextView manageConsignmentEmptyView;
    private ManageConsignmentAdapter manageConsignmentAdapter;
    private List<Consignment> consignmentList = new ArrayList<>();
    private FloatingActionButton floatingActionButtonPrint;
    private String status, chkId, dataChanged;
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;

    public static ManageConsignmentFragment newInstance(String title) {
        ManageConsignmentFragment f = new ManageConsignmentFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
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
        floatingActionButtonPrint = (FloatingActionButton) rootView.findViewById(R.id.manage_consignment_goods_receipt_print);

        //Disable  FloatingActionButton
        floatingActionButtonPrint.setEnabled(false);
        floatingActionButtonPrint.setVisibility(View.GONE);

        manageConsignmentAdapter = new ManageConsignmentAdapter(getActivity(), consignmentList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        manageConsignmentRecyclerView.setLayoutManager(mLayoutManager);
        manageConsignmentRecyclerView.setHasFixedSize(true);
        manageConsignmentRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageConsignmentRecyclerView.setNestedScrollingEnabled(false);
        manageConsignmentRecyclerView.setAdapter(manageConsignmentAdapter);

        chkId = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);

        floatingActionButtonPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Successful", Toast.LENGTH_SHORT).show();
            }
        });

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
                        getConsignmentsList(chkId, consignmentList.get(totalItemCount - 1).getCreatedTs(), "2");
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
                        getConsignmentsList(chkId, getString(R.string.last_updated_date), "1");
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
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_consignment_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_consignment_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_consignment_fragment));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intentView = new Intent(getActivity(), ViewConsignmentActivity.class);
        intentView.putExtra("iscid", consignmentList.get(position).getIscid());
        startActivity(intentView);
    }

    @Override
    public void onExecute() {
    }

    private void getConsignmentsList(String chkId, final String time, final String traversalValue) {
        String task = getString(R.string.fetch_consignments);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getConsignmentLists(version, key, task, userId, accessToken, chkId,
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
                        for (final Consignment consignment : apiResponse.getData().getConsignments()) {
                            if (consignment != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(consignment.getCreatedTs())) {
                                        consignmentList.add(consignment);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (manageConsignmentSwipeRefreshLayout != null &&
                                            manageConsignmentSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(consignment.getCreatedTs())) {
                                            consignmentList.add(0, consignment);
                                        }
                                    } else {
                                        if (!time.equals(consignment.getCreatedTs())) {
                                            consignmentList.add(consignment);
                                        }
                                    }
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
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
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
                Log.d("SO :cancelled quotation", t.getMessage());
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (consignmentList != null && consignmentList.size() > 0) {
                getConsignmentsList(chkId, consignmentList.get(0).getCreatedTs(), "1");
            } else {
                getConsignmentsList(chkId, getString(R.string.last_updated_date), "1");
            }
            manageConsignmentRecyclerView.setVisibility(View.VISIBLE);
            manageConsignmentEmptyView.setVisibility(View.GONE);
            manageConsignmentSwipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            manageConsignmentRecyclerView.setVisibility(View.GONE);
            manageConsignmentEmptyView.setVisibility(View.VISIBLE);
            manageConsignmentEmptyView.setText(getString(R.string.no_internet_try_later));
            manageConsignmentSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
