package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
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
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 19/1/18.
 */

public class PackedAgainstStockAdapter extends RecyclerView.Adapter<PackedAgainstStockAdapter.MyViewHolder> {
    private Context context;
    private List<PackedItem> packedItemList;
    private PackedAgainstAdapterListener listener;

    public PackedAgainstStockAdapter(Context context, List<PackedItem> packedItemList, PackedAgainstAdapterListener listener) {
        this.context = context;
        this.packedItemList = packedItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_packed_against, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        PackedItem packedItem = packedItemList.get(position);
        holder.packageIdTextView.setText(packedItem.getPackageId());
       // holder.packageIdTextView.setPaintFlags(holder.packageIdTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        holder.invoiceNoTextView.setText("Invoice No : " + packedItem.getInvoiceNo());

        if (packedItem.getCustomerName() != null && !packedItem.getCustomerName().isEmpty()
                && packedItem.getCustomerName().toString().trim().length() != 0) {
            holder.customerNameTextView.setText(packedItem.getCustomerName());
            holder.customerNameTextView.setVisibility(View.VISIBLE);
        } else {
            holder.customerNameTextView.setVisibility(View.GONE);
        }

        holder.containerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMessageRowClicked(position);
            }
        });
        //applyClickEvents(holder, position);

        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy");
        DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date startDate = targetFormat.parse(packedItem.getInvoiceDate());
            holder.invoiceDateTextView.setText(formatter.format(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return packedItemList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PackedAgainstAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView packageIdTextView;
        private TextView invoiceDateTextView;
        private TextView customerNameTextView;
        private TextView invoiceNoTextView;
        private LinearLayout containerLayout;
        private RelativeLayout mainlayout;

        public MyViewHolder(View view) {
            super(view);
          //  mainlayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            containerLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            packageIdTextView = (TextView) view.findViewById(R.id.list_row_packed_package_id);
            invoiceDateTextView = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            customerNameTextView = (TextView) view.findViewById(R.id.list_row_running_orders_customer);
            invoiceNoTextView = (TextView) view.findViewById(R.id.list_row_packed_invoice_number);
        }
    }

}
