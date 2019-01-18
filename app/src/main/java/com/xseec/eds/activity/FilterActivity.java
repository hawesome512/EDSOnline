package com.xseec.eds.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.xseec.eds.R;
import com.xseec.eds.adapter.FilterAdapter;
import com.xseec.eds.model.FilterLabel;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.deviceconfig.DeviceConfig;
import com.xseec.eds.util.IOHelper;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.xseec.eds.util.EDSApplication.getContext;

public class FilterActivity extends BaseActivity {

    private static final String FILTER_TYPE="filterType";
    public static final String DATA_RESULT="result";

    public static final int WORKORDER=0;
    public static final int ACTION=1;
    public static final int ALARM=2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.button_filter)
    Button button;

    //nj--筛选标签数据 2018/12/20
    private List<FilterLabel> labelList;
    private FilterAdapter adapter;
    private int filterType;
    private Map<String,FilterLabel> checkLabel=new HashMap<>(  );
    CountDownLatch downLatch=new CountDownLatch( 1 );

    public static void start(Activity context, int filterType,int requestCode){
        Intent intent=new Intent( context,FilterActivity.class );
        intent.putExtra( FILTER_TYPE,filterType );
        context.startActivityForResult( intent,requestCode );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_filter );
        ButterKnife.inject( this );
        ViewHelper.initToolbar( this, toolbar, R.drawable.ic_arrow_back_white_24dp );
        initView();
        onRefreshViews();
    }

    //nj--初始化recycler 2018/12/20
    private void initView() {
        filterType=getIntent().getIntExtra( FILTER_TYPE,3 );

        if (filterType==WORKORDER){
            setTitle( R.string.filter_workorder );
        }else if (filterType==ACTION){
            setTitle( R.string.filter_action );
        }else {
            setTitle( R.string.filter_alarm );
        }

        GridLayoutManager layoutManager=new GridLayoutManager( this,3 );
        layoutManager.setSpanSizeLookup( new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (labelList.get( position ).getState()==FilterLabel.HEADER){
                    return 3;//nj--判断为标题，把3格占满
                }else {
                    return 1;
                }
            }
        } );
        recycler.setLayoutManager( layoutManager );
    }

    private void onRefreshViews() {
        getDateToThread( filterType );
        try {
            downLatch.await();
            adapter=new FilterAdapter( this,labelList );
            recycler.setAdapter( adapter);
            adapter.setOnClickListener( new FilterAdapter.OnItemClick() {
                @Override
                public void onChargeCheck(FilterLabel item) {
                    checkLabel=adapter.getCheckLabel();
                }
            } );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.button_filter)
    public void onButtonClicked() {
        List<FilterLabel> resultList=new ArrayList<>(  );
        if (checkLabel==null){
            resultList=null;
        }else {
            for (String key:checkLabel.keySet()){
                resultList.add( checkLabel.get( key ) );
            }
        }
        Intent intent=new Intent(  );
        intent.putParcelableArrayListExtra( DATA_RESULT, (ArrayList<FilterLabel>) resultList );
        setResult( RESULT_OK,intent );
        finish();
    }

    private void getDateToThread(final int type){
        new Thread( new Runnable() {
            @Override
            public void run() {
                String jsonData=IOHelper.inputStreamToString( getContext().getResources().
                        openRawResource( R.raw.filter_label_data ) );
                JsonParser parser=new JsonParser();
                JsonElement jsonArray=parser.parse( jsonData ).getAsJsonArray().get( type );
                Gson gson=new Gson();
                labelList=gson.fromJson( jsonArray.toString(), new TypeToken<List<FilterLabel>>(){}.getType() );

                if (type==ALARM) {
                    LinkedHashMap<String, String> aliasMap = WAServicer.getBasic().getAliasMap();
                    String label=getString( R.string.filter_device_label );
                    String groupName=getString( R.string.filter_device_name );
                    labelList.add( new FilterLabel( label, groupName , FilterLabel.HEADER ) );

                    for (String key : aliasMap.keySet()) {
                        String[] temps = key.split( "_" );
                        if (temps.length != 1) {
                            FilterLabel deviceLabel = new FilterLabel( aliasMap.get( key ), groupName, key );
                            labelList.add( deviceLabel );
                        }
                    }
                }
                downLatch.countDown();
            }
        } ).start();
    }
}
