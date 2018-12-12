package com.xseec.eds.model.servlet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.xseec.eds.R;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.Date;

/**
 * Created by Administrator on 2018/10/8.
 */

public class Workorder extends BaseModel implements Comparable,Parcelable {

    protected Workorder(Parcel in) {
        id = in.readString();
        state = in.readInt();
        type = in.readInt();
        title = in.readString();
        task = in.readString();
        start=DateHelper.getDate(in.readString());
        end=DateHelper.getDate(in.readString());
        location = in.readString();
        worker = in.readString();
        log = in.readString();
        image = in.readString();
        creator = in.readString();
    }

    public static final Creator<Workorder> CREATOR = new Creator<Workorder>() {
        @Override
        public Workorder createFromParcel(Parcel in) {
            return new Workorder(in);
        }

        @Override
        public Workorder[] newArray(int size) {
            return new Workorder[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(state);
        dest.writeInt(type);
        dest.writeString(title);
        dest.writeString(task);
        dest.writeString(DateHelper.getString(start));
        dest.writeString(DateHelper.getString(end));
        dest.writeString(location);
        dest.writeString(worker);
        dest.writeString(log);
        dest.writeString(image);
        dest.writeString(creator);
    }

    /*
                state:0→未执行，1→已完成
                type:0→计划任务，1→异常维护，2→随工运维
             */
    public enum WorkorderState {
        DONE, DUE, OVERDUE
    }

    private int state;
    private int type;
    private String title;
    private String task;
    private Date start;
    private Date end;
    private String location;
    private String worker;
    private String image;

    private String log;
    private String creator;

    public Workorder() {
    }

    public Workorder(String id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setStateDone(){
        this.state=1;
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public WorkorderState getWorkorderState() {
        if (state == 1) {
            return WorkorderState.DONE;
        } else {
            Date date = new Date();
            if (date.after(end)) {
                return WorkorderState.OVERDUE;
            } else {
                return WorkorderState.DUE;
            }
        }
    }

    public int getStateImgRes() {
        switch (getWorkorderState()) {
            case DUE:
                return R.drawable.ic_access_time_grey_600_24dp;
            case DONE:
                return R.drawable.ic_done_blue_500_24dp;
            default:
                return R.drawable.ic_warning_yellow_24dp;
        }
    }

    public String getTypeString() {
        return EDSApplication.getContext().getResources().getStringArray(R.array.workorder_types)
                [getType()];
    }

    public String getDateRange() {
        return EDSApplication.getContext().getString(R.string.workorder_time, DateHelper
                .getMDString(start), DateHelper.getMDString(end));
    }

    public int getStateTextRes() {
        switch (getWorkorderState()) {
            case DUE:
                return R.string.workorder_due;
            case DONE:
                return R.string.workorder_done;
            default:
                return R.string.workorder_overdue;
        }
    }

    public String toShare(){

        Context context = EDSApplication.getContext();
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(context.getString(R.string.app_name)+"\n");
        stringBuilder.append("▶"+context.getString(R.string.nav_workorder)+":"+title+"\n");
        stringBuilder.append("▶"+context.getString(getStateTextRes())+"\n");
        if(getWorkorderState()==WorkorderState.DONE){
            stringBuilder.append("▶"+context.getString(R.string.workorder_log)+":\n"+getShowString(log));
        }else {
            stringBuilder.append("▶"+context.getString(R.string.workorder_task)+":\n"+getShowString(task)+"\n");
            stringBuilder.append("▶"+context.getString(R.string.workorder_range)+":"+getDateRange());
        }
        return stringBuilder.toString();
    }

    @Override
    public String toJson() {
        Context context = EDSApplication.getContext();
        return context.getString(R.string.svl_workorder_request, id, state, type, title, task,
                DateHelper.getString(start), DateHelper.getString(end), location, worker, log,
                image, creator);
    }


    @Override
    public int compareTo(@NonNull Object o) {
        Workorder target = (Workorder) o;
        if (getWorkorderState().ordinal() > target.getWorkorderState().ordinal()) {
            return 1;
        } else if (getWorkorderState().ordinal() < target.getWorkorderState().ordinal()) {
            return -1;
        } else {
            if (end.before(target.getEnd())) {
                return 1;
            } else if (end.after(target.getEnd())) {
                return -1;
            }
        }
        return 0;
    }

    //换行符“\n"传输至服务器被删除，先替换为“；”在上传
    public static String getServletString(String source){
        if(source==null){
            return null;
        }
        return source.replaceAll("^\n+","").replaceAll("\n+$","").replaceAll("\n+",SPIT);
    }

    //替换分隔符“；”改为换行符
    public static String getShowString(String source){
        if(source==null){
            return null;
        }
        return source.replaceAll("^;+","").replaceAll(";+$","").replaceAll(";+","\n");
    }
}
