package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 8/5/17.
 */

public class Notification {
    @SerializedName("uaid")
    @Expose
    private String uaid;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("mid")
    @Expose
    private String mid;
    @SerializedName("motaid")
    @Expose
    private String motaid;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("hex")
    @Expose
    private String fontbackgroundcolor;
    @SerializedName("icon_codes")
    @Expose
    private String fontawesomeicons;
    @SerializedName("comment")
    @Expose
    private List<Comment> comment = null;

    public List<Comment> getComment() {
        return comment;
    }

    public void setComment(List<Comment> comment) {
        this.comment = comment;
    }

    public String getFontbackgroundcolor() {
        return fontbackgroundcolor;
    }

    public void setFontbackgroundcolor(String fontbackgroundcolor) {
        this.fontbackgroundcolor = fontbackgroundcolor;
    }

    public String getFontawesomeicons() {
        return fontawesomeicons;
    }

    public void setFontawesomeicons(String fontawesomeicons) {
        this.fontawesomeicons = fontawesomeicons;
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

    public String getMid() {
        return mid;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMotaid() {
        return motaid;
    }

    public void setMotaid(String motaid) {
        this.motaid = motaid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

}
