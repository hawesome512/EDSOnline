package com.xseec.eds.model.tags;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.util.EDSApplication;

/**
 * Created by Administrator on 2018/7/10.
 */

public class StoredTag extends Tag{

    //Units Types选项顺序应严格与StoredTag Enum顺序一致,对应WebAccess说明文档【0-Last,1-Min,2-Max,3-Avg】
    //但是经实际测试，文档中Max与Min的数据正好相反，故调整1-Max,2-Min
    public static final String NULL_VALUE="#";
    public enum DataType{LAST,MAX,MIN,AVG}
    public enum IntervalType{S,M,H,D}

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

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
