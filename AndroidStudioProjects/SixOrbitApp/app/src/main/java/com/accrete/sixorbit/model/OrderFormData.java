package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 30/3/18.
 */

public class OrderFormData {
    @SerializedName("outlet")
    @Expose
    private List<Outlet> outlet = null;
    @SerializedName("templates_Data")
    @Expose
    private List<TemplatesData> templates = null;
    @SerializedName("ledgerInfo")

    @Expose
    private LedgerInfo ledgerInfo;
    @SerializedName("customer_sales_types")
    @Expose
    private List<CustomerSalesType> salesTypes = null;
    @SerializedName("paymentModeData")
    @Expose
    private List<PaymentModeData> paymentModeData = null;

    public List<Outlet> getOutlet() {
        return outlet;
    }

    public void setOutlet(List<Outlet> outlet) {
        this.outlet = outlet;
    }

    public List<TemplatesData> getTemplates() {
        return templates;
    }

    public void setTemplates(List<TemplatesData> templates) {
        this.templates = templates;
    }

    public LedgerInfo getLedgerInfo() {
        return ledgerInfo;
    }

    public void setLedgerInfo(LedgerInfo ledgerInfo) {
        this.ledgerInfo = ledgerInfo;
    }

    public List<CustomerSalesType> getSalesTypes() {
        return salesTypes;
    }

    public void setSalesTypes(List<CustomerSalesType> salesTypes) {
        this.salesTypes = salesTypes;
    }

    public List<PaymentModeData> getPaymentModeData() {
        return paymentModeData;
    }

    public void setPaymentModeData(List<PaymentModeData> paymentModeData) {
        this.paymentModeData = paymentModeData;
    }

}
