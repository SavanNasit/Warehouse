package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 22/12/17.
 */

public class ChargesList2 {

    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ecid")
    @Expose
    private String ecid;
    @SerializedName("ectid")
    @Expose
    private String ectid;
    private String discountAmountValue;
    @SerializedName("value")
    @Expose
    private String discountValue;

    public String getDiscountAmountValue() {
        return discountAmountValue;
    }

    public void setDiscountAmountValue(String discountAmountValue) {
        this.discountAmountValue = discountAmountValue;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getEctid() {
        return ectid;
    }

    public void setEctid(String ectid) {
        this.ectid = ectid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEcid() {
        return ecid;
    }

    public void setEcid(String ecid) {
        this.ecid = ecid;
    }
}
