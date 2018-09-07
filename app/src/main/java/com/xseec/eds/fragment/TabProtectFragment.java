package com.xseec.eds.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabProtectFragment extends TabBaseFragment {

    private static final String KEY_PROTECTS = "protects";

    private List<Protect> protectList;

    public static Fragment newInstance(String deviceName, List<Protect> protectList) {
        Fragment fragment = new TabProtectFragment();
        List<Tag> tags = new ArrayList<>();
        for (Protect protect : protectList) {
            tags.add(new Tag(deviceName + ":" + protect.getName()));
        }
        Bundle bundle = getBundle(tags);
        bundle.putParcelableArrayList(KEY_PROTECTS, (ArrayList<Protect>)protectList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLayout() {
        protectList=getArguments().getParcelableArrayList(KEY_PROTECTS);
        addCard(getString(R.string.device_long), Arrays.asList("Ir", "Tr"));
        addCard(getString(R.string.device_short), Arrays.asList("Isd", "Tsd"));
        addCard(getString(R.string.device_instant), Arrays.asList("Ii"));
        addCard(getString(R.string.device_ground), Arrays.asList("Ig", "Tg"));
    }

    @Override
    public void onRefreshed(List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        List<Tag> switchTags=TagsFilter.filterDeviceTagList(validTagList,"Switch");
        String switchValue=switchTags.get(0).getTagValue();
        List<String> switchItems=getProtectItems("Switch");
        for (int i = 0; i < validTagList.size(); i++) {
            String value="";
            Tag tag=validTagList.get(i);
            boolean isOff;
            String alias=tag.getTagShortName();
            switch (alias){
                case "Isd":
                case "Ii":
                case "Ig":
                    isOff=Generator.checkProtectState(switchValue,switchItems,alias);
                    value=isOff?getString(R.string.device_protect_off):tag.getTagValue();
                    break;
                case "Tsd":
                case "Tg":
                    isOff=Generator.checkProtectState(switchValue,switchItems,alias);
                    value+= "I2t:"+(isOff?"OFF":"ON")+"   "+tag.getTagValue();
                    break;
                default:
                    value=tag.getTagValue();
            }
            tagList.get(i).setTagValue(value);
        }
    }

    private List<String> getProtectItems(String name){
        for(Protect protect:protectList){
            if(protect.getName().equals(name)){
                return protect.getItems();
            }
        }
        return null;
    }

    @Override
    public void onTagClick(Tag tag) {

    }
}
