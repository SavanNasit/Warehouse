package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ConsignmentItem;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by agt on 21/1/18.
 */

public class ReceiveConsignmentItemsAdapter extends RecyclerView.Adapter<ReceiveConsignmentItemsAdapter.MyViewHolder> {
    private List<ConsignmentItem> consignmentItemList;
    private Activity activity;
    private ReceiveConsignmentItemsAdapterListener listener;
    private String type;

    public ReceiveConsignmentItemsAdapter(Activity activity, List<ConsignmentItem> consignmentItemList,
                                          ReceiveConsignmentItemsAdapterListener listener, String type) {
        this.activity = activity;
        this.consignmentItemList = consignmentItemList;
        this.listener = listener;
        this.type = type;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_receive_consignment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    //To deal with empty string of amount
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final ConsignmentItem objectItem = consignmentItemList.get(position);

        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        holder.itemAutoCompleteTextView.setText(objectItem.getName());
        holder.skuCodeEdittext.setText(objectItem.getInternalCode());
        holder.orderQuantityEdittext.setText(formatter.format(ParseDouble(objectItem.getOrderQuantity())));
        holder.receivingQuantityEdittext.setText(formatter.format(ParseDouble(objectItem.getReceiveQuantity())));
        holder.priceEdittext.setText(formatter.format(ParseDouble(objectItem.getUnitPrice())));
        holder.commentEdittext.setText(objectItem.getComment());
        holder.expiryDateEdittext.setText(objectItem.getExpiryDate());
        holder.rejectedQuantityEdittext.setText(formatter.format(ParseDouble(objectItem.getRejectedQuantity())));
        holder.rejectedReasonEdittext.setText(objectItem.getReasonRejection());
        holder.unitEdittext.setText(objectItem.getUnit());

        if (type.equals("Directly")) {
            holder.skuCodeEdittext.setVisibility(View.GONE);
            holder.orderQuantityEdittext.setVisibility(View.GONE);
        } else {
            holder.skuCodeEdittext.setVisibility(View.VISIBLE);
            holder.orderQuantityEdittext.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i > objectItem.getMeasurements().size(); i++) {
            if (objectItem.getMeasurements().get(i).getSelected()) {
                holder.unitEdittext.setText(objectItem.getMeasurements().get(i).getName());
            }
        }

        holder.imageBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeItemAndNotify(position);
            }
        });

        holder.imageBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editItemAndOpenDialog(position);
            }
        });

        holder.imageBtnEdit.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return consignmentItemList.size();
    }

    public interface ReceiveConsignmentItemsAdapterListener {
        void editItemAndOpenDialog(int position);

        void removeItemAndNotify(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageButton imageBtnClose;
        private ImageButton imageBtnEdit;
        private TextInputLayout itemTextInputLayout;
        private TextView itemAutoCompleteTextView;
        private LinearLayout codeQuantityLayout;
        private EditText skuCodeEdittext;
        private EditText orderQuantityEdittext;
        private LinearLayout quantityUnitLayout;
        private EditText receivingQuantityEdittext;
        private EditText unitEdittext;
        private LinearLayout priceCommentLayout;
        private EditText priceEdittext;
        private EditText commentEdittext;
        private LinearLayout expiryDateRejectedQuantityLayout;
        private EditText expiryDateEdittext;
        private EditText rejectedQuantityEdittext;
        private EditText rejectedReasonEdittext;

        public MyViewHolder(View view) {
            super(view);
            imageBtnClose = (ImageButton) view.findViewById(R.id.imageBtn_close);
            imageBtnEdit = (ImageButton) view.findViewById(R.id.imageBtn_edit);
            itemTextInputLayout = (TextInputLayout) view.findViewById(R.id.item_textInputLayout);
            itemAutoCompleteTextView = (TextView) view.findViewById(R.id.item_autoCompleteTextView);
            codeQuantityLayout = (LinearLayout) view.findViewById(R.id.code_quantity_layout);
            skuCodeEdittext = (EditText) view.findViewById(R.id.sku_code_edittext);
            orderQuantityEdittext = (EditText) view.findViewById(R.id.order_quantity_edittext);
            quantityUnitLayout = (LinearLayout) view.findViewById(R.id.quantity_unit_layout);
            receivingQuantityEdittext = (EditText) view.findViewById(R.id.receiving_quantity_edittext);
            unitEdittext = (EditText) view.findViewById(R.id.unit_edittext);
            priceCommentLayout = (LinearLayout) view.findViewById(R.id.price_comment_layout);
            priceEdittext = (EditText) view.findViewById(R.id.price_edittext);
            commentEdittext = (EditText) view.findViewById(R.id.comment_edittext);
            expiryDateRejectedQuantityLayout = (LinearLayout) view.findViewById(R.id.expiryDate_rejectedQuantity_layout);
            expiryDateEdittext = (EditText) view.findViewById(R.id.expiryDate_edittext);
            rejectedQuantityEdittext = (EditText) view.findViewById(R.id.rejectedQuantity_edittext);
            rejectedReasonEdittext = (EditText) view.findViewById(R.id.rejectedReason_edittext);

        }
    }
}

