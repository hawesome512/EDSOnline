package com.xseec.eds.util;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.model.Tags.OverviewTag;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Generator {

    public static void genOverviewTagList(List<OverviewTag> tagList){
        tagList.clear();
        Random random=new Random();
        tagList.add(new OverviewTag("环境温度",String.valueOf(random.nextInt(10)+20), R.drawable.circle_tempreture,"℃"));
        tagList.add(new OverviewTag("环境湿度",String.valueOf(random.nextInt(20)+40), R.drawable.circle_water,"%"));
        tagList.add(new OverviewTag("有功功率",String.valueOf(random.nextInt(60)+90), R.drawable.circle_line_2,"kW"));
        tagList.add(new OverviewTag("无功功率",String.valueOf(random.nextInt(10)+30), R.drawable.circle_line_4,"kVar"));
        tagList.add(new OverviewTag("今日用电",String.valueOf(random.nextInt(500)+1000), R.drawable.circle_light,"kW·h"));
        tagList.add(new OverviewTag("谐波监测","查看详情", R.drawable.circle_bar_hor,""));
    }


    public static String genString(String source, int repeat) {
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0;i<repeat;i++){
            stringBuilder.append(source);
        }
        return stringBuilder.toString();
    }

    public static String genTime(){
        Calendar calendar=Calendar.getInstance();
        return DateHelper.getString(calendar.getTime());
    }

    public static String[] getBasicInfo(){
        String[] basic=new String[5];
        Context context= EDSApplication.getContext();
        String[] status= context.getResources().getStringArray(R.array.overview_status);
        Random random=new Random();
        int index=random.nextInt(status.length);
        basic[0]=status[index];
        int deviceNumber=random.nextInt(50);
        basic[1]=context.getString(R.string.overview_device_value,deviceNumber);
        basic[2]=null;
        basic[3]="徐海生 18759282157";
        basic[4]="厦门市 集美区 孙坂南路92号";
        return basic;
    }

    public static int getScheduleImageRes(){
        Random random=new Random();
        int index=random.nextInt(8);
        Context context=EDSApplication.getContext();
        return context.getResources().getIdentifier("schedule_"+String.valueOf(index),"drawable",context.getPackageName());
    }
}
