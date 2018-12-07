package com.xseec.eds.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xseec.eds.R;
import com.xseec.eds.activity.ReportActivity;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.Report;
import com.xseec.eds.util.ReportHelper;
import com.xseec.eds.widget.ReportItemView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends  RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    public final static String REPORT_TEMPERATURE = "XRD:Tempreture";
    public final static String REPORT_HUMIDITY = "XRD:Humidity";

    private List<Report> reportList;
    private Context context;
    private DataLogFactor factor;

    public ReportAdapter(Context context,List<Report> reportList,DataLogFactor factor){
        this.context=context;
        this.reportList=reportList;
        this.factor=factor;
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from( context ).inflate( R.layout.item_report, parent, false );
        return new ViewHolder( view );
    }

    @Override
    public void onBindViewHolder(ReportAdapter.ViewHolder holder, int position) {

        Report report = reportList.get( position );
        holder.item.setTitle( report.getTitle() );
        holder.item.setTitleLeft( report.getLeft() );
        holder.item.setTitleCenter( report.getCenter() );
        holder.item.setTitleRight( report.getRight() );

        //nj--根据不同列表类型加载不同值 2018/12/6
        if (report.getEnvironmentList()==null){
            holder.item.setValueLeft( report.getValueLeft() );
            holder.item.setValueCenter( report.getValueCenter() );
            holder.item.setValueRight( report.getValueRight() );
        }else {
            List<String> list=report.getEnvironmentList();
            holder.item.setValueLeft( ReportHelper.getMax( list ) );
            holder.item.setValueCenter( ReportHelper.getMin( list ) );
            holder.item.setValueRight( ReportHelper.getAve( list ) );
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ReportItemView item;

        public ViewHolder(View itemView) {
            super( itemView );
            item = itemView.findViewById( R.id.report_item );
            item.setClickListener( new ReportItemView.ClickListener() {
                @Override
                public void Click(View v) {
                    int position=getAdapterPosition();
                    if (position<3){
                        //nj--执行工单、操作、异常点击事件 2018/12/6
                        onDeviceClick( v,position );
                    }else{
                        //nj--执行温度、湿度点击事件 2018/12/6
                        onEnverimentItemClicked( v,position );
                    }
                }
            } );
        }
    }

    private void onDeviceClick(View v,int position) {
        List list = null;
        switch (v.getId()) {
            case R.id.item_left:
                list = reportList.get( position ).getLeftList();
                break;
            case R.id.item_center:
                list= reportList.get( position ).getCenterList();
                break;
            case R.id.item_right:
                list = reportList.get( position ).getRightList();
                break;
        }
        ReportActivity.start( context, position, list );
    }

    private void onEnverimentItemClicked(View v,int position) {
        ArrayList<String> tags = new ArrayList<>();
        if (position==3) {
            tags.add( REPORT_TEMPERATURE );
        } else {
            tags.add( REPORT_HUMIDITY );
        }
        List<String> valueList = reportList.get( position ).getEnvironmentList();
        ReportActivity.start( context, factor, tags, valueList );
    }
}


