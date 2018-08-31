package com.xseec.eds.model.tags;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/7/10.
 */

public class ValidTag extends Tag {

    public ValidTag(String tagName, String tagValue) {
        super(tagName, tagValue);
    }

    @Override
    public String toString() {
        Context context= EDSApplication.getContext();
        return context.getString(R.string.was_writable_tag,getTagName(),getTagValue());
    }
}
