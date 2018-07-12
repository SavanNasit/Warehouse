package com.accrete.warehouse.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by poonam on 6/7/18.
 */


public class TransportMode implements Parcelable {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("modes")
    @Expose
    private List<Mode> modes = null;


    protected TransportMode(Parcel in) {
        type = in.readString();
        modes = in.createTypedArrayList(Mode.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeTypedList(modes);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransportMode> CREATOR = new Creator<TransportMode>() {
        @Override
        public TransportMode createFromParcel(Parcel in) {
            return new TransportMode(in);
        }

        @Override
        public TransportMode[] newArray(int size) {
            return new TransportMode[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Mode> getModes() {
        return modes;
    }

    public void setModes(List<Mode> modes) {
        this.modes = modes;
    }

}