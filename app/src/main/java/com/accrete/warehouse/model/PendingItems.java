package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/28/17.
 */

public class PendingItems {
    private String status;
    private String item;

    public String getStatus() {
        return status;
    }

    public String getItem() {
        return item;
    }

    public String getSKUCode() {
        return SKUCode;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public String getQuantity() {
        return quantity;
    }

    private String SKUCode;
    private String batchNumber;
    private String quantity;

    public void setItem(String item) {
        this.item = item;
    }

    public void setSKUCode(String SKUCode) {
        this.SKUCode = SKUCode;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
