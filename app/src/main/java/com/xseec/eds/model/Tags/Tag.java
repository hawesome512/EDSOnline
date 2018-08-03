package com.xseec.eds.model.Tags;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/10.
 */

public class Tag implements Parcelable {

    @SerializedName("Name")
    private String tagName;
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

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
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
