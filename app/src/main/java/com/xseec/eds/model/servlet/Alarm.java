package com.xseec.eds.model.servlet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Alarm extends BaseModel implements Comparable,Parcelable {

    private static final String ZONE_SPIT = "-";

    private String device;
    @SerializedName("alarm")
    private String info;
    private Date time;
    private int confirm;
    private String report;

    public Alarm() {
    }

    public Alarm(String id) {
        this.id = id;
    }

    protected Alarm(Parcel in) {
        id=in.readString();
        device = in.readString();
        info = in.readString();
        time=DateHelper.getDate(in.readString());
        confirm = in.readInt();
        report = in.readString();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    public String getDevice() {
        return device.split(ZONE_SPIT)[1];
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

    public int getConfirm() {
        return confirm;
    }

    public void setConfirm(int confirm) {
        this.confirm = confirm;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public void genId(String zoneId) {
        this.id = zoneId + "-" + DateHelper.getNowForId();
    }

    public boolean checked() {
        return getConfirm() > 0;
    }

    @Override
    public String toJson() {
        Context context = EDSApplication.getContext();
        return context.getString(R.string.svl_alarm_request, id, device, info, DateHelper
                .getString(time), confirm, report);
    }

    public String getAlarmCode() {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(info);
        if (matcher.find()) {
            return matcher.group(0);
        } else {
            return null;
        }
    }

    @Override
    public int compareTo(@NonNull Object o) {
        Alarm alarm = (Alarm) o;
        if (this.confirm < alarm.getConfirm()) {
            return 1;
        } else if (this.confirm > alarm.getConfirm()) {
            return -1;
        } else {
            if (this.time.before(alarm.getTime())) {
                return -1;
            } else if (this.time.after(alarm.getTime())) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(device);
        dest.writeString(info);
        dest.writeString(DateHelper.getString(time));
        dest.writeInt(confirm);
        dest.writeString(report);
    }
}
