package com.xseec.eds.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * Created by Administrator on 2018/8/20.
 * 此方法将频繁触发Fragment重新加载，优化为MyFragmentPagerAdapter
 */

public class DevicePagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList;

    public DevicePagerAdapter(FragmentManager fm,List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList=fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
