package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/7/17.
 */


/**
 * Modified by poonam on 28/7/17.
 */

public class Contacts {
    @SerializedName("is_owner")
    @Expose
    public String isOwner;
    @SerializedName("codeid")
    @Expose
    private String codeid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone_no")
    @Expose
    private String phoneNo;
    private int id;
    private String leaid;
    private String syncID;
    @SerializedName("type")
    @Expose
    private String type;
    private String owner;
    private String cuId;
    private String contactTypeId;
    private String contactTypeValue;
    private String extraAttribute;
    private String specifyText;
    private boolean checkOwner;

    public String getIsOwner() {
        return isOwner;
    }

    public void setIsOwner(String isOwner) {
        this.isOwner = isOwner;
    }

    public boolean isCheckOwner() {
        return checkOwner;
    }

    public void setCheckOwner(boolean checkOwner) {
        this.checkOwner = checkOwner;
    }

    public String getSpecifyText() {
        return specifyText;
    }

    public void setSpecifyText(String specifyText) {
        this.specifyText = specifyText;
    }

    public String getExtraAttribute() {
        return extraAttribute;
    }

    public void setExtraAttribute(String extraAttribute) {
        this.extraAttribute = extraAttribute;
    }

    public String getContactTypeId() {
        return contactTypeId;
    }

    public void setContactTypeId(String contactTypeId) {
        this.contactTypeId = contactTypeId;
    }

    public String getContactTypeValue() {
        return contactTypeValue;
    }

    public void setContactTypeValue(String contactTypeValue) {
        this.contactTypeValue = contactTypeValue;
    }

    public String getCuId() {
        return cuId;
    }

    public void setCuId(String cuId) {
        this.cuId = cuId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    // getting ID
    public int getID() {
        return this.id;
    }

    // setting id
    public void setID(int id) {
        this.id = id;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }

    public String getSyncID() {
        return syncID;
    }

    public void setSyncID(String syncID) {
        this.syncID = syncID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}