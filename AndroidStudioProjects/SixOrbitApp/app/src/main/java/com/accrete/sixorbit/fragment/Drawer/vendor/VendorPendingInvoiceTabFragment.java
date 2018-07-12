package com.accrete.sixorbit.fragment.Drawer.vendor;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.adapter.VendorPendingInvoiceAdapter;
import com.accrete.sixorbit.fragment.Drawer.customer.CustomerPendingInvoiceTabFragment;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.CustomisedToast;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.VendorPendingInvoice;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.RestrictFutureDatePickerFragment;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.text.DateFormat;
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

public class VendorPendingInvoiceTabFragment extends Fragment implements
        VendorPendingInvoiceAdapter.PendingInvoiceAdapterListener, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, PassDateToCounsellor {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SpannableString fromSpannable, fromDateSpannable, toSpannable, toDateSpannable;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView customerPendingInvoiceEmptyView;
    private List<VendorPendingInvoice> vendorPendingInvoiceList = new ArrayList<VendorPendingInvoice>();
    private List<VendorPendingInvoice> tempList = new ArrayList<VendorPendingInvoice>();
    private VendorPendingInvoiceAdapter mAdapter;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton filterFab;
    private RelativeLayout vendorPendingInvoiceFilterLayout;
    private RelativeLayout vendorPendingInvoiceEmailLayout;
    private FloatingActionButton vendorPendingInvoiceFabEmail;
    private RelativeLayout vendorPendingInvoiceDownloadLayout;
    private FloatingActionButton vendorPendingInvoiceFabDownload;
    private FloatingActionButton vendorPendingInvoiceFabAdd;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private View dialogView;
    private AlertDialog alertDialog;
    private RestrictFutureDatePickerFragment datePickerFragment;
    private TextView dialogFilterEdtFrom;
    private TextView dialogFilterEdtTo;
    private Date startDate, enddate, filterStartDate, filterEndDate, emailStartDate, emailEndDate, pdfStartDate, pdfEndDate;
    private String dataChanged, dataRefreshed, venId, strStartDate = "", strEndDate = "", name, email;
    private AlertDialog dialogSendEmail, dialogPdfDownload;
    private DownloadManager downloadManager;
    private boolean isFiltered = false;
    private RelativeLayout topLayout;
    private TextView filterTextView;
    private TextView fromTextView;
    private TextView toTextView;
    private ImageButton closeImageButton;
    private ImageView imageView;

    public static CustomerPendingInvoiceTabFragment newInstance() {
        return new CustomerPendingInvoiceTabFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        venId = bundle.getString(getString(R.string.venid));
        email = bundle.getString(getString(R.string.email));
        name = bundle.getString(getString(R.string.name));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_customer_pending_invoice, container, false);
        initializeView(rootView);
        return rootView;
    }

    private void initializeView(final View rootView) {
        topLayout = (RelativeLayout) rootView.findViewById(R.id.top_layout);
        filterTextView = (TextView) rootView.findViewById(R.id.filter_textView);
        fromTextView = (TextView) rootView.findViewById(R.id.from_textView);
        toTextView = (TextView) rootView.findViewById(R.id.to_textView);
        closeImageButton = (ImageButton) rootView.findViewById(R.id.clear_customerInfo_imageButton);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.customer_pending_invoice_swipe_refresh_layout);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.customer_pending_invoice_recycler_view);
        customerPendingInvoiceEmptyView = (TextView) rootView.findViewById(R.id.customer_pending_invoice_empty_view);
        customerPendingInvoiceEmptyView.setText("There is no pending invoice");
        filterFab = (FloatingActionButton) rootView.findViewById(R.id.filter_fab);
        vendorPendingInvoiceFilterLayout = (RelativeLayout) rootView.findViewById(R.id.vendor_pending_invoice_filter_layout);
        vendorPendingInvoiceEmailLayout = (RelativeLayout) rootView.findViewById(R.id.vendor_pending_invoice_email_layout);
        vendorPendingInvoiceFabEmail = (FloatingActionButton) rootView.findViewById(R.id.vendor_pending_invoice_fab_email);
        vendorPendingInvoiceDownloadLayout = (RelativeLayout) rootView.findViewById(R.id.vendor_pending_invoice_download_layout);
        vendorPendingInvoiceFabDownload = (FloatingActionButton) rootView.findViewById(R.id.vendor_pending_invoice_fab_download);
        vendorPendingInvoiceFabAdd = (FloatingActionButton) rootView.findViewById(R.id.vendor_pending_invoice_fab_add);
        imageView = (ImageView) rootView.findViewById(R.id.imageView);

        //Hide by default
        vendorPendingInvoiceFabAdd.setVisibility(View.GONE);

        mAdapter = new VendorPendingInvoiceAdapter(getActivity(), vendorPendingInvoiceList, venId, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mAdapter);

        //Animations
        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && vendorPendingInvoiceList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getPendingInvoice(vendorPendingInvoiceList.get(totalItemCount - 1).getDate(), "2",
                                strStartDate, strEndDate);
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
                if (dy > 0 || dy < 0 && vendorPendingInvoiceFabAdd.isShown()) {
                    vendorPendingInvoiceFabAdd.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    vendorPendingInvoiceFabAdd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        //doRefresh();
        swipeRefreshLayout.setOnRefreshListener(this);
        filterFab.setOnClickListener(this);
        vendorPendingInvoiceFabEmail.setOnClickListener(this);
        vendorPendingInvoiceFabDownload.setOnClickListener(this);
        vendorPendingInvoiceFabAdd.setOnClickListener(this);
        closeImageButton.setOnClickListener(this);
        recyclerView.setNestedScrollingEnabled(false);
        topLayout.setVisibility(View.GONE);
    }

    private void getPendingInvoice(final String time, final String traversalValue, final String strStartDate,
                                   final String strEndDate) {
        task = getString(R.string.vender_pending_invoice_task);

        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getVendorsPendingInvoice(version, key, task, userId, accessToken, venId,
                time, traversalValue, strStartDate, strEndDate);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        for (VendorPendingInvoice vendorPendingInvoice : apiResponse.getData().getVendorPendingInvoice()) {
                            if (apiResponse.getData().getVendorPendingInvoice() != null) {
                                if (traversalValue.equals("2")) {
                                    vendorPendingInvoiceList.add(vendorPendingInvoice);
                                    dataChanged = "yes";
                                } else if (traversalValue.equals("1")) {
                                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                        // To remove duplicacy of a new item
                                        if (!time.equals(vendorPendingInvoice.getDate())) {
                                            vendorPendingInvoiceList.add(0, vendorPendingInvoice);
                                        }
                                    } else {
                                        vendorPendingInvoiceList.add(vendorPendingInvoice);
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
                    }

                    if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() == 0) {
                        customerPendingInvoiceEmptyView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        vendorPendingInvoiceFabAdd.setVisibility(View.GONE);
                    } else {
                        customerPendingInvoiceEmptyView.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        vendorPendingInvoiceFabAdd.setVisibility(View.VISIBLE);
                    }

                    //Check Filtered Or Not
                    if (isFiltered || (dataChanged != null && dataChanged.equals("yes"))) {
                        vendorPendingInvoiceFabAdd.setVisibility(View.VISIBLE);
                        if (isFiltered) {
                            topLayout.setVisibility(View.VISIBLE);
                            //From
                            fromSpannable = new SpannableString("From : ");
                            fromSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, 6, 0); // set size
                            fromTextView.setText(fromSpannable);

                            fromDateSpannable = new SpannableString(dialogFilterEdtFrom.getText().toString().trim());
                            fromTextView.append(fromDateSpannable);

                            //To
                            toSpannable = new SpannableString("To : ");
                            toSpannable.setSpan(new RelativeSizeSpan(1.2f), 0, 4, 0); // set size
                            toTextView.setText(toSpannable);

                            toDateSpannable = new SpannableString(dialogFilterEdtTo.getText().toString().trim());
                            toTextView.append(toDateSpannable);
                        } else {
                            topLayout.setVisibility(View.GONE);
                        }
                    } else {
                        vendorPendingInvoiceFabAdd.setVisibility(View.GONE);
                        topLayout.setVisibility(View.GONE);
                    }

                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    if (traversalValue.equals("2")) {
                        mAdapter.notifyDataSetChanged();
                        if (dataChanged != null && dataChanged.equals("yes")) {
                            // recyclerView.smoothScrollToPosition(mAdapter.getItemCount() + 1);
                        }
                    } else if (traversalValue.equals("1")) {
                        if (dataChanged != null && dataChanged.equals("yes")) {
                            mAdapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
                //Hide loader
                hideLoader();
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                if (getActivity() != null && isAdded()) {
                    Toast.makeText(getActivity(), getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    //Hide loader
                    hideLoader();
                }
            }
        });
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
                            if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() == 0) {
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
    public void onMessageRowClicked(int position) {
    }

    @Override
    public void updateList(List<VendorPendingInvoice> customerPendingInvoices) {
        if (customerPendingInvoices != null) {
            tempList.clear();
            tempList.addAll(customerPendingInvoices);
            mAdapter = new VendorPendingInvoiceAdapter(getActivity(), tempList, venId, this);
            recyclerView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            if (tempList != null && tempList.size() == 0) {
                customerPendingInvoiceEmptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                vendorPendingInvoiceFabAdd.setVisibility(View.GONE);
            } else {
                customerPendingInvoiceEmptyView.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                vendorPendingInvoiceFabAdd.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateDateOriginalList() {
        tempList.clear();
        mAdapter = new VendorPendingInvoiceAdapter(getActivity(), vendorPendingInvoiceList, venId, this);
        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() == 0) {
            customerPendingInvoiceEmptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            vendorPendingInvoiceFabAdd.setVisibility(View.GONE);
        } else {
            customerPendingInvoiceEmptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            vendorPendingInvoiceFabAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        loading = true;
        if (!isFiltered) {
            strEndDate = "";
            strStartDate = "";
        }
        //calling API
        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setRefreshing(true);
            }
            if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() > 0) {
                getPendingInvoice(vendorPendingInvoiceList.get(0).getDate(), "1", strStartDate, strEndDate);
            } else {
                showLoader();
                getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
            }
        } else {
            if (swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            //CustomisedToast.error(getActivity(), "Please check internet connection.").show();
        }
    }

    public void doRefresh() {
        if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() == 0) {
            loading = true;
            if (!isFiltered) {
                strEndDate = "";
                strStartDate = "";
            }
            //calling API
            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                dataRefreshed = "yes";
                showLoader();
                if (vendorPendingInvoiceList != null && vendorPendingInvoiceList.size() > 0) {
                    vendorPendingInvoiceList.clear();
                    getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
                } else {
                    getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
                }
            } else {
                //CustomisedToast.error(getActivity(), "Please check internet connection.").show();
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void passDate(String s) {
        String stringStartDate = null, stringEndDate = null;
        if (datePickerFragment.getTag().equals(getString(R.string.dailogue_from))) {
            stringStartDate = s;

            try {
                startDate = formatter.parse(stringStartDate);
                strStartDate = targetFormat.format(targetFormat.parse(stringStartDate));
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
                strEndDate = targetFormat.format(targetFormat.parse(stringEndDate));
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

    @Override
    public void onClick(View v) {
        if (v == filterFab) {
            // Handle clicks for filterFab
            isFabOpen = true;
            animateFAB();
            filterDialog();
        } else if (v == vendorPendingInvoiceFabEmail) {
            // Handle clicks for vendorPendingInvoiceFabEmail
            isFabOpen = true;
            animateFAB();
            sendEmailDialog();
        } else if (v == vendorPendingInvoiceFabDownload) {
            // Handle clicks for vendorPendingInvoiceFabDownload
            isFabOpen = true;
            animateFAB();
            downloadPdfDailog();
        } else if (v == vendorPendingInvoiceFabAdd) {
            // Handle clicks for vendorPendingInvoiceFabAdd
            animateFAB();
        } else if (v == closeImageButton) {
            strEndDate = "";
            strStartDate = "";
            //Update Variables
            filterStartDate = null;
            filterEndDate = null;
            isFiltered = false;

            vendorPendingInvoiceList.clear();
            topLayout.setVisibility(View.GONE);
            if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                showLoader();
                getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void filterDialog() {
        dialogView = View.inflate(getActivity(), R.layout.filter_pending_invoice, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView)
                .setCancelable(false);
        alertDialog = builder.create();
        alertDialog.setCancelable(true);
        //DatePicker
        datePickerFragment = new RestrictFutureDatePickerFragment();
        datePickerFragment.setListener(this);
        //whole process of filter
        ImageView dialogFilterClearAll = (ImageView) dialogView.findViewById(R.id.dialog_filter_clear_all);
        LinearLayout expandCustomRange = (LinearLayout) dialogView.findViewById(R.id.expand_custom_range);
        dialogFilterEdtFrom = (TextView) dialogView.findViewById(R.id.dialog_filter_edt_from);
        dialogFilterEdtTo = (TextView) dialogView.findViewById(R.id.dialog_filter_edt_to);

        if (filterStartDate != null) {
            dialogFilterEdtFrom.setText(formatter.format(filterStartDate));
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
        if (filterEndDate != null) {
            dialogFilterEdtTo.setText(formatter.format(filterEndDate));
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

        dialogView.findViewById(R.id.dialog_filter_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog.dismiss();
                }
            }
        });
        dialogView.findViewById(R.id.dialog_filter_edt_from).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_from));

            }
        });

        dialogView.findViewById(R.id.dialog_filter_edt_to).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerFragment.show(getFragmentManager(), getString(R.string.dailogue_to));
            }
        });

        dialogView.findViewById(R.id.dialog_filter_clear_all).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                // updateDateOriginalList();
                strEndDate = "";
                strStartDate = "";
                //Update Variables
                filterStartDate = null;
                filterEndDate = null;
                isFiltered = false;

                vendorPendingInvoiceList.clear();
                if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                    showLoader();
                    getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogView.findViewById(R.id.dialog_filter_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date fromDate = null, endDate = null;
                //Get Dates and change them
                try {
                    fromDate = formatter.parse(dialogFilterEdtFrom.getText().toString().trim());                 // parse input
                    strStartDate = targetFormat.format(fromDate);    // format output
                    endDate = formatter.parse(dialogFilterEdtTo.getText().toString().trim());                 // parse input
                    strEndDate = targetFormat.format(endDate);    // format output
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (fromDate != null && endDate != null) {
                    if (endDate.before(fromDate)) {
                        CustomisedToast.error(getActivity(), "End date should be later than start date.").show();
                    } else {
                        if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                            //  mAdapter.filter(dialogFilterEdtFrom.getText().toString().trim() + " 00:00:00", dialogFilterEdtTo.getText().toString().trim() + " 00:00:00");
                            //Update Variables
                            filterStartDate = startDate;
                            filterEndDate = enddate;

                            vendorPendingInvoiceList.clear();
                            isFiltered = true;
                            getPendingInvoice(getString(R.string.last_updated_date), "1", strStartDate, strEndDate);
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    CustomisedToast.error(getActivity(), "Please select start and end dates.").show();
                }
            }
        });
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }

    public void animateFAB() {
        if (isFabOpen) {
            //Fabs
            vendorPendingInvoiceFabAdd.startAnimation(rotate_backward);
            vendorPendingInvoiceFabDownload.startAnimation(fab_close);
            vendorPendingInvoiceFabEmail.startAnimation(fab_close);
            filterFab.startAnimation(fab_close);

            //Layouts
            vendorPendingInvoiceDownloadLayout.startAnimation(fab_close);
            vendorPendingInvoiceEmailLayout.startAnimation(fab_close);
            vendorPendingInvoiceFilterLayout.startAnimation(fab_close);

            //Fabs
            vendorPendingInvoiceFabDownload.setClickable(false);
            vendorPendingInvoiceFabEmail.setClickable(false);
            filterFab.setClickable(false);

            //Layouts
            vendorPendingInvoiceDownloadLayout.setVisibility(View.GONE);
            vendorPendingInvoiceEmailLayout.setVisibility(View.GONE);
            vendorPendingInvoiceFilterLayout.setVisibility(View.GONE);
            isFabOpen = false;
        } else {
            //Fabs
            vendorPendingInvoiceFabAdd.startAnimation(rotate_forward);
            vendorPendingInvoiceFabDownload.startAnimation(fab_open);
            vendorPendingInvoiceFabEmail.startAnimation(fab_open);
            filterFab.startAnimation(fab_open);

            vendorPendingInvoiceDownloadLayout.startAnimation(fab_open);
            vendorPendingInvoiceEmailLayout.startAnimation(fab_open);
            vendorPendingInvoiceFilterLayout.startAnimation(fab_open);

            //Fabs
            vendorPendingInvoiceFabDownload.setClickable(true);
            vendorPendingInvoiceFabEmail.setClickable(true);
            filterFab.setClickable(true);

            //Layouts
            vendorPendingInvoiceDownloadLayout.setVisibility(View.VISIBLE);
            vendorPendingInvoiceEmailLayout.setVisibility(View.VISIBLE);
            vendorPendingInvoiceFilterLayout.setVisibility(View.VISIBLE);
            isFabOpen = true;
        }
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
        ProgressBar dialogProgressBar;
        TextView btnCancel;
        btnDownload = (TextView) dialogView.findViewById(R.id.btn_send_email);
        dialogFilterEdtFrom = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_from);
        dialogFilterEdtTo = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_to);
        dialogProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

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
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                        dialogProgressBar.setVisibility(View.VISIBLE);

                        //Update Dates for PDF Dialog
                        pdfStartDate = startDate;
                        pdfEndDate = enddate;

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
        task = getString(R.string.vendor_pending_invoice_send_email);
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
        task = getString(R.string.vendor_pending_invoice_send_email);
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

}
