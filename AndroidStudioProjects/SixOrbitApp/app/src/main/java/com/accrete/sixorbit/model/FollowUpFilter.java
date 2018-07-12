package com.accrete.sixorbit.model;

/**
 * Created by poonam on 1/6/17.
 */

public class FollowUpFilter {

    String today;
    String yesterday;
    String thisWeek;
    String taken;
    String pending;
    String startDate;
    String endDate;
    String enquiry;
    String quotation;
    String purchaseOrder;
    String customerSalesOrder;
    String lead;
    int ID;

    public FollowUpFilter(String today, String yesterday, String taken, String pending,
                          String startDate, String endDate, String lead, String enquiry,
                          String quotation, String purchaseOrder, String customerSalesOrder,
                          String thisWeek) {

        this.today = today;
        this.yesterday = yesterday;
        this.taken = taken;
        this.pending = pending;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lead = lead;
        this.enquiry = enquiry;
        this.quotation = quotation;
        this.purchaseOrder = purchaseOrder;
        this.customerSalesOrder = customerSalesOrder;
        this.thisWeek = thisWeek;

    }

    public FollowUpFilter() {

    }

    public String getThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(String thisWeek) {
        this.thisWeek = thisWeek;
    }

    public String getCustomerSalesOrder() {
        return customerSalesOrder;
    }

    public void setCustomerSalesOrder(String customerSalesOrder) {
        this.customerSalesOrder = customerSalesOrder;
    }

    public String getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(String enquiry) {
        this.enquiry = enquiry;
    }

    public String getQuotation() {
        return quotation;
    }

    public void setQuotation(String quotation) {
        this.quotation = quotation;
    }

    public String getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(String purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getLead() {
        return lead;
    }

    public void setLead(String lead) {
        this.lead = lead;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getYesterday() {
        return yesterday;
    }

    public void setYesterday(String yesterday) {
        this.yesterday = yesterday;
    }

    public String getTaken() {
        return taken;
    }

    public void setTaken(String taken) {
        this.taken = taken;
    }

    public String getPending() {
        return pending;
    }

    public void setPending(String pending) {
        this.pending = pending;
    }

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

}
