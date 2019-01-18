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
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/8/16.
 * 两种标签类型：区域标签，设备标签
 * 区域标签处于折叠状态时，其子设备不显示
 * allItems:所有区域标签不折叠，默认
 * showItems:实际显示状态
 * collapsState:区域标签折叠状态，默认：不折叠<false>
 * clllapsNum:区域标签下子设备数量
 */

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

    private static final int TYPE_ZONE = 0;
    private static final int TYPE_DEVICE = 1;

    private Context context;
    private List<Tag> stateTags;
    private List<String> allItems;
    private List<String> showItems;
    private LinkedHashMap<String, Boolean> collapsState;
    private LinkedHashMap<String, Integer> collapsNum;

    public DeviceAdapter(Context context, List<Tag> stateTags) {
        this.context = context;
        this.stateTags = stateTags;
        allItems = new ArrayList<>();
        collapsState = new LinkedHashMap<>();
        collapsNum=new LinkedHashMap<>();
        for (Tag tag : stateTags) {
            String zone = tag.getTagName().split(Tag.SPIT_ZONE)[0];
            if (!allItems.contains(zone)) {
                allItems.add(zone);
                collapsState.put(zone, false);
                collapsNum.put(zone, 1);
            } else {
                collapsNum.put(zone, collapsNum.get(zone) + 1);
            }
            allItems.add(tag.getTagName());
        }
        updateShowItems();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int lyRes = viewType == TYPE_ZONE ? R.layout.item_zone : R.layout.item_device;
        View view = LayoutInflater.from(parent.getContext()).inflate(lyRes,
                parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Context context = EDSApplication.getContext();
        String item = showItems.get(position);
        Device device = Device.initWithTagName(item);
        if (device == null) {
            //区域标签
            String alias=Device.getAliasMap().containsKey(item)?Device.getAliasMap().get(item):item;
            holder.textName.setText(alias);
            holder.textValue.setText(collapsNum.get(item).toString());
            if (ApiLevelHelper.isAtLeast(17)) {
                int resId = collapsState.get(item) ? R.drawable.ic_keyboard_arrow_up_blue_500_24dp : R
                        .drawable.ic_keyboard_arrow_down_blue_500_24dp;
                holder.textValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, resId, 0);
            }
        } else {
            //设备标签
            Tag tag = TagsFilter.filterTagList(stateTags,item).get(0);
            State state = DeviceConverterCenter.getState(tag);
            String stateText = context.getString(R.string.detail_state, DeviceConverterCenter.getStateText(tag));
            holder.imageDevice.setImageResource(device.getDeviceResId());
            holder.textValue.setText(stateText);
            holder.textName.setText(device.getDeviceAlias());
            holder.imageState.setImageResource(state.getStateColorRes());
            //state.setUnusualAnimator(holder.imageState);
            //最后一个设备标签不显示分割线，美化效果
            if(position==showItems.size()-1||Device.initWithTagName(showItems.get(position+1))==null){
                holder.viewDivider.setVisibility(View.GONE);
            }else {
                holder.viewDivider.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        Device device = Device.initWithTagName(showItems.get(position));
        return device == null ? TYPE_ZONE : TYPE_DEVICE;
        //return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return showItems.size();
        //return stateTags.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageDevice;
        private TextView textName;
        private TextView textValue;
        private View viewDivider;
        private CircleImageView imageState;

        public ViewHolder(View itemView) {
            super(itemView);
            textName = itemView.findViewById(R.id.text_device_name);
            textValue = itemView.findViewById(R.id.text_device_value);
            imageDevice = itemView.findViewById(R.id.image_device);
            imageState = itemView.findViewById(R.id.image_device_state);
            viewDivider=itemView.findViewById(R.id.view_divider);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String item = showItems.get(getAdapterPosition());
                    Device device = Device.initWithTagName(item);
                    if (device == null) {
                        boolean collaps=collapsState.get(item);
                        collapsState.put(item,!collaps);
                        updateShowItems();
                        notifyDataSetChanged();
                    } else {
                        DeviceActivity.start(context, item);
                    }
                }
            });
        }
    }

    private void updateShowItems(){
        showItems=new ArrayList<>();
        int index=0;
        //区域折叠状态，忽略子设备
        for(Map.Entry<String,Boolean> entry:collapsState.entrySet()){
            int length=entry.getValue()?1:1+collapsNum.get(entry.getKey());
            showItems.addAll(allItems.subList(index,index+length));
            index+=1+collapsNum.get(entry.getKey());
        }
    }
}
