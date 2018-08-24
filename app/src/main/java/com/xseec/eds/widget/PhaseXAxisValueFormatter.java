package com.xseec.eds.widget;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by Administrator on 2018/8/21.
 */

public class PhaseXAxisValueFormatter implements IAxisValueFormatter {

    private String[] items={"Ia","Ib","Ic"};

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index=(int)value;
        return items[index%items.length];
    }
}
