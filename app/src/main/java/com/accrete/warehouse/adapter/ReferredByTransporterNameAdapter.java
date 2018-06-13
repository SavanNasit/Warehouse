package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.TransporterNameSearchDatum;

import java.util.List;

/**
 * Created by poonam on 6/8/18.
 */

public class ReferredByTransporterNameAdapter extends RecyclerView.Adapter<ReferredByTransporterNameAdapter.MyViewHolder> {
    private ReferredByTransporterNameAdapterListener listener;
    private Activity activity;
    private List<TransporterNameSearchDatum> itemDataList;

    public ReferredByTransporterNameAdapter(Activity activity, List<TransporterNameSearchDatum> itemDataList,
                                            ReferredByTransporterNameAdapterListener listener) {
        this.activity = activity;
        this.itemDataList = itemDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transporter_name_reference_by_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final TransporterNameSearchDatum customerSearchDatum = itemDataList.get(position);
        if (customerSearchDatum.getName() != null && customerSearchDatum.getName().toString().trim() != null && !customerSearchDatum.getName().toString().trim().isEmpty()) {
            holder.nameLayout.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(customerSearchDatum.getName().toString().trim());
        } else {
            holder.nameLayout.setVisibility(View.GONE);
        }


        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCustomerClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public interface ReferredByTransporterNameAdapterListener {
        void onCustomerClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout nameLayout, mainLayout;
        private TextView nameTitleTextView;
        private TextView nameTextView;
        private LinearLayout companyLayout;
        private TextView companyTitleTextView;
        private TextView companyTextView;
        private LinearLayout emailLayout;
        private TextView emailTitleTextView;
        private TextView emailTextView;
        private LinearLayout mobileLayout;
        private TextView mobileTitleTextView;
        private TextView mobileTextView;
        private LinearLayout typeLayout;
        private TextView typeTitleTextView;
        private TextView typeTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            nameTitleTextView = (TextView) view.findViewById(R.id.name_title_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            companyLayout = (LinearLayout) view.findViewById(R.id.company_layout);
            companyTitleTextView = (TextView) view.findViewById(R.id.company_title_textView);
            companyTextView = (TextView) view.findViewById(R.id.company_textView);
            emailLayout = (LinearLayout) view.findViewById(R.id.email_layout);
            emailTitleTextView = (TextView) view.findViewById(R.id.email_title_textView);
            emailTextView = (TextView) view.findViewById(R.id.email_textView);
            mobileLayout = (LinearLayout) view.findViewById(R.id.mobile_layout);
            mobileTitleTextView = (TextView) view.findViewById(R.id.mobile_title_textView);
            mobileTextView = (TextView) view.findViewById(R.id.mobile_textView);
            typeLayout = (LinearLayout) view.findViewById(R.id.type_layout);
            typeTitleTextView = (TextView) view.findViewById(R.id.type_title_textView);
            typeTextView = (TextView) view.findViewById(R.id.type_textView);

        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}