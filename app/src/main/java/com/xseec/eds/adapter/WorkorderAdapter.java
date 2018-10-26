package com.xseec.eds.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.WorkorderActivity;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.List;

/**
 * Created by Administrator on 2018/10/10.
 */

public class WorkorderAdapter extends RecyclerView.Adapter<WorkorderAdapter.ViewHolder> {

    private List<Workorder> workorderList;
    private Context context;

    public WorkorderAdapter(Context context,List<Workorder> workorderList) {
        this.context=context;
        this.workorderList = workorderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item_workorder,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Workorder workorder=workorderList.get(position);
        holder.textTitle.setText(workorder.getTitle());
        holder.textTime.setText(workorder.getDateRange());
        holder.textTask.setText(workorder.getTask());
        holder.imageExecute.setImageResource(workorder.getStateImgRes());
        holder.textExecute.setText(workorder.getStateTextRes());
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
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WorkorderActivity.start(context,workorderList.get(getAdapterPosition()));
                }
            });
        }
    }
}
