package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 6/11/18.
 */


public class EditPackageFormdata {
    @SerializedName("invoice_number_label")
    @Expose
    private String invoiceNumberLabel;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("eway_no")
    @Expose
    private String ewayNo;

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getEwayNo() {
        return ewayNo;
    }

    public void setEwayNo(String ewayNo) {
        this.ewayNo = ewayNo;
    }

    public String getInvoiceNumberLabel() {
        return invoiceNumberLabel;
    }

    public void setInvoiceNumberLabel(String invoiceNumberLabel) {
        this.invoiceNumberLabel = invoiceNumberLabel;
    }

}