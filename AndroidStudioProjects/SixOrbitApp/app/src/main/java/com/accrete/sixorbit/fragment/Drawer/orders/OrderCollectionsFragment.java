package com.accrete.sixorbit.fragment.Drawer.orders;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.CollectionOrderTransactionAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CollectionTransactionData;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

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

/**
 * Created by {Anshul} on 6/6/18.
 */

public class OrderCollectionsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        CollectionOrderTransactionAdapter.MyCollectionsListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView textViewEmpty;
    private String orderId;
    private CollectionOrderTransactionAdapter adapter;
    private List<CollectionTransactionData> collectionTransactionDataList = new ArrayList<CollectionTransactionData>();

    public OrderCollectionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        orderId = bundle.getString(getString(R.string.order_id));
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            //Enable Touch Back
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (getActivity() != null) {
                                if (imageView.getVisibility() == View.GONE) {
                                    if (swipeRefreshLayout != null &&
                                            swipeRefreshLayout.getVisibility() != View.VISIBLE) {
                                        imageView.setVisibility(View.VISIBLE);
                                    }
                                }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                Ion.with(imageView)
                                        .animateGif(AnimateGifMode.ANIMATE)
                                        .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                        .withBitmapInfo();
                            }
                        }
                    });
                }
            }
        });
        thread.start();
    }

    public void doRefresh() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (collectionTransactionDataList != null && !collectionTransactionDataList.isEmpty()) {
                            collectionTransactionDataList.clear();
                        }
                        showLoader();
                        getCollectionsHistory(orderId);
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pending_manage_quotation, container, false);
        findViews(rootView);
        return rootView;
    }

    private void findViews(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textViewEmpty = (TextView) view.findViewById(R.id.empty_textView);

        adapter = new CollectionOrderTransactionAdapter(getActivity(), collectionTransactionDataList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(adapter);

        //Set Padding to recyclerview
        int sideMargin = getActivity().getResources().getDimensionPixelSize(R.dimen._8sdp);
        recyclerView.setPadding(sideMargin, 0, sideMargin, 0);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);
    }

    private void getCollectionsHistory(String orderId) {
        try {
            if (getActivity() != null) {
                task = getString(R.string.collection_order_collections);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getOrdersCollectionsHistoryList(version, key, task, userId,
                        accessToken, orderId);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));

                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (CollectionTransactionData collectionTransactionData :
                                        apiResponse.getData().getCollectionTransactionData()) {
                                    if (collectionTransactionData != null) {
                                        collectionTransactionDataList.add(collectionTransactionData);
                                    }
                                }
                            }
                            //Deleted User
                            else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            }
                            if (collectionTransactionDataList != null &&
                                    collectionTransactionDataList.size() == 0) {
                                textViewEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                textViewEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            adapter.notifyDataSetChanged();
                            hideLoader();
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            hideLoader();
                        }
                    }


                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        if (getActivity() != null && isAdded()) {
                            Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            hideLoader();
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            hideLoader();
        }
    }


    @Override
    public void onRefresh() {
        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (collectionTransactionDataList != null && collectionTransactionDataList.size() > 0) {
                showLoader();
                getCollectionsHistory(orderId);
            } else {
                showLoader();
                getCollectionsHistory(orderId);
            }
        } else {
            if (getActivity() != null && isAdded()) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onLongClicked(int position) {

    }
}
