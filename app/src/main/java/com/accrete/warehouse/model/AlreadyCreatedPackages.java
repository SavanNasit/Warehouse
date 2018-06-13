package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 1/24/18.
 */

public class AlreadyCreatedPackages implements Parcelable{
    @SerializedName("packageId")
    @Expose
    private String packageId;

    public String getInvid() {
        return invid;
    }

    public void setInvid(String invid) {
        this.invid = invid;
    }

    @SerializedName("invid")
    @Expose
    private String invid;
    @SerializedName("orderID")
    @Expose
    private String orderID;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("pacid")
    @Expose
    private String pacid;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("invoice_date")
    @Expose
    private String invoiceDate;
    @SerializedName("customerName")
    @Expose
    private String customerName;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("to_date")
    @Expose
    private String toDate;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("invoice_print_flag")
    @Expose
    private  Boolean printFlag;

    public AlreadyCreatedPackages(Parcel in) {
        packageId = in.readString();
        orderID = in.readString();
        chkoid = in.readString();
        pacid = in.readString();
        invoiceNo = in.readString();
        invoiceDate = in.readString();
        customerName = in.readString();
        cuid = in.readString();
        zipCode = in.readString();
        toDate = in.readString();
        paymentStatus = in.readString();
        invid = in.readString();
        printFlag = in.readInt() == 1;
    }

    public static final Creator<AlreadyCreatedPackages> CREATOR = new Creator<AlreadyCreatedPackages>() {
        @Override
        public AlreadyCreatedPackages createFromParcel(Parcel in) {
            return new AlreadyCreatedPackages(in);
        }

        @Override
        public AlreadyCreatedPackages[] newArray(int size) {
            return new AlreadyCreatedPackages[size];
        }
    };

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Boolean getPrintFlag() {
        return printFlag;
    }

    public void setPrintFlag(Boolean printFlag) {
        this.printFlag = printFlag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageId);
        dest.writeString(orderID);
        dest.writeString(chkoid);
        dest.writeString(pacid);
        dest.writeString(invoiceNo);
        dest.writeString(invoiceDate);
        dest.writeString(customerName);
        dest.writeString(cuid);
        dest.writeString(zipCode);
        dest.writeString(toDate);
        dest.writeString(paymentStatus);
        dest.writeString(invid);
        dest.writeInt(printFlag ? 1 : 0);    }
}
