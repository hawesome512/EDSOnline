package com.xseec.eds.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.github.mikephil.charting.data.Entry;
import com.xseec.eds.R;
import com.xseec.eds.fragment.ActionListFragment;
import com.xseec.eds.fragment.AlarmListFragment;
import com.xseec.eds.fragment.EnergyFragment;
import com.xseec.eds.fragment.ReportFragment;
import com.xseec.eds.fragment.ReportListFragment;
import com.xseec.eds.fragment.SettingFragment;
import com.xseec.eds.fragment.WorkorderListFragment;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.Function;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.model.tags.EnergyTag;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Generator {

    public static void initOverviewTagStore() {
        LitePal.getDatabase();
        Context context = EDSApplication.getContext();
        String name = context.getString(R.string.overview_item_tempreture);
        OverviewTag tag0 = new OverviewTag(0, R.drawable.circle_tempreture, name, null, "℃",
                "XRD:Tempreture", true);
        tag0.save();

        name = context.getString(R.string.overview_item_humidity);
        OverviewTag tag1 = new OverviewTag(1, R.drawable.circle_water, name, null, "%",
                "XRD:Humidity", true);
        tag1.save();

        name = context.getString(R.string.overview_item_activie_power);
        OverviewTag tag2 = new OverviewTag(2, R.drawable.circle_line_2, name, null, "kW",
                "XRD:P", true);
        tag2.save();

        name = context.getString(R.string.overview_item_reactive_power);
        OverviewTag tag3 = new OverviewTag(3, R.drawable.circle_line_4, name, null, "kVar",
                "XRD:Q", true);
        tag3.save();

        name = context.getString(R.string.overview_item_energy);
        OverviewTag tag4 = new OverviewTag(4, R.drawable.circle_light, name, null, "kW·h",
                "XRD:Energy", true);
        tag4.save();

        name = context.getString(R.string.overview_item_harmonic);
        String valueText = context.getString(R.string.overview_item_detail);
        OverviewTag tag5 = new OverviewTag(5, R.drawable.circle_bar_hor, name, valueText, null,
                "RD_A3_6:THD", true);
        tag5.save();
    }

    public static List<Function> genFunctions() {
        List<Function> functions = new ArrayList<>();
        functions.add(new Function(R.drawable.ic_workorder, R.string.nav_workorder,R.id.nav_schedule,
                WorkorderListFragment.newInstance()));
        functions.add(new Function(R.drawable.ic_report, R.string.nav_report,R.id.nav_trend, ReportFragment
                .newInstance()));
        functions.add(new Function(R.drawable.ic_alarm, R.string.nav_alarm,R.id.nav_alarm, AlarmListFragment
                .newInstance()));
        functions.add(new Function(R.drawable.ic_meter, R.string.nav_energy,R.id.nav_energy, EnergyFragment
                .newInstance()));
        functions.add(new Function(R.drawable.ic_action, R.string.nav_action,R.id.nav_action, ActionListFragment
                .newInstance()));
        functions.add(new Function(R.drawable.ic_setting, R.string.nav_setting,R.id.nav_setting, SettingFragment
                .newInstance()));
        return functions;
    }

    public static String genString(String source, int repeat) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            stringBuilder.append(source);
        }
        return stringBuilder.toString();
    }

    public static String getResourceString(String name) {
        Context context = EDSApplication.getContext();
        int resId = context.getResources().getIdentifier(name, "string", context.getPackageName());
        if (resId != 0) {
            return context.getString(resId);
        } else {
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

    public static int getWorkorderImageResWithType(int type) {
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

    public static List<Entry> convertEntryList(List<String> source, float minValue) {
        List<Entry> entryList = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            entryList.add(new Entry(i + minValue, floatTryParse(source.get(i))));
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
        for (int i = 0; i < entryList.size() && i < limitSize; i++) {
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

    public static boolean checkProtectStateZero(String switchValue, List<String> items, String
            item) {
        int value = (int) floatTryParse(switchValue);
        int index = items.indexOf(item);
        int indexNegate = items.indexOf(item + Protect.NEGATE);
        if (index >= 0) {
            return (value & (int) Math.pow(2, index)) == 0;
        } else if (indexNegate >= 0) {
            return (value & (int) Math.pow(2, indexNegate)) != 0;
        } else {
            return false;
        }
    }

    public static int setProtectState(String switchValue, List<String> items, String item,
            boolean switchOn) {
        int value = (int) floatTryParse(switchValue);
        int index = items.indexOf(item);
        int indexNegate = items.indexOf(item + Protect.NEGATE);
        if (index >= 0) {
            return switchOn ? (value | (int) (Math.pow(2, index))) : (value & (int) (Math.pow(2,
                    16) - 1 - Math.pow(2, index)));
        } else if (indexNegate >= 0) {
            return (!switchOn) ? (value | (int) (Math.pow(2, indexNegate))) : (value & (int)
                    (Math.pow(2,
                            16) - 1 - Math.pow(2, indexNegate)));
        } else {
            return value;
        }
    }

    public static String getAlarmStateText(String status, List<String> items) {
        int value = (int) floatTryParse(status);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            int index = value & (int) Math.pow(2, i);
            if (index > 0 && !items.get(i).equals("*")) {
                stringBuilder.append(items.get(i) + " ");
            }
        }
        String result = stringBuilder.toString();
        return result.trim();
    }

    public static String getAlarmStateTextWithTag(Tag tag) {
        Device device = Device.initWithTagName(tag.getTagName());
        return getAlarmStateText(tag.getTagValue(), device.getStatusItems());
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

    public static List<EnergyTag> genEnergyTagList() {
        List<EnergyTag> tagList = new ArrayList<>();
        tagList.add(new EnergyTag("E_0_厦门士林电机", "100"));

        tagList.add(new EnergyTag("E_00_车间1", "40"));
        tagList.add(new EnergyTag("E_000_空调", "10"));
        tagList.add(new EnergyTag("E_001_照明", "5"));
        tagList.add(new EnergyTag("E_002_动力", "20"));
        tagList.add(new EnergyTag("E_003_插座", "5"));

        tagList.add(new EnergyTag("E_01_车间2", "35"));
        tagList.add(new EnergyTag("E_010_空调", "10"));
        tagList.add(new EnergyTag("E_011_照明", "5"));
        tagList.add(new EnergyTag("E_012_动力", "15"));
        tagList.add(new EnergyTag("E_013_插座", "5"));

        tagList.add(new EnergyTag("E_02_办公楼", "25"));
        tagList.add(new EnergyTag("E_020_空调", "5"));
        tagList.add(new EnergyTag("E_021_照明", "10"));
        tagList.add(new EnergyTag("E_022_动力", "5"));
        tagList.add(new EnergyTag("E_023_插座", "5"));

        return tagList;
    }

    public static List<String> genLastNowEntryList(Calendar start, int field, String value) {
        int baseValue;
        int fieldItem;
        int nValue = Integer.valueOf(value);
        Calendar time = (Calendar) start.clone();
        time.add(field, -1);
        switch (field) {
            case Calendar.YEAR:
                baseValue = 24 * 30 * nValue;
                fieldItem = Calendar.MONTH;
                time.set(Calendar.MONTH, 0);
                break;
            case Calendar.MONTH:
                baseValue = 24 * nValue;
                fieldItem = Calendar.DATE;
                time.set(Calendar.DAY_OF_MONTH, 1);
                break;
            default:
                baseValue = nValue;
                fieldItem = Calendar.HOUR;
                break;
        }
        time.set(Calendar.HOUR_OF_DAY, 0);
        time.set(Calendar.MINUTE, 0);
        time.set(Calendar.SECOND, 0);
        Calendar end = (Calendar) time.clone();
        end.add(field, 2);
        Calendar now = Calendar.getInstance();
        List<String> values = new ArrayList<>();
        int first = time.get(field);
        Random random = new Random();
        while (time.before(end) && time.before(now)) {
            values.add((80 + random.nextInt(40)) * baseValue / 100 + "");
            time.add(fieldItem, 1);
        }
        return values;
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
    public static int getNearestIndex(String item, List<String> items) {
        if (items.contains(item)) {
            return items.indexOf(item);
        } else {
            float f0 = Generator.floatTryParse(item);
            float delta0 = Float.MAX_VALUE;
            int index = 0;
            for (int i = 0; i < items.size(); i++) {
                float f = Generator.floatTryParse(items.get(i));
                float delta = Math.abs(f - f0);
                if (delta < delta0) {
                    index = i;
                    delta0 = delta;
                }
            }
            return index;
        }
    }

    /*
     *两个float/double操作，很可能出错，缘由见于网络
     *特别注意：value1/value2类型应该为String,若未float/double还是会计算失真
     */
    public enum Operator {
        ADD, SUBTRACT, MULTIPLY, DIVIDE
    }

    public static String calFloatValue(String value1, String value2, Operator operator) {
        BigDecimal d1 = new BigDecimal(value1);
        BigDecimal d2 = new BigDecimal(value2);
        switch (operator) {
            case ADD:
                return d1.add(d2).toString();
            case SUBTRACT:
                return d1.subtract(d2).toString();
            case MULTIPLY:
                return d1.multiply(d2).toString();
            case DIVIDE:
                //BigBecimal必须加后两个参数，否则会出错
                return d1.divide(d2, 3, BigDecimal.ROUND_HALF_UP).toString();
            default:
                return null;
        }
    }

    //nj--统计list的尺寸 2018/11/19
    public static String countList(List sources){
            return sources!=null?String.valueOf( sources.size() ):"0";
    }

    //nj--获取报表时间段信息 2018/11/19
    public static String getReportTime(String startTime,String endTime){
        Date start=DateHelper.getServletDate( startTime );
        Date end=DateHelper.getServletDate( endTime );
        Context context=EDSApplication.getContext();
        String time=context.getString( R.string.workorder_time,DateHelper.getYMDString( start ),
                DateHelper.getYMDString( end ));
        return time;
    }

    //nj--工单信息筛选 2018/11/19
    public static List<Workorder> filterWorkorderListOfState(List<Workorder> sources, int state){
        List<Workorder> workorderList=new ArrayList<Workorder>(  );
        if (state==-1){
            workorderList.addAll( sources );
        }else{
            for(Workorder workorder:sources){
                if (workorder.getWorkorderState().ordinal()==state){
                    workorderList.add( workorder);
                }
            }
        }
        return workorderList;
    }

    public static List<Workorder> filterWorkorderListOfType(List<Workorder> sources, int type){
        List<Workorder> workorderList=new ArrayList<>(  );
        if (type==-1){
            workorderList.addAll( sources );
        }else{
            for(Workorder workorder:sources){
                if (workorder.getType()==type){
                    workorderList.add( workorder);
                }
            }
        }
        return workorderList;
    }

    //nj--异常管理信息筛选 2018/11/19
    public static List<Alarm> filterAlarmConfim(List<Alarm> sources, int confirm){
        List<Alarm> alarmList=new ArrayList<>(  );
        if (confirm==-1){
             alarmList.addAll( sources );
        }else {
            for (Alarm alarm:sources){
                if (alarm.getConfirm()==confirm){
                    alarmList.add( alarm );
                }
            }
        }
        return alarmList;
    }

    public static List<Alarm> filterAlarmDevice(List<Alarm> sources, String device){
        List<Alarm> alarmList=new ArrayList<>(  );
        for (Alarm alarm:sources){
            if (alarm.getDevice().equals( device )){
                alarmList.add( alarm );
            }
        }
        return alarmList;
    }

    //nj--操作信息筛选 2018/11/19
    public static List<Action> filterActionList(List<Action> sources, Action.ActionType type){
        List<Action> actionList=new ArrayList<>(  );
        for (Action action:sources){
            if (action.getActionType()==type){
                actionList.add( action );
            }
        }
        return actionList;
    }

    //nj--环境报表数据总数、平均值2018/11/25
    public static String getReportMax(List<String> sources){
        String value=Collections.max( sources );;
        if (!value.equals( "#" )){
            float max=Math.round( Float.valueOf( value )*100 )/100f;
            return String.valueOf( max );
        }else {
            return String.valueOf( 0.0 );
        }
    }

    public static String getReportMin(List<String> sources){
        String value=Collections.min( sources );;
        if (!value.equals( "#" )){
            float min=Math.round( Float.valueOf( value )*100 )/100f;
            return String.valueOf( min );
        }else {
            return String.valueOf( 0.0 );
        }
    }

    public static String getReportSum(List<String> sources){
        float sum=0;
        for (int i=0;i<sources.size();i++){
            float value;
            String itemValue=sources.get( i );
            if (itemValue.equals( "#" )){
                value=0;
            }else {
                value=Float.valueOf( sources.get( i ) );
            }
            sum+=value;
        }
        return String.valueOf( sum );
    }

    public static String getReportAve(List<String> sources){
        String sum=getReportSum( sources );
        float ave=Math.round(Float.valueOf( sum )/sources.size()*100)/100f;
        return String.valueOf( ave );
    }
}
