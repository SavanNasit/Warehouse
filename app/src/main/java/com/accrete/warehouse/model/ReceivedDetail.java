package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 19/1/18.
 */

public class ReceivedDetail {
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("po_quantity")
    @Expose
    private String poQuantity;
    @SerializedName("received_quantity")
    @Expose
    private String receivedQuantity;
    @SerializedName("item_received")
    @Expose
    private String itemReceived;

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getPoQuantity() {
        return poQuantity;
    }

    public void setPoQuantity(String poQuantity) {
        this.poQuantity = poQuantity;
    }

    public String getReceivedQuantity() {
        return receivedQuantity;
    }

    public void setReceivedQuantity(String receivedQuantity) {
        this.receivedQuantity = receivedQuantity;
    }

    public String getItemReceived() {
        return itemReceived;
    }

    public void setItemReceived(String itemReceived) {
        this.itemReceived = itemReceived;
    }
}
