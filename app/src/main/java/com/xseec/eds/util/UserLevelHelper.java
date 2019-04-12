package com.xseec.eds.util;

import android.view.MenuItem;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.model.Function;
import com.xseec.eds.model.User;
import com.xseec.eds.model.UserType;
import com.xseec.eds.model.WAServicer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserLevelHelper {

    /*
       用户权限功能控制
       checkCustom:用户自定义权限检测
       checkTabFragment:设备参数修改与远程控制
       checkWorkorderListFragment:工单创建功能
       checkWorkorder:工单删除和工单修改
       checkAlarmAdapter:异常管理
     */

    public static boolean checkTabFragment(){
        return checkAuthority(UserType.USER_ADMIN,UserType.TEL_ADMIN);
    }

    public static boolean checkCustom(){
        return checkAuthority(UserType.USER_ADMIN,UserType.TEL_ADMIN);
    }

    public static void checkWorkorderActivity(View view,MenuItem menuItem){
        boolean visible=checkAuthority(UserType.USER_ADMIN,UserType.TEL_ADMIN);
        setVisibility(view,visible);
        menuItem.setVisible(visible);
    }

    public static void checkWorkorderListFragment(View view){
        setVisibility(view,checkAuthority(UserType.USER_ADMIN,UserType.TEL_ADMIN));
    }

    public static void checkAlarmAdapter(View view){
        setVisibility(view,checkAuthority(UserType.USER_ADMIN,UserType.TEL_ADMIN));
    }

    private static boolean checkAuthority(UserType... userTypes){
        UserType userType=WAServicer.getUser().getUserType();
        return Arrays.asList(userTypes).contains(userType);
    }

    private static void setVisibility(View view,boolean visiable){
        view.setVisibility(visiable?View.VISIBLE:View.GONE);
    }
}
