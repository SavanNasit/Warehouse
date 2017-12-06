package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/5/17.
 */

public class ItemsInsidePackage {
    private String description;

    public String getDescription() {
        return description;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getItemName() {
        return itemName;
    }

    private String quantity;
    private String itemName;

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
