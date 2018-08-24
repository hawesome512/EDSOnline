package com.xseec.eds.fragment;

import android.view.View;

import com.xseec.eds.model.DeviceConfig;
import com.xseec.eds.model.WAServicer;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabMonitorFragment extends TabBaseFragment {

    @Override
    protected void initLayout() {
        DeviceConfig.RealZone realZone = WAServicer.getDeviceConfigs().get(2).getRealZone();
        if (realZone.getCurrent().size() != 0) {
            addCard("电流", realZone.getCurrent());
        }
        if(realZone.getVoltage().size()!=0){
            addCard("电压",realZone.getVoltage());
        }
        if(realZone.getGrid().size()!=0){
            addCard("电网",realZone.getGrid());
        }
        if(realZone.getPower().size()!=0){
            addCard("功率",realZone.getPower());
        }
        if(realZone.getEnergy().size()!=0){
            addCard("电能",realZone.getEnergy());
        }
        if(realZone.getHarmonic().size()!=0){
            addCard("谐波监测",new ArrayList<String>());
        }
    }

    @Override
    public void onClick(View v) {

    }
}
