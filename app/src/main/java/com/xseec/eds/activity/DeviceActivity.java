package com.xseec.eds.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.xseec.eds.R;
import com.xseec.eds.adapter.MyFragmentPagerApdater;
import com.xseec.eds.fragment.TabControlFragment;
import com.xseec.eds.fragment.TabMonitorFragment;
import com.xseec.eds.fragment.TabOerviewFragment;
import com.xseec.eds.fragment.TabProtectFragment;
import com.xseec.eds.util.ViewHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeviceActivity extends AppCompatActivity {

    @InjectView(R.id.tab)
    TabLayout tab;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    Fragment[] fragmentList;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.inject(this);
        setTitle("ACB (RD#2)");
        fragmentList = new Fragment[4];
        fragmentList[0]=(new TabOerviewFragment());
        fragmentList[1]=(new TabMonitorFragment());
        fragmentList[2]=(new TabProtectFragment());
        fragmentList[3]=(new TabControlFragment());
        viewPager.setAdapter(new MyFragmentPagerApdater(getSupportFragmentManager(), fragmentList));
        //viewPager.setOffscreenPageLimit(3);
        ViewHelper.initToolbar(this, toolbar, ViewHelper.DEFAULT_HOME_RES);
        tab.setupWithViewPager(viewPager);
        int[] res = {R.drawable.tab_overview, R.drawable.tab_monitor, R.drawable.tab_param, R
                .drawable.tab_control};
        for (int i = 0; i < tab.getTabCount(); i++) {
            ImageView image = new ImageView(this);
            image.setImageResource(res[i]);
            tab.getTabAt(i).setCustomView(image);
        }
    }
}
