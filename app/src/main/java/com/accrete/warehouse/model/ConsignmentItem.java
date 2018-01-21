package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 20/1/18.
 */

public class ConsignmentItem {
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("iid")
    @Expose

    private String iid;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("internal_code")
    @Expose
    private String internalCode;
    @SerializedName("order_quantity")
    @Expose
    private String orderQuantity;
    @SerializedName("receive_quantity")
    @Expose
    private String receiveQuantity;
    @SerializedName("measurements")
    @Expose
    private String measurements;

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(String orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getReceiveQuantity() {
        return receiveQuantity;
    }

    public void setReceiveQuantity(String receiveQuantity) {
        this.receiveQuantity = receiveQuantity;
    }

    public String getMeasurements() {
        return measurements;
    }

    public void setMeasurements(String measurements) {
        this.measurements = measurements;
    }
}
