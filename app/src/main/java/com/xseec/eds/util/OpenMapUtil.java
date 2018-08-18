package com.xseec.eds.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class OpenMapUtil {

    private static Intent openGaoDeNavi(String lat, String lng) {
        StringBuffer stringBuffer = new StringBuffer("androidamap://navi?sourceApplication=")
                .append("yitu8_driver").append("&lat=").append(lat)
                .append("&lon=").append(lng)
                .append("&dev=").append(1)
                .append("&style=").append(0);
        Intent intent = new Intent(Intent.ACTION_VIEW, android.net.Uri.parse(stringBuffer
                .toString()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setPackage("com.autonavi.minimap");
        return intent;
    }

    private static Intent openBaiduNavi(String lat, String lng) {
        StringBuffer stringBuffer = new StringBuffer("baidumap://map/navi?location=")
                .append(lat).append(",").append(lng).append("&type=TIME");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.baidu.BaiduMap");
        return intent;
    }

    private static Intent openGoogleNavi(String lat, String lng) {
        StringBuffer stringBuffer = new StringBuffer("google.navigation:q=").append(lat).append
                (",").append(lng).append("&mode=d");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    /* 检查手机上是否安装了指定的软件
     * @param context
     * @param packageName：应用包名
     * @return
             */
    private static boolean isAvilible(Context context, String packageName) {
        //获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                if (packName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Intent getMapAppIntent(Context context, String lat, String lng) {
        String[] maps = {"com.autonavi.minimap", "com.baidu.BaiduMap", "com.google.android.apps" +
                ".maps"};
        if (isAvilible(context, maps[0])) {
            return openGaoDeNavi(lat, lng);
        } else if (isAvilible(context, maps[1])) {
            return openBaiduNavi(lat, lng);
        } else if (isAvilible(context, maps[2])) {
            return openGoogleNavi(lat, lng);
        } else {
            return null;
        }
    }

}
