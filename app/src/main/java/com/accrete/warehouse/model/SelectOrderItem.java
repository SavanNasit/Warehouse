package com.accrete.warehouse.model;


/**
 * Created by poonam on 11/29/17.
 */

public class SelectOrderItem {

    public String getInventory() {
        return inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getPurchasedOn() {
        return purchasedOn;
    }

    public void setPurchasedOn(String purchasedOn) {
        this.purchasedOn = purchasedOn;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(String availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public String getAllotQuantity() {
        return allotQuantity;
    }

    public void setAllotQuantity(String allotQuantity) {
        this.allotQuantity = allotQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    String inventory;
    String vendor;
    String purchasedOn;
    String remark;
    String availableQuantity;
    String allotQuantity;
    String unit;



}
