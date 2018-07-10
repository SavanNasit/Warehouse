package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 4/6/18.
 */

public class CollectionTransactionData {

    @SerializedName("invtid")
    @Expose
    private String invtid;
    @SerializedName("avid")
    @Expose
    private String avid;
    @SerializedName("invrid")
    @Expose
    private String invrid;
    @SerializedName("invtsid")
    @Expose
    private String invtsid;
    @SerializedName("transactionID")
    @Expose
    private String transactionID;
    @SerializedName("voucherID")
    @Expose
    private String voucherID;
    @SerializedName("referenceID")
    @Expose
    private String referenceID;
    @SerializedName("transaction_type")
    @Expose
    private String transactionType;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("transaction_mode")
    @Expose
    private String transactionMode;
    @SerializedName("invoice_transaction_data")
    @Expose
    private List<TransactionModeData> invoiceTransactionData = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("approved_user")
    @Expose
    private String approvedUser;
    @SerializedName("approved_date")
    @Expose
    private String approvedDate;
    @SerializedName("rcln_user")
    @Expose
    private String rclnUser;
    @SerializedName("rcln_date")
    @Expose
    private String rclnDate;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public List<TransactionModeData> getInvoiceTransactionData() {
        return invoiceTransactionData;
    }

    public void setInvoiceTransactionData(List<TransactionModeData> invoiceTransactionData) {
        this.invoiceTransactionData = invoiceTransactionData;
    }

    public String getInvtid() {
        return invtid;
    }

    public void setInvtid(String invtid) {
        this.invtid = invtid;
    }

    public String getAvid() {
        return avid;
    }

    public void setAvid(String avid) {
        this.avid = avid;
    }

    public String getInvrid() {
        return invrid;
    }

    public void setInvrid(String invrid) {
        this.invrid = invrid;
    }

    public String getInvtsid() {
        return invtsid;
    }

    public void setInvtsid(String invtsid) {
        this.invtsid = invtsid;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(String voucherID) {
        this.voucherID = voucherID;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApprovedUser() {
        return approvedUser;
    }

    public void setApprovedUser(String approvedUser) {
        this.approvedUser = approvedUser;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRclnUser() {
        return rclnUser;
    }

    public void setRclnUser(String rclnUser) {
        this.rclnUser = rclnUser;
    }

    public String getRclnDate() {
        return rclnDate;
    }

    public void setRclnDate(String rclnDate) {
        this.rclnDate = rclnDate;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }
}
