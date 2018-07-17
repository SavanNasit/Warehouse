package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 31/10/17.
 */
public class Quotation {
    @SerializedName("data")
    @Expose
    private QuotationComment comment;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("qoid")
    @Expose
    private String qoid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("quotation_id")
    @Expose
    private String quotationId;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("cc")
    @Expose
    private String cc;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("payable_amount")
    @Expose
    private String payableAmount;
    @SerializedName("said")
    @Expose
    private String said;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("c_customer_name")
    @Expose
    private String customerCName;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("assigned")
    @Expose
    private String assigned;
    @SerializedName("qosid")
    @Expose
    private String qosid;
    @SerializedName("status_name")
    @Expose
    private String statusName;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("assigned_uid")
    @Expose
    private String assignedUid;
    @SerializedName("updated_uid")
    @Expose
    private String updatedUid;
    @SerializedName("created_user")
    @Expose
    private String createdUser;
    @SerializedName("updated_user")
    @Expose
    private String updatedUser;
    @SerializedName("enid")
    @Expose
    private String enid;
    @SerializedName("approval_reason")
    @Expose
    private String approvalReason;
    @SerializedName("quotationID")
    @Expose
    private String quotationID;

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getAssignedUid() {
        return assignedUid;
    }

    public void setAssignedUid(String assignedUid) {
        this.assignedUid = assignedUid;
    }

    public String getUpdatedUid() {
        return updatedUid;
    }

    public void setUpdatedUid(String updatedUid) {
        this.updatedUid = updatedUid;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public String getEnid() {
        return enid;
    }

    public void setEnid(String enid) {
        this.enid = enid;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getQuotationID() {
        return quotationID;
    }

    public void setQuotationID(String quotationID) {
        this.quotationID = quotationID;
    }

    public QuotationComment getComment() {

        return comment;
    }

    public void setComment(QuotationComment comment) {
        this.comment = comment;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getQoid() {
        return qoid;
    }

    public void setQoid(String qoid) {
        this.qoid = qoid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(String payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getSaid() {
        return said;
    }

    public void setSaid(String said) {
        this.said = said;
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

    public String getCustomerCName() {
        return customerCName;
    }

    public void setCustomerCName(String customerCName) {
        this.customerCName = customerCName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssigned() {
        return assigned;
    }

    public void setAssigned(String assigned) {
        this.assigned = assigned;
    }

    public String getQosid() {
        return qosid;
    }

    public void setQosid(String qosid) {
        this.qosid = qosid;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
