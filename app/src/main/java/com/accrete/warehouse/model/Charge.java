package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by poonam on 6/8/18.
 */

public class Charge implements Parcelable {
    public static final Creator<Charge> CREATOR = new Creator<Charge>() {
        @Override
        public Charge createFromParcel(Parcel in) {
            return new Charge(in);
        }

        @Override
        public Charge[] newArray(int size) {
            return new Charge[size];
        }
    };
    @SerializedName("ecid")
    @Expose
    private String ecid;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("value")
    @Expose
    private String value;
    @SerializedName("chkoecid")
    @Expose
    private String chkoecid;

    protected Charge(Parcel in) {
        ecid = in.readString();
        title = in.readString();
        value = in.readString();
        chkoecid = in.readString();
    }

    public String getEcid() {
        return ecid;
    }

    public void setEcid(String ecid) {
        this.ecid = ecid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChkoecid() {
        return chkoecid;
    }

    public void setChkoecid(String chkoecid) {
        this.chkoecid = chkoecid;
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ecid);
        dest.writeString(title);
        dest.writeString(value);
        dest.writeString(chkoecid);
    }
}