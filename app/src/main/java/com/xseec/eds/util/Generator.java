package com.xseec.eds.util;

import android.content.Context;

import com.github.mikephil.charting.data.Entry;
import com.xseec.eds.R;
import com.xseec.eds.model.tags.OverviewTag;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Generator {

    public static void initOverviewTagStore() {
        LitePal.getDatabase();
        Context context = EDSApplication.getContext();
        String name = context.getString(R.string.overview_item_tempreture);
        OverviewTag tag0 = new OverviewTag(0, R.drawable.circle_tempreture, name, null, "℃",
                "AREA:Tempreture", true);
        tag0.save();

        name = context.getString(R.string.overview_item_humidity);
        OverviewTag tag1 = new OverviewTag(1, R.drawable.circle_water, name, null, "%",
                "AREA:Humidity", true);
        tag1.save();

        name = context.getString(R.string.overview_item_activie_power);
        OverviewTag tag2 = new OverviewTag(2, R.drawable.circle_line_2, name, null, "kW",
                "AREA:P", true);
        tag2.save();

        name = context.getString(R.string.overview_item_reactive_power);
        OverviewTag tag3 = new OverviewTag(3, R.drawable.circle_line_4, name, null, "kVar",
                "AREA:Q", true);
        tag3.save();

        name = context.getString(R.string.overview_item_energy);
        OverviewTag tag4 = new OverviewTag(4, R.drawable.circle_light, name, null, "kW·h",
                "AREA:Energy", true);
        tag4.save();

        name = context.getString(R.string.overview_item_harmonic);
        String valueText = context.getString(R.string.overview_item_detail);
        OverviewTag tag5 = new OverviewTag(5, R.drawable.circle_bar_hor, name, valueText, null,
                "RD_A3_6:THD", true);
        tag5.save();
    }

    public static String genString(String source, int repeat) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            stringBuilder.append(source);
        }
        return stringBuilder.toString();
    }

    public static String genTime() {
        Calendar calendar = Calendar.getInstance();
        return DateHelper.getString(calendar.getTime());
    }

    public static int getScheduleImageRes() {
        Random random = new Random();
        int index = random.nextInt(8);
        Context context = EDSApplication.getContext();
        return context.getResources().getIdentifier("schedule_" + String.valueOf(index),
                "drawable", context.getPackageName());
    }

    public static List<Entry> getEntryList(int size) {
        Random random = new Random();
        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            entryList.add(new Entry(i, random.nextInt(3000) + 5000));
        }
        return entryList;
    }

    public static List<Entry> convertEntryList(List<String> source) {
        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            entryList.add(new Entry(i, floatTryParse(source.get(i))));
        }
        return entryList;
    }

    public static float getAvgFromEntryList(List<Entry> entryList) {
        float tmp = 0;
        for (Entry entry : entryList) {
            tmp += entry.getY();
        }
        return Math.round(tmp / entryList.size() * 100) / 100f;
    }

    public static float getSumFromEntryList(List<Entry> entryList,int limitSize){
        float tmp = 0;
        for (int i=0;i<limitSize;i++) {
            tmp += entryList.get(i).getY();
        }
        return tmp;
    }

    public static float floatTryParse(String source) {
        try {
            return Float.parseFloat(source);
        } catch (Exception exp) {
            return 0;
        }
    }

    public static List<String> genYesTodayEntryList(Calendar startTime) {
        Random random = new Random();
        Calendar now=Calendar.getInstance();
        Calendar target= (Calendar) startTime.clone();
        List<String> values = new ArrayList<>();
        for (int i = 0; i < 48&&target.before(now); i++) {
            int hour = target.get(Calendar.HOUR_OF_DAY);
            int value;
            if (hour >= 8 && hour <= 20) {
                value = (160 + random.nextInt(40));
            } else {
                value = (80 + random.nextInt(40));
            }
            values.add(String.valueOf(value));
            target.add(Calendar.HOUR,1);
        }
        return values;
    }
}
