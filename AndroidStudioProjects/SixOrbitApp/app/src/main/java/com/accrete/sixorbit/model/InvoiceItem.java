package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 5/6/18.
 */

public class InvoiceItem {

    @SerializedName("inviid")
    @Expose
    private String inviid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("variationID")
    @Expose
    private String variationID;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("measurement")
    @Expose
    private String measurement;
    @SerializedName("installation")
    @Expose
    private String installation;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("sub_total")
    @Expose
    private String subTotal;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("discount_type")
    @Expose
    private String discountType;

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInviid() {
        return inviid;
    }

    public void setInviid(String inviid) {
        this.inviid = inviid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getVariationID() {
        return variationID;
    }

    public void setVariationID(String variationID) {
        this.variationID = variationID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getInstallation() {
        return installation;
    }

    public void setInstallation(String installation) {
        this.installation = installation;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
}
