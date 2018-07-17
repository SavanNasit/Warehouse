package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.lead.ActivityFeedsCommentActivity;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Notification;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by poonam on 20/4/17.
 */

public class AllActivitiesAdapter extends RecyclerView.Adapter<AllActivitiesAdapter.MyViewHolder> {
    public List<Notification> notifications;
    DatabaseHandler db;
    private Context mContext;
    private SparseBooleanArray selectedItems;
    private List<String> items = new ArrayList<>();
    private int mExpandedPosition = -1;
    private DatabaseHandler databaseHandler;

    //implements BubbleTextGetter
  /*  @Override
    public String getTextToShowInBubble(int pos) {
        //return Character.toString(notifications.get(pos).getMessage().charAt(0));
        return Character.toString(items.get(pos).charAt(0));

    }
*/
    public AllActivitiesAdapter(Context mContext, List<Notification> notifications, int numOfCount) {
        this.mContext = mContext;
        this.notifications = notifications;
        selectedItems = new SparseBooleanArray();
        List<String> items = new ArrayList<>();

        java.util.Random r = new java.util.Random();
        for (int i = 0; i < numOfCount; i++)
            items.add(((char) ('A' + r.nextInt('Z' - 'A'))) + " " + Integer.toString(i));
        java.util.Collections.sort(items);
        this.items = items;
        if (mContext != null)
            databaseHandler = new DatabaseHandler(mContext);

    }

    //deprecated for N
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_notifications, parent, false);
        db = new DatabaseHandler(mContext);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Notification notification = notifications.get(position);

        // displaying text view data
        String messageText = notification.getMessage();
        holder.message.setText(messageText);
        /// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        /*long time = 0;
        try {
            time = sdf.parse(notification.getCreatedTs()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        //long now = System.currentTimeMillis();

        ChatContacts chatContacts  = databaseHandler.getUserData(Integer.valueOf(notification.getUid()));

        if (chatContacts.getImagePath() != null) {
            //Displaying images into background thread
            File file = new File(chatContacts.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(mContext)
                    .load(imageUri).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                    .into(holder.imgProfile);
        }

        if(notification.getUid().equals(AppPreferences.getUserId(mContext, AppUtils.USER_ID))){
            Glide.with(mContext)
                    .load(AppPreferences.getPhoto(mContext, AppUtils.USER_PHOTO))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_neutral_profile)
                    .into(holder.imgProfile);

        }
        holder.username.setText(chatContacts.getName());
        if (chatContacts.getDesignation() != null && !chatContacts.getDesignation().isEmpty()) {
            holder.designation.setText(chatContacts.getDesignation());
            holder.designation.setVisibility(View.VISIBLE);
        } else {
            holder.designation.setVisibility(View.GONE);
        }


        //CharSequence ago =
        //         DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
        //holder.username.setText("Anshul Tyagi");

        for (int i = 0; i < notifications.size(); i++) {
            items.add(notifications.get(i).getMessage());
        }

        // displaying the font awesome icon and background color
        /*try {
            if (notification.getFontawesomeicons() != null && notification.getFontawesomeicons() != "") {
                Typeface font = Typeface.createFromAsset(mContext.getAssets(), mContext.getString(R.string.font_awesome));
                holder.designation.setText(fromHtml(notification.getFontawesomeicons()));
                holder.designation.setTypeface(font);
            } else {
                holder.designation.setText(notification.getMotaid().substring(0, 1));
                holder.designation.setAllCaps(true);
            }

            applyProfilePicture(holder, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        try {
            if (notification.getCreatedTs() != null) {
                Date pastDate = simpleDateFormat.parse(notification.getCreatedTs());
                Date currentDate = new Date();
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
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minutes_ago));
                        } else
                            holder.postTime.setText(minutes + " " + mContext.getString(R.string.minutes_ago));
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hours_ago));
                        } else
                            holder.postTime.setText(hours + " " + mContext.getString(R.string.hours_ago));
                    } else {
                        if ((currentDate.getDate() - pastDate.getDate() == 1) && (currentDate.getMonth() == pastDate.getMonth())
                                && (currentDate.getYear() == pastDate.getYear())) {
                            holder.postTime.setText(mContext.getString(R.string.yesterday));
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
                        if (pastDate.getDate() - currentDate.getDate() == 1 && (currentDate.getMonth() == pastDate.getMonth())
                                && (currentDate.getYear() == pastDate.getYear())) {
                            holder.postTime.setText(mContext.getString(R.string.tommorow));
                        } else
                            holder.postTime.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final boolean isExpanded = position == mExpandedPosition;
//        holder.expandView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityFeedsCommentActivity.class);
                intent.putExtra(mContext.getString(R.string.uid), notification.getUid());
                intent.putExtra(mContext.getString(R.string.uaid), notification.getUaid());
                intent.putExtra(mContext.getString(R.string.uName), holder.username.getText().toString().trim());
                intent.putExtra(mContext.getString(R.string.post_message), holder.message.getText().toString().trim());
                intent.putExtra(mContext.getString(R.string.post_time), holder.postTime.getText().toString().trim());
                ((Activity) mContext).startActivityForResult(intent, AppUtils.FEEDS_RESULT_CODE);
            }
        });
        holder.commentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ActivityFeedsCommentActivity.class);
                intent.putExtra(mContext.getString(R.string.uid), notification.getUid());
                intent.putExtra(mContext.getString(R.string.uaid), notification.getUaid());
                intent.putExtra(mContext.getString(R.string.uName), holder.username.getText().toString().trim());
                intent.putExtra(mContext.getString(R.string.post_message), holder.message.getText().toString().trim());
                intent.putExtra(mContext.getString(R.string.post_time), holder.postTime.getText().toString().trim());
                ((Activity) mContext).startActivityForResult(intent, AppUtils.FEEDS_RESULT_CODE);
            }
        });

        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "" + holder.message.getText().toString().trim()
                        + "\nBy: " + holder.username.getText().toString().trim());
                mContext.startActivity(Intent.createChooser(sharingIntent, mContext.getString(R.string.share_post)));
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                // collapse any currently expanded items
                if (mExpandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(mExpandedPosition);
                }
                // enableExpandAndCollapse(holder);
                notifyDataSetChanged();

            }
        });
        // change the row state to activated
        holder.itemView.setActivated(selectedItems.get(position, false));
        //GetCounts from database and display
        if (databaseHandler.getCommentsCount(notification.getUaid()) == 0) {
            holder.commentTextView.setText(mContext.getString(R.string.no_comments));
        } else if (databaseHandler.getCommentsCount(notification.getUaid()) == 1) {
            holder.commentTextView.setText(databaseHandler.getCommentsCount(notification.getUaid()) + " " + mContext.getString(R.string.comment));
        } else
            holder.commentTextView.setText(databaseHandler.getCommentsCount(notification.getUaid()) + " " + mContext.getString(R.string.comments));
    }

    private void applyProfilePicture(MyViewHolder holder, Notification notifications) {
        holder.imgProfile.setImageResource(R.drawable.bg_square);
        holder.imgProfile.setColorFilter(Color.parseColor(notifications.getFontbackgroundcolor()));
        holder.designation.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView username, message, designation, postTime, commentTextView;
        public ImageView imgProfile, comment;
        public LinearLayout contentContainer, expandView, commentButton, shareButton;
        public RelativeLayout iconContainer, iconBack, iconFront, relativeLayoutContainer;

        public MyViewHolder(View view) {
            super(view);
            username = (TextView) view.findViewById(R.id.user_name);
            message = (TextView) view.findViewById(R.id.message_text);
            designation = (TextView) view.findViewById(R.id.user_designation);
            postTime = (TextView) view.findViewById(R.id.post_time);
            iconBack = (RelativeLayout) view.findViewById(R.id.icon_back);
            imgProfile = (ImageView) view.findViewById(R.id.profile_image);
            comment = (ImageView) view.findViewById(R.id.imgProfile);
            contentContainer = (LinearLayout) view.findViewById(R.id.notification_container);
            expandView = (LinearLayout) view.findViewById(R.id.notifications_expand_view);
            commentButton = (LinearLayout) view.findViewById(R.id.comment_button);
            shareButton = (LinearLayout) view.findViewById(R.id.share_button);
            iconContainer = (RelativeLayout) view.findViewById(R.id.icon_container);
            relativeLayoutContainer = (RelativeLayout) view.findViewById(R.id.relativelayout_container);
            commentTextView = (TextView) view.findViewById(R.id.comment_text);
        }

    }

}