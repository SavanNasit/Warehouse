package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.OrderData;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import static com.accrete.warehouse.utils.AppPreferences.roundTwoDecimals;

/**
 * Created by poonam on 11/28/17.
 */

public class RunningOrderExecuteAdapter extends RecyclerView.Adapter<RunningOrderExecuteAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private List<OrderData> orderData;
    private int mExpandedPosition = -1;
    private PendingItemsAdapterListener listener;
    private int qty, posToUpdate;
    private boolean flagScan;

    public RunningOrderExecuteAdapter(Context context, List<OrderData> orderDataList, PendingItemsAdapterListener listener, int quantity, int posToUpdate, boolean flagScan) {
        this.context = context;
        this.orderData = orderDataList;
        this.listener = listener;
        this.qty = quantity;
        this.posToUpdate = posToUpdate;
        this.flagScan = flagScan;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final OrderData orderDataList = orderData.get(position);
        holder.listRowPendingItemsBatchNumber.setText(orderDataList.getIsid());
        holder.listRowPendingItemsItem.setText(orderDataList.getItemVariationName());
        if (orderDataList.getMeaid() != null && !orderDataList.getMeaid().isEmpty()) {
            holder.listRowPendingItemsQuantity.setText(orderDataList.getItemQuantity());
        } else {
            holder.listRowPendingItemsQuantity.setText(orderDataList.getItemQuantity());
        }
        holder.listRowOrderItemPurchasedOn.setText(orderDataList.getExecuteItemData().getPurchasedOn());
        holder.listRowOrderItemRemarks.setText(orderDataList.getExecuteItemData().getRemark());
        holder.listRowOrderItemInventory.setText(orderDataList.getExecuteItemData().getInventory());
        if (orderDataList.getImage() != null && !orderDataList.getImage().isEmpty()) {
            holder.listRowOrderItemImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(orderDataList.getImage())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.listRowOrderItemImage);
        } else {
            holder.listRowOrderItemImage.setVisibility(View.GONE);
        }

        if (flagScan && position == posToUpdate) {
            holder.listRowPendingItemsQuantityUsed.setText(orderData.get(posToUpdate).getUsedQuantity());
        } else {
            holder.listRowPendingItemsQuantityUsed.setText(orderDataList.getUsedQuantity());
        }

        //  holder.listRowPendingItemsStatus.setText(pendingItem.getItemStatus());
        holder.listRowPendingItemsQuantityUnit.setText(orderDataList.getItemUnit());

      /*  if (holder.listRowPendingItemsSkuCode!=null && orderDataList.getItemSkuCode()!= null && !orderDataList.getItemSkuCode().isEmpty()) {
            holder.listRowPendingItemsSkuCode.setText(orderDataList.getItemSkuCode());
            holder.listRowOrderItemViewOne.setVisibility(View.VISIBLE);
        } else {
            holder.listRowPendingItemsSkuCode.setVisibility(View.GONE);
            holder.listRowOrderItemViewOne.setVisibility(View.GONE);
        }
*/


  /*      if (pendingItem.getItemStatus().equals("Executed")) {
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.GRAY);
        } else {
            holder.listRowPendingItemsStatus.setBackgroundColor(Color.parseColor("#3C9971"));
        }*/


        holder.listRowOrderItemAddQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderDataList.getItemStatus().equals("Executed")) {
                    Toast.makeText(context, "Item already executed", Toast.LENGTH_SHORT).show();
                } else {

                    if (orderDataList.getExecuteItemData() != null && !orderDataList.getExecuteItemData().equals("")) {
                        listener.onExecute(orderDataList, position);
                    } else {
                        Toast.makeText(context, "No inventory found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


        holder.listRowOrderItemAddAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderDataList.getItemStatus().equals("Executed")) {
                    Toast.makeText(context, "Item already executed", Toast.LENGTH_SHORT).show();
                } else if (holder.listRowOrderItemAddAll.getText().toString().equals("Remove All")) {
                    //Toast.makeText(context, "Item already added", Toast.LENGTH_SHORT).show();
                    orderDataList.setUsedQuantity("0");
                    holder.listRowOrderItemAddAll.setText("Add All");
                    listener.onRemoveDirectFromPackage(orderDataList, position);
                    notifyDataSetChanged();
                } else {
                    orderDataList.setUsedQuantity(orderDataList.getItemQuantity());
                    holder.listRowOrderItemAddAll.setText("Remove All");
                    listener.onAddDirectToPackage(orderDataList, position);
                    notifyDataSetChanged();
                }
            }
        });

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return orderData.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PendingItemsAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute(OrderData orderDataList, int position);

        void onAddDirectToPackage(OrderData orderDataList, int position);

        void onRemoveDirectFromPackage(OrderData orderDataList, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView listRowPendingItemsItem;
        private TextView listRowOrderItemVendor;
        private TextView listRowPendingItemsSkuCode;
        private View listRowPendingItemsView;
        private TextView listRowPendingItemsBatchNumber;
        private TextView listRowOrderItemAvailableQuantity;
        private View listRowOrderItemViewOne;
        private TextView listRowOrderItemInventory;
        private View listRowOrderItemViewTwo;
        private TextView listRowOrderItemPurchasedOn;
        private TextView listRowPendingItemsQuantityUsed;
        private TextView listRowPendingItemsQuantity;
        private TextView listRowPendingItemsQuantityUnit;
        private TextView listRowOrderItemRemarks;
        private View listRowOrderItemView;
        private TextView listRowOrderItemAddQuantity, listRowOrderItemAddAll;
        private ImageView listRowOrderItemImage;

        public MyViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            listRowPendingItemsItem = (TextView) view.findViewById(R.id.list_row_pending_items_item);
            listRowOrderItemVendor = (TextView) view.findViewById(R.id.list_row_order_item_vendor);
            listRowPendingItemsSkuCode = (TextView) view.findViewById(R.id.list_row_pending_items_sku_code);
            listRowPendingItemsView = (View) view.findViewById(R.id.list_row_pending_items_view);
            listRowPendingItemsBatchNumber = (TextView) view.findViewById(R.id.list_row_pending_items_batch_number);
            listRowOrderItemAvailableQuantity = (TextView) view.findViewById(R.id.list_row_order_item_available_quantity);
            listRowOrderItemViewOne = (View) view.findViewById(R.id.list_row_order_item_view_one);
            listRowOrderItemInventory = (TextView) view.findViewById(R.id.list_row_order_item_inventory);
            listRowOrderItemViewTwo = (View) view.findViewById(R.id.list_row_order_item_view_two);
            listRowOrderItemPurchasedOn = (TextView) view.findViewById(R.id.list_row_order_item_purchased_on);
            listRowPendingItemsQuantityUsed = (TextView) view.findViewById(R.id.list_row_pending_items_quantity_used);
            listRowPendingItemsQuantity = (TextView) view.findViewById(R.id.list_row_pending_items_quantity);
            listRowPendingItemsQuantityUnit = (TextView) view.findViewById(R.id.list_row_pending_items_quantity_unit);
            listRowOrderItemRemarks = (TextView) view.findViewById(R.id.list_row_order_item_remarks);
            listRowOrderItemView = (View) view.findViewById(R.id.list_row_order_item_view);
            listRowOrderItemAddQuantity = (TextView) view.findViewById(R.id.list_row_order_item_add_quantity);
            listRowOrderItemImage = (ImageView) view.findViewById(R.id.list_row_pending_items_image);
            listRowOrderItemAddAll = (TextView) view.findViewById(R.id.list_row_order_item_add_all);
        }
    }

}
