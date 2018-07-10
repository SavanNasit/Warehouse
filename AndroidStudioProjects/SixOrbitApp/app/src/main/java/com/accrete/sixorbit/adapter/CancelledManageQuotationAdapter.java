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
import com.accrete.sixorbit.model.QuotationDatum;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by poonam on 1/8/18.
 */

public class CancelledManageQuotationAdapter extends RecyclerView.Adapter<CancelledManageQuotationAdapter.MyViewHolder> {
    private Context context;
    private List<QuotationDatum> pendingList;
    private CancelledManageQuotationAdapterListener listener;
    private String flagTobeApproved;
    private SimpleDateFormat simpleDateFormat;

    public CancelledManageQuotationAdapter(Context context, List<QuotationDatum> pendingList, CancelledManageQuotationAdapterListener listener, String toBeApproved) {
        this.context = context;
        this.pendingList = pendingList;
        this.listener = listener;
        this.flagTobeApproved = toBeApproved;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_cancelled_manage_quotation, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final QuotationDatum quotationDatum = pendingList.get(position);

            if (quotationDatum.getCustomerCName() != null && !quotationDatum.getCustomerCName().isEmpty()) {
                holder.cancelledManageQuotationCustomerName.setText(quotationDatum.getCustomerCName().toString().trim());
                holder.cancelledManageQuotationCustomerName.setVisibility(View.VISIBLE);
            } else {
                holder.cancelledManageQuotationCustomerName.setVisibility(View.GONE);
            }

            if (flagTobeApproved.equals("ToBeApproved")) {
                if (quotationDatum.getApprovalReason() != null && !quotationDatum.getApprovalReason().isEmpty()) {
                    holder.cancelledManageQuotationReason.setText(quotationDatum.getApprovalReason());
                    holder.cancelledManageQuotationReason.setVisibility(View.VISIBLE);
                } else {
                    holder.cancelledManageQuotationReason.setVisibility(View.GONE);
                }
            } else {
                if (quotationDatum.getComment() != null && quotationDatum.getComment().getComment() != null
                        && !quotationDatum.getComment().getComment().isEmpty()) {
                    holder.cancelledManageQuotationReason.setText(quotationDatum.getComment().getComment());
                    holder.cancelledManageQuotationReason.setVisibility(View.VISIBLE);
                } else {
                    holder.cancelledManageQuotationReason.setVisibility(View.GONE);
                }
            }

            if (quotationDatum.getAssigned() != null && !quotationDatum.getAssigned().isEmpty()) {
                holder.cancelledManageQuotationAssignedTo.setText("Assigned to: " + quotationDatum.getAssigned());
                holder.cancelledManageQuotationAssignedTo.setVisibility(View.VISIBLE);
            } else {
                holder.cancelledManageQuotationAssignedTo.setText("");
                holder.cancelledManageQuotationAssignedTo.setVisibility(View.GONE);
            }

            if (quotationDatum.getCreatedUser() != null && !quotationDatum.getCreatedUser().isEmpty()) {
                holder.cancelledManageQuotationCreatedBy.setText("Created by: " + quotationDatum.getCreatedUser() + " ");
                holder.cancelledManageQuotationCreatedBy.setVisibility(View.VISIBLE);
            } else {
                holder.cancelledManageQuotationCreatedBy.setVisibility(View.GONE);
            }

            holder.cancelledManageQuotationId.setText("" + quotationDatum.getQuotationID());
            //    holder.cancelledManageQuotationId.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);

            double amount = Double.parseDouble(quotationDatum.getPayableAmount());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            holder.cancelledManageQuotationTotal.setText(context.getString(R.string.Rs) + " " + formatter.format(amount));

            //Created TS
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(quotationDatum.getCreatedTs());
                holder.cancelledManageQuotationTotal.append(" | " + outputFormat.format(date).toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    applyClickEvents(holder, position);
                }
            });
            holder.container.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    listener.onLongClicked(position);
                    return false;
                }
            });

            if (position == 0) {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._6sdp);
                int sideMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardViewInner, sideMargin, topMargin, sideMargin, 0);
            } else if (position == pendingList.size() - 1) {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardViewInner, topMargin, topMargin, topMargin, topMargin);
            } else {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardViewInner, topMargin, topMargin, topMargin, 0);
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

    @Override
    public int getItemCount() {
        return pendingList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface CancelledManageQuotationAdapterListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView cancelledManageQuotationCustomerName;
        private TextView cancelledManageQuotationReason;
        private TextView cancelledManageQuotationAssignedTo;
        private TextView cancelledManageQuotationTotal;
        private TextView cancelledManageQuotationCreatedBy;
        private TextView cancelledManageQuotationId;
        private LinearLayout container;
        private CardView cardViewInner;

        public MyViewHolder(View view) {
            super(view);
            container = (LinearLayout) view.findViewById(R.id.container);
            cancelledManageQuotationCustomerName = (TextView) view.findViewById(R.id.cancelled_manage_quotation_customer_name);
            cancelledManageQuotationReason = (TextView) view.findViewById(R.id.cancelled_manage_quotation_reason);
            cancelledManageQuotationAssignedTo = (TextView) view.findViewById(R.id.cancelled_manage_quotation_assigned_to);
            cancelledManageQuotationTotal = (TextView) view.findViewById(R.id.cancelled_manage_quotation_total);
            cancelledManageQuotationCreatedBy = (TextView) view.findViewById(R.id.cancelled_manage_quotation_created_by);
            cancelledManageQuotationId = (TextView) view.findViewById(R.id.cancelled_manage_quotation_id);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
        }
    }
}
