package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/27/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RunningOrder implements Parcelable {


    public static final Creator<RunningOrder> CREATOR = new Creator<RunningOrder>() {
        @Override
        public RunningOrder createFromParcel(Parcel in) {
            return new RunningOrder(in);
        }

        @Override
        public RunningOrder[] newArray(int size) {
            return new RunningOrder[size];
        }
    };
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("po_number")
    @Expose
    private String poNumber;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("customer")
    @Expose
    private String customer;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("assigned_user_name")
    @Expose
    private String assignedUserName;
    @SerializedName("checkpointOrderID")
    @Expose
    private String checkpointOrderID;
    @SerializedName("order_items")
    @Expose
    private List<OrderData> orderItems = null;
    @SerializedName("packages")
    @Expose
    private List<Packages> packages = null;
    @SerializedName("customer_info")
    @Expose
    private CustomerInfo customerInfo;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public RunningOrder() {

    }

    public RunningOrder(Parcel in) {
        chkoid = in.readString();
        chkid = in.readString();
        poNumber = in.readString();
        date = in.readString();
        customer = in.readString();
        contact = in.readString();
        assignedUserName = in.readString();
        checkpointOrderID = in.readString();
        orderItems = in.createTypedArrayList(OrderData.CREATOR);
        customerInfo = in.readParcelable(CustomerInfo.class.getClassLoader());
        createdTs = in.readString();
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getCheckpointOrderID() {
        return checkpointOrderID;
    }

    public void setCheckpointOrderID(String checkpointOrderID) {
        this.checkpointOrderID = checkpointOrderID;
    }

    public List<OrderData> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderData> orderItems) {
        this.orderItems = orderItems;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chkoid);
        dest.writeString(chkid);
        dest.writeString(poNumber);
        dest.writeString(date);
        dest.writeString(customer);
        dest.writeString(contact);
        dest.writeString(assignedUserName);
        dest.writeString(checkpointOrderID);
        dest.writeTypedList(orderItems);
        dest.writeParcelable(customerInfo, flags);
        dest.writeString(createdTs);
    }
}


