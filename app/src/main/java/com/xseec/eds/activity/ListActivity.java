package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.xseec.eds.R;
import com.xseec.eds.adapter.DeviceAdapter;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;

    private static final String EXT_TITLE="title";
    private static final String EXT_TAGS="tags";
    public static void start(Context context,String title,List<Tag> tagList){
        Intent intent=new Intent(context,ListActivity.class);
        intent.putExtra(EXT_TITLE,title);
        intent.putExtra(EXT_TAGS,(ArrayList)tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        ButterKnife.inject(this);
        ViewHelper.initToolbar(this,toolbar,R.drawable.ic_arrow_back_white_24dp);
        setTitle(getIntent().getStringExtra(EXT_TITLE));
        List<Tag> tagList=getIntent().getParcelableArrayListExtra(EXT_TAGS);
        //个性化列表
        initDeviceRecycler(tagList);
    }

    private void initDeviceRecycler(List<Tag> tagList) {
        int column=ViewHelper.isPort()?1:2;
        GridLayoutManager manager=new GridLayoutManager(this,column);
        DeviceAdapter adapter=new DeviceAdapter(tagList);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

}
