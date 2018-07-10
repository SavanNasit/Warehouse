package com.accrete.sixorbit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by agt on 21/12/17.
 */

public class ItemData implements Parcelable {

    public static final Creator<ItemData> CREATOR = new Creator<ItemData>() {
        @Override
        public ItemData createFromParcel(Parcel in) {
            return new ItemData(in);
        }

        @Override
        public ItemData[] newArray(int size) {
            return new ItemData[size];
        }
    };
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("measurement")
    @Expose
    private String measurement;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("meaid")
    @Expose
    private String meaid;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("conversion_rate")
    @Expose
    private String conversionRate;
    @SerializedName("price_conversion_rate")
    @Expose
    private String priceConversionRate;
    @SerializedName("box_conversion_rate")
    @Expose
    private String boxConversionRate;
    @SerializedName("sku_code")
    @Expose
    private String skuCode;
    @SerializedName("package_qty")
    @Expose
    private String packageQty;
    @SerializedName("item_same")
    @Expose
    private String itemSame;
    @SerializedName("taxes")
    @Expose
    private List<Tax> taxes = null;
    @SerializedName("butapid")
    @Expose
    private String butapid;
    @SerializedName("tax_value")
    @Expose
    private String taxValue;
    @SerializedName("stock_value")
    @Expose
    private String stockValue;
    @SerializedName("quantity")
    @Expose
    private String quantity;
    @SerializedName("units_data")
    @Expose
    private List<UnitsData> unitsData = null;
    @SerializedName("isvid")
    @Expose
    private String isvid;
    @SerializedName("iid")
    @Expose
    private String iid;
    @SerializedName("iitid")
    @Expose
    private String iitid;
    private String selectedTaxValue;
    @SerializedName("measurement_name")
    @Expose
    private String quantityType;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("discount_type")
    @Expose
    private String discountType;
    @SerializedName("discounted_amount")
    @Expose
    private String discountedAmount;
    @SerializedName("box_qty")
    @Expose
    private String boxQty;
    @SerializedName("box_unit_name")
    @Expose
    private String boxUnitName;
    @SerializedName("measurements")
    @Expose
    private Measurements measurements;
    @SerializedName("dealer_price")
    @Expose
    private String dealerPrice;
    private String dealerPriceSubtotal;
    private String strDealerFlag;
    @SerializedName("item_taxes")
    @Expose
    private List<Tax> itemTaxes = null;
    @SerializedName("tax")
    @Expose
    private String tax;
    @SerializedName("item_amount")
    @Expose
    private String amount;
    @SerializedName("subtotal")
    @Expose
    private String subtotalAmount;
    //New attributes of Quotation Details
    @SerializedName("hsn_code")
    @Expose
    private String hsnCode;
    @SerializedName("item_tax_amount")
    @Expose
    private String itemTaxAmount;
    @SerializedName("tax_name")
    @Expose
    private String taxName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("remarks")
    @Expose
    private String remarks;
    //TODO - New vars added on 4th of April
    @SerializedName("item_id")
    @Expose
    private String itemId;
    @SerializedName("item_name")
    @Expose
    private String itemName;
    @SerializedName("internal_code")
    @Expose
    private String internalCode;
    @SerializedName("unit")
    @Expose
    private String unit;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("item_tax")
    @Expose
    private String itemTax;
    @SerializedName("sub_total")
    @Expose
    private String subTotal;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("allocated_quantity")
    @Expose
    private String allocatedQuantity;
    //TODO - Added new variables on 6th April
    @SerializedName("selected")
    @Expose
    private String selected;
    @SerializedName("cr")
    @Expose
    private String cr;
    @SerializedName("chkoiid")
    @Expose
    private String chkoiid;
    @SerializedName("stock")
    @Expose
    private String stock;
    @SerializedName("tax_price")
    @Expose
    private String taxPrice;
    @SerializedName("price_fixed")
    @Expose
    private String priceFixed;
    @SerializedName("is_allocated_item")
    @Expose
    private boolean isAllocatedItem = false;
    @SerializedName("checkpoint_order_remarks")
    @Expose
    private String checkpointOrderRemarks;
    //TODO Added on 2nd May
    @SerializedName("selected_tax_name")
    @Expose
    private String selectedTaxName;
    //TODO Added on 10th May
    @SerializedName("is_price_include_tax_show")
    @Expose
    private boolean isPriceIncludeTaxShow;

    public ItemData() {
    }

    public ItemData(Parcel in) {
        taxes = new ArrayList<Tax>();
        unitsData = new ArrayList<UnitsData>();
        name = in.readString();
        measurement = in.readString();
        image = in.readString();
        meaid = in.readString();
        unitName = in.readString();
        conversionRate = in.readString();
        priceConversionRate = in.readString();
        boxConversionRate = in.readString();
        boxQty = in.readString();
        skuCode = in.readString();
        packageQty = in.readString();
        itemSame = in.readString();
        taxes = in.readArrayList(Tax.class.getClassLoader());
        butapid = in.readString();
        taxValue = in.readString();
        stockValue = in.readString();
        quantity = in.readString();
        price = in.readString();
        unitsData = in.readArrayList(UnitsData.class.getClassLoader());
        isvid = in.readString();
        iid = in.readString();
        iitid = in.readString();
        tax = in.readString();
        selectedTaxValue = in.readString();
        discount = in.readString();
        discountType = in.readString();
        amount = in.readString();
        subtotalAmount = in.readString();
        quantityType = in.readString();
        remarks = in.readString();
        hsnCode = in.readString();
        itemTax = in.readString();
        itemTaxAmount = in.readString();
        taxName = in.readString();
        dealerPrice = in.readString();
        dealerPriceSubtotal = in.readString();
        strDealerFlag = in.readString();
        type = in.readString();
        itemId = in.readString();
        itemName = in.readString();
        internalCode = in.readString();
        unit = in.readString();
        price = in.readString();
        subTotal = in.readString();
        createdTs = in.readString();
        allocatedQuantity = in.readString();
        //TODO - Added new variables on 6th April
        selected = in.readString();
        cr = in.readString();
        chkoiid = in.readString();
        stock = in.readString();
        taxPrice = in.readString();
        priceFixed = in.readString();
        isAllocatedItem = in.readInt() == 1;
        checkpointOrderRemarks = in.readString();
        selectedTaxName = in.readString();
        isPriceIncludeTaxShow = in.readInt() == 1;
    }

    public static Creator<ItemData> getCREATOR() {
        return CREATOR;
    }

    public boolean isPriceIncludeTaxShow() {
        return isPriceIncludeTaxShow;
    }

    public void setPriceIncludeTaxShow(boolean priceIncludeTaxShow) {
        isPriceIncludeTaxShow = priceIncludeTaxShow;
    }

    public boolean isAllocatedItem() {
        return isAllocatedItem;
    }

    public void setAllocatedItem(boolean allocatedItem) {
        isAllocatedItem = allocatedItem;
    }

    public String getSelectedTaxName() {
        return selectedTaxName;
    }

    public void setSelectedTaxName(String selectedTaxName) {
        this.selectedTaxName = selectedTaxName;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(String internalCode) {
        this.internalCode = internalCode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(String subTotal) {
        this.subTotal = subTotal;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getAllocatedQuantity() {
        return allocatedQuantity;
    }

    public void setAllocatedQuantity(String allocatedQuantity) {
        this.allocatedQuantity = allocatedQuantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStrDealerFlag() {
        return strDealerFlag;
    }

    public void setStrDealerFlag(String strDealerFlag) {
        this.strDealerFlag = strDealerFlag;
    }

    public String getDealerPriceSubtotal() {
        return dealerPriceSubtotal;
    }

    public void setDealerPriceSubtotal(String dealerPriceSubtotal) {
        this.dealerPriceSubtotal = dealerPriceSubtotal;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
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

    public String getItemTaxAmount() {
        return itemTaxAmount;
    }

    public void setItemTaxAmount(String itemTaxAmount) {
        this.itemTaxAmount = itemTaxAmount;
    }

    public String getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(String discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public String getBoxUnitName() {
        return boxUnitName;
    }

    public void setBoxUnitName(String boxUnitName) {
        this.boxUnitName = boxUnitName;
    }

    public Measurements getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    public String getDealerPrice() {
        return dealerPrice;
    }

    public void setDealerPrice(String dealerPrice) {
        this.dealerPrice = dealerPrice;
    }

    public List<Tax> getItemTaxes() {
        return itemTaxes;
    }

    public void setItemTaxes(List<Tax> itemTaxes) {
        this.itemTaxes = itemTaxes;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
    }

    public String getCr() {
        return cr;
    }

    public void setCr(String cr) {
        this.cr = cr;
    }

    public String getChkoiid() {
        return chkoiid;
    }

    public void setChkoiid(String chkoiid) {
        this.chkoiid = chkoiid;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getTaxPrice() {
        return taxPrice;
    }

    public void setTaxPrice(String taxPrice) {
        this.taxPrice = taxPrice;
    }

    public String getPriceFixed() {
        return priceFixed;
    }

    public void setPriceFixed(String priceFixed) {
        this.priceFixed = priceFixed;
    }

    public Boolean getAllocatedItem() {
        return isAllocatedItem;
    }

    public void setAllocatedItem(Boolean allocatedItem) {
        isAllocatedItem = allocatedItem;
    }

    public String getCheckpointOrderRemarks() {
        return checkpointOrderRemarks;
    }

    public void setCheckpointOrderRemarks(String checkpointOrderRemarks) {
        this.checkpointOrderRemarks = checkpointOrderRemarks;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(measurement);
        dest.writeString(image);
        dest.writeString(meaid);
        dest.writeString(unitName);
        dest.writeString(conversionRate);
        dest.writeString(priceConversionRate);
        dest.writeString(boxConversionRate);
        dest.writeString(boxQty);
        dest.writeString(skuCode);
        dest.writeString(packageQty);
        dest.writeString(itemSame);
        dest.writeList(taxes);
        dest.writeString(butapid);
        dest.writeString(taxValue);
        dest.writeString(stockValue);
        dest.writeString(quantity);
        dest.writeString(price);
        dest.writeList(unitsData);
        dest.writeString(isvid);
        dest.writeString(iid);
        dest.writeString(iitid);
        dest.writeString(tax);
        dest.writeString(selectedTaxValue);
        dest.writeString(discount);
        dest.writeString(discountType);
        dest.writeString(amount);
        dest.writeString(subtotalAmount);
        dest.writeString(quantityType);
        dest.writeString(remarks);
        dest.writeString(hsnCode);
        dest.writeString(itemTax);
        dest.writeString(itemTaxAmount);
        dest.writeString(taxName);
        dest.writeString(dealerPrice);
        dest.writeString(dealerPriceSubtotal);
        dest.writeString(strDealerFlag);
        dest.writeString(type);
        dest.writeString(itemId);
        dest.writeString(itemName);
        dest.writeString(internalCode);
        dest.writeString(unit);
        dest.writeString(price);
        dest.writeString(subTotal);
        dest.writeString(createdTs);
        dest.writeString(allocatedQuantity);
        //TODO - Added new variables on 6th April
        dest.writeString(selected);
        dest.writeString(cr);
        dest.writeString(chkoiid);
        dest.writeString(stock);
        dest.writeString(taxPrice);
        dest.writeString(priceFixed);
        dest.writeInt(isAllocatedItem ? 1 : 0);
        dest.writeString(checkpointOrderRemarks);
        dest.writeString(selectedTaxName);
        dest.writeInt(isPriceIncludeTaxShow ? 1 : 0);
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getIitid() {
        return iitid;
    }

    public void setIitid(String iitid) {
        this.iitid = iitid;
    }

    public String getSelectedTaxValue() {
        return selectedTaxValue;
    }

    public void setSelectedTaxValue(String selectedTaxValue) {
        this.selectedTaxValue = selectedTaxValue;
    }

    public String getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(String quantityType) {
        this.quantityType = quantityType;
    }

    public String getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(String subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public String getBoxQty() {
        return boxQty;
    }

    public void setBoxQty(String boxQty) {
        this.boxQty = boxQty;
    }

    public String getItemSame() {
        return itemSame;
    }

    public void setItemSame(String itemSame) {
        this.itemSame = itemSame;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public String getPackageQty() {
        return packageQty;
    }

    public void setPackageQty(String packageQty) {
        this.packageQty = packageQty;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Tax> taxes) {
        this.taxes = taxes;
    }

    public String getButapid() {
        return butapid;
    }

    public void setButapid(String butapid) {
        this.butapid = butapid;
    }

    public String getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(String taxValue) {
        this.taxValue = taxValue;
    }

    public String getStockValue() {
        return stockValue;
    }

    public void setStockValue(String stockValue) {
        this.stockValue = stockValue;
    }

    public String getTax() {
        return tax;
    }

    public void setTax(String tax) {
        this.tax = tax;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<UnitsData> getUnitsData() {
        return unitsData;
    }

    public void setUnitsData(List<UnitsData> unitsData) {
        this.unitsData = unitsData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMeasurement() {
        return measurement;
    }

    public void setMeasurement(String measurement) {
        this.measurement = measurement;
    }

    public String getMeaid() {
        return meaid;
    }

    public void setMeaid(String meaid) {
        this.meaid = meaid;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate) {
        this.conversionRate = conversionRate;
    }

    public String getPriceConversionRate() {
        return priceConversionRate;
    }

    public void setPriceConversionRate(String priceConversionRate) {
        this.priceConversionRate = priceConversionRate;
    }

    public String getBoxConversionRate() {
        return boxConversionRate;
    }

    public void setBoxConversionRate(String boxConversionRate) {
        this.boxConversionRate = boxConversionRate;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
