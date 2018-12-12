package com.xseec.eds.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.xseec.eds.R;
import com.xseec.eds.adapter.ActionAdapter;
import com.xseec.eds.adapter.AlarmAdapter;
import com.xseec.eds.adapter.WorkorderAdapter;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.model.servlet.Alarm;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReportListFragment extends BaseFragment {

    private static final String EXT_TYPE = "type";
    private static final String EXT_LIST = "list";

    public static final int WORKORDER = 0;
    public static final int ALARM = 1;
    public static final int ACTION = 2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int type;
    private List list;

    public static ReportListFragment newInstance(int type, List list){
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList( EXT_LIST, (ArrayList<? extends Parcelable>) list );
        bundle.putInt( EXT_TYPE,type );
        ReportListFragment fragment=new ReportListFragment();
        fragment.setArguments( bundle );
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu( true );
        View view = inflater.inflate( R.layout.layout_list, container, false );
        ButterKnife.inject( this, view );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(),toolbar,R.drawable.ic_arrow_back_white_24dp );
        progress.setVisibility( View.GONE );
        swipeRefreshLayout.setEnabled( false );
        LinearLayoutManager manager=new LinearLayoutManager( getContext() );
        recycler.setLayoutManager( manager );
        Bundle bundle=this.getArguments();
        type=bundle.getInt( EXT_TYPE );
        list=bundle.getParcelableArrayList( EXT_LIST );
        initData();
        return view;
    }

    private void initData() {
        switch (type) {
            case WORKORDER:
                getActivity().setTitle( R.string.nav_workorder);
                Collections.sort( list, Collections.<Workorder>reverseOrder() );
                WorkorderAdapter workorderAdapter = new WorkorderAdapter( getActivity(), list );
                recycler.setAdapter( workorderAdapter );
                break;
            case ALARM:
                getActivity().setTitle( R.string.nav_alarm );
                Collections.sort( list, Collections.<Alarm>reverseOrder() );
                AlarmAdapter alarmAdapter = new AlarmAdapter( list, getContext() );
                recycler.setAdapter( alarmAdapter );
                break;
            case ACTION:
                getActivity().setTitle( R.string.nav_action );
                Collections.sort( list, Collections.<Action>reverseOrder() );
                ActionAdapter actionAdapter = new ActionAdapter( getContext(), list );
                recycler.setAdapter( actionAdapter );
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }
}
