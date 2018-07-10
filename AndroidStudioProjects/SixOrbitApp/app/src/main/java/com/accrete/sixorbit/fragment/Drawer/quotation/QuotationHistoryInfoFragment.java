package com.accrete.sixorbit.fragment.Drawer.quotation;


import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.CustomerInfo;
import com.accrete.sixorbit.model.ExtraInfo;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuotationHistoryInfoFragment extends Fragment {

    private LinearLayout layoutOrderInfo;
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
    private LinearLayout layoutExtraInfo;
    private TextView textViewExtraInfo;
    private LinearLayout typeLayout;
    private TextView typeTextView;
    private TextView typeValueTextView;
    private LinearLayout createdByLayout;
    private TextView createdByTextView;
    private TextView createdByValueTextView;
    private LinearLayout createdOnLayout;
    private TextView createdOnTextView;
    private TextView createdOnValueTextView;
    private TextView emptyTextView;
    private CustomerInfo customerInfo;
    private ExtraInfo extraInfo;
    private CoordinatorLayout container;

    public QuotationHistoryInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quotation_history_info, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View view) {
        container = (CoordinatorLayout) view.findViewById(R.id.container);
        layoutOrderInfo = (LinearLayout) view.findViewById(R.id.layout_order_info);
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
        layoutExtraInfo = (LinearLayout) view.findViewById(R.id.layout_extra_info);
        textViewExtraInfo = (TextView) view.findViewById(R.id.textView_extra_info);
        typeLayout = (LinearLayout) view.findViewById(R.id.type_layout);
        typeTextView = (TextView) view.findViewById(R.id.type_textView);
        typeValueTextView = (TextView) view.findViewById(R.id.type_value_textView);
        createdByLayout = (LinearLayout) view.findViewById(R.id.createdBy_layout);
        createdByTextView = (TextView) view.findViewById(R.id.createdBy_textView);
        createdByValueTextView = (TextView) view.findViewById(R.id.createdBy_value_textView);
        createdOnLayout = (LinearLayout) view.findViewById(R.id.createdOn_layout);
        createdOnTextView = (TextView) view.findViewById(R.id.createdOn_textView);
        createdOnValueTextView = (TextView) view.findViewById(R.id.createdOn_value_textView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);

        container.setVisibility(View.GONE);
        emptyTextView.setVisibility(View.VISIBLE);
    }

    public void getCustomerExtraInfo(CustomerInfo customerInfo, ExtraInfo extraInfo) {
        this.customerInfo = customerInfo;
        this.extraInfo = extraInfo;

        setData();
    }

    private void setData() {
        if (customerInfo != null) {

            //Name
            if (customerInfo.getName() != null && !customerInfo.getName().isEmpty()) {
                nameValueTextView.setText(customerInfo.getName() + "");
                nameLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                nameLayout.setVisibility(View.GONE);
            }

            //Mobile
            if (customerInfo.getMobile() != null && !customerInfo.getMobile().isEmpty()) {
                mobileValueTextView.setText(customerInfo.getMobile() + "");
                mobileLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                mobileLayout.setVisibility(View.GONE);
            }

            //Email
            if (customerInfo.getEmail() != null && !customerInfo.getEmail().isEmpty()) {
                emailValueTextView.setText(customerInfo.getEmail() + "");
                emailLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                emailLayout.setVisibility(View.GONE);
            }
        }

        if (extraInfo != null) {

            //Type
            if (extraInfo.getType() != null && !extraInfo.getType().isEmpty()) {
                typeValueTextView.setText(extraInfo.getType() + "");
                typeLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                typeLayout.setVisibility(View.GONE);
            }

            //Created By
            if (extraInfo.getCreatedBy() != null && !extraInfo.getCreatedBy().isEmpty()) {
                createdByValueTextView.setText(extraInfo.getCreatedBy() + "");
                createdByLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                createdByLayout.setVisibility(View.GONE);
            }

            //Created On
            if (extraInfo.getCreatedOn() != null && !extraInfo.getCreatedOn().isEmpty()) {
                /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
                    Date date = simpleDateFormat.parse(extraInfo.getCreatedOn());
                    createdOnValueTextView.setText(outputFormat.format(date).toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
                createdOnValueTextView.setText(extraInfo.getCreatedOn() + "");
                createdOnLayout.setVisibility(View.VISIBLE);
                container.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
            } else {
                createdOnLayout.setVisibility(View.GONE);
            }
        }
    }

}
