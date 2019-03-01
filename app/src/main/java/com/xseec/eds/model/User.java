package com.xseec.eds.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Phone;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/7/9.
 */

public class User implements Parcelable{

    //nj--定义系统用户名
    private static final String SYSTEM_USER_NAME="admin";

    public static final int LEVEL_ADMIN=0;
    public static final int LEVEL_NORMAL=1;
    public static final int LEVEL_OPERATOR=2;

    //[username:psw] base64 encode，eg.admin:xseec→YWRtaW46eHNlZWM=
    private String authority;
    //owned device，eg.1/ADAM3600:1→portNumber;ADAM3600→deviceName
    private String deviceName;
    private String userName;
    //nj--暂定-->0:管理员 1：普通访客 2：运维人员
    private int level;

    //nj--账号登录
    public User(String authority, String deviceName) {
        this.authority = authority;
        this.deviceName = deviceName;
        this.userName=CodeHelper.decode( authority ).split( ":" )[0];
        this.level=LEVEL_ADMIN;
    }

    //nj--手机号登录
    public User(String authority,String deviceName,Phone phone){
        this.authority=authority;
        this.deviceName=deviceName;
        this.userName=phone.getId();
        this.level=LEVEL_NORMAL;
    }

    public String getAuthority() {
        return authority;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getGatewayName(){
        return deviceName.split("/")[1];
    }

    //1/ADAM3600→1_ADAM3600
    public String getStorableDirName(){
        return deviceName.replaceAll("\\W","_");
    }

    public String getUsername(){
        return userName;
    }

    public int getLevel(){
        return level;
    }

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

    public boolean isAdministrator(){
        return getUsername().equals( SYSTEM_USER_NAME )?true:false;
    }

    //Parcelable:pass user message in intent between activities.
    protected User(Parcel in) {
        authority = in.readString();
        deviceName = in.readString();
        userName=in.readString();
        level=in.readInt();
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
        dest.writeString( userName );
        dest.writeInt( level );
    }

    @Override
    public String toString() {
        return "Authority:"+authority+";Device Name:"+deviceName;
    }
}
