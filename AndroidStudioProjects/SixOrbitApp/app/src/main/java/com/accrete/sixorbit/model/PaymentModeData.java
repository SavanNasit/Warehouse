package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 30/3/18.
 */

public class PaymentModeData {
    @SerializedName("cpoid")
    @Expose
    private String cpoid;
    @SerializedName("cptid")
    @Expose
    private String cptid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("payment_option_attribute")
    @Expose
    private List<PaymentOptionAttribute> paymentOptionAttribute = null;
    @SerializedName("agid")
    @Expose
    private String agid;

    public String getAgid() {
        return agid;
    }

    public void setAgid(String agid) {
        this.agid = agid;
    }

    public List<PaymentOptionAttribute> getPaymentOptionAttribute() {
        return paymentOptionAttribute;
    }

    public void setPaymentOptionAttribute(List<PaymentOptionAttribute> paymentOptionAttribute) {
        this.paymentOptionAttribute = paymentOptionAttribute;
    }

    public String getCpoid() {
        return cpoid;
    }

    public void setCpoid(String cpoid) {
        this.cpoid = cpoid;
    }

    public String getCptid() {
        return cptid;
    }

    public void setCptid(String cptid) {
        this.cptid = cptid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
