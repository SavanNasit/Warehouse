package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 1/21/18.
 */

public class ViewGatepassPackages {
    @SerializedName("packageID")
    @Expose
    private String packageID;
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("exp_date")
    @Expose
    private String expDate;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("itemList")
    @Expose
    private List<ItemsInsidePackage> itemList = null;

    @SerializedName("chkoid")
    @Expose
    public String chkoid;

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    @SerializedName("invoice_number")
    @Expose
    public String invoiceNumber;

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }


    public String getPackageID() {
        return packageID;
    }

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getExpDate() {
        return expDate;
    }

    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public List<ItemsInsidePackage> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemsInsidePackage> itemList) {
        this.itemList = itemList;
    }
}