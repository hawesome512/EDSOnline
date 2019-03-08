package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xseec.eds.R;
import com.xseec.eds.adapter.CustomAdapter;
import com.xseec.eds.model.Custom;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.ViewHelper;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.recycler)
    RecyclerView recycler;

    private CustomAdapter adapter;
    private List<Custom> customList;

    public static SettingFragment newInstance(){
        return new SettingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate( R.layout.fragment_setting, container, false );
        ButterKnife.inject( this, view );
        ViewHelper.initToolbar( (AppCompatActivity) getActivity(), toolbar, R.drawable.menu );
        toolbar.setTitle( R.string.nav_setting );
        customList= Generator.genSettings();
        LinearLayoutManager layoutManager=new LinearLayoutManager( getContext() );
        recycler.setLayoutManager( layoutManager );
        adapter=new CustomAdapter( getActivity(),customList );
        recycler.setAdapter( adapter );
        return view;
    }

    @Override
    protected void onRefreshViews(String jsonData) {

    }
}
