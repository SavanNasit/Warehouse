package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.RecentLogin;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 21/6/17.
 */

public class RecentLoginListAdapter extends RecyclerView.Adapter<RecentLoginListAdapter.MyViewHolder> {

    public List<RecentLogin> recentLoginList = new ArrayList<>();
    public Context context;
    public RecentLoginListAdapter(Context applicationContext, List<RecentLogin> recentLoginList) {
      this.context = applicationContext;
      this.recentLoginList=recentLoginList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_recent_login, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
         RecentLogin recentLogin = recentLoginList.get(position);
         holder.textViewTime.setText(recentLogin.getTime());
        holder.textViewIpAddress.setText(recentLogin.getIpaddress());
        if(recentLogin.getUssid().equals("1")){
            holder.textViewStatus.setText("Active");
            holder.textViewStatus.setTextColor(Color.GREEN);
        }else{
            holder.textViewStatus.setText("Inactive");
            holder.textViewStatus.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return recentLoginList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewTime, textViewStatus,textViewIpAddress ;

        public MyViewHolder(View view) {
            super(view);

            textViewTime = (TextView) view.findViewById(R.id.list_row_recent_login_time);
            textViewStatus = (TextView) view.findViewById(R.id.list_row_recent_login_status);
            textViewIpAddress = (TextView) view.findViewById(R.id.list_row_recent_login_ip_address);

        }
    }

}