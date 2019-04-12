package com.xseec.eds.model;

import android.content.Context;

import com.xseec.eds.R;

//用户管理员；电话管理员；电话访客；电话临时访客；超级管理员
public enum UserType {
    USER_ADMIN(R.string.user_admin,R.drawable.ic_account_admin),
    TEL_ADMIN(R.string.user_phone_admin,R.drawable.ic_user_tel_admin),
    TEL_GUEST(R.string.user_phone,R.drawable.ic_user_normal),
    TEL_TEMP(R.string.user_phone_temp,R.drawable.ic_user_temp),
    SUPER_ADMIN(R.string.user_admin_super,R.drawable.ic_user_manager);
    private int nameRes;
    private int iconRes;
    private UserType(int nameRes,int iconRes){
        this.nameRes=nameRes;
        this.iconRes=iconRes;
    }

    public static UserType initWithIndex(int index){
        return UserType.values()[index];
    }

    public int getNameRes() {
        return nameRes;
    }

    public int getIconRes() {
        return iconRes;
    }

    public String getTypeName(Context context){
        return context.getString(nameRes);
    }
}