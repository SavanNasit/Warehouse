package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/27/17.
 */

public class RunningOrders {
    private String orderID;
    private String mobile;

    public String getOrderID() {
        return orderID;
    }

    public String getDate() {
        return date;
    }

    private String customer;
    private String date;

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getCustomer() {
        return customer;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMobile() {
        return mobile;
    }
}
