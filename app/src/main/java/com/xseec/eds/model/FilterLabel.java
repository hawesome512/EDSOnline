package com.xseec.eds.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/*NJ--定义筛选的标签类 2018/12/20
数据类型定义方式：groupName-->0_类型代码-->0_type:
                            0_一级列表  00_二级列表
                label-->计划工单：
                labelValue：标签类型的实际值-->0;
                type:0：列表标题 1：列表标签值*/

public class FilterLabel implements Parcelable {
    private static final  String SPLIT="_";

    public static final int HEADER=0;
    public static final int LABLE=1;

    //nj--定义标签类型枚举 2018/12/20
    @IntDef( { HEADER,LABLE } )
    @Retention( RetentionPolicy.SOURCE )
    public @interface State{};

    private String groupName;
    private String label;
    @SerializedName( "labelValue" )
    private String value;
    @SerializedName( "type" )
    private @State int state;

    private boolean isChecked=false;

    //nj--初始化标签 2018/12/20
    public FilterLabel(String label,String groupName,String value){
        this.label=label;
        this.state=LABLE;
        this.groupName=groupName;
        this.value=value;
    }

    //nj--初始化标题 2018/12/20
    public FilterLabel(String label,String groupName,int state){
        this.label=label;
        this.state=state;
        this.groupName=groupName;
    }

    public String getType(){
        return groupName.split( SPLIT )[1];
    }

    public boolean isGroup(){
        String[] temps=groupName.split( SPLIT );
        return temps[0].equals( "0" )?true:false;
    }

    protected FilterLabel(Parcel in) {
        groupName = in.readString();
        label = in.readString();
        value = in.readString();
        state = in.readInt();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( groupName );
        dest.writeString( label );
        dest.writeString( value );
        dest.writeInt( state );
        dest.writeByte( (byte) (isChecked ? 1 : 0) );
    }

    public String getGroupName() {
        return groupName;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getValueOfInt(){
        return Integer.valueOf( value );
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public static final Creator<FilterLabel> CREATOR = new Creator<FilterLabel>() {
        @Override
        public FilterLabel createFromParcel(Parcel in) {
            return new FilterLabel( in );
        }

        @Override
        public FilterLabel[] newArray(int size) {
            return new FilterLabel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }
}
