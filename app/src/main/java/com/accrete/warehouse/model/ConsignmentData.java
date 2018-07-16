package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 18/1/18.
 */

public class ConsignmentData {

    @SerializedName("containerID")
    @Expose
    private String containerID;
    @SerializedName("authorizedName")
    @Expose
    private String authorizedName;
    @SerializedName("purchaseOrder")
    @Expose
    private String purchaseOrder;
    @SerializedName("purchasedOn")
    @Expose
    private String purchasedOn;
    @SerializedName("invoiceNo")
    @Expose
    private String invoiceNo;
    @SerializedName("invoiceDate")
    @Expose
    private String invoiceDate;
    @SerializedName("vendorName")
    @Expose
    private String vendorName;
    @SerializedName("createdBy")
    @Expose
    private String createdBy;
    @SerializedName("createdTime")
    @Expose
    private String createdTime;
    @SerializedName("warehouseName")
    @Expose
    private String warehouseName;
    @SerializedName("receivedOn")
    @Expose
    private String receivedOn;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("stockRequest")
    @Expose
    private String stockRequest;
    //TODO Added on 16th July 2k18
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("purchase_date")
    @Expose
    private String purchaseDate;
    @SerializedName("receive_date")
    @Expose
    private String receiveDate;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("authorized_id")
    @Expose
    private String authorizedId;
    @SerializedName("authorized_by")
    @Expose
    private String authorizedBy;
    @SerializedName("iscsid")
    @Expose
    private String iscsid;

    public String getIscsid() {
        return iscsid;
    }

    public void setIscsid(String iscsid) {
        this.iscsid = iscsid;
    }

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getAuthorizedId() {
        return authorizedId;
    }

    public void setAuthorizedId(String authorizedId) {
        this.authorizedId = authorizedId;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getStockRequest() {
        return stockRequest;
    }

    public void setStockRequest(String stockRequest) {
        this.stockRequest = stockRequest;
    }

    public String getContainerID() {
        return containerID;
    }

    public void setContainerID(String containerID) {
        this.containerID = containerID;
    }

    public String getAuthorizedName() {
        return authorizedName;
    }

    public void setAuthorizedName(String authorizedName) {
        this.authorizedName = authorizedName;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(String purchasedOn) {
        this.purchasedOn = purchasedOn;
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

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getWarehouseName() {
        return warehouseName;
    }

    public void setWarehouseName(String warehouseName) {
        this.warehouseName = warehouseName;
    }

    public String getReceivedOn() {
        return receivedOn;
    }

    public void setReceivedOn(String receivedOn) {
        this.receivedOn = receivedOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
