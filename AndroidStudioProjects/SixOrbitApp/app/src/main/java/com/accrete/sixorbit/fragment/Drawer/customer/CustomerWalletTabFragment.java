package com.accrete.sixorbit.fragment.Drawer.customer;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.accrete.sixorbit.adapter.CustomerWalletAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerWallet;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.RestrictFutureDatePickerFragment;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.io.File;
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
 * Created by poonam on 31/10/17.
 */

public class CustomerWalletTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, PassDateToCounsellor {
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private DownloadManager downloadManager;
    private SwipeRefreshLayout customerWalletSwipeRefreshLayout;
    private RecyclerView customerWalletRecyclerView;
    private TextView customerWalletEmptyView;
    private LinearLayout customerWalletEmail;
    private FloatingActionButton customerWalletFabEmail;
    private LinearLayout customerWalletDownload;
    private FloatingActionButton customerWalletFabDownload;
    private FloatingActionButton customerWalletFabAdd;
    private CustomerWalletAdapter customerWalletAdapter;
    private List<CustomerWallet> customerWalletList = new ArrayList<>();
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private Boolean isFabOpen = false;
    private AlertDialog dialogSendEmail, dialogPdfDownload;
    private String status, dataChanged, walletBalance;
    private String cuid, strStartDate = "", strEndDate = "";
    private String email, name;
    private RestrictFutureDatePickerFragment datePickerFragment;
    private TextView dialogFilterEdtFrom;
    private TextView dialogFilterEdtTo;
    private Date startDate, enddate, emailStartDate, emailEndDate, pdfStartDate, pdfEndDate;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout bottomLayout;
    private TextView balanceTextView;
    private ImageView imageView;

    public void getEmailAddress(String emailAddress) {
        email = emailAddress;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        cuid = bundle.getString(getString(R.string.cuid));
        email = bundle.getString(getString(R.string.customer_email_id));
        name = bundle.getString(getString(R.string.name));
        walletBalance = bundle.getString(getString(R.string.wallet_balance));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_wallet, container, false);
        initalizeView(rootView);
        return rootView;
    }

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
                            if (customerWalletList != null && customerWalletList.size() == 0) {
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

    private void initalizeView(final View rootView) {
        try {
            customerWalletSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.customer_wallet_swipe_refresh_layout);
            customerWalletRecyclerView = (RecyclerView) rootView.findViewById(R.id.customer_wallet_recycler_view);
            customerWalletEmptyView = (TextView) rootView.findViewById(R.id.customer_wallet_empty_view);
            customerWalletEmail = (LinearLayout) rootView.findViewById(R.id.customer_wallet_email);
            customerWalletFabEmail = (FloatingActionButton) rootView.findViewById(R.id.customer_wallet_fab_email);
            customerWalletDownload = (LinearLayout) rootView.findViewById(R.id.customer_wallet_download);
            customerWalletFabDownload = (FloatingActionButton) rootView.findViewById(R.id.customer_wallet_fab_download);
            customerWalletFabAdd = (FloatingActionButton) rootView.findViewById(R.id.customer_wallet_fab_add);
            bottomLayout = (LinearLayout) rootView.findViewById(R.id.bottom_layout);
            balanceTextView = (TextView) rootView.findViewById(R.id.balance_textView);
            imageView = (ImageView) rootView.findViewById(R.id.imageView);

            fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
            fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
            rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
            rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

            //Hide by default
            customerWalletFabAdd.setVisibility(View.GONE);

            customerWalletFabEmail.setOnClickListener(this);
            customerWalletFabDownload.setOnClickListener(this);
            customerWalletFabAdd.setOnClickListener(this);

            customerWalletAdapter = new CustomerWalletAdapter(getActivity(), customerWalletList, cuid);
            mLayoutManager = new LinearLayoutManager(getActivity());
            customerWalletRecyclerView.setLayoutManager(mLayoutManager);
            customerWalletRecyclerView.setAdapter(customerWalletAdapter);

            //Scroll Listener
            customerWalletRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = mLayoutManager.getItemCount();
                    lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && customerWalletList.size() > 0) {
                        // End has been reached
                        // Do something
                        loading = true;
                        //calling API
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            getWalletRecentTransaction(customerWalletList.get(totalItemCount - 1).getCreatedTs(), "2");
                        } else {
                            if (customerWalletSwipeRefreshLayout.isRefreshing()) {
                                customerWalletSwipeRefreshLayout.setRefreshing(false);
                            }
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (dy > 0 || dy < 0 && customerWalletFabAdd.isShown()) {
                        customerWalletFabAdd.hide();
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        customerWalletFabAdd.show();
                    }
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });

            customerWalletSwipeRefreshLayout.setOnRefreshListener(this);

            double amount = Constants.ParseDouble(walletBalance);
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

            balanceTextView.setText("Total : " + getString(R.string.Rs) + " " + formatter.format(amount));
            if (amount < 0) {
                balanceTextView.setTextColor(getResources().getColor(R.color.black));
            } else {
                balanceTextView.setTextColor(getResources().getColor(R.color.black));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doRefresh() {
        if (customerWalletList != null && customerWalletList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showLoader();
                        getWalletRecentTransaction(getString(R.string.last_updated_date), "1");
                    }
                }, 200);
            } else {
                customerWalletRecyclerView.setVisibility(View.GONE);
                customerWalletEmptyView.setVisibility(View.VISIBLE);
                bottomLayout.setVisibility(View.GONE);
                customerWalletFabAdd.setVisibility(View.GONE);
                customerWalletEmptyView.setText(getString(R.string.no_internet_try_later));
            }
        }
    }

    private void getWalletRecentTransaction(final String time, final String traversalValue) {
        task = getString(R.string.wallet_recent_transaction);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getWalletTransaction(version, key, task, userId, accessToken, cuid, time,
                traversalValue);
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
                        for (final CustomerWallet customerWallet : apiResponse.getData().getCustomerRecentTransaction()) {
                            if (apiResponse.getData().getCustomerRecentTransaction() != null) {
                                if (traversalValue.equals("2")) {
                                    if (!time.equals(customerWallet.getCreatedTs())) {
                                        customerWalletList.add(customerWallet);
                                    }
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (customerWalletSwipeRefreshLayout != null && customerWalletSwipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(customerWallet.getCreatedTs())) {
                                            customerWalletList.add(0, customerWallet);
                                        }
                                    } else {
                                        if (!time.equals(customerWallet.getCreatedTs())) {
                                            customerWalletList.add(customerWallet);
                                        }
                                    }
                                    dataChanged = "yes";
                                }
                            }
                        }
                        loading = false;
                        if (customerWalletList != null && customerWalletList.size() == 0) {
                            customerWalletEmptyView.setVisibility(View.VISIBLE);
                            customerWalletFabAdd.setVisibility(View.GONE);
                            customerWalletRecyclerView.setVisibility(View.GONE);
                            bottomLayout.setVisibility(View.GONE);
                        } else {
                            customerWalletEmptyView.setVisibility(View.GONE);
                            customerWalletFabAdd.setVisibility(View.VISIBLE);
                            customerWalletRecyclerView.setVisibility(View.VISIBLE);
                            bottomLayout.setVisibility(View.VISIBLE);
                        }
                        if (customerWalletSwipeRefreshLayout != null && customerWalletSwipeRefreshLayout.isRefreshing()) {
                            customerWalletSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            customerWalletAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // customerWalletRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                customerWalletAdapter.notifyDataSetChanged();
                                customerWalletRecyclerView.smoothScrollToPosition(0);
                            }
                        }
                    } else {
                        loading = false;
                        if (customerWalletList != null && customerWalletList.size() == 0) {
                            customerWalletEmptyView.setVisibility(View.VISIBLE);
                            customerWalletFabAdd.setVisibility(View.GONE);
                            customerWalletRecyclerView.setVisibility(View.GONE);
                            bottomLayout.setVisibility(View.GONE);
                        } else {
                            customerWalletEmptyView.setVisibility(View.GONE);
                            bottomLayout.setVisibility(View.VISIBLE);
                            customerWalletFabAdd.setVisibility(View.VISIBLE);
                            customerWalletRecyclerView.setVisibility(View.VISIBLE);
                        }
                        if (customerWalletSwipeRefreshLayout != null && customerWalletSwipeRefreshLayout.isRefreshing()) {
                            customerWalletSwipeRefreshLayout.setRefreshing(false);
                        }
                        if (traversalValue.equals("2")) {
                            customerWalletAdapter.notifyDataSetChanged();
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                // customerWalletRecyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                            }
                        } else if (traversalValue.equals("1")) {
                            if (dataChanged != null && dataChanged.equals("yes")) {
                                customerWalletAdapter.notifyDataSetChanged();
                                customerWalletRecyclerView.smoothScrollToPosition(0);
                            }
                        }

                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                        } else {
                            //   Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    hideLoader();

                } catch (Exception e) {
                    e.printStackTrace();
                    if (customerWalletSwipeRefreshLayout != null && customerWalletSwipeRefreshLayout.isRefreshing()) {
                        customerWalletSwipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    Log.d("errorInWallet", t.getMessage() + "");
                    customerWalletSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });

    }

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (customerWalletList != null && customerWalletList.size() > 0) {
                getWalletRecentTransaction(customerWalletList.get(0).getCreatedTs(), "1");
                customerWalletRecyclerView.setVisibility(View.VISIBLE);
                customerWalletEmptyView.setVisibility(View.GONE);
                customerWalletFabAdd.setVisibility(View.VISIBLE);
                customerWalletSwipeRefreshLayout.setRefreshing(true);
                bottomLayout.setVisibility(View.VISIBLE);
            } else {
                showLoader();
                getWalletRecentTransaction(getString(R.string.last_updated_date), "1");
            }
        } else {
            customerWalletRecyclerView.setVisibility(View.GONE);
            customerWalletEmptyView.setVisibility(View.VISIBLE);
            customerWalletFabAdd.setVisibility(View.GONE);
            bottomLayout.setVisibility(View.GONE);
            customerWalletEmptyView.setText(getString(R.string.no_internet_try_later));
            customerWalletSwipeRefreshLayout.setRefreshing(false);

        }
    }

    @Override
    public void onClick(View v) {
        if (v == customerWalletFabEmail) {
            // Handle clicks for customerWalletFabEmail
            isFabOpen = true;
            animateFAB();
            sendEmailDialog();
        } else if (v == customerWalletFabDownload) {
            // Handle clicks for customerWalletFabDownload
            isFabOpen = true;
            animateFAB();
            downloadPdfDailog();
        } else if (v == customerWalletFabAdd) {
            // Handle clicks for customerWalletFabAdd
            animateFAB();
        }
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
        final EditText edittextSendEmail;
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
                        //Update Dates for PDF Dialog
                        pdfStartDate = startDate;
                        pdfEndDate = enddate;

                        dialogProgressBar.setVisibility(View.VISIBLE);

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
        task = getString(R.string.customer_wallet_send_email);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadCustomerWalletPDF(version, key, task, userId, accessToken, cuid, fromDate, toDate);
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
                            Log.i("Download1", String.valueOf(request));
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                        //showPdf();

                        /*startActivity(new Intent(Intent.ACTION_VIEW, ));*/
                        // Toast.makeText(getActivity(), getString(R.string.download_pdf_success), Toast.LENGTH_LONG).show();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Log.d("errorInWallet", t.getMessage() + "");
                }
            }
        });
    }

    public void showPdf() {
        File file = new File(Environment.getExternalStorageDirectory() + "/pdf/Read.pdf");
        PackageManager packageManager = getActivity().getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        startActivity(intent);
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
        Button btnCancel;
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

    private void sendEmail(final String fromDate, final String toDate, final String email,
                           ProgressBar progressBar) {
        task = getString(R.string.customer_wallet_send_email);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.walletOrderSendEmail(version, key, task, userId,
                accessToken, cuid, email, "1", fromDate, toDate);
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
                        Toast.makeText(getActivity(), getString(R.string.email_link_success), Toast.LENGTH_LONG).show();
                        dialogSendEmail.dismiss();

                    }
                    //Deleted User
                    else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
                    } else {
                        Toast.makeText(getActivity(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                    Log.d("errorInWallet", t.getMessage() + "");
                }
            }
        });
    }

    public void animateFAB() {
        if (isFabOpen) {
            customerWalletFabAdd.startAnimation(rotate_backward);
            customerWalletFabDownload.startAnimation(fab_close);
            customerWalletFabEmail.startAnimation(fab_close);

            customerWalletDownload.startAnimation(fab_close);
            customerWalletEmail.startAnimation(fab_close);

            customerWalletFabDownload.setClickable(false);
            customerWalletFabEmail.setClickable(false);
            customerWalletEmail.setVisibility(View.GONE);
            customerWalletDownload.setVisibility(View.GONE);
            isFabOpen = false;
            Log.d("Lead", "close");
        } else {
            customerWalletFabAdd.startAnimation(rotate_forward);
            customerWalletFabDownload.startAnimation(fab_open);
            customerWalletFabEmail.startAnimation(fab_open);

            customerWalletDownload.startAnimation(fab_open);
            customerWalletEmail.startAnimation(fab_open);

            customerWalletEmail.setVisibility(View.VISIBLE);
            customerWalletDownload.setVisibility(View.VISIBLE);
            customerWalletFabDownload.setClickable(true);
            customerWalletFabEmail.setClickable(true);
            isFabOpen = true;
            Log.d("Lead", "open");
        }
    }


}
