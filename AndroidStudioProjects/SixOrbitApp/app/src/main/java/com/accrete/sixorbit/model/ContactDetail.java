package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 13/12/17.
 */

public class ContactDetail {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contact_name")
    @Expose
    private String contactName;
    @SerializedName("contact_designation")
    @Expose
    private String contactDesignation;
    @SerializedName("contact_email")
    @Expose
    private String contactEmail;
    @SerializedName("contact_phoneNo")
    @Expose
    private String contactPhoneNo;
    @SerializedName("contact_typeId")
    @Expose
    private String contactTypeId;
    @SerializedName("contact_typeValue")
    @Expose
    private String contactTypeValue;
    @SerializedName("contact_attributeId")
    @Expose
    private String contactAttributeId;
    @SerializedName("extra_attributeId")
    @Expose
    private String extraAttributeId;
    @SerializedName("extra_attributeValue")
    @Expose
    private String extraAttributeValue;
    private boolean selected;
    @SerializedName("is_owner")
    @Expose
    private Boolean isOwner;

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public String getExtraAttributeId() {
        return extraAttributeId;
    }

    public void setExtraAttributeId(String extraAttributeId) {
        this.extraAttributeId = extraAttributeId;
    }

    public String getExtraAttributeValue() {
        return extraAttributeValue;
    }

    public void setExtraAttributeValue(String extraAttributeValue) {
        this.extraAttributeValue = extraAttributeValue;
    }

    public boolean isSelected() {
        return selected;
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getContactAttributeId() {
        return contactAttributeId;
    }

    public void setContactAttributeId(String contactAttributeId) {
        this.contactAttributeId = contactAttributeId;
    }

    public String getContactTypeValue() {
        return contactTypeValue;
    }

    public void setContactTypeValue(String contactTypeValue) {
        this.contactTypeValue = contactTypeValue;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactDesignation() {
        return contactDesignation;
    }

    public void setContactDesignation(String contactDesignation) {
        this.contactDesignation = contactDesignation;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhoneNo() {
        return contactPhoneNo;
    }

    public void setContactPhoneNo(String contactPhoneNo) {
        this.contactPhoneNo = contactPhoneNo;
    }

    public String getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(String contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
