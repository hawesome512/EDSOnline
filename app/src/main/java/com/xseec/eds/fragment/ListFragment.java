package com.xseec.eds.fragment;


import android.content.res.Configuration;
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
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {


    @InjectView(R.id.recycler)
    RecyclerView recycler;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    private List<Tag> tagList;

    private static final String ARG_TAGS = "tags";
    private static final String ARG_TITLE = "title";

    public static ListFragment newInstance(String title, ArrayList<Tag> tagList) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TITLE, title);
        bundle.putParcelableArrayList(ARG_TAGS, tagList);
        fragment.setArguments(bundle);
        return fragment;
    }

    public ListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.inject(this, view);
        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable
                .ic_arrow_back_white_24dp);
        getActivity().setTitle(getArguments().getString(ARG_TITLE));
        tagList = getArguments().getParcelableArrayList(ARG_TAGS);
        DeviceAdapter adapter = new DeviceAdapter(tagList);
        recycler.setAdapter(adapter);
        int column = ViewHelper.isPort() ? 1 : 2;
        GridLayoutManager manager = new GridLayoutManager(getContext(), column);
        recycler.setLayoutManager(manager);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
