package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/5/17.
 */

public class ItemsInsidePackage  implements Parcelable {

    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("description")
    @Expose
    private String description;

    protected ItemsInsidePackage(Parcel in) {
        itemName = in.readString();
        quantity = in.readString();
        description = in.readString();
    }

    public static final Creator<ItemsInsidePackage> CREATOR = new Creator<ItemsInsidePackage>() {
        @Override
        public ItemsInsidePackage createFromParcel(Parcel in) {
            return new ItemsInsidePackage(in);
        }

        @Override
        public ItemsInsidePackage[] newArray(int size) {
            return new ItemsInsidePackage[size];
        }
    };

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(quantity);
        dest.writeString(description);
    }
}