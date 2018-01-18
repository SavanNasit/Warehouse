package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PackageDetailsList;

import java.util.List;

/**
 * Created by poonam on 11/29/17.
 */

public class PackageDetailsAdapter extends RecyclerView.Adapter<PackageDetailsAdapter.MyViewHolder> {

    PackageDetailsAdapterListener listener;
    private Context context;
    private List<PackageDetailsList> packageDetailsLists;

    public PackageDetailsAdapter(Context context, List<PackageDetailsList> packageDetailsLists, PackageDetailsAdapterListener listener) {
        this.context = context;
        this.packageDetailsLists = packageDetailsLists;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_package_details, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        PackageDetailsList packageDetailsList = packageDetailsLists.get(position);
        if(holder.listRowPackageDetailsItem.length()>25){
            holder.listRowPackageDetailsItem.setText(packageDetailsList.getItem().substring(0,25)+"...");
        }else{
            holder.listRowPackageDetailsItem.setText(packageDetailsList.getItem());
        }

        holder.listRowPackageDetailsBatchNumber.setText(packageDetailsList.getBatchNumber());
        holder.listRowPackageDetailsQuantity.setText(packageDetailsList.getQuantity());
        holder.listRowPackageDetailsRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExecute();
            }
        });

        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return packageDetailsLists.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface PackageDetailsAdapterListener {
        void onMessageRowClicked(int position);
        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView listRowPackageDetailsItem;
        private TextView listRowPackageDetailsBatchNumber;
        private TextView listRowPackageDetailsQuantity;
        private TextView listRowPackageDetailsRemove;


        public MyViewHolder(View view) {
            super(view);
                listRowPackageDetailsItem = (TextView)view.findViewById( R.id.list_row_package_details_item );
                listRowPackageDetailsBatchNumber = (TextView)view.findViewById( R.id.list_row_package_details_batch_number );
                listRowPackageDetailsQuantity = (TextView)view.findViewById( R.id.list_row_package_details_quantity );
                listRowPackageDetailsRemove = (TextView)view.findViewById( R.id.list_row_package_details_remove );
            }
          }
    }


