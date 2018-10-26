package com.xseec.eds.util;

import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/7/12.
 * allTagList:工程所有点列表
 * filterTagList:模糊匹配，包含filter的点列表
 * filterDeviceTagList：精准匹配，在Device中查点列表
 * getBasicTagList：所有基本点
 * getStateList：所有State点
 * getAbnormalStateList:所有异常State点
 * getDeviceList：筛选出设备列表
 * getDeviceCount：计算设备数量
 * refreshOverviewTagsByTags：更新OverviewTags
 * getStateByTagList：获取工程状态
 */

public class TagsFilter {

    private static final String FILTER_AREA = "AREA";
    private static final String FILTER_STATE = "State";

    private static List<Tag> allTagList;

    public static List<Tag> getAllTagList() {
        return allTagList;
    }

    public static void setAllTagList(List<Tag> allTagList) {
        TagsFilter.allTagList = allTagList;
    }

    public static List<Tag> filterTagList(List<Tag> source, String... filters) {
        if (source == null) {
            source = allTagList;
        }
        List<Tag> target = new ArrayList<>();
        //此处嵌套顺序重要：保证返回List按filters匹配顺序进行
        for (String filter : filters) {
            for (Tag tag : source) {
                if (tag.getTagName().contains(filter)) {
                    target.add(tag);
                }
            }
        }
        return target;
    }

    public static List<Tag> filterDeviceTagList(List<Tag> source, String... filters) {
        List<Tag> target = new ArrayList<>();
        if (source != null) {
            //此处嵌套顺序重要：保证返回List按filters匹配顺序进行
            //使用equals精准匹配
            for (String filter : filters) {
                for (Tag tag : source) {
                    if (tag.getTagName().split(":")[1].equals(filter)) {
                        target.add(tag);
                    }
                }
            }
        }
        return target;
    }

    public static List getAbnormalStateList(List<Tag> source) {
        List<Tag> target = filterTagList(source, FILTER_STATE);
        for (int i = target.size() - 1; i >= 0; i--) {
            State state = State.getState(target.get(i).getTagValue());
            switch (state) {
                case OFF:
                case ON:
                    target.remove(i);
                    break;
                default:
                    break;
            }
        }
        return target;
    }

    public static List<Tag> getStateList(List<Tag> source) {
        return filterTagList(source, FILTER_STATE);
    }

    public static List<Tag> getBasicTagList(List<Tag> source) {
        List<Tag> target = new ArrayList<>();
        target.addAll(filterTagList(source, FILTER_AREA));
        target.addAll(filterTagList(source, FILTER_STATE));
        return target;
    }

    public static List<String> getDeviceList(List<Tag> source) {
        List<String> deviceList = new ArrayList<>();
        for (int i = 0; i < source.size(); i++) {
            String tagName = source.get(i).getTagName();
            String deviceName = tagName.split(":")[0];
            if (!deviceList.contains(deviceName)) {
                deviceList.add(deviceName);
            }
        }
        return deviceList;
    }

    public static int getDeviceCount(List<Tag> source) {
        List<String> deviceList = getDeviceList(source);
        if (deviceList.contains(FILTER_AREA)) {
            return deviceList.size() - 1;
        } else {
            return deviceList.size();
        }
    }

    public static void refreshOverviewTagsByTags(List<Tag> source, List<OverviewTag> target) {
        for (OverviewTag overviewTag : target) {
            for (int i = 0; i < source.size(); i++) {
                Tag tag = source.get(i);
                if (overviewTag.getMapTagName().equals(tag.getTagName())) {
                    overviewTag.setTagValue(tag.getTagValue());
                    break;
                }
            }
        }
    }

    public static State getStateByTagList(List<Tag> source) {
        List<Tag> filter = filterTagList(source, FILTER_STATE);
        List<State> stateList = new ArrayList<>();
        for (Tag tag : filter) {
            stateList.add(State.getState(tag.getTagValue()));
        }
        if (stateList.contains(State.ALARM)) {
            return State.ALARM;
        } else if (stateList.contains(State.OFFLINE)) {
            return State.OFFLINE;
        } else {
            return State.ON;
        }
    }
}
