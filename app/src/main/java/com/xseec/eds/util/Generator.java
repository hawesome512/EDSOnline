package com.xseec.eds.util;

import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mikephil.charting.data.Entry;
import com.xseec.eds.R;
import com.xseec.eds.fragment.ActionListFragment;
import com.xseec.eds.fragment.AlarmListFragment;
import com.xseec.eds.fragment.EnergyFragment;
import com.xseec.eds.fragment.ReportFragment;
import com.xseec.eds.fragment.SettingFragment;
import com.xseec.eds.fragment.WorkorderListFragment;
import com.xseec.eds.model.Custom;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.Function;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.model.tags.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Generator {

    public static final String NULL_VALUE = "#";

    public static List<Function> genFunctions(Basic basic) {
        List<Function> functions = new ArrayList<>();
        functions.add(new Function(R.drawable.ic_workorder, R.string.nav_workorder, R.id
                .nav_schedule,
                WorkorderListFragment.newInstance()));
        functions.add(new Function(R.drawable.ic_report, R.string.nav_report, R.id.nav_trend,
                ReportFragment
                        .newInstance()));
        functions.add(new Function(R.drawable.ic_alarm, R.string.nav_alarm, R.id.nav_alarm,
                AlarmListFragment
                        .newInstance()));
        functions.add(new Function(R.drawable.ic_meter, R.string.nav_energy, R.id.nav_energy,
                EnergyFragment
                        .newInstance(basic.getEnergy())));
        functions.add(new Function(R.drawable.ic_action, R.string.nav_action, R.id.nav_action,
                ActionListFragment
                        .newInstance()));
        functions.add(new Function(R.drawable.ic_setting, R.string.nav_setting, R.id.nav_setting,
                SettingFragment
                        .newInstance()));
        return functions;
    }

    public static List<Custom> genSettings() {
        List<Custom> customList = new ArrayList<>();
        customList.add(new Custom(R.drawable.ic_building_blue_700_24dp, R.string.setting_basic,
                Custom.CustomType.AREA));
        customList.add(new Custom(R.drawable.ic_devices_other_blue_700_24dp, R.string
                .setting_device,
                Custom.CustomType.ALIAS));
        customList.add(new Custom(R.drawable.ic_users_manage, R.string.setting_user,
                Custom.CustomType.USER));
        customList.add(new Custom(R.drawable.ic_energy_setting, R.string.setting_energy,
                Custom.CustomType.ENERGY));
        customList.add(new Custom(R.drawable.ic_overview_tag_setting, R.string.setting_overview,
                Custom.CustomType.OVERVIEWTAG));
        return customList;
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

    public static int getImageRes(String imageName) {
        Context context = EDSApplication.getContext();
        return context.getResources().getIdentifier(imageName,
                "drawable", context.getPackageName());
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

    public static boolean checkIsOne(String switchValue, int index) {
        int value = (int) floatTryParse(switchValue);
        return (value & (int) Math.pow(2, index)) > 0;
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
        Device device = Device.initWith(tag.getTagName());
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

    public static List<String> genPerEnergyEntryList(List<String> totals) {
        List<String> pers = new ArrayList<>();
        for (int i = 1; i < totals.size(); i++) {
            float now = floatTryParse(totals.get(i));
            float pre = floatTryParse(totals.get(i - 1));
            //暂时方案，数据清零等操作造成累加值突变or通信异常造成pre为0
            now = (pre == 0 || now == 0) ? 0 : now - pre;
            pers.add(String.valueOf(now));
        }
        return pers;
    }

    public static String removeFuture(String jsonData) {
        return jsonData.replaceAll("(,\"#\")+]", "]");
    }

    public static List<String> getMonthList(List<String> dayList, Calendar start) {
        int index = 0;
        int toIndex;
        List<String> result = new ArrayList<>();
        List<String> tmps = null;
        while (index < dayList.size()) {
            toIndex = index + start.getActualMaximum(Calendar.DAY_OF_MONTH);
            toIndex = toIndex > dayList.size() ? dayList.size() : toIndex;
            tmps = dayList.subList(index, toIndex);
            result.add(String.valueOf(calSum(tmps)));
            index = toIndex;
            start.add(Calendar.MONTH, 1);
        }
        return result;
    }

    public static int calSum(List<String> yValues) {
        int sum = 0;
        for (String value : yValues) {
            if (value.equals(StoredTag.NULL_VALUE)) {
                continue;
            }
            sum += Generator.floatTryParse(value);
        }
        return sum;
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

    public static List<String> addTwoTagLogs(List<String> first, List<String> secend) {
        int length = Math.max(first.size(), secend.size());
        List<String> result = new ArrayList<>();
        String str1, str2;
        for (int i = 0; i < length; i++) {
            str1 = getListItem(first, i);
            str2 = getListItem(secend, i);
            if (str1.equals(StoredTag.NULL_VALUE) && str2.equals(StoredTag.NULL_VALUE)) {
                result.add(StoredTag.NULL_VALUE);
            } else {
                float value = floatTryParse(str1) + floatTryParse(str2);
                result.add(String.valueOf(value));
            }
        }
        return result;
    }

    private static String getListItem(List<String> list, int index) {
        return index >= list.size() ? StoredTag.NULL_VALUE : list.get(index);
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
    public static String countList(List sources) {
        return sources != null ? String.valueOf(sources.size()) : "0";
    }

    //nj--获取报表时间段信息 2018/11/19
    public static String getReportTime(String startTime, String endTime) {
        Date start = DateHelper.getServletDate(startTime);
        Date end = DateHelper.getServletDate(endTime);
        Context context = EDSApplication.getContext();
        String time = context.getString(R.string.workorder_time, DateHelper.getYMDString(start),
                DateHelper.getYMDString(end));
        return time;
    }

    //nj--工单信息筛选 2018/11/19
    public static List<Workorder> filterWorkorderListOfState(List<Workorder> sources, int state) {
        List<Workorder> workorderList = new ArrayList<Workorder>();
        if (state == -1) {
            workorderList.addAll(sources);
        } else {
            for (Workorder workorder : sources) {
                if (workorder.getWorkorderState().ordinal() == state) {
                    workorderList.add(workorder);
                }
            }
        }
        return workorderList;
    }

    public static List<Workorder> filterWorkorderListOfType(List<Workorder> sources, int type) {
        List<Workorder> workorderList = new ArrayList<>();
        if (type == -1) {
            workorderList.addAll(sources);
        } else {
            for (Workorder workorder : sources) {
                if (workorder.getType() == type) {
                    workorderList.add(workorder);
                }
            }
        }
        return workorderList;
    }

    //nj--异常管理信息筛选 2018/11/19
    public static List<Alarm> filterAlarmConfirm(List<Alarm> sources, int confirm) {
        List<Alarm> alarmList = new ArrayList<>();
        if (confirm == -1) {
            alarmList.addAll(sources);
        } else {
            for (Alarm alarm : sources) {
                if (alarm.getConfirm() == confirm) {
                    alarmList.add(alarm);
                }
            }
        }
        return alarmList;
    }

    public static List<Alarm> filterAlarmDevice(List<Alarm> sources, String device) {
        List<Alarm> alarmList = new ArrayList<>();
        for (Alarm alarm : sources) {
            if (alarm.getDevice().equals(device)) {
                alarmList.add(alarm);
            }
        }
        return alarmList;
    }

    //nj--操作信息筛选 2018/11/19
    public static List<Action> filterActionsType(List<Action> sources, int type) {
        List<Action> actionList = new ArrayList<>();
        if (type == -1) {
            actionList.addAll(sources);
        } else {
            for (Action action : sources) {
                if (action.getActionType().ordinal() == type)
                    actionList.add(action);
            }
        }
        return actionList;
    }

    public static List<Action> filterActionsMethod(List<Action> sources, int method) {
        List<Action> actionList = new ArrayList<>();
        for (Action action : sources) {
            if (action.getActionMethod().ordinal() == method)
                actionList.add(action);
        }
        return actionList;
    }

    //nj--环境报表数据总数、平均值2018/11/25
    public static String getReportMax(List<String> sources) {
        float avg = Float.valueOf(getReportAve(sources));
        float max = 0;
        for (String value : sources) {
            float tmp = floatTryParse(value);
            max = floatTryParse(value) > max ? floatTryParse(value) : max;
        }
        return String.valueOf(Math.round(max * 10) / 10f);
    }

    public static String getReportMin(List<String> sources) {
        float avg = Float.valueOf(getReportAve(sources));
        float min = 0;
        for (String value : sources) {
            float tmp = floatTryParse(value);
            min = tmp < avg ? tmp : avg;
        }
        return String.valueOf(Math.round(min * 10) / 10f);
    }

    public static String getReportSum(List<String> sources) {
        float sum = 0;
        for (String value : sources) {
            sum += Generator.floatTryParse(value);
        }
        return String.valueOf(sum);
    }

    public static String getReportAve(List<String> sources) {
        float sum = 0;
        for (String value : sources) {
            sum += floatTryParse(value);
        }
        float ave = Math.round(Float.valueOf(sum) / sources.size() * 100) / 100f;
        return String.valueOf(ave);
    }

    public static String getPhoneShow(String input){
        input=getPhoneValue(input);
        Pattern pattern=Pattern.compile("(\\d{3})(\\d{4})(\\d{4})");
        Matcher matcher=pattern.matcher(input);
        if(matcher.find()){
            return String.format("%1$s %2$s %3$s",matcher.group(1),matcher.group(2),matcher.group(3));
        }else {
            return input;
        }
    }

    public static String getPhoneValue(String input){
        return input.replaceAll("\\D","");
    }
}
