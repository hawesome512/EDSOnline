package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DataLogFragment;
import com.xseec.eds.model.Tags.StoredTag;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DetailActivity extends AppCompatActivity {

    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;

    private static final String EXT_NAME="ext";
    private static final String EXT_INTERVAL_TYPE="interval_type";
    public static void start(Context context,String ext,StoredTag.IntervalType intervalType){
        Intent intent=new Intent(context,DetailActivity.class);
        intent.putExtra(EXT_NAME,ext);
        intent.putExtra(EXT_INTERVAL_TYPE,intervalType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.inject(this);
        String extName=getIntent().getStringExtra(EXT_NAME);
        StoredTag.IntervalType intervalType= (StoredTag.IntervalType) getIntent().getSerializableExtra(EXT_INTERVAL_TYPE);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_container,
                DataLogFragment.newInstance(extName,intervalType)).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    //在Activity中重写onActivityResult方法触发Fragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
