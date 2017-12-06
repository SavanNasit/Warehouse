package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/29/17.
 */

public class PackageDetailsList {
    private String quantity;
    private String item;
    private String batchNumber;

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
}
