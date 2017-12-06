package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/30/17.
 */

public class OutForDelivery{
    private String deliveryUser;
    private String packageId;
    private String invoiceNumber;

    public String getDeliveryUser() {
        return deliveryUser;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getGatePassId() {
        return gatePassId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getExpdod() {
        return expdod;
    }

    private String invoiceDate;
    private String customerName;
    private String gatePassId;
    private String orderId;
    private String expdod;

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setGatePassId(String gatePassId) {
        this.gatePassId = gatePassId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setExpdod(String expdod) {
        this.expdod = expdod;
    }

    public void setDeliveryUser(String deliveryUser) {
        this.deliveryUser = deliveryUser;
    }
}
