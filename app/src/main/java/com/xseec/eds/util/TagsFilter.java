package com.xseec.eds.util;

import com.xseec.eds.model.Tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/12.
 */

public class TagsFilter {

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
}
