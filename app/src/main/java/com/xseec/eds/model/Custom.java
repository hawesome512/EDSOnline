package com.xseec.eds.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Custom implements Parcelable{

    private int imageRes;
    private int nameRes;
    private CustomType customType;

    public  enum CustomType{
        AREA,ALIAS,USER,ENERGY,OVERVIEWTAG
    }

    public Custom(int imageRes, int nameRes, CustomType customType){
        this.imageRes=imageRes;
        this.nameRes=nameRes;
        this.customType=customType;
    }

    public CustomType getCustomType() {
        return customType;
    }

    public int getImageRes() {

        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public int getNameRes() {
        return nameRes;
    }

    public void setNameRes(int nameRes) {
        this.nameRes = nameRes;
    }

    protected Custom(Parcel in) {
        imageRes = in.readInt();
        nameRes = in.readInt();
        customType=CustomType.values()[in.readInt()];
    }

    public static final Creator<Custom> CREATOR = new Creator<Custom>() {
        @Override
        public Custom createFromParcel(Parcel in) {
            return new Custom( in );
        }

        @Override
        public Custom[] newArray(int size) {
            return new Custom[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt( imageRes );
        dest.writeInt( nameRes );
        dest.writeInt( customType.ordinal() );
    }
}
