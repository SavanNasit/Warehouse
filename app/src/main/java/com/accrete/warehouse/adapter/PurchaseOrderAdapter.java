package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PurchaseOrder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 12/18/17.
 */

public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.MyViewHolder> {
    private Activity activity;
    private List<PurchaseOrder> purchaseOrderList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private PurchaseOrderAdapterListener listener;

    public PurchaseOrderAdapter(Activity activity, List<PurchaseOrder> purchaseOrderList,
                                PurchaseOrderAdapterListener listener) {
        this.activity = activity;
        this.purchaseOrderList = purchaseOrderList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_receive_against_purchase_order, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PurchaseOrder purchaseOrder = purchaseOrderList.get(position);

        holder.orderIdTextView.setText(purchaseOrder.getPurchaseOrderId());
        holder.orderIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.orderIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        holder.wareHouseTextView.setText(purchaseOrder.getWarehouseName());
        holder.wareHouseTextView.setVisibility(View.INVISIBLE);

        double amount = Double.parseDouble(purchaseOrder.getAmount());
        double tax = Double.parseDouble(purchaseOrder.getTax());
        double amountAfterTax = Double.parseDouble(purchaseOrder.getAmountAfterTax());
        double payableAmount = Double.parseDouble(purchaseOrder.getPayableAmount());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        holder.amountTextView.setText("Amount\n" + activity.getString(R.string.Rs) + " " + formatter.format(amount));
        holder.taxTextView.setText("Tax\n" + activity.getString(R.string.Rs) + " " + formatter.format(tax));
        holder.amountTaxTextView.setText("After Tax\n" + activity.getString(R.string.Rs) + " " + formatter.format(amountAfterTax));
        holder.payableAmountTextView.setText("Payable Amount " + activity.getString(R.string.Rs) + " " + formatter.format(payableAmount));

        holder.createdByTextView.setText("By: " + purchaseOrder.getCreatedBy());


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            DateFormat outputFormat = new SimpleDateFormat("hh:mm a - dd MMM, yyyy");
            Date date = simpleDateFormat.parse(purchaseOrder.getCreatedTs());
            holder.dateTextView.setText("" + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

        if (purchaseOrder.getPurorsid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            holder.statusTextView.setText("Created");
        } else if (purchaseOrder.getPurorsid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Partial Received");
        } else if (purchaseOrder.getPurorsid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Received");
        } else if (purchaseOrder.getPurorsid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Cancelled");
        } else if (purchaseOrder.getPurorsid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Closed");
        } else if (purchaseOrder.getPurorsid().equals("6")) {
            drawable.setColor(activity.getResources().getColor(R.color.orange_purchase_order));
            holder.statusTextView.setText("Pending");
        } else if (purchaseOrder.getPurorsid().equals("7")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Expected Delivery");
        } else if (purchaseOrder.getPurorsid().equals("8")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Pending Transportation");
        }

        //Click Item
        applyClickEvents(holder, position, purchaseOrder.getOrderId(), purchaseOrder.getPurchaseOrderId());
    }

    @Override
    public int getItemCount() {
        return purchaseOrderList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final String orderId, final String orderText) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, orderId, orderText);
            }
        });
    }

    public interface PurchaseOrderAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mainLayout;
        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView statusTextView, amountTextView, wareHouseTextView;
        private TextView payableAmountTextView;
        private TextView createdByTextView, taxTextView, amountTaxTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            orderIdTextView = (TextView) view.findViewById(R.id.orderId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            wareHouseTextView = (TextView) view.findViewById(R.id.wareHouse_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            createdByTextView = (TextView) view.findViewById(R.id.createdBy_textView);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            amountTaxTextView = (TextView) view.findViewById(R.id.amount_tax_textView);
        }
    }
}