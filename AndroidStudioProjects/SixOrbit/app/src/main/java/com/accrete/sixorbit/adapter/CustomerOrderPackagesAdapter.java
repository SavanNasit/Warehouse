package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Packages;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 8/12/17.
 */

public class CustomerOrderPackagesAdapter extends RecyclerView.Adapter<CustomerOrderPackagesAdapter.MyViewHolder> {
    private Activity activity;
    private List<Packages> packagesList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private CustomerOrderPackagesAdapterListener listener;

    public CustomerOrderPackagesAdapter(Activity activity, List<Packages> packagesList, String venId,
                                        CustomerOrderPackagesAdapterListener listener) {
        this.activity = activity;
        this.packagesList = packagesList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_package_list_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final Packages packages = packagesList.get(position);

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            holder.packageIdTextView.setText(packages.getPackageId());
            holder.packageIdTextView.setTextColor(Color.BLUE);
            holder.packageIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

            holder.warehouseTextView.setText(packages.getWarehouseName());

            //Invoice
            if (packages.getInvoiceNo() != null && !packages.getInvoiceNo().isEmpty() &&
                    !packages.getInvoiceNo().equals("null")) {
                holder.invoiceNumberTextView.setText("Inv. No. " + packages.getInvoiceNo());
                holder.invoiceNumberTextView.setVisibility(View.VISIBLE);
                holder.invoiceNumberTextView.setTextColor(Color.BLUE);
                holder.invoiceNumberTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            } else {
                holder.invoiceNumberTextView.setVisibility(View.GONE);
            }
            holder.statusTextView.setText(packages.getStatusName());

            //Packages
            if (packages.getName() != null && !packages.getName().isEmpty() &&
                    !packages.getName().equals("null")) {
                holder.deliveryByTextView.setText("By: " + packages.getName());
                holder.deliveryByTextView.setVisibility(View.VISIBLE);
            } else {
                holder.deliveryByTextView.setVisibility(View.GONE);
            }

            holder.expectedDateTextView.setText("Expected Date: ");

            if (packages.getInvoiceDate() != null && !packages.getInvoiceDate().isEmpty()) {
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(packages.getInvoiceDate());
                    holder.invoiceDateTextView.setText(outputFormat.format(date).toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                holder.invoiceDateTextView.setText("");
            }

            if (packages.getToDate() != null && !packages.getToDate().isEmpty()) {
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(packages.getToDate());
                    holder.expectedDateTextView.append(outputFormat.format(date).toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                holder.expectedDateTextView.append("NA");
            }

            if (position == 0) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardview, 0, topMargin, 0, 0);
            } else {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardview, 0, topMargin, 0, 0);
            }

            //On Item Click
            //  applyClickEvents(holder, position);
            holder.packageIdTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onChallanPrint(position);
                }
            });

            holder.invoiceNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onInvoicePrint(position);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
      /*  holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onActionsRowClicked(position);
            }
        });*/


    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return packagesList.size();
    }

    public interface CustomerOrderPackagesAdapterListener {
        // void onActionsRowClicked(int position);
        void onInvoicePrint(int position);

        void onChallanPrint(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardview;
        private TextView packageIdTextView;
        private TextView warehouseTextView;
        private TextView deliveryByTextView;
        private TextView expectedDateTextView;
        private TextView invoiceNumberTextView;
        private TextView invoiceDateTextView;
        private TextView statusTextView;


        public MyViewHolder(View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.cardview);
            packageIdTextView = (TextView) view.findViewById(R.id.package_id_textView);
            invoiceDateTextView = (TextView) view.findViewById(R.id.invoice_date_textView);
            warehouseTextView = (TextView) view.findViewById(R.id.warehouse_textView);
            deliveryByTextView = (TextView) view.findViewById(R.id.delivery_by_textView);
            expectedDateTextView = (TextView) view.findViewById(R.id.expected_date_textView);
            invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);

        }
    }
}
