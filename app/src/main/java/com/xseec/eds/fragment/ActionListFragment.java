package com.xseec.eds.fragment;

import android.content.Intent;
import android.os.Bundle;
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
import com.xseec.eds.activity.FilterActivity;
import com.xseec.eds.adapter.ActionAdapter;
import com.xseec.eds.model.FilterLabel;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static android.app.Activity.RESULT_OK;

/*
  nj--create a fragment for action list on 2018/11/2
* */
public class ActionListFragment extends BaseFragment {

    private static final int REQUEST_FILTER=2;

    @InjectView( R.id.toolbar )
    Toolbar toolbar;
    @InjectView( R.id.progress_data_log )
    ProgressBar progress;
    @InjectView( R.id.recycler_action )
    RecyclerView recyclerView;

    private ActionAdapter adapter;
    private List<Action> actionList;
    private List<FilterLabel> filterFactor;

    private String startTime;
    private String endTime;

    public static ActionListFragment newInstance(){
        return new ActionListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu( true );
        View view=inflater.inflate( R.layout.fragment_action_list,container,false );
        ButterKnife.inject( this,view );
        getActivity().setTitle( R.string.nav_action );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(),toolbar,R.drawable.menu );
        //nj--查询时间为近一个月时间 2018/12/19
        startTime=DateHelper.getServletString( DateHelper.getNearTimeOfMonth( 0,-7 ) );
        endTime=DateHelper.getServletString( DateHelper.getNearTimeOfMonth( 0,0 ) );
        initRecyclerView();
        return view;
    }

    private void initRecyclerView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( layoutManager );
        actionList=new ArrayList( );
        queryAction();
    }

    private void queryAction() {
        Action action=new Action( WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendActionQueryRequest( action, startTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                List<Action> totalList = WAJsonHelper.getActionList( response );
                actionList=new ArrayList<>(  );
                //nj--筛选操作记录
                actionList=filterAction( filterFactor,totalList );
                Collections.sort( actionList,Collections.<Action>reverseOrder());
                refreshViewsInThread( response );
            }
        } );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate( R.menu.filter_menu,menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.filter:
                //nj--操作记录查询筛选
                FilterActivity.start(getActivity(), FilterActivity.ACTION,REQUEST_FILTER);
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //nj--启动操作记录筛选查询
        switch (requestCode){
            case REQUEST_FILTER:
                if(resultCode==RESULT_OK){
                    startTime=null;
                    endTime=null;
                    filterFactor=data.getParcelableArrayListExtra( FilterActivity.DATA_RESULT );
                    progress.setVisibility( View.VISIBLE );
                    queryAction();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progress.setVisibility( View.GONE );
        adapter=new ActionAdapter(getContext(), actionList );
        recyclerView.setAdapter( adapter );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset( this );
    }

    //nj--筛选操作记录信息 2018/12/22
    private List<Action> filterAction(List<FilterLabel> factor,List<Action> sources){
        List<Action> actionList=new ArrayList<>(  );
        if (factor==null||factor.size()==0){
            for (Action action:sources){
                if (action.getActionType()!= Action.ActionType.LOGIN)
                    actionList.add( action);
            }
        }else if (factor.size()==1){
            actionList= Generator.filterActionsType( sources,factor.get( 0 ).getValueOfInt() );
        }else{
            actionList= Generator.filterActionsType( sources,factor.get( 0 ).getValueOfInt() );
            sources.clear();
            sources.addAll( actionList );
            actionList=Generator.filterActionsMethod( sources,factor.get( 1 ).getValueOfInt() );
        }
        return actionList;
    }
}
