package com.xseec.eds.util.Device;

import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/1/2.
 */

public class DeviceConverterCenter {
    /* 备忘录
     * A3控制位：对应modbus地址0x4000,开关位：1
     * T3控制位：对应modbus地址0x2000,开关位：6
     * 相序统一：0→逆序，1→正序，2→断相，参照A3为准，T3需转换
     * 电网状态位：A/M/T各不同，同类别尽量统一
     * 设备图片：M2各框架使用不同图片
     */

    private static final String CTRL_MODE = "CtrlMode";
    private static final String CTRL_CODE = "CtrlCode";

    public static void convert(List<Tag> tagList){
        Device device=getDevice(tagList);
        if(device==null){
            return;
        }
        switch (device){
            case A3:
                ACBHelper.convertA3(tagList);
                break;
            case T3:
                ATSHelper.convert(tagList);
                break;
            default:
                break;
        }
    }

    public static Device initWith(String tagName){
        Device device= Device.initWith(tagName);
        if(device==null){
            return null;
        }
        switch (device){
            case M2:
                return MCCBHelper.convertDevice(device);
            default:
                return device;
        }
    }

    private static Device getDevice(List<Tag> tagList){
        String tagName=tagList.get(0).getTagName();
        return Device.initWith(tagName);
    }

    public static List<Tag> getCtrlList(List<Tag> tagList){
        //注意List优先次序：0→模式；1→密码
        return TagsFilter.filterDeviceTagList(tagList, CTRL_MODE, CTRL_CODE);
    }

    public static Map<Integer,List<String>> genProtectCardMaps(List<Tag> tagList){
        switch (getDevice(tagList)){
            case T3:
                return ATSHelper.genProtectCardMaps();
            default:
                return genDefaultProtectCardMaps();
        }
    }

    private static Map<Integer,List<String>> genDefaultProtectCardMaps() {
        Map<Integer,List<String>> cards=new LinkedHashMap<>();
        cards.put(R.string.device_long, Arrays.asList("Ir", "Tr"));
        cards.put(R.string.device_short, Arrays.asList("Isd", "Tsd"));
        cards.put(R.string.device_instant, Arrays.asList("Ii"));
        cards.put(R.string.device_ground, Arrays.asList("Ig", "Tg"));
        return cards;
    }


    public static State getState(Tag statusTag){
        int status=Integer.valueOf(statusTag.getTagValue());
        if(status==-1){
            return State.OFFLINE;
        }
        switch (Device.initWith(statusTag.getTagName())){
            case A1:
            case A2:
            case A3:
                return ACBHelper.getState(status);
            case M1:
            case M2:
                return MCCBHelper.getState(status);
            case T3:
                return ATSHelper.getState(status);
            default:
                return State.OFFLINE;
        }
    }

    public static String getStateText(Tag statusTag){
        State state=getState(statusTag);
        if(state==State.OFFLINE){
            return state.getStateText();
        }
        switch (Device.initWith(statusTag.getTagName())){
            case T3:
                return Generator.getAlarmStateTextWithTag(statusTag);
            default:
                if (state == State.ALARM) {
                    return Generator.getAlarmStateTextWithTag(statusTag);
                } else {
                    return state.getStateText();
                }
        }
    }

}
