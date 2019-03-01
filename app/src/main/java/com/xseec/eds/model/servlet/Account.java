package com.xseec.eds.model.servlet;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/1/21.
 */

public class Account extends BaseModel implements Parcelable {
    private static final String SPILT_PHONE=";";
    private String authority;
    private int number;
    private String phone;

    protected Account(Parcel in) {
        id=in.readString();
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

    public void addPhone(String newPhone){
        phone=phone+SPILT_PHONE+newPhone;
    }

    public void updatePhone(String oldPhone,String newPhone){
        phone=phone.replace( oldPhone,newPhone );
    }

    public void deletePhone(String oldPhone){
        String regex=oldPhone+SPILT_PHONE;
        Pattern pattern=Pattern.compile( regex );
        Matcher matcher=pattern.matcher( phone );
        if (matcher.find()){
            phone=phone.replace( oldPhone+SPILT_PHONE,"" );
        }else {
            phone=phone.replace( SPILT_PHONE+oldPhone,"" );
        }
    }

    public boolean isCreatedForPhone(String newPhone){
        String regex=newPhone;
        Pattern pattern=Pattern.compile( regex );
        Matcher matcher=pattern.matcher( phone );
        return matcher.find();
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
        dest.writeString( id );
        dest.writeString(authority);
        dest.writeInt(number);
        dest.writeString(phone);
    }
}
