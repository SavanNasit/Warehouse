package com.accrete.sixorbit.fragment.Drawer.quotation;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.order.EditOrderActivity;
import com.accrete.sixorbit.activity.quotations.EditQuotationsProductActivity;
import com.accrete.sixorbit.activity.quotations.QuotationDetailsActivity;
import com.accrete.sixorbit.activity.quotations.QuotationHistoryActivity;
import com.accrete.sixorbit.adapter.LongClickActionsAdapter;
import com.accrete.sixorbit.adapter.PendingManageQuotationAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.QuotationDatum;
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
 * Created by poonam on 12/28/17.
 */

public class PendingManageQuotationFragment extends Fragment implements PendingManageQuotationAdapter.
        PendingManageQuotationAdapterListener, SwipeRefreshLayout.OnRefreshListener,
        LongClickActionsAdapter.LongClickActionsAdapterListener {

    private SwipeRefreshLayout pendingManageQuotationSwipeRefreshLayout;
    private RecyclerView pendingManageQuotationRecyclerView;
    private TextView pendingManageQuotationEmptyView;
    private PendingManageQuotationAdapter pendingManageQuotationAdapter;
    private List<QuotationDatum> pendingList = new ArrayList<>();
    private ImageView imageView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private String status, dataChanged;
    private String[] actionsArr = new String[]{"View Details", "Edit Quotation", "Download Quotation",
            "Cancel Quotation", "Quotation History", "Convert into Order"};
    private AlertDialog dialogActions;
    private LongClickActionsAdapter actionsAdapter;
    private LinearLayoutManager mActionsLayoutManager;
    private TextView btnYes;
    private TextView btnCancel;
    private ProgressBar progressBar;
    private DownloadManager downloadManager;
    private AlertDialog alertDialog, alertDialogCancelQuotation;
    private TextView downloadConfirmMessage, titleTextView;
    private DatabaseHandler databaseHandler;
    private String searchText;

    @Override
    public void onLongClicked(int position) {
        if (dialogActions == null || !dialogActions.isShowing()) {
            dialogActionsItems(position);
        }
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
                                //  if (pendingList != null && pendingList.size() == 0) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                                //   }
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_manage_quotation, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        try {
            pendingManageQuotationSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
            pendingManageQuotationRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
            pendingManageQuotationEmptyView = (TextView) rootView.findViewById(R.id.empty_textView);
            imageView = (ImageView) rootView.findViewById(R.id.imageView);
            imageView.setVisibility(View.GONE);

            databaseHandler = new DatabaseHandler(getActivity());

            pendingManageQuotationAdapter = new PendingManageQuotationAdapter(getActivity(), pendingList, this);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            pendingManageQuotationRecyclerView.setLayoutManager(mLayoutManager);
            pendingManageQuotationRecyclerView.setHasFixedSize(true);
            pendingManageQuotationRecyclerView.setItemAnimator(null);
            pendingManageQuotationRecyclerView.setNestedScrollingEnabled(false);
            pendingManageQuotationRecyclerView.setAdapter(pendingManageQuotationAdapter);

            callAPI("");

            pendingManageQuotationSwipeRefreshLayout.setEnabled(false);
            pendingManageQuotationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (pendingManageQuotationSwipeRefreshLayout.isRefreshing()) {
                        pendingManageQuotationSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            });

            //Scroll Listener
            pendingManageQuotationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = mLayoutManager.getItemCount();
                    lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && pendingList.size() > 0) {
                        // End has been reached
                        // Do something
                        loading = true;
                        if (getActivity() != null && isAdded()) {
                            //calling API
                            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                getPendingQuotationDetailsList(pendingList.get(totalItemCount - 1).getCreatedTs(), "2");
                            } else {
                                if (pendingManageQuotationSwipeRefreshLayout.isRefreshing()) {
                                    pendingManageQuotationSwipeRefreshLayout.setRefreshing(false);
                                }
                                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            }
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

            pendingManageQuotationSwipeRefreshLayout.setOnRefreshListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callAPI(String searchQuery) {
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() != null && isAdded()) {
                        searchText = searchQuery;
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            if (pendingList != null && pendingList.size() > 0) {
                                pendingList.clear();
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        // Stuff that updates the UI
                                        pendingManageQuotationAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                            showLoader();
                            getPendingQuotationDetailsList(getString(R.string.last_updated_date), "1");
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
            });
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getPendingQuotationDetailsList(final String time, final String traversalValue) {
        try {
            String task = getString(R.string.manage_quotation_task);

            if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getPendingQuotationList(version, key, task, userId, accessToken,
                    time, traversalValue, "2", searchText);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // enquiryList.clear();
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())) + "");
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse != null && apiResponse.getSuccess()) {
                            pendingManageQuotationRecyclerView.setVisibility(View.VISIBLE);
                            pendingManageQuotationEmptyView.setVisibility(View.GONE);

                            for (QuotationDatum packedItem : apiResponse.getData().getQuotationData()) {
                                if (packedItem != null) {
                                    if (traversalValue.equals("2")) {
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            pendingList.add(packedItem);
                                        }
                                        dataChanged = "yes";
                                    } else if (traversalValue.equals("1")) {
                                        if (pendingManageQuotationSwipeRefreshLayout != null &&
                                                pendingManageQuotationSwipeRefreshLayout.isRefreshing()) {
                                            // To remove duplicacy of a new item
                                            if (!time.equals(packedItem.getCreatedTs())) {
                                                pendingList.add(0, packedItem);
                                            }
                                        } else {
                                            if (!time.equals(packedItem.getCreatedTs())) {
                                                pendingList.add(packedItem);
                                            }
                                        }
                                        dataChanged = "yes";
                                    }
                                }
                            }
                            loading = false;
                            if (pendingList != null && pendingList.size() == 0) {
                                pendingManageQuotationEmptyView.setVisibility(View.VISIBLE);
                                pendingManageQuotationRecyclerView.setVisibility(View.GONE);
                                pendingManageQuotationEmptyView.setText("No collection.");
                                //  customerOrderFabAdd.setVisibility(View.GONE);
                            } else {
                                pendingManageQuotationEmptyView.setVisibility(View.GONE);
                                pendingManageQuotationRecyclerView.setVisibility(View.VISIBLE);
                                //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                            }
                            if (pendingManageQuotationSwipeRefreshLayout != null && pendingManageQuotationSwipeRefreshLayout.isRefreshing()) {
                                pendingManageQuotationSwipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversalValue.equals("2")) {
                                pendingManageQuotationAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    // pendingManageQuotationRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    pendingManageQuotationAdapter.notifyDataSetChanged();
                                    pendingManageQuotationRecyclerView.smoothScrollToPosition(0);
                                }
                            }
                        } else {
                            if (apiResponse != null && apiResponse.getSuccessCode() != null &&
                                    apiResponse.getSuccessCode().equals("10001")) {
                                pendingManageQuotationEmptyView.setText(getString(R.string.no_data_available));
                                pendingManageQuotationRecyclerView.setVisibility(View.GONE);
                                pendingManageQuotationEmptyView.setVisibility(View.VISIBLE);
                            }//Deleted User
                            else if (apiResponse != null && apiResponse != null &&
                                    apiResponse.getSuccessCode() != null &&
                                    (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN))) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            }
                            if (pendingList != null && pendingList.size() == 0) {
                                pendingManageQuotationEmptyView.setVisibility(View.VISIBLE);
                                pendingManageQuotationEmptyView.setText(getString(R.string.no_data_available));
                                pendingManageQuotationRecyclerView.setVisibility(View.GONE);
                            } else {
                                pendingManageQuotationEmptyView.setVisibility(View.GONE);
                                pendingManageQuotationRecyclerView.setVisibility(View.VISIBLE);
                            }
                            if (pendingManageQuotationSwipeRefreshLayout != null && pendingManageQuotationSwipeRefreshLayout.isRefreshing()) {
                                pendingManageQuotationSwipeRefreshLayout.setRefreshing(false);
                            }
                        }
                  /*  if (sw != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }*/
                        hideLoader();
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    if (getActivity() != null && isAdded()) {
                        Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                        hideLoader();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToQuotationsDetails(int position) {
        Intent intent = new Intent(getActivity(), QuotationDetailsActivity.class);
        intent.putExtra(getString(R.string.qo_id), pendingList.get(position).getQoid());
        intent.putExtra(getString(R.string.edit), getString(R.string.edit));
        intent.putExtra(getString(R.string.cuid), pendingList.get(position).getCuid());
        intent.putExtra(getString(R.string.qo_id_text), pendingList.get(position).getQuotationID());
        startActivity(intent);
    }

    private void navigateToQuotationsHistory(int position) {
        Intent intent = new Intent(getActivity(), QuotationHistoryActivity.class);
        intent.putExtra(getString(R.string.qo_id), pendingList.get(position).getQoid());
        intent.putExtra(getString(R.string.qo_id_text), pendingList.get(position).getQuotationID());
        startActivity(intent);
    }

    private void navigateToEditQuotations(int position) {
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.quotation_edit_permission))) {
            Intent intent = new Intent(getActivity(), EditQuotationsProductActivity.class);
            intent.putExtra(getString(R.string.qo_id), pendingList.get(position).getQoid());
            intent.putExtra(getString(R.string.qo_id_text), pendingList.get(position).getQuotationID());
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "You have no permission to edit any quotation.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (dialogActions != null && dialogActions.isShowing()) {
            dialogActions.dismiss();
        }
        navigateToQuotationsDetails(position);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null && isAdded()) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (pendingList != null && pendingList.size() > 0) {
                    getPendingQuotationDetailsList(pendingList.get(0).getCreatedTs(), "1");
                } else {
                    showLoader();
                    getPendingQuotationDetailsList(getString(R.string.last_updated_date), "1");
                }
                pendingManageQuotationRecyclerView.setVisibility(View.VISIBLE);
                pendingManageQuotationEmptyView.setVisibility(View.GONE);
                pendingManageQuotationSwipeRefreshLayout.setRefreshing(true);

            } else {
                pendingManageQuotationRecyclerView.setVisibility(View.GONE);
                pendingManageQuotationEmptyView.setVisibility(View.VISIBLE);
                pendingManageQuotationEmptyView.setText(getString(R.string.no_internet_try_later));
                pendingManageQuotationSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public void onBackPressed() {
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
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
    public void onActionsRowClicked(int position, int itemsPosition) {
        if (position == 0) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateToQuotationsDetails(itemsPosition);
        } else if (position == 1) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateToEditQuotations(itemsPosition);
        } else if (position == 2) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                downloadQuotationDialog(pendingList.get(itemsPosition).getQoid(),
                        pendingList.get(itemsPosition).getQuotationId());
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        } else if (position == 3) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            if (alertDialogCancelQuotation == null || !alertDialogCancelQuotation.isShowing()) {
                cancelQuotationDialog(pendingList.get(itemsPosition).getQoid());
            }
        } else if (position == 4) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateToQuotationsHistory(itemsPosition);
        } else if (position == 5) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            convertQuotationIntoOrderDialog(pendingList.get(itemsPosition).getQoid());
        }
    }

    public void downloadQuotationDialog(final String qoId, final String fileName) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        downloadConfirmMessage.setText(getActivity().getString(R.string.download_quotation_confirm_msg));
        titleTextView.setText(getActivity().getString(R.string.download_quotation));
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
                    downloadPdf(alertDialog, qoId, fileName);
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

    private void downloadPdf(final AlertDialog alertDialog, final String qoId, final String fileName) {
        task = getString(R.string.manage_quotation_download_quotation);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadQuotations(version, key, task, userId, accessToken, qoId);
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
                                    .setTitle(fileName + "_quotation" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_quotation" + ".pdf")
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

    public void cancelQuotationDialog(final String qoId) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_quotation, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialogCancelQuotation = builder.create();
        alertDialogCancelQuotation.setCanceledOnTouchOutside(true);

        LinearLayout linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        EditText reasonEditText = (EditText) dialogView.findViewById(R.id.reason_editText);
        TextView okTextView = (TextView) dialogView.findViewById(R.id.ok_textView);
        TextView cancelTextView = (TextView) dialogView.findViewById(R.id.cancel_textView);
        ProgressBar progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogCancelQuotation.dismiss();
            }
        });
        okTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
                okTextView.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getActivity().getString(R.string.not_connected_to_internet))) {
                    cancelQuotationTask(qoId, reasonEditText.getText().toString().trim() +
                            "", alertDialogCancelQuotation);
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        okTextView.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialogCancelQuotation.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialogCancelQuotation.show();
    }


    public void convertQuotationIntoOrderDialog(final String qoId) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        downloadConfirmMessage.setText(getActivity().getString(R.string.convert_quotation_to_order_confirm_msg));
        titleTextView.setText(getActivity().getString(R.string.convert_quotation_to_order));
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
                    //API Call
                    convertQuotationIntoOrderTask(qoId, alertDialog);
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


    //TODO - Cancel Quotation
    public void cancelQuotationTask(String qoId, String reason, AlertDialog alertDialog) {
        task = getString(R.string.cancel_quotation_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        //TODO - this function also has same functions so we can use it to cancel/approve quotations
        Call<ApiResponse> call = apiService.cancelQuotation(version, key, task, userId, accessToken,
                qoId, reason);
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

                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        }

                        if (pendingList != null && pendingList.size() > 0) {
                            pendingList.clear();
                        }
                        callAPI(searchText);

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

    //TODO - Convert Quotation into Order
    public void convertQuotationIntoOrderTask(String qoId, AlertDialog alertDialog) {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }

        Intent intent = new Intent(getActivity(), EditOrderActivity.class);
        intent.putExtra(getString(R.string.convert_from), true);
        intent.putExtra(getString(R.string.qo_id), qoId);
        startActivity(intent);
    }
}