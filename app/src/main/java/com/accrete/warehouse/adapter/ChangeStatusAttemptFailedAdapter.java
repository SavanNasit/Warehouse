package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.LogHistoryDatum;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 23/1/18.
 */

public class ChangeStatusAttemptFailedAdapter extends RecyclerView.Adapter<ChangeStatusAttemptFailedAdapter.MyViewHolder> {
    public List<LogHistoryDatum> logHistoryDatumArrayList = new ArrayList<>();
    private Context mContext;

    public ChangeStatusAttemptFailedAdapter(Context context, List<LogHistoryDatum> logHistoryDatumArrayList) {
        this.mContext = context;
        this.logHistoryDatumArrayList = logHistoryDatumArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_package_status, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        LogHistoryDatum logHistoryDatum = logHistoryDatumArrayList.get(position);

        //Status
        if (logHistoryDatum.getStatus() != null && !logHistoryDatum.getStatus().isEmpty()) {
            holder.statusTextView.setText(logHistoryDatum.getStatus().toString().trim());
        }

        //Date
        if (logHistoryDatum.getDate() != null && !logHistoryDatum.getDate().isEmpty()) {
            holder.dateTextView.setText(logHistoryDatum.getDate().toString().trim());
        }

        //Narration
        if (logHistoryDatum.getNarration() != null && !logHistoryDatum.getNarration().isEmpty()) {
            holder.narrationTextView.setText(logHistoryDatum.getNarration().toString().trim());
            holder.narrationLayout.setVisibility(View.VISIBLE);
        }else {
            holder.narrationLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return logHistoryDatumArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout statusLayout;
        private TextView statusTextView;
        private LinearLayout dateLayout;
        private TextView dateTextView;
        private LinearLayout filesLayout;
        private TextView filesTextView;
        private TextView narrationTextView;
        private LinearLayout narrationLayout;

        public MyViewHolder(View view) {
            super(view);
            statusLayout = (LinearLayout) view.findViewById(R.id.status_layout);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            dateLayout = (LinearLayout) view.findViewById(R.id.date_layout);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            filesLayout = (LinearLayout) view.findViewById(R.id.files_layout);
            filesTextView = (TextView) view.findViewById(R.id.files_textView);
            narrationTextView = (TextView) view.findViewById(R.id.narration_textView);
            narrationLayout = (LinearLayout) view.findViewById(R.id.narration_layout);
        }
    }
}
