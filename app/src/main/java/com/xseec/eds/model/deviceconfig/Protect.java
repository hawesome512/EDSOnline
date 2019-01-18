package com.xseec.eds.model.deviceconfig;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/29.
 */

public class Protect implements Parcelable {

    public static final String SWITCH_OFF="off";
    public static final String I2T_ON="(I2t on)";
    public static final String I2T_OFF="(I2t off)";
    public static final String NEGATE="_negate";
    public static final String IN="In";
    public static final String IE="Ie";
    public static final String LOCAL="76";
    public static final String REMOTE="82";
    public static final String CTRL_MODE="CtrlMode";
    private static final String STEP="*";

    @SerializedName("Name")
    private String name;
    @SerializedName("Items")
    private List<String> items;
    @SerializedName("Unit")
    private String unit;

    protected Protect(Parcel in) {
        name = in.readString();
        items = in.createStringArrayList();
        unit=in.readString();
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
        if(items.contains(STEP)&&items.size()>=4){
            List<String> tmps=new ArrayList<>();
            float start=Generator.floatTryParse(items.get(1));
            float step=Generator.floatTryParse(items.get(2));
            float end=Generator.floatTryParse(items.get(3));
            NumberFormat nf=NumberFormat.getInstance();
            for(float i =start;i<=end;i+=step){
                tmps.add(nf.format(i));
            }
            return tmps;
        }
        int index=items.indexOf(SWITCH_OFF);
        //device_config.json中“关闭”固定为“off"，而在界面中动态映射于strings
        if(index>=0){
            Context context= EDSApplication.getContext();
            items.set(index,context.getString(R.string.device_protect_off));
        }
        return items;
    }

    public void setItems(List<String> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeStringList(items);
        dest.writeString(unit);
    }
}