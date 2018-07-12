package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.DatabaseHandler;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerAllInvoice;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 * Created by agt on 11/12/17.
 */

public class CustomerInvoiceTabAdapter extends RecyclerView.Adapter<CustomerInvoiceTabAdapter.MyViewHolder> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context context;
    private List<CustomerAllInvoice> customerAllInvoices;
    private List<CustomerAllInvoice> tempList = new ArrayList<CustomerAllInvoice>();
    private DatabaseHandler databaseHandler;
    private InvoiceAdapterListener listener;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private String cuId;
    private ProgressBar progressBar;

    public CustomerInvoiceTabAdapter(Context context, List<CustomerAllInvoice> customerAllInvoices,
                                     String cuId, InvoiceAdapterListener listener) {
        this.context = context;
        this.customerAllInvoices = customerAllInvoices;
        this.listener = listener;
        this.cuId = cuId;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_invoice, parent, false);
        return new MyViewHolder(itemView);
    }

    public void downloadVoucherDialog(final String cuId, final String pinVid, final String fileName) {
        final View dialogView = View.inflate(context, R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        TextView titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        titleTextView.setText("Download Invoice");
        downloadConfirmMessage.setText("Are you sure to download invoice?");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //calling API
                btnYes.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);
                if (!NetworkUtil.getConnectivityStatusString(context).equals(context.getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, cuId, pinVid, fileName);
                } else {
                    Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        btnYes.setEnabled(true);
                    }
                }, 3000);
            }
        });

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        alertDialog.show();
    }

    private void downloadPdf(final AlertDialog alertDialog, final String cuId, final String pinvid, final String fileName) {
        task = context.getString(R.string.customer_pending_invoice_download_voucher);
        if (AppPreferences.getIsLogin(context, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(context, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(context, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadCustomerPendingInvoiceVoucher(version, key, task, userId, accessToken, cuId,
                pinvid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(context).equals(context.getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_sales_invoice" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_sales_invoice" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals("10001")) {
                        alertDialog.dismiss();
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        alertDialog.dismiss();
                        //Logout
                        Constants.logoutWrongCredentials((Activity) context, apiResponse.getMessage());
                    } else {
                        Toast.makeText((Activity) context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(context, context.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            final CustomerAllInvoice customerAllInvoice = customerAllInvoices.get(position);
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            if (customerAllInvoice.getInvid() != null && !customerAllInvoice.getInvid().isEmpty()) {
                holder.invoiceNumberTextView.setText(customerAllInvoice.getInvid().toString().trim());
            } else {
                holder.invoiceNumberTextView.setText(customerAllInvoice.getInvoiceNumber().toString().trim());
            }
            holder.invoiceNumberTextView.setMovementMethod(LinkMovementMethod.getInstance());
            holder.invoiceNumberTextView.setTextColor(Color.BLUE);
            holder.invoiceNumberTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.invoiceNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do some thing
                    downloadVoucherDialog(cuId, customerAllInvoice.getInvoiceId(), customerAllInvoice.getInvid());
                }
            });

            holder.payableAmountTextView.setText("Total: " + context.getString(R.string.Rs) + " " +
                    formatter.format(ParseDouble(customerAllInvoice.getPayableAmount())) + "");

            if (ParseDouble(customerAllInvoice.getPayableAmount()) == ParseDouble(customerAllInvoice.getPayableAmount()) -
                    ParseDouble(customerAllInvoice.getPaidAmount())) {
                holder.pendingAmountTextView.setTextColor(context.getResources().getColor(R.color.lightRed));
                holder.pendingAmountTextView.setVisibility(View.VISIBLE);

            } else if ((ParseDouble(customerAllInvoice.getPayableAmount()) -
                    ParseDouble(customerAllInvoice.getPaidAmount()) > 0)) {
                holder.pendingAmountTextView.setTextColor(context.getResources().getColor(R.color.Coral));
                holder.pendingAmountTextView.setVisibility(View.VISIBLE);

            } else {
                holder.pendingAmountTextView.setVisibility(View.GONE);
            }

            if (!customerAllInvoice.getPaymentStatus().equals("Partially Paid")) {
                if (ParseDouble(customerAllInvoice.getPayableAmount()) -
                        ParseDouble(customerAllInvoice.getPaidAmount()) != 0) {
                    holder.payableAmountTextView.append(" | ");
                    holder.pendingAmountTextView.setText("Pending: " + context.getString(R.string.Rs) + " " +
                            formatter.format(ParseDouble(customerAllInvoice.getPayableAmount()) -
                                    ParseDouble(customerAllInvoice.getPaidAmount())) + "");
                } else {
                    holder.pendingAmountTextView.setVisibility(View.GONE);
                }
            } else {
                holder.pendingAmountTextView.setTextColor(context.getResources().getColor(R.color.blue_purchase_order));
                holder.pendingAmountTextView.setVisibility(View.VISIBLE);
                holder.payableAmountTextView.append(" | ");
                holder.pendingAmountTextView.setText("Paid Amount: " + context.getString(R.string.Rs) + " " +
                        formatter.format(ParseDouble(customerAllInvoice.getPaidAmount())) + "");
            }


            if (customerAllInvoice.getDate() == null || customerAllInvoice.getDate().isEmpty()) {
                holder.pendingSinceTextView.setVisibility(View.GONE);
            } else {
                holder.pendingSinceTextView.setVisibility(View.VISIBLE);
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(customerAllInvoice.getDate());
                    holder.pendingSinceTextView.setText(outputFormat.format(date).toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

            if (customerAllInvoice.getPaymentStatus() != null && !customerAllInvoice.getPaymentStatus().isEmpty()) {
                holder.statusTextView.setText(customerAllInvoice.getPaymentStatus());
                holder.statusTextView.setVisibility(View.VISIBLE);
                if (customerAllInvoice.getPaymentStatus().equals("Pending")) {
                    drawable.setColor(context.getResources().getColor(R.color.red_order));
                } else if (customerAllInvoice.getPaymentStatus().equals("Partially Paid")) {
                    drawable.setColor(context.getResources().getColor(R.color.orange_order));
                } else {
                    drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                }
            }

            if (customerAllInvoice.getNarration() == null || customerAllInvoice.getNarration().isEmpty()) {
                holder.pendingNarrationTextview.setVisibility(View.GONE);
            } else {
                holder.pendingNarrationTextview.setVisibility(View.VISIBLE);
                holder.pendingNarrationTextview.setText(customerAllInvoice.getNarration().toString().trim());
            }

            if (customerAllInvoice.getTransactionModeName() == null ||
                    customerAllInvoice.getTransactionModeName().isEmpty()) {
                holder.paymentModeTextView.setVisibility(View.GONE);
            } else {
                holder.paymentModeTextView.setVisibility(View.VISIBLE);
                holder.paymentModeTextView.setText("Mode: "+customerAllInvoice.getTransactionModeName().
                        toString().trim());
            }

            //Click Event
            applyClickEvents(holder, position);

            int sideMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
            if (position == 0) {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._6sdp);
                setMargins(holder.cardView, sideMargin, topMargin, sideMargin, 0);
            } else {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, sideMargin, topMargin, sideMargin, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        return customerAllInvoices.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position);
            }
        });
    }

    public void filter(String startDate, String endDate) {
        try {
            listener.updateList(getNewList(customerAllInvoices, startDate, endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    private List<CustomerAllInvoice> getNewList(List<CustomerAllInvoice> oldList, String startDate, String endDate) throws java.text.ParseException {
        List<CustomerAllInvoice> newList = new ArrayList<CustomerAllInvoice>();
        Date d2 = null, d3 = null, d4 = null;
        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        d3 = f.parse(startDate);
        d4 = f.parse(endDate);
        for (int i = 0; i < oldList.size(); i++) {
            String b = oldList.get(i).getDate();
            d2 = simpleDateFormat.parse(b);
            if (d2.compareTo(d3) >= 0 && d2.compareTo(d4) <= 0) {
                newList.add(oldList.get(i));
            }
        }
        return newList;
    }

    //To deal with empty string of amount
    double ParseDouble(String strNumber) {
        if (strNumber != null && strNumber.length() > 0) {
            try {
                return Double.parseDouble(strNumber);
            } catch (Exception e) {
                return -1;   // or some value to mark this field is wrong. or make a function validates field first ...
            }
        } else return 0;
    }

    public interface InvoiceAdapterListener {
        void onMessageRowClicked(int position);

        void updateList(List<CustomerAllInvoice> customerAllInvoices);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView invoiceNumberTextView;
        private TextView payableAmountTextView;
        private TextView pendingAmountTextView;
        private RelativeLayout container;
        private TextView pendingSinceTextView, statusTextView;
        private TextView pendingNarrationTextview, paymentModeTextView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_inner);
            container = (RelativeLayout) view.findViewById(R.id.container);
            invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            pendingAmountTextView = (TextView) view.findViewById(R.id.pending_amount_textView);
            pendingSinceTextView = (TextView) view.findViewById(R.id.pending_since_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            pendingNarrationTextview = (TextView) view.findViewById(R.id.pending_invoice_narration);
            paymentModeTextView = (TextView) view.findViewById(R.id.payment_mode_textView);
        }
    }
}

