package com.xseec.eds.util.Device;

import com.xseec.eds.model.State;
import com.xseec.eds.model.deviceconfig.Protect;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.Generator;

import java.util.List;

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
            case 128:
                return State.TRIP;
            default:
                return State.ALARM;
        }
    }

    public static void convertA3(List<Tag> validTagList) {
        for(Tag tag:validTagList){
            switch (tag.getTagShortName()){
                case "CtrlMode":
                    tag.setTagValue(covCtrlMode(tag));
                default:
                    break;
            }
        }
    }

    private static String covCtrlMode(Tag tag) {
        String tagValue = tag.getTagValue();
        tagValue = Generator.checkIsOne(tagValue, 0) ? Protect.REMOTE : Protect.LOCAL;
        return tagValue;
    }
}
