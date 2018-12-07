package com.xseec.eds.model.tags;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/12/3.
 * TagName→XRD:E_0_xxx           第一级
 *               E_00_xxx        第二级
 *                 E_000_xxx     第三级
 */

public class EnergyTag extends Tag {
    private static final String SPIT = "_";

    public EnergyTag(String tagName) {
        super(tagName);
    }

    public EnergyTag(String tagName, String tagValue) {
        super(tagName, tagValue);
    }

    protected EnergyTag(Parcel in) {
        super(in);
    }

    @Override
    public String getTagShortName() {
        String[] items = getTagName().split(SPIT);
        return items[2];
    }

    public String getSerial() {
        String[] items = getTagName().split(SPIT);
        return items[1];
    }

    public List<EnergyTag> getEnergyChildren(List<EnergyTag> source) {
        List<EnergyTag> target = new ArrayList<>();
        String regex = SPIT + getSerial() + "\\d" + SPIT;
        Pattern pattern = Pattern.compile(regex);
        for (EnergyTag tag : source) {
            if (pattern.matcher(tag.getTagName()).find()) {
                target.add(tag);
            }
        }
        return target;
    }

    public List<EnergyTag> getEnergyParents(List<EnergyTag> source) {
        List<EnergyTag> target = new ArrayList<>();
        String serial = getSerial();
        Pattern pattern;
        while (serial.length() > 0) {
            pattern = Pattern.compile(SPIT + serial + SPIT);
            for (EnergyTag tag : source) {
                if (pattern.matcher(tag.getTagName()).find()) {
                    target.add(0, tag);
                }
            }
            serial = serial.substring(0, serial.length() - 1);
        }
        return target;
    }
}
