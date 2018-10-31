package com.xseec.eds.model.servlet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.R;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/10/29.
 */

public class Basic extends BaseModel implements Parcelable {

    private static final String SPIT=";";

    private String user;
    private String banner;
    private String pricipal;
    private String location;

    public Basic(){
        Context context=EDSApplication.getContext();
        this.id= WAServicer.getUser().getDeviceName();
        this.user=context.getString(R.string.app_name);
        this.banner="xs.jpg";
    }

    public Basic(String id) {
        this.id = id;
    }

    protected Basic(Parcel in) {
        id = in.readString();
        user = in.readString();
        banner = in.readString();
        pricipal = in.readString();
        location = in.readString();
        image = in.readString();
    }
    public static final Creator<Basic> CREATOR = new Creator<Basic>() {
        @Override
        public Basic createFromParcel(Parcel in) {
            return new Basic(in);
        }

        @Override
        public Basic[] newArray(int size) {
            return new Basic[size];
        }
    };

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBanner() {
        return banner;
    }

    public String getBannerUrl(){
        return WAServicer.getDownloadImageUrl()+banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getPricipal() {
        return pricipal;
    }

    public void setPricipal(String pricipal) {
        this.pricipal = pricipal;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toJson(){
        return EDSApplication.getContext().getString(R.string.svl_basic_request,id,user,banner,pricipal,location,image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(user);
        dest.writeString(banner);
        dest.writeString(pricipal);
        dest.writeString(location);
        dest.writeString(image);
    }
}
