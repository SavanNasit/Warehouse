package com.accrete.sixorbit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.followup.RecordFollowUpActivity;
import com.accrete.sixorbit.activity.lead.LeadInfoActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static com.accrete.sixorbit.utils.AppUtils.PUSH_NOTIFICATIONS_GROUP;

public class AppFirebaseMessagingService extends FirebaseMessagingService {

    String leaId, foid, message;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("sd", "From: to " + remoteMessage.getData());

        String type = "";
        type = remoteMessage.getData().get("type");
        if (type != null && !type.equals("")) {
            try {
                JSONObject object = new JSONObject(type);
                Log.i("gcm response", "" + object.toString());

                if (type.equals(getString(R.string.followup_alert))) {
                    leaId = object.getString(getString(R.string.leaid));
                    foid = object.getString(getString(R.string.foid));
                    message = object.getString(getString(R.string.message));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Intent notificationLeadInfoIntent = new Intent(this, LeadInfoActivity.class);
        notificationLeadInfoIntent.putExtra(getString(R.string.leaid), "" + leaId);
        notificationLeadInfoIntent.putExtra(getString(R.string.id), foid);

        Intent notificationRecordFollowupintent = new Intent(this, RecordFollowUpActivity.class);
        if (leaId != null && !leaId.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.lead_id), leaId);
        }
        if (foid != null && !foid.isEmpty()) {
            notificationRecordFollowupintent.putExtra(getString(R.string.foid), foid);
        }

        PendingIntent contentLeadInfoIntent = PendingIntent.getActivity(this, 0, notificationLeadInfoIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent contentRecordFollowupIntent = PendingIntent.getActivity(this, 0, notificationRecordFollowupintent, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.followup_custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, " " + getString(R.string.followup_alert));
        contentView.setTextViewText(R.id.view_lead_textView, getString(R.string.lead_info));
        contentView.setTextViewText(R.id.record_followup_textView, getString(R.string.record_follow_up));
        contentView.setOnClickPendingIntent(R.id.view_lead_textView, contentLeadInfoIntent);
        contentView.setOnClickPendingIntent(R.id.record_followup_textView, contentRecordFollowupIntent);

        contentView.setTextViewText(R.id.text, message);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle(" " + message);

        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis())
                    //   .setPriority(PRIORITY_MAX)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    .setCustomContentView(contentView)
                    .setCustomBigContentView(contentView)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setAutoCancel(true);
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(type)
                    .setContentText(" " + message)
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    .setCustomContentView(contentView)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setContentIntent(contentRecordFollowupIntent)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setAutoCancel(true);

        }
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(0, notification);
    }

}
