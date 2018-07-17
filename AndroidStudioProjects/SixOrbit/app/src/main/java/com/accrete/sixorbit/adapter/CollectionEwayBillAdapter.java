package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.EwayBillData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by {Anshul} on 6/6/18.
 */

public class CollectionEwayBillAdapter extends
        RecyclerView.Adapter<CollectionEwayBillAdapter.MyViewHolder> {
    private Activity activity;
    private List<EwayBillData> ewayBillDataList;
    private EwayBillClickListener listener;


    public CollectionEwayBillAdapter(Activity activity, List<EwayBillData> ewayBillDataList,
                                     EwayBillClickListener listener) {
        this.activity = activity;
        this.ewayBillDataList = ewayBillDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_collection_eway_bill, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final EwayBillData ewayBillData = ewayBillDataList.get(position);

        if (ewayBillData.getHsn() != null && !ewayBillData.getHsn().isEmpty() &&
                !ewayBillData.getHsn().equals("null")) {
            holder.hsnCodeTextView.setText("HSN: " + ewayBillData.getHsn());
            holder.hsnCodeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.hsnCodeTextView.setVisibility(View.GONE);
        }

        if (ewayBillData.getTax() != null && !ewayBillData.getTax().isEmpty() &&
                !ewayBillData.getTax().equals("null")) {
            holder.taxTextView.setText("Tax: " + new BigDecimal(Constants.roundTwoDecimals
                    (Constants.ParseDouble(ewayBillData.getTax())))
                    .setScale(2, RoundingMode.HALF_UP).toPlainString() + " %");
            holder.taxTextView.setVisibility(View.VISIBLE);
        } else {
            holder.taxTextView.setVisibility(View.GONE);
        }

        if (ewayBillData.getAmount() != null && !ewayBillData.getAmount().isEmpty() &&
                !ewayBillData.getAmount().equals("null")) {
            holder.amountTextView.setText("Amount: " + activity.getString(R.string.Rs) + " " +
                    new BigDecimal(Constants.roundTwoDecimals
                            (Constants.ParseDouble(ewayBillData.getAmount())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
            holder.amountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.amountTextView.setVisibility(View.GONE);
        }

        if (ewayBillData.getQuantity() != null && !ewayBillData.getQuantity().isEmpty() &&
                !ewayBillData.getQuantity().equals("null")) {
            holder.quantityTextView.setText("Qty: " + ewayBillData.getQuantity());
            holder.quantityTextView.setVisibility(View.VISIBLE);
        } else {
            holder.quantityTextView.setVisibility(View.GONE);
        }

        if (ewayBillData.getTotal() != null && !ewayBillData.getTotal().isEmpty() &&
                !ewayBillData.getTotal().equals("null")) {
            holder.totalAmountTextView.setText("Total Amount: " + activity.getString(R.string.Rs) +
                    " " + new BigDecimal(Constants.roundTwoDecimals
                    (Constants.ParseDouble(ewayBillData.getTotal())))
                    .setScale(2, RoundingMode.HALF_UP).toPlainString());
            holder.totalAmountTextView.setVisibility(View.VISIBLE);
        } else {
            holder.totalAmountTextView.setVisibility(View.GONE);
        }

        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._4sdp);
            setMargins(holder.cardview, 0, topMargin, 0, 0);
        } else if (position == ewayBillDataList.size() - 1) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardview, 0, topMargin, 0, topMargin);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardview, 0, topMargin, 0, 0);
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
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ewayBillDataList.size();
    }

    public interface EwayBillClickListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardview;
        private TextView hsnCodeTextView;
        private TextView quantityTextView;
        private TextView amountTextView;
        private TextView taxTextView;
        private TextView totalAmountTextView;

        public MyViewHolder(View view) {
            super(view);
            cardview = (CardView) view.findViewById(R.id.cardview);
            hsnCodeTextView = (TextView) view.findViewById(R.id.hsn_code_textView);
            quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            totalAmountTextView = (TextView) view.findViewById(R.id.total_amount_textView);
        }
    }
}

