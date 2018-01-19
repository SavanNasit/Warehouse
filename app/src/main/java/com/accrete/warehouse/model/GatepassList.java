package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 1/19/18.
 */

public class GatepassList {

    @SerializedName("pacdelgatid")
    @Expose
    private String pacdelgatid;
    @SerializedName("packages")
    @Expose
    private String packages;
    @SerializedName("created_on")
    @Expose
    private String createdOn;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("gatepass_status")
    @Expose
    private String gatepassStatus;
    @SerializedName("shipping_company_name")
    @Expose
    private String shippingCompanyName;
    @SerializedName("shipping_type")
    @Expose
    private String shippingType;
    @SerializedName("out_for_delivery")
    @Expose
    private String outForDelivery;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("pacdelgatsid")
    @Expose
    private String pacdelgatsid;
    @SerializedName("gatePassId")
    @Expose
    private String gatePassId;

    public String getPacdelgatid() {
        return pacdelgatid;
    }

    public void setPacdelgatid(String pacdelgatid) {
        this.pacdelgatid = pacdelgatid;
    }

    public String getPackages() {
        return packages;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getGatepassStatus() {
        return gatepassStatus;
    }

    public void setGatepassStatus(String gatepassStatus) {
        this.gatepassStatus = gatepassStatus;
    }

    public String getShippingCompanyName() {
        return shippingCompanyName;
    }

    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }

    public String getShippingType() {
        return shippingType;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }

    public String getOutForDelivery() {
        return outForDelivery;
    }

    public void setOutForDelivery(String outForDelivery) {
        this.outForDelivery = outForDelivery;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getPacdelgatsid() {
        return pacdelgatsid;
    }

    public void setPacdelgatsid(String pacdelgatsid) {
        this.pacdelgatsid = pacdelgatsid;
    }

    public String getGatePassId() {
        return gatePassId;
    }

    public void setGatePassId(String gatePassId) {
        this.gatePassId = gatePassId;
    }

}