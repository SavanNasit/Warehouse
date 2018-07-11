package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.EnquiryProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by {Anshul} on 29/6/18.
 */

public class EnquiryProductsAdapter extends RecyclerView.Adapter<EnquiryProductsAdapter.MyViewHolder> {
    private Activity activity;
    private List<EnquiryProduct> productArrayList;

    public EnquiryProductsAdapter(Activity activity, List<EnquiryProduct> productArrayList) {
        this.activity = activity;
        this.productArrayList = productArrayList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_enquiry_add_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final EnquiryProduct itemData = productArrayList.get(position);

        holder.productNameTextView.setText(itemData.getProductName());

        try {
            if (itemData.getResult() instanceof ArrayList) {
                ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();
                mylist.addAll(((ArrayList) itemData.getResult()));
                for (Map<String, String> map : mylist)
                    for (String str : map.keySet()) {
                        String key = str;
                        Log.e("KEY", key);
                        if (map.get(str) instanceof String) {
                            String value = map.get(str);
                            if (holder.quantityTextView.getText().toString().length() == 0) {
                                holder.quantityTextView.setText(key.trim() + " : " + value.trim());
                            } else {
                                holder.quantityTextView.append("\n" + key.trim() + " : " + value.trim());
                            }
                            Log.e("VALUE", value);
                        } else {
                            ArrayList<String> value = new ArrayList<String>();
                            value.add(map.get(str));
                            Log.e("VALUE", value.toString());
                        }
                    }
                if (holder.quantityTextView.getText().toString().length() != 0) {
                    holder.quantityTextView.setVisibility(View.VISIBLE);
                }
            } else {
                holder.quantityTextView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.quantityTextView.setVisibility(View.GONE);
        }

        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._5sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        try {
            if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                p.setMargins(l, t, r, b);
                v.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return productArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView productNameTextView;
        private TextView quantityTextView;
        private LinearLayout expandViewLayout;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            productNameTextView = (TextView) view.findViewById(R.id.product_name_textView);
            quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
            expandViewLayout = (LinearLayout) view.findViewById(R.id.expand_viewLayout);
            expandViewLayout.setVisibility(View.GONE);
            quantityTextView.setVisibility(View.GONE);
        }
    }
}