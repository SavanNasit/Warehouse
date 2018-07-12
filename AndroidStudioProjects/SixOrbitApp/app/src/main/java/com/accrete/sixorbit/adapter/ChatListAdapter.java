package com.accrete.sixorbit.adapter;

import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by poonam on 12/6/17.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.MyViewHolder> {
    private final FragmentActivity mContext;
    private List<ChatMessage> chatList = new ArrayList<>();
    private ChatListAdapterListener listener;
    private DatabaseHandler databaseHandler;

    public ChatListAdapter(FragmentActivity activity, List<ChatMessage> chatList, ChatListAdapterListener listener) {
        this.mContext = activity;
        this.chatList = chatList;
        this.listener = listener;
        if (mContext != null)
            databaseHandler = new DatabaseHandler(mContext);
    }

    @Override
    public ChatListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_chat_list, parent, false);
        return new ChatListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatMessage chatMessage = chatList.get(position);

        // apply click events
        applyClickEvents(holder, position);

        ChatContacts chatContacts = new ChatContacts();
        chatContacts = databaseHandler.getUserData(Integer.parseInt(chatMessage.getUid()));

        if (chatContacts.getOnlineStatus() != null && !chatContacts.getOnlineStatus().isEmpty()) {
            if (chatContacts.getOnlineStatus().equals(mContext.getString(R.string.online_status))) {
                holder.onlineStatusImageView.setImageResource(R.drawable.online_circle);
            } else {
                holder.onlineStatusImageView.setImageResource(R.drawable.offline_cirlce);
            }
        }

        // displaying text view data
        if (chatContacts.getName() != null) {
            holder.nameTextView.setText(chatContacts.getName().toString().trim());
        }

        //holder.messageTextView.setText(databaseHandler.getlastChatMessage(chatMessage.getUid()));

        if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
            holder.messageTextView.setText(chatMessage.getMessage());
            holder.fileTypeImageView.setVisibility(View.GONE);
        }
        if (chatMessage.getFileType() != null) {
            if (chatMessage.getFileType().equals("jpg") || chatMessage.getFileType().equals("png") ||
                    chatMessage.getFileType().equals("jpeg") || chatMessage.getFileType().equals("bmp")) {
                if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
                    holder.messageTextView.setText(chatMessage.getMessage());
                } else {
                    holder.messageTextView.setText("Photo");
                }
                holder.fileTypeImageView.setVisibility(View.VISIBLE);
                holder.fileTypeImageView.setImageResource(android.R.drawable.ic_menu_camera);
            } else if (chatMessage.getFileType().contains("xls") || chatMessage.getFileType().equals("doc") ||
                    chatMessage.getFileType().equals("pdf")) {
                if (chatMessage.getMessage() != null && !chatMessage.getMessage().isEmpty()) {
                    holder.messageTextView.setText(chatMessage.getMessage());
                } else if (chatMessage.getFileName() != null) {
                    holder.messageTextView.setText(chatMessage.getFileName().substring(chatMessage.getFileName().lastIndexOf("/") + 1));
                } else {
                    holder.messageTextView.setText("");
                }
                holder.fileTypeImageView.setVisibility(View.VISIBLE);
                holder.fileTypeImageView.setImageResource(R.drawable.ic_description_black_24dp);
            }
        }

        if (chatContacts.getImagePath() != null && !chatContacts.getImagePath().isEmpty()) {
            //&& loadImageFromStorage(chatContacts.getImagePath()) != null) {

            //Displaying images into background thread
            File file = new File(chatContacts.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(mContext)
                    .load(imageUri)
                    .placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(holder.imgProfile);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date current_itemDate = simpleDateFormat.parse(chatMessage.getCreatedTs());

            DateFormat outputFormat = new SimpleDateFormat("hh:mm a");
            Date date = simpleDateFormat.parse(chatMessage.getCreatedTs());
            holder.timeTextView.setText(outputFormat.format(date).toString().trim());
            long previousTs = 0;
            if (position > 0) {
                ChatMessage pm = chatList.get(position - 1);
                previousTs = simpleDateFormat.parse(pm.getCreatedTs()).getTime();
            }
            setTimeTextVisibility(current_itemDate.getTime(), previousTs, holder.timeTextView);

        } catch (ParseException e) {
            e.printStackTrace();
        }

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
        return chatList.size();
    }

    private void setTimeTextVisibility(long now_tm, long msg_tm, TextView timeText) {
        Date nowDate = new Date();
        nowDate.setTime(now_tm);
        Date msgDate = new Date();
        msgDate.setTime(msg_tm);
        Calendar now_calendar = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        now_calendar.setTimeInMillis(now_tm);
        if (msg_tm == 0) {
            timeText.setVisibility(View.VISIBLE);
            if (DateUtils.isToday(now_calendar.getTimeInMillis())) {
                //Today Time
                timeText.setText("" + new SimpleDateFormat("hh:mm a").format(new Date(now_tm)));
            } else if ((now.get(Calendar.DATE) - now_calendar.get(Calendar.DATE) == 1) && (now.get(Calendar.MONTH) ==
                    now_calendar.get(Calendar.MONTH)) && (now.get(Calendar.YEAR) ==
                    now_calendar.get(Calendar.YEAR))) {
                //Yesterday
                timeText.setText(mContext.getString(R.string.yesterday));
            } else {
                //Previous Date
                timeText.setText("" + new SimpleDateFormat("dd MMM, yyyy").format(new Date(now_tm)));
            }
        } else {
            if (msgDate.before(nowDate)) {
                Calendar msg_calendar = Calendar.getInstance();
                msg_calendar.setTimeInMillis(msg_tm);
                boolean sameDay = now_calendar.get(Calendar.YEAR) == msg_calendar.get(Calendar.YEAR) &&
                        now_calendar.get(Calendar.MONTH) == msg_calendar.get(Calendar.MONTH)
                        && now_calendar.get(Calendar.DAY_OF_MONTH) == msg_calendar.get(Calendar.DAY_OF_MONTH);
                if (sameDay) {
                    //Today Time
                    timeText.setVisibility(View.VISIBLE);
                    timeText.setText("" + new SimpleDateFormat("hh:mm a").format(new Date(now_tm)));
                } else {
                    timeText.setVisibility(View.VISIBLE);
                    if (DateUtils.isToday(now_calendar.getTimeInMillis())) {
                        //Today Time
                        timeText.setText("" + new SimpleDateFormat("hh:mm a").format(new Date(now_tm)));
                    } else if ((now.get(Calendar.DATE) - now_calendar.get(Calendar.DATE) == 1) && (now.get(Calendar.MONTH) ==
                            now_calendar.get(Calendar.MONTH)) && (now.get(Calendar.YEAR) ==
                            now_calendar.get(Calendar.YEAR))) {
                        //Yesterday
                        timeText.setText(mContext.getString(R.string.yesterday));
                    } else {
                        //Previous Date
                        timeText.setText("" + new SimpleDateFormat("dd MMM, yyyy").format(new Date(now_tm)));
                    }
                }
            } else {
                //Today Time
                timeText.setVisibility(View.VISIBLE);
                // timeText.setText("" + new SimpleDateFormat("hh:mm a").format(new Date(now_tm)));
                if (DateUtils.isToday(now_calendar.getTimeInMillis())) {
                    //Today Time
                    timeText.setText("" + new SimpleDateFormat("hh:mm a").format(new Date(now_tm)));
                } else if (now.get(Calendar.DATE) - now_calendar.get(Calendar.DATE) == 1) {
                    //Yesterday
                    timeText.setText(mContext.getString(R.string.yesterday));
                } else {
                    //Previous Date
                    timeText.setText("" + new SimpleDateFormat("dd MMM, yyyy").format(new Date(now_tm)));
                }
            }
        }

    }

    public interface ChatListAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, messageTextView, timeTextView;
        public CircleImageView imgProfile;
        public RelativeLayout contentContainer;
        private ImageView onlineStatusImageView, fileTypeImageView;

        public MyViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.chat_list_name);
            onlineStatusImageView = (ImageView) view.findViewById(R.id.online_status_imageView);
            messageTextView = (TextView) view.findViewById(R.id.chat_list_latest_message);
            timeTextView = (TextView) view.findViewById(R.id.chat_list_time);
            imgProfile = (CircleImageView) view.findViewById(R.id.icon_profile);
            contentContainer = (RelativeLayout) view.findViewById(R.id.message_container);
            fileTypeImageView = (ImageView) view.findViewById(R.id.file_imageView);
        }

    }

}