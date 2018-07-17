package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.ContactDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 10/11/17.
 */

public class CustomerContactDetailAdapter extends RecyclerView.Adapter<CustomerContactDetailAdapter.MyViewHolder> {
    public List<ContactDetail> contactDetailList = new ArrayList<>();
    String customerAddress;
    private Context mContext;

    public CustomerContactDetailAdapter(Context context, List<ContactDetail> contactDetailList) {
        this.mContext = context;
        this.contactDetailList = contactDetailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_vendor_contact_detail_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ContactDetail contactDetail = contactDetailList.get(position);

        //Name
        if (contactDetail.getName() != null && !contactDetail.getName().isEmpty()) {
            holder.nameTextView.setText("Name : ");
            holder.nameValueTextView.setText(contactDetail.getName().toString().trim());
            holder.nameLayout.setVisibility(View.VISIBLE);
        } else {
            holder.nameLayout.setVisibility(View.GONE);
        }

        //Designation
        if (contactDetail.getDesignation() != null && !contactDetail.getDesignation().isEmpty()) {
            holder.designationTextView.setText("Designation : ");
            holder.designationValueTextView.setText(contactDetail.getDesignation().toString().trim());
            holder.designationLayout.setVisibility(View.VISIBLE);
        } else {
            holder.designationLayout.setVisibility(View.GONE);
        }

        //Phone
        if (contactDetail.getPhoneNo() != null && !contactDetail.getPhoneNo().isEmpty()) {
            holder.phoneTextView.setText("Phone : ");
            holder.phoneValueTextView.setText(contactDetail.getPhoneNo().toString().trim());
            holder.phoneLayout.setVisibility(View.VISIBLE);
        } else {
            holder.phoneLayout.setVisibility(View.GONE);
        }

        //Email
        if (contactDetail.getEmail() != null && !contactDetail.getEmail().isEmpty()) {
            holder.emailTextView.setText("Email : ");
            holder.emailValueTextView.setText(contactDetail.getEmail().toString().trim());
            holder.emailLayout.setVisibility(View.VISIBLE);
        } else {
            holder.emailLayout.setVisibility(View.GONE);
        }

        //Person Type
        if (contactDetail.getContactTypeValue() != null && !contactDetail.getContactTypeValue().isEmpty()) {
            holder.contactPersonTypeTextView.setText("Contact Person Type : ");
            holder.contactPersonTypeValueTextView.setText(contactDetail.getContactTypeValue().toString().trim());
            holder.contactPersonTypeLayout.setVisibility(View.VISIBLE);
        } else {
            holder.contactPersonTypeLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return contactDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout nameLayout;
        private TextView nameTextView;
        private TextView nameValueTextView;
        private LinearLayout designationLayout;
        private TextView designationTextView;
        private TextView designationValueTextView;
        private LinearLayout phoneLayout;
        private TextView phoneTextView;
        private TextView phoneValueTextView;
        private LinearLayout emailLayout;
        private TextView emailTextView;
        private TextView emailValueTextView;
        private LinearLayout contactPersonTypeLayout;
        private TextView contactPersonTypeTextView;
        private TextView contactPersonTypeValueTextView;

        public MyViewHolder(View view) {
            super(view);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            nameValueTextView = (TextView) view.findViewById(R.id.name_value_textView);
            designationLayout = (LinearLayout) view.findViewById(R.id.designation_layout);
            designationTextView = (TextView) view.findViewById(R.id.designation_textView);
            designationValueTextView = (TextView) view.findViewById(R.id.designation_value_textView);
            phoneLayout = (LinearLayout) view.findViewById(R.id.phone_layout);
            phoneTextView = (TextView) view.findViewById(R.id.phone_textView);
            phoneValueTextView = (TextView) view.findViewById(R.id.phone_value_textView);
            emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
            emailTextView = (TextView) view.findViewById(R.id.email_textView);
            emailValueTextView = (TextView) view.findViewById(R.id.email_value_textView);
            contactPersonTypeLayout = (LinearLayout) view.findViewById(R.id.contact_person_type_layout);
            contactPersonTypeTextView = (TextView) view.findViewById(R.id.contact_person_type_textView);
            contactPersonTypeValueTextView = (TextView) view.findViewById(R.id.contact_person_type_value_textView);
        }
    }
}