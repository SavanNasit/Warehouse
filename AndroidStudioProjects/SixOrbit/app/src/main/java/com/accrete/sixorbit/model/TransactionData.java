package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 29/5/18.
 */

public class TransactionData {
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("transactionID")
    @Expose
    private String transactionID;
    @SerializedName("ledger")
    @Expose
    private String ledger;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("transaction_mode")
    @Expose
    private String transactionMode;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("created_user")
    @Expose
    private String createdUser;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_user")
    @Expose
    private String updatedUser;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("transaction_mode_data")
    @Expose
    private List<TransactionModeData> transactionModeData = null;

    public List<TransactionModeData> getTransactionModeData() {
        return transactionModeData;
    }

    public void setTransactionModeData(List<TransactionModeData> transactionModeData) {
        this.transactionModeData = transactionModeData;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getLedger() {
        return ledger;
    }

    public void setLedger(String ledger) {
        this.ledger = ledger;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public String getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(String updatedTs) {
        this.updatedTs = updatedTs;
    }
}
