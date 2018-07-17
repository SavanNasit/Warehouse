package com.accrete.sixorbit.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by agt on 25/9/17.
 */

public class ApiChatConversationService extends IntentService {

    public static final int STATUS_RUNNING = 0;
    private static final String TAG = "ChatConversationsService";
    ResultReceiver receiver;
    //private SharedPreferences settings, sharedPreferences;
    //private String MyPREFERENCES = "MyPrefs";
    private DatabaseHandler db;

    /**
     * Creates an IntentService.Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    public ApiChatConversationService(String name) {
        super(name);
    }

    public ApiChatConversationService() {
        super(ApiChatConversationService.class.getName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = intent.getParcelableExtra(getString(R.string.receiver));
        syncContacts();
    }

    private void syncContacts() {
        isDbSynced();
    }

    private void isDbSynced() {
        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        db = new DatabaseHandler(this);
       /* if (db.getAllAssignee().size() != 0) {
            // dbSyncService();
        } else {*/
        getAssignee();
        // }

    }

    //    Chat conversations API Call back by Anshul
    public void getAssignee() {
        task = getString(R.string.chat_conversations);
        /*settings = getSharedPreferences(MyPREFERENCES, 0);
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        if (settings != null && settings.getString("userId", null) != null) {
            userId = settings.getString("userId", null);
            accessToken = settings.getString("accessToken", null);
            ApiClient.BASE_URL = "http://" + sharedPreferences.getString(PREFS_LAST_DOMAIN, null) + "/";
        }*/
        if (AppPreferences.getIsLogin(ApiChatConversationService.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(ApiChatConversationService.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(ApiChatConversationService.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(ApiChatConversationService.this, AppUtils.LAST_DOMAIN);
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                  /*  for (ChatConversations chatConversations : apiResponse.getData().getChatConversations()) {
                    }*/

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
               // Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}
