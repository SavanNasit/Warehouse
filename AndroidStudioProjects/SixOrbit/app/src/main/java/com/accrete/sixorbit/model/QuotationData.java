package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 22/3/18.
 */

public class QuotationData {
    @SerializedName("smstid")
    @Expose
    private String smstid;
    @SerializedName("etid")
    @Expose
    private String etid;
    @SerializedName("smsTemplate_data")
    @Expose
    private List<SmsTemplateDatum> smsTemplateData = null;
    @SerializedName("emailTemplatesData")
    @Expose
    private List<EmailTemplateArr> emailTemplatesData = null;
    @SerializedName("customer_info")
    @Expose
    private CustomerInfo customerInfo;
    @SerializedName("tax_list")
    @Expose
    private List<TaxList> taxList = null;
    /*  @SerializedName("current_addresses")
          @Expose
          private List<CurrentAddress> currentAddresses = null;
          @SerializedName("site_addresses")
          @Expose
          private List<SiteAddress> siteAddresses = null;*/
    @SerializedName("atribute_data")
    @Expose
    private List<Object> atributeData = null;
    @SerializedName("quotation_items_data")
    @Expose
    private List<ItemData> quotationItemsData = null;
    @SerializedName("taxExtraData")
    @Expose
    private TaxExtraData taxExtraData;
    @SerializedName("charges_list")
    @Expose
    private List<ChargesList> chargesList = null;
    @SerializedName("charges_list2")
    @Expose
    private List<ChargesList2> chargesList2 = null;
    @SerializedName("assigned_data")
    @Expose
    private List<AssigneeData> assigneeData = null;
    @SerializedName("extensive_tax_required")
    @Expose
    private String extensiveTaxRequired;
    @SerializedName("charges_list3")
    @Expose
    private List<ChargesList3> chargesList3 = null;
    @SerializedName("qotemid")
    @Expose
    private String qotemid;
    @SerializedName("chkId")
    @Expose
    private String chkId;
    @SerializedName("quotation_remark")
    @Expose
    private String quotationRemark;
    @SerializedName("current_addresses")
    @Expose
    private List<AddressList> currentAddresses = null;
    @SerializedName("site_addresses")
    @Expose
    private List<AddressList> siteAddresses = null;
    @SerializedName("extra_discountData")
    @Expose
    private List<ExtraDiscountData> extraDiscountData = null;
    @SerializedName("image_show")
    @Expose
    private Boolean imageShow;
    @SerializedName("taxInclusive")
    @Expose
    private Boolean taxInclusive;
    //TODO Added on 22nd June 2k18
    @SerializedName("is_outlet_enable")
    @Expose
    private Boolean isOutletEnable;
    @SerializedName("outlet")
    @Expose
    private List<Outlet> outlet = null;

    public List<Outlet> getOutlet() {
        return outlet;
    }

    public void setOutlet(List<Outlet> outlet) {
        this.outlet = outlet;
    }

    public Boolean getOutletEnable() {
        return isOutletEnable;
    }

    public void setOutletEnable(Boolean outletEnable) {
        isOutletEnable = outletEnable;
    }

    public List<ExtraDiscountData> getExtraDiscountData() {
        return extraDiscountData;
    }

    public void setExtraDiscountData(List<ExtraDiscountData> extraDiscountData) {
        this.extraDiscountData = extraDiscountData;
    }

    public Boolean getImageShow() {
        return imageShow;
    }

    public void setImageShow(Boolean imageShow) {
        this.imageShow = imageShow;
    }

    public Boolean getTaxInclusive() {
        return taxInclusive;
    }

    public void setTaxInclusive(Boolean taxInclusive) {
        this.taxInclusive = taxInclusive;
    }

    public List<AddressList> getCurrentAddresses() {
        return currentAddresses;
    }

    public void setCurrentAddresses(List<AddressList> currentAddresses) {
        this.currentAddresses = currentAddresses;
    }

    public List<AddressList> getSiteAddresses() {
        return siteAddresses;
    }

    public void setSiteAddresses(List<AddressList> siteAddresses) {
        this.siteAddresses = siteAddresses;
    }

    public String getQotemid() {
        return qotemid;
    }

    public void setQotemid(String qotemid) {
        this.qotemid = qotemid;
    }

    public String getChkId() {
        return chkId;
    }

    public void setChkId(String chkId) {
        this.chkId = chkId;
    }

    public String getQuotationRemark() {
        return quotationRemark;
    }

    public void setQuotationRemark(String quotationRemark) {
        this.quotationRemark = quotationRemark;
    }

    public List<TaxList> getTaxList() {
        return taxList;
    }

    public void setTaxList(List<TaxList> taxList) {
        this.taxList = taxList;
    }

    public List<ChargesList3> getChargesList3() {
        return chargesList3;
    }

    public void setChargesList3(List<ChargesList3> chargesList3) {
        this.chargesList3 = chargesList3;
    }

    public String getExtensiveTaxRequired() {
        return extensiveTaxRequired;
    }

    public void setExtensiveTaxRequired(String extensiveTaxRequired) {
        this.extensiveTaxRequired = extensiveTaxRequired;
    }

    public List<SmsTemplateDatum> getSmsTemplateData() {
        return smsTemplateData;
    }

    public void setSmsTemplateData(List<SmsTemplateDatum> smsTemplateData) {
        this.smsTemplateData = smsTemplateData;
    }

    public List<EmailTemplateArr> getEmailTemplatesData() {
        return emailTemplatesData;
    }

    public void setEmailTemplatesData(List<EmailTemplateArr> emailTemplatesData) {
        this.emailTemplatesData = emailTemplatesData;
    }

    public List<AssigneeData> getAssigneeData() {
        return assigneeData;
    }

    public void setAssigneeData(List<AssigneeData> assigneeData) {
        this.assigneeData = assigneeData;
    }

    public List<ChargesList2> getChargesList2() {
        return chargesList2;
    }

    public void setChargesList2(List<ChargesList2> chargesList2) {
        this.chargesList2 = chargesList2;
    }

    public List<ChargesList> getChargesList() {
        return chargesList;
    }

    public void setChargesList(List<ChargesList> chargesList) {
        this.chargesList = chargesList;
    }

    public TaxExtraData getTaxExtraData() {
        return taxExtraData;
    }

    public void setTaxExtraData(TaxExtraData taxExtraData) {
        this.taxExtraData = taxExtraData;
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

    public CustomerInfo getCustomerInfo() {
        return customerInfo;
    }

    public void setCustomerInfo(CustomerInfo customerInfo) {
        this.customerInfo = customerInfo;
    }

    public List<Object> getAtributeData() {
        return atributeData;
    }

    public void setAtributeData(List<Object> atributeData) {
        this.atributeData = atributeData;
    }

    public List<ItemData> getQuotationItemsData() {
        return quotationItemsData;
    }

    public void setQuotationItemsData(List<ItemData> quotationItemsData) {
        this.quotationItemsData = quotationItemsData;
    }
}
