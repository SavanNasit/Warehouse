package com.accrete.sixorbit.fragment.Drawer.vendor;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.VendorRecentTransactionAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.VendorRecentTransaction;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.RestrictFutureDatePickerFragment;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
 * Created by poonam on 1/11/17.
 */

public class VendorTransactionTabFragment extends Fragment implements View.OnClickListener, PassDateToCounsellor,
        VendorRecentTransactionAdapter.RecentTransactionAdapterListener, SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout vendorTransactionSwipeRefreshLayout;
    private RecyclerView vendorTransactionRecyclerView;
    private TextView vendorTransactionEmptyView;
    private LinearLayout vendorTransactionEmail;
    private FloatingActionButton vendorTransactionFabEmail;
    private LinearLayout vendorTransactionDownload;
    private FloatingActionButton vendorTransactionFabDownload;
    private FloatingActionButton vendorTransactionFabAdd;
    private String venId, email, status, name, walletBalance;
    private List<VendorRecentTransaction> vendorRecentTransactionList = new ArrayList<VendorRecentTransaction>();
    private LinearLayoutManager mLayoutManager;
    private VendorRecentTransactionAdapter mAdapter;
    private String dataChanged, strStartDate = "", strEndDate = "";
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private AlertDialog dialogSendEmail, dialogPdfDownload;
    private RestrictFutureDatePickerFragment datePickerFragment;
    private TextView dialogFilterEdtFrom;
    private TextView dialogFilterEdtTo;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private Date startDate, enddate, emailStartDate, emailEndDate, pdfStartDate, pdfEndDate;
    private DownloadManager downloadManager;
    private LinearLayout bottomLayout;
    private TextView balanceTextView;
    private ImageView imageView;

    private void hideLoader() {
        if (imageView != null && imageView.getVisibility() == View.VISIBLE) {
            imageView.setVisibility(View.GONE);
        }
    }

    private void showLoader() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (vendorRecentTransactionList != null && vendorRecentTransactionList.size() == 0) {
                                if (imageView.getVisibility() == View.GONE) {
                                    imageView.setVisibility(View.VISIBLE);
                                }
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        venId = bundle.getString(getString(R.string.venid));
        email = bundle.getString(getString(R.string.email));
        name = bundle.getString(getString(R.string.name));
        walletBalance = bundle.getString(getString(R.string.wallet_balance));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_vendor_transaction, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(final View rootView) {
        vendorTransactionSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.vendor_transaction_swipe_refresh_layout);
        vendorTransactionRecyclerView = (RecyclerView) rootView.findViewById(R.id.vendor_transaction_recycler_view);
        vendorTransactionEmptyView = (TextView) rootView.findViewById(R.id.vendor_transaction_empty_view);
        vendorTransactionEmail = (LinearLayout) rootView.findViewById(R.id.vendor_transaction_email);
        vendorTransactionFabEmail = (FloatingActionButton) rootView.findViewById(R.id.vendor_transaction_fab_email);
        vendorTransactionDownload = (LinearLayout) rootView.findViewById(R.id.vendor_transaction_download);
        vendorTransactionFabDownload = (FloatingActionButton) rootView.findViewById(R.id.vendor_transaction_fab_download);
        vendorTransactionFabAdd = (FloatingActionButton) rootView.findViewById(R.id.vendor_transaction_fab_add);
        bottomLayout = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
        balanceTextView = (TextView) rootView.findViewById(R.id.balance_textView);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        //Hide by default
        vendorTransactionFabAdd.setVisibility(View.GONE);

        mAdapter = new VendorRecentTransactionAdapter(getActivity(), vendorRecentTransactionList, venId, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        vendorTransactionRecyclerView.setLayoutManager(mLayoutManager);
        vendorTransactionRecyclerView.setAdapter(mAdapter);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        vendorTransactionFabEmail.setOnClickListener(this);
        vendorTransactionFabDownload.setOnClickListener(this);
        vendorTransactionFabAdd.setOnClickListener(this);

        //Scroll Listener
        vendorTransactionRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && vendorRecentTransactionList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getVendorTransactionInfo(venId, vendorRecentTransactionList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (vendorTransactionSwipeRefreshLayout.isRefreshing()) {
                            vendorTransactionSwipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }

                if (dy > 0 || dy < 0 && vendorTransactionFabAdd.isShown()) {
                    vendorTransactionFabAdd.hide();

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    vendorTransactionFabAdd.show();

                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        double amount = Double.parseDouble(walletBalance);
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        balanceTextView.setText("Total : " + getString(R.string.Rs) + " " + formatter.format(amount));
        if (amount < 0) {
            balanceTextView.setTextColor(getResources().getColor(R.color.black));
        } else {
            balanceTextView.setTextColor(getResources().getColor(R.color.black));
        }

        vendorTransactionSwipeRefreshLayout.setOnRefreshListener(this);
    }

    public void doRefresh() {
        if (vendorRecentTransactionList != null && vendorRecentTransactionList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLoader();
                        getVendorTransactionInfo(venId, getString(R.string.last_updated_date), "1");

                    }
                }, 200);
            } else {
                vendorTransactionRecyclerView.setVisibility(View.GONE);
                bottomLayout.setVisibility(View.GONE);
                vendorTransactionEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == vendorTransactionFabEmail) {
            // Handle clicks for vendorTransactionFabEmail
            isFabOpen = true;
            animateFAB();
            sendEmailDialog();
        } else if (v == vendorTransactionFabDownload) {
            // Handle clicks for vendorTransactionFabDownload
            isFabOpen = true;
            animateFAB();
            downloadPdfDailog();
        } else if (v == vendorTransactionFabAdd) {
            // Handle clicks for vendorTransactionFabAdd
            animateFAB();
        }
    }

    private void getVendorTransactionInfo(String venId, final String time, final String traversalValue) {
        task = getString(R.string.vendor_recent_transaction);
        userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
        accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
        ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call call = apiService.getVendorRecentTransaction(version, key, task, userId, accessToken, venId, time,
                traversalValue);
        Log.v("Request", String.valueOf(call));
        Log.v("url", String.valueOf(call.request().url()));

        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                ApiResponse apiResponse = (ApiResponse) response.body();
                if (apiResponse.getSuccess()) {
                    for (final VendorRecentTransaction vendorRecentTransaction : apiResponse.getData().getVendorRecentTransaction()) {
                        if (apiResponse.getData().getVendorRecentTransaction() != null) {
                            if (traversalValue.equals("2")) {
                                if (!time.equals(vendorRecentTransaction.getCreatedTs())) {
                                    vendorRecentTransactionList.add(vendorRecentTransaction);
                                }
                                dataChanged = "yes";
                            } else if (traversalValue.equals("1")) {
                                if (vendorTransactionSwipeRefreshLayout != null && vendorTransactionSwipeRefreshLayout.isRefreshing()) {
                                    // To remove duplicacy of a new item
                                    if (!time.equals(vendorRecentTransaction.getCreatedTs())) {
                                        vendorRecentTransactionList.add(0, vendorRecentTransaction);
                                    }
                                } else {
                                    if (!time.equals(vendorRecentTransaction.getCreatedTs())) {
                                        vendorRecentTransactionList.add(vendorRecentTransaction);
                                    }
                                }
                                dataChanged = "yes";
                            }
                        }
                    }
                }  //Deleted User
                else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                        apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                    //Logout
                    Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                }
                loading = false;
                if (vendorRecentTransactionList != null && vendorRecentTransactionList.size() == 0) {
                    vendorTransactionEmptyView.setVisibility(View.VISIBLE);
                    vendorTransactionEmptyView.setText(getString(R.string.no_data_available));
                    vendorTransactionRecyclerView.setVisibility(View.GONE);
                    vendorTransactionFabAdd.setVisibility(View.GONE);
                    bottomLayout.setVisibility(View.GONE);
                } else {
                    vendorTransactionEmptyView.setVisibility(View.GONE);
                    vendorTransactionRecyclerView.setVisibility(View.VISIBLE);
                    vendorTransactionFabAdd.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                }
                if (traversalValue.equals("2")) {
                    mAdapter.notifyDataSetChanged();
                    if (dataChanged != null && dataChanged.equals("yes")) {
                        // vendorTransactionRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                    }
                } else if (traversalValue.equals("1")) {
                    if (dataChanged != null && dataChanged.equals("yes")) {
                        mAdapter.notifyDataSetChanged();
                        vendorTransactionRecyclerView.smoothScrollToPosition(0);
                    }
                }
                mAdapter.notifyDataSetChanged();
                if (vendorTransactionSwipeRefreshLayout != null && vendorTransactionSwipeRefreshLayout.isRefreshing()) {
                    vendorTransactionSwipeRefreshLayout.setRefreshing(false);
                }
                //Hide loader
                hideLoader();
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                if (vendorTransactionSwipeRefreshLayout != null && vendorTransactionSwipeRefreshLayout.isRefreshing()) {
                    vendorTransactionSwipeRefreshLayout.setRefreshing(false);
                }
                //Hide loader
                hideLoader();
            }

        });

    }

    @Override
    public void onMessageRowClicked(int position) {

    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (vendorRecentTransactionList != null && vendorRecentTransactionList.size() > 0) {
                getVendorTransactionInfo(venId, vendorRecentTransactionList.get(0).getCreatedTs(), "1");
                vendorTransactionRecyclerView.setVisibility(View.VISIBLE);
                vendorTransactionEmptyView.setVisibility(View.GONE);
                vendorTransactionSwipeRefreshLayout.setRefreshing(true);
                vendorTransactionFabAdd.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                showLoader();
                getVendorTransactionInfo(venId, getString(R.string.last_updated_date), "1");
            }
        } else {
            vendorTransactionRecyclerView.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            vendorTransactionEmptyView.setVisibility(View.VISIBLE);
            vendorTransactionFabAdd.setVisibility(View.GONE);
            vendorTransactionEmptyView.setText(getString(R.string.no_internet_try_later));
            if (vendorTransactionSwipeRefreshLayout != null && vendorTransactionSwipeRefreshLayout.isRefreshing()) {
                vendorTransactionSwipeRefreshLayout.setRefreshing(false);
            }

        }

    }

    public void animateFAB() {
        if (isFabOpen) {
            vendorTransactionFabAdd.startAnimation(rotate_backward);
            vendorTransactionFabDownload.startAnimation(fab_close);
            vendorTransactionFabEmail.startAnimation(fab_close);

            vendorTransactionDownload.startAnimation(fab_close);
            vendorTransactionEmail.startAnimation(fab_close);

            vendorTransactionFabDownload.setClickable(false);
            vendorTransactionFabEmail.setClickable(false);
            vendorTransactionEmail.setVisibility(View.GONE);
            vendorTransactionDownload.setVisibility(View.GONE);
            isFabOpen = false;
        } else {
            vendorTransactionFabAdd.startAnimation(rotate_forward);
            vendorTransactionFabDownload.startAnimation(fab_open);
            vendorTransactionFabEmail.startAnimation(fab_open);

            vendorTransactionDownload.startAnimation(fab_open);
            vendorTransactionEmail.startAnimation(fab_open);

            vendorTransactionEmail.setVisibility(View.VISIBLE);
            vendorTransactionDownload.setVisibility(View.VISIBLE);
            vendorTransactionFabDownload.setClickable(true);
            vendorTransactionFabEmail.setClickable(true);
            isFabOpen = true;
        }
    }

    private void sendEmailDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_send_email, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogSendEmail = builder.create();
        dialogSendEmail.setCanceledOnTouchOutside(true);

        //DatePicker
        datePickerFragment = new RestrictFutureDatePickerFragment();
        datePickerFragment.setListener(this);
        final EditText edittextSendEmail;
        final Button btnSendEmail;
        final Button btnCancel;
        ProgressBar dialogProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);
        edittextSendEmail = (EditText) dialogView.findViewById(R.id.edittext_send_email);
        btnSendEmail = (Button) dialogView.findViewById(R.id.btn_send_email);
        dialogFilterEdtFrom = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_from);
        dialogFilterEdtTo = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_to);

        if (email != null && !email.isEmpty()) {
            edittextSendEmail.setText(email);
        }

        if (emailStartDate != null) {
            dialogFilterEdtFrom.setText(formatter.format(emailStartDate));
        } else {
            try {
                Date strDate = formatter.parse("01-04-" + Calendar.getInstance().get(Calendar.YEAR));
                if (new Date().after(strDate)) {
                    if (Calendar.getInstance().get(Calendar.YEAR) > strDate.getYear()) {
                        strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR);
                    } else if (Calendar.getInstance().get(Calendar.YEAR) == strDate.getYear()) {
                        strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR + 1);
                    }
                } else {
                    strStartDate = "01-04-" + (Calendar.getInstance().get(Calendar.YEAR) - 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dialogFilterEdtFrom.setText(strStartDate);
        }
        if (emailEndDate != null) {
            dialogFilterEdtTo.setText(formatter.format(emailEndDate));
        } else {
            try {
                Date strDate = formatter.parse("31-03-" + Calendar.getInstance().get(Calendar.YEAR) + 1);
                if (new Date().after(strDate)) {
                    if (Calendar.getInstance().get(Calendar.YEAR) > strDate.getYear()) {
                        strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR));
                    } else if (Calendar.getInstance().get(Calendar.YEAR) == strDate.getYear()) {
                        strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
                    }
                } else {
                    strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dialogFilterEdtTo.setText(strEndDate);
        }

        dialogView.findViewById(R.id.dialog_send_email_edt_from).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_from));
            }
        });

        dialogView.findViewById(R.id.dialog_send_email_edt_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_to));
            }
        });
        btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    btnSendEmail.setEnabled(false);
                    dialogProgressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            btnSendEmail.setEnabled(true);
                        }
                    }, 3000);
                    EmailValidator emailValidator = new EmailValidator();
                    boolean valid = emailValidator.validateEmail(edittextSendEmail.getText().toString().trim());
                    if (valid) {
                        if (edittextSendEmail.getText().toString().trim() != null &&
                                !edittextSendEmail.getText().toString().trim().isEmpty() &&
                                dialogFilterEdtFrom.getText().toString() != null && !dialogFilterEdtFrom.getText().toString().isEmpty() &&
                                dialogFilterEdtTo.getText().toString() != null && !dialogFilterEdtTo.getText().toString().isEmpty()) {
                            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                                //Update Dates for Email Dialog
                                emailStartDate = startDate;
                                emailEndDate = enddate;

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                                String fromDate = format.format(dateFormat.parse(dialogFilterEdtFrom.getText().toString()));
                                String toDate = format.format(dateFormat.parse(dialogFilterEdtTo.getText().toString()));
                                sendEmail(fromDate, toDate,
                                        edittextSendEmail.getText().toString().trim(), dialogProgressBar);
                            } else {
                                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                            }
                        } else if (dialogFilterEdtFrom.getText().toString() == null || dialogFilterEdtFrom.getText().toString().isEmpty() ||
                                dialogFilterEdtTo.getText().toString() == null || dialogFilterEdtTo.getText().toString().isEmpty()) {
                            Toast.makeText(getActivity(), R.string.enter_date, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), R.string.enter_email, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        btnSendEmail.setEnabled(false);
                        Toast.makeText(getActivity(), getString(R.string.valid_email_error), Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                btnSendEmail.setEnabled(true);
                            }
                        }, 3000);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSendEmail.dismiss();
            }
        });
        dialogSendEmail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSendEmail.show();
    }

    @Override
    public void passDate(String s) {
        String stringStartDate = null, stringEndDate = null;
        if (datePickerFragment.getTag().equals(getString(R.string.dailogue_from))) {
            stringStartDate = s;
            try {
                startDate = formatter.parse(stringStartDate);
                System.out.println(startDate);
                String from = formatter.format(startDate);
                dialogFilterEdtFrom.setText(from);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            try {
                stringEndDate = s;
                enddate = formatter.parse(stringEndDate);
                String to = formatter.format(enddate);
                dialogFilterEdtTo.setText(to);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void passTime(String s) {

    }

    private void sendEmail(final String fromDate, final String toDate, final String email,
                           ProgressBar progressBar) {
        task = getString(R.string.vendor_wallet_send_email);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.vendorWalletSendEmail(version, key, task, userId, accessToken, venId, email, "1", fromDate, toDate);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        Toast.makeText(getActivity(), getString(R.string.email_link_success), Toast.LENGTH_LONG).show();
                        dialogSendEmail.dismiss();
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        if (isAdded() && getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (getActivity() != null && isAdded()) {
                        if (dialogSendEmail != null && dialogSendEmail.isShowing() &&
                                progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (getActivity() != null && isAdded()) {
                        if (dialogSendEmail != null && dialogSendEmail.isShowing() &&
                                progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                    dialogSendEmail.dismiss();
                }
            }
        });
    }

    private void downloadPdfDailog() {
        final View dialogView = View.inflate(getActivity(), R.layout.dialog_download, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(true);
        dialogPdfDownload = builder.create();
        dialogPdfDownload.setCanceledOnTouchOutside(true);
        //DatePicker
        datePickerFragment = new RestrictFutureDatePickerFragment();
        datePickerFragment.setListener(this);
        TextView btnDownload;
        TextView btnCancel;
        ProgressBar dialogProgressBar;
        btnDownload = (TextView) dialogView.findViewById(R.id.btn_send_email);
        dialogProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);
        dialogFilterEdtFrom = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_from);
        dialogFilterEdtTo = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_to);
        if (pdfStartDate != null) {
            dialogFilterEdtFrom.setText(formatter.format(pdfStartDate));
        } else {
            try {
                Date strDate = formatter.parse("01-04-" + Calendar.getInstance().get(Calendar.YEAR));
                if (new Date().after(strDate)) {
                    if (Calendar.getInstance().get(Calendar.YEAR) > strDate.getYear()) {
                        strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR);
                    } else if (Calendar.getInstance().get(Calendar.YEAR) == strDate.getYear()) {
                        strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR + 1);
                    }
                } else {
                    strStartDate = "01-04-" + (Calendar.getInstance().get(Calendar.YEAR) - 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dialogFilterEdtFrom.setText(strStartDate);
        }
        if (pdfEndDate != null) {
            dialogFilterEdtTo.setText(formatter.format(pdfEndDate));
        } else {
            try {
                Date strDate = formatter.parse("31-03-" + Calendar.getInstance().get(Calendar.YEAR) + 1);
                if (new Date().after(strDate)) {
                    if (Calendar.getInstance().get(Calendar.YEAR) > strDate.getYear()) {
                        strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR));
                    } else if (Calendar.getInstance().get(Calendar.YEAR) == strDate.getYear()) {
                        strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
                    }
                } else {
                    strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dialogFilterEdtTo.setText(strEndDate);
        }


        dialogView.findViewById(R.id.dialog_send_email_edt_from).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_from));

            }
        });

        dialogView.findViewById(R.id.dialog_send_email_edt_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_to));
            }
        });
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {

                        dialogProgressBar.setVisibility(View.VISIBLE);
                        //Update Dates for PDF Dialog
                        pdfStartDate = startDate;
                        pdfEndDate = enddate;

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                        String fromDate = format.format(dateFormat.parse(dialogFilterEdtFrom.getText().toString()));
                        String toDate = format.format(dateFormat.parse(dialogFilterEdtTo.getText().toString()));
                        downloadPdf(fromDate, toDate, dialogPdfDownload);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPdfDownload.dismiss();
            }
        });

        dialogPdfDownload.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogPdfDownload.show();
    }

    private void downloadPdf(final String fromDate, final String toDate, final AlertDialog alertDialog) {
        task = getString(R.string.vendor_wallet_send_email);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadVendorTransactionPDF(version, key, task, userId, accessToken, venId,
                fromDate, toDate);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                // leadList.clear();
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        // Toast.makeText(getActivity(), getString(R.string.email_link_success), Toast.LENGTH_LONG).show();
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle("ledger_statement_" + name + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            "ledger_statement_" + name + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        if (isAdded() && getActivity() != null) {
                            Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

}

