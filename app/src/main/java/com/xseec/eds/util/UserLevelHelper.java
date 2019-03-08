package com.xseec.eds.util;

import android.view.MenuItem;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.model.Function;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;

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

    //nj--获取用户权限后，功能显示与隐藏的标志列表  {0:管理员、1:普通用户、2:运维人员}
    private static Map<Integer,Boolean > getLevelMap(boolean operatorEnable){
        Map<Integer,Boolean> levelMap=new HashMap<>(  );
        levelMap.put( User.LEVEL_ADMIN,true );
        levelMap.put( User.LEVEL_NORMAL,false );
        levelMap.put( User.LEVEL_OPERATOR,operatorEnable );
        return levelMap;
    }

    private static void setVisibilityForView(View view,boolean isVisible){
        int visibility=isVisible?View.VISIBLE:View.GONE;
        view.setVisibility( visibility );
    }

    private static void setVisibilityForMenuItem(MenuItem menuItem,boolean isVisible){
        menuItem.setVisible( isVisible );
    }

    private static void setWorkorderDelete(MenuItem menuItem){
        Map<Integer,Boolean> usability=getLevelMap(false);
        int level=WAServicer.getUser().getLevel();
        setVisibilityForMenuItem( menuItem,usability.get( level ) );
    }

    private static void setWorkorderUpdate(View view){
        Map<Integer,Boolean> usability=getLevelMap(true);
        int level=WAServicer.getUser().getLevel();
        setVisibilityForView( view,usability.get( level ) );
    }

    public static boolean checkTabFragment(){
        Map<Integer,Boolean> usability=getLevelMap( true );
        int level=WAServicer.getUser().getLevel();
        return usability.get( level );
    }

    public static boolean checkCustom(){
        Map<Integer,Boolean> usability=getLevelMap( false );
        int level=WAServicer.getUser().getLevel();
        return usability.get( level );
    }

    public static void checkWorkorderActivity(View view,MenuItem menuItem){
        setWorkorderUpdate( view );
        setWorkorderDelete( menuItem );
    }

    public static void checkWorkorderListFragment(View view){
        Map<Integer,Boolean> usability=getLevelMap(false);
        int level= WAServicer.getUser().getLevel();
        setVisibilityForView( view,usability.get( level ) );
    }

    public static void checkAlarmAdapter(View view){
        Map<Integer,Boolean> usability=getLevelMap(true);
        int level= WAServicer.getUser().getLevel();
        setVisibilityForView( view,usability.get( level ) );
    }
}
