package com.xseec.eds.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.CodeHelper;

/**
 * Created by Administrator on 2018/7/9.
 */

public class User implements Parcelable {

    //nj--定义系统用户名
    private static final String SYSTEM_USER_NAME = "admin";

    //[username:psw] base64 encode，eg.admin:xseec→YWRtaW46eHNlZWM=
    private String authority;
    //owned device，eg.1/ADAM3600:1→portNumber;ADAM3600→deviceName
    private String deviceName;
    private String userName;
    private UserType userType;

    //nj--账号登录
    public User(String authority, String deviceName) {
        this.authority = authority;
        this.deviceName = deviceName;
        this.userName = CodeHelper.decode(authority).split(":")[0];
        this.userType = isSuperAdmin() ? UserType.SUPER_ADMIN : UserType.USER_ADMIN;
    }

    //nj--手机号登录
    public User(String authority, String deviceName, Phone phone) {
        this.authority = authority;
        this.deviceName = deviceName;
        this.userName = phone.getName();
        this.userType = UserType.values()[phone.getLevel()];
    }

    public String getAuthority() {
        return authority;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getGatewayName() {
        return deviceName.split("/")[1];
    }

    //1/ADAM3600→1_ADAM3600
    public String getStorableDirName() {
        return deviceName.replaceAll("\\W", "_");
    }

    public UserType getUserType() {
        return userType;
    }

    public String getUsername() {
        return userName;
    }

    public boolean isSuperAdmin() {
        return getUsername().equals(SYSTEM_USER_NAME);
    }

    //Parcelable:pass user message in intent between activities.
    protected User(Parcel in) {
        authority = in.readString();
        deviceName = in.readString();
        userName = in.readString();
        userType=UserType.values()[in.readInt()];
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
        dest.writeString(userName);
        dest.writeInt(userType.ordinal());
    }

    @Override
    public String toString() {
        return "Authority:" + authority + ";Device Name:" + deviceName;
    }
}
