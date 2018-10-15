package com.xseec.eds.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.xseec.eds.R;

/**
 * Created by Administrator on 2018/7/25.
 */

public class ViewHelper {

    public static final int DEFAULT_HOME_RES= R.drawable.ic_arrow_back_white_24dp;

    public static void initToolbar(AppCompatActivity activity, Toolbar toolbar, int homeRes) {
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(homeRes);
        }
    }

    public static boolean isPort() {
        Context context = EDSApplication.getContext();
        return context.getResources().getConfiguration().orientation == Configuration
                .ORIENTATION_PORTRAIT;
    }

    public static void lockOrientation(Activity activity){
        int orientation=activity.getResources().getConfiguration().orientation;
        if(orientation==Configuration.ORIENTATION_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public static String getStringByName(String name){
        Context context=EDSApplication.getContext();
        int resId=context.getResources().getIdentifier(name,"string",context.getPackageName());
        if(resId!=0){
            return context.getString(resId);
        }else {
            return null;
        }
    }

    public static void startViewAnimator(View view) {
        ViewPropertyAnimator animator = view.animate();
        float scaleValue = view.getHeight() * 1.0f / view.getWidth();
        animator.scaleX(scaleValue);
        animator.alpha(0f);
        animator.setDuration(500);
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.start();
    }

    public static void resetViewAnimator(View view) {
        view.setScaleX(1f);
        view.setAlpha(1f);
    }

}
