package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 2/11/17.
 */

public class CustomerPendingInvoice {
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
    @SerializedName("pending_since")
    @Expose
    private String pendingSince;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("invoice_id")
    @Expose
    private String invoiceId;
    @SerializedName("narration")
    @Expose
    private String narration;
    //TODO Added on 31st May
    @SerializedName("jocaid")
    @Expose
    private String jocaid;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;
    @SerializedName("invoice_type")
    @Expose
    private String invoiceType;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("shipping_address")
    @Expose
    private String shippingAddress;
    @SerializedName("billing_address")
    @Expose
    private String billingAddress;
    @SerializedName("c_assigned_user_name")
    @Expose
    private String cAssignedUserName;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("total_discount_amount")
    @Expose
    private String totalDiscountAmount;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("c_paid_amount")
    @Expose
    private String cPaidAmount;
    @SerializedName("c_pending_amount")
    @Expose
    private String cPendingAmount;
    @SerializedName("c_pending_since")
    @Expose
    private String cPendingSince;
    @SerializedName("c_transaction_mode_name")
    @Expose
    private String cTransactionModeName;
    @SerializedName("invsid")
    @Expose
    private String invsid;
    @SerializedName("butapid")
    @Expose
    private String butapid;
    @SerializedName("taxation_error")
    @Expose
    private String taxationError;
    @SerializedName("c_created_user_name")
    @Expose
    private String cCreatedUserName;
    @SerializedName("c_updated_user_name")
    @Expose
    private String cUpdatedUserName;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("packageID")
    @Expose
    private String packageID;
    @SerializedName("jobcardID")
    @Expose
    private String jobcardID;
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("StockRequestID")
    @Expose
    private String stockRequestID;

    public String getJocaid() {
        return jocaid;
    }

    public void setJocaid(String jocaid) {
        this.jocaid = jocaid;
    }

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getStockreqid() {
        return stockreqid;
    }

    public void setStockreqid(String stockreqid) {
        this.stockreqid = stockreqid;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getcAssignedUserName() {
        return cAssignedUserName;
    }

    public void setcAssignedUserName(String cAssignedUserName) {
        this.cAssignedUserName = cAssignedUserName;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getTotalDiscountAmount() {
        return totalDiscountAmount;
    }

    public void setTotalDiscountAmount(String totalDiscountAmount) {
        this.totalDiscountAmount = totalDiscountAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getcPaidAmount() {
        return cPaidAmount;
    }

    public void setcPaidAmount(String cPaidAmount) {
        this.cPaidAmount = cPaidAmount;
    }

    public String getcPendingAmount() {
        return cPendingAmount;
    }

    public void setcPendingAmount(String cPendingAmount) {
        this.cPendingAmount = cPendingAmount;
    }

    public String getcPendingSince() {
        return cPendingSince;
    }

    public void setcPendingSince(String cPendingSince) {
        this.cPendingSince = cPendingSince;
    }

    public String getcTransactionModeName() {
        return cTransactionModeName;
    }

    public void setcTransactionModeName(String cTransactionModeName) {
        this.cTransactionModeName = cTransactionModeName;
    }

    public String getInvsid() {
        return invsid;
    }

    public void setInvsid(String invsid) {
        this.invsid = invsid;
    }

    public String getButapid() {
        return butapid;
    }

    public void setButapid(String butapid) {
        this.butapid = butapid;
    }

    public String getTaxationError() {
        return taxationError;
    }

    public void setTaxationError(String taxationError) {
        this.taxationError = taxationError;
    }

    public String getcCreatedUserName() {
        return cCreatedUserName;
    }

    public void setcCreatedUserName(String cCreatedUserName) {
        this.cCreatedUserName = cCreatedUserName;
    }

    public String getcUpdatedUserName() {
        return cUpdatedUserName;
    }

    public void setcUpdatedUserName(String cUpdatedUserName) {
        this.cUpdatedUserName = cUpdatedUserName;
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

    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getJobcardID() {
        return jobcardID;
    }

    public void setJobcardID(String jobcardID) {
        this.jobcardID = jobcardID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getStockRequestID() {
        return stockRequestID;
    }

    public void setStockRequestID(String stockRequestID) {
        this.stockRequestID = stockRequestID;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public String getPendingSince() {
        return pendingSince;
    }

    public void setPendingSince(String pendingSince) {
        this.pendingSince = pendingSince;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

}
