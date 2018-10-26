package com.xseec.eds.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/8/24.
 * 如何让ViewPager+Fragment来回切换时数据不重复加载
 * https://blog.csdn.net/ForgetFormerly/article/details/51701133
 * 解决：缓存过大、频繁加载Fragment的问题
 */

public class MyFragmentPagerAdapter extends PagerAdapter {
    private Fragment[] fragments;
    private FragmentManager manager;

    public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super();
        manager=fm;
        this.fragments = fragments;
    }


    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = fragments[position];
        //判断当前的fragment是否已经被添加进入Fragmentanager管理器中
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(fragment, fragment.getClass().getSimpleName());
            //不保存系统参数，自己控制加载的参数
            transaction.commitAllowingStateLoss();
            //手动调用,立刻加载Fragment片段
            manager.executePendingTransactions();
        }
        if (fragment.getView().getParent() == null) {
            //添加布局
            container.addView(fragment.getView());
        }
        return fragment.getView();
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //移除布局
        container.removeView(fragments[position].getView());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
