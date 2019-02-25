package com.xseec.eds.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.VerificationCodeActivity;
import com.xseec.eds.model.ComListener;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.tags.ValidTag;
import com.xseec.eds.service.ComService;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by Administrator on 2018/7/14.
 */

public abstract class ComFragment extends Fragment implements ComListener {

    private static final String KEY_TAGS = "tag_list";
    private static final String CTRL_LOCAL = "76";

    protected static Bundle getBundle(List<Tag> tagList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArray(KEY_TAGS, tagList.toArray(new Tag[tagList.size()]));
        return bundle;
    }

    protected List<Tag> tagList;
    //在unbind service过程中经常出现service not registered
    private boolean binded = false;
    protected static final int REQUEST_PROTECT_AUTHORITY = 1;
    protected static final int REQUEST_CONTROL_AUTHORITY = 11;
    protected boolean hasCode = false;

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

    /*
     * 解除绑定放在onStop中存在问题
     * 初始→Com1 onBind→调用Com2→Com2 onBind→Com1被Com2覆盖触发onStop
     * 此时执行的是Com2 的onUnbind,相对于Com1继续运行，Com2被停止
     */
    @Override
    public void onStop() {
        super.onStop();
//        onUnbindService();
    }

    @Override
    public void onPause() {
        super.onPause();
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
        if (getContext() != null && binded == false) {
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
        DeviceConverterCenter.convert(validTagList);
        Activity activity = getActivity();
        if (validTagList == null || validTagList.size() == 0 || getActivity() == null) {
            return;
        }
    }

    protected boolean checkCtrlAuthority(int authorityCode) {
        List<Tag> ctrlTags = DeviceConverterCenter.getCtrlList(tagList);
        if (ctrlTags.size() == 2) {
            String mode = ctrlTags.get(0).getTagValue();
            if (mode == null) {
                Toast.makeText(getContext(), R.string.device_modify_null, Toast.LENGTH_SHORT)
                        .show();
                return false;
            } else if (mode.equals(CTRL_LOCAL)) {
                //极小概率触发：初始远程模式，已有密码，切换本地模式，仍能遥调
                hasCode = false;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                        .setTitle(R.string.device_modify_refuse)
                        .setMessage(R.string.device_modify_local)
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return false;
            }
            if (!hasCode) {
                int code = Integer.valueOf(ctrlTags.get(1).getTagValue());
                String BCDCode = String.format("%04x", code);
                //需分别参数修改和远程操作的请求码，若相同同时出发其onActivityResult
                VerificationCodeActivity.start(getActivity(), authorityCode, BCDCode);
            }
        }
        return true;
    }

    protected void onModifyTags(List<ValidTag> targets, final View view) {
        WAServiceHelper.sendSetValueRequest(targets, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Snackbar.make(view, R.string.device_modify_success, Snackbar
                        .LENGTH_SHORT).show();
            }
        });
    }
}
