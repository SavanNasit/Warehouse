package com.accrete.sixorbit.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by poonam on 2/21/18.
 */

public class CallLogDetails implements Parcelable {

    String phNumber ;

    String callType ;
    long callDate ;
    String callDayTime ;
    String callDuration;
    String dir = null;

    public CallLogDetails(){

    }

    public CallLogDetails(Parcel in) {
        phNumber = in.readString();
        callType = in.readString();
        callDate = in.readLong();
        callDuration = in.readString();
        dir = in.readString();
        callTypeCode = in.readInt();
    }

    public static final Creator<CallLogDetails> CREATOR = new Creator<CallLogDetails>() {
        @Override
        public CallLogDetails createFromParcel(Parcel in) {
            return new CallLogDetails(in);
        }

        @Override
        public CallLogDetails[] newArray(int size) {
            return new CallLogDetails[size];
        }
    };

    public int getCallTypeCode() {
        return callTypeCode;
    }

    public void setCallTypeCode(int callTypeCode) {
        this.callTypeCode = callTypeCode;
    }

    int callTypeCode;

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public long getCallDate() {
        return callDate;
    }

    public void setCallDate(long callDate) {
        this.callDate = callDate;
    }

    public String getCallDayTime() {
        return callDayTime;
    }

    public void setCallDayTime(String callDayTime) {
        this.callDayTime = callDayTime;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(phNumber);
        dest.writeString(callType);
        dest.writeLong(callDate);
        dest.writeString(callDuration);
        dest.writeString(dir);
        dest.writeInt(callTypeCode);
    }
}
