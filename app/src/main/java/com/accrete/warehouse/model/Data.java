package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Modified by poonam on 22/9/17.
 */
public class Data {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("warehouseList")
    @Expose
    private List<WarehouseList> warehouseList = null;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("usid")
    @Expose
    private String userSessionId;
    @SerializedName("maskconfig")
    @Expose
    private List<String> maskconfig = null;

    @SerializedName("waid")
    @Expose
    private String waid;
    @SerializedName("running_orders")
    @Expose
    private List<RunningOrder> runningOrders = null;

    @SerializedName("running_order_count")
    @Expose
    private String runningOrderCount;

    @SerializedName("packedItems")
    @Expose
    private List<PackedItem> packedItems = null;

    public List<PackedItem> getPackedItems() {
        return packedItems;
    }

    public void setPackedItems(List<PackedItem> packedItems) {
        this.packedItems = packedItems;
    }

    public String getRunningOrderCount() {
        return runningOrderCount;
    }

    public void setRunningOrderCount(String runningOrderCount) {
        this.runningOrderCount = runningOrderCount;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("company_code")
    @Expose
    private String companyCode;

    @SerializedName("user_name")
    @Expose
    private String name;


    @SerializedName("select_orders_items")
    @Expose
    private List<SelectOrderItem> selectOrdersItems = null;

    public List<SelectOrderItem> getSelectOrdersItems() {
        return selectOrdersItems;
    }

    public void setSelectOrdersItems(List<SelectOrderItem> selectOrdersItems) {
        this.selectOrdersItems = selectOrdersItems;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public List<String> getMaskconfig() {
        return maskconfig;
    }


    public String getWaid() {
        return waid;
    }

    public void setWaid(String waid) {
        this.waid = waid;
    }

    public List<RunningOrder> getRunningOrders() {
        return runningOrders;
    }

    public void setRunningOrders(List<RunningOrder> runningOrders) {
        this.runningOrders = runningOrders;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<WarehouseList> getWarehouseList() {
        return warehouseList;
    }

    public void setWarehouseList(List<WarehouseList> warehouseList) {
        this.warehouseList = warehouseList;
    }
}
