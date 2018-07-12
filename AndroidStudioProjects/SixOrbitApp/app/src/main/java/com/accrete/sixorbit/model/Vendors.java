package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 6/11/17.
 */

public class Vendors {
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("alid")
    @Expose
    private String alid;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("registered")
    @Expose
    private String registered;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("coverid")
    @Expose
    private String coverid;
    @SerializedName("coverlid")
    @Expose
    private String coverlid;
    @SerializedName("stid")
    @Expose
    private String stid;
    @SerializedName("ctid")
    @Expose
    private String ctid;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("landline")
    @Expose
    private String landline;
    @SerializedName("person_of_contact")
    @Expose
    private String personOfContact;
    @SerializedName("vendor_email")
    @Expose
    private String vendorEmail;
    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("payment_terms")
    @Expose
    private String paymentTerms;
    @SerializedName("payment_day")
    @Expose
    private String paymentDay;
    @SerializedName("credit_limit")
    @Expose
    private String creditLimit;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("vensid")
    @Expose
    private String vensid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("vendor_number")
    @Expose
    private String vendorNumber;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("v_name")
    @Expose
    private String vName;

    public String getVendorNumber() {
        return vendorNumber;
    }

    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    public String getvName() {
        return vName;
    }

    public void setvName(String vName) {
        this.vName = vName;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getAlid() {
        return alid;
    }

    public void setAlid(String alid) {
        this.alid = alid;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCoverid() {
        return coverid;
    }

    public void setCoverid(String coverid) {
        this.coverid = coverid;
    }

    public String getCoverlid() {
        return coverlid;
    }

    public void setCoverlid(String coverlid) {
        this.coverlid = coverlid;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getPersonOfContact() {
        return personOfContact;
    }

    public void setPersonOfContact(String personOfContact) {
        this.personOfContact = personOfContact;
    }

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public String getPaymentDay() {
        return paymentDay;
    }

    public void setPaymentDay(String paymentDay) {
        this.paymentDay = paymentDay;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVensid() {
        return vensid;
    }

    public void setVensid(String vensid) {
        this.vensid = vensid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(String updatedTs) {
        this.updatedTs = updatedTs;
    }
}
