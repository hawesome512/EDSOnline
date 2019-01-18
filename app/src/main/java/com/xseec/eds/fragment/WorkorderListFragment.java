package com.xseec.eds.fragment;


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

import com.github.mikephil.charting.formatter.IFillFormatter;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.FilterActivity;
import com.xseec.eds.activity.WorkorderCreatorActivity;
import com.xseec.eds.adapter.WorkorderAdapter;
import com.xseec.eds.model.FilterLabel;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Workorder;
import com.xseec.eds.util.DateHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.net.IDN;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkorderListFragment extends BaseFragment {

    private static final int REQUEST_CREATE = 1;
    private static final int REQUEST_FILTER=2;

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

    private String startTime;
    private String endTime;
    private List<FilterLabel> filterList;

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
        getActivity().setTitle(R.string.nav_workorder);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        //nj--查询时间为当前时间的前后三个月 2018/12/19
        startTime= DateHelper.getServletString( DateHelper.getNearTimeOfMonth( -2,0 ) );
        endTime=DateHelper.getServletString( DateHelper.getNearTimeOfMonth( 1,0 ) );

        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
            queryWorkorders();
    }

    private void queryWorkorders() {

        String systemId=getString( R.string.woekorder_sys_id );
        String userId=WAServicer.getUser().getDeviceName();
        //nj--判断是否为系统工单 2018/12/26
        String workorderId=isSystemsWorkorder()?systemId:userId;

        Workorder workorder=new Workorder( workorderId );
        WAServiceHelper.sendWorkorderQueryRequest(workorder, startTime, endTime, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(final Response response) throws IOException {
                workorderList=WAJsonHelper.getWorkorderList(response);
                //nj--筛选工单 2018/12/24
                filterWorkorderList( filterList );
                Collections.sort(workorderList,Collections.<Workorder>reverseOrder());
                refreshViewsInThread(response);
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
                FilterActivity.start( getActivity(), FilterActivity.WORKORDER,REQUEST_FILTER );
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
        switch (requestCode){
            case REQUEST_FILTER:
                if (resultCode==RESULT_OK){
                    startTime=null;
                    endTime=null;
                    filterList=data.getParcelableArrayListExtra( FilterActivity.DATA_RESULT );
                }
                break;
            default:
                break;
        }
        queryWorkorders();
    }

    @Override
    protected void onRefreshViews(String jsonData) {
        progress.setVisibility(View.GONE);
        workorderAdapter = new WorkorderAdapter(getContext(),workorderList);
        recycler.setAdapter(workorderAdapter);
    }

    //nj--筛选工单
    private void filterWorkorderList(List<FilterLabel> factor){
        if (factor==null){
           return;
        }else {
            List<Workorder> totalList=new ArrayList<>(  );
            List<Workorder> filterList=new ArrayList<>(  );
            String type=getString( R.string.filter_type );
            String state=getString( R.string.filter_state );
            totalList.addAll( workorderList );
            workorderList.clear();
            for (int i=0;i<factor.size();i++){
                FilterLabel label=factor.get( i );
                if (label.getType().equals( type )){
                    //NJ-筛选工单类型
                    filterList=Generator.filterWorkorderListOfType( totalList,label.getValueOfInt());
                }else if (label.getType().equals( state )){
                    //nj--筛选工单状态
                    filterList=Generator.filterWorkorderListOfState( totalList,label.getValueOfInt() );
                }
                totalList.clear();
                totalList.addAll( filterList );
            }
            workorderList.addAll( totalList );
        }
    }

    private boolean isSystemsWorkorder(){
        if (filterList!=null){
            String type=getString( R.string.filter_type );
            FilterLabel label=filterList.get( 0 );
            return (label.getType().equals( type )&&label.getValueOfInt()==3)?true:false;
        }else {
            return false;
        }
    }
}
