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
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
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
 * Created by agt on 23/2/18.
 */

public class TestWalletAdapter extends RecyclerView.Adapter<TestWalletAdapter.MyViewHolder> {
    private Context mContext;
    private List<CustomerWallet> customerWalletList;
    private SpannableString spannableString;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private String cuId;
    private SimpleDateFormat simpleDateFormat;
    private ProgressBar progressBar;

    public TestWalletAdapter(Context mContext, List<CustomerWallet> customerWalletList, String cuId) {
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
                                    .setTitle(fileName + "_purchase" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_purchase" + ".pdf")
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_customer_wallet, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CustomerWallet customerWallet = customerWalletList.get(position);
        holder.customerWalletNarration.setText(customerWallet.getNarration().toString().trim());
        /*if (customerWallet.getType().equals("1")) {
            holder.customerWalletDebitTitle.setText("CR");
            holder.customerWalletDebit.setTextColor(mContext.getResources().getColor(R.color.green));
            holder.customerWalletDebitTitle.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            holder.customerWalletDebitTitle.setText("DR");
            holder.customerWalletDebit.setTextColor(mContext.getResources().getColor(R.color.lightRed));
            holder.customerWalletDebitTitle.setTextColor(mContext.getResources().getColor(R.color.lightRed));
        }

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Voucher Number
        spannableString = new SpannableString("" + customerWallet.getAvid());
        ClickableSpan clickableString = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(customerWallet.getDate());
                    downloadVoucherDialog(cuId, customerWallet.getAccVoucherId(), outputFormat.format(date).toString(),
                            customerWallet.getAvid());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        spannableString.setSpan(clickableString, 0, customerWallet.getAvid().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(Color.BLUE), 0,
                customerWallet.getAvid().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.customerWalletInfo.setMovementMethod(LinkMovementMethod.getInstance());

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date = simpleDateFormat.parse(customerWallet.getDate());
            holder.customerWalletInfo.setText(customerWallet.getAccountVoucherType() + " | ");
            holder.customerWalletInfo.append(spannableString);
            holder.customerWalletInfo.append(" | " + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
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

        holder.avIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View textView) {
                // do some thing
                try {
                    DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
                    Date date = simpleDateFormat.parse(customerWallet.getDate());
                    downloadVoucherDialog(cuId, customerWallet.getAccVoucherId(), outputFormat.format(date).toString(),
                            customerWallet.getAvid());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

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

        public MyViewHolder(View view) {
            super(view);
            cardViewOuter = (CardView) view.findViewById(R.id.card_view_outer);
            cardViewInner = (CardView) view.findViewById(R.id.card_view_inner);
            avIdTextView = (TextView) view.findViewById(R.id.avId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            customerWalletNarration = (TextView) view.findViewById(R.id.customer_wallet_narration);
            customerWalletInfo = (TextView) view.findViewById(R.id.customer_wallet_info);
            customerWalletDebit = (TextView) view.findViewById(R.id.customer_wallet_debit);
        }
    }

}