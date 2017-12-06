package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/6/17.
 */

public class Inventory {
    private String availableStock;
    private String inventoryID;
    private String item;

    public String getAvailableStock() {
        return availableStock;
    }

    public String getInventoryID() {
        return inventoryID;
    }

    public String getItem() {
        return item;
    }

    public String getSKUCode() {
        return SKUCode;
    }

    public String getReceivedQuantity() {
        return receivedQuantity;
    }

    private String SKUCode;
    private String receivedQuantity;

    public void setInventoryID(String inventoryID) {
        this.inventoryID = inventoryID;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public void setSKUCode(String SKUCode) {
        this.SKUCode = SKUCode;
    }

    public void setReceivedQuantity(String receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public void setAvailableStock(String availableStock) {
        this.availableStock = availableStock;
    }
}
