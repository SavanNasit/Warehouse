package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Modified by poonam on 22/9/17.
 */
public class Data implements Serializable {

    @SerializedName("lead_number")
    @Expose
    private String leadNumber;
    @SerializedName("leadId")
    @Expose
    private String leadId;
    @SerializedName("codeid")
    @Expose
    private String codeid;
    @SerializedName("orders")
    @Expose
    private List<Order> orders = null;
    @SerializedName("current_followup")
    @Expose
    private String currentFollowUpId;
    @SerializedName("aumvid")
    @Expose
    private String aumvid;
    @SerializedName("auevid")
    @Expose
    private String auevid;
    @SerializedName("company_code")
    @Expose
    private String companyCode;
    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("usid")
    @Expose
    private String userSessionId;
    /*@SerializedName("maskconfig")
    @Expose
    private List<String> maskconfig = null;*/
    @SerializedName("permission")
    @Expose
    private List<Permission> permission = null;
    @SerializedName("followups")
    @Expose
    private List<FollowUp> followups = null;
    @SerializedName("notification")
    @Expose
    private List<Notification> notification = null;
    @SerializedName("notification_time")
    @Expose
    private List<NotificationTime> notificationTime = null;
    @SerializedName("chat_user")
    @Expose
    private List<ChatContacts> chatContacts = null;
    @SerializedName("lead")
    @Expose
    private List<Lead> lead = null;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("chat_message")
    @Expose
    private List<ChatMessage> chatMessage = null;
    @SerializedName("lead_id")
    @Expose
    private String leaid;
    @SerializedName("contacts")
    @Expose
    private List<Contacts> contacts = null;
    @SerializedName("user_name")
    @Expose
    private String name;
    @SerializedName("user_photo")
    @Expose
    private String photo;
    @SerializedName("otp_settings")
    @Expose
    private int otpSettings;
    @SerializedName("followup_pending_count")
    @Expose
    private String followupPendingcount;
    @SerializedName("lead_new_count")
    @Expose
    private String leadNewcount;
    @SerializedName("foid")
    @Expose
    private String foid;
    @SerializedName("notify")
    @Expose
    private List<Notify> notify = null;
    @SerializedName("customer")
    @Expose
    private List<Customer> customer = null;
    @SerializedName("otp_mobile_fetch")
    @Expose
    private List<OtpMobileFetch> otpMobilefetch = null;
    @SerializedName("otp_email_fetch")
    @Expose
    private List<OtpEmailFetch> otpEmailfetch = null;
    @SerializedName("profile")
    @Expose
    private Profile profile;
    @SerializedName("recent_login")
    @Expose
    private List<RecentLogin> recentLogin = null;
    @SerializedName("quotation")
    @Expose
    private List<Quotation> quotation = null;
    @SerializedName("enquiry")
    @Expose
    private List<Enquiry> enquiry = null;
    @SerializedName("customer_pending_invoice")
    @Expose
    private List<CustomerPendingInvoice> customerPendingInvoice = null;
    @SerializedName("customers")
    @Expose
    private List<Customers> customers = null;
    @SerializedName("customer_recent_transaction")
    @Expose
    private List<CustomerWallet> customerWallets = null;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("filename")
    @Expose
    private String filename;
    @SerializedName("vendors")
    @Expose
    private List<Vendors> vendors = null;
    @SerializedName("vendor_pending_invoice")
    @Expose
    private List<VendorPendingInvoice> vendorPendingInvoice = null;
    @SerializedName("vendor")
    @Expose
    private List<Vendor> vendor = null;
    @SerializedName("vendor_recent_transaction")
    @Expose
    private List<VendorRecentTransaction> vendorRecentTransaction = null;
    @SerializedName("purchase_order")
    @Expose
    private List<PurchaseOrder> purchaseOrder = null;
    @SerializedName("consignment")
    @Expose
    private List<Consignment> consignment = null;
    @SerializedName("vendor_invoice")
    @Expose
    private List<VendorInvoice> vendorInvoice = null;
    @SerializedName("customer_all_invoice")
    @Expose
    private List<CustomerAllInvoice> customerAllInvoice = null;
    @SerializedName("order_details")
    @Expose
    private OrderDetails orderDetails;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("package")
    @Expose
    private List<Packages> packages = null;
    @SerializedName("salutations")
    @Expose
    private List<Salutation> salutations = null;
    @SerializedName("customers_types")
    @Expose
    private List<CustomersType> customersTypes = null;
    @SerializedName("customer_sales_types")
    @Expose
    private List<CustomerSalesType> customerSalesTypes = null;
    @SerializedName("account_groups")
    @Expose
    private List<AccountGroup> accountGroups = null;
    @SerializedName("collection")
    @Expose
    private List<Collection> collection = null;
    @SerializedName("search_reffered_data")
    @Expose
    private List<SearchRefferedDatum> searchRefferedData = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("contactDetails")
    @Expose
    private List<ContactDetail> contactDetails = null;
    @SerializedName("default_account_groupId")
    @Expose
    private Integer defaultAccountGroupId;
    @SerializedName("refereral_add_contact")
    @Expose
    private List<ReferralAddContact> referralAddContact = null;
    @SerializedName("country_list")
    @Expose
    private List<CountryList> countryList = null;
    @SerializedName("state_list")
    @Expose
    private List<StateList> stateList = null;
    @SerializedName("city_names")
    @Expose
    private List<CityName> cityNames = null;
    @SerializedName("default_country")
    @Expose
    private String defaultCountry;
    @SerializedName("default_state")
    @Expose
    private String defaultState;
    @SerializedName("quotation_details")
    @Expose
    private QuotationDetails quotationDetails;
    @SerializedName("quotation_products")
    @Expose
    private List<QuotationProduct> quotationProducts = null;
    @SerializedName("order_items")
    @Expose
    private List<OrderItem> orderItems = null;
    @SerializedName("consignment_info")
    @Expose
    private ConsignmentInfo consignmentInfo;
    @SerializedName("consignment_items")
    @Expose
    private List<ConsignmentItem> consignmentItems = null;
    @SerializedName("attributs_ptc")
    @Expose
    private List<AttributsPtc> attributsPtc = null;
    @SerializedName("customer_info")
    @Expose
    private CustomerInfo customerInfo;
    @SerializedName("otp_email")
    @Expose
    private List<OtpEmailFetch> otpEmail = null;
    @SerializedName("otp_mobile")
    @Expose
    private List<OtpMobileFetch> otpMobile = null;
    @SerializedName("item_list")
    @Expose
    private List<ItemList> itemList = null;
    @SerializedName("item_data")
    @Expose
    private ItemData itemData;
    @SerializedName("address_list")
    @Expose
    private List<AddressList> addressList = null;
    @SerializedName("charges_list")
    @Expose
    private List<ChargesList> chargesList = null;
    @SerializedName("tax_list")
    @Expose
    private List<TaxList> taxList = null;
    @SerializedName("charges_list2")
    @Expose
    private List<ChargesList2> chargesList2 = null;
    @SerializedName("charges_list3")
    @Expose
    private List<ChargesList3> chargesList3 = null;
    @SerializedName("outlet")
    @Expose
    private List<Outlet> outlet = null;
    @SerializedName("templates_Data")
    @Expose
    private List<TemplatesData> templatesData = null;
    @SerializedName("said")
    @Expose
    private String said;
    @SerializedName("baid")
    @Expose
    private String baid;
    @SerializedName("customer_type")
    @Expose
    private String customerType;
    @SerializedName("contact_personArr")
    @Expose
    private List<ContactPerson> contactPersonArr = null;
    @SerializedName("contact_person_addData")
    @Expose
    private List<ContactPersonTypeData> contactPersonTypeData = null;
    @SerializedName("smsTemplate_data")
    @Expose
    private List<SmsTemplateDatum> smsTemplateData = null;
    @SerializedName("emailTemplateArr")
    @Expose
    private List<EmailTemplateArr> emailTemplateArr = null;
    @SerializedName("quotationData")
    @Expose
    private List<QuotationDatum> quotationData = null;
    @SerializedName("customerList")
    @Expose
    private List<CustomerList> customerList = null;
    @SerializedName("followup_communication_modes")
    @Expose
    private List<FollowupCommunicationMode> followupCommunicationModes = null;
    @SerializedName("clid")
    @Expose
    private Integer clid;
    @SerializedName("extra_discountData")
    @Expose
    private List<ExtraDiscountData> extraDiscountData = null;
    @SerializedName("extensive_tax_required")
    @Expose
    private String extensiveTaxRequired;
    @SerializedName("quotation_data")
    @Expose
    private QuotationData editQuotationData;
    @SerializedName("quotationDetailsInfo")
    @Expose
    private QuotationDetailsInfo quotationDetailsInfo;
    @SerializedName("order_form_data")
    @Expose
    private OrderFormData orderFormData;
    @SerializedName("ledger_search_data")
    @Expose
    private List<LedgerSearchData> ledgerSearchData = null;
    @SerializedName("checkpoint_order_data")
    @Expose
    private CheckpointOrderData checkpointOrderData;
    //TODO - Added new Vars on 6th April
    @SerializedName("ledgerInfo")
    @Expose
    private LedgerInfo ledgerInfo;
    @SerializedName("order_type")
    @Expose
    private String orderType;
    @SerializedName("percent_limit")
    @Expose
    private String percentLimit;
    @SerializedName("payment_term")
    @Expose
    private String paymentTerm;
    @SerializedName("purchase_order_number")
    @Expose
    private String purchaseOrderNumber;
    @SerializedName("purchase_order_date")
    @Expose
    private String purchaseOrderDate;
    @SerializedName("delivery_date")
    @Expose
    private String deliveryDate;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("assigned_user_name")
    @Expose
    private String assignedUserName;
    @SerializedName("assigned_user_id")
    @Expose
    private String assignedUserId;
    @SerializedName("checkpoint_order_remarks")
    @Expose
    private String checkpointOrderRemarks;
    @SerializedName("smstid")
    @Expose
    private String smstid;
    @SerializedName("etid")
    @Expose
    private String etid;
    @SerializedName("order_products")
    @Expose
    private ArrayList<ItemData> orderProducts = null;
    @SerializedName("taken_date")
    @Expose
    private String takenDate;
    @SerializedName("dealer_price_include")
    @Expose
    private String dealerPriceInclude;
    @SerializedName("payment_term_day")
    @Expose
    private String paymentTermDay;
    @SerializedName("all_history_orders")
    @Expose
    private List<Order> allHistoryOrders = null;
    @SerializedName("ortemid")
    @Expose
    private String ortemid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("extra_info")
    @Expose
    private ExtraInfo extraInfo;
    @SerializedName("quotation_history")
    @Expose
    private List<QuotationHistory> quotationHistory = null;
    //TODO Added on 15th May
    @SerializedName("sms_data")
    @Expose
    private SmsData smsData;
    @SerializedName("email_Data")
    @Expose
    private EmailData emailData;
    @SerializedName("paymentModeData")
    @Expose
    private List<PaymentModeData> paymentModeData = null;
    @SerializedName("is_outlet_enable")
    @Expose
    private boolean isOutletEnable;
    @SerializedName("total_pending")
    @Expose
    private String totalPending;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("invoice_list")
    @Expose
    private List<InvoiceList> invoiceList = null;
    //TODO Added on 29th May
    @SerializedName("collection_data")
    @Expose
    private List<CollectionData> collectionData = null;
    @SerializedName("transactions")
    @Expose
    private List<TransactionData> transactions = null;
    @SerializedName("jobcard_collection_data")
    @Expose
    private List<JobcardCollectionData> jobcardCollectionData = null;
    @SerializedName("total")
    @Expose
    private String total;
    @SerializedName("round_off")
    @Expose
    private String roundOff;
    //TODO Added on 4th June
    @SerializedName("collection_transaction_data")
    @Expose
    private List<CollectionTransactionData> collectionTransactionData = null;
    @SerializedName("customer_data")
    @Expose
    private CustomerData customerData;
    @SerializedName("invoice_items")
    @Expose
    private List<InvoiceItem> invoiceItems = null;
    @SerializedName("eway_bill_data")
    @Expose
    private List<EwayBillData> ewayBillData = null;
    //TODO Added on 7th June
    @SerializedName("approve_count")
    @Expose
    private String approveCount;
    @SerializedName("order_reference")
    @Expose
    private List<OrderReference> orderReference = null;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("path")
    @Expose
    private String path;
    //TODO Added on 11th June
    @SerializedName("tid")
    @Expose
    private String tid;
    @SerializedName("transactionID")
    @Expose
    private String transactionID;
    @SerializedName("transaction_time")
    @Expose
    private String transactionTime;
    @SerializedName("paying_amount")
    @Expose
    private String payingAmount;
    @SerializedName("total_pending_amount")
    @Expose
    private String totalPendingAmount;
    private List<CustomerSalesType> salesTypes = null;
    //TODO Added on 15th June
    @SerializedName("followup_codeid")
    @Expose
    private String followupCodeid;
    //TODO Added on 22nd June
    @SerializedName("company_data")
    @Expose
    private List<CompanyData> companyData = null;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("company_id")
    @Expose
    private String companyId;
    @SerializedName("customer_outstanding_collections")
    @Expose
    private List<CustomerOutstandingCollection> customerOutstandingCollections = null;
    @SerializedName("all_enquiry")
    @Expose
    private List<AllEnquiry> allEnquiry = null;
    //TODO Added on 29th June
    @SerializedName("basic_details")
    @Expose
    private BasicDetails basicDetails;
    @SerializedName("followupData")
    @Expose
    private List<FollowUp> followupData = null;
    //TODO Added on 9th July 2k18
    @SerializedName("enquiry_products")
    @Expose
    private List<EnquiryProduct> enquiryProducts = null;

    public List<EnquiryProduct> getEnquiryProducts() {
        return enquiryProducts;
    }

    public void setEnquiryProducts(List<EnquiryProduct> enquiryProducts) {
        this.enquiryProducts = enquiryProducts;
    }

    public BasicDetails getBasicDetails() {
        return basicDetails;
    }

    public void setBasicDetails(BasicDetails basicDetails) {
        this.basicDetails = basicDetails;
    }

    public List<FollowUp> getFollowupData() {
        return followupData;
    }

    public void setFollowupData(List<FollowUp> followupData) {
        this.followupData = followupData;
    }

    public List<AllEnquiry> getAllEnquiry() {
        return allEnquiry;
    }

    public void setAllEnquiry(List<AllEnquiry> allEnquiry) {
        this.allEnquiry = allEnquiry;
    }

    public List<CustomerOutstandingCollection> getCustomerOutstandingCollections() {
        return customerOutstandingCollections;
    }

    public void setCustomerOutstandingCollections(List<CustomerOutstandingCollection> customerOutstandingCollections) {
        this.customerOutstandingCollections = customerOutstandingCollections;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<CompanyData> getCompanyData() {
        return companyData;
    }

    public void setCompanyData(List<CompanyData> companyData) {
        this.companyData = companyData;
    }

    public String getFollowupCodeid() {
        return followupCodeid;
    }

    public void setFollowupCodeid(String followupCodeid) {
        this.followupCodeid = followupCodeid;
    }

    public List<CustomerSalesType> getSalesTypes() {
        return salesTypes;
    }

    public void setSalesTypes(List<CustomerSalesType> salesTypes) {
        this.salesTypes = salesTypes;
    }

    public String getTotalPendingAmount() {
        return totalPendingAmount;
    }

    public void setTotalPendingAmount(String totalPendingAmount) {
        this.totalPendingAmount = totalPendingAmount;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getPayingAmount() {
        return payingAmount;
    }

    public void setPayingAmount(String payingAmount) {
        this.payingAmount = payingAmount;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getApproveCount() {
        return approveCount;
    }

    public void setApproveCount(String approveCount) {
        this.approveCount = approveCount;
    }

    public List<EwayBillData> getEwayBillData() {
        return ewayBillData;
    }

    public void setEwayBillData(List<EwayBillData> ewayBillData) {
        this.ewayBillData = ewayBillData;
    }

    public List<OrderReference> getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(List<OrderReference> orderReference) {
        this.orderReference = orderReference;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
    }

    public List<CollectionTransactionData> getCollectionTransactionData() {
        return collectionTransactionData;
    }

    public void setCollectionTransactionData(List<CollectionTransactionData> collectionTransactionData) {
        this.collectionTransactionData = collectionTransactionData;
    }

    public String getRoundOff() {
        return roundOff;
    }

    public void setRoundOff(String roundOff) {
        this.roundOff = roundOff;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<JobcardCollectionData> getJobcardCollectionData() {
        return jobcardCollectionData;
    }

    public void setJobcardCollectionData(List<JobcardCollectionData> jobcardCollectionData) {
        this.jobcardCollectionData = jobcardCollectionData;
    }

    public List<TransactionData> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionData> transactions) {
        this.transactions = transactions;
    }

    public List<CollectionData> getCollectionData() {
        return collectionData;
    }


    public void setCollectionData(List<CollectionData> collectionData) {
        this.collectionData = collectionData;
    }

    public String getTotalPending() {
        return totalPending;
    }

    public void setTotalPending(String totalPending) {
        this.totalPending = totalPending;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<InvoiceList> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(List<InvoiceList> invoiceList) {
        this.invoiceList = invoiceList;
    }

    public boolean isOutletEnable() {
        return isOutletEnable;
    }

    public void setOutletEnable(boolean outletEnable) {
        isOutletEnable = outletEnable;
    }

    public List<PaymentModeData> getPaymentModeData() {
        return paymentModeData;
    }

    public void setPaymentModeData(List<PaymentModeData> paymentModeData) {
        this.paymentModeData = paymentModeData;
    }

    public SmsData getSmsData() {
        return smsData;
    }

    public void setSmsData(SmsData smsData) {
        this.smsData = smsData;
    }

    public EmailData getEmailData() {
        return emailData;
    }

    public void setEmailData(EmailData emailData) {
        this.emailData = emailData;
    }


    public ExtraInfo getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(ExtraInfo extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<QuotationHistory> getQuotationHistory() {
        return quotationHistory;
    }

    public void setQuotationHistory(List<QuotationHistory> quotationHistory) {
        this.quotationHistory = quotationHistory;
    }

    public String getBaid() {
        return baid;
    }

    public void setBaid(String baid) {
        this.baid = baid;
    }

    public String getOrtemid() {
        return ortemid;
    }

    public void setOrtemid(String ortemid) {
        this.ortemid = ortemid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public List<Order> getAllHistoryOrders() {
        return allHistoryOrders;
    }

    public void setAllHistoryOrders(List<Order> allHistoryOrders) {
        this.allHistoryOrders = allHistoryOrders;
    }

    public String getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    public String getDealerPriceInclude() {
        return dealerPriceInclude;
    }

    public void setDealerPriceInclude(String dealerPriceInclude) {
        this.dealerPriceInclude = dealerPriceInclude;
    }

    public String getPaymentTermDay() {
        return paymentTermDay;
    }

    public void setPaymentTermDay(String paymentTermDay) {
        this.paymentTermDay = paymentTermDay;
    }

    public ArrayList<ItemData> getOrderProducts() {
        return orderProducts;
    }

    public void setOrderProducts(ArrayList<ItemData> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public LedgerInfo getLedgerInfo() {
        return ledgerInfo;
    }

    public void setLedgerInfo(LedgerInfo ledgerInfo) {
        this.ledgerInfo = ledgerInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPercentLimit() {
        return percentLimit;
    }

    public void setPercentLimit(String percentLimit) {
        this.percentLimit = percentLimit;
    }

    public String getPaymentTerm() {
        return paymentTerm;
    }

    public void setPaymentTerm(String paymentTerm) {
        this.paymentTerm = paymentTerm;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getPurchaseOrderDate() {
        return purchaseOrderDate;
    }

    public void setPurchaseOrderDate(String purchaseOrderDate) {
        this.purchaseOrderDate = purchaseOrderDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(String assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public String getCheckpointOrderRemarks() {
        return checkpointOrderRemarks;
    }

    public void setCheckpointOrderRemarks(String checkpointOrderRemarks) {
        this.checkpointOrderRemarks = checkpointOrderRemarks;
    }

    public String getSmstid() {
        return smstid;
    }

    public void setSmstid(String smstid) {
        this.smstid = smstid;
    }

    public String getEtid() {
        return etid;
    }

    public void setEtid(String etid) {
        this.etid = etid;
    }

    public CheckpointOrderData getCheckpointOrderData() {
        return checkpointOrderData;
    }

    public void setCheckpointOrderData(CheckpointOrderData checkpointOrderData) {
        this.checkpointOrderData = checkpointOrderData;
    }

    public List<LedgerSearchData> getLedgerSearchData() {
        return ledgerSearchData;
    }

    public void setLedgerSearchData(List<LedgerSearchData> ledgerSearchData) {
        this.ledgerSearchData = ledgerSearchData;
    }

    public OrderFormData getOrderFormData() {
        return orderFormData;
    }

    public void setOrderFormData(OrderFormData orderFormData) {
        this.orderFormData = orderFormData;
    }

    public QuotationDetailsInfo getQuotationDetailsInfo() {
        return quotationDetailsInfo;
    }

    public void setQuotationDetailsInfo(QuotationDetailsInfo quotationDetailsInfo) {
        this.quotationDetailsInfo = quotationDetailsInfo;
    }

    public QuotationData getEditQuotationData() {
        return editQuotationData;
    }

    public void setEditQuotationData(QuotationData editQuotationData) {
        this.editQuotationData = editQuotationData;
    }

    public List<ExtraDiscountData> getExtraDiscountData() {
        return extraDiscountData;
    }

    public void setExtraDiscountData(List<ExtraDiscountData> extraDiscountData) {
        this.extraDiscountData = extraDiscountData;
    }

    public String getExtensiveTaxRequired() {
        return extensiveTaxRequired;
    }

    public void setExtensiveTaxRequired(String extensiveTaxRequired) {
        this.extensiveTaxRequired = extensiveTaxRequired;
    }

    public Integer getClid() {
        return clid;
    }

    public void setClid(Integer clid) {
        this.clid = clid;
    }

    public List<FollowupCommunicationMode> getFollowupCommunicationModes() {
        return followupCommunicationModes;
    }

    public void setFollowupCommunicationModes(List<FollowupCommunicationMode> followupCommunicationModes) {
        this.followupCommunicationModes = followupCommunicationModes;
    }

    public String getLeadNumber() {
        return leadNumber;
    }

    public void setLeadNumber(String leadNumber) {
        this.leadNumber = leadNumber;
    }


    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }


    public List<CustomerList> getCustomerList() {
        return customerList;
    }

    public void setCustomerList(List<CustomerList> customerList) {
        this.customerList = customerList;
    }

    public List<QuotationDatum> getQuotationData() {
        return quotationData;
    }

    public void setQuotationData(List<QuotationDatum> quotationData) {
        this.quotationData = quotationData;
    }


    public List<SmsTemplateDatum> getSmsTemplateData() {
        return smsTemplateData;
    }

    public void setSmsTemplateData(List<SmsTemplateDatum> smsTemplateData) {
        this.smsTemplateData = smsTemplateData;
    }

    public List<EmailTemplateArr> getEmailTemplateArr() {
        return emailTemplateArr;
    }

    public void setEmailTemplateArr(List<EmailTemplateArr> emailTemplateArr) {
        this.emailTemplateArr = emailTemplateArr;
    }

    public List<ContactPersonTypeData> getContactPersonTypeData() {
        return contactPersonTypeData;
    }

    public void setContactPersonTypeData(List<ContactPersonTypeData> contactPersonTypeData) {
        this.contactPersonTypeData = contactPersonTypeData;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getSaid() {
        return said;
    }

    public void setSaid(String said) {
        this.said = said;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public List<ContactPerson> getContactPersonArr() {
        return contactPersonArr;
    }

    public void setContactPersonArr(List<ContactPerson> contactPersonArr) {
        this.contactPersonArr = contactPersonArr;
    }

    public List<TemplatesData> getTemplatesData() {
        return templatesData;
    }

    public void setTemplatesData(List<TemplatesData> templatesData) {
        this.templatesData = templatesData;
    }

    public List<Outlet> getOutlet() {
        return outlet;
    }

    public void setOutlet(List<Outlet> outlet) {
        this.outlet = outlet;
    }

    public List<CustomerWallet> getCustomerWallets() {
        return customerWallets;
    }

    public void setCustomerWallets(List<CustomerWallet> customerWallets) {
        this.customerWallets = customerWallets;
    }

    public List<ReferralAddContact> getReferralAddContact() {
        return referralAddContact;
    }

    public void setReferralAddContact(List<ReferralAddContact> referralAddContact) {
        this.referralAddContact = referralAddContact;
    }

    public List<ChargesList> getChargesList() {
        return chargesList;
    }

    public void setChargesList(List<ChargesList> chargesList) {
        this.chargesList = chargesList;
    }

    public List<TaxList> getTaxList() {
        return taxList;
    }

    public void setTaxList(List<TaxList> taxList) {
        this.taxList = taxList;
    }

    public List<ChargesList2> getChargesList2() {
        return chargesList2;
    }

    public void setChargesList2(List<ChargesList2> chargesList2) {
        this.chargesList2 = chargesList2;
    }

    public List<ChargesList3> getChargesList3() {
        return chargesList3;
    }

    public void setChargesList3(List<ChargesList3> chargesList3) {
        this.chargesList3 = chargesList3;
    }

    public List<AddressList> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<AddressList> addressList) {
        this.addressList = addressList;
    }

    public ItemData getItemData() {
        return itemData;
    }

    public void setItemData(ItemData itemData) {
        this.itemData = itemData;
    }

    public List<ItemList> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemList> itemList) {
        this.itemList = itemList;
    }

    public List<OtpMobileFetch> getOtpMobile() {
        return otpMobile;
    }

    public void setOtpMobile(List<OtpMobileFetch> otpMobile) {
        this.otpMobile = otpMobile;
    }

    public List<OtpEmailFetch> getOtpEmail() {
        return otpEmail;
    }

    public void setOtpEmail(List<OtpEmailFetch> otpEmail) {
        this.otpEmail = otpEmail;
    }

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<AttributsPtc> getAttributsPtc() {
        return attributsPtc;
    }

    public void setAttributsPtc(List<AttributsPtc> attributsPtc) {
        this.attributsPtc = attributsPtc;
    }

    public List<ConsignmentItem> getConsignmentItems() {
        return consignmentItems;
    }

    public void setConsignmentItems(List<ConsignmentItem> consignmentItems) {
        this.consignmentItems = consignmentItems;
    }

    public ConsignmentInfo getConsignmentInfo() {
        return consignmentInfo;
    }

    public void setConsignmentInfo(ConsignmentInfo consignmentInfo) {
        this.consignmentInfo = consignmentInfo;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<QuotationProduct> getQuotationProducts() {
        return quotationProducts;
    }

    public void setQuotationProducts(List<QuotationProduct> quotationProducts) {
        this.quotationProducts = quotationProducts;
    }

    public QuotationDetails getQuotationDetails() {
        return quotationDetails;
    }

    public void setQuotationDetails(QuotationDetails quotationDetails) {
        this.quotationDetails = quotationDetails;
    }

    public String getDefaultCountry() {
        return defaultCountry;
    }

    public void setDefaultCountry(String defaultCountry) {
        this.defaultCountry = defaultCountry;
    }

    public String getDefaultState() {
        return defaultState;
    }

    public void setDefaultState(String defaultState) {
        this.defaultState = defaultState;
    }

    public List<CityName> getCityNames() {
        return cityNames;
    }

    public void setCityNames(List<CityName> cityNames) {
        this.cityNames = cityNames;
    }

    public List<StateList> getStateList() {
        return stateList;
    }

    public void setStateList(List<StateList> stateList) {
        this.stateList = stateList;
    }

    public List<CountryList> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryList> countryList) {
        this.countryList = countryList;
    }

    public List<ReferralAddContact> getRefereralAddContact() {
        return referralAddContact;
    }

    public void setRefereralAddContact(List<ReferralAddContact> referralAddContact) {
        this.referralAddContact = referralAddContact;
    }

    public Integer getDefaultAccountGroupId() {
        return defaultAccountGroupId;
    }

    public void setDefaultAccountGroupId(Integer defaultAccountGroupId) {
        this.defaultAccountGroupId = defaultAccountGroupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ContactDetail> getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(List<ContactDetail> contactDetails) {
        this.contactDetails = contactDetails;
    }

    public List<SearchRefferedDatum> getSearchRefferedData() {
        return searchRefferedData;
    }

    public void setSearchRefferedData(List<SearchRefferedDatum> searchRefferedData) {
        this.searchRefferedData = searchRefferedData;
    }

    public List<Salutation> getSalutations() {
        return salutations;
    }

    public void setSalutations(List<Salutation> salutations) {
        this.salutations = salutations;
    }

    public List<CustomersType> getCustomersTypes() {
        return customersTypes;
    }

    public void setCustomersTypes(List<CustomersType> customersTypes) {
        this.customersTypes = customersTypes;
    }

    public List<CustomerSalesType> getCustomerSalesTypes() {
        return customerSalesTypes;
    }

    public void setCustomerSalesTypes(List<CustomerSalesType> customerSalesTypes) {
        this.customerSalesTypes = customerSalesTypes;
    }

    public List<AccountGroup> getAccountGroups() {
        return accountGroups;
    }

    public void setAccountGroups(List<AccountGroup> accountGroups) {
        this.accountGroups = accountGroups;
    }

    public List<Collection> getCollection() {
        return collection;
    }

    public void setCollection(List<Collection> collection) {
        this.collection = collection;
    }

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public List<Packages> getPackages() {
        return packages;
    }

    public void setPackages(List<Packages> packages) {
        this.packages = packages;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<CustomerAllInvoice> getCustomerAllInvoice() {
        return customerAllInvoice;
    }

    public void setCustomerAllInvoice(List<CustomerAllInvoice> customerAllInvoice) {
        this.customerAllInvoice = customerAllInvoice;
    }

    public List<VendorInvoice> getVendorInvoice() {
        return vendorInvoice;
    }

    public void setVendorInvoice(List<VendorInvoice> vendorInvoice) {
        this.vendorInvoice = vendorInvoice;
    }

    public List<Consignment> getConsignment() {
        return consignment;
    }

    public void setConsignment(List<Consignment> consignment) {
        this.consignment = consignment;
    }

    public List<PurchaseOrder> getPurchaseOrder() {
        return purchaseOrder;
    }

    public void setPurchaseOrder(List<PurchaseOrder> purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    public String getAuevid() {
        return auevid;
    }

    public void setAuevid(String auevid) {
        this.auevid = auevid;
    }

    public String getAumvid() {
        return aumvid;
    }

    public void setAumvid(String aumvid) {
        this.aumvid = aumvid;
    }

    public String getCurrentFollowUpId() {
        return currentFollowUpId;
    }

    public void setCurrentFollowUpId(String currentFollowUpId) {
        this.currentFollowUpId = currentFollowUpId;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public List<VendorRecentTransaction> getVendorRecentTransaction() {
        return vendorRecentTransaction;
    }

    public void setVendorRecentTransaction(List<VendorRecentTransaction> vendorRecentTransaction) {
        this.vendorRecentTransaction = vendorRecentTransaction;
    }

    public List<Vendor> getVendor() {
        return vendor;
    }

    public void setVendor(List<Vendor> vendor) {
        this.vendor = vendor;
    }

    public List<VendorPendingInvoice> getVendorPendingInvoice() {
        return vendorPendingInvoice;
    }

    public void setVendorPendingInvoice(List<VendorPendingInvoice> vendorPendingInvoice) {
        this.vendorPendingInvoice = vendorPendingInvoice;
    }

    public List<Vendors> getVendors() {
        return vendors;
    }

    public void setVendors(List<Vendors> vendors) {
        this.vendors = vendors;
    }

    public List<Customers> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customers> customers) {
        this.customers = customers;
    }

    public List<CustomerPendingInvoice> getCustomerPendingInvoice() {
        return customerPendingInvoice;
    }

    public void setCustomerPendingInvoice(List<CustomerPendingInvoice> customerPendingInvoice) {
        this.customerPendingInvoice = customerPendingInvoice;
    }

    public List<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(List<Customer> customer) {
        this.customer = customer;
    }

    public List<CustomerWallet> getCustomerRecentTransaction() {
        return customerWallets;
    }

    public void setCustomerRecentTransaction(List<CustomerWallet> customerRecentTransaction) {
        this.customerWallets = customerRecentTransaction;
    }

    public List<Quotation> getQuotation() {
        return quotation;
    }

    public void setQuotation(List<Quotation> quotation) {
        this.quotation = quotation;
    }

    public List<Enquiry> getEnquiry() {
        return enquiry;
    }

    public void setEnquiry(List<Enquiry> enquiry) {
        this.enquiry = enquiry;
    }

    public List<OtpMobileFetch> getOtpMobilefetch() {
        return otpMobilefetch;
    }

    public void setOtpMobilefetch(List<OtpMobileFetch> otpMobilefetch) {
        this.otpMobilefetch = otpMobilefetch;
    }

    public List<OtpEmailFetch> getOtpEmailfetch() {
        return otpEmailfetch;
    }

    public void setOtpEmailfetch(List<OtpEmailFetch> otpEmailfetch) {
        this.otpEmailfetch = otpEmailfetch;
    }

    public List<RecentLogin> getRecentLogin() {
        return recentLogin;
    }

    public void setRecentLogin(List<RecentLogin> recentLogin) {
        this.recentLogin = recentLogin;
    }


    public List<Notify> getNotify() {
        return notify;
    }

    public void setNotify(List<Notify> notify) {
        this.notify = notify;
    }

    public String getFoid() {
        return foid;
    }

    public void setFoid(String foid) {
        this.foid = foid;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFollowupPendingcount() {
        return followupPendingcount;
    }

    public void setFollowupPendingcount(String followupPendingcount) {
        this.followupPendingcount = followupPendingcount;
    }

    public String getLeadNewcount() {
        return leadNewcount;
    }

    public void setLeadNewcount(String leadNewcount) {
        this.leadNewcount = leadNewcount;
    }

    public int getOtpSettings() {
        return otpSettings;
    }

    public void setOtpSettings(int otpSettings) {
        this.otpSettings = otpSettings;
    }
    /* public List<ChatConversations> getChatConversations() {
        return chatConversations;
    }

    public void ChatConversations(List<ChatConversations> chatConversations) {
        this.chatConversations = chatConversations;
    }*/

    public List<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    public List<ChatMessage> getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(List<ChatMessage> chatMessage) {
        this.chatMessage = chatMessage;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<Lead> getLead() {
        return lead;
    }

    public void setLead(List<Lead> lead) {
        this.lead = lead;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Permission> getPermission() {
        return permission;
    }

    public void setPermission(List<Permission> permission) {
        this.permission = permission;
    }

    public List<FollowUp> getFollowups() {
        return followups;
    }

    public void setFollowups(List<FollowUp> followups) {
        this.followups = followups;
    }

    public List<Notification> getNotification() {
        return notification;
    }

    public void setNotification(List<Notification> notification) {
        this.notification = notification;
    }

    public List<NotificationTime> getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(List<NotificationTime> notificationTime) {
        this.notificationTime = notificationTime;
    }


    public List<ChatContacts> getChatContacts() {
        return chatContacts;
    }

    public void setChatContacts(List<ChatContacts> chatContacts) {
        this.chatContacts = chatContacts;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
