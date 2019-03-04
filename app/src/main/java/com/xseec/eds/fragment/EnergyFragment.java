package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xseec.eds.R;
import com.xseec.eds.adapter.MyFragmentPagerAdapter;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;

import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class EnergyFragment extends BaseFragment implements ViewPager.OnPageChangeListener {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.tab)
    TabLayout tab;
    @InjectView(R.id.view_pager)
    ViewPager viewPager;

    Fragment[] fragments=new Fragment[3];
    String[] tabs;
    public static final String KEY_ENERGY_INFO="info";

    public static Fragment newInstance(String info) {
        Fragment fragment = new EnergyFragment();
        Bundle bundle=new Bundle();
        bundle.putString(KEY_ENERGY_INFO,info);
        fragment.setArguments(bundle);
        return fragment;
    }


    public EnergyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_energy, container, false);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(),toolbar,R.drawable.menu);
        toolbar.setTitle(R.string.nav_energy);
        String info=getArguments().getString(KEY_ENERGY_INFO);
        fragments[0]=EnergyTabItemFragment.newInstance(Calendar.DATE,info);
        fragments[1]=EnergyTabItemFragment.newInstance(Calendar.MONTH,info);
        fragments[2]=EnergyTabItemFragment.newInstance(Calendar.YEAR,info);
        viewPager.addOnPageChangeListener(this);
        viewPager.setAdapter(new MyFragmentPagerAdapter(getActivity().getSupportFragmentManager(),fragments));
        tab.setupWithViewPager(viewPager);
        tabs=getResources().getStringArray(R.array.energy_tags);
        for(int i=0;i<tab.getTabCount();i++){
            tab.getTabAt(i).setText(tabs[i]);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < fragments.length; i++) {
            if (i == position) {
                continue;
            }
            fragments[i].setUserVisibleHint(false);
        }
        fragments[position].setUserVisibleHint(true);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }
}
