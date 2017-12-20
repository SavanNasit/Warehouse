package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/12/17.
 */

public class Packages implements Parcelable{

    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("pacshsid")
    @Expose
    private String pacshsid;
    @SerializedName("pacshtid")
    @Expose
    private String pacshtid;
    @SerializedName("tracking_id")
    @Expose
    private String trackingId;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("scompid")
    @Expose
    private String scompid;
    @SerializedName("oid")
    @Expose
    private String oid;
    @SerializedName("pacsid")
    @Expose
    private String pacsid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;

    protected Packages(Parcel in) {
        pacid = in.readString();
        invid = in.readString();
        pacshsid = in.readString();
        pacshtid = in.readString();
        trackingId = in.readString();
        chkoid = in.readString();
        scompid = in.readString();
        oid = in.readString();
        pacsid = in.readString();
        chkid = in.readString();
        uid = in.readString();
        cuid = in.readString();
        invoiceNo = in.readString();
        invoiceDate = in.readString();
        zipCode = in.readString();
        customerName = in.readString();
        toDate = in.readString();
        createdTs = in.readString();
        stockreqid = in.readString();
    }

    public Packages(){

    }
    public static final Creator<Packages> CREATOR = new Creator<Packages>() {
        @Override
        public Packages createFromParcel(Parcel in) {
            return new Packages(in);
        }

        @Override
        public Packages[] newArray(int size) {
            return new Packages[size];
        }
    };

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    public String getPacshsid() {
        return pacshsid;
    }

    public void setPacshsid(String pacshsid) {
        this.pacshsid = pacshsid;
    }

    public String getPacshtid() {
        return pacshtid;
    }

    public void setPacshtid(String pacshtid) {
        this.pacshtid = pacshtid;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getScompid() {
        return scompid;
    }

    public void setScompid(String scompid) {
        this.scompid = scompid;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getPacsid() {
        return pacsid;
    }

    public void setPacsid(String pacsid) {
        this.pacsid = pacsid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getStockreqid() {
        return stockreqid;
    }

    public void setStockreqid(String stockreqid) {
        this.stockreqid = stockreqid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pacid);
        dest.writeString(invid);
        dest.writeString(pacshsid);
        dest.writeString(pacshtid);
        dest.writeString(trackingId);
        dest.writeString(chkoid);
        dest.writeString(scompid);
        dest.writeString(oid);
        dest.writeString(pacsid);
        dest.writeString(chkid);
        dest.writeString(uid);
        dest.writeString(cuid);
        dest.writeString(invoiceNo);
        dest.writeString(invoiceDate);
        dest.writeString(zipCode);
        dest.writeString(customerName);
        dest.writeString(toDate);
        dest.writeString(createdTs);
        dest.writeString(stockreqid);
    }
}