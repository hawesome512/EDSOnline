package com.xseec.eds.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.xseec.eds.R;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.deviceconfig.RealZone;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.TagsFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabMonitorFragment extends TabBaseFragment {

    private static final String KEY_REAL_ZONE="real_zone";

    public static Fragment newInstance(String deviceName,RealZone realZone){
        Fragment fragment=new TabMonitorFragment();
        List<Tag> tags=realZone.getAllTagList(deviceName);
        Bundle bundle=getBundle(tags);
        bundle.putParcelable(KEY_REAL_ZONE,realZone);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected void initLayout() {
        RealZone realZone = getArguments().getParcelable(KEY_REAL_ZONE);
        if (realZone.getCurrent().size() != 0) {
            addCard(getString(R.string.device_current), realZone.getCurrent());
        }
        if(realZone.getVoltage().size()!=0){
            addCard(getString(R.string.device_voltage),realZone.getVoltage());
        }
        if(realZone.getGrid().size()!=0){
            addCard(getString(R.string.device_grid),realZone.getGrid());
        }
        if(realZone.getPower().size()!=0){
            addCard(getString(R.string.device_power),realZone.getPower());
        }
        if(realZone.getEnergy().size()!=0){
            addCard(getString(R.string.device_energy),realZone.getEnergy());
        }
        if(realZone.getHarmonic().size()!=0){
            addCard(getString(R.string.overview_item_harmonic),new ArrayList<String>());
        }
    }

    @Override
    public void onClick(View v) {

    }
}
