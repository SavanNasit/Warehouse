package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/3/18.
 */

public class ConsumptionConsignment {

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
    private Object orderMeasurement;
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
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("pacshlid")
    @Expose
    private Object pacshlid;
    @SerializedName("status_name")
    @Expose
    private String statusName;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("isid_number")
    @Expose
    private String isidNumber;
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("allocated_stock")
    @Expose
    private String allocatedStock;

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

    public Object getOrderMeasurement() {
        return orderMeasurement;
    }

    public void setOrderMeasurement(Object orderMeasurement) {
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

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public Object getPacshlid() {
        return pacshlid;
    }

    public void setPacshlid(Object pacshlid) {
        this.pacshlid = pacshlid;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
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

    public String getAllocatedStock() {
        return allocatedStock;
    }

    public void setAllocatedStock(String allocatedStock) {
        this.allocatedStock = allocatedStock;
    }

}
