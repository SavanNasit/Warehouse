package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 6/12/17.
 */

public class Order {

    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("chkosid")
    @Expose
    private String chkosid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("checkpoint_order_id")
    @Expose
    private String checkpointOrderId;
    @SerializedName("discount_percent")
    @Expose
    private String discountPercent;
    @SerializedName("tax_percent")
    @Expose
    private String taxPercent;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("approval_reason")
    @Expose
    private String approvalReason;
    @SerializedName("taken_date")
    @Expose
    private String takenDate;
    @SerializedName("discounted_amount")
    @Expose
    private String discountedAmount;

    public String getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(String discountPercent) {
        this.discountPercent = discountPercent;
    }

    public String getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(String taxPercent) {
        this.taxPercent = taxPercent;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getApprovalReason() {
        return approvalReason;
    }

    public void setApprovalReason(String approvalReason) {
        this.approvalReason = approvalReason;
    }

    public String getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    public String getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(String discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getChkosid() {
        return chkosid;
    }

    public void setChkosid(String chkosid) {
        this.chkosid = chkosid;
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

    public String getCheckpointOrderId() {
        return checkpointOrderId;
    }

    public void setCheckpointOrderId(String checkpointOrderId) {
        this.checkpointOrderId = checkpointOrderId;
    }
}
