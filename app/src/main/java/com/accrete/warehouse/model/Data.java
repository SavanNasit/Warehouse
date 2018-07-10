package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Modified by poonam on 22/9/17.
 */
public class Data {
    @SerializedName("package_files")
    @Expose
    private List<PackageFile> packageFiles = null;
    @SerializedName("stock_requests")
    @Expose
    private List<StockRequestList> stockRequestList = null;
    @SerializedName("consumption_consignment")
    @Expose
    private List<ConsumptionConsignment> consumptionConsignment = null;
    @SerializedName("allocate_consignment")
    @Expose
    private List<AllocateConsignment> allocateConsignment = null;
    @SerializedName("is_invoice_enabled")
    @Expose
    private String isInvoiceNumberEnabled;
    @SerializedName("interstate_flag")
    @Expose
    private String interStateFlag;
    @SerializedName("package_upload_data")
    @Expose
    private List<ImagesUpload> packageUploadData = null;
    @SerializedName("item_info")
    @Expose
    private OrderData itemInfo = null;
    @SerializedName("edit_package_formdata")
    @Expose
    private EditPackageFormdata editPackageFormdata;
    @SerializedName("ledger_search_data")
    @Expose
    private List<TransporterNameSearchDatum> ledgerSearchData = null;
    @SerializedName("is_execute_order_taxes_edit")
    @Expose
    private Boolean isExecuteOrderTaxesEdit;
    @SerializedName("charges")
    @Expose
    private List<Charge> charges = null;
    @SerializedName("transport_modes")
    @Expose
    private List<TransportMode> transprotModes = null;
    @SerializedName("stock_request_data")
    @Expose
    private List<StockRequestDatum> stockRequestData = null;
    @SerializedName("packages")
    @Expose
    private List<AlreadyCreatedPackages> packages = null;

    public List<AlreadyCreatedPackages> getPackages() {
        return packages;
    }

    public void setPackages(List<AlreadyCreatedPackages> packages) {
        this.packages = packages;
    }

    @SerializedName("pacid")
    @Expose
    private String pacid;

    public String getPacid() {
        return pacid;
    }

    public void setPacid(String pacid) {
        this.pacid = pacid;
    }
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

    // TODO Both have same type of data so we are using same Model class for both
    @SerializedName("packageShippedData")
    @Expose
    private List<PackedItem> packageShippedData = null;
    @SerializedName("packedItems")
    @Expose
    private List<PackedItem> packedItems = null;
    @SerializedName("users")
    @Expose
    private List<User> users = null;
    @SerializedName("shipping_by")
    @Expose
    private List<ShippingBy> shippingBy = null;
    @SerializedName("shipping_types")
    @Expose
    private List<ShippingType> shippingTypes = null;
    @SerializedName("consignments")
    @Expose
    private List<Consignment> consignments = null;
    @SerializedName("consignmentData")
    @Expose
    private ConsignmentData consignmentData;
    @SerializedName("vendorTransportationData")
    @Expose
    private VendorTransportationData vendorTransportationData;
    @SerializedName("inventoryData")
    @Expose
    private List<Inventory> inventoryData = null;
    @SerializedName("packageItems")
    @Expose
    private List<PackageItem> packageItems = null;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("iscid")
    @Expose
    private String iscid;
    @SerializedName("purchase_orders")
    @Expose
    private List<PurchaseOrder> purchaseOrders = null;
    @SerializedName("vendorData")
    @Expose
    private VendorData vendorData;
    @SerializedName("orderDetail")
    @Expose
    private OrderDetail orderDetail;
    @SerializedName("receivedDetails")
    @Expose
    private List<ReceivedDetail> receivedDetails = null;
    @SerializedName("consignmentDetails")
    @Expose
    private List<ConsignmentDetail> consignmentDetails = null;
    @SerializedName("gatePassData")
    @Expose
    private List<ViewGatepassPackages> gatePassData = null;
    @SerializedName("customer_info")
    @Expose
    private CustomerInfo customerInfo;
    @SerializedName("packageData")
    @Expose
    private PackageData packageData;
    @SerializedName("shipping_company")
    @Expose
    private List<ShippingCompany> shippingCompany = null;
    @SerializedName("gatepassList")
    @Expose
    private List<GatepassList> gatepassList = null;
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
    @SerializedName("purchaseOrderData")
    @Expose
    private PurchaseOrderData purchaseOrderData;
    @SerializedName("purchaseDetails")
    @Expose
    private PurchaseDetails purchaseDetails;
    @SerializedName("consignment_items")
    @Expose
    private List<ConsignmentItem> consignmentItems = null;
    @SerializedName("transportationData")
    @Expose
    private TransportationData transportationData;
    @SerializedName("isExistTransportationDetails")
    @Expose
    private String isExistTransportationDetails;
    @SerializedName("vendors")
    @Expose
    private List<Vendor> vendors = null;
    @SerializedName("item_list")
    @Expose
    private List<ItemList> itemList = null;
    @SerializedName("consignment_item")
    @Expose
    private ConsignmentItem consignmentItem;
    @SerializedName("statuses")
    @Expose
    private List<Status> statuses = null;
    @SerializedName("logHistoryData")
    @Expose
    private List<LogHistoryDatum> logHistoryData = null;
    @SerializedName("orderData")
    @Expose
    private List<OrderData> orderData = null;

    public List<OrderData> getOrderData() {
        return orderData;
    }

    public void setOrderData(List<OrderData> orderData) {
        this.orderData = orderData;
    }

    public List<LogHistoryDatum> getLogHistoryData() {
        return logHistoryData;
    }

    public void setLogHistoryData(List<LogHistoryDatum> logHistoryData) {
        this.logHistoryData = logHistoryData;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public ProfileInfo getProfile() {
        return profile;
    }

    public void setProfile(ProfileInfo profile) {
        this.profile = profile;
    }

    @SerializedName("profile")
    @Expose
    private ProfileInfo profile;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<PackedItem> getPackageShippedData() {
        return packageShippedData;
    }

    public void setPackageShippedData(List<PackedItem> packageShippedData) {
        this.packageShippedData = packageShippedData;
    }

    public PackageData getPackageData() {
        return packageData;
    }

    public void setPackageData(PackageData packageData) {
        this.packageData = packageData;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<ViewGatepassPackages> getGatePassData() {
        return gatePassData;
    }

    public void setGatePassData(List<ViewGatepassPackages> gatePassData) {
        this.gatePassData = gatePassData;
    }

    public ConsignmentItem getConsignmentItem() {
        return consignmentItem;
    }

    public void setConsignmentItem(ConsignmentItem consignmentItem) {
        this.consignmentItem = consignmentItem;
    }

    public List<ItemList> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public List<Vendor> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendor> vendors) {
        this.vendors = vendors;
    }

    public String getIsExistTransportationDetails() {
        return isExistTransportationDetails;
    }

    public void setIsExistTransportationDetails(String isExistTransportationDetails) {
        this.isExistTransportationDetails = isExistTransportationDetails;
    }

    public PurchaseOrderData getPurchaseOrderData() {
        return purchaseOrderData;
    }

    public void setPurchaseOrderData(PurchaseOrderData purchaseOrderData) {
        this.purchaseOrderData = purchaseOrderData;
    }

    public PurchaseDetails getPurchaseDetails() {
        return purchaseDetails;
    }

    public void setPurchaseDetails(PurchaseDetails purchaseDetails) {
        this.purchaseDetails = purchaseDetails;
    }

    public List<ConsignmentItem> getConsignmentItems() {
        return consignmentItems;
    }

    public void setConsignmentItems(List<ConsignmentItem> consignmentItems) {
        this.consignmentItems = consignmentItems;
    }

    public VendorData getVendorData() {
        return vendorData;
    }

    public void setVendorData(VendorData vendorData) {
        this.vendorData = vendorData;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public List<ReceivedDetail> getReceivedDetails() {
        return receivedDetails;
    }

    public void setReceivedDetails(List<ReceivedDetail> receivedDetails) {
        this.receivedDetails = receivedDetails;
    }

    public List<ConsignmentDetail> getConsignmentDetails() {
        return consignmentDetails;
    }

    public void setConsignmentDetails(List<ConsignmentDetail> consignmentDetails) {
        this.consignmentDetails = consignmentDetails;
    }

    public List<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(List<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getIscid() {
        return iscid;
    }

    public void setIscid(String iscid) {
        this.iscid = iscid;
    }

    public List<PackageItem> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<PackageItem> packageItems) {
        this.packageItems = packageItems;
    }

    public List<Inventory> getInventoryData() {
        return inventoryData;
    }

    public void setInventoryData(List<Inventory> inventoryData) {
        this.inventoryData = inventoryData;
    }

    public List<GatepassList> getGatepassList() {
        return gatepassList;
    }

    public void setGatepassList(List<GatepassList> gatepassList) {
        this.gatepassList = gatepassList;
    }

    public List<ShippingCompany> getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(List<ShippingCompany> shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

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

    public void setPackedItems(List<PackedItem> packedItems) {
        this.packedItems = packedItems;
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

    public List<TransportMode> getTransprotModes() {
        return transprotModes;
    }

    public void setTransprotModes(List<TransportMode> transprotModes) {
        this.transprotModes = transprotModes;
    }

    public List<Charge> getCharges() {
        return charges;
    }

    public void setCharges(List<Charge> charges) {
        this.charges = charges;
    }

    public Boolean getExecuteOrderTaxesEdit() {
        return isExecuteOrderTaxesEdit;
    }

    public void setExecuteOrderTaxesEdit(Boolean executeOrderTaxesEdit) {
        isExecuteOrderTaxesEdit = executeOrderTaxesEdit;
    }

    public List<StockRequestDatum> getStockRequestData() {
        return stockRequestData;
    }

    public void setStockRequestData(List<StockRequestDatum> stockRequestData) {
        this.stockRequestData = stockRequestData;
    }


    public List<TransporterNameSearchDatum> getLedgerSearchData() {
        return ledgerSearchData;
    }

    public void setLedgerSearchData(List<TransporterNameSearchDatum> ledgerSearchData) {
        this.ledgerSearchData = ledgerSearchData;
    }

    public EditPackageFormdata getEditPackageFormdata() {
        return editPackageFormdata;
    }

    public void setEditPackageFormdata(EditPackageFormdata editPackageFormdata) {
        this.editPackageFormdata = editPackageFormdata;
    }


    public OrderData getItemInfo() {
        return itemInfo;
    }
    public void setItemInfo(OrderData itemInfo) {
        this.itemInfo = itemInfo;
    }

    public List<ImagesUpload> getPackageUploadData() {
        return packageUploadData;
    }

    public void setPackageUploadData(List<ImagesUpload> packageUploadData) {
        this.packageUploadData = packageUploadData;
    }

    public String getInterStateFlag() {
        return interStateFlag;
    }

    public void setInterStateFlag(String interStateFlag) {
        this.interStateFlag = interStateFlag;
    }

    public String getIsInvoiceNumberEnabled() {
        return isInvoiceNumberEnabled;
    }

    public void setIsInvoiceNumberEnabled(String isInvoiceNumberEnabled) {
        this.isInvoiceNumberEnabled = isInvoiceNumberEnabled;
    }


    public List<AllocateConsignment> getAllocateConsignment() {
        return allocateConsignment;
    }

    public void setAllocateConsignment(List<AllocateConsignment> allocateConsignment) {
        this.allocateConsignment = allocateConsignment;
    }

    public List<ConsumptionConsignment> getConsumptionConsignment() {
        return consumptionConsignment;
    }

    public void setConsumptionConsignment(List<ConsumptionConsignment> consumptionConsignment) {
        this.consumptionConsignment = consumptionConsignment;
    }


    public List<StockRequestList> getStockRequestList() {
        return stockRequestList;
    }

    public void setStockRequestList(List<StockRequestList> stockRequestData) {
        this.stockRequestList = stockRequestData;
    }

    public List<PackageFile> getPackageFiles() {
        return packageFiles;
    }

    public void setPackageFiles(List<PackageFile> packageFiles) {
        this.packageFiles = packageFiles;
    }
}
