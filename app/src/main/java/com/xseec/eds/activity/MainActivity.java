package com.xseec.eds.activity;

import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xseec.eds.R;
import com.xseec.eds.fragment.OverviewFragment;
import com.xseec.eds.fragment.WorkorderListFragment;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.tags.Tag;
import com.xseec.eds.model.WAServicer;
import com.xseec.eds.util.ApiLevelHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity implements NavigationView
        .OnNavigationItemSelectedListener {

    private static final String EXT_BASIC = "basic_info";
    private static final String EXT_TAGS = "tag_list";
    private BasicInfo basicInfo;
    private ArrayList<Tag> tagList;
    private FragmentManager fragmentManager;

    private static final String TAG = "MainActivity";
    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public static void start(Context context, BasicInfo basicInfo, ArrayList<Tag>
            tagList) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXT_BASIC, basicInfo);
        //传递到Intent里时，参数类型必须为ArrayList,而非List
        intent.putParcelableArrayListExtra(EXT_TAGS, tagList);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        navView.setNavigationItemSelectedListener(this);
        basicInfo = getIntent().getParcelableExtra(EXT_BASIC);
        tagList = getIntent().getParcelableArrayListExtra(EXT_TAGS);
        if (basicInfo != null && tagList != null) {
            TextView textUser = navView.getHeaderView(0).findViewById(R.id.text_account);
            textUser.setText(getString(R.string.nav_account, WAServicer.getUser().getUsername()));
            fragmentManager = getSupportFragmentManager();
            replaceFragment(OverviewFragment.newInstance(basicInfo, tagList));
        } else {
            Snackbar.make(drawerLayout, R.string.main_failure, Snackbar.LENGTH_LONG).setAction(R
                    .string.main_exit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            }).show();
        }
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
        int statusColor= ContextCompat.getColor(this,R.color.colorPrimaryDark);
        switch (item.getItemId()) {
            case R.id.nav_schedule:
                fragment = WorkorderListFragment.newInstance();
                break;
            default:
                fragment = OverviewFragment.newInstance(basicInfo, tagList);
                statusColor=Color.TRANSPARENT;
                break;
        }
        if(ApiLevelHelper.isAtLeast(21)){
            getWindow().setStatusBarColor(statusColor);
        }
        replaceFragment(fragment);
        drawerLayout.closeDrawer(navView);
        return true;
    }

    private void replaceFragment(Fragment fragment) {
        fragmentManager.beginTransaction().replace(R.id.layout_container, fragment).commit();
    }
}
