package com.accrete.sixorbit.fragment.Drawer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.chats.ChatMessageActivity;
import com.accrete.sixorbit.activity.chats.ContactsActivity;
import com.accrete.sixorbit.adapter.ChatListAdapter;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.model.ChatMessage;
import com.accrete.sixorbit.service.ChatService;

import java.util.ArrayList;
import java.util.List;

import static com.accrete.sixorbit.utils.MSupportConstants.REQUEST_CODE_ASK_STORAGE_PERMISSIONS;
import static com.accrete.sixorbit.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 9/6/17.
 */

public class ChatListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ChatListAdapter.ChatListAdapterListener, View.OnClickListener {

    public TextView textViewEmptyView;
    public ChatListAdapter mAdapter;
    public FloatingActionButton floatingActionButtonFilter;
    ChatService chatService;
    boolean mServiceBound = false;
    Handler handler = new Handler();
    private final Runnable updateOnlineStatus = new Runnable() {
        public void run() {
            try {
                mAdapter.notifyDataSetChanged();
                handler.postDelayed(this, 1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ChatMessage> ChatList = new ArrayList<ChatMessage>();
    private DatabaseHandler databaseHandler;
    private List<ChatContacts> chatcontacts = new ArrayList<>();

    public static ChatListFragment newInstance(String title) {
        ChatListFragment chatListFragment = new ChatListFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        chatListFragment.setArguments(args);
        return (chatListFragment);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_list, container, false);
        initializeView(rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateOnlineStatus);
    }

    private void initializeView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_chat_list_recycler_view);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_chat_list_swipe_refresh_layout);
        floatingActionButtonFilter = (FloatingActionButton) rootView.findViewById(R.id.fragment_chat_list_create_new_chat);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.fragment_chat_list_empty_view);
        databaseHandler = new DatabaseHandler(getActivity());

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
        mAdapter = new ChatListAdapter(getActivity(), ChatList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        floatingActionButtonFilter.setOnClickListener(this);
        textViewEmptyView.setTypeface(Typeface.SANS_SERIF);

        textViewEmptyView.setText(getString(R.string.not_done_chat_with_anyone));

        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                    }
                }
        );
        handler.post(updateOnlineStatus);
        //  getDataFromDB();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity()
                .setTitle(getString(R.string.chat_fragment));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity()
                .setTitle(getString(R.string.chat_fragment));
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        ChatList = databaseHandler.getRecentChats();
        mAdapter = new ChatListAdapter(getActivity(), ChatList, this);
        recyclerView.setAdapter(mAdapter);
        if (ChatList.size() == 0) {
            textViewEmptyView.setVisibility(View.VISIBLE);
        } else textViewEmptyView.setVisibility(View.GONE);

    }

    @Override
    public void onRefresh() {
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity()
                    .setTitle(getString(R.string.chat_fragment));
        }
    }

    private void getDataFromDB() {
        if (databaseHandler != null && databaseHandler.getAllAssignee().size() != 0) {
            chatcontacts = databaseHandler.getAllAssignee();
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
        intent.putExtra(getString(R.string.uid), Integer.valueOf(ChatList.get(position).getUid()));
        ChatContacts chatcontacts = databaseHandler.getUserData(Integer.valueOf(ChatList.get(position).getUid()));
        if (chatcontacts.getName() != null) {
            if (chatcontacts.getName().toString().trim().contains(" ")) {
                intent.putExtra(getString(R.string.fname), chatcontacts.getName().substring(0, chatcontacts.getName().lastIndexOf(' ')));
            } else {
                intent.putExtra(getString(R.string.fname), chatcontacts.getName());
            }
        }
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        /*if (android.os.Build.VERSION.SDK_INT >= 23) {
            askStoragePermission();
        } else {*/
        Intent intent = new Intent(getActivity(), ContactsActivity.class);
        startActivity(intent);
        //}

    }

    public void askStoragePermission() {
        if (checkPermissionWithRationale(getActivity(), new ChatListFragment(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_STORAGE_PERMISSIONS)) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        }
    }

}
