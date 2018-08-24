package com.xseec.eds.util;

import android.content.Context;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.databinding.ItemCardSubBinding;
import com.xseec.eds.model.Tags.Tag;

import java.util.List;

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
}
