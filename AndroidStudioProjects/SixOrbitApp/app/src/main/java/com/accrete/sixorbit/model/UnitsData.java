package com.accrete.sixorbit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by agt on 21/12/17.
 */

public class UnitsData implements Parcelable {
    public static final Creator<UnitsData> CREATOR = new Creator<UnitsData>() {
        @Override
        public UnitsData createFromParcel(Parcel in) {
            return new UnitsData(in);
        }

        @Override
        public UnitsData[] newArray(int size) {
            return new UnitsData[size];
        }
    };
    @SerializedName("meaid")
    @Expose
    private String meaid;
    @SerializedName("conversionrate")
    @Expose
    private String conversionrate;
    @SerializedName("name")
    @Expose
    private String name;

    public UnitsData() {
    }

    public UnitsData(Parcel in) {
        meaid = in.readString();
        conversionrate = in.readString();
        name = in.readString();
    }

    public String getMeaid() {
        return meaid;
    }

    public void setMeaid(String meaid) {
        this.meaid = meaid;
    }

    public String getConversionrate() {
        return conversionrate;
    }

    public void setConversionrate(String conversionrate) {
        this.conversionrate = conversionrate;
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
        dest.writeString(meaid);
        dest.writeString(conversionrate);
        dest.writeString(name);
    }
}
