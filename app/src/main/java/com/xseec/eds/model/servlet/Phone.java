package com.xseec.eds.model.servlet;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.User;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2019/1/21.
 */

public class Phone extends BaseModel implements Parcelable {
    private String code;
    private String time;
    private int level;
    private String account;

    public Phone(String id){
        this.id=id;
        //nj--暂时设置手机用户登录时，用户权限为普通访客
        this.level=1;
    }

    protected Phone(Parcel in) {
        id=in.readString();
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

    //nj--用户权限等级名称
    public String getLevelState(){
        switch (level) {
            case 0:
                return  EDSApplication.getContext().getResources().getString( R.string.user_admin ) ;
            case 1:
                return  EDSApplication.getContext().getResources().getString( R.string.user_normal  );
            default:
                return  EDSApplication.getContext().getResources().getString( R.string.user_operator ) ;
        }
    }

    //NJ--用户权限等级图标
    public int getLevelImgRes(){
        switch (level){
            case User.LEVEL_ADMIN:
                return R.drawable.ic_account_admin;
            case User.LEVEL_NORMAL:
                return R.drawable.ic_user_normal;
            default:
                return R.drawable.ic_user_operator;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( id );
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
