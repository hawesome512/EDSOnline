package com.xseec.eds.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.activity.ChartActivity;
import com.xseec.eds.model.DataLogFactor;
import com.xseec.eds.model.deviceconfig.RealZone;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabMonitorFragment extends TabBaseFragment {

    private static final String KEY_REAL_ZONE = "real_zone";
    private static final String KEY_DEVICE_NAME = "device_name";

    public static Fragment newInstance(String deviceName, RealZone realZone) {
        Fragment fragment = new TabMonitorFragment();
        List<Tag> tags = realZone.getAllTagList(deviceName);
        Bundle bundle = getBundle(tags);
        bundle.putParcelable(KEY_REAL_ZONE, realZone);
        bundle.putString(KEY_DEVICE_NAME, deviceName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLayout() {
        RealZone realZone = getArguments().getParcelable(KEY_REAL_ZONE);
        if (realZone.getSource1().size() != 0) {
            addCard(R.string.device_source1, realZone.getSource1());
        }
        if (realZone.getSource2().size() != 0) {
            addCard(R.string.device_source2, realZone.getSource2());
        }
        if (realZone.getCurrent().size() != 0) {
            addCard(R.string.device_current, realZone.getCurrent());
        }
        if (realZone.getVoltage().size() != 0) {
            addCard(R.string.device_voltage, realZone.getVoltage());
        }
        if (realZone.getGrid().size() != 0) {
            addCard(R.string.device_grid, realZone.getGrid());
        }
        if (realZone.getPower().size() != 0) {
            addCard(R.string.device_power, realZone.getPower());
        }
        if (realZone.getEnergy().size() != 0) {
            addCard(R.string.device_energy, realZone.getEnergy());
        }
        if (realZone.getHarmonic().size() != 0) {
            final View view = addCard(R.string.overview_item_harmonic, new
                    ArrayList<String>());
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String deviceName = getArguments().getString(KEY_DEVICE_NAME);
                    onTagClick(new Tag(deviceName + ":THD"), view);
                }
            });
        }
    }

    @Override
    public void onRefreshed(List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        List<Tag> currents = TagsFilter.filterDeviceTagList(validTagList, "Ia", "Ib", "Ic");
        List<Tag> voltages = TagsFilter.filterDeviceTagList(validTagList, "Ua", "Ub", "Uc");
        for (int i = 0; i < validTagList.size(); i++) {
            Tag tag = validTagList.get(i);
            String value=tag.getTagValue();
            if (value.equals(Tag.NULL_TAG_VALUE)) {
                switch (tag.getTagShortName()) {
                    case "Iavg":
                        value = String.valueOf(Generator.getAvgTagsValue(currents));
                        break;
                    case "Ip":
                        value = String.valueOf(Generator.getMaxDeltaTagsValue(currents));
                        break;
                    case "Uavg":
                        value = String.valueOf(Generator.getAvgTagsValue(voltages));
                        break;
                    case "Up":
                        value = String.valueOf(Generator.getMaxDeltaTagsValue(voltages));
                        break;
                    default:
                        value = Tag.NULL_TAG_SHOW_VALUE;
                        break;
                }
            } else if (tag.getTagShortName().equals("Phase")) {
                value = Generator.getResourceString("tag_Phase_" + value);
            }
            tagList.get(i).setTagValue(value);
        }
    }

    @Override
    public void onTagClick(Tag tag, View view) {
        ArrayList<String> tags = new ArrayList<>();
        tags.add(tag.getTagName());
        ChartActivity.start(getContext(), tags, new DataLogFactor());
    }
}
