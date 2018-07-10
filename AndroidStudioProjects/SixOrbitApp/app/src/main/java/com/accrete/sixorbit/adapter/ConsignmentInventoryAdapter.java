package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ConsignmentItem;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by agt on 8/12/17.
 */

public class ConsignmentInventoryAdapter extends RecyclerView.Adapter<ConsignmentInventoryAdapter.MyViewHolder> {
    private Activity activity;
    private List<ConsignmentItem> consignmentList;
    private String venId;
    private ConsignmentInventoryAdapter.ConsignmentAdapterListener listener;

    public ConsignmentInventoryAdapter(Activity activity, List<ConsignmentItem> consignmentList, String venId,
                                       ConsignmentInventoryAdapter.ConsignmentAdapterListener listener) {
        this.activity = activity;
        this.consignmentList = consignmentList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vendor_inventory_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ConsignmentItem consignment = consignmentList.get(position);

        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        //ID
        holder.inventoryIdTextView.setText(consignment.getInventoryId().toString().trim());
        holder.inventoryIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.inventoryIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        //Name
        holder.itemsTextView.setText(consignment.getItemName().toString().trim());

        //mrp
        holder.mrpTextView.setText("MRP " + formatter.format(Double.parseDouble(consignment.getMrp().toString().trim())));

        //Received Quantity
        holder.receivedTextView.setText("Received: " + consignment.getReceivedQuantity().toString().trim());

        //Selling
        holder.sellingPriceTextView.setText("Selling Price " + formatter.format
                (Double.parseDouble(consignment.getSellingPrice().toString().trim())));

        //Available
        holder.availableTextView.setText("Available: " + consignment.getStock().toString().trim());

        //On Item Click
        applyClickEvents(holder, position);
        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consignmentList.size();
    }

    public interface ConsignmentAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView inventoryIdTextView;
        private TextView itemsTextView;
        private TextView mrpTextView;
        private TextView sellingPriceTextView;
        private TextView receivedTextView;
        private TextView availableTextView;
        private LinearLayout mainLayout;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);

            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            inventoryIdTextView = (TextView) view.findViewById(R.id.inventoryId_textView);
            itemsTextView = (TextView) view.findViewById(R.id.items_textView);
            mrpTextView = (TextView) view.findViewById(R.id.mrp_textView);
            sellingPriceTextView = (TextView) view.findViewById(R.id.selling_price_textView);
            receivedTextView = (TextView) view.findViewById(R.id.received_textView);
            availableTextView = (TextView) view.findViewById(R.id.available_textView);
        }
    }
}
