package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.User;

import java.util.List;

/**
 * Created by agt on 22/1/18.
 */

public class AuthorizedByUserAdapter extends RecyclerView.Adapter<AuthorizedByUserAdapter.MyViewHolder> {
    private AuthorizedByUserAdapterListener listener;
    private Activity activity;
    private List<User> itemDataList;
    private String userType;

    public AuthorizedByUserAdapter(Activity activity, List<User> itemDataList,
                                   AuthorizedByUserAdapterListener listener, String userType) {
        this.activity = activity;
        this.itemDataList = itemDataList;
        this.listener = listener;
        this.userType = userType;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_vendor_name, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        //On Item Click
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final User objectItem = itemDataList.get(position);
        if (objectItem.getName().toString().trim() != null && !objectItem.getName().toString().trim().isEmpty()) {
            holder.nameLayout.setVisibility(View.VISIBLE);
            holder.nameTextView.setText(objectItem.getName().toString().trim());
        } else {
            holder.nameLayout.setVisibility(View.GONE);
        }

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAuthorizedUserClick(position, userType);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemDataList.size();
    }

    public interface AuthorizedByUserAdapterListener {
        void onAuthorizedUserClick(int position, String userType);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout nameLayout, mainLayout;
        private TextView nameTitleTextView;
        private TextView nameTextView;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.main_layout);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            nameTitleTextView = (TextView) view.findViewById(R.id.name_title_textView);
            nameTextView = (TextView) view.findViewById(R.id.name_textView);
        }
    }
}

