package com.xseec.eds.model.servlet;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2019/1/21.
 */

public class Phone extends BaseModel implements Parcelable {
    private String code;
    private String time;
    private int level;
    private String account;

    protected Phone(Parcel in) {
        code = in.readString();
        time = in.readString();
        level = in.readInt();
        account = in.readString();
    }

    public static final Creator<Phone> CREATOR = new Creator<Phone>() {
        @Override
        public Phone createFromParcel(Parcel in) {
            return new Phone(in);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(time);
        dest.writeInt(level);
        dest.writeString(account);
    }

    @Override
    public String toJson() {
        return EDSApplication.getContext().getString(R.string.svl_phone_request,id,code,time,level,account);
    }
}
