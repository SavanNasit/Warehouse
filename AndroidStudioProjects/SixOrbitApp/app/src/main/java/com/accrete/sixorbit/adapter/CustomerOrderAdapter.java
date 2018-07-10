package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Order;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 5/12/17.
 */

public class CustomerOrderAdapter extends RecyclerView.Adapter<CustomerOrderAdapter.MyViewHolder> {
    private Activity activity;
    private List<Order> orderList;
    private String cuId;
    private SimpleDateFormat simpleDateFormat;
    private CustomerOrderAdapterListener listener;

    public CustomerOrderAdapter(Activity activity, List<Order> orderList, String cuId, CustomerOrderAdapterListener listener) {
        this.activity = activity;
        this.orderList = orderList;
        this.cuId = cuId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_customer_order_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Order order = orderList.get(position);

        holder.orderIdTextView.setText(order.getCheckpointOrderId());
        holder.orderIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.orderIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        holder.nameTextView.setText(order.getCustomerName() + "");

        double amount = Double.parseDouble(order.getAmount());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        holder.amountTextView.setText(activity.getString(R.string.Rs) + " " + formatter.format(amount));

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(order.getDate());
            holder.dateTextView.setText(" | " + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

        if (order.getChkosid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.orange_order));
            holder.statusTextView.setText("Pending");
        } else if (order.getChkosid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.statusTextView.setText("Partial Assigned");
        } else if (order.getChkosid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_order));
            holder.statusTextView.setText("Assigned");
        } else if (order.getChkosid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.statusTextView.setText("Partial Delivered");
        } else if (order.getChkosid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_order));
            holder.statusTextView.setText("Delivered");
        } else if (order.getChkosid().equals("6")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_order));
            holder.statusTextView.setText("Rejected");
        } else if (order.getChkosid().equals("7")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_order));
            holder.statusTextView.setText("Revoked");
        } else if (order.getChkosid().equals("8")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.statusTextView.setText("Partially Executed");
        } else if (order.getChkosid().equals("9")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_order));
            holder.statusTextView.setText("Executed");
        } else if (order.getChkosid().equals("10")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_order));
            holder.statusTextView.setText("Cancelled");
        } else if (order.getChkosid().equals("11")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_order));
            holder.statusTextView.setText("Deleted");
        } else if (order.getChkosid().equals("12")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_order));
            holder.statusTextView.setText("To be Approved");
        } else {
            drawable.setColor(activity.getResources().getColor(R.color.green_order));
            holder.statusTextView.setText("Executed");
        }

        //On Item Click
        applyClickEvents(holder, position, order.getChkoid(), order.getCheckpointOrderId());
        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
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

    private void applyClickEvents(MyViewHolder holder, final int position, final String orderId, final String orderText) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, orderId, orderText);
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public interface CustomerOrderAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mainLayout;
        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView statusTextView, amountTextView, nameTextView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            orderIdTextView = (TextView) view.findViewById(R.id.orderId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
        }
    }
}

