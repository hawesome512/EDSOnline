package com.xseec.eds.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xseec.eds.R;
import com.xseec.eds.model.deviceconfig.DeviceConfig;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.IOHelper;

import java.util.List;

/**
 * Created by Administrator on 2018/7/9.
 */

public class WAServicer {
    private static final String PREF_HOST="host";
    private static final String PREF_PROJECT="project";
    private static final String PREF_NODE="node";

    public static String getHostUrl() {
        return hostUrl;
    }

    public static String getProjectName() {
        return projectName;
    }

    public static String getNodeName() {
        return nodeName;
    }

    private static String hostUrl;
    private static String projectName;
    private static String nodeName;
    private static User user;
    private static Context context;
    private static List<DeviceConfig> deviceConfigs;

    public static void initWAServicer(){
        context= EDSApplication.getContext();
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        hostUrl=preferences.getString(PREF_HOST,"www.eds.ink");
        projectName=preferences.getString(PREF_PROJECT,"EDS");
        nodeName=preferences.getString(PREF_NODE,"XS");
        initDeviceConfigsinThread();
    }

    public static void setWAServicer(String host, String project, String node) {
        hostUrl = host;
        projectName = project;
        nodeName = node;
        saveConfig();
    }

    private static void saveConfig(){
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(PREF_HOST,hostUrl);
        editor.putString(PREF_PROJECT,projectName);
        editor.putString(PREF_NODE,nodeName);
    }

    private static void initDeviceConfigsinThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String strJson= IOHelper.inputStreamToString(context.getResources().openRawResource(R.raw.device_config));
                Gson gson=new Gson();
                deviceConfigs=gson.fromJson(strJson,new TypeToken<List<DeviceConfig>>(){}.getType());
            }
        }).start();
    }

    public static List<DeviceConfig> getDeviceConfigs() {
        return deviceConfigs;
    }

    public static String getLoginUrl(){
        return context.getString(R.string.was_login,hostUrl,projectName);
    }

    public static String getTagListUrl(String deviceName){
        return context.getString(R.string.was_tag_list,hostUrl,projectName,nodeName,deviceName);
    }

    public static String getGetValueUrl(){
        return context.getString(R.string.was_get_value,hostUrl,projectName);
    }

    public static String getSetValueUrl(){
        return context.getString(R.string.was_set_value,hostUrl,projectName);
    }

    public static String getTagLogUrl(){
        return context.getString(R.string.was_get_log,hostUrl,projectName);
    }

    public static String getBasicInfoUrl(String deviceName){
        return context.getString(R.string.was_basic_info,hostUrl,deviceName);
    }

    public static String getBasicImageUrl(String deviceName,String imageName){
        return context.getString(R.string.was_basic_header_img,hostUrl,deviceName,imageName);
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        WAServicer.user = user;
    }
}
