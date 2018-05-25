package com.accrete.warehouse.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 11/29/17.
 */

public class
SelectOrderItem implements Parcelable{
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

    public String getFinalQuantity() {
        return finalQuantity;
    }

    public void setFinalQuantity(String finalQuantity) {
        this.finalQuantity = finalQuantity;
    }


    private String  finalQuantity;


    public SelectOrderItem(){

    }

    protected SelectOrderItem(Parcel in) {
        inventory = in.readString();
        vendor = in.readString();
        purchasedOn = in.readString();
        remark = in.readString();
        finalQuantity=in.readString();
        availableQuantity = in.readString();
        if (in.readByte() == 0) {
            allocatedQuantity = null;
        } else {
            allocatedQuantity = in.readString();
        }
        unit = in.readString();
        inventoryName = in.readString();

    }

    public static final Creator<SelectOrderItem> CREATOR = new Creator<SelectOrderItem>() {
        @Override
        public SelectOrderItem createFromParcel(Parcel in) {
            return new SelectOrderItem(in);
        }

        @Override
        public SelectOrderItem[] newArray(int size) {
            return new SelectOrderItem[size];
        }
    };


    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inventory);
        dest.writeString(vendor);
        dest.writeString(purchasedOn);
        dest.writeString(finalQuantity);
        dest.writeString(remark);
        dest.writeString(availableQuantity);
        if (allocatedQuantity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeString(allocatedQuantity);
        }
        dest.writeString(unit);
        dest.writeString(inventoryName);
    }
}