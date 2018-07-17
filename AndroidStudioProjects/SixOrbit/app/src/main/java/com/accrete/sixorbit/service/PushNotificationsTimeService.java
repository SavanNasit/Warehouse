package com.accrete.sixorbit.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.format.DateUtils;
import android.widget.RemoteViews;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AppIllustration.ApplicationClass;
import com.accrete.sixorbit.activity.enquiry.EnquiryDetailActivity;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.activity.lead.ActivityFeedsCommentActivity;
import com.accrete.sixorbit.activity.lead.LeadInfoActivity;
import com.accrete.sixorbit.activity.order.OrdersTabActivity;
import com.accrete.sixorbit.activity.quotations.QuotationDetailsActivity;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.PushNotifications;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.accrete.sixorbit.utils.AppUtils.PUSH_NOTIFICATIONS_GROUP;

/**
 * Created by agt on 28/9/17.
 */
public class PushNotificationsTimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 60 * 1000; // 5 minutes
    private static final int NOTIFICATION_BUNDLED_BASE_ID = 1000;
    private static final int FEED_NOTIFICATION_BUNDLED_BASE_ID = 1111;
    private final static String GROUP_KEY_BUNDLED = "group_key_bundled";
    public static int m;
    public static HashMap<Integer, List<CharSequence>> dictMap =
            new HashMap<Integer, List<CharSequence>>();
    public static HashMap<Integer, List<CharSequence>> feedsMap =
            new HashMap<Integer, List<CharSequence>>();
    //simple way to keep track of the number of bundled notifications
    private static List<CharSequence> issuedMessages = new LinkedList<>();
    private static List<CharSequence> issuedFeedsMessages = new LinkedList<>();
    //simple way to keep track of the number of bundled notifications
    private static int numberOfBundled = 0, numberOfFeedBundled = 0;
    Random random = new Random();
    private PendingIntent contentLeadInfoIntent, contentRecordFollowupIntent, activityFeedsInfoIntent,
            contentOrdersInfoIntent, contentQuotationsInfoIntent, contentEnquiriesInfoIntent;
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private DatabaseHandler databaseHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }


        databaseHandler = new DatabaseHandler(getApplicationContext());
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //TODO Followups of Leads Only
    public void LeadsFollowUpsPushNotification(String id, String name, String leadId, String foId,
                                               String foSyncId, String scheduleDate, String communicationMode) {
        PushNotifications pushNotifications = new PushNotifications();
        pushNotifications.setFoid(foId);
        pushNotifications.setLeaid(leadId);
        pushNotifications.setMessage(scheduleDate);
        pushNotifications.setType(getString(R.string.follow_up_alert_title));

        databaseHandler.insertPushNotification(pushNotifications, String.valueOf(true));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        m = random.nextInt(9999 - 1000) + 1000;

        Intent notificationLeadInfoIntent = new Intent(this, LeadInfoActivity.class);
        notificationLeadInfoIntent.putExtra(getString(R.string.name), "" + name);
        notificationLeadInfoIntent.putExtra(getString(R.string.leaid), "" + leadId);
        notificationLeadInfoIntent.putExtra(getString(R.string.id), id);
        notificationLeadInfoIntent.putExtra(getString(R.string.notify_id), m);
        TaskStackBuilder stackBuilderLead = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderLead.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderLead.addNextIntent(notificationLeadInfoIntent);

        Intent notificationRecordFollowupintent = new Intent(this, RecordFollowUpActivity.class);
        if (leadId != null && !leadId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.lead_id), leadId);
        }
        if (foId != null && !foId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.foid), foId);
        }
        if (foSyncId != null && !foSyncId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.sync_id), foSyncId);
        }
        TaskStackBuilder stackBuilderRecordFollowUp = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderRecordFollowUp.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderRecordFollowUp.addNextIntent(notificationRecordFollowupintent);
        notificationRecordFollowupintent.putExtra(getString(R.string.notify_id), m);

        contentLeadInfoIntent = stackBuilderLead.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);
        contentRecordFollowupIntent = stackBuilderRecordFollowUp.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.followup_custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "" + getString(R.string.follow_up_alert_title));
        contentView.setTextViewText(R.id.view_lead_textView, getString(R.string.lead_info_title));
        contentView.setTextViewText(R.id.record_followup_textView, getString(R.string.record_follow_up_title));
        contentView.setOnClickPendingIntent(R.id.view_lead_textView, contentLeadInfoIntent);
        contentView.setOnClickPendingIntent(R.id.record_followup_textView, contentRecordFollowupIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("" + name);
        String contentText = "";
        if (communicationMode != null) {
            contentView.setTextViewText(R.id.text, " " + communicationMode + " " + getString(R.string.with) + " " + name + "  " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText("" + communicationMode + " " + getString(R.string.with) + " " + name + "  " + getDateTime(scheduleDate));
            contentText = "" + communicationMode + " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate);
        } else {
            contentView.setTextViewText(R.id.text, " " + getString(R.string.call) + " " + getString(R.string.with) + name + " " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText(getString(R.string.call) +
                    " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate));
            contentText = getString(R.string.call) + " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate);
        }


        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bundledFollowUpPushNotifications(contentText, contentLeadInfoIntent,
                    contentRecordFollowupIntent, getString(R.string.lead_info_title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Create the reply action and add the remote input.
            Notification.Action actionLead =
                    new Notification.Action.Builder(0,
                            getString(R.string.lead_info_title), contentLeadInfoIntent)
                            .build();
            Notification.Action actionRecordFollowUp =
                    new Notification.Action.Builder(0,
                            getString(R.string.record_follow_up_title), contentRecordFollowupIntent)
                            .build();

            // Build the notification and add the action.
            Notification newMessageNotification =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.follow_up_alert_title))
                            .setContentText(contentText)
                            .setStyle(new Notification.BigTextStyle().bigText(contentText))
                            .addAction(actionLead)
                            .addAction(actionRecordFollowUp)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setContentIntent(contentRecordFollowupIntent)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setGroup(PUSH_NOTIFICATIONS_GROUP)
                            .build();

            // Issue the notification.
            nm.notify(m, newMessageNotification);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.followup_alert))
                    .setContentText(" " + name)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    //  .setCustomContentView(contentView)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(m, notification);
        }

    }

    //TODO Followups of Sales Orders Only
    public void SalesOrdersFollowUpsPushNotification(String name, String chkOId, String foId,
                                                     String foSyncId, String scheduleDate, String communicationMode) {
        PushNotifications pushNotifications = new PushNotifications();
        pushNotifications.setFoid(foId);
        pushNotifications.setMessage(scheduleDate);
        pushNotifications.setType(getString(R.string.follow_up_alert_title));

        databaseHandler.insertPushNotification(pushNotifications, String.valueOf(true));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        m = random.nextInt(9999 - 1000) + 1000;

        Intent notificationOrdersInfoIntent = new Intent(this, OrdersTabActivity.class);
        notificationOrdersInfoIntent.putExtra(getString(R.string.order_id), "" + chkOId);
        notificationOrdersInfoIntent.putExtra(getString(R.string.order_id_text), "" + "");
        notificationOrdersInfoIntent.putExtra(getString(R.string.notify_id), m);
        TaskStackBuilder stackBuilderOrders = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderOrders.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderOrders.addNextIntent(notificationOrdersInfoIntent);

        Intent notificationRecordFollowupintent = new Intent(this, RecordFollowUpActivity.class);
        if (chkOId != null && !chkOId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.order_id), chkOId);
        }
        if (foId != null && !foId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.foid), foId);
        }
        if (foSyncId != null && !foSyncId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.sync_id), foSyncId);
        }
        TaskStackBuilder stackBuilderRecordFollowUp = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderRecordFollowUp.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderRecordFollowUp.addNextIntent(notificationRecordFollowupintent);
        notificationRecordFollowupintent.putExtra(getString(R.string.notify_id), m);

        contentOrdersInfoIntent = stackBuilderOrders.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);
        contentRecordFollowupIntent = stackBuilderRecordFollowUp.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.followup_custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "" + getString(R.string.follow_up_alert_title));
        contentView.setTextViewText(R.id.view_lead_textView, getString(R.string.order_details_title));
        contentView.setTextViewText(R.id.record_followup_textView, getString(R.string.record_follow_up_title));
        contentView.setOnClickPendingIntent(R.id.view_lead_textView, contentOrdersInfoIntent);
        contentView.setOnClickPendingIntent(R.id.record_followup_textView, contentRecordFollowupIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("" + name);
        String contentText = "";
        if (communicationMode != null) {
            contentView.setTextViewText(R.id.text, " " + communicationMode + " " +
                    getString(R.string.with) + " " + name + "  " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText("" + communicationMode + " " + getString(R.string.with) +
                    " " + name + "  " + getDateTime(scheduleDate));
            contentText = "" + communicationMode + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        } else {
            contentView.setTextViewText(R.id.text, " " + getString(R.string.call) + " " +
                    getString(R.string.with) + name + " " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText(getString(R.string.call) +
                    " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate));
            contentText = getString(R.string.call) + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        }


        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bundledFollowUpPushNotifications(contentText, contentOrdersInfoIntent,
                    contentRecordFollowupIntent, getString(R.string.order_details_title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Create the reply action and add the remote input.
            Notification.Action actionLead =
                    new Notification.Action.Builder(0,
                            getString(R.string.order_details_title), contentOrdersInfoIntent)
                            .build();
            Notification.Action actionRecordFollowUp =
                    new Notification.Action.Builder(0,
                            getString(R.string.record_follow_up_title), contentRecordFollowupIntent)
                            .build();

            // Build the notification and add the action.
            Notification newMessageNotification =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.follow_up_alert_title))
                            .setContentText(contentText)
                            .setStyle(new Notification.BigTextStyle().bigText(contentText))
                            .addAction(actionLead)
                            .addAction(actionRecordFollowUp)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setContentIntent(contentRecordFollowupIntent)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setGroup(PUSH_NOTIFICATIONS_GROUP)
                            .build();

            // Issue the notification.
            nm.notify(m, newMessageNotification);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.followup_alert))
                    .setContentText(" " + name)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    //  .setCustomContentView(contentView)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(m, notification);
        }

    }

    //TODO Followups of Quotations Only
    public void QuotationsFollowUpsPushNotification(String name, String qoId, String foId,
                                                    String foSyncId, String scheduleDate, String communicationMode) {
        PushNotifications pushNotifications = new PushNotifications();
        pushNotifications.setFoid(foId);
        pushNotifications.setMessage(scheduleDate);
        pushNotifications.setType(getString(R.string.follow_up_alert_title));

        databaseHandler.insertPushNotification(pushNotifications, String.valueOf(true));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        m = random.nextInt(9999 - 1000) + 1000;

        Intent notificationOrdersInfoIntent = new Intent(this, QuotationDetailsActivity.class);
        notificationOrdersInfoIntent.putExtra(getString(R.string.qo_id), "" + qoId);
        notificationOrdersInfoIntent.putExtra(getString(R.string.qo_id_text), "" + "");
        notificationOrdersInfoIntent.putExtra(getString(R.string.notify_id), m);
        TaskStackBuilder stackBuilderOrders = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderOrders.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderOrders.addNextIntent(notificationOrdersInfoIntent);

        Intent notificationRecordFollowupintent = new Intent(this, RecordFollowUpActivity.class);
        if (qoId != null && !qoId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.qo_id), qoId);
        }
        if (foId != null && !foId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.foid), foId);
        }
        if (foSyncId != null && !foSyncId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.sync_id), foSyncId);
        }
        TaskStackBuilder stackBuilderRecordFollowUp = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderRecordFollowUp.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderRecordFollowUp.addNextIntent(notificationRecordFollowupintent);
        notificationRecordFollowupintent.putExtra(getString(R.string.notify_id), m);

        contentQuotationsInfoIntent = stackBuilderOrders.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);
        contentRecordFollowupIntent = stackBuilderRecordFollowUp.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.followup_custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "" + getString(R.string.follow_up_alert_title));
        contentView.setTextViewText(R.id.view_lead_textView, getString(R.string.quotation_info_title));
        contentView.setTextViewText(R.id.record_followup_textView, getString(R.string.record_follow_up_title));
        contentView.setOnClickPendingIntent(R.id.view_lead_textView, contentQuotationsInfoIntent);
        contentView.setOnClickPendingIntent(R.id.record_followup_textView, contentRecordFollowupIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("" + name);
        String contentText = "";
        if (communicationMode != null) {
            contentView.setTextViewText(R.id.text, " " + communicationMode + " " +
                    getString(R.string.with) + " " + name + "  " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText("" + communicationMode + " " + getString(R.string.with) +
                    " " + name + "  " + getDateTime(scheduleDate));
            contentText = "" + communicationMode + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        } else {
            contentView.setTextViewText(R.id.text, " " + getString(R.string.call) + " " +
                    getString(R.string.with) + name + " " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText(getString(R.string.call) +
                    " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate));
            contentText = getString(R.string.call) + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        }


        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bundledFollowUpPushNotifications(contentText, contentQuotationsInfoIntent,
                    contentRecordFollowupIntent, getString(R.string.quotation_info_title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Create the reply action and add the remote input.
            Notification.Action actionLead =
                    new Notification.Action.Builder(0,
                            getString(R.string.quotation_info_title), contentQuotationsInfoIntent)
                            .build();
            Notification.Action actionRecordFollowUp =
                    new Notification.Action.Builder(0,
                            getString(R.string.record_follow_up_title), contentRecordFollowupIntent)
                            .build();

            // Build the notification and add the action.
            Notification newMessageNotification =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.follow_up_alert_title))
                            .setContentText(contentText)
                            .setStyle(new Notification.BigTextStyle().bigText(contentText))
                            .addAction(actionLead)
                            .addAction(actionRecordFollowUp)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setContentIntent(contentRecordFollowupIntent)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setGroup(PUSH_NOTIFICATIONS_GROUP)
                            .build();

            // Issue the notification.
            nm.notify(m, newMessageNotification);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.followup_alert))
                    .setContentText(" " + name)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    //  .setCustomContentView(contentView)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(m, notification);
        }

    }

    //TODO Followups of Enquiries Only
    public void EnquiriesFollowUpsPushNotification(String name, String enId, String foId,
                                                   String foSyncId, String scheduleDate, String communicationMode) {
        PushNotifications pushNotifications = new PushNotifications();
        pushNotifications.setFoid(foId);
        pushNotifications.setMessage(scheduleDate);
        pushNotifications.setType(getString(R.string.follow_up_alert_title));

        databaseHandler.insertPushNotification(pushNotifications, String.valueOf(true));

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        m = random.nextInt(9999 - 1000) + 1000;

        Intent notificationOrdersInfoIntent = new Intent(this, EnquiryDetailActivity.class);
        notificationOrdersInfoIntent.putExtra(getString(R.string.en_id), "" + enId);
        notificationOrdersInfoIntent.putExtra(getString(R.string.enquiry_id_text), "" + "");
        notificationOrdersInfoIntent.putExtra(getString(R.string.notify_id), m);
        TaskStackBuilder stackBuilderOrders = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderOrders.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderOrders.addNextIntent(notificationOrdersInfoIntent);

        Intent notificationRecordFollowupintent = new Intent(this, RecordFollowUpActivity.class);
        if (enId != null && !enId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.en_id), enId);
        }
        if (foId != null && !foId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.foid), foId);
        }
        if (foSyncId != null && !foSyncId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.sync_id), foSyncId);
        }
        TaskStackBuilder stackBuilderRecordFollowUp = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilderRecordFollowUp.addParentStack(RecordFollowUpActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilderRecordFollowUp.addNextIntent(notificationRecordFollowupintent);
        notificationRecordFollowupintent.putExtra(getString(R.string.notify_id), m);

        contentEnquiriesInfoIntent = stackBuilderOrders.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);
        contentRecordFollowupIntent = stackBuilderRecordFollowUp.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.followup_custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "" + getString(R.string.follow_up_alert_title));
        contentView.setTextViewText(R.id.view_lead_textView, getString(R.string.enquiry_info_title));
        contentView.setTextViewText(R.id.record_followup_textView, getString(R.string.record_follow_up_title));
        contentView.setOnClickPendingIntent(R.id.view_lead_textView, contentEnquiriesInfoIntent);
        contentView.setOnClickPendingIntent(R.id.record_followup_textView, contentRecordFollowupIntent);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("" + name);
        String contentText = "";
        if (communicationMode != null) {
            contentView.setTextViewText(R.id.text, " " + communicationMode + " " +
                    getString(R.string.with) + " " + name + "  " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText("" + communicationMode + " " + getString(R.string.with) +
                    " " + name + "  " + getDateTime(scheduleDate));
            contentText = "" + communicationMode + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        } else {
            contentView.setTextViewText(R.id.text, " " + getString(R.string.call) + " " +
                    getString(R.string.with) + name + " " + getDateTime(scheduleDate));
            bigTextStyle.setSummaryText(getString(R.string.call) +
                    " " + getString(R.string.with) + " " + name + " " + getDateTime(scheduleDate));
            contentText = getString(R.string.call) + " " + getString(R.string.with) + " " + name +
                    " " + getDateTime(scheduleDate);
        }


        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            bundledFollowUpPushNotifications(contentText, contentEnquiriesInfoIntent,
                    contentRecordFollowupIntent, getString(R.string.enquiry_info_title));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // Create the reply action and add the remote input.
            Notification.Action actionLead =
                    new Notification.Action.Builder(0,
                            getString(R.string.enquiry_info_title), contentEnquiriesInfoIntent)
                            .build();
            Notification.Action actionRecordFollowUp =
                    new Notification.Action.Builder(0,
                            getString(R.string.record_follow_up_title), contentRecordFollowupIntent)
                            .build();

            // Build the notification and add the action.
            Notification newMessageNotification =
                    new Notification.Builder(this)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.follow_up_alert_title))
                            .setContentText(contentText)
                            .setStyle(new Notification.BigTextStyle().bigText(contentText))
                            .addAction(actionLead)
                            .addAction(actionRecordFollowUp)
                            .setDefaults(NotificationCompat.DEFAULT_SOUND)
                            .setContentIntent(contentRecordFollowupIntent)
                            .setColor(getResources().getColor(R.color.colorPrimary))
                            .setGroup(PUSH_NOTIFICATIONS_GROUP)
                            .build();

            // Issue the notification.
            nm.notify(m, newMessageNotification);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.followup_alert))
                    .setContentText(" " + name)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    //  .setCustomContentView(contentView)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(m, notification);
        }

    }

    //Bundle Follow Ups Notifications
    public void bundledFollowUpPushNotifications(String contentText, PendingIntent detailsPendingIntent,
                                                 PendingIntent recordFollowupIntent,
                                                 String detailsTitleStr) {
        try {
            // Create the reply action and add the remote input.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Notification.Action actionDetails =
                        new Notification.Action.Builder(0,
                                detailsTitleStr, detailsPendingIntent)
                                .build();

                Notification.Action actionRecordFollowUp =
                        new Notification.Action.Builder(0,
                                getString(R.string.record_follow_up_title), recordFollowupIntent)
                                .build();

                Context context = ApplicationClass.context();

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                int notificationId = 1;
                String channelId = "channel-01";
                String channelName = "Follow Ups";
                int importance = NotificationManager.IMPORTANCE_HIGH;

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel(
                            channelId, channelName, importance);
                    notificationManager.createNotificationChannel(mChannel);
                }

                ++numberOfBundled;
                issuedMessages.add(contentText);

                Notification.Builder mBuilder = new Notification.Builder(context, channelId)
                        .setContentTitle(getString(R.string.follow_up_alert_title))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setContentIntent(recordFollowupIntent)
                        .setGroup(GROUP_KEY_BUNDLED);

                Notification.InboxStyle inboxStyle =
                        new Notification.InboxStyle();
                inboxStyle.setBigContentTitle(getString(R.string.follow_up_alert_title));
                for (CharSequence cs : issuedMessages) {
                    inboxStyle.addLine(cs);
                }

                mBuilder.setStyle(inboxStyle);

                notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID, mBuilder.build());

                //issue the Bundled notification. Since there is a summary notification, this will only display
                //on systems with Nougat or later
                Notification.Builder builder = new Notification.Builder(context, channelId)
                        .setContentTitle(getString(R.string.follow_up_alert_title))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .addAction(actionDetails)
                        .addAction(actionRecordFollowUp)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setContentIntent(recordFollowupIntent)
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(contentText))
                        .setGroup(GROUP_KEY_BUNDLED);

                //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
                //in this simple case, but is important if we need to take action on a specific notification, such as
                //deleting a message
                int requestCode = NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled;
                dictMap.put(requestCode, issuedMessages);
                //  PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled, builder.build());
                AppPreferences.setNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER, requestCode);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {


                NotificationCompat.Action actionDetails =
                        new NotificationCompat.Action.Builder(0,
                                detailsTitleStr, detailsPendingIntent)
                                .build();

                NotificationCompat.Action actionRecordFollowUp =
                        new NotificationCompat.Action.Builder(0,
                                getString(R.string.record_follow_up_title), recordFollowupIntent)
                                .build();

                Context context = ApplicationClass.context();

                //TODO Updated on 29th June
                NotificationManager mNotificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                ++numberOfBundled;
                issuedMessages.add(contentText);

                //Build and issue the group summary. Use inbox style so that all messages are displayed
                NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle(getString(R.string.follow_up_alert_title))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setContentIntent(recordFollowupIntent)
                        .setGroup(GROUP_KEY_BUNDLED);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(getString(R.string.follow_up_alert_title));
                for (CharSequence cs : issuedMessages) {
                    inboxStyle.addLine(cs);
                }
                summaryBuilder.setStyle(inboxStyle);
                mNotificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID, summaryBuilder.build());

                //issue the Bundled notification. Since there is a summary notification, this will only display
                //on systems with Nougat or later
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(getString(R.string.follow_up_alert_title))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .addAction(actionDetails)
                        .addAction(actionRecordFollowUp)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setContentIntent(recordFollowupIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                        .setGroup(GROUP_KEY_BUNDLED);

                //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
                //in this simple case, but is important if we need to take action on a specific notification, such as
                //deleting a message
                int requestCode = NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled;
                dictMap.put(requestCode, issuedMessages);
                //  PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                mNotificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled, builder.build());
                AppPreferences.setNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Bundle Feeds Push Notifications
    public void bundledFeedsPushNotifications(String contentText) {
        try {
            // Create the reply action and add the remote input.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                Context context = ApplicationClass.context();
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                ++numberOfFeedBundled;
                issuedFeedsMessages.add(contentText);

                //Build and issue the group summary. Use inbox style so that all messages are displayed
                NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context)
                        .setContentTitle(getString(R.string.activity_feeds)).setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setGroupSummary(true)
                        .setAutoCancel(true)
                        .setContentIntent(activityFeedsInfoIntent)
                        .setGroup(GROUP_KEY_BUNDLED);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(getString(R.string.activity_feeds));
                for (CharSequence cs : issuedFeedsMessages) {
                    inboxStyle.addLine(cs);
                }
                summaryBuilder.setStyle(inboxStyle);

                notificationManager.notify(FEED_NOTIFICATION_BUNDLED_BASE_ID, summaryBuilder.build());

                //issue the Bundled notification. Since there is a summary notification, this will only display
                //on systems with Nougat or later
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setContentTitle(getString(R.string.activity_feeds))
                        .setContentText(contentText)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setContentIntent(activityFeedsInfoIntent)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(contentText))
                        .setGroup(GROUP_KEY_BUNDLED);


                //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
                //in this simple case, but is important if we need to take action on a specific notification, such as
                //deleting a message
                int requestCode = FEED_NOTIFICATION_BUNDLED_BASE_ID + numberOfFeedBundled;
                feedsMap.put(requestCode, issuedFeedsMessages);
                //  PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationManager.notify(FEED_NOTIFICATION_BUNDLED_BASE_ID + numberOfFeedBundled, builder.build());
                AppPreferences.setFeedNotificationNumber(this, AppUtils.PUSH_FEEDS_NOTIFICATION_NUMBER, requestCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDateTime(String scheduledDate) {
        String strDate = "";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss");
        try {
            if (scheduledDate != null) {
                Date pastDate = simpleDateFormat.parse(scheduledDate);
                Date currentDate = new Date();
                Calendar now = Calendar.getInstance();
                long nowInMillis = SystemClock.uptimeMillis();

                Calendar now_calendar = Calendar.getInstance();
                now_calendar.setTimeInMillis(nowInMillis);

                long seconds = TimeUnit.MILLISECONDS.toSeconds(pastDate.getTime() - currentDate.getTime());
                long minutes = TimeUnit.MILLISECONDS.toMinutes(pastDate.getTime() - currentDate.getTime());
                long hours = TimeUnit.MILLISECONDS.toHours(pastDate.getTime() - currentDate.getTime());
                long days = TimeUnit.MILLISECONDS.toDays(pastDate.getTime() - currentDate.getTime());
                if (hours < 24 && (pastDate.getDate() == currentDate.getDate())) {
                    if (hours == 1) {
                        strDate = getString(R.string.today_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate);
                    } else
                        strDate = getString(R.string.today_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate);
                } else {
                    if ((pastDate.getDate() - currentDate.getDate() == 1) && (currentDate.getMonth() == pastDate.getMonth())
                            && (currentDate.getYear() == pastDate.getYear())) {
                        strDate = getString(R.string.tommorow_at) + " " + new SimpleDateFormat("hh:mm a").format(pastDate);
                    } else
                        strDate = "on " + "" + new SimpleDateFormat("dd MMM, yyyy").format(pastDate) +
                                " at " + new SimpleDateFormat("hh:mm a").format(pastDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;

    }

    //Push notifications of Activity Feeds
    public void PushNotificationFeeds(com.accrete.sixorbit.model.Notification notification) {
        try {
            PushNotifications pushNotifications = new PushNotifications();
            pushNotifications.setUaid(notification.getUaid());
            pushNotifications.setMessage(notification.getMessage());
            pushNotifications.setType(getString(R.string.activity_feeds));

            databaseHandler.insertPushNotification(pushNotifications, String.valueOf(true));

            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

            m = random.nextInt(9999 - 1000) + 1000;

            Intent activityFeedsIntent = new Intent(this, ActivityFeedsCommentActivity.class);
            activityFeedsIntent.putExtra("uid", notification.getUid());
            activityFeedsIntent.putExtra("uaid", notification.getUaid());
            activityFeedsIntent.putExtra("post_message", notification.getMessage());
            activityFeedsIntent.putExtra("post_time", notification.getCreatedTs());
            activityFeedsIntent.putExtra("notify_id", m);
            TaskStackBuilder stackBuilderFeeds = TaskStackBuilder.create(this);
            // Adds the back stack
            stackBuilderFeeds.addParentStack(ActivityFeedsCommentActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilderFeeds.addNextIntent(activityFeedsIntent);

            activityFeedsInfoIntent = stackBuilderFeeds.getPendingIntent(m, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
            bigTextStyle.setBigContentTitle("" + notification.getMessage());
            String contentText = "" + notification.getMessage();

            //set
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bundledFeedsPushNotifications(contentText);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                // Build the notification and add the action.
                Notification newMessageNotification =
                        new Notification.Builder(this)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle(getString(R.string.activity_feeds))
                                .setContentText(contentText)
                                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                                .setContentIntent(activityFeedsInfoIntent)
                                .setColor(getResources().getColor(R.color.colorPrimary))
                                .setGroup(PUSH_NOTIFICATIONS_GROUP)
                                .build();

                // Issue the notification.
                nm.notify(m, newMessageNotification);
            } else {
                builder.setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.activity_feeds))
                        .setContentText("" + notification.getMessage())
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(NotificationCompat.DEFAULT_SOUND)
                        .setStyle(bigTextStyle)
                        //  .setCustomContentView(contentView)
                        //   .setPriority(PRIORITY_MAX)
                        .setContentIntent(activityFeedsInfoIntent)
                        .setGroup(PUSH_NOTIFICATIONS_GROUP)
                        .setAutoCancel(true);
                Notification notificationPush = builder.build();
                notificationPush.flags |= Notification.FLAG_AUTO_CANCEL;
                nm.notify(m, notificationPush);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@TargetApi(19)
    public void showNotification(Context context, String title, String body, Intent intent) {
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationId = 1;
            String channelId = "channel-01";
            String channelName = "Channel Name";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(
                        channelId, channelName, importance);
                notificationManager.createNotificationChannel(mChannel);
            }

            Notification.Builder mBuilder = new Notification.Builder(context, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );
            mBuilder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, mBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    try {
                        // display notifications for follow ups
                        List<FollowUp> followUps = databaseHandler.getAllfollowUps();
                        Date alertDateTime = null;
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                        for (int i = 0; i < followUps.size(); i++) {
                            try {
                                if (followUps.get(i).getAlertOn() != null) {
                                    alertDateTime = simpleDateFormat.parse(followUps.get(i).getAlertOn());
                                }

                                Calendar alert_calendar = Calendar.getInstance();
                                alert_calendar.setTimeInMillis(alertDateTime.getTime());

                                long difference = new Date().getTime() - alertDateTime.getTime();
                                int days = (int) (difference / (1000 * 60 * 60 * 24));
                                int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60 * 24));
                                int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
                                //     Log.e("TIME", min + "");
                                if (DateUtils.isToday(alert_calendar.getTimeInMillis())) {
                                    //TODO Followups for Leads
                                    if (min <= 5 && min >= -5 && followUps.get(i).getLeadId() != null &&
                                            !followUps.get(i).getLeadId().isEmpty() && followUps.get(i).getFoid() != null &&
                                            !followUps.get(i).getFoid().isEmpty()) {
                                        Date pastDate = simpleDateFormat.parse(followUps.get(i).getScheduledDate());
                                        String date = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss").format(pastDate);

                                        // displaying the first
                                        List<Lead> leads = databaseHandler.getFreshLeads(followUps.get(i).getLeadId());
                                        for (int j = 0; j < leads.size(); j++) {
                                            if (!databaseHandler.checkFoIdInPush(followUps.get(i).getFoid())) {
                                                LeadsFollowUpsPushNotification(leads.get(j).getSyncId(), leads.get(j).getName(),
                                                        leads.get(j).getLeaid(), followUps.get(i).getFoid(),
                                                        followUps.get(i).getSyncId(), date, followUps.get(i).getFollowupCommunicationMode());
                                            }
                                        }
                                    }
                                    //TODO Followups for Sales Order
                                    else if (min <= 5 && min >= -5 && followUps.get(i).getChkoid() != null &&
                                            !followUps.get(i).getChkoid().isEmpty() && followUps.get(i).getFoid() != null &&
                                            !followUps.get(i).getFoid().isEmpty()) {
                                        Date pastDate = simpleDateFormat.parse(followUps.get(i).getScheduledDate());
                                        String date = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss").format(pastDate);
                                        if (!databaseHandler.checkFoIdInPush(followUps.get(i).getFoid())) {
                                            SalesOrdersFollowUpsPushNotification(followUps.get(i).getName(),
                                                    followUps.get(i).getChkoid(), followUps.get(i).getFoid(),
                                                    followUps.get(i).getSyncId(), date, followUps.get(i).getFollowupCommunicationMode());
                                        }
                                    }
                                    //TODO Followups for Quotations
                                    else if (min <= 5 && min >= -5 && followUps.get(i).getQoid() != null &&
                                            !followUps.get(i).getQoid().isEmpty() && followUps.get(i).getFoid() != null &&
                                            !followUps.get(i).getFoid().isEmpty()) {
                                        Date pastDate = simpleDateFormat.parse(followUps.get(i).getScheduledDate());
                                        String date = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss").format(pastDate);
                                        if (!databaseHandler.checkFoIdInPush(followUps.get(i).getFoid())) {
                                            QuotationsFollowUpsPushNotification(followUps.get(i).getName(),
                                                    followUps.get(i).getQoid(), followUps.get(i).getFoid(),
                                                    followUps.get(i).getSyncId(), date, followUps.get(i).getFollowupCommunicationMode());
                                        }
                                    }
                                    //TODO Followups for Enquiries
                                    else if (min <= 5 && min >= -5 && followUps.get(i).getEnid() != null &&
                                            !followUps.get(i).getEnid().isEmpty() && followUps.get(i).getFoid() != null &&
                                            !followUps.get(i).getFoid().isEmpty()) {
                                        Date pastDate = simpleDateFormat.parse(followUps.get(i).getScheduledDate());
                                        String date = new SimpleDateFormat("dd MMM, yyyy HH:mm:ss").format(pastDate);
                                        if (!databaseHandler.checkFoIdInPush(followUps.get(i).getFoid())) {
                                            EnquiriesFollowUpsPushNotification(followUps.get(i).getName(),
                                                    followUps.get(i).getEnid(), followUps.get(i).getFoid(),
                                                    followUps.get(i).getSyncId(), date, followUps.get(i).getFollowupCommunicationMode());
                                        }
                                    }
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }
                        if (AppPreferences.getNotificationRead(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ) != null) {
                            //          Log.e("PUSH_READS", AppPreferences.getNotificationRead(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ));
                        }
                        if (AppPreferences.getNotificationRead(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ) != null &&
                                !(AppPreferences.getNotificationRead(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ).equals("0"))) {
                            //         Log.e("PUSH_READ", AppPreferences.getNotificationRead(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ));
                            //Date
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String str = AppPreferences.getNotificationReadTime(PushNotificationsTimeService.this, AppUtils.NOTIFICATION_READ_TIME);
                            String currentDate = df.format(c.getTime());
                            // display notifications for Activity feeds
                            List<com.accrete.sixorbit.model.Notification> notificationList = databaseHandler.getUnreadActivityFeeds(str,
                                    currentDate);

                            for (int i = 0; i < notificationList.size(); i++) {
                                try {
                                    // displaying the first
                                    if (!databaseHandler.checkUaIdInPush(notificationList.get(i).getUaid()) &&
                                            databaseHandler.checkMotAIdInNotify(notificationList.get(i).getMotaid())) {
                                        PushNotificationFeeds(notificationList.get(i));
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
}
