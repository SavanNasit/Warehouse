package com.accrete.sixorbit.activity.chats;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.ChatContactsAdapter;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ContactsActivity extends AppCompatActivity implements ChatContactsAdapter.ChatContactsAdapterListener {

    public ChatContactsAdapter mAdapter;
    Map<ChatContacts, Integer> mapIndex;
    private DatabaseHandler databaseHandler;
    private FastScrollRecyclerView contactsRecyclerView;
    private TextView fragmentChatListEmptyView;
    private List<ChatContacts> chatcontacts = new ArrayList<>();
    // private SharedPreferences sharedPreferences;
    // private String MyPREFERENCES = "MyPrefs",
    private String loggedinUId;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHandler = new DatabaseHandler(this);

        contactsRecyclerView = (FastScrollRecyclerView) findViewById(R.id.contacts_recyclerView);
        fragmentChatListEmptyView = (TextView) findViewById(R.id.fragment_chat_list_empty_view);
        fragmentChatListEmptyView.setTypeface(Typeface.SANS_SERIF);

        mAdapter = new ChatContactsAdapter(ContactsActivity.this, chatcontacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        contactsRecyclerView.setLayoutManager(mLayoutManager);
        contactsRecyclerView.setItemAnimator(new DefaultItemAnimator());
        contactsRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        contactsRecyclerView.setAdapter(mAdapter);

        /*sharedPreferences = getSharedPreferences(MyPREFERENCES, 0);
        if (sharedPreferences != null && sharedPreferences.getString("userId", null) != null) {
            loggedinUId = sharedPreferences.getString("userId", null);
        }*/

        if (AppPreferences.getIsLogin(ContactsActivity.this, AppUtils.ISLOGIN)) {
            loggedinUId = AppPreferences.getUserId(ContactsActivity.this, AppUtils.USER_ID);
        }
        getDataFromDB();

    }

    private void getDataFromDB() {
        if (databaseHandler != null && databaseHandler.getAllAssignee().size() != 0) {
            chatcontacts = databaseHandler.getAllAssignee();
            for (int j = 0; j < chatcontacts.size(); j++) {
                if (chatcontacts.get(j).getUid() == Integer.valueOf(loggedinUId))
                    chatcontacts.remove(j);
            }
            //Arranged list alphabetically
            Collections.sort(chatcontacts, new Comparator<ChatContacts>() {
                public int compare(ChatContacts v1, ChatContacts v2) {
                    return v1.getName().compareTo(v2.getName());
                }
            });

            mAdapter = new ChatContactsAdapter(this, chatcontacts, this);
            contactsRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intent = new Intent(this, ChatMessageActivity.class);
        intent.putExtra(getString(R.string.uid), chatcontacts.get(position).getUid());
        intent.putExtra(getString(R.string.mobile), chatcontacts.get(position).getMobile() + "");
        if (chatcontacts.get(position).getName().contains(" ")) {
            intent.putExtra(getString(R.string.fname), chatcontacts.get(position).getName().substring(0, chatcontacts.get(position).getName().lastIndexOf(' ')));
        } else {
            intent.putExtra(getString(R.string.fname), chatcontacts.get(position).getName());
        }
        startActivity(intent);
        finish();
    }


}
