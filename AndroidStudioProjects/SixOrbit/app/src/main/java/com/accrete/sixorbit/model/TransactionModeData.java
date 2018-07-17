package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 30/5/18.
 */

public class TransactionModeData {
    @SerializedName("cpoaid")
    @Expose
    private Integer cpoaid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("value")
    @Expose
    private String value;

    public Integer getCpoaid() {
        return cpoaid;
    }

    public void setCpoaid(Integer cpoaid) {
        this.cpoaid = cpoaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
