package com.xseec.eds.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.xseec.eds.R;
import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.widget.PercentageValueFormatter;
import com.xseec.eds.widget.PhaseXAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
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
    @InjectView(R.id.text_peak)
    TextView textPeak;
    @InjectView(R.id.text_trip)
    TextView textTrip;
    @InjectView(R.id.progress)
    ProgressBar progress;


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
        initChart();
        return view;
    }

    private void initChart() {
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new PhaseXAxisValueFormatter());
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
        barData.setValueTextColor(dataSet.getYMin()<30?android.R.color.transparent:getColor(R.color.colorWhite));
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

    @OnClick(R.id.text_peak)
    public void onTextPeakClicked() {
    }

    @OnClick(R.id.text_trip)
    public void onTextTripClicked() {
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
                List<Tag> tags = TagsFilter.filterDeviceTagList(validTagList, "State");
                if (tags != null) {
                    State state = State.getState(tags.get(0).getTagValue());
                    imageState.setImageResource(state.getStateColorRes());
                    state.setUnusualAnimator(imageState);
                    textState.setText(state.getStateText());
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
                tags = TagsFilter.filterDeviceTagList(validTagList, "TripYM", "TripDH",
                        "TripMS");
                if (tags.size() == 3) {
                    int value0 = (int) Generator.floatTryParse(tags.get(0).getTagValue());
                    if (value0 > 0 && value0 < 65535) {
                        //BCD码
                        String[] values = new String[6];
                        for (int i = 0; i < tags.size(); i++) {
                            int nValue = (int) Generator.floatTryParse(tags.get(i)
                                    .getTagValue());
                            values[2 * i] = Integer.toHexString(nValue / 256);
                            values[2 * i + 1] = Integer.toHexString(nValue % 256);
                        }
                        String time = getString(R.string.device_trip_time, values[0],
                                values[1], values[2], values[3], values[4], values[5]);
                        textTrip.setText(time);
                    }
                }
            }
        });

    }
}
