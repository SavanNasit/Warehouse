package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ItemList;

import java.util.List;

/**
 * Created by agt on 27/12/17.
 */

public class QuotationProductsAdapter extends RecyclerView.Adapter<QuotationProductsAdapter.MyViewHolder> {
    private QuotationProductsAdapterListener listener;
    private Activity activity;
    private List<ItemList> itemDataList;

    public QuotationProductsAdapter(Activity activity, List<ItemList> itemDataList,
                                    QuotationProductsAdapterListener listener) {
        this.activity = activity;
        this.itemDataList = itemDataList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_quotation_product_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final ItemList objectItem = itemDataList.get(position);
        if (objectItem.getName().toString().trim() != null && !objectItem.getName().toString().trim().isEmpty()) {
            holder.nameLayout.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(objectItem.getName().toString().trim());
        } else {
            holder.nameLayout.setVisibility(View.GONE);
        }
        if (objectItem.getPacking().toString().trim() != null && !objectItem.getPacking().toString().trim().isEmpty()) {
            holder.packagingLayout.setVisibility(View.VISIBLE);
            holder.packagingTextView.setText(objectItem.getPacking().toString().trim());
        } else {
            holder.packagingLayout.setVisibility(View.GONE);
        }
        if (objectItem.getSkuCode().toString().trim() != null && !objectItem.getSkuCode().toString().trim().isEmpty()) {
            holder.skuLayout.setVisibility(View.VISIBLE);
            holder.skuCodeTextView.setText(objectItem.getSkuCode().toString().trim());
        } else {
            holder.skuLayout.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public interface QuotationProductsAdapterListener {
        void onProductClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout mainLayout;
        private LinearLayout nameLayout;
        private TextView nameTitleTextView;
        private TextView nameTextView;
        private LinearLayout skuLayout;
        private TextView skuCodeTitleTextView;
        private TextView skuCodeTextView;
        private LinearLayout packagingLayout;
        private TextView packagingTitleTextView;
        private TextView packagingTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            nameTitleTextView = (TextView) view.findViewById(R.id.name_title_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
            skuLayout = (LinearLayout) view.findViewById(R.id.sku_layout);
            skuCodeTitleTextView = (TextView) view.findViewById(R.id.skuCode_title_textView);
            skuCodeTextView = (TextView) view.findViewById(R.id.skuCode_textView);
            packagingLayout = (LinearLayout) view.findViewById(R.id.packaging_layout);
            packagingTitleTextView = (TextView) view.findViewById(R.id.packaging_title_textView);
            packagingTextView = (TextView) view.findViewById(R.id.packaging_textView);

        }
    }
}
