package com.accrete.sixorbit.fragment.Drawer.orders;


import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.order.EditOrderActivity;
import com.accrete.sixorbit.activity.order.OrdersTabActivity;
import com.accrete.sixorbit.adapter.ApproveOrderAdapter;
import com.accrete.sixorbit.adapter.LongClickActionsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Order;
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
public class OrderHistoryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ApproveOrderAdapter.ApproveOrderListener, LongClickActionsAdapter.LongClickActionsAdapterListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private String dataChanged, status;
    private TextView textViewEmpty;
    private ApproveOrderAdapter orderAdapter;
    private List<Order> orderArrayList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager, mActionsLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private AlertDialog dialogActions;
    private String[] actionsArr = new String[]{"Cancel Order", "Delete Order", "Close Sales Order",
            "Edit Order", "Download Sales Order", "View Details"};
    private LongClickActionsAdapter actionsAdapter;
    private AlertDialog alertDialog;
    private TextView downloadConfirmMessage, titleTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private ProgressBar progressBar;
    private DownloadManager downloadManager;
    private DatabaseHandler databaseHandler;
    private String searchText;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    private void hideLoader() {
        if (getActivity() != null) {
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
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                //  if (orderArrayList != null && orderArrayList.size() == 0) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                                //  }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    @Override
    public void onLongClicked(int position) {
        if (dialogActions == null || !dialogActions.isShowing()) {
            dialogActionsItems(position);
        }
    }

    private void findViews(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);

        databaseHandler = new DatabaseHandler(getActivity());
        imageView.setVisibility(View.GONE);

        //Adapter
        orderAdapter = new ApproveOrderAdapter(getActivity(), orderArrayList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(orderAdapter);

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && orderArrayList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getOrdersList(orderArrayList.get(totalItemCount - 1).getCreatedTs(), "2");
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
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //   customerOrderFabAdd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        doRefresh("");
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    public void doRefresh(String searchQuery) {
        Thread thread = null;
        try {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        searchText = searchQuery;
                        if (orderArrayList != null) {
                            if (orderArrayList.size() > 0) {
                                orderArrayList.clear();
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        orderAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            status = NetworkUtil.getConnectivityStatusString(getActivity());
                            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                                loading = true;
                                showLoader();
                                getOrdersList(getString(R.string.last_updated_date), "1");
                            } else {
                                recyclerView.setVisibility(View.GONE);
                                textViewEmpty.setVisibility(View.VISIBLE);
                                textViewEmpty.setText(getString(R.string.no_internet_try_later));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        thread.start();
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null && isAdded()) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (orderArrayList != null && orderArrayList.size() > 0) {
                    orderArrayList.clear();
                    orderAdapter.notifyDataSetChanged();
                }
                showLoader();
                getOrdersList(getString(R.string.last_updated_date), "1");

                recyclerView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(true);

            } else {
                recyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText(getString(R.string.no_internet_try_later));
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (dialogActions != null && dialogActions.isShowing()) {
            dialogActions.dismiss();
        }
        navigateDetailScreen(orderArrayList.get(position).getChkoid(),
                orderArrayList.get(position).getCheckpointOrderId(),
                orderArrayList.get(position).getCustomerId());
    }

    private void navigateDetailScreen(String orderId, String orderText, String cuId) {
        Intent intent = new Intent(getActivity(), OrdersTabActivity.class);
        intent.putExtra(getString(R.string.order_id), orderId);
        intent.putExtra(getString(R.string.order_id_text), orderText);
        intent.putExtra(getString(R.string.cuid), cuId);
        startActivity(intent);
    }

    private void navigateEditScreen(String orderId, String orderText) {
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.order_edit_permission))) {
            Intent intent = new Intent(getActivity(), EditOrderActivity.class);
            intent.putExtra(getString(R.string.order_id), orderId);
            intent.putExtra(getString(R.string.order_id_text), orderText);
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "You have no permission to edit any order.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void getOrdersList(final String time, final String traversalValue) {
        task = getString(R.string.task_fetch_order_history);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getOrdersList(version, key, task, userId, accessToken,
                time, traversalValue, searchText);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse != null && apiResponse.getSuccess()) {
                        if (apiResponse.getData() != null &&
                                apiResponse.getData().getAllHistoryOrders() != null) {
                            for (final Order order : apiResponse.getData().getAllHistoryOrders()) {
                                if (apiResponse.getData().getAllHistoryOrders() != null) {
                                    /*if (traversalValue.equals("2")) {
                                        if (!time.equals(order.getCreatedTs())) {
                                            orderArrayList.add(order);
                                        }
                                        dataChanged = "yes";
                                    } else if (traversalValue.equals("1")) {
                                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                            // To remove duplicacy of a new item
                                            if (!time.equals(order.getCreatedTs())) {
                                                orderArrayList.add(0, order);
                                            }
                                        } else {
                                            if (!time.equals(order.getCreatedTs())) {
                                                orderArrayList.add(order);
                                            }
                                        }
                                        dataChanged = "yes";
                                    }*/
                                    orderArrayList.add(order);
                                }
                            }
                        }
                        loading = false;
                        if (orderArrayList != null && orderArrayList.size() == 0) {
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
                            orderAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            //  if (dataChanged != null && dataChanged.equals("yes")) {
                            orderAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                            //  }
                        } else {
                            orderAdapter.notifyDataSetChanged();
                        }
                    } else {
                        loading = false;
                        if (orderArrayList != null && orderArrayList.size() == 0) {
                            textViewEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            //       customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            textViewEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //       customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            orderAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            //  if (dataChanged != null && dataChanged.equals("yes")) {
                            orderAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                            //  }
                        } else {
                            orderAdapter.notifyDataSetChanged();
                        }

                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
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

    private void dialogActionsItems(final int itemsPosition) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_orders_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogActions = builder.create();
        dialogActions.setCanceledOnTouchOutside(false);
        ImageView imageViewBack;
        RecyclerView recyclerView;

        recyclerView = (RecyclerView) dialogView.findViewById(R.id.recyclerView);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);

        //Adapter
        actionsAdapter = new LongClickActionsAdapter(getActivity(), actionsArr, this, itemsPosition);
        mActionsLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mActionsLayoutManager);
        recyclerView.setAdapter(actionsAdapter);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogActions.dismiss();
            }
        });

        dialogActions.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogActions.isShowing()) {
            dialogActions.show();
        }
    }

    @Override
    public void onActionsRowClicked(int rowPosition, int itemsPosition) {
        if (rowPosition == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                cancelDeleteCloseOrderTask(orderArrayList.get(itemsPosition).getChkoid(), "cancel");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (rowPosition == 1) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                cancelDeleteCloseOrderTask(orderArrayList.get(itemsPosition).getChkoid(), "delete");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (rowPosition == 2) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                cancelDeleteCloseOrderTask(orderArrayList.get(itemsPosition).getChkoid(), "close");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (rowPosition == 3) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateEditScreen(orderArrayList.get(itemsPosition).getChkoid(),
                    orderArrayList.get(itemsPosition).getCheckpointOrderId());
        } else if (rowPosition == 4) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            if (alertDialog == null || !alertDialog.isShowing()) {
                downloadOrderDialog(orderArrayList.get(itemsPosition).getChkoid(),
                        orderArrayList.get(itemsPosition).getCustomerId(),
                        orderArrayList.get(itemsPosition).getCheckpointOrderId());
            }
        } else if (rowPosition == 5) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateDetailScreen(orderArrayList.get(itemsPosition).getChkoid(),
                    orderArrayList.get(itemsPosition).getCheckpointOrderId(),
                    orderArrayList.get(itemsPosition).getCustomerId());
        }
    }

    public void downloadOrderDialog(final String orderId, final String cuId, final String fileName) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        downloadConfirmMessage.setText(getActivity().getString(R.string.download_order_confirm_msg));
        titleTextView.setText(getActivity().getString(R.string.download_order));
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
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getActivity().getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, orderId, cuId, fileName);
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    private void downloadPdf(final AlertDialog alertDialog, final String orderId,
                             final String cuId, final String fileName) {
        task = getActivity().getString(R.string.download_sales_order_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadOrder(version, key, task, userId, accessToken, orderId, cuId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getActivity().getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_sales_order" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_sales_order" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.connect_server_failed),
                            Toast.LENGTH_SHORT).show();
                    if (alertDialog != null && alertDialog.isShowing()) {
                        alertDialog.dismiss();
                    }
                }
            }
        });
    }

    public void cancelDeleteCloseOrderTask(String orderId, String type) {
        if (type.equals("cancel")) {
            task = getString(R.string.cancel_order_task);
        } else if (type.equals("delete")) {
            task = getString(R.string.delete_order_task);
        } else if (type.equals("close")) {
            task = getString(R.string.close_order_task);
        }
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.fetchExistingOrderData(version, key, task, userId, accessToken, orderId);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {

                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        if (orderArrayList != null && orderArrayList.size() > 0) {
                            orderArrayList.clear();
                        }
                        doRefresh(searchText);

                    } //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
