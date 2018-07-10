package com.accrete.sixorbit.fragment.Drawer.vendor;

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

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ConsignmentInfo;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 8/12/17.
 */

public class ConsignmentDetailsTabFragment extends Fragment {
    private String venid, consignmentId, containerId, authorizedBy, purchaseOrder, purchasedOn, invoiceNumber, invoiceDate, vendor,
            warehouse, receivedOn, consignmentStatus;
    private LinearLayout layoutConsignmentInfo;
    private TextView textViewConsignmentDetails;
    private LinearLayout containerIdLayout;
    private TextView containerIdTextView;
    private TextView containerIdValueTextView;
    private LinearLayout authorizedByLayout;
    private TextView authorizedByTextView;
    private TextView authorizedByValueTextView;
    private LinearLayout purchaseOrderLayout;
    private TextView purchaseOrderTextView;
    private TextView purchaseOrderValueTextView;
    private LinearLayout purchasedOnLayout;
    private TextView purchasedOnTextView;
    private TextView purchasedOnValueTextView;
    private LinearLayout invoiceNumberLayout;
    private TextView invoiceNumberTextView;
    private TextView invoiceNumberValueTextView;
    private LinearLayout invoiceDateLayout;
    private TextView invoiceDateTextView;
    private TextView invoiceDateValueTextView;
    private LinearLayout vendorLayout;
    private TextView vendorTextView;
    private TextView vendorValueTextView;
    private LinearLayout warehouseLayout;
    private TextView warehouseTextView;
    private TextView warehouseValueTextView;
    private LinearLayout receivedOnLayout;
    private TextView receivedOnTextView;
    private TextView receivedOnValueTextView;
    private LinearLayout consignmentStatusLayout;
    private TextView consignmentStatusTextView;
    private TextView consignmentStatusValueTextView;
    private TextView emptyTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        venid = bundle.getString(getString(R.string.venid));
        consignmentId = bundle.getString(getString(R.string.consignment_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_consignment_detail_tab, container, false);
        //Find all widgets
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        layoutConsignmentInfo = (LinearLayout) view.findViewById(R.id.layout_consignment_info);
        textViewConsignmentDetails = (TextView) view.findViewById(R.id.textView_consignment_details);
        containerIdLayout = (LinearLayout) view.findViewById(R.id.container_id_layout);
        containerIdTextView = (TextView) view.findViewById(R.id.container_id_textView);
        containerIdValueTextView = (TextView) view.findViewById(R.id.container_id_value_textView);
        authorizedByLayout = (LinearLayout) view.findViewById(R.id.authorized_by_layout);
        authorizedByTextView = (TextView) view.findViewById(R.id.authorized_by_textView);
        authorizedByValueTextView = (TextView) view.findViewById(R.id.authorized_by_value_textView);
        purchaseOrderLayout = (LinearLayout) view.findViewById(R.id.purchase_order_layout);
        purchaseOrderTextView = (TextView) view.findViewById(R.id.purchase_order_textView);
        purchaseOrderValueTextView = (TextView) view.findViewById(R.id.purchase_order_value_textView);
        purchasedOnLayout = (LinearLayout) view.findViewById(R.id.purchased_on_layout);
        purchasedOnTextView = (TextView) view.findViewById(R.id.purchased_on_textView);
        purchasedOnValueTextView = (TextView) view.findViewById(R.id.purchased_on_value_textView);
        invoiceNumberLayout = (LinearLayout) view.findViewById(R.id.invoice_number_layout);
        invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
        invoiceNumberValueTextView = (TextView) view.findViewById(R.id.invoice_number_value_textView);
        invoiceDateLayout = (LinearLayout) view.findViewById(R.id.invoice_date_layout);
        invoiceDateTextView = (TextView) view.findViewById(R.id.invoice_date_textView);
        invoiceDateValueTextView = (TextView) view.findViewById(R.id.invoice_date_value_textView);
        vendorLayout = (LinearLayout) view.findViewById(R.id.vendor_layout);
        vendorTextView = (TextView) view.findViewById(R.id.vendor_textView);
        vendorValueTextView = (TextView) view.findViewById(R.id.vendor_value_textView);
        warehouseLayout = (LinearLayout) view.findViewById(R.id.warehouse_layout);
        warehouseTextView = (TextView) view.findViewById(R.id.warehouse_textView);
        warehouseValueTextView = (TextView) view.findViewById(R.id.warehouse_value_textView);
        receivedOnLayout = (LinearLayout) view.findViewById(R.id.received_on_layout);
        receivedOnTextView = (TextView) view.findViewById(R.id.received_on_textView);
        receivedOnValueTextView = (TextView) view.findViewById(R.id.received_on_value_textView);
        consignmentStatusLayout = (LinearLayout) view.findViewById(R.id.consignment_status_layout);
        consignmentStatusTextView = (TextView) view.findViewById(R.id.consignment_status_textView);
        consignmentStatusValueTextView = (TextView) view.findViewById(R.id.consignment_status_value_textView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);

        emptyTextView.setText("No data.");
        emptyTextView.setVisibility(View.GONE);

        //Call API
        doRefresh();

        //Load data after getting connection
        emptyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyTextView.getText().toString().trim().equals(getString(R.string.not_connected_to_internet))) {
                    doRefresh();
                }
            }
        });

    }

    public void doRefresh() {
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getConsignmentInfo(venid, consignmentId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.not_connected_to_internet));
            layoutConsignmentInfo.setVisibility(View.GONE);
        }

    }

    public void setData() {

        //Container Id
        if (containerId != null && !containerId.isEmpty() && !containerId.equals("null")) {
            containerIdValueTextView.setText(containerId.toString().trim());
            containerIdLayout.setVisibility(View.VISIBLE);
        } else {
            containerIdLayout.setVisibility(View.GONE);
        }

        //Authorized By
        if (authorizedBy != null && !authorizedBy.isEmpty() && !authorizedBy.equals("null")) {
            authorizedByValueTextView.setText(authorizedBy.toString().trim());
            authorizedByLayout.setVisibility(View.VISIBLE);
        } else {
            authorizedByLayout.setVisibility(View.GONE);
        }

        //Purchase Order
        if (purchaseOrder != null && !purchaseOrder.isEmpty() && !purchaseOrder.equals("null")) {
            purchaseOrderValueTextView.setText(purchaseOrder.toString().trim());
            purchaseOrderLayout.setVisibility(View.VISIBLE);
        } else {
            purchaseOrderLayout.setVisibility(View.GONE);
        }

        //Purchased Date
        if (purchasedOn != null && !purchasedOn.isEmpty() && !purchasedOn.equals("null")) {
            purchasedOnValueTextView.setText(purchasedOn.toString().trim());
            purchasedOnLayout.setVisibility(View.VISIBLE);
        } else {
            purchasedOnLayout.setVisibility(View.GONE);
        }

        //Invoice Number
        if (invoiceNumber != null && !invoiceNumber.isEmpty() && !invoiceNumber.equals("null")) {
            invoiceNumberValueTextView.setText(invoiceNumber.toString().trim());
            invoiceNumberLayout.setVisibility(View.VISIBLE);
        } else {
            invoiceNumberLayout.setVisibility(View.GONE);
        }

        //Invoice Date
        if (invoiceDate != null && !invoiceDate.isEmpty() && !invoiceDate.equals("null")) {
            invoiceDateValueTextView.setText(invoiceDate.toString().trim());
            invoiceDateLayout.setVisibility(View.VISIBLE);
        } else {
            invoiceDateLayout.setVisibility(View.GONE);
        }

        //Vendor
        if (vendor != null && !vendor.isEmpty() && !vendor.equals("null")) {
            vendorValueTextView.setText(vendor.toString().trim());
            vendorLayout.setVisibility(View.VISIBLE);
        } else {
            vendorLayout.setVisibility(View.GONE);
        }

        //WareHouse
        if (warehouse != null && !warehouse.isEmpty() && !warehouse.equals("null")) {
            warehouseValueTextView.setText(warehouse.toString().trim());
            warehouseLayout.setVisibility(View.VISIBLE);
        } else {
            warehouseLayout.setVisibility(View.GONE);
        }

        //Received On
        if (receivedOn != null && !receivedOn.isEmpty() && !receivedOn.equals("null")) {
            receivedOnValueTextView.setText(receivedOn.toString().trim());
            receivedOnLayout.setVisibility(View.VISIBLE);
        } else {
            receivedOnLayout.setVisibility(View.GONE);
        }

        //Status
        if (consignmentStatus != null && !consignmentStatus.isEmpty() && !consignmentStatus.equals("null")) {
            consignmentStatusLayout.setVisibility(View.VISIBLE);
            consignmentStatusValueTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) consignmentStatusValueTextView.getBackground();
            if (consignmentStatus.equals("1")) {
                drawable.setColor(getResources().getColor(R.color.green_purchase_order));
                consignmentStatusValueTextView.setText("Active");
            } else if (consignmentStatus.equals("2")) {
                drawable.setColor(getResources().getColor(R.color.red_purchase_order));
                consignmentStatusValueTextView.setText("Inactive");
            } else if (consignmentStatus.equals("3")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
                consignmentStatusValueTextView.setText("Delete");
            } else if (consignmentStatus.equals("4")) {
                drawable.setColor(getResources().getColor(R.color.gray_order));
                consignmentStatusValueTextView.setText("Freezed");
            } else if (consignmentStatus.equals("5")) {
                drawable.setColor(getResources().getColor(R.color.blue_purchase_order));
                consignmentStatusValueTextView.setText("Payment Approved");
            }

        } else {
            consignmentStatusLayout.setVisibility(View.GONE);
        }

    }


    private void getConsignmentInfo(String venid, final String consignmentId) {
        task = getString(R.string.vendor_consignment_info);
        userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getVendorConsignmentInfo(version, key, task, userId, accessToken, consignmentId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    ConsignmentInfo consignmentInfo = apiResponse.getData().getConsignmentInfo();
                    if (consignmentInfo != null) {

                        containerId = consignmentInfo.getConsignmentId();
                        purchaseOrder = consignmentInfo.getPurchaseOrder();
                        purchasedOn = consignmentInfo.getPurchasedOn();
                        invoiceNumber = consignmentInfo.getInvoiceNo();
                        invoiceDate = consignmentInfo.getInvoiceDate();
                        vendor = consignmentInfo.getVendor();
                        warehouse = consignmentInfo.getWarehouse();
                        receivedOn = consignmentInfo.getReceivedOn();
                        consignmentStatus = consignmentInfo.getStatus();
                        authorizedBy = consignmentInfo.getAuthorizedBy();

                        //Set Data
                        setData();
                    }
                    emptyTextView.setVisibility(View.GONE);
                    emptyTextView.setText("No data");
                    layoutConsignmentInfo.setVisibility(View.VISIBLE);
                }
                //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                } else {
                    if (isAdded() && getActivity() != null) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText(apiResponse.getMessage());
                        layoutConsignmentInfo.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No data");
                    layoutConsignmentInfo.setVisibility(View.GONE);
                }
            }

        });

    }

}
