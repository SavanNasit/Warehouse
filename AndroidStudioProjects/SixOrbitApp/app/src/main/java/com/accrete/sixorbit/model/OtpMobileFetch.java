package com.accrete.sixorbit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by amp on 26/10/17.
 */

public class OtpMobileFetch {

    @SerializedName("aumvid")
    @Expose
    private String aumvid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("imei")
    @Expose
    private String imei;
    @SerializedName("gcm_id")
    @Expose
    private String gcmId;
    @SerializedName("app_version")
    @Expose
    private String appVersion;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("primary_no")
    @Expose
    private String primaryNo;
    @SerializedName("aumvsid")
    @Expose
    private String aumvsid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("verified_ts")
    @Expose
    private String verifiedTs;

    public String getAumvid() {
        return aumvid;
    }

    public void setAumvid(String aumvid) {
        this.aumvid = aumvid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getPrimaryNo() {
        return primaryNo;
    }

    public void setPrimaryNo(String primaryNo) {
        this.primaryNo = primaryNo;
    }

    public String getAumvsid() {
        return aumvsid;
    }

    public void setAumvsid(String aumvsid) {
        this.aumvsid = aumvsid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getVerifiedTs() {
        return verifiedTs;
    }

    public void setVerifiedTs(String verifiedTs) {
        this.verifiedTs = verifiedTs;
    }

}