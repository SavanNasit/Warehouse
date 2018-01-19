package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 1/19/18.
 */

public class ShippingCompany {

    @SerializedName("scompid")
    @Expose
    private String scompid;
    @SerializedName("name")
    @Expose
    private String name;

    public String getScompid() {
        return scompid;
    }

    public void setScompid(String scompid) {
        this.scompid = scompid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}