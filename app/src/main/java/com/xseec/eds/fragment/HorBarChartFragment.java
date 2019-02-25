package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.xseec.eds.widget.THDXAxisValueFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HorBarChartFragment extends BaseFragment {


    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.hor_bar_chart)
    HorizontalBarChart chart;

    List<Tag> tagList;

    private static final String ARG_TAGS = "tags";
    @InjectView(R.id.progress_data_log)
    ProgressBar progress;

    public static Fragment newInstance(List<String> tagNames) {
        Fragment fragment = new HorBarChartFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(ARG_TAGS, (ArrayList<String>) tagNames);
        fragment.setArguments(bundle);
        return fragment;
    }

    public HorBarChartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hor_bar_chart, container, false);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, ViewHelper
                .DEFAULT_HOME_RES);
        getActivity().setTitle(R.string.overview_item_harmonic);
        List<String> tagNames = getArguments().getStringArrayList(ARG_TAGS);
        tagList = TagsFilter.filterTagList(null, tagNames.get(0));
        initChart();
        return view;

    }

    private void initChart() {

        XAxis xl = chart.getXAxis();
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(true);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new THDXAxisValueFormatter());

        YAxis yl = chart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis yr = chart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(false);
        yr.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);

        setData();
    }

    private void setData() {
        progress.setVisibility(View.VISIBLE);
        WAServiceHelper.sendGetValueRequest(tagList, new
                Callback() {

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
    protected void onRefreshViews(String jsonData) {
        progress.setVisibility(View.GONE);

        ArrayList<Tag> tagList = (ArrayList<Tag>) WAJsonHelper.refreshTagValue(jsonData);
        //HorBarChart中，BarEntry.x最大的值显示在顶部，翻转数组使显示数据自上而下为：THD:I,U,thd:u,i
        //注意匹配THDXAxisValueFormatter
        Collections.reverse(tagList);
        ArrayList<BarEntry> yVals = new ArrayList<>();
        for (int i = 0; i < tagList.size(); i++) {
            yVals.add(new BarEntry(i, Generator.floatTryParse(tagList.get(i).getTagValue())));
        }

        BarDataSet set1;
        set1 = new BarDataSet(yVals.subList(0, tagList.size() / 2), getString(R.string
                .detail_thd_v));
        set1.setColors(ContextCompat.getColor(getContext(), R.color.colorAccentLight));

        BarDataSet set2;
        set2 = new BarDataSet(yVals.subList(tagList.size() / 2, tagList.size()), getString(R.string.detail_thd_c));
        set2.setColors(ContextCompat.getColor(getContext(), R.color.colorPrimaryLight));

        BarData data = new BarData(set1, set2);
        data.setValueTextSize(10f);
        chart.getXAxis().setLabelCount(yVals.size());
        chart.setData(data);
        chart.animateY(2000);
        chart.invalidate();
    }
}
