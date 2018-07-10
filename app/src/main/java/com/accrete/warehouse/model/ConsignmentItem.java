package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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
    private List<Measurement> measurements = null;
    @SerializedName("box_qnty")
    @Expose
    private String boxQnty;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("unit_price")
    @Expose
    private String unitPrice;


    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    @SerializedName("measurement_unit")
    @Expose
    private String measurementUnit;


    private String price;
    private String unitId;
    private String comment;
    private String expiryDate;
    private String rejectedQuantity;
    private String reasonRejection;

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getRejectedQuantity() {
        return rejectedQuantity;
    }

    public void setRejectedQuantity(String rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

    public String getReasonRejection() {
        return reasonRejection;
    }

    public void setReasonRejection(String reasonRejection) {
        this.reasonRejection = reasonRejection;
    }

    public String getBoxQnty() {
        return boxQnty;
    }

    public void setBoxQnty(String boxQnty) {
        this.boxQnty = boxQnty;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

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

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }


}
