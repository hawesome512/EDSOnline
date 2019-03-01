package com.xseec.eds.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.adapter.FunctionAdapter;
import com.xseec.eds.fragment.ActionListFragment;
import com.xseec.eds.fragment.AlarmListFragment;
import com.xseec.eds.fragment.EnergyFragment;
import com.xseec.eds.fragment.OverviewFragment;
import com.xseec.eds.fragment.ReportFragment;
import com.xseec.eds.fragment.SettingFragment;
import com.xseec.eds.fragment.UserListFragment;
import com.xseec.eds.fragment.WorkorderListFragment;
import com.xseec.eds.model.Device;
import com.xseec.eds.model.Function;
import com.xseec.eds.model.User;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.model.servlet.Basic;
import com.xseec.eds.model.tags.OverviewTag;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.util.ApiLevelHelper;
import com.xseec.eds.util.UserLevelHelper;
import com.xseec.eds.util.ViewHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener, FunctionAdapter.FunctionListener {

    private static final String EXT_TAGS = "tag_list";
    private static final String EXT_OVERVEIW_TAGS = "overview_tag_list";
    private static final String EXT_BASIC = "basic";
    private static final String KEY_MENU_ITEM = "menu_item";
    private static final int NULL_SELECTED = -1;
    private int selectedId;
    private ArrayList<Tag> tagList;
    private ArrayList<OverviewTag> overviewTagList;
    private FragmentManager fragmentManager;
    private Basic basic;

    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public static void start(Context context, ArrayList<Tag>
            tagList, ArrayList<OverviewTag> overviewTagList, Basic basic) {
        Intent intent = new Intent(context, MainActivity.class);
        //传递到Intent里时，参数类型必须为ArrayList,而非List
        intent.putParcelableArrayListExtra(EXT_TAGS, tagList);
        intent.putParcelableArrayListExtra(EXT_OVERVEIW_TAGS, overviewTagList);
        intent.putExtra(EXT_BASIC, basic);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        fragmentManager = getSupportFragmentManager();
        navView.setNavigationItemSelectedListener(this);
        //nj--检查用户权限，控制界面UI.
        checkUserLevelFunction();
        tagList = getIntent().getParcelableArrayListExtra(EXT_TAGS);
        overviewTagList = getIntent().getParcelableArrayListExtra(EXT_OVERVEIW_TAGS);
        basic = getIntent().getParcelableExtra(EXT_BASIC);
        if (basic != null && tagList != null) {
            User user = WAServicer.getUser();
            if (user != null) {
                TextView textUser = navView.getHeaderView(0).findViewById(R.id.text_account);
                textUser.setText(getString(R.string.nav_account, user.getUsername()));
                //nj--用户权限
                TextView textLimit=navView.getHeaderView( 0 ).findViewById( R.id.text_limit );
                textLimit.setText( getString( R.string.nav_limit,user.getLevelState() ) );
            }
            if (savedInstanceState != null) {
                selectedId = savedInstanceState.getInt(KEY_MENU_ITEM);
            } else {
                selectedId = R.id.nav_overview;
            }
            //NJ--用户图像点击时，提示退出
            CircleImageView profileImage=navView.getHeaderView( 0 ).findViewById( R.id.image_profile );
            profileImage.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkExitForUser();
                }
            } );

            navView.getMenu().performIdentifierAction(selectedId, 0);
            Device.setAliasMap(basic.getAliasMap());
        } else {
            Snackbar.make(drawerLayout, R.string.main_failure, Snackbar.LENGTH_LONG).setAction(R
                    .string.main_exit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
        setCheckExit(true, getString(R.string.app_name));
    }

    private void checkUserLevelFunction() {
        MenuItem userItem=navView.getMenu().findItem( R.id.nav_users );
        MenuItem settingItem=navView.getMenu().findItem( R.id.nav_setting );
        MenuItem[] menuItems={userItem,settingItem};
        UserLevelHelper.checkMainActivity( menuItems );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        selectedId = item.getItemId();
        switch (item.getItemId()) {
            case R.id.nav_schedule:
                fragment = WorkorderListFragment.newInstance();
                break;
            case R.id.nav_energy:
                fragment = EnergyFragment.newInstance();
                break;
            case R.id.nav_alarm:
                fragment = AlarmListFragment.newInstance();
                break;
            case R.id.nav_setting:
                fragment = SettingFragment.newInstance();
                break;
            case R.id.nav_exit:
                confirmExit();
                return false;
            //nj--主界面跳转到操作记录界面中
            case R.id.nav_action:
                fragment = ActionListFragment.newInstance();
                break;
            //nj--主界面跳转到报表分析界面中 2018/11/17
            case R.id.nav_trend:
                fragment = ReportFragment.newInstance();
                break;
            //nj--用户管理界面
            case R.id.nav_users:
                fragment= UserListFragment.newInstance();
                break;
            default:
                fragment = OverviewFragment.newInstance(tagList,overviewTagList, basic);
                break;
        }
        replaceFragment(fragment);
        drawerLayout.closeDrawer(navView);
        return true;
    }

    private void checkExitForUser(){
        String info=getString( R.string.current_user,WAServicer.getUser().getUsername() );
        ViewHelper.checkExit( this, info, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoginActivity.start( getApplicationContext() );
                finish();
            }
        } );
    }

    @Override
    public void onFunctionChanged(Function function) {
        selectedId = function.getMenuId();
        replaceFragment(function.getFragment());
    }

    private void replaceFragment(Fragment fragment) {
        int statusColor = ContextCompat.getColor(this, R.color.colorPrimaryDark);
        if (fragment instanceof OverviewFragment) {
            ((OverviewFragment) fragment).setFunctionListener(this);
            statusColor = Color.TRANSPARENT;
        }
        if (ApiLevelHelper.isAtLeast(21)) {
            getWindow().setStatusBarColor(statusColor);
        }
        fragmentManager.beginTransaction().replace(R.id.layout_container, fragment).commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_MENU_ITEM, selectedId);
    }
}
