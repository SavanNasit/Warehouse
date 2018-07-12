package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by agt on 21/1/18.
 */

public class ItemList {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("packing")
    @Expose
    private String packing;
    @SerializedName("dealer_price")
    @Expose
    private String dealerPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getPacking() {
        return packing;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public String getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(String dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

        @SerializedName("isvid")
        @Expose
        private String isvid;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("iid")
        @Expose
        private String iid;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("item_hsn_code")
        @Expose
        private String itemHsnCode;
        @SerializedName("sku_code")
        @Expose
        private String skuCode;
        @SerializedName("order_quantity")
        @Expose
        private String orderQuantity;
        @SerializedName("receive_quantity")
        @Expose
        private int receiveQuantity;
        @SerializedName("box_qty_str")
        @Expose
        private String boxQtyStr;
        @SerializedName("measurements")
        @Expose
        private List<Measurements> measurements = null;
        @SerializedName("item_price")
        @Expose
        private String itemPrice;
        @SerializedName("barcode_mandatory")
        @Expose
        private String barcodeMandatory;


        public String getItemHsnCode() {
            return itemHsnCode;
        }

        public void setItemHsnCode(String itemHsnCode) {
            this.itemHsnCode = itemHsnCode;
        }

        public String getOrderQuantity() {
            return orderQuantity;
        }

        public void setOrderQuantity(String orderQuantity) {
            this.orderQuantity = orderQuantity;
        }

        public int getReceiveQuantity() {
            return receiveQuantity;
        }

        public void setReceiveQuantity(int receiveQuantity) {
            this.receiveQuantity = receiveQuantity;
        }

        public String getBoxQtyStr() {
            return boxQtyStr;
        }

        public void setBoxQtyStr(String boxQtyStr) {
            this.boxQtyStr = boxQtyStr;
        }

        public List<Measurements> getMeasurements() {
            return measurements;
        }

        public void setMeasurements(List<Measurements> measurements) {
            this.measurements = measurements;
        }

        public String getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(String itemPrice) {
            this.itemPrice = itemPrice;
        }

        public String getBarcodeMandatory() {
            return barcodeMandatory;
        }

        public void setBarcodeMandatory(String barcodeMandatory) {
            this.barcodeMandatory = barcodeMandatory;
        }

    }

