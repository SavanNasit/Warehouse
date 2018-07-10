package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 22/12/17.
 */

public class ChargesList {
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("ecid")
    @Expose
    private String ecid;
    @SerializedName("ectid")
    @Expose
    private String ectid;
    private String discountAmountValue;
    @SerializedName("value")
    @Expose
    private String discountValue;
    private int selectedIndex;
    @SerializedName("butapid")
    @Expose
    private String butapid;

    public String getButapid() {
        return butapid;
    }

    public void setButapid(String butapid) {
        this.butapid = butapid;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public String getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(String discountValue) {
        this.discountValue = discountValue;
    }

    public String getDiscountAmountValue() {
        return discountAmountValue;
    }

    public void setDiscountAmountValue(String discountAmountValue) {
        this.discountAmountValue = discountAmountValue;
    }

    public String getEctid() {
        return ectid;
    }

    public void setEctid(String ectid) {
        this.ectid = ectid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEcid() {
        return ecid;
    }

    public void setEcid(String ecid) {
        this.ecid = ecid;
    }
}
