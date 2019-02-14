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
import java.util.regex.Pattern;

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
    private static final String EXT_FACTOR = "default_factor";
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
        defaultFactor = getArguments().getParcelable(EXT_FACTOR);
//        initFactor();
        initChart();
    }

    private void initChart() {
        //lineChart.animateX(2000);
        lineChart.getDescription().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv);
        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

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
        //取消曲线上已经高亮的点，否则重绘（如查询其他时间）时因找不到高亮点而闪退
        lineChart.highlightValues(null);

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

    private boolean isEnergy() {
        String tagName = storedTagList.get(0).getTagName();
        return Pattern.compile("E[PQS]").matcher(tagName).find();
    }

    private boolean hasLinkRadio() {
        return isEnergy() && defaultFactor.getIntervalType() == StoredTag.IntervalType.H &&
                defaultFactor.getRecords() == DataLogFactor.INTERVAL_ENERGY_LINK_RADIO;
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        //查询时间可能包含尚未发生的"未来时间区段"数据
        jsonData = jsonData.replaceAll("(,\"#\")+]", "]");
        List<String>[] values = WAJsonHelper.getTagLog(jsonData);
        if (values == null || values.length == 0) {
            return;
        }
        //电能累加值换算
        if (isEnergy()) {
            values[0] = Generator.genPerEnergyEntryList(values[0]);
        }
        progressDataLog.setVisibility(View.GONE);
        LineData lineData = new LineData();
        String legenName = null;

        LineDataSet lineDataSet1 = null;
        //今日值曲线+昨日环比
        if (hasLinkRadio() && values[0].size() > 24) {
            List<String> value = values[0];// Generator.genPerEnergyEntryList(values[0]);
            List<String> today = value.subList(24, value.size());
            legenName = getString(R.string.detail_yesterday);
            value = value.subList(0, 24);
            //新增今日值曲线
            List<Entry> entryList1 = Generator.convertEntryList(today, 0);
            lineDataSet1 = new LineDataSet(entryList1, getString(R.string.detail_today));
            lineDataSet1.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
            lineDataSet1.setCircleColor(ContextCompat.getColor(getContext(), R.color
                    .colorAccent));
            lineDataSet1.setLineWidth(2f);
            lineDataSet1.setCircleRadius(4f);
            //lineChart.getAxisLeft().setAxisMinimum(0);
            //lineData.addDataSet(lineDataSet1);
            //赋值
            float totalToday = Generator.getSumFromEntryList(entryList1, entryList1.size());
            //此语句与下面语句重复计算
            List<Entry> yesterdayEntryList = Generator.convertEntryList(value, 0);
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
            int resId = linkRadio < 0 ? R.drawable.ic_arrow_downward_green_a700_24dp : R
                    .drawable.ic_arrow_upward_red_a200_24dp;
            ViewHelper.drawTextBounds(textLast, resId, 0, 0, 0);
            values[0] = value;
        } else {
            textItemFirst.setText(getString(R.string.detail_max));
            textItemSecend.setText(getString(R.string.detail_min));
            textItemLast.setText(getString(R.string.detail_avg));
            ViewHelper.drawTextBounds(textLast, 0, 0, 0, 0);
        }

        int[] colors = new int[]{R.color.colorPhaseA, R.color.colorPhaseB, R.color
                .colorPhaseC, R
                .color.colorAlarm};
        List<Entry> entryLists = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            List<String> value = values[i];
            if(value.size()==0){
                continue;
            }
            List<Entry> entryList = Generator.convertEntryList(value, 0);
            legenName = (i == 0 && legenName != null) ? legenName : storedTagList.get(i)
                    .getTagAlias();
            LineDataSet lineDataSet = new LineDataSet(entryList, legenName);
            int colorIndex;
            if (values.length == 1) {
                colorIndex = 1;
                lineDataSet.setDrawFilled(true);
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable
                        .chart_fade_blue);
                lineDataSet.setFillDrawable(drawable);
            } else {
                colorIndex = i % colors.length;
                lineDataSet.setLineWidth(2f);
            }

            lineDataSet.setColor(ContextCompat.getColor(getContext(), colors[colorIndex]));
            lineDataSet.setCircleColor(ContextCompat.getColor(getContext(),
                    colors[colorIndex]));
            //临时方案：Ir,不能计入均值计算列中
            if (i >= 3) {
                lineDataSet.setDrawCircles(false);
            } else {
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
        if (!hasLinkRadio()) {
            textFrist.setText(String.valueOf(lineData.getYMax()));
            textSecend.setText(String.valueOf(lineData.getYMin()));
            textLast.setText(String.valueOf(Generator.getAvgFromEntryList(entryLists)));
        }
    }
}
