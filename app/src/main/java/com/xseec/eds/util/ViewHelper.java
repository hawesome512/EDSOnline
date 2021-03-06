package com.xseec.eds.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.TextView;

import com.xseec.eds.R;

/**
 * Created by Administrator on 2018/7/25.
 */

public class ViewHelper {

    public static final int CHART_ANIMATE_DURATION=2000;
    public static final int ANIMATION_DURATION=500;

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

    public static void checkExit(final Activity activity,String info,DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        String[] infos=info.split("/");
        builder.setMessage(infos[0]);
        if(listener==null){
            listener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    activity.finish();
                }
            };
        }
        builder.setPositiveButton(infos[1], listener);
        builder.setNegativeButton(infos[2],null);
        AlertDialog dialog=builder.show();
        int gray=ContextCompat.getColor(activity,R.color.colorGrayNormal);
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(gray);
    }

    //nj--添加单按钮对话框
    public static void singleAlertDialog(final Activity activity,String info,DialogInterface.OnClickListener listener){
        String[] infos=info.split( "/" );
        AlertDialog.Builder builder=new AlertDialog.Builder( activity )
                .setTitle( infos[0])
                .setMessage(infos[1])
                .setNegativeButton( infos[2], listener);
        if (listener==null){
            listener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            };
        }
        builder.setCancelable(false);
        builder.show();
    }

    public static void lockOrientation(Activity activity){
        int orientation=activity.getResources().getConfiguration().orientation;
        if(orientation==Configuration.ORIENTATION_PORTRAIT){
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }else {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        }
    }

    public static void startViewAnimator(View... views) {
        for (View view : views) {
            ViewPropertyAnimator animator = view.animate();
            float scaleValue = view.getHeight() * 1.0f / view.getWidth();
            animator.scaleX(scaleValue);
            animator.alpha(0f);
            animator.setDuration(ANIMATION_DURATION);
            animator.setInterpolator(new FastOutSlowInInterpolator());
            animator.start();
        }
    }

    public static void resetViewAnimator(View... views) {
        for (View view : views) {
            //startAnimator执行时长500ms,必须确保重置发生在动画执行完成之前
            view.setVisibility(View.VISIBLE);
            view.setScaleX(1f);
            view.setAlpha(1f);
            view.postInvalidate();
        }
    }

    public static void drawTextBounds(TextView view,int start,int top,int end,int bottom){
        if (ApiLevelHelper.isAtLeast(17)) {
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, end, bottom);
        }
    }

}
