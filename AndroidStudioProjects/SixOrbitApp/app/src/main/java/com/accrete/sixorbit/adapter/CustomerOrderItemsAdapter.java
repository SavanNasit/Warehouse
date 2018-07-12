package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.OrderItem;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by agt on 14/12/17.
 */

public class CustomerOrderItemsAdapter extends RecyclerView.Adapter<CustomerOrderItemsAdapter.MyViewHolder> {
    private Context context;
    private List<OrderItem> orderItemList;
    private String cuId;
    private OrderItemsAdapterListener listener;
    private SimpleDateFormat simpleDateFormat;

    public CustomerOrderItemsAdapter(Context context, List<OrderItem> orderItemList, String cuId,
                                     OrderItemsAdapterListener listener) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.cuId = cuId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_order_item_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final OrderItem orderItem = orderItemList.get(position);


        //holder.itemIdTextView.setText(orderItem.getItemId());
        //holder.itemIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.itemIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        //Name
        if (orderItem.getItemName() != null && !orderItem.getItemName().isEmpty()) {
            holder.nameTextView.setText(orderItem.getItemName().toString().trim());
            holder.nameTextView.setVisibility(View.VISIBLE);
        } else {
            holder.nameTextView.setVisibility(View.GONE);
        }


        //Internal Code
        if (orderItem.getInternalCode() != null && !orderItem.getInternalCode().isEmpty()) {
            holder.internalCodeTextView.setText("SKU : " + orderItem.getInternalCode().toString().trim());
            holder.internalCodeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.internalCodeTextView.setVisibility(View.GONE);
        }

        //Total Quantity
        if (orderItem.getQuantity() != null && !orderItem.getQuantity().isEmpty()) {
            holder.totalQuantityTextView.setText("T.Q. " + formatter.format(Double.parseDouble(orderItem.getQuantity().toString().trim()))
                    + " " + orderItem.getUnit().toString().trim());
            holder.totalQuantityTextView.setVisibility(View.VISIBLE);
        } else {
            holder.totalQuantityTextView.setVisibility(View.GONE);
        }

        //Allocated Quantity
        if (orderItem.getAllocatedQuantity() != null && !orderItem.getAllocatedQuantity().isEmpty()) {
            holder.allocatedQuantityTextView.setText("A.Q. " + formatter.format(Double.parseDouble(orderItem.getAllocatedQuantity().toString().trim()))
                    + " " + orderItem.getUnit().toString().trim());
            holder.allocatedQuantityTextView.setVisibility(View.VISIBLE);
        } else {
            holder.allocatedQuantityTextView.setVisibility(View.GONE);
        }

        holder.priceTextView.setText("Price " + context.getString(R.string.Rs) + " " +
                formatter.format(Double.parseDouble(orderItem.getPrice())));
        holder.taxTextView.setText("Tax " +
                formatter.format(Double.parseDouble(orderItem.getItemTax())));
        holder.subtotalTextView.setText("Subtotal " + context.getString(R.string.Rs) + " " +
                formatter.format(Double.parseDouble(orderItem.getSubTotal())));

        if (position == 0) {
            setMargins(holder.cardView, 0, 0, 0, 0);
        } else {
            int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
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

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public interface OrderItemsAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout leftLayout;
        private TextView itemIdTextView;
        private TextView nameTextView;
        private TextView internalCodeTextView;
        private TextView priceTextView;
        private TextView taxTextView;
        private TextView subtotalTextView;
        private TextView totalQuantityTextView, allocatedQuantityTextView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            subtotalTextView = (TextView) view.findViewById(R.id.subtotal_textView);
            totalQuantityTextView = (TextView) view.findViewById(R.id.total_quantity_textView);
            allocatedQuantityTextView = (TextView) view.findViewById(R.id.allocated_quantity_textView);
            priceTextView = (TextView) view.findViewById(R.id.price_textView);
            itemIdTextView = (TextView) view.findViewById(R.id.itemId_textView);
            internalCodeTextView = (TextView) view.findViewById(R.id.internal_code_textView);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);

        }
    }
}
