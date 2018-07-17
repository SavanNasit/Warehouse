package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 14/12/17.
 */

public class OrderItem {
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("internal_code")
    @Expose
    private String internalCode;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("item_tax")
    @Expose
    private String itemTax;
    @SerializedName("sub_total")
    @Expose
    private String subTotal;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("allocated_quantity")
    @Expose
    private String allocatedQuantity;

    public String getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(String allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getItemTax() {
        return itemTax;
    }

    public void setItemTax(String itemTax) {
        this.itemTax = itemTax;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }
}
