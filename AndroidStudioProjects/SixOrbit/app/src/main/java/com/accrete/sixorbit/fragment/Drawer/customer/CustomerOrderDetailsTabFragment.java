package com.accrete.sixorbit.fragment.Drawer.customer;

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
import com.accrete.sixorbit.model.OrderDetails;
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

public class CustomerOrderDetailsTabFragment extends Fragment {
    private String cuId, orderIdStr, orderId, orderDate, poNo, poDate, quotationId, enquiryId, createdDate, createdBy,
            lastUpdatedTime, customerSaleType, orderType, assignedTo, orderStatus, billingAddress, deliveryAddress,
            siteAddress, currentAddress;
    private LinearLayout layoutOrderInfo;
    private TextView textViewOrderDetails;
    private LinearLayout orderIdLayout;
    private TextView orderIdTextView;
    private TextView orderIdValueTextView;
    private LinearLayout orderDateLayout;
    private TextView orderDateTextView;
    private TextView orderDateValueTextView;
    private LinearLayout poNoLayout;
    private TextView poNoTextView;
    private TextView poNoValueTextView;
    private LinearLayout poDateLayout;
    private TextView poDateTextView;
    private TextView poDateValueTextView;
    private LinearLayout quotationIdLayout;
    private TextView quotationIdTextView;
    private TextView quotationIdValueTextView;
    private LinearLayout enquiryIdLayout;
    private TextView enquiryIdTextView;
    private TextView enquiryIdValueTextView;
    private LinearLayout createdDateLayout;
    private TextView createdDateTextView;
    private TextView createdDateValueTextView;
    private LinearLayout createdByLayout;
    private TextView createdByTextView;
    private TextView createdByValueTextView;
    private LinearLayout lastUpdatedTimeLayout;
    private TextView lastUpdatedTimeTextView;
    private TextView lastUpdatedTimeValueTextView;
    private LinearLayout customerSaleTypeLayout;
    private TextView customerSaleTypeTextView;
    private TextView customerSaleTypeValueTextView;
    private LinearLayout orderTypeLayout;
    private TextView orderTypeTextView;
    private TextView orderTypeValueTextView;
    private LinearLayout assignedToLayout;
    private TextView assignedToTextView;
    private TextView assignedToValueTextView;
    private LinearLayout orderStatusLayout;
    private TextView orderStatusTextView;
    private TextView orderStatusValueTextView;
    private LinearLayout billingAddressLayout;
    private TextView billingAddressTextView;
    private TextView billingAddressValueTextView;
    private LinearLayout deliveryAddressLayout;
    private TextView deliveryAddressTextView;
    private TextView deliveryAddressValueTextView;
    private LinearLayout siteAddressLayout;
    private TextView siteAddressTextView;
    private TextView siteAddressValueTextView;
    private LinearLayout currentAddressLayout;
    private TextView currentAddressTextView;
    private TextView currentAddressValueTextView;
    private TextView emptyTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        cuId = bundle.getString(getString(R.string.cuid));
        orderId = bundle.getString(getString(R.string.order_id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_order_detail_tab, container, false);
        //Find all widgets
        findViews(rootView);
        return rootView;
    }

    private void findViews(final View view) {
        layoutOrderInfo = (LinearLayout) view.findViewById(R.id.layout_order_info);
        textViewOrderDetails = (TextView) view.findViewById(R.id.textView_order_details);
        orderIdLayout = (LinearLayout) view.findViewById(R.id.order_id_layout);
        orderIdTextView = (TextView) view.findViewById(R.id.order_id_textView);
        orderIdValueTextView = (TextView) view.findViewById(R.id.order_id_value_textView);
        orderDateLayout = (LinearLayout) view.findViewById(R.id.order_date_layout);
        orderDateTextView = (TextView) view.findViewById(R.id.order_date_textView);
        orderDateValueTextView = (TextView) view.findViewById(R.id.order_date_value_textView);
        poNoLayout = (LinearLayout) view.findViewById(R.id.po_no_layout);
        poNoTextView = (TextView) view.findViewById(R.id.po_no_textView);
        poNoValueTextView = (TextView) view.findViewById(R.id.po_no_value_textView);
        poDateLayout = (LinearLayout) view.findViewById(R.id.po_date_layout);
        poDateTextView = (TextView) view.findViewById(R.id.po_date_textView);
        poDateValueTextView = (TextView) view.findViewById(R.id.po_date_value_textView);
        quotationIdLayout = (LinearLayout) view.findViewById(R.id.quotation_id_layout);
        quotationIdTextView = (TextView) view.findViewById(R.id.quotation_id_textView);
        quotationIdValueTextView = (TextView) view.findViewById(R.id.quotation_id_value_textView);
        enquiryIdLayout = (LinearLayout) view.findViewById(R.id.enquiry_id_layout);
        enquiryIdTextView = (TextView) view.findViewById(R.id.enquiry_id_textView);
        enquiryIdValueTextView = (TextView) view.findViewById(R.id.enquiry_id_value_textView);
        createdDateLayout = (LinearLayout) view.findViewById(R.id.created_date_layout);
        createdDateTextView = (TextView) view.findViewById(R.id.created_date_textView);
        createdDateValueTextView = (TextView) view.findViewById(R.id.created_date_value_textView);
        createdByLayout = (LinearLayout) view.findViewById(R.id.created_by_layout);
        createdByTextView = (TextView) view.findViewById(R.id.created_by_textView);
        createdByValueTextView = (TextView) view.findViewById(R.id.created_by_value_textView);
        lastUpdatedTimeLayout = (LinearLayout) view.findViewById(R.id.last_updated_time_layout);
        lastUpdatedTimeTextView = (TextView) view.findViewById(R.id.last_updated_time_textView);
        lastUpdatedTimeValueTextView = (TextView) view.findViewById(R.id.last_updated_time_value_textView);
        customerSaleTypeLayout = (LinearLayout) view.findViewById(R.id.customer_sale_type_layout);
        customerSaleTypeTextView = (TextView) view.findViewById(R.id.customer_sale_type_textView);
        customerSaleTypeValueTextView = (TextView) view.findViewById(R.id.customer_sale_type_value_textView);
        orderTypeLayout = (LinearLayout) view.findViewById(R.id.order_type_layout);
        orderTypeTextView = (TextView) view.findViewById(R.id.order_type_textView);
        orderTypeValueTextView = (TextView) view.findViewById(R.id.order_type_value_textView);
        assignedToLayout = (LinearLayout) view.findViewById(R.id.assigned_to_layout);
        assignedToTextView = (TextView) view.findViewById(R.id.assigned_to_textView);
        assignedToValueTextView = (TextView) view.findViewById(R.id.assigned_to_value_textView);
        orderStatusLayout = (LinearLayout) view.findViewById(R.id.order_status_layout);
        orderStatusTextView = (TextView) view.findViewById(R.id.order_status_textView);
        orderStatusValueTextView = (TextView) view.findViewById(R.id.order_status_value_textView);
        billingAddressLayout = (LinearLayout) view.findViewById(R.id.billing_address_layout);
        billingAddressTextView = (TextView) view.findViewById(R.id.billing_address_textView);
        billingAddressValueTextView = (TextView) view.findViewById(R.id.billing_address_value_textView);
        deliveryAddressLayout = (LinearLayout) view.findViewById(R.id.delivery_address_layout);
        deliveryAddressTextView = (TextView) view.findViewById(R.id.delivery_address_textView);
        deliveryAddressValueTextView = (TextView) view.findViewById(R.id.delivery_address_value_textView);
        deliveryAddressLayout = (LinearLayout) view.findViewById(R.id.delivery_address_layout);
        deliveryAddressTextView = (TextView) view.findViewById(R.id.delivery_address_textView);
        deliveryAddressValueTextView = (TextView) view.findViewById(R.id.delivery_address_value_textView);
        siteAddressLayout = (LinearLayout) view.findViewById(R.id.site_address_layout);
        siteAddressTextView = (TextView) view.findViewById(R.id.site_address_textView);
        siteAddressValueTextView = (TextView) view.findViewById(R.id.site_address_value_textView);
        currentAddressLayout = (LinearLayout) view.findViewById(R.id.current_address_layout);
        currentAddressTextView = (TextView) view.findViewById(R.id.current_address_textView);
        currentAddressValueTextView = (TextView) view.findViewById(R.id.current_address_value_textView);
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
                    getOrderInfo(cuId, orderId);
                }
            }, 200);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.not_connected_to_internet));
            layoutOrderInfo.setVisibility(View.GONE);
        }

    }

    public void setData() {

        //Order Id
        if (orderIdStr != null && !orderIdStr.isEmpty() && !orderIdStr.equals("null") && !orderIdStr.equals("NA")) {
            orderIdValueTextView.setText(orderIdStr.toString().trim());
            orderIdLayout.setVisibility(View.VISIBLE);
        } else {
            orderIdLayout.setVisibility(View.GONE);
        }

        //Order Date
        if (orderDate != null && !orderDate.isEmpty() && !orderDate.equals("null") && !orderDate.equals("NA")) {
            orderDateValueTextView.setText(orderDate.toString().trim());
            orderDateLayout.setVisibility(View.VISIBLE);
        } else {
            orderDateLayout.setVisibility(View.GONE);
        }

        //P.O. No
        if (poNo != null && !poNo.isEmpty() && !poNo.equals("null") && !poNo.equals("NA")) {
            poNoValueTextView.setText(poNo.toString().trim());
            poNoLayout.setVisibility(View.VISIBLE);
        } else {
            poNoLayout.setVisibility(View.GONE);
        }

        //P.O. Date
        if (poDate != null && !poDate.isEmpty() && !poDate.equals("null") && !poDate.equals("NA")) {
            poDateValueTextView.setText(poDate.toString().trim());
            poDateLayout.setVisibility(View.VISIBLE);
        } else {
            poDateLayout.setVisibility(View.GONE);
        }

        //Quotation Id
        if (quotationId != null && !quotationId.isEmpty() && !quotationId.equals("null") && !quotationId.equals("NA")) {
            quotationIdValueTextView.setText(quotationId.toString().trim());
            quotationIdLayout.setVisibility(View.VISIBLE);
        } else {
            quotationIdLayout.setVisibility(View.GONE);
        }

        //Quotation Id
        if (enquiryId != null && !enquiryId.isEmpty() && !enquiryId.equals("null") && !enquiryId.equals("NA")) {
            enquiryIdValueTextView.setText(enquiryId.toString().trim());
            enquiryIdLayout.setVisibility(View.VISIBLE);
        } else {
            enquiryIdLayout.setVisibility(View.GONE);
        }

        //Created Date
        if (createdDate != null && !createdDate.isEmpty() && !createdDate.equals("null") && !createdDate.equals("NA")) {
            createdDateValueTextView.setText(createdDate.toString().trim());
            createdDateLayout.setVisibility(View.VISIBLE);
        } else {
            createdDateLayout.setVisibility(View.GONE);
        }

        //Created By
        if (createdBy != null && !createdBy.isEmpty() && !createdBy.equals("null") && !createdBy.equals("NA")) {
            createdByValueTextView.setText(createdBy.toString().trim());
            createdByLayout.setVisibility(View.VISIBLE);
        } else {
            createdByLayout.setVisibility(View.GONE);
        }


        /*//Status
        if (orderStatus != null && !orderStatus.isEmpty() && !orderStatus.equals("null")) {
            orderStatusValueTextView.setText(orderStatus.toString().trim());
            orderStatusLayout.setVisibility(View.VISIBLE);
        } else {
            orderStatusLayout.setVisibility(View.GONE);
        }*/

        //Last Updated Time
        if (lastUpdatedTime != null && !lastUpdatedTime.isEmpty() && !lastUpdatedTime.equals("null") && !lastUpdatedTime.equals("NA")) {
            lastUpdatedTimeValueTextView.setText(lastUpdatedTime.toString().trim());
            lastUpdatedTimeLayout.setVisibility(View.VISIBLE);
        } else {
            lastUpdatedTimeLayout.setVisibility(View.GONE);
        }

        //Customer Sale Type
        if (customerSaleType != null && !customerSaleType.isEmpty() && !customerSaleType.equals("null") && !customerSaleType.equals("NA")) {
            customerSaleTypeValueTextView.setText(customerSaleType.toString().trim());
            customerSaleTypeLayout.setVisibility(View.VISIBLE);
        } else {
            customerSaleTypeLayout.setVisibility(View.GONE);
        }

        //Order Type
        if (orderType != null && !orderType.isEmpty() && !orderType.equals("null") && !orderType.equals("NA")) {
            orderTypeValueTextView.setText(orderType.toString().trim());
            orderTypeLayout.setVisibility(View.VISIBLE);
        } else {
            orderTypeLayout.setVisibility(View.GONE);
        }

        //Assigned To
        if (assignedTo != null && !assignedTo.isEmpty() && !assignedTo.equals("null") && !assignedTo.equals("NA")) {
            assignedToValueTextView.setText(assignedTo.toString().trim());
            assignedToLayout.setVisibility(View.VISIBLE);
        } else {
            assignedToLayout.setVisibility(View.GONE);
        }

        //Order Status
        if (orderStatus != null && !orderStatus.isEmpty() && !orderStatus.equals("null") && !orderStatus.equals("NA")) {
            //orderStatusValueTextView.setText(orderStatus.toString().trim());
            setOrderStatus(orderStatus.trim(), orderStatusValueTextView);
            orderStatusLayout.setVisibility(View.VISIBLE);
        } else {
            orderStatusLayout.setVisibility(View.GONE);
        }

        //Billing Address
        if (billingAddress != null && !billingAddress.isEmpty() && !billingAddress.equals("null") && !billingAddress.equals("NA")) {
            billingAddressValueTextView.setText(billingAddress.toString().trim());
            billingAddressLayout.setVisibility(View.VISIBLE);
        } else {
            billingAddressLayout.setVisibility(View.GONE);
        }

        //Delivery Address
        if (deliveryAddress != null && !deliveryAddress.isEmpty() && !deliveryAddress.equals("null") && !deliveryAddress.equals("NA")) {
            deliveryAddressValueTextView.setText(deliveryAddress.toString().trim());
            deliveryAddressLayout.setVisibility(View.VISIBLE);
        } else {
            deliveryAddressLayout.setVisibility(View.GONE);
        }

        //Site Address
        if (siteAddress != null && !siteAddress.isEmpty() && !siteAddress.equals("null") && !siteAddress.equals("NA")) {
            siteAddressValueTextView.setText(siteAddress.toString().trim());
            siteAddressLayout.setVisibility(View.VISIBLE);
        } else {
            siteAddressLayout.setVisibility(View.GONE);
        }

        //Current Address
        if (currentAddress != null && !currentAddress.isEmpty() && !currentAddress.equals("null") && !currentAddress.equals("NA")) {
            currentAddressValueTextView.setText(currentAddress.toString().trim());
            currentAddressLayout.setVisibility(View.VISIBLE);
        } else {
            currentAddressLayout.setVisibility(View.GONE);
        }

    }

    private void getOrderInfo(String cuId, String orderId) {
        task = getString(R.string.customer_order_info);
        userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getCustomerOrderInfo(version, key, task, userId, accessToken, cuId, orderId);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    OrderDetails orderDetails = apiResponse.getData().getOrderDetails();
                    if (orderDetails != null) {

                        orderIdStr = orderDetails.getOrderId();
                        orderDate = orderDetails.getOrderDate();
                        poNo = orderDetails.getPoNumber();
                        poDate = orderDetails.getPoDate();
                        quotationId = orderDetails.getQuotationId();
                        enquiryId = orderDetails.getEnquiryId();
                        createdDate = orderDetails.getCreatedDate();
                        createdBy = orderDetails.getCreatedBy();
                        lastUpdatedTime = orderDetails.getLastUpdatedTs();
                        customerSaleType = orderDetails.getCustSaleType();
                        orderType = orderDetails.getOrderType();
                        assignedTo = orderDetails.getAssignedTo();
                        orderStatus = orderDetails.getOrderStatus();
                        billingAddress = orderDetails.getBillingAddress();
                        siteAddress = orderDetails.getSiteAddress();
                        currentAddress = orderDetails.getCurrentAddress();
                        setData();
                    }
                    emptyTextView.setVisibility(View.GONE);
                    emptyTextView.setText("No data");
                    layoutOrderInfo.setVisibility(View.VISIBLE);
                } else if (apiResponse.getSuccessCode().equals("20004")) {
                    if (isAdded() && getActivity() != null) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText(apiResponse.getMessage());
                        layoutOrderInfo.setVisibility(View.GONE);
                    }
                }//Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                } else {
                    if (isAdded() && getActivity() != null) {
                        emptyTextView.setVisibility(View.VISIBLE);
                        emptyTextView.setText(apiResponse.getMessage());
                        layoutOrderInfo.setVisibility(View.GONE);
                    }
                    Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if (isAdded() && getActivity() != null) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    //   Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_SHORT).show();
                    emptyTextView.setVisibility(View.VISIBLE);
                    emptyTextView.setText("No data");
                    layoutOrderInfo.setVisibility(View.GONE);
                }
            }

        });

    }

    private void setOrderStatus(String strStatus, TextView textView) {
        textView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) textView.getBackground();

        if (strStatus.equals("Pending")) {
            drawable.setColor(getResources().getColor(R.color.orange_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Partial Assigned")) {
            drawable.setColor(getResources().getColor(R.color.blue_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Assigned")) {
            drawable.setColor(getResources().getColor(R.color.green_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Partial Delivered")) {
            drawable.setColor(getResources().getColor(R.color.blue_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Delivered")) {
            drawable.setColor(getResources().getColor(R.color.green_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Rejected")) {
            drawable.setColor(getResources().getColor(R.color.red_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Revoked")) {
            drawable.setColor(getResources().getColor(R.color.green_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Partially Executed")) {
            drawable.setColor(getResources().getColor(R.color.blue_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Executed")) {
            drawable.setColor(getResources().getColor(R.color.green_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Cancelled")) {
            drawable.setColor(getResources().getColor(R.color.red_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("Deleted")) {
            drawable.setColor(getResources().getColor(R.color.red_order));
            textView.setText(strStatus);
        } else if (strStatus.equals("To be Approved")) {
            drawable.setColor(getResources().getColor(R.color.blue_order));
            textView.setText(strStatus);
        } else {
            drawable.setColor(getResources().getColor(R.color.green_order));
            textView.setText("Executed");
        }
    }


}
