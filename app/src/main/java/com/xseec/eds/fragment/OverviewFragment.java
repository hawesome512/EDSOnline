package com.xseec.eds.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xseec.eds.R;
import com.xseec.eds.activity.DeviceListActivity;
import com.xseec.eds.adapter.OverviewAdapter;
import com.xseec.eds.model.ComListener;
import com.xseec.eds.model.Tags.OverviewTag;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.service.ComService;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


public class OverviewFragment extends ComFragment implements ComListener,View.OnClickListener {


    @InjectView(R.id.image_area)
    ImageView imageArea;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @InjectView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @InjectView(R.id.text_status)
    TextView textStatus;
    @InjectView(R.id.image_status)
    CircleImageView imageStatus;
    @InjectView(R.id.text_device)
    TextView textDevice;
    @InjectView(R.id.image_enlarge_grid)
    ImageView imageEnlargeGrid;
    @InjectView(R.id.text_engineer)
    TextView textEngineer;
    @InjectView(R.id.image_phone)
    ImageView imagePhone;
    @InjectView(R.id.text_location)
    TextView textLocation;
    @InjectView(R.id.image_navigation)
    ImageView imageNavigation;
    @InjectView(R.id.recycler_overview)
    RecyclerView recyclerOverview;
    @InjectView(R.id.layout_container)
    LinearLayout layoutContainer;
    @InjectView(R.id.image_device_list)
    ImageView imageDeviceList;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.image_schedule)
    ImageView imageSchedule;

    List<OverviewTag> tagList;
    OverviewAdapter overviewAdapter;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(User user, List<Tag> tagList) {
        OverviewFragment fragment = new OverviewFragment();
        fragment.setArguments(getBundle(user, tagList));
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(getContext(), ComService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.inject(this, view);

        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        initViews();
        return view;
    }

    private void initViews() {
        //init Title
        getActivity().setTitle(R.string.overview_area);
        //init Overview Recycler
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerOverview.setLayoutManager(layoutManager);
        tagList = new ArrayList<OverviewTag>();
        Generator.genOverviewTagList(tagList);
        overviewAdapter = new OverviewAdapter(tagList);
        recyclerOverview.setAdapter(overviewAdapter);
        //init SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        //init ClickListener
        imageDeviceList.setOnClickListener(this);

        setValues();
    }

    private void setValues() {
        String[] basicInfos = Generator.getBasicInfo();
        int statusRes = basicInfos[0].equals("正常") ? R.color.colorNormal : (basicInfos[0].equals
                ("异常") ? R.color.colorError : R.color.colorAlarm);
        imageStatus.setImageResource(statusRes);
        textStatus.setText(basicInfos[0]);
        textDevice.setText(basicInfos[1]);
        textEngineer.setText(basicInfos[3]);
        textLocation.setText(basicInfos[4]);//init Schedule Card
        Glide.with(this).load(Generator.getScheduleImageRes()).into(imageSchedule);
    }

    private void refreshData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setValues();
                        Generator.genOverviewTagList(tagList);
                        overviewAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefreshed(final List<Tag> validTagList) {
        final String content = validTagList.get(0).getTagValue();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Update ui here
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_device_list:
                DeviceListActivity.start(getContext());
                break;
            default:
                break;
        }
    }
}
