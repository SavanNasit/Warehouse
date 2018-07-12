package com.accrete.sixorbit.adapter;

import android.app.DownloadManager;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

public class ConvertedManageQuotationAdapter extends RecyclerView.Adapter<ConvertedManageQuotationAdapter.MyViewHolder> {
    private Context context;
    private List<QuotationDatum> pendingList;
    private ConvertedManageQuotationAdapterListener listener;
    private AlertDialog alertDialog;
    private TextView downloadConfirmMessage, titleTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private ProgressBar progressBar;
    private DownloadManager downloadManager;
    private SimpleDateFormat simpleDateFormat;

    public ConvertedManageQuotationAdapter(Context context, List<QuotationDatum> pendingList, ConvertedManageQuotationAdapterListener listener) {
        this.context = context;
        this.pendingList = pendingList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_manage_quotation, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final QuotationDatum quotationDatum = pendingList.get(position);
            if (quotationDatum.getCustomerCName() != null && !quotationDatum.getCustomerCName().isEmpty()) {
                holder.pendingManageQuotationCustomerName.setText(quotationDatum.getCustomerCName().toString().trim());
                holder.pendingManageQuotationCustomerName.setVisibility(View.VISIBLE);
            } else {
                holder.pendingManageQuotationCustomerName.setVisibility(View.GONE);
            }
            if (quotationDatum.getAssigned() != null && !quotationDatum.getAssigned().isEmpty()) {
                holder.pendingManageQuotationAssignedTo.setText("Assigned to: " + quotationDatum.getAssigned());
                holder.pendingManageQuotationAssignedTo.setVisibility(View.VISIBLE);
            } else {
                holder.pendingManageQuotationAssignedTo.setVisibility(View.GONE);
                holder.pendingManageQuotationAssignedTo.setText("");
            }

            if (quotationDatum.getCreatedUser() != null && !quotationDatum.getCreatedUser().isEmpty()) {
                holder.pendingManageQuotationCreatedBy.setText("Created by: " + quotationDatum.getCreatedUser() + " ");
                holder.pendingManageQuotationCreatedBy.setVisibility(View.VISIBLE);
            } else {
                holder.pendingManageQuotationCreatedBy.setVisibility(View.GONE);
            }

            holder.pendingManageQuotationId.setText("" + quotationDatum.getQuotationID());
            //  holder.pendingManageQuotationId.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            double amount = Double.parseDouble(quotationDatum.getPayableAmount());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
            holder.pendingManageQuotationTotal.setText(context.getString(R.string.Rs) + " " + formatter.format(amount));

            //Created TS
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(quotationDatum.getCreatedTs());
                holder.pendingManageQuotationTotal.append(" | " + outputFormat.format(date).toString().trim());
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

    public interface ConvertedManageQuotationAdapterListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView pendingManageQuotationCustomerName;
        private TextView pendingManageQuotationAssignedTo;
        private TextView pendingManageQuotationTotal;
        private TextView pendingManageQuotationCreatedBy;
        private TextView pendingManageQuotationId;
        private LinearLayout container;
        private CardView cardViewInner;

        public MyViewHolder(View view) {
            super(view);
            container = (LinearLayout) view.findViewById(R.id.container);
            pendingManageQuotationCustomerName = (TextView) view.findViewById(R.id.pending_manage_quotation_customer_name);
            pendingManageQuotationAssignedTo = (TextView) view.findViewById(R.id.pending_manage_quotation_assigned_to);
            pendingManageQuotationTotal = (TextView) view.findViewById(R.id.pending_manage_quotation_total);
            pendingManageQuotationCreatedBy = (TextView) view.findViewById(R.id.pending_manage_quotation_created_by);
            pendingManageQuotationId = (TextView) view.findViewById(R.id.pending_manage_quotation_id);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
        }
    }

}
