package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/12/17.
 */

public class CustomerInfo implements Parcelable {
    public static final Creator<CustomerInfo> CREATOR = new Creator<CustomerInfo>() {
        @Override
        public CustomerInfo createFromParcel(Parcel in) {
            return new CustomerInfo(in);
        }

        @Override
        public CustomerInfo[] newArray(int size) {
            return new CustomerInfo[size];
        }
    };
    @SerializedName("packageId")
    @Expose
    private String packageId;
    @SerializedName("invoiceNo")
    @Expose
    private String invoiceNo;
    @SerializedName("invoiceDate")
    @Expose
    private String invoiceDate;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose

    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("shipping_addr_name")
    @Expose
    private String shippingAddrName;
    @SerializedName("shipping_addr_line")
    @Expose
    private String shippingAddrLine;
    @SerializedName("shipping_addr_city")
    @Expose
    private String shippingAddrCity;
    @SerializedName("shipping_addr_pincode")
    @Expose
    private String shippingAddrPincode;
    @SerializedName("shipping_addr_stateName")
    @Expose
    private String shippingAddrStateName;
    @SerializedName("shipping_addr_countryName")
    @Expose
    private String shippingAddrCountryName;
    @SerializedName("shipping_addr_mobile")
    @Expose
    private String shippingAddrMobile;
    @SerializedName("billing_addr_name")
    @Expose
    private String billingAddrName;
    @SerializedName("billing_addr_line")
    @Expose
    private String billingAddrLine;
    @SerializedName("billing_addr_city")
    @Expose
    private String billingAddrCity;
    @SerializedName("billing_addr_pincode")
    @Expose
    private String billingAddrPincode;
    @SerializedName("billing_addr_stateName")
    @Expose
    private String billingAddrStateName;
    @SerializedName("billing_addr_countryName")
    @Expose
    private String billingAddrCountryName;
    @SerializedName("billing_addr_mobile")
    @Expose
    private String billingAddrMobile;
    protected CustomerInfo(Parcel in) {
        name = in.readString();
        email = in.readString();
        mobile = in.readString();
        shippingAddrName = in.readString();
        shippingAddrLine = in.readString();
        shippingAddrCity = in.readString();
        shippingAddrPincode = in.readString();
        shippingAddrStateName = in.readString();
        shippingAddrCountryName = in.readString();
        shippingAddrMobile = in.readString();
        billingAddrName = in.readString();
        billingAddrLine = in.readString();
        billingAddrCity = in.readString();
        billingAddrPincode = in.readString();
        billingAddrStateName = in.readString();
        billingAddrCountryName = in.readString();
        billingAddrMobile = in.readString();
    }
    public CustomerInfo() {

    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getShippingAddrName() {
        return shippingAddrName;
    }

    public void setShippingAddrName(String shippingAddrName) {
        this.shippingAddrName = shippingAddrName;
    }

    public String getShippingAddrLine() {
        return shippingAddrLine;
    }

    public void setShippingAddrLine(String shippingAddrLine) {
        this.shippingAddrLine = shippingAddrLine;
    }

    public String getShippingAddrCity() {
        return shippingAddrCity;
    }

    public void setShippingAddrCity(String shippingAddrCity) {
        this.shippingAddrCity = shippingAddrCity;
    }

    public String getShippingAddrPincode() {
        return shippingAddrPincode;
    }

    public void setShippingAddrPincode(String shippingAddrPincode) {
        this.shippingAddrPincode = shippingAddrPincode;
    }

    public String getShippingAddrStateName() {
        return shippingAddrStateName;
    }

    public void setShippingAddrStateName(String shippingAddrStateName) {
        this.shippingAddrStateName = shippingAddrStateName;
    }

    public String getShippingAddrCountryName() {
        return shippingAddrCountryName;
    }

    public void setShippingAddrCountryName(String shippingAddrCountryName) {
        this.shippingAddrCountryName = shippingAddrCountryName;
    }

    public String getShippingAddrMobile() {
        return shippingAddrMobile;
    }

    public void setShippingAddrMobile(String shippingAddrMobile) {
        this.shippingAddrMobile = shippingAddrMobile;
    }

    public String getBillingAddrName() {
        return billingAddrName;
    }

    public void setBillingAddrName(String billingAddrName) {
        this.billingAddrName = billingAddrName;
    }

    public String getBillingAddrLine() {
        return billingAddrLine;
    }

    public void setBillingAddrLine(String billingAddrLine) {
        this.billingAddrLine = billingAddrLine;
    }

    public String getBillingAddrCity() {
        return billingAddrCity;
    }

    public void setBillingAddrCity(String billingAddrCity) {
        this.billingAddrCity = billingAddrCity;
    }

    public String getBillingAddrPincode() {
        return billingAddrPincode;
    }

    public void setBillingAddrPincode(String billingAddrPincode) {
        this.billingAddrPincode = billingAddrPincode;
    }

    public String getBillingAddrStateName() {
        return billingAddrStateName;
    }

    public void setBillingAddrStateName(String billingAddrStateName) {
        this.billingAddrStateName = billingAddrStateName;
    }

    public String getBillingAddrCountryName() {
        return billingAddrCountryName;
    }

    public void setBillingAddrCountryName(String billingAddrCountryName) {
        this.billingAddrCountryName = billingAddrCountryName;
    }

    public String getBillingAddrMobile() {
        return billingAddrMobile;
    }

    public void setBillingAddrMobile(String billingAddrMobile) {
        this.billingAddrMobile = billingAddrMobile;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(mobile);
        dest.writeString(shippingAddrName);
        dest.writeString(shippingAddrLine);
        dest.writeString(shippingAddrCity);
        dest.writeString(shippingAddrPincode);
        dest.writeString(shippingAddrStateName);
        dest.writeString(shippingAddrCountryName);
        dest.writeString(shippingAddrMobile);
        dest.writeString(billingAddrName);
        dest.writeString(billingAddrLine);
        dest.writeString(billingAddrCity);
        dest.writeString(billingAddrPincode);
        dest.writeString(billingAddrStateName);
        dest.writeString(billingAddrCountryName);
        dest.writeString(billingAddrMobile);
    }
}