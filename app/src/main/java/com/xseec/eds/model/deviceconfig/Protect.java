package com.xseec.eds.model.deviceconfig;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 */

public class Protect implements Parcelable {

    @SerializedName("Name")
    private String name;
    @SerializedName("Items")
    private List<String> items;

    protected Protect(Parcel in) {
        name = in.readString();
        items = in.createStringArrayList();
    }

    public static final Creator<Protect> CREATOR = new Creator<Protect>() {
        @Override
        public Protect createFromParcel(Parcel in) {
            return new Protect(in);
        }

        @Override
        public Protect[] newArray(int size) {
            return new Protect[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(items);
    }
}