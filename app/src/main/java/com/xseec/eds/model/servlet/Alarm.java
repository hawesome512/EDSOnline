package com.xseec.eds.model.servlet;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.util.DateHelper;

import java.util.Date;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Alarm {

    private String id;
    private String device;
    @SerializedName("alarm")
    private String info;
    private Date time;

    public Alarm(){}

    public Alarm(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();
        if(TextUtils.isEmpty(id)){
            return null;
        }else {
            stringBuilder.append("id="+id);
        }
        if(!TextUtils.isEmpty(device)){
            stringBuilder.append("&device="+device);
        }
        if(!TextUtils.isEmpty(info)){
            stringBuilder.append("&alarm="+info);
        }
        return stringBuilder.toString();
    }
}
