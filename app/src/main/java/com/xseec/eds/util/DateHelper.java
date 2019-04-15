package com.xseec.eds.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2018/7/25.
 */

public class DateHelper {

    public final static String dateRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";

    public final static SimpleDateFormat sdfServlet = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
    public final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd " +
            "HH:mm:ss");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private final static SimpleDateFormat sdfYMD = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat sdfMD = new SimpleDateFormat("MMM dd");

    public static String getString(Date date) {
        if (date == null) {
            date = new Date();
        }
        return simpleDateFormat.format(date);
    }

    public static Date getServletDate(String strDate) {
        try {
            return sdfServlet.parse(strDate);
        } catch (Exception exp) {
            return null;
        }
    }

    public static Calendar getCalendar(String formatTime) {
        Calendar calendar = Calendar.getInstance();
        Date date = null;
        try {
            date = simpleDateFormat.parse(formatTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(date);
        return calendar;
    }

    public static String getYMDString(Date date) {
        return sdfYMD.format(date);
    }

    public static String getServletString(Date date) {
        return sdfServlet.format(date);
    }

    public static Date getYMDDate(String strDate) {
        try {
            return sdfYMD.parse(strDate);

        } catch (Exception exp) {
            return null;
        }
    }

    public static String getMDString(Date date) {
        return sdfMD.format(date);
    }

    public static Date getDate(String strDate) {
        try {
            return simpleDateFormat.parse(strDate);

        } catch (Exception exp) {
            return null;
        }
    }

    public static String getNowForId() {
        Calendar now = Calendar.getInstance();
        return sdf.format(now.getTime());
    }

    public static Date getSdfDate(String strDate) {
        try {
            return sdf.parse(strDate);

        } catch (Exception exp) {
            return null;
        }
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

    public static Calendar getCalendarWithDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static String[] getXvaluesByField(int field) {
        String[] items;
        switch (field) {
            case Calendar.DATE:
                items = new String[24];
                for (int i = 0; i < 24; i++) {
                    items[i] = String.format("%02d:00", i);
                }
                break;
            case Calendar.MONTH:
                items = new String[31];
                for (int i = 0; i < 31; i++) {
                    items[i] = String.format("%02d", i + 1);
                }
                break;
            case Calendar.YEAR:
                items = new String[12];
                for (int i = 0; i < 12; i++) {
                    items[i] = String.format("%02d", i + 1);
                }
                break;
            default:
                items = new String[0];
                break;
        }
        return items;
    }

    public static String getStringByField(Calendar calendar, int field) {
        SimpleDateFormat sdf;
        switch (field) {
            case Calendar.YEAR:
                sdf = new SimpleDateFormat("yyyy");
                break;
            case Calendar.MONTH:
                sdf = new SimpleDateFormat("yyyy MMM");
                break;
            default:
                sdf = new SimpleDateFormat("MMM dd,EEE");
                break;
        }
        return sdf.format(calendar.getTime());
    }

    //nj--获取上周起始时间 2018/11/17
    public static String getStartTimeOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar = getDayStartTime(calendar);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, -(i + 6));
        return getServletString(calendar.getTime());
    }

    //nj--获取上周末时间 2018/11/17
    public static String getEndTimeOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar = getDayStartTime(calendar);
        int i = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_MONTH, -(i - 1));
        return getServletString(calendar.getTime());
    }

    //nj--获取上月的起始日期 2018/11/20
    public static String getStartTimeOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar = getDayStartTime(calendar);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return getServletString(calendar.getTime());
    }

    //nj--获取上月结束日期 2018/11/20
    public static String getEndTimeOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar = getDayStartTime(calendar);
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int i = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.add(Calendar.DAY_OF_MONTH, i);
        return getServletString(calendar.getTime());
    }

    //nj--计算两日期的间隔 2018/11/23
    public static long getBetweenOfSecond(String startTime, String endTime) {
        Date start = getServletDate(startTime);
        Date end = getServletDate(endTime);
        long seconds =  (end.getTime() - start.getTime()) / 1000 ;
        return seconds;
    }

    //nj-计算当前时间往前往后的一段时间 2018/12/19
    public static Date getNearTimeOfMonth(int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, month);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }
}
