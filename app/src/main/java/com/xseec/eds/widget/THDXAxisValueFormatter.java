package com.xseec.eds.widget;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * Created by Administrator on 2018/8/17.
 */

public class THDXAxisValueFormatter implements IAxisValueFormatter {

    private String[] axisItems={"Ia","Ib","Ic","In","Ua","Ub","Uc","Ia","Ib","Ic","In","Ua","Ub","Uc"};

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index=axisItems.length-1-(int)value;
        return axisItems[index];
    }
}
