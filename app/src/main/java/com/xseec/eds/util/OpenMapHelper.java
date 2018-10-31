package com.xseec.eds.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

import com.xseec.eds.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/8/15.
 */

public class OpenMapHelper {

    private static String getGaoDeNavi(String location) {
        return String.format("androidamap://poi?sourceApplication=edsonline&keywords=%s&dev=0",location);
    }

    private static String getBaiduNavi(String location) {
        return String.format("baidumap://map/geocoder?src=andr.baidu.openAPIdemo&address=%s",location);
    }

    private static Intent openGoogleNavi(String lat, String lng) {
        StringBuffer stringBuffer = new StringBuffer("google.navigation:q=").append(lat).append
                (",").append(lng).append("&mode=d");
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(stringBuffer.toString()));
        intent.setPackage("com.google.android.apps.maps");
        return intent;
    }

    private static String getTencentMapUrl(String location){
        return String.format("qqmap://map/search?keyword=%s&center=CurrentLocation&radius=1000&referer=4T3BZ-YMAL2-B72UM-CHMUQ-5OAVS-OZBSM",location);
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

    public static void getMapAppIntent(Context context, String location) {
        String[] maps = {"com.autonavi.minimap", "com.baidu.BaiduMap","com.tencent.map"};
        String uri;
        if (isAvilible(context, maps[0])) {
            uri= getGaoDeNavi(location);
        } else if (isAvilible(context, maps[1])) {
            uri= getBaiduNavi(location);
        } else if (isAvilible(context, maps[2])) {
            uri=getTencentMapUrl(location);
        } else {
            Toast.makeText(context, R.string.overview_map_error, Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent=new Intent();
        intent.setData(Uri.parse(uri));
        context.startActivity(intent);
    }

}
