package com.accrete.sixorbit.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 13/6/17.
 */

public class ChatMessage {
    @SerializedName("type")
    @Expose
    private String msgType;
    @SerializedName("uid")
    @Expose
    private String uid;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("reciever_name")
    @Expose
    private String recieverName;
    @SerializedName("sender_name")
    @Expose
    private String senderName;
    @SerializedName("message_time")
    @Expose
    private String createdTs;
    @SerializedName("chat_id")
    @Expose
    private String chatId;
    private String syncId;
    private Bitmap bitmap;
    @SerializedName("file_name")
    @Expose
    private String fileName;
    private String filePath;
    @SerializedName("file_type")
    @Expose
    private String fileType;
    @SerializedName("url")
    @Expose
    private String fileUrl;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getSyncId() {
        return syncId;
    }

    public void setSyncId(String syncId) {
        this.syncId = syncId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecieverName() {
        return recieverName;
    }

    public void setRecieverName(String recieverName) {
        this.recieverName = recieverName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}