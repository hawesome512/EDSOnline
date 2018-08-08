package com.xseec.eds.util;

import com.xseec.eds.model.State;
import com.xseec.eds.model.Tags.OverviewTag;
import com.xseec.eds.model.Tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/12.
 */

public class TagsFilter {

    private static final String FILTER_AREA="AREA";
    private static final String FILTER_STATE="State";
    private static final String STATE_OFF="0";
    private static final String STATE_OFFLINE="-1";
    private static final String STATE_ALARM="6";
    private static final String STATE_ON="7";

    public static List<Tag> filterTagList(List<Tag> source, String filter) {
        List<Tag> target = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            Tag tag = source.get(i);
            if (tag.getTagName().contains(filter)) {
                target.add(tag);
            }
        }
        return target;
    }

    public static List<Tag> getBasicTagList(List<Tag> source){
        List<Tag> target=new ArrayList<>();
        target.addAll(filterTagList(source,FILTER_AREA));
        target.addAll(filterTagList(source,FILTER_STATE));
        return target;
    }

    public static List<String> getDeviceList(List<Tag> source){
        List<String> deviceList=new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            String tagName=source.get(i).getTagName();
            String deviceName=tagName.split(":")[0];
            if(!deviceList.contains(deviceName)){
                deviceList.add(deviceName);
            }
        }
        return deviceList;
    }

    public static int getDeviceCount(List<Tag> source){
        List<String> deviceList=getDeviceList(source);
        if(deviceList.contains(FILTER_AREA)){
            return deviceList.size()-1;
        }else {
            return deviceList.size();
        }
    }

    public static void refreshOverviewTagsByTags(List<Tag> source,List<OverviewTag> target){
        for (OverviewTag overviewTag:target) {
            for (int i = 0; i <source.size() ; i++) {
                Tag tag=source.get(i);
                if(overviewTag.getMapTagName().equals(tag.getTagName())){
                    overviewTag.setTagValue(tag.getTagValue());
                    break;
                }
            }
        }
    }

    public static State getStateByTagList(List<Tag> source){
        List<Tag> filter=filterTagList(source,FILTER_STATE);
        List<String> stateList=new ArrayList<>();
        for (Tag tag:source) {
            stateList.add(tag.getTagValue());
        }
        if(stateList.contains(STATE_ALARM)){
            return State.ALARM;
        }else if(stateList.contains(STATE_OFFLINE)){
            return State.OFFLINE;
        }else {
            return State.ON;
        }
    }
}
