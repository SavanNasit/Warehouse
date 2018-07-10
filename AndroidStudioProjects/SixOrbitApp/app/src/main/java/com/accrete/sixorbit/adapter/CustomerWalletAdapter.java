package com.accrete.sixorbit.adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.CustomerWallet;
import com.accrete.sixorbit.rest.ApiClient;
import com.accrete.sixorbit.rest.ApiInterface;
import com.accrete.sixorbit.utils.AppPreferences;
import com.accrete.sixorbit.utils.AppUtils;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

public class CustomerWalletAdapter extends RecyclerView.Adapter<CustomerWalletAdapter.MyViewHolder> {
    private Context mContext;
    private List<CustomerWallet> customerWalletList;
    private SpannableString spannableString;
    private TextView downloadConfirmMessage;
    private TextView downloadTitleMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private String cuId;
    private SimpleDateFormat simpleDateFormat;
    private ProgressBar progressBar;

    public CustomerWalletAdapter(Context mContext, List<CustomerWallet> customerWalletList, String cuId) {
        this.mContext = mContext;
        this.customerWalletList = customerWalletList;
        this.cuId = cuId;
    }

    private void downloadPdf(final AlertDialog alertDialog, final String cuId, final String avid, final String date,
                             final String fileName) {
        task = mContext.getString(R.string.customer_wallet_download_voucher);
        if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadCustomerWalletVoucher(version, key, task, userId, accessToken, cuId, avid);
        Log.d("Request", String.valueOf(call));
        Log.d("url", String.valueOf(call.request().url()));
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call call, Response response) {
                Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                final ApiResponse apiResponse = (ApiResponse) response.body();
                try {
                    if (apiResponse.getSuccess()) {
                        if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                            alertDialog.dismiss();

                            //Download a file and display in phone's download folder
                            Environment
                                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    .mkdirs();
                            downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
                            String url = apiResponse.getData().getFilename();
                            Uri uri = Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri)
                                    .setTitle(fileName + "_sales_invoice_reference" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_sales_invoice_reference" + ".pdf")
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            downloadManager.enqueue(request);
                        } else {
                            Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                        }
                    } else if (apiResponse.getSuccessCode().equals(Constants.WRONG_CREDENTIALS) ||
                            apiResponse.getSuccessCode().equals(Constants.INVALID_ACCESSTOKEN)) {
                        //Logout
                        alertDialog.dismiss();
                        Constants.logoutWrongCredentials((Activity) mContext, apiResponse.getMessage());
                    } else {
                        Toast.makeText((Activity) mContext, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }


            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public CustomerWalletAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_customer_wallet, parent, false);
        return new CustomerWalletAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CustomerWalletAdapter.MyViewHolder holder, final int position) {
        try {
            final CustomerWallet customerWallet = customerWalletList.get(position);
            holder.customerWalletNarration.setText(customerWallet.getNarration().toString().trim());
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (customerWallet.getDate() != null && !customerWallet.getDate().isEmpty()) {
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(customerWallet.getDate());
                    holder.dateTextView.setText(outputFormat.format(date).toString().trim());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.customerWalletInfo.setText(customerWallet.getAccountVoucherType());
            holder.avIdTextView.setText(customerWallet.getAvid());
            holder.avIdTextView.setMovementMethod(LinkMovementMethod.getInstance());

            double amount = Double.parseDouble(customerWallet.getAmount());
            DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");
            if (customerWallet.getType().equals("1")) {
                holder.customerWalletDebit.setText("CR \n" + mContext.getString(R.string.Rs) + " " + formatter.format(amount));
                holder.customerWalletDebit.setTextColor(mContext.getResources().getColor(R.color.green));
            } else {
                holder.customerWalletDebit.setTextColor(mContext.getResources().getColor(R.color.lightRed));
                holder.customerWalletDebit.setText("DR \n" + mContext.getString(R.string.Rs) + " " + formatter.format(amount));
            }
            if (position == 0) {
                int topMargin = mContext.getResources().getDimensionPixelSize(R.dimen._4sdp);
                int sideMargin = mContext.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardViewInner, sideMargin, topMargin, sideMargin, 0);
            } else {
                int topMargin = mContext.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardViewInner, topMargin, topMargin, topMargin, 0);
            }

            if (customerWallet.getReferenceNo() != null && !customerWallet.getReferenceNo().isEmpty()) {
                //Reference Number
                Spannable word = new SpannableString("Reference No. ");
                word.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.lightGray)),
                        0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.customerReferenceNumber.setText(word);
                Spannable wordTwo = new SpannableString(customerWallet.getReferenceNo() + "");

                wordTwo.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View v) {
                        if (customerWallet.getType() != null && customerWallet.getType().equals("2")) {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                                Date date = simpleDateFormat.parse(customerWallet.getDate());
                                downloadVoucherDialog(cuId, customerWallet.getAccVoucherId(), outputFormat.format(date).toString(),
                                        customerWallet.getAvid());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, 0, wordTwo.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                // All the rest will have the same spannable.
                ClickableSpan cs = new ClickableSpan() {
                    @Override
                    public void onClick(View v) {

                        if (customerWallet.getType() != null && customerWallet.getType().equals("2")) {
                            try {
                                DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                                Date date = simpleDateFormat.parse(customerWallet.getDate());
                                downloadVoucherDialog(cuId, customerWallet.getAccVoucherId(), outputFormat.format(date).toString(),
                                        customerWallet.getAvid());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };


                wordTwo.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.blue)),
                        0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordTwo.setSpan(new UnderlineSpan(),
                        0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                wordTwo.setSpan(cs, 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.customerReferenceNumber.append(wordTwo);
                holder.customerReferenceNumber.setMovementMethod(LinkMovementMethod.getInstance());
                holder.customerReferenceNumber.setVisibility(View.VISIBLE);
            } else {
                holder.customerReferenceNumber.setVisibility(View.GONE);
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
        return customerWalletList.size();
    }

    public void downloadVoucherDialog(final String cuId, final String avid, final String date, final String fileName) {
        final View dialogView = View.inflate(mContext, R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        downloadTitleMessage = (TextView) dialogView.findViewById(R.id.title_textView);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        downloadTitleMessage.setText("Download Invoice Reference");
        downloadConfirmMessage.setText("Are you sure to download invoice reference?");

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
                if (!NetworkUtil.getConnectivityStatusString(mContext).equals(mContext.getString(R.string.not_connected_to_internet))) {
                    downloadPdf(alertDialog, cuId, avid, date, fileName);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
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

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardViewOuter;
        private CardView cardViewInner;
        private TextView avIdTextView;
        private TextView dateTextView;
        private TextView customerWalletNarration;
        private TextView customerWalletInfo;
        private TextView customerWalletDebit;
        private TextView customerReferenceNumber;

        public MyViewHolder(View view) {
            super(view);
            cardViewOuter = (CardView) view.findViewById(R.id.card_view_outer);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
            avIdTextView = (TextView) view.findViewById(R.id.avId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            customerWalletNarration = (TextView) view.findViewById(R.id.customer_wallet_narration);
            customerWalletInfo = (TextView) view.findViewById(R.id.customer_wallet_info);
            customerWalletDebit = (TextView) view.findViewById(R.id.customer_wallet_debit);
            customerReferenceNumber = (TextView) view.findViewById(R.id.customer_reference_number);
        }
    }

}