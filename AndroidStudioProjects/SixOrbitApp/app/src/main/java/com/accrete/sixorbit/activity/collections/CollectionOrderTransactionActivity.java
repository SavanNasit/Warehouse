package com.accrete.sixorbit.activity.collections;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CollectionOrderTransactionAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CollectionTransactionData;
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
 * Created by {Anshul} on 4/6/18.
 */

public class CollectionOrderTransactionActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        CollectionOrderTransactionAdapter.MyCollectionsListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private CollectionOrderTransactionAdapter adapter;
    private TextView textViewEmpty;
    private List<CollectionTransactionData> collectionTransactionDataList = new ArrayList<CollectionTransactionData>();
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private String dataChanged, chkoId, jocId, invId, orderIdText, jobCardText, invoiceIdText;
    private Toolbar toolbar;
    private AlertDialog alertDialog;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private ProgressBar progressBar;
    private TextView titleTextView;

    public CollectionOrderTransactionActivity() {
        // Required empty public constructor
    }

    private void hideLoader() {
        if (CollectionOrderTransactionActivity.this != null) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_order_transaction);
        findViews();
    }

    private void findViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction History");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (getIntent() != null) {
            if (getIntent().hasExtra(getString(R.string.chkoid))) {
                chkoId = getIntent().getStringExtra(getString(R.string.chkoid));
            } else {
                chkoId = "";
            }
            if (getIntent().hasExtra(getString(R.string.jocid))) {
                jocId = getIntent().getStringExtra(getString(R.string.jocid));
            } else {
                jocId = "";
            }
            if (getIntent().hasExtra(getString(R.string.invid))) {
                invId = getIntent().getStringExtra(getString(R.string.invid));
            } else {
                invId = "";
            }

            // Title of toolbar
            if (getIntent().hasExtra(getString(R.string.order_id_text))) {
                orderIdText = getIntent().getStringExtra(getString(R.string.order_id_text));
                toolbar.setTitle("Transaction History of " + orderIdText);
            } else if (getIntent().hasExtra(getString(R.string.jobcard_id_text))) {
                jobCardText = getIntent().getStringExtra(getString(R.string.jobcard_id_text));
                toolbar.setTitle("Transaction History of " + jobCardText);
            } else if (getIntent().hasExtra(getString(R.string.invoice_id_text))) {
                invoiceIdText = getIntent().getStringExtra(getString(R.string.invoice_id_text));
                toolbar.setTitle("Transaction History of " + invoiceIdText);
            }
        }

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        imageView = (ImageView) findViewById(R.id.imageView);
        textViewEmpty = (TextView) findViewById(R.id.textView_empty);

        adapter = new CollectionOrderTransactionAdapter(CollectionOrderTransactionActivity.this, collectionTransactionDataList, this);
        mLayoutManager = new LinearLayoutManager(CollectionOrderTransactionActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) &&
                        collectionTransactionDataList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(CollectionOrderTransactionActivity.this).equals(getString(R.string.not_connected_to_internet))) {
                        getCollectionsHistory(collectionTransactionDataList.get(totalItemCount - 1).getCreatedTs(),
                                "2", chkoId, jocId, invId);
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(CollectionOrderTransactionActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

        if (!NetworkUtil.getConnectivityStatusString(CollectionOrderTransactionActivity.this)
                .equals(getString(R.string.not_connected_to_internet))) {
            showLoader();
            getCollectionsHistory(getString(R.string.last_updated_date), "1",
                    chkoId, jocId, invId);
        } else {
            Toast.makeText(CollectionOrderTransactionActivity.this,
                    getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (CollectionOrderTransactionActivity.this != null) {
                    CollectionOrderTransactionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (CollectionOrderTransactionActivity.this != null) {
                                if (imageView.getVisibility() == View.GONE) {
                                    if (swipeRefreshLayout != null &&
                                            swipeRefreshLayout.getVisibility() != View.VISIBLE) {
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                }
                                //Disable Touch
                                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + CollectionOrderTransactionActivity.this.getPackageName() + "/" + R.raw.loader)
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
    public void onRefresh() {
        loading = true;

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(CollectionOrderTransactionActivity.this).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (collectionTransactionDataList != null && collectionTransactionDataList.size() > 0) {
                showLoader();
                getCollectionsHistory(collectionTransactionDataList.get(0).getCreatedTs(), "1",
                        chkoId, jocId, invId);
            } else {
                showLoader();
                getCollectionsHistory(getString(R.string.last_updated_date), "1",
                        chkoId, jocId, invId);
            }
        } else {
            if (CollectionOrderTransactionActivity.this != null) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(CollectionOrderTransactionActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCollectionsHistory(String time, String traversalValue,
                                       String chkoId, String jocId, String invId) {
        try {
            if (CollectionOrderTransactionActivity.this != null) {
                task = getString(R.string.collection_order_transaction_history);
                userId = AppPreferences.getUserId(CollectionOrderTransactionActivity.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(CollectionOrderTransactionActivity.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(CollectionOrderTransactionActivity.this, AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getCollectionsTransactionsHistoryList(version, key, task, userId,
                        accessToken, time, traversalValue, chkoId, jocId, invId);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (CollectionTransactionData collectionTransactionData :
                                        apiResponse.getData().getCollectionTransactionData()) {
                                    if (collectionTransactionData != null) {
                                        if (traversalValue.equals("2")) {
                                            collectionTransactionDataList.add(collectionTransactionData);
                                            dataChanged = "yes";
                                        } else if (traversalValue.equals("1")) {
                                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                                // To remove existing item
                                                if (!time.equals(collectionTransactionData.getCreatedTs())) {
                                                    collectionTransactionDataList.add(0, collectionTransactionData);
                                                }
                                            } else {
                                                collectionTransactionDataList.add(collectionTransactionData);
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
                                Constants.logoutWrongCredentials(CollectionOrderTransactionActivity.this, apiResponse.getMessage());
                            }
                            if (collectionTransactionDataList != null &&
                                    collectionTransactionDataList.size() == 0) {
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
                        if (CollectionOrderTransactionActivity.this != null) {
                            Toast.makeText(CollectionOrderTransactionActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public void onLongClicked(int position) {
        if (collectionTransactionDataList.get(position).getInvtsid() != null &&
                !collectionTransactionDataList.get(position).getInvtsid().isEmpty() &&
                collectionTransactionDataList.get(position).getInvtsid().equals("3")) {
            if (alertDialog == null || !alertDialog.isShowing()) {
                deleteDialog(position);
            }
        }
    }

    private void deleteDialog(int position) {
        final View dialogView = View.inflate(CollectionOrderTransactionActivity.this, R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionOrderTransactionActivity.this);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        titleTextView.setText("Delete Transaction");
        downloadConfirmMessage.setText("Are you sure you want to delete this transaction?");
        btnYes.setText("Yes, delete");

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
                if (!NetworkUtil.getConnectivityStatusString(CollectionOrderTransactionActivity.this).equals(
                        getString(R.string.not_connected_to_internet))) {

                    //Delete
                    deleteTransactionAPI(alertDialog, collectionTransactionDataList.get(position).getInvtid());
                } else {
                    Toast.makeText(CollectionOrderTransactionActivity.this, getString(R.string.network_error),
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

    private void deleteTransactionAPI(final AlertDialog alertDialog, String invtId) {
        try {
            task = getString(R.string.collection_delete_transaction);
            if (AppPreferences.getIsLogin(this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(this, AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.deleteTransaction(version,
                    key, task, userId, accessToken, invtId);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            if (CollectionOrderTransactionActivity.this != null) {
                                alertDialog.dismiss();
                                Toast.makeText(CollectionOrderTransactionActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        } else if (apiResponse.getSuccessCode().equals("10001")) {
                            if (CollectionOrderTransactionActivity.this != null) {
                                alertDialog.dismiss();
                                Toast.makeText(CollectionOrderTransactionActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            if (CollectionOrderTransactionActivity.this != null) {
                                //Logout
                                alertDialog.dismiss();
                                Constants.logoutWrongCredentials(CollectionOrderTransactionActivity.this,
                                        apiResponse.getMessage());
                            }
                        } else {
                            if (CollectionOrderTransactionActivity.this != null) {
                                Toast.makeText(CollectionOrderTransactionActivity.this,
                                        apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
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
                    if (CollectionOrderTransactionActivity.this != null) {
                        Toast.makeText(CollectionOrderTransactionActivity.this,
                                getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doRefresh() {
        if (!NetworkUtil.getConnectivityStatusString(CollectionOrderTransactionActivity.this)
                .equals(getString(R.string.not_connected_to_internet))) {
            if (collectionTransactionDataList != null && collectionTransactionDataList.size() > 0) {
                collectionTransactionDataList.clear();
            }
            showLoader();
            getCollectionsHistory(getString(R.string.last_updated_date), "1",
                    chkoId, jocId, invId);
        } else {
            Toast.makeText(CollectionOrderTransactionActivity.this,
                    getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }
}
