package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 3/9/18.
 */

public class ExecuteItemData {

    @SerializedName("inventory")
    @Expose
    private String inventory;
    @SerializedName("vendor")
    @Expose
    private String vendor;
    @SerializedName("purchasedOn")
    @Expose
    private String purchasedOn;
    @SerializedName("remark")
    @Expose
    private String remark;
    @SerializedName("available_quantity")
    @Expose
    private String availableQuantity;
    @SerializedName("allocated_quantity")
    @Expose
    private String allocatedQuantity;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("inventory_name")
    @Expose
    private String inventoryName;

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

    public String getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(String allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

}