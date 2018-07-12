package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 3/4/18.
 */

public class PaymentOptionAttribute {
    @SerializedName("cpoaid")
    @Expose
    private String cpoaid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("required")
    @Expose
    private String required;
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCpoaid() {
        return cpoaid;
    }

    public void setCpoaid(String cpoaid) {
        this.cpoaid = cpoaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

}
