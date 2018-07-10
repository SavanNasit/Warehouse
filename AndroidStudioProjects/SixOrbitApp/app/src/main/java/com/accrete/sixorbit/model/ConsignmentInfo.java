package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 14/12/17.
 */

public class ConsignmentInfo {
    @SerializedName("consignment_id")
    @Expose
    private String consignmentId;
    @SerializedName("purchase_order")
    @Expose
    private String purchaseOrder;
    @SerializedName("purchased_on")
    @Expose
    private String purchasedOn;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("vendor")
    @Expose
    private String vendor;
    @SerializedName("warehouse")
    @Expose
    private String warehouse;
    @SerializedName("received_on")
    @Expose
    private String receivedOn;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("authorized_by")
    @Expose
    private String authorizedBy;
    @SerializedName("purchase_date")
    @Expose
    private String purchase_date;

    public String getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(String purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public String getConsignmentId() {
        return consignmentId;
    }

    public void setConsignmentId(String consignmentId) {
        this.consignmentId = consignmentId;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(String purchasedOn) {
        this.purchasedOn = purchasedOn;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(String receivedOn) {
        this.receivedOn = receivedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
