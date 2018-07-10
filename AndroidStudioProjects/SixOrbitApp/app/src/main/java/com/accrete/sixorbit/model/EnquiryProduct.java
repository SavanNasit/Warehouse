package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by poonam on 31/10/17.
 */


public class EnquiryProduct {

    @SerializedName("enpid")
    @Expose
    private String enpid;
    @SerializedName("enid")
    @Expose
    private String enid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("variation_name")
    @Expose
    private String variationName;
    @SerializedName("enpsid")
    @Expose
    private String enpsid;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("keys")
    @Expose
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getEnpid() {
        return enpid;
    }

    public void setEnpid(String enpid) {
        this.enpid = enpid;
    }

    public String getEnid() {
        return enid;
    }

    public void setEnid(String enid) {
        this.enid = enid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getVariationName() {
        return variationName;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public String getEnpsid() {
        return enpsid;
    }

    public void setEnpsid(String enpsid) {
        this.enpsid = enpsid;
    }
}