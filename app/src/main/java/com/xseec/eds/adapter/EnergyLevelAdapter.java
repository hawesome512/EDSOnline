package com.xseec.eds.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.tags.EnergyTag;

import java.util.List;

/**
 * Created by Administrator on 2018/12/3.
 */

public class EnergyLevelAdapter extends RecyclerView.Adapter<EnergyLevelAdapter.ViewHolder> {

    private Context context;
    //父级链包含本身
    private List<EnergyTag> parentList;
    private ParentListener listener;

    public EnergyLevelAdapter(Context context, List<EnergyTag> levelList, ParentListener listener) {
        this.context = context;
        this.parentList = levelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_energy_tag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textLevel.setText(parentList.get(position).getAlias());
    }

    @Override
    public int getItemCount() {
        return parentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textLevel;

        public ViewHolder(View itemView) {
            super(itemView);
            textLevel = (TextView) itemView;
            textLevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //点击当前级不触发重新加载数据
                    if (getAdapterPosition() < parentList.size() - 1) {
                        listener.onParentSelected(parentList.get(getAdapterPosition()));
                    }
                }
            });
        }
    }

    public interface ParentListener {
        void onParentSelected(EnergyTag currentLevel);
    }
}
