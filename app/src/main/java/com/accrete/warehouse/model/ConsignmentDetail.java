package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 19/1/18.
 */

public class ConsignmentDetail {
    @SerializedName("consignmentID")
    @Expose
    private String consignmentID;
    @SerializedName("vendor")
    @Expose
    private String vendor;
    @SerializedName("purchase_date")
    @Expose
    private String purchaseDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("iscid")
    @Expose
    private String iscid;

    public String getIscid() {
        return iscid;
    }

    public void setIscid(String iscid) {
        this.iscid = iscid;
    }

    public String getConsignmentID() {
        return consignmentID;
    }

    public void setConsignmentID(String consignmentID) {
        this.consignmentID = consignmentID;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
