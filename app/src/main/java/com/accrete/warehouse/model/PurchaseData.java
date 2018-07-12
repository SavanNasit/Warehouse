package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/11/18.
 */

public class PurchaseData {
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("receive_date")
    @Expose
    private String receiveDate;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(String receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

}



