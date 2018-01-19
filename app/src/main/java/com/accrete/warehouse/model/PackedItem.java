package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/14/17.
 */
public class PackedItem {

    @SerializedName("crereqappacsid")
    @Expose
    private String crereqappacsid;
    @SerializedName("crereqappac")
    @Expose
    private String crereqappac;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("invid")

    @Expose
    private String invid;
    @SerializedName("pacshsid")
    @Expose
    private String pacshsid;
    @SerializedName("pacshtid")
    @Expose
    private String pacshtid;
    @SerializedName("tracking_id")
    @Expose
    private String trackingId;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("scompid")
    @Expose
    private String scompid;
    @SerializedName("oid")
    @Expose
    private String oid;
    @SerializedName("pacsid")
    @Expose
    private String pacsid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;
    @SerializedName("order_payment_type")
    @Expose
    private String orderPaymentType;
    @SerializedName("order_payment_type_text")
    @Expose
    private String orderPaymentTypeText;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("payment_status_id")
    @Expose
    private String paymentStatusId;
    @SerializedName("pending_invoice_amount")
    @Expose
    private String pendingInvoiceAmount;
    @SerializedName("order_type_cash_pending_amount")
    @Expose
    private String orderTypeCashPendingAmount;
    @SerializedName("invoice_payable_amount")
    @Expose
    private String invoicePayableAmount;
    @SerializedName("invoice_paid_amount")
    @Expose
    private String invoicePaidAmount;
    @SerializedName("packageId")
    @Expose
    private String packageId;
    private boolean selected;

    public String getCrereqappacsid() {
        return crereqappacsid;
    }

    public void setCrereqappacsid(String crereqappacsid) {
        this.crereqappacsid = crereqappacsid;
    }

    public String getCrereqappac() {
        return crereqappac;
    }

    public void setCrereqappac(String crereqappac) {
        this.crereqappac = crereqappac;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getOrderPaymentType() {
        return orderPaymentType;
    }

    public void setOrderPaymentType(String orderPaymentType) {
        this.orderPaymentType = orderPaymentType;
    }

    public String getOrderPaymentTypeText() {
        return orderPaymentTypeText;
    }

    public void setOrderPaymentTypeText(String orderPaymentTypeText) {
        this.orderPaymentTypeText = orderPaymentTypeText;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(String paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getPendingInvoiceAmount() {
        return pendingInvoiceAmount;
    }

    public void setPendingInvoiceAmount(String pendingInvoiceAmount) {
        this.pendingInvoiceAmount = pendingInvoiceAmount;
    }

    public String getOrderTypeCashPendingAmount() {
        return orderTypeCashPendingAmount;
    }

    public void setOrderTypeCashPendingAmount(String orderTypeCashPendingAmount) {
        this.orderTypeCashPendingAmount = orderTypeCashPendingAmount;
    }

    public String getInvoicePayableAmount() {
        return invoicePayableAmount;
    }

    public void setInvoicePayableAmount(String invoicePayableAmount) {
        this.invoicePayableAmount = invoicePayableAmount;
    }

    public String getInvoicePaidAmount() {
        return invoicePaidAmount;
    }

    public void setInvoicePaidAmount(String invoicePaidAmount) {
        this.invoicePaidAmount = invoicePaidAmount;
    }

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getPacshsid() {
        return pacshsid;
    }

    public void setPacshsid(String pacshsid) {
        this.pacshsid = pacshsid;
    }

    public String getPacshtid() {
        return pacshtid;
    }

    public void setPacshtid(String pacshtid) {
        this.pacshtid = pacshtid;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getScompid() {
        return scompid;
    }

    public void setScompid(String scompid) {
        this.scompid = scompid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPacsid() {
        return pacsid;
    }

    public void setPacsid(String pacsid) {
        this.pacsid = pacsid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getStockreqid() {
        return stockreqid;
    }

    public void setStockreqid(String stockreqid) {
        this.stockreqid = stockreqid;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
