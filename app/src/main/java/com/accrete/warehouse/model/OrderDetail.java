package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 19/1/18.
 */

public class OrderDetail {
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("subTotal")
    @Expose
    private String subTotal;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("roundOff")
    @Expose
    private String roundOff;
    @SerializedName("payable")
    @Expose
    private String payable;

    @SerializedName("created_ts")
    @Expose
    private String poDate;



    public String getPoDate() {
        return poDate;
    }

    public void setPoDate(String poDate) {
        this.poDate = poDate;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRoundOff() {
        return roundOff;
    }

    public void setRoundOff(String roundOff) {
        this.roundOff = roundOff;
    }

    public String getPayable() {
        return payable;
    }

    public void setPayable(String payable) {
        this.payable = payable;
    }

}
