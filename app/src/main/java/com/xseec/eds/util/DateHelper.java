package com.xseec.eds.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/7/25.
 */

public class DateHelper {

    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat sdfYMD=new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdfMD=new SimpleDateFormat("MM-dd");

    public static String getString(Date date) {
        return simpleDateFormat.format(date);
    }

    public static String getYMDString(Date date){
        return sdfYMD.format(date);
    }

    public static Date getYMDDate(String strDate){
        try {
            return sdfYMD.parse(strDate);

        } catch (Exception exp) {
            return null;
        }
    }

    public static String getMDString(Date date){
        return sdfMD.format(date);
    }

    public static Date getDate(String strDate) {
        try {
            return simpleDateFormat.parse(strDate);

        } catch (Exception exp) {
            return null;
        }
    }

    public static String getNowForId(){
        Calendar now=Calendar.getInstance();
        return sdf.format(now.getTime());
    }

    public static String getSecondTime(Calendar calendar) {
        return getMinuteTime(calendar) + ":" + calendar.get(Calendar.SECOND);
    }

    public static String getMinuteTime(Calendar calendar) {
        return getHourTime(calendar) + ":" + calendar.get(Calendar.MINUTE);
    }

    public static String getHourTime(Calendar calendar) {
        return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
    }

    public static String getWeekdayTime(Calendar calendar) {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.US);
    }

    public static String getMonthTime(Calendar calendar) {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
    }

    public static String getMonthDayTime(Calendar calendar) {
        return getMonthTime(calendar) + " " + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static Calendar getDayStartTime(Calendar now) {
        Calendar tmp = (Calendar) now.clone();
        tmp.set(Calendar.HOUR_OF_DAY, 0);
        tmp.clear(Calendar.MINUTE);
        tmp.clear(Calendar.SECOND);
        return tmp;
    }
}
