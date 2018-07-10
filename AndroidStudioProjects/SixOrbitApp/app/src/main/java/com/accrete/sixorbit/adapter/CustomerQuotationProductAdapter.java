package com.accrete.sixorbit.adapter;

import android.content.Context;
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
import com.accrete.sixorbit.model.QuotationProduct;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

import static com.accrete.sixorbit.helper.Constants.ParseDouble;
import static com.accrete.sixorbit.helper.Constants.removeComma;
import static com.accrete.sixorbit.helper.Constants.roundTwoDecimals;

/**
 * Created by agt on 14/12/17.
 */

public class CustomerQuotationProductAdapter extends RecyclerView.Adapter<CustomerQuotationProductAdapter.MyViewHolder> {
    private Context context;
    private List<QuotationProduct> quotationProductList;
    private String cuId;
    private QuotationProductAdapterListener listener;

    public CustomerQuotationProductAdapter(Context context, List<QuotationProduct> quotationProductList, String cuId,
                                           QuotationProductAdapterListener listener) {
        this.context = context;
        this.quotationProductList = quotationProductList;
        this.cuId = cuId;
        this.listener = listener;
    }

    @Override
    public CustomerQuotationProductAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_quotation_add_item, parent, false);
        return new CustomerQuotationProductAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final QuotationProduct itemData = quotationProductList.get(position);
        DecimalFormat formatter = new DecimalFormat("#,##,##,##,###.##");
        if (itemData.getName() != null && !itemData.getName().isEmpty()) {
            holder.productNameTextView.setText(itemData.getName());
        }

        if (itemData.getMeaid() != null && !itemData.getMeaid().isEmpty() && Integer.parseInt(itemData.getMeaid()) > 0) {
            holder.quantityTextView.setText("Qty: " + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                    Constants.removeComma(itemData.getQuantity())))).setScale(2, RoundingMode.HALF_UP).toPlainString()
                    + " " + itemData.getMeasurementName());
        } else {
            holder.quantityTextView.setText("Qty: " + new BigDecimal(Constants.roundTwoDecimals(Constants.ParseDouble(
                    Constants.removeComma(itemData.getQuantity())))).setScale(0, RoundingMode.HALF_UP).toPlainString()
                    + " " + itemData.getMeasurementName());
        }
        if (itemData.getDiscountType() != null && itemData.getDiscountType().equals("1")) {
            holder.discountTextView.setText("Discount: " + Constants.roundTwoDecimals(
                    Constants.ParseDouble(itemData.getDiscount())) + " " + "%");
        } else {
            holder.discountTextView.setText("Discount: " + Constants.roundTwoDecimals(
                    Constants.ParseDouble(itemData.getDiscount()))
                    + " " + "INR");
        }
        if (itemData.getTaxName() == null || itemData.getTaxName().isEmpty()) {
            holder.taxTextView.setText("No tax");
        } else {
            if (itemData.getTaxName() != null && !itemData.getTaxName().toLowerCase().equals("null")) {
                holder.taxTextView.setText("Tax: " + itemData.getTaxName());
                holder.taxTextView.setVisibility(View.VISIBLE);
            } else {
                holder.taxTextView.setVisibility(View.GONE);
            }
        }
        holder.priceTextView.setText("Price: " + itemData.getPrice());
        holder.priceTextView.setVisibility(View.GONE);
        if (itemData.getItemAmount() != null) {
            holder.amountTextView.setText("Amount: " + formatter.format(Double.parseDouble(itemData.getItemAmount())));
        }
        holder.amountTextView.setVisibility(View.GONE);


        if (itemData.getDiscountType() != null && itemData.getDiscountType().equals("1")) {
            //TODO - Updated On 10th May
            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                holder.subtotalTextView.setText("Subtotal: " +
                        new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100)
                                +
                                ((((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                        ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                        (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100) *
                                        ParseDouble(itemData.getItemTax())) / 100)
                                -
                                ((((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                        ParseDouble(removeComma(itemData.getQuantity().toString())) *
                                        (100.00 - ParseDouble(removeComma(itemData.getDiscount().toString())))) / 100) *
                                        ParseDouble(itemData.getItemTax())) / 100)))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());

            } else {
                if (itemData.getSubtotal() != null) {
                    holder.subtotalTextView.setText("Subtotal: " + formatter.format(Double.parseDouble(itemData.getSubtotal())));
                }
            }
        } else if (itemData.getDiscountType() != null && itemData.getDiscountType().equals("2")) {
            //TODO - Updated On 10th May
            if (itemData.isPriceIncludeTaxShow() && itemData.getButapid() != null &&
                    !itemData.getButapid().isEmpty() && !itemData.getButapid().equals("0")) {
                holder.subtotalTextView.setText("Subtotal: " +
                        new BigDecimal(roundTwoDecimals(((ParseDouble(removeComma(itemData.getPrice().toString())) *
                                ParseDouble(removeComma(itemData.getQuantity().toString()))) -
                                ParseDouble(removeComma(itemData.getDiscount().toString())))))
                                .setScale(2, RoundingMode.HALF_UP).toPlainString());
            } else {
                if (itemData.getSubtotal() != null) {
                    holder.subtotalTextView.setText("Subtotal: " + formatter.format(Double.parseDouble(itemData.getSubtotal())));
                }
            }
        } else {
            if (itemData.getSubtotal() != null) {
                holder.subtotalTextView.setText("Subtotal: " + formatter.format(Double.parseDouble(itemData.getSubtotal())));
            }
        }


        if (itemData.getUnitName() != null && !itemData.getUnitName().toString().trim().isEmpty()) {
            holder.boxQuantityTextView.setText("Box Qty: " + Constants.roundTwoDecimals(Double.parseDouble(itemData.getBoxQty().toString()) *
                    (Double.parseDouble(itemData.getQuantity()))) + "\n(" + Constants.roundTwoDecimals(Double.parseDouble(itemData.getBoxQty().toString()))
                    + " " + itemData.getUnitName() + "/PCS" + ")");
        } else {
            holder.boxQuantityTextView.setText("Box Qty: " + Constants.roundTwoDecimals(Double.parseDouble(itemData.getBoxQty().toString()) *
                    (Double.parseDouble(itemData.getQuantity()))) + "");
        }

        if (itemData.getImage() != null && !itemData.getImage().isEmpty()) {
            holder.productImage.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(itemData.getImage())
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.productImage);
        } else {
            holder.productImage.setVisibility(View.GONE);
        }

        if (position == 0) {
            int topMargin = context.getResources().getDimensionPixelOffset(R.dimen._6sdp);
            setMargins(holder.cardView, 0, topMargin, 0, 0);
        } else if (position == quotationProductList.size() - 1) {
            int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
            setMargins(holder.cardView, 0, topMargin, 0, topMargin);
        } else {
            int topMargin = context.getResources().getDimensionPixelSize(R.dimen._8sdp);
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
        return quotationProductList.size();
    }

    public interface QuotationProductAdapterListener {
        void onMessageRowClicked(int position, String qoId, String qoText);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView productImage;
        private LinearLayout nameLayout;
        private TextView productNameTextView;
        private TextView boxQuantityTextView;
        private TextView quantityTextView;
        private LinearLayout taxDiscountLayout;
        private TextView taxTextView;
        private TextView discountTextView;
        private TextView priceTextView;
        private TextView amountTextView;
        private TextView subtotalTextView;
        private LinearLayout expandViewLayout;
        private LinearLayout editLayout;
        private ImageView imgEdit;
        private LinearLayout removeLayout;

        public MyViewHolder(View view) {
            super(view);
            cardView = (CardView) view.findViewById(R.id.card_view);
            productImage = (ImageView) view.findViewById(R.id.product_image);
            nameLayout = (LinearLayout) view.findViewById(R.id.name_layout);
            productNameTextView = (TextView) view.findViewById(R.id.product_name_textView);
            boxQuantityTextView = (TextView) view.findViewById(R.id.box_quantity_textView);
            quantityTextView = (TextView) view.findViewById(R.id.quantity_textView);
            taxDiscountLayout = (LinearLayout) view.findViewById(R.id.tax_discount_layout);
            taxTextView = (TextView) view.findViewById(R.id.tax_textView);
            discountTextView = (TextView) view.findViewById(R.id.discount_textView);
            priceTextView = (TextView) view.findViewById(R.id.price_textView);
            amountTextView = (TextView) view.findViewById(R.id.amount_textView);
            subtotalTextView = (TextView) view.findViewById(R.id.subtotal_textView);
            expandViewLayout = (LinearLayout) view.findViewById(R.id.expand_viewLayout);
            editLayout = (LinearLayout) view.findViewById(R.id.edit_layout);
            imgEdit = (ImageView) view.findViewById(R.id.imgEdit);
            removeLayout = (LinearLayout) view.findViewById(R.id.remove_layout);

            expandViewLayout.setVisibility(View.GONE);
        }
    }
}
