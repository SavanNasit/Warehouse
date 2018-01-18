package com.accrete.warehouse.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.AlreadyAddedItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/20/17.
 */

public class AddedItemsAdapter extends RecyclerView.Adapter<AddedItemsAdapter.MyViewHolder> {
    private Context context;
    private AddedItemsAdapterListener listener;
    private List<AlreadyAddedItem> alreadyAddedItemList = new ArrayList<>();

    public AddedItemsAdapter(Context context, List<AlreadyAddedItem> alreadyAddedItemList, AddedItemsAdapterListener listener) {
        this.context = context;
        this.alreadyAddedItemList = alreadyAddedItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_added_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final AlreadyAddedItem alreadyAddedItem = alreadyAddedItemList.get(position);
        holder.alreadyAddedItemsId.setText(alreadyAddedItem.getPackageID());
        holder.alreadyAddedItemsRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alreadyAddedItemList.size() > 0 && alreadyAddedItemList.get(position) != null) {
                    try {
                        alreadyAddedItemList.remove(position);
                        notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return alreadyAddedItemList.size();
    }

    private void applyClickEvents(DeliveredAdapter.MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface AddedItemsAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView alreadyAddedItemsId;
        private TextView alreadyAddedItemsRemove;

        public MyViewHolder(View view) {
            super(view);
            alreadyAddedItemsId = (TextView) view.findViewById(R.id.already_added_items_id);
            alreadyAddedItemsRemove = (TextView) view.findViewById(R.id.already_added_items_remove);
        }
    }

}
