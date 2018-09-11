package com.xseec.eds.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.ProtectSettingActivity;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.tags.ValidTag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        addCard(getString(R.string.device_long), Arrays.asList(NAME_IR, NAME_TR));
        addCard(getString(R.string.device_short), Arrays.asList(NAME_ISD, NAME_TSD));
        addCard(getString(R.string.device_instant), Arrays.asList(NAME_II));
        addCard(getString(R.string.device_ground), Arrays.asList(NAME_IG, NAME_TG));
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
                case NAME_ISD:
                case NAME_II:
                case NAME_IG:
                    isOff = Generator.checkProtectState(switchValue, switchItems, alias);
                    value = isOff ? strSwitchOff : tag.getTagValue();
                    break;
                case NAME_TSD:
                case NAME_TG:
                    isOff = Generator.checkProtectState(switchValue, switchItems, alias);
                    value += (isOff ? Protect.I2T_OFF : Protect.I2T_ON) + tag.getTagValue();
                    break;
                default:
                    value = tag.getTagValue();
            }
            tagList.get(i).setTagValue(value);
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

    private float getFactor(String name) {
        List<Tag> tags = TagsFilter.filterDeviceTagList(tagList, name);
        if (tags.size() == 0) {
            return 0;
        } else {
            return Generator.floatTryParse(tags.get(0).getTagValue());
        }
    }

    @Override
    public void onTagClick(Tag tag) {
        modifyTag = tag;
        checkCtrlAuthority();
        if (hasCode) {
            selectItems();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_MODIFY:
                if (resultCode == Activity.RESULT_OK) {
                    modifyTags(data);
                }
                break;
            case REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    hasCode = true;
                    selectItems();
                }
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
                float factor = getFactor(protect.getUnit());
                if (factor != 0) {
                    float value = Generator.floatTryParse(modifyTag.getTagValue()) / factor;
                    NumberFormat nf = NumberFormat.getInstance();
                    target.setTagValue(nf.format(value));
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

        //保护开关设置
        Tag switchTag = TagsFilter.filterDeviceTagList(tagList, NAME_SWITCH).get(0);
        boolean switchOff = tmps[1].contains(Protect.I2T_OFF) || tmps[1].contains(strSwitchOff);
        List<String> switchItems = getProtect(NAME_SWITCH).getItems();
        int switchValue = Generator.setProtectState(switchTag.getTagValue(), switchItems,
                tmps[0], !switchOff);
        targets.add(new ValidTag(switchTag.getTagName(), String.valueOf(switchValue)));

        //保护值设置
        Tag protectTag = TagsFilter.filterDeviceTagList(tagList, tmps[0]).get(0);
        if (!tmps[1].contains(strSwitchOff)) {
            Protect protect = getProtect(tmps[0]);
            float factor = getFactor(protect.getUnit());
            //(I2t on)0.4s→0.4
            String strValue = tmps[1].replaceAll(Protect.I2T_OFF, "").replaceAll(Protect
                    .I2T_ON, "").replaceAll(protect.getUnit(), "").replaceAll("\\(\\)", "");
            if (factor != 0) {
                strValue = String.valueOf(Float.valueOf(strValue) * factor);
            }
            targets.add(new ValidTag(protectTag.getTagName(), strValue));
        }

        WAServiceHelper.sendSetValueRequest(targets, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Snackbar.make(layoutContainer, R.string.device_modify_success, Snackbar
                        .LENGTH_LONG).show();
            }
        });
    }
}
