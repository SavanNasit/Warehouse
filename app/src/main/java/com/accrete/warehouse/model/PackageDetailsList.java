package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.accrete.warehouse.R;

/**
 * Created by poonam on 11/29/17.
 */

public class PackageDetailsList implements Parcelable {

    private String quantity;
    private String item;
    private String batchNumber;
    private String isvid;
    private String isid;
    private String iid;
    private String qty;

    protected PackageDetailsList(Parcel in) {
        quantity = in.readString();
        item = in.readString();
        batchNumber = in.readString();
        isvid = in.readString();
        isid = in.readString();
        iid = in.readString();
        qty = in.readString();
        oiid = in.readString();
        meaid = in.readString();
        name = in.readString();
        variation = in.readString();
        unit = in.readString();
    }

    public static final Creator<PackageDetailsList> CREATOR = new Creator<PackageDetailsList>() {
        @Override
        public PackageDetailsList createFromParcel(Parcel in) {
            return new PackageDetailsList(in);
        }

        @Override
        public PackageDetailsList[] newArray(int size) {
            return new PackageDetailsList[size];
        }
    };

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getOiid() {
        return oiid;
    }

    public void setOiid(String oiid) {
        this.oiid = oiid;
    }

    public String getMeaid() {
        return meaid;
    }

    public void setMeaid(String meaid) {
        this.meaid = meaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    private String oiid;
    private String meaid;
    private String name;
    private String variation;
    private String unit;

    public PackageDetailsList(){

    }

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


    public String getisvid() {
        return isvid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(quantity);
        dest.writeString(item);
        dest.writeString(batchNumber);
        dest.writeString(isvid);
        dest.writeString(isid);
        dest.writeString(iid);
        dest.writeString(qty);
        dest.writeString(oiid);
        dest.writeString(meaid);
        dest.writeString(name);
        dest.writeString(variation);
        dest.writeString(unit);
    }
}
