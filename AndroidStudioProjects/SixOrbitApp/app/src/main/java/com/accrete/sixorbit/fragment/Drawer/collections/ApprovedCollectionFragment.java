package com.accrete.sixorbit.fragment.Drawer.collections;


import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.PendingCollectionAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.TransactionData;
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
 * A simple {@link Fragment} subclass.
 */
public class ApprovedCollectionFragment extends Fragment implements
        PendingCollectionAdapter.PendingCollectionListener,
        SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private String dataChanged, status;
    private TextView textViewEmpty;
    private boolean loading;
    private PendingCollectionAdapter adapter;
    private LinearLayoutManager mLayoutManager;
    private List<TransactionData> pendingCollectionList = new ArrayList<TransactionData>();
    private LinearLayout bottomLayout;
    private TextView balanceTextView;
    private AlertDialog alertDialog;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private ProgressBar progressBar;
    private DownloadManager downloadManager;

    public ApprovedCollectionFragment() {
        // Required empty public constructor
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_orders, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        bottomLayout = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
        balanceTextView = (TextView) rootView.findViewById(R.id.balance_textView);

        imageView.setVisibility(View.GONE);
        adapter = new PendingCollectionAdapter(getActivity(), pendingCollectionList, this);
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
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) &&
                        pendingCollectionList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getPendingCollections(pendingCollectionList.get(totalItemCount - 1).getCreatedTs(), "2");
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
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Listeners
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    private void getPendingCollections(String time, String traversalValue) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.collections_fetch_myTransactions);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getCollectionsMyTransactions(version, key, task, userId, accessToken, time,
                        traversalValue, "2");
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (TransactionData transactionData : apiResponse.getData().getTransactions()) {
                                    if (transactionData != null) {
                                        if (traversalValue.equals("2")) {
                                            pendingCollectionList.add(transactionData);
                                            dataChanged = "yes";
                                        } else if (traversalValue.equals("1")) {
                                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                                // To remove existing item
                                                if (!time.equals(transactionData.getCreatedTs())) {
                                                    pendingCollectionList.add(0, transactionData);
                                                }
                                            } else {
                                                pendingCollectionList.add(transactionData);
                                            }
                                            dataChanged = "yes";
                                        }
                                    }
                                }
                                loading = false;
                            }
                            //Deleted User
                            else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            } else {
                               // Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            if (pendingCollectionList != null && pendingCollectionList.size() == 0) {
                                textViewEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                textViewEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
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

                            //TODO Total amount value in bottom added on 30th May 2k18
                            if (apiResponse != null && apiResponse.getData() != null &&
                                    apiResponse.getData().getTotal() != null && !apiResponse.getData().getTotal().isEmpty()) {
                                balanceTextView.setText("Total: " + apiResponse.getData().getTotal());
                                bottomLayout.setVisibility(View.VISIBLE);
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
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        downloadVoucherDialog(pendingCollectionList.get(position).getTransactionID(),
                pendingCollectionList.get(position).getTid());
    }

    @Override
    public void onRefresh() {
        loading = true;

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (pendingCollectionList != null && pendingCollectionList.size() > 0) {
                getPendingCollections(pendingCollectionList.get(0).getCreatedTs(), "1");
            } else {
                showLoader();
                getPendingCollections(getString(R.string.last_updated_date), "1");
            }
        } else {
            if (getActivity() != null && isAdded()) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void doRefresh() {
        if (pendingCollectionList != null && pendingCollectionList.size() == 0) {
            loading = true;
            if (getActivity() != null && isAdded()) {
                //calling API
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    if (pendingCollectionList != null && pendingCollectionList.size() > 0) {
                        pendingCollectionList.clear();
                        getPendingCollections(getString(R.string.last_updated_date), "1");
                    } else {
                        getPendingCollections(getString(R.string.last_updated_date), "1");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            loading = true;
            if (getActivity() != null && isAdded()) {
                //calling API
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    if (pendingCollectionList != null && pendingCollectionList.size() > 0) {
                        pendingCollectionList.clear();
                        getPendingCollections(getString(R.string.last_updated_date), "1");
                    } else {
                        getPendingCollections(getString(R.string.last_updated_date), "1");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void downloadVoucherDialog(final String fileName, final String tId) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
                btnYes.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(
                        getActivity().getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, tId, fileName);
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.network_error),
                            Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void downloadPdf(final AlertDialog alertDialog, String tId, final String fileName) {
        try {
            task = getActivity().getString(R.string.collection_download_voucher);
            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.downloadCollectionsMyTransactionsVoucher(version,
                    key, task, userId, accessToken, tId);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals
                                    (getActivity().getString(R.string.not_connected_to_internet))) {
                                alertDialog.dismiss();

                                //Download a file and display in phone's download folder
                                Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                        .mkdirs();
                                downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                                String url = apiResponse.getData().getFilename();
                                Uri uri = Uri.parse(url);
                                DownloadManager.Request request = new DownloadManager.Request(uri)
                                        .setTitle(fileName + "_receipt_voucher" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_receipt_voucher" + ".pdf")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                downloadManager.enqueue(request);
                            } else {
                                Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            }
                        } else if (apiResponse.getSuccessCode().equals("10001")) {
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            alertDialog.dismiss();
                            Constants.logoutWrongCredentials((Activity) getActivity(), apiResponse.getMessage());
                        } else {
                            Toast.makeText((Activity) getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}