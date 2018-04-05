package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private String strUnit;

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
        final ConsignmentItem consignmentItem = consignmentItemList.get(position);

        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        if(consignmentItem.getUnit()==null || consignmentItem.getUnit().isEmpty()){
            consignmentItem.setUnit("PCS");
        }
        
        if(consignmentItem.getName()!=null && !consignmentItem.getName().isEmpty()) {
            holder.receiveConsignmentItemName.setText(consignmentItem.getName().trim());
        }else {
            holder.receiveConsignmentItemName.setText("Item Name : NA");

        }

        if(consignmentItem.getInternalCode()!=null && !consignmentItem.getInternalCode().isEmpty()) {
            holder.receiveConsignmentItemSkuCode.setText("SKU Code : "+consignmentItem.getInternalCode());
        }else {
            holder.receiveConsignmentItemSkuCode.setText("SKU Code : NA");
        }

        if(consignmentItem.getOrderQuantity()!=null && !consignmentItem.getOrderQuantity().isEmpty()) {
            holder.receiveConsignmentItemOrderedQty.setText("Ordered Qty : "+formatter.format(ParseDouble(consignmentItem.getOrderQuantity()))+" "+consignmentItem.getUnit());
        }else {
            holder.receiveConsignmentItemOrderedQty.setText("Ordered Qty : NA");
        }

        if(consignmentItem.getReceiveQuantity()!=null && !consignmentItem.getReceiveQuantity().isEmpty()) {
            holder.receiveConsignmentItemReceivedQty.setText("Received Qty : "+formatter.format(ParseDouble(consignmentItem.getReceiveQuantity()))+" "+consignmentItem.getUnit());
        }else {
            holder.receiveConsignmentItemReceivedQty.setText("Received Qty : NA");
        }

        if(consignmentItem.getComment()!=null && !consignmentItem.getComment().isEmpty()) {
            holder.receiveConsignmentItemComment.setText(consignmentItem.getComment());
            holder.receiveConsignmentItemCommentLayout.setVisibility(View.VISIBLE);
            holder.receiveConsignmentItemComment.setVisibility(View.VISIBLE);
        }else {
            holder.receiveConsignmentItemCommentLayout.setVisibility(View.GONE);
            holder.receiveConsignmentItemComment.setVisibility(View.GONE);
        }

        if(consignmentItem.getExpiryDate()!=null && !consignmentItem.getExpiryDate().isEmpty()) {
            holder.receiveConsignmentItemExpDate.setText(consignmentItem.getExpiryDate());
            holder.receiveConsignmentItemExpDate.setVisibility(View.VISIBLE);
        }else {
            holder.receiveConsignmentItemExpDate.setVisibility(View.GONE);
        }



        if(consignmentItem.getRejectedQuantity()!=null && !consignmentItem.getRejectedQuantity().isEmpty()) {
            holder.receiveConsignmentItemRejectedQuantity.setText(formatter.format(ParseDouble(consignmentItem.getRejectedQuantity()))+" "+consignmentItem.getUnit());
            holder.receiveConsignmentItemRejectQuantityLayout.setVisibility(View.VISIBLE);
            holder.receiveConsignmentItemRejectedQuantity.setVisibility(View.VISIBLE);
            holder.receiveConsignmentItemReasonOfRejection.setText(consignmentItem.getReasonRejection());
            holder.receiveConsignmentItemReasonOfRejection.setVisibility(View.VISIBLE);
            holder.receiveConsignmentItemReasonOfRejectionLayout.setVisibility(View.VISIBLE);

        }else {
            holder.receiveConsignmentItemRejectedQuantity.setVisibility(View.GONE);
            holder.receiveConsignmentItemRejectQuantityLayout.setVisibility(View.GONE);
            holder.receiveConsignmentItemReasonOfRejection.setVisibility(View.GONE);
            holder.receiveConsignmentItemReasonOfRejectionLayout.setVisibility(View.GONE);

        }


        // holder.priceEdittext.setText(formatter.format(ParseDouble(consignmentItem.getUnitPrice())));


     //   holder.unitEdittext.setText(consignmentItem.getUnit());

        if (type.equals("Directly")) {
           // holder.receiveConsignmentItemSkuCode.setVisibility(View.GONE);
            holder.receiveConsignmentItemOrderedQty.setVisibility(View.GONE);
            holder.receiveConsignmentItemOrderedQty.setVisibility(View.GONE);

        } else {
           // holder.receiveConsignmentItemSkuCode.setVisibility(View.VISIBLE);
            holder.receiveConsignmentItemOrderedQty.setVisibility(View.VISIBLE);
        }

        for (int i = 0; i > consignmentItem.getMeasurements().size(); i++) {
            if (consignmentItem.getMeasurements().get(i).getSelected()) {
               strUnit =consignmentItem.getMeasurements().get(i).getName();
            }
        }

        holder.removeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.removeItemAndNotify(position);
            }
        });

        holder.editLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.editItemAndOpenDialog(position,consignmentItem);
            }
        });


    }

    @Override
    public int getItemCount() {
        return consignmentItemList.size();
    }

    public interface ReceiveConsignmentItemsAdapterListener {
        void editItemAndOpenDialog(int position, ConsignmentItem consignmentItem);

        void removeItemAndNotify(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView receiveConsignmentItemName;
        private TextView receiveConsignmentItemSkuCode;
        private TextView receiveConsignmentItemExpDate;
        private TextView receiveConsignmentItemOrderedQty;
        private TextView receiveConsignmentItemReceivedQty;
        private LinearLayout receiveConsignmentItemCommentLayout;
        private TextView receiveConsignmentItemComment;
        private LinearLayout receiveConsignmentItemRejectQuantityLayout;
        private TextView receiveConsignmentItemRejectedQuantity;
        private LinearLayout receiveConsignmentItemReasonOfRejectionLayout;
        private TextView receiveConsignmentItemReasonOfRejection;
        private LinearLayout expandViewLayout;
        private LinearLayout editLayout;
        private ImageView imgEdit;
        private LinearLayout removeLayout;


        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView)view.findViewById( R.id.card_view );
            receiveConsignmentItemName = (TextView)view.findViewById( R.id.receive_consignment_item_name );
            receiveConsignmentItemSkuCode = (TextView)view.findViewById( R.id.receive_consignment_item_sku_code );
            receiveConsignmentItemExpDate = (TextView)view.findViewById( R.id.receive_consignment_item_exp_date );
            receiveConsignmentItemOrderedQty = (TextView)view.findViewById( R.id.receive_consignment_item_ordered_qty );
            receiveConsignmentItemReceivedQty = (TextView)view.findViewById( R.id.receive_consignment_item_received_qty );
            receiveConsignmentItemCommentLayout = (LinearLayout)view.findViewById( R.id.receive_consignment_item_comment_layout );
            receiveConsignmentItemComment = (TextView)view.findViewById( R.id.receive_consignment_item_comment );
            receiveConsignmentItemRejectQuantityLayout = (LinearLayout)view.findViewById( R.id.receive_consignment_item_reject_quantity_layout );
            receiveConsignmentItemRejectedQuantity = (TextView)view.findViewById( R.id.receive_consignment_item_rejected_quantity );
            receiveConsignmentItemReasonOfRejectionLayout = (LinearLayout)view.findViewById( R.id.receive_consignment_item_reason_of_rejection_layout );
            receiveConsignmentItemReasonOfRejection = (TextView)view.findViewById( R.id.receive_consignment_item_reason_of_rejection );
            expandViewLayout = (LinearLayout)view.findViewById( R.id.expand_viewLayout );
            editLayout = (LinearLayout)view.findViewById( R.id.edit_layout );
            imgEdit = (ImageView)view.findViewById( R.id.imgEdit );
            removeLayout = (LinearLayout)view.findViewById( R.id.remove_layout );

        }
    }
}

