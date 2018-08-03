package com.xseec.eds.activity;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.squareup.okhttp.Response;
import com.xseec.eds.R;
import com.xseec.eds.fragment.OverviewFragment;
import com.xseec.eds.model.Tags.Tag;
import com.xseec.eds.model.User;
import com.xseec.eds.util.CodeHelper;
import com.xseec.eds.util.WAJsonHelper;
import com.xseec.eds.util.WAServiceHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @InjectView(R.id.layout_container)
    FrameLayout layoutContainer;
    @InjectView(R.id.nav_view)
    NavigationView navView;
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        String authority = CodeHelper.encode("eds:ink");
        User user = new User(authority, null);
        List<Tag> tagList = new ArrayList<>();
        tagList.add(new Tag("CY_A1:Ia"));
        tagList.add(new Tag("CY_A1:StateText"));
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.layout_container, OverviewFragment
                .newInstance(user, tagList)).commit();
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
