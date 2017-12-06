package com.accrete.warehouse.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
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

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ManageGatepassAdapter;
import com.accrete.warehouse.model.ManageGatepass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageGatePassFragment extends Fragment implements ManageGatepassAdapter.ManageGatepassAdapterrListener {

    private static final String KEY_TITLE = "ManageGatePass";
    private SwipeRefreshLayout manageGatepassSwipeRefreshLayout;
    private RecyclerView manageGatepassRecyclerView;
    private TextView manageGatepassEmptyView;
    private ManageGatepassAdapter manageGatePassAdapter;
    private List<ManageGatepass> gatepassList = new ArrayList<>();
    private ManageGatepass manageGatepass = new ManageGatepass();
    private AlertDialog dialogSelectEvent;

    public static ManageGatePassFragment newInstance(String title) {
        ManageGatePassFragment f = new ManageGatePassFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_manage_gatepass, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View rootView) {
        manageGatepassSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.manage_gatepass_swipe_refresh_layout);
        manageGatepassRecyclerView = (RecyclerView) rootView.findViewById(R.id.manage_gatepass_recycler_view);
        manageGatepassEmptyView = (TextView) rootView.findViewById(R.id.manage_gatepass__empty_view);

        manageGatePassAdapter = new ManageGatepassAdapter(getActivity(), gatepassList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        manageGatepassRecyclerView.setLayoutManager(mLayoutManager);
        manageGatepassRecyclerView.setHasFixedSize(true);
        manageGatepassRecyclerView.setItemAnimator(new DefaultItemAnimator());
        manageGatepassRecyclerView.setNestedScrollingEnabled(false);
        manageGatepassRecyclerView.setAdapter(manageGatePassAdapter);


        manageGatepass.setGatepassID("RPDORDG100138");
        manageGatepass.setPackages("1");
        manageGatepass.setDeliveryUser("Ms Poonam Kukreti");
        manageGatepass.setGeneratedOn("18 Sep, 2017");
        manageGatepass.setGatepassStatus("Running");
        manageGatepass.setShippingCompanyName("AGT Pvt Ltd");
        manageGatepass.setShippingType("Internal");

        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);
        gatepassList.add(manageGatepass);


    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.manage_gatepass_fragment);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.manage_gatepass_fragment));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.manage_gatepass_fragment));
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        dialogItemEvents(position);
    }

    @Override
    public void onExecute() {

    }


    private void dialogItemEvents(int position) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_select_actions_gatepass, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSelectEvent = builder.create();
        dialogSelectEvent.setCanceledOnTouchOutside(true);
        LinearLayout linearLayout;
        LinearLayout actionsFilter;
        LinearLayout actionsViewPackage;
        LinearLayout actionsPrintPackage;
        LinearLayout actionsCancelGatepass;
        ImageView imageBack;

        linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
        actionsFilter = (LinearLayout) dialogView.findViewById(R.id.actions_filter);
        actionsViewPackage = (LinearLayout) dialogView.findViewById(R.id.actions_view_package);
        actionsPrintPackage = (LinearLayout) dialogView.findViewById(R.id.actions_print_package);
        actionsCancelGatepass = (LinearLayout) dialogView.findViewById(R.id.actions_cancel_gatepass);
        imageBack = (ImageView) dialogView.findViewById(R.id.image_back);

        actionsCancelGatepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCancelGatepass();
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectEvent.dismiss();
            }
        });


        dialogSelectEvent.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogSelectEvent.isShowing()) {
            dialogSelectEvent.show();
        }
    }

    private void dialogCancelGatepass() {
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
}
