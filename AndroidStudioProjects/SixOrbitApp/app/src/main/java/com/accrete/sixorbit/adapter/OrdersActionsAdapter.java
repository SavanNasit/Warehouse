package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import com.accrete.sixorbit.R;

/**
 * Created by {Anshul} on 12/4/18.
 */

public class OrdersActionsAdapter extends RecyclerView.Adapter<OrdersActionsAdapter.MyViewHolder> {
    private Activity activity;
    private String[] strArr;
    private OrdersActionsAdapterListener listener;
    private int itemsPosition;

    public OrdersActionsAdapter(Activity activity, String[] strArr,
                                OrdersActionsAdapterListener listener, int itemsPosition) {
        this.activity = activity;
        this.strArr = strArr;
        this.listener = listener;
        this.itemsPosition = itemsPosition;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_orders_actions, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.checkedTextView.setText(strArr[position].toString());

        if (position % 2 == 0) {
            holder.container.setBackgroundColor(activity.getResources().getColor(R.color.gray_bg));
        } else {
            holder.container.setBackgroundColor(Color.WHITE);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(holder, position, itemsPosition);
            }
        });
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final int itemsPosition) {
        listener.onActionsRowClicked(position, itemsPosition);
    }

    @Override
    public int getItemCount() {
        return strArr.length;
    }

    public interface OrdersActionsAdapterListener {
        void onActionsRowClicked(int position, int itemsPosition);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView checkedTextView;
        private LinearLayout container;

        public MyViewHolder(View view) {
            super(view);
            container = (LinearLayout) view.findViewById(R.id.container);
            checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTextView);
        }
    }
}
