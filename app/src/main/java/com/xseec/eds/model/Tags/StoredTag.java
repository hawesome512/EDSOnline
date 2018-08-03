package com.xseec.eds.model.Tags;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/7/10.
 */

public class StoredTag extends Tag {
    public enum DataType{LAST,MIN,MAX,AVG}
    public enum IntervalType{S,M,H,D}

    private DataType dataType;
    public StoredTag(String tagName,DataType dataType) {
        super(tagName);
        this.dataType=dataType;
    }

    @Override
    public String toString() {
        Context context= EDSApplication.getContext();
        return context.getString(R.string.was_storable_tag,getTagName(),dataType.ordinal());
    }
}
