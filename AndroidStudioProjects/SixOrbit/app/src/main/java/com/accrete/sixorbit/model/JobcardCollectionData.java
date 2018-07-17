package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 30/5/18.
 */

public class JobcardCollectionData {
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("chekPointOrderID")
    @Expose
    private String chekPointOrderID;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("order_amount")
    @Expose
    private String orderAmount;
    @SerializedName("billed_amount")
    @Expose
    private String billedAmount;
    @SerializedName("balance")
    @Expose
    private String balance;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("invoiceID")
    @Expose
    private String invoiceID;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("paid_amount")
    @Expose
    private String paidAmount;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getChekPointOrderID() {
        return chekPointOrderID;
    }

    public void setChekPointOrderID(String chekPointOrderID) {
        this.chekPointOrderID = chekPointOrderID;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getBilledAmount() {
        return billedAmount;
    }

    public void setBilledAmount(String billedAmount) {
        this.billedAmount = billedAmount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(String invoiceID) {
        this.invoiceID = invoiceID;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }
}
