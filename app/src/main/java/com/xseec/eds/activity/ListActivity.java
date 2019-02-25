package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import com.xseec.eds.R;
import com.xseec.eds.fragment.DeviceListFragment;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private static final String EXT_TITLE = "title";
    private static final String EXT_TAGS = "tags";
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    public static void start(Context context, String title, List<Tag> tagList) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra(EXT_TITLE, title);
        intent.putExtra(EXT_TAGS, (ArrayList) tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_device_list);
        ButterKnife.inject(this);
        ViewHelper.initToolbar(this, toolbar, R.drawable.ic_arrow_back_white_24dp);
        setTitle(getIntent().getStringExtra(EXT_TITLE));
        swipeRefreshLayout.setEnabled(false);
        List<Tag> tagList = getIntent().getParcelableArrayListExtra(EXT_TAGS);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layout_container, DeviceListFragment.newInstance((ArrayList<Tag>) tagList)).commit();
    }


}
