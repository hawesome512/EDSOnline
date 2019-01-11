package com.xseec.eds.util.Device;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/1/2.
 */

public class DeviceConverterCenter {

    private static final String CTRL_MODE = "CtrlMode";
    private static final String CTRL_CODE = "CtrlCode";

    public static void convert(List<Tag> tagList){
        switch (getDevice(tagList)){
            case T3:
                ATSHelper.convert(tagList);
                break;
            default:
                break;
        }
    }

    private static Device getDevice(List<Tag> tagList){
        String tagName=tagList.get(0).getTagName();
        return Device.initWithTagName(tagName);
    }

    public static List<Tag> getCtrlList(List<Tag> tagList){
        //注意List优先次序：0→模式；1→密码
        List<Tag> results=new ArrayList<>();
        switch (getDevice(tagList)){
            case T3:
                results.add(ATSHelper.covCtrlMode(tagList));
                results.addAll(TagsFilter.filterDeviceTagList(tagList, CTRL_CODE));
                break;
            default:
                results= TagsFilter.filterDeviceTagList(tagList, CTRL_MODE, CTRL_CODE);
                break;
        }
        return results;
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
        switch (Device.initWithTagName(statusTag.getTagName())){
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
        switch (Device.initWithTagName(statusTag.getTagName())){
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
