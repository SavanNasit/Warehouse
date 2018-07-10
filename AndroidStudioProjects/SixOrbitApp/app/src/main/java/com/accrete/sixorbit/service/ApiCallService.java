package com.accrete.sixorbit.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.model.Contacts;
import com.accrete.sixorbit.model.Enquiry;
import com.accrete.sixorbit.model.FollowUp;
import com.accrete.sixorbit.model.Lead;
import com.accrete.sixorbit.model.LeadShippingAddress;
import com.accrete.sixorbit.model.Notification;
import com.accrete.sixorbit.model.NotificationTime;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.receiver.CallLogBroadcastReceiver;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 1/8/17.
 */

public class ApiCallService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;
    public static final long NOTIFY_INTERVAL = 30 * 1000; // 30 seconds
    private ResultReceiver receiver;
    private Bundle bundle = new Bundle();
    private Timer mTimer = null;
    private DatabaseHandler db;
    private Contacts contacts = new Contacts();
    private Comment comment = new Comment();
    private LeadShippingAddress leadShippingAddress = new LeadShippingAddress();
    private boolean pullFollowUpAPI = true, pullLeadAPI = true;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int followupsLimit = 0, leadsLimit = 0, followupsCount = 0, leadsCount = 0;

    /**
     * Creates an IntentService.Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public ApiCallService(String name) {
        super(name);
    }

    public ApiCallService() {
        super(ApiCallService.class.getName());
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }

        db = new DatabaseHandler(this);
        if (db != null) {
            if (!NetworkUtil.getConnectivityStatusString(ApiCallService.this).equals(getString(R.string.not_connected_to_internet))) {
                getActivityFeeds();
                getNotificationReadTime();
                //TODO - Not in use
                //getQuotations();
            }
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new ApiPullTimerTask(), 0, NOTIFY_INTERVAL);

        Intent intentSend = new Intent(this, CallLogBroadcastReceiver.class);
      /*  intentSend.putParcelableArrayListExtra("sendData", callLogDetailsArrayList);
        // intentSend.putExtra("data", "pooki");*/
        sendBroadcast(intentSend);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 234324243, intentSend, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60, pendingIntent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //   Log.d(TAG, "Api call service is  running....");

        //  Log.d(TAG, "Service Started!");
        receiver = intent.getParcelableExtra(getString(R.string.receiver));

        // syncFollowUps();
    }

    private void syncFollowUps() {
        isDbSynced();
    }

    private void isDbSynced() {
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        if (pullFollowUpAPI) {
            getFollowUps();
        }
        if (pullLeadAPI) {
            getLeads();
            getEnquiry();
        }

    }

    private void getActivityFeeds() {
        task = getString(R.string.notification);
        String lastUpdated;
        try {
            if (AppPreferences.getIsLogin(ApiCallService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ApiCallService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(ApiCallService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getDomain(ApiCallService.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
                //lastUpdated = lastUpdated.substring(0, lastUpdated.indexOf(" "));
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                lastUpdated = sdf.format(new Date(0));
            }
            Call<ApiResponse> call = apiService.getNotification(version, key, task, userId, accessToken, lastUpdated, "1");
            Log.d("Request", "" + String.valueOf(call));
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    receiver.send(STATUS_FINISHED, bundle);
                    AsyncTask<Object, Object, Object> saveFollowUpsAsyncTask = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            if (apiResponse != null && apiResponse.getData() != null
                                    && apiResponse.getData().getNotification() != null) {
                                for (final Notification notification : apiResponse.getData().getNotification()) {
                                    if (db.checkNotificationUAID(notification.getUaid())) {
                                        Log.d("UPDATE: ", "UPDATE ACTIVITY FEEDS.." + notification.getUid());
                                        db.updateActivityFeeds(notification);
                                    } else
                                        db.insertActivityFeedsData(notification);
                                    if (notification.getComment() != null) {
                                        for (int i = 0; i < notification.getComment().size(); i++) {
                                            comment.setUacid(notification.getComment().get(i).getUacid());
                                            comment.setUid(notification.getComment().get(i).getUid());
                                            comment.setUaid(notification.getComment().get(i).getUaid());
                                            comment.setComment(notification.getComment().get(i).getComment());
                                            comment.setCreatedTs(notification.getComment().get(i).getCreatedTs());
                                            comment.setSid(notification.getComment().get(i).getSid());
                                            comment.setSync_id(notification.getComment().get(i).getSync_id());
                                            comment.setSync(1);
                                            comment.setPost_uid(notification.getUid());
                                            if (db.checkCommentUACID(notification.getComment().get(i).getUacid())) {
                                                db.updateFeedsCommentsDetail(comment);
                                            } else {
                                                if (db.checkCommentSyncID(notification.getComment().get(i).getSync_id())) {
                                                    db.updateFeedsCommentsDetail(comment);
                                                } else
                                                    db.insertActivityFeedsComments(comment);
                                            }
                                        }
                                    }

                                }
                            }
                            SyncCheck syncCheck = new SyncCheck();
                            syncCheck.setService(getString(R.string.notification));
                            if (apiResponse != null && apiResponse.getData() != null &&
                                    apiResponse.getData().getTime() != null) {
                                syncCheck.setCallTime(apiResponse.getData().getTime());
                            }
                            if (db.getSyncTime(task).getCallTime() != null) {
                                db.updateSyncCheck(syncCheck);
                            } else db.addSyncCheck(syncCheck);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            receiver.send(STATUS_FINISHED, bundle);
                        }
                    };

                    saveFollowUpsAsyncTask.execute((Object[]) null);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dbSyncService() {
        startService(new Intent(this, rapidKartDbSyncService.class));
    }

    public void getNotificationReadTime() {
        try {
            task = getString(R.string.notification_read);
            if (AppPreferences.getIsLogin(ApiCallService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ApiCallService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(ApiCallService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(ApiCallService.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", "" + String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    AsyncTask<Object, Object, Object> saveReadTimeAsyncTask = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            if (apiResponse != null && apiResponse.getSuccess() &&
                                    apiResponse.getData() != null && apiResponse.getData().getNotificationTime() != null) {
                                for (NotificationTime notificationTime : apiResponse.getData().getNotificationTime()) {
                                    AppPreferences.setNotificationReadTime(ApiCallService.this,
                                            AppUtils.NOTIFICATION_READ_TIME, notificationTime.getCreatedTs());
                                }
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            receiver.send(STATUS_FINISHED, bundle);

                            if (apiResponse != null && apiResponse.getSuccess() != null &&
                                    !apiResponse.getSuccess()) {
                                //CustomisedToast.error(ApiCallService.this, apiResponse.getMessage()).show();
                            }


                        }
                    };
                    saveReadTimeAsyncTask.execute((Object[]) null);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void getFollowUps() {
        try {
            task = getString(R.string.follow_up_fetch);
            String lastUpdated;
            if (AppPreferences.getIsLogin(ApiCallService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ApiCallService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(ApiCallService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getDomain(ApiCallService.this, AppUtils.DOMAIN);
            }
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }
            Call<ApiResponse> call = apiService.getFollowUp(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", "" + String.valueOf(call));
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // clear the inbox
                    Log.d("Response", "" + String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    // bundle.putParcelable("followUp",apiResponse);
                    receiver.send(STATUS_FINISHED, bundle);

                    AsyncTask<Object, Object, Object> saveFollowUpsAsyncTask = new AsyncTask<Object, Object, Object>() {
                        @Override
                        protected Object doInBackground(Object... params) {
                            if (apiResponse != null && apiResponse.getData().getFollowups() != null) {
                                for (final FollowUp followUp : apiResponse.getData().getFollowups()) {

                                    Log.d("Insert: ", "Inserting Follow Ups .." + followUp.getName());
                                    Log.d("Inside API Call Service", followUp.getContactPersonEmail() + " " + followUp.getContactPersonMobile());

                                    if (followUp.getFoid() != null && !followUp.getFoid().equals("null") && !followUp.getFoid().isEmpty()) {
                                        if (db.checkFollowUpResult(followUp.getFoid())) {
                                            db.updateFollowUp(followUp);
                                        } else {
                                            if (followUp.getSyncId() != null && !followUp.getSyncId().isEmpty() && db.checkSyncIdFollowUp(followUp.getSyncId())) {
                                                db.updateFollowUpDataSyncId(followUp);
                                            } else {

                                                //  followUp.setLeadLocalId(String.valueOf(db.getLead(followUp.getLeaid(),"leaid").getID()));
                                                try {
                                                    if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                                            && db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                                            ) {
                                                        for (int i = 0; i < db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                                            if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                                    .equals(followUp.getContactedPerson())) {
                                                                followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                                            }
                                                        }
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                db.insertFollowUpData(followUp);


                                            }
                                        }
                                    } else {
                                        //  followUp.setLeadLocalId(String.valueOf(db.getLead(followUp.getLeaid(),"leaid").getID()));
                                        try {
                                            if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts() != null
                                                    && db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size() > 0
                                                    ) {
                                                for (int i = 0; i < db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().size(); i++) {
                                                    if (db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts().get(i).getName()
                                                            .equals(followUp.getContactedPerson())) {
                                                        followUp.setContactsId(String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getContacts()));

                                                    }
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        Log.d("Follow up ID 4", "" + String.valueOf(db.getLead(followUp.getLeadId(), getString(R.string.leaid)).getID()));
                                        db.insertFollowUpData(followUp);
                                    }

                                }

                                SyncCheck syncCheck = new SyncCheck();
                                syncCheck.setService(getString(R.string.follow_up_fetch));
                                if (!apiResponse.getData().getFollowups().equals("") && apiResponse.getData().getFollowups().size() > 0) {
                                    syncCheck.setCallTime(apiResponse.getData().getFollowups().get(apiResponse.getData().getFollowups().size() - 1).getUpdatedTs());
                                } else {
                                    syncCheck.setCallTime(db.getFollowUpUpdatedTs());
                                }

                                if (db != null) {
                                    db.addSyncCheck(syncCheck);
                                }

                                if (followupsCount == 0) {
                                    followupsCount++;
                                    followupsLimit += apiResponse.getData().getFollowups().size();
                                }
                                if (followupsCount != 0 && followupsLimit != 0 && followupsLimit == apiResponse.getData().getFollowups().size()) {
                                    pullFollowUpAPI = true;
                                } else {
                                    pullFollowUpAPI = false;
                                }

                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Object result) {
                            receiver.send(STATUS_FINISHED, bundle);
                        }
                    };

                    saveFollowUpsAsyncTask.execute((Object[]) null);
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLeads() {
        try {
            task = getString(R.string.lead);
            String lastUpdated;

            if (AppPreferences.getIsLogin(ApiCallService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ApiCallService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(ApiCallService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getDomain(ApiCallService.this, AppUtils.DOMAIN);
            }
            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getLead(version, key, task, userId, accessToken, lastUpdated);
            //  Log.d("Request", String.valueOf(call));
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", "" + String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    receiver.send(STATUS_FINISHED, bundle);
                    try {
                        AsyncTask<Object, Object, Object> saveLeadsAsyncTask = new AsyncTask<Object, Object, Object>() {
                            @Override
                            protected Object doInBackground(Object... params) {
                                if (apiResponse != null && apiResponse.getData().getLead() != null) {
                                    for (final Lead lead : apiResponse.getData().getLead()) {
                                        lead.setLeadSync("true");
                                        int localLeadId, localContacts;
                                        if (lead.getLeaid() != null) {
                                            boolean flagLeadForUpdate = db.checkLeadIdInLead(lead.getLeaid());
                                            if (flagLeadForUpdate) {
                                                Log.d("Update: ", "dbLeadForUpdate" + lead.getName());
                                                String specialInstruction = lead.getSpecialInstructions().replaceAll("'", "''");
                                                lead.setSpecialInstructions(specialInstruction);
                                                db.updateLead(lead, getString(R.string.leaid));
                                                db.deleteLeadContacts(lead.getLeaid());
                                                db.deleteLeadAddress(lead.getLeaid());
                                                db.deleteLeadFollowUp(lead.getLeaid());
                                            } else {
                                                if (lead.getSyncId() != null && !lead.getSyncId().isEmpty()) {
                                                    boolean flagSyncForUpdate = db.checkSyncIdInLead(lead.getSyncId());
                                                    Lead dbLeadForAdd = db.getLead(lead.getSyncId(), getString(R.string.syncID));
                                                    if (flagSyncForUpdate &&
                                                            dbLeadForAdd.getLeaid() != null &&
                                                            dbLeadForAdd.getLeaid().equals("")) {
                                                        Log.d("Update: ", "dbLeadForAdd" + lead.getName());
                                                        String specialInstruction = lead.getSpecialInstructions().replaceAll("'", "''");
                                                        lead.setSpecialInstructions(specialInstruction);
                                                        db.updateLead(lead, getString(R.string.leaid));
                                                        db.deleteLeadContacts(lead.getLeaid());
                                                        db.deleteLeadAddress(lead.getLeaid());
                                                        db.deleteLeadFollowUp(lead.getLeaid());
                                                    } else {
                                                        lead.setID(db.addLeads(lead));
                                                        db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()));
                                                    }
                                                } else {
                                                    lead.setID(db.addLeads(lead));
                                                    db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()));
                                                }
                                            }


                                            if (lead.getContacts() != null && lead.getContacts().size() > 0) {
                                                for (int i = 0; i < lead.getContacts().size(); i++) {
                                                    //  db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()), String.valueOf(leadContacts.getID()));
                                                    contacts.setCodeid(lead.getContacts().get(i).getCodeid());
                                                    contacts.setName(lead.getContacts().get(i).getName());
                                                    contacts.setPhoneNo(lead.getContacts().get(i).getPhoneNo());
                                                    contacts.setEmail(lead.getContacts().get(i).getEmail());
                                                    contacts.setDesignation(lead.getContacts().get(i).getDesignation());
                                                    contacts.setSyncID(lead.getSyncId());
                                                    contacts.setLeaid(lead.getLeaid());
                                                    contacts.setIsOwner(lead.getContacts().get(i).getIsOwner());

                                                    //TODO - Added on 2nd May
                                                    if (contacts.getCodeid() != null &&
                                                            !contacts.getCodeid().isEmpty()) {
                                                        List<FollowUp> upList = new ArrayList<>();
                                                        upList.addAll(db.getSameContactPersonsFollowUps(lead.getContacts().get(i).getCodeid()));
                                                        if (upList != null && upList.size() > 0) {
                                                            for (int j = 0; j < upList.size(); j++) {
                                                                if (db.checkFollowUpContactResult(contacts.getCodeid())) {
                                                                    db.updateFollowUpContacts(contacts.getName(),
                                                                            contacts.getPhoneNo(),
                                                                            contacts.getEmail(),
                                                                            contacts.getCodeid());
                                                                }
                                                            }
                                                        }
                                                    }

                                                    contacts.setID(db.addLeadContacts(contacts, lead.getLeaid(), lead.getSyncId()));
                                                    //  db.updateLocalLeadID(lead.getLeaid(), String.valueOf(lead.getID()), String.valueOf(contacts.getID()));

                                                }
                                            }

                                            Log.d("Insert: ", "Inserting Leads.." + lead.getName());
                                            Log.d("leadAddress size", "" + String.valueOf(lead.getShippingAddress().size()));
                                            if (lead.getShippingAddress() != null && lead.getShippingAddress().size() > 0) {
                                                for (int i = 0; i < lead.getShippingAddress().size(); i++) {
                                                    leadShippingAddress.setSaid(lead.getShippingAddress().get(i).getSaid());
                                                    leadShippingAddress.setFirstName(lead.getShippingAddress().get(i).getFirstName());
                                                    leadShippingAddress.setLastName(lead.getShippingAddress().get(i).getLastName());
                                                    leadShippingAddress.setLine1(lead.getShippingAddress().get(i).getLine1());
                                                    leadShippingAddress.setLine2(lead.getShippingAddress().get(i).getLine2());
                                                    leadShippingAddress.setCoverid(lead.getShippingAddress().get(i).getCoverid());
                                                    leadShippingAddress.setStid(lead.getShippingAddress().get(i).getStid());
                                                    leadShippingAddress.setCtid(lead.getShippingAddress().get(i).getCtid());
                                                    leadShippingAddress.setZipCode(lead.getShippingAddress().get(i).getZipCode());
                                                    leadShippingAddress.setMobile(lead.getShippingAddress().get(i).getMobile());
                                                    leadShippingAddress.setSatid(lead.getShippingAddress().get(i).getSatid());
                                                    leadShippingAddress.setSasid(lead.getShippingAddress().get(i).getSasid());
                                                    leadShippingAddress.setCity(lead.getShippingAddress().get(i).getCity());
                                                    leadShippingAddress.setCountry(lead.getShippingAddress().get(i).getCountry());
                                                    leadShippingAddress.setIsoCode(lead.getShippingAddress().get(i).getIsoCode());
                                                    leadShippingAddress.setState(lead.getShippingAddress().get(i).getState());
                                                    leadShippingAddress.setStateCode(lead.getShippingAddress().get(i).getStateCode());
                                                    leadShippingAddress.setSyncID(lead.getSyncId());
                                                    leadShippingAddress.setSiteName(lead.getShippingAddress().get(i).getSiteName());
                                                    leadShippingAddress.setLeaid(lead.getLeaid());
                                                    db.addLeadAddress(leadShippingAddress, lead.getLeaid());
                                                }
                                            }

                                        }

                                    }

                                    SyncCheck syncCheck = new SyncCheck();
                                    syncCheck.setService(getString(R.string.lead));
                                    if (!apiResponse.getData().getLead().equals("") && apiResponse.getData().getLead().size() > 0) {
                                        syncCheck.setCallTime(apiResponse.getData().getLead().get(apiResponse.getData().getLead().size() - 1).getUpdatedTs());

                                    } else {
                                        syncCheck.setCallTime(db.getLeadUpdatedTs());
                                    }

                                    if (db != null) {
                                        db.addSyncCheck(syncCheck);
                                    }

                                    if (leadsCount == 0) {
                                        leadsCount++;
                                        leadsLimit += apiResponse.getData().getLead().size();
                                    }
                                    if (leadsCount != 0 && leadsLimit != 0 && leadsLimit == apiResponse.getData().getLead().size()) {
                                        pullLeadAPI = true;
                                    } else {
                                        pullLeadAPI = false;
                                    }

                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Object result) {
                                receiver.send(STATUS_FINISHED, bundle);
                            }
                        };

                        saveLeadsAsyncTask.execute((Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEnquiry() {
        try {
            task = getString(R.string.enquiry_task);
            String lastUpdated;

            if (AppPreferences.getIsLogin(ApiCallService.this, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(ApiCallService.this, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(ApiCallService.this, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(ApiCallService.this, AppUtils.DOMAIN);
            }

            if (db.getSyncTime(task).getCallTime() != null) {
                lastUpdated = db.getSyncTime(task).getCallTime();
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                System.out.println(sdf.format(new Date(0))); //1970-01-01-00:00:00
                lastUpdated = sdf.format(new Date(0));
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.getEnquiry(version, key, task, userId, accessToken, lastUpdated);
            Log.d("Request", "" + String.valueOf(call));
            Log.d("url", "" + String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    // enquiryList.clear();
                    Log.d("Response", "" + String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse != null && apiResponse.getSuccess() != null && apiResponse.getSuccess()) {
                            for (Enquiry enquiry : apiResponse.getData().getEnquiry()) {
                                if (apiResponse.getData().getEnquiry() != null) {
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        //mTimer.cancel();
        // Log.i("APICALLSERVICE", "onCreate() , service stopped...");
    }

    class ApiPullTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!NetworkUtil.getConnectivityStatusString(ApiCallService.this).equals(getString(R.string.not_connected_to_internet))) {
                            isDbSynced();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}