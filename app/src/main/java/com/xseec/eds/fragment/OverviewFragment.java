package com.xseec.eds.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.activity.ListActivity;
import com.xseec.eds.adapter.OverviewAdapter;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.State;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ContentHelper;
import com.xseec.eds.util.EDSApplication;
import com.xseec.eds.util.Generator;
import com.xseec.eds.util.OpenMapHelper;
import com.xseec.eds.util.TagsFilter;
import com.xseec.eds.util.ViewHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.loader.ImageLoader;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;


//public class OverviewFragment extends ComFragment implements View.OnClickListener {
public class OverviewFragment extends BaseFragment {

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

    private static final String KEY_BASIC = "basic_info";
    private static final String KEY_TAGS = "tag_list";
    BasicInfo basicInfo;
    List<Tag> tagList;
    List<Tag> basicTagList;
    List<OverviewTag> overviewTagList;
    OverviewAdapter overviewAdapter;
    @InjectView(R.id.text_schedule_title)
    TextView textScheduleTitle;
    @InjectView(R.id.text_schedule_time)
    TextView textScheduleTime;
    @InjectView(R.id.btn_schedule_execute)
    Button btnScheduleExecute;
    @InjectView(R.id.btn_schedule_notify)
    Button btnScheduleNotify;
    @InjectView(R.id.btn_schedule_cancel)
    Button btnScheduleCancel;
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

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(BasicInfo basicInfo, ArrayList<Tag>
            tagList) {
        OverviewFragment fragment = new OverviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BASIC, basicInfo);
        bundle.putParcelableArrayList(KEY_TAGS, tagList);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        basicInfo = bundle.getParcelable(KEY_BASIC);
        tagList = bundle.getParcelableArrayList(KEY_TAGS);
        overviewTagList = LitePal.findAll(OverviewTag.class);
        if (overviewTagList == null || overviewTagList.size() == 0) {
            Generator.initOverviewTagStore();
            overviewTagList = LitePal.findAll(OverviewTag.class);
        }
        basicTagList = TagsFilter.getBasicTagList(tagList);

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
        //initViews();
        return view;
    }

    private void initViews() {
        //init BasicInfo
        getActivity().setTitle(basicInfo.getTitle());
        String deviceName = WAServicer.getUser().getDeviceName();
        String headerImage = WAServicer.getBasicImageUrl(deviceName, basicInfo.getHeaderImg());
        Glide.with(this).load(headerImage).into(imageArea);
        int deviceCount = TagsFilter.getDeviceCount(tagList);
        textDevice.setText(getResources().getString(R.string.overview_device_value, deviceCount));
        textEngineer.setText(basicInfo.getPricipal());
        textLocation.setText(basicInfo.getLocation().split(",")[0]);
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
        refreshData();
    }

    private void refreshData() {
        WAServiceHelper.sendGetValueRequest(basicTagList, new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                refreshViewsInThread(response);
            }
        });
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

    @Override
    protected void onRefreshViews(Response response) {
        basicTagList = WAJsonHelper.refreshTagValue(response);
        TagsFilter.refreshOverviewTagsByTags(basicTagList, overviewTagList);
        final State state = TagsFilter.getStateByTagList(basicTagList);
        overviewAdapter.notifyDataSetChanged();
        imageStatus.setImageResource(state.getStateColorRes());
        state.setUnusualAnimator(imageStatus);
        textStatus.setText(state.getStateText());
        swipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.layout_status)
    public void onLayoutStatusClicked() {
        ArrayList<Tag> abnormalDevices = (ArrayList<Tag>) TagsFilter.getAbnormalStateList
                (basicTagList);
        if (abnormalDevices.size() != 0) {
            ListActivity.start(getContext(), getString(R.string.detail_title_error),
                    abnormalDevices);
        }
    }

    @OnClick(R.id.layout_device)
    public void onLayoutDeviceClicked() {
        ArrayList<Tag> devices = (ArrayList<Tag>) TagsFilter.getStateList(basicTagList);
        ListActivity.start(getContext(), getString(R.string.detail_title), devices);
    }

    @OnClick(R.id.layout_grid)
    public void onLayoutGridClicked() {
        final Dialog dialog = new Dialog(getContext(), android.R.style
                .Theme_Translucent_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.preview_image);
        List<String> images = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        for (int i = 0; i < basicInfo.getPictures().size(); i++) {
            String picture = basicInfo.getPictures().get(i);
            titles.add(picture.split("\\.")[0]);
            images.add(WAServicer.getBasicImageUrl(WAServicer.getUser().getDeviceName(), picture));
        }
        Banner banner = dialog.findViewById(R.id.banner);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE)
                .setBannerTitles(titles)
                .setImages(images)
                .isAutoPlay(false)
                .setImageLoader(new ImageLoader() {
                    @Override
                    public void displayImage(Context context, Object path, ImageView imageView) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        Glide.with(context).load((String) path).into(imageView);
                    }
                })
                .start();
        Button btnClose = dialog.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.layout_engineer)
    public void onLayoutEngineerClicked() {
        ContentHelper.callPhone(getContext(),basicInfo.getPricipal());
    }

    @OnClick(R.id.layout_location)
    public void onLayoutLocationClicked() {
        Pattern pattern = Pattern.compile("\\d+.\\d+");
        Matcher matcher = pattern.matcher(basicInfo.getLocation());
        List<String> locations = new ArrayList<>();
        while (matcher.find()) {
            locations.add(matcher.group());
        }
        if (locations.size() == 2) {
            Intent intent = OpenMapHelper.getMapAppIntent(EDSApplication.getContext(), locations
                    .get(0), locations.get(1));
            if (intent == null) {
                Snackbar.make(collapsingLayout, getString(R.string.overview_map_error), Snackbar
                        .LENGTH_SHORT).show();
            } else {
                startActivity(intent);
            }
        }
    }
}
