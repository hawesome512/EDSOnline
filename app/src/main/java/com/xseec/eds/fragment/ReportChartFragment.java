package com.xseec.eds.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.widget.MyMarkerView;
import com.xseec.eds.widget.TimeXAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
//nj--暂时报表温度、湿度曲线图查看 2018/12/6
public class ReportChartFragment extends BaseFragment {

    public static final String EXT_FACTOR="factor";
    public static final String EXT_VALUES="values";
    public static final String EXT_TAGS="tags";

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.progress_data_log)
    ProgressBar progressDataLog;
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

    private List<String> tagName;
    private DataLogFactor defaultFactor;
    private List<String> value;
    List<String>[] values;
    public static ReportChartFragment newInstant(DataLogFactor factor,List<String> values,
                                                 List<String> tagName){
        Bundle bundle=new Bundle(  );
        bundle.putParcelable( EXT_FACTOR,factor );
        bundle.putStringArrayList( EXT_VALUES,(ArrayList)values);
        bundle.putParcelableArrayList( EXT_TAGS, (ArrayList) tagName );
        ReportChartFragment fragment=new ReportChartFragment();
        fragment.setArguments( bundle );
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_data_log, container, false );
        ButterKnife.inject( this, view );
        getActivity().setTitle( R.string.detail_trend );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(),toolbar,
                R.drawable.ic_arrow_back_white_24dp );
        progressDataLog.setVisibility( View.GONE );
        initData();
        initChart();
        onRefreshView();
        return view;
    }

    private void initData(){
        Bundle bundle=this.getArguments();
        defaultFactor=bundle.getParcelable( EXT_FACTOR );
        value=bundle.getStringArrayList( EXT_VALUES );
        tagName=bundle.getStringArrayList( EXT_TAGS );
        values=new List[1];
        values[0]=value;
    }

    private void initChart() {
        //lineChart.animateX(2000);
        lineChart.getDescription().setEnabled(false);

        MyMarkerView mv = new MyMarkerView(getContext(), R.layout.custom_marker_view);
        mv.setChartView(lineChart); // For bounds control

        YAxis yAxis = lineChart.getAxisRight();
        yAxis.setEnabled(false);

        lineChart.setMarker(mv);

        //横坐标设定
        XAxis xAxis = lineChart.getXAxis();
        IAxisValueFormatter iAxisValueFormatter = new TimeXAxisValueFormatter( lineChart,
                defaultFactor );
        xAxis.setValueFormatter( iAxisValueFormatter );

        LineData data = lineChart.getData();
        if (data != null) {
            data.clearValues();
        }
    }

    private void onRefreshView(){
        getActivity().runOnUiThread( new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        } );
    }

    private void loadData(){
        if (values==null||values.length == 0) {
            return;
        }
        LineData lineData = new LineData();
        String legenName = null;

        //暂时方案,模拟值
        LineDataSet lineDataSet1 = null;
        int[] colors=new int[]{R.color.colorPhaseA,R.color.colorPhaseB,R.color.colorPhaseC,R.color.colorGrayNormal};
        List<Entry> entryLists=new ArrayList<>();
        int l=values.length;
        for (int i = 0; i < l; i++) {
            List<String> value = values[i];
            List<Entry> entryList = Generator.convertEntryList(value,0);
            entryLists.addAll(entryList);
            legenName=tagName.get( i ).split( ":" )[1];
//            legenName=(i==0&&legenName!=null)?legenName:storedTagList.get(i).getTagShortName();
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
        textFirst.setText(String.valueOf(lineData.getYMax()));
        textSecend.setText(String.valueOf(lineData.getYMin()));
        textLast.setText(String.valueOf(Generator.getAvgFromEntryList(entryLists)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }
}
