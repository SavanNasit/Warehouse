package com.accrete.sixorbit.fragment.Drawer.orders;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.OrderDetails;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersBasicInfoFragment extends Fragment {
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
    private LinearLayout layoutCustomerInfo;
    private TextView textViewCustomerDetails;
    private LinearLayout nameLayout;
    private TextView nameTextView;
    private TextView nameValueTextView;
    private LinearLayout emailLayout;
    private TextView emailTextView;
    private TextView emailValueTextView;
    private LinearLayout mobileLayout;
    private TextView mobileTextView;
    private TextView mobileValueTextView;
    private TextView emptyTextView;
    private OrderDetails orderDetailsInfo;
    private LinearLayout dueDateLayout;
    private TextView dueDateTextView;
    private TextView dueDateValueTextView;
    private LinearLayout remarksLayout;
    private TextView remarksTextView;
    private TextView remarksValueTextView;
    private LinearLayout updatedByLayout;
    private TextView updatedByTextView;
    private TextView updatedByValueTextView;
    private LinearLayout shippingAddressLayout;
    private TextView shippingAddressTextView;
    private TextView shippingAddressValueTextView;
    public OrdersBasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_orders_basic_info, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        layoutOrderInfo = (LinearLayout) view.findViewById(R.id.layout_order_info);
        textViewOrderDetails = (TextView) view.findViewById(R.id.textView_order_details);
        orderIdLayout = (LinearLayout) view.findViewById(R.id.order_id_layout);
        orderIdTextView = (TextView) view.findViewById(R.id.order_id_textView);
        orderIdValueTextView = (TextView) view.findViewById(R.id.order_id_value_textView);
        orderDateLayout = (LinearLayout) view.findViewById(R.id.order_date_layout);
        orderDateTextView = (TextView) view.findViewById(R.id.order_date_textView);
        orderDateValueTextView = (TextView) view.findViewById(R.id.order_date_value_textView);
        dueDateLayout = (LinearLayout) view.findViewById(R.id.due_date_layout);
        dueDateTextView = (TextView) view.findViewById(R.id.due_date_textView);
        dueDateValueTextView = (TextView) view.findViewById(R.id.due_date_value_textView);
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
        updatedByLayout = (LinearLayout) view.findViewById(R.id.updated_by_layout);
        updatedByTextView = (TextView) view.findViewById(R.id.updated_by_textView);
        updatedByValueTextView = (TextView) view.findViewById(R.id.updated_by_value_textView);
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
        siteAddressLayout = (LinearLayout) view.findViewById(R.id.site_address_layout);
        siteAddressTextView = (TextView) view.findViewById(R.id.site_address_textView);
        siteAddressValueTextView = (TextView) view.findViewById(R.id.site_address_value_textView);
        currentAddressLayout = (LinearLayout) view.findViewById(R.id.current_address_layout);
        currentAddressTextView = (TextView) view.findViewById(R.id.current_address_textView);
        currentAddressValueTextView = (TextView) view.findViewById(R.id.current_address_value_textView);
        remarksLayout = (LinearLayout) view.findViewById(R.id.remarks_layout);
        remarksTextView = (TextView) view.findViewById(R.id.remarks_textView);
        remarksValueTextView = (TextView) view.findViewById(R.id.remarks_value_textView);
        layoutCustomerInfo = (LinearLayout) view.findViewById(R.id.layout_customer_info);
        textViewCustomerDetails = (TextView) view.findViewById(R.id.textView_customer_details);
        nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
        nameTextView = (TextView) view.findViewById(R.id.name_textView);
        nameValueTextView = (TextView) view.findViewById(R.id.name_value_textView);
        emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
        emailTextView = (TextView) view.findViewById(R.id.email_textView);
        emailValueTextView = (TextView) view.findViewById(R.id.email_value_textView);
        mobileLayout = (LinearLayout) view.findViewById(R.id.mobile_layout);
        mobileTextView = (TextView) view.findViewById(R.id.mobile_textView);
        mobileValueTextView = (TextView) view.findViewById(R.id.mobile_value_textView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);
        shippingAddressLayout = (LinearLayout)view.findViewById( R.id.shipping_address_layout );
        shippingAddressTextView = (TextView)view.findViewById( R.id.shipping_address_textView );
        shippingAddressValueTextView = (TextView)view.findViewById( R.id.shipping_address_value_textView );

        layoutOrderInfo.setVisibility(View.GONE);
    }

    public void getDataFromParent(OrderDetails orderDetails) {
        this.orderDetailsInfo = orderDetails;

        setData();
    }

    private void setData() {
        if (orderDetailsInfo != null) {

            //Order Id
            if (orderDetailsInfo.getOrderId() != null && !orderDetailsInfo.getOrderId().isEmpty()) {
                orderIdValueTextView.setText(orderDetailsInfo.getOrderId() + "");
                orderIdLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                orderIdLayout.setVisibility(View.GONE);
            }

            //Order date
            if (orderDetailsInfo.getOrderDate() != null && !orderDetailsInfo.getOrderDate().isEmpty()) {
                orderDateValueTextView.setText(orderDetailsInfo.getOrderDate() + "");
                orderDateLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                orderDateLayout.setVisibility(View.GONE);
            }

            //Due date
            if (orderDetailsInfo.getDueDate() != null && !orderDetailsInfo.getDueDate().isEmpty()) {
                dueDateValueTextView.setText(orderDetailsInfo.getDueDate() + "");
                dueDateLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                dueDateLayout.setVisibility(View.GONE);
            }

            //PO No
            if (orderDetailsInfo.getPoNumber() != null && !orderDetailsInfo.getPoNumber().isEmpty()) {
                poNoValueTextView.setText(orderDetailsInfo.getPoNumber() + "");
                poNoLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                poNoLayout.setVisibility(View.GONE);
            }

            //PO Date
            if (orderDetailsInfo.getPoDate() != null && !orderDetailsInfo.getPoDate().isEmpty()) {
                poDateValueTextView.setText(orderDetailsInfo.getPoDate() + "");
                poDateLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                poDateLayout.setVisibility(View.GONE);
            }

            //Quotation Id
            if (orderDetailsInfo.getQuotationId() != null && !orderDetailsInfo.getQuotationId().isEmpty()) {
                quotationIdValueTextView.setText(orderDetailsInfo.getQuotationId() + "");
                quotationIdLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                quotationIdLayout.setVisibility(View.GONE);
            }

            //Enquiry Id
            if (orderDetailsInfo.getEnquiryId() != null && !orderDetailsInfo.getEnquiryId().isEmpty()) {
                enquiryIdValueTextView.setText(orderDetailsInfo.getEnquiryId() + "");
                enquiryIdLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                enquiryIdLayout.setVisibility(View.GONE);
            }

            //Created Date
            if (orderDetailsInfo.getCreatedDate() != null && !orderDetailsInfo.getCreatedDate().isEmpty()) {
                createdDateValueTextView.setText(orderDetailsInfo.getCreatedDate() + "");
                createdDateLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                createdDateLayout.setVisibility(View.GONE);
            }

            //Created By
            if (orderDetailsInfo.getCreatedBy() != null && !orderDetailsInfo.getCreatedBy().isEmpty()) {
                createdByValueTextView.setText(orderDetailsInfo.getCreatedBy() + "");
                createdByLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                createdByLayout.setVisibility(View.GONE);
            }

            //Updated By
            if (orderDetailsInfo.getUpdatedBy() != null && !orderDetailsInfo.getUpdatedBy().isEmpty()) {
                updatedByValueTextView.setText(orderDetailsInfo.getUpdatedBy() + "");
                updatedByLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                updatedByLayout.setVisibility(View.GONE);
            }

            //Last updated Time
            if (orderDetailsInfo.getLastUpdatedTime() != null && !orderDetailsInfo.getLastUpdatedTime().isEmpty()) {
                lastUpdatedTimeValueTextView.setText(orderDetailsInfo.getLastUpdatedTime() + "");
                lastUpdatedTimeLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                lastUpdatedTimeLayout.setVisibility(View.GONE);
            }

            //Customer Sale Type
            if (orderDetailsInfo.getCustomerSaleType() != null && !orderDetailsInfo.getCustomerSaleType().isEmpty()) {
                customerSaleTypeValueTextView.setText(orderDetailsInfo.getCustomerSaleType() + "");
                customerSaleTypeLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                customerSaleTypeLayout.setVisibility(View.GONE);
            }

            //Order Type
            if (orderDetailsInfo.getOrderType() != null && !orderDetailsInfo.getOrderType().isEmpty()) {
                orderTypeValueTextView.setText(orderDetailsInfo.getOrderType() + "");
                orderTypeLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                orderTypeLayout.setVisibility(View.GONE);
            }

            //Assigned To
            if (orderDetailsInfo.getAssignedTo() != null && !orderDetailsInfo.getAssignedTo().isEmpty()) {
                assignedToValueTextView.setText(orderDetailsInfo.getAssignedTo() + "");
                assignedToLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                assignedToLayout.setVisibility(View.GONE);
            }

            //Order Status
            if (orderDetailsInfo.getOrderStatus() != null && !orderDetailsInfo.getOrderStatus().isEmpty()) {
                orderStatusValueTextView.setText(orderDetailsInfo.getOrderStatus() + "");
                orderStatusLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
                setOrderStatus(orderDetailsInfo.getOrderStatus(), orderStatusValueTextView);
            } else {
                orderStatusLayout.setVisibility(View.GONE);
            }

            //address
            deliveryAddressLayout.setVisibility(View.GONE);
            billingAddressLayout.setVisibility(View.GONE);

            //Shipping Address
            if ((orderDetailsInfo.getShippingAddressLine1() != null && !orderDetailsInfo.getShippingAddressLine1().isEmpty())
                    || (orderDetailsInfo.getShippingAddressLine2() != null && !orderDetailsInfo.getShippingAddressLine2().isEmpty())
                    || (orderDetailsInfo.getShippingAddressCity() != null && !orderDetailsInfo.getShippingAddressCity().isEmpty())
                    || (orderDetailsInfo.getShippingAddressZipcode() != null && !orderDetailsInfo.getShippingAddressZipcode().isEmpty())
                    || (orderDetailsInfo.getShippingAddressCountry() != null && !orderDetailsInfo.getShippingAddressCountry().isEmpty())) {
                shippingAddressLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);

                String currentAddress = "";

                //Address Line 1
                if (orderDetailsInfo.getShippingAddressLine1() != null && !orderDetailsInfo.getShippingAddressLine1().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getShippingAddressLine1() + ", ";
                }

                //Address Line 2
                if (orderDetailsInfo.getShippingAddressLine2() != null && !orderDetailsInfo.getShippingAddressLine2().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getShippingAddressLine2() + ", ";
                }

                //City & Zip Code
                if (orderDetailsInfo.getShippingAddressCity() != null &&
                        !orderDetailsInfo.getShippingAddressCity().toString().trim().isEmpty() &&
                        orderDetailsInfo.getShippingAddressZipcode() != null &&
                        !orderDetailsInfo.getShippingAddressZipcode().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getShippingAddressCity().toString().trim() + " - " +
                            orderDetailsInfo.getShippingAddressZipcode()
                                    .toString().trim() + "," + "\n";
                } else if (orderDetailsInfo.getShippingAddressCity() != null &&
                        !orderDetailsInfo.getShippingAddressCity().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getShippingAddressCity().toString().trim() + ", ";
                } else if (orderDetailsInfo.getShippingAddressZipcode() != null &&
                        !orderDetailsInfo.getShippingAddressZipcode().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getShippingAddressZipcode()
                                    .toString().trim() + ", ";
                }
                //State
                if (orderDetailsInfo.getShippingAddressState() != null &&
                        !orderDetailsInfo.getShippingAddressState().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getShippingAddressState() + ", ";
                }

                //Country
                if (orderDetailsInfo.getShippingAddressCountry() != null &&
                        !orderDetailsInfo.getShippingAddressCountry().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getShippingAddressCountry() + " ";
                }

                shippingAddressValueTextView.setText(currentAddress + "");

            } else {
                shippingAddressLayout.setVisibility(View.GONE);
            }

            //Current Address
            if ((orderDetailsInfo.getCurrentAddressLine1() != null && !orderDetailsInfo.getCurrentAddressLine1().isEmpty())
                    || (orderDetailsInfo.getCurrentAddressLine2() != null && !orderDetailsInfo.getCurrentAddressLine2().isEmpty())
                    || (orderDetailsInfo.getCurrentAddressCity() != null && !orderDetailsInfo.getCurrentAddressCity().isEmpty())
                    || (orderDetailsInfo.getCurrentAddressZipcode() != null && !orderDetailsInfo.getCurrentAddressZipcode().isEmpty())
                    || (orderDetailsInfo.getCurrentAddressCountry() != null && !orderDetailsInfo.getCurrentAddressCountry().isEmpty())) {
                currentAddressLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);

                String currentAddress = "";

                //Address Line 1
                if (orderDetailsInfo.getCurrentAddressLine1() != null && !orderDetailsInfo.getCurrentAddressLine1().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getCurrentAddressLine1() + ", ";
                }

                //Address Line 2
                if (orderDetailsInfo.getCurrentAddressLine2() != null && !orderDetailsInfo.getCurrentAddressLine2().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getCurrentAddressLine2() + ", ";
                }

                //City & Zip Code
                if (orderDetailsInfo.getCurrentAddressCity() != null &&
                        !orderDetailsInfo.getCurrentAddressCity().toString().trim().isEmpty() &&
                        orderDetailsInfo.getCurrentAddressZipcode() != null &&
                        !orderDetailsInfo.getCurrentAddressZipcode().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getCurrentAddressCity().toString().trim() + " - " +
                            orderDetailsInfo.getCurrentAddressZipcode()
                                    .toString().trim() + "," + "\n";
                } else if (orderDetailsInfo.getCurrentAddressCity() != null &&
                        !orderDetailsInfo.getCurrentAddressCity().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getCurrentAddressCity().toString().trim() + ", ";
                } else if (orderDetailsInfo.getCurrentAddressZipcode() != null &&
                        !orderDetailsInfo.getCurrentAddressZipcode().toString().trim().isEmpty()) {
                    currentAddress = currentAddress +
                            orderDetailsInfo.getCurrentAddressZipcode()
                                    .toString().trim() + ", ";
                }
                //State
                if (orderDetailsInfo.getCurrentAddressState() != null &&
                        !orderDetailsInfo.getCurrentAddressState().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getCurrentAddressState() + ", ";
                }

                //Country
                if (orderDetailsInfo.getCurrentAddressCountry() != null &&
                        !orderDetailsInfo.getCurrentAddressCountry().isEmpty()) {
                    currentAddress = currentAddress + orderDetailsInfo.getCurrentAddressCountry() + " ";
                }

                currentAddressValueTextView.setText(currentAddress + "");
                currentAddressTextView.setText("Current Address :");
            } else {
                currentAddressLayout.setVisibility(View.GONE);
            }

            //Billing Address
            if ((orderDetailsInfo.getBillingAddressLine1() != null && !orderDetailsInfo.getBillingAddressLine1().isEmpty())
                    || (orderDetailsInfo.getBillingAddressLine2() != null && !orderDetailsInfo.getBillingAddressLine2().isEmpty())
                    || (orderDetailsInfo.getBillingAddressCity() != null && !orderDetailsInfo.getBillingAddressCity().isEmpty())
                    || (orderDetailsInfo.getBillingAddressZipcode() != null && !orderDetailsInfo.getBillingAddressZipcode().isEmpty())
                    || (orderDetailsInfo.getBillingAddressCountry() != null && !orderDetailsInfo.getBillingAddressCountry().isEmpty())) {
                billingAddressLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);

                String siteAddress = "";

                //Address Line 1
                if (orderDetailsInfo.getBillingAddressLine1() != null && !orderDetailsInfo.getBillingAddressLine1().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getBillingAddressLine1() + ", ";
                }

                //Address Line 2
                if (orderDetailsInfo.getBillingAddressLine2() != null && !orderDetailsInfo.getBillingAddressLine2().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getBillingAddressLine2() + ", ";
                }

                //City & Zip Code
                if (orderDetailsInfo.getBillingAddressCity() != null &&
                        !orderDetailsInfo.getBillingAddressCity().toString().trim().isEmpty() &&
                        orderDetailsInfo.getBillingAddressZipcode() != null &&
                        !orderDetailsInfo.getBillingAddressZipcode().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getBillingAddressCity().toString().trim() + " - " +
                            orderDetailsInfo.getBillingAddressZipcode()
                                    .toString().trim() + "," + "\n";
                } else if (orderDetailsInfo.getBillingAddressCity() != null &&
                        !orderDetailsInfo.getBillingAddressCity().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getBillingAddressCity().toString().trim() + ", ";
                } else if (orderDetailsInfo.getBillingAddressZipcode() != null &&
                        !orderDetailsInfo.getBillingAddressZipcode().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getBillingAddressZipcode()
                                    .toString().trim() + ", ";
                }

                //State
                if (orderDetailsInfo.getBillingAddressState() != null &&
                        !orderDetailsInfo.getBillingAddressState().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getBillingAddressState() + ", ";
                }

                //Country
                if (orderDetailsInfo.getBillingAddressCountry() != null &&
                        !orderDetailsInfo.getBillingAddressCountry().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getBillingAddressCountry() + " ";
                }

                billingAddressValueTextView.setText(siteAddress + "");

            } else {
                billingAddressLayout.setVisibility(View.GONE);
            }

            //Site Address
            if ((orderDetailsInfo.getSiteAddressLine1() != null && !orderDetailsInfo.getSiteAddressLine1().isEmpty())
                    || (orderDetailsInfo.getSiteAddressLine2() != null && !orderDetailsInfo.getSiteAddressLine2().isEmpty())
                    || (orderDetailsInfo.getSiteAddressCity() != null && !orderDetailsInfo.getSiteAddressCity().isEmpty())
                    || (orderDetailsInfo.getSiteAddressZipcode() != null && !orderDetailsInfo.getSiteAddressZipcode().isEmpty())
                    || (orderDetailsInfo.getSiteAddressCountry() != null && !orderDetailsInfo.getSiteAddressCountry().isEmpty())) {
                siteAddressLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);

                String siteAddress = "";

                //Address Line 1
                if (orderDetailsInfo.getSiteAddressLine1() != null && !orderDetailsInfo.getSiteAddressLine1().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getSiteAddressLine1() + ", ";
                }

                //Address Line 2
                if (orderDetailsInfo.getSiteAddressLine2() != null && !orderDetailsInfo.getSiteAddressLine2().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getSiteAddressLine2() + ", ";
                }

                //City & Zip Code
                if (orderDetailsInfo.getSiteAddressCity() != null &&
                        !orderDetailsInfo.getSiteAddressCity().toString().trim().isEmpty() &&
                        orderDetailsInfo.getSiteAddressZipcode() != null &&
                        !orderDetailsInfo.getSiteAddressZipcode().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getSiteAddressCity().toString().trim() + " - " +
                            orderDetailsInfo.getSiteAddressZipcode()
                                    .toString().trim() + "," + "\n";
                } else if (orderDetailsInfo.getSiteAddressCity() != null &&
                        !orderDetailsInfo.getSiteAddressCity().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getSiteAddressCity().toString().trim() + ", ";
                } else if (orderDetailsInfo.getSiteAddressZipcode() != null &&
                        !orderDetailsInfo.getSiteAddressZipcode().toString().trim().isEmpty()) {
                    siteAddress = siteAddress +
                            orderDetailsInfo.getSiteAddressZipcode()
                                    .toString().trim() + ", ";
                }

                //State
                if (orderDetailsInfo.getSiteAddressState() != null &&
                        !orderDetailsInfo.getSiteAddressState().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getSiteAddressState() + ", ";
                }

                //Country
                if (orderDetailsInfo.getSiteAddressCountry() != null &&
                        !orderDetailsInfo.getSiteAddressCountry().isEmpty()) {
                    siteAddress = siteAddress + orderDetailsInfo.getSiteAddressCountry() + " ";
                }

                siteAddressValueTextView.setText(siteAddress + "");
                siteAddressTextView.setText("Site Address :");

            } else {
                siteAddressLayout.setVisibility(View.GONE);
            }

            //Delivery Address
            if ((orderDetailsInfo.getDeliveryAddressLine1() != null && !orderDetailsInfo.getDeliveryAddressLine1().isEmpty())
                    || (orderDetailsInfo.getDeliveryAddressLine2() != null && !orderDetailsInfo.getDeliveryAddressLine2().isEmpty())
                    || (orderDetailsInfo.getDeliveryAddressCity() != null && !orderDetailsInfo.getDeliveryAddressCity().isEmpty())
                    || (orderDetailsInfo.getDeliveryAddressZipcode() != null && !orderDetailsInfo.getDeliveryAddressZipcode().isEmpty())
                    || (orderDetailsInfo.getDeliveryAddressCountry() != null && !orderDetailsInfo.getDeliveryAddressCountry().isEmpty())) {
                deliveryAddressLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);

                String deliveryAddress = "";

                //Address Line 1
                if (orderDetailsInfo.getDeliveryAddressLine1() != null && !orderDetailsInfo.getDeliveryAddressLine1().isEmpty()) {
                    deliveryAddress = deliveryAddress + orderDetailsInfo.getDeliveryAddressLine1() + ", ";
                }

                //Address Line 2
                if (orderDetailsInfo.getDeliveryAddressLine2() != null && !orderDetailsInfo.getDeliveryAddressLine2().isEmpty()) {
                    deliveryAddress = deliveryAddress + orderDetailsInfo.getDeliveryAddressLine2() + ", ";
                }

                //City & Zip Code
                if (orderDetailsInfo.getDeliveryAddressCity() != null &&
                        !orderDetailsInfo.getDeliveryAddressCity().toString().trim().isEmpty() &&
                        orderDetailsInfo.getDeliveryAddressZipcode() != null &&
                        !orderDetailsInfo.getDeliveryAddressZipcode().toString().trim().isEmpty()) {
                    deliveryAddress = deliveryAddress +
                            orderDetailsInfo.getDeliveryAddressCity().toString().trim() + " - " +
                            orderDetailsInfo.getDeliveryAddressZipcode()
                                    .toString().trim() + "," + "\n";
                } else if (orderDetailsInfo.getDeliveryAddressCity() != null &&
                        !orderDetailsInfo.getDeliveryAddressCity().toString().trim().isEmpty()) {
                    deliveryAddress = deliveryAddress +
                            orderDetailsInfo.getDeliveryAddressCity().toString().trim() + ", ";
                } else if (orderDetailsInfo.getDeliveryAddressZipcode() != null &&
                        !orderDetailsInfo.getDeliveryAddressZipcode().toString().trim().isEmpty()) {
                    deliveryAddress = deliveryAddress +
                            orderDetailsInfo.getDeliveryAddressZipcode()
                                    .toString().trim() + ", ";
                }

                //State
                if (orderDetailsInfo.getDeliveryAddressState() != null &&
                        !orderDetailsInfo.getDeliveryAddressState().isEmpty()) {
                    deliveryAddress = deliveryAddress + orderDetailsInfo.getDeliveryAddressState() + ", ";
                }

                //Country
                if (orderDetailsInfo.getDeliveryAddressCountry() != null &&
                        !orderDetailsInfo.getDeliveryAddressCountry().isEmpty()) {
                    deliveryAddress = deliveryAddress + orderDetailsInfo.getDeliveryAddressCountry() + " ";
                }

                deliveryAddressValueTextView.setText(deliveryAddress + "");

            } else {
                deliveryAddressLayout.setVisibility(View.GONE);
            }

            //Name
            if (orderDetailsInfo.getName() != null && !orderDetailsInfo.getName().isEmpty()) {
                nameValueTextView.setText(orderDetailsInfo.getName() + "");
                nameLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                nameLayout.setVisibility(View.GONE);
            }

            //Mobile
            if (orderDetailsInfo.getMobile() != null && !orderDetailsInfo.getMobile().isEmpty()) {
                mobileValueTextView.setText(orderDetailsInfo.getMobile() + "");
                mobileLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                mobileLayout.setVisibility(View.GONE);
            }

            //Email
            if (orderDetailsInfo.getEmail() != null && !orderDetailsInfo.getEmail().isEmpty()) {
                emailValueTextView.setText(orderDetailsInfo.getEmail() + "");
                emailLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                emailLayout.setVisibility(View.GONE);
            }

            //Remarks
            if (orderDetailsInfo.getCheckpointRemarks() != null &&
                    !orderDetailsInfo.getCheckpointRemarks().isEmpty()) {
                remarksValueTextView.setText(orderDetailsInfo.getCheckpointRemarks() + "");
                remarksLayout.setVisibility(View.VISIBLE);
                layoutOrderInfo.setVisibility(View.VISIBLE);
            } else {
                remarksLayout.setVisibility(View.GONE);
            }

        }
    }

    private void setOrderStatus(String strStatus, TextView textView) {
        textView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) textView.getBackground();
        if (getActivity() != null && isAdded()) {
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


}
