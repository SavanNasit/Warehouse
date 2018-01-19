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

    @SerializedName("shipping_by")
    @Expose
    private List<ShippingBy> shippingBy = null;
    @SerializedName("shipping_types")
    @Expose
    private List<ShippingType> shippingTypes = null;
    @SerializedName("consignments")
    @Expose
    private List<Consignment> consignments = null;
    @SerializedName("company_code")

    @Expose
    private String companyCode;
    @SerializedName("user_name")
    @Expose
    private String name;
    @SerializedName("select_orders_items")
    @Expose
    private List<SelectOrderItem> selectOrdersItems = null;
    @SerializedName("consignmentData")
    @Expose
    private ConsignmentData consignmentData;
    @SerializedName("vendorTransportationData")
    @Expose
    private VendorTransportationData vendorTransportationData;
    @SerializedName("transportationData")
    @Expose
    private TransportationData transportationData;

    public ConsignmentData getConsignmentData() {
        return consignmentData;
    }

    public void setConsignmentData(ConsignmentData consignmentData) {
        this.consignmentData = consignmentData;
    }

    public VendorTransportationData getVendorTransportationData() {
        return vendorTransportationData;
    }

    public void setVendorTransportationData(VendorTransportationData vendorTransportationData) {
        this.vendorTransportationData = vendorTransportationData;
    }

    public TransportationData getTransportationData() {
        return transportationData;
    }

    public void setTransportationData(TransportationData transportationData) {
        this.transportationData = transportationData;
    }

    public List<Consignment> getConsignments() {
        return consignments;
    }

    public void setConsignments(List<Consignment> consignments) {
        this.consignments = consignments;
    }

    public List<ShippingBy> getShippingBy() {
        return shippingBy;
    }

    public void ListShippingBy(List<ShippingBy> shippingBy) {
        this.shippingBy = shippingBy;
    }

    public List<ShippingType> getShippingTypes() {
        return shippingTypes;
    }

    public void ListShippingTypes(List<ShippingType> shippingTypes) {
        this.shippingTypes = shippingTypes;
    }

    public List<PackedItem> getPackedItems() {
        return packedItems;
    }

    public void ListPackedItems(List<PackedItem> packedItems) {
        this.packedItems = packedItems;
    }

    public String getRunningOrderCount() {
        return runningOrderCount;
    }

    public void ListRunningOrderCount(String runningOrderCount) {
        this.runningOrderCount = runningOrderCount;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void ListCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getName() {
        return name;
    }

    public void ListName(String name) {
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


    @SerializedName("delivery_user_list")
    @Expose
    private List<DeliveryUserList> deliveryUserList = null;

    public List<DeliveryUserList> getDeliveryUserList() {
        return deliveryUserList;
    }

    public void setDeliveryUserList(List<DeliveryUserList> deliveryUserList) {
        this.deliveryUserList = deliveryUserList;
    }


    public List<SelectOrderItem> getSelectOrdersItems() {
        return selectOrdersItems;
    }

    public void ListSelectOrdersItems(List<SelectOrderItem> selectOrdersItems) {
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

    public void ListWaid(String waid) {
        this.waid = waid;
    }

    public List<RunningOrder> getRunningOrders() {
        return runningOrders;
    }

    public void ListRunningOrders(List<RunningOrder> runningOrders) {
        this.runningOrders = runningOrders;
    }


    public String getUserId() {
        return userId;
    }

    public void ListUserId(String userId) {
        this.userId = userId;
    }

    public List<WarehouseList> getWarehouseList() {
        return warehouseList;
    }

    public void ListWarehouseList(List<WarehouseList> warehouseList) {
        this.warehouseList = warehouseList;
    }
}
