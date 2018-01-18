package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/14/17.
 */


public class PackedItemWithoutCheckboxAdapter extends RecyclerView.Adapter<PackedItemWithoutCheckboxAdapter.MyViewHolder> {
    private Context context;
    private PackedItemAdapterListener listener;
    private List<PackedItem> packedList = new ArrayList<>();
    ArrayList<String> packageIdList = new ArrayList<>();

    public PackedItemWithoutCheckboxAdapter(Context context, List<PackedItem> packedList, PackedItemAdapterListener listener) {
        this.context = context;
        this.packedList = packedList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_packed, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final PackedItem packed = packedList.get(position);
        holder.listRowPackedCheckbox.setVisibility(View.INVISIBLE);
        holder.listRowPackedPackageId.setText(packed.getPackageId());
        holder.listRowPackedPackageId.setPaintFlags(holder.listRowPackedPackageId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (packed.getOid() != null && !packed.getOid().isEmpty()) {
            holder.listRowPackedOrderId.setText(packed.getOid());
            holder.listRowPackedOrderId.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedOrderId.setVisibility(View.GONE);
        }

        if (packed.getInvoiceNo() != null && !packed.getInvoiceNo().isEmpty()) {
            holder.listRowPackedInvoiceNumber.setText(packed.getInvoiceNo());
            holder.listRowPackedInvoiceNumber.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedInvoiceNumber.setVisibility(View.GONE);
        }

        if (packed.getInvoiceDate() != null && !packed.getInvoiceDate().isEmpty()) {
            holder.listRowPackedInvoiceDate.setText(packed.getInvoiceNo());
            holder.listRowPackedInvoiceDate.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedInvoiceDate.setVisibility(View.GONE);
        }


        if (packed.getCustomerName() != null && !packed.getCustomerName().isEmpty()) {
            holder.listRowPackedCustomerName.setText(packed.getCustomerName());
            holder.listRowPackedCustomerName.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedCustomerName.setVisibility(View.GONE);
        }

        if (packed.getZipCode() != null && !packed.getZipCode().isEmpty()) {
            holder.listRowPackedPincode.setText(packed.getZipCode());
            holder.listRowPackedPincode.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedPincode.setVisibility(View.GONE);
        }

        if (packed.getToDate() != null && !packed.getToDate().isEmpty()) {
            holder.listRowPackedExpDod.setText(packed.getToDate());
            holder.listRowPackedExpDod.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPackedExpDod.setVisibility(View.GONE);
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
        private TextView listRowPackedPackageId;
        private TextView listRowPackedOrderId;
        private TextView listRowPackedInvoiceNumber;
        private TextView listRowPackedInvoiceDate;
        private TextView listRowPackedCustomerName;
        private TextView listRowPackedPincode;
        private TextView listRowPackedExpDod;
        private CheckBox listRowPackedCheckbox;


        public MyViewHolder(View view) {
            super(view);
            listRowPackedPackageId = (TextView) view.findViewById(R.id.list_row_packed_package_id);
            listRowPackedOrderId = (TextView) view.findViewById(R.id.list_row_packed_order_id);
            listRowPackedInvoiceNumber = (TextView) view.findViewById(R.id.list_row_packed_invoice_number);
            listRowPackedInvoiceDate = (TextView) view.findViewById(R.id.list_row_packed_invoice_date);
            listRowPackedCustomerName = (TextView) view.findViewById(R.id.list_row_packed_customer_name);
            listRowPackedPincode = (TextView) view.findViewById(R.id.list_row_packed_pincode);
            listRowPackedExpDod = (TextView) view.findViewById(R.id.list_row_packed_exp_dod);
            listRowPackedCheckbox = (CheckBox) view.findViewById(R.id.list_row_package_checkbox);
        }
    }

}
