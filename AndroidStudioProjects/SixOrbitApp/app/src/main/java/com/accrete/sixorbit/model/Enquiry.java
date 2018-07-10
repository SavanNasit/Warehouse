package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 31/10/17.
 */
public class Enquiry {

    @SerializedName("enid")
    @Expose
    private String enid;
    @SerializedName("leaid")
    @Expose
    private String leaid;
    @SerializedName("taken_date")
    @Expose
    private String takenDate;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("said")
    @Expose
    private String said;
    @SerializedName("baid")
    @Expose
    private String baid;
    @SerializedName("ensid")
    @Expose
    private String ensid;
    @SerializedName("qoid")
    @Expose
    private String qoid;
    @SerializedName("special_instructions")
    @Expose
    private String specialInstructions;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("updated_uid")
    @Expose
    private String updatedUid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("enquiry_product")
    @Expose
    private List<EnquiryProduct> enquiryProduct = null;

    public String getEnid() {
        return enid;
    }

    public void setEnid(String enid) {
        this.enid = enid;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }

    public String getTakenDate() {
        return takenDate;
    }

    public void setTakenDate(String takenDate) {
        this.takenDate = takenDate;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getSaid() {
        return said;
    }

    public void setSaid(String said) {
        this.said = said;
    }

    public String getBaid() {
        return baid;
    }

    public void setBaid(String baid) {
        this.baid = baid;
    }

    public String getEnsid() {
        return ensid;
    }

    public void setEnsid(String ensid) {
        this.ensid = ensid;
    }

    public String getQoid() {
        return qoid;
    }

    public void setQoid(String qoid) {
        this.qoid = qoid;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getCreatedUid() {
        return createdUid;
    }

    public void setCreatedUid(String createdUid) {
        this.createdUid = createdUid;
    }

    public String getUpdatedUid() {
        return updatedUid;
    }

    public void setUpdatedUid(String updatedUid) {
        this.updatedUid = updatedUid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getUpdatedTs() {
        return updatedTs;
    }

    public void setUpdatedTs(String updatedTs) {
        this.updatedTs = updatedTs;
    }

    public List<EnquiryProduct> getEnquiryProduct() {
        return enquiryProduct;
    }

    public void setEnquiryProduct(List<EnquiryProduct> enquiryProduct) {
        this.enquiryProduct = enquiryProduct;
    }

}
