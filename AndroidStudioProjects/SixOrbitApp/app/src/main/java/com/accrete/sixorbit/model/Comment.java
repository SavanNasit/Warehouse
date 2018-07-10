package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by amp on 6/9/17.
 */

public class Comment {
    public int sync;
    @SerializedName("uacid")
    @Expose
    private String uacid;
    @SerializedName("uaid")
    @Expose
    private String uaid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("sid")
    @Expose
    private String sid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("sync_id")
    @Expose
    private String sync_id;
    private String post_uid;
    private String profile_name, designation, commentedtext;

    public Comment(String s, String s1) {
        this.profile_name = s;
        this.designation = s1;
    }
    public Comment(String profile_name, String designation, String commentedtext) {
        this.profile_name = profile_name;
        this.designation = designation;
        this.commentedtext = commentedtext;
    }

    public Comment() {

    }

    public String getPost_uid() {
        return post_uid;
    }

    public void setPost_uid(String post_uid) {
        this.post_uid = post_uid;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public String getSync_id() {
        return sync_id;
    }

    public void setSync_id(String sync_id) {
        this.sync_id = sync_id;
    }

    public String getUacid() {
        return uacid;
    }

    public void setUacid(String uacid) {
        this.uacid = uacid;
    }

    public String getUaid() {
        return uaid;
    }

    public void setUaid(String uaid) {
        this.uaid = uaid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getProfile_name() {
        return profile_name;
    }

    public void setProfile_name(String name) {
        this.profile_name = name;
    }

    public String getCommentedtext() {
        return commentedtext;
    }

    public void setCommentedtext(String commentedtext) {
        this.commentedtext = commentedtext;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
