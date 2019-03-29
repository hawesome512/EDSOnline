package com.xseec.eds.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.xseec.eds.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/3/26.
 */

public class WorkorderTaskAdapter extends RecyclerView.Adapter<WorkorderTaskAdapter.ViewHolder> {

    private Context context;

    public Map<String, Boolean> getTaskMap() {
        return taskMap;
    }

    private Map<String,Boolean> taskMap;
    private List<String> tasks;
    private boolean editable = false;

    public WorkorderTaskAdapter(Context context,Map<String,Boolean> taskMap) {
        this.context = context;
        this.taskMap=taskMap;
        tasks=new ArrayList<>(taskMap.keySet());
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_workorder_task, parent,
                false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String task=tasks.get(position);
        holder.checkedTextView.setText(task);
        holder.checkedTextView.setChecked(taskMap.get(task));
    }

    @Override
    public int getItemCount() {
        return taskMap.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckedTextView checkedTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkedTextView = (CheckedTextView) itemView;
            checkedTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editable) {
                        checkedTextView.toggle();
                        taskMap.put(tasks.get(getAdapterPosition()),checkedTextView.isChecked());
                    }
                }
            });
        }
    }
}
