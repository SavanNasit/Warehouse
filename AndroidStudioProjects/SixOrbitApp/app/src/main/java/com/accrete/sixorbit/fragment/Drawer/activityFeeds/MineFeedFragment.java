package com.accrete.sixorbit.fragment.Drawer.activityFeeds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.AllActivitiesAdapter;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.model.Notification;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by poonam on 14/7/17.
 */

public class MineFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener,
        SearchView.OnSuggestionListener {
    public static SearchView searchView;
    public AllActivitiesAdapter mAdapter;
    List<String> list = new ArrayList<>();
    private List<Notification> notifications = new ArrayList<>();
    private RecyclerView recyclerView;
    private SimpleCursorAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ArrayList<String> items = new ArrayList<>();
    private DatabaseHandler db;
    private String loggedinUId;
    private TextView empty_textView;

    public static MineFeedFragment newInstance() {
        return new MineFeedFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        db = new DatabaseHandler(getActivity());
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(final View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        empty_textView = (TextView) rootView.findViewById(R.id.empty_textView);
        empty_textView.setText(getString(R.string.no_feed));

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AllActivitiesAdapter(getActivity(), notifications, notifications.size());
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setEnabled(false);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            loggedinUId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        }
        //Display Data from Database
        getDataFromDB();
        if (notifications.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            empty_textView.setVisibility(View.GONE);
        } else {
            recyclerView.setVisibility(View.GONE);
            empty_textView.setVisibility(View.VISIBLE);
        }

    }

    public void onBackPressed() {
        //handle back press event
    }

    @Override
    public void onRefresh() {
//        getNotifications();
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
        builderDialog.setTitle(getString(R.string.select_file));
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
        getActivity().setTitle(R.string.activity_feeds);
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
        /*final List<Notification> filteredList = new ArrayList<>();
        for (int i = 0; i < notifications.size(); i++) {
            final String text = notifications.get(i).getMessage().toLowerCase();
            if (text.contains(searchView.getQuery())) {
                filteredList.add(notifications.get(i));
            }
        }
        mAdapter = new AllActivitiesAdapter(getActivity(), filteredList, filteredList.size());
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();*/

        return true;
    }

    public void getDataFromDB() {
        if (db != null) {
            Calendar c = Calendar.getInstance();
            System.out.println("Current time => " + c.getTime());
            Calendar lastWeek = Calendar.getInstance();
            lastWeek.add(Calendar.DAY_OF_YEAR, -7);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = df.format(c.getTime());
            String lastWeekDate = df.format(lastWeek.getTime());
            notifications.clear();
            list = db.getCommentUaid(loggedinUId);
            for (int k = 0; k < list.size(); k++) {
                notifications.addAll(db.getLastWeekActivityFeedsForAnUser(lastWeekDate, currentDate, list.get(k)));
            }

        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSuggestionClick(int position) {
       /* Cursor cursor = searchView.getSuggestionsAdapter().getCursor();
        cursor.moveToPosition(position);
        String suggestion = cursor.getString(1);//2 is the index of col containing suggestion name.
        searchView.setQuery(suggestion, false);//setting suggestion*/
        Cursor c = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
        c.moveToPosition(position);
        String suggestion = c.getString(c.getColumnIndex(getString(R.string.city_name)));
        Log.d(TAG, "onSuggestionClick: position: " + position + " suggestion: " + suggestion + " " + c.getColumnIndex("cityName"));
        searchView.setQuery(suggestion, false);


        return false;
    }
}
