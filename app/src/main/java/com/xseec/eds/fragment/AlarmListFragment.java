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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.FilterActivity;
import com.xseec.eds.adapter.AlarmAdapter;
import com.xseec.eds.model.FilterLabel;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
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
    private static final int REQUEST_FILTER=2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.progress)
    ProgressBar progress;

    private AlarmAdapter alarmAdapter;
    //NJ--筛选列表条件列表 2018/12/25
    private List<FilterLabel> filterList;
    //nj--查询的时间段 2018/12/19
    private String startTime;
    private String endTime;

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
        setHasOptionsMenu( true );
        View view = inflater.inflate(R.layout.layout_list, container, false);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        getActivity().setTitle(R.string.nav_alarm);
        swipeRefreshLayout.setEnabled(false);
        //nj--设置查询时间为近期15天 2018/12/19
        endTime= DateHelper.getServletString(DateHelper.getNearTimeOfMonth( 0,0 ));
        startTime=DateHelper.getServletString( DateHelper.getNearTimeOfMonth( 0,-15 ) );

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
        final Alarm alarm=new Alarm(WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendAlarmQueryRequest(alarm, startTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Alarm> alarms= WAJsonHelper.getAlarmList(response);
                Collections.sort(alarms,Collections.<Alarm>reverseOrder());
                //nj--筛选信息 2018/12/26
                filterAlarmList( alarms,filterList );
                alarmAdapter=new AlarmAdapter(alarms,getContext());
                refreshViewsInThread(response);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.filter_menu,menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                //nj--异常记录筛选
                FilterActivity.start( getActivity(),FilterActivity.ALARM,REQUEST_FILTER );
                break;
        }
        return super.onOptionsItemSelected( item );
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
        switch (requestCode){
            case REQUEST_CREATE:
                if (resultCode==Activity.RESULT_OK){
                    queryAlarms();
                }
                break;
            case REQUEST_FILTER:
                if (resultCode==Activity.RESULT_OK){
                    startTime=null;
                    endTime=null;
                    filterList=data.getParcelableArrayListExtra( FilterActivity.DATA_RESULT );
                    queryAlarms();
                }
                break;
            default:
                break;
        }
    }

    //nj--筛选异常列表数据 2018/12/25
    private void filterAlarmList(List<Alarm> sources,List<FilterLabel> filterList){
        if (filterList==null){
            return ;
        }else {
            for(FilterLabel label:filterList){
                List<Alarm> alarmList;
                if (label.getType().equals( "confirm" )){
                    alarmList=Generator.filterAlarmConfirm( sources,label.getValueOfInt());
                }else {
                    alarmList=Generator.filterAlarmDevice( sources,label.getValue() );
                }
                sources.clear();
                sources.addAll( alarmList );
            }
        }
    }
}
