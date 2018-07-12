package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 28/12/17.
 */

public class ContactPersonTypeData {
    @SerializedName("attribute-id")
    @Expose
    private String attributeId;
    @SerializedName("attribute-name")
    @Expose
    private String attributeName;
    @SerializedName("cusavid")
    @Expose
    private String cusavid;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("data")
    @Expose
    private String data;
    @SerializedName("extra-attribute")
    @Expose
    private String extraAttribute;

    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getCusavid() {
        return cusavid;
    }

    public void setCusavid(String cusavid) {
        this.cusavid = cusavid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExtraAttribute() {
        return extraAttribute;
    }

    public void setExtraAttribute(String extraAttribute) {
        this.extraAttribute = extraAttribute;
    }

    @Override
    public String toString() {
        return value;
    }
}
