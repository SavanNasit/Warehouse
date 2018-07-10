package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by {Anshul} on 5/6/18.
 */

public class CustomerData {
    @SerializedName("customer_name")
    @Expose
    private String customerName;
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("jobcard_id")
    @Expose
    private String jobcardId;
    @SerializedName("stockreq_id")
    @Expose
    private String stockreqId;
    @SerializedName("shipping_address")
    @Expose
    private String shippingAddress;
    @SerializedName("billing_address")
    @Expose
    private String billingAddress;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("name")
    @Expose
    private String name;
    //TODO Added on 29th June
    @SerializedName("company_name")
    @Expose
    private String companyName;
    /* @SerializedName("contact_details")
     @Expose
     private List<Object> contactDetails = null;*/
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
    @SerializedName("department")
    @Expose
    private String department;
    @SerializedName("ensid")
    @Expose
    private String ensid;

    public String getEnsid() {
        return ensid;
    }

    public void setEnsid(String ensid) {
        this.ensid = ensid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getJobcardId() {
        return jobcardId;
    }

    public void setJobcardId(String jobcardId) {
        this.jobcardId = jobcardId;
    }

    public String getStockreqId() {
        return stockreqId;
    }

    public void setStockreqId(String stockreqId) {
        this.stockreqId = stockreqId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
