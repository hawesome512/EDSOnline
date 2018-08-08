package com.xseec.eds.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.fragment.OverviewFragment;
import com.xseec.eds.model.BasicInfo;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    private static final String EXT_USER = "user";
    private static final String EXT_BASIC = "basic_info";
    private static final String EXT_TAGS = "tag_list";
    private User user;
    private BasicInfo basicInfo;
    private ArrayList<Tag> tagList;

    private static final String TAG = "MainActivity";
    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    public static void start(Context context, User user, BasicInfo basicInfo, ArrayList<Tag>
            tagList) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(EXT_USER, user);
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
        user =getIntent().getParcelableExtra(EXT_USER);
        basicInfo = getIntent().getParcelableExtra(EXT_BASIC);
        tagList = getIntent().getParcelableArrayListExtra(EXT_TAGS);
        if (user != null && basicInfo != null && tagList != null) {
            TextView textUser=navView.getHeaderView(0).findViewById(R.id.text_account);
            textUser.setText(getString(R.string.nav_account,user.getUsername()));
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.layout_container, OverviewFragment
                    .newInstance(user, basicInfo, tagList)).commit();
        }else {
            Snackbar.make(drawerLayout,R.string.main_failure,Snackbar.LENGTH_LONG).setAction(R.string.main_exit, new View.OnClickListener() {
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

        }
        return super.onOptionsItemSelected(item);
    }
}
