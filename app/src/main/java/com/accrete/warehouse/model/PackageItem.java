package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 19/1/18.
 */

public class PackageItem {
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("pacshsid")
    @Expose
    private String pacshsid;
    @SerializedName("pacdelgatpacsid")
    @Expose
    private String pacdelgatpacsid;
    @SerializedName("from_date")
    @Expose
    private String fromDate;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("e_sugam")
    @Expose
    private String eSugam;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("pacsid")
    @Expose
    private String pacsid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("pacdelgatpacid")
    @Expose
    private String pacdelgatpacid;
    @SerializedName("pacdelgatid")
    @Expose
    private String pacdelgatid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;
    @SerializedName("packageId")
    @Expose
    private String packageId;
    @SerializedName("orderId")
    @Expose
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
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

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getPacshsid() {
        return pacshsid;
    }

    public void setPacshsid(String pacshsid) {
        this.pacshsid = pacshsid;
    }

    public String getPacdelgatpacsid() {
        return pacdelgatpacsid;
    }

    public void setPacdelgatpacsid(String pacdelgatpacsid) {
        this.pacdelgatpacsid = pacdelgatpacsid;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String geteSugam() {
        return eSugam;
    }

    public void seteSugam(String eSugam) {
        this.eSugam = eSugam;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPacsid() {
        return pacsid;
    }

    public void setPacsid(String pacsid) {
        this.pacsid = pacsid;
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

    public String getPacdelgatpacid() {
        return pacdelgatpacid;
    }

    public void setPacdelgatpacid(String pacdelgatpacid) {
        this.pacdelgatpacid = pacdelgatpacid;
    }

    public String getPacdelgatid() {
        return pacdelgatid;
    }

    public void setPacdelgatid(String pacdelgatid) {
        this.pacdelgatid = pacdelgatid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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
}
