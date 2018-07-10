package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 13/11/17.
 */

public class BankDetail {

    @SerializedName("badeid")
    @Expose
    private String badeid;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("account_no")
    @Expose
    private String accountNo;
    @SerializedName("account_holder")
    @Expose
    private String accountHolder;
    @SerializedName("bank_name")
    @Expose
    private String bankName;
    @SerializedName("branch_name")
    @Expose
    private String branchName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("ifsc_code")
    @Expose
    private String ifscCode;
    @SerializedName("badetid")
    @Expose
    private String badetid;
    @SerializedName("badesid")
    @Expose
    private String badesid;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("updated_uid")
    @Expose
    private String updatedUid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("type")
    @Expose
    private String type;

    public String getBadeid() {
        return badeid;
    }

    public void setBadeid(String badeid) {
        this.badeid = badeid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBadetid() {
        return badetid;
    }

    public void setBadetid(String badetid) {
        this.badetid = badetid;
    }

    public String getBadesid() {
        return badesid;
    }

    public void setBadesid(String badesid) {
        this.badesid = badesid;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getUpdatedUid() {
        return updatedUid;
    }

    public void setUpdatedUid(String updatedUid) {
        this.updatedUid = updatedUid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(String updatedTs) {
        this.updatedTs = updatedTs;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
