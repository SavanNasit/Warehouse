package com.accrete.sixorbit.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ChatContacts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 12/6/17.
 */

public class ChatContactsHorizontalAdapter extends RecyclerView.Adapter<ChatContactsHorizontalAdapter.MyViewHolder> {


    private final FragmentActivity mContext;
    private List<ChatContacts> chatContacts = new ArrayList<>();
    private ChatContactsHorizontalAdapterListener listener;

    public ChatContactsHorizontalAdapter(FragmentActivity activity, List<ChatContacts> chatList, ChatContactsHorizontalAdapterListener listener) {
        this.mContext = activity;
        this.chatContacts = chatList;
        this.listener = listener;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_chat_contacts_horizontal, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        // displaying text view data
        holder.numberOfPings.setText("12");
        // holder.imageProfile.setImageDrawable(R.drawable.bg_circle_red);

        // display profile image
        // applyProfilePicture(holder,);

        // apply click events
        applyClickEvents(holder, position);
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {

        holder.contentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return chatContacts.size();
    }



    public interface ChatContactsHorizontalAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView numberOfPings;
        public ImageView imgProfile;
        public LinearLayout contentContainer;

        public MyViewHolder(View view) {
            super(view);
            numberOfPings = (TextView) view.findViewById(R.id.number_of_pings);
            imgProfile = (ImageView) view.findViewById(R.id.icon_profile);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
        }

    }
}