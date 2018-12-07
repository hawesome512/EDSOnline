package com.xseec.eds.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.adapter.AlarmAdapter;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlarmListFragment extends BaseFragment {

    public static final int REQUEST_CREATE=1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.progress)
    ProgressBar progress;

    AlarmAdapter alarmAdapter;

    public static Fragment newInstance() {
        Fragment fragment = new AlarmListFragment();
        return fragment;
    }


    public AlarmListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_list, container, false);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        getActivity().setTitle(R.string.nav_alarm);
        swipeRefreshLayout.setEnabled(false);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        queryAlarms();
    }

    private void queryAlarms(){
        progress.setVisibility(View.VISIBLE);
        Alarm alarm=new Alarm(WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendAlarmQueryRequest(alarm, null, null, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Alarm> alarms= WAJsonHelper.getAlarmList(response);
                Collections.sort(alarms,Collections.<Alarm>reverseOrder());
                alarmAdapter=new AlarmAdapter(alarms,getContext());
                refreshViewsInThread(response);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progress.setVisibility(View.GONE);
        recycler.setAdapter(alarmAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CREATE&&resultCode==Activity.RESULT_OK){
            queryAlarms();
        }
    }
}
