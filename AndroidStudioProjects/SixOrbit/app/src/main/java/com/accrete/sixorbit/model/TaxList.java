package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 22/12/17.
 */

public class TaxList {
    @SerializedName("butapid")
    @Expose
    private String butapid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("count")
    @Expose
    private String count;
    @SerializedName("butapatid")
    @Expose
    private String butapatid;
    @SerializedName("butaprid")
    @Expose
    private String butaprid;
    @SerializedName("selected")
    @Expose
    private Boolean selected;
    @SerializedName("tax_amount")
    @Expose
    private String taxAmount;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(String taxAmount) {
        this.taxAmount = taxAmount;
    }

    public String getButapatid() {
        return butapatid;
    }

    public void setButapatid(String butapatid) {
        this.butapatid = butapatid;
    }

    public String getButaprid() {
        return butaprid;
    }

    public void setButaprid(String butaprid) {
        this.butaprid = butaprid;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getButapid() {
        return butapid;
    }

    public void setButapid(String butapid) {
        this.butapid = butapid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return name;
    }
}
