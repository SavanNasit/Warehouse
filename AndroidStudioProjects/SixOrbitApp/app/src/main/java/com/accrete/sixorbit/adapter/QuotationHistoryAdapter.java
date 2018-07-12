package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.QuotationHistory;

import java.util.List;

/**
 * Created by {Anshul} on 16/4/18.
 */

public class QuotationHistoryAdapter extends RecyclerView.Adapter<QuotationHistoryAdapter.MyViewHolder> {
    private Activity activity;
    private List<QuotationHistory> itemDataList;

    public QuotationHistoryAdapter(Activity activity, List<QuotationHistory> itemDataList) {
        this.activity = activity;
        this.itemDataList = itemDataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_quotation_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final QuotationHistory itemData = itemDataList.get(position);
        holder.emailSentTitle.setText(itemData.getEmailSent());
        holder.smsSentTitle.setText(itemData.getSmsSent());
        holder.updatedByTitle.setText(itemData.getUpdatedBy());
        holder.updatedOnTitle.setText(itemData.getUpdatedOn());

        if (position % 2 == 0) {
            holder.container.setBackgroundColor(activity.getResources().getColor(R.color.gray_bg));
        } else {
            holder.container.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView updatedOnTitle;
        private TextView updatedByTitle;
        private TextView smsSentTitle;
        private TextView emailSentTitle;
        private LinearLayout container;

        public MyViewHolder(View view) {
            super(view);
            container=(LinearLayout) view.findViewById(R.id.container);
            updatedOnTitle = (TextView) view.findViewById(R.id.updatedOn_title);
            updatedByTitle = (TextView) view.findViewById(R.id.updatedBy_title);
            smsSentTitle = (TextView) view.findViewById(R.id.smsSent_title);
            emailSentTitle = (TextView) view.findViewById(R.id.emailSent_title);
        }
    }
}
