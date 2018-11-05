package com.xseec.eds.model.servlet;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Date;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Alarm extends BaseModel {

    private String device;
    @SerializedName("alarm")
    private String info;
    private Date time;

    public Alarm(){}

    public Alarm(String id){
        this.id=id;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void genId(String zoneId){
        this.id=zoneId+"-"+DateHelper.getNowForId();
    }

    @Override
    public String toJson() {
        Context context = EDSApplication.getContext();
        return context.getString(R.string.svl_alarm_request, id, device, info, DateHelper.getString(time));
    }
}
