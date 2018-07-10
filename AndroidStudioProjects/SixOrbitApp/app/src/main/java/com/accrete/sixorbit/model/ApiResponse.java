package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by poonam on 13/4/17.
 */

public class ApiResponse implements Serializable {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("result_code")
    @Expose
    private String successCode;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getSuccessCode() {
        return successCode;
    }

    public void setSuccessCode(String successCode) {
        this.successCode = successCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
