package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/22/17.
 */

public class ShippingBy {

    @SerializedName("pacdelgatpactid")
    @Expose
    private String pacdelgatpactid;
    @SerializedName("name")
    @Expose
    private String name;

    public String getPacdelgatpactid() {
        return pacdelgatpactid;
    }

    public void setPacdelgatpactid(String pacdelgatpactid) {
        this.pacdelgatpactid = pacdelgatpactid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}