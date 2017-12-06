package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/6/17.
 */

public class Packed {
    private String packageID;
    private String orderId;

    public String getPackageID() {
        return packageID;
    }

    public String getOrderId() {
        return orderId;
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

    public String getExpDOD() {
        return expDOD;
    }

    public String getPincode() {
        return pincode;
    }

    private String invoiceNumber;
    private String invoiceDate;
    private String customerName;
    private String expDOD;
    private String pincode;

    public void setPackageID(String packageID) {
        this.packageID = packageID;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public void setExpDOD(String expDOD) {
        this.expDOD = expDOD;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
