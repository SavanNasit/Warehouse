package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.Charge;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 6/8/18.
 */

public class DynamicChargeAdapter extends RecyclerView.Adapter<DynamicChargeAdapter.MyViewHolder> {
    private Context context;
    private DynamicChargeAdapterListener listener;
    private List<Charge> chargeList = new ArrayList<>();

    public DynamicChargeAdapter(Context context, List<Charge> chargeList, DynamicChargeAdapterListener listener) {
        this.context = context;
        this.chargeList = chargeList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_dynamic_charges, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Charge charge = chargeList.get(position);
        holder.textInputLayout.setHint(charge.getTitle());
        holder.autoCompleteTextViewTitle.setText(charge.getValue());

    }

    @Override
    public int getItemCount() {
        return chargeList.size();
    }

    private void applyClickEvents(DeliveredAdapter.MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface DynamicChargeAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextInputEditText autoCompleteTextViewTitle;
        private TextInputLayout textInputLayout;

        public MyViewHolder(View view) {
            super(view);
            autoCompleteTextViewTitle = (TextInputEditText) view.findViewById(R.id.dynamic_charges_value);
            textInputLayout =(TextInputLayout)view.findViewById(R.id.dynamic_charges_title);
        }
    }

}
