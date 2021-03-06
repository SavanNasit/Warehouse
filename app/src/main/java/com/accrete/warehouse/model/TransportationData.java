package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 18/1/18.
 */

public class TransportationData {

    @SerializedName("purotransdid")
    @Expose
    private String purotransdid;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("lrNumber")
    @Expose
    private String lrNumber;
    @SerializedName("vehicleNumber")
    @Expose
    private String vehicleNumber;
    @SerializedName("expectedDate")
    @Expose
    private String expectedDate;
    @SerializedName("vendor_name")
    @Expose
    private String vendorName;
    @SerializedName("vendor_id")
    @Expose
    private String vendorId;
    @SerializedName("purorid")
    @Expose
    private String purorid;
    //TODO Added on 16th July
    @SerializedName("isctrandid")
    @Expose
    private String isctrandid;
    @SerializedName("isExistTransportationDetails")
    @Expose
    private String isExistTransportationDetails;

    public String getIsctrandid() {
        return isctrandid;
    }

    public void setIsctrandid(String isctrandid) {
        this.isctrandid = isctrandid;
    }

    public String getIsExistTransportationDetails() {
        return isExistTransportationDetails;
    }

    public void setIsExistTransportationDetails(String isExistTransportationDetails) {
        this.isExistTransportationDetails = isExistTransportationDetails;
    }

    public String getPurorid() {
        return purorid;
    }

    public void setPurorid(String purorid) {
        this.purorid = purorid;
    }

    public String getPurotransdid() {
        return purotransdid;
    }

    public void setPurotransdid(String purotransdid) {
        this.purotransdid = purotransdid;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getLrNumber() {
        return lrNumber;
    }

    public void setLrNumber(String lrNumber) {
        this.lrNumber = lrNumber;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
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

}