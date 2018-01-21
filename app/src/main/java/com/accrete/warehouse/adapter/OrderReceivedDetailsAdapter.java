package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ReceivedDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 20/1/18.
 */

public class OrderReceivedDetailsAdapter extends RecyclerView.Adapter<OrderReceivedDetailsAdapter.MyViewHolder> {
    public List<ReceivedDetail> receivedDetailList = new ArrayList<>();
    private Context mContext;

    public OrderReceivedDetailsAdapter(Context context, List<ReceivedDetail> receivedDetailList) {
        this.mContext = context;
        this.receivedDetailList = receivedDetailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_order_received_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ReceivedDetail receivedDetail = receivedDetailList.get(position);

        //Name
        if (receivedDetail.getItemName() != null && !receivedDetail.getItemName().isEmpty()) {
            holder.itemNameTextView.setText("Item : ");
            holder.itemNameValueTextView.setText(receivedDetail.getItemName().toString().trim());
            holder.itemNameLayout.setVisibility(View.VISIBLE);
        } else {
            holder.itemNameLayout.setVisibility(View.GONE);
        }

        //Purchase Order Quantity
        if (receivedDetail.getPoQuantity() != null && !receivedDetail.getPoQuantity().isEmpty()) {
            holder.purchasedQuantityTextView.setText("Purchase Order Quantity : ");
            holder.purchasedQuantityValueTextView.setText(receivedDetail.getPoQuantity().toString().trim());
            holder.purchasedQuantityLayout.setVisibility(View.VISIBLE);
        } else {
            holder.purchasedQuantityLayout.setVisibility(View.GONE);
        }

        //Received Quantity
        if (receivedDetail.getReceivedQuantity() != null && !receivedDetail.getReceivedQuantity().isEmpty()) {
            holder.receivedQuantityTextView.setText("Received Quantity : ");
            holder.receivedQuantityValueTextView.setText(receivedDetail.getReceivedQuantity().toString().trim());
            holder.receivedQuantityLayout.setVisibility(View.VISIBLE);
        } else {
            holder.receivedQuantityLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return receivedDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout itemNameLayout;
        private TextView itemNameTextView;
        private TextView itemNameValueTextView;
        private LinearLayout purchasedQuantityLayout;
        private TextView purchasedQuantityTextView;
        private TextView purchasedQuantityValueTextView;
        private LinearLayout receivedQuantityLayout;
        private TextView receivedQuantityTextView;
        private TextView receivedQuantityValueTextView;

        public MyViewHolder(View view) {
            super(view);
            itemNameLayout = (LinearLayout) view.findViewById(R.id.itemName_layout);
            itemNameTextView = (TextView) view.findViewById(R.id.itemName_textView);
            itemNameValueTextView = (TextView) view.findViewById(R.id.itemName_value_textView);
            purchasedQuantityLayout = (LinearLayout) view.findViewById(R.id.purchasedQuantity_layout);
            purchasedQuantityTextView = (TextView) view.findViewById(R.id.purchasedQuantity_textView);
            purchasedQuantityValueTextView = (TextView) view.findViewById(R.id.purchasedQuantity_value_textView);
            receivedQuantityLayout = (LinearLayout) view.findViewById(R.id.receivedQuantity_layout);
            receivedQuantityTextView = (TextView) view.findViewById(R.id.receivedQuantity_textView);
            receivedQuantityValueTextView = (TextView) view.findViewById(R.id.receivedQuantity_value_textView);
        }
    }
}