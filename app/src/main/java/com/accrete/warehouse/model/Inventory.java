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
