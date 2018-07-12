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
import android.os.Looper;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.CustomerDetailsActivity;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.PackedAgainstStockAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageFile;
import com.accrete.warehouse.model.PackedItem;
import com.accrete.warehouse.model.UploadDocument;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.rest.FilesUploadingAsyncTask;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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
 * Created by agt on 19/1/18.
 */

public class PackedAgainstStockFragment extends Fragment implements PackedAgainstStockAdapter.PackedAgainstAdapterListener,
        DocumentUploaderAdapter.DocAdapterListener, SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout packedAgainstRefreshLayout;
    private RecyclerView packedAgainstRecyclerView;
    private TextView packedAgainstEmptyView;
    private PackedAgainstStockAdapter packedAgainstStockAdapter;
    private List<PackedItem> packedAgainstList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;
    private AlertDialog dialogUploadDoc;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<PackageFile> uploadDocumentList = new ArrayList<>();
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
    private ProgressBar dialogUploadProgressBar;
    private TextView btnAddImageView;
    private TextView btnUpload;
    private String typeForPrint;
    private int postionForPrint;
    private TextView textViewEmpty;
    private ImageView imageViewLoader;
    private String stringSearchText;
    private String chkid;

    public void addDocument(String selectedFilePath, String fileName) {
        PackageFile uploadDocument = new PackageFile();
        uploadDocument.setName(fileName);
        uploadDocument.setFileUrl(selectedFilePath);
        uploadDocument.setType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        uploadDocumentList.add(uploadDocument);
        documentUploaderAdapter.notifyDataSetChanged();

        if(uploadDocumentList.size()>0){
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_attempt_failed, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packedAgainstRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.attempt_failed_refresh_layout);
        packedAgainstRecyclerView = (RecyclerView) rootView.findViewById(R.id.attempt_failed_recycler_view);
        packedAgainstEmptyView = (TextView) rootView.findViewById(R.id.attempt_failed_empty_view);
        imageViewLoader = (ImageView) rootView.findViewById(R.id.imageView_loader);
        packedAgainstStockAdapter = new PackedAgainstStockAdapter(getActivity(), packedAgainstList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        packedAgainstRecyclerView.setLayoutManager(mLayoutManager);
        packedAgainstRecyclerView.setHasFixedSize(true);
        packedAgainstRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedAgainstRecyclerView.setNestedScrollingEnabled(false);
        packedAgainstRecyclerView.setAdapter(packedAgainstStockAdapter);


          doRefresh();

        //Scroll Listener
        packedAgainstRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && packedAgainstList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (getActivity() != null && isAdded()) {
                            showLoader();
                          //  getPackedAgainstStockList(packedAgainstList.get(totalItemCount - 1).getCreatedTs(), "2");
                            getPackedAgainstStockList(packedAgainstList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");
                        }
                    } else {
                        if (packedAgainstRefreshLayout.isRefreshing()) {
                            packedAgainstRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        packedAgainstRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        packedAgainstEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packedAgainstEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    Log.d("fool3","0");
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
        TextView textViewActionPackageStatus;
        Button btnOk;
        ProgressBar dialogSelectActionsProgressBar;
        Button btnCancel;
        ImageView imageViewBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsItemsInsidePackage = (LinearLayout) dialogView.findViewById(R.id.actions_items_inside_package);
        actionsPackageStatus = (LinearLayout) dialogView.findViewById(R.id.actions_package_status);
        TextView itemsInsideTextView = (TextView) dialogView.findViewById(R.id.items_inside_textView);
        TextView textCancel = (TextView) dialogView.findViewById(R.id.actions_package_status_text);

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
//Edit Package
        itemsInsideTextView.setText("Edit Package");
        // actionsItemsInsidePackage.setVisibility(View.GONE);

        //Cancel Package
        textCancel.setText("Cancel Package");

        if(packedAgainstList.get(position).getInvid().equals("0")){
            actionsPrintInvoice.setVisibility(View.GONE);
        }

        //Hidden Items
        actionsItemsInsidePackage.setVisibility(View.VISIBLE);
        actionsPackageStatus.setVisibility(View.VISIBLE);
        actionsOtherDocuments.setVisibility(View.GONE);
        actionsPrintGatepass.setVisibility(View.GONE);
        actionsPrintLoadingSlip.setVisibility(View.GONE);

        //Edit Package
        actionsItemsInsidePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentEditPackage = new Intent(getActivity(), EditPackageActivity.class);
                intentEditPackage.putExtra("packageId", packedAgainstList.get(position).getPackageId());
                intentEditPackage.putExtra("pacid", packedAgainstList.get(position).getPacid());
                startActivity(intentEditPackage);

            }
        });

        //Cancel Package
        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    if (packedAgainstList != null && packedAgainstList.size() > 0) {
                        cancelPackedPackage(packedAgainstList.get(position).getPacid());
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                if (dialogSelectEvent != null && dialogSelectEvent.isShowing()) {
                    dialogSelectEvent.dismiss();
                }

            }
        });


      /*  actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               *//* Intent intentStatus = new Intent(getActivity(), ChangePackageStatusActivity.class);
                startActivity(intentStatus);*//*
                dialogRevertPackageDelivery();
            }
        });
*/

        actionsPackageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentPackageHistory = new Intent(getActivity(), PackageHistoryActivity.class);
                intentPackageHistory.putExtra("packageid", packedAgainstList.get(position).getPacid().toString());
                startActivity(intentPackageHistory);
            }
        });

        actionsOtherDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                openDialogUploadDoc(getActivity(), packedAgainstList.get(position).getPacid().toString());
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
                intentCustomerDetails.putExtra(getString(R.string.pacId), packedAgainstList.get(position).getPacid().toString());
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
                    typeForPrint = getString(R.string.invoice);
                    postionForPrint = position;
                } else {
                    downloadDialog(packedAgainstList.get(position).getPackageId(), getString(R.string.invoice),
                            packedAgainstList.get(position).getCuid(), packedAgainstList.get(position).getInvid());
                }
            }
        });

        //Download Delivery Challan
        actionsPrintDeliveryChallan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    askStoragePermission(position, getString(R.string.challan));
                    typeForPrint = getString(R.string.challan);
                    postionForPrint = position;

                } else {
                    downloadDialog(packedAgainstList.get(position).getPackageId(), getString(R.string.challan),
                            packedAgainstList.get(position).getPacid(), "");
                }
            }
        });

        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }


    //Opening Dialog to Upload Documents
    private void openDialogUploadDoc(final Activity activity, final String pacId) {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_upload_doc, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        dialogUploadDoc = builder.create();
        dialogUploadDoc.setCanceledOnTouchOutside(false);

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogUploadDocRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_upload_doc_recycler_view);
        btnAddImageView = (TextView) dialogView.findViewById(R.id.select_file_textView);
        btnUpload = (TextView) dialogView.findViewById(R.id.btn_upload);
        dialogUploadProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_upload_progress_bar);
        final TextView btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        textViewEmpty = (TextView) dialogView.findViewById(R.id.dialog_upload_doc_empty_view);
        final ImageView imageView = (ImageView) dialogView.findViewById(R.id.imageView_loader);
        documentUploaderAdapter = new DocumentUploaderAdapter(getActivity(), uploadDocumentList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);
        if(uploadDocumentList.size()>0){
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }

        btnCancel.setEnabled(true);

        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (dialogUploadDoc != null) {

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (imageView.getVisibility() == View.GONE) {
                                                imageView.setVisibility(View.VISIBLE);
                                            }
                                            //Disable Touch
                                            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                            btnCancel.setEnabled(false);
                                            Ion.with(imageView)
                                                    .animateGif(AnimateGifMode.ANIMATE)
                                                    .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                                    .withBitmapInfo();
                                        }
                                    });
                                }
                            });

                            thread.start();
                        }

                       /* FilesUploadingAsyncTask filesUploadingAsyncTask = new FilesUploadingAsyncTask(activity, uploadDocumentList, pacId, dialogUploadDoc, imageView,btnCancel);
                        filesUploadingAsyncTask.execute();
*/

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
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
                if (getActivity() != null) {
                    if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                        imageView.setVisibility(View.GONE);
                    }

                    //Enable Touch Back
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }
                dialogUploadDoc.dismiss();
            }
        });

        //Call Intent to select file and add into List
        btnAddImageView.setOnClickListener(new View.OnClickListener() {
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

    //Cancel Package
    private void  cancelPackedPackage(String pacid) {
        task = getString(R.string.task_cancel_package);
        String chkid = null;
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.cancelPackage(version, key, task, userId, accessToken, chkid, pacid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        //Refresh List
                        packedAgainstList.clear();
                        doRefresh();
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("warehouse", t.getMessage());
            }
        });
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
            }
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

    //Intent to select file
    //Intent to select file
    private void selectFile() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            askStoragePermission(0, getString(R.string.add_file));
            typeForPrint = getString(R.string.add_file);
            //   postionForPrint = position;
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
        }

        dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
        textViewEmpty.setVisibility(View.GONE);


    }

    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
            uploadDocumentList.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            if (uploadDocumentList != null && uploadDocumentList.size() > 0) {
                dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } else {
                dialogUploadDocRecyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText("No file selected");
            }
        }else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    @Override
    public void onExecute() {

    }

    public void clearListAndRefresh() {
        Log.d("fool2","0");
        stringSearchText ="";
        if (packedAgainstList != null && packedAgainstList.size() > 0) {
            packedAgainstList.clear();
        }
        packedAgainstRecyclerView.removeAllViewsInLayout();
        packedAgainstStockAdapter.notifyDataSetChanged();
        stringSearchText ="";
        doRefresh();
    }

    public void doRefresh() {
        if (packedAgainstList != null && packedAgainstList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && isAdded()) {
                            showLoader();
                            packedAgainstEmptyView.setText(getString(R.string.no_data_available));
                            getPackedAgainstStockList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
                        }
                    }
                }, 200);
            } else {
                packedAgainstEmptyView.setVisibility(View.VISIBLE);
                packedAgainstEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getPackedAgainstStockList(final String time, final String traversalValue,
    String searchValue, String startDate, String endDate){
        String task = getString(R.string.packed_packages);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageLists(version, key, task, userId, accessToken, chkid,
                time, traversalValue, searchValue, startDate, endDate,"2");
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
                        for (final PackedItem packedItem : apiResponse.getData().getPackedItems()) {
                            if (packedItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(packedItem.getCreatedTs())) {
                                        packedAgainstList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (packedAgainstRefreshLayout != null &&
                                            packedAgainstRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedAgainstList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedAgainstList.add(packedItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (packedAgainstList != null && packedAgainstList.size() == 0) {
                            packedAgainstEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            packedAgainstEmptyView.setVisibility(View.GONE);
                        }
                        if (packedAgainstRefreshLayout != null &&
                                packedAgainstRefreshLayout.isRefreshing()) {
                            packedAgainstRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            packedAgainstStockAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedAgainstStockAdapter.notifyDataSetChanged();
                                packedAgainstRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (packedAgainstList != null && packedAgainstList.size() == 0) {
                            packedAgainstEmptyView.setVisibility(View.VISIBLE);
                        } else {
                            packedAgainstEmptyView.setVisibility(View.GONE);
                        }
                        if (packedAgainstRefreshLayout != null && packedAgainstRefreshLayout.isRefreshing()) {
                            packedAgainstRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            packedAgainstStockAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedAgainstStockAdapter.notifyDataSetChanged();
                                packedAgainstRecyclerView.smoothScrollToPosition(0);
                            }
                        }

                        if (apiResponse.getSuccess() != null && apiResponse.getMessage() != null) {
                            //    Toast.makeText(getActivity(), apiResponse.getMessage() + "", Toast.LENGTH_SHORT).show();
                        } else {
                            //   Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                        }


                    }

                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (packedAgainstRefreshLayout != null && packedAgainstRefreshLayout.isRefreshing()) {
                        packedAgainstRefreshLayout.setRefreshing(false);
                    }
                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();

                    }
                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();

                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (packedAgainstList != null && packedAgainstList.size() > 0) {
              /*  if (getActivity() != null && isAdded()) {
                    showLoader();
                    getPackedAgainstStockList(packedAgainstList.get(0).getCreatedTs(), "1");
                }
            } else {
                if (getActivity() != null && isAdded()) {
                    showLoader();
                    getPackedAgainstStockList(getString(R.string.last_updated_date), "1");
                }*/
              packedAgainstList.clear();
            }
            packedAgainstRefreshLayout.setRefreshing(true);
            getPackedAgainstStockList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
          //  packedAgainstEmptyView.setVisibility(View.GONE);
         //   packedAgainstRefreshLayout.setRefreshing(true);
        } else {
            packedAgainstEmptyView.setVisibility(View.VISIBLE);
            packedAgainstEmptyView.setText(getString(R.string.no_internet_try_later));
            packedAgainstRefreshLayout.setRefreshing(false);
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
        } else {
            downloadConfirmMessage.setText(getString(R.string.download_delivery_challan_confirm_msg));
            titleDownloadTextView.setText("Download delivery challan");
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
        Call<ApiResponse> call;

        if (type.equals(getString(R.string.invoice))) {
            task = getString(R.string.download_invoice_task);
            call = apiService.downloadInvoicePDF(version, key, task, userId, accessToken,
                    cuIdPacId, invId);
        } else {
            task = getString(R.string.download_delivery_challan_task);
            call = apiService.downloadChallanPDF(version, key, task, userId, accessToken,
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

    public void downloadPDF() {
        if (typeForPrint.equals(getString(R.string.challan))) {
            downloadDialog(packedAgainstList.get(postionForPrint).getPackageId(), getString(R.string.challan),
                    packedAgainstList.get(postionForPrint).getPacid(), "");
        } else if (typeForPrint.equals(getString(R.string.add_file))) {
            if (dialogUploadDoc != null && dialogUploadDoc.isShowing()) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
            }
        } else {
            downloadDialog(packedAgainstList.get(postionForPrint).getPackageId(), getString(R.string.invoice),
                    packedAgainstList.get(postionForPrint).getCuid(), packedAgainstList.get(postionForPrint).getInvid());
        }
    }

    private void hideLoader() {
        if (getActivity() != null) {
            if (imageViewLoader != null && imageViewLoader.getVisibility() == View.VISIBLE) {
                imageViewLoader.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null) {
                            if (packedAgainstList != null && packedAgainstList.size() == 0) {
                                if (imageViewLoader.getVisibility() == View.GONE) {
                                    imageViewLoader.setVisibility(View.VISIBLE);
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Ion.with(imageViewLoader)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    }
                });
            }
        });

        thread.start();
    }


    public void searchAPI(final String searchText) {
           Log.d("fool1","0");
        stringSearchText = searchText;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {

                    if (packedAgainstList != null) {
                        if (packedAgainstList.size() > 0) {
                            packedAgainstList.clear();
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    packedAgainstStockAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            //  loading = true;
                            showLoader();
                            getPackedAgainstStockList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
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
            }
        });
        thread.start();

    }

}
