package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.ChargesList;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created by agt on 2/3/18.
 */

public class QuotationPreChargesAdapter extends RecyclerView.Adapter<QuotationPreChargesAdapter.MyViewHolder> {
    private Activity activity;
    private List<ChargesList> chargesLists;
    private PreChargesAdapterListener listener;


    public QuotationPreChargesAdapter(Activity activity, List<ChargesList> chargesLists, PreChargesAdapterListener listener) {
        this.activity = activity;
        this.chargesLists = chargesLists;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pre_charges, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(final MyViewHolder holder, final int position) {
        final ChargesList chargesList = chargesLists.get(position);
        holder.titleTextView.setText(chargesList.getTitle());
        //holder.titleTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.titleTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        if (chargesList.getDiscountAmountValue() != null && !chargesList.getDiscountAmountValue().isEmpty()) {
            holder.chargeValueTextView.setText("" +
                    new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(chargesList.getDiscountAmountValue())))
                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
        } else {
            holder.chargeValueTextView.setText("0.0");
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.PreChargeDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chargesLists.size();
    }

    public interface PreChargesAdapterListener {
        void PreChargeDialog(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private TextView titleTextView;
        private TextView chargeValueTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            titleTextView = (TextView) view.findViewById(R.id.title_textView);
            chargeValueTextView = (TextView) view.findViewById(R.id.charge_value_textView);
        }
    }
}
