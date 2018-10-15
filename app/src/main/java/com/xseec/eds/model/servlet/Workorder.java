package com.xseec.eds.model.servlet;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.xseec.eds.R;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.WAJsonHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Workorder implements Comparable {

    /*
            state:0→未执行，1→已完成
            type:0→计划任务，1→异常维护，2→随工运维
         */
    public enum WorkorderState{DONE,DUE,OVERDUE}

    private String id;
    private int state;
    private int type;
    private String title;
    private String task;
    private Date start;
    private Date end;
    private String location;
    private String worker;
    private String log;
    private String image;
    private String creator;

    public Workorder(){}

    public Workorder(String id){
        this.id=id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getWorker() {
        return worker;
    }

    public void setWorker(String worker) {
        this.worker = worker;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }


    public void genId(String zoneId){
        this.id=zoneId+"-"+DateHelper.getNowForId();
    }

    public WorkorderState getWorkorderState(){
        if(state==1){
            return WorkorderState.DONE;
        }else {
            Date date=new Date();
            if(date.after(end)){
                return WorkorderState.OVERDUE;
            }else {
                return WorkorderState.DUE;
            }
        }
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

    public String toJson(){
        Context context= EDSApplication.getContext();
        return context.getString(R.string.svl_workorder_request,id,state,type,title,task,DateHelper.getString(start),DateHelper.getString(end),location,worker,log,image,creator);
    }


    @Override
    public int compareTo(@NonNull Object o) {
        Workorder target=(Workorder) o;
        if(getWorkorderState().ordinal()>target.getWorkorderState().ordinal()){
            return 1;
        }else if(getWorkorderState().ordinal()<target.getWorkorderState().ordinal()) {
            return -1;
        }else {
            if(end.before(target.getEnd())){
                return 1;
            }else if(end.after(target.getEnd())){
                return -1;
            }
        }
        return 0;
    }
}
