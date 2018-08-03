package com.xseec.eds.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2018/7/25.
 */

public class DateHelper {

    private static SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getString(Date date){
        return simpleDateFormat.format(date);
    }
}
