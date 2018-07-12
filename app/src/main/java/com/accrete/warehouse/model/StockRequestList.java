package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/3/18.
 */

public class StockRequestList{

    @SerializedName("oiid")
    @Expose
    private String oiid;
    @SerializedName("chkoid")
    @Expose
    private Object chkoid;
    @SerializedName("chkid")
    @Expose
    private String chkid;
    @SerializedName("chkoiid")
    @Expose
    private Object chkoiid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("stockreqiid")
    @Expose
    private String stockreqiid;
    @SerializedName("stockreqid")
    @Expose
    private String stockreqid;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("isid")
    @Expose
    private String isid;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("meaid")
    @Expose
    private Object meaid;
    @SerializedName("offid")
    @Expose
    private String offid;
    @SerializedName("discounted_price")
    @Expose
    private String discountedPrice;
    @SerializedName("item_price")
    @Expose
    private String itemPrice;
    @SerializedName("oisid")
    @Expose
    private String oisid;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private Object updatedTs;
    @SerializedName("assign_type")
    @Expose
    private String assignType;
    @SerializedName("assign_id")
    @Expose
    private Object assignId;
    @SerializedName("stockreqsid")
    @Expose
    private String stockreqsid;
    @SerializedName("stock_request_chkid")
    @Expose
    private String stockRequestChkid;
    @SerializedName("due_date")
    @Expose
    private String dueDate;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("created_user")
    @Expose
    private String createdUser;
    @SerializedName("request_chkid")
    @Expose
    private String requestChkid;
    @SerializedName("stock_request_number")
    @Expose
    private String stockRequestNumber;
    @SerializedName("request")
    @Expose
    private String request;
    @SerializedName("status")
    @Expose
    private String status;

    public String getOiid() {
        return oiid;
    }

    public void setOiid(String oiid) {
        this.oiid = oiid;
    }

    public Object getChkoid() {
        return chkoid;
    }

    public void setChkoid(Object chkoid) {
        this.chkoid = chkoid;
    }

    public String getChkid() {
        return chkid;
    }

    public void setChkid(String chkid) {
        this.chkid = chkid;
    }

    public Object getChkoiid() {
        return chkoiid;
    }

    public void setChkoiid(Object chkoiid) {
        this.chkoiid = chkoiid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getStockreqiid() {
        return stockreqiid;
    }

    public void setStockreqiid(String stockreqiid) {
        this.stockreqiid = stockreqiid;
    }

    public String getStockreqid() {
        return stockreqid;
    }

    public void setStockreqid(String stockreqid) {
        this.stockreqid = stockreqid;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Object getMeaid() {
        return meaid;
    }

    public void setMeaid(Object meaid) {
        this.meaid = meaid;
    }

    public String getOffid() {
        return offid;
    }

    public void setOffid(String offid) {
        this.offid = offid;
    }

    public String getDiscountedPrice() {
        return discountedPrice;
    }

    public void setDiscountedPrice(String discountedPrice) {
        this.discountedPrice = discountedPrice;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getOisid() {
        return oisid;
    }

    public void setOisid(String oisid) {
        this.oisid = oisid;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public Object getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(Object updatedTs) {
        this.updatedTs = updatedTs;
    }

    public String getAssignType() {
        return assignType;
    }

    public void setAssignType(String assignType) {
        this.assignType = assignType;
    }

    public Object getAssignId() {
        return assignId;
    }

    public void setAssignId(Object assignId) {
        this.assignId = assignId;
    }

    public String getStockreqsid() {
        return stockreqsid;
    }

    public void setStockreqsid(String stockreqsid) {
        this.stockreqsid = stockreqsid;
    }

    public String getStockRequestChkid() {
        return stockRequestChkid;
    }

    public void setStockRequestChkid(String stockRequestChkid) {
        this.stockRequestChkid = stockRequestChkid;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getRequestChkid() {
        return requestChkid;
    }

    public void setRequestChkid(String requestChkid) {
        this.requestChkid = requestChkid;
    }

    public String getStockRequestNumber() {
        return stockRequestNumber;
    }

    public void setStockRequestNumber(String stockRequestNumber) {
        this.stockRequestNumber = stockRequestNumber;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


