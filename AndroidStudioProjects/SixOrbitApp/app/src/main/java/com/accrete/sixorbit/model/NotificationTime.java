package com.accrete.sixorbit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 8/5/17.
 */

public class NotificationTime {

    @SerializedName("created_ts")
    @Expose
    private String createdTs;

    public String getCreatedTs() {
        return createdTs;
    }

    public void setCreatedTs(String createdTs) {
        this.createdTs = createdTs;
    }
}
