package com.accrete.warehouse.fragment.receiveConsignment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.ReceiveItemsAdapter;
import com.accrete.warehouse.fragment.manageConsignment.ManageConsignmentFragment;
import com.accrete.warehouse.model.ReceiveSubItems;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/8/17.
 */

public class ReceiveDirectlyFragment extends Fragment implements ReceiveItemsAdapter.ReceiveItemsAdapterListener {
    private static String KEY_TITLE = "receive_directly";

    private AutoCompleteTextView receiveDirectlyVendor;
    private AutoCompleteTextView receiveDirectlyReceiveDate;
    private AutoCompleteTextView receiveDirectlyInvoiceNumber;
    private AutoCompleteTextView receiveDirectlyInvoiceDate;
    private AutoCompleteTextView receiveDirectlyAuthorizedBy;
    private LinearLayout receiveDirectlyAddMore;
    private CheckBox receiveDirectlyTransportationDetails;
    private AutoCompleteTextView receiveDirectlyTansporter;
    private AutoCompleteTextView receiveDirectlyWeight;
    private AutoCompleteTextView receiveDirectlyLrNumber;
    private AutoCompleteTextView receiveDirectlyVehicleNumber;
    private AutoCompleteTextView receiveDirectlyExpectedDate;
    private TextView receiveDirectlyReceiveItems;
    private String strScheduleChecked;
    private LinearLayout linearLayoutFillDetails;
    private RecyclerView recyclerViewDirectly;
    private ReceiveItemsAdapter mAdapter;

    ReceiveSubItems receiveSubItems = new ReceiveSubItems();
    private List<ReceiveSubItems> receiveItemList = new ArrayList<>();


    public static ReceiveDirectlyFragment newInstance(String title) {
        ReceiveDirectlyFragment f = new ReceiveDirectlyFragment();
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        f.setArguments(args);
        return (f);
    }


    private void findViews(View rootView) {

        receiveDirectlyVendor = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_vendor);
        receiveDirectlyReceiveDate = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_receive_date);
        receiveDirectlyInvoiceNumber = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_invoice_number);
        receiveDirectlyInvoiceDate = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_invoice_date);
        receiveDirectlyAuthorizedBy = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_authorized_by);
        receiveDirectlyAddMore = (LinearLayout) rootView.findViewById(R.id.receive_directly_add_more);
        receiveDirectlyTransportationDetails = (CheckBox) rootView.findViewById(R.id.receive_directly_transportation_details);
        receiveDirectlyTansporter = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_tansporter);
        receiveDirectlyWeight = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_weight);
        receiveDirectlyLrNumber = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_lr_number);
        receiveDirectlyVehicleNumber = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_vehicle_number);
        receiveDirectlyExpectedDate = (AutoCompleteTextView) rootView.findViewById(R.id.receive_directly_expected_date);
        receiveDirectlyReceiveItems = (TextView) rootView.findViewById(R.id.receive_directly_receive_items);
        linearLayoutFillDetails = (LinearLayout) rootView.findViewById(R.id.receive_directly_transportation_layout);
        recyclerViewDirectly = (RecyclerView)rootView.findViewById(R.id.receive_directly_recycler_view);
        mAdapter = new ReceiveItemsAdapter(getActivity(), receiveItemList, this);
        recyclerViewDirectly.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewDirectly.setLayoutManager(mLayoutManager);
        recyclerViewDirectly.setItemAnimator(new DefaultItemAnimator());
        recyclerViewDirectly.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));

        strScheduleChecked = "2";

        receiveDirectlyTransportationDetails.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    linearLayoutFillDetails.setVisibility(View.VISIBLE);
                    Animation slide_down = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.slide_down);
                    linearLayoutFillDetails.startAnimation(slide_down);
                    strScheduleChecked = "1";
                } else {
                    linearLayoutFillDetails.setVisibility(View.GONE);
                    Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                            R.anim.slide_up);
                  linearLayoutFillDetails.startAnimation(slide_up);
                    strScheduleChecked = "2";
                }
            }
        });

        receiveDirectlyReceiveItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManageConsignmentFragment manageConsignmentFragment = new ManageConsignmentFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.receive_consignment_container, manageConsignmentFragment).addToBackStack(null).commit();
            }
        });

        receiveDirectlyAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddItems();
            }
        });

        receiveSubItems.setItemVariation("100 Pipers");
        receiveSubItems.setReceivingQuantity("00");
        receiveSubItems.setUnit("00");
        receiveSubItems.setComment("fhduf fhsdufhds ");
        receiveSubItems.setExpiryDate("22,7,2017 12:33:00 ");
        receiveSubItems.setRejectedQuantity("322");
        receiveSubItems.setReasonforRejection("each item has low quality material");

        receiveItemList.add(receiveSubItems);
        receiveItemList.add(receiveSubItems);
        receiveItemList.add(receiveSubItems);
        receiveItemList.add(receiveSubItems);
        receiveItemList.add(receiveSubItems);
        receiveItemList.add(receiveSubItems);


    }

    private void dialogAddItems() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_add_items, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        final AlertDialog  alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

         AutoCompleteTextView dialogAddItemsItemVariation;
         AutoCompleteTextView dialogAddItemsReceivingQuantity;
         AutoCompleteTextView dialogAddItemsReceivingUnit;
         AutoCompleteTextView dialogAddItemsComment;
         AutoCompleteTextView dialogAddItemsExpiryDate;
         AutoCompleteTextView dialogAddItemsReasonForRejection;
         AutoCompleteTextView dialogAddItemsRejectedQuantity;
         TextView dialogAddItemsAdd;

            dialogAddItemsItemVariation = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_item_variation );
            dialogAddItemsReceivingQuantity = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_receiving_quantity );
            dialogAddItemsReceivingUnit = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_receiving_unit );
            dialogAddItemsComment = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_comment );
            dialogAddItemsExpiryDate = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_expiry_date );
            dialogAddItemsReasonForRejection = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_reason_for_rejection );
            dialogAddItemsRejectedQuantity = (AutoCompleteTextView)dialogView.findViewById( R.id.dialog_add_items_rejected_quantity );
            dialogAddItemsAdd = (TextView)dialogView.findViewById( R.id.dialog_add_items_add );

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_receive_directly, container, false);
        findViews(rootView);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.receive_directly);
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity()
                .setTitle(getString(R.string.receive_directly));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.receive_directly));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.receive_directly));
        }
    }


    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onExecute() {

    }
}
