package com.xseec.eds.util.Device;

import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;

/**
 * Created by Administrator on 2019/1/10.
 */

public class ACBHelper {
    public static State getState(int status) {
        switch (status){
            case 0:
                return State.ON;
            case 64:
                return State.OFF;
            default:
                return State.ALARM;
        }
    }
}
