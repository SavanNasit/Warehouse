package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackageStatusList;

import java.util.List;

/**
 * Created by poonam on 12/4/17.
 */

public class PackageStatusAdapter extends RecyclerView.Adapter<PackageStatusAdapter.MyViewHolder>  {
    Activity activity;
    private Context context;
    private List<PackageStatusList> packageStatusLists;
    private int mExpandedPosition = -1;
    private PackageStatusAdapterListener listener;

    public PackageStatusAdapter(Context context, List<PackageStatusList> packageStatusLists, PackageStatusAdapterListener listener) {
        this.context = context;
        this.packageStatusLists = packageStatusLists;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_package_history, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
      //  holder.selectDocName.setText("(" + packageStatusLists.get(position)+ ")");
        //applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return packageStatusLists.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PackageStatusAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView selectDocName;
        private ImageView deleteSelectedDoc;


        public MyViewHolder(View view) {
            super(view);
           // selectDocName = (TextView)view.findViewById( R.id.select_doc_name );
            //deleteSelectedDoc = (ImageView)view.findViewById( R.id.delete_selected_doc );
        }
    }

}
