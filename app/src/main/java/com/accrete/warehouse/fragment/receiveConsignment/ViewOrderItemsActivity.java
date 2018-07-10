package com.accrete.warehouse.fragment.receiveConsignment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.adapter.OrderConsignmentDetailsAdapter;
import com.accrete.warehouse.adapter.OrderReceivedDetailsAdapter;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ConsignmentDetail;
import com.accrete.warehouse.model.OrderDetail;
import com.accrete.warehouse.model.ReceivedDetail;
import com.accrete.warehouse.model.VendorData;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

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
    private TextView textviewConsignmentDetailsEmpty,textViewPODate;
    private String purOrId;
    private OrderConsignmentDetailsAdapter orderConsignmentDetailsAdapter;
    private OrderReceivedDetailsAdapter orderReceivedDetailsAdapter;
    private List<ConsignmentDetail> consignmentDetailList = new ArrayList<ConsignmentDetail>();
    private List<ReceivedDetail> receivedDetailList = new ArrayList<ReceivedDetail>();
    private View viewTitles;
    private LinearLayout titlesLayout,linearLayoutPoDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order_items);
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.purOrId))) {
            purOrId = getIntent().getStringExtra(getString(R.string.purOrId));
        }

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
        linearLayoutPoDate = (LinearLayout) findViewById(R.id.po_date_layout);
        textViewPODate = (TextView) findViewById(R.id.po_date_textView);
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
        titlesLayout = (LinearLayout) findViewById(R.id.titles_layout);
        viewTitles = (View) findViewById(R.id.view_titles);

        //Order Consignment Details adapter
        orderConsignmentDetailsAdapter = new OrderConsignmentDetailsAdapter(this, consignmentDetailList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewConsignmentDetails.setLayoutManager(mLayoutManager);
        recyclerViewConsignmentDetails.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewConsignmentDetails.setAdapter(orderConsignmentDetailsAdapter);

        //Order Received Details adapter
        orderReceivedDetailsAdapter = new OrderReceivedDetailsAdapter(this, receivedDetailList);
        RecyclerView.LayoutManager mLayoutManagerReceived = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewReceivedDetails.setLayoutManager(mLayoutManagerReceived);
        recyclerViewReceivedDetails.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerViewReceivedDetails.setAdapter(orderReceivedDetailsAdapter);

        //Smooth Scroll
        recyclerViewConsignmentDetails.setNestedScrollingEnabled(false);
        recyclerViewReceivedDetails.setNestedScrollingEnabled(false);

        if (!NetworkUtil.getConnectivityStatusString(ViewOrderItemsActivity.this).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getOrderDetails(purOrId);
                }
            }, 200);
        } else {
            Toast.makeText(ViewOrderItemsActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    private void getOrderDetails(String purOrId) {
        task = getString(R.string.fetch_purchase_order_info);
        userId = AppPreferences.getUserId(ViewOrderItemsActivity.this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(ViewOrderItemsActivity.this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(ViewOrderItemsActivity.this, AppUtils.DOMAIN);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.viewOrderDetails(version, key, task, userId, accessToken,
                AppPreferences.getWarehouseDefaultCheckId(ViewOrderItemsActivity.this, AppUtils.WAREHOUSE_CHK_ID), purOrId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));


        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {

                    setDataIntoView(apiResponse.getData().getVendorData(),
                            apiResponse.getData().getOrderDetail());

                    for (final ConsignmentDetail consignmentDetail : apiResponse.getData().getConsignmentDetails()) {
                        if (apiResponse.getData().getConsignmentDetails() != null) {
                            consignmentDetailList.add(consignmentDetail);
                        }
                    }

                    if (!consignmentDetailList.isEmpty() && !consignmentDetailList.equals("null") && consignmentDetailList.size() > 0) {
                        recyclerViewConsignmentDetails.setVisibility(View.VISIBLE);
                        textviewConsignmentDetailsEmpty.setVisibility(View.GONE);
                        orderConsignmentDetailsAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewConsignmentDetails.setVisibility(View.GONE);
                        textviewConsignmentDetailsEmpty.setVisibility(View.VISIBLE);
                        textviewConsignmentDetailsEmpty.setText("No data available");
                    }

                    for (final ReceivedDetail receivedDetail : apiResponse.getData().getReceivedDetails()) {
                        if (apiResponse.getData().getReceivedDetails() != null) {
                            receivedDetailList.add(receivedDetail);
                        }
                    }
                    if (!receivedDetailList.isEmpty() && !receivedDetailList.equals("null") && receivedDetailList.size() > 0) {
                        recyclerViewReceivedDetails.setVisibility(View.VISIBLE);
                        titlesLayout.setVisibility(View.VISIBLE);
                        textviewReceivedDetailsEmpty.setVisibility(View.GONE);
                        orderReceivedDetailsAdapter.notifyDataSetChanged();
                    } else {
                        recyclerViewReceivedDetails.setVisibility(View.GONE);
                        titlesLayout.setVisibility(View.GONE);
                        textviewReceivedDetailsEmpty.setVisibility(View.VISIBLE);
                        textviewReceivedDetailsEmpty.setText("No date available");
                    }


                } else if (apiResponse.getSuccessCode().equals("10005")) {
                    Toast.makeText(ViewOrderItemsActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else if (apiResponse.getSuccessCode().equals("10006")) {
                    Toast.makeText(ViewOrderItemsActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
                   /* new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            coordinatorLayoutMain.setVisibility(View.VISIBLE);
                        }
                    }, 100);*/
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(ViewOrderItemsActivity.this, getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }

        });
    }

    public void setDataIntoView(VendorData vendorData, OrderDetail orderDetail) {
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        //Vendor Details
        if (vendorData.getName() != null && !vendorData.getName().isEmpty()) {
            nameLayout.setVisibility(View.VISIBLE);
            nameTextView.setText(vendorData.getName());
        } else {
            nameLayout.setVisibility(View.GONE);
        }

        if (vendorData.getMobile() != null && !vendorData.getMobile().isEmpty()) {
            contactNoLayout.setVisibility(View.VISIBLE);
            contactNoTextView.setText(vendorData.getMobile());
        } else {
            contactNoLayout.setVisibility(View.GONE);
        }

        if (vendorData.getEmail() != null && !vendorData.getEmail().isEmpty()) {
            emailLayout.setVisibility(View.VISIBLE);
            emailTextView.setText(vendorData.getEmail());
        } else {
            emailLayout.setVisibility(View.GONE);
        }

        if (vendorData.getAddress() != null && !vendorData.getAddress().isEmpty()) {
            addressLayout.setVisibility(View.VISIBLE);
            addressTextView.setText(vendorData.getAddress());
        } else {
            addressLayout.setVisibility(View.GONE);
        }


        //Order Details
        if (orderDetail.getOrderId() != null && !orderDetail.getOrderId().isEmpty()) {
            orderIdLayout.setVisibility(View.VISIBLE);
            orderIdTextView.setText(orderDetail.getOrderId());
        } else {
            orderIdLayout.setVisibility(View.GONE);
        }

        if (orderDetail.getPoDate() != null && !orderDetail.getPoDate().isEmpty()) {
            linearLayoutPoDate.setVisibility(View.VISIBLE);
            textViewPODate.setText(orderDetail.getPoDate());
        } else {
            linearLayoutPoDate.setVisibility(View.GONE);
        }

        if (orderDetail.getStatus() != null && !orderDetail.getStatus().isEmpty()) {
            statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) statusTextView.getBackground();
            statusLayout.setVisibility(View.VISIBLE);
            statusTextView.setText(orderDetail.getStatus());

            if (orderDetail.getStatus().equals("Created")) {
                drawable.setColor(getResources().getColor(R.color.green_purchase_order));
            } else if (orderDetail.getStatus().equals("Partial Received")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
            } else if (orderDetail.getStatus().equals("Received")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
            } else if (orderDetail.getStatus().equals("Cancelled")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            } else if (orderDetail.getStatus().equals("Closed")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            } else if (orderDetail.getStatus().equals("Pending")) {
                drawable.setColor(getResources().getColor(R.color.orange_purchase_order));
            } else if (orderDetail.getStatus().equals("Expected Delivery")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
            } else if (orderDetail.getStatus().equals("Pending Transportation")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
            }

        } else {
            statusLayout.setVisibility(View.GONE);
        }

        if (orderDetail.getSubTotal() != null && !orderDetail.getSubTotal().isEmpty()) {
            subTotalLayout.setVisibility(View.VISIBLE);
            subTotalTextView.setText(getString(R.string.Rs) + " " + formatter.format(ParseDouble(orderDetail.getSubTotal())));
        } else {
            subTotalLayout.setVisibility(View.GONE);
        }

        if (orderDetail.getTotal() != null && !orderDetail.getTotal().isEmpty()) {
            totalLayout.setVisibility(View.VISIBLE);
            totalTextView.setText(getString(R.string.Rs) + " " + formatter.format(ParseDouble(orderDetail.getTotal())));
        } else {
            totalLayout.setVisibility(View.GONE);
        }

       /* if (orderDetail.getRoundOff() != null && !orderDetail.getRoundOff().isEmpty()) {
            roundOffLayout.setVisibility(View.VISIBLE);
            roundOffTextView.setText(getString(R.string.Rs) + " " + formatter.format(ParseDouble(orderDetail.getRoundOff())));
        } else {
            roundOffLayout.setVisibility(View.GONE);
        }*/

        if (orderDetail.getPayable() != null && !orderDetail.getPayable().isEmpty()) {
            payableLayout.setVisibility(View.VISIBLE);
            payableTextView.setText(getString(R.string.Rs) + " " + formatter.format(ParseDouble(orderDetail.getPayable())));
        } else {
            payableLayout.setVisibility(View.GONE);
        }
    }

    //To deal with empty string of amount
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }
}
