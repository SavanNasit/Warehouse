package com.accrete.sixorbit.activity.Settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.RecentLoginListAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.RecentLogin;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

public class RecentLoginListActivity extends AppCompatActivity {

    public List<RecentLogin> recentLoginList = new ArrayList<>();
    private Toolbar toolbar;
    private RecentLoginListAdapter mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_login_list);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recent_login_recycler_view);
        mAdapter = new RecentLoginListAdapter(getApplicationContext(), recentLoginList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        toolbar.setTitle(getString(R.string.title_recent_login));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (!NetworkUtil.getConnectivityStatusString(RecentLoginListActivity.this).equals(getString(R.string.not_connected_to_internet))) {
            getRecentLoginList();
        } else {
            Toast.makeText(RecentLoginListActivity.this, getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    private void getRecentLoginList() {
        task = getString(R.string.task_recent_login);
        userId = AppPreferences.getUserId(this, AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(this, AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getApplicationContext(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    for (RecentLogin recentLogin : apiResponse.getData().getRecentLogin()) {
                        recentLoginList.add(recentLogin);
                        mAdapter.notifyDataSetChanged();
                    }
                } //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(RecentLoginListActivity.this, apiResponse.getMessage());
                } else {
                    Toast.makeText(RecentLoginListActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(RecentLoginListActivity.this, getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(call.request())));
                t.printStackTrace();
            }

        });
    }

    // http://localhost/rapidkartprocessadminv2/?urlq=service&version=1.0&key=123
    // &task=profile_info/fetch-recent-login&user_id=1&access_token=6366445353223245316
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
