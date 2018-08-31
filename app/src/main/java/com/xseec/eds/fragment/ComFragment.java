package com.xseec.eds.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.xseec.eds.model.ComListener;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.service.ComService;

import java.util.Arrays;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Administrator on 2018/7/14.
 */

public abstract class ComFragment extends Fragment implements ComListener {

    private static final String KEY_TAGS = "tag_list";

    protected static Bundle getBundle(List<Tag> tagList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(KEY_TAGS, tagList.toArray(new Tag[tagList.size()]));
        return bundle;
    }

    protected List<Tag> tagList;
    //在unbind service过程中经常出现service not registered
    private boolean binded = false;

    private ComService.ComBinder comBinder;
    protected ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            comBinder = (ComService.ComBinder) service;
            comBinder.startCom(ComFragment.this, tagList);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Tag[] tags = (Tag[]) getArguments().getParcelableArray(KEY_TAGS);
            tagList = Arrays.asList(tags);
        }
    }

    /*
    * 问题：在ViewPager中的Fragment切换时，不会正确触发onStart,故此不能在此绑定
    * 解决方案：在ViewPager切换fragment时，设定fragment可见属性，触发绑定、解绑服务
    * onStop:关闭屏幕或销毁fragment时即中断服务
    */
    @Override
    public void onStart() {
        super.onStart();
//        onBindService();
    }

    @Override
    public void onStop() {
        super.onStop();
        onUnbindService();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onBindService();
        } else {
            onUnbindService();
        }
    }

    protected void onBindService() {
        //DeviceActivity在Resume中触发，context可能为null
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), ComService.class);
            getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            binded = true;
        }
    }

    protected void onUnbindService() {
        if (comBinder != null) {
            comBinder.cancelCom();
        }
        if (binded == true && isServiceRunning(ComService.class)) {
            getActivity().unbindService(serviceConnection);
        }
        binded = false;
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) (getActivity().getSystemService
                (ACTIVITY_SERVICE));
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer
                .MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRefreshed(List<Tag> validTagList) {
        Activity activity=getActivity();
        if(validTagList==null||validTagList.size()==0||getActivity()==null){
            return;
        }
    }
}
