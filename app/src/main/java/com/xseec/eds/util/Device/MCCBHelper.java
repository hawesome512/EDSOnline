package com.xseec.eds.util.Device;

import com.xseec.eds.R;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.State;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.TagsFilter;

import java.util.List;

/**
 * Created by Administrator on 2019/1/10.
 */

public class MCCBHelper {
    //MCCB低位在前，高位在后2^6转换为2^14
    //脱扣标志位（7位→15位）
    //脱扣记录未读（12位→4位）,报警记录未读（13位→5位）：2^16-1-2^4-2^5=65487
    public static State getState(int status) {
        status=status&65487;
        switch (status){
            case 0:
                return State.OFF;
            case 16384:
                return State.ON;
            case 32768:
                return State.TRIP;
            default:
                return State.ALARM;
        }
    }

    public static Device convertDevice(Device device){
        String dvName=device.getDeviceName();
        List<Tag> result= TagsFilter.filterTagList(null,dvName+":Ie");
        if(result.size()>0){
            String strIe=result.get(0).getTagValue();
            float Ie= Generator.floatTryParse(strIe);
            if(Ie>=630){
                device.setDeviceResId(R.drawable.device_m2_630);
            }else if(Ie>=400){
                device.setDeviceResId(R.drawable.device_m2_400);
            }
        }
        return device;
    }
}
