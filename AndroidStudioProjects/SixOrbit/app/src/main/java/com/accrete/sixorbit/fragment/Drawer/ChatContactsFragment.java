package com.accrete.sixorbit.fragment.Drawer;


import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.chats.ChatMessageActivity;
import com.accrete.sixorbit.adapter.ChatContactsAdapter;
import com.accrete.sixorbit.adapter.ChatContactsHorizontalAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DividerItemDecoration;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.ChatContacts;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.activity.domain.DomainActivity.PREFS_LAST_DOMAIN;
import static com.accrete.sixorbit.activity.domain.DomainActivity.PREFS_NAME;
import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * Created by poonam on 9/6/17.
 */

public class ChatContactsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, ChatContactsAdapter.ChatContactsAdapterListener, View.OnClickListener, ChatContactsHorizontalAdapter.ChatContactsHorizontalAdapterListener {

    public static TextView textViewEmptyView;
    public ChatContactsAdapter mAdapter;
    public FloatingActionButton floatingActionButtonFilter;
    SharedPreferences sharedPreferences;
    private RecyclerView recyclerView, recyclerViewHorizontal;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ChatContacts> chatcontacts = new ArrayList<>();
    private ChatContactsHorizontalAdapter mAdapterHorizontal;
    private String MyPREFERENCES = "MyPrefs";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_contacts, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.fragment_chat_list_recycler_view);
        recyclerViewHorizontal = (RecyclerView) rootView.findViewById(R.id.fragment_chat_list_rv_latest_ping);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fragment_chat_list_swipe_refresh_layout);
        floatingActionButtonFilter = (FloatingActionButton) rootView.findViewById(R.id.fragment_chat_list_create_new_chat);
        textViewEmptyView = (TextView) rootView.findViewById(R.id.fragment_chat_list_empty_view);
        swipeRefreshLayout.setOnRefreshListener(this);
        mAdapter = new ChatContactsAdapter(getActivity(), chatcontacts, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        mAdapterHorizontal = new ChatContactsHorizontalAdapter(getActivity(), chatcontacts, this);

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHorizontal.setLayoutManager(layoutManager);
        recyclerView.setMinimumWidth(200);
        recyclerViewHorizontal.setAdapter(mAdapterHorizontal);

        floatingActionButtonFilter.setOnClickListener(this);


        // show loader and fetch messages
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        getChatContacts();
                    }
                }
        );

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
                .setTitle(R.string.chat_fragment);
    }

    @Override
    public void onRefresh() {
        // swipe refresh is performed, fetch the messages again
        getChatContacts();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            //getActivity()
            //.setProfile_name(R.string.chat_fragment);
        }
    }

    @Override
    public void onMessageRowClicked(int position) {
        /*ChatMessagesFragment chatMessagesFragment = new ChatMessagesFragment();
        getFragmentManager().beginTransaction().replace(R.id.chat_contact_container, chatMessagesFragment, "ChatMessagesFragment").addToBackStack(null).commit();*/
        Intent intent = new Intent(getActivity(), ChatMessageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
    }

    private void getChatContacts() {
        task = getString(R.string.chat_contacts);
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences(MyPREFERENCES, 0);
        sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, 0);
        if (settings != null && settings.getString(getString(R.string.userId), null) != null) {
            userId = settings.getString(getString(R.string.userId), null);
            accessToken = settings.getString(getString(R.string.access_token), null);
            ApiClient.BASE_URL = "http://" + sharedPreferences.getString(PREFS_LAST_DOMAIN, null) + "/";
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getNotificationRead(version, key, task, userId, accessToken);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // clear the inbox
                chatcontacts.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    for (ChatContacts chatContacts : apiResponse.getData().getChatContacts()) {
                        //notification.setFontbackgroundcolor(String.valueOf(Color.parseColor("#000000")));
                        chatcontacts.add(chatContacts);
                    }
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
            }
        });

    }

}
