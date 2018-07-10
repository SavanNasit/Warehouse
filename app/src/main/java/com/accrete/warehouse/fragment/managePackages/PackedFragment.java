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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.CustomerDetailsActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.PackedItemWithoutCheckboxAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.PackageFile;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PackedItem;
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
 * Created by poonam on 11/30/17.
 */

public class PackedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        DocumentUploaderAdapter.DocAdapterListener, PackedItemWithoutCheckboxAdapter.PackedItemAdapterListener {
    String status = "";
    private SwipeRefreshLayout packedSwipeRefreshLayout;
    private RecyclerView packedRecyclerView;
    private TextView packedEmptyView, packedAdd, packedDeliver;
    private PackedItemWithoutCheckboxAdapter packedItemAdapter;
    private List<PackedItem> packedList = new ArrayList<>();
    private Packages packages = new Packages();
    private boolean loading;
    private int lastVisibleItem, totalItemCount;
    private int visibleThreshold = 2;
    private String dataChanged;
    private AlertDialog dialogSelectEvent;
    private AlertDialog dialogUploadDoc;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<PackageFile> uploadDocumentList = new ArrayList<>();
    private List<PackageFile> viewUploadDocuments = new ArrayList<>();
    private List<PackageFile> fileUploadList = new ArrayList<>();
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private RecyclerView dialogUploadDocRecyclerView;
    private TextView btnAddImageView;
    private TextView btnUpload;
    private ProgressBar dialogUploadProgressBar;
    private String typeForPrint;
    private int postionForPrint;
    private TextView textViewEmpty;
    private ImageView imageViewLoader;
    private String stringSearchText;

    //Remove file/document from list
    @Override
    public void onClickedDeleteBtn(int position) {
        if (viewUploadDocuments != null && viewUploadDocuments.size() > 0) {
            viewUploadDocuments.remove(position);
            documentUploaderAdapter.notifyDataSetChanged();
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
            if (viewUploadDocuments != null && viewUploadDocuments.size() > 0) {
                dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
                textViewEmpty.setVisibility(View.GONE);
            } else {
                dialogUploadDocRecyclerView.setVisibility(View.GONE);
                textViewEmpty.setVisibility(View.VISIBLE);
                textViewEmpty.setText("No file selected");
            }
        } else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_packed, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        packedSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.packed_swipe_refresh_layout);
        packedRecyclerView = (RecyclerView) rootView.findViewById(R.id.packed_recycler_view);
        packedEmptyView = (TextView) rootView.findViewById(R.id.packed_empty_view);
        imageViewLoader = (ImageView) rootView.findViewById(R.id.imageView_loader);

        packedItemAdapter = new PackedItemWithoutCheckboxAdapter(getActivity(), packedList, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packedRecyclerView.setLayoutManager(mLayoutManager);
        packedRecyclerView.setHasFixedSize(true);
        packedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedRecyclerView.setNestedScrollingEnabled(false);
        packedRecyclerView.setAdapter(packedItemAdapter);

        doRefresh();

        //Scroll Listener
        packedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && packedList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (getActivity() != null && isAdded()) {
                            // getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2");
                            getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2", stringSearchText, "", "");
                        }
                    } else {
                        if (packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        packedSwipeRefreshLayout.setOnRefreshListener(this);

        //Load data after getting connection
        packedEmptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (packedEmptyView.getText().toString().trim().equals(getString(R.string.no_internet_try_later))) {
                    doRefresh();
                }
            }
        });


    }

    public void doRefresh() {
        if (packedList != null && packedList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getActivity() != null && isAdded()) {
                            showLoader();
                            packedEmptyView.setText(getString(R.string.no_data_available));
                            getPackageDetailsList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
                        }
                    }
                }, 200);
            } else {
                packedRecyclerView.setVisibility(View.GONE);
                packedEmptyView.setVisibility(View.VISIBLE);
                packedEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void dialogCreateGatepass() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_create_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogCreateGatepass = builder.create();
        dialogCreateGatepass.setCanceledOnTouchOutside(true);

        LinearLayout linearLayout;
        TextView cancelGatepassTitle;
        AutoCompleteTextView dialogCreateGatepassShippingBy;
        AutoCompleteTextView dialogCreateGatepassVehicleNumber;
        AutoCompleteTextView dialogCreateGatepassShippingType;
        AutoCompleteTextView dialogCreateGatepassShippingCompany;
        Button btnOk;
        ProgressBar cancelGatepassProgressBar;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        cancelGatepassTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_title);
        dialogCreateGatepassShippingBy = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_by);
        dialogCreateGatepassVehicleNumber = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_vehicle_number);
        dialogCreateGatepassShippingType = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_type);
        dialogCreateGatepassShippingCompany = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_create_gatepass_shipping_company);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCreateGatepass.dismiss();
            }
        });

        dialogCreateGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogCreateGatepass.isShowing()) {
            dialogCreateGatepass.show();
        }
    }

    private void dialogConfirmGatepass() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_gatepass_authentication, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogConfirmGatepass = builder.create();
        dialogConfirmGatepass.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        TextView dialogGatepassAuthenticationTitle;
        AutoCompleteTextView dialogGatepassAuthenticationDeliveryUser;
        Button dialogGatepassAuthenticationConfirm;
        ProgressBar cancelGatepassProgressBar;
        Button dialogGatepassAuthenticationCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        dialogGatepassAuthenticationTitle = (TextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_title);
        dialogGatepassAuthenticationDeliveryUser = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_delivery_user);
        dialogGatepassAuthenticationConfirm = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_confirm);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        dialogGatepassAuthenticationCancel = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_cancel);

        dialogGatepassAuthenticationConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass.dismiss();
            }
        });

        dialogGatepassAuthenticationCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogConfirmGatepass.dismiss();
            }
        });

        dialogConfirmGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogConfirmGatepass.isShowing()) {
            dialogConfirmGatepass.show();
        }
    }

    public void clearListAndRefresh() {
        if (packedList != null && packedList.size() > 0) {
            packedList.clear();
        }
        packedItemAdapter.notifyDataSetChanged();
        stringSearchText = "";
        doRefresh();
    }

    private void dialogDeliverPackages() {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_cancel_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog dialogDeliver = builder.create();
        dialogDeliver.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        Button btnOk;
        ProgressBar cancelGatepassProgressBar;
        TextView textViewMessage, textViewTitle;
        Button btnCancel;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        btnOk = (Button) dialogView.findViewById(R.id.btn_ok);
        cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
        textViewMessage = (TextView) dialogView.findViewById(R.id.cancel_gatepass_message);
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        textViewTitle = (TextView) dialogView.findViewById(R.id.cancel_gatepass_title);
        textViewTitle.setText("Deliver");
        textViewMessage.setText("Do you want to deliver selected packages without selecting Shipping company and delivery user?");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeliver.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDeliver.dismiss();
            }
        });

        dialogDeliver.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogDeliver.isShowing()) {
            dialogDeliver.show();
        }
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
        TextView itemsInsideTextView;
        TextView editPackageTextView;
        LinearLayout actionsPackageStatus;
        TextView actionsPackageStatusText;
        LinearLayout actionsPackageHistory;
        LinearLayout actionsCustomerDetails;
        LinearLayout actionsPrintInvoice;
        LinearLayout actionsPrintDeliveryChallan;
        LinearLayout actionsOtherDocuments;
        LinearLayout actionsPrintGatepass;
        LinearLayout actionsPrintLoadingSlip;
        LinearLayout actionsEditPackage;
        TextView textViewActionPackageStatus;
        Button btnOk;
        ProgressBar dialogSelectActionsProgressBar;
        Button btnCancel;
        ImageView imageViewBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsEditPackage = (LinearLayout) dialogView.findViewById(R.id.actions_edit_package);
        editPackageTextView = (TextView) dialogView.findViewById(R.id.edit_package_textView);
        actionsItemsInsidePackage = (LinearLayout) dialogView.findViewById(R.id.actions_items_inside_package);
        itemsInsideTextView = (TextView) dialogView.findViewById(R.id.items_inside_textView);
        actionsPackageStatus = (LinearLayout) dialogView.findViewById(R.id.actions_package_status);
        actionsPackageStatusText = (TextView) dialogView.findViewById(R.id.actions_package_status_text);
        actionsPackageHistory = (LinearLayout) dialogView.findViewById(R.id.actions_package_history);
        actionsCustomerDetails = (LinearLayout) dialogView.findViewById(R.id.actions_customer_details);
        actionsPrintInvoice = (LinearLayout) dialogView.findViewById(R.id.actions_print_invoice);
        actionsPrintDeliveryChallan = (LinearLayout) dialogView.findViewById(R.id.actions_print_delivery_challan);
        actionsOtherDocuments = (LinearLayout) dialogView.findViewById(R.id.actions_other_documents);
        actionsPrintGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_print_gatepass);
        actionsPrintLoadingSlip = (LinearLayout) dialogView.findViewById(R.id.actions_print_loading_slip);
        dialogSelectActionsProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_select_warehouse_progress_bar);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);
        textViewActionPackageStatus = (TextView) dialogView.findViewById(R.id.actions_package_status_text);

        actionsPrintGatepass.setVisibility(View.GONE);
        actionsPrintLoadingSlip.setVisibility(View.GONE);
        // actionsItemsInsidePackage.setVisibility(View.GONE);
        // actionsPackageStatus.setVisibility(View.GONE);

        if (packedList.get(position).getInvid().equals("0")) {
            actionsPrintInvoice.setVisibility(View.GONE);
        }

        //Edit Package
        itemsInsideTextView.setText("Edit Package");
        // actionsItemsInsidePackage.setVisibility(View.GONE);

        //Cancel Package
        actionsPackageStatusText.setText("Cancel Package");

        //Cancel Package
        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    if (packedList != null && packedList.size() > 0) {
                        cancelPackedPackage(packedList.get(position).getPacid());
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                if (dialogSelectEvent != null && dialogSelectEvent.isShowing()) {
                    dialogSelectEvent.dismiss();
                }

            }
        });

        //Edit Package
        actionsItemsInsidePackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentEditPackage = new Intent(getActivity(), EditPackageActivity.class);
                intentEditPackage.putExtra("packageId", packedList.get(position).getPackageId());
                intentEditPackage.putExtra("pacid", packedList.get(position).getPacid());
                startActivity(intentEditPackage);

            }
        });

        actionsPackageHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                Intent intentPackageHistory = new Intent(getActivity(), PackageHistoryActivity.class);
                intentPackageHistory.putExtra("packageid", packedList.get(position).getPacid().toString());
                startActivity(intentPackageHistory);
            }
        });

        actionsOtherDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
                openDialogUploadDoc(getActivity(), packedList.get(position).getPacid().toString(), position);
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
                intentCustomerDetails.putExtra(getString(R.string.pacId), packedList.get(position).getPacid().toString());
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
                    downloadDialog(packedList.get(position).getPackageId(), getString(R.string.invoice),
                            packedList.get(position).getCuid(), packedList.get(position).getInvid());
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
                    typeForPrint = getString(R.string.challan);
                    postionForPrint = position;

                } else {
                    downloadDialog(packedList.get(position).getPackageId(), getString(R.string.challan),
                            packedList.get(position).getPacid(), "");
                }
            }
        });

        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }

    public void askStoragePermission(int position, String type) {
        if (checkPermissionWithRationale(getActivity(), new PackedFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

    }


    //Opening Dialog to Upload Documents
    private void openDialogUploadDoc(final Activity activity, final String pacId, int position) {
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
        documentUploaderAdapter = new DocumentUploaderAdapter(getActivity(), viewUploadDocuments, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(activity);
        dialogUploadDocRecyclerView.setLayoutManager(mLayoutManager);
        dialogUploadDocRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        dialogUploadDocRecyclerView.setAdapter(documentUploaderAdapter);

        if (viewUploadDocuments.size() > 0) {
            viewUploadDocuments.clear();
        }else if (uploadDocumentList.size()>0){
            uploadDocumentList.clear();
        }else if (fileUploadList.size()>0){
            fileUploadList.clear();
        }

        downloadUploadedDocs(pacId, position);


        btnCancel.setEnabled(true);

        //Upload files and dismiss dialog
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewUploadDocuments != null && viewUploadDocuments.size() > 0) {
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

                        FilesUploadingAsyncTask filesUploadingAsyncTask = new FilesUploadingAsyncTask(activity, fileUploadList, pacId, dialogUploadDoc, imageView, btnCancel, uploadDocumentList);
                        filesUploadingAsyncTask.execute();

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
                if (viewUploadDocuments != null && viewUploadDocuments.size() > 0) {
                    viewUploadDocuments.clear();
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

    //Intent to select file
    private void selectFile() {
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            askStoragePermission(0, getString(R.string.add_file));
            typeForPrint = getString(R.string.add_file);
            //postionForPrint = position;
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
        }
    }

    @Override
    public void onExecute(ArrayList<String> packageIdList) {
    }

    private void getPackageDetailsList(final String time, final String traversalValue,
                                       String searchValue, String startDate, String endDate) {
        task = getString(R.string.packed_packages_list_task);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageLists(version, key, task, userId, accessToken, chkid,
                time, traversalValue, searchValue, startDate, endDate, "1");
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
                                        packedList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                  /*  if (packedSwipeRefreshLayout != null &&
                                            packedSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            packedList.add(packedItem);
                                        }
                                    }*/
                                    packedList.add(packedItem);
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (packedList != null && packedList.size() == 0) {
                            packedEmptyView.setVisibility(View.VISIBLE);
                            packedEmptyView.setText("No data available");
                            packedRecyclerView.setVisibility(View.GONE);
                            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        } else {
                            packedEmptyView.setVisibility(View.GONE);
                            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            packedRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (packedSwipeRefreshLayout != null &&
                                packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            packedItemAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedItemAdapter.notifyDataSetChanged();
                                packedRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        if (packedList != null && packedList.size() == 0) {
                            packedEmptyView.setVisibility(View.VISIBLE);
                            packedEmptyView.setText("No data available");
                            packedRecyclerView.setVisibility(View.VISIBLE);
                            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        } else {
                            packedEmptyView.setVisibility(View.GONE);
                            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                            packedRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (packedSwipeRefreshLayout != null &&
                                packedSwipeRefreshLayout.isRefreshing()) {
                            packedSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            packedItemAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                packedItemAdapter.notifyDataSetChanged();
                                packedRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    }

                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                    }
                    if (packedSwipeRefreshLayout != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null && isAdded()) {
                        hideLoader();
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Toast.makeText(ApiCallService.this, "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
                if (getActivity() != null && isAdded()) {
                    hideLoader();
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
          /*  if (packedList != null && packedList.size() > 0) {
                if (getActivity() != null && isAdded()) {
                    showLoader();
                    getPackageDetailsList(packedList.get(0).getCreatedTs(), "1");
                }
            } else {*/
            if (getActivity() != null && isAdded()) {
                if (packedList != null && packedList.size() > 0) {
                    packedList.clear();
                }
                showLoader();
                getPackageDetailsList(getString(R.string.last_updated_date), "1", stringSearchText, "", "");
                //}
            }
          /*  packedRecyclerView.setVisibility(View.VISIBLE);
            packedEmptyView.setVisibility(View.GONE);
            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setRefreshing(true);*/
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            packedRecyclerView.setVisibility(View.VISIBLE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
            packedSwipeRefreshLayout.setRefreshing(false);
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

    //Add Document into List
    public void addDocument(String selectedFilePath, String fileName) {
        PackageFile uploadDocument = new PackageFile();
        uploadDocument.setName(fileName);
        uploadDocument.setFileUrl(selectedFilePath);
        uploadDocument.setType(selectedFilePath.substring(selectedFilePath.lastIndexOf(".") + 1, selectedFilePath.length()));
        fileUploadList.add(uploadDocument);
        viewUploadDocuments.addAll(fileUploadList);
        documentUploaderAdapter.notifyDataSetChanged();

        if (viewUploadDocuments.size() > 0) {
            dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        } else {
            dialogUploadDocRecyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            textViewEmpty.setText("No file selected");
        }
    }

    //Cancel Package
    private void cancelPackedPackage(String pacid) {
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
                        packedList.clear();
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

    public void downloadPDF() {
        try {
            // Toast.makeText(getActivity(), "download completed", Toast.LENGTH_SHORT).show();
            if (typeForPrint.equals(getString(R.string.challan))) {
                downloadDialog(packedList.get(postionForPrint).getPackageId(), getString(R.string.challan),
                        packedList.get(postionForPrint).getPacid(), "");
            } else if (typeForPrint.equals(getString(R.string.invoice))) {
                downloadDialog(packedList.get(postionForPrint).getPackageId(), getString(R.string.invoice),
                        packedList.get(postionForPrint).getCuid(), packedList.get(postionForPrint).getInvid());
            } else if (typeForPrint.equals(getString(R.string.add_file))) {
                if (dialogUploadDoc != null && dialogUploadDoc.isShowing()) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    getActivity().startActivityForResult(intent, PICK_FILE_RESULT_CODE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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
                });
            }
        });

        thread.start();
    }

    public void searchAPI(final String searchText) {

        stringSearchText = searchText;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {

                    if (packedList != null) {
                        if (packedList.size() > 0) {
                            packedList.clear();
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    packedItemAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        status = NetworkUtil.getConnectivityStatusString(getActivity());
                        if (!status.equals(getString(R.string.not_connected_to_internet))) {
                            //  loading = true;
                            showLoader();
                            getPackageDetailsList(getString(R.string.last_updated_date), "1", searchText, "", "");
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


    private void downloadUploadedDocs(String pacid, final int postionForDownload) {
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = null;
        task = getString(R.string.task_uploaded_docs);
        call = apiService.getCustomerInfoByPacId(version, key, task, userId, accessToken,
                pacid);

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
                            // dialogUploadDoc.dismiss();
                            uploadDocumentList.addAll(apiResponse.getData().getPackageFiles());
                            viewUploadDocuments.addAll(uploadDocumentList);
                            documentUploaderAdapter.notifyDataSetChanged();
                            //Download a file and display in phone's download folder
                        /*    Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = null;
                            request = new DownloadManager.Request(uri)
                                        .setTitle(apiResponse.getData().getPackageFiles().get(postionForDownload).getActualName() + "" + "")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                apiResponse.getData().getPackageFiles().get(postionForDownload).getActualName()  + "" + "")
                                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                            downloadManager.enqueue(request);*/
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

                    if (viewUploadDocuments.size() > 0) {
                        dialogUploadDocRecyclerView.setVisibility(View.VISIBLE);
                        textViewEmpty.setVisibility(View.GONE);
                    } else {
                        dialogUploadDocRecyclerView.setVisibility(View.GONE);
                        textViewEmpty.setVisibility(View.VISIBLE);
                        textViewEmpty.setText("No file selected");
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

}
