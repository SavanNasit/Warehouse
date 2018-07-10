package com.accrete.sixorbit.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.accrete.sixorbit.R;
import com.accrete.sixorbit.helper.Constants;
import com.accrete.sixorbit.model.InvoiceItem;

import java.util.List;

/**
 * Created by {Anshul} on 5/6/18.
 */

public class CollectionsInvoiceItemsAdapter extends
        RecyclerView.Adapter<CollectionsInvoiceItemsAdapter.MyViewHolder> {
    private Context context;
    private List<InvoiceItem> invoiceItemList;
    private CollectionsInvoiceItemsAdapterListener listener;

    public CollectionsInvoiceItemsAdapter(Context context, List<InvoiceItem> invoiceItemList,
                                          CollectionsInvoiceItemsAdapterListener listener) {
        this.context = context;
        this.invoiceItemList = invoiceItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_collections_invoice_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        try {
            final InvoiceItem invoiceItem = invoiceItemList.get(position);
            holder.quantityTextView.setText("Qty: " + invoiceItem.getQuantity() + " " +
                    invoiceItem.getMeasurement());
            holder.productNameTextView.setText("" + invoiceItem.getItemId() + " - " +
                    invoiceItem.getItemName());
            holder.taxTextView.setText("Tax: " + Constants.roundTwoDecimals(
                    Constants.ParseDouble(invoiceItem.getTax())) + " %");
            if (invoiceItem.getDiscountType() != null && !invoiceItem.getDiscountType().isEmpty() &&
                    invoiceItem.getDiscountType().equals("2")) {
                holder.discountTextView.setText("Discount: " + context.getString(R.string.Rs)
                        + " " + Constants.roundTwoDecimals(Constants.ParseDouble(invoiceItem.getDiscount())));
            } else {
                holder.discountTextView.setText("Discount: "
                        + Constants.roundTwoDecimals(
                        Constants.ParseDouble(invoiceItem.getDiscount())) + " %");
            }
            holder.priceTextView.setText("Price: " + context.getString(R.string.Rs) + " " + Constants.roundTwoDecimals(
                    Constants.ParseDouble(invoiceItem.getPrice())));
            holder.amountTextView.setText("Amount: " + context.getString(R.string.Rs) +
                    " " + Constants.roundTwoDecimals(Constants.ParseDouble(invoiceItem.getAmount())));
            holder.subtotalTextView.setText("Subtotal Amount: " + context.getString(R.string.Rs) + " " +
                    Constants.roundTwoDecimals(Constants.ParseDouble(invoiceItem.getSubTotal())));

            if (position == 0) {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._4sdp);
                int sideMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, sideMargin, topMargin, sideMargin, 0);
            } else {
                int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
                setMargins(holder.cardView, topMargin, topMargin, topMargin, 0);
            }

            holder.installationTextView.setText("Installation ");
            holder.statusValueTextView.setBackgroundResource(R.drawable.tags_rounded_corner);

            GradientDrawable drawable = (GradientDrawable) holder.statusValueTextView.getBackground();
            holder.statusValueTextView.setText("" + invoiceItem.getInstallation());

            if (invoiceItem.getInstallation() != null && !invoiceItem.getInstallation().isEmpty()) {
                if (invoiceItem.getInstallation().toLowerCase().equals("no")) {
                    drawable.setColor(context.getResources().getColor(R.color.gray_order));
                } else {
                    drawable.setColor(context.getResources().getColor(R.color.green_purchase_order));
                }
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
        return invoiceItemList.size();

    }

    private void applyClickEvents(MyViewHolder holder, final int position) {
        listener.onMessageRowClicked(position);
    }

    public interface CollectionsInvoiceItemsAdapterListener {
        void onMessageRowClicked(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView productImage;
        private LinearLayout installationLayout;
        private TextView installationTextView;
        private TextView statusValueTextView;
        private TextView quantityTextView;
        private LinearLayout nameLayout;
        private TextView productNameTextView;
        private LinearLayout taxDiscountLayout;
        private TextView taxTextView;
        private TextView discountTextView;
        private TextView priceTextView;
        private TextView amountTextView;
        private TextView subtotalTextView;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            productImage = (ImageView) view.findViewById(R.id.product_image);
            installationLayout = (LinearLayout) view.findViewById(R.id.installation_layout);
            installationTextView = (TextView) view.findViewById(R.id.installation_textView);
            statusValueTextView = (TextView) view.findViewById(R.id.status_value_textView);
            quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            productNameTextView = (TextView) view.findViewById(R.id.product_name_textView);
            taxDiscountLayout = (LinearLayout) view.findViewById(R.id.tax_discount_layout);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            discountTextView = (TextView) view.findViewById(R.id.discount_textView);
            priceTextView = (TextView) view.findViewById(R.id.price_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            subtotalTextView = (TextView) view.findViewById(R.id.subtotal_textView);
        }
    }

}

