package com.accrete.sixorbit.fragment.Drawer.collectionsInvoice;

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
import com.accrete.sixorbit.adapter.CollectionsInvoiceItemsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.InvoiceItem;
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
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionsInvoiceItemsFragment extends Fragment implements
        SwipeRefreshLayout.OnRefreshListener,
        CollectionsInvoiceItemsAdapter.CollectionsInvoiceItemsAdapterListener {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView emptyTextView;
    private CollectionsInvoiceItemsAdapter collectionsInvoiceItemsAdapter;
    private List<InvoiceItem> invoiceItemList = new ArrayList<>();
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private String status, dataChanged;
    private String invId;

    public CollectionsInvoiceItemsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        invId = bundle.getString(getString(R.string.invid));
    }

    public void doRefresh() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (invoiceItemList != null && !invoiceItemList.isEmpty()) {
                            invoiceItemList.clear();
                        }
                        showLoader();
                        getInvoicesList(getString(R.string.last_updated_date), "1", invId);
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
                                if (invoiceItemList != null && invoiceItemList.size() == 0) {
                                    if (imageView.getVisibility() == View.GONE) {
                                        if (emptyTextView.getVisibility() != View.VISIBLE)
                                            imageView.setVisibility(View.VISIBLE);
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
                        }
                    });
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
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);

        collectionsInvoiceItemsAdapter = new CollectionsInvoiceItemsAdapter(getActivity(), invoiceItemList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setAdapter(collectionsInvoiceItemsAdapter);

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && invoiceItemList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (getActivity() != null && isAdded()) {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            getInvoicesList(invoiceItemList.get(totalItemCount - 1).getCreatedTs(), "2",
                                    invId);
                        } else {
                            if (swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null && isAdded()) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (invoiceItemList != null && invoiceItemList.size() > 0) {
                    getInvoicesList(invoiceItemList.get(0).getCreatedTs(), "1", invId);
                } else {
                    showLoader();
                    getInvoicesList(getString(R.string.last_updated_date), "1", invId);
                }
                recyclerView.setVisibility(View.VISIBLE);
                emptyTextView.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(true);

            } else {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText(getString(R.string.no_internet_try_later));
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void getInvoicesList(final String time, final String traversalValue, final String invId) {
        String task = getString(R.string.collection_invoice_items_details);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getCollectionsInvoicesItemsList(version, key, task, userId, accessToken,
                time, traversalValue, invId);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // enquiryList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse != null && apiResponse.getSuccess()) {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);

                        for (InvoiceItem invoiceItem : apiResponse.getData().getInvoiceItems()) {
                            if (invoiceItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(invoiceItem.getCreatedTs())) {
                                        invoiceItemList.add(invoiceItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (swipeRefreshLayout != null &&
                                            swipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(invoiceItem.getCreatedTs())) {
                                            invoiceItemList.add(0, invoiceItem);
                                        }
                                    } else {
                                        if (!time.equals(invoiceItem.getCreatedTs())) {
                                            invoiceItemList.add(invoiceItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (invoiceItemList != null && invoiceItemList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            emptyTextView.setText("No collection.");
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            collectionsInvoiceItemsAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // quotationRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                collectionsInvoiceItemsAdapter.notifyDataSetChanged();
                                recyclerView.smoothScrollToPosition(0);
                            }
                        }


                    } else {
                        if (apiResponse != null && apiResponse.getSuccessCode() != null &&
                                apiResponse.getSuccessCode().equals("10001")) {
                            emptyTextView.setText(getString(R.string.no_data_available));
                            recyclerView.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.VISIBLE);
                        }
                        //Deleted User
                        else if (apiResponse != null && apiResponse != null &&
                                apiResponse.getSuccessCode() != null &&
                                (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN))) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                        if (invoiceItemList != null && invoiceItemList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            emptyTextView.setText(getString(R.string.no_data_available));
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoader();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    hideLoader();
                }
            }
        });
    }

    @Override
    public void onMessageRowClicked(int position) {

    }
}
