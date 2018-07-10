package com.accrete.sixorbit.activity.lead;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CommentListAdapter;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.receiver.ApiResultReceiver;
import com.accrete.sixorbit.service.ApiCallService;
import com.accrete.sixorbit.service.PostFeedsCommentsService;
import com.accrete.sixorbit.service.PushNotificationsTimeService;
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

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by amp on 6/9/17.
 */

public class ActivityFeedsCommentActivity extends AppCompatActivity implements ApiResultReceiver.Receiver {
    public String editTextcommentedstring;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Comment> commentList = new ArrayList<>();
    private String loggedinUId;
    private CommentListAdapter mAdapter;
    private String postTimeStr, postMessageStr, uidStr, uaidStr, uName;
    private LinearLayout listPostItem;
    private CircleImageView profileImage;
    private TextView nameUserTextView;
    private TextView userDesignationTextView;
    private TextView postTimeTextView;
    private TextView messageTextView;
    private LinearLayout notificationsExpandView;
    private LinearLayout commentButton;
    private ImageView imgProfile;
    private LinearLayout shareButton;
    private TextView commentsTitleTextView;
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private CircleImageView activittyfeedsCommentProfie;
    private EditText editTextComment;
    private ImageView sendComment;
    private DatabaseHandler databaseHandler;
    private ApiResultReceiver mReceiver;
    private boolean updateListBackScreen;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activityfeeds_comment);
        databaseHandler = new DatabaseHandler(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back_arrow));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra(getString(R.string.notify_id), -1);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
        if (PushNotificationsTimeService.feedsMap.size() <= 1) {
            notificationManager.cancel(1111);
        }
        PushNotificationsTimeService.feedsMap.remove(AppPreferences.getFeedNotificationNumber(this, AppUtils.PUSH_FEEDS_NOTIFICATION_NUMBER));
        notificationManager.cancel(AppPreferences.getFeedNotificationNumber(this, AppUtils.PUSH_FEEDS_NOTIFICATION_NUMBER));

        if (getIntent() != null && getIntent().hasExtra("notify_id")) {
            updateListBackScreen = true;
        }

        mReceiver = new ApiResultReceiver(new Handler());
        mReceiver.setReceiver(this);

        listPostItem = (LinearLayout) findViewById(R.id.list_post_item);
        profileImage = (CircleImageView) findViewById(R.id.profile_image);
        nameUserTextView = (TextView) findViewById(R.id.name_user_textView);
        userDesignationTextView = (TextView) findViewById(R.id.user_designation_textView);
        postTimeTextView = (TextView) findViewById(R.id.post_time_textView);
        messageTextView = (TextView) findViewById(R.id.message_textView);
        notificationsExpandView = (LinearLayout) findViewById(R.id.notifications_expand_view);
        commentButton = (LinearLayout) findViewById(R.id.comment_button);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        shareButton = (LinearLayout) findViewById(R.id.share_button);
        commentsTitleTextView = (TextView) findViewById(R.id.comments_title_textView);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        emptyTextView = (TextView) findViewById(R.id.lead_empty_view);
        activittyfeedsCommentProfie = (CircleImageView) findViewById(R.id.activittyfeeds_comment_Profie);
        editTextComment = (EditText) findViewById(R.id.editTextComment);
        sendComment = (ImageView) findViewById(R.id.send_comment);
//        commentThecomment = (ImageView)findViewById(R.id.send_comment) ;
        mLinearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setNestedScrollingEnabled(false);

        intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(getString(R.string.uid))) {
                uidStr = intent.getStringExtra(getString(R.string.uid));
            }
            if (intent.hasExtra(getString(R.string.post_message))) {
                postMessageStr = intent.getStringExtra(getString(R.string.post_message));
            }
            if (intent.hasExtra(getString(R.string.post_time))) {
                postTimeStr = intent.getStringExtra(getString(R.string.post_time));
            }
            if (intent.hasExtra(getString(R.string.uaid))) {
                uaidStr = intent.getStringExtra(getString(R.string.uaid));
            }
            if (intent.hasExtra(getString(R.string.uName))) {
                uName = intent.getStringExtra(getString(R.string.uName));
            }
        }
        getSupportActionBar().setTitle(getString(R.string.activity_by) + uName);
        mAdapter = new CommentListAdapter(ActivityFeedsCommentActivity.this, commentList, uidStr);
        messageTextView.setText(postMessageStr);
        //Set Time
        //setPostTime(postTimeTextView, postTimeStr);
        postTimeTextView.setText(postTimeStr);

        if (AppPreferences.getIsLogin(ActivityFeedsCommentActivity.this, AppUtils.ISLOGIN)) {
            loggedinUId = AppPreferences.getUserId(ActivityFeedsCommentActivity.this, AppUtils.USER_ID);
        }

        //Chat contacts of Posted user
        ChatContacts chatContacts = databaseHandler.getUserData(Integer.valueOf(uidStr));
        getSupportActionBar().setTitle("Activity by " + chatContacts.getName());
        //Chat contacts of loggedin user
        ChatContacts chatContactsUser = databaseHandler.getUserData(Integer.valueOf(loggedinUId));

        if (chatContacts.getImagePath() != null) {
            //Displaying images into background thread
            File file = new File(chatContacts.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(this)
                    .load(imageUri).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                    .into(profileImage);
        }
       /* if (chatContactsUser.getImagePath() != null) {
            //Displaying images into background thread
            File file = new File(chatContactsUser.getImagePath());
            Uri imageUri = Uri.fromFile(file);
            Glide.with(this)
                    .load(imageUri).placeholder(R.mipmap.ic_launcher)
                    .diskCacheStrategy(DiskCacheStrategy.ALL).dontAnimate()
                    .into(activittyfeedsCommentProfie);
        }*/

        if(uidStr.equals(AppPreferences.getUserId(this, AppUtils.USER_ID))){
            Glide.with(this)
                    .load(AppPreferences.getPhoto(this, AppUtils.USER_PHOTO))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.icon_neutral_profile)
                    .into(profileImage);


        }

        Glide.with(this)
                .load(AppPreferences.getPhoto(this, AppUtils.USER_PHOTO))
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.icon_neutral_profile)
                .into(activittyfeedsCommentProfie);

        nameUserTextView.setText(chatContacts.getName());
        if (chatContacts.getDesignation() != null && !chatContacts.getDesignation().isEmpty()) {
            userDesignationTextView.setText(chatContacts.getDesignation());
            userDesignationTextView.setVisibility(View.VISIBLE);
        } else userDesignationTextView.setVisibility(View.GONE);
        editTextComment.requestFocus();
        editTextComment.setCursorVisible(true);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        commentList = databaseHandler.getActivityFeedsComments(uaidStr);
        mAdapter = new CommentListAdapter(ActivityFeedsCommentActivity.this, commentList, uidStr);
        recyclerView.setAdapter(mAdapter);
        if (commentList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            commentsTitleTextView.setVisibility(View.VISIBLE);
        } else {
            commentsTitleTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_comments));
        }
        //   mAdapter.notifyDataSetChanged();
        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepareComments();
            }
        });


        //Click Listeners
        editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0 || editTextComment.getText().toString().trim().length() == 0) {
                    sendComment.setImageResource(R.drawable.ic_send_hover);
                    sendComment.setEnabled(false);
                } else {
                    sendComment.setImageResource(R.drawable.ic_send);
                    sendComment.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        sendComment.setEnabled(false);


    }

    private void scrollToBottom() {
        recyclerView.scrollToPosition(commentList.size());
    }

    public void displayCommentsInView() {
        if (commentList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            commentsTitleTextView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            commentsTitleTextView.setVisibility(View.VISIBLE);
        }
        mAdapter.notifyDataSetChanged();
        emptyTextView.setVisibility(View.GONE);
        commentsTitleTextView.setVisibility(View.VISIBLE);
        scrollToBottom();
    }

    private void prepareComments() {
        //Create sync id
        long time_nano = System.nanoTime();
        long micro_seconds = time_nano / 1000;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        editTextcommentedstring = editTextComment.getText().toString();
        Comment comment = new Comment();

        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
        if (TextUtils.isEmpty(editTextcommentedstring)) {
            return;
        }
        comment.setComment(editTextcommentedstring);
        comment.setUacid("0");
        comment.setUaid(uaidStr);
        comment.setUid(loggedinUId);
        comment.setCreatedTs(simpleDateFormat.format(new Date()));
        comment.setSid("");
        comment.setSync_id(String.valueOf(micro_seconds));
        comment.setPost_uid(uidStr);
        commentList.add(comment);
        if (recyclerView.getVisibility() == View.GONE) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        mAdapter = new CommentListAdapter(ActivityFeedsCommentActivity.this, commentList, uidStr);
        displayCommentsInView();
        mAdapter.notifyDataSetChanged();
        editTextComment.setText("");
        comment.setSync(0);
        databaseHandler.insertActivityFeedsComments(comment);

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(this).equals(R.string.not_connected_to_internet)) {
            Intent intent_service = new Intent(Intent.ACTION_SYNC, null, ActivityFeedsCommentActivity.this,
                    PostFeedsCommentsService.class);
            intent_service.putExtra(getString(R.string.uaid), uaidStr);
            intent_service.putExtra(getString(R.string.comment_small), editTextcommentedstring);
            intent_service.putExtra(getString(R.string.sid), "");
            intent_service.putExtra(getString(R.string.post_uid), uidStr);
            intent_service.putExtra(getString(R.string.syncid), String.valueOf(micro_seconds));
            startService(intent_service);
        }
        updateListBackScreen = true;
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case ApiCallService.STATUS_RUNNING:
                break;
            case ApiCallService.STATUS_FINISHED:
                final ApiResponse results = resultData.getParcelable(getString(R.string.followup));
                break;
            case ApiCallService.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (updateListBackScreen == true) {
            Intent intent = new Intent();
            intent.putExtra("updateListBackScreen", updateListBackScreen);
            intent.putExtra("CODE", AppUtils.FEEDS_RESULT_CODE);
            setResult(AppUtils.FEEDS_RESULT_CODE, intent);
            finish();
        }
    }

    public void setPostTime(TextView postTimeTextView, String postTimeStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        try {
            if (postTimeStr != null) {
                Date pastDate = simpleDateFormat.parse(postTimeStr);
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
                            postTimeTextView.setText("Just Now");
                        } else if (seconds == 1) {
                            postTimeTextView.setText(seconds + " second ago");
                        } else {
                            postTimeTextView.setText(seconds + " seconds ago");
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            postTimeTextView.setText(minutes + " minute ago");
                        } else postTimeTextView.setText(minutes + " minutes ago");
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            postTimeTextView.setText(hours + " hour ago");
                        } else postTimeTextView.setText(hours + " hours ago");
                    } else {
                        if ((currentDate.getDate() - pastDate.getDate() == 1) && (currentDate.getMonth() == pastDate.getMonth())
                                && (currentDate.getYear() == pastDate.getYear())) {
                            postTimeTextView.setText("Yesterday");
                        } else
                            postTimeTextView.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                } else {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(pastDate.getTime() - currentDate.getTime());
                    long hours = TimeUnit.MILLISECONDS.toHours(pastDate.getTime() - currentDate.getTime());
                    long days = TimeUnit.MILLISECONDS.toDays(pastDate.getTime() - currentDate.getTime());
                    if (seconds < 60) {
                        if (seconds == 0) {
                            postTimeTextView.setText("Just Now");
                        } else if (seconds == 1) {
                            postTimeTextView.setText(seconds + " second later");
                        } else {
                            postTimeTextView.setText(seconds + " seconds later");
                        }
                    } else if (minutes < 60) {
                        if (minutes == 1) {
                            postTimeTextView.setText(minutes + " minute later");
                        } else postTimeTextView.setText(minutes + " minutes later");
                    } else if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                        if (hours == 1) {
                            postTimeTextView.setText(hours + " hour later");
                        } else postTimeTextView.setText(hours + " hours later");
                    } else {
                        if (pastDate.getDate() - currentDate.getDate() == 1 && (currentDate.getMonth() == pastDate.getMonth())
                                && (currentDate.getYear() == pastDate.getYear())) {
                            postTimeTextView.setText("Tomorrow");
                        } else
                            postTimeTextView.setText("" + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate));
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}