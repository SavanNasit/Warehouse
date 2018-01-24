package com.accrete.warehouse.adapter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accrete.warehouse.CustomerDetailsActivity;
import com.accrete.warehouse.R;
import com.accrete.warehouse.fragment.runningorders.RunningOrdersFragment;
import com.accrete.warehouse.model.Packages;
import com.accrete.warehouse.model.PendingItems;
import com.accrete.warehouse.model.RunningOrder;
import com.accrete.warehouse.utils.AppPreferences;
import com.accrete.warehouse.utils.AppUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.accrete.warehouse.utils.MSupportConstants.REQUEST_CODE_FOR_RUNNING_ORDER_CALL_PERMISSIONS;
import static com.accrete.warehouse.utils.PersmissionConstant.checkPermissionWithRationale;

/**
 * Created by poonam on 11/27/17.
 */

public class RunningOrdersAdapter extends RecyclerView.Adapter<RunningOrdersAdapter.MyViewHolder> {
    Activity activity;
    private Context context;
    private List<RunningOrder> runningOrderList;
    private int mExpandedPosition = -1;
    private RunningOrdersAdapterListener listener;


    public RunningOrdersAdapter(Context context, List<RunningOrder> ordersAdapterList, RunningOrdersAdapterListener listener) {
        this.context = context;
        this.runningOrderList = ordersAdapterList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_running_orders, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final RunningOrder runningOrder = runningOrderList.get(position);
        holder.listRowRunningOrdersOrderId.setText(AppPreferences.getCompanyCode(context, AppUtils.COMPANY_CODE)+runningOrder.getChkoid());
        holder.listRowRunningOrdersCustomer.setText(runningOrder.getCustomer());

        holder.listRowRunningOrdersMobile.setText(runningOrder.getContact());
        holder.listRowRunningOrdersEmail.setText(runningOrder.getCustomerInfo().getEmail());
        holder.listRowRunningOrdersExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onExecute(runningOrder.getPackages(), runningOrder.getSelectOrderItems(), runningOrder.getChkid(), runningOrder.getChkoid());
            }
        });

     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            DateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = simpleDateFormat.parse(runningOrder.getDate());
            holder.listRowRunningOrdersDate.setText(outputFormat.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.imageViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runningOrderList.get(position).getCustomerInfo().getEmail() == null) {
                    Toast.makeText(context, context.getString(R.string.email_error), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("contactPersonEmail", runningOrderList.get(position).getCustomerInfo().getEmail() + "");
                    Intent email = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            context.getString(R.string.mail_to), runningOrderList.get(position).getCustomerInfo().getEmail(), null));
                    email.putExtra(Intent.EXTRA_SUBJECT, "");
                    email.putExtra(Intent.EXTRA_TEXT, "");
                    context.startActivity(Intent.createChooser(email, context.getString(R.string.choose_email_client)));
                }

            }
        });

        holder.imageViewCustomerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCustomerDetails = new Intent(context, CustomerDetailsActivity.class);
                intentCustomerDetails.putExtra("customerInfo", runningOrder.getCustomerInfo());
                context.startActivity(intentCustomerDetails);
            }
        });
        holder.imageViewMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (runningOrder.getContact().isEmpty() || runningOrder.getContact() == null) {
                    Snackbar snackbar = Snackbar
                            .make(holder.imageViewMobile, context.getString(R.string.phone_number_not_valid_error), Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    snackbarView.setBackgroundResource(R.color.red);
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.WHITE);
                    snackbar.show();

                } else {
                    String runningOrderMobile = runningOrder.getContact();
                    //   dtInterface.sendMobileNumber(runningOrderMobile);
                    listener.onCall(runningOrder.getContact());
                    Intent intentCall = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + runningOrder.getContact()));
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkPermissionWithRationale((Activity) context, new RunningOrdersFragment(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_FOR_RUNNING_ORDER_CALL_PERMISSIONS)) {
                            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                // TODO: Consider calling
                                //    ActivityCompat#requestPermissions
                                // here to request the missing permissions, and then overriding
                                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                //                                          int[] grantResults)
                                // to handle the case where the user grants the permission. See the documentation
                                // for ActivityCompat#requestPermissions for more details.
                                return;
                            }
                            //   mContext.startActivity(intentCall);
                        }
                    } else {
                        if (runningOrderMobile != null && !runningOrderMobile.isEmpty()) {
                            context.startActivity(intentCall);
                        } else {
                            Toast.makeText(context, context.getString(R.string.phone_number_not_valid_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                    holder.imageViewMobile.setEnabled(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            holder.imageViewMobile.setEnabled(true);
                        }
                    }, 2000);
                }
            }
        });
        applyClickEvents(holder, position);
    }

    @Override
    public int getItemCount() {
        return runningOrderList.size();
    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        final boolean isExpanded = position == mExpandedPosition;
        holder.frameLayoutRunningOrders.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExpandedPosition = isExpanded ? -1 : position;
                // collapse any currently expanded items
                if (mExpandedPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(mExpandedPosition);
                }

                listener.onMessageRowClicked(position);
                //  notifyDataSetChanged();

            }
        });
    }

    public interface RunningOrdersAdapterListener {
        void onMessageRowClicked(int position);

        void onExecute(List<Packages> packages, List<PendingItems> selectOrderItems, String chkid, String chkoid);

        void onCall(String contact);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView listRowRunningOrdersOrderId;
        private TextView listRowRunningOrdersCustomer;
        private TextView listRowRunningOrdersDate;
        private TextView listRowRunningOrdersMobile;
        private TextView listRowRunningOrdersEmail;
        private LinearLayout listRowRunningOrdersExecute;
        private ImageView imageViewCustomerInfo;
        private ImageView imageViewMobile, imageViewEmail;
        private FrameLayout frameLayoutRunningOrders;


        public MyViewHolder(View view) {
            super(view);
            listRowRunningOrdersOrderId = (TextView) view.findViewById(R.id.list_row_running_orders_order_id);
            listRowRunningOrdersCustomer = (TextView) view.findViewById(R.id.list_row_running_orders_customer);
            listRowRunningOrdersDate = (TextView) view.findViewById(R.id.list_row_running_orders_date);
            frameLayoutRunningOrders = (FrameLayout) view.findViewById(R.id.frame_container_running_orders);
            listRowRunningOrdersMobile = (TextView) view.findViewById(R.id.list_row_running_orders_customer_mobile);
            listRowRunningOrdersExecute = (LinearLayout) view.findViewById(R.id.running_orders_execute);
            listRowRunningOrdersEmail = (TextView) view.findViewById(R.id.list_row_running_orders_customer_email);
            imageViewCustomerInfo = (ImageView) view.findViewById(R.id.running_orders_info);
            imageViewMobile = (ImageView) view.findViewById(R.id.running_orders_call);
            imageViewEmail = (ImageView) view.findViewById(R.id.running_orders_email);
        }
    }

}
