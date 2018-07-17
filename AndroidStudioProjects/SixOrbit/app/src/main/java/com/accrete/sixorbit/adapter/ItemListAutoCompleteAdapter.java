package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ItemList;

import java.util.ArrayList;

/**
 * Created by agt on 21/12/17.
 */

public class ItemListAutoCompleteAdapter extends ArrayAdapter<ItemList> {
    private Activity activity;
    private int layoutResourceId;
    private ArrayList<ItemList> itemListArrayList;

    public ItemListAutoCompleteAdapter(Activity activity, int layoutResourceId,
                                       ArrayList<ItemList> itemListArrayList) {
        super(activity, layoutResourceId, itemListArrayList);
        this.layoutResourceId = layoutResourceId;
        this.activity = activity;
        this.itemListArrayList = itemListArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        try {
            if (convertView == null) {
                // inflate the layout
                LayoutInflater inflater = activity.getLayoutInflater();
                convertView = inflater.inflate(layoutResourceId, parent, false);
            }

            // object item based on the position
            ItemList objectItem = itemListArrayList.get(position);

            // get the TextView and then set the text (item name) and tag (item ID) values
            LinearLayout nameLayout = (LinearLayout) convertView.findViewById(R.id.name_layout);
            TextView nameTitleTextView = (TextView) convertView.findViewById(R.id.name_title_textView);
            TextView nameTextView = (TextView) convertView.findViewById(R.id.name_textView);
            LinearLayout packagingLayout = (LinearLayout) convertView.findViewById(R.id.packaging_layout);
            TextView packagingTitleTextView = (TextView) convertView.findViewById(R.id.packaging_title_textView);
            TextView packagingTextView = (TextView) convertView.findViewById(R.id.packaging_textView);

            if (objectItem.getName().toString().trim() != null && !objectItem.getName().toString().trim().isEmpty()) {
                nameLayout.setVisibility(View.VISIBLE);
                nameTextView.setText(objectItem.getName().toString().trim());
            } else {
                nameLayout.setVisibility(View.GONE);
            }

            packagingLayout.setVisibility(View.GONE);


        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;

    }
}