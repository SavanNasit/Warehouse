package com.accrete.sixorbit.fragment.Drawer.quotation;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.accrete.sixorbit.activity.quotations.QuotationDetailsActivity;
import com.accrete.sixorbit.activity.quotations.QuotationHistoryActivity;
import com.accrete.sixorbit.adapter.CancelledManageQuotationAdapter;
import com.accrete.sixorbit.adapter.LongClickActionsAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.QuotationDatum;
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
 * Created by poonam on 12/28/17.
 */

public class CancelledManageQuotationFragment extends Fragment implements CancelledManageQuotationAdapter.
        CancelledManageQuotationAdapterListener, SwipeRefreshLayout.OnRefreshListener,
        LongClickActionsAdapter.LongClickActionsAdapterListener {

    private SwipeRefreshLayout quotationSwipeRefreshLayout;
    private RecyclerView quotationRecyclerView;
    private TextView quotationTextView;
    private CancelledManageQuotationAdapter quotationsAdapter;
    private List<QuotationDatum> quotationList = new ArrayList<>();
    private ImageView imageView;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private boolean loading;
    private String status, dataChanged;
    private String[] actionsArr = new String[]{"View Details", "Quotation History"};
    private AlertDialog dialogActions;
    private LongClickActionsAdapter actionsAdapter;
    private LinearLayoutManager mActionsLayoutManager;
    private String searchText;

    @Override
    public void onLongClicked(int position) {
        if (dialogActions == null || !dialogActions.isShowing()) {
            dialogActionsItems(position);
        }
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
                              //  if (quotationList != null && quotationList.size() == 0) {
                                    if (imageView.getVisibility() == View.GONE) {
                                        //if (quotationTextView.getVisibility() != View.VISIBLE)
                                            imageView.setVisibility(View.VISIBLE);
                                    }

                                    Ion.with(imageView)
                                            .animateGif(AnimateGifMode.ANIMATE)
                                            .load("android.resource://" + getActivity().getPackageName() + "/" + R.raw.loader)
                                            .withBitmapInfo();
                             //   }
                                //Disable Touch
                                getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

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
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(View rootView) {
        quotationSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        quotationRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        quotationTextView = (TextView) rootView.findViewById(R.id.empty_textView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);
        imageView.setVisibility(View.GONE);

        quotationsAdapter = new CancelledManageQuotationAdapter(getActivity(), quotationList, this, "");
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        quotationRecyclerView.setLayoutManager(mLayoutManager);
        quotationRecyclerView.setHasFixedSize(true);
        quotationRecyclerView.setItemAnimator(new DefaultItemAnimator());
        quotationRecyclerView.setNestedScrollingEnabled(false);
        quotationRecyclerView.setAdapter(quotationsAdapter);

        callAPI("");

        quotationSwipeRefreshLayout.setEnabled(false);
        quotationSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (quotationSwipeRefreshLayout.isRefreshing()) {
                    quotationSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        //Scroll Listener
        quotationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && quotationList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (getActivity() != null && isAdded()) {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            getQuotationsList(quotationList.get(totalItemCount - 1).getCreatedTs(), "2");
                        } else {
                            if (quotationSwipeRefreshLayout.isRefreshing()) {
                                quotationSwipeRefreshLayout.setRefreshing(false);
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

        quotationSwipeRefreshLayout.setOnRefreshListener(this);
    }

    public void callAPI(String searchQuery) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null && isAdded()) {
                    searchText = searchQuery;
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        if (quotationList != null && quotationList.size()>0) {
                            quotationList.clear();
                            getActivity().runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    // Stuff that updates the UI
                                    quotationsAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                        showLoader();
                        getQuotationsList(getString(R.string.last_updated_date), "1");
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

    private void getQuotationsList(final String time, final String traversalValue) {
        String task = getString(R.string.manage_quotation_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getPendingQuotationList(version, key, task, userId, accessToken,
                time, traversalValue, "3", searchText);
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
                        quotationRecyclerView.setVisibility(View.VISIBLE);
                        quotationTextView.setVisibility(View.GONE);

                        for (QuotationDatum packedItem : apiResponse.getData().getQuotationData()) {
                            if (packedItem != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(packedItem.getCreatedTs())) {
                                        quotationList.add(packedItem);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (quotationSwipeRefreshLayout != null &&
                                            quotationSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            quotationList.add(0, packedItem);
                                        }
                                    } else {
                                        if (!time.equals(packedItem.getCreatedTs())) {
                                            quotationList.add(packedItem);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (quotationList != null && quotationList.size() == 0) {
                            quotationTextView.setVisibility(View.VISIBLE);
                            quotationRecyclerView.setVisibility(View.GONE);
                            quotationTextView.setText("No collection.");
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            quotationTextView.setVisibility(View.GONE);
                            quotationRecyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (quotationSwipeRefreshLayout != null && quotationSwipeRefreshLayout.isRefreshing()) {
                            quotationSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            quotationsAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // quotationRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                quotationsAdapter.notifyDataSetChanged();
                                quotationRecyclerView.smoothScrollToPosition(0);
                            }
                        }


                    } else {
                        if (apiResponse != null && apiResponse.getSuccessCode() != null &&
                                apiResponse.getSuccessCode().equals("10001")) {
                            quotationTextView.setText(getString(R.string.no_data_available));
                            quotationRecyclerView.setVisibility(View.GONE);
                            quotationTextView.setVisibility(View.VISIBLE);
                        }
                        //Deleted User
                        else if (apiResponse != null && apiResponse != null &&
                                apiResponse.getSuccessCode() != null &&
                                (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN))) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        }
                        if (quotationList != null && quotationList.size() == 0) {
                            quotationTextView.setVisibility(View.VISIBLE);
                            quotationTextView.setText(getString(R.string.no_data_available));
                            quotationRecyclerView.setVisibility(View.GONE);
                        } else {
                            quotationTextView.setVisibility(View.GONE);
                            quotationRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (quotationSwipeRefreshLayout != null && quotationSwipeRefreshLayout.isRefreshing()) {
                            quotationSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                  /*  if (sw != null && packedSwipeRefreshLayout.isRefreshing()) {
                        packedSwipeRefreshLayout.setRefreshing(false);
                    }*/
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

    private void navigateToQuotationsDetails(int position) {
        Intent intent = new Intent(getActivity(), QuotationDetailsActivity.class);
        intent.putExtra(getString(R.string.qo_id), quotationList.get(position).getQoid());
        intent.putExtra(getString(R.string.cuid), quotationList.get(position).getCuid());
        intent.putExtra(getString(R.string.qo_id_text), quotationList.get(position).getQuotationID());
        startActivity(intent);
    }

    @Override
    public void onMessageRowClicked(int position) {
        if (dialogActions != null && dialogActions.isShowing()) {
            dialogActions.dismiss();
        }
        navigateToQuotationsDetails(position);
    }

    @Override
    public void onRefresh() {
        if (getActivity() != null && isAdded()) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                if (quotationList != null && quotationList.size() > 0) {
                    getQuotationsList(quotationList.get(0).getCreatedTs(), "1");
                } else {
                    showLoader();
                    getQuotationsList(getString(R.string.last_updated_date), "1");
                }
                quotationRecyclerView.setVisibility(View.VISIBLE);
                quotationTextView.setVisibility(View.GONE);
                quotationSwipeRefreshLayout.setRefreshing(true);

            } else {
                quotationRecyclerView.setVisibility(View.GONE);
                quotationTextView.setVisibility(View.VISIBLE);
                quotationTextView.setText(getString(R.string.no_internet_try_later));
                quotationSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    private void navigateToQuotationsHistory(int position) {
        Intent intent = new Intent(getActivity(), QuotationHistoryActivity.class);
        intent.putExtra(getString(R.string.qo_id), quotationList.get(position).getQoid());
        intent.putExtra(getString(R.string.qo_id_text), quotationList.get(position).getQuotationID());
        startActivity(intent);
    }

    @Override
    public void onActionsRowClicked(int position, int itemsPosition) {
        if (position == 0) {
            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateToQuotationsDetails(itemsPosition);
        } else if (position == 1) {

            if (dialogActions != null && dialogActions.isShowing()) {
                dialogActions.dismiss();
            }
            navigateToQuotationsHistory(itemsPosition);
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


}
