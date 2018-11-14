package com.xseec.eds.util;

import android.content.Context;
import android.content.res.Resources;

import com.github.mikephil.charting.data.Entry;
import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;

import org.litepal.LitePal;

import java.math.BigDecimal;
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

    public static String getResourceString(String name){
        Context context=EDSApplication.getContext();
        int resId=context.getResources().getIdentifier(name,"string",context.getPackageName());
        if(resId!=0){
            return context.getString(resId);
        }else {
            return null;
        }
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

    public static int getWorkorderImageResWithType(int type){
        Context context = EDSApplication.getContext();
        return context.getResources().getIdentifier("schedule_" + String.valueOf(type),
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

    public static float getSumFromEntryList(List<Entry> entryList, int limitSize) {
        float tmp = 0;
        for (int i = 0; i < limitSize; i++) {
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

    public static boolean checkProtectStateZero(String switchValue, List<String> items, String item) {
        int value = (int) floatTryParse(switchValue);
        int index = items.indexOf(item);
        int indexNegate=items.indexOf(item+ Protect.NEGATE);
        if(index>=0){
            return (value & (int) Math.pow(2, index)) == 0;
        }else if(indexNegate>=0){
            return  (value & (int) Math.pow(2, indexNegate)) != 0;
        }else {
            return false;
        }
    }

    public static int setProtectState(String switchValue, List<String> items, String item,
            boolean switchOn) {
        int value = (int) floatTryParse(switchValue);
        int index = items.indexOf(item);
        int indexNegate=items.indexOf(item+ Protect.NEGATE);
        if(index>=0){
            return switchOn ? (value | (int) (Math.pow(2, index))) : (value & (int) (Math.pow(2,
                    16) - 1 - Math.pow(2, index)));
        }else if(indexNegate>=0){
            return (!switchOn) ? (value | (int) (Math.pow(2, indexNegate))) : (value & (int) (Math.pow(2,
                    16) - 1 - Math.pow(2, indexNegate)));
        }else {
            return value;
        }
    }

    public static String getAlarmStateText(String status,List<String> items){
        int value=(int)floatTryParse(status);
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<items.size();i++){
            int index=value&(int)Math.pow(2,i);
            if(index>0&&!items.get(i).equals("*")){
                stringBuilder.append(items.get(i)+" ");
            }
        }
        String result=stringBuilder.toString();
        return result.trim();
    }

    public static String getAlarmStateTextWithTag(Tag tag){
        Device device=Device.initWithTagName(tag.getTagName());
        return getAlarmStateText(tag.getTagValue(),device.getStatusItems());
    }

    public static float getAvgTagsValue(List<Tag> tagList) {
        float sum = 0;
        for (Tag tag : tagList) {
            sum += floatTryParse(tag.getTagValue());
        }
        return Math.round(sum / tagList.size());
    }

    public static float getMaxDeltaTagsValue(List<Tag> tagList) {
        float avg = getAvgTagsValue(tagList);
        float max = 0;
        for (Tag tag : tagList) {
            float tmp = Math.abs(floatTryParse(tag.getTagValue()) - avg) / avg * 100;
            max = tmp > max ? tmp : max;
        }
        return Math.round(max * 10) / 10f;
    }

    public static List<String> genYesTodayEntryList(Calendar startTime) {
        Random random = new Random();
        Calendar now = Calendar.getInstance();
        Calendar target = (Calendar) startTime.clone();
        List<String> values = new ArrayList<>();
        for (int i = 0; i < 48 && target.before(now); i++) {
            int hour = target.get(Calendar.HOUR_OF_DAY);
            int value;
            if (hour >= 8 && hour <= 20) {
                value = (160 + random.nextInt(40));
            } else {
                value = (80 + random.nextInt(40));
            }
            values.add(String.valueOf(value));
            target.add(Calendar.HOUR, 1);
        }
        return values;
    }

    /*
     *在数值数组中找到最接近的值
     * eg:<items>0.8,0.9,0.95,1</items>
     * 630*0.95=598.5，取整599，反馈599/630=0.951，取近值0.95
     */
    public static int getNearestIndex(String item,List<String> items){
        if(items.contains(item)){
            return items.indexOf(item);
        }else {
            float f0=Generator.floatTryParse(item);
            float delta0=Float.MAX_VALUE;
            int index=0;
            for (int i = 0; i < items.size(); i++) {
                float f=Generator.floatTryParse(items.get(i));
                float delta=Math.abs(f-f0);
                if(delta<delta0){
                    index=i;
                    delta0=delta;
                }
            }
            return index;
        }
    }

    /*
     *两个float/double操作，很可能出错，缘由见于网络
     *特别注意：value1/value2类型应该为String,若未float/double还是会计算失真
     */
    public enum Operator{ADD,SUBTRACT,MULTIPLY,DIVIDE}
    public static String calFloatValue(String value1,String value2,Operator operator){
        BigDecimal d1=new BigDecimal(value1);
        BigDecimal d2=new BigDecimal(value2);
        switch (operator){
            case ADD:
                return d1.add(d2).toString();
            case SUBTRACT:
                return d1.subtract(d2).toString();
            case MULTIPLY:
                return d1.multiply(d2).toString();
            case DIVIDE:
                //BigBecimal必须加后两个参数，否则会出错
                return d1.divide(d2,3,BigDecimal.ROUND_HALF_UP).toString();
            default:
                return null;
        }
    }
}
