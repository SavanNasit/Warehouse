package com.accrete.warehouse.model;

/**
 * Created by poonam on 11/27/17.
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
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
    @SerializedName("order_items")
    @Expose
    private List<PendingItems> selectOrderItems = null;
    @SerializedName("packages")
    @Expose
    private List<Packages> packages = null;
    @SerializedName("customer_info")
    @Expose
    private CustomerInfo customerInfo;


    protected RunningOrder(Parcel in) {
        chkoid = in.readString();
        chkid = in.readString();
        poNumber = in.readString();
        date = in.readString();
        customer = in.readString();
        contact = in.readString();
        assignedUserName = in.readString();
        customerInfo = in.readParcelable(CustomerInfo.class.getClassLoader());
        packages = new ArrayList<Packages>();
        in.readList(packages,null);
    }

    public RunningOrder() {

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

    public List<PendingItems> getSelectOrderItems() {
        return selectOrderItems;
    }

    public void setSelectOrderItems(List<PendingItems> selectOrderItems) {
        this.selectOrderItems = selectOrderItems;
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
        dest.writeParcelable(customerInfo, flags);
        dest.writeList(packages);
    }
}
