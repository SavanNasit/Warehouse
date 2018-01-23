package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 23/1/18.
 */

public class Status {
    @SerializedName("pacdelgatpacsid")
    @Expose
    private String pacdelgatpacsid;
    @SerializedName("name")
    @Expose
    private String name;

    @Override
    public String toString() {
        return name;
    }

    public String getPacdelgatpacsid() {
        return pacdelgatpacsid;
    }

    public void setPacdelgatpacsid(String pacdelgatpacsid) {
        this.pacdelgatpacsid = pacdelgatpacsid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
