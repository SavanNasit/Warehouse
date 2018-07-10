package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by {Anshul} on 23/3/18.
 */

public class TaxExtraData {

    @SerializedName("discountAfterMaterialCost")
    @Expose
    private Integer discountAfterMaterialCost;
    @SerializedName("discountTypeAfterMaterialCost")
    @Expose
    private String discountTypeAfterMaterialCost;
    @SerializedName("roundOff")
    @Expose
    private Integer roundOff;
    @SerializedName("chargeList")
    @Expose

    private List<ChargesList> chargeList = null;

    public Integer getDiscountAfterMaterialCost() {
        return discountAfterMaterialCost;
    }

    public void setDiscountAfterMaterialCost(Integer discountAfterMaterialCost) {
        this.discountAfterMaterialCost = discountAfterMaterialCost;
    }

    public String getDiscountTypeAfterMaterialCost() {
        return discountTypeAfterMaterialCost;
    }

    public void setDiscountTypeAfterMaterialCost(String discountTypeAfterMaterialCost) {
        this.discountTypeAfterMaterialCost = discountTypeAfterMaterialCost;
    }

    public Integer getRoundOff() {
        return roundOff;
    }

    public void setRoundOff(Integer roundOff) {
        this.roundOff = roundOff;
    }

    public List<ChargesList> getChargeList() {
        return chargeList;
    }

    public void setChargeList(List<ChargesList> chargeList) {
        this.chargeList = chargeList;
    }
}
