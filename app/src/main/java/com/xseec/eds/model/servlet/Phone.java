package com.xseec.eds.model.servlet;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.User;
import com.xseec.eds.model.UserType;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2019/1/21.
 */

public class Phone extends BaseModel implements Parcelable {
    public static final int NUMBER_LENGTH=11;

    private String code;
    private String time;
    private int level;
    private String account;
    private String name;

    public Phone() {
    }

    protected Phone(Parcel in) {
        id = in.readString();
        code = in.readString();
        time = in.readString();
        level = in.readInt();
        account = in.readString();
        name = in.readString();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UserType getUserType() {
        return UserType.initWithIndex(level);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static Phone initWithInfo(String info){
        Phone phone=new Phone();
        Pattern pattern=Pattern.compile("(\\d{11})\\/(\\d)\\/(\\S+)");
        Matcher matcher=pattern.matcher(info);
        if(matcher.find()){
            phone.setId(matcher.group(1));
            phone.setLevel(Integer.valueOf(matcher.group(2)));
            phone.setName(matcher.group(3));
        }
        return phone;
    }

    public String getPhoneInfo(){
        return String.format("%1$s/%2$d/%3$s",id,level,name);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(time);
        dest.writeInt(level);
        dest.writeString(account);
        dest.writeString(name);
    }

    @Override
    public String toJson() {
        return EDSApplication.getContext().getString(R.string.svl_phone_request, id, code, time,
                level, account, name);
    }
}
