package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 12/4/17.
 */
public class FollowUp {

    private int id;
    private int color = 0;
    private String type;
    private String contactedPerson;
    private String syncStatus;
    private String followupType;
    private String leadLocalId;
    private String contactsId;

    @SerializedName("followup_number")
    @Expose
    private String followupNumber;
    @SerializedName("foid")
    @Expose
    private String foid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("person_type")
    @Expose
    private String personType;
    @SerializedName("customer_type")
    @Expose
    private String customerType;
    @SerializedName("cuid")
    @Expose
    private String cuid;
    @SerializedName("enid")
    @Expose
    private String enid;
    @SerializedName("qoid")
    @Expose
    private String qoid;
    @SerializedName("chkoid")
    @Expose
    private String chkoid;
    @SerializedName("purorid")
    @Expose
    private String purorid;
    @SerializedName("venid")
    @Expose
    private String venid;
    @SerializedName("codeid")
    @Expose
    private String codeid;
    @SerializedName("contact_person")
    @Expose
    private String contactPerson;
    @SerializedName("contact_person_mobile")
    @Expose
    private String contactPersonMobile;
    @SerializedName("contact_person_email")
    @Expose
    private String contactPersonEmail;
    @SerializedName("scheduled_date")
    @Expose
    private String scheduledDate;
    @SerializedName("alert_on")
    @Expose
    private String alertOn;
    @SerializedName("followup_type_name")
    @Expose
    private String followupTypeName;
    @SerializedName("followup_type_status")
    @Expose
    private String followupTypeStatus;
    @SerializedName("followup_type_status_id")
    @Expose
    private String followupTypeStatusId;
    @SerializedName("leaid")
    @Expose
    private String leaid;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("sync_id")
    @Expose
    private String syncId;
    @SerializedName("taken_on")
    @Expose
    private String takenOn;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("reason")
    @Expose
    private String reason;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("followup_outcome")
    @Expose
    private String followupOutcome;
    @SerializedName("followup_communication_mode")
    @Expose
    private String followupCommunicationMode;
    @SerializedName("alert_mode")
    @Expose
    private String alertMode;
    @SerializedName("assigned_user")
    @Expose
    private String assignedUser;
    @SerializedName("created_user")
    @Expose
    private String createdUser;
    @SerializedName("updated_user")
    @Expose
    private String updatedUser;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("updated_ts")
    @Expose
    private String updatedTs;
    @SerializedName("customer")
    @Expose
    private List<Customer> customer = null;
    @SerializedName("vendor")
    @Expose
    private List<Vendor> vendor = null;
    @SerializedName("commid")
    @Expose
    private String commid;
    @SerializedName("assigned_uid")
    @Expose
    private String assignedUid;
    @SerializedName("followupId")
    @Expose
    private String followupId;
    @SerializedName("enquiryNumber")
    @Expose
    private String enquiryNumber;
    @SerializedName("enquiryId")
    @Expose
    private String enquiryId;
    @SerializedName("quotationNumber")
    @Expose
    private String quotationNumber;
    @SerializedName("quotationId")
    @Expose
    private String quotationId;
    @SerializedName("order_sequence_number")
    @Expose
    private String orderSequenceNumber;
    @SerializedName("orderId")
    @Expose
    private String orderId;
    @SerializedName("purchaseOrderNumber")
    @Expose
    private String purchaseOrderNumber;
    @SerializedName("purchaseOrderId")
    @Expose
    private String purchaseOrderId;
    @SerializedName("leadNumber")
    @Expose
    private String leadNumber;
    @SerializedName("leadId")
    @Expose
    private String leadId;
    //New Variables added on 28th March for follow ups of quotation
    @SerializedName("fooutid")
    @Expose
    private String fooutid;
    @SerializedName("fosid")
    @Expose
    private String fosid;
    @SerializedName("fotid")
    @Expose
    private String fotid;
    @SerializedName("created_uid")
    @Expose
    private String createdUid;
    @SerializedName("updated_uid")
    @Expose
    private String updatedUid;
    @SerializedName("alert_sent")
    @Expose
    private String alertSent;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("qosid")
    @Expose
    private String qosid;
    //TODO - Added on 19th April
    @SerializedName("ensid")
    @Expose
    private String ensid;
    @SerializedName("chkosid")
    @Expose
    private String chkosid;
    @SerializedName("purorsid")
    @Expose
    private String purorsid;
    @SerializedName("leasid")
    @Expose
    private String leasid;
    @SerializedName("parent_followup_id")
    @Expose
    private String parentFollowupId;
    public FollowUp(String foid, String alertOn, int color, String customerName, String contactPerson, String fosid, String scheduledDate, String type) {
        this.foid = foid;
        this.alertOn = alertOn;
        this.color = color;
        this.name = customerName;
        this.contactPerson = contactPerson;
        this.fosid = fosid;
        this.scheduledDate = scheduledDate;
        this.type = type;

    }
    public FollowUp(int id, String alertOn, int color, String customerName, String contactPerson, String fosid, String foid, String scheduledDate, String type, String leadId) {
        this.id = id;
        this.foid = foid;
        this.alertOn = alertOn;
        this.color = color;
        this.name = customerName;
        this.contactPerson = contactPerson;
        this.fosid = fosid;
        this.scheduledDate = scheduledDate;
        this.type = type;
        this.leaid = leadId;
    }

    public FollowUp() {

    }

    public String getParentFollowupId() {
        return parentFollowupId;
    }

    public void setParentFollowupId(String parentFollowupId) {
        this.parentFollowupId = parentFollowupId;
    }

    public String getEnsid() {
        return ensid;
    }

    public void setEnsid(String ensid) {
        this.ensid = ensid;
    }

    public String getChkosid() {
        return chkosid;
    }

    public void setChkosid(String chkosid) {
        this.chkosid = chkosid;
    }

    public String getPurorsid() {
        return purorsid;
    }

    public void setPurorsid(String purorsid) {
        this.purorsid = purorsid;
    }

    public String getLeasid() {
        return leasid;
    }

    public void setLeasid(String leasid) {
        this.leasid = leasid;
    }

    public String getQosid() {
        return qosid;
    }

    public void setQosid(String qosid) {
        this.qosid = qosid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPersonType() {
        return personType;
    }

    public void setPersonType(String personType) {
        this.personType = personType;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getLeaid() {
        return leaid;
    }

    public void setLeaid(String leaid) {
        this.leaid = leaid;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public List<Customer> getCustomer() {
        return customer;
    }

    public void setCustomer(List<Customer> customer) {
        this.customer = customer;
    }

    public List<Vendor> getVendor() {
        return vendor;
    }

    public void setVendor(List<Vendor> vendor) {
        this.vendor = vendor;
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

    public String getChkoid() {
        return chkoid;
    }

    public void setChkoid(String chkoid) {
        this.chkoid = chkoid;
    }

    public String getPurorid() {
        return purorid;
    }

    public void setPurorid(String purorid) {
        this.purorid = purorid;
    }

    public String getVenid() {
        return venid;
    }

    public void setVenid(String venid) {
        this.venid = venid;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getCodeid() {
        return codeid;
    }

    public void setCodeid(String codeid) {
        this.codeid = codeid;
    }

    public String getCommid() {
        return commid;
    }

    public void setCommid(String commid) {
        this.commid = commid;
    }

    public String getFotid() {
        return fotid;
    }

    public void setFotid(String fotid) {
        this.fotid = fotid;
    }

    public String getAssignedUid() {
        return assignedUid;
    }

    public void setAssignedUid(String assignedUid) {
        this.assignedUid = assignedUid;
    }

    public String getContactPersonMobile() {
        return contactPersonMobile;
    }

    public void setContactPersonMobile(String contactPersonMobile) {
        this.contactPersonMobile = contactPersonMobile;
    }

    public String getContactPersonEmail() {
        return contactPersonEmail;
    }

    public void setContactPersonEmail(String contactPersonEmail) {
        this.contactPersonEmail = contactPersonEmail;
    }

    public String getSyncStatus() {
        return syncStatus;
    }

    public void setSyncStatus(String syncStatus) {
        this.syncStatus = syncStatus;
    }

    public String getPerson_type() {
        return personType;
    }

    public void setPerson_type(String person_type) {
        this.personType = person_type;
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

    public String getFollowupType() {
        return followupType;
    }

    public void setFollowupType(String followupType) {
        this.followupType = followupType;
    }

    public String getFollowupTypeName() {
        return followupTypeName;
    }

    public void setFollowupTypeName(String followupTypeName) {
        this.followupTypeName = followupTypeName;
    }

    public String getFollowupTypeStatus() {
        return followupTypeStatus;
    }

    public void setFollowupTypeStatus(String followupTypeStatus) {
        this.followupTypeStatus = followupTypeStatus;
    }

    public String getFollowupTypeStatusId() {
        return followupTypeStatusId;
    }

    public void setFollowupTypeStatusId(String followupTypeStatusId) {
        this.followupTypeStatusId = followupTypeStatusId;
    }

    public String getLeadId() {
        return leaid;
    }

    public void setLeadId(String leadId) {
        this.leaid = leadId;
    }

    public String getTakenOn() {
        return takenOn;
    }

    public void setTakenOn(String takenOn) {
        this.takenOn = takenOn;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFollowupCommunicationMode() {
        return followupCommunicationMode;
    }

    public void setFollowupCommunicationMode(String followupCommunicationMode) {
        this.followupCommunicationMode = followupCommunicationMode;
    }

    public String getAlertMode() {
        return alertMode;
    }

    public void setAlertMode(String alertMode) {
        this.alertMode = alertMode;
    }

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String assignedUser) {
        this.assignedUser = assignedUser;
    }

    public void setAssigned_user(String assigned_user) {
        this.assignedUser = assigned_user;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getFollowupOutcome() {
        return followupOutcome;
    }

    public void setFollowupOutcome(String followupOutcome) {
        this.followupOutcome = followupOutcome;
    }

    public String getUpdatedUser() {
        return updatedUser;
    }

    public void setUpdatedUser(String updatedUser) {
        this.updatedUser = updatedUser;
    }

    public String getFoid() {
        return foid;
    }

    public void setFoid(String foid) {
        this.foid = foid;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(String scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getAlertOn() {
        return alertOn;
    }

    public void setAlertOn(String alertOn) {
        this.alertOn = alertOn;
    }

    public String getFosid() {
        return fosid;
    }

    public void setFosid(String fosid) {
        this.fosid = fosid;
    }

    public String getFooutid() {
        return fooutid;
    }

    public void setFooutid(String fooutid) {
        this.fooutid = fooutid;
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

    public String getAlertSent() {
        return alertSent;
    }

    public void setAlertSent(String alertSent) {
        this.alertSent = alertSent;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContactedPerson() {
        return contactedPerson;
    }

    public void setContactedPerson(String contactedPerson) {
        this.contactedPerson = contactedPerson;
    }

    public String getLeadLocalId() {
        return leadLocalId;
    }

    public void setLeadLocalId(String leadLocalId) {
        this.leadLocalId = leadLocalId;
    }

    public String getContactsId() {
        return contactsId;
    }

    public void setContactsId(String contactsId) {
        this.contactsId = contactsId;
    }

    public String getFollowupNumber() {
        return followupNumber;
    }

    public void setFollowupNumber(String followupNumber) {
        this.followupNumber = followupNumber;
    }

    public String getFollowupId() {
        return followupId;
    }

    public void setFollowupId(String followupId) {
        this.followupId = followupId;
    }

    public String getEnquiryNumber() {
        return enquiryNumber;
    }

    public void setEnquiryNumber(String enquiryNumber) {
        this.enquiryNumber = enquiryNumber;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }

    public String getQuotationNumber() {
        return quotationNumber;
    }

    public void setQuotationNumber(String quotationNumber) {
        this.quotationNumber = quotationNumber;
    }

    public String getQuotationId() {
        return quotationId;
    }

    public void setQuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    public String getOrderSequenceNumber() {
        return orderSequenceNumber;
    }

    public void setOrderSequenceNumber(String orderSequenceNumber) {
        this.orderSequenceNumber = orderSequenceNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPurchaseOrderNumber() {
        return purchaseOrderNumber;
    }

    public void setPurchaseOrderNumber(String purchaseOrderNumber) {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    public String getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(String purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getLeadNumber() {
        return leadNumber;
    }

    public void setLeadNumber(String leadNumber) {
        this.leadNumber = leadNumber;
    }

    public String getLeadIdR() {
        return leadId;
    }

    public void setLeadIdR(String leadId) {
        this.leadId = leadId;
    }

}