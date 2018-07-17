package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.AllEnquiry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by {Anshul} on 27/6/18.
 */

public class ManageEnquiryAdapter extends
        RecyclerView.Adapter<ManageEnquiryAdapter.MyViewHolder> {
    private Activity activity;
    private List<AllEnquiry> allEnquiries;
    private EnquiriesListener listener;
    private SimpleDateFormat simpleDateFormat;

    public ManageEnquiryAdapter(Activity activity, List<AllEnquiry>
            allEnquiries, EnquiriesListener listener) {
        this.activity = activity;
        this.allEnquiries = allEnquiries;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_manage_enquiries, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final AllEnquiry allEnquiry = allEnquiries.get(position);

            if (allEnquiry.getCustomerName() != null &&
                    !allEnquiry.getCustomerName().toString().trim().isEmpty()) {
                holder.nameTextView.setText(allEnquiry.getCustomerName().toString().trim());
                holder.nameTextView.setVisibility(View.VISIBLE);
            } else {
                holder.nameTextView.setVisibility(View.GONE);
            }

            if (allEnquiry.getEnquiryID() != null &&
                    !allEnquiry.getEnquiryID().isEmpty()) {
                holder.enquiryIdTextView.setText(allEnquiry.getEnquiryID().toString().trim());
                holder.enquiryIdTextView.setVisibility(View.VISIBLE);
            } else {
                holder.enquiryIdTextView.setVisibility(View.GONE);
            }

            if (allEnquiry.getCreatedBy() != null &&
                    !allEnquiry.getCreatedBy().isEmpty()) {
                holder.createdByTextView.setText("Created by: " + allEnquiry.getCreatedBy().toString().trim());
                holder.createdByTextView.setVisibility(View.VISIBLE);
            } else {
                holder.createdByTextView.setVisibility(View.GONE);
            }

            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                Date date = simpleDateFormat.parse(allEnquiry.getCreatedTs());
                holder.dateTextView.setText(outputFormat.format(date).toString().trim());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

            if (allEnquiry.getEnsid().equals("2")) {
                drawable.setColor(activity.getResources().getColor(R.color.orange_order));
                holder.statusTextView.setText(allEnquiry.getEnquiryStatus() + "");
                holder.statusTextView.setVisibility(View.VISIBLE);
            } else if (allEnquiry.getEnsid().equals("1")) {
                drawable.setColor(activity.getResources().getColor(R.color.green_order));
                holder.statusTextView.setText(allEnquiry.getEnquiryStatus() + "");
                holder.statusTextView.setVisibility(View.VISIBLE);
            } else if (allEnquiry.getEnsid().equals("5")) {
                drawable.setColor(activity.getResources().getColor(R.color.orange_order));
                holder.statusTextView.setText(allEnquiry.getEnquiryStatus() + "");
                holder.statusTextView.setVisibility(View.VISIBLE);
            } else if (allEnquiry.getEnsid().equals("3")) {
                drawable.setColor(activity.getResources().getColor(R.color.red_order));
                holder.statusTextView.setText(allEnquiry.getEnquiryStatus() + "");
                holder.statusTextView.setVisibility(View.VISIBLE);
            } else {
                holder.statusTextView.setVisibility(View.GONE);
            }

            //On Item Click
            applyClickEvents(holder, position);

            if (position == 0) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
            } else if (position == allEnquiries.size() - 1) {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, 0, topMargin, 0, topMargin);
            } else {
                int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, 0, topMargin, 0, 0);
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

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
        holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClicked(position);
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return allEnquiries.size();
    }

    public interface EnquiriesListener {
        void onMessageRowClicked(int position);

        void onLongClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private CardView cardView;
        private TextView enquiryIdTextView;
        private TextView dateTextView;
        private TextView nameTextView;
        private TextView statusTextView;
        private TextView createdByTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            cardView = (CardView) view.findViewById(R.id.outlet_cardview);
            enquiryIdTextView = (TextView) view.findViewById(R.id.enquiry_id_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            createdByTextView = (TextView) view.findViewById(R.id.created_by_textView);
        }
    }
}



