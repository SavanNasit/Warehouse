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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.Consignment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 7/12/17.
 */

public class ConsignmentAdapter extends RecyclerView.Adapter<ConsignmentAdapter.MyViewHolder> {
    private Activity activity;
    private List<Consignment> consignmentList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private ConsignmentAdapterListener listener;

    public ConsignmentAdapter(Activity activity, List<Consignment> consignmentList, String venId, ConsignmentAdapterListener listener) {
        this.activity = activity;
        this.consignmentList = consignmentList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_consignment_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Consignment consignment = consignmentList.get(position);

        holder.consignmentIdTextView.setText(consignment.getConsignmentId());
        holder.consignmentIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.consignmentIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (consignment.getWarehouse() != null && !consignment.getWarehouse().isEmpty()) {
            holder.wareHouseTextView.setText(consignment.getWarehouse());
            holder.wareHouseTextView.setVisibility(View.VISIBLE);
        } else {
            holder.wareHouseTextView.setVisibility(View.GONE);
        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(consignment.getPurchaseDate());
            holder.dateTextView.setText("" + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

        if (consignment.getIscsid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            holder.statusTextView.setText("Active");
        } else if (consignment.getIscsid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Inactive");
        } else if (consignment.getIscsid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Delete");
        } else if (consignment.getIscsid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Freezed");
        } else if (consignment.getIscsid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Payment Approved");
        }

        //On Item Click
        applyClickEvents(holder, position, consignment.getIscid(), consignment.getConsignmentId());

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

    private void applyClickEvents(MyViewHolder holder, final int position, final String consignmentId,
                                  final String consignmentText) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, consignmentId, consignmentText);
            }
        });
    }

    @Override
    public int getItemCount() {
        return consignmentList.size();
    }

    public interface ConsignmentAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout leftLayout;
        private TextView consignmentIdTextView;
        private TextView wareHouseTextView;
        private TextView dateTextView;
        private TextView statusTextView;
        private RelativeLayout mainLayout;

        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);

            mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            consignmentIdTextView = (TextView) view.findViewById(R.id.consignmentId_textView);
            wareHouseTextView = (TextView) view.findViewById(R.id.wareHouse_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
        }
    }
}
