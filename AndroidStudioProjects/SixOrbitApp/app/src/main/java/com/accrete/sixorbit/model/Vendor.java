package com.accrete.sixorbit.model;

import com.accrete.sixorbit.helper.ContactDetail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 13/10/17.
 */

public class Vendor {

    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("vendor_email")
    @Expose
    private String vendorEmail;
    @SerializedName("contacts")
    @Expose
    private List<Contacts> contacts = null;
    @SerializedName("alid")
    @Expose
    private String alid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("landline")
    @Expose
    private String landline;
    @SerializedName("contact_name")
    @Expose
    private String contactName;
    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("gst_registered")
    @Expose
    private String gstRegistered;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("tin")
    @Expose
    private String tin;
    @SerializedName("excise_duty")
    @Expose
    private String exciseDuty;
    @SerializedName("vendor_shipping_address")
    @Expose
    private List<VendorShippingAddress> vendorShippingAddress = null;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("payment_terms")
    @Expose
    private String paymentTerms;
    @SerializedName("contact_details")
    @Expose
    private List<ContactDetail> contactDetails = null;
    @SerializedName("bank_details")
    @Expose
    private List<BankDetail> bankDetails = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentTerms() {
        return paymentTerms;
    }

    public void setPaymentTerms(String paymentTerms) {
        this.paymentTerms = paymentTerms;
    }

    public List<BankDetail> getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(List<BankDetail> bankDetails) {
        this.bankDetails = bankDetails;
    }

    public List<VendorShippingAddress> getVendorShippingAddress() {
        return vendorShippingAddress;
    }

    public void setVendorShippingAddress(List<VendorShippingAddress> vendorShippingAddress) {
        this.vendorShippingAddress = vendorShippingAddress;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public List<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

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

    public String getVendorEmail() {
        return vendorEmail;
    }

    public void setVendorEmail(String vendorEmail) {
        this.vendorEmail = vendorEmail;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public List<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    public String getAlid() {
        return alid;
    }

    public void setAlid(String alid) {
        this.alid = alid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getGstRegistered() {
        return gstRegistered;
    }

    public void setGstRegistered(String gstRegistered) {
        this.gstRegistered = gstRegistered;
    }

    public String getPan() {
        return pan;
    }

    public void setPan(String pan) {
        this.pan = pan;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getExciseDuty() {
        return exciseDuty;
    }

    public void setExciseDuty(String exciseDuty) {
        this.exciseDuty = exciseDuty;
    }
}
