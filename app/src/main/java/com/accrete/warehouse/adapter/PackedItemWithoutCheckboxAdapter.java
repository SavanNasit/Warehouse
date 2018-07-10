package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackedItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 12/14/17.
 */


public class PackedItemWithoutCheckboxAdapter extends RecyclerView.Adapter<PackedItemWithoutCheckboxAdapter.MyViewHolder> {
    ArrayList<String> packageIdList = new ArrayList<>();
    private Context context;
    private PackedItemAdapterListener listener;
    private List<PackedItem> packedList = new ArrayList<>();

    public PackedItemWithoutCheckboxAdapter(Context context, List<PackedItem> packedList, PackedItemAdapterListener listener) {
        this.context = context;
        this.packedList = packedList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_out_for_delivery, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PackedItem packed = packedList.get(position);
        holder.outForDeliveryPackageId.setText(packed.getPackageId());
      //  holder.outForDeliveryPackageId.setPaintFlags(holder.outForDeliveryPackageId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.outForDeliveryInvoiceNo.setText("Invoice No: " + packed.getInvoiceNo());
        if (packed.getCustomerName() != null && !packed.getCustomerName().trim().isEmpty()) {
            holder.outForDeliveryCustomerName.setText(packed.getCustomerName().trim());
            holder.outForDeliveryCustomerName.setVisibility(View.VISIBLE);
        } else {
            holder.outForDeliveryCustomerName.setVisibility(View.GONE);
        }
        //TODO NO GatePass ID in this process
        holder.outForDeliveryGatepassId.setVisibility(View.GONE);

        holder.outForDeliveryOrderId.setText("Order ID: " + packed.getOrderID());
        if (packed.getToDate() != null && !packed.getToDate().isEmpty()) {
            holder.outForDeliveryExpDod.setText("Exp DOD: " + packed.getToDate());
            holder.outForDeliveryExpDod.setVisibility(View.VISIBLE);
        } else {
            holder.outForDeliveryExpDod.setVisibility(View.GONE);
        }

        if (packed.getOrderPaymentTypeText() != null && !packed.getOrderPaymentTypeText().trim().isEmpty()) {
            holder.outForDeliveryUser.setText("Payment Type: " + packed.getOrderPaymentTypeText().trim());
            holder.outForDeliveryUser.setVisibility(View.VISIBLE);
        } else {
            holder.outForDeliveryUser.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = targetFormat.parse(packed.getInvoiceDate());
            holder.outForDeliveryInvoiceDate.setText(formatter.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Status
        if (packed.getPaymentStatus() != null && !packed.getPaymentStatus().isEmpty()) {
            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
            if (packed.getPaymentStatus().contains("Partially")) {
                holder.statusTextView.setText(packed.getPaymentStatus());
                drawable.setColor(context.getResources().getColor(R.color.orange_purchase_order));
            } else if (packed.getPaymentStatus().contains("Invoice")) {
                holder.statusTextView.setText(packed.getPaymentStatus());
                drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
            } else if (packed.getPaymentStatus().contains("Pending")) {
                holder.statusTextView.setText("Payment " + packed.getPaymentStatus());
                drawable.setColor(context.getResources().getColor(R.color.red_purchase_order));
            } else {
                holder.statusTextView.setText(packed.getPaymentStatus());
                drawable.setColor(context.getResources().getColor(R.color.red_purchase_order));
            }

            holder.statusTextView.setVisibility(View.VISIBLE);
        } else {
            holder.statusTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return packedList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position, ArrayList<String> packageIdList) {
        listener.onMessageRowClicked(position);
        listener.onExecute(packageIdList);
    }

    public interface PackedItemAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute(ArrayList<String> packageIdList);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView outForDeliveryPackageId;
        private TextView outForDeliveryInvoiceNo;
        private TextView outForDeliveryInvoiceDate;
        private TextView outForDeliveryCustomerName;
        private TextView outForDeliveryGatepassId;
        private TextView outForDeliveryOrderId;
        private TextView outForDeliveryExpDod;
        private TextView outForDeliveryUser;
        private LinearLayout outForDeliveryContainer;
        private TextView statusTextView;
        private RelativeLayout mainLayout;

        public MyViewHolder(View view) {
            super(view);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            outForDeliveryPackageId = (TextView) view.findViewById(R.id.out_for_delivery_package_id);
            outForDeliveryInvoiceNo = (TextView) view.findViewById(R.id.out_for_delivery_invoice_no);
            outForDeliveryInvoiceDate = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            outForDeliveryCustomerName = (TextView) view.findViewById(R.id.out_for_delivery_customer_name);
            outForDeliveryGatepassId = (TextView) view.findViewById(R.id.out_for_delivery_gatepass_id);
            outForDeliveryOrderId = (TextView) view.findViewById(R.id.out_for_delivery_order_id);
            outForDeliveryExpDod = (TextView) view.findViewById(R.id.out_for_delivery_exp_dod);
            outForDeliveryUser = (TextView) view.findViewById(R.id.out_for_delivery_user);
            //outForDeliveryContainer = (LinearLayout) view.findViewById(R.id.out_for_delivery_container);
            mainLayout = (RelativeLayout) view.findViewById(R.id.relativelayout_container);
        }
    }


}
