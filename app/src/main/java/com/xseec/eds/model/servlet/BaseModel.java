package com.xseec.eds.model.servlet;

import android.text.TextUtils;

import com.xseec.eds.util.DateHelper;

/**
 * Created by Administrator on 2018/10/29.
 */

public abstract class BaseModel {
    //使用英文“;”作为分隔符
    public static final String SPIT = ";";
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void genId(String zoneId) {
        this.id = zoneId + "-" + DateHelper.getNowForId();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        if (TextUtils.isEmpty(id)) {
            return null;
        } else {
            stringBuilder.append("id=" + id);
        }
        return stringBuilder.toString();
    }

    public abstract String toJson();
}
