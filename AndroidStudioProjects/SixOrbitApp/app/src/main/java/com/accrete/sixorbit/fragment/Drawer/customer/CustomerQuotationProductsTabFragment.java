package com.accrete.sixorbit.fragment.Drawer.customer;

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
import com.accrete.sixorbit.adapter.CustomerQuotationProductAdapter;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.helper.PassDateToCounsellor;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.QuotationProduct;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.accrete.sixorbit.utils.EmailValidator;
import com.accrete.sixorbit.utils.RestrictFutureDatePickerFragment;
import com.google.gson.GsonBuilder;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.AnimateGifMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
 * Created by agt on 14/12/17.
 */

public class CustomerQuotationProductsTabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, PassDateToCounsellor, CustomerQuotationProductAdapter.QuotationProductAdapterListener {
    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private CustomerQuotationProductAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String cuid, dataChanged, status, strStartDate = "", strEndDate = "", email, name;
    private List<QuotationProduct> quotationProductList = new ArrayList<>();
    private boolean loading;
    private LinearLayoutManager mLayoutManager;
    private int visibleThreshold = 2, lastVisibleItem, totalItemCount;
    private LinearLayout customerOrderEmail;
    private FloatingActionButton customerOrderFabEmail;
    private LinearLayout customerOrderDownload;
    private FloatingActionButton customerOrderFabDownload;
    private FloatingActionButton customerOrderFabAdd;
    private Boolean isFabOpen = false;
    private Animation fab_open, fab_close, rotate_forward, rotate_backward;
    private SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    private TextView dialogFilterEdtFrom;
    private TextView dialogFilterEdtTo;
    private Date startDate, enddate, emailStartDate, emailEndDate, pdfStartDate, pdfEndDate;
    private RestrictFutureDatePickerFragment datePickerFragment;
    private AlertDialog dialogSendEmail, dialogPdfDownload;
    private DownloadManager downloadManager;
    private String cuId, qoId;
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
                            if (quotationProductList != null && quotationProductList.size() == 0) {
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

    private void findViews(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        emptyTextView = (TextView) view.findViewById(R.id.empty_textView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        imageView = (ImageView) view.findViewById(R.id.imageView);

        customerOrderEmail = (LinearLayout) view.findViewById(R.id.customer_order_email);
        customerOrderFabEmail = (FloatingActionButton) view.findViewById(R.id.customer_order_fab_email);
        customerOrderDownload = (LinearLayout) view.findViewById(R.id.customer_order_download);
        customerOrderFabDownload = (FloatingActionButton) view.findViewById(R.id.customer_order_fab_download);
        customerOrderFabAdd = (FloatingActionButton) view.findViewById(R.id.customer_order_fab_add);

        customerOrderFabEmail.setOnClickListener(this);
        customerOrderFabDownload.setOnClickListener(this);
        customerOrderFabAdd.setOnClickListener(this);

        fab_open = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_backward);

        //Hide by default
        customerOrderFabAdd.setVisibility(View.GONE);

        adapter = new CustomerQuotationProductAdapter(getActivity(), quotationProductList, cuid, this);
        mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);

       /* //Scroll Listener
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = mLayoutManager.getItemCount();
                lastVisibleItem = mLayoutManager.findLastCompletelyVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold) && quotationProductList.size() > 0) {
                    // End has been reached
                    // Do something
                    loading = true;
                    //calling API
                    if (!NetworkUtil.getConnectivityStatusString(getActivity()).equals(getString(R.string.not_connected_to_internet))) {
                        getQuotationProductsList(quotationProductList.get(totalItemCount - 1).getCreatedTs(), "2");
                    } else {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        Toast.makeText(getActivity(), getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
                if (dy > 0 || dy < 0 && customerOrderFabAdd.isShown()) {
                    //  customerOrderFabAdd.hide();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //   customerOrderFabAdd.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });*/

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setEnabled(false);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_purchase_order_history, container, false);
        findViews(rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == customerOrderFabEmail) {
            // Handle clicks for customerOrderFabEmail
            isFabOpen = true;
            animateFAB();
            sendEmailDialog();
        } else if (v == customerOrderFabDownload) {
            // Handle clicks for customerOrderFabDownload
            isFabOpen = true;
            animateFAB();
            downloadPdfDailog();
        } else if (v == customerOrderFabAdd) {
            // Handle clicks for customerOrderFabAdd
            animateFAB();
        }
    }

    public void doRefresh() {
        if (quotationProductList != null && quotationProductList.size() == 0) {
            status = NetworkUtil.getConnectivityStatusString(getActivity());
            if (!status.equals(getString(R.string.not_connected_to_internet))) {
                loading = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (quotationProductList != null && quotationProductList.size() == 0) {
                            showLoader();
                        }
                        getQuotationProductsList();
                    }
                }, 200);
            } else {
                recyclerView.setVisibility(View.GONE);
                emptyTextView.setVisibility(View.VISIBLE);
                emptyTextView.setText(getString(R.string.no_internet_try_later));
                customerOrderFabAdd.setVisibility(View.GONE);
            }
        }
    }

    public void animateFAB() {
        if (isFabOpen) {
            customerOrderFabAdd.startAnimation(rotate_backward);
            customerOrderFabDownload.startAnimation(fab_close);
            customerOrderFabEmail.startAnimation(fab_close);

            customerOrderDownload.startAnimation(fab_close);
            customerOrderEmail.startAnimation(fab_close);

            customerOrderFabDownload.setClickable(false);
            customerOrderFabEmail.setClickable(false);
            customerOrderEmail.setVisibility(View.GONE);
            customerOrderDownload.setVisibility(View.GONE);
            isFabOpen = false;
        } else {
            customerOrderFabAdd.startAnimation(rotate_forward);
            customerOrderFabDownload.startAnimation(fab_open);
            customerOrderFabEmail.startAnimation(fab_open);

            customerOrderDownload.startAnimation(fab_open);
            customerOrderEmail.startAnimation(fab_open);

            customerOrderEmail.setVisibility(View.VISIBLE);
            customerOrderDownload.setVisibility(View.VISIBLE);
            customerOrderFabDownload.setClickable(true);
            customerOrderFabEmail.setClickable(true);
            isFabOpen = true;
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

    @Override
    public void onRefresh() {
        status = NetworkUtil.getConnectivityStatusString(getActivity());
        if (!status.equals(getString(R.string.not_connected_to_internet))) {
            if (quotationProductList != null && quotationProductList.size() > 0) {
                getQuotationProductsList();
            } else {
                if (quotationProductList != null && quotationProductList.size() == 0) {
                    showLoader();
                }
                getQuotationProductsList();
            }
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            //  customerOrderFabAdd.setVisibility(View.VISIBLE);

        } else {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
            emptyTextView.setText(getString(R.string.no_internet_try_later));
            swipeRefreshLayout.setRefreshing(false);
            customerOrderFabAdd.setVisibility(View.GONE);
        }
    }

    private void getQuotationProductsList() {
        task = getString(R.string.quotation_products_task);
        if (AppPreferences.getIsLogin(getActivity(), AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(getActivity(), AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(getActivity(), AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(getActivity(), AppUtils.DOMAIN);
        }
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.getCustomerProductQuotationInfo(version, key, task, userId, accessToken, qoId);
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
                        for (final QuotationProduct quotationProduct : apiResponse.getData().getQuotationProducts()) {
                            if (apiResponse.getData().getQuotationProducts() != null) {
                                //TODO - Updated On 10th May
                                if (quotationProduct.isPriceIncludeTaxShow() && quotationProduct.getButapid() != null &&
                                        !quotationProduct.getButapid().isEmpty() && !quotationProduct.getButapid().equals("0")) {
                                    quotationProduct.setPrice(new BigDecimal(((Constants.ParseDouble(quotationProduct.getPrice())
                                            * Constants.ParseDouble(quotationProduct.getItemTax())) / 100) +
                                            Constants.ParseDouble(quotationProduct.getPrice()))
                                            .setScale(2, RoundingMode.HALF_UP).toPlainString());
                                }
                                quotationProductList.add(quotationProduct);
                            }
                        }
                        loading = false;
                        if (quotationProductList != null && quotationProductList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            //  customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //   customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        loading = false;
                        if (quotationProductList != null && quotationProductList.size() == 0) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            //       customerOrderFabAdd.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            //       customerOrderFabAdd.setVisibility(View.VISIBLE);
                        }
                        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        adapter.notifyDataSetChanged();

                        //Deleted User
                        if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                                apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                            //Logout
                            Constants.logoutWrongCredentials(getActivity(), apiResponse.getMessage());
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
                    Log.d("errorInWallet", t.getMessage() + "");
                    if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    hideLoader();
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
        final EditText edittextSendEmail;
        TextView btnDownload;
        TextView btnCancel;
        final ProgressBar dialogProgressBar;
        btnDownload = (TextView) dialogView.findViewById(R.id.btn_send_email);
        dialogFilterEdtFrom = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_from);
        dialogFilterEdtTo = (TextView) dialogView.findViewById(R.id.dialog_send_email_edt_to);
        dialogProgressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        if (pdfStartDate != null) {
            dialogFilterEdtFrom.setText(formatter.format(pdfStartDate));
        } else {
            strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR);
            dialogFilterEdtFrom.setText(strStartDate);
        }
        if (pdfEndDate != null) {
            dialogFilterEdtTo.setText(formatter.format(pdfEndDate));
        } else {
            strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
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
                                    .setTitle("order" + name + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            "order" + name + ".pdf")
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
            strStartDate = "01-04-" + Calendar.getInstance().get(Calendar.YEAR);
            dialogFilterEdtFrom.setText(strStartDate);
        }
        if (emailEndDate != null) {
            dialogFilterEdtTo.setText(formatter.format(emailEndDate));
        } else {
            strEndDate = "31-03-" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
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

    @Override
    public void onMessageRowClicked(int position, String qoId, String qoText) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        //here is your string
        cuId = bundle.getString(getString(R.string.cuid));
        qoId = bundle.getString(getString(R.string.qo_id));
    }
}
