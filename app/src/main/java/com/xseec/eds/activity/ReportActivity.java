package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.fragment.ReportListFragment;
import com.xseec.eds.model.DataLogFactor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

//nj--报表信息详细界面 2018/12/3
public class ReportActivity extends BaseActivity {

    private static  final String EXT_FACTOR="factor";
    private static final String EXT_TAGS="tags";

    private int itemType;

    public static void start(Context context, int itemType, List itemList) {
        Intent intent = new Intent( context, ReportActivity.class );
        intent.putExtra( ReportListFragment.EXT_ITEM_TYPE, itemType );
        intent.putParcelableArrayListExtra( ReportListFragment.EXT_ITEM_LIST, (ArrayList) itemList );
        context.startActivity( intent );
    }

    public static void start(Context context, DataLogFactor factor, List<String> tagName) {
        Intent intent = new Intent( context, ReportActivity.class );
        intent.putExtra( EXT_FACTOR,factor );
        intent.putExtra( EXT_TAGS, (ArrayList) tagName );
        context.startActivity( intent );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );
        ButterKnife.inject( this );
        itemType = getIntent().getIntExtra( ReportListFragment.EXT_ITEM_TYPE, 3 );
        initData();
    }

    private void initData() {
        Fragment fragment;
        //nj--type==3跳转到温度、湿度图表界面中 2018/12/6
        if (itemType != 3) {
            List valueList = getIntent().getParcelableArrayListExtra( ReportListFragment.EXT_ITEM_LIST );
            fragment=ReportListFragment.newInstance( itemType, valueList );
        }else {
            List<String> tagNames=getIntent().getStringArrayListExtra( EXT_TAGS );
            DataLogFactor factor=getIntent().getParcelableExtra( EXT_FACTOR );
            fragment= DataLogFragment.newInstance( tagNames,factor );
        }
        getSupportFragmentManager().beginTransaction().replace( R.id.layout_container,
                fragment ).commit();
    }
}
