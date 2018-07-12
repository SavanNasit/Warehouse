package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 20/1/18.
 */

public class PurchaseOrderData {

    @SerializedName("poid")
    @Expose
    private String poid;
    @SerializedName("purorid")
    @Expose
    private String purorid;
    @SerializedName("authorizedBy")
    @Expose
    private String authorizedBy;
    @SerializedName("authorizedById")
    @Expose
    private String authorizedById;
    @SerializedName("vendorName")
    @Expose
    private String vendorName;
    @SerializedName("vendorId")
    @Expose
    private String vendorId;
    @SerializedName("purorsid")
    @Expose
    private String purorsid;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public String getPoid() {
        return poid;
    }

    public void setPoid(String poid) {
        this.poid = poid;
    }

    public String getPurorid() {
        return purorid;
    }

    public void setPurorid(String purorid) {
        this.purorid = purorid;
    }

    public String getAuthorizedBy() {
        return authorizedBy;
    }

    public void setAuthorizedBy(String authorizedBy) {
        this.authorizedBy = authorizedBy;
    }

    public String getAuthorizedById() {
        return authorizedById;
    }

    public void setAuthorizedById(String authorizedById) {
        this.authorizedById = authorizedById;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getPurorsid() {
        return purorsid;
    }

    public void setPurorsid(String purorsid) {
        this.purorsid = purorsid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }
}