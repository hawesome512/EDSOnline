package com.xseec.eds.util;

import android.content.Context;

import com.xseec.eds.R;
import com.xseec.eds.model.Tags.OverviewTag;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2018/7/25.
 */

public class Generator {

    public static void initOverviewTagStore(){
        LitePal.getDatabase();
        Context context=EDSApplication.getContext();
        String name=context.getString(R.string.overview_item_tempreture);
        OverviewTag tag0=new OverviewTag(0,R.drawable.circle_tempreture,name,null,"℃","AREA:Tempreture",true);
        tag0.save();

        name=context.getString(R.string.overview_item_humidity);
        OverviewTag tag1=new OverviewTag(1,R.drawable.circle_water,name,null,"%","AREA:Humidity",true);
        tag1.save();

        name=context.getString(R.string.overview_item_activie_power);
        OverviewTag tag2=new OverviewTag(2,R.drawable.circle_line_2,name,null,"kW","AREA:P",true);
        tag2.save();

        name=context.getString(R.string.overview_item_reactive_power);
        OverviewTag tag3=new OverviewTag(3,R.drawable.circle_line_4,name,null,"kVar","AREA:Q",true);
        tag3.save();

        name=context.getString(R.string.overview_item_energy);
        OverviewTag tag4=new OverviewTag(4,R.drawable.circle_light,name,null,"kW·h","AREA:Energy",true);
        tag4.save();

        name=context.getString(R.string.overview_item_harmonic);
        String valueText=context.getString(R.string.overview_item_detail);
        OverviewTag tag5=new OverviewTag(5,R.drawable.circle_bar_hor,name,valueText,null,"AREA:Harmonic",true);
        tag5.save();
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
