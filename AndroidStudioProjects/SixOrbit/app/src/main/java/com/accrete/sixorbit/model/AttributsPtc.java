package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 15/12/17.
 */

public class AttributsPtc {
    @SerializedName("cusaid")
    @Expose
    private String cusaid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("required")
    @Expose
    private String required;
    @SerializedName("attribute_show_id")
    @Expose
    private String attributeShowId;

    public String getCusaid() {
        return cusaid;
    }

    public void setCusaid(String cusaid) {
        this.cusaid = cusaid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public String getAttributeShowId() {
        return attributeShowId;
    }

    public void setAttributeShowId(String attributeShowId) {
        this.attributeShowId = attributeShowId;
    }
}
