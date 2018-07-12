package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 6/7/18.
 */

public class Mode implements Parcelable {
    @SerializedName("pacdelgatpactid")
    @Expose
    private String pacdelgatpactid;
    @SerializedName("name")
    @Expose
    private String name;

    protected Mode(Parcel in) {
        pacdelgatpactid = in.readString();
        name = in.readString();
    }

    public static final Creator<Mode> CREATOR = new Creator<Mode>() {
        @Override
        public Mode createFromParcel(Parcel in) {
            return new Mode(in);
        }

        @Override
        public Mode[] newArray(int size) {
            return new Mode[size];
        }
    };

    public String getPacdelgatpactid() {
        return pacdelgatpactid;
    }

    public void setPacdelgatpactid(String pacdelgatpactid) {
        this.pacdelgatpactid = pacdelgatpactid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(pacdelgatpactid);
        dest.writeString(name);
    }
}