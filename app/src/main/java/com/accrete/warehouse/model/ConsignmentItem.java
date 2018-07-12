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
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    @SerializedName("internal_code")
    @Expose
    private String internalCode;
    @SerializedName("order_quantity")
    @Expose
    private String orderQuantity;
    @SerializedName("receive_quantity")
    @Expose
    private String receiveQuantity;
    @SerializedName("box_qty_str")
    @Expose
    private String boxQtyStr;
    @SerializedName("box_quantity")
    @Expose
    private Integer boxQuantity;
    @SerializedName("measurement_unit")
    @Expose
    private String measurementUnit;
    @SerializedName("convertsionRate")
    @Expose
    private String convertsionRate;
    @SerializedName("measurements")
    @Expose
    private List<Measurement> measurements = null;
    @SerializedName("unit_price")
    @Expose
    private String unitPrice;
    @SerializedName("barcode_mandatory")
    @Expose
    private String barcodeMandatory;

    private String price;

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
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

    private String unitId;
    private String comment;
    private String expiryDate;
    private String rejectedQuantity;
    private String reasonRejection;


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

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
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

    public String getBoxQtyStr() {
        return boxQtyStr;
    }

    public void setBoxQtyStr(String boxQtyStr) {
        this.boxQtyStr = boxQtyStr;
    }

    public Integer getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(Integer boxQuantity) {
        this.boxQuantity = boxQuantity;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getConvertsionRate() {
        return convertsionRate;
    }

    public void setConvertsionRate(String convertsionRate) {
        this.convertsionRate = convertsionRate;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBarcodeMandatory() {
        return barcodeMandatory;
    }

    public void setBarcodeMandatory(String barcodeMandatory) {
        this.barcodeMandatory = barcodeMandatory;
    }

}