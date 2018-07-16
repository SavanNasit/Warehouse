package com.accrete.warehouse.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.ApiResponse;
import com.accrete.warehouse.model.Consignment;
import com.accrete.warehouse.rest.ApiClient;
import com.accrete.warehouse.rest.ApiInterface;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;
import com.accrete.warehouse.utils.NetworkUtil;
import com.accrete.warehouse.utils.SwipeRevealLayout;
import com.accrete.warehouse.utils.ViewBinderHelper;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.accrete.warehouse.utils.Constants.accessToken;
import static com.accrete.warehouse.utils.Constants.key;
import static com.accrete.warehouse.utils.Constants.task;
import static com.accrete.warehouse.utils.Constants.userId;
import static com.accrete.warehouse.utils.Constants.version;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignmentAdapter extends RecyclerView.Adapter<ManageConsignmentAdapter.MyViewHolder> {
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();
    private Context context;
    private List<Consignment> consignmentList;
    private ManageConsignmentAdapterListener listener;

    public ManageConsignmentAdapter(Context context, List<Consignment> consignmentList, ManageConsignmentAdapterListener listener) {
        this.context = context;
        this.consignmentList = consignmentList;
        this.listener = listener;
        binderHelper.setOpenOnlyOne(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_manage_consignment, parent, false);
        return new MyViewHolder(itemView);
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        binderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");
        DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");

        final Consignment manageConsignment = consignmentList.get(position);
        holder.listRowManageConsignmentConsignmentId.setText("ID : " + manageConsignment.getConsignmentId());
        holder.listRowManageConsignmentConsignmentId.setPaintFlags(holder.listRowManageConsignmentConsignmentId.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (manageConsignment.getPurchaseOrderID() != null && !manageConsignment.getPurchaseOrderID().isEmpty()) {
            holder.listRowManageConsignmentPurchaseOrder.setText("PO : " + manageConsignment.getPurchaseOrderID());
        } else {
            holder.listRowManageConsignmentPurchaseOrder.setVisibility(View.GONE);
        }

        if (manageConsignment.getPurchaseNumber() != null && !manageConsignment.getPurchaseNumber().isEmpty()
                && !manageConsignment.getPurchaseNumber().equals("NA")) {
            holder.listRowManageConsignmentInvoiceNumber.setText("Invoice : " + manageConsignment.getPurchaseNumber());
        } else {
            holder.listRowManageConsignmentInvoiceNumber.setText("Invoice : " + "NA");
        }
        //  holder.listRowManageConsignmentInvoiceDate.setText(manageConsignment.getInvoiceDate());
        //  holder.listRowManageConsignmentPurchaseOrderDate.setText(manageConsignment.getPurchaseOrderDate());
        if (manageConsignment.getVendor() != null && !manageConsignment.getVendor().isEmpty()) {
            if (manageConsignment.getVendor().length() > 25) {
                holder.listRowManageConsignmentVendor.setText(capitalize(manageConsignment.getVendor().substring(0, 25) + "..."));
            } else {
                holder.listRowManageConsignmentVendor.setText(capitalize(manageConsignment.getVendor()));
            }
            holder.listRowManageConsignmentVendor.setVisibility(View.VISIBLE);
        } else {
            holder.listRowManageConsignmentVendor.setVisibility(View.INVISIBLE);
        }
        // holder.listRowManageConsignmentWarehouse.setText(manageConsignment.getWarehouse());

        try {
            holder.listRowManageConsignmentReceivedOn.setText("Received on: " + (outputFormat.format(simpleDateFormat.parse(
                    manageConsignment.getCreatedTs()))) + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //Statuses
        holder.listRowManageConsignmentStatus.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.listRowManageConsignmentStatus.getBackground();
        if (manageConsignment.getIscsid() != null) {
            if (manageConsignment.getIscsid().equals("1")) {
                drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Active");
            } else if (manageConsignment.getIscsid().equals("2")) {
                drawable.setColor(context.getResources().getColor(R.color.red_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Inactive");
            } else if (manageConsignment.getIscsid().equals("3")) {
                drawable.setColor(context.getResources().getColor(R.color.gray_order));
                holder.listRowManageConsignmentStatus.setText("Delete");
            } else if (manageConsignment.getIscsid().equals("4")) {
                drawable.setColor(context.getResources().getColor(R.color.gray_order));
                holder.listRowManageConsignmentStatus.setText("Freezed");
            } else if (manageConsignment.getIscsid().equals("5")) {
                drawable.setColor(context.getResources().getColor(R.color.blue_purchase_order));
                holder.listRowManageConsignmentStatus.setText("Payment Approved");
            } else if (manageConsignment.getIscsid().equals("6")) {
                drawable.setColor(context.getResources().getColor(R.color.orange_to_be_approved));
                holder.listRowManageConsignmentStatus.setText("To be Approved");
            }
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyClickEvents(position);
            }
        });

        holder.deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manageConsignment.getIsDeleteConsignmentEnable() != null &&
                        !manageConsignment.getIsDeleteConsignmentEnable().isEmpty() &&
                        manageConsignment.getIsDeleteConsignmentEnable().equals("1")) {
                    dialogConfirmDeleteConsignment(context, manageConsignment.getIscid());
                }
            }
        });

    }

    private void dialogConfirmDeleteConsignment(Context activity, final String iscId) {
        try {
            View dialogView = View.inflate(activity, R.layout.dialog_gatepass_authentication, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setView(dialogView)
                    .setCancelable(true);
            final AlertDialog dialogConfirmGatepass = builder.create();
            dialogConfirmGatepass.setCanceledOnTouchOutside(true);
            LinearLayout linearLayout;
            TextView dialogGatepassAuthenticationTitle;
            AutoCompleteTextView dialogGatepassAuthenticationDeliveryUser;
            Button dialogGatepassAuthenticationConfirm;
            final ProgressBar cancelGatepassProgressBar;
            Button dialogGatepassAuthenticationCancel;

            linearLayout = (LinearLayout) dialogView.findViewById(R.id.linearLayout);
            dialogGatepassAuthenticationTitle = (TextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_title);
            dialogGatepassAuthenticationDeliveryUser = (AutoCompleteTextView) dialogView.findViewById(R.id.dialog_gatepass_authentication_delivery_user);
            dialogGatepassAuthenticationConfirm = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_confirm);
            cancelGatepassProgressBar = (ProgressBar) dialogView.findViewById(R.id.cancel_gatepass_progress_bar);
            dialogGatepassAuthenticationCancel = (Button) dialogView.findViewById(R.id.dialog_gatepass_authentication_cancel);

            dialogGatepassAuthenticationTitle.setText("Are you sure want to delete this consignment?");
            dialogGatepassAuthenticationDeliveryUser.setVisibility(View.GONE);
            dialogGatepassAuthenticationDeliveryUser.setEnabled(false);

            dialogGatepassAuthenticationConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkUtil.getConnectivityStatusString(
                            context).equals(context.getString(R.string.not_connected_to_internet))) {
                        deleteConsignment(context, dialogConfirmGatepass, iscId, cancelGatepassProgressBar);
                    } else {
                        Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            dialogGatepassAuthenticationCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogConfirmGatepass.dismiss();
                }
            });

            dialogConfirmGatepass.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            if (!dialogConfirmGatepass.isShowing()) {
                dialogConfirmGatepass.show();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return consignmentList.size();
    }

    private void applyClickEvents(final int position) {
        listener.onMessageRowClicked(position);
    }

    private void deleteConsignment(final Context mContext, final AlertDialog alertDialog, String iscId,
                                   final ProgressBar progressBar) {
        try {
            task = mContext.getString(R.string.delete_consignment_task);
            if (AppPreferences.getIsLogin(mContext, AppUtils.ISLOGIN)) {
                userId = AppPreferences.getUserId(mContext, AppUtils.USER_ID);
                accessToken = AppPreferences.getAccessToken(mContext, AppUtils.ACCESS_TOKEN);
                ApiClient.BASE_URL = AppPreferences.getLastDomain(mContext, AppUtils.DOMAIN);
            }

            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ApiResponse> call = apiService.deleteConsignment(version, key, task, userId,
                    accessToken, iscId);
            Log.d("Request", String.valueOf(call));
            Log.d("url", String.valueOf(call.request().url()));
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call call, Response response) {
                    Log.d("Response", String.valueOf(new GsonBuilder().setPrettyPrinting().create().toJson(response.body())));
                    final ApiResponse apiResponse = (ApiResponse) response.body();
                    try {
                        if (apiResponse.getSuccess()) {
                            Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
                            listener.refreshData();
                            if (alertDialog != null && alertDialog.isShowing()) {
                                alertDialog.dismiss();
                            }
                        } else {
                            Toast.makeText(mContext, apiResponse.getMessage(), Toast.LENGTH_LONG).show();
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
                    if (mContext != null) {
                        Toast.makeText(mContext, context.getString(R.string.error_domain), Toast.LENGTH_LONG).show();
                        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
            }
        }
    }

    public interface ManageConsignmentAdapterListener {
        void onMessageRowClicked(int position);

        void refreshData();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView listRowManageConsignmentConsignmentId;
        private TextView listRowManageConsignmentPurchaseOrder;
        private TextView listRowManageConsignmentInvoiceNumber;
        // private TextView listRowManageConsignmentInvoiceDate;
        // private TextView listRowManageConsignmentPurchaseOrderDate;
        private TextView listRowManageConsignmentVendor;
        //   private TextView listRowManageConsignmentWarehouse;
        private TextView listRowManageConsignmentReceivedOn;
        private TextView listRowManageConsignmentStatus;
        private LinearLayout layout;
        private SwipeRevealLayout swipeRevealLayout;
        private ImageView deleteImageView;

        public MyViewHolder(View view) {
            super(view);
            swipeRevealLayout = (SwipeRevealLayout) view.findViewById(R.id.swipe_layout);
            layout = (LinearLayout) view.findViewById(R.id.linear_layout_manage_consignment);
            listRowManageConsignmentConsignmentId = (TextView) view.findViewById(R.id.list_row_manage_consignment_consignment_id);
            listRowManageConsignmentPurchaseOrder = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order);
            listRowManageConsignmentInvoiceNumber = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_number);
            //   listRowManageConsignmentInvoiceDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_invoice_date);
            //   listRowManageConsignmentPurchaseOrderDate = (TextView) view.findViewById(R.id.list_row_manage_consignment_purchase_order_date);
            listRowManageConsignmentVendor = (TextView) view.findViewById(R.id.list_row_manage_consignment_vendor);
            //   listRowManageConsignmentWarehouse = (TextView) view.findViewById(R.id.list_row_manage_consignment_warehouse);
            listRowManageConsignmentReceivedOn = (TextView) view.findViewById(R.id.list_row_manage_consignment_received_on);
            listRowManageConsignmentStatus = (TextView) view.findViewById(R.id.list_row_manage_consignment_status);
            deleteImageView = (ImageView) view.findViewById(R.id.img_delete);
        }
    }


}
