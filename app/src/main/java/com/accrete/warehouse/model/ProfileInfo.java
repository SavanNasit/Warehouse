package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 1/22/18.
 */

public class ProfileInfo {

    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("ustatusid")
    @Expose
    private String ustatusid;
    @SerializedName("is_admin")
    @Expose
    private String isAdmin;
    @SerializedName("is_super")
    @Expose
    private String isSuper;
    @SerializedName("empid")
    @Expose
    private String empid;
    @SerializedName("report_to")
    @Expose
    private String reportTo;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("address_line_1")
    @Expose
    private String addressLine1;
    @SerializedName("address_line_2")
    @Expose
    private String addressLine2;
    @SerializedName("coverid")
    @Expose
    private String coverid;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("coverlid")
    @Expose
    private String coverlid;
    @SerializedName("locality_name")
    @Expose
    private String localityName;
    @SerializedName("genderid")
    @Expose
    private String genderid;
    @SerializedName("stid")
    @Expose
    private String stid;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("ctid")
    @Expose
    private String ctid;
    @SerializedName("country")
    @Expose
    private String country;
    @SerializedName("iso_code")
    @Expose
    private String isoCode;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("photo")
    @Expose
    private String photo;
    @SerializedName("date_of_birth")
    @Expose
    private String dateOfBirth;
    @SerializedName("date_of_joining")
    @Expose
    private String dateOfJoining;
    @SerializedName("date_of_leaving")
    @Expose
    private String dateOfLeaving;
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
    @SerializedName("last_seen")
    @Expose
    private String lastSeen;
    @SerializedName("roles")
    @Expose
    private List<Role> roles = null;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUstatusid() {
        return ustatusid;
    }

    public void setUstatusid(String ustatusid) {
        this.ustatusid = ustatusid;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getIsSuper() {
        return isSuper;
    }

    public void setIsSuper(String isSuper) {
        this.isSuper = isSuper;
    }

    public String getEmpid() {
        return empid;
    }

    public void setEmpid(String empid) {
        this.empid = empid;
    }

    public String getReportTo() {
        return reportTo;
    }

    public void setReportTo(String reportTo) {
        this.reportTo = reportTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCoverid() {
        return coverid;
    }

    public void setCoverid(String coverid) {
        this.coverid = coverid;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCoverlid() {
        return coverlid;
    }

    public void setCoverlid(String coverlid) {
        this.coverlid = coverlid;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public String getGenderid() {
        return genderid;
    }

    public void setGenderid(String genderid) {
        this.genderid = genderid;
    }

    public String getStid() {
        return stid;
    }

    public void setStid(String stid) {
        this.stid = stid;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCtid() {
        return ctid;
    }

    public void setCtid(String ctid) {
        this.ctid = ctid;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfJoining() {
        return dateOfJoining;
    }

    public void setDateOfJoining(String dateOfJoining) {
        this.dateOfJoining = dateOfJoining;
    }

    public String getDateOfLeaving() {
        return dateOfLeaving;
    }

    public void setDateOfLeaving(String dateOfLeaving) {
        this.dateOfLeaving = dateOfLeaving;
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

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

}
