package com.accrete.warehouse.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.accrete.warehouse.R;
import com.accrete.warehouse.model.PurchaseOrder;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by poonam on 12/18/17.
 */

public class PurchaseOrderAdapter extends RecyclerView.Adapter<PurchaseOrderAdapter.MyViewHolder> {
    private Activity activity;
    private List<PurchaseOrder> purchaseOrderList;
    private String venId;
    private SimpleDateFormat simpleDateFormat;
    private PurchaseOrderAdapterListener listener;
    private Typeface fontAwesomeFont;

    public PurchaseOrderAdapter(Activity activity, List<PurchaseOrder> purchaseOrderList,
                                PurchaseOrderAdapterListener listener) {
        this.activity = activity;
        this.purchaseOrderList = purchaseOrderList;
        this.venId = venId;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_receive_against_purchase_order, parent, false);
        return new MyViewHolder(itemView);
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

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final PurchaseOrder purchaseOrder = purchaseOrderList.get(position);

        if (purchaseOrder.getPurchaseOrderId() != null && !purchaseOrder.getPurchaseOrderId().isEmpty()) {
            holder.orderIdTextView.setText("PO : " + purchaseOrder.getPurchaseOrderId());
            holder.orderIdTextView.setMovementMethod(LinkMovementMethod.getInstance());
            holder.orderIdTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        } else {
            holder.orderIdTextView.setText("NA");
        }


        if (purchaseOrder.getVendorName() != null && !purchaseOrder.getVendorName().isEmpty()) {
            holder.vendorNameTextView.setText(capitalize(purchaseOrder.getVendorName()));
        }

        if (purchaseOrder.getCreatedBy() != null && !purchaseOrder.getCreatedBy().isEmpty()) {
            holder.createdByTextView.setText("Created by :"+capitalize(purchaseOrder.getCreatedBy()));
            holder.createdByTextView.setVisibility(View.VISIBLE);
        }

        holder.wareHouseTextView.setText(purchaseOrder.getWarehouseName());
        holder.wareHouseTextView.setVisibility(View.GONE);

        double amount = ParseDouble(purchaseOrder.getAmount());
        double tax = ParseDouble(purchaseOrder.getTax());
        double amountAfterTax = ParseDouble(purchaseOrder.getAmountAfterTax());
        double payableAmount = ParseDouble(purchaseOrder.getPayableAmount());
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");

        //  holder.amountTextView.setText("Amount\n" + activity.getString(R.string.Rs) + " " + formatter.format(amount));
        //  holder.taxTextView.setText("Tax\n" + activity.getString(R.string.Rs) + " " + formatter.format(tax));
        //  holder.amountTaxTextView.setText("After Tax\n" + activity.getString(R.string.Rs) + " " + formatter.format(amountAfterTax));
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
        holder.textViewReceive.setBackgroundResource(R.drawable.tags_rounded_corner);

        GradientDrawable drawable = (GradientDrawable) holder.statusTextView.getBackground();
        if (purchaseOrder.getPurorsid().equals("1")) {
            drawable.setColor(activity.getResources().getColor(R.color.green_purchase_order));
            holder.statusTextView.setText("Created");
            holder.textViewReceive.setEnabled(true);
            holder.textViewReceive.setVisibility(View.VISIBLE);
            holder.isSwipeToRecord = true;
        } else if (purchaseOrder.getPurorsid().equals("2")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Partial Received");
            holder.textViewReceive.setEnabled(true);
            holder.textViewReceive.setVisibility(View.VISIBLE);
            holder.isSwipeToRecord = true;
        } else if (purchaseOrder.getPurorsid().equals("3")) {
            drawable.setColor(activity.getResources().getColor(R.color.blue_purchase_order));
            holder.statusTextView.setText("Received");
            holder.textViewReceive.setEnabled(false);
            holder.textViewReceive.setVisibility(View.GONE);
            holder.viewBackground.setVisibility(View.GONE);
            holder.isSwipeToRecord = false;

        } else if (purchaseOrder.getPurorsid().equals("4")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Cancelled");
            holder.textViewReceive.setEnabled(false);
            holder.textViewReceive.setVisibility(View.GONE);
            holder.isSwipeToRecord = true;
        } else if (purchaseOrder.getPurorsid().equals("5")) {
            drawable.setColor(activity.getResources().getColor(R.color.red_purchase_order));
            holder.statusTextView.setText("Closed");
            holder.textViewReceive.setEnabled(false);
            holder.textViewReceive.setVisibility(View.GONE);
            holder.isSwipeToRecord = true;

        } else if (purchaseOrder.getPurorsid().equals("6")) {
            drawable.setColor(activity.getResources().getColor(R.color.orange_purchase_order));
            holder.statusTextView.setText("Pending");
            holder.textViewReceive.setEnabled(false);
            holder.textViewReceive.setVisibility(View.GONE);
            holder.isSwipeToRecord = true;

        } else if (purchaseOrder.getPurorsid().equals("7")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Expected Delivery");
            holder.textViewReceive.setEnabled(true);
            holder.textViewReceive.setVisibility(View.VISIBLE);
            holder.isSwipeToRecord = true;

        } else if (purchaseOrder.getPurorsid().equals("8")) {
            drawable.setColor(activity.getResources().getColor(R.color.gray_order));
            holder.statusTextView.setText("Pending Transportation");
            holder.textViewReceive.setEnabled(false);
            holder.textViewReceive.setVisibility(View.GONE);
            holder.isSwipeToRecord = true;

        }

        //Receive
        GradientDrawable drawableReceive = (GradientDrawable) holder.textViewReceive.getBackground();
        drawableReceive.setColor(Color.TRANSPARENT);

       /* holder.textViewReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, POReceiveConsignmentActivity.class);
                intent.putExtra(activity.getString(R.string.purOrId), purchaseOrder.getPurorid());
                activity.startActivity(intent);
            }
        });*/
        //Click Item
        applyClickEvents(holder, position, purchaseOrder.getOrderId(), purchaseOrder.getPurchaseOrderId());
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

    public interface PurchaseOrderAdapterListener {
        void onMessageRowClicked(int position, String orderId, String orderText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayoutContainer;
        RelativeLayout viewBackground;
        private LinearLayout mainLayout;
        private TextView orderIdTextView;
        private TextView dateTextView;
        private TextView statusTextView, wareHouseTextView, vendorNameTextView;
        private TextView payableAmountTextView;
        //private TextView amountTextView, taxTextView,amountTaxTextView;
        private TextView createdByTextView, textViewReceive;
        public boolean isSwipeToRecord;

        public MyViewHolder(View view) {
            super(view);
            mainLayout = (LinearLayout) view.findViewById(R.id.child_layout);
            orderIdTextView = (TextView) view.findViewById(R.id.orderId_textView);
            dateTextView = (TextView) view.findViewById(R.id.date_textView);
            statusTextView = (TextView) view.findViewById(R.id.status_textView);
            //  amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            wareHouseTextView = (TextView) view.findViewById(R.id.wareHouse_textView);
            payableAmountTextView = (TextView) view.findViewById(R.id.payable_amount_textView);
            createdByTextView = (TextView) view.findViewById(R.id.createdBy_textView);
            //  taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            // amountTaxTextView = (TextView) view.findViewById(R.id.amount_tax_textView);
            textViewReceive = (TextView) view.findViewById(R.id.textView_receive);
            vendorNameTextView = (TextView) view.findViewById(R.id.vendorName_textView);

            fontAwesomeFont = Typeface.createFromAsset(activity.getAssets(), "font/fontawesome-webfont.ttf");
            textViewReceive.setTypeface(fontAwesomeFont);
            viewBackground = (RelativeLayout) view.findViewById(R.id.view_background);
            relativeLayoutContainer = (RelativeLayout) view.findViewById(R.id.relativelayout_container);
        }
    }
}