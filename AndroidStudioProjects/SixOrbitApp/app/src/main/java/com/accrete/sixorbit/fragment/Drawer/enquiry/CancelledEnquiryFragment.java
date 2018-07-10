package com.accrete.sixorbit.fragment.Drawer.enquiry;


import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.accrete.sixorbit.activity.enquiry.EnquiryDetailActivity;
import com.accrete.sixorbit.adapter.ManageEnquiryAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.AllEnquiry;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.lang.reflect.Field;
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
public class CancelledEnquiryFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        ManageEnquiryAdapter.EnquiriesListener {
    private SearchView searchView = null;
    private SearchView.OnQueryTextListener queryTextListener;
    private Timer timer;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private ImageView imageView;
    private TextView textViewEmpty;
    private LinearLayout bottomLayout;
    private TextView balanceTextView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private LinearLayoutManager mLayoutManager;
    private List<AllEnquiry> allEnquiryList = new ArrayList<AllEnquiry>();
    private boolean loading;
    private ManageEnquiryAdapter adapter;
    private String dataChanged, searchText;
    private DatabaseHandler databaseHandler;

    public CancelledEnquiryFragment() {
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
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
        View view = inflater.inflate(R.layout.fragment_manage_orders, container, false);
        findViews(view);
        return view;
    }

    private void findViews(View view) {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        imageView = (ImageView) view.findViewById(R.id.imageView);
        textViewEmpty = (TextView) view.findViewById(R.id.textView_empty);
        bottomLayout = (LinearLayout) view.findViewById(R.id.bottom_layout);
        balanceTextView = (TextView) view.findViewById(R.id.balance_textView);

        adapter = new ManageEnquiryAdapter(getActivity(), allEnquiryList, this);
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
                        allEnquiryList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getEnquiries(allEnquiryList.
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
        // doRefresh("");

    }

    public void doRefresh(String searchQuery) {
        if (allEnquiryList != null) {
            loading = true;
            if (getActivity() != null && isAdded()) {
                searchText = searchQuery;
                //calling API
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    if (allEnquiryList != null && allEnquiryList.size() > 0) {
                        allEnquiryList.clear();
                        getEnquiries(getString(R.string.last_updated_date),
                                "1");
                    } else {
                        getEnquiries(getString(R.string.last_updated_date),
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
            if (allEnquiryList != null && allEnquiryList.size() > 0) {
                showLoader();
                getEnquiries(allEnquiryList.get(0).getCreatedTs(), "1");
            } else {
                showLoader();
                getEnquiries(getString(R.string.last_updated_date), "1");
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

    private void navigateDetailScreen(String enquiryId, String enquiryText) {
        Intent intent = new Intent(getActivity(), EnquiryDetailActivity.class);
        intent.putExtra(getString(R.string.en_id), enquiryId);
        intent.putExtra(getString(R.string.enquiry_id_text), enquiryText);
        startActivity(intent);
    }

    @Override
    public void onMessageRowClicked(int position) {
        navigateDetailScreen(allEnquiryList.get(position).getEnid(),
                allEnquiryList.get(position).getEnquiryID());
    }


    @Override
    public void onLongClicked(int position) {

    }

    private void getEnquiries(String time, String traversalValue) {
        try {
            if (getActivity() != null && isAdded()) {
                task = getString(R.string.fetch_enquiries_task);
                userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);

                final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
                Call call = apiService.getEnquiryList(version, key, task, userId,
                        accessToken, time, traversalValue, searchText, "3");
                Log.v("Request", String.valueOf(call));
                Log.v("url", String.valueOf(call.request().url()));


                call.enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                        final ApiResponse apiResponse = (ApiResponse) response.body();
                        try {
                            if (apiResponse != null && apiResponse.getSuccess()) {
                                for (AllEnquiry enquiry :
                                        apiResponse.getData().getAllEnquiry()) {
                                    if (enquiry != null) {
                                        if (traversalValue.equals("2")) {
                                            if (!time.equals(enquiry.getCreatedTs())) {
                                                allEnquiryList.add(enquiry);
                                            }
                                            dataChanged = "yes";
                                        } else if (traversalValue.equals("1")) {
                                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                                // To remove existing item
                                                if (!time.equals(enquiry.getCreatedTs())) {
                                                    allEnquiryList.add(0, enquiry);
                                                }
                                            } else {
                                                if (!time.equals(enquiry.getCreatedTs())) {
                                                    allEnquiryList.add(enquiry);
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
                            if (allEnquiryList != null && allEnquiryList.size() == 0) {
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

}
