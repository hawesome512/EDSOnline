package com.xseec.eds.model.tags;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2018/7/25.
 */

public class OverviewTag implements Parcelable {

    private int tagResId;
    private String tagName;
    private String tagValue;
    private String tagUnit;
    private String aliasTagName;

    public OverviewTag(@NonNull int tagResId,@NonNull String tagName, String tagValue, String mapTagName, String tagUnit) {
        this.tagResId = tagResId;
        this.tagName = tagName;
        this.tagValue = tagValue;
        this.tagUnit = tagUnit;
        this.aliasTagName = mapTagName;
    }

    protected OverviewTag(Parcel in) {
        tagResId = in.readInt();
        tagName = in.readString();
        tagValue = in.readString();
        tagUnit = in.readString();
        aliasTagName = in.readString();
    }

    public static final Creator<OverviewTag> CREATOR = new Creator<OverviewTag>() {
        @Override
        public OverviewTag createFromParcel(Parcel in) {
            return new OverviewTag(in);
        }

        @Override
        public OverviewTag[] newArray(int size) {
            return new OverviewTag[size];
        }
    };

    public int getTagResId() {
        return tagResId;
    }

    public void setTagResId(int tagResId) {
        this.tagResId = tagResId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagUnit() {
        return tagUnit;
    }

    public void setTagUnit(String tagUnit) {
        this.tagUnit = tagUnit;
    }

    public String getAliasTagName() {
        return aliasTagName;
    }

    public void setAliasTagName(String aliasTagName) {
        this.aliasTagName = aliasTagName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tagResId);
        dest.writeString(tagName);
        dest.writeString(tagValue);
        dest.writeString(tagUnit);
        dest.writeString(aliasTagName);
    }
}
