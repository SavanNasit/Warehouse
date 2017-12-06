package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Modified by poonam on 22/9/17.
 */
public class Data {

    public String getAuevid() {
        return auevid;
    }

    public void setAuevid(String auevid) {
        this.auevid = auevid;
    }

    public String getAumvid() {
        return aumvid;
    }

    public void setAumvid(String aumvid) {
        this.aumvid = aumvid;
    }

    @SerializedName("aumvid")
    @Expose
    private String aumvid;

    @SerializedName("auevid")
    @Expose
    private String auevid;

    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("usid")
    @Expose
    private String userSessionId;
    @SerializedName("maskconfig")
    @Expose
    private List<String> maskconfig = null;
}
