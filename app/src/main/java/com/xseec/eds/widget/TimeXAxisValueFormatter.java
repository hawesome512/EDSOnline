package com.xseec.eds.widget;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/8/10.
 */

public class TimeXAxisValueFormatter implements IAxisValueFormatter{

    private BarLineChartBase<?> chartBase;
    private StoredTag.IntervalType intervalType;
    private Calendar startTime;
    private int interval;

    public TimeXAxisValueFormatter(BarLineChartBase<?> chartBase,DataLogFactor factor) {
        this.chartBase = chartBase;
        this.intervalType=factor.getIntervalType();
        this.interval=factor.getInterval();
        this.startTime=factor.getStartTime();
    }

    public TimeXAxisValueFormatter(BarLineChartBase<?> chartBase,StoredTag.IntervalType intervalType,Calendar startTime,int interval) {
        this.chartBase = chartBase;
        this.intervalType=intervalType;
        this.startTime=startTime;
        this.interval=interval;
    }
    List<Float> nums=new ArrayList<>();
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar tmp= (Calendar) startTime.clone();
        int index=(int)value;
        switch (intervalType){
            case S:
                tmp.add(Calendar.SECOND,interval*index);
                return DateHelper.getSecondTime(tmp);
            case M:
                tmp.add(Calendar.MINUTE,interval*index);
                return DateHelper.getMinuteTime(tmp);
            case H:
                tmp.add(Calendar.HOUR,interval*index);
                return DateHelper.getHourTime(tmp);
            case D:
                tmp.add(Calendar.DATE,interval*index);
                return DateHelper.getMonthDayTime(tmp);
            default:
                tmp.add(Calendar.MONTH,interval*index);
                return DateHelper.getMonthTime(tmp);
        }
    }
}
