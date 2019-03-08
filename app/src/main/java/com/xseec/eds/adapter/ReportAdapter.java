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
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.widget.ReportItemView;

import java.util.ArrayList;
import java.util.List;

public class ReportAdapter extends  RecyclerView.Adapter<ReportAdapter.ViewHolder> {

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

        holder.item.setTitleLeft( report.getLeftTitle() );
        holder.item.setTitleCenter( report.getCenterTitle() );
        holder.item.setTitleRight( report.getRightTitle() );

        holder.item.setValueLeft( report.getLeftValue() );
        holder.item.setValueCenter( report.getCenterValue() );
        holder.item.setValueRight( report.getRightValue() );
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
                    if (reportList.get( position ).getReportType()==0){
                        //nj--执行工单、操作、异常点击事件 2018/12/6
                        onDeviceClick( v,position );
                    }
                }
            } );
        }
    }

    private void onDeviceClick(View v,int position) {
        switch (v.getId()) {
            case R.id.item_left:
                List leftList = reportList.get( position ).getLeftList();
                ReportActivity.start( context, position, leftList );
                break;
            case R.id.item_center:
                List centerList= reportList.get( position ).getCenterList();
                ReportActivity.start( context, position, centerList );
                break;
            case R.id.item_right:
                List rightList = reportList.get( position ).getRightList();
                ReportActivity.start( context, position, rightList );
                break;
        }
    }

}


