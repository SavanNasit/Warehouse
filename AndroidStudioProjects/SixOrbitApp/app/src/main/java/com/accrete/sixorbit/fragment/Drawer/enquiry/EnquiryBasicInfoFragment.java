package com.accrete.sixorbit.fragment.Drawer.enquiry;


import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.BasicDetails;
import com.accrete.sixorbit.model.CustomerData;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnquiryBasicInfoFragment extends Fragment {
    private LinearLayout containerLayout;
    private LinearLayout layoutCustomerInfo;
    private TextView textViewCustomerDetails;
    private TextView textViewCustomerInfo;
    private LinearLayout customerNameLayout;
    private TextView customerNameTextView;
    private TextView customerNameValueTextView;
    private LinearLayout companyNameLayout;
    private TextView companyNameTextView;
    private TextView companyNameValueTextView;
    private LinearLayout customerEmailLayout;
    private TextView customerEmailTextView;
    private TextView customerEmailValueTextView;
    private LinearLayout customerMobileLayout;
    private TextView customerMobileTextView;
    private TextView customerMobileValueTextView;
    private LinearLayout departmentLayout;
    private TextView departmentTextView;
    private TextView departmentValueTextView;
    private LinearLayout shippingAddressLayout;
    private TextView shippingAddressTextView;
    private TextView shippingAddressValueTextView;
    private LinearLayout billingAddressLayout;
    private TextView billingAddressTextView;
    private TextView billingAddressValueTextView;
    private TextView textViewBasicInfo;
    private LinearLayout remarksLayout;
    private TextView remarksTextView;
    private TextView remarksValueTextView;
    private LinearLayout reasonLayout;
    private TextView reasonTextView;
    private TextView reasonValueTextView;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView statusValueTextView;
    private CustomerData data;
    private BasicDetails basicDetails;

    public EnquiryBasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_enquiry_basic_info, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        containerLayout = (LinearLayout) view.findViewById(R.id.container_layout);
        layoutCustomerInfo = (LinearLayout) view.findViewById(R.id.layout_customer_info);
        textViewCustomerDetails = (TextView) view.findViewById(R.id.textView_customer_details);
        textViewCustomerInfo = (TextView) view.findViewById(R.id.textView_customer_info);
        customerNameLayout = (LinearLayout) view.findViewById(R.id.customer_name_layout);
        customerNameTextView = (TextView) view.findViewById(R.id.customer_name_textView);
        customerNameValueTextView = (TextView) view.findViewById(R.id.customer_name_value_textView);
        companyNameLayout = (LinearLayout) view.findViewById(R.id.company_name_layout);
        companyNameTextView = (TextView) view.findViewById(R.id.company_name_textView);
        companyNameValueTextView = (TextView) view.findViewById(R.id.company_name_value_textView);
        customerEmailLayout = (LinearLayout) view.findViewById(R.id.customer_email_layout);
        customerEmailTextView = (TextView) view.findViewById(R.id.customer_email_textView);
        customerEmailValueTextView = (TextView) view.findViewById(R.id.customer_email_value_textView);
        customerMobileLayout = (LinearLayout) view.findViewById(R.id.customer_mobile_layout);
        customerMobileTextView = (TextView) view.findViewById(R.id.customer_mobile_textView);
        customerMobileValueTextView = (TextView) view.findViewById(R.id.customer_mobile_value_textView);
        departmentLayout = (LinearLayout) view.findViewById(R.id.department_layout);
        departmentTextView = (TextView) view.findViewById(R.id.department_textView);
        departmentValueTextView = (TextView) view.findViewById(R.id.department_value_textView);
        shippingAddressLayout = (LinearLayout) view.findViewById(R.id.shipping_address_layout);
        shippingAddressTextView = (TextView) view.findViewById(R.id.shipping_address_textView);
        shippingAddressValueTextView = (TextView) view.findViewById(R.id.shipping_address_value_textView);
        billingAddressLayout = (LinearLayout) view.findViewById(R.id.billing_address_layout);
        billingAddressTextView = (TextView) view.findViewById(R.id.billing_address_textView);
        billingAddressValueTextView = (TextView) view.findViewById(R.id.billing_address_value_textView);
        textViewBasicInfo = (TextView) view.findViewById(R.id.textView_basic_info);
        remarksLayout = (LinearLayout) view.findViewById(R.id.remarks_layout);
        remarksTextView = (TextView) view.findViewById(R.id.remarks_textView);
        remarksValueTextView = (TextView) view.findViewById(R.id.remarks_value_textView);
        reasonLayout = (LinearLayout) view.findViewById(R.id.reason_layout);
        reasonTextView = (TextView) view.findViewById(R.id.reason_textView);
        reasonValueTextView = (TextView) view.findViewById(R.id.reason_value_textView);
        statusLayout = (LinearLayout) view.findViewById(R.id.status_layout);
        statusTextView = (TextView) view.findViewById(R.id.status_textView);
        statusValueTextView = (TextView) view.findViewById(R.id.status_value_textView);

        containerLayout.setVisibility(View.GONE);
    }

    protected void getBasicInfo(CustomerData customerData, BasicDetails details) {
        try {
            this.data = customerData;
            this.basicDetails = details;
            if (getActivity() != null) {
                setData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        try {
            if (getActivity() != null && data != null) {
                //Name
                if (data.getCustomerName() != null && !data.getCustomerName().toString().trim().isEmpty()) {
                    customerNameValueTextView.setText(data.getCustomerName().toString().trim());
                    customerNameLayout.setVisibility(View.VISIBLE);
                    companyNameLayout.setVisibility(View.GONE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else if (data.getCompanyName() != null && !data.getCompanyName().toString().trim().isEmpty()) {
                    companyNameValueTextView.setText(data.getCompanyName().toString().trim());
                    customerNameLayout.setVisibility(View.GONE);
                    companyNameLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerNameLayout.setVisibility(View.GONE);
                    companyNameLayout.setVisibility(View.GONE);
                }

                //Mobile
                if (data.getMobile() != null && !data.getMobile().toString().trim().isEmpty()) {
                    customerMobileValueTextView.setText(data.getMobile() + "");
                    customerMobileLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerMobileLayout.setVisibility(View.GONE);
                }

                //Email
                if (data.getEmail() != null && !data.getEmail().toString().trim().isEmpty()) {
                    customerEmailValueTextView.setText(data.getEmail() + "");
                    customerEmailLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerEmailLayout.setVisibility(View.GONE);
                }

                //Department
                if (data.getDepartment() != null && !data.getDepartment().toString().trim().isEmpty()) {
                    departmentValueTextView.setText(data.getDepartment() + "");
                    departmentLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    departmentLayout.setVisibility(View.GONE);
                }

                //Shipping address Address
                if ((data.getShippingAddressLine1() != null && !data.getShippingAddressLine1().isEmpty())
                        || (data.getShippingAddressLine2() != null && !data.getShippingAddressLine2().isEmpty())
                        || (data.getShippingAddressCity() != null && !data.getShippingAddressCity().isEmpty())
                        || (data.getShippingAddressZipcode() != null && !data.getShippingAddressZipcode().isEmpty())
                        || (data.getShippingAddressCountry() != null && !data.getShippingAddressCountry().isEmpty())) {
                    shippingAddressLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);

                    String currentAddress = "";

                    //Address Line 1
                    if (data.getShippingAddressLine1() != null && !data.getShippingAddressLine1().isEmpty()) {
                        currentAddress = currentAddress + data.getShippingAddressLine1() + ", ";
                    }

                    //Address Line 2
                    if (data.getShippingAddressLine2() != null && !data.getShippingAddressLine2().isEmpty()) {
                        currentAddress = currentAddress + data.getShippingAddressLine2() + ", ";
                    }

                    //City & Zip Code
                    if (data.getShippingAddressCity() != null &&
                            !data.getShippingAddressCity().toString().trim().isEmpty() &&
                            data.getShippingAddressZipcode() != null &&
                            !data.getShippingAddressZipcode().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                data.getShippingAddressCity().toString().trim() + " - " +
                                data.getShippingAddressZipcode()
                                        .toString().trim() + "," + "\n";
                    } else if (data.getShippingAddressCity() != null &&
                            !data.getShippingAddressCity().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                data.getShippingAddressCity().toString().trim() + ", ";
                    } else if (data.getShippingAddressZipcode() != null &&
                            !data.getShippingAddressZipcode().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                data.getShippingAddressZipcode()
                                        .toString().trim() + ", ";
                    }
                    //State
                    if (data.getShippingAddressState() != null &&
                            !data.getShippingAddressState().isEmpty()) {
                        currentAddress = currentAddress + data.getShippingAddressState() + ", ";
                    }

                    //Country
                    if (data.getShippingAddressCountry() != null &&
                            !data.getShippingAddressCountry().isEmpty()) {
                        currentAddress = currentAddress + data.getShippingAddressCountry() + " ";
                    }

                    shippingAddressValueTextView.setText(currentAddress + "");
                } else {
                    shippingAddressLayout.setVisibility(View.GONE);
                }

                //Billing Address
                if ((data.getBillingAddressLine1() != null && !data.getBillingAddressLine1().isEmpty())
                        || (data.getBillingAddressLine2() != null && !data.getBillingAddressLine2().isEmpty())
                        || (data.getBillingAddressCity() != null && !data.getBillingAddressCity().isEmpty())
                        || (data.getBillingAddressZipcode() != null && !data.getBillingAddressZipcode().isEmpty())
                        || (data.getBillingAddressCountry() != null && !data.getBillingAddressCountry().isEmpty())) {
                    billingAddressLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);

                    String siteAddress = "";

                    //Address Line 1
                    if (data.getBillingAddressLine1() != null && !data.getBillingAddressLine1().isEmpty()) {
                        siteAddress = siteAddress + data.getBillingAddressLine1() + ", ";
                    }

                    //Address Line 2
                    if (data.getBillingAddressLine2() != null && !data.getBillingAddressLine2().isEmpty()) {
                        siteAddress = siteAddress + data.getBillingAddressLine2() + ", ";
                    }

                    //City & Zip Code
                    if (data.getBillingAddressCity() != null &&
                            !data.getBillingAddressCity().toString().trim().isEmpty() &&
                            data.getBillingAddressZipcode() != null &&
                            !data.getBillingAddressZipcode().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                data.getBillingAddressCity().toString().trim() + " - " +
                                data.getBillingAddressZipcode()
                                        .toString().trim() + "," + "\n";
                    } else if (data.getBillingAddressCity() != null &&
                            !data.getBillingAddressCity().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                data.getBillingAddressCity().toString().trim() + ", ";
                    } else if (data.getBillingAddressZipcode() != null &&
                            !data.getBillingAddressZipcode().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                data.getBillingAddressZipcode()
                                        .toString().trim() + ", ";
                    }

                    //State
                    if (data.getBillingAddressState() != null &&
                            !data.getBillingAddressState().isEmpty()) {
                        siteAddress = siteAddress + data.getBillingAddressState() + ", ";
                    }

                    //Country
                    if (data.getBillingAddressCountry() != null &&
                            !data.getBillingAddressCountry().isEmpty()) {
                        siteAddress = siteAddress + data.getBillingAddressCountry() + " ";
                    }

                    billingAddressValueTextView.setText(siteAddress + "");

                } else {
                    billingAddressLayout.setVisibility(View.GONE);
                }

            }
            if (getActivity() != null && basicDetails != null) {

                //Remarks
                if (basicDetails.getRemarks() != null && !basicDetails.getRemarks().toString().trim().isEmpty()) {
                    remarksValueTextView.setText(basicDetails.getRemarks() + "");
                    remarksLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    remarksLayout.setVisibility(View.GONE);
                }

                //Reason
                if (basicDetails.getReason() != null && !basicDetails.getReason().toString().trim().isEmpty()) {
                    reasonValueTextView.setText(basicDetails.getReason() + "");
                    reasonLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    reasonLayout.setVisibility(View.GONE);
                }


                statusValueTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

                GradientDrawable drawable = (GradientDrawable) statusValueTextView.getBackground();

                if (data.getEnsid().equals("2")) {
                    drawable.setColor(getResources().getColor(R.color.orange_order));
                    statusValueTextView.setText(basicDetails.getStatus() + "");
                    statusLayout.setVisibility(View.VISIBLE);
                } else if (data.getEnsid().equals("1")) {
                    drawable.setColor(getResources().getColor(R.color.green_order));
                    statusValueTextView.setText(basicDetails.getStatus() + "");
                    statusLayout.setVisibility(View.VISIBLE);
                } else if (data.getEnsid().equals("5")) {
                    drawable.setColor(getResources().getColor(R.color.orange_order));
                    statusValueTextView.setText(basicDetails.getStatus() + "");
                    statusLayout.setVisibility(View.VISIBLE);
                } else if (data.getEnsid().equals("3")) {
                    drawable.setColor(getResources().getColor(R.color.red_order));
                    statusValueTextView.setText(basicDetails.getStatus() + "");
                    statusLayout.setVisibility(View.VISIBLE);
                } else {
                    statusLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
