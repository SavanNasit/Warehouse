package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.model.ChatContacts;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by poonam on 12/6/17.
 */

public class ChatContactsAdapter extends RecyclerView.Adapter<ChatContactsAdapter.MyViewHolder>
        implements FastScrollRecyclerView.SectionedAdapter,
        FastScrollRecyclerView.MeasurableAdapter {
    private final Activity mContext;
    public List<ChatContacts> chatContacts = new ArrayList<>();
    private ChatContactsAdapterListener listener;

    public ChatContactsAdapter(Activity activity, List<ChatContacts> chatList, ChatContactsAdapterListener listener
    ) {
        this.mContext = activity;
        this.chatContacts = chatList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        //return String.valueOf(position);
        return chatContacts.get(position).getName().substring(0, 1);
    }

    @Override
    public int getViewTypeHeight(RecyclerView recyclerView, int viewType) {
        return recyclerView.getResources().getDimensionPixelSize(R.dimen._15sdp);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_chat_contacts, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ChatContacts chatContact = chatContacts.get(position);
        // displaying text view data
        holder.nameTextView.setText(chatContact.getName().toString().trim());
        if (chatContact.getMobile().isEmpty() || chatContact.getMobile() == null || chatContact.getMobile().equals("null")) {
            holder.numberTextView.setVisibility(View.GONE);
            holder.numberTextView.setText("");
        } else {
            holder.numberTextView.setText(chatContact.getMobile().toString().trim());
            holder.numberTextView.setVisibility(View.VISIBLE);
        }

        String url = chatContact.getPhoto();
        if (chatContact.getImagePath() != null && !chatContact.getImagePath().isEmpty()) {// && loadImageFromStorage(chatContact.getImagePath()) != null) {
            //Displaying images into background thread
            File file = new File(chatContact.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(mContext)
                    .load(imageUri).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(holder.imageProfile);
        } else {
            Glide.with(mContext).load(url)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(holder.imageProfile);
        }

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

    public interface ChatContactsAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, numberTextView;
        public CircleImageView imageProfile;
        public LinearLayout contentContainer;

        public MyViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.chat_contact_name);
            numberTextView = (TextView) view.findViewById(R.id.chat_contact_number);
            imageProfile = (CircleImageView) view.findViewById(R.id.icon_profile);
            contentContainer = (LinearLayout) view.findViewById(R.id.message_container);
        }

    }
}