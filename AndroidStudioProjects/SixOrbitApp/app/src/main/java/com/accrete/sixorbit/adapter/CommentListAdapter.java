package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by amp on 6/9/17.
 */

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.MyViewHolder> {
    Context mContext;
    private List<Comment> commentList;
    private String uId;
    private DatabaseHandler databaseHandler;

    public CommentListAdapter(Context mContext, List<Comment> commentList, String uId) {
        this.commentList = commentList;
        this.uId = uId;
        this.mContext = mContext;
        if (mContext != null)
            databaseHandler = new DatabaseHandler(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_comments, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Comment comment = commentList.get(position);

        holder.comment.setText(comment.getComment());

        ChatContacts chatContacts = new ChatContacts();
        chatContacts = databaseHandler.getUserData(Integer.valueOf(comment.getUid()));


        if (chatContacts.getImagePath() != null) {
            //Displaying images into background thread
            File file = new File(chatContacts.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(mContext)
                    .load(imageUri).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                    .into(holder.profileImageView);
        }

        if(comment.getUid().equals(AppPreferences.getUserId(mContext, AppUtils.USER_ID))){
            Glide.with(mContext)
                    .load(AppPreferences.getPhoto(mContext, AppUtils.USER_PHOTO))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_neutral_profile)
                    .into(holder.profileImageView);
        }


        holder.profileName.setText(chatContacts.getName());
        if (chatContacts.getDesignation() != null && !chatContacts.getDesignation().isEmpty()) {
            holder.profileProfession.setText(chatContacts.getDesignation());
            holder.profileProfession.setVisibility(View.VISIBLE);
        } else {
            holder.profileProfession.setVisibility(View.GONE);
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        try {
            if (comment.getCreatedTs() != null) {
                Date pastDate = simpleDateFormat.parse(comment.getCreatedTs());
                Date currentDate = new Date();
                Calendar now = Calendar.getInstance();
                long nowInMillis = SystemClock.uptimeMillis();

                Calendar now_calendar = Calendar.getInstance();
                now_calendar.setTimeInMillis(nowInMillis);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(pastDate.getTime() - currentDate.getTime());

                if (seconds < 0) {
                    seconds = TimeUnit.MILLISECONDS.toSeconds(currentDate.getTime() - pastDate.getTime());
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(currentDate.getTime() - pastDate.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(currentDate.getTime() - pastDate.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(currentDate.getTime() - pastDate.getTime());
                    if (seconds < 60) {
                        if (seconds == 0) {
                            holder.postTime.setText(mContext.getString(R.string.just_now));
                        } else if (seconds == 1) {
                            holder.postTime.setText(seconds + " " + mContext.getString(R.string.second_ago));
                        } else {
                            holder.postTime.setText(seconds + " " + mContext.getString(R.string.seconds_ago));
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minute_ago));
                        } else
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minutes_ago));
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hour_ago));
                        } else
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hours_ago));
                    } else {
                        if (currentDate.getDate() - pastDate.getDate() == 1) {
                            holder.postTime.setText(R.string.yesterday);
                        } else
                            holder.postTime.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                } else {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(pastDate.getTime() - currentDate.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(pastDate.getTime() - currentDate.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(pastDate.getTime() - currentDate.getTime());
                    if (seconds < 60) {
                        if (seconds == 0) {
                            holder.postTime.setText(mContext.getString(R.string.just_now));
                        } else if (seconds == 1) {
                            holder.postTime.setText(seconds + " " + mContext.getString(R.string.second_ago));
                        } else {
                            holder.postTime.setText(seconds + " " + mContext.getString(R.string.seconds_ago));
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minute_later));
                        } else
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minutes_later));
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hour_later));
                        } else
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hours_later));
                    } else {
                        if (pastDate.getDate() - currentDate.getDate() == 1) {
                            holder.postTime.setText(R.string.tommorow);
                        } else
                            holder.postTime.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView comment, profileName, profileProfession, postTime;
        public ImageView profileImageView;

        public MyViewHolder(View view) {
            super(view);
            comment = (TextView) view.findViewById(R.id.textView_comment_comment);
            profileName = (TextView) view.findViewById(R.id.textView_profile_name_comment);
            profileProfession = (TextView) view.findViewById(R.id.textView_designation_comment);
            profileImageView = (ImageView) view.findViewById(R.id.profile_image);
            postTime = (TextView) view.findViewById(R.id.post_time_textView);
        }
    }
}
