package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/11/18.
 */

public class ItemLabels {

    @SerializedName("item_variation")
    @Expose
    private Boolean itemVariation;
    @SerializedName("hsn_code")
    @Expose
    private Boolean hsnCode;
    @SerializedName("sku_code")
    @Expose
    private Boolean skuCode;
    @SerializedName("order_quantity")
    @Expose
    private Boolean orderQuantity;
    @SerializedName("receiving_quantity")
    @Expose
    private Boolean receivingQuantity;
    @SerializedName("unit")
    @Expose
    private Boolean unit;
    @SerializedName("price")
    @Expose
    private Boolean price;
    @SerializedName("comment")
    @Expose
    private Boolean comment;
    @SerializedName("box_qty")
    @Expose
    private Boolean boxQty;
    @SerializedName("manufacturing_date")
    @Expose
    private Boolean manufacturingDate;
    @SerializedName("expiry_date")
    @Expose
    private Boolean expiryDate;
    @SerializedName("barcode_title")
    @Expose
    private Boolean barcodeTitle;
    @SerializedName("barcode_title_name")
    @Expose
    private String barcodeTitleName;
    @SerializedName("reason_for_rejection")
    @Expose
    private Boolean reasonForRejection;
    @SerializedName("rejected_quantity")
    @Expose
    private Boolean rejectedQuantity;

    public Boolean getItemVariation() {
        return itemVariation;
    }

    public void setItemVariation(Boolean itemVariation) {
        this.itemVariation = itemVariation;
    }

    public Boolean getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(Boolean hsnCode) {
        this.hsnCode = hsnCode;
    }

    public Boolean getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(Boolean skuCode) {
        this.skuCode = skuCode;
    }

    public Boolean getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(Boolean orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public Boolean getReceivingQuantity() {
        return receivingQuantity;
    }

    public void setReceivingQuantity(Boolean receivingQuantity) {
        this.receivingQuantity = receivingQuantity;
    }

    public Boolean getUnit() {
        return unit;
    }

    public void setUnit(Boolean unit) {
        this.unit = unit;
    }

    public Boolean getPrice() {
        return price;
    }

    public void setPrice(Boolean price) {
        this.price = price;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }

    public Boolean getBoxQty() {
        return boxQty;
    }

    public void setBoxQty(Boolean boxQty) {
        this.boxQty = boxQty;
    }

    public Boolean getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(Boolean manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public Boolean getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Boolean expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Boolean getBarcodeTitle() {
        return barcodeTitle;
    }

    public void setBarcodeTitle(Boolean barcodeTitle) {
        this.barcodeTitle = barcodeTitle;
    }

    public String getBarcodeTitleName() {
        return barcodeTitleName;
    }

    public void setBarcodeTitleName(String barcodeTitleName) {
        this.barcodeTitleName = barcodeTitleName;
    }

    public Boolean getReasonForRejection() {
        return reasonForRejection;
    }

    public void setReasonForRejection(Boolean reasonForRejection) {
        this.reasonForRejection = reasonForRejection;
    }

    public Boolean getRejectedQuantity() {
        return rejectedQuantity;
    }

    public void setRejectedQuantity(Boolean rejectedQuantity) {
        this.rejectedQuantity = rejectedQuantity;
    }

}
