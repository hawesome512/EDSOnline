package com.xseec.eds.util.Device;

import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;

/**
 * Created by Administrator on 2019/1/10.
 */

public class MCCBHelper {
    //MCCB地位在前，高位在后2^6转换为2^14
    //脱扣（7位→15位），脱扣记录未读（12位→4位）,报警记录未读（13位→5位）：2^16-1-2^4-2^15-2^5=32719
    public static State getState(int status) {
        status=status&32719;
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
