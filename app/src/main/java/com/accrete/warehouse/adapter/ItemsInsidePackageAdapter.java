package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ItemsInsidePackage;

import java.util.List;

/**
 * Created by poonam on 12/5/17.
 */

public class ItemsInsidePackageAdapter extends RecyclerView.Adapter<ItemsInsidePackageAdapter.MyViewHolder> {

    private Context context;
    private List<ItemsInsidePackage> itemsInsidePackageList;
    private ItemsInsidePackageAdapterListener listener;

    public ItemsInsidePackageAdapter(Context context, List<ItemsInsidePackage> itemsInsidePackageList, ItemsInsidePackageAdapterListener listener) {
        this.context = context;
        this.itemsInsidePackageList = itemsInsidePackageList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_items_in_package, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ItemsInsidePackage itemsInsidePackage = itemsInsidePackageList.get(position);
        holder.itemName.setText(itemsInsidePackage.getItemName());
        holder.itemQuantity.setText(itemsInsidePackage.getQuantity());
        holder.itemDescription.setText(itemsInsidePackage.getDescription());

        //applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return itemsInsidePackageList.size();

    }

    private void applyClickEvents(DocumentUploaderAdapter.MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface ItemsInsidePackageAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private TextView itemQuantity;
        private TextView itemDescription;


        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.item_name);
            itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
            itemDescription = (TextView) view.findViewById(R.id.item_description);
        }
    }

}
