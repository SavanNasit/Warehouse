package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 11/12/17.
 */

public class Collection {

    @SerializedName("invtid")
    @Expose
    private String invtid;
    @SerializedName("invrid")
    @Expose
    private String invrid;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("transaction_mode")
    @Expose
    private String transactionMode;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("invttid")
    @Expose
    private String invttid;
    @SerializedName("invtsid")
    @Expose
    private String invtsid;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("chkosid")
    @Expose
    private String chkosid;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("approved_uid")
    @Expose
    private String approvedUid;
    @SerializedName("approved_date")
    @Expose
    private String approvedDate;
    @SerializedName("cheque_uid")
    @Expose
    private String chequeUid;
    @SerializedName("cheque_date")
    @Expose
    private String chequeDate;
    @SerializedName("approved_user")
    @Expose
    private String approvedUser;
    @SerializedName("cheque_user")
    @Expose
    private String chequeUser;
    @SerializedName("jocaid")
    @Expose
    private String jocaid;
    @SerializedName("invsid")
    @Expose
    private String invsid;
    @SerializedName("invoice_transactionId")
    @Expose
    private String invoiceTransactionId;
    @SerializedName("transactionId")
    @Expose
    private String transactionId;
    @SerializedName("invoice_refrenceId")
    @Expose
    private String invoiceRefrenceId;
    @SerializedName("invoiceId")
    @Expose
    private String invoiceId;

    public String getInvtid() {
        return invtid;
    }

    public void setInvtid(String invtid) {
        this.invtid = invtid;
    }

    public String getInvrid() {
        return invrid;
    }

    public void setInvrid(String invrid) {
        this.invrid = invrid;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTransactionMode() {
        return transactionMode;
    }

    public void setTransactionMode(String transactionMode) {
        this.transactionMode = transactionMode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvttid() {
        return invttid;
    }

    public void setInvttid(String invttid) {
        this.invttid = invttid;
    }

    public String getInvtsid() {
        return invtsid;
    }

    public void setInvtsid(String invtsid) {
        this.invtsid = invtsid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getChkosid() {
        return chkosid;
    }

    public void setChkosid(String chkosid) {
        this.chkosid = chkosid;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getApprovedUid() {
        return approvedUid;
    }

    public void setApprovedUid(String approvedUid) {
        this.approvedUid = approvedUid;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getChequeUid() {
        return chequeUid;
    }

    public void setChequeUid(String chequeUid) {
        this.chequeUid = chequeUid;
    }

    public String getChequeDate() {
        return chequeDate;
    }

    public void setChequeDate(String chequeDate) {
        this.chequeDate = chequeDate;
    }

    public String getApprovedUser() {
        return approvedUser;
    }

    public void setApprovedUser(String approvedUser) {
        this.approvedUser = approvedUser;
    }

    public String getChequeUser() {
        return chequeUser;
    }

    public void setChequeUser(String chequeUser) {
        this.chequeUser = chequeUser;
    }

    public String getJocaid() {
        return jocaid;
    }

    public void setJocaid(String jocaid) {
        this.jocaid = jocaid;
    }

    public String getInvsid() {
        return invsid;
    }

    public void setInvsid(String invsid) {
        this.invsid = invsid;
    }

    public String getInvoiceTransactionId() {
        return invoiceTransactionId;
    }

    public void setInvoiceTransactionId(String invoiceTransactionId) {
        this.invoiceTransactionId = invoiceTransactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getInvoiceRefrenceId() {
        return invoiceRefrenceId;
    }

    public void setInvoiceRefrenceId(String invoiceRefrenceId) {
        this.invoiceRefrenceId = invoiceRefrenceId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
