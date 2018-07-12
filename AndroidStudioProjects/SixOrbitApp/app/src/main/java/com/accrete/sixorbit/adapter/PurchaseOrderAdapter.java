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
import android.text.Html;
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
import com.accrete.sixorbit.helper.NetworkUtil;
import com.accrete.sixorbit.model.ApiResponse;
import com.accrete.sixorbit.model.PurchaseOrder;
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
 * Created by agt on 5/12/17.
 */

public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.MyViewHolder> {
    private Activity activity;
    private List<PurchaseOrder> purchaseOrderList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private PurchaseOrderAdapterListener listener;
    private AlertDialog alertDialog;
    private DownloadManager downloadManager;
    private ProgressBar progressBar;
    private TextView downloadConfirmMessage;
    private TextView btnYes;
    private TextView btnCancel;

    public PurchaseOrderAdapter(Activity activity, List<PurchaseOrder> purchaseOrderList, String venId,
                                PurchaseOrderAdapterListener listener) {
        this.activity = activity;
        this.purchaseOrderList = purchaseOrderList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_purchase_order_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PurchaseOrder purchaseOrder = purchaseOrderList.get(position);

        holder.orderIdTextView.setText(purchaseOrder.getPurchaseOrderId());
        holder.orderIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
        holder.orderIdTextView.setTextColor(Color.BLUE);
        holder.orderIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        holder.orderIdTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //do some thing
                downloadPurchaseOrderDialog(activity, venId, purchaseOrder.getPurorid(),
                        purchaseOrder.getPurchaseOrderId());
            }
        });

        holder.wareHouseTextView.setText(purchaseOrder.getWarehouseName());

        double amount = Double.parseDouble(purchaseOrder.getAmount());
        double tax = Double.parseDouble(purchaseOrder.getTax());
        double amountAfterTax = Double.parseDouble(purchaseOrder.getAmountAfterTax());
        double payableAmount = Double.parseDouble(purchaseOrder.getPayableAmount());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###");

        holder.amountTextView.setText("Amount\r\n " + activity.getString(R.string.Rs) + " " + formatter.format(amount));
        holder.taxTextView.setText("Tax\r\n " + activity.getString(R.string.Rs) + " " + formatter.format(tax));
        holder.amountTaxTextView.setText("After Tax\r\n "+ activity.getString(R.string.Rs) + " " + formatter.format(amountAfterTax));
        holder.payableAmountTextView.setText("Payable Amount " + activity.getString(R.string.Rs) + " " + formatter.format(payableAmount));

        holder.createdByTextView.setText("By: " + purchaseOrder.getCreatedBy());


        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(purchaseOrder.getCreatedTs());
            holder.dateTextView.setText("" + outputFormat.format(date).toString().trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.statusTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();

        if (purchaseOrder.getPurorsid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            holder.statusTextView.setText("Created");
        } else if (purchaseOrder.getPurorsid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Partial Received");
        } else if (purchaseOrder.getPurorsid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Received");
        } else if (purchaseOrder.getPurorsid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Cancelled");
        } else if (purchaseOrder.getPurorsid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Closed");
        } else if (purchaseOrder.getPurorsid().equals("6")) {
            drawable.setColor(activity.getResources().getColor(R.color.orange_purchase_order));
            holder.statusTextView.setText("Pending");
        } else if (purchaseOrder.getPurorsid().equals("7")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Expected Delivery");
        } else if (purchaseOrder.getPurorsid().equals("8")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Pending Transportation");
        }

        //Click Item
        applyClickEvents(holder, position, purchaseOrder.getOrderId(), purchaseOrder.getPurchaseOrderId());

        if (position == 0) {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._4sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else {
            int topMargin = activity.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
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
        return purchaseOrderList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position, final String orderId, final String orderText) {
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onMessageRowClicked(position, orderId, orderText);
            }
        });
    }

    public void downloadPurchaseOrderDialog(Context context, final String venId, final String orderId,
                                            final String fileName) {
        final View dialogView = View.inflate(context, R.layout.dialog_download_voucher, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView)
                .setCancelable(true);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);

        TextView title_textView = (TextView) dialogView.findViewById(R.id.title_textView);
        downloadConfirmMessage = (TextView) dialogView.findViewById(R.id.download_confirm_message);
        btnYes = (TextView) dialogView.findViewById(R.id.btn_yes);
        btnCancel = (TextView) dialogView.findViewById(R.id.btn_cancel);
        progressBar = (ProgressBar) dialogView.findViewById(R.id.dialog_progress_bar);

        title_textView.setText("Download Purchase Order");
        downloadConfirmMessage.setText(context.getString(R.string.download_purchase_order_confirm_msg));

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
                    downloadPdf(context, alertDialog, venId, orderId, fileName);
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

    private void downloadPdf(Context context, final AlertDialog alertDialog,
                             final String venId, final String orderId, final String fileName) {
        task = context.getString(R.string.vendor_purchase_order_download);
        if (AppPreferences.getIsLogin(context, AppUtils.ISLOGIN)) {
            userId = AppPreferences.getUserId(context, AppUtils.USER_ID);
            accessToken = AppPreferences.getAccessToken(context, AppUtils.ACCESS_TOKEN);
            ApiClient.BASE_URL = AppPreferences.getLastDomain(context, AppUtils.DOMAIN);
        }

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ApiResponse> call = apiService.downloadVendorPurchaseOrder(version, key, task, userId,
                accessToken, venId, orderId);
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
                                    .setTitle(fileName + "_purchase_order" + ".pdf")
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                                            fileName + "_purchase_order" + ".pdf")
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
                        Constants.logoutWrongCredentials((Activity) context, apiResponse.getMessage());
                    } else {
                        Toast.makeText(context, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
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
                if (context != null) {
                    Toast.makeText(context, context.getString(R.string.connect_server_failed), Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }
            }
        });
    }

    public interface PurchaseOrderAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mainLayout;
        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView statusTextView, amountTextView, wareHouseTextView;
        private TextView payableAmountTextView;
        private TextView createdByTextView, taxTextView, amountTaxTextView;
        private CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            mainLayout = (RelativeLayout) view.findViewById(R.id.main_layout);
            orderIdTextView = (TextView) view.findViewById(R.id.orderId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            wareHouseTextView = (TextView) view.findViewById(R.id.wareHouse_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            createdByTextView = (TextView) view.findViewById(R.id.createdBy_textView);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            amountTaxTextView = (TextView) view.findViewById(R.id.amount_tax_textView);
        }
    }

}
