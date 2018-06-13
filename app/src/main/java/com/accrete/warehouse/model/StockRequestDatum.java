package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 6/4/18.
 */

public class StockRequestDatum {

    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;
    @SerializedName("requestID")
    @Expose
    private String requestID;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("created_user")
    @Expose
    private String createdUser;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public String getStockreqid() {
        return stockreqid;
    }

    public void setStockreqid(String stockreqid) {
        this.stockreqid = stockreqid;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

}