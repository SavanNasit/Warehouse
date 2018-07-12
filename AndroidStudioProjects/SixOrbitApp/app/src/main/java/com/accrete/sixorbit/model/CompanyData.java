package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 25/6/18.
 */

public class CompanyData {
    @SerializedName("liccoid")
    @Expose
    private String liccoid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("licid")
    @Expose
    private String licid;
    @SerializedName("company_id")
    @Expose
    private String companyId;

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getLiccoid() {
        return liccoid;
    }

    public void setLiccoid(String liccoid) {
        this.liccoid = liccoid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getLicid() {
        return licid;
    }

    public void setLicid(String licid) {
        this.licid = licid;
    }

    @Override
    public String toString() {
        return name;
    }
}
