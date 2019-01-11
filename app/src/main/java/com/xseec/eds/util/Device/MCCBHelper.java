package com.xseec.eds.util.Device;

import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;

/**
 * Created by Administrator on 2019/1/10.
 */

public class MCCBHelper {
    //MCCB地位在前，高位在后2^6转换为2^14
    public static State getState(int status) {
        switch (status){
            case 0:
                return State.OFF;
            case 16384:
                return State.ON;
            default:
                return State.ALARM;
        }
    }
}
