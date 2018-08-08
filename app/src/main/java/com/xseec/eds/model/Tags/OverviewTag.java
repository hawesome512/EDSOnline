package com.xseec.eds.model.Tags;

import android.support.annotation.NonNull;

import org.litepal.crud.DataSupport;
import org.litepal.crud.LitePalSupport;

/**
 * Created by Administrator on 2018/7/25.
 */

public class OverviewTag extends LitePalSupport {

    private int index;
    private int tagResId;
    private String tagName;
    private String tagValue;
    private String tagUnit;
    private String mapTagName;
    private boolean isDefault;

    public OverviewTag(@NonNull int index, @NonNull int tagResId,@NonNull String tagName, String tagValue, String tagUnit, String
            mapTagName,boolean isDefault) {
        this.index=index;
        this.tagResId = tagResId;
        this.tagName = tagName;
        this.tagValue = tagValue;
        this.tagUnit = tagUnit;
        this.mapTagName = mapTagName;
        this.isDefault=isDefault;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getTagResId() {
        return tagResId;
    }

    public void setTagResId(int tagResId) {
        this.tagResId = tagResId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getTagValue() {
        return tagValue;
    }

    public void setTagValue(String tagValue) {
        this.tagValue = tagValue;
    }

    public String getTagUnit() {
        return tagUnit;
    }

    public void setTagUnit(String tagUnit) {
        this.tagUnit = tagUnit;
    }

    public String getMapTagName() {
        return mapTagName;
    }

    public void setMapTagName(String mapTagName) {
        this.mapTagName = mapTagName;
    }
}
