package com.xseec.eds.fragment;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/8/24.
 */

public class TabProtectFragment extends TabBaseFragment {

    private List<String> longList,shortList,instantList,groundList;

    private void initItems(){
        longList=Arrays.asList("Ir","Tr");
        shortList=Arrays.asList("Isd","Tsd");
        instantList=Arrays.asList("Ii");
        groundList=Arrays.asList("Ig","Tg");
    }

    @Override
    protected void initLayout() {
        initItems();
        addCard("过载保护",longList);
        addCard("短路保护",shortList);
        addCard("瞬时保护",instantList);
        addCard("接地保护",groundList);
    }

    @Override
    public void onClick(View v) {

    }
}
