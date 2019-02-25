package com.xseec.eds.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.ListActivity;
import com.xseec.eds.adapter.FunctionAdapter;
import com.xseec.eds.adapter.OverviewAdapter;
import com.xseec.eds.model.State;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.OpenMapHelper;
import com.xseec.eds.util.PhotoPicker;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


//public class OverviewFragment extends ComFragment implements View.OnClickListener {
public class OverviewFragment extends ComFragment {

    @InjectView(R.id.image_area)
    ImageView imageArea;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_layout)
    CollapsingToolbarLayout collapsingLayout;
    @InjectView(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @InjectView(R.id.text_status)
    TextView textStatus;
    @InjectView(R.id.image_status)
    CircleImageView imageStatus;
    @InjectView(R.id.text_device)
    TextView textDevice;
    @InjectView(R.id.image_enlarge_grid)
    ImageView imageEnlargeGrid;
    @InjectView(R.id.text_engineer)
    TextView textEngineer;
    @InjectView(R.id.image_phone)
    ImageView imagePhone;
    @InjectView(R.id.text_location)
    TextView textLocation;
    @InjectView(R.id.image_navigation)
    ImageView imageNavigation;
    @InjectView(R.id.recycler_overview)
    RecyclerView recyclerOverview;
    @InjectView(R.id.layout_container)
    LinearLayout layoutContainer;
    @InjectView(R.id.image_device_list)
    ImageView imageDeviceList;
    @InjectView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private static final String KEY_BASIC = "basic";
    private static final String KEY_OVERVIEW_TAG="overview_tag";
    Basic basic;
    List<Tag> basicTagList;
    List<OverviewTag> overviewTagList;

    OverviewAdapter overviewAdapter;
    FunctionAdapter functionAdapter;
    FunctionAdapter.FunctionListener functionListener;

    @InjectView(R.id.layout_status)
    LinearLayout layoutStatus;
    @InjectView(R.id.layout_device)
    LinearLayout layoutDevice;
    @InjectView(R.id.layout_grid)
    LinearLayout layoutGrid;
    @InjectView(R.id.layout_engineer)
    LinearLayout layoutEngineer;
    @InjectView(R.id.layout_location)
    LinearLayout layoutLocation;
    @InjectView(R.id.recycler_function)
    RecyclerView recyclerFunction;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(ArrayList<Tag>
            tagList,ArrayList<OverviewTag> overviewTagList, Basic basic) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle bundle =getBundle(TagsFilter.getBasicTagList(tagList,overviewTagList));
        bundle.putParcelableArrayList(KEY_OVERVIEW_TAG,overviewTagList);
        bundle.putParcelable(KEY_BASIC, basic);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        basic = bundle.getParcelable(KEY_BASIC);
        overviewTagList=bundle.getParcelableArrayList(KEY_OVERVIEW_TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
        onBindService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.inject(this, view);

        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        //initViews();
        return view;
    }

    private void initViews() {
        //init BasicInfo
        getActivity().setTitle(basic.getUser());
        Glide.with(this).load(basic.getBannerUrl()).into(imageArea);
        int deviceCount = TagsFilter.getDeviceList(tagList).size();
        textDevice.setText(getResources().getString(R.string.overview_device_value, deviceCount));
        textEngineer.setText(basic.getPricipal());textLocation.setText(basic.getLocation());
        //textLocation.setText(basic.getLocation().split(",")[0]);
        //init Overview Recycler
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerOverview.setLayoutManager(layoutManager);
        overviewAdapter = new OverviewAdapter(overviewTagList);

        GridLayoutManager layoutManager1 = new GridLayoutManager(getContext(), 3);
        recyclerOverview.setAdapter(overviewAdapter);
        recyclerFunction.setLayoutManager(layoutManager1);
        functionAdapter=new FunctionAdapter(getContext(),Generator.genFunctions(),functionListener);
        recyclerFunction.setAdapter(functionAdapter);
        swipeRefreshLayout.setEnabled(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.layout_status)
    public void onLayoutStatusClicked() {
        ArrayList<Tag> abnormalDevices = (ArrayList<Tag>) TagsFilter.getAbnormalStateList
                (basicTagList);
        if (abnormalDevices.size() != 0) {
            ListActivity.start(getContext(), getString(R.string.detail_title_error),
                    abnormalDevices);
        } else {
            Toast.makeText(getContext(), R.string.overview_devices_normal, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @OnClick(R.id.layout_device)
    public void onLayoutDeviceClicked() {
        ArrayList<Tag> devices = (ArrayList<Tag>) TagsFilter.getStateList(basicTagList);
        ListActivity.start(getContext(), getString(R.string.detail_title), devices);
    }

    @OnClick(R.id.layout_grid)
    public void onLayoutGridClicked() {
        PhotoPicker.previewSelectedPhotos(getActivity(), PhotoPicker.getImageMediaList(basic
                .getImage()), 0);
    }

    @OnClick(R.id.layout_engineer)
    public void onLayoutEngineerClicked() {
        ContentHelper.callPhone(getContext(), basic.getPricipal());
    }

    @OnClick(R.id.layout_location)
    public void onLayoutLocationClicked() {
        OpenMapHelper.getMapAppIntent(getContext(), basic.getLocation());
    }

    public void setFunctionListener(FunctionAdapter.FunctionListener functionListener) {
        this.functionListener=functionListener;
    }

    @Override
    public void onRefreshed(final List<Tag> validTagList) {
        super.onRefreshed(validTagList);
        basicTagList=validTagList;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TagsFilter.refreshOverviewTagsByTags(validTagList, overviewTagList);
                final State state = TagsFilter.getStateByTagList(validTagList);
                overviewAdapter.notifyDataSetChanged();
                imageStatus.setImageResource(state.getStateColorRes());
                state.setUnusualAnimator(imageStatus);
                textStatus.setText(state.getStateText());
                swipeRefreshLayout.setRefreshing(false);

            }
        });
    }
}
