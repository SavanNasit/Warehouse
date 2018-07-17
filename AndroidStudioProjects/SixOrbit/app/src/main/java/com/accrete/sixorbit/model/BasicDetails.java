package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 29/6/18.
 */

public class BasicDetails {
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("reason")
    @Expose

    private String reason;
    @SerializedName("remarks")
    @Expose
    private String remarks;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
