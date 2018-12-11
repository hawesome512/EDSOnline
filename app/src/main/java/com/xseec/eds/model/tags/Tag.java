package com.xseec.eds.model.tags;

import android.content.Context;
import android.databinding.BaseObservable;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;

/**
 * Created by Administrator on 2018/7/10.
 */

public class Tag extends BaseObservable implements Parcelable {
    public static final String SPIT_ZONE="_";
    public static final String SPIT_NAME=":";

    @SerializedName("Name")
    private String tagName;
    private String tagAlias;
    @SerializedName("Value")
    private String tagValue;


    public Tag(String tagName) {
        this.tagName = tagName;
    }

    public Tag(String tagName, String tagValue) {
        this.tagName = tagName;
        this.tagValue = tagValue;
    }

    protected Tag(Parcel in) {
        tagName = in.readString();
        tagValue = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };

    public String getTagName() {
        return tagName;
    }

    public String getTagValue() {
        return tagValue;
    }

    public String getTagAlias() {
        String alias= getTagShortName();
        String value= Generator.getResourceString("tag_"+alias);
        return value!=null?value:alias;
    }

    public String getTagShortName(){
        return tagName.split(":")[1];
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
        notifyChange();
    }

    @Override
    public String toString() {
        Context context= EDSApplication.getContext();
        return context.getString(R.string.was_readable_tag,tagName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tagName);
        dest.writeString(tagValue);
    }
}
