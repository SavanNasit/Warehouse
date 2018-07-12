package com.accrete.warehouse.fragment.manageConsignment;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.ConsignmentData;
import com.accrete.warehouse.model.TransportationData;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/5/17.
 */

public class DetailsFragment extends Fragment {
    private LinearLayout containerIdLayout;
    private TextView containerIdTextView;
    private LinearLayout authorizedByLayout;
    private TextView authorizedByTextView;
    private LinearLayout purchaseOrderLayout;
    private TextView purchaseOrderTextView;
    private LinearLayout purchasedOnLayout;
    private TextView purchasedOnTextView;
    private LinearLayout invoiceNumberLayout;
    private TextView invoiceNumberTextView;
    private LinearLayout invoiceDateLayout;
    private TextView invoiceDateTextView;
    private LinearLayout vendorLayout;
    private TextView vendorTextView;
    private LinearLayout createdByLayout;
    private TextView createdByTextView;
    private LinearLayout createdTimeLayout;
    private TextView createdTimeTextView;
    private LinearLayout wareHouseLayout;
    private TextView wareHouseTextView;
    private LinearLayout receivedOnLayout;
    private TextView receivedOnTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView transportationTitleTextView;
    private TextView transportationSubTitleTextView;
    private View transportationTitleView;
    private LinearLayout vendorNameLayout;
    private TextView vendorNameTextView;
    private LinearLayout lrNumberLayout;
    private TextView lrNumberTextView;
    private LinearLayout vehicleNumberLayout;
    private TextView vehicleNumberTextView;
    private LinearLayout expectedDateLayout;
    private TextView expectedDateTextView;
    private LinearLayout weightLayout;
    private TextView weightTextView;
    private String chkId, iscId;
    private LinearLayout stockRequestLayout;
    private TextView stockRequestTextView;
    private LinearLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details_consignment, container, false);
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        iscId = bundle.getString(getString(R.string.iscid));
    }

    private void findViews(View v) {
        mainLayout = (LinearLayout) v.findViewById(R.id.mainLayout);
        containerIdLayout = (LinearLayout) v.findViewById(R.id.containerId_layout);
        containerIdTextView = (TextView) v.findViewById(R.id.containerId_textView);
        authorizedByLayout = (LinearLayout) v.findViewById(R.id.authorizedBy_layout);
        authorizedByTextView = (TextView) v.findViewById(R.id.authorizedBy_textView);
        purchaseOrderLayout = (LinearLayout) v.findViewById(R.id.purchaseOrder_layout);
        purchaseOrderTextView = (TextView) v.findViewById(R.id.purchaseOrder_textView);
        purchasedOnLayout = (LinearLayout) v.findViewById(R.id.purchasedOn_layout);
        purchasedOnTextView = (TextView) v.findViewById(R.id.purchasedOn_textView);
        invoiceNumberLayout = (LinearLayout) v.findViewById(R.id.invoiceNumber_layout);
        invoiceNumberTextView = (TextView) v.findViewById(R.id.invoiceNumber_textView);
        invoiceDateLayout = (LinearLayout) v.findViewById(R.id.invoiceDate_layout);
        invoiceDateTextView = (TextView) v.findViewById(R.id.invoiceDate_textView);
        vendorLayout = (LinearLayout) v.findViewById(R.id.vendor_layout);
        vendorTextView = (TextView) v.findViewById(R.id.vendor_textView);
        createdByLayout = (LinearLayout) v.findViewById(R.id.createdBy_layout);
        createdByTextView = (TextView) v.findViewById(R.id.createdBy_textView);
        createdTimeLayout = (LinearLayout) v.findViewById(R.id.createdTime_layout);
        createdTimeTextView = (TextView) v.findViewById(R.id.createdTime_textView);
        wareHouseLayout = (LinearLayout) v.findViewById(R.id.wareHouse_layout);
        wareHouseTextView = (TextView) v.findViewById(R.id.wareHouse_textView);
        receivedOnLayout = (LinearLayout) v.findViewById(R.id.receivedOn_layout);
        receivedOnTextView = (TextView) v.findViewById(R.id.receivedOn_textView);
        stockRequestLayout = (LinearLayout) v.findViewById(R.id.stockRequest_layout);
        stockRequestTextView = (TextView) v.findViewById(R.id.stockRequest_textView);
        statusLayout = (LinearLayout) v.findViewById(R.id.status_layout);
        statusTextView = (TextView) v.findViewById(R.id.status_textView);
        transportationTitleTextView = (TextView) v.findViewById(R.id.transportationTitle_textView);
        transportationSubTitleTextView = (TextView) v.findViewById(R.id.transportationSubTitle_textView);
        transportationTitleView = (View) v.findViewById(R.id.transportationTitle_view);
        vendorNameLayout = (LinearLayout) v.findViewById(R.id.vendorName_layout);
        vendorNameTextView = (TextView) v.findViewById(R.id.vendorName_textView);
        lrNumberLayout = (LinearLayout) v.findViewById(R.id.lrNumber_layout);
        lrNumberTextView = (TextView) v.findViewById(R.id.lrNumber_textView);
        vehicleNumberLayout = (LinearLayout) v.findViewById(R.id.vehicleNumber_layout);
        vehicleNumberTextView = (TextView) v.findViewById(R.id.vehicleNumber_textView);
        expectedDateLayout = (LinearLayout) v.findViewById(R.id.expectedDate_layout);
        expectedDateTextView = (TextView) v.findViewById(R.id.expectedDate_textView);
        weightLayout = (LinearLayout) v.findViewById(R.id.weight_layout);
        weightTextView = (TextView) v.findViewById(R.id.weight_textView);
        mainLayout.setVisibility(View.GONE);

        chkId = AppPreferences.getWarehouseDefaultCheckId(getActivity(), AppUtils.WAREHOUSE_CHK_ID);

        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getConsignmentDetails(iscId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }


    private void getConsignmentDetails(String iscid) {
        if (getActivity() != null && isAdded()) {
            task = getString(R.string.consignment_info);
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

            final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call call = apiService.getConsignmentDetails(version, key, task, userId, accessToken, chkId, iscid);
            Log.v("Request", String.valueOf(call));
            Log.v("url", String.valueOf(call.request().url()));


            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    ApiResponse apiResponse = (ApiResponse) response.body();
                    if (apiResponse.getSuccess()) {

                        if (apiResponse.getData().getConsignmentData() != null) {

                        }

                        if (isAdded() && getActivity() != null) {
                            setDataToConsignmentDetails(apiResponse.getData().getConsignmentData(),
                                    apiResponse.getData().getTransportationData());
                        }

                    } else if (apiResponse.getSuccessCode().equals("10005")) {
                        if (isAdded() && getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10006")) {
                        if (isAdded() && getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
                    if (isAdded() && getActivity() != null) {
                    }
                    Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }

            });
        }
    }

    public void setDataToConsignmentDetails(ConsignmentData consignmentData,
                                            TransportationData transportationData) {
        //Container Id
        if (consignmentData.getContainerID() != null && !consignmentData.getContainerID().isEmpty()) {
            containerIdTextView.setText(consignmentData.getContainerID());
            containerIdLayout.setVisibility(View.VISIBLE);
        } else {
            containerIdLayout.setVisibility(View.GONE);
        }

        //Authorized By
        if (consignmentData.getAuthorizedName() != null && !consignmentData.getAuthorizedName().isEmpty()) {
            authorizedByTextView.setText(consignmentData.getAuthorizedName());
            authorizedByLayout.setVisibility(View.VISIBLE);
        } else {
            authorizedByLayout.setVisibility(View.GONE);
        }

        //Purchase Order
        if (consignmentData.getPurchaseOrder() != null && !consignmentData.getPurchaseOrder().isEmpty()) {
            purchaseOrderTextView.setText(consignmentData.getPurchaseOrder());
            purchaseOrderLayout.setVisibility(View.VISIBLE);
        } else {
            purchaseOrderLayout.setVisibility(View.GONE);
        }

        //Purchased On
        if (consignmentData.getPurchasedOn() != null && !consignmentData.getPurchasedOn().isEmpty()) {
            purchasedOnTextView.setText(consignmentData.getPurchasedOn());
            purchasedOnLayout.setVisibility(View.VISIBLE);
        } else {
            purchasedOnLayout.setVisibility(View.GONE);
        }

        //Invoice Number
        if (consignmentData.getInvoiceNo() != null && !consignmentData.getInvoiceNo().isEmpty()) {
            invoiceNumberTextView.setText(consignmentData.getInvoiceNo());
            invoiceNumberLayout.setVisibility(View.VISIBLE);
        } else {
            invoiceNumberLayout.setVisibility(View.GONE);
        }

        //Invoice Date
        if (consignmentData.getInvoiceDate() != null && !consignmentData.getInvoiceDate().isEmpty()) {
            invoiceDateTextView.setText(consignmentData.getInvoiceDate());
            invoiceDateLayout.setVisibility(View.VISIBLE);
        } else {
            invoiceDateLayout.setVisibility(View.GONE);
        }

        //Vendor
        if (consignmentData.getVendorName() != null && !consignmentData.getVendorName().isEmpty()) {
            vendorTextView.setText(consignmentData.getVendorName());
            vendorLayout.setVisibility(View.VISIBLE);
        } else {
            vendorLayout.setVisibility(View.GONE);
        }

        //Created By
        if (consignmentData.getCreatedBy() != null && !consignmentData.getCreatedBy().isEmpty()) {
            createdByTextView.setText(consignmentData.getCreatedBy());
            createdByLayout.setVisibility(View.VISIBLE);
        } else {
            createdByLayout.setVisibility(View.GONE);
        }

        //Created Time
        if (consignmentData.getCreatedTime() != null && !consignmentData.getCreatedTime().isEmpty()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
            try {
                DateFormat outputFormat = new SimpleDateFormat("hh:mm aa - dd MMM, yyyy");
                Date date = simpleDateFormat.parse(consignmentData.getCreatedTime());
                createdTimeTextView.setText(outputFormat.format(date).toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            createdTimeLayout.setVisibility(View.VISIBLE);
        } else {
            createdTimeLayout.setVisibility(View.GONE);
        }

        //Warehouse
        if (consignmentData.getWarehouseName() != null && !consignmentData.getWarehouseName().isEmpty()) {
            wareHouseTextView.setText(consignmentData.getWarehouseName());
            wareHouseLayout.setVisibility(View.VISIBLE);
        } else {
            wareHouseLayout.setVisibility(View.GONE);
        }

        //Received On
        if (consignmentData.getReceivedOn() != null && !consignmentData.getReceivedOn().isEmpty()) {
            receivedOnTextView.setText(consignmentData.getReceivedOn());
            receivedOnLayout.setVisibility(View.VISIBLE);
        } else {
            receivedOnLayout.setVisibility(View.GONE);
        }

        //Stock Request
        if (consignmentData.getStockRequest() != null && !consignmentData.getStockRequest().isEmpty() && !consignmentData.getStockRequest().equals("NA")) {
            stockRequestTextView.setText(consignmentData.getStockRequest());
            stockRequestLayout.setVisibility(View.VISIBLE);
        } else {
            stockRequestLayout.setVisibility(View.GONE);
        }

        //Status
        if (consignmentData.getStatus() != null && !consignmentData.getStatus().isEmpty()) {
            statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) statusTextView.getBackground();
            if (consignmentData.getStatus().equals("Freezed")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
            } else if (consignmentData.getStatus().equals("Active")) {
                drawable.setColor(getResources().getColor(R.color.green_purchase_order));
            } else if (consignmentData.getStatus().equals("Inactive")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
            } else if (consignmentData.getStatus().equals("Delete")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
            } else if (consignmentData.getStatus().equals("Payment Approved")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
            }
            statusTextView.setText(consignmentData.getStatus());
            statusLayout.setVisibility(View.VISIBLE);
        } else {
            statusLayout.setVisibility(View.GONE);
        }


        //Vendor Name
        if (transportationData.getVendorName() != null && !transportationData.getVendorName().isEmpty()) {
            vendorNameTextView.setText(transportationData.getVendorName());
            vendorNameLayout.setVisibility(View.VISIBLE);
        } else {
            vendorNameLayout.setVisibility(View.GONE);
        }

        //LR Number
        if (transportationData.getLrNumber() != null && !transportationData.getLrNumber().isEmpty()) {
            lrNumberTextView.setText(transportationData.getLrNumber());
            lrNumberLayout.setVisibility(View.VISIBLE);
        } else {
            lrNumberLayout.setVisibility(View.GONE);
        }

        //Vehicle Number
        if (transportationData.getVehicleNumber() != null && !transportationData.getVehicleNumber().isEmpty()) {
            vehicleNumberTextView.setText(transportationData.getVehicleNumber());
            vehicleNumberLayout.setVisibility(View.VISIBLE);
        } else {
            vehicleNumberLayout.setVisibility(View.GONE);
        }

        //Expected Date
        if (transportationData.getExpectedDate() != null && !transportationData.getExpectedDate().isEmpty()) {
            expectedDateTextView.setText(transportationData.getExpectedDate());
            expectedDateLayout.setVisibility(View.VISIBLE);
        } else {
            expectedDateLayout.setVisibility(View.GONE);
        }

        //Weight
        if (transportationData.getWeight() != null && !transportationData.getWeight().isEmpty()) {
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
            weightTextView.setText(formatter.format(ParseDouble(transportationData.getWeight())));
            weightLayout.setVisibility(View.VISIBLE);
        } else {
            weightLayout.setVisibility(View.GONE);
        }

        if (transportationData != null && ((transportationData.getWeight() != null &&
                !transportationData.getWeight().isEmpty()) || (transportationData.getExpectedDate() != null &&
                !transportationData.getExpectedDate().isEmpty()) || (transportationData.getVehicleNumber() != null &&
                !transportationData.getVehicleNumber().isEmpty()) || (transportationData.getLrNumber() != null &&
                !transportationData.getLrNumber().isEmpty()) || (transportationData.getVendorName() != null &&
                !transportationData.getVendorName().isEmpty()))) {
            transportationSubTitleTextView.setVisibility(View.VISIBLE);
            transportationTitleTextView.setVisibility(View.VISIBLE);
            transportationTitleView.setVisibility(View.VISIBLE);
        } else {
            transportationSubTitleTextView.setVisibility(View.GONE);
            transportationTitleTextView.setVisibility(View.GONE);
            transportationTitleView.setVisibility(View.GONE);
        }


        mainLayout.setVisibility(View.VISIBLE);

    }//To deal with empty string of amount

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
