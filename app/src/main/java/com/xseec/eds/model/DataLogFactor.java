package com.xseec.eds.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.DateHelper;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/13.
 */

public class DataLogFactor implements Parcelable {

    private Calendar startTime;
    private StoredTag.IntervalType intervalType;
    private int interval;
    private int records;
    private StoredTag.DataType dataType;

    public static final int INTERVAL_ENERGY_LINK_RADIO=48;

    public DataLogFactor() {
        initDefault();
    }

    private void initDefault() {
        Calendar startTime = Calendar.getInstance();
        startTime.add(Calendar.MINUTE, -5);
        this.startTime = startTime;
        this.intervalType = StoredTag.IntervalType.S;
        this.interval = 5;
        this.records = 60;
        this.dataType = StoredTag.DataType.MAX;
    }

    public DataLogFactor(String tagName) {
        if (Pattern.compile("E[PQS]").matcher(tagName).find()) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            Calendar yesterday = DateHelper.getDayStartTime(calendar);
            this.startTime=yesterday;
            this.intervalType= StoredTag.IntervalType.H;
            this.interval=1;
            //电能为累加值，取49比数据处理得48比数据
            this.records=INTERVAL_ENERGY_LINK_RADIO;
            this.dataType = StoredTag.DataType.MAX;
        } else {
            initDefault();
        }
    }

    public DataLogFactor(Calendar startTime, StoredTag.IntervalType intervalType, int interval,
            int records) {
        this.startTime = startTime;
        this.intervalType = intervalType;
        this.interval = interval;
        this.records = records;
        this.dataType = StoredTag.DataType.MAX;
    }

    public DataLogFactor(Calendar startTime, StoredTag.IntervalType intervalType, int interval,
            int records, StoredTag.DataType dataType) {
        this(startTime, intervalType, interval, records);
        this.dataType = dataType;
    }

    protected DataLogFactor(Parcel in) {
        startTime = Calendar.getInstance();
        startTime.setTime(DateHelper.getDate(in.readString()));
        intervalType = StoredTag.IntervalType.values()[in.readInt()];
        interval = in.readInt();
        records = in.readInt();
        dataType = StoredTag.DataType.values()[in.readInt()];
    }

    public static final Creator<DataLogFactor> CREATOR = new Creator<DataLogFactor>() {
        @Override
        public DataLogFactor createFromParcel(Parcel in) {
            return new DataLogFactor(in);
        }

        @Override
        public DataLogFactor[] newArray(int size) {
            return new DataLogFactor[size];
        }
    };

    public StoredTag.DataType getDataType() {
        return dataType;
    }

    public void setDataType(StoredTag.DataType dataType) {
        this.dataType = dataType;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public StoredTag.IntervalType getIntervalType() {
        return intervalType;
    }

    public void setIntervalType(StoredTag.IntervalType intervalType) {
        this.intervalType = intervalType;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getRecords() {
        return records;
    }

    public void setRecords(int records) {
        this.records = records;
    }

    public String getStartTimeString() {
        return DateHelper.getString(startTime.getTime());
    }

    public Calendar getEndTime(){
        int field=Calendar.SECOND;
        switch (intervalType){
            case S:
                field=Calendar.SECOND;
                break;
            case M:
                field=Calendar.MINUTE;
                break;
            case H:
                field=Calendar.HOUR_OF_DAY;
                break;
            case D:
                field=Calendar.DATE;
                break;
        }
        Calendar endTime= (Calendar) startTime.clone();
        endTime.add(field,interval*records);
        return endTime;
    }

    public void setEndTime(Calendar endTime){
        long intervalSecend=(endTime.getTime().getTime()-startTime.getTime().getTime())/1000;
        if(intervalSecend<=60*30){
            //30min
            intervalType= StoredTag.IntervalType.S;
            records=60;
            interval= (int) (intervalSecend/records);
        }else if(intervalSecend<=60*60*12) {
            //12h
            intervalType= StoredTag.IntervalType.M;
            records= 60;
            interval=(int)(intervalSecend/60/records);
        }else if(intervalSecend<=60*60*48){
            //48h
            intervalType=StoredTag.IntervalType.H;
            interval=1;
            records=(int)(intervalSecend/60/60);
        }else {
            intervalType= StoredTag.IntervalType.D;
            interval=1;
            records=(int)(intervalSecend/60/60/24);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(DateHelper.getString(startTime.getTime()));
        dest.writeInt(intervalType.ordinal());
        dest.writeInt(interval);
        dest.writeInt(records);
        dest.writeInt(dataType.ordinal());
    }
}
