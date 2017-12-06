package com.accrete.warehouse.fragment;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.accrete.warehouse.CustomerDetailsActivity;
import com.accrete.warehouse.ItemsInsidePackageActivity;
import com.accrete.warehouse.PackageHistoryActivity;
import com.accrete.warehouse.PackageOrderStatusActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.DocumentUploaderAdapter;
import com.accrete.warehouse.adapter.OutForDeliveryAdapter;
import com.accrete.warehouse.model.OutForDelivery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 11/30/17.
 */

public class OutForDeliveryFragment extends Fragment implements DocumentUploaderAdapter.DocAdapterListener, OutForDeliveryAdapter.OutForDeliveryAdapterListener {
    OutForDelivery outForDelivery = new OutForDelivery();
    private SwipeRefreshLayout outForDeliverySwipeRefreshLayout;
    private RecyclerView pendingItemsRecyclerView;
    private TextView outForDeliveryEmptyView;
    private OutForDeliveryAdapter outForDeliveryAdapter;
    private List<OutForDelivery> outForDeliveryList = new ArrayList<>();
    private AlertDialog dialogSelectEvent;
    private AlertDialog dialogUploadDoc;
    private DocumentUploaderAdapter documentUploaderAdapter;
    private List<String> documentList = new ArrayList<>();


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
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        pendingItemsRecyclerView.setLayoutManager(mLayoutManager);
        pendingItemsRecyclerView.setHasFixedSize(true);
        pendingItemsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        pendingItemsRecyclerView.setNestedScrollingEnabled(false);
        pendingItemsRecyclerView.setAdapter(outForDeliveryAdapter);

        outForDelivery.setPackageId("RPDPAK20171128001687");
        outForDelivery.setInvoiceNumber("1343434");
        outForDelivery.setInvoiceDate("1853");
        outForDelivery.setCustomerName("2 pieces");
        outForDelivery.setGatePassId("RPDPAKGP000466");
        outForDelivery.setOrderId("RPD23423100119");
        outForDelivery.setExpdod("NA");
        outForDelivery.setDeliveryUser("Admin");

        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
        outForDeliveryList.add(outForDelivery);
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

        actionsPackageStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStatus = new Intent(getActivity(), PackageOrderStatusActivity.class);
                startActivity(intentStatus);
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
}
