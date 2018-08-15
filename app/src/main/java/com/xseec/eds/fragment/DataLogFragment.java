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
import com.xseec.eds.model.Tags.StoredTag;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.MyMarkerView;
import com.xseec.eds.widget.TimeXAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataLogFragment extends Fragment {

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

    private User user;
    private StoredTag.IntervalType defaultIntervalType;
    private List<StoredTag> tagList;
    private DataLogFactor defaultFactor;

    private static final String ARG_TAG = "tag_name";
    private static final String ARG_INTERVAL_TYPE = "interval_type";
    public static final String KEY_FACOTR = "factor";
    private static final int REQUEST_CODE_TIME = 1;

    public static DataLogFragment newInstance(String tagName, StoredTag.IntervalType intervalType) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TAG, tagName);
        bundle.putInt(ARG_INTERVAL_TYPE, intervalType.ordinal());
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
        String tagName = getArguments().getString(ARG_TAG);
        int typeOrdinal = getArguments().getInt(ARG_INTERVAL_TYPE);
        defaultIntervalType = StoredTag.IntervalType.values()[typeOrdinal];
        initFactor();
        user = WAServicer.getUser();
        tagList = new ArrayList<>();
        tagList.add(new StoredTag(tagName, StoredTag.DataType.MAX));
        if (user != null) {
            initChart(tagName);
        } else {
            //直接返回
        }
    }

    private void initFactor() {
        Calendar calendar = Calendar.getInstance();
        switch (defaultIntervalType) {
            case S:
                //过去5分钟，60点
                calendar.add(Calendar.MINUTE, -5);
                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.S, 5, 60);
                break;
            case M:
                //过去1小时，60点
                calendar.add(Calendar.HOUR_OF_DAY, -1);
                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.M, 1, 60);
                break;
            case H:
                //昨日+今日，48点
                calendar.add(Calendar.DATE, -1);
                Calendar yesterday = DateHelper.getDayStartTime(calendar);
                defaultFactor = new DataLogFactor(yesterday, StoredTag.IntervalType.H, 1, 48);
                break;
            case D:
                //本月数据
                calendar.add(Calendar.DATE, 1 - calendar.get(Calendar.DAY_OF_MONTH));
                calendar = DateHelper.getDayStartTime(calendar);
                int maxMonthDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                defaultFactor = new DataLogFactor(calendar, StoredTag.IntervalType.D, 1,
                        maxMonthDay);
                break;
            default:
                defaultFactor = null;
                break;
        }
    }

    private void initChart(String tagName) {
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
        for (StoredTag tag : tagList) {
            tag.setDataType(defaultFactor.getDataType());
        }

        WAServiceHelper.sendTagLogRequest(user.getAuthority(), defaultFactor.getStartTimeString(),
                defaultFactor.getIntervalType(), defaultFactor.getInterval(), defaultFactor
                        .getRecords(), tagList, new Callback() {

                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        final List<String>[] values = WAJsonHelper.getTagLog(response);
                        //横竖屏切换，getActivity可能为null
                        if (getActivity() == null) {
                            return;
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshViews(values[0]);
                            }
                        });
                    }
                });
    }

    private boolean isEnergyAnalysis() {
        return defaultFactor.getIntervalType() == StoredTag.IntervalType.H && defaultFactor
                .getRecords() == 48;
    }

    private void refreshViews(List<String> value) {
        progressDataLog.setVisibility(View.GONE);
        LineData lineData = new LineData();
        String legenName = tagList.get(0).getTagName();

        //暂时方案,模拟值
        LineDataSet lineDataSet1 = null;
        if (isEnergyAnalysis()) {
            value = Generator.genYesTodayEntryList(defaultFactor.getStartTime());
            List<String> today = value.subList(24, value.size());
            legenName = getString(R.string.detail_yesterday);
            value = value.subList(0, 24);
            //新增今日值曲线
            List<Entry> entryList1 = Generator.convertEntryList(today);
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
            List<Entry> yesterdayEntryList = Generator.convertEntryList(value);
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
        }

        List<Entry> entryList = Generator.convertEntryList(value);
        LineDataSet lineDataSet = new LineDataSet(entryList, legenName);
        lineDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPhaseB));
        lineDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPhaseB));
        lineDataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.chart_fade_blue);
        lineDataSet.setFillDrawable(drawable);

        lineData.addDataSet(lineDataSet);
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
            textLast.setText(String.valueOf(Generator.getAvgFromEntryList(entryList)));
        }
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
}
