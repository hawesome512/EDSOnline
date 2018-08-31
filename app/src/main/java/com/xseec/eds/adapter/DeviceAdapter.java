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
import com.xseec.eds.activity.DeviceActivity;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.EDSApplication;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/8/16.
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private List<Tag> stateTags;

    public DeviceAdapter(List<Tag> stateTags) {
        this.stateTags = stateTags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device,parent,false);
        ViewHolder holder=new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context= EDSApplication.getContext();
        Tag tag= stateTags.get(position);
        State state=State.getState(tag.getTagValue());
        Device device=Device.initWithTagName(tag.getTagName());;
        String stateText=context.getString(R.string.detail_state,state.getStateText());
        holder.imageDevice.setImageResource(device.getDeviceResId());
        holder.textState.setText(stateText);
        holder.textName.setText(device.getDeviceAlias());
        holder.imageState.setImageResource(state.getStateColorRes());
        state.setUnusualAnimator(holder.imageState);
    }

    @Override
    public int getItemCount() {
        return stateTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imageDevice;
        private TextView textName;
        private TextView textState;
        private CircleImageView imageState;

        public ViewHolder(final View itemView) {
            super(itemView);
            imageDevice=itemView.findViewById(R.id.image_device);
            textName=itemView.findViewById(R.id.text_device_name);
            textState=itemView.findViewById(R.id.text_device_state);
            imageState=itemView.findViewById(R.id.image_device_state);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tagName=stateTags.get(getAdapterPosition()).getTagName();
                    DeviceActivity.start(itemView.getContext(),tagName);
                }
            });
        }
    }
}
