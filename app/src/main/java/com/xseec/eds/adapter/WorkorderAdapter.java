package com.xseec.eds.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.List;

/**
 * Created by Administrator on 2018/10/10.
 */

public class WorkorderAdapter extends RecyclerView.Adapter<WorkorderAdapter.ViewHolder> {

    private List<Workorder> workorderList;

    public WorkorderAdapter(List<Workorder> workorderList) {
        this.workorderList = workorderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_workorder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workorder workorder=workorderList.get(position);
        holder.textTitle.setText(workorder.getTitle());
        String time= EDSApplication.getContext().getString(R.string.workorder_time,DateHelper.getMDString(workorder.getStart()),DateHelper.getMDString(workorder.getEnd()));
        holder.textTime.setText(time);
        holder.textTask.setText(workorder.getTask());
        switch (workorder.getWorkorderState()){
            case DONE:
                holder.imageExecute.setImageResource(R.drawable.ic_done_blue_24dp);
                holder.textExecute.setText(R.string.workorder_done);
                break;
            case OVERDUE:
                holder.imageExecute.setImageResource(R.drawable.ic_warning_yellow_24dp);
                holder.textExecute.setText(R.string.workorder_overdue);
                break;
            default:
                holder.imageExecute.setImageResource(R.drawable.ic_access_time_grey_600_24dp);
                holder.textExecute.setText(R.string.workorder_due);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return workorderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textTitle;
        private TextView textTime;
        private TextView textTask;
        private TextView textExecute;
        private ImageView imageExecute;

        public ViewHolder(View itemView) {
            super(itemView);
            textTitle=itemView.findViewById(R.id.text_title);
            textTime=itemView.findViewById(R.id.text_time);
            textTask=itemView.findViewById(R.id.text_task);
            textExecute=itemView.findViewById(R.id.text_execute);
            imageExecute=itemView.findViewById(R.id.image_execute);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
