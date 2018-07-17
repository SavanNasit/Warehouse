package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 30/3/18.
 */

public class LedgerSearchData {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("agid")
    @Expose
    private String agid;
    @SerializedName("parent_agid")
    @Expose
    private String parentAgid;

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

    public String getAgid() {
        return agid;
    }

    public void setAgid(String agid) {
        this.agid = agid;
    }

    public String getParentAgid() {
        return parentAgid;
    }

    public void setParentAgid(String parentAgid) {
        this.parentAgid = parentAgid;
    }

    @Override
    public String toString() {
        return name;
    }
}
