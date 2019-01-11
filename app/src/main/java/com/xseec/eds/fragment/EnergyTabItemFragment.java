package com.xseec.eds.fragment;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.xseec.eds.R;
import com.xseec.eds.adapter.EnergyLevelAdapter;
import com.xseec.eds.model.tags.EnergyTag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.widget.ItemXAxisValueFormatter;
import com.xseec.eds.widget.MonthYearPickerDialog;
import com.xseec.eds.widget.MyMarkerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 * LineChart:显示当前层的环比值，同比值
 * TextView*3:当前值，环比同期，环比
 * BarChart:当前层分支值
 * PieChart:当前层分支占比
 */
public class EnergyTabItemFragment extends BaseFragment implements OnChartValueSelectedListener,
        EnergyLevelAdapter.ParentListener, DatePickerDialog.OnDateSetListener {

    @InjectView(R.id.line_chart)
    LineChart lineChart;
    @InjectView(R.id.text_item_first)
    TextView textItemFirst;
    @InjectView(R.id.text_first)
    TextView textFirst;
    @InjectView(R.id.text_item_secend)
    TextView textItemSecend;
    @InjectView(R.id.text_secend)
    TextView textSecend;
    @InjectView(R.id.text_item_last)
    TextView textItemLast;
    @InjectView(R.id.text_last)
    TextView textLast;
    @InjectView(R.id.bar_chart)
    BarChart barChart;
    @InjectView(R.id.pie_chart)
    PieChart pieChart;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.text_time)
    TextView textTime;
    @InjectView(R.id.recyclerView_tag)
    RecyclerView recyclerViewTag;
    @InjectView(R.id.scroll_view)
    NestedScrollView scrollView;
    @InjectView(R.id.text_tip)
    TextView textTip;

    private EnergyLevelAdapter levelAdapter;
    private List<EnergyTag> energyList;
    private List<EnergyTag> parents;
    private List<EnergyTag> children;
    private int currentIndex;
    private String[] showNames;
    //模拟值临时方案，实际使用删除此变量，一次性查询所有能耗数据的（环比值+当前值）
    //countOfLast:环比值个数
    private List<String>[] tagLogs;
    private int countOfLast;
    private Map<String, Integer> childrenValues;
    private Calendar startTime;

    //模式：日、月、年
    private int field;
    private static final String KEY_FIELD = "field";

    public static Fragment newInstance(int field) {
        Fragment fragment = new EnergyTabItemFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_FIELD, field);
        fragment.setArguments(bundle);
        return fragment;
    }

    public EnergyTabItemFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_energy_tab_item, container, false);
        ButterKnife.inject(this, view);
        initViews();
        return view;
    }

    private void initViews() {
        field = getArguments().getInt(KEY_FIELD);

        showNames = getResources().getStringArray(R.array.energy_analysis);
        textItemFirst.setText(showNames[0]);
        textItemSecend.setText(showNames[1]);
        textItemLast.setText(showNames[2]);

        //使用模拟值，待正式运行后修正
        energyList = Generator.genEnergyTagList();
        currentIndex = 0;
        startTime = Calendar.getInstance();
        initLineChart();
        initPieChart();
        initBarCart();
        requestDatalog();

        //父级标签，横向排列，点击标签切换当前层级
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTag.setLayoutManager(layoutManager);
        parents = energyList.get(currentIndex).getEnergyParents(energyList);
        levelAdapter = new EnergyLevelAdapter(getContext(), parents, this);
        recyclerViewTag.setAdapter(levelAdapter);
    }

    private void initLineChart() {
        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control
        lineChart.setMarker(mv);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0);
    }

    private void setLineData() {
        LineData lineData = lineChart.getData();
        if (lineData != null) {
            lineData.clearValues();
        } else {
            lineData = new LineData();
        }

        List<String> yValues = tagLogs[currentIndex];
        List<String> current = yValues.subList(countOfLast, yValues.size());
        List<String> last = yValues.subList(0, countOfLast);
        //日期<时>0:00开始，月份<日期>从1号、年份<月>从1月开始
        int minXValue = field == Calendar.DATE ? 0 : 1;

        //环比值曲线
        List<Entry> lastsEntries = Generator.convertEntryList(last, minXValue);
        LineDataSet lastDataSet = new LineDataSet(lastsEntries, showNames[1]);
        lastDataSet.setDrawFilled(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable
                .chart_fade_blue);
        lastDataSet.setFillDrawable(drawable);
        lastDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        lastDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        lineData.addDataSet(lastDataSet);

        //当前值曲线
        List<Entry> curEntries = Generator.convertEntryList(current, minXValue);
        LineDataSet curDataSet = new LineDataSet(curEntries, showNames[0]);
        curDataSet.setColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        curDataSet.setCircleColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        curDataSet.setLineWidth(2f);
        curDataSet.setCircleRadius(4f);
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineData.addDataSet(curDataSet);

        lineData.setDrawValues(false);
        lineChart.setData(lineData);
        lineChart.animateX(ViewHelper.CHART_ANIMATE_DURATION);
        lineChart.invalidate();

        //计算
        float totalToday = Generator.getSumFromEntryList(curEntries, curEntries.size());
        float totalYesterday = Generator.getSumFromEntryList(lastsEntries, curEntries.size());
        float linkRadio = Math.round((totalToday - totalYesterday) / totalYesterday * 10000)
                / 100f;
        textFirst.setText(String.valueOf(totalToday));
        textSecend.setText(String.valueOf(totalYesterday));
        textLast.setText(linkRadio + "%");
        int resId = linkRadio < 0 ? R.drawable.ic_arrow_downward_green_a700_24dp : R
                .drawable.ic_arrow_upward_red_a200_24dp;
        ViewHelper.drawTextBounds(textLast, resId, 0, 0, 0);
    }

    private void initPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieChart.setExtraOffsets(0.f, 10.f, 0.f, 10.f);
        pieChart.setOnChartValueSelectedListener(this);
    }

    private void setPieData() {
        String current = energyList.get(currentIndex).getTagShortName();
        pieChart.setCenterText(generateCenterSpannableText(getString(R.string.energy_pie_title,
                current)));

        PieData pieData = pieChart.getData();
        if (pieData != null) {
            pieData.clearValues();
        } else {
            pieData = new PieData();
        }

        List<PieEntry> yValues = new ArrayList<>();
        for (String key : childrenValues.keySet()) {
            yValues.add(new PieEntry(childrenValues.get(key), key));
        }

        PieDataSet pieDataSet = new PieDataSet(yValues, getString(R.string.energy_pie_title));
        pieDataSet.setColors(com.xseec.eds.widget.ColorTemplate.COLORS);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueTextSize(12f);
        pieData.addDataSet(pieDataSet);
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.animateX(ViewHelper.CHART_ANIMATE_DURATION);
        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText(String input) {
        SpannableString s = new SpannableString(input);
        int index1 = input.indexOf("\n");
        int index2 = input.indexOf(" ");
        s.setSpan(new RelativeSizeSpan(1.5f), 0, index1, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), index1, index2, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), index1, index2, 0);
        s.setSpan(new RelativeSizeSpan(.65f), index1, index2, 0);
        s.setSpan(new StyleSpan(Typeface.ITALIC), index2, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), index2, s.length
                (), 0);
        return s;
    }

    private void initBarCart() {
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0);
        barChart.setTouchEnabled(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
    }

    private void setBarData() {

        BarData barData = barChart.getBarData();
        if (barData != null) {
            barData.clearValues();
        } else {
            barData = new BarData();
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setLabelCount(childrenValues.size());
        xAxis.setValueFormatter(new ItemXAxisValueFormatter(childrenValues.keySet().toArray(new
                String[]{})));
        List<BarEntry> yValues = new ArrayList<>();
        int index = 0;
        for (String key : childrenValues.keySet()) {
            yValues.add(new BarEntry(index++, childrenValues.get(key)));
        }
        BarDataSet barDataSet = new BarDataSet(yValues, getString(R.string.energy_bar_title));
        barDataSet.setColors(com.xseec.eds.widget.ColorTemplate.COLORS);
        barData.addDataSet(barDataSet);
        barDataSet.setValueTextSize(12f);
        barChart.setData(barData);
        barChart.animateY(ViewHelper.CHART_ANIMATE_DURATION);
        barChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    private void requestDatalog() {
        progress.setVisibility(View.VISIBLE);
        textTime.setText(DateHelper.getStringByField(startTime, field));
        switch (field) {
            case Calendar.YEAR:
                countOfLast = 12;
                break;
            case Calendar.MONTH:
                Calendar lastTime = (Calendar) startTime.clone();
                lastTime.add(field, -1);
                countOfLast = lastTime.getActualMaximum(Calendar.DAY_OF_MONTH);
                break;
            case Calendar.DATE:
                countOfLast = 24;
                break;
            default:
                break;
        }
        //使用模拟值
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    updateChildren();
                    refreshViewsInThread(null);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int calSum(List<String> yValues) {
        int sum = 0;
        for (String value : yValues) {
            sum += Integer.parseInt(value);
        }
        return sum;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        scrollView.scrollTo(0, 0);
        PieEntry pieEntry = (PieEntry) e;
        for (EnergyTag tag : children) {
            if (tag.getTagShortName().equals(pieEntry.getLabel())) {
                onParentSelected(tag);
                break;
            }
        }
    }

    @Override
    public void onNothingSelected() {
    }

    @OnClick(R.id.text_time)
    public void onViewClicked() {
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog();
        switch (field) {
            case Calendar.DATE:
                new DatePickerDialog(getContext(), this, startTime.get(Calendar.YEAR), startTime
                        .get(Calendar.MONTH), startTime.get(Calendar.DATE)).show();
                break;
            case Calendar.YEAR:
                pickerDialog.hideMonth();
            case Calendar.MONTH:
                pickerDialog.setListener(this);
                pickerDialog.show(getFragmentManager(), "");
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progress.setVisibility(View.GONE);
        setLineData();
        int visibility = children.size() > 0 ? View.VISIBLE : View.GONE;
        barChart.setVisibility(visibility);
        pieChart.setVisibility(visibility);
        textTip.setVisibility(visibility);
        if (children.size() > 0) {
            setBarData();
            setPieData();
        }
    }

    @Override
    public void onParentSelected(EnergyTag currentLevel) {
        parents.clear();
        parents.addAll(currentLevel.getEnergyParents(energyList));
        levelAdapter.notifyDataSetChanged();
        currentIndex = energyList.indexOf(currentLevel);
        updateChildren();
        onRefreshViews(null);
    }

    private void updateChildren() {
        tagLogs = new List[energyList.size()];
        childrenValues = new ArrayMap<>();
        children = energyList.get(currentIndex).getEnergyChildren(energyList);
        for (int i = 0; i < energyList.size(); i++) {
            EnergyTag tag = energyList.get(i);
            String baseValue = tag.getTagValue();
            tagLogs[i] = Generator.genLastNowEntryList(startTime, field, baseValue);
            if (children.contains(tag)) {
                List<String> yValues = tagLogs[i].subList(countOfLast, tagLogs[i]
                        .size());
                childrenValues.put(tag.getTagShortName(), calSum(yValues));
            }
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        startTime.set(year, month, dayOfMonth);
        requestDatalog();
    }
}
