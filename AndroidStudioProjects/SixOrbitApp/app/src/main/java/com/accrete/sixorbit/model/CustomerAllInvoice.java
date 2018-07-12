package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 11/12/17.
 */

public class CustomerAllInvoice {
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("c_transaction_mode_name")
    @Expose
    private String transactionModeName;
    @SerializedName("narration")
    @Expose
    private String narration;

    public String getTransactionModeName() {
        return transactionModeName;
    }

    public void setTransactionModeName(String transactionModeName) {
        this.transactionModeName = transactionModeName;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
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

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
