package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 3/10/17.
 */

public class PushNotifications {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("foid")
    @Expose
    private String foid;
    @SerializedName("leaid")
    @Expose
    private String leaid;
    private String chkoid, enid, qoid, puroid;
    private String uaid;

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getEnid() {
        return enid;
    }

    public void setEnid(String enid) {
        this.enid = enid;
    }

    public String getQoid() {
        return qoid;
    }

    public void setQoid(String qoid) {
        this.qoid = qoid;
    }

    public String getPuroid() {
        return puroid;
    }

    public void setPuroid(String puroid) {
        this.puroid = puroid;
    }

    public String getUaid() {
        return uaid;
    }

    public void setUaid(String uaid) {
        this.uaid = uaid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFoid() {
        return foid;
    }

    public void setFoid(String foid) {
        this.foid = foid;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }
}
