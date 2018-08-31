package com.xseec.eds.model;

import android.text.TextUtils;

import com.xseec.eds.R;

/**
 * Created by Administrator on 2018/8/16.
 */

public enum Device {
    A1("ACB", R.drawable.device_a65),
    A2("ACB",R.drawable.device_a65),
    A3("ACB",R.drawable.device_a66),
    M1("MCCB",R.drawable.device_m1),
    M2("MCCB",R.drawable.device_m2),
    C1("MIC",R.drawable.device_c),
    G1("ADAM3600",R.drawable.device_com_adam3600),
    P1("METER",R.drawable.device_meter);
    private String deviceType;
    private int deviceResId;
    private static String[] infos;
    private Device(String deviceType,int deviceResId){
        this.deviceType=deviceType;
        this.deviceResId=deviceResId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getDeviceResId() {
        return deviceResId;
    }

    public static Device initWithTagName(String tagName){
        infos=tagName.split(":")[0].split("_");
        return valueOf(infos[1]);
    }
    
    public String getDeviceName(){
        return TextUtils.join("_",infos);
    }

    public String getDeviceAlias(){
        return String.format("%s (%s#%s)",deviceType,infos[0],infos[2]);
    }
}
