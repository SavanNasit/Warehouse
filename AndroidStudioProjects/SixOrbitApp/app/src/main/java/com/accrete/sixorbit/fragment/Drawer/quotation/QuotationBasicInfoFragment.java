package com.accrete.sixorbit.fragment.Drawer.quotation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.QuotationDetails;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationBasicInfoFragment extends Fragment {
    private LinearLayout containerLayout;
    private LinearLayout layoutCustomerInfo;
    private TextView textViewCustomerDetails;
    private LinearLayout customerNameLayout;
    private TextView customerNameTextView;
    private TextView customerNameValueTextView;
    private LinearLayout customerEmailLayout;
    private TextView customerEmailTextView;
    private TextView customerEmailValueTextView;
    private LinearLayout customerMobileLayout;
    private TextView customerMobileTextView;
    private TextView customerMobileValueTextView;
    private LinearLayout customerCurrentAddressLayout;
    private TextView customerCurrentAddressTextView;
    private TextView customerCurrentAddressValueTextView;
    private LinearLayout customerSiteAddressLayout;
    private TextView customerSiteAddressTextView;
    private TextView customerSiteAddressValueTextView;
    private LinearLayout remarksLayout;
    private TextView remarksTextView;
    private TextView remarksValueTextView;
    private QuotationDetails quotationDetails;
    private LinearLayout statusLayout;
    private TextView statusTextView;
    private TextView statusValueTextView;
    private LinearLayout createdDateLayout;
    private TextView createdDateTextView;
    private TextView createdDateValueTextView;
    private LinearLayout createdByLayout;
    private TextView createdByTextView;
    private TextView createdByValueTextView;
    private LinearLayout updatedByLayout;
    private TextView updatedByTextView;
    private TextView updatedByValueTextView;

    public QuotationBasicInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_quotation_basic_info, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(final View rootView) {
        containerLayout = (LinearLayout) rootView.findViewById(R.id.container_layout);
        layoutCustomerInfo = (LinearLayout) rootView.findViewById(R.id.layout_customer_info);
        textViewCustomerDetails = (TextView) rootView.findViewById(R.id.textView_customer_details);
        customerNameLayout = (LinearLayout) rootView.findViewById(R.id.customer_name_layout);
        customerNameTextView = (TextView) rootView.findViewById(R.id.customer_name_textView);
        customerNameValueTextView = (TextView) rootView.findViewById(R.id.customer_name_value_textView);
        customerEmailLayout = (LinearLayout) rootView.findViewById(R.id.customer_email_layout);
        customerEmailTextView = (TextView) rootView.findViewById(R.id.customer_email_textView);
        customerEmailValueTextView = (TextView) rootView.findViewById(R.id.customer_email_value_textView);
        customerMobileLayout = (LinearLayout) rootView.findViewById(R.id.customer_mobile_layout);
        customerMobileTextView = (TextView) rootView.findViewById(R.id.customer_mobile_textView);
        customerMobileValueTextView = (TextView) rootView.findViewById(R.id.customer_mobile_value_textView);
        customerCurrentAddressLayout = (LinearLayout) rootView.findViewById(R.id.customer_current_address_layout);
        customerCurrentAddressTextView = (TextView) rootView.findViewById(R.id.customer_current_address_textView);
        customerCurrentAddressValueTextView = (TextView) rootView.findViewById(R.id.customer_current_address_value_textView);
        customerSiteAddressLayout = (LinearLayout) rootView.findViewById(R.id.customer_site_address_layout);
        customerSiteAddressTextView = (TextView) rootView.findViewById(R.id.customer_site_address_textView);
        customerSiteAddressValueTextView = (TextView) rootView.findViewById(R.id.customer_Site_address_value_textView);
        remarksLayout = (LinearLayout) rootView.findViewById(R.id.remarks_layout);
        remarksTextView = (TextView) rootView.findViewById(R.id.remarks_textView);
        remarksValueTextView = (TextView) rootView.findViewById(R.id.remarks_value_textView);
        statusLayout = (LinearLayout) rootView.findViewById(R.id.status_layout);
        statusTextView = (TextView) rootView.findViewById(R.id.status_textView);
        statusValueTextView = (TextView) rootView.findViewById(R.id.status_value_textView);
        createdDateLayout = (LinearLayout) rootView.findViewById(R.id.created_date_layout);
        createdDateTextView = (TextView) rootView.findViewById(R.id.created_date_textView);
        createdDateValueTextView = (TextView) rootView.findViewById(R.id.created_date_value_textView);
        createdByLayout = (LinearLayout) rootView.findViewById(R.id.created_by_layout);
        createdByTextView = (TextView) rootView.findViewById(R.id.created_by_textView);
        createdByValueTextView = (TextView) rootView.findViewById(R.id.created_by_value_textView);
        updatedByLayout = (LinearLayout) rootView.findViewById(R.id.updated_by_layout);
        updatedByTextView = (TextView) rootView.findViewById(R.id.updated_by_textView);
        updatedByValueTextView = (TextView) rootView.findViewById(R.id.updated_by_value_textView);

        containerLayout.setVisibility(View.GONE);
    }

    public void getCustomerInfo(QuotationDetails quotationDetails) {
        try {
            this.quotationDetails = quotationDetails;
            if (getActivity() != null) {
                setData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {
        try {
            if (getActivity() != null && quotationDetails != null) {

                //Name
                if (quotationDetails.getName() != null && !quotationDetails.getName().isEmpty()) {
                    customerNameValueTextView.setText(quotationDetails.getName() + "");
                    customerNameLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerNameLayout.setVisibility(View.GONE);
                }

                //Mobile
                if (quotationDetails.getMobile() != null && !quotationDetails.getMobile().isEmpty()) {
                    customerMobileValueTextView.setText(quotationDetails.getMobile() + "");
                    customerMobileLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerMobileLayout.setVisibility(View.GONE);
                }

                //Email
                if (quotationDetails.getEmail() != null && !quotationDetails.getEmail().isEmpty()) {
                    customerEmailValueTextView.setText(quotationDetails.getEmail() + "");
                    customerEmailLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    customerEmailLayout.setVisibility(View.GONE);
                }

                //Status
                if (quotationDetails.getQosId() != null && !quotationDetails.getQosId().isEmpty()) {
                    if (quotationDetails.getQosId() != null && quotationDetails.getQosId().equals("1")) {
                        statusValueTextView.setText("Converted");
                        statusValueTextView.setTextColor(getResources().getColor(R.color.green));
                    } else if (quotationDetails.getQosId() != null && quotationDetails.getQosId().equals("2")) {
                        statusValueTextView.setText("Pending");
                        statusValueTextView.setTextColor(getResources().getColor(R.color.orange));
                    } else if (quotationDetails.getQosId() != null && quotationDetails.getQosId().equals("3")) {
                        statusValueTextView.setText("Cancelled");
                        statusValueTextView.setTextColor(getResources().getColor(R.color.red));
                    } else if (quotationDetails.getQosId() != null && quotationDetails.getQosId().equals("4")) {
                        statusValueTextView.setText("To Be Approved");
                        statusValueTextView.setTextColor(getResources().getColor(R.color.blueTurquoise));
                    }

                    statusLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    statusLayout.setVisibility(View.GONE);
                }

                //Remark
                if (quotationDetails.getQuotationRemark() != null && !quotationDetails.getQuotationRemark().isEmpty()
                        && !quotationDetails.getQuotationRemark().toLowerCase().equals("null")) {
                    remarksValueTextView.setText(quotationDetails.getQuotationRemark() + "");
                    remarksLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    remarksLayout.setVisibility(View.GONE);
                }

                //Created By
                if (quotationDetails.getCreatedBy() != null && !quotationDetails.getCreatedBy().isEmpty()
                        && !quotationDetails.getCreatedBy().toLowerCase().equals("null")) {
                    createdByValueTextView.setText(quotationDetails.getCreatedBy() + "");
                    createdByLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    createdByLayout.setVisibility(View.GONE);
                }

                //Updated By
                if (quotationDetails.getUpdatedBy() != null && !quotationDetails.getUpdatedBy().isEmpty()
                        && !quotationDetails.getUpdatedBy().toLowerCase().equals("null")) {
                    updatedByValueTextView.setText(quotationDetails.getUpdatedBy() + "");
                    updatedByLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);
                } else {
                    updatedByLayout.setVisibility(View.GONE);
                }

                //Current Address
                if ((quotationDetails.getShippingAddressLine1() != null && !quotationDetails.getShippingAddressLine1().isEmpty())
                        || (quotationDetails.getShippingAddressLine2() != null && !quotationDetails.getShippingAddressLine2().isEmpty())
                        || (quotationDetails.getShippingAddressCity() != null && !quotationDetails.getShippingAddressCity().isEmpty())
                        || (quotationDetails.getShippingAddressZipcode() != null && !quotationDetails.getShippingAddressZipcode().isEmpty())
                        || (quotationDetails.getShippingAddressCountry() != null && !quotationDetails.getShippingAddressCountry().isEmpty())) {
                    customerCurrentAddressLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);

                    String currentAddress = "";

                    //Address Line 1
                    if (quotationDetails.getShippingAddressLine1() != null && !quotationDetails.getShippingAddressLine1().isEmpty()) {
                        currentAddress = currentAddress + quotationDetails.getShippingAddressLine1() + ", ";
                    }

                    //Address Line 2
                    if (quotationDetails.getShippingAddressLine2() != null && !quotationDetails.getShippingAddressLine2().isEmpty()) {
                        currentAddress = currentAddress + quotationDetails.getShippingAddressLine2() + ", ";
                    }

                    //City & Zip Code
                    if (quotationDetails.getShippingAddressCity() != null &&
                            !quotationDetails.getShippingAddressCity().toString().trim().isEmpty() &&
                            quotationDetails.getShippingAddressZipcode() != null &&
                            !quotationDetails.getShippingAddressZipcode().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                quotationDetails.getShippingAddressCity().toString().trim() + " - " +
                                quotationDetails.getShippingAddressZipcode()
                                        .toString().trim() + "," + "\n";
                    } else if (quotationDetails.getShippingAddressCity() != null &&
                            !quotationDetails.getShippingAddressCity().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                quotationDetails.getShippingAddressCity().toString().trim() + ", ";
                    } else if (quotationDetails.getShippingAddressZipcode() != null &&
                            !quotationDetails.getShippingAddressZipcode().toString().trim().isEmpty()) {
                        currentAddress = currentAddress +
                                quotationDetails.getShippingAddressZipcode()
                                        .toString().trim() + ", ";
                    }
                    //State
                    if (quotationDetails.getShippingAddressState() != null &&
                            !quotationDetails.getShippingAddressState().isEmpty()) {
                        currentAddress = currentAddress + quotationDetails.getShippingAddressState() + ", ";
                    }

                    //Country
                    if (quotationDetails.getShippingAddressCountry() != null &&
                            !quotationDetails.getShippingAddressCountry().isEmpty()) {
                        currentAddress = currentAddress + quotationDetails.getShippingAddressCountry() + " ";
                    }

                    customerCurrentAddressValueTextView.setText(currentAddress + "");
                } else {
                    customerCurrentAddressLayout.setVisibility(View.GONE);
                }

                //Site Address
                if ((quotationDetails.getBillingAddressLine1() != null && !quotationDetails.getBillingAddressLine1().isEmpty())
                        || (quotationDetails.getBillingAddressLine2() != null && !quotationDetails.getBillingAddressLine2().isEmpty())
                        || (quotationDetails.getBillingAddressCity() != null && !quotationDetails.getBillingAddressCity().isEmpty())
                        || (quotationDetails.getBillingAddressZipcode() != null && !quotationDetails.getBillingAddressZipcode().isEmpty())
                        || (quotationDetails.getBillingAddressCountry() != null && !quotationDetails.getBillingAddressCountry().isEmpty())) {
                    customerSiteAddressLayout.setVisibility(View.VISIBLE);
                    containerLayout.setVisibility(View.VISIBLE);

                    String siteAddress = "";

                    //Address Line 1
                    if (quotationDetails.getBillingAddressLine1() != null && !quotationDetails.getBillingAddressLine1().isEmpty()) {
                        siteAddress = siteAddress + quotationDetails.getBillingAddressLine1() + ", ";
                    }

                    //Address Line 2
                    if (quotationDetails.getBillingAddressLine2() != null && !quotationDetails.getBillingAddressLine2().isEmpty()) {
                        siteAddress = siteAddress + quotationDetails.getBillingAddressLine2() + ", ";
                    }

                    //City & Zip Code
                    if (quotationDetails.getBillingAddressCity() != null &&
                            !quotationDetails.getBillingAddressCity().toString().trim().isEmpty() &&
                            quotationDetails.getBillingAddressZipcode() != null &&
                            !quotationDetails.getBillingAddressZipcode().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                quotationDetails.getBillingAddressCity().toString().trim() + " - " +
                                quotationDetails.getBillingAddressZipcode()
                                        .toString().trim() + "," + "\n";
                    } else if (quotationDetails.getBillingAddressCity() != null &&
                            !quotationDetails.getBillingAddressCity().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                quotationDetails.getBillingAddressCity().toString().trim() + ", ";
                    } else if (quotationDetails.getBillingAddressZipcode() != null &&
                            !quotationDetails.getBillingAddressZipcode().toString().trim().isEmpty()) {
                        siteAddress = siteAddress +
                                quotationDetails.getBillingAddressZipcode()
                                        .toString().trim() + ", ";
                    }

                    //State
                    if (quotationDetails.getBillingAddressState() != null &&
                            !quotationDetails.getBillingAddressState().isEmpty()) {
                        siteAddress = siteAddress + quotationDetails.getBillingAddressState() + ", ";
                    }

                    //Country
                    if (quotationDetails.getBillingAddressCountry() != null &&
                            !quotationDetails.getBillingAddressCountry().isEmpty()) {
                        siteAddress = siteAddress + quotationDetails.getBillingAddressCountry() + " ";
                    }

                    customerSiteAddressValueTextView.setText(siteAddress + "");

                } else {
                    customerSiteAddressLayout.setVisibility(View.GONE);
                }


                //Created Time
                if (quotationDetails.getCreatedTs() != null && !quotationDetails.getCreatedTs().isEmpty()
                        && !quotationDetails.getCreatedTs().toLowerCase().equals("null")) {

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        DateFormat outputFormat = new SimpleDateFormat("hh:mm aa, dd MMM, yyyy");
                        Date date = simpleDateFormat.parse(quotationDetails.getCreatedTs());
                        createdDateValueTextView.setText("" + outputFormat.format(date).toString().trim());
                        createdDateLayout.setVisibility(View.VISIBLE);
                        containerLayout.setVisibility(View.VISIBLE);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        createdDateLayout.setVisibility(View.GONE);
                    }

                } else {
                    createdDateLayout.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
