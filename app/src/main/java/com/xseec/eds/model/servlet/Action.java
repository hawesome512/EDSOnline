package com.xseec.eds.model.servlet;


import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.xseec.eds.R;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Date;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Action extends BaseModel implements Comparable{

    private String user;
    @SerializedName("action")
    private String info;
    private Date time;

    public Action(){}

    public Action(String id){
        this.id=id;
    }

    /*
    nj--设置操作记录种类的枚举变量：NORMAL-登录；IMPORTANT-工单；SENIOR-设备；OTHER-其他测试
    2018/11/03
    * */
    private enum ActionType{
        LOGIN,WORKORDER,DEVICE,OTHER
    }

    /*
    nj--设置操作类型所使用的操作方式枚举变量
    工单操作：S-工单创建；H-工单执行；L：工单删除 M:暂定
    设备操作:S-参数修改；H-远程合闸；L-远程分闸  M:暂定 2018/11/06
     */
    private enum ActionMethod{S,H,L,M}

    //nj--获取操作记录类别别信息 2018/11/9
    private ActionType getActionType(){
        String[] temps=info.split( "_" );
            if (temps[0].equals( "Login" )){
                return ActionType.LOGIN;
            } else if (temps[0].equals( "WorkOrder" )){
                return ActionType.WORKORDER;
            } else if (temps[0].equals( "Device" )){
                return ActionType.DEVICE;
            } else {
                return ActionType.OTHER;
            }
    }

    //nj--获取操作记录类别方法 2018/11/9
    private ActionMethod getActionMethod(){
        String[] temps=info.split( "_" );
        if(temps[0]!=null&&temps.length==3){
            if (temps[1].equals( "1" )){
                return ActionMethod.S;
            } else if(temps[1].equals( "2" )){
                return ActionMethod.H;
            } else if (temps[1].equals( "3" )){
                return ActionMethod.L;
            } else {
                return ActionMethod.M;
            }
        }else {
         return ActionMethod.M;
        }
    }

    //nj--加载图标
    public int getImageType(){
        switch (getActionType()){
            case LOGIN:
                    return R.drawable.ic_action_login_grey_300_24dp;
            case WORKORDER:
                    return R.drawable.ic_event_note_white_24dp;
            case DEVICE:
                switch (getActionMethod()){
                    case S:return R.drawable.ic_action_device_yellow_700_24dp;
                    case H:return R.drawable.ic_action_close_yellow_700_24dp;
                    case L:return R.drawable.ic_action_open_yellow_700_24dp;
                    default:break;
                }
            default:
                return 0;
        }
    }

    //nj--筛选Info信息 2018/11/6
    public String getActionInfo(){
        String[] temps=info.split( "_" );
        if(temps.length==3){
            return temps[2];
        }else {return info;}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String device) {
        this.user = device;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void genId(String zoneId){
        this.id=zoneId+"-"+DateHelper.getNowForId();
    }

    //NJ--排序 2018/11/6
    @Override
    public int compareTo( Object o) {
        Action target=(Action)o;
        if (time.before( target.getTime())){
            return -1;
        }
        if (time.after( target.getTime() )){
            return 1;
        }else {
            return 0;
        }
    }

    @Override
    public String toJson() {
        Context context = EDSApplication.getContext();
        return context.getString(R.string.svl_action_request, id, user, info, DateHelper.getString(time));
    }
}
