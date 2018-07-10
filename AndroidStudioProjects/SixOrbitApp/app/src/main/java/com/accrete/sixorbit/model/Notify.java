package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 11/10/17.
 */

public class Notify {

    @SerializedName("uanid")
    @Expose
    private String uanid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("motaid")
    @Expose
    private String motaid;
    @SerializedName("mid")
    @Expose
    private String mid;

    public String getUanid() {
        return uanid;
    }

    public void setUanid(String uanid) {
        this.uanid = uanid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMotaid() {
        return motaid;
    }

    public void setMotaid(String motaid) {
        this.motaid = motaid;
    }

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

}
