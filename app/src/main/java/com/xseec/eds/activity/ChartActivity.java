package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.FrameLayout;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.fragment.HorBarChartFragment;
import com.xseec.eds.model.tags.StoredTag;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChartActivity extends BaseActivity {

    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;

    private static final String EXT_TAGS = "tags";
    private static final String EXT_INTERVAL_TYPE = "type";

    public static void start(Context context, ArrayList<String> tagList, StoredTag.IntervalType
            intervalType) {
        Intent intent = new Intent(context, ChartActivity.class);
        intent.putExtra(EXT_TAGS, tagList);
        intent.putExtra(EXT_INTERVAL_TYPE, intervalType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        List<String> tagList = getIntent().getStringArrayListExtra(EXT_TAGS);
        Fragment fragment;
        if (tagList.get(0).contains("THD")) {
            fragment = HorBarChartFragment.newInstance(tagList);
        } else {
            StoredTag.IntervalType intervalType = (StoredTag.IntervalType) getIntent()
                    .getSerializableExtra(EXT_INTERVAL_TYPE);
            fragment = DataLogFragment.newInstance(tagList, intervalType);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                fragment).commit();
    }

    //在Activity中重写onActivityResult方法触发Fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
