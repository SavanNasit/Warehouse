package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.TemplatesData;

import java.util.List;

/**
 * Created by agt on 21/2/18.
 */

public class QuotationTemplatesAdapter extends RecyclerView.Adapter<QuotationTemplatesAdapter.MyViewHolder> {
    private Context context;
    private List<TemplatesData> templatesList;
    private QuotationTemplatesAdapterListener listener;

    public QuotationTemplatesAdapter(Context context, List<TemplatesData> templatesList,
                                     QuotationTemplatesAdapterListener listener) {
        this.context = context;
        this.templatesList = templatesList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_templates, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final TemplatesData templatesData = templatesList.get(position);
        holder.checkedTextView.setText(templatesData.getTitle());
        holder.checkedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.checkedTextView.isChecked()) {
                    holder.checkedTextView.setChecked(true);
                }
                applyClickEvents(holder, position);
            }
        });

        if (templatesData.isChecked()) {
            holder.checkedTextView.setChecked(true);
        } else {
            holder.checkedTextView.setChecked(false);
        }
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    @Override
    public int getItemCount() {
        return templatesList.size();
    }

    public interface QuotationTemplatesAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckedTextView checkedTextView;

        public MyViewHolder(View view) {
            super(view);
            checkedTextView = (CheckedTextView) view.findViewById(R.id.checkedTextView);
        }
    }
}
