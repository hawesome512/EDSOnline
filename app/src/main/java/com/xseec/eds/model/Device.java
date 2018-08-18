package com.xseec.eds.model;

import com.xseec.eds.R;
import com.xseec.eds.model.Tags.Tag;

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
}
