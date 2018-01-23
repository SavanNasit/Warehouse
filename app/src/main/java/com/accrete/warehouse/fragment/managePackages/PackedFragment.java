package com.accrete.warehouse.fragment.managePackages;

import android.Manifest;
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
import android.widget.AutoCompleteTextView;
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
import com.accrete.warehouse.adapter.PackedItemWithoutCheckboxAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PackedItem;
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

    /*   private void dialogAddPackages() {
           View dialogView = View.inflate(getActivity(), R.layout.dialog_add_packages, null);
           AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
           builder.setView(dialogView)
                   .setCancelable(true);
           final AlertDialog dialogDeliver = builder.create();
           dialogDeliver.setCanceledOnTouchOutside(true);
           AddedItemsAdapter addedItemsAdapter;
           TextView addPackagesCancel;
           AutoCompleteTextView addPackagesSearchWithPackageId;
           RecyclerView dialogAddPackagesRecyclerView;
           TextView dialogAddPackagesSelectShippingCompany;
           List<AlreadyAddedItem> alreadyAddedItemList = new ArrayList<>();
           AlreadyAddedItem alreadyAddedItem = new AlreadyAddedItem();

           addPackagesCancel = (TextView) dialogView.findViewById(R.id.add_packages_cancel);
           addPackagesSearchWithPackageId = (AutoCompleteTextView) dialogView.findViewById(R.id.add_packages_search_with_package_id);
           dialogAddPackagesRecyclerView = (RecyclerView) dialogView.findViewById(R.id.dialog_add_packages_recycler_view);
           dialogAddPackagesSelectShippingCompany = (TextView) dialogView.findViewById(R.id.dialog_add_packages_select_shipping_company);
           addedItemsAdapter = new AddedItemsAdapter(getActivity(), alreadyAddedItemList, this);
           LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
           dialogAddPackagesRecyclerView.setLayoutManager(mLayoutManager);
           dialogAddPackagesRecyclerView.setHasFixedSize(true);
           dialogAddPackagesRecyclerView.setItemAnimator(new DefaultItemAnimator());
           dialogAddPackagesRecyclerView.setNestedScrollingEnabled(false);
           dialogAddPackagesRecyclerView.setAdapter(addedItemsAdapter);

           dialogAddPackagesSelectShippingCompany.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialogCreateGatepass();
               }
           });

           addPackagesCancel.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   dialogDeliver.dismiss();
               }
           });


           alreadyAddedItem.setPackageID("PAK1002890001");
           alreadyAddedItemList.add(alreadyAddedItem);
           alreadyAddedItemList.add(alreadyAddedItem);
           alreadyAddedItemList.add(alreadyAddedItem);
           alreadyAddedItemList.add(alreadyAddedItem);
           alreadyAddedItemList.add(alreadyAddedItem);
           alreadyAddedItemList.add(alreadyAddedItem);

           dialogDeliver.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
           if (!dialogDeliver.isShowing()) {
               dialogDeliver.show();
           }
       }*/
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<String> documentList = new ArrayList<>();
    private TextView downloadConfirmMessage, titleDownloadTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;

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
     /*   packedAdd = (TextView) rootView.findViewById(R.id.packed_text_add);
        packedDeliver = (TextView) rootView.findViewById(R.id.packed_text_deliver);*/

        packedItemAdapter = new PackedItemWithoutCheckboxAdapter(getActivity(), packedList, this);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        packedRecyclerView.setLayoutManager(mLayoutManager);
        packedRecyclerView.setHasFixedSize(true);
        packedRecyclerView.setItemAnimator(new DefaultItemAnimator());
        packedRecyclerView.setNestedScrollingEnabled(false);
        packedRecyclerView.setAdapter(packedItemAdapter);

       /* packedAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedAdd.setBackgroundColor(getResources().getColor(R.color.add_dark_blue));
               //dialogAddPackages();
            }
        });
        packedDeliver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                packedDeliver.setBackgroundColor(getResources().getColor(R.color.add_dark_red));
                dialogDeliverPackages();
            }
        });
*/
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
                        getPackageDetailsList(packedList.get(totalItemCount - 1).getCreatedTs(), "2");
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
                        if (isAdded()) {
                            packedEmptyView.setText(getString(R.string.no_data_available));
                            getPackageDetailsList(getString(R.string.last_updated_date), "1");
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

    private void apiCall() {
        String status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            loading = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getPackageDetailsList(getString(R.string.last_updated_date), "1");
                    packedRecyclerView.setVisibility(View.VISIBLE);
                    packedEmptyView.setVisibility(View.GONE);
                    packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
                }
            }, 00);
        } else {
            packedRecyclerView.setVisibility(View.VISIBLE);
            packedEmptyView.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
            packedEmptyView.setText(getString(R.string.no_internet_try_later));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    @Override
    public void onExecute() {

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

        actionsItemsInsidePackage.setVisibility(View.GONE);
        actionsPackageStatus.setVisibility(View.GONE);

        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intentStatus = new Intent(getActivity(), ChangePackageStatusActivity.class);
                startActivity(intentStatus);*/
                // dialogRevertPackageDelivery();
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
                dialogUploadDoc();
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
                    downloadDialog(packedList.get(position).getPackageId(), getString(R.string.challan),
                            packedList.get(position).getPacid(), "");
                } else if (type.equals(getString(R.string.invoice))) {
                    downloadDialog(packedList.get(position).getPackageId(), getString(R.string.invoice),
                            packedList.get(position).getCuid(), packedList.get(position).getInvid());
                }

            }
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
    public void onExecute(ArrayList<String> packageIdList) {
    }

    private void getPackageDetailsList(final String updatedDate, final String traversalValue) {
        task = getString(R.string.packed_packages_list_task);
        String chkid = null;

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            chkid = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPackageDetails(version, key, task, userId, accessToken, chkid, "1", updatedDate,
                traversalValue);
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
                                    if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                        packedList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (packedSwipeRefreshLayout != null &&
                                            packedSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                            packedList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!updatedDate.equals(packedItem.getCreatedTs())) {
                                            packedList.add(packedItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
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

                    if (packedSwipeRefreshLayout != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
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
            if (packedList != null && packedList.size() > 0) {
                getPackageDetailsList(packedList.get(0).getCreatedTs(), "1");
            } else {
                getPackageDetailsList(getString(R.string.last_updated_date), "1");
            }
            packedRecyclerView.setVisibility(View.VISIBLE);
            packedEmptyView.setVisibility(View.GONE);
            packedSwipeRefreshLayout.setVisibility(View.VISIBLE);
            packedSwipeRefreshLayout.setRefreshing(true);
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
}
