package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.Quotation;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by agt on 31/10/17.
 */

public class QuotationAdapter extends RecyclerView.Adapter<QuotationAdapter.MyViewHolder> {
    private Context context;
    private List<Quotation> quotationList;
    private DatabaseHandler databaseHandler;
    private String cuId;
    private CustomerQuotationAdapterListener listener;
    private SimpleDateFormat simpleDateFormat;

    public QuotationAdapter(Context context, List<Quotation> quotationList, String cuId,
                            CustomerQuotationAdapterListener listener) {
        this.context = context;
        this.quotationList = quotationList;
        this.cuId = cuId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_quotation_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final Quotation quotation = quotationList.get(position);

            if (quotation.getCustomerCName() != null && !quotation.getCustomerCName().isEmpty()) {
                holder.textViewCustomerName.setText(quotation.getCustomerCName().toString().trim());
                holder.textViewCustomerName.setVisibility(View.VISIBLE);
            } else {
                holder.textViewCustomerName.setVisibility(View.GONE);
            }
            if (quotation.getAssigned() != null && !quotation.getAssigned().isEmpty()) {
                holder.quotationAssignedTo.setText("Assigned to: " + quotation.getAssigned());
                holder.quotationAssignedTo.setVisibility(View.VISIBLE);
            } else {
                holder.quotationAssignedTo.setVisibility(View.GONE);
                holder.quotationAssignedTo.setText("");
            }
            holder.nameTextView.setText("Created by: " + quotation.getCreatedUser());
            holder.quotationIdTextView.setText("" + quotation.getQuotationID());
            //  holder.pendingManageQuotationId.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            double amount = Double.parseDouble(quotation.getPayableAmount());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
            holder.totalTextView.setText(context.getString(R.string.Rs) + " " + formatter.format(amount));

            //Status
            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

            if (quotation.getQosid().equals("1")) {
                drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                holder.statusTextView.setText("Converted");
            } else if (quotation.getQosid().equals("2")) {
                drawable.setColor(context.getResources().getColor(R.color.orange_purchase_order));
                holder.statusTextView.setText("Pending");
            } else if (quotation.getQosid().equals("3")) {
                drawable.setColor(context.getResources().getColor(R.color.red_purchase_order));
                holder.statusTextView.setText("Cancelled");
            } else if (quotation.getQosid().equals("4")) {
                drawable.setColor(context.getResources().getColor(R.color.blue_order));
                holder.statusTextView.setText("To be Approved");
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyClickEvents(holder, position, quotation.getQoid(), quotation.getQuotationID(),
                            quotation.getQosid());
                }
            });

            // applyClickEvents(holder, position, quotation.getQoid(), quotation.getQuotationId());
            if (position == 0) {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
            } else {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
            }

            //Created Time
            if (quotation.getCreatedTs() != null && !quotation.getCreatedTs().isEmpty()
                    && !quotation.getCreatedTs().toLowerCase().equals("null")) {

                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(quotation.getCreatedTs());
                    holder.dateTextView.setText("" + outputFormat.format(date).toString().trim());
                    holder.dateTextView.setVisibility(View.VISIBLE);
                } catch (ParseException e) {
                    e.printStackTrace();
                    holder.dateTextView.setVisibility(View.GONE);
                }

            } else {
                holder.dateTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final String qoId,
                                  final String qoText, final String qosId) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, qoId, qoText,
                        qosId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quotationList.size();
    }

    public interface CustomerQuotationAdapterListener {
        void onMessageRowClicked(int position, String qoId, String qoText,String qosId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView quotationIdTextView;
        private TextView nameTextView;
        private TextView textViewCustomerName;
        private TextView quotationAssignedTo;
        private TextView totalTextView;
        private TextView statusTextView;
        private CardView cardView;
        private TextView dateTextView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            quotationIdTextView = (TextView) view.findViewById(R.id.quotationId_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            textViewCustomerName = (TextView) view.findViewById(R.id.textView_customer_name);
            quotationAssignedTo = (TextView) view.findViewById(R.id.quotation_assigned_to);
            totalTextView = (TextView) view.findViewById(R.id.total_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);

        }
    }
}
