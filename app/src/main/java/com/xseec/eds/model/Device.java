package com.xseec.eds.model;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.xseec.eds.R;
import com.xseec.eds.model.deviceconfig.DeviceConfig;
import com.xseec.eds.util.Generator;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/8/16.
 */

public enum Device {
    A1("ACB", R.drawable.device_a65),
    A2("ACB", R.drawable.device_a65),
    A3("ACB", R.drawable.device_a66),
    M1("MCCB", R.drawable.device_m1),
    M2("MCCB", R.drawable.device_m2),
    C1("MIC", R.drawable.device_c),
    G1("ADAM3600", R.drawable.device_com_adam3600),
    P1("METER", R.drawable.device_meter);
    private String deviceType;
    private int deviceResId;
    private static String[] infos;
    private static LinkedHashMap<String, String> aliasMap;

    private Device(String deviceType, int deviceResId) {
        this.deviceType = deviceType;
        this.deviceResId = deviceResId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public int getDeviceResId() {
        return deviceResId;
    }

    public static Device initWithTagName(String tagName) {
        infos = tagName.split(":")[0].split("_");
        if (infos.length != 3) {
            return null;
        } else {
            return valueOf(infos[1]);
        }
    }

    public String getDeviceName() {
        return TextUtils.join("_", infos);
    }

    public String getDeviceAlias() {
        if (aliasMap.containsKey(getDeviceName())) {
            return aliasMap.get(getDeviceName());
        } else {
            return String.format("%s (%s#%s)", deviceType, infos[0], infos[2]);
        }
    }

    public List<String> getStatusItems() {
        String itemsName = String.format("device_%s_status", this.toString());
        String itemsValue = Generator.getResourceString(itemsName);
        return Arrays.asList(itemsValue.split("_"));
    }

    public DeviceConfig getDeviceConfig() {
        List<DeviceConfig> configs = WAServicer.getDeviceConfigs();
        for (int i = 0; i < configs.size(); i++) {
            if (configs.get(i).getDeviceType().equals(name())) {
                return configs.get(i);
            }
        }
        return null;
    }

    public static LinkedHashMap<String, String> getAliasMap() {
        return aliasMap;
    }

    public static void setAliasMap(LinkedHashMap<String, String> aliasMap) {
        Device.aliasMap = aliasMap;
    }
}
