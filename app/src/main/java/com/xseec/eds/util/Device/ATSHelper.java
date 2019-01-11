package com.xseec.eds.util.Device;

import android.content.Context;
import android.text.TextUtils;

import com.xseec.eds.R;
import com.xseec.eds.model.State;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2018/12/29.
 */

public class ATSHelper {

    public static void convert(List<Tag> validTagList) {
        List<Tag> newTags=new ArrayList<>();
        for(Tag tag:validTagList){
            switch (tag.getTagShortName()){
                case "Phase":
                    tag.setTagValue(covPhase(tag));
                    break;
                default:
                    break;
            }
        }
        validTagList.addAll(newTags);
    }

    private static String covPhase(Tag source){
        String value=source.getTagValue();
        if(value.equals("73")){
            value="0";
        }else if(value.equals("70")){
            value="1";
        }
        return value;
    }

    public static Tag covCtrlMode(List<Tag> tagList){
        Tag source= TagsFilter.filterDeviceTagList(tagList, "Sw1").get(0);
        String tagValue=source.getTagValue();
        tagValue=Generator.checkIsOne(tagValue,6)?Protect.REMOTE:Protect.LOCAL;
        String tagName=source.getDeviceID()+Tag.SPIT_NAME+Protect.CTRL_MODE;
        return new Tag(tagName,tagValue);
    }

    public static Map<Integer,List<String>> genProtectCardMaps() {
        Map<Integer,List<String>> map=new LinkedHashMap<>();
        map.put(R.string.device_ATS_U,Arrays.asList("Ul","UlR","Uo","UoR"));
        map.put(R.string.device_ATS_F,Arrays.asList("Fl","FlR","Fo","FoR"));
        map.put(R.string.device_ATS_Up,Arrays.asList("UpSet","UpR"));
        map.put(R.string.device_ATS_T,Arrays.asList("T1","T2","T3","T4","T5"));
        return map;
    }

    public static State getState(int status) {
        if((status&18)==0){
            //常电[1]未投且备电[4]未投
            return State.OFF;
        }else if((status&36)>0){
            //常电[2]故障或备电[5]故障
            return State.ALARM;
        }else {
            //投常，投备
            return State.ON;
        }
    }
}
