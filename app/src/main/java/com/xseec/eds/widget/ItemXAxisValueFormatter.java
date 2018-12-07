package com.xseec.eds.widget;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

/**
 * Created by Administrator on 2018/8/21.
 */

public class ItemXAxisValueFormatter implements IAxisValueFormatter {

    private String[] items;

    public ItemXAxisValueFormatter(String[] items) {
        this.items = items;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        int index=(int)value;
        return items[index%items.length];
    }
}
