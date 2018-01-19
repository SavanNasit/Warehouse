package com.accrete.warehouse.fragment.managePackages;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.CustomerDetailsActivity;
import com.accrete.warehouse.ItemsInsidePackageActivity;
import com.accrete.warehouse.PackageHistoryActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.OutForDeliveryAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageItem;
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
 * Created by poonam on 11/30/17.
 */

public class ReAttemptFragment extends Fragment implements OutForDeliveryAdapter.OutForDeliveryAdapterListener,
        DocumentUploaderAdapter.DocAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout reAttemptRefreshLayout;
    private RecyclerView reAattemptRecyclerView;
    private TextView reAttemptEmptyView;
    private OutForDeliveryAdapter outForDeliveryAdapter;
    private List<PackageItem> reAttemptList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;
    private AlertDialog dialogUploadDoc;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<String> documentList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String status, dataChanged;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attempt_failed, container, false);
        findViews(rootView);
        return rootView;
    }


    private void findViews(View rootView) {
        reAttemptRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.attempt_failed_refresh_layout);
        reAattemptRecyclerView = (RecyclerView) rootView.findViewById(R.id.attempt_failed_recycler_view);
        reAttemptEmptyView = (TextView) rootView.findViewById(R.id.attempt_failed_empty_view);

        outForDeliveryAdapter = new OutForDeliveryAdapter(getActivity(), reAttemptList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        reAattemptRecyclerView.setLayoutManager(mLayoutManager);
        reAattemptRecyclerView.setHasFixedSize(true);
        reAattemptRecyclerView.setItemAnimator(new DefaultItemAnimator());
        reAattemptRecyclerView.setNestedScrollingEnabled(false);
        reAattemptRecyclerView.setAdapter(outForDeliveryAdapter);


        doRefresh();

        //Scroll Listener
        reAattemptRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && reAttemptList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getReAttemptList(reAttemptList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (reAttemptRefreshLayout.isRefreshing()) {
                            reAttemptRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        reAttemptRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        reAttemptEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reAttemptEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });


    }

    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    private void dialogItemEvents(int position) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_select_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectEvent = builder.create();
        dialogSelectEvent.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        LinearLayout actionsItemsInsidePackage;
        LinearLayout actionsPackageStatus;
        LinearLayout actionsPackageHistory;
        LinearLayout actionsCustomerDetails;
        LinearLayout actionsPrintInvoice;
        LinearLayout actionsPrintDeliveryChallan;
        LinearLayout actionsOtherDocuments;
        LinearLayout actionsPrintGatepass;
        LinearLayout actionsPrintLoadingSlip;
        TextView textViewActionPackageStatus;
        Button btnOk;
        ProgressBar dialogSelectActionsProgressBar;
        Button btnCancel;
        ImageView imageViewBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsItemsInsidePackage = (LinearLayout) dialogView.findViewById(R.id.actions_items_inside_package);
        actionsPackageStatus = (LinearLayout) dialogView.findViewById(R.id.actions_package_status);
        actionsPackageHistory = (LinearLayout) dialogView.findViewById(R.id.actions_package_history);
        actionsCustomerDetails = (LinearLayout) dialogView.findViewById(R.id.actions_customer_details);
        actionsPrintInvoice = (LinearLayout) dialogView.findViewById(R.id.actions_print_invoice);
        actionsPrintDeliveryChallan = (LinearLayout) dialogView.findViewById(R.id.actions_print_delivery_challan);
        actionsOtherDocuments = (LinearLayout) dialogView.findViewById(R.id.actions_other_documents);
        actionsPrintGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_print_gatepass);
        actionsPrintLoadingSlip = (LinearLayout) dialogView.findViewById(R.id.actions_print_loading_slip);
        // btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        dialogSelectActionsProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_select_warehouse_progress_bar);
        //btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);
        textViewActionPackageStatus = (TextView) dialogView.findViewById(R.id.actions_package_status_text);
        textViewActionPackageStatus.setText("Revert Package Delivery");


        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intentStatus = new Intent(getActivity(), PackageOrderStatusActivity.class);
                startActivity(intentStatus);*/
                dialogRevertPackageDelivery();
            }
        });

        actionsItemsInsidePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentItems = new Intent(getActivity(), ItemsInsidePackageActivity.class);
                startActivity(intentItems);
            }
        });

        actionsPackageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPackageHistory = new Intent(getActivity(), PackageHistoryActivity.class);
                startActivity(intentPackageHistory);
            }
        });

        actionsOtherDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                dialogUploadDoc();
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });

      /*  btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });*/

        actionsCustomerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCustomerDetails = new Intent(getActivity(), CustomerDetailsActivity.class);
                startActivity(intentCustomerDetails);
            }
        });

        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }


    private void dialogRevertPackageDelivery() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogCancelGatepass = builder.create();
        dialogCancelGatepass.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        Button btnOk;
        ProgressBar cancelGatepassProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        TextView textViewMessage = (TextView) dialogView.findViewById(R.id.cancel_gatepass_message);
        TextView textViewTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_title);
        textViewMessage.setText("Are you sure to revert delivery of package?");
        textViewTitle.setText("Revert Package Delivery");


        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass.dismiss();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass.dismiss();
            }
        });


        dialogCancelGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogCancelGatepass.isShowing()) {
            dialogCancelGatepass.show();
        }

    }

    private void dialogUploadDoc() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(true);


        LinearLayout linearLayout;
        RecyclerView dialogUploadDocRecyclerView;
        Button btnUpload;
        ProgressBar dialogUploadProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        btnUpload = (Button) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        documentUploaderAdapter = new DocumentUploaderAdapter(getActivity(), documentList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if (documentList.size() > 0) {
            documentList.clear();
        }

        documentList.add("awesome-file.jpg");
        documentList.add("awesome-file.jpg");
        documentList.add("awesome-file.jpg");


        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUploadDoc.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUploadDoc.dismiss();
            }
        });

        dialogUploadDoc.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogUploadDoc.isShowing()) {
            dialogUploadDoc.show();
        }
    }

    @Override
    public void onExecute() {

    }

    public void doRefresh() {
        if (reAttemptList != null && reAttemptList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reAttemptEmptyView.setText(getString(R.string.no_data_available));
                        getReAttemptList(getString(R.string.last_updated_date), "1");
                    }
                }, 200);
            } else {
                reAattemptRecyclerView.setVisibility(View.GONE);
                reAttemptEmptyView.setVisibility(View.VISIBLE);
                reAttemptRefreshLayout.setVisibility(View.GONE);
                reAttemptEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getReAttemptList(final String time, final String traversalValue) {
        String task = getString(R.string.fetch_packages);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getOutForDeliveryList(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID),
                time, traversalValue, "6", "3");
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
                        for (final PackageItem packageItem : apiResponse.getData().getPackageItems()) {
                            if (packageItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(packageItem.getCreatedTs())) {
                                        reAttemptList.add(packageItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (reAttemptRefreshLayout != null &&
                                            reAttemptRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packageItem.getCreatedTs())) {
                                            reAttemptList.add(0, packageItem);
                                        }
                                    } else {
                                        if (!time.equals(packageItem.getCreatedTs())) {
                                            reAttemptList.add(packageItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (reAttemptList != null && reAttemptList.size() == 0) {
                            reAttemptEmptyView.setVisibility(View.VISIBLE);
                            reAattemptRecyclerView.setVisibility(View.GONE);
                            reAttemptRefreshLayout.setVisibility(View.GONE);
                        } else {
                            reAttemptEmptyView.setVisibility(View.GONE);
                            reAttemptRefreshLayout.setVisibility(View.VISIBLE);
                            reAattemptRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (reAttemptRefreshLayout != null &&
                                reAttemptRefreshLayout.isRefreshing()) {
                            reAttemptRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            outForDeliveryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                outForDeliveryAdapter.notifyDataSetChanged();
                                reAattemptRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (reAttemptList != null && reAttemptList.size() == 0) {
                            reAttemptEmptyView.setVisibility(View.VISIBLE);
                            reAattemptRecyclerView.setVisibility(View.GONE);
                            reAttemptRefreshLayout.setVisibility(View.GONE);
                        } else {
                            reAttemptEmptyView.setVisibility(View.GONE);
                            reAattemptRecyclerView.setVisibility(View.VISIBLE);
                            reAttemptRefreshLayout.setVisibility(View.VISIBLE);
                        }
                        if (reAttemptRefreshLayout != null && reAttemptRefreshLayout.isRefreshing()) {
                            reAttemptRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            outForDeliveryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                outForDeliveryAdapter.notifyDataSetChanged();
                                reAattemptRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (reAttemptRefreshLayout != null && reAttemptRefreshLayout.isRefreshing()) {
                        reAttemptRefreshLayout.setRefreshing(false);
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
            if (reAttemptList != null && reAttemptList.size() > 0) {
                getReAttemptList(reAttemptList.get(0).getCreatedTs(), "1");
            } else {
                getReAttemptList(getString(R.string.last_updated_date), "1");
            }
            reAattemptRecyclerView.setVisibility(View.VISIBLE);
            reAttemptEmptyView.setVisibility(View.GONE);
            reAttemptRefreshLayout.setRefreshing(true);
            reAttemptRefreshLayout.setVisibility(View.VISIBLE);

        } else {
            reAattemptRecyclerView.setVisibility(View.GONE);
            reAttemptEmptyView.setVisibility(View.VISIBLE);
            reAttemptRefreshLayout.setVisibility(View.GONE);
            reAttemptEmptyView.setText(getString(R.string.no_internet_try_later));
            reAttemptRefreshLayout.setRefreshing(false);
        }
    }
}
