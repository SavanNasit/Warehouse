package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 14/12/17.
 */

public class QuotationDetails {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
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
    @SerializedName("quotation_remark")
    @Expose
    private String quotationRemark;
    //Added here to show in details screen of quotation
    @SerializedName("qosid")
    @Expose
    private String qosId;
    //TODO Added on 12th June
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getCreatedTs() {

        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getQosId() {
        return qosId;
    }

    public void setQosId(String qosId) {
        this.qosId = qosId;
    }

    public String getShippingAddressState() {
        return shippingAddressState;
    }

    public void setShippingAddressState(String shippingAddressState) {
        this.shippingAddressState = shippingAddressState;
    }

    public String getBillingAddressState() {
        return billingAddressState;
    }

    public void setBillingAddressState(String billingAddressState) {
        this.billingAddressState = billingAddressState;
    }

    public String getQuotationRemark() {
        return quotationRemark;
    }

    public void setQuotationRemark(String quotationRemark) {
        this.quotationRemark = quotationRemark;
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

    public String getBillingAddressCountry() {
        return billingAddressCountry;
    }

    public void setBillingAddressCountry(String billingAddressCountry) {
        this.billingAddressCountry = billingAddressCountry;
    }
}
