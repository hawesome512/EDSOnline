package com.xseec.eds.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.WorkorderCreatorActivity;
import com.xseec.eds.adapter.WorkorderAdapter;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkorderListFragment extends Fragment {

    private static final int REQUEST_CREATE = 1;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.fab_add)
    FloatingActionButton fabAdd;
    @InjectView(R.id.progress_data_log)
    ProgressBar progress;

    private WorkorderAdapter workorderAdapter;
    private List<Workorder> workorderList;

    public static Fragment newInstance() {
        return new WorkorderListFragment();
    }

    public WorkorderListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_workorder_list, container, false);
        ButterKnife.inject(this, view);
        getActivity().setTitle(R.string.nav_schedule);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        queryWorkorders();
    }

    private void queryWorkorders() {
        Workorder workorder = new Workorder(WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendWorkorderQueryRequest(workorder, null, null, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                workorderList=WAJsonHelper.getWorkorderList(response);
                Collections.sort(workorderList,Collections.<Workorder>reverseOrder());
                workorderAdapter = new WorkorderAdapter(workorderList);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress.setVisibility(View.GONE);
                        recycler.setAdapter(workorderAdapter);
                    }
                });
            }
        });
    }

    private void refreshWorkorderList(List<Workorder> workorders) {
        workorderList.clear();
        if (workorders != null) {
            workorderList.addAll(workorders);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menu, menu);
        //super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.fab_add)
    public void onViewClicked() {
        WorkorderCreatorActivity.start(getActivity(), REQUEST_CREATE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CREATE && resultCode == Activity.RESULT_OK) {
            queryWorkorders();
        }
    }
}
