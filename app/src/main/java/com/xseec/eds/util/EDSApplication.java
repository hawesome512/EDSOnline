package com.xseec.eds.util;

import android.app.Application;
import android.content.Context;

import com.xseec.eds.model.WAServicer;

/**
 * Created by Administrator on 2018/7/9.
 */

public class EDSApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        //与WAServicer互为耦合，不是良好的设计
        WAServicer.initWAServicer();
    }

    public static Context getContext(){
        return context;
    }
}
