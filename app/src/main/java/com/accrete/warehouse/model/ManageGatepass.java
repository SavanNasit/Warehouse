package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageGatepass {

    public String getShippingType() {
        return shippingType;
    }

    public String getGatepassID() {
        return gatepassID;
    }

    public String getPackages() {
        return packages;
    }

    public String getDeliveryUser() {
        return deliveryUser;
    }

    public String getGeneratedOn() {
        return generatedOn;
    }

    public String getGatepassStatus() {
        return gatepassStatus;
    }

    public String getShippingCompanyName() {
        return shippingCompanyName;
    }

    private String shippingType;
    private String gatepassID;
    private String packages;
    private String deliveryUser;
    private String generatedOn;
    private String gatepassStatus;
    private String shippingCompanyName;

    public void setGatepassID(String gatepassID) {
        this.gatepassID = gatepassID;
    }

    public void setPackages(String packages) {
        this.packages = packages;
    }

    public void setDeliveryUser(String deliveryUser) {
        this.deliveryUser = deliveryUser;
    }

    public void setGeneratedOn(String generatedOn) {
        this.generatedOn = generatedOn;
    }

    public void setGatepassStatus(String gatepassStatus) {
        this.gatepassStatus = gatepassStatus;
    }

    public void setShippingCompanyName(String shippingCompanyName) {
        this.shippingCompanyName = shippingCompanyName;
    }

    public void setShippingType(String shippingType) {
        this.shippingType = shippingType;
    }
}
