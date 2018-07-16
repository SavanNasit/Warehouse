package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by agt on 20/1/18.
 */

public class ConsignmentItem {
    @SerializedName("oiid")
    @Expose
    private String oiid;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("unit_price")
    @Expose
    private String unitPrice;
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
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    @SerializedName("order_quantity")
    @Expose
    private String orderQuantity;
    @SerializedName("receive_quantity")
    @Expose
    private String receiveQuantity;
    @SerializedName("box_quantity")
    @Expose
    private String boxQuantity;
    @SerializedName("measurement_unit")
    @Expose
    private String measurementUnit;
    @SerializedName("convertsionRate")
    @Expose
    private String convertsionRate;
    @SerializedName("measurements")
    @Expose
    private List<Measurements> measurements = null;
    @SerializedName("barcode_mandatory")
    @Expose
    private String barcodeMandatory;
    //TODO Added on 16th July 2k18
    @SerializedName("manufacturing_date")
    @Expose
    private String manufacturingDate;
    @SerializedName("expiring_date")
    @Expose
    private String expiringDate;
    @SerializedName("barcode")
    @Expose
    private String barcode;
    @SerializedName("rejected_reason")
    @Expose
    private String rejectedReason;
    @SerializedName("rejected_quantity")
    @Expose
    private String rejectedQuantity;
    @SerializedName("iscpuiid")
    @Expose
    private String iscpuiid;
    @SerializedName("isid")
    @Expose
    private String isid;
    @SerializedName("consignment")
    @Expose
    private String consignment;
    @SerializedName("comment")
    @Expose
    private String comment;
    private String expiryDate;
    private String reasonRejection;
    private String price;
    private String unitId;

    public String getExpiringDate() {
        return expiringDate;
    }

    public void setExpiringDate(String expiringDate) {
        this.expiringDate = expiringDate;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }

    public String getIscpuiid() {
        return iscpuiid;
    }

    public void setIscpuiid(String iscpuiid) {
        this.iscpuiid = iscpuiid;
    }

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getConsignment() {
        return consignment;
    }

    public void setConsignment(String consignment) {
        this.consignment = consignment;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
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

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
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

    public String getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(String boxQuantity) {
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

    public List<Measurements> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurements> measurements) {
        this.measurements = measurements;
    }

    public String getBarcodeMandatory() {
        return barcodeMandatory;
    }

    public void setBarcodeMandatory(String barcodeMandatory) {
        this.barcodeMandatory = barcodeMandatory;
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


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }


    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }


    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(String manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public String getOiid() {
        return oiid;
    }

    public void setOiid(String oiid) {
        this.oiid = oiid;
    }

}