package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.CustomerShippingAddress;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amp on 2/11/17.
 */

public class CustomerAddressAdapter extends RecyclerView.Adapter<CustomerAddressAdapter.MyViewHolder> {
    public List<CustomerShippingAddress> customerAddressList = new ArrayList<>();
    private Context mContext;

    public CustomerAddressAdapter(Context context, List<CustomerShippingAddress> customerAddressList) {
        this.mContext = context;
        this.customerAddressList = customerAddressList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_customer_address, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CustomerAddressAdapter.MyViewHolder holder, final int position) {

        CustomerShippingAddress address = customerAddressList.get(position);
        //Office Name
        if (address.getSiteName() != null && !address.getSiteName().isEmpty()) {
            holder.officeNameValueTextView.setText(address.getSiteName().toString().trim());
            holder.officeNameTextView.setText("Office Name : ");
            holder.officeNameLayout.setVisibility(View.VISIBLE);
        } /*else if ((address.getFirstName() != null && !address.getFirstName().isEmpty()) ||
                (address.getLastName() != null && !address.getLastName().isEmpty())) {
            holder.officeNameValueTextView.setText(address.getFirstName().toString().trim() + " " +
                    address.getLastName().toString().trim());
            holder.officeNameTextView.setText("Office Name : ");
            holder.officeNameLayout.setVisibility(View.VISIBLE);
        }*/ else {
            holder.officeNameLayout.setVisibility(View.GONE);
        }

        //Line 1
        if (address.getLine1() != null && !address.getLine1().isEmpty() && !address.getLine1().equals("null")) {
            holder.line1ValueTextView.setText(address.getLine1().toString().trim());
            holder.line1TextView.setText("Line 1 : ");
            holder.line1Layout.setVisibility(View.VISIBLE);
        } else {
            holder.line1Layout.setVisibility(View.GONE);
        }

        //Line 2
        if (address.getLine2() != null && !address.getLine2().isEmpty() && !address.getLine2().equals("null")) {
            holder.line2ValueTextView.setText(address.getLine2().toString().trim());
            holder.line2TextView.setText("Line 2 : ");
            holder.line2Layout.setVisibility(View.VISIBLE);
        } else {
            holder.line2Layout.setVisibility(View.GONE);
        }

        //City
        if (address.getCityName() != null && !address.getCityName().isEmpty() && !address.getCityName().equals("null")) {
            holder.cityValueTextView.setText(address.getCityName().toString().trim());
            holder.cityTextView.setText("City : ");
            holder.cityLayout.setVisibility(View.VISIBLE);
        } else {
            holder.cityLayout.setVisibility(View.GONE);
        }

        //State
        if (address.getState() != null && !address.getState().isEmpty() && !address.getState().equals("null")) {
            holder.stateValueTextView.setText(address.getState().toString().trim());
            holder.stateTextView.setText("State : ");
            holder.stateLayout.setVisibility(View.VISIBLE);
        } else {
            holder.stateLayout.setVisibility(View.GONE);
        }

        //Country
        if (address.getCountry() != null && !address.getCountry().isEmpty() && !address.getCountry().equals("null")) {
            holder.countryValueTextView.setText(address.getCountry().toString().trim());
            holder.countryTextView.setText("Country : ");
            holder.countryLayout.setVisibility(View.VISIBLE);
        } else {
            holder.countryLayout.setVisibility(View.GONE);
        }

        //Pincode
        if (address.getZipCode() != null && !address.getZipCode().isEmpty() && !address.getZipCode().equals("null")
                && !address.getZipCode().equals("0")) {
            holder.pincodeValueTextView.setText(address.getZipCode().toString().trim());
            holder.pincodeTextView.setText("ZIP code : ");
            holder.pincodeLayout.setVisibility(View.VISIBLE);
        } else {
            holder.pincodeLayout.setVisibility(View.GONE);
        }

        //Phone
        if (address.getMobile() != null && !address.getMobile().isEmpty() && !address.getMobile().equals("null")
                && !address.getMobile().equals("0")) {
            holder.phoneValueTextView.setText(address.getMobile().toString().trim());
            holder.phoneTextView.setText("Phone No. : ");
            holder.phoneLayout.setVisibility(View.VISIBLE);
        } else {
            holder.phoneLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return customerAddressList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout officeNameLayout;
        private TextView officeNameTextView;
        private TextView officeNameValueTextView;
        private LinearLayout line1Layout;
        private TextView line1TextView;
        private TextView line1ValueTextView;
        private LinearLayout line2Layout;
        private TextView line2TextView;
        private TextView line2ValueTextView;
        private LinearLayout cityLayout;
        private TextView cityTextView;
        private TextView cityValueTextView;
        private LinearLayout stateLayout;
        private TextView stateTextView;
        private TextView stateValueTextView;
        private LinearLayout countryLayout;
        private TextView countryTextView;
        private TextView countryValueTextView;
        private LinearLayout pincodeLayout;
        private TextView pincodeTextView;
        private TextView pincodeValueTextView;
        private LinearLayout phoneLayout;
        private TextView phoneTextView;
        private TextView phoneValueTextView;

        public MyViewHolder(View view) {
            super(view);
            officeNameLayout = (LinearLayout) view.findViewById(R.id.office_name_layout);
            officeNameTextView = (TextView) view.findViewById(R.id.office_name_textView);
            officeNameValueTextView = (TextView) view.findViewById(R.id.office_name_value_textView);
            line1Layout = (LinearLayout) view.findViewById(R.id.line1_layout);
            line1TextView = (TextView) view.findViewById(R.id.line1_textView);
            line1ValueTextView = (TextView) view.findViewById(R.id.line1_value_textView);
            line2Layout = (LinearLayout) view.findViewById(R.id.line2_layout);
            line2TextView = (TextView) view.findViewById(R.id.line2_textView);
            line2ValueTextView = (TextView) view.findViewById(R.id.line2_value_textView);
            cityLayout = (LinearLayout) view.findViewById(R.id.city_layout);
            cityTextView = (TextView) view.findViewById(R.id.city_textView);
            cityValueTextView = (TextView) view.findViewById(R.id.city_value_textView);
            stateLayout = (LinearLayout) view.findViewById(R.id.state_layout);
            stateTextView = (TextView) view.findViewById(R.id.state_textView);
            stateValueTextView = (TextView) view.findViewById(R.id.state_value_textView);
            countryLayout = (LinearLayout) view.findViewById(R.id.country_layout);
            countryTextView = (TextView) view.findViewById(R.id.country_textView);
            countryValueTextView = (TextView) view.findViewById(R.id.country_value_textView);
            pincodeLayout = (LinearLayout) view.findViewById(R.id.pincode_layout);
            pincodeTextView = (TextView) view.findViewById(R.id.pincode_textView);
            pincodeValueTextView = (TextView) view.findViewById(R.id.pincode_value_textView);
            phoneLayout = (LinearLayout) view.findViewById(R.id.phone_layout);
            phoneTextView = (TextView) view.findViewById(R.id.phone_textView);
            phoneValueTextView = (TextView) view.findViewById(R.id.phone_value_textView);
        }
    }
}