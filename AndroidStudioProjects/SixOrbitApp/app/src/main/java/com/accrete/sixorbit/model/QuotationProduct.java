package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 31/10/17.
 */

public class QuotationProduct {
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("measurement_name")
    @Expose
    private String measurementName;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("discounted_amount")
    @Expose
    private String discountedAmount;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    @SerializedName("item_tax")
    @Expose
    private String itemTax;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("iitid")
    @Expose
    private String iitid;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("meaid")
    @Expose
    private String meaid;
    @SerializedName("box_qty")
    @Expose
    private String boxQty;
    @SerializedName("item_amount")
    @Expose
    private String itemAmount;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("dealer_price")
    @Expose
    private Object dealerPrice;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("item_tax_amount")
    @Expose
    private String itemTaxAmount;
    @SerializedName("butapid")
    @Expose
    private String butapid;
    @SerializedName("tax_name")
    @Expose
    private String taxName;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("is_price_include_tax_show")
    @Expose
    private boolean isPriceIncludeTaxShow;

    public boolean isPriceIncludeTaxShow() {
        return isPriceIncludeTaxShow;
    }

    public void setPriceIncludeTaxShow(boolean priceIncludeTaxShow) {
        isPriceIncludeTaxShow = priceIncludeTaxShow;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMeasurementName() {
        return measurementName;
    }

    public void setMeasurementName(String measurementName) {
        this.measurementName = measurementName;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(String discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHsnCode() {
        return hsnCode;
    }

    public void setHsnCode(String hsnCode) {
        this.hsnCode = hsnCode;
    }

    public String getItemTax() {
        return itemTax;
    }

    public void setItemTax(String itemTax) {
        this.itemTax = itemTax;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getIitid() {
        return iitid;
    }

    public void setIitid(String iitid) {
        this.iitid = iitid;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getMeaid() {
        return meaid;
    }

    public void setMeaid(String meaid) {
        this.meaid = meaid;
    }

    public String getBoxQty() {
        return boxQty;
    }

    public void setBoxQty(String boxQty) {
        this.boxQty = boxQty;
    }

    public String getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(String itemAmount) {
        this.itemAmount = itemAmount;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Object getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(Object dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getItemTaxAmount() {
        return itemTaxAmount;
    }

    public void setItemTaxAmount(String itemTaxAmount) {
        this.itemTaxAmount = itemTaxAmount;
    }

    public String getButapid() {
        return butapid;
    }

    public void setButapid(String butapid) {
        this.butapid = butapid;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }
}