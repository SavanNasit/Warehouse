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
            holder.itemsTextView.setText(receivedDetail.getItemName().toString().trim());
        }

        //Purchase Order Quantity
        if (receivedDetail.getPoQuantity() != null && !receivedDetail.getPoQuantity().isEmpty()) {
            holder.purchaseOrderQuantityTextView.setText(receivedDetail.getPoQuantity().toString().trim());
        }

        //Received Quantity
        if (receivedDetail.getReceivedQuantity() != null && !receivedDetail.getReceivedQuantity().isEmpty()) {
            holder.receiveQuantityTextView.setText(receivedDetail.getReceivedQuantity().toString().trim());
        }

    }

    @Override
    public int getItemCount() {
        return receivedDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout titlesLayout;
        private TextView itemsTextView;
        private TextView purchaseOrderQuantityTextView;
        private TextView receiveQuantityTextView;


        public MyViewHolder(View view) {
            super(view);
            titlesLayout = (LinearLayout) view.findViewById(R.id.titles_layout);
            itemsTextView = (TextView) view.findViewById(R.id.items_textView);
            purchaseOrderQuantityTextView = (TextView) view.findViewById(R.id.purchaseOrderQuantity_textView);
            receiveQuantityTextView = (TextView) view.findViewById(R.id.receiveQuantity_textView);
        }
    }
}