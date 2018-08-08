package com.xseec.eds.util;

import android.os.Build;

/**
 * Created by Administrator on 2018/8/4.
 */

public class ApiLevelHelper {

    public static boolean isAtLeast(int apiLevel){
        return Build.VERSION.SDK_INT>=apiLevel;
    }

}
