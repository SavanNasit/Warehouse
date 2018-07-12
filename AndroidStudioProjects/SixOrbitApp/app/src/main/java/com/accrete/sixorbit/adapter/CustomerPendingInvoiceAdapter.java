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
import com.accrete.sixorbit.model.CustomerPendingInvoice;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

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
 * Created by agt on 2/11/17.
 */

public class CustomerPendingInvoiceAdapter extends RecyclerView.Adapter<CustomerPendingInvoiceAdapter.MyViewHolder> {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Context context;
    private List<CustomerPendingInvoice> customerPendingInvoiceList;
    private List<CustomerPendingInvoice> tempList = new ArrayList<CustomerPendingInvoice>();
    private DatabaseHandler databaseHandler;
    private PendingInvoiceAdapterListener listener;
    private TextView downloadConfirmMessage, titleTextView;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private String cuId;
    private ProgressBar progressBar;

    public CustomerPendingInvoiceAdapter(Context context, List<CustomerPendingInvoice> customerPendingInvoiceList,
                                         String cuId, PendingInvoiceAdapterListener listener) {
        this.context = context;
        this.customerPendingInvoiceList = customerPendingInvoiceList;
        this.listener = listener;
        this.cuId = cuId;
    }

    public void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    @Override
    public CustomerPendingInvoiceAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_pending_invoice, parent, false);
        return new CustomerPendingInvoiceAdapter.MyViewHolder(itemView);
    }

    public void downloadVoucherDialog(final String cuId, final String pinVid, final String fileName) {
        final View dialogView = View.inflate(context, R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        titleTextView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);

        titleTextView.setText("Download Pending Invoice");
        downloadConfirmMessage.setText("Are you sure to download Pending Invoice?");

        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

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
        try {
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
                                        .setTitle(fileName + "_pending_sales_invoice" + ".pdf")
                                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                                fileName + "_pending_sales_invoice" + ".pdf")
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
                            //Logout
                            alertDialog.dismiss();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    @Override
    public void onBindViewHolder(CustomerPendingInvoiceAdapter.MyViewHolder holder, int position) {
        try {
            final CustomerPendingInvoice customerPendingInvoice = customerPendingInvoiceList.get(position);
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            if (customerPendingInvoice.getInvoiceNumber() != null && !customerPendingInvoice.getInvoiceNumber().isEmpty()) {
                holder.invoiceNumberTextView.setText(customerPendingInvoice.getInvoiceNumber().toString().trim());
            } else {
                holder.invoiceNumberTextView.setText(customerPendingInvoice.getInvid().toString().trim());
            }
            holder.invoiceNumberTextView.setMovementMethod(LinkMovementMethod.getInstance());
            holder.invoiceNumberTextView.setTextColor(Color.BLUE);
            holder.invoiceNumberTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            holder.invoiceNumberTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //do some thing
                    downloadVoucherDialog(cuId, customerPendingInvoice.getInvoiceId(), customerPendingInvoice.getInvid());
                }
            });

            holder.payableAmountTextView.setText("Total: " + context.getString(R.string.Rs) + " " +
                    formatter.format(Double.parseDouble(customerPendingInvoice.getPayableAmount())) + "");

            if (Double.parseDouble(customerPendingInvoice.getPayableAmount()) == Double.parseDouble(customerPendingInvoice.getPayableAmount()) -
                    ParseDouble(customerPendingInvoice.getPaidAmount())) {
                holder.pendingAmountTextView.setTextColor(context.getResources().getColor(R.color.lightRed));
                holder.pendingAmountTextView.setVisibility(View.VISIBLE);
                holder.payableAmountTextView.append(" | ");
            } else if ((Double.parseDouble(customerPendingInvoice.getPayableAmount()) -
                    ParseDouble(customerPendingInvoice.getPaidAmount()) > 0)) {
                holder.pendingAmountTextView.setTextColor(context.getResources().getColor(R.color.Coral));
                holder.pendingAmountTextView.setVisibility(View.VISIBLE);
                holder.payableAmountTextView.append(" | ");
            } else {
                holder.pendingAmountTextView.setVisibility(View.GONE);
            }
            holder.pendingAmountTextView.setText("Pending: " + context.getString(R.string.Rs) + " " +
                    formatter.format(Double.parseDouble(customerPendingInvoice.getPayableAmount()) -
                            ParseDouble(customerPendingInvoice.getPaidAmount())) + "");

            if (customerPendingInvoice.getPendingSince() == null || customerPendingInvoice.getPendingSince().isEmpty()) {
                holder.pendingSinceTextView.setVisibility(View.GONE);
            } else {
                holder.pendingSinceTextView.setVisibility(View.VISIBLE);
                holder.pendingSinceTextView.setText(customerPendingInvoice.getPendingSince().toString().trim());
            }

            if (customerPendingInvoice.getNarration() == null || customerPendingInvoice.getNarration().isEmpty()) {
                holder.pendingNarrationTextview.setVisibility(View.GONE);
            } else {
                holder.pendingNarrationTextview.setVisibility(View.VISIBLE);
                holder.pendingNarrationTextview.setText(customerPendingInvoice.getNarration().toString().trim());
            }

            if (customerPendingInvoice.getInvoiceType() != null
                    && !customerPendingInvoice.getInvoiceType().isEmpty()) {
                holder.typeTextView.setVisibility(View.VISIBLE);
                holder.typeTextView.setText("Type : " + customerPendingInvoice.getInvoiceType());
            } else {
                holder.typeTextView.setVisibility(View.GONE);
            }

            holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

            if (customerPendingInvoice.getPaymentStatus() != null && !customerPendingInvoice.getPaymentStatus().isEmpty()) {
                holder.statusTextView.setText(customerPendingInvoice.getPaymentStatus());
                holder.statusTextView.setVisibility(View.VISIBLE);
                if (customerPendingInvoice.getPaymentStatus().equals("Pending")) {
                    drawable.setColor(context.getResources().getColor(R.color.red_order));
                } else if (customerPendingInvoice.getPaymentStatus().equals("Partially Paid")) {
                    drawable.setColor(context.getResources().getColor(R.color.orange_order));
                } else {
                    drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                }
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

    @Override
    public int getItemCount() {
        return customerPendingInvoiceList.size();
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
            listener.updateList(getNewList(customerPendingInvoiceList, startDate, endDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    private List<CustomerPendingInvoice> getNewList(List<CustomerPendingInvoice> oldList, String startDate, String endDate) throws java.text.ParseException {
        List<CustomerPendingInvoice> newList = new ArrayList<CustomerPendingInvoice>();
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

    public interface PendingInvoiceAdapterListener {
        void onMessageRowClicked(int position);

        void updateList(List<CustomerPendingInvoice> customerPendingInvoices);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView invoiceNumberTextView;
        private TextView payableAmountTextView;
        private TextView pendingAmountTextView;
        private RelativeLayout container;
        private TextView pendingSinceTextView;
        private TextView pendingNarrationTextview;
        private TextView typeTextView, paymentModeTextView, statusTextView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view_inner);
            container = (RelativeLayout) view.findViewById(R.id.container);
            invoiceNumberTextView = (TextView) view.findViewById(R.id.invoice_number_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            pendingAmountTextView = (TextView) view.findViewById(R.id.pending_amount_textView);
            pendingSinceTextView = (TextView) view.findViewById(R.id.pending_since_textView);
            pendingNarrationTextview = (TextView) view.findViewById(R.id.pending_invoice_narration);
            typeTextView = (TextView) view.findViewById(R.id.type_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            paymentModeTextView = (TextView) view.findViewById(R.id.payment_mode_textView);
            paymentModeTextView.setVisibility(View.GONE);
        }
    }
}
