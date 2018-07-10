package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Created by agt on 15/12/17.
 */

public class CustomerInfo {
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("company_website")
    @Expose
    private String companyWebsite;
    @SerializedName("salutationId")
    @Expose
    private String salutationId;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("customer-type-radio")
    @Expose
    private String customerTypeRadio;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("telephone")
    @Expose
    private String telephone;
    @SerializedName("gstin")
    @Expose
    private String gstin;
    @SerializedName("soft_credit_limit")
    @Expose
    private String softCreditLimit;
    @SerializedName("hard_credit_limit")
    @Expose
    private String hardCreditLimit;
    @SerializedName("soft_credit_days")
    @Expose
    private String softCreditDays;
    @SerializedName("hard_credit_days")
    @Expose
    private String hardCreditDays;
    @SerializedName("customer_typeID")
    @Expose
    private String customerTypeID;
    @SerializedName("customer_saleType")
    @Expose
    private String customerSaleType;
    @SerializedName("pan")
    @Expose
    private String pan;
    @SerializedName("tin")
    @Expose
    private String tin;
    @SerializedName("cst")
    @Expose
    private String cst;
    @SerializedName("contact_details")
    @Expose
    private List<ContactDetail> contactDetails = null;
    @SerializedName("referal_data")
    @Expose
    private List<ReferalData> referalData = null;
    @SerializedName("contact_personArr")
    @Expose
    private List<ContactPersonArr> contactPersonArr = null;
    @SerializedName("collection_date")
    @Expose
    private String collectionDate;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("codeid")
    @Expose
    private String codeid;
    @SerializedName("markup")
    @Expose
    private String markup;
    //TODO - Vars added on 6th APril
    @SerializedName("shipping_address_line1")
    @Expose
    private String shippingAddressLine1;
    @SerializedName("shipping_address_line2")
    @Expose
    private String shippingAddressLine2;
    @SerializedName("shipping_address_city")
    @Expose
    private String shippingAddressCity;
    @SerializedName("shipping_address_zipcode")
    @Expose
    private String shippingAddressZipcode;
    @SerializedName("shipping_address_state")
    @Expose
    private String shippingAddressState;
    @SerializedName("shipping_address_country")
    @Expose
    private String shippingAddressCountry;
    @SerializedName("billing_address_line1")
    @Expose
    private String billingAddressLine1;
    @SerializedName("billing_address_line2")
    @Expose
    private String billingAddressLine2;
    @SerializedName("billing_address_city")
    @Expose
    private String billingAddressCity;
    @SerializedName("billing_address_zipcode")
    @Expose
    private String billingAddressZipcode;
    @SerializedName("billing_address_state")
    @Expose
    private String billingAddressState;
    @SerializedName("billing_address_country")
    @Expose
    private String billingAddressCountry;
    @SerializedName("cuid")
    @Expose
    private String cuid;

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getShippingAddressLine1() {
        return shippingAddressLine1;
    }

    public void setShippingAddressLine1(String shippingAddressLine1) {
        this.shippingAddressLine1 = shippingAddressLine1;
    }

    public String getShippingAddressLine2() {
        return shippingAddressLine2;
    }

    public void setShippingAddressLine2(String shippingAddressLine2) {
        this.shippingAddressLine2 = shippingAddressLine2;
    }

    public String getShippingAddressCity() {
        return shippingAddressCity;
    }

    public void setShippingAddressCity(String shippingAddressCity) {
        this.shippingAddressCity = shippingAddressCity;
    }

    public String getShippingAddressZipcode() {
        return shippingAddressZipcode;
    }

    public void setShippingAddressZipcode(String shippingAddressZipcode) {
        this.shippingAddressZipcode = shippingAddressZipcode;
    }

    public String getShippingAddressState() {
        return shippingAddressState;
    }

    public void setShippingAddressState(String shippingAddressState) {
        this.shippingAddressState = shippingAddressState;
    }

    public String getShippingAddressCountry() {
        return shippingAddressCountry;
    }

    public void setShippingAddressCountry(String shippingAddressCountry) {
        this.shippingAddressCountry = shippingAddressCountry;
    }

    public String getBillingAddressLine1() {
        return billingAddressLine1;
    }

    public void setBillingAddressLine1(String billingAddressLine1) {
        this.billingAddressLine1 = billingAddressLine1;
    }

    public String getBillingAddressLine2() {
        return billingAddressLine2;
    }

    public void setBillingAddressLine2(String billingAddressLine2) {
        this.billingAddressLine2 = billingAddressLine2;
    }

    public String getBillingAddressCity() {
        return billingAddressCity;
    }

    public void setBillingAddressCity(String billingAddressCity) {
        this.billingAddressCity = billingAddressCity;
    }

    public String getBillingAddressZipcode() {
        return billingAddressZipcode;
    }

    public void setBillingAddressZipcode(String billingAddressZipcode) {
        this.billingAddressZipcode = billingAddressZipcode;
    }

    public String getBillingAddressState() {
        return billingAddressState;
    }

    public void setBillingAddressState(String billingAddressState) {
        this.billingAddressState = billingAddressState;
    }

    public String getBillingAddressCountry() {
        return billingAddressCountry;
    }

    public void setBillingAddressCountry(String billingAddressCountry) {
        this.billingAddressCountry = billingAddressCountry;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getMarkup() {
        return markup;
    }

    public void setMarkup(String markup) {
        this.markup = markup;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getCustomerTypeRadio() {
        return customerTypeRadio;
    }

    public void setCustomerTypeRadio(String customerTypeRadio) {
        this.customerTypeRadio = customerTypeRadio;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getGstin() {
        return gstin;
    }

    public void setGstin(String gstin) {
        this.gstin = gstin;
    }

    public String getSoftCreditLimit() {
        return softCreditLimit;
    }

    public void setSoftCreditLimit(String softCreditLimit) {
        this.softCreditLimit = softCreditLimit;
    }

    public String getHardCreditLimit() {
        return hardCreditLimit;
    }

    public void setHardCreditLimit(String hardCreditLimit) {
        this.hardCreditLimit = hardCreditLimit;
    }

    public String getSoftCreditDays() {
        return softCreditDays;
    }

    public void setSoftCreditDays(String softCreditDays) {
        this.softCreditDays = softCreditDays;
    }

    public String getHardCreditDays() {
        return hardCreditDays;
    }

    public void setHardCreditDays(String hardCreditDays) {
        this.hardCreditDays = hardCreditDays;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyWebsite() {
        return companyWebsite;
    }

    public void setCompanyWebsite(String companyWebsite) {
        this.companyWebsite = companyWebsite;
    }

    public List<ReferalData> getReferalData() {
        return referalData;
    }

    public void setReferalData(List<ReferalData> referalData) {
        this.referalData = referalData;
    }

    public List<ContactPersonArr> getContactPersonArr() {
        return contactPersonArr;
    }

    public void setContactPersonArr(List<ContactPersonArr> contactPersonArr) {
        this.contactPersonArr = contactPersonArr;
    }

    public List<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getCustomerTypeID() {
        return customerTypeID;
    }

    public void setCustomerTypeID(String customerTypeID) {
        this.customerTypeID = customerTypeID;
    }

    public String getCustomerSaleType() {
        return customerSaleType;
    }

    public void setCustomerSaleType(String customerSaleType) {
        this.customerSaleType = customerSaleType;
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

}
