package com.accrete.sixorbit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.AppIllustration.ApplicationClass;
import com.accrete.sixorbit.activity.chats.ChatMessageActivity;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.accrete.sixorbit.utils.AppUtils.PUSH_NOTIFICATIONS_GROUP;

/**
 * Created by agt on 10/9/17.
 */

public class ChatService extends Service {
    private static final String TAG = "ChatService";
    private static final int NOTIFICATION_BUNDLED_BASE_ID = 1000;
    private final static String GROUP_KEY_BUNDLED = "group_key_bundled";
    public static int m;
    public static HashMap<String, ArrayList<String>> messagesMap =
            new HashMap<String, ArrayList<String>>();
    //simple way to keep track of the number of bundled notifications
    private static List<CharSequence> issuedMessages = new LinkedList<>();
    //simple way to keep track of the number of bundled notifications
    private static int numberOfBundled = 0, numberOfChildBundled = 0;
    public final Emitter.Listener MESSAGE_READ = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO Fetch all unread messages
            Log.d(TAG, "read chat message" + args[0] + Arrays.toString(args));
            JSONObject arg = null;
        }
    };
    public final IBinder mBinder = new MyBinder();
    private final android.os.Handler HANDLER = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1001:
                    // Toast.makeText(TestActivity.this, (String) message.obj, Toast.LENGTH_SHORT).show();
                    // sendMessageToUi((String) message.obj);
                    return true;
            }
            return false;
        }
    });
    io.socket.client.Socket socket;
    public final Emitter.Listener DISCONNECTED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Disconnected");
            logoutUser();
        }

    };
    String userSessionId, userId, baseUrl;
    Random random = new Random();
    private List<ChatMessage> chatMessages;
    private DatabaseHandler databaseHandler;
    public final Emitter.Listener CHATS_ACTIVE = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO Fetch all active users
            Log.d(TAG, "message chats active" + args[0] + " ARRAY " + Arrays.toString(args));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(args[0].toString());
                if (jsonObject.has("chats")) {
                    JSONArray array = jsonObject.getJSONArray("chats");
                    int[] numbers = new int[array.length()];
                    // Extract numbers from JSON array.
                    String active_users = array.toString();
                    String[] items = active_users.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
                    int[] results = new int[items.length];
                    for (int i = 0; i < items.length; i++) {
                        try {
                            results[i] = Integer.parseInt(items[i]);
                            ChatContacts chatContacts = new ChatContacts();
                            if (Integer.parseInt(userId) != results[i]) {
                                chatContacts.setOnlineStatus(getString(R.string.online_status));
                                chatContacts.setUid(results[i]);
                                databaseHandler.updateAssigneSpinner(chatContacts);
                            } else {
                                chatContacts.setOnlineStatus(getString(R.string.offline_status));
                                chatContacts.setUid(results[i]);
                                databaseHandler.updateAssigneSpinner(chatContacts);
                            }
                        } catch (NumberFormatException nfe) {
                            //NOTE: write something here if you need to recover from formatting errors
                        }
                    }

                    //Constants.active_users = array.toString();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    // Binder given to clients
    public final Emitter.Listener CONNECTED = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO Fetc all previous message
            Log.d(TAG, "Connected");
            chatMessages = new ArrayList<ChatMessage>();
            if (databaseHandler.getNonSyncChatMessages().size() > 0) {
                chatMessages = databaseHandler.getNonSyncChatMessages();
                for (int i = 0; i < chatMessages.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        if (AppPreferences.getIsLogin(ChatService.this, AppUtils.ISLOGIN)) {
                            userId = AppPreferences.getUserId(ChatService.this, AppUtils.USER_ID);
                        }
                        jsonObject.put("uid", Integer.parseInt(userId));
                        jsonObject.put("message", "" + chatMessages.get(i).getMessage());
                        jsonObject.put("to", chatMessages.get(i).getUid());
                        jsonObject.put("sync_id", chatMessages.get(i).getSyncId());
                        Log.d("send message", jsonObject.toString());
                        socket.emit("chat-message", jsonObject.toString());
                        Log.e("EMIT", jsonObject.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }

    };
    public final Emitter.Listener CHAT_LOGIN = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO Fetch all login users
            Log.d(TAG, "message chat login" + args[0] + " ARRAY " + Arrays.toString(args));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(args[0].toString());
                if (jsonObject.has("chat")) {
                    int active_users = jsonObject.getInt("chat");
                    ChatContacts chatContacts = new ChatContacts();
                    chatContacts.setOnlineStatus(getString(R.string.online_status));
                    chatContacts.setUid(active_users);
                    databaseHandler.updateAssigneSpinner(chatContacts);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    // Binder given to clients
    public final Emitter.Listener LOGOUT = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //TODO Fetch all active users
            Log.d(TAG, "message chats logout" + args[0] + " ARRAY " + Arrays.toString(args));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(args[0].toString());
                if (jsonObject.has("chat")) {
                    int active_user = jsonObject.getInt("chat");
                    ChatContacts chatContacts = new ChatContacts();
                    chatContacts.setOnlineStatus(getString(R.string.offline_status));
                    chatContacts.setUid(active_user);
                    databaseHandler.updateAssigneSpinner(chatContacts);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };
    public final Emitter.Listener CHAT_MESSAGE = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            //TODO Fetch all previous message
            //fetchAllMessages()
            Log.d(TAG, "message recive" + args[0]);
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(args[0].toString());
                if (jsonObject.has("sync_id")) {
                    ChatMessage chatMessage = new ChatMessage();
                    Log.d(TAG, "message insertchek sync" + String.valueOf(jsonObject.getString("sync_id")));
                    if (databaseHandler.checkSyncId((jsonObject.getString("sync_id")).toString())) {
                        Log.d(TAG, "message insertchek sync" + args[0]);
                        chatMessage.setMessage(jsonObject.getString("message"));
                        chatMessage.setSyncId(String.valueOf(jsonObject.getLong("sync_id")));
                        chatMessage.setChatId(String.valueOf(jsonObject.getInt("message_id")));
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis(Long.parseLong(jsonObject.getString("time")));
                        int month = (cl.get(Calendar.MONTH) + 1);
                        String month_str;
                        if ((month) < 10) {
                            month_str = "0" + (month);
                        } else {
                            month_str = "" + month;
                        }
                        int hours = (cl.get(Calendar.HOUR_OF_DAY));
                        String hours_str;
                        if ((hours) < 10) {
                            hours_str = "0" + (hours);
                        } else {
                            hours_str = "" + hours;
                        }
                        int day = cl.get(Calendar.DAY_OF_MONTH);
                        String day_str;
                        if ((day) < 10) {
                            day_str = "0" + (day);
                        } else {
                            day_str = "" + day;
                        }
                        int minute = cl.get(Calendar.MINUTE);
                        String minute_str;
                        if ((minute) < 10) {
                            minute_str = "0" + (minute);
                        } else {
                            minute_str = "" + minute;
                        }
                        int second = cl.get(Calendar.SECOND);
                        String second_str;
                        if ((second) < 10) {
                            second_str = "0" + (second);
                        } else {
                            second_str = "" + second;
                        }
                        String date = cl.get(Calendar.YEAR) + "-" + month_str + "-" + day_str + " " +
                                hours_str + ":" + minute_str + ":" + second_str;
                        chatMessage.setCreatedTs(date);
                        if (Integer.parseInt(userId) == jsonObject.getInt("to")) {
                            chatMessage.setMsgType("2");
                            chatMessage.setUid(String.valueOf(jsonObject.getInt("uid")) + "");
                        } else {
                            chatMessage.setMsgType("1");
                            chatMessage.setUid(String.valueOf(jsonObject.getInt("to")) + "");
                        }
                        databaseHandler.updateChatMessages(chatMessage);
                        sendMessageToUi("updateCount", chatMessage.getChatId());
                    } else {
                        Log.d(TAG, "message insert msg" + args[0]);
                        chatMessage.setMessage(jsonObject.getString("message"));
                        chatMessage.setChatId(String.valueOf(jsonObject.getInt("message_id")) + "");
                        if (Integer.parseInt(userId) == jsonObject.getInt("to")) {
                            chatMessage.setMsgType("2");
                            chatMessage.setUid(String.valueOf(jsonObject.getInt("uid")) + "");
                        } else {
                            chatMessage.setMsgType("1");
                            chatMessage.setUid(String.valueOf(jsonObject.getInt("to")) + "");
                        }
                        Log.d(TAG, "message id" + userId + "  " + jsonObject.getInt("to") + "  " +
                                jsonObject.getInt("uid"));
                        Calendar cl = Calendar.getInstance();
                        cl.setTimeInMillis(Long.parseLong(jsonObject.getString("time")));
                        int month = (cl.get(Calendar.MONTH) + 1);
                        String month_str;
                        if ((month) < 10) {
                            month_str = "0" + (month);
                        } else {
                            month_str = "" + month;
                        }
                        int hours = (cl.get(Calendar.HOUR_OF_DAY));
                        String hours_str;
                        if ((hours) < 10) {
                            hours_str = "0" + (hours);
                        } else {
                            hours_str = "" + hours;
                        }
                        int day = cl.get(Calendar.DAY_OF_MONTH);
                        String day_str;
                        if ((day) < 10) {
                            day_str = "0" + (day);
                        } else {
                            day_str = "" + day;
                        }
                        int minute = cl.get(Calendar.MINUTE);
                        String minute_str;
                        if ((minute) < 10) {
                            minute_str = "0" + (minute);
                        } else {
                            minute_str = "" + minute;
                        }
                        int second = cl.get(Calendar.SECOND);
                        String second_str;
                        if ((second) < 10) {
                            second_str = "0" + (second);
                        } else {
                            second_str = "" + second;
                        }
                        String date = cl.get(Calendar.YEAR) + "-" + month_str + "-" + day_str + " " +
                                hours_str + ":" + minute_str + ":" + second_str;
                        chatMessage.setCreatedTs(date);
                        Log.d(TAG, "message insert sync" + args[0] + date);
                        databaseHandler.insertChatMessages(chatMessage);
                        sendMessageToUi("insert", chatMessage.getChatId());
                        Log.e("Value", ApplicationClass.isActivityVisible() + "");
                        if (!ApplicationClass.isActivityVisible()) {
                            PushNotification(jsonObject.getString("uid"), jsonObject.getString("message"));
                        } else {
                            if (messagesMap.get(chatMessage.getUid()) != null && messagesMap.get(chatMessage.getUid()).size() > 0) {
                                messagesMap.get(chatMessage.getUid()).clear();
                            }
                        }

                    }
                } else {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(jsonObject.getString("message"));
                    chatMessage.setUid(jsonObject.getString("uid"));
                    chatMessage.setChatId(jsonObject.getString("message_id"));
                    if (Integer.parseInt(userId) == jsonObject.getInt("to")) {
                        chatMessage.setMsgType("2");
                        chatMessage.setUid(String.valueOf(jsonObject.getInt("uid")) + "");
                    } else {
                        chatMessage.setMsgType("1");
                        chatMessage.setUid(String.valueOf(jsonObject.getInt("to")) + "");
                    }
                    Calendar cl = Calendar.getInstance();
                    cl.setTimeInMillis(Long.parseLong(jsonObject.getString("time")));
                    int month = (cl.get(Calendar.MONTH) + 1);
                    String month_str;
                    if ((month) < 10) {
                        month_str = "0" + (month);
                    } else {
                        month_str = "" + month;
                    }
                    int hours = (cl.get(Calendar.HOUR_OF_DAY));
                    String hours_str;
                    if ((hours) < 10) {
                        hours_str = "0" + (hours);
                    } else {
                        hours_str = "" + hours;
                    }
                    int day = cl.get(Calendar.DAY_OF_MONTH);
                    String day_str;
                    if ((day) < 10) {
                        day_str = "0" + (day);
                    } else {
                        day_str = "" + day;
                    }
                    int minute = cl.get(Calendar.MINUTE);
                    String minute_str;
                    if ((minute) < 10) {
                        minute_str = "0" + (minute);
                    } else {
                        minute_str = "" + minute;
                    }
                    int second = cl.get(Calendar.SECOND);
                    String second_str;
                    if ((second) < 10) {
                        second_str = "0" + (second);
                    } else {
                        second_str = "" + second;
                    }
                    String date = cl.get(Calendar.YEAR) + "-" + month_str + "-" + day_str + " " +
                            hours_str + ":" + minute_str + ":" + second_str;
                    chatMessage.setCreatedTs(date);
                    Log.d(TAG, "message insert " + args[0]);
                    databaseHandler.insertChatMessages(chatMessage);
                    Log.e("Value", ApplicationClass.isActivityVisible() + "");
                    if (!ApplicationClass.isActivityVisible()) {
                        PushNotification(jsonObject.getString("uid"), jsonObject.getString("message"));
                    } else {
                        if (messagesMap.get(chatMessage.getUid()) != null && messagesMap.get(chatMessage.getUid()).size() > 0) {
                            messagesMap.get(chatMessage.getUid()).clear();
                        }
                    }
                    sendMessageToUi("insert", chatMessage.getChatId());
                }
                //   Message.obtain(HANDLER, 1001, message).sendToTarget();
                //   Message.obtain(HANDLER, 1002, message).sendToTarget();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    };
    private BroadcastReceiver mMessageSender = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            int to_user = intent.getIntExtra("to", 0);
            long sync_id_long = intent.getLongExtra("sync_id", 0);
            sendMessage(message, to_user, sync_id_long);
        }
    };
    private BroadcastReceiver notifyLogoutReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (AppPreferences.getIsLogin(ChatService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ChatService.this, AppUtils.USER_ID);
            }
            if (socket != null) {
                socket.on(Socket.EVENT_DISCONNECT, DISCONNECTED);
            }
            logoutUser();
        }
    };

    public static void bundledNotification(String message, String name, String uId) {
        Context context = ApplicationClass.context();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (messagesMap.get(uId) != null) {
            ++numberOfChildBundled;
        }
        ++numberOfBundled;
        issuedMessages.add(message);

        //Build and issue the group summary. Use inbox style so that all messages are displayed
        NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setGroupSummary(true)
                .setGroup(uId);

        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Messages:");
        for (CharSequence cs : issuedMessages) {
            inboxStyle.addLine(cs);
        }
        summaryBuilder.setStyle(inboxStyle);

        notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID, summaryBuilder.build());

        //Pending Intent
        Intent notificationIntent = new Intent(context, ChatMessageActivity.class);
        notificationIntent.putExtra("uid", Integer.parseInt(uId));
        notificationIntent.putExtra(context.getString(R.string.notify_id), Integer.parseInt(uId));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack
        stackBuilder.addParentStack(ChatMessageActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        //issue the Bundled notification. Since there is a summary notification, this will only display
        //on systems with Nougat or later
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setGroupSummary(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(name))
                .setGroup(uId);

        /*NotificationCompat.InboxStyle newBuilderInboxStyle =
                new NotificationCompat.InboxStyle();
        newBuilderInboxStyle.setBigContentTitle(name);
        for (CharSequence cs : issuedMessages) {
            newBuilderInboxStyle.addLine(cs);
        }
        builder.setStyle(newBuilderInboxStyle);*/

        //Each notification needs a unique request code, so that each pending intent is unique. It does not matter
        //in this simple case, but is important if we need to take action on a specific notification, such as
        //deleting a message
        int requestCode = NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled;
        if (messagesMap.get(uId) != null) {
            messagesMap.get(uId).add(message);
            notificationManager.notify(AppPreferences.getChatNotificationNumber(context, AppUtils.PUSH_CHATS_NOTIFICATION_NUMBER),
                    builder.build());
        } else {
            ArrayList<String> messageList = new ArrayList<String>();
            messageList.add(message);
            messagesMap.put(uId, messageList);
            notificationManager.notify(NOTIFICATION_BUNDLED_BASE_ID + numberOfBundled, builder.build());
            AppPreferences.setChatNotificationNumber(context, AppUtils.PUSH_CHATS_NOTIFICATION_NUMBER, requestCode);
        }

        NotificationCompat.Builder childBuilder = new NotificationCompat.Builder(context)
                .setContentTitle(name)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(contentIntent)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setGroup(uId);
        int childRequestCode = requestCode + numberOfChildBundled;
        if (messagesMap.get(uId) != null) {
            notificationManager.notify(AppPreferences.getChatNotificationNumber(context, AppUtils.PUSH_CHATS_NOTIFICATION_NUMBER),
                    childBuilder.build());
        } else {
            notificationManager.notify(childRequestCode,
                    childBuilder.build());
        }

    }

    public void PushNotification(String uId, String message) {
        m = random.nextInt(9999 - 1000) + 1000;
        int notificationNumber = AppPreferences.getNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        ChatContacts chatContacts = databaseHandler.getUserData(Integer.parseInt(uId));
        Intent notificationIntent = new Intent(getBaseContext(), ChatMessageActivity.class);
        notificationIntent.putExtra("uid", Integer.parseInt(uId));
        notificationIntent.putExtra(getString(R.string.notify_id), Integer.parseInt(uId));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack
        stackBuilder.addParentStack(ChatMessageActivity.class);
        // Adds the Intent to the top of the stack
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.custom_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, "" + chatContacts.getName());
        contentView.setTextViewText(R.id.text, "" + message.substring(0, 1).toUpperCase() + message.substring(1));

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("" + chatContacts.getName());
        if (message.length() > 500) {
            message = message.substring(0, 500);
            message = message + "...";
        }
        bigTextStyle.bigText("" + message.substring(0, 1).toUpperCase() + message.substring(1));

        //set
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            if (issuedMessages != null) {
                issuedMessages.clear();
                issuedMessages.add(message);
            }

            if (messagesMap.get(uId) != null) {
                messagesMap.get(uId).add(message);
                //Build and issue the group summary. Use inbox style so that all messages are displayed
                NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("" + chatContacts.getName())
                        .setContentText("" + messagesMap.get(uId).size() + " messages")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        .setGroup(PUSH_NOTIFICATIONS_GROUP).setPriority(Notification.PRIORITY_HIGH);
                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("" + chatContacts.getName());
                for (CharSequence cs : messagesMap.get(uId)) {
                    inboxStyle.addLine(cs);
                }
                summaryBuilder.setStyle(inboxStyle);
                notificationManager.notify(Integer.parseInt(uId), summaryBuilder.build());
            } else {
                ArrayList<String> messageList = new ArrayList<String>();
                messageList.add(message);
                messagesMap.put(uId, messageList);
                //Build and issue the group summary. Use inbox style so that all messages are displayed
                NotificationCompat.Builder summaryBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("" + chatContacts.getName())
                        .setContentText("" + issuedMessages.size() + " messages")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent)
                        //  .setGroupSummary(true)
                        .setGroup(PUSH_NOTIFICATIONS_GROUP).setPriority(Notification.PRIORITY_HIGH);

                NotificationCompat.InboxStyle inboxStyle =
                        new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle("" + chatContacts.getName());
                for (CharSequence cs : issuedMessages) {
                    inboxStyle.addLine(cs);
                }
                summaryBuilder.setStyle(inboxStyle);
                notificationManager.notify(Integer.parseInt(uId), summaryBuilder.build());
            }
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(chatContacts.getName())
                    .setWhen(System.currentTimeMillis())
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_SOUND)
                    .setStyle(bigTextStyle)
                    //   .setPriority(PRIORITY_MAX)
                    .setCustomBigContentView(contentView)
                    .setGroup(PUSH_NOTIFICATIONS_GROUP)
                    .setContentIntent(contentIntent);
            Notification notificationPush = builder.build();
            notificationPush.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(m, notificationPush);
        }
        notificationNumber++;
        AppPreferences.setNotificationNumber(this, AppUtils.PUSH_NOTIFICATION_NUMBER, notificationNumber);
        /*Notification notification = builder.build();
        nm.notify("chat", 0, notification);*/
        //     bundledNotification(message, chatContacts.getName(), uId);
    }

    // Send an Intent with an action named "custom-event-name". The Intent sent should
    // be received by the Receiver Fragment/Activity.
    private void sendMessageToUi(String message, String chatId) {
        Log.d("sender", "Broadcasting message" + message);
        Intent intent = new Intent(getString(R.string.chat_message_broadcast_event));
        // You can also include some extra data.
        intent.putExtra("message", message);
        intent.putExtra("chatId", chatId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void logoutUser() {
        JSONObject jsonObject = new JSONObject();
        try {
            if (AppPreferences.getIsLogin(ChatService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ChatService.this, AppUtils.USER_ID);
            }
            if (userId != null) {
                jsonObject.put("chat", Integer.parseInt(userId));
            }
            socket.emit("chat-logout", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, int to_user, long sync_id_long) {
        JSONObject jsonObject = new JSONObject();
        try {
            if (AppPreferences.getIsLogin(ChatService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ChatService.this, AppUtils.USER_ID);
            }
            jsonObject.put("uid", Integer.parseInt(userId));
            jsonObject.put("message", "" + message);
            jsonObject.put("to", to_user);
            jsonObject.put("sync_id", sync_id_long);
            Log.d("send message", jsonObject.toString());
            socket.emit("chat-message", jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (AppPreferences.getIsLogin(ChatService.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(ChatService.this, AppUtils.USER_ID);
            userSessionId = AppPreferences.getUserSessionId(ChatService.this, AppUtils.USER_SESSION_ID);
            baseUrl = AppPreferences.getLastDomain(ChatService.this, AppUtils.LAST_DOMAIN);
        }
        databaseHandler = new DatabaseHandler(getApplicationContext());
        if (baseUrl == null) {
            stopSelf();
        }
        startRunning();
    }

    //connect to server
    public void startRunning() {
        try {
            if (baseUrl != null) {
                if ((baseUrl.contains("/"))) {
                    baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf("/"));
                }

                socket = IO.socket("http://" + baseUrl + ":3000?uid=" + userId + "&usid=" + userSessionId);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (socket == null) return;
        socket
                .on(Socket.EVENT_CONNECT, CONNECTED)
                .on(Socket.EVENT_DISCONNECT, DISCONNECTED)
                .on("chat-message", CHAT_MESSAGE)
                .on("chats-active", CHATS_ACTIVE)
                .on("chat-logout", LOGOUT)
                .on("messages-read", MESSAGE_READ)
                .on("chat-login", CHAT_LOGIN);
        socket.connect();

        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageSender,
                new IntentFilter(getString(R.string.send_chat_message_broadcast_event)));

        LocalBroadcastManager.getInstance(this).registerReceiver(notifyLogoutReceiver,
                new IntentFilter(getString(R.string.logout_user_broadcast_event)));
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(TAG, "in onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(TAG, "in onUnbind");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageSender);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyLogoutReceiver);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(TAG, "in onRebind");
        super.onRebind(intent);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "in onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageSender);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyLogoutReceiver);
    }

    public class MyBinder extends Binder {
        public ChatService getService() {
            return ChatService.this;
        }
    }
}
