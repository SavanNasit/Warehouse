package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 12/12/17.
 */

public class CustomersType {
    @SerializedName("cutid")
    @Expose
    private String cutid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("cuttid")
    @Expose
    private String cuttid;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("updated_uid")
    @Expose
    private String updatedUid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;

    public String getCutid() {
        return cutid;
    }

    public void setCutid(String cutid) {
        this.cutid = cutid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCuttid() {
        return cuttid;
    }

    public void setCuttid(String cuttid) {
        this.cuttid = cuttid;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getUpdatedUid() {
        return updatedUid;
    }

    public void setUpdatedUid(String updatedUid) {
        this.updatedUid = updatedUid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(String updatedTs) {
        this.updatedTs = updatedTs;
    }

    @Override
    public String toString() {
        return name;
    }
}
