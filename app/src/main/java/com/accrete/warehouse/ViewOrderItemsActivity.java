package com.accrete.warehouse;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by agt on 19/1/18.
 */

public class ViewOrderItemsActivity extends AppCompatActivity {
    private TextView vendorDetailsTitleTextView;
    private TextView vendorDetailsSubTitleTextView;
    private View vendorDetailsView;
    private LinearLayout nameLayout;
    private TextView nameTextView;
    private LinearLayout contactNoLayout;
    private TextView contactNoTextView;
    private LinearLayout addressLayout;
    private TextView addressTextView;
    private LinearLayout emailLayout;
    private TextView emailTextView;
    private TextView orderDetailsTitleTextView;
    private TextView orderDetailsSubTitleTextView;
    private View orderDetailsView;
    private LinearLayout orderIdLayout;
    private TextView orderIdTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private LinearLayout subTotalLayout;
    private TextView subTotalTextView;
    private LinearLayout totalLayout;
    private TextView totalTextView;
    private LinearLayout roundOffLayout;
    private TextView roundOffTextView;
    private LinearLayout payableLayout;
    private TextView payableTextView;
    private TextView receivedDetailsTitleTextView;
    private TextView receivedDetailsSubTitleTextView;
    private View receivedDetailsView;
    private RelativeLayout layoutReceivedDetails;
    private RecyclerView recyclerViewReceivedDetails;
    private TextView textviewReceivedDetailsEmpty;
    private TextView consignmentDetailsTitleTextView;
    private TextView consignmentDetailsSubTitleTextView;
    private View consignmentDetailsView;
    private RelativeLayout layoutConsignmentDetails;
    private RecyclerView recyclerViewConsignmentDetails;
    private TextView textviewConsignmentDetailsEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_items);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViews();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void findViews() {
        vendorDetailsTitleTextView = (TextView) findViewById(R.id.vendorDetailsTitle_textView);
        vendorDetailsSubTitleTextView = (TextView) findViewById(R.id.vendorDetailsSubTitle_textView);
        vendorDetailsView = (View) findViewById(R.id.vendorDetails_view);
        nameLayout = (LinearLayout) findViewById(R.id.name_layout);
        nameTextView = (TextView) findViewById(R.id.name_textView);
        contactNoLayout = (LinearLayout) findViewById(R.id.contactNo_layout);
        contactNoTextView = (TextView) findViewById(R.id.contactNo_textView);
        addressLayout = (LinearLayout) findViewById(R.id.address_layout);
        addressTextView = (TextView) findViewById(R.id.address_textView);
        emailLayout = (LinearLayout) findViewById(R.id.email_layout);
        emailTextView = (TextView) findViewById(R.id.email_textView);
        orderDetailsTitleTextView = (TextView) findViewById(R.id.orderDetailsTitle_textView);
        orderDetailsSubTitleTextView = (TextView) findViewById(R.id.orderDetailsSubTitle_textView);
        orderDetailsView = (View) findViewById(R.id.orderDetails_view);
        orderIdLayout = (LinearLayout) findViewById(R.id.orderId_layout);
        orderIdTextView = (TextView) findViewById(R.id.orderId_textView);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        statusTextView = (TextView) findViewById(R.id.status_textView);
        subTotalLayout = (LinearLayout) findViewById(R.id.subTotal_layout);
        subTotalTextView = (TextView) findViewById(R.id.subTotal_textView);
        totalLayout = (LinearLayout) findViewById(R.id.total_layout);
        totalTextView = (TextView) findViewById(R.id.total_textView);
        roundOffLayout = (LinearLayout) findViewById(R.id.roundOff_layout);
        roundOffTextView = (TextView) findViewById(R.id.roundOff_textView);
        payableLayout = (LinearLayout) findViewById(R.id.payable_layout);
        payableTextView = (TextView) findViewById(R.id.payable_textView);
        receivedDetailsTitleTextView = (TextView) findViewById(R.id.receivedDetailsTitle_textView);
        receivedDetailsSubTitleTextView = (TextView) findViewById(R.id.receivedDetailsSubTitle_textView);
        receivedDetailsView = (View) findViewById(R.id.receivedDetails_view);
        layoutReceivedDetails = (RelativeLayout) findViewById(R.id.layout_receivedDetails);
        recyclerViewReceivedDetails = (RecyclerView) findViewById(R.id.recycler_view_receivedDetails);
        textviewReceivedDetailsEmpty = (TextView) findViewById(R.id.textview_receivedDetails_empty);
        consignmentDetailsTitleTextView = (TextView) findViewById(R.id.consignmentDetailsTitle_textView);
        consignmentDetailsSubTitleTextView = (TextView) findViewById(R.id.consignmentDetailsSubTitle_textView);
        consignmentDetailsView = (View) findViewById(R.id.consignmentDetails_view);
        layoutConsignmentDetails = (RelativeLayout) findViewById(R.id.layout_consignmentDetails);
        recyclerViewConsignmentDetails = (RecyclerView) findViewById(R.id.recycler_view_consignmentDetails);
        textviewConsignmentDetailsEmpty = (TextView) findViewById(R.id.textview_consignmentDetails_empty);
    }

}
