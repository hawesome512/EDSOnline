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
import com.xseec.eds.adapter.ActionAdapter;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Action;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/*
  nj--create a fragment for action list on 2018/11/2
* */
public class ActionListFragment extends BaseFragment {

    @InjectView( R.id.toolbar )
    Toolbar toolbar;
    @InjectView( R.id.progress_data_log )
    ProgressBar progress;
    @InjectView( R.id.recycler_action )
    RecyclerView recyclerView;

    private ActionAdapter adapter;
    private List<Action> actionList;

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
        initRecycleView();
        return view;
    }

    private void initRecycleView(){
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager( layoutManager );
        actionList=new ArrayList( );
        queryAction();
    }

    private void queryAction() {
        Action action=new Action( WAServicer.getUser().getDeviceName());
        WAServiceHelper.sendActionQueryRequest( action, null, null, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }
            @Override
            public void onResponse(Response response) throws IOException {
                List<Action> list = WAJsonHelper.getActionList( response );

                actionList=new ArrayList<>(  );
                //nj--用户信息屏蔽 2018/11/14
                for (Action action:list){
                    if (action.getActionType()!= Action.ActionType.LOGIN){
                        actionList.add( action);
                    }
                }
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
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //nj--启动操作记录筛选查询
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
}
