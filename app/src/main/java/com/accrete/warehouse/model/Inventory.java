package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/6/17.
 */

public class Inventory {
    @SerializedName("inventoryID")
    @Expose
    private String inventoryID;
    @SerializedName("item")
    @Expose
    private String item;
    @SerializedName("sku_code")
    @Expose
    private String skuCode;
    @SerializedName("received_quantity")
    @Expose
    private String receivedQuantity;
    @SerializedName("available_stock")
    @Expose
    private String availableStock;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    @SerializedName("manufacturing_date")
    @Expose
    private String manufacturingDate;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("isid_number")
    @Expose
    private String isidNumber;

    public String getIsidNumber() {
        return isidNumber;
    }

    public void setIsidNumber(String isidNumber) {
        this.isidNumber = isidNumber;
    }

    public String getExpiryDate() {

        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getExpiry_date() {
        return expiryDate;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiryDate = expiry_date;
    }


    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(String receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(String availableStock) {
        this.availableStock = availableStock;
    }
}
