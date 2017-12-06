package com.accrete.warehouse.model;

/**
 * Created by poonam on 12/5/17.
 */

public class ManageConsignment {
    private String consignmentID;
    private String purchaseOrder;
    private String invoiceNumber;
    private String invoiceDate;
    private String purchaseOrderDate;

    public String getConsignmentID() {
        return consignmentID;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public String getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    public String getVendor() {
        return vendor;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public String getReceivedOn() {
        return receivedOn;
    }

    public String getStatus() {
        return status;
    }

    private String vendor;
    private String warehouse;
    private String receivedOn;
    private String status;

    public void setConsignmentID(String consignmentID) {
        this.consignmentID = consignmentID;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public void setReceivedOn(String receivedOn) {
        this.receivedOn = receivedOn;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
