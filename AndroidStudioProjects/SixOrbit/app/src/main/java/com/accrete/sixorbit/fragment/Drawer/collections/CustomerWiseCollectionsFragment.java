package com.accrete.sixorbit.fragment.Drawer.collections;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.activity.collections.CollectPaymentCustomerActivity;
import com.accrete.sixorbit.activity.customers.CustomersTabActivity;
import com.accrete.sixorbit.adapter.CustomerwiseCollectionsAdapter;
import com.accrete.sixorbit.adapter.LongClickActionsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerOutstandingCollection;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.sixorbit.helper.Constants.accessToken;
import static com.accrete.sixorbit.helper.Constants.key;
import static com.accrete.sixorbit.helper.Constants.task;
import static com.accrete.sixorbit.helper.Constants.userId;
import static com.accrete.sixorbit.helper.Constants.version;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomerWiseCollectionsFragment extends Fragment
        implements SwipeRefreshLayout.OnRefreshListener,
        CustomerwiseCollectionsAdapter.CollectionsListener,
        LongClickActionsAdapter.LongClickActionsAdapterListener {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private FloatingActionButton filterFab;
    private TextView textViewEmpty;
    private LinearLayout topLayout;
    private LinearLayoutManager mLayoutManager, mActionsLayoutManager;
    private List<CustomerOutstandingCollection> outstandingCollectionArrayList =
            new ArrayList<CustomerOutstandingCollection>();
    private boolean loading;
    private CustomerwiseCollectionsAdapter adapter;
    private String dataChanged, internetStatus, searchText;
    private DatabaseHandler databaseHandler;
    private AlertDialog dialogActions;
    private LongClickActionsAdapter actionsAdapter;


    public CustomerWiseCollectionsFragment() {
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
        getActivity().setTitle(getString(R.string.customerwise_outstanding));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(getString(R.string.customerwise_outstanding));
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // Set title
            getActivity().setTitle(getString(R.string.customerwise_outstanding));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(getString(R.string.customerwise_outstanding));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().setTitle(getString(R.string.collections));
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    public void onBackPressed() {
        //Enable Touch Back
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
        getChildFragmentManager().popBackStack();
    }

    private void hideLoader() {
        if (getActivity() != null && isAdded()) {
            if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
                imageView.setVisibility(View.GONE);
            }
            getActivity().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (searchView != null) {
                        searchView.setOnQueryTextListener(queryTextListener);
                    }
                }
            });
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
                                            !swipeRefreshLayout.isRefreshing()) {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections_invoice, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View rootView) {
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        textViewEmpty = (TextView) rootView.findViewById(R.id.textView_empty);
        topLayout = (LinearLayout) rootView.findViewById(R.id.top_layout);
        filterFab = (FloatingActionButton) rootView.findViewById(R.id.main_fab);
        filterFab.setVisibility(View.GONE);
        topLayout.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);

        adapter = new CustomerwiseCollectionsAdapter(getActivity(), outstandingCollectionArrayList, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

        databaseHandler = new DatabaseHandler(getActivity());

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) &&
                        outstandingCollectionArrayList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getCollectionsInvoices(outstandingCollectionArrayList.
                                get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //Listeners
        swipeRefreshLayout.setOnRefreshListener(this);
        doRefresh("");

    }

    public void doRefresh(String searchQuery) {
        if (outstandingCollectionArrayList != null) {
            loading = true;
            if (getActivity() != null && isAdded()) {
                searchText = searchQuery;
                //calling API
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    if (outstandingCollectionArrayList != null && outstandingCollectionArrayList.size() > 0) {
                        outstandingCollectionArrayList.clear();
                        getCollectionsInvoices(getString(R.string.last_updated_date),
                                "1");
                    } else {
                        getCollectionsInvoices(getString(R.string.last_updated_date),
                                "1");
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        try {
            menu.clear();
            inflater.inflate(R.menu.main_activity_actions, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            searchItem.setVisible(true);
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

            if (searchItem != null) {
                searchView = (SearchView) searchItem.getActionView();
            }

            if (searchView != null) {
                searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
                } catch (Exception e) {
                }
            }

            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //Log.e("OnTextChangeSubmit", newText);
                    if (timer != null) {
                        timer.cancel();
                    }
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            // do your actual work here
                            if (getActivity() != null && isAdded()) {
                                getActivity().runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (searchView != null) {
                                            searchView.setOnQueryTextListener(null);
                                        }
                                    }
                                });
                                doRefresh(newText);
                            }
                        }
                    }, 600); // 600ms delay before the timer executes the „run“ method from TimerTask

                    return false;
                }

            };
            searchView.setOnQueryTextListener(queryTextListener);
            super.onCreateOptionsMenu(menu, inflater);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh() {
        loading = true;

        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (outstandingCollectionArrayList != null && outstandingCollectionArrayList.size() > 0) {
                showLoader();
                getCollectionsInvoices(outstandingCollectionArrayList.get(0).getCreatedTs(), "1");
            } else {
                showLoader();
                getCollectionsInvoices(getString(R.string.last_updated_date), "1");
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
        if (AppPreferences.getBoolean(getActivity(), AppUtils.ISADMIN) ||
                databaseHandler.checkUsersPermission(getString(R.string.customers_view_details_permission))) {
            Intent intent = new Intent(getActivity(), CustomersTabActivity.class);
            intent.putExtra(getString(R.string.cuid), outstandingCollectionArrayList.get(position).getCuid());
            intent.putExtra(getString(R.string.wallet_balance), outstandingCollectionArrayList.get(position).getWalletBalance());
            //TODO To open Pending Invoice Tab
            intent.putExtra("redirect", 2);
            if (outstandingCollectionArrayList.get(position).getCustomerName() != null &&
                    !outstandingCollectionArrayList.get(position).getCustomerName().isEmpty()) {
                intent.putExtra(getString(R.string.name), outstandingCollectionArrayList.get(position)
                        .getCustomerName().trim());
            }
            startActivity(intent);
        } else {
            Toast.makeText(getActivity(), "Sorry, you've no permission to view details of a customer.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLongClicked(int position) {
        if (dialogActions == null || !dialogActions.isShowing()) {
            if (outstandingCollectionArrayList.get(position).getPendingAmount() != null &&
                    !outstandingCollectionArrayList.get(position).getPendingAmount().isEmpty() &&
                    Constants.ParseDouble(outstandingCollectionArrayList.get(position).getPendingAmount()) > 0) {
                dialogActionsItems(position);
            }
        }
    }

    private void getCollectionsInvoices(String time, String traversalValue) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.customer_outstanding_collections_task);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getCustomerwiseOutstanding(version, key, task, userId,
                        accessToken, time, traversalValue, searchText);
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));


                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (CustomerOutstandingCollection collectionData :
                                        apiResponse.getData().getCustomerOutstandingCollections()) {
                                    if (collectionData != null) {
                                        if (traversalValue.equals("2")) {
                                            if (!time.equals(collectionData.getCreatedTs())) {
                                                outstandingCollectionArrayList.add(collectionData);
                                            }
                                            dataChanged = "yes";
                                        } else if (traversalValue.equals("1")) {
                                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                                // To remove existing item
                                                if (!time.equals(collectionData.getCreatedTs())) {
                                                    outstandingCollectionArrayList.add(0, collectionData);
                                                }
                                            } else {
                                                if (!time.equals(collectionData.getCreatedTs())) {
                                                    outstandingCollectionArrayList.add(collectionData);
                                                }
                                            }
                                            dataChanged = "yes";
                                        }
                                    }
                                }
                                loading = false;
                            }
                            //Deleted User
                            else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                    apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                                //Logout
                                Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                            } else {
                                //  Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            if (outstandingCollectionArrayList != null && outstandingCollectionArrayList.size() == 0) {
                                textViewEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                textViewEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }

                            if (traversalValue.equals("2")) {
                                adapter.notifyDataSetChanged();
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                                }
                            } else if (traversalValue.equals("1")) {
                                if (dataChanged != null && dataChanged.equals("yes")) {
                                    adapter.notifyDataSetChanged();
                                    recyclerView.smoothScrollToPosition(0);
                                }
                            }

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

    private void dialogActionsItems(final int itemsPosition) {
        View dialogView = View.inflate(getActivity(), R.layout.dialog_orders_actions, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogActions = builder.create();
        dialogActions.setCanceledOnTouchOutside(false);
        ImageView imageViewBack;
        RecyclerView recyclerView;
        String[] actionsArr = null;

        actionsArr = new String[]{"Collect Payment"};

        recyclerView = (RecyclerView) dialogView.findViewById(R.id.recyclerView);
        imageViewBack = (ImageView) dialogView.findViewById(R.id.image_back);

        //Adapter
        actionsAdapter = new LongClickActionsAdapter(getActivity(), actionsArr, this, itemsPosition);
        mActionsLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mActionsLayoutManager);
        recyclerView.setAdapter(actionsAdapter);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogActions.dismiss();
            }
        });

        dialogActions.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (!dialogActions.isShowing()) {
            dialogActions.show();
        }
    }


    @Override
    public void onActionsRowClicked(int rowPosition, int itemsPosition) {
        if (rowPosition == 0) {
            internetStatus = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!internetStatus.equals(getString(R.string.not_connected_to_internet))) {
                if (dialogActions != null && dialogActions.isShowing()) {
                    dialogActions.dismiss();
                }
                navigateCollectPaymentScreen(outstandingCollectionArrayList.get(itemsPosition).getCuid(),
                        outstandingCollectionArrayList.get(itemsPosition).getCustomerName(),
                        outstandingCollectionArrayList.get(itemsPosition).getAlid(),
                        new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                                outstandingCollectionArrayList.get(itemsPosition).getPendingAmount())))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString(),
                        "");
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet_try_later), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void navigateCollectPaymentScreen(String cuId, String customerName, String alId,
                                              String pendingAmount, String invId) {
        Intent intent = new Intent(getActivity(), CollectPaymentCustomerActivity.class);
        intent.putExtra(getString(R.string.cuid), cuId);
        intent.putExtra(getString(R.string.customer), customerName);
        intent.putExtra(getString(R.string.alid), alId);
        intent.putExtra(getString(R.string.pending_amount), pendingAmount);
        intent.putExtra(getString(R.string.invid), invId);
        startActivity(intent);
    }
}
