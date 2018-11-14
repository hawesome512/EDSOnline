package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.adapter.DeviceAdapter;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ListActivity extends BaseActivity {

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;

    private static final String EXT_TITLE = "title";
    private static final String EXT_TAGS = "tags";
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    List<Tag> tagList;
    DeviceAdapter adapter;

    public static void start(Context context, String title, List<Tag> tagList) {
        Intent intent = new Intent(context, ListActivity.class);
        intent.putExtra(EXT_TITLE, title);
        intent.putExtra(EXT_TAGS, (ArrayList) tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_list);
        ButterKnife.inject(this);
        ViewHelper.initToolbar(this, toolbar, R.drawable.ic_arrow_back_white_24dp);
        setTitle(getIntent().getStringExtra(EXT_TITLE));
        tagList = getIntent().getParcelableArrayListExtra(EXT_TAGS);
        //个性化列表
        initDeviceRecycler();
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                WAServiceHelper.sendGetValueRequest(tagList, new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        tagList=WAJsonHelper.refreshTagValue(response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter=new DeviceAdapter(tagList);
                                recycler.setAdapter(adapter);
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });
                    }
                });
            }
        });
    }

    private void initDeviceRecycler() {
        int column = ViewHelper.isPort() ? 1 : 2;
        GridLayoutManager manager = new GridLayoutManager(this, column);
        adapter = new DeviceAdapter(tagList);
        recycler.setLayoutManager(manager);
        recycler.setAdapter(adapter);
    }

}
