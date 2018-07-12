package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 25/12/17.
 */

public class Outlet {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("sale_alid")
    @Expose
    private String saleAlid;
    @SerializedName("purchase_alid")
    @Expose
    private String purchaseAlid;
    @SerializedName("stid")
    @Expose
    private String stid;
    @SerializedName("selected")
    @Expose
    private boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getSaleAlid() {
        return saleAlid;
    }

    public void setSaleAlid(String saleAlid) {
        this.saleAlid = saleAlid;
    }

    public String getPurchaseAlid() {
        return purchaseAlid;
    }

    public void setPurchaseAlid(String purchaseAlid) {
        this.purchaseAlid = purchaseAlid;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    @Override
    public String toString() {
        return name;
    }
}
