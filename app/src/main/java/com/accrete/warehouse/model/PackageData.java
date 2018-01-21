package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 1/21/18.
 */

public class PackageData {

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    @SerializedName("packageId")
    @Expose
    private String packageId;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("historyData")
    @Expose
    private List<PackageStatusList> historyData = null;

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public List<PackageStatusList> getHistoryData() {
        return historyData;
    }

    public void setHistoryData(List<PackageStatusList> historyData) {
        this.historyData = historyData;
    }

}