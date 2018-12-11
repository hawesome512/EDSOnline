package com.xseec.eds.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.ReportTimeSettingActivity;
import com.xseec.eds.adapter.ReportAdapter;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.Report;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.PermissionHelper;
import com.xseec.eds.util.ReportHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.MySpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.xseec.eds.activity.ReportTimeSettingActivity.KEY_END_TIME;
import static com.xseec.eds.activity.ReportTimeSettingActivity.KEY_START_TIME;

//nj--create report fragment on 2018/11/17
public class ReportFragment extends BaseFragment {

    public final static String REPORT_TEMPERATURE = "XRD:Tempreture";
    public final static String REPORT_HUMIDITY = "XRD:Humidity";

    private final static int LAST_WEEK = 0;
    private final static int LAST_MONTH = 1;
    private final static int DEF_TIME = 2;

    private final static int UN_EXECUTE = 0;
    private final static int EXECUTE = 1;
    private final static int SETTING_TIME = 1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.spinner_time_type)
    MySpinner spinner;
    @InjectView(R.id.progress_data_log)
    ProgressBar progress;
    @InjectView(R.id.text_time_type)
    TextView textTime;
    @InjectView(R.id.linear_layout)
    LinearLayout linearLayout;
    @InjectView(R.id.recycler)
    RecyclerView recycler;

    //nj-报表数据类型 2018/11/21
    private Report<Workorder> workorder;
    private Report<Alarm> alarm;
    private Report<Action> action;
    private Report<String> temperature;
    private Report<String> humidity;
    private List<Report> reportList;

    private DataLogFactor factor;
    private String startTime;
    private String endTime;
    private ReportAdapter adapter;

    //nj--网络请求数量统计 2018/11/21
    private CyclicBarrier barrier;

    public static ReportFragment newInstance() {
        return new ReportFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // nj--Inflater the layout for this fragment 2018/11/17
        setHasOptionsMenu( true );
        View view = inflater.inflate( R.layout.fragment_report, container, false );
        ButterKnife.inject( this, view );
        getActivity().setTitle( R.string.nav_trend );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(), toolbar, R.drawable.menu );
        initReportTitle();
        initRecycle();
        barrier = new CyclicBarrier( 4 );
        spinner.setReSelectionItem( DEF_TIME );
        onSpinnerClicked();
        return view;
    }

    private void initRecycle(){
        LinearLayoutManager layoutManager=new LinearLayoutManager( getContext());
        recycler.setLayoutManager( layoutManager );
    }

    //nj--加载item标题数据 2018/12/6
    private void initReportTitle(){
        String[] workorders=getResources().getStringArray( R.array.report_workorder );
        workorder=new Report<>( workorders);
        String[] alarms=getResources().getStringArray( R.array.report_alarm );
        alarm=new Report<>( alarms );
        String[] actions=getResources().getStringArray( R.array.report_action );
        action=new Report<>( actions );
        String[] temperatures=getResources().getStringArray( R.array.report_temperture );
        temperature=new Report<>( temperatures );
        String[] humiditys=getResources().getStringArray( R.array.report_humidity );
        humidity=new Report<>( humiditys );
    }

    //nj--发生网络请求 2018/11/19
    private void queryReportInfo() {
        progress.setVisibility( View.VISIBLE );
        queryWorkorders( startTime, endTime );
        queryAlarms( startTime, endTime );
        queryActions( startTime, endTime );
        queryEnvironment( startTime, endTime );
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        String time = ReportHelper.getTime(startTime,endTime);
        textTime.setText( time );
        setData();
        adapter=new ReportAdapter( getContext(),reportList,factor );
        recycler.setAdapter( adapter );
        progress.setVisibility( View.GONE );
        barrier.reset();
    }

    //nj--加载列表数据 2018/11/19
    private void setData() {
        reportList=new ArrayList<>(  );
        reportList.add( workorder );
        reportList.add( alarm );
        reportList.add( action );
        reportList.add( temperature );
        reportList.add( humidity );
    }

    //==========nj--网络数据请求 2018/11/19=================================================
    private void queryWorkorders(String startTime, String endTime) {
        Workorder work = new Workorder( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendWorkorderQueryRequest( work, startTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Workorder> workorderList=WAJsonHelper.getWorkorderList( response );
                List list;
                workorder.setLeftList( workorderList );
                list = ReportHelper.filterWorkorderList( workorderList, EXECUTE );
                workorder.setCenterList( list );
                list = ReportHelper.filterWorkorderList( workorderList, UN_EXECUTE );
                workorder.setRightList( list );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryAlarms(String starTime, String endTime) {
        Alarm ala = new Alarm( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendAlarmQueryRequest( ala, starTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Alarm> alarmList=WAJsonHelper.getAlarmList( response );
                List<Alarm> list;
                alarm.setLeftList( alarmList );
                list = ReportHelper.filterAlarmList( alarmList, EXECUTE );
                alarm.setCenterList( list );
                list = ReportHelper.filterAlarmList( alarmList, UN_EXECUTE );
                alarm.setRightList( list );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryActions(String starTime, String endTime) {
        Action ac = new Action( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendActionQueryRequest( ac, starTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Action> actionList=WAJsonHelper.getActionList( response );
                List<Action> list;
                list = ReportHelper.filterActionList( actionList, Action.ActionType.DEVICE );
                action.setLeftList( list );
                list = ReportHelper.filterActionList( actionList, Action.ActionType.WORKORDER );
                action.setCenterList( list );
                list = ReportHelper.filterActionList( actionList, Action.ActionType.LOGIN );
                action.setRightList( list );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryEnvironment(String startTime, String endTime) {
        factor = ReportHelper.getDataLogFactor( startTime, endTime );
        List<StoredTag> storedTags = new ArrayList<>();
        storedTags.add( new StoredTag( REPORT_TEMPERATURE, StoredTag.DataType.MAX ) );
        storedTags.add( new StoredTag( REPORT_HUMIDITY, StoredTag.DataType.MAX ) );
        WAServiceHelper.sendTagLogRequest( factor, storedTags, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<String>[] tagList = WAJsonHelper.getTagLog( response.body().string() );
                //nj--暂时生成温度、湿度数据 2018/11/25
                if (tagList[0].get( 0 ).equals( "#" )) {
                    tagList[0] = ReportHelper.randomList( tagList[0].size(), 35, 70 );
                    tagList[1] = ReportHelper.randomList( tagList[0].size(), 30, 100 );
                }
                temperature.setEnvironmentList( tagList[0] );
                humidity .setEnvironmentList( tagList[1] );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }
    //=====================================================================================

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.share_menu, menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_share:
                //nj--分享报表数据 2018/11/27
                PermissionHelper.checkPermission( getActivity(), WRITE_EXTERNAL_STORAGE,
                        PermissionHelper.CODE_READ_CONTACTS );
                Bitmap bitmap = ReportHelper.getBitmap( linearLayout, recycler );
                ReportHelper.shareReport( getContext(), bitmap );
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    private void onSpinnerClicked() {
        spinner.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case LAST_WEEK:
                        startTime = DateHelper.getStartTimeOfWeek();
                        endTime = DateHelper.getEndTimeOfWeek();
                        queryReportInfo();
                        break;
                    case LAST_MONTH:
                        startTime = DateHelper.getStartTimeOfMonth();
                        endTime = DateHelper.getEndTimeOfMonth();
                        queryReportInfo();
                        break;
                    case DEF_TIME:
                        ReportTimeSettingActivity.start( getActivity(), SETTING_TIME );
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        } );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SETTING_TIME:
                if (resultCode == RESULT_OK) {
                    startTime = data.getStringExtra( KEY_START_TIME );
                    endTime = data.getStringExtra( KEY_END_TIME );
                    queryReportInfo();
                }
                break;
            default:
                break;
        }
    }

    //nj--网络请求CyclicBarrier阻拦方法 2018/12/5
    private void setBarrierAwait(CyclicBarrier barrier) {
        try {
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
