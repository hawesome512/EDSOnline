package com.xseec.eds.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xseec.eds.R;
import com.xseec.eds.adapter.DeviceAdapter;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeviceListFragment extends ComFragment {

    @InjectView(R.id.recycler)
    RecyclerView recycler;
    DeviceAdapter adapter;

    public static Fragment newInstance(ArrayList<Tag> tags) {
        Fragment fragment = new DeviceListFragment();
        fragment.setArguments(getBundle(tags));
        return fragment;
    }


    public DeviceListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu( true );
        View view = inflater.inflate(R.layout.item_recycler, container, false);
        ButterKnife.inject(this, view);
        adapter=new DeviceAdapter(getContext(),tagList);
        recycler.setAdapter(adapter);
        GridLayoutManager layoutManager=new GridLayoutManager(getContext(),1);
        recycler.setLayoutManager(layoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        onBindService();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @Override
    public void onRefreshed(final List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        TagsFilter.refreshTagsByTags(validTagList,tagList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
