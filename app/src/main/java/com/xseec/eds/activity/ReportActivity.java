package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.xseec.eds.R;
import com.xseec.eds.fragment.ReportChartFragment;
import com.xseec.eds.fragment.ReportListFragment;
import com.xseec.eds.model.DataLogFactor;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

//nj--报表信息详细界面 2018/12/3
public class ReportActivity extends BaseActivity {

    private static final String EXT_TYPLE = "type";
    private static final String EXT_LIST = "list";

    private int type;
    private List list;

    private List<String> tagName;
    private DataLogFactor defaultFactor;
    private List<String> value;

    public static void start(Context context, int type, List list) {
        Intent intent = new Intent( context, ReportActivity.class );
        intent.putExtra( EXT_TYPLE, type );
        intent.putParcelableArrayListExtra( EXT_LIST, (ArrayList) list );
        context.startActivity( intent );
    }

    public static void start(Context context, DataLogFactor factor, List<String> tags,
                             List<String> stringList) {
        Intent intent = new Intent( context, ReportActivity.class );
        intent.putExtra( ReportChartFragment.EXT_FACTOR,factor );
        intent.putExtra( ReportChartFragment.EXT_TAGS, (ArrayList) tags );
        intent.putParcelableArrayListExtra( ReportChartFragment.EXT_VALUES, (ArrayList) stringList );
        context.startActivity( intent );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );
        ButterKnife.inject( this );
        type = getIntent().getIntExtra( EXT_TYPLE, 3 );
        initData();
    }

    private void initData() {
        Fragment fragment;
        //nj--type==3跳转到温度、湿度图表界面中 2018/12/6
        if (type != 3) {
            list = getIntent().getParcelableArrayListExtra( EXT_LIST );
            fragment=ReportListFragment.newInstance( type, list );
        }else {
            tagName=getIntent().getStringArrayListExtra( ReportChartFragment.EXT_TAGS );
            defaultFactor=getIntent().getParcelableExtra( ReportChartFragment.EXT_FACTOR );
            value=getIntent().getStringArrayListExtra( ReportChartFragment.EXT_VALUES );
            fragment= ReportChartFragment.newInstant( defaultFactor,value,tagName );
        }
        getSupportFragmentManager().beginTransaction().replace( R.id.layout_container,
                fragment ).commit();
    }
}
