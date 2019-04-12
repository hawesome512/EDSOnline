package com.xseec.eds.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.tags.ValidTag;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.UserLevelHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabControlFragment extends ComFragment {

    private static final String KEY_CONTROL = "control";
    private static final int VALUE_ON = 0xAAAA;
    private static final int VALUE_OFF = 0x5555;
    @InjectView(R.id.text_state)
    TextView textState;
    @InjectView(R.id.btn_on)
    Button btnOn;
    @InjectView(R.id.btn_off)
    Button btnOff;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.layout_container)
    NestedScrollView layoutContainer;

    private int farCtrlCode = VALUE_OFF;

    public static Fragment newInstance(String deviceName, List<String> farControl) {
        Fragment fragment = new TabControlFragment();
        List<Tag> tags = new ArrayList<>();
        for (String name : farControl) {
            tags.add(new Tag(deviceName + ":" + name));
        }
        fragment.setArguments(getBundle(tags));
        return fragment;
    }

    public TabControlFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tab_control, container, false);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onRefreshed(final List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        for (int i = 0; i < validTagList.size(); i++) {
            tagList.get(i).setTagValue(validTagList.get(i).getTagValue());
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progress.setVisibility(View.GONE);
                List<Tag> tags = TagsFilter.filterDeviceTagList(validTagList, "Status");
                if (tags != null && tags.size() >= 1) {
                    State state = DeviceConverterCenter.getState(tags.get(0));
                    int bgColorRes;
                    boolean openable;
                    switch (state) {
                        case OFF:
                            bgColorRes = R.drawable.state_off_gradient;
                            openable = false;
                            break;
                        case ON:
                            bgColorRes = R.drawable.state_on_gradient;
                            openable = true;
                            break;
                        case ALARM:
                            bgColorRes = R.drawable.state_alarm_gradient;
                            openable = true;
                            break;
                        case TRIP:
                            bgColorRes = R.drawable.state_alarm_gradient;
                            openable = false;
                            break;
                        default:
                            bgColorRes = R.drawable.state_offline_gradient;
                            openable = false;
                            break;
                    }
                    textState.setBackgroundResource(bgColorRes);
                    textState.setText(DeviceConverterCenter.getStateText(tags.get(0)));
                    btnOff.setEnabled(openable);
                    btnOff.setBackgroundResource(openable ? R.color.colorOff : R.color
                            .colorGrayNormal);
                    btnOn.setEnabled(!openable);
                    btnOn.setBackgroundResource(openable ? R.color.colorGrayNormal : R.color
                            .colorOn);
                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick({R.id.btn_on, R.id.btn_off})
    public void onViewClicked(View view) {
        //nj--远程合分闸用户权限检查
        boolean Usability=UserLevelHelper.checkTabFragment();
        if (!Usability){
            User user= WAServicer.getUser();
            String info=getString( R.string.device_modify_control,user.getUserType().getTypeName(getContext()));
            hintUserLevel( info );
            return;
        }
        checkCtrlAuthority(REQUEST_CONTROL_AUTHORITY);
        switch (view.getId()) {
            case R.id.btn_on:
                farCtrlCode = VALUE_ON;
                break;
            case R.id.btn_off:
                farCtrlCode = VALUE_OFF;
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONTROL_AUTHORITY:
                //输入正确设备密码
                if (resultCode == Activity.RESULT_OK) {
                    List<ValidTag> targets = new ArrayList<>();
                    List<Tag> tags = TagsFilter.filterDeviceTagList(tagList, "FarCtrl");
                    if (tags != null) {
                        targets.add(new ValidTag(tags.get(0).getTagName(), String.valueOf
                                (farCtrlCode)));
                        onModifyTags(targets, layoutContainer);

                        //nj--添加远程合分闸操作记录 18/11/05
                        String deviceName=tags.get( 0 ).getTagName();
                        Device device=Device.initWith( deviceName);
                        String actionDevice=device.getDeviceAlias();
                        String actionInfo;
                        switch (farCtrlCode){
                            case VALUE_ON:
                                actionInfo=getString( R.string.action_devic_close,actionDevice);
                                break;
                            default:
                                actionInfo=getString( R.string.action_devic,actionDevice );
                                break;
                        }
                            RecordHelper.actionLog(actionInfo);
                    }
                }
                break;
            default:
                break;
        }
    }
}
