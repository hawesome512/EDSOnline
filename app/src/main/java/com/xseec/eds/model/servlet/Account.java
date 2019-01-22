package com.xseec.eds.model.servlet;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2019/1/21.
 */

public class Account extends BaseModel implements Parcelable {

    private String authority;
    private int number;
    private String phone;

    protected Account(Parcel in) {
        authority = in.readString();
        number = in.readInt();
        phone = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel in) {
            return new Account(in);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String key) {
        this.authority = key;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toJson() {
        return EDSApplication.getContext().getString(R.string.svl_account_request,id,authority,number,phone);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(authority);
        dest.writeInt(number);
        dest.writeString(phone);
    }
}
