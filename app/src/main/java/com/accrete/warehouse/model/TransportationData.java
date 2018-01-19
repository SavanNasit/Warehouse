package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 18/1/18.
 */

public class TransportationData {
    @SerializedName("vendorName")
    @Expose
    private String vendorName;
    @SerializedName("lrnumber")
    @Expose
    private String lrnumber;
    @SerializedName("vehicleNumber")
    @Expose
    private String vehicleNumber;
    @SerializedName("exceptedDate")
    @Expose
    private String exceptedDate;
    @SerializedName("weight")
    @Expose
    private String weight;

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getLrnumber() {
        return lrnumber;
    }

    public void setLrnumber(String lrnumber) {
        this.lrnumber = lrnumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getExceptedDate() {
        return exceptedDate;
    }

    public void setExceptedDate(String exceptedDate) {
        this.exceptedDate = exceptedDate;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
