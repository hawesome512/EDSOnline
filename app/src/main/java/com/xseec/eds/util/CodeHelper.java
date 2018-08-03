package com.xseec.eds.util;

import android.util.Base64;

/**
 * Created by Administrator on 2018/7/10.
 */

public class CodeHelper {

    public static String encode(String input){
        return Base64.encodeToString(input.getBytes(),Base64.DEFAULT);
    }

    public static String decode(String input){
        return new String(Base64.decode(input,Base64.DEFAULT));
    }
}
