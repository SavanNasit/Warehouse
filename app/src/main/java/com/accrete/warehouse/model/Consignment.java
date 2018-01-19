package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 18/1/18.
 */

public class Consignment {

    @SerializedName("iscid")
    @Expose
    private String iscid;
    @SerializedName("purorid")
    @Expose
    private String purorid;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("purchase_date")
    @Expose
    private String purchaseDate;
    @SerializedName("purchase_number")
    @Expose
    private String purchaseNumber;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("iscsid")
    @Expose
    private String iscsid;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("interstate")
    @Expose
    private String interstate;
    @SerializedName("isid")
    @Expose
    private String isid;
    @SerializedName("vendor")
    @Expose
    private String vendor;
    @SerializedName("purchase_order_date")
    @Expose
    private String purchaseOrderDate;
    @SerializedName("warehouse")
    @Expose
    private String warehouse;
    @SerializedName("consignment_id")
    @Expose
    private String consignmentId;
    @SerializedName("purchase_orderID")
    @Expose
    private String purchaseOrderID;

    public String getIscid() {
        return iscid;
    }

    public void setIscid(String iscid) {
        this.iscid = iscid;
    }

    public String getPurorid() {
        return purorid;
    }

    public void setPurorid(String purorid) {
        this.purorid = purorid;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getIscsid() {
        return iscsid;
    }

    public void setIscsid(String iscsid) {
        this.iscsid = iscsid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getInterstate() {
        return interstate;
    }

    public void setInterstate(String interstate) {
        this.interstate = interstate;
    }

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getConsignmentId() {
        return consignmentId;
    }

    public void setConsignmentId(String consignmentId) {
        this.consignmentId = consignmentId;
    }

    public String getPurchaseOrderID() {
        return purchaseOrderID;
    }

    public void setPurchaseOrderID(String purchaseOrderID) {
        this.purchaseOrderID = purchaseOrderID;
    }
}
