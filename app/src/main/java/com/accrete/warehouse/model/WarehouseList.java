package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/11/17.
 */

public class WarehouseList {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("order_count")
    @Expose
    private String orderCount;
    @SerializedName("package_count")
    @Expose
    private String packageCount;
    @SerializedName("gatepass_count")
    @Expose
    private String gatepassCount;
    @SerializedName("consignmentCount")
    @Expose
    private String consignmentCount;
    @SerializedName("receiveConsignmentCount")
    @Expose
    private String receiveConsignmentCount;

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

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public String getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(String orderCount) {
        this.orderCount = orderCount;
    }

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }

    public String getGatepassCount() {
        return gatepassCount;
    }

    public void setGatepassCount(String gatepassCount) {
        this.gatepassCount = gatepassCount;
    }

    public String getConsignmentCount() {
        return consignmentCount;
    }

    public void setConsignmentCount(String consignmentCount) {
        this.consignmentCount = consignmentCount;
    }

    public String getReceiveConsignmentCount() {
        return receiveConsignmentCount;
    }

    public void setReceiveConsignmentCount(String receiveConsignmentCount) {
        this.receiveConsignmentCount = receiveConsignmentCount;
    }


}