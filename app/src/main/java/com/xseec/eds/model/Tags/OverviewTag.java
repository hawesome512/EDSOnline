package com.xseec.eds.model.Tags;

/**
 * Created by Administrator on 2018/7/25.
 */

public class OverviewTag extends Tag {

    private int tagResId;
    private String tagUnit;

    public OverviewTag(String tagName, String tagValue, int tagResId, String tagUnit) {
        super(tagName, tagValue);
        this.tagResId = tagResId;
        this.tagUnit = tagUnit;
    }

    public int getTagResId() {
        return tagResId;
    }

    public String getTagUnit() {
        return tagUnit;
    }
}
