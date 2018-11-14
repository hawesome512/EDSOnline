package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.fragment.HorBarChartFragment;
import com.xseec.eds.model.DataLogFactor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChartActivity extends BaseActivity {

    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;

    private static final String EXT_TAGS = "tags";
    private static final String EXT_FACTOR = "factor";

    public static void start(Context context, ArrayList<String> tagList, DataLogFactor factor) {
        Intent intent = new Intent(context, ChartActivity.class);
        intent.putExtra(EXT_TAGS, tagList);
        intent.putExtra(EXT_FACTOR, factor);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        List<String> tagList = getIntent().getStringArrayListExtra(EXT_TAGS);
        //电量、谐波、能耗三种曲线类型
        Fragment fragment;
        if (tagList.get(0).contains("THD")) {
            fragment = HorBarChartFragment.newInstance(tagList);
        } else {
            DataLogFactor factor = (DataLogFactor) getIntent()
                    .getParcelableExtra(EXT_FACTOR);
            fragment = DataLogFragment.newInstance(tagList, factor);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                fragment).commit();
    }

}
