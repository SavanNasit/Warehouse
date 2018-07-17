package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 4/4/18.
 */

public class CheckpointOrderData {

    @SerializedName("order_details")
    @Expose
    private OrderDetails orderDetails;
    @SerializedName("order_items_data")
    @Expose
    private List<ItemData> orderItemsData = null;
    @SerializedName("followupData")
    @Expose
    private List<FollowUp> followupData = null;

    public List<FollowUp> getFollowupData() {
        return followupData;
    }

    public void setFollowupData(List<FollowUp> followupData) {
        this.followupData = followupData;
    }

    public OrderDetails getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(OrderDetails orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<ItemData> getOrderItemsData() {
        return orderItemsData;
    }

    public void setOrderItemsData(List<ItemData> orderItemsData) {
        this.orderItemsData = orderItemsData;
    }
}
