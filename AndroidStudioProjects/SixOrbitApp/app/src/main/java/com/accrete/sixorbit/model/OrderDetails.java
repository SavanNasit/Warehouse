package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 11/12/17.
 */

public class OrderDetails {

    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("chkosid")
    @Expose
    private String chkosid;
    @SerializedName("order_id")
    @Expose
    private String orderId;
    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("po_number")
    @Expose
    private String poNumber;
    @SerializedName("po_date")
    @Expose
    private String poDate;
    @SerializedName("quotation_id")
    @Expose
    private String quotationId;
    @SerializedName("enquiry_id")
    @Expose
    private String enquiryId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("created_by")
    @Expose
    private String createdBy;
    @SerializedName("last_updatedTs")
    @Expose
    private String lastUpdatedTs;
    @SerializedName("cust_saleType")
    @Expose
    private String custSaleType;
    @SerializedName("order_type")
    @Expose
    private String orderType;
    @SerializedName("assigned_to")
    @Expose
    private String assignedTo;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("billing_address")
    @Expose
    private String billingAddress;
    @SerializedName("site_address")
    @Expose
    private String siteAddress;
    @SerializedName("current_address")
    @Expose
    private String currentAddress;
    //TODO - New variables added on 4th of April
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("last_updated_time")
    @Expose
    private String lastUpdatedTime;
    @SerializedName("customer_sale_type")
    @Expose
    private String customerSaleType;
    @SerializedName("checkpoint_remarks")
    @Expose
    private String checkpointRemarks;
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
    @SerializedName("delivery_address_line1")
    @Expose
    private String deliveryAddressLine1;
    @SerializedName("delivery_address_line2")
    @Expose
    private String deliveryAddressLine2;
    @SerializedName("delivery_address_city")
    @Expose
    private String deliveryAddressCity;
    @SerializedName("delivery_address_zipcode")
    @Expose
    private String deliveryAddressZipcode;
    @SerializedName("delivery_address_state")
    @Expose
    private String deliveryAddressState;
    @SerializedName("delivery_address_country")
    @Expose
    private String deliveryAddressCountry;
    @SerializedName("updatedby")
    @Expose
    private String updatedBy;
    //TODO Added on 10th July
    @SerializedName("current_address_line1")
    @Expose
    private String currentAddressLine1;
    @SerializedName("current_address_line2")
    @Expose
    private String currentAddressLine2;
    @SerializedName("current_address_city")
    @Expose
    private String currentAddressCity;
    @SerializedName("current_address_zipcode")
    @Expose
    private String currentAddressZipcode;
    @SerializedName("current_address_state")
    @Expose
    private String currentAddressState;
    @SerializedName("current_address_country")
    @Expose
    private String currentAddressCountry;
    @SerializedName("site_address_line1")
    @Expose
    private String siteAddressLine1;
    @SerializedName("site_address_line2")
    @Expose
    private String siteAddressLine2;
    @SerializedName("site_address_city")
    @Expose
    private String siteAddressCity;
    @SerializedName("site_address_zipcode")
    @Expose
    private String siteAddressZipcode;
    @SerializedName("site_address_state")
    @Expose
    private String siteAddressState;
    @SerializedName("site_address_country")
    @Expose
    private String siteAddressCountry;

    public String getCurrentAddressLine1() {
        return currentAddressLine1;
    }

    public void setCurrentAddressLine1(String currentAddressLine1) {
        this.currentAddressLine1 = currentAddressLine1;
    }

    public String getCurrentAddressLine2() {
        return currentAddressLine2;
    }

    public void setCurrentAddressLine2(String currentAddressLine2) {
        this.currentAddressLine2 = currentAddressLine2;
    }

    public String getCurrentAddressCity() {
        return currentAddressCity;
    }

    public void setCurrentAddressCity(String currentAddressCity) {
        this.currentAddressCity = currentAddressCity;
    }

    public String getCurrentAddressZipcode() {
        return currentAddressZipcode;
    }

    public void setCurrentAddressZipcode(String currentAddressZipcode) {
        this.currentAddressZipcode = currentAddressZipcode;
    }

    public String getCurrentAddressState() {
        return currentAddressState;
    }

    public void setCurrentAddressState(String currentAddressState) {
        this.currentAddressState = currentAddressState;
    }

    public String getCurrentAddressCountry() {
        return currentAddressCountry;
    }

    public void setCurrentAddressCountry(String currentAddressCountry) {
        this.currentAddressCountry = currentAddressCountry;
    }

    public String getSiteAddressLine1() {
        return siteAddressLine1;
    }

    public void setSiteAddressLine1(String siteAddressLine1) {
        this.siteAddressLine1 = siteAddressLine1;
    }

    public String getSiteAddressLine2() {
        return siteAddressLine2;
    }

    public void setSiteAddressLine2(String siteAddressLine2) {
        this.siteAddressLine2 = siteAddressLine2;
    }

    public String getSiteAddressCity() {
        return siteAddressCity;
    }

    public void setSiteAddressCity(String siteAddressCity) {
        this.siteAddressCity = siteAddressCity;
    }

    public String getSiteAddressZipcode() {
        return siteAddressZipcode;
    }

    public void setSiteAddressZipcode(String siteAddressZipcode) {
        this.siteAddressZipcode = siteAddressZipcode;
    }

    public String getSiteAddressState() {
        return siteAddressState;
    }

    public void setSiteAddressState(String siteAddressState) {
        this.siteAddressState = siteAddressState;
    }

    public String getSiteAddressCountry() {
        return siteAddressCountry;
    }

    public void setSiteAddressCountry(String siteAddressCountry) {
        this.siteAddressCountry = siteAddressCountry;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getDeliveryAddressLine1() {
        return deliveryAddressLine1;
    }

    public void setDeliveryAddressLine1(String deliveryAddressLine1) {
        this.deliveryAddressLine1 = deliveryAddressLine1;
    }

    public String getDeliveryAddressLine2() {
        return deliveryAddressLine2;
    }

    public void setDeliveryAddressLine2(String deliveryAddressLine2) {
        this.deliveryAddressLine2 = deliveryAddressLine2;
    }

    public String getDeliveryAddressCity() {
        return deliveryAddressCity;
    }

    public void setDeliveryAddressCity(String deliveryAddressCity) {
        this.deliveryAddressCity = deliveryAddressCity;
    }

    public String getDeliveryAddressZipcode() {
        return deliveryAddressZipcode;
    }

    public void setDeliveryAddressZipcode(String deliveryAddressZipcode) {
        this.deliveryAddressZipcode = deliveryAddressZipcode;
    }

    public String getDeliveryAddressState() {
        return deliveryAddressState;
    }

    public void setDeliveryAddressState(String deliveryAddressState) {
        this.deliveryAddressState = deliveryAddressState;
    }

    public String getDeliveryAddressCountry() {
        return deliveryAddressCountry;
    }

    public void setDeliveryAddressCountry(String deliveryAddressCountry) {
        this.deliveryAddressCountry = deliveryAddressCountry;
    }

    public String getChkosid() {
        return chkosid;
    }

    public void setChkosid(String chkosid) {
        this.chkosid = chkosid;
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

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }

    public String getCustomerSaleType() {
        return customerSaleType;
    }

    public void setCustomerSaleType(String customerSaleType) {
        this.customerSaleType = customerSaleType;
    }

    public String getCheckpointRemarks() {
        return checkpointRemarks;
    }

    public void setCheckpointRemarks(String checkpointRemarks) {
        this.checkpointRemarks = checkpointRemarks;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getPoDate() {
        return poDate;
    }

    public void setPoDate(String poDate) {
        this.poDate = poDate;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedTs() {
        return lastUpdatedTs;
    }

    public void setLastUpdatedTs(String lastUpdatedTs) {
        this.lastUpdatedTs = lastUpdatedTs;
    }

    public String getCustSaleType() {
        return custSaleType;
    }

    public void setCustSaleType(String custSaleType) {
        this.custSaleType = custSaleType;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getSiteAddress() {
        return siteAddress;
    }

    public void setSiteAddress(String siteAddress) {
        this.siteAddress = siteAddress;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
}
