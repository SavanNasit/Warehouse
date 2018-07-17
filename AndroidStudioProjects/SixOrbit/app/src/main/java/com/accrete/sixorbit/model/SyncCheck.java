package com.accrete.sixorbit.model;

/**
 * Created by Ediga on 2/8/17.
 */

public class SyncCheck {
    //private variables
    int id;
    String service;
    String callTime;

    // Empty constructor
    public SyncCheck() {

    }

    // constructor
    public SyncCheck(int id, String service, String callTime) {
        this.id = id;
        this.service = service;
        this.callTime = callTime;
    }

    // constructor
    public SyncCheck(String service, String call_time) {
        this.service = service;
        this.callTime = call_time;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

}