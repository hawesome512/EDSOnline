package com.xseec.eds.model.deviceconfig;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2018/8/23.
 */

public class DeviceConfig {

    @SerializedName("Type")
    private String deviceType;
    @SerializedName("Name")
    private String deviceName;
    @SerializedName("Overview")
    private List<String> overviewZone;
    @SerializedName("Real")
    private RealZone realZone;
    @SerializedName("Protect")
    private List<Protect> protectZone;
    @SerializedName("Control")
    private List<String> farControl;
    @SerializedName("Alarm")
    private List<String> alarmParams;

    public List<String> getAlarmParams() {
        return alarmParams;
    }

    public void setAlarmParams(List<String> alarmParams) {
        this.alarmParams = alarmParams;
    }

    public List<String> getOverviewZone() {
        return overviewZone;
    }

    public void setOverviewZone(List<String> overviewZone) {
        this.overviewZone = overviewZone;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public RealZone getRealZone() {
        return realZone;
    }

    public void setRealZone(RealZone realZone) {
        this.realZone = realZone;
    }

    public List<Protect> getProtectZone() {
        return protectZone;
    }

    public void setProtectZone(List<Protect> protectZone) {
        this.protectZone = protectZone;
    }

    public List<String> getFarControl() {
        return farControl;
    }

    public void setFarControl(List<String> farControl) {
        this.farControl = farControl;
    }


}
