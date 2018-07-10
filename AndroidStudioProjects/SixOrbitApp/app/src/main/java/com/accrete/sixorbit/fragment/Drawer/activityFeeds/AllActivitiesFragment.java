package com.accrete.sixorbit.fragment.Drawer.activityFeeds;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.AllActivitiesAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.Comment;
import com.accrete.sixorbit.model.Notification;
import com.accrete.sixorbit.model.NotificationTime;
import com.accrete.sixorbit.model.SyncCheck;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 10/4/17.
 */

public class AllActivitiesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        SearchView.OnSuggestionListener {
    public static SearchView searchView;
    public AllActivitiesAdapter mAdapter;
    private List<CharSequence> list = new ArrayList<CharSequence>();
    private LinearLayoutManager mLayoutManager;
    private String dataChanged;
    private List<Notification> notifications = new ArrayList<>();
    private List<Notification> tempList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SimpleCursorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView empty_textView;
    private View dialogView;
    private ArrayList<String> items = new ArrayList<>();
    private DatabaseHandler db;
    private boolean isUserScrolling = false;
    private boolean isListGoingUp = true;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private ProgressBar progressBar;

    public static AllActivitiesFragment newInstance() {
        return new AllActivitiesFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        db = new DatabaseHandler(getActivity());
        initializeView(rootView);
        return rootView;
    }

    public void updateUIPeriodicallyFromDb() {
        if (tempList != null && tempList.size() > 0) {
            tempList.clear();
        }
        tempList = db.getLastWeekActivityFeeds("13", notifications.size() + 1);
        for (int i = 0; i < tempList.size(); i++) {
            Notification notification = new Notification();
            notification.setUaid(tempList.get(i).getUaid());
            notification.setUid(tempList.get(i).getUid());
            notification.setMid(tempList.get(i).getMid());
            notification.setMotaid(tempList.get(i).getMotaid());
            notification.setMessage(tempList.get(i).getMessage());
            notification.setCreatedTs(tempList.get(i).getCreatedTs());
            notification.setFontawesomeicons(tempList.get(i).getFontawesomeicons());
            notification.setFontbackgroundcolor(tempList.get(i).getFontbackgroundcolor());
            notifications.add(notification);
        }
        loading = false;
        recyclerView.post(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initializeView(final View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        empty_textView = (TextView) rootView.findViewById(R.id.empty_textView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        //progressBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleHorizontal);
        empty_textView.setText(getString(R.string.no_feed));
        mAdapter = new AllActivitiesAdapter(getActivity(), notifications, notifications.size());

        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setEnabled(true);

        //Display Data from Database
        getDataFromDB();
        if (notifications.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            empty_textView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            empty_textView.setVisibility(View.VISIBLE);
        }

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        //progressBar.setVisibility(View.VISIBLE);
                        //progressBar.setMax(100);
                        if (db.getLastWeekActivityFeeds("13", notifications.size() + 1).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        } else
                            loadMoreFeeds("2", notifications.get(totalItemCount - 1).getCreatedTs());
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (db.getLastWeekActivityFeeds("13", notifications.size() + 1).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        }
                        //CustomisedToast.error(getActivity(), "Please check internet connection.").show();
                    }
                } else if (notifications.size() > 1 && notifications.size() < 4) {
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        //progressBar.setVisibility(View.VISIBLE);
                        //progressBar.setMax(100);
                        if (db.getLastWeekActivityFeeds("13", notifications.size() + 1).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        } else
                            loadMoreFeeds("2", notifications.get(totalItemCount - 1).getCreatedTs());
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (db.getLastWeekActivityFeeds("13", notifications.size() + 1).size() > 0) {
                            updateUIPeriodicallyFromDb();
                        }
                        //CustomisedToast.error(getActivity(), "Please check internet connection.").show();
                    }
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);

        if (notifications.size() > 0 && !NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            postNotificationReadTime(notifications.get(0).getCreatedTs(), true);
            getNotificationReadTime(true);
        }
        //    ((DrawerActivity) getActivity()).onUpdate();

    }

    public void onBackPressed() {
        //handle back press event
    }


    @Override
    public void onRefresh() {
        loading = true;
        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (notifications != null && notifications.size() > 0) {
                loadMoreFeeds("1", notifications.get(0).getCreatedTs());
            } else {
                loadMoreFeeds("1", getString(R.string.last_updated_date));
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            //CustomisedToast.error(getActivity(), "Please check internet connection.").show();
        }
    }

    @Override
    public void onClick(View v) {
        dialogModule();
    }

    private void dialogModule() {
        for (int i = 0; i < 20; i++) {
            list.add("test " + i);
        }

        final CharSequence[] dialogList = list.toArray(new CharSequence[list.size()]);
        final AlertDialog.Builder builderDialog = new AlertDialog.Builder(getActivity());
        builderDialog.setTitle(getString(R.string.select_title));
        int count = dialogList.length;
        boolean[] is_checked = new boolean[count];

        // Creating multiple selection by using setMutliChoiceItem method
        builderDialog.setMultiChoiceItems(dialogList, is_checked,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton, boolean isChecked) {
                    }
                });

        builderDialog.setPositiveButton(getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ListView list = ((AlertDialog) dialog).getListView();
                        // make selected item in the comma seprated string
                        StringBuilder stringBuilder = new StringBuilder();
                        for (int i = 0; i < list.getCount(); i++) {
                            boolean checked = list.isItemChecked(i);

                            if (checked) {
                                if (stringBuilder.length() > 0) stringBuilder.append(",");
                                stringBuilder.append(list.getItemAtPosition(i));


                            }
                        }


                    }

                });

        builderDialog.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  ((TextView) findViewById(R.id.text)).setText("Click here to open Dialog");
                    }
                });
        AlertDialog alert = builderDialog.create();
        alert.show();


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.activity_feeds));
        setHasOptionsMenu(true);
    }

    // History
    public void loadHistory(String query) {
        try {
            Log.d("query", query);
            Log.d("sizeQueryList", String.valueOf(items.size()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                ArrayList<Object> filteredList = new ArrayList<>();
                // Cursor
                String[] columns = new String[]{getString(R.string._id), getString(R.string.city_name),};
                Object[] temp = new Object[]{0, "default"};

                MatrixCursor cursor = new MatrixCursor(columns);

                for (int i = 0; i < items.size(); i++) {
                    temp[0] = i;
                    temp[1] = items.get(i);

                    String text = null;
                    if (items.get(i) != null) {
                        text = items.get(i).toLowerCase();
                        if (text.contains(query)) {
                            filteredList.add(items.get(i));
                            cursor.addRow(temp);
                        }
                        adapter.changeCursor(cursor);
                        searchView.setSuggestionsAdapter(adapter);
                        searchView.setOnSuggestionListener(this);
                    }
                }

            }
            searchView.setOnSuggestionListener(this);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onSuggestionSelect(int position) {
        final List<Notification> filteredList = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            final String text = notifications.get(i).getMessage().toLowerCase();
            if (text.contains(searchView.getQuery())) {
                filteredList.add(notifications.get(i));
            }
        }
        mAdapter = new AllActivitiesAdapter(getActivity(), filteredList, filteredList.size());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return true;
    }

    public void getDataFromDB() {
        if (db != null) {
            /*Calendar c = Calendar.getInstance();
            Calendar lastWeek = Calendar.getInstance();
            lastWeek.add(Calendar.DAY_OF_YEAR, -10);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = df.format(c.getTime());
            String lastWeekDate = df.format(lastWeek.getTime());*/
            notifications.clear();
            for (int i = 0; i < db.getLastWeekActivityFeeds("13", 0).size(); i++) {
                if (db.getLastWeekActivityFeeds("13", 0).get(i).getUid() != null &&
                        !db.getLastWeekActivityFeeds("13", 0).get(i).getUid().isEmpty()
                        && !db.getLastWeekActivityFeeds("13", 0).get(i).getUid().equals("")) {
                    notifications.add(db.getLastWeekActivityFeeds("13", 0).get(i));
                }
            }
            //notifications = db.getLastWeekActivityFeeds("13", "0");
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSuggestionClick(int position) {
        Cursor c = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        c.moveToPosition(position);
        String suggestion = c.getString(c.getColumnIndex(getString(R.string.city_name)));
        Log.d(TAG, "onSuggestionClick: position: " + position + " suggestion: " + suggestion + " " + c.getColumnIndex("cityName"));
        searchView.setQuery(suggestion, false);
        return false;
    }

    private void loadMoreFeeds(final String traversal_value, String lastUpdated) {
        task = getString(R.string.notification);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotification(version, key, task, userId, accessToken, lastUpdated, traversal_value);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                AsyncTask<Object, Object, Object> addUpdateNotificationsAsyncTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (apiResponse != null && apiResponse.getSuccess() && apiResponse.getData().getNotification() != null) {
                            for (final Notification notification : apiResponse.getData().getNotification()) {
                                if (db.checkNotificationUAID(notification.getUaid())) {
                                    Log.d("UPDATE: ", "UPDATE ACTIVITY FEEDS.." + notification.getUid());
                                    db.updateActivityFeeds(notification);
                                } else {
                                    db.insertActivityFeedsData(notification);
                                    if (traversal_value.equals("2")) {
                                        notifications.add(notification);
                                        dataChanged = "yes";
                                    } else if (traversal_value.equals("1")) {
                                        notifications.add(0, notification);
                                        dataChanged = "yes";
                                    }
                                }
                                if (notification.getComment() != null) {
                                    for (int i = 0; i < notification.getComment().size(); i++) {
                                        Comment comment = new Comment();
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
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }

                        SyncCheck syncCheck = new SyncCheck();
                        syncCheck.setService(getString(R.string.notification));
                        syncCheck.setCallTime(apiResponse.getData().getTime());
                        if (db.getSyncTime(task).getCallTime() != null) {
                            db.updateSyncCheck(syncCheck);
                        } else db.addSyncCheck(syncCheck);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        if (!apiResponse.getSuccess()) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            loading = false;
                        } else {
                            loading = false;
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            if (traversal_value.equals("2")) {
                                mAdapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                                }
                            } else if (traversal_value.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    mAdapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }
                            if (progressBar.getVisibility() == View.VISIBLE) {
                                progressBar.setVisibility(View.GONE);
                            }
                            if (notifications.size() > 0) {
                                if (recyclerView.getVisibility() == View.GONE) {
                                    recyclerView.setVisibility(View.VISIBLE);
                                    empty_textView.setVisibility(View.GONE);
                                }
                            } else {
                                if (recyclerView.getVisibility() == View.VISIBLE) {
                                    recyclerView.setVisibility(View.GONE);
                                    empty_textView.setVisibility(View.VISIBLE);
                                }
                            }
                            if (notifications != null && notifications.size() > 0
                                    && !NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                postNotificationReadTime(notifications.get(0).getCreatedTs(), false);
                                getNotificationReadTime(false);
                            }

                            if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            }
                        }
                    }
                };

                addUpdateNotificationsAsyncTask.execute((Object[]) null);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

    }

    public void getNotificationReadTime(final boolean updateUI) {
        task = getString(R.string.notification_read);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                AsyncTask<Object, Object, Object> saveReadTimeAsyncTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (apiResponse != null && apiResponse.getSuccess() && apiResponse.getData().getNotificationTime() != null) {
                            for (NotificationTime notificationTime : apiResponse.getData().getNotificationTime()) {
                                AppPreferences.setNotificationReadTime(getActivity(),
                                        AppUtils.NOTIFICATION_READ_TIME, notificationTime.getCreatedTs());

                            }
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        if (!apiResponse.getSuccess()) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (AppPreferences.getNotificationReadTime(getActivity(), AppUtils.NOTIFICATION_READ_TIME) != null) {
                            String str = AppPreferences.getNotificationReadTime(getActivity(), AppUtils.NOTIFICATION_READ_TIME);
                            String currentDate = df.format(c.getTime());
                            int a = db.getCountsUnreadActivityFeeds(str, currentDate);
                            if (a > 0) {
                                a = a - 1;
                            }
                            AppPreferences.setNotificationRead(getActivity(), AppUtils.NOTIFICATION_READ, String.valueOf(a));
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //Code to be executed after desired time
                                    if (updateUI) {
                                        //          ((DrawerActivity) getActivity()).onUpdate();
                                    }
                                }
                            }, 1 * 200);

                        }
                    }
                };
                saveReadTimeAsyncTask.execute((Object[]) null);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void postNotificationReadTime(String time, final boolean updateUI) {
        task = getString(R.string.notification_update_read);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);

        Call<ApiResponse> call = apiService.updateNotificationReadTime(version, key, task, userId, accessToken,
                userId, time);
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                AsyncTask<Object, Object, Object> updateReadTimeAsyncTask = new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (apiResponse != null && apiResponse.getSuccess()) {
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object result) {
                        if (!apiResponse.getSuccess()) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        if (AppPreferences.getNotificationReadTime(getActivity(), AppUtils.NOTIFICATION_READ_TIME) != null) {
                            String str = AppPreferences.getNotificationReadTime(getActivity(), AppUtils.NOTIFICATION_READ_TIME);
                            String currentDate = df.format(c.getTime());
                            int a = db.getCountsUnreadActivityFeeds(str, currentDate);
                            if (a > 0) {
                                a = a - 1;
                            }
                            AppPreferences.setNotificationRead(getActivity(), AppUtils.NOTIFICATION_READ, String.valueOf(a));
                            new Handler().postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    //Code to be executed after desired time
                                    if (updateUI) {
                                        //          ((DrawerActivity) getActivity()).onUpdate();
                                    }
                                }
                            }, 1 * 200);

                        }

                    }
                };
                updateReadTimeAsyncTask.execute((Object[]) null);
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}