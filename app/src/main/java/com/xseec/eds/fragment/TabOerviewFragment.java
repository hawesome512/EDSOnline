package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.xseec.eds.R;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.util.EDSApplication;
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
public class TabOerviewFragment extends Fragment {


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

    private List<Tag> tagList;
    private int[] colors={getColor(R.color.colorPrimaryLight),getColor(R.color.colorAccentLight),getColor(R.color.colorAccent)};

    private static final String ARG_TAGS="tags";
    public static Fragment newInstance(List<Tag> tagList){
        TabOerviewFragment fragment=new TabOerviewFragment();
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList(ARG_TAGS,(ArrayList<Tag>)tagList);
        fragment.setArguments(bundle);
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
        XAxis xAxis=barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new PhaseXAxisValueFormatter());
        xAxis.setLabelCount(3);
        barChart.getLegend().setEnabled(false);
        barChart.animateY(1500);

        YAxis yAxis=barChart.getAxisLeft();
        yAxis.addLimitLine(genLimitLine(90,colors[0]));
        yAxis.addLimitLine(genLimitLine(105,colors[1]));
        yAxis.addLimitLine(genLimitLine(120,colors[2]));
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(120);
        yAxis.setLabelCount(4);

        List<BarEntry> entries=new ArrayList<>();
        entries.add(new BarEntry(0,100));
        entries.add(new BarEntry(1,90));
        entries.add(new BarEntry(2,120));
        BarDataSet dataSet=new BarDataSet(entries,"");
        dataSet.setHighlightEnabled(false);
        dataSet.setValueTextSize(14);
        dataSet.setColors(colors[1],colors[0],colors[2]);
        BarData barData=new BarData(dataSet);
        barChart.setData(barData);
    }

    private LimitLine genLimitLine(float value,int color){
        LimitLine limitLine=new LimitLine(value,"");
        limitLine.setLineWidth(2);
        limitLine.setLineColor(color);
        limitLine.enableDashedLine(20,20,0);
        return limitLine;
    }

    private int getColor(int colorRes){
        return ContextCompat.getColor(EDSApplication.getContext(),colorRes);
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
}
