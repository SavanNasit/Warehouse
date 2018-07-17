package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 14/12/17.
 */

public class ConsignmentItem {

    @SerializedName("inventoryId")
    @Expose
    private String inventoryId;
    @SerializedName("itemName")
    @Expose
    private String itemName;
    @SerializedName("sku_code")
    @Expose
    private String skuCode;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("selling_price")
    @Expose
    private String sellingPrice;
    @SerializedName("received_quantity")
    @Expose
    private String receivedQuantity;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("created_ts")
    @Expose

    private String createdTs;

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(String receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }
}
