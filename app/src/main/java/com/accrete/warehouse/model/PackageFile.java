package com.accrete.warehouse.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 7/9/18.
 */

public class PackageFile {

    @SerializedName("pacfid")
    @Expose
    private String pacfid;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("actual_name")
    @Expose
    private String actualName;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("file_url")
    @Expose
    private String fileUrl;

    public String getPacfid() {
        return pacfid;
    }

    public void setPacfid(String pacfid) {
        this.pacfid = pacfid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActualName() {
        return actualName;
    }

    public void setActualName(String actualName) {
        this.actualName = actualName;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

}