package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 3/9/18.
 */

public class OrderData implements Parcelable {
    public static final Creator<OrderData> CREATOR = new Creator<OrderData>() {
        @Override
        public OrderData createFromParcel(Parcel in) {
            return new OrderData(in);
        }

        @Override
        public OrderData[] newArray(int size) {
            return new OrderData[size];
        }
    };
    public String usedQuantity;
    @SerializedName("item_variation_name")
    @Expose
    private String itemVariationName;
    @SerializedName("item_sku_code")
    @Expose
    private String itemSkuCode;
    @SerializedName("item_quantity")
    @Expose
    private String itemQuantity;
    @SerializedName("item_unit")
    @Expose
    private String itemUnit;
    @SerializedName("item_status")
    @Expose
    private String itemStatus;
    @SerializedName("oiid")
    @Expose
    private String oiid;
    @SerializedName("chkoiid")
    @Expose
    private String chkoiid;
    @SerializedName("stockreqiid")
    @Expose
    private String stockreqiid;
    @SerializedName("isid")
    @Expose
    private String isid;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("meaid")
    @Expose
    private String meaid;
    @SerializedName("measurements")
    @Expose
    private List<Measurements> measurements = null;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("execute_item_data")
    @Expose
    private ExecuteItemData executeItemData;
    private double previousConversionRate;
    private String currentConversionRate;
    private String previousUnit;
    //TODO - Added on 4th of April
    @SerializedName("invoice_number")
    @Expose
    private String invoiceNumber;
    @SerializedName("invoice_number_label")
    @Expose
    private String invoiceNumberLabel;
    @SerializedName("isid_number")
    @Expose
    private String batchNumber;
    @SerializedName("variation_number")
    @Expose
    private String variationNumber;
    @SerializedName("barcode")
    @Expose
    private String barcode;


    protected OrderData(Parcel in) {
        itemVariationName = in.readString();
        itemSkuCode = in.readString();
        itemQuantity = in.readString();
        itemUnit = in.readString();
        itemStatus = in.readString();
        oiid = in.readString();
        chkoiid = in.readString();
        stockreqiid = in.readString();
        isid = in.readString();
        isvid = in.readString();
        iid = in.readString();
        meaid = in.readString();
        image = in.readString();
        usedQuantity = in.readString();
        previousConversionRate = in.readDouble();
        currentConversionRate = in.readString();
        previousUnit = in.readString();
        invoiceNumber = in.readString();
        invoiceNumberLabel = in.readString();
        batchNumber = in.readString();
        variationNumber = in.readString();
        barcode = in.readString();
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumberLabel() {
        return invoiceNumberLabel;
    }

    public void setInvoiceNumberLabel(String invoiceNumberLabel) {
        this.invoiceNumberLabel = invoiceNumberLabel;
    }

    public String getUsedQuantity() {
        return usedQuantity;
    }

    public void setUsedQuantity(String usedQuantity) {
        this.usedQuantity = usedQuantity;
    }

    public String getItemVariationName() {
        return itemVariationName;
    }

    public void setItemVariationName(String itemVariationName) {
        this.itemVariationName = itemVariationName;
    }

    public String getItemSkuCode() {
        return itemSkuCode;
    }

    public void setItemSkuCode(String itemSkuCode) {
        this.itemSkuCode = itemSkuCode;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(String itemStatus) {
        this.itemStatus = itemStatus;
    }

    public String getOiid() {
        return oiid;
    }

    public void setOiid(String oiid) {
        this.oiid = oiid;
    }

    public String getChkoiid() {
        return chkoiid;
    }

    public void setChkoiid(String chkoiid) {
        this.chkoiid = chkoiid;
    }

    public String getStockreqiid() {
        return stockreqiid;
    }

    public void setStockreqiid(String stockreqiid) {
        this.stockreqiid = stockreqiid;
    }

    public String getIsid() {
        return isid;
    }

    public void setIsid(String isid) {
        this.isid = isid;
    }

    public String getIsvid() {
        return isvid;
    }

    public void setIsvid(String isvid) {
        this.isvid = isvid;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getMeaid() {
        return meaid;
    }

    public void setMeaid(String meaid) {
        this.meaid = meaid;
    }

    public List<Measurements> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurements> measurements) {
        this.measurements = measurements;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ExecuteItemData getExecuteItemData() {
        return executeItemData;
    }

    public void setExecuteItemData(ExecuteItemData executeItemData) {
        this.executeItemData = executeItemData;
    }

    public double getPreviousConversionRate() {
        return previousConversionRate;
    }

    public void setPreviousConversionRate(double previousConversionRate) {
        this.previousConversionRate = previousConversionRate;
    }

    public String getCurrentConversionRate() {
        return currentConversionRate;
    }

    public void setCurrentConversionRate(String currentConversionRate) {
        this.currentConversionRate = currentConversionRate;
    }


    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public String getPreviousUnit() {
        return previousUnit;
    }

    public void setPreviousUnit(String previousUnit) {
        this.previousUnit = previousUnit;
    }

    public String getVariationNumber() {
        return variationNumber;
    }

    public void setVariationNumber(String variationNumber) {
        this.variationNumber = variationNumber;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemVariationName);
        dest.writeString(itemSkuCode);
        dest.writeString(itemQuantity);
        dest.writeString(itemUnit);
        dest.writeString(itemStatus);
        dest.writeString(oiid);
        dest.writeString(chkoiid);
        dest.writeString(stockreqiid);
        dest.writeString(isid);
        dest.writeString(isvid);
        dest.writeString(iid);
        dest.writeString(meaid);
        dest.writeString(image);
        dest.writeString(usedQuantity);
        dest.writeDouble(previousConversionRate);
        dest.writeString(currentConversionRate);
        dest.writeString(previousUnit);
        dest.writeString(invoiceNumber);
        dest.writeString(invoiceNumberLabel);
        dest.writeString(batchNumber);
        dest.writeString(variationNumber);
        dest.writeString(barcode);
    }


}