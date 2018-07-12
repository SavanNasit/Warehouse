package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/22/17.
 */

public class ShippingType {

    @SerializedName("pacshtid")
    @Expose
    private String pacshtid;
    @SerializedName("name")
    @Expose
    private String name;

    public String getPacshtid() {
        return pacshtid;
    }

    public void setPacshtid(String pacshtid) {
        this.pacshtid = pacshtid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}