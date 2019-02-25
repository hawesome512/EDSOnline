package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.adapter.AlarmAdapter;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.PercentageValueFormatter;
import com.xseec.eds.widget.ItemXAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabOerviewFragment extends ComFragment {

    @InjectView(R.id.text_current)
    TextView textCurrent;
    @InjectView(R.id.bar_chart)
    BarChart barChart;
    @InjectView(R.id.image_state)
    CircleImageView imageState;
    @InjectView(R.id.text_state)
    TextView textState;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.recyclerView_alarm)
    RecyclerView recyclerViewAlarm;


    private int[] colors = {getColor(R.color.colorPrimaryLight), getColor(R.color
            .colorAccentLight), getColor(R.color.colorAccent)};

    public static Fragment newInstance(String deviceName, List<String> overviewZone) {
        TabOerviewFragment fragment = new TabOerviewFragment();
        List<Tag> tags = new ArrayList<>();
        for (String tagName : overviewZone) {
            tags.add(new Tag(deviceName + ":" + tagName));
        }
        fragment.setArguments(getBundle(tags));
        return fragment;
    }

    public TabOerviewFragment() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_overview, container, false);
        ButterKnife.inject(this, view);
//        tagList=getArguments().getParcelableArrayList(ARG_TAGS);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlarm.setLayoutManager(layoutManager);
        queryAlarms();

        initChart();
        return view;
    }

    private void queryAlarms(){
        Alarm alarm=new Alarm(WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendAlarmQueryRequest(alarm, null, null, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Alarm> alarms= WAJsonHelper.getAlarmList(response);
                Collections.sort(alarms,Collections.<Alarm>reverseOrder());
                Device device=Device.initWith(tagList.get(0).getTagName());
                List<Alarm> deviceAlarms=new ArrayList<>();
                for(Alarm alarm:alarms){
                    if(alarm.getDevice().equals(device.getDeviceName())){
                        deviceAlarms.add(alarm);
                        //最多展示3条记录
                        if(deviceAlarms.size()==3){
                            break;
                        }
                    }
                }
                if(deviceAlarms.size()==0){
                    return;
                }
                final AlarmAdapter alarmAdapter=new AlarmAdapter(deviceAlarms,getContext());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerViewAlarm.setAdapter(alarmAdapter);
                    }
                });
            }
        });
    }

    private void initChart() {
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        String[] xItems={"Ia","Ib","Ic"};
        xAxis.setValueFormatter(new ItemXAxisValueFormatter(xItems));
        xAxis.setLabelCount(3);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1500);
        barChart.setDrawValueAboveBar(false);
        barChart.setScaleEnabled(false);

        YAxis yAxis = barChart.getAxisLeft();
        yAxis.addLimitLine(genLimitLine(90, colors[0]));
        yAxis.addLimitLine(genLimitLine(105, colors[1]));
        yAxis.addLimitLine(genLimitLine(120, colors[2]));
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(120);
        yAxis.setLabelCount(4);
    }

    private void setData(float... datas) {
        BarData barData = barChart.getBarData();
        BarDataSet dataSet;
        List<BarEntry> entries = new ArrayList<>();
        int[] currentColors = new int[datas.length];
        for (int i = 0; i < datas.length; i++) {
            float yValue = datas[i];
            entries.add(new BarEntry(i, yValue));
            currentColors[i] = yValue < 90 ? colors[0] : (yValue <= 105 ? colors[1] : colors[2]);
        }
        if (barData != null && barData.getDataSetCount() > 0) {
            dataSet = (BarDataSet) barData.getDataSetByIndex(0);
            dataSet.setValues(entries);
            barData.notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            dataSet = new BarDataSet(entries, "");
            dataSet.setHighlightEnabled(false);
            dataSet.setValueTextSize(20);
            dataSet.setColors(currentColors);
            barData = new BarData(dataSet);
            //barData.setValueTextColor(android.R.color.transparent);
            barData.setValueFormatter(new PercentageValueFormatter());
            barChart.setData(barData);
        }
        //value值过小时，标注会遮盖横坐标的Ia/Ib/Ic,暂处理：<30不显示值
        barData.setValueTextColor(dataSet.getYMin() < 30 ? android.R.color.transparent : getColor
                (R.color.colorWhite));
        barChart.invalidate();
    }

    private LimitLine genLimitLine(float value, int color) {
        LimitLine limitLine = new LimitLine(value, "");
        limitLine.setLineWidth(2);
        limitLine.setLineColor(color);
        limitLine.enableDashedLine(20, 20, 0);
        return limitLine;
    }

    private int getColor(int colorRes) {
        return ContextCompat.getColor(EDSApplication.getContext(), colorRes);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    // 首次创建时即绑定服务，更新此页面
    // 不在onResume中使用，因Activity:onPaused→onResume时，viewpager可能停留在其他页面
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        onBindService();
    }

    @Override
    public void onRefreshed(final List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                List<Tag> tags = TagsFilter.filterDeviceTagList(validTagList, "Status");
                if (tags != null && tags.size() >= 1) {
                    State state = DeviceConverterCenter.getState(tags.get(0));
                    imageState.setImageResource(state.getStateColorRes());
                    state.setUnusualAnimator(imageState);
                    textState.setText(DeviceConverterCenter.getStateText(tags.get(0)));
                }

                //使用模糊匹配，因MCCB中IrReal
                tags = TagsFilter.filterTagList(validTagList, "Ir", "IdA", "IdB", "IdC");
                if (tags.size() == 4) {
                    String strIr = tags.get(0).getTagValue();
                    String showValue = getString(R.string.device_demand, strIr);
                    textCurrent.setText(showValue);
                    float Ir = Generator.floatTryParse(tags.get(0).getTagValue());
                    if (Ir != 0) {
                        float[] items = new float[3];
                        for (int i = 0; i < 3; i++) {
                            float current = Generator.floatTryParse(tags.get(i + 1)
                                    .getTagValue());
                            float value = current / Ir * 100;
                            items[i] = value > 120 ? 120 : value;
                        }
                        setData(items);
                    }
                }
            }
        });

    }
}
