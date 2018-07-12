package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 16/4/18.
 */

public class QuotationHistory {
    @SerializedName("updated_on")
    @Expose
    private String updatedOn;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;
    @SerializedName("sms_sent")
    @Expose
    private String smsSent;
    @SerializedName("email_sent")
    @Expose
    private String emailSent;

    public String getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(String updatedOn) {
        this.updatedOn = updatedOn;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(String smsSent) {
        this.smsSent = smsSent;
    }

    public String getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(String emailSent) {
        this.emailSent = emailSent;
    }
}
