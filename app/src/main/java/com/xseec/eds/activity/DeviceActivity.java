package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.xseec.eds.R;
import com.xseec.eds.adapter.MyFragmentPagerApdater;
import com.xseec.eds.fragment.ComFragment;
import com.xseec.eds.fragment.TabControlFragment;
import com.xseec.eds.fragment.TabMonitorFragment;
import com.xseec.eds.fragment.TabOerviewFragment;
import com.xseec.eds.fragment.TabProtectFragment;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.deviceconfig.DeviceConfig;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.ViewHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DeviceActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.tab)
    TabLayout tab;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;
    @InjectView(R.id.image_device)
    ImageView imageDevice;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    Fragment[] fragmentList;


    private static final String KEY_DEVICE_NAME = "device";

    public static void start(Context context, String deviceName) {
        Intent intent = new Intent(context, DeviceActivity.class);
        intent.putExtra(KEY_DEVICE_NAME, deviceName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        ButterKnife.inject(this);
        String tagName = getIntent().getStringExtra(KEY_DEVICE_NAME);
        Device device = Device.initWithTagName(tagName);
        DeviceConfig deviceConfig = getDeviceConfig(device);
        setTitle(device.getDeviceAlias());
        imageDevice.setImageResource(device.getDeviceResId());
        fragmentList = new Fragment[4];
        fragmentList[0] = (TabOerviewFragment.newInstance(device.getDeviceName(), deviceConfig
                .getOverviewZone()));
        fragmentList[1] = (TabMonitorFragment.newInstance(device.getDeviceName(),deviceConfig.getRealZone()));
        fragmentList[2] = (TabProtectFragment.newInstance(device.getDeviceName(),deviceConfig.getProtectZone()));
        fragmentList[3] = (TabControlFragment.newInstance(device.getDeviceName(),deviceConfig.getFarControl()));
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new MyFragmentPagerApdater(getSupportFragmentManager(), fragmentList));
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

    private DeviceConfig getDeviceConfig(Device device) {
        List<DeviceConfig> configs = WAServicer.getDeviceConfigs();
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getDeviceType().equals(device.name())) {
                return configs.get(i);
            }
        }
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        int index=viewPager.getCurrentItem();
        fragmentList[index].setUserVisibleHint(true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < fragmentList.length; i++) {
            if (i == position) {
                continue;
            }
            fragmentList[i].setUserVisibleHint(false);
        }
        fragmentList[position].setUserVisibleHint(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
