package com.xseec.eds.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
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

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.DeviceListActivity;
import com.xseec.eds.adapter.OverviewAdapter;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.State;
import com.xseec.eds.model.Tags.OverviewTag;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.Generator;
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
import de.hdodenhof.circleimageview.CircleImageView;


//public class OverviewFragment extends ComFragment implements View.OnClickListener {
public class OverviewFragment extends Fragment implements View.OnClickListener {

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
    @InjectView(R.id.image_schedule)
    ImageView imageSchedule;

    private static final String KEY_USER = "user";
    private static final String KEY_BASIC="basic_info";
    private static final String KEY_TAGS = "tag_list";
    BasicInfo basicInfo;
    User user;
    List<Tag> tagList;
    List<Tag> basicTagList;
    List<OverviewTag> overviewTagList;
    OverviewAdapter overviewAdapter;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(User user,BasicInfo basicInfo, ArrayList<Tag> tagList) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_USER, user);
        bundle.putParcelable(KEY_BASIC,basicInfo);
        bundle.putParcelableArrayList(KEY_TAGS, tagList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle=getArguments();
        user=bundle.getParcelable(KEY_USER);
        basicInfo=bundle.getParcelable(KEY_BASIC);
        tagList= bundle.getParcelableArrayList(KEY_TAGS);
        overviewTagList = LitePal.findAll(OverviewTag.class);
        if(overviewTagList==null||overviewTagList.size()==0){
            Generator.initOverviewTagStore();
            overviewTagList = LitePal.findAll(OverviewTag.class);
        }
        basicTagList=TagsFilter.getBasicTagList(tagList);

        //绑定服务
        //Intent intent = new Intent(getContext(), ComService.class);
        //getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.inject(this, view);

        ViewHelper.initToolbar((AppCompatActivity) getActivity(), toolbar, R.drawable.menu);
        initViews();
        return view;
    }

    private void initViews() {
        //init BasicInfo
        getActivity().setTitle(basicInfo.getTitle());
        String headerImage=WAServicer.getBasicHeaderImageUrl(user.getDeviceName(),basicInfo.getHeaderImg());
        Glide.with(this).load(headerImage).into(imageArea);
        int deviceCount= TagsFilter.getDeviceCount(tagList);
        textDevice.setText(getResources().getString(R.string.overview_device_value,deviceCount));
        textEngineer.setText(basicInfo.getPricipal());
        textLocation.setText(basicInfo.getLocation());
        //init Schedule Card
        //……
        Glide.with(this).load(Generator.getScheduleImageRes()).into(imageSchedule);
        //init Overview Recycler
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerOverview.setLayoutManager(layoutManager);
        overviewAdapter = new OverviewAdapter(overviewTagList);
        recyclerOverview.setAdapter(overviewAdapter);
        //init SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
        //init ClickListener
        imageDeviceList.setOnClickListener(this);
        refreshData();
    }

    private void refreshData() {
        WAServiceHelper.sendGetValueRequest(user.getAuthority(), basicTagList, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                List<Tag> tags=WAJsonHelper.refreshTagValue(response);
                TagsFilter.refreshOverviewTagsByTags(tags,overviewTagList);
                final State state=TagsFilter.getStateByTagList(tags);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        overviewAdapter.notifyDataSetChanged();
                        int stateColorRes;
                        String stateText;
                        switch (state){
                            case ALARM:
                                stateColorRes=R.color.colorError;
                                stateText=getString(R.string.overview_state_alarm);
                                break;
                            case OFFLINE:
                                stateColorRes=R.color.colorAlarm;
                                stateText=getString(R.string.overview_state_offline);
                                break;
                            default:
                                stateColorRes=R.color.colorNormal;
                                stateText=getString(R.string.overview_state_on);
                                break;
                        }
                        imageStatus.setImageResource(stateColorRes);
                        textStatus.setText(stateText);
                    }
                });
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

//    ComListener更新
//    @Override
//    public void onRefreshed(final List<Tag> validTagList) {
//        final String content = validTagList.get(0).getTagValue();
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                //Update ui here
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image_device_list:
                DeviceListActivity.start(getContext());
                break;
            default:
                break;
        }
    }
}
