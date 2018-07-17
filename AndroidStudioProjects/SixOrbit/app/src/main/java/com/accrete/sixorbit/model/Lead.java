package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by poonam on 21/6/17.
 * <p>
 * Modified by poonam on 28/7/17.
 * <p>
 * Modified by poonam on 14/9/17.
 * <p>
 * Modified by poonam on 28/7/17.
 * <p>
 * Modified by poonam on 14/9/17.
 * <p>
 * Modified by poonam on 28/7/17.
 * <p>
 * Modified by poonam on 14/9/17.
 */

/**
 * Modified by poonam on 28/7/17.
 */

/**
 * Modified by poonam on 14/9/17.
 */

/**
 * Modified by poonam on 19/9/17.
 */


public class Lead {
    private int ID = 0;
    private String leadSync;

    @SerializedName("lead_number")
    @Expose
    private String leadNumber;

    @SerializedName("leaid")
    @Expose
    private String leaid;
    @SerializedName("leadId")
    @Expose
    private String leadId;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("leatid")
    @Expose
    private String leatid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("lead_person_type")
    @Expose
    private String leadPersonType;
    @SerializedName("off_addr")
    @Expose
    private String offAddr;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("sync_id")
    @Expose
    private String syncId;
    @SerializedName("genderid")
    @Expose
    private String genderid;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("assigned_uid")
    @Expose
    private String assignedUid;
    @SerializedName("leasid")
    @Expose
    private String leasid;
    @SerializedName("enid")
    @Expose
    private String enid;
    @SerializedName("qoid")
    @Expose
    private String qoid;
    @SerializedName("cancel_reason")
    @Expose
    private String cancelReason;
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
    @SerializedName("contacts")
    @Expose
    private List<Contacts> contacts = null;
    @SerializedName("shipping_address")
    @Expose
    private List<LeadShippingAddress> shippingAddress = null;
    @SerializedName("followups")
    @Expose
    private List<FollowUp> followups = null;
    private ArrayList<String> codeIds;

    public Lead(int id, String leaid, String ownerId, String leatid, String name,
                String leadPersonType, String website, String genderid, String mobile,
                String email, String assignedUid, String leasid, String enid, String qoid,
                String cancelReason, String specialInstructions, String createdUid, String updatedUid,
                String createdTs, String updatedTs, String sync, String syncId) {

        this.ID = id;
        this.leaid = leaid;
        this.ownerId = ownerId;
        this.leatid = leatid;
        this.name = name;
        this.leadPersonType = leadPersonType;
        this.website = website;
        this.genderid = genderid;
        this.mobile = mobile;
        this.email = email;
        this.assignedUid = assignedUid;
        this.leasid = leasid;
        this.leatid = leatid;
        this.enid = enid;
        this.qoid = qoid;
        this.cancelReason = cancelReason;
        this.specialInstructions = specialInstructions;
        this.createdUid = createdUid;
        this.updatedUid = updatedUid;
        this.createdTs = createdTs;
        this.updatedTs = updatedTs;
        this.leadSync = sync;
        this.syncId = syncId;

    }

    public Lead() {

    }

    public String getLeadId() {
        return leadId;
    }

    public void setLeadId(String leadId) {
        this.leadId = leadId;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getLeatid() {
        return leatid;
    }

    public void setLeatid(String leatid) {
        this.leatid = leatid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLeadPersonType() {
        return leadPersonType;
    }

    public void setLeadPersonType(String leadPersonType) {
        this.leadPersonType = leadPersonType;
    }

    public String getOffAddr() {
        return offAddr;
    }

    public void setOffAddr(String offAddr) {
        this.offAddr = offAddr;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public String getGenderid() {
        return genderid;
    }

    public void setGenderid(String genderid) {
        this.genderid = genderid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssignedUid() {
        return assignedUid;
    }

    public void setAssignedUid(String assignedUid) {
        this.assignedUid = assignedUid;
    }

    public String getLeasid() {
        return leasid;
    }

    public void setLeasid(String leasid) {
        this.leasid = leasid;
    }

    public String getEnid() {
        return enid;
    }

    public void setEnid(String enid) {
        this.enid = enid;
    }

    public String getQoid() {
        return qoid;
    }

    public void setQoid(String qoid) {
        this.qoid = qoid;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
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

    public List<Contacts> getContacts() {
        return contacts;
    }

    public void setContacts(List<Contacts> contacts) {
        this.contacts = contacts;
    }

    public List<LeadShippingAddress> getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(List<LeadShippingAddress> shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public List<FollowUp> getFollowups() {
        return followups;
    }

    public void setFollowups(List<FollowUp> followups) {
        this.followups = followups;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getLeadSync() {
        return leadSync;
    }

    public void setLeadSync(String leadSync) {
        this.leadSync = leadSync;
    }

    public ArrayList<String> getCodeIds() {
        return codeIds;
    }

    public void setCodeIds(ArrayList<String> codeIds) {
        this.codeIds = codeIds;
    }


    public String getLeadNumber() {
        return leadNumber;
    }

    public void setLeadNumber(String leadNumber) {
        this.leadNumber = leadNumber;
    }
}