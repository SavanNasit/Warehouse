package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 25/9/17.
 */

public class ChatConversations {
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("created_ts")
    @Expose
    private String createdTs;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("chaliid")
    @Expose
    private String chatId;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
