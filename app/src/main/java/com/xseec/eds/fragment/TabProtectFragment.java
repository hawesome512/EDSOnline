package com.xseec.eds.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.activity.ProtectSettingActivity;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.tags.ValidTag;
import com.xseec.eds.util.Device.DeviceConverterCenter;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.RecordHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.UserLevelHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabProtectFragment extends TabBaseFragment {

    private static final String KEY_PROTECTS = "protects";
    public static final String RESULT_TAG = "tag";
    private static final int REQUEST_MODIFY = 2;
    private static final String NAME_IR = "Ir";
    private static final String NAME_TR = "Tr";
    private static final String NAME_ISD = "Isd";
    private static final String NAME_TSD = "Tsd";
    private static final String NAME_II = "Ii";
    private static final String NAME_IG = "Ig";
    private static final String NAME_TG = "Tg";
    private static final String NAME_SWITCH = "Switch";

    private List<Protect> protectList;
    private String strSwitchOff;
    private Tag modifyTag;
    private ProgressBar modifyProgress;
    private TextView textValue;
    private static final int REPEAT_LIMIT = 20;
    private int modifyRepeat;

    //nj--创建设备参数操作记录数值 18\11\05
    private String actionDevic;
    private String actionName;
    private String oldActionValue;
    private String newActionValue;

    public static Fragment newInstance(String deviceName, List<Protect> protectList) {
        Fragment fragment = new TabProtectFragment();
        List<Tag> tags = new ArrayList<>();
        for (Protect protect : protectList) {
            tags.add(new Tag(deviceName + ":" + protect.getName()));
        }
        Bundle bundle = getBundle(tags);
        bundle.putParcelableArrayList(KEY_PROTECTS, (ArrayList<Protect>) protectList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLayout() {
        protectList = getArguments().getParcelableArrayList(KEY_PROTECTS);
        strSwitchOff = getString(R.string.device_protect_off);
        Map<Integer, List<String>> cards = DeviceConverterCenter.genProtectCardMaps(tagList);
        for (Map.Entry<Integer, List<String>> kv : cards.entrySet()) {
            addCard(kv.getKey(), kv.getValue());
        }
    }

    @Override
    public void onRefreshed(List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        List<Tag> switchTags = TagsFilter.filterDeviceTagList(validTagList, NAME_SWITCH);
        String switchValue = switchTags.get(0).getTagValue();
        List<String> switchItems = getProtect(NAME_SWITCH).getItems();
        for (int i = 0; i < validTagList.size(); i++) {
            String value = "";
            Tag tag = validTagList.get(i);
            boolean isOff;
            String alias = tag.getTagShortName();
            switch (alias) {
                case NAME_IR:
                case NAME_ISD:
                case NAME_II:
                case NAME_IG:
                    String tagValue = tag.getTagValue();
                    //eg. TabProtect统一电流显示单位“A",以xIr,xIn为单位的需调整
                    String xUnit = getXUnit(alias);
                    if (!TextUtils.isEmpty(xUnit)) {
                        Tag base = TagsFilter.filterDeviceTagList(tagList, xUnit).get(0);
                        tagValue = Generator.calFloatValue(base.getTagValue(), tagValue, Generator
                                .Operator.MULTIPLY);
                    }
                    isOff = Generator.checkProtectStateZero(switchValue, switchItems, alias);
                    value = isOff ? strSwitchOff : tagValue;
                    break;
                case NAME_TSD:
                case NAME_TG:
                    isOff = Generator.checkProtectStateZero(switchValue, switchItems, alias);
                    value += (isOff ? Protect.I2T_OFF : Protect.I2T_ON) + tag.getTagValue();
                    break;
                default:
                    value = tag.getTagValue();
            }
            tagList.get(i).setTagValue(value);
        }
        if (modifyTag != null) {
            Tag tag = TagsFilter.filterTagList(tagList, modifyTag.getTagName()).get(0);
            modifyRepeat++;
            //tag.value发生变化,修改参数成功or超时修改失败
            if (!tag.getTagValue().equals(modifyTag.getTagValue()) || modifyRepeat >=
                    REPEAT_LIMIT) {
                modifyProgress.setVisibility(View.INVISIBLE);
                modifyTag = null;
                if (modifyRepeat >= REPEAT_LIMIT) {
                    Snackbar.make(layoutContainer, R.string.device_modify_timeout, Snackbar
                            .LENGTH_SHORT);
                }
                modifyRepeat = 0;
            }
        }
    }

    private Protect getProtect(String name) {
        for (Protect protect : protectList) {
            if (protect.getName().equals(name)) {
                return protect;
            }
        }
        return null;
    }

    private String getXUnit(String tagShortName) {
        //eg. xIr → Ir
        Protect protect = getProtect(tagShortName);
        Matcher matcher = Pattern.compile("^x(\\w+)").matcher(protect.getUnit());
        return matcher.find() ? matcher.group(1) : null;
    }

    private String getFactor(String name) {
        List<Tag> tags = TagsFilter.filterDeviceTagList(tagList, name);
        if (tags.size() == 0) {
            return null;
        } else {
            return tags.get(0).getTagValue();
        }
    }

    @Override
    public boolean tagClickEnable(Tag tag,View view) {
        //nj--根据权限设置
        boolean usability=UserLevelHelper.checkTabFragment();
        return usability;
    }

    @Override
    public void onTagClick(Tag tag, View view) {
        if (modifyTag != null) {
            //正在提交参数修改
            return;
        }
        modifyProgress = view.findViewById(R.id.progress_modify);

        //nj--记录设备名称、参数名称与参数旧值
        String DeviceName = tag.getTagName();
        Device device = Device.initWith(DeviceName);
        actionDevic = device.getDeviceAlias();
        oldActionValue = tag.getTagValue();

        if(checkCtrlAuthority(REQUEST_PROTECT_AUTHORITY)){
            //传递副本，对其修改不会影响原tag
            modifyTag = new Tag(tag.getTagName(), tag.getTagValue());
        }
        if (hasCode) {
            selectItems();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            modifyTag = null;
            return;
        }
        switch (requestCode) {
            case REQUEST_MODIFY:
                //返回修改值
                modifyTags(data);
                //nj--记录参数修改操作的新值、添加参数操作记录
                String actionInfo = getString(R.string.action_device_paramenter, actionDevic,
                        actionName, oldActionValue, newActionValue);
                RecordHelper.actionLog(actionInfo);
                break;
            case REQUEST_PROTECT_AUTHORITY:
                //输入正确设备密码
                hasCode = true;
                selectItems();
                break;
            default:
                break;
        }
    }

    private void selectItems() {
        if (modifyTag != null) {
            Protect protect = getProtect(modifyTag.getTagShortName());
            //因对tag.value的修改会直接反馈于界面，故传递副本
            String tv = modifyTag.getTagValue();
            Tag target = new Tag(modifyTag.getTagName(), tv);
            if (!tv.equals(strSwitchOff)) {
                //eg. 在TabProtect统一显示电流单位：A,在ProtectSetting统一显示单位：xIr,xIn
                String xUnit = getXUnit(modifyTag.getTagShortName());
                String unit = TextUtils.isEmpty(xUnit) ? protect.getUnit() : xUnit;
                String factor = getFactor(unit);
                if (factor != null) {
                    String value = Generator.calFloatValue(modifyTag.getTagValue(), factor,
                            Generator.Operator.DIVIDE);
                    target.setTagValue(String.valueOf(value));
                }
            }
            ProtectSettingActivity.start(getActivity(), REQUEST_MODIFY, target, getProtect(modifyTag
                    .getTagShortName()));
        }
    }

    private void modifyTags(Intent data) {
        List<ValidTag> targets = new ArrayList<>();

        //result→①Ir:1In ②Ir:1xIn ③Isd:关闭 ④Tsd:(I2t on)0.4s
        String result = data.getStringExtra(RESULT_TAG);
        result = result.replace(Protect.IN, Protect.IE);
        String[] tmps = result.split(":");

        //保护开关设置：Isd(on/off),Tsd(I2t on/I2t off),Ii(on/off),Ig(on/off),Tg(I2t on/I2t off)
        Tag switchTag = TagsFilter.filterDeviceTagList(tagList, NAME_SWITCH).get(0);
        boolean switchOff = tmps[1].contains(Protect.I2T_OFF) || tmps[1].contains(strSwitchOff);
        List<String> switchItems = getProtect(NAME_SWITCH).getItems();
        int switchValue = Generator.setProtectState(switchTag.getTagValue(), switchItems,
                tmps[0], !switchOff);
        targets.add(new ValidTag(switchTag.getTagName(), String.valueOf(switchValue)));

        //nj--记录修改参数的名称 2018/11/6
        actionName = tmps[0];

        //保护值设置
        Tag protectTag = TagsFilter.filterDeviceTagList(tagList, tmps[0]).get(0);
        //Isd,Ii,Ig为“off",不再设置相应值
        if (!tmps[1].contains(strSwitchOff)) {
            Protect protect = getProtect(tmps[0]);
            String factor = getFactor(protect.getUnit());
            //eg. 源：(I2t on)0.4s → ()0.4s → ()0.4 →0.4
            String strValue = tmps[1].replaceAll(Protect.I2T_OFF, "").replaceAll(Protect
                    .I2T_ON, "").replaceAll(protect.getUnit(), "").replaceAll("\\(\\)", "");
            //eg. ACB:6xIr设置为6x1000=6000; MCCB:6xIr设置为6（factor=null)
            if (factor != null) {
                //eg. 630A*0.95=598.5A
                strValue = Generator.calFloatValue(strValue, factor, Generator.Operator.MULTIPLY);
                strValue = String.valueOf(Math.round(Float.valueOf(strValue)));
                //nj--记录参数修改后的数值 2018/11/6
                newActionValue = strValue;
            } else {
                newActionValue = tmps[1];
            }
            targets.add(new ValidTag(protectTag.getTagName(), strValue));

            //Isd为Ir的倍数，当Ir改变时，Isd应跟着改变，以保持不变的倍数,而以xIr为单位的不处理
            if (tmps[0].equals(NAME_IR) && TextUtils.isEmpty(getXUnit(NAME_IR))) {
                Tag IsdTag = TagsFilter.filterDeviceTagList(tagList, NAME_ISD).get(0);
                //Isd:off，短延时保护关闭，不处理
                if (!IsdTag.getTagValue().equals(getString(R.string.device_protect_off))) {
                    String times = Generator.calFloatValue(IsdTag.getTagValue(), protectTag
                            .getTagValue(), Generator.Operator.DIVIDE);
                    strValue = Generator.calFloatValue(strValue, times, Generator.Operator
                            .MULTIPLY);
                    targets.add(new ValidTag(IsdTag.getTagName(), strValue));
                }
            }
        }
        onModifyTags(targets, layoutContainer);
        modifyProgress.setVisibility(View.VISIBLE);
    }
}
