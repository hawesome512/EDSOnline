package com.xseec.eds.fragment;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.DataLogSettingActivity;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.MyMarkerView;
import com.xseec.eds.widget.TimeXAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataLogFragment extends BaseFragment {

    @InjectView(R.id.line_chart)
    LineChart lineChart;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.text_first)
    TextView textFrist;
    @InjectView(R.id.text_secend)
    TextView textSecend;
    @InjectView(R.id.text_last)
    TextView textLast;
    @InjectView(R.id.progress_data_log)
    ProgressBar progressDataLog;
    @InjectView(R.id.text_item_first)
    TextView textItemFirst;
    @InjectView(R.id.text_item_secend)
    TextView textItemSecend;
    @InjectView(R.id.text_item_last)
    TextView textItemLast;

    private List<StoredTag> storedTagList;
    private DataLogFactor defaultFactor;

    private static final String EXT_TAG = "tags";
    private static final String EXT_FACTOR="default_factor";
    public static final String KEY_FACOTR = "factor";
    private static final int REQUEST_CODE_TIME = 1;

    public static DataLogFragment newInstance(List<String> tagNames, DataLogFactor factor) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(EXT_TAG, (ArrayList<String>) tagNames);
        bundle.putParcelable(EXT_FACTOR, factor);
        DataLogFragment fragment = new DataLogFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public DataLogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //必须添加此语句，方能触发onCreateOptionsMenu
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_data_log, container, false);
        ButterKnife.inject(this, view);
        getActivity().setTitle(R.string.detail_trend);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable
                .ic_arrow_back_white_24dp);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        List<String> tagNames = getArguments().getStringArrayList(EXT_TAG);
        storedTagList = new ArrayList<>();
        for (String tagName : tagNames) {
            storedTagList.add(new StoredTag(tagName, StoredTag.DataType.MAX));
        }
        defaultFactor=getArguments().getParcelable(EXT_FACTOR);
//        initFactor();
        initChart();
    }

//    private void initFactor() {
//        Calendar calendar = Calendar.getInstance();
//        switch (defaultFactor.getIntervalType()) {
//            case S:
//                //过去5分钟，60点
//                calendar.add(Calendar.MINUTE, -5);
//                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.S, 5, 60);
//                break;
//            case M:
//                //过去1小时，60点
//                calendar.add(Calendar.HOUR_OF_DAY, -1);
//                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.M, 1, 60);
//                break;
//            case H:
//                //昨日+今日，48点
//                calendar.add(Calendar.DATE, -1);
//                Calendar yesterday = DateHelper.getDayStartTime(calendar);
//                defaultFactor = new DataLogFactor(yesterday, StoredTag.IntervalType.H, 1, 48);
//                break;
//            case D:
//                //本月数据
//                calendar.add(Calendar.DATE, 1 - calendar.get(Calendar.DAY_OF_MONTH));
//                calendar = DateHelper.getDayStartTime(calendar);
//                int maxMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.D, 1,
//                        maxMonthDay);
//                break;
//            default:
//                defaultFactor = null;
//                break;
//        }
//    }

    private void initChart() {
        //lineChart.animateX(2000);
        lineChart.getDescription().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        lineChart.setMarker(mv);

        setData();
    }

    private void setData() {
        progressDataLog.setVisibility(View.VISIBLE);
        //横坐标设定
        XAxis xAxis = lineChart.getXAxis();
        IAxisValueFormatter iAxisValueFormatter = new TimeXAxisValueFormatter(lineChart,
                defaultFactor);
        xAxis.setValueFormatter(iAxisValueFormatter);

        LineData data = lineChart.getData();
        if (data != null) {
            data.clearValues();
        }
        for (StoredTag tag : storedTagList) {
            tag.setDataType(defaultFactor.getDataType());
        }

        WAServiceHelper.sendTagLogRequest(defaultFactor.getStartTimeString(),
                defaultFactor.getIntervalType(), defaultFactor.getInterval(), defaultFactor
                        .getRecords(), storedTagList, new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        refreshViewsInThread(response);
                    }
                });
    }

    private boolean isEnergyAnalysis() {
        return defaultFactor.getIntervalType() == StoredTag.IntervalType.H && defaultFactor
                .getRecords() == 48;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.query_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.query_time:
                if (defaultFactor != null) {
                    DataLogSettingActivity.start(getActivity(), REQUEST_CODE_TIME, defaultFactor);
                }
                break;
            case R.id.query_config:

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //在Activity中重写onActivityResult方法触发Fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_TIME:
                if (resultCode == RESULT_OK) {
                    defaultFactor = data.getParcelableExtra(KEY_FACOTR);
                    setData();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        List<String>[] values = WAJsonHelper.getTagLog(jsonData);
        if (values==null||values.length == 0) {
            return;
        }
        progressDataLog.setVisibility(View.GONE);
        LineData lineData = new LineData();
        String legenName = null;

        //暂时方案,模拟值
        LineDataSet lineDataSet1 = null;
        if (isEnergyAnalysis()) {
            List<String> value = values[0];
            value = Generator.genYesTodayEntryList(defaultFactor.getStartTime());
            List<String> today = value.subList(24, value.size());
            legenName = getString(R.string.detail_yesterday);
            value = value.subList(0, 24);
            //新增今日值曲线
            List<Entry> entryList1 = Generator.convertEntryList(today,0);
            lineDataSet1 = new LineDataSet(entryList1, getString(R.string.detail_today));
            lineDataSet1.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            lineDataSet1.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setCircleRadius(4f);
            lineChart.getAxisLeft().setAxisMinimum(0);
            //lineData.addDataSet(lineDataSet1);
            //赋值
            float totalToday = Generator.getSumFromEntryList(entryList1, entryList1.size());
            //此语句与下面语句重复计算
            List<Entry> yesterdayEntryList = Generator.convertEntryList(value,0);
            float totalYesterday = Generator.getSumFromEntryList(yesterdayEntryList,
                    entryList1.size());
            float linkRadio = Math.round((totalToday - totalYesterday) / totalYesterday * 10000)
                    / 100f;
            textItemFirst.setText(getString(R.string.detail_today));
            textItemSecend.setText(getString(R.string.detail_yesterday_same));
            textItemLast.setText(getString(R.string.detail_link_ratio));
            textFrist.setText(String.valueOf(totalToday));
            textSecend.setText(String.valueOf(totalYesterday));
            textLast.setText(linkRadio + "%");
            if (ApiLevelHelper.isAtLeast(17)) {
                int resId = linkRadio < 0 ? R.drawable.ic_arrow_downward_green_a700_24dp : R
                        .drawable.ic_arrow_upward_red_a200_24dp;
                textLast.setCompoundDrawablesRelativeWithIntrinsicBounds(resId, 0, 0, 0);
            }
            values[0]=value;
        }

        int[] colors=new int[]{R.color.colorPhaseA,R.color.colorPhaseB,R.color.colorPhaseC,R.color.colorAlarm};
        List<Entry> entryLists=new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            List<String> value = values[i];
            List<Entry> entryList = Generator.convertEntryList(value,0);
            legenName=(i==0&&legenName!=null)?legenName:storedTagList.get(i).getTagAlias();
            LineDataSet lineDataSet = new LineDataSet(entryList,legenName);
            int colorIndex;
            if (values.length == 1) {
                colorIndex=1;
                lineDataSet.setDrawFilled(true);
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable
                        .chart_fade_blue);
                lineDataSet.setFillDrawable(drawable);
            }else {
                colorIndex=i%colors.length;
                lineDataSet.setLineWidth(2f);
            }

            lineDataSet.setColor(ContextCompat.getColor(getContext(), colors[colorIndex]));
            lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), colors[colorIndex]));
            //临时方案：Ir,不能计入均值计算列中
            if(i>=3){
                lineDataSet.setDrawCircles(false);
            }else {
                entryLists.addAll(entryList);
            }
            lineData.addDataSet(lineDataSet);
        }

        //保证今日值曲线置于顶层
        if (lineDataSet1 != null) {
            lineData.addDataSet(lineDataSet1);
        }
        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        //动画
        lineChart.animateX(2000);
        lineChart.invalidate();
        //赋值
        if (!isEnergyAnalysis()) {
            textFrist.setText(String.valueOf(lineData.getYMax()));
            textSecend.setText(String.valueOf(lineData.getYMin()));
            textLast.setText(String.valueOf(Generator.getAvgFromEntryList(entryLists)));
        }
    }
}
