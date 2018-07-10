package com.accrete.sixorbit.model;

import com.accrete.sixorbit.helper.ContactDetail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 13/10/17.
 */

public class Customer {
    @SerializedName("alid")
    @Expose
    private String alid;
    @SerializedName("salutation_id")
    @Expose
    private String salutationId;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("customer_type")
    @Expose
    private String customerType;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("genderid")
    @Expose
    private String genderid;
    @SerializedName("custatusid")
    @Expose
    private String custatusid;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("gst_registered")
    @Expose
    private String gstRegistered;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("tin")
    @Expose
    private String tin;
    @SerializedName("cst_no")
    @Expose
    private String cst;
    @SerializedName("telephone")
    @Expose
    private String telephone;
    @SerializedName("website_name")
    @Expose
    private String website_name;
    @SerializedName("customer_shipping_address")
    @Expose
    private List<CustomerShippingAddress> customerShippingAddresses = null;
    @SerializedName("wallet_balance")
    @Expose
    private String walletBalance;
    @SerializedName("contact_details")
    @Expose
    private List<ContactDetail> contactDetails = null;
    @SerializedName("reference_name")
    @Expose
    private String referenceName;
    @SerializedName("reference_type")
    @Expose
    private String referenceType;
    @SerializedName("reference_contact_person")
    @Expose
    private String referenceContactPerson;

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceType() {
        return referenceType;
    }

    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }

    public String getReferenceContactPerson() {
        return referenceContactPerson;
    }

    public void setReferenceContactPerson(String referenceContactPerson) {
        this.referenceContactPerson = referenceContactPerson;
    }

    public String getWebsite_name() {
        return website_name;
    }

    public void setWebsite_name(String website_name) {
        this.website_name = website_name;
    }

    public String getAlid() {
        return alid;
    }

    public void setAlid(String alid) {
        this.alid = alid;
    }

    public String getSalutationId() {
        return salutationId;
    }

    public void setSalutationId(String salutationId) {
        this.salutationId = salutationId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getGenderid() {
        return genderid;
    }

    public void setGenderid(String genderid) {
        this.genderid = genderid;
    }

    public String getCustatusid() {
        return custatusid;
    }

    public void setCustatusid(String custatusid) {
        this.custatusid = custatusid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
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

    public String getCst() {
        return cst;
    }

    public void setCst(String cst) {
        this.cst = cst;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public List<CustomerShippingAddress> getCustomerShippingAddresses() {
        return customerShippingAddresses;
    }

    public void setCustomerShippingAddresses(List<CustomerShippingAddress> customerShippingAddresses) {
        this.customerShippingAddresses = customerShippingAddresses;
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
}
