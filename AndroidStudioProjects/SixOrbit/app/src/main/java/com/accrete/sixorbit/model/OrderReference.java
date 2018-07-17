package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 7/6/18.
 */

public class OrderReference {
    @SerializedName("invrid")
    @Expose
    private String invrid;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("avid")
    @Expose
    private String avid;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("jocaid")
    @Expose
    private String jocaid;
    @SerializedName("invoiceReferenceID")
    @Expose
    private String invoiceReferenceID;
    @SerializedName("transactionID")
    @Expose
    private String transactionID;
    @SerializedName("voucherID")
    @Expose
    private String voucherID;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("jobcardID")
    @Expose
    private String jobcardID;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("used_amount")
    @Expose
    private String usedAmount;
    @SerializedName("pending_amount")
    @Expose
    private String pendingAmount;
    @SerializedName("balance_amount")
    @Expose
    private String balanceAmount;
    @SerializedName("transaction_mode_name")
    @Expose
    private String transactionModeName;
    @SerializedName("invoice_reference_data")
    @Expose
    private String invoiceReferenceData = null;
    @SerializedName("transaction_data")
    @Expose
    private List<TransactionModeData> transactionData = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("reference_date")
    @Expose
    private String referenceDate;
    @SerializedName("narration")
    @Expose
    private String narration;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("invrsid")
    @Expose
    private String invrsid;

    public String getInvrsid() {
        return invrsid;
    }

    public void setInvrsid(String invrsid) {
        this.invrsid = invrsid;
    }

    public String getInvoiceReferenceData() {
        return invoiceReferenceData;
    }

    public void setInvoiceReferenceData(String invoiceReferenceData) {
        this.invoiceReferenceData = invoiceReferenceData;
    }

    public List<TransactionModeData> getTransactionData() {

        return transactionData;
    }

    public void setTransactionData(List<TransactionModeData> transactionData) {
        this.transactionData = transactionData;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInvrid() {
        return invrid;
    }

    public void setInvrid(String invrid) {
        this.invrid = invrid;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getAvid() {
        return avid;
    }

    public void setAvid(String avid) {
        this.avid = avid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getJocaid() {
        return jocaid;
    }

    public void setJocaid(String jocaid) {
        this.jocaid = jocaid;
    }

    public String getInvoiceReferenceID() {
        return invoiceReferenceID;
    }

    public void setInvoiceReferenceID(String invoiceReferenceID) {
        this.invoiceReferenceID = invoiceReferenceID;
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

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getJobcardID() {
        return jobcardID;
    }

    public void setJobcardID(String jobcardID) {
        this.jobcardID = jobcardID;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUsedAmount() {
        return usedAmount;
    }

    public void setUsedAmount(String usedAmount) {
        this.usedAmount = usedAmount;
    }

    public String getPendingAmount() {
        return pendingAmount;
    }

    public void setPendingAmount(String pendingAmount) {
        this.pendingAmount = pendingAmount;
    }

    public String getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(String balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public String getTransactionModeName() {
        return transactionModeName;
    }

    public void setTransactionModeName(String transactionModeName) {
        this.transactionModeName = transactionModeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReferenceDate() {
        return referenceDate;
    }

    public void setReferenceDate(String referenceDate) {
        this.referenceDate = referenceDate;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }
}
