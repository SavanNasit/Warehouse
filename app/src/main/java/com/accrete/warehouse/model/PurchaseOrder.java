package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/18/17.
 */

public class PurchaseOrder {
    @SerializedName("purorid")
    @Expose
    private String purorid;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("vendor_name")
    @Expose
    private String vendorName;
    @SerializedName("discount_amount")
    @Expose
    private String discountAmount;
    @SerializedName("amount_after_discount")
    @Expose
    private String amountAfterDiscount;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("warehouse_name")
    @Expose
    private String warehouseName;
    @SerializedName("waid")
    @Expose
    private String waid;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("amount_after_tax")
    @Expose
    private String amountAfterTax;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("status_name")
    @Expose
    private String statusName;
    @SerializedName("purorsid")
    @Expose
    private String purorsid;
    @SerializedName("purchase_order_id")
    @Expose
    private String purchaseOrderId;

    public String getPurorid() {
        return purorid;
    }

    public void setPurorid(String purorid) {
        this.purorid = purorid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getWaid() {
        return waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getAmountAfterTax() {
        return amountAfterTax;
    }

    public void setAmountAfterTax(String amountAfterTax) {
        this.amountAfterTax = amountAfterTax;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getPurorsid() {
        return purorsid;
    }

    public void setPurorsid(String purorsid) {
        this.purorsid = purorsid;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }
}
