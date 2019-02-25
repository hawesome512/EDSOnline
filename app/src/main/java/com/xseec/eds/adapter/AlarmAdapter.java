package com.xseec.eds.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.ChartActivity;
import com.xseec.eds.activity.DeviceActivity;
import com.xseec.eds.activity.WorkorderActivity;
import com.xseec.eds.activity.WorkorderCreatorActivity;
import com.xseec.eds.fragment.AlarmListFragment;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.Generator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Administrator on 2018/11/8.
 */

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    private List<Alarm> alarmList;
    private Context context;

    public AlarmAdapter(List<Alarm> alarmList, Context context) {
        this.alarmList = alarmList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alarm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Alarm alarm = alarmList.get(position);
        Device device = DeviceConverterCenter.initWith(alarm.getDevice());
        //Device device=Device.initWith(alarm.getDevice());
        String status = Generator.getAlarmStateText(alarm.getAlarmCode(), device.getStatusItems());
        holder.textAlarm.setText(status);
        holder.imageDevice.setImageResource(device.getDeviceResId());
        int bgRes = alarm.checked() ? R.drawable.primary_gradient : R.drawable.alarm_gradient;
        holder.imageDevice.setBackgroundResource(bgRes);
        holder.imageState.setVisibility(alarm.checked() ? View.VISIBLE : View.GONE);
        holder.textDevice.setText(context.getString(R.string.alarm_device, device.getDeviceAlias
                ()));
        String time = DateHelper.getString(alarm.getTime());
        holder.textTime.setText(context.getString(R.string.alarm_time, time));
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageDevice;
        private ImageView imageState;
        private ImageView imageTrend;
        private ImageView imageWorkorder;
        private ImageView imageGoto;
        private TextView textAlarm;
        private TextView textDevice;
        private TextView textTime;

        public ViewHolder(View itemView) {
            super(itemView);
            imageDevice = itemView.findViewById(R.id.image_device);
            imageState = itemView.findViewById(R.id.image_state);
            imageTrend = itemView.findViewById(R.id.image_trend);
            imageWorkorder = itemView.findViewById(R.id.image_workorder);
            imageGoto = itemView.findViewById(R.id.image_goto);
            textAlarm = itemView.findViewById(R.id.text_alarm);
            textDevice = itemView.findViewById(R.id.text_device);
            textTime = itemView.findViewById(R.id.text_time);
            imageGoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = alarmList.get(getAdapterPosition());
                    String deviceName = alarm.getDevice();
                    DeviceActivity.start(context, deviceName);
                }
            });
            imageWorkorder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = alarmList.get(getAdapterPosition());
                    if (alarm.checked()) {
                        WorkorderActivity.start(context,alarm.getReport());
                    } else {
                        WorkorderCreatorActivity.start((Activity) context, alarm,
                                AlarmListFragment.REQUEST_CREATE);
                    }
                }
            });
            imageTrend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Alarm alarm = alarmList.get(getAdapterPosition());
                    Device device=Device.initWith(alarm.getDevice());
                    List<String> params=device.getDeviceConfig().getAlarmParams();
                    ArrayList<String> tagNames=new ArrayList<>();
                    for(String param:params){
                        tagNames.add(alarm.getDevice()+":"+param);
                    }
                    DataLogFactor factor=new DataLogFactor();
                    Calendar startTime=DateHelper.getCalendarWithDate(alarm.getTime());
                    startTime.add(Calendar.MINUTE,-3);
                    factor.setStartTime(startTime);
                    ChartActivity.start(context,tagNames, factor);
                }
            });
        }
    }
}
