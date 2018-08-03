package com.xseec.eds.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import com.xseec.eds.util.CodeHelper;

/**
 * Created by Administrator on 2018/7/9.
 */

public class User implements Parcelable{

    //[username:psw] base64 encode，eg.admin:xseec→YWRtaW46eHNlZWM=
    private String authority;
    //owned device，eg.1/ADAM3600:1→portNumber;ADAM3600→deviceName
    private String deviceName;

    public User(String authority, String deviceName) {
        this.authority = authority;
        this.deviceName = deviceName;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getUsername(){
        return CodeHelper.decode(authority).split(":")[0];
    }

    //Parcelable:pass user message in intent between activities.
    protected User(Parcel in) {
        authority = in.readString();
        deviceName = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authority);
        dest.writeString(deviceName);
    }

    @Override
    public String toString() {
        return "Authority:"+authority+";Device Name:"+deviceName;
    }
}
