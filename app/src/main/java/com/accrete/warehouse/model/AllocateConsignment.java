package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/3/18.
 */

public class AllocateConsignment {

    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("variation_name")
    @Expose
    private String variationName;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("allocated_qty")
    @Expose
    private String allocatedQty;
    @SerializedName("order_measurement")
    @Expose
    private String orderMeasurement;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("item_measurement")
    @Expose
    private Object itemMeasurement;
    @SerializedName("iitid")
    @Expose
    private String iitid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    @SerializedName("internal_code")
    @Expose
    private String internalCode;
    @SerializedName("isid")
    @Expose
    private String isid;
    @SerializedName("iscid")
    @Expose
    private String iscid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("isid_number")
    @Expose
    private String isidNumber;
    @SerializedName("orderID")
    @Expose
    private String orderID;

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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAllocatedQty() {
        return allocatedQty;
    }

    public void setAllocatedQty(String allocatedQty) {
        this.allocatedQty = allocatedQty;
    }

    public String getOrderMeasurement() {
        return orderMeasurement;
    }

    public void setOrderMeasurement(String orderMeasurement) {
        this.orderMeasurement = orderMeasurement;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Object getItemMeasurement() {
        return itemMeasurement;
    }

    public void setItemMeasurement(Object itemMeasurement) {
        this.itemMeasurement = itemMeasurement;
    }

    public String getIitid() {
        return iitid;
    }

    public void setIitid(String iitid) {
        this.iitid = iitid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
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

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getIscid() {
        return iscid;
    }

    public void setIscid(String iscid) {
        this.iscid = iscid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getIsidNumber() {
        return isidNumber;
    }

    public void setIsidNumber(String isidNumber) {
        this.isidNumber = isidNumber;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

}