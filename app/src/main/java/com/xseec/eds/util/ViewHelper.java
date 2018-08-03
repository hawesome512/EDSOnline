package com.xseec.eds.util;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.xseec.eds.R;

/**
 * Created by Administrator on 2018/7/25.
 */

public class ViewHelper {

    public static void initToolbar(AppCompatActivity activity,Toolbar toolbar,int homeRes) {
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(homeRes);
        }
    }
}
