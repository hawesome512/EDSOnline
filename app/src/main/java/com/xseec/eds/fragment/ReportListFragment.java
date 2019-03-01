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

    public static final String EXT_ITEM_TYPE = "itemType";
    public static final String EXT_ITEM_LIST = "itemList";

    private static final int WORKORDER = 0;
    private static final int ALARM = 1;
    private static final int ACTION = 2;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.progress)
    ProgressBar progress;
    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private int itemType;
    private List itemList;

    public static ReportListFragment newInstance(int itemType, List itemList){
        Bundle bundle=new Bundle();
        bundle.putParcelableArrayList( EXT_ITEM_LIST, (ArrayList<? extends Parcelable>) itemList );
        bundle.putInt( EXT_ITEM_TYPE,itemType );
        ReportListFragment fragment=new ReportListFragment();
        fragment.setArguments( bundle );
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu( true );
        View view = inflater.inflate( R.layout.layout_list, container, false);
        ButterKnife.inject( this, view );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(),toolbar,R.drawable.ic_arrow_back_white_24dp );
        initView();
        loadData();
        return view;
    }

    private void initView(){
        progress.setVisibility( View.GONE );
        swipeRefreshLayout.setEnabled( false );
        LinearLayoutManager manager=new LinearLayoutManager( getContext() );
        recycler.setLayoutManager( manager );
        Bundle bundle=this.getArguments();
        itemType=bundle.getInt( EXT_ITEM_TYPE);
        itemList=bundle.getParcelableArrayList( EXT_ITEM_LIST );
    }

    private void loadData() {
        switch (itemType) {
            case WORKORDER:
                getActivity().setTitle( R.string.nav_workorder );
                Collections.sort( itemList, Collections.<Workorder>reverseOrder() );
                WorkorderAdapter workorderAdapter = new WorkorderAdapter( getActivity(), itemList );
                recycler.setAdapter( workorderAdapter );
                break;
            case ALARM:
                getActivity().setTitle( R.string.nav_alarm );
                Collections.sort( itemList, Collections.<Alarm>reverseOrder() );
                AlarmAdapter alarmAdapter = new AlarmAdapter( itemList, getContext() );
                recycler.setAdapter( alarmAdapter );
                break;
            case ACTION:
                getActivity().setTitle( R.string.nav_action );
                Collections.sort( itemList, Collections.<Action>reverseOrder() );
                ActionAdapter actionAdapter = new ActionAdapter( getContext(), itemList );
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
    protected void onRefreshViews(String jsonData) { }
}
