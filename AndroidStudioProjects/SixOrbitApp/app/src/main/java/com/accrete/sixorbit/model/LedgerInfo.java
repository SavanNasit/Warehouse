package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 30/3/18.
 */

public class LedgerInfo {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("alid")
    @Expose
    private String alid;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlid() {
        return alid;
    }

    public void setAlid(String alid) {
        this.alid = alid;
    }
}
