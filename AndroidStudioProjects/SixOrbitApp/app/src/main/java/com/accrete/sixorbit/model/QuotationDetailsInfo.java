package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 27/3/18.
 */

public class QuotationDetailsInfo {
    @SerializedName("quotation_details")
    @Expose
    private QuotationDetails quotationDetails;
    @SerializedName("quotation_items_data")
    @Expose
    private List<ItemData> quotationItemsData = null;
    @SerializedName("followupData")
    @Expose
    private List<FollowUp> followupData = null;

    public List<FollowUp> getFollowupData() {
        return followupData;
    }

    public void setFollowupData(List<FollowUp> followupData) {
        this.followupData = followupData;
    }

    public List<ItemData> getQuotationItemsData() {
        return quotationItemsData;
    }

    public void setQuotationItemsData(List<ItemData> quotationItemsData) {
        this.quotationItemsData = quotationItemsData;
    }

    public QuotationDetails getQuotationDetails() {

        return quotationDetails;
    }

    public void setQuotationDetails(QuotationDetails quotationDetails) {
        this.quotationDetails = quotationDetails;
    }
}
