package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 12/29/17.
 */

public class EmailTemplateArr {

    @SerializedName("etid")
    @Expose
    private String etid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("body")
    @Expose
    private String body;
    @SerializedName("selected")
    @Expose
    private Boolean selected;

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getEtid() {
        return etid;
    }

    public void setEtid(String etid) {
        this.etid = etid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return title;
    }


}