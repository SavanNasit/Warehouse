package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 6/11/17.
 */

public class VendorPendingInvoice {
    @SerializedName("pinvid")
    @Expose
    private String pinvid;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("pending_since")
    @Expose
    private String pendingSince;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("pending_invoice_id")
    @Expose
    private String pendingInvoiceId;

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    @SerializedName("narration")
    @Expose
    private String narration;

    public String getPendingInvoiceId() {
        return pendingInvoiceId;
    }

    public void setPendingInvoiceId(String pendingInvoiceId) {
        this.pendingInvoiceId = pendingInvoiceId;
    }

    public String getPinvid() {
        return pinvid;
    }

    public void setPinvid(String pinvid) {
        this.pinvid = pinvid;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPendingSince() {
        return pendingSince;
    }

    public void setPendingSince(String pendingSince) {
        this.pendingSince = pendingSince;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
