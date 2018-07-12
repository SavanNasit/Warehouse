package com.accrete.sixorbit.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Comment;
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
 * Created by agt on 29/9/17.
 */

public class PostFeedsCommentsService extends IntentService {
    public static final int STATUS_RUNNING = 0;
    private static final String TAG = "PostFeedsCommentsService";
    ResultReceiver receiver;
    //private SharedPreferences settings, sharedPreferences;
    private String actionChat, commentText, timeStamp = "1970-01-01";
    private DatabaseHandler db;
    private String uaid, syncId, postUId;

    public PostFeedsCommentsService(String name) {
        super(name);
    }

    public PostFeedsCommentsService() {
        super(PostFeedsCommentsService.class.getName());
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        receiver = intent.getParcelableExtra(getString(R.string.receiver));
        uaid = intent.getExtras().getString(getString(R.string.uaid));
        syncId = intent.getExtras().getString(getString(R.string.syncID));
        commentText = intent.getExtras().getString(getString(R.string.comment_small));
        postUId = intent.getExtras().getString(getString(R.string.post_uid));
        syncContacts();
    }

    private void syncContacts() {
        isDbSynced();
    }

    private void isDbSynced() {
//        receiver.send(STATUS_RUNNING, Bundle.EMPTY);
        db = new DatabaseHandler(this);
        getPostCommentResponse();
    }

    private void dbSyncService() {
        startService(new Intent(this, rapidKartDbSyncService.class));
    }

    //    Send Comment API
    public void getPostCommentResponse() {
        task = getString(R.string.post_comment);
        /*settings = getSharedPreferences(MyPREFERENCES, 0);
        sharedPreferences = getSharedPreferences(PREFS_NAME, 0);
        if (settings != null && settings.getString("userId", null) != null) {
            userId = settings.getString("userId", null);
            accessToken = settings.getString("accessToken", null);
            ApiClient.BASE_URL = "http://" + sharedPreferences.getString(PREFS_LAST_DOMAIN, null) + "/";
        }*/
        if (AppPreferences.getIsLogin(PostFeedsCommentsService.this, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(PostFeedsCommentsService.this, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(PostFeedsCommentsService.this, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(PostFeedsCommentsService.this, AppUtils.DOMAIN);
        }
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        String type = "";
        Call<ApiResponse> call = apiService.postFeedsComments(version, key, task, userId, accessToken, userId, uaid, commentText, "", syncId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    Comment comment = new Comment();
                    comment.setSync_id(syncId);
                    comment.setUaid(uaid);
                    comment.setSync(1);
                    comment.setUid(userId);
                    comment.setPost_uid(postUId);
                    db.updateActivityFeedsComments(comment);
                } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials((Activity) getApplicationContext(), apiResponse.getMessage());
                } else {
                    Toast.makeText(PostFeedsCommentsService.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                //Toast.makeText(getApplicationContext(), "Unable to fetch json: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    //callbacks interface for communication with service clients!
    public interface Callbacks {
        void updateClient(long data);
    }
}

