package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ConsignmentDetail;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by agt on 20/1/18.
 */

public class OrderConsignmentDetailsAdapter extends RecyclerView.Adapter<OrderConsignmentDetailsAdapter.MyViewHolder> {
    public List<ConsignmentDetail> consignmentDetailList = new ArrayList<>();
    private Context mContext;

    public OrderConsignmentDetailsAdapter(Context context, List<ConsignmentDetail> consignmentDetailList) {
        this.mContext = context;
        this.consignmentDetailList = consignmentDetailList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_consignment_details_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");

        ConsignmentDetail consignmentDetail = consignmentDetailList.get(position);

        //Consignment Id
        if (consignmentDetail.getConsignmentID() != null && !consignmentDetail.getConsignmentID().isEmpty()) {
            holder.consignmentIdValueTextView.setText(consignmentDetail.getConsignmentID().toString().trim());
            holder.consignmentIdValueTextView.setPaintFlags(holder.consignmentIdValueTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else {
            //    holder.consignmentIdLayout.setVisibility(View.GONE);
        }

        //Vendor
        if (consignmentDetail.getVendor() != null && !consignmentDetail.getVendor().isEmpty()) {
            holder.vendorValueTextView.setText(capitalize(consignmentDetail.getVendor().toString().trim()));
        } else {
            //   holder.vendorLayout.setVisibility(View.GONE);
        }

        //Purchase Date
        if (consignmentDetail.getPurchaseDate() != null && !consignmentDetail.getPurchaseDate().isEmpty()) {
            try {
                holder.purchaseDateValueTextView.setText((outputFormat.format(simpleDateFormat.parse(
                        consignmentDetail.getPurchaseDate()))) + "");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            //   holder.purchaseDateLayout.setVisibility(View.GONE);
        }

        //Status
        if (consignmentDetail.getStatus() != null && !consignmentDetail.getStatus().isEmpty()) {
            holder.statusValueTextView.setBackgroundResource(R.drawable.tags_rounded_corner);
            GradientDrawable drawable = (GradientDrawable) holder.statusValueTextView.getBackground();

            if (consignmentDetail.getIscid().equals("1")) {
                drawable.setColor(mContext.getResources().getColor(R.color.green_purchase_order));
            } else if (consignmentDetail.getIscid().equals("2")) {
                drawable.setColor(mContext.getResources().getColor(R.color.red_purchase_order));
            } else if (consignmentDetail.getIscid().equals("3")) {
                drawable.setColor(mContext.getResources().getColor(R.color.gray_order));
            } else if (consignmentDetail.getIscid().equals("4")) {
                drawable.setColor(mContext.getResources().getColor(R.color.gray_order));
            } else if (consignmentDetail.getIscid().equals("5")) {
                drawable.setColor(mContext.getResources().getColor(R.color.blue_purchase_order));
            }
            holder.statusValueTextView.setText(consignmentDetail.getStatus().toString().trim());
        } else {
            //  holder.statusLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return consignmentDetailList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        //  private TextView consignmentIdTextView;
        private TextView consignmentIdValueTextView;
        //    private LinearLayout vendorLayout;
        //   private TextView vendorTextView;
        private TextView vendorValueTextView;
        //  private LinearLayout purchaseDateLayout;
        //  private TextView purchaseDateTextView;
        private TextView purchaseDateValueTextView;
        //  private LinearLayout statusLayout;
        //  private TextView statusTextView;
        private TextView statusValueTextView;

        public MyViewHolder(View view) {
            super(view);
            //consignmentIdTextView = (TextView) view.findViewById(R.id.consignmentId_textView);
            consignmentIdValueTextView = (TextView) view.findViewById(R.id.consignmentId_value_textView);
            //  vendorLayout = (LinearLayout) view.findViewById(R.id.vendor_layout);
            //  vendorTextView = (TextView) view.findViewById(R.id.vendor_textView);
            vendorValueTextView = (TextView) view.findViewById(R.id.vendor_value_textView);
            //  purchaseDateLayout = (LinearLayout) view.findViewById(R.id.purchaseDate_layout);
            //   purchaseDateTextView = (TextView) view.findViewById(R.id.purchaseDate_textView);
            purchaseDateValueTextView = (TextView) view.findViewById(R.id.purchaseDate_value_textView);
            //   statusLayout = (LinearLayout) view.findViewById(R.id.status_layout);
            //   statusTextView = (TextView) view.findViewById(R.id.status_textView);
            statusValueTextView = (TextView) view.findViewById(R.id.status_value_textView);
        }
    }
}