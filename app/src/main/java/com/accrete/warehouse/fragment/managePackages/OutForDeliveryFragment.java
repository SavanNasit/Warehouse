package com.accrete.warehouse.fragment.managePackages;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
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

import com.accrete.warehouse.ChangePackageStatusActivity;
import com.accrete.warehouse.CustomerDetailsActivity;
import com.accrete.warehouse.ItemsInsidePackageActivity;
import com.accrete.warehouse.PackageHistoryActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.OutForDeliveryAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageItem;
import com.accrete.warehouse.model.UploadDocument;
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

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;
import static com.accrete.warehouse.utils.MSupportConstants.PICK_FILE_RESULT_CODE;
import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_ASK_STORAGE_PERMISSIONS;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 11/30/17.
 */

public class OutForDeliveryFragment extends Fragment implements DocumentUploaderAdapter.DocAdapterListener,
        OutForDeliveryAdapter.OutForDeliveryAdapterListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout outForDeliverySwipeRefreshLayout;
    private RecyclerView pendingItemsRecyclerView;
    private TextView outForDeliveryEmptyView;
    private OutForDeliveryAdapter outForDeliveryAdapter;
    private List<PackageItem> outForDeliveryList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;
    private AlertDialog dialogUploadDoc;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<UploadDocument> uploadDocumentList = new ArrayList<>();
    private LinearLayoutManager mLayoutManager;
    private String status, dataChanged;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private RecyclerView dialogUploadDocRecyclerView;
    private ImageView addImageView;
    private Button btnUpload;
    private ProgressBar dialogUploadProgressBar;


    //Add Document into List
    public void addDocument(String selectedFilePath, String fileName) {
        UploadDocument uploadDocument = new UploadDocument();
        uploadDocument.setFileName(fileName);
        uploadDocument.setFilePath(selectedFilePath);
        uploadDocument.setFileType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        uploadDocumentList.add(uploadDocument);
        documentUploaderAdapter.notifyDataSetChanged();
    }

    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            uploadDocumentList.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_out_for_delivery, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        outForDeliverySwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.out_for_delivery_swipe_refresh_layout);
        pendingItemsRecyclerView = (RecyclerView) rootView.findViewById(R.id.out_for_delivery_recycler_view);
        outForDeliveryEmptyView = (TextView) rootView.findViewById(R.id.out_for_delivery_empty_view);

        outForDeliveryAdapter = new OutForDeliveryAdapter(getActivity(), outForDeliveryList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        pendingItemsRecyclerView.setLayoutManager(mLayoutManager);
        pendingItemsRecyclerView.setHasFixedSize(true);
        pendingItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingItemsRecyclerView.setNestedScrollingEnabled(false);
        pendingItemsRecyclerView.setAdapter(outForDeliveryAdapter);

        //doRefresh();

        //Scroll Listener
        pendingItemsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && outForDeliveryList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getoutForDeliveryList(outForDeliveryList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (outForDeliverySwipeRefreshLayout.isRefreshing()) {
                            outForDeliverySwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        outForDeliverySwipeRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        outForDeliveryEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outForDeliveryEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });

    }

    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    private void dialogItemEvents(final int position) {
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
        actionsItemsInsidePackage.setVisibility(View.GONE);

        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentStatus = new Intent(getActivity(), ChangePackageStatusActivity.class);
                intentStatus.putExtra(getString(R.string.pacdelgatpacid), outForDeliveryList.get(position).getPacdelgatpacid());
                startActivityForResult(intentStatus, 456);
            }
        });

        actionsItemsInsidePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentItems = new Intent(getActivity(), ItemsInsidePackageActivity.class);
                startActivity(intentItems);
            }
        });

        actionsPackageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentPackageHistory = new Intent(getActivity(), PackageHistoryActivity.class);
                intentPackageHistory.putExtra("packageid", outForDeliveryList.get(position).getPacid().toString());
                startActivity(intentPackageHistory);
            }
        });
        actionsOtherDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                dialogUploadDoc(getActivity());
            }
        });

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });

        //Load Customer's Info
        actionsCustomerDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentCustomerDetails = new Intent(getActivity(), CustomerDetailsActivity.class);
                intentCustomerDetails.putExtra(getString(R.string.pacId), outForDeliveryList.get(position).getPacid().toString());
                startActivity(intentCustomerDetails);
            }
        });

        //Download Invoice
        actionsPrintInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    askStoragePermission(position, getString(R.string.invoice));
                } else {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.invoice),
                            outForDeliveryList.get(position).getCuid(), outForDeliveryList.get(position).getInvid());
                }
            }
        });

        //Download Challan
        actionsPrintDeliveryChallan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    askStoragePermission(position, getString(R.string.challan));
                } else {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.challan),
                            outForDeliveryList.get(position).getPacid(), "");
                }
            }
        });

        //Download GatePass
        actionsPrintGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    askStoragePermission(position, getString(R.string.gatepass));
                } else {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.gatepass),
                            outForDeliveryList.get(position).getPacdelgatid(), "");
                }
            }
        });

        //Download Loading Slip
        actionsPrintLoadingSlip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    askStoragePermission(position, getString(R.string.loading_slip));
                } else {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.loading_slip),
                            outForDeliveryList.get(position).getPacdelgatid(), "");
                }
            }
        });

        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }

    public void askStoragePermission(int position, String type) {
        if (checkPermissionWithRationale(getActivity(), new PackedAgainstStockFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            } else {
                if (type.equals(getString(R.string.challan))) {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.challan),
                            outForDeliveryList.get(position).getPacid(), "");
                } else if (type.equals(getString(R.string.invoice))) {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.invoice),
                            outForDeliveryList.get(position).getCuid(), outForDeliveryList.get(position).getInvid());
                } else if (type.equals(getString(R.string.gatepass))) {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.gatepass),
                            outForDeliveryList.get(position).getPacdelgatid(), "");
                } else if (type.equals(getString(R.string.loading_slip))) {
                    downloadDialog(outForDeliveryList.get(position).getPackageId(), getString(R.string.loading_slip),
                            outForDeliveryList.get(position).getPacdelgatid(), "");
                }

            }
        }
    }

    //Opening Dialog to Upload Documents
    private void dialogUploadDoc(Activity activity) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(true);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        addImageView = (ImageView) dialogView.findViewById(R.id.add_imageView);
        btnUpload = (Button) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        documentUploaderAdapter = new DocumentUploaderAdapter(getActivity(), uploadDocumentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if (uploadDocumentList.size() > 0) {
            uploadDocumentList.clear();
        }

        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    dialogUploadDoc.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please upload atleast one doc.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Dismiss dialog
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    uploadDocumentList.clear();
                }
                dialogUploadDoc.dismiss();
            }
        });

        //Call Intent to select file and add into List
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

        dialogUploadDoc.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogUploadDoc.isShowing()) {
            dialogUploadDoc.show();
        }
    }

    //Intent to select file
    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
    }

    @Override
    public void onExecute() {

    }

    public void doRefresh() {
        if (outForDeliveryList != null && outForDeliveryList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        outForDeliveryEmptyView.setText(getString(R.string.no_data_available));
                        getoutForDeliveryList(getString(R.string.last_updated_date), "1");
                    }
                }, 200);
            } else {
                outForDeliveryEmptyView.setVisibility(View.VISIBLE);
                outForDeliveryEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getoutForDeliveryList(final String time, final String traversalValue) {
        String task = getString(R.string.fetch_packages);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getOutForDeliveryList(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID),
                time, traversalValue, "3", "1");
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
                                        outForDeliveryList.add(packageItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (outForDeliverySwipeRefreshLayout != null &&
                                            outForDeliverySwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packageItem.getCreatedTs())) {
                                            outForDeliveryList.add(0, packageItem);
                                        }
                                    } else {
                                        if (!time.equals(packageItem.getCreatedTs())) {
                                            outForDeliveryList.add(packageItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (outForDeliveryList != null && outForDeliveryList.size() == 0) {
                            outForDeliveryEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            outForDeliveryEmptyView.setVisibility(View.GONE);
                        }
                        if (outForDeliverySwipeRefreshLayout != null &&
                                outForDeliverySwipeRefreshLayout.isRefreshing()) {
                            outForDeliverySwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            outForDeliveryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                outForDeliveryAdapter.notifyDataSetChanged();
                                pendingItemsRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (outForDeliveryList != null && outForDeliveryList.size() == 0) {
                            outForDeliveryEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            outForDeliveryEmptyView.setVisibility(View.GONE);
                        }
                        if (outForDeliverySwipeRefreshLayout != null && outForDeliverySwipeRefreshLayout.isRefreshing()) {
                            outForDeliverySwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            outForDeliveryAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                outForDeliveryAdapter.notifyDataSetChanged();
                                pendingItemsRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (outForDeliverySwipeRefreshLayout != null && outForDeliverySwipeRefreshLayout.isRefreshing()) {
                        outForDeliverySwipeRefreshLayout.setRefreshing(false);
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
            if (outForDeliveryList != null && outForDeliveryList.size() > 0) {
                getoutForDeliveryList(outForDeliveryList.get(0).getCreatedTs(), "1");
            } else {
                getoutForDeliveryList(getString(R.string.last_updated_date), "1");
            }
            outForDeliveryEmptyView.setVisibility(View.GONE);
            outForDeliverySwipeRefreshLayout.setRefreshing(true);

        } else {
            outForDeliveryEmptyView.setVisibility(View.VISIBLE);
            outForDeliveryEmptyView.setText(getString(R.string.no_internet_try_later));
            outForDeliverySwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void downloadDialog(final String fileName, final String type, final String cuIdPacId, final String InvId) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download_receipt, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        titleDownloadTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        if (type.equals(getString(R.string.invoice))) {
            downloadConfirmMessage.setText(getString(R.string.download_invoice_confirm_msg));
            titleDownloadTextView.setText("Download invoice");
        } else if (type.equals(getString(R.string.challan))) {
            downloadConfirmMessage.setText(getString(R.string.download_delivery_challan_confirm_msg));
            titleDownloadTextView.setText("Download delivery challan");
        } else if (type.equals(getString(R.string.gatepass))) {
            downloadConfirmMessage.setText(getString(R.string.download_gatepass_confirm_msg));
            titleDownloadTextView.setText("Download gatepass");
        } else if (type.equals(getString(R.string.loading_slip))) {
            downloadConfirmMessage.setText(getString(R.string.download_loading_slip_confirm_msg));
            titleDownloadTextView.setText("Download loading slip");
        }

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
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, fileName, type, cuIdPacId, InvId);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    private void downloadPdf(final AlertDialog alertDialog, final String fileName, final String type, String cuIdPacId, String invId) {
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = null;

        if (type.equals(getString(R.string.invoice))) {
            task = getString(R.string.download_invoice_task);
            call = apiService.downloadInvoicePDF(version, key, task, userId, accessToken,
                    cuIdPacId, invId);
        } else if (type.equals(getString(R.string.challan))) {
            task = getString(R.string.download_delivery_challan_task);
            call = apiService.downloadChallanPDF(version, key, task, userId, accessToken,
                    cuIdPacId);
        } else if (type.equals(getString(R.string.gatepass))) {
            task = getString(R.string.download_gatepass);
            call = apiService.downloadGatePassPDF(version, key, task, userId, accessToken,
                    cuIdPacId);
        } else if (type.equals(getString(R.string.loading_slip))) {
            task = getString(R.string.download_loading_slip_task);
            call = apiService.downloadGatePassPDF(version, key, task, userId, accessToken,
                    cuIdPacId);
        }


        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = null;

                            if (type.equals(getString(R.string.invoice))) {
                                request = new DownloadManager.Request(uri)
                                        .setTitle(fileName + "_invoice" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_invoice" + ".pdf")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            } else if (type.equals(getString(R.string.gatepass))) {
                                request = new DownloadManager.Request(uri)
                                        .setTitle(fileName + "_gatePass" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_gatePass" + ".pdf")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            } else if (type.equals(getString(R.string.loading_slip))) {
                                request = new DownloadManager.Request(uri)
                                        .setTitle(fileName + "_loadingSlip" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_loadingSlip" + ".pdf")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            } else {
                                request = new DownloadManager.Request(uri)
                                        .setTitle(fileName + "_delivery_challan" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_delivery_challan" + ".pdf")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            }
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                //Toast.makeText(this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                alertDialog.dismiss();
            }
        });
    }

    public void clearListAndRefresh() {
        if (outForDeliveryList != null && outForDeliveryList.size() > 0) {
            outForDeliveryList.clear();
        }
        pendingItemsRecyclerView.removeAllViewsInLayout();
        outForDeliveryAdapter.notifyDataSetChanged();
        doRefresh();
    }

}
