package com.xseec.eds.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import android.widget.FrameLayout;
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
import com.xseec.eds.model.tags.EnergyTag;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.PermissionHelper;
import com.xseec.eds.util.ShareHelper;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.MySpinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
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

    private final static int LAST_WEEK = 0;
    private final static int LAST_MONTH = 1;
    private final static int DEF_TIME = 2;

    private final static int DUE = 0;
    private final static int DONE = 1;
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
    RecyclerView recyclerView;
    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;

    //nj-报表数据类型 2018/11/21
    private Report<Workorder> workorder;
    private Report<Alarm> alarm;
    private Report<Action> action;
    private Report<String> energy;
    private Report<String> temperature;
    private List<Report> reportList;
    //nj--报表查询时间 2018/12/11
    private String startTime;
    private String endTime;

    private DataLogFactor factor;
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
        getActivity().setTitle( R.string.nav_report );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(), toolbar, R.drawable.menu );

        final Snackbar snackbar = Snackbar.make( layoutContainer, R.string.function_null, Snackbar.LENGTH_INDEFINITE );
        snackbar.setAction( R.string.ok, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        } );
        snackbar.show();

        initView();
        return view;
    }

    private void initView() {
        initReportTitle();
        LinearLayoutManager layoutManager = new LinearLayoutManager( getContext() );
        recyclerView.setLayoutManager( layoutManager );
        barrier = new CyclicBarrier( 4 );
        spinner.setReSelectionItem( DEF_TIME );
        onSpinnerClicked();
    }

    //nj--加载item标题数据 2018/12/6
    private void initReportTitle() {
        String[] workorders = getResources().getStringArray( R.array.report_workorder );
        workorder = new Report<>( workorders, 0 );
        String[] alarms = getResources().getStringArray( R.array.report_alarm );
        alarm = new Report<>( alarms, 0 );
        String[] actions = getResources().getStringArray( R.array.report_action );
        action = new Report<>( actions, 0 );
        String[] temperatures = getResources().getStringArray( R.array.report_temperture );
        temperature = new Report<>( temperatures, 2 );
        String[] energys = getResources().getStringArray( R.array.report_energy );
        energy = new Report<>( energys, 1 );
    }

    //nj--发送网络请求 2018/11/19
    private void queryReportInfo() {
        progress.setVisibility( View.VISIBLE );
        queryWorkorders( startTime, endTime );
        queryAlarms( startTime, endTime );
        queryActions( startTime, endTime );
        queryEnergy( startTime, endTime );
        //NJ--暂时取消环境报表内容
//        queryEnvironment( startTime, endTime );
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        String timeTitle = Generator.getReportTime( startTime, endTime );
        textTime.setText( timeTitle );
        addReportList();
        adapter = new ReportAdapter( getContext(), reportList, factor );
        recyclerView.setAdapter( adapter );
        progress.setVisibility( View.GONE );
        barrier.reset();
    }

    //nj--加载列表数据 2018/11/19
    private void addReportList() {
        reportList = new ArrayList<>();
        reportList.add( workorder );
        reportList.add( alarm );
        reportList.add( action );
        reportList.add( energy );
//        reportList.add( temperature );
    }

    //==========nj--网络数据请求 2018/11/19=================================================
    private void queryWorkorders(String startTime, String endTime) {
        Workorder RequestWorkorder = new Workorder( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendWorkorderQueryRequest( RequestWorkorder, startTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Workorder> workorderList = WAJsonHelper.getWorkorderList( response );
                List<Workorder> itemList;
                workorder.setLeftList( workorderList );
                itemList = Generator.filterWorkorderListOfState( workorderList, DUE );
                workorder.setCenterList( itemList );
                itemList = Generator.filterWorkorderListOfState( workorderList, DONE );
                workorder.setRightList( itemList );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryAlarms(String starTime, String endTime) {
        Alarm RequestAlarm = new Alarm( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendAlarmQueryRequest( RequestAlarm, starTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Alarm> alarmList = WAJsonHelper.getAlarmList( response );
                List<Alarm> itemList;
                alarm.setLeftList( alarmList );
                itemList = Generator.filterAlarmConfirm( alarmList, DONE );
                alarm.setCenterList( itemList );
                itemList = Generator.filterAlarmConfirm( alarmList, DUE );
                alarm.setRightList( itemList );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryActions(String starTime, String endTime) {
        Action RequestAction = new Action( WAServicer.getUser().getDeviceName() );
        WAServiceHelper.sendActionQueryRequest( RequestAction, starTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Action> actionList = WAJsonHelper.getActionList( response );
                List<Action> itemList;
                itemList = Generator.filterActionsType( actionList, Action.ActionType.DEVICE.ordinal() );
                action.setLeftList( itemList );
                itemList = Generator.filterActionsType( actionList, Action.ActionType.WORKORDER.ordinal() );
                action.setCenterList( itemList );
                itemList = Generator.filterActionsType( actionList, Action.ActionType.LOGIN.ordinal() );
                action.setRightList( itemList );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    private void queryEnvironment(String startTime, String endTime) {
        factor = getDataLogFactor( startTime, endTime );
        List<StoredTag> storedTags = Generator.genStoredTags();
        WAServiceHelper.sendTagLogRequest( factor, storedTags, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<String>[] tagList = WAJsonHelper.getTagLog( response.body().string() );
                temperature.setEnvironmentList( tagList[0] );
                //nj--阻挡线程 2018//12/5
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } );
    }

    //nj--暂时生成能耗模拟值 2018/12/18
    private void queryEnergy(final String startTime, final String endTime) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                int interval = (int) (DateHelper.getBetweenOfSecond( startTime, endTime ) / (3600 * 24));
                List<EnergyTag> energyList = Generator.genEnergyTagList();
                String value = energyList.get( 0 ).getTagValue();
                List<String> realEnergyList = new ArrayList<>();
                List<String> expectEnergyList = new ArrayList<>();
                int baseValue = Integer.valueOf( value ) * 24;
                Random random = new Random();
                for (int i = 0; i < interval; i++) {
                    realEnergyList.add( (80 + random.nextInt( 40 )) * baseValue / 100 + "" );
                    expectEnergyList.add( baseValue + "" );
                }
                energy.setLeftList( realEnergyList );
                energy.setCenterList( expectEnergyList );
                Response response = null;
                setBarrierAwait( barrier );
                refreshViewsInThread( response );
            }
        } ).start();
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
                Bitmap bitmap = ShareHelper.getBitmap( linearLayout, recyclerView );
                ShareHelper.shareCapture( getContext(), bitmap );
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
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

    //nj--产生字符串List 2018/11/25
    public static List<String> randomList(int length, int min, int max) {
        Random rand = new Random();
        List<String> valueList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            float value = (rand.nextInt( max - min ) + min) * 10 / 10f;
            valueList.add( String.valueOf( value ) );
        }
        return valueList;
    }

    //nj--环境报表查询请求信息 2018/11/23
    private static DataLogFactor getDataLogFactor(String startTime, String endTime) {
        DataLogFactor factor = new DataLogFactor();
        Date start = DateHelper.getServletDate( startTime );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( start );
        factor.setStartTime( calendar );
        factor.setDataType( StoredTag.DataType.MAX );
        factor.setIntervalType( StoredTag.IntervalType.D );
        factor.setInterval( 1 );
        int records = (int) (DateHelper.getBetweenOfSecond( startTime, endTime ) / (3600 * 24));
        factor.setRecords( records );
        return factor;
    }
}
