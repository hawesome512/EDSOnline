package com.xseec.eds.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.ChartActivity;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.StoredTag;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.EDSApplication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/7/25.
 */

public class OverviewAdapter extends RecyclerView.Adapter<OverviewAdapter.ViewHolder> {

    private List<OverviewTag> tagList;

    public OverviewAdapter(List<OverviewTag> tagList) {
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_overview,
                parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OverviewTag tag = tagList.get(position);
        holder.imageItem.setImageResource(tag.getTagResId());
        holder.textName.setText(tag.getTagName());
        holder.textValue.setText(tag.getTagValue());
        holder.textUnit.setText(tag.getTagUnit());
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageItem;
        private TextView textName;
        private TextView textValue;
        private TextView textUnit;

        public ViewHolder(View itemView) {
            super(itemView);
            imageItem = itemView.findViewById(R.id.image_item);
            textName = itemView.findViewById(R.id.text_name);
            textValue = itemView.findViewById(R.id.text_value);
            textUnit = itemView.findViewById(R.id.text_unit);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tagName = tagList.get(getAdapterPosition()).getMapTagName();
                    DataLogFactor factor;
                    if (tagName.contains("Energy")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, -1);
                        Calendar yesterday = DateHelper.getDayStartTime(calendar);
                        factor = new DataLogFactor(yesterday, StoredTag.IntervalType.H, 1, 48);
                    } else {
                        factor = new DataLogFactor();
                    }
                    StoredTag.IntervalType intervalType = tagName.contains("Energy") ? StoredTag
                            .IntervalType.H : StoredTag.IntervalType.M;
                    ArrayList<String> tags = new ArrayList<>();
                    tags.add(tagName);
                    ChartActivity.start(EDSApplication.getContext(), tags, factor);
                }
            });
        }
    }

}
