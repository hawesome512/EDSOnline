package com.xseec.eds.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2018/8/23.
 */

public class IOHelper {

    public static String inputStreamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, bytes.length);
            String json = new String(bytes);
            return json;
        } catch (IOException e) {
            return null;
        }
    }
}
