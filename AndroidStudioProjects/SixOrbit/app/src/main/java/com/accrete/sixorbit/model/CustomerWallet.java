package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 2/11/17.
 */

public class CustomerWallet {


    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("avid")
    @Expose
    private String avid;
    @SerializedName("account_voucher_type")
    @Expose
    private String accountVoucherType;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("transaction_data")
    @Expose
    private String transactionData;
    @SerializedName("narration")
    @Expose
    private String narration;
    @SerializedName("acc_voucher_id")
    @Expose
    private String accVoucherId;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("reference_no")
    @Expose
    private String referenceNo;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getAccVoucherId() {
        return accVoucherId;
    }

    public void setAccVoucherId(String accVoucherId) {
        this.accVoucherId = accVoucherId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAvid() {
        return avid;
    }

    public void setAvid(String avid) {
        this.avid = avid;
    }

    public String getAccountVoucherType() {
        return accountVoucherType;
    }

    public void setAccountVoucherType(String accountVoucherType) {
        this.accountVoucherType = accountVoucherType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionData() {
        return transactionData;
    }

    public void setTransactionData(String transactionData) {
        this.transactionData = transactionData;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

}
